package me.i2000c.newalb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.subcommands.*;
import me.i2000c.newalb.utils.logging.Logger;

public class CommandManager implements CommandExecutor, TabCompleter {
    
    private final Map<String, SubCommand> subCommands;
    
    public CommandManager() {
        subCommands = new TreeMap<>();
        subCommands.put("help", new SubCommandHelp());
        subCommands.put("reload", new SubCommandReload());
        subCommands.put("give", new SubCommandGive());
        subCommands.put("take", new SubCommandTake());
        subCommands.put("randomblocks", new SubCommandRandomBlocks());
        subCommands.put("clear", new SubCommandClear());
        subCommands.put("worlds", new SubCommandWorlds());
        subCommands.put("menu", new SubCommandMenu());
        subCommands.put("return", new SubCommandReturn());
        subCommands.put("loadSchematic", new SubCommandLoadSchematic());
        subCommands.put("loadSchem", subCommands.get("loadSchematic"));
        subCommands.put("loadS", subCommands.get("loadSchematic"));
        subCommands.put("saveSchematic", new SubCommandSaveSchematic());
        subCommands.put("saveSchem", subCommands.get("saveSchematic"));
        subCommands.put("saveS", subCommands.get("saveSchematic"));
        subCommands.put("removeSchematic", new SubCommandRemoveSchematic());
        subCommands.put("removeSchem", subCommands.get("removeSchematic"));
        subCommands.put("removeS", subCommands.get("removeSchematic"));
        subCommands.put("getSkull", new SubCommandGetSkull());
        subCommands.put("luckyEvent", new SubCommandLuckyEvent());
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String commandName = command.getName().toLowerCase();
        if(!commandName.equals("alb") && !commandName.equals("nalb")){
            return false;
        }
        
        if(args.length == 0) {
            Logger.sendMessage(NewAmazingLuckyBlocks.getInstance().name + " &a" + NewAmazingLuckyBlocks.getInstance().version, sender);
            Logger.sendMessage(ConfigManager.getLangMessage("HelpMessage"), sender);
            return false;
        }
        
        String subCommandName = args[0];
        SubCommand subCommand = subCommands.get(subCommandName);
        if(subCommand != null) {
            List<String> subArgs = Arrays.asList(args);
            subArgs = subArgs.subList(1, subArgs.size());
            return subCommand.execute(sender, subArgs);
        } else {
            Logger.sendMessage(ConfigManager.getLangMessage("UnknownCommand"), sender);
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String commandName = command.getName().toLowerCase();
        if(!commandName.equals("alb") && !commandName.equals("nalb")){
            return Collections.emptyList();
        }
        
        if(!sender.hasPermission(ConfigManager.getMainConfig().getString("Commands.Tab-completer"))){
            return Collections.emptyList();
        }
        
        if(args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            String subCommandPrefix = args[0];
            StringUtil.copyPartialMatches(subCommandPrefix, subCommands.keySet(), suggestions);
            return suggestions;
        } else {
            // args.length is always greater than 0
            String subcommandName = args[0];
            SubCommand subCommand = subCommands.get(subcommandName);
            if(subCommand != null) {
                List<String> subArgs = Arrays.asList(args);
                subArgs = subArgs.subList(1, subArgs.size());
                List<String> items = subCommand.onTabComplete(sender, subArgs);
                if(items.isEmpty()) {
                    return Collections.emptyList();
                } else {
                    List<String> suggestions = new ArrayList<>();
                    String token = subArgs.get(subArgs.size() - 1);
                    StringUtil.copyPartialMatches(token, items, suggestions);
                    Collections.sort(suggestions);
                    return suggestions;
                }
            } else {
                return Collections.emptyList();
            }
        }
    }
}
