package me.i2000c.newalb.subcommands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.utils.logging.Logger;

public interface SubCommand {
    
    public static final String NO_CONSOLE_MSG = "&cYou can't send this command from the console";
    
    public default boolean checkNotConsole(CommandSender sender) {
        if(sender instanceof Player) {
            return true;
        } else {
            Logger.sendMessage(NO_CONSOLE_MSG, sender);
            return false;
        }
    }
    
    public default boolean checkHasPermission(CommandSender sender, String permissionPath) {
        String noPermissionMessage = ConfigManager.getLangMessage("NoPermission");
        String permission = ConfigManager.getMainConfig().getString(permissionPath);
        if(sender.hasPermission(permission)) {
            return true;
        } else {
            Logger.sendMessage(noPermissionMessage, sender, false);
            return false;
        }
    }
    
    public default Player getOnlinePlayer(CommandSender sender, String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        if(player == null) {
            Logger.sendMessage("&cPlayer " + playerName + "&c is offline", sender);
        }
        return player;
    }
    
    public default List<String> getOnlinePlayers() {
        List<String> players = new ArrayList<>();
        for(Player player : Bukkit.getOnlinePlayers()) {
            players.add(player.getName());
        }
        return players;
    }
    
    public boolean execute(CommandSender sender, List<String> args);
    
    public default List<String> onTabComplete(CommandSender sender, List<String> args) {
        return Collections.emptyList();
    }
}
