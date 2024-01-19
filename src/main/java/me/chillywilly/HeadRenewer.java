package me.chillywilly;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class HeadRenewer extends JavaPlugin implements CommandExecutor {
    
    @Override
    public void onEnable() {
        //TODO Enable Function
        getLogger().info("Plugin is enabled!");
        this.getCommand("renewhead").setExecutor(this);
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
            player.sendMessage(color("&4test"));
        } else {
            sender.sendMessage("This command only applies to players");
        }
        return true;
    }
}