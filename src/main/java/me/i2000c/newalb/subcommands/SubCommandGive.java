package me.i2000c.newalb.subcommands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.listeners.interact.SpecialItems;
import me.i2000c.newalb.lucky_blocks.rewards.LuckyBlockType;
import me.i2000c.newalb.lucky_blocks.rewards.TypeManager;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.menus.GiveMenu;

public class SubCommandGive implements SubCommand {
    
    private static final String USAGE_MESSAGE = "&cUsage: &7/alb give [<wands, objects, luckyblocks, lucky_tool, other_items...> [amount] [player]]";
    
    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        // /alb give
        if(args.isEmpty()) {
            if(!checkHasPermission(sender, "Commands.Give.GiveMenu")) {
                return false;
            }
            
            if(!checkNotConsole(sender)) {
                return false;
            }
                        
            GiveMenu.reset();
            GiveMenu.openGiveMenu((Player) sender);
            return true;
        }
        
        Player target;
        int amount = 1;
        String selectedItem = args.get(0);
        
        switch(args.size()) {
            case 1:
                // /alb give <item>
                if(!checkNotConsole(sender)) {
                    return false;
                }
                
                target = (Player) sender;
                break;
            case 2:
                try {
                    // /alb give <item> [amount]
                    amount = Integer.parseInt(args.get(1));
                    if(!checkNotConsole(sender)) {
                        return false;
                    }
                    target = (Player) sender;
                } catch(NumberFormatException ex) {
                    // /alb give <item> [player]
                    target = getOnlinePlayer(sender, args.get(1));
                    if(target == null) {
                        return false;
                    }
                }
                break;
            case 3:
                // /alb give <item> [amount] [player]
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
                
                String loadwands = ConfigManager.getLangMessage("LoadingWands");
                Logger.sendMessage(loadwands, sender);
                for(SpecialItem wand : SpecialItems.getWands()) {
                    ItemStack stack = wand.getItem();
                    stack.setAmount(amount);
                    target.getInventory().addItem(stack);
                }
                break;
            case "objects":
                if(!checkHasPermission(sender, "Commands.Give.Objects")) {
                    return false;
                }
                
                String loadobjects = ConfigManager.getLangMessage("LoadingObjects");                    
                Logger.sendMessage(loadobjects, sender);
                for(SpecialItem object : SpecialItems.getObjects()) {
                    ItemStack stack = object.getItem();
                    stack.setAmount(amount);
                    target.getInventory().addItem(stack);
                }
                break;
            case "luckyblocks":
                if(!checkHasPermission(sender, "Commands.Give.LuckyBlocks")) {
                    return false;
                }
                
                String loadblocks = ConfigManager.getLangMessage("LoadingBlocks");
                Logger.sendMessage(loadblocks, sender);
                for(LuckyBlockType type : TypeManager.getTypes()) {
                    ItemStack stack = type.getItem().toItemStack();
                    stack.setAmount(amount);
                    target.getInventory().addItem(stack);
                }
                break;
            default:
                if(!checkHasPermission(sender, "Commands.Give.OtherItems")) {
                    return false;
                }
                
                ItemStack stack = null;
                
                // Get special item
                SpecialItem specialItem = SpecialItems.getByName(selectedItem);
                
                // If selected item is not a special item, check if it is a LuckyBlockType
                if(specialItem == null){
                    for(LuckyBlockType type : TypeManager.getTypes()) {
                        if(type.getTypeName().equals(selectedItem)) {
                            stack = type.getItem().toItemStack();
                            break;
                        }
                    }
                    
                    if(stack == null) {
                        Logger.sendMessage(USAGE_MESSAGE, sender);
                        return false;
                    }
                } else {
                    stack = specialItem.getItem();
                }
                
                String loadSpecialItem = ConfigManager.getLangMessage("LoadingSpecialItem");
                Logger.sendMessage(loadSpecialItem, sender);                    
                stack.setAmount(amount);
                target.getInventory().addItem(stack);
                break;
        }
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        List<String> items = new ArrayList<>();
        switch(args.size()) {
            case 1:
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
