package me.i2000c.newalb.utils2;

import lombok.Getter;
import me.i2000c.newalb.utils.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class WorldGuardManager {
    
    private static final String WORLDGUARD_PLUGIN_NAME = "WorldGuard";
    
    @Getter
    private static boolean worldGuardEnabled = false;
    
    public static void initialize() {
        PluginManager pm = Bukkit.getPluginManager();
        worldGuardEnabled = pm.getPlugin(WORLDGUARD_PLUGIN_NAME) != null;
        
        if(worldGuardEnabled) {
            try {
                WorldGuardManagerAux.initialize();
            } catch(IllegalStateException ex) {
                Logger.warn("A new flag has been tried to be registered after WorldGuard has been enabled");
                Logger.warn("WorldGuard support will be disabled to avoid problems");
                worldGuardEnabled = false;
            }
        }
    }
    
    private static boolean checkFlag(Player player, Location location, Flag flag) {
        if(player == null || !worldGuardEnabled) {
            return true;
        } else {
            return WorldGuardManagerAux.checkFlag(player, location, Flag.BUILD, flag);
        }
    }
    
    public static boolean canBuild(Player player, Location location) {
        return checkFlag(player, location, Flag.BLOCK_PLACE);
    }
    
    public static boolean canBreak(Player player, Location location) {
        return checkFlag(player, location, Flag.BLOCK_BREAK);
    }
    
    public static boolean canUse(Player player, Location location) {
        return checkFlag(player, location, Flag.USE);
    }
    
    public static boolean canPasteSchematic(Player player, Location location) {
        return checkFlag(player, location, Flag.SCHEMATIC_PASTE);
    }
    
    protected static enum Flag {
        BUILD,
        BLOCK_PLACE,
        BLOCK_BREAK,
        USE,
        SCHEMATIC_PASTE;
        
        public String getWorldGuardFlagName() {
            return this.name().toLowerCase().replace('_', '-');
        }
    }
}
