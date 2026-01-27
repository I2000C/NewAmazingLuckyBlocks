package me.i2000c.newalb.subcommands;

import java.io.File;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.api.version.MinecraftVersion;
import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.integration.Schematic;
import me.i2000c.newalb.utils.logging.Logger;

public class SubCommandSaveSchematic implements SubCommand {
    
    private static final String USAGE_MESSAGE = "&cUsage: &7/alb saveSchematic <schematicName>";
    
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
        String suffix = MinecraftVersion.CURRENT_VERSION.isLegacyVersion() ? ".schematic" : ".schem";
        if(schematicName.endsWith(suffix)) {
            Logger.sendMessage("&cSchematic files must end in &6" + suffix, sender);
            return false;
        }
        
        File file = new File(ConfigManager.getDataFolder(), "schematics" + File.separator + schematicName);
        Player player = (Player) sender;
        try {
            Schematic schematic = new Schematic();
            schematic.loadFromPlayerClipboard(player);
            schematic.saveToFile(file, player.getWorld());
            Logger.sendMessage("&aSchematic &6\"" + schematicName + "\" &ahas been &bsaved &afrom your clipboard", sender);
            return true;
        } catch(Exception ex) {
            if(ex.getClass().getName().contains("EmptyClipboardException")) {
                Logger.sendMessage("&cYour clipboard is empty", sender);
            } else {
                Logger.sendMessage("&cAn error occurred. More info in the Console", sender);
                Logger.err("An error occurred:");
                ex.printStackTrace();
            }
            return false;
        }
    }
}
