package me.i2000c.newalb.subcommands;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.integration.Schematic;
import me.i2000c.newalb.utils.logging.Logger;

public class SubCommandLoadSchematic implements SubCommand {
    
    private static final String USAGE_MESSAGE = "&cUsage: &7/alb loadSchematic <schematicName>";
    
    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if(!checkHasPermission(sender, "Commands.Schematic")) {
            return false;
        }
        
        if(!checkNotConsole(sender)) {
            return false;
        }
        
        if(NewAmazingLuckyBlocks.getWorldEditPlugin() == null) {
            Logger.sendMessage("&cYou need WorldEdit in order to use that command", sender);
            return false;
        }
        
        if(args.size() != 1) {
            Logger.sendMessage(USAGE_MESSAGE, sender);
            return false;
        }
        
        String schematicName = args.get(0);
        File file = new File(ConfigManager.getDataFolder(), "schematics" + File.separator + schematicName);
        if(!file.exists()) {
            Logger.sendMessage("&cSchematic &6\"" + schematicName + "\" &cdoesn't exist", sender);
            return false;
        }
        
        Player player = (Player) sender;
        try {
            Schematic schematic = new Schematic();
            schematic.loadFromFile(file, player.getWorld());
            schematic.copyToPlayerClipboard(player);
            Logger.sendMessage("&aSchematic &6\"" + schematicName + "\" &ahas been &eloaded &ainto your clipboard", sender);
            return true;
        } catch(Exception ex) {
            Logger.sendMessage("&cAn error occurred. More info in the Console", sender);
            Logger.err("An error occurred:");
            ex.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if(args.size() != 1) {
            return Collections.emptyList();
        }
        
        List<String> items = new ArrayList<>();
        File schematicFolder = new File(ConfigManager.getDataFolder(), "schematics");
        if(schematicFolder.exists()) {
            Arrays.asList(schematicFolder.list()).forEach(items::add);
        }
        return items;
    }
}
