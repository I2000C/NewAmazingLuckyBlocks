package me.i2000c.newalb.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.utils.locations.WorldManager;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.menus.WorldMenu;

public class SubCommandWorlds implements SubCommand {
    
    private static final String USAGE_MESSAGE = "&cUsage: &7/alb worlds [list | changeListMode | [add <world>] | [delete <world>]]";
    
    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if(!checkHasPermission(sender, "Commands.Worlds-permission")) {
            return false;
        }
        
        switch(args.size()) {
            case 0:
                // /alb worlds
                if(!checkNotConsole(sender)) {
                    return false;
                }
                
                WorldMenu.reset();
                WorldMenu.openWorldsMenu((Player) sender);
                return true;
            case 1:
                switch(args.get(0)) {
                    case "list":
                        // /alb worlds list
                        WorldManager.WorldListMode currentMode = WorldManager.getWorldListMode();
                        String currentModeMessage = ConfigManager.getLangMessage("World-management1.currentMode")
                                                                 .replace("%worldListMode%", currentMode.toString());
                        Logger.sendMessage(currentModeMessage, sender);
                        Logger.sendMessage(ConfigManager.getLangMessage("World-management1.title"), sender);
                        Logger.sendMessage(ConfigManager.getLangMessage("World-management1.separator"), sender);
                        WorldManager.getWorlds().forEach(worldName -> {
                            String message = ConfigManager.getLangMessage("World-management1.worldName").replace("%world%", worldName);
                            Logger.sendMessage(message, sender);
                        });
                        return true;
                    case "changeListMode":
                        WorldManager.WorldListMode nextMode = WorldManager.getWorldListMode().next();
                        WorldManager.setWorldListMode(nextMode);
                        String message = ConfigManager.getLangMessage("World-management2.worldListModeChanged")
                                                      .replace("%worldListMode%", nextMode.toString());
                        Logger.sendMessage(message, sender);
                        return true;
                    case "add":
                        Logger.sendMessage("&cUsage: &7/alb worlds add <world>", sender);
                        return false;
                    case "delete":
                        Logger.sendMessage("&cUsage: &7/alb worlds delete <world>", sender);
                        return false;
                    default:
                        Logger.sendMessage(USAGE_MESSAGE, sender);
                        return false;
                }
            case 2:
                switch(args.get(0)) {
                    // /alb worlds add <world>
                    case "add":
                        String worldName = args.get(1);
                        String message;
                        if(Bukkit.getWorld(worldName) == null) {
                            message = ConfigManager.getLangMessage("World-management2.worldNotExists")
                                                   .replace("%world%", worldName);
                            Logger.sendMessage(message, sender);
                            return false;
                        }
                        
                        if(WorldManager.addWorld(worldName)) {
                            message = ConfigManager.getLangMessage("World-management2.worldAdded")
                                                   .replace("%world%", worldName);
                            Logger.sendMessage(message, sender);
                            return true;
                        } else {
                            message = ConfigManager.getLangMessage("World-management2.worldAlreadyAdded")
                                                   .replace("%world%", worldName);
                            Logger.sendMessage(message, sender);
                            return false;
                        }
                    case "delete":
                        // /alb worlds delete <world>
                        worldName = args.get(1);
                        if(Bukkit.getWorld(worldName) == null) {
                            message = ConfigManager.getLangMessage("World-management2.worldNotExists")
                                                   .replace("%world%", worldName);
                            Logger.sendMessage(message, sender);
                            return false;
                        }
                        
                        if(WorldManager.deleteWorld(worldName)) {
                            message = ConfigManager.getLangMessage("World-management2.worldDeleted")
                                                   .replace("%world%", worldName);
                            Logger.sendMessage(message, sender);
                            return true;
                        } else {
                            message = ConfigManager.getLangMessage("World-management2.worldAlreadyDeleted")
                                                   .replace("%world%", worldName);
                            Logger.sendMessage(message, sender);
                            return false;
                        }
                    default:
                        Logger.sendMessage(USAGE_MESSAGE, sender);
                        return false;
                }
            default:
                Logger.sendMessage(USAGE_MESSAGE, sender);
                return false;
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        List<String> items = new ArrayList<>();
        switch(args.size()) {
            case 1:
                items.add("list");
                items.add("add");
                items.add("delete");
                items.add("changeListMode");
                break;
            case 2:
                String firstArg = args.get(0);
                if(firstArg.equals("add")) {
                    Bukkit.getWorlds().forEach(world -> items.add(world.getName()));
                    items.removeIf(WorldManager.getWorlds()::contains);
                } else if(firstArg.equals("delete")) {
                    WorldManager.getWorlds().forEach(items::add);
                }
                break;
        }
        return items;
    }
}
