package me.i2000c.newalb.subcommands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.lucky_blocks.rewards.LuckyBlockType;
import me.i2000c.newalb.lucky_blocks.rewards.PackManager;
import me.i2000c.newalb.lucky_blocks.rewards.TypeManager;
import me.i2000c.newalb.utils.logging.Logger;

public class SubCommandLuckyEvent implements SubCommand {
    
    private static final String USAGE_MESSAGE = "&cUsage: &7/alb luckyEvent [LuckyBlock type] [player]";
    
    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if(!checkHasPermission(sender, "Commands.LuckyEvent-permission")) {
            return false;
        }
        
        if(PackManager.IS_LOADING_PACKS()) {
            Logger.sendMessage(ConfigManager.getLangMessage("Loading.not-fully-loaded"), sender);
            return false;
        }
        
        Player player;
        LuckyBlockType type;
        switch(args.size()) {
            case 0:
                // /alb luckyEvent
                if(!checkNotConsole(sender)) {
                    return false;
                }
                player = (Player) sender;
                type = TypeManager.getRandomLuckyBlockType();
                break;
            case 1:
                String typeNameOrPlayerName = args.get(0);
                type = TypeManager.getType(typeNameOrPlayerName);
                if(type == null) {
                    // /alb luckyEvent [player]
                    player = getOnlinePlayer(sender, typeNameOrPlayerName);
                    if(player == null) {
                        return false;
                    }
                    type = TypeManager.getRandomLuckyBlockType();
                } else {
                    // /alb luckyEvent [LuckyBlock type]
                    if(!checkNotConsole(sender)) {
                        return false;
                    }
                    player = (Player) sender;
                }
                break;
            case 2:
                String typeName = args.get(0);
                String playerName = args.get(1);
                player = getOnlinePlayer(sender, playerName);
                if(player == null) {
                    return false;
                }
                type = TypeManager.getType(typeName);
                if(type == null) {
                    Logger.sendMessage("&cLuckyBlock type &b" + typeName + "&cdoesn't exist", sender);
                    return false;
                }
                break;
            default:
                Logger.sendMessage(USAGE_MESSAGE, sender);
                return false;
        }
        
        type.execute(player, player.getLocation());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        List<String> items = new ArrayList<>();
        switch(args.size()) {
            case 1:
                TypeManager.getTypes().forEach(type -> items.add(type.getTypeName()));
                items.addAll(getOnlinePlayers());
                break;
            case 2:
                String typeOrPlayer = args.get(1);
                LuckyBlockType type = TypeManager.getType(typeOrPlayer);
                if(type != null) {
                    items.addAll(getOnlinePlayers());
                } else {
                    // Not suggesting player names since a player name has already been introduced
                }
                break;
            default:
                return Collections.emptyList();
        }
        
        return items;
    }
}
