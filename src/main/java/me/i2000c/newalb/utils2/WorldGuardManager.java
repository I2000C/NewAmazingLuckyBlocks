package me.i2000c.newalb.utils2;

import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
    
    private static boolean checkFlag(@Nullable Player player, Location location, Flag flag) {
        if(!worldGuardEnabled) {
            return true;
        } else {
            return WorldGuardManagerAux.checkFlag(player, location, flag);
        }
    }
    
    public static boolean canBuild(@Nullable Player player, Location location) {
        return checkFlag(player, location, Flag.BLOCK_PLACE);
    }
    
    public static boolean canBreak(@Nullable Player player, Location location) {
        return checkFlag(player, location, Flag.BLOCK_BREAK);
    }
    
    public static boolean canUse(@Nullable Player player, Location location) {
        return checkFlag(player, location, Flag.USE);
    }
    
    public static boolean canPasteSchematic(@Nullable Player player, Location location) {
        return checkFlag(player, location, Flag.SCHEMATIC_PASTE);
    }
    
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static enum Flag {
        BLOCK_PLACE(true),
        BLOCK_BREAK(true),
        USE(true),
        SCHEMATIC_PASTE(true);
        
        private final boolean checkBuildFlag;
        
        public String getWorldGuardFlagName() {
            return this.name().toLowerCase().replace('_', '-');
        }
    }
}
