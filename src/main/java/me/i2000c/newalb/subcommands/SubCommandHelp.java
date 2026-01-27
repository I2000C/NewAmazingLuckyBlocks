package me.i2000c.newalb.subcommands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.utils.logging.Logger;

public class SubCommandHelp implements SubCommand {
    
    private int getMaxPage() {
        int maxPage;
        for(maxPage=1; maxPage<100; maxPage++) {
            if(!ConfigManager.getLangConfig().existsPath("Helpmenu" + (maxPage+1))) {
                break;
            }
        }
        return maxPage;
    }
    
    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if(args.size() > 1) {
            Logger.sendMessage("&cUsage: &7/alb help [number]", sender);
            return false;
        }
        
        int page;
        if(args.isEmpty()) {
            page = 1;
        } else {
            try {
                page = Integer.parseInt(args.get(0));
            } catch(NumberFormatException ex) {
                page = -1;
            }
        }
        
        int maxPage = getMaxPage();
        if(page < 1 || page > maxPage) {
            Logger.sendMessage("&cThat page doesn't exist", sender);
            return false;
        }
        
        String key = "Helpmenu" + (page > 1 ? page : "");
        ConfigurationSection helpmenuSection = ConfigManager.getLangConfig().getConfigurationSection(key);
        for(String line : helpmenuSection.getKeys(false)) {
            String text = helpmenuSection.getString(line);
            Logger.sendMessage(text
                    .replace("%prefix%", NewAmazingLuckyBlocks.getInstance().prefix)
                    .replace("%maxPages%", String.valueOf(maxPage)), sender, false);
        }
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if(args.size() != 1) {
            return Collections.emptyList();
        }
        
        int maxPage = getMaxPage();
        List<String> items = new ArrayList<>(maxPage);
        for(int i=1; i<=maxPage; i++) {
            items.add(i + "");
        }
        return items;
    }
}
