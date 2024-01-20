package me.chillywilly;

import java.io.File;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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

    private String pluginPrefix; //Init Empty String to store Prefix Text
    
    @Override
    public void onEnable() {
        this.saveDefaultConfig(); //Attempt to Save Default Config File
        if (!new File(this.getDataFolder() + File.separator + "messages.yml").exists()) { //Check if Messages File exists
            this.saveResource("messages.yml", false); //Attempt to Save Default Messages File
        }
        this.getCommand("renewhead").setExecutor(this); //Register Command with server
        this.pluginPrefix = this.getConfig().getString("prefix"); //Load Prefix from config file
    }

    private String getMessage(String path, Boolean useColor) {
        File f = new File(this.getDataFolder() + File.separator + "messages.yml"); //Load Messages file from storage
        FileConfiguration msgfile = YamlConfiguration.loadConfiguration(f); //"Open" the file to read contents
        if (useColor) { //Check if we are using color
            return color(msgfile.getString(path).replace("{prefix}", pluginPrefix)); //Read the message from the file, replace {prefix} with the prefix and parse color
        } else {
            return msgfile.getString(path).replace("{prefix}", pluginPrefix); //Read the message from the file, replace {prefix} with the prefix and don't parse color
        }
    }

    public String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) { //Check that it is actually a player running this command (not console, command block, etc.)
            Player player = (Player) sender; //Set variable for easier understanding
            if (player.hasPermission("headrenewer.renew")) { //Check player permissions
                ItemStack oldSkull = null; //Initialize variable for later use
                ItemStack newSkull = new ItemStack(Material.PLAYER_HEAD); //Set this to a blank new skull

                if (player.getInventory().getItemInMainHand().getType() == Material.PLAYER_HEAD) { //If player has a skull in their main hand
                    oldSkull = player.getInventory().getItemInMainHand(); //Set the empty variable we made earlier to the item in the player's main hand
                } else if (player.getInventory().getItemInOffHand().getType() == Material.PLAYER_HEAD) { //If player has a skull in their off hand
                    oldSkull = player.getInventory().getItemInOffHand(); //Set the empty variable we made earlier to the item in the player's off hand
                } else {
                    player.sendMessage(getMessage("player_not_holding_skull", true)); //load and send message from messages.yml
                    return true; //stop running code here
                }

                SkullMeta oldSkullMeta = (SkullMeta)oldSkull.getItemMeta(); //extract item meta from old skull (owner name, lore, item name)
                SkullMeta newSkullMeta = (SkullMeta)oldSkull.getItemMeta(); //also put item meta on new skull (for item name and lore)

                PlayerProfile oldProfile = oldSkullMeta.getOwnerProfile(); //get owner profile from the old skull (used to store the skin)
                
                UUID skullPlayerUUID = oldProfile.getUniqueId(); //get UUID from the profile

                OfflinePlayer skullOwner = this.getServer().getOfflinePlayer(skullPlayerUUID); //Get Player Via UUID

                if (!skullOwner.hasPlayedBefore()) { // check if player has played before (causes issues)
                    player.sendMessage(getMessage("has_not_joined", true)); // send error message
                    return true;
                }

                String skullPlayerName = this.getServer().getOfflinePlayer(skullPlayerUUID).getName(); //get name from stored player file

                PlayerProfile newProfile = this.getServer().createPlayerProfile(skullPlayerUUID, skullPlayerName); //create a new profile for the name and UUID (grabs new skin from mojang)

                newSkullMeta.setOwnerProfile(newProfile); //applies profile to the new skull meta

                newSkull.setItemMeta(newSkullMeta); //applies new skull meta to the new skull
                
                player.getInventory().removeItem(oldSkull);
                player.getInventory().addItem(newSkull); //replaces item in hand with skull


                player.sendMessage(getMessage("renew_successful", true)); //load and send success message
            } else {
                player.sendMessage(getMessage("no_permission", true)); //load and send no permission message
                return true;
            }
        } else {
            sender.sendMessage(getMessage("only_applies_to_players", false)); //load and send only applies to player message (to console)
            return true;
        }
        return true;
    }
}