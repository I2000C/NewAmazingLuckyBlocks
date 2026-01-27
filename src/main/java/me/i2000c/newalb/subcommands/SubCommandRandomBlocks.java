package me.i2000c.newalb.subcommands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.random.RandomBlocks;
import me.i2000c.newalb.utils.random.RandomBlocksOptions;

public class SubCommandRandomBlocks implements SubCommand {
    
    private static final String USAGE_MESSAGE = "&cUsage: &7/alb randomblocks stop &cor: &7/alb randomblocks <rad x> <rad y> <rad <z";
    
    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if(!checkHasPermission(sender, "Commands.RandomBlocks-permission")) {
            return false;
        }
        
        if(args.size() == 1 && args.get(0).equals("stop")) {
            // /alb randomblocks stop
            RandomBlocks.stopRandomBlocksMainTask(sender);
            return true;
        }
        
        Player player;
        if(args.size() == 5) {
            // /alb randomblocks rad_x rad_y rad_z block_number mode
            if(!checkNotConsole(sender)) {
                return false;
            }
            player = (Player) sender;
        } else if(args.size() == 6) {
            player = getOnlinePlayer(sender, args.get(args.size() - 1));
            if(player == null) {
                return false;
            }
        } else {
            Logger.sendMessage(USAGE_MESSAGE, sender);
            return false;
        }
        
        int radx;
        int rady;
        int radz;
        int blocks;
        try {
            radx = Integer.parseInt(args.get(0));
        } catch(NumberFormatException numberFormatException) {
            Logger.sendMessage("&cRad x must be an &ainteger&c, not &6" + args.get(0), sender);
            return false;
        }

        try {
            rady = Integer.parseInt(args.get(1));
        } catch(NumberFormatException numberFormatException) {
            Logger.sendMessage("&cRad y must be an &ainteger&c, not &6" + args.get(1), sender);
            return false;
        }

        try {
            radz = Integer.parseInt(args.get(2));
        } catch(NumberFormatException numberFormatException) {
            Logger.sendMessage("&cRad z must be an &ainteger&c, not &6" + args.get(2), sender);
            return false;
        }

        try {
            blocks = Integer.parseInt(args.get(3));
        } catch(NumberFormatException numberFormatException) {
            Logger.sendMessage("&cBlock number must be an &ainteger&c, not &6" + args.get(3), sender);
            return false;
        }
        
        boolean allowFloatingBlocks;
        boolean preScanSafeLocations;
        switch(args.get(4)) {
            case "true":
                allowFloatingBlocks = true;
                preScanSafeLocations = false;
                break;
            case "false":
                allowFloatingBlocks = false;
                preScanSafeLocations = false;
                break;
            case "force":
                allowFloatingBlocks = false;
                preScanSafeLocations = true;
                break;
            default:
                Logger.sendMessage("&cMode must be &atrue&c, &afalse &cor &aforce&c, not &6" + args.get(4), sender);
                return false;
        }
        
        RandomBlocksOptions options = RandomBlocksOptions.builder()
                                                         .radx(radx)
                                                         .rady(rady)
                                                         .radz(radz)
                                                         .blocks(blocks)
                                                         .allowFloatingBlocks(allowFloatingBlocks)
                                                         .preScanSafeLocations(preScanSafeLocations)
                                                         .senderToNotify(sender)
                                                         .sendFinishMessage(true)
                                                         .location(player.getLocation())
                                                         .senderToNotify(sender)
                                                         .build();
        if(RandomBlocks.placeRandomBlocks(options)) {
            String placeblocks = ConfigManager.getLangMessage("PlacingBlocks");
            Logger.sendMessage(placeblocks, sender);
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if(args.size() > 1 && args.get(0).equals("stop")) {
            return Collections.emptyList();
        }
        
        List<String> items = new ArrayList<>();
        switch(args.size()) {
            case 1:
                items.add("stop");
                // x
                items.add("5");
                break;
            case 2:
            case 3:
            case 4:
                // y, z and number of blocks
                items.add("5");
                break;
            case 5:
                // mode
                items.add("true");
                items.add("false");
                items.add("force");
                break;
            case 6:
                items.addAll(getOnlinePlayers());
                break;
            default:
                return Collections.emptyList();
        }
        
        return items;
    }
}
