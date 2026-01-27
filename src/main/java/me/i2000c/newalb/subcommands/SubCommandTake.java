package me.i2000c.newalb.subcommands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.listeners.interact.SpecialItems;
import me.i2000c.newalb.lucky_blocks.rewards.LuckyBlockType;
import me.i2000c.newalb.lucky_blocks.rewards.TypeManager;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.OtherUtils;

public class SubCommandTake implements SubCommand {
    
    private static final String USAGE_MESSAGE = "&cUsage: &7/alb take <all, wands, objects, luckyblocks, lucky_tool, other_items...> [amount] [player]";
    
    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        // /alb take
        if(args.isEmpty()) {
            Logger.sendMessage(USAGE_MESSAGE, sender);
            return false;
        }
        
        Player target;
        int amount = 1;
        String selectedItem = args.get(0);
        
        switch(args.size()) {
            case 1:
                // /alb take <item>
                if(!checkNotConsole(sender)) {
                    return false;
                }
                
                target = (Player) sender;
                break;
            case 2:
                try {
                    // /alb take <item> [amount]
                    amount = Integer.parseInt(args.get(1));
                    if(!checkNotConsole(sender)) {
                        return false;
                    }
                    target = (Player) sender;
                } catch(NumberFormatException ex) {
                    // /alb take <item> [player]
                    target = getOnlinePlayer(sender, args.get(1));
                    if(target == null) {
                        return false;
                    }
                }
                break;
            case 3:
                // /alb take <item> [amount] [player]
                target = getOnlinePlayer(sender, args.get(2));
                if(target == null){
                    return false;
                }
                
                try {
                    amount = Integer.parseInt(args.get(1));
                } catch(NumberFormatException ex) {
                    Logger.sendMessage("&cInvalid selected amount: " + args.get(1), sender);
                    return false;
                }
                break;
            default:
                Logger.sendMessage(USAGE_MESSAGE, sender);
                return false;
        }
        
        switch(selectedItem) {
            case "wands":
                if(!checkHasPermission(sender, "Commands.Give.Wands")) {
                    return false;
                }
                
                OtherUtils.removePlayerItems(target, amount, itemStack -> {
                    SpecialItem specialItem = SpecialItems.getByItemStack(itemStack);
                    return specialItem != null && specialItem.isWand();
                });
                return true;
            case "objects":
                if(!checkHasPermission(sender, "Commands.Give.Objects")) {
                    return false;
                }
                
                OtherUtils.removePlayerItems(target, amount, itemStack -> {
                    SpecialItem specialItem = SpecialItems.getByItemStack(itemStack);
                    return specialItem != null && !specialItem.isWand();
                });
                return true;
            case "luckyblocks":
                if(!checkHasPermission(sender, "Commands.Give.LuckyBlocks")) {
                    return false;
                }

                OtherUtils.removePlayerItems(target, amount, itemStack -> {
                    LuckyBlockType type = TypeManager.getType(itemStack);
                    return type != null;
                });
                return true;
            case "all":
                if(!checkHasPermission(sender, "Commands.Give.Wands")) {
                    return false;
                }
                if(!checkHasPermission(sender, "Commands.Give.LuckyBlocks")) {
                    return false;
                }
                if(!checkHasPermission(sender, "Commands.Give.Objects")) {
                    return false;
                }
                
                OtherUtils.removePlayerItems(target, amount, itemStack -> {
                    SpecialItem specialItem = SpecialItems.getByItemStack(itemStack);
                    if(specialItem != null){
                        return true;
                    }
                    
                    LuckyBlockType type = TypeManager.getType(itemStack);
                    return type != null;
                });
                return true;
            default:
                if(!checkHasPermission(sender, "Commands.Give.OtherItems")) {
                    return false;
                }
                
             // Get special item
                SpecialItem specialItem = SpecialItems.getByName(selectedItem);
                if(specialItem != null){
                    OtherUtils.removePlayerItems(target, amount, itemStack -> {
                        return SpecialItems.getByItemStack(itemStack) == specialItem;
                    });
                    return true;
                }

                // If selected item is not a special item, check if it is a LuckyBlockType
                LuckyBlockType luckyBlockType = TypeManager.getType(selectedItem);
                if(luckyBlockType != null){
                    OtherUtils.removePlayerItems(target, amount, itemStack -> {
                        LuckyBlockType type = TypeManager.getType(itemStack);
                        return luckyBlockType.equals(type);
                    });
                    return true;
                }                
                
                Logger.sendMessage(USAGE_MESSAGE, sender);
                return false;
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        List<String> items = new ArrayList<>();
        switch(args.size()) {
            case 1:
                items.add("all");
                items.add("wands");
                items.add("objects");
                items.add("luckyblocks");
                items.addAll(SpecialItems.getItemsNames());
                break;
            case 2:
                items.add("5");
                items.addAll(getOnlinePlayers());
                break;
            case 3:
                String amountOrPlayer = args.get(1);
                try {
                    Integer.parseInt(amountOrPlayer);
                    items.addAll(getOnlinePlayers());
                } catch(NumberFormatException ex) {
                    // Not suggesting player names since a player name has already been introduced
                }
                
                break;
            default:
                return Collections.emptyList();
        }
        
        return items;
    }
}
