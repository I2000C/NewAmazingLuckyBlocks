package me.i2000c.newalb.subcommands;

import java.io.File;
import java.util.List;

import org.bukkit.command.CommandSender;

import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.utils.logging.Logger;

public class SubCommandRemoveSchematic extends SubCommandLoadSchematic {
    
    private static final String USAGE_MESSAGE = "&cUsage: &7/alb removeSchematic <schematicName>";
    
    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if(!checkHasPermission(sender, "Commands.Schematic")) {
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
        
        if(file.delete()) {
            Logger.sendMessage("&aSchematic &6\"" + schematicName + "\" &ahas been &4removed", sender);
            return true;
        } else {
            Logger.sendMessage("&aSchematic &6\"" + schematicName + "\" &ccouldn't be removed", sender);
            return false;
        }
    }
}
