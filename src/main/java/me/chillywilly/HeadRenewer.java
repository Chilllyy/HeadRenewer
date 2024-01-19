package me.chillywilly;

import java.io.File;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerProfile;

import net.md_5.bungee.api.ChatColor;

public class HeadRenewer extends JavaPlugin implements CommandExecutor {

    private String pluginPrefix;
    
    @Override
    public void onEnable() {
        getLogger().info("Plugin is enabled!");
        this.getCommand("renewhead").setExecutor(this);
        this.saveDefaultConfig();
        this.pluginPrefix = this.getConfig().getString("prefix");
        if (!new File(this.getDataFolder() + File.separator + "messages.yml").exists()) {
            this.saveResource("messages.yml", false);
        }
    }

    private String getMessage(String path, Boolean useColor) {
        File f = new File(this.getDataFolder() + File.separator + "messages.yml");
        FileConfiguration msgfile = YamlConfiguration.loadConfiguration(f);
        if (useColor) {
            return color(msgfile.getString(path).replace("{prefix}", pluginPrefix));
        } else {
            return msgfile.getString(path).replace("{prefix}", pluginPrefix);
        }
    }


    @Override
    public void onDisable() {
        getLogger().info("Plugin is disabled!");
    }

    public String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("headrenewer.renew")) {
                ItemStack oldSkull = null;
                ItemStack newSkull = new ItemStack(Material.PLAYER_HEAD);

                if (player.getInventory().getItemInMainHand().getType() == Material.PLAYER_HEAD) {
                    oldSkull = player.getInventory().getItemInMainHand();
                } else if (player.getInventory().getItemInOffHand().getType() == Material.PLAYER_HEAD) {
                    oldSkull = player.getInventory().getItemInOffHand();
                } else {
                    player.sendMessage(getMessage("player_not_holding_skull", true));
                    return true;
                }

                SkullMeta oldSkullMeta = (SkullMeta)oldSkull.getItemMeta();
                SkullMeta newSkullMeta = (SkullMeta)oldSkull.getItemMeta();

                PlayerProfile oldProfile = oldSkullMeta.getOwnerProfile();

                String skullPlayerName = oldProfile.getName();
                UUID skullPlayerUUID = oldProfile.getUniqueId();

                PlayerProfile newProfile = this.getServer().createPlayerProfile(skullPlayerUUID, skullPlayerName);

                newSkullMeta.setOwnerProfile(newProfile);

                newSkull.setItemMeta(newSkullMeta);
                
                player.getInventory().setItemInMainHand(newSkull);


                player.sendMessage(getMessage("renew_successful", true));
            } else {
                player.sendMessage(getMessage("no_permission", true));
                return true;
            }
        } else {
            sender.sendMessage(getMessage("only_applies_to_players", false));
            return true;
        }
        return true;
    }
}