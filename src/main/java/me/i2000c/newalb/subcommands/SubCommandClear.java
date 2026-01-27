package me.i2000c.newalb.subcommands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import me.i2000c.newalb.utils.locations.LocationManager;
import me.i2000c.newalb.utils.locations.WorldManager;
import me.i2000c.newalb.utils.logging.Logger;

public class SubCommandClear implements SubCommand {
    
    private static final String USAGE_MESSAGE = "&cUsage: &7/alb clear [world]";
    
    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if(!checkHasPermission(sender, "Commands.Clear-permission")) {
            return false;
        }
        
        if(args.isEmpty()) {
            LocationManager.removeLocations(() -> Logger.sendMessage("&aAll LuckyBlocks were removed", sender));
            return true;
        } else if(args.size() == 1) {
            String world = args.get(0);
            if(WorldManager.isEnabled(world)) {
                LocationManager.removeLocations(world, () -> Logger.sendMessage("&aAll LuckyBlocks of the world &b" + world + " &awere removed", sender));
                return true;
            }
            
            Logger.sendMessage("&cThe world &b" + world + " &cisn't in the world list", sender);
            return false;
        } else {
            Logger.sendMessage(USAGE_MESSAGE, sender);
            return false;
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if(args.size() != 1) {
            return Collections.emptyList();
        }
        
        List<String> items = new ArrayList<>();
        Bukkit.getWorlds().forEach(world -> items.add(world.getName()));
        return items;
    }
}
