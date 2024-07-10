package me.i2000c.newalb.utils2;

import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.i2000c.newalb.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
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
    
    private static boolean checkFlag(@Nullable Entity entity, Location location, Flag flag) {
        if(!worldGuardEnabled) {
            return true;
        } else {
            Player player = entity instanceof Player ? (Player) entity : null;
            return WorldGuardManagerAux.checkFlag(player, location, flag);
        }
    }
    
    public static boolean canBuild(@Nullable Entity entity, Location location) {
        return checkFlag(entity, location, Flag.BLOCK_PLACE);
    }
    
    public static boolean canBreak(@Nullable Entity entity, Location location) {
        return checkFlag(entity, location, Flag.BLOCK_BREAK);
    }
    
    public static boolean canUse(@Nullable Entity entity, Location location) {
        return checkFlag(entity, location, Flag.USE);
    }
    
    public static boolean canPasteSchematic(@Nullable Entity entity, Location location) {
        return checkFlag(entity, location, Flag.SCHEMATIC_PASTE);
    }
    
    public static boolean canPvp(Player player, Location location) {
        return checkFlag(player, location, Flag.PVP);
    }
    
    public static boolean canMobDamage(Location location) {
        return checkFlag(null, location, Flag.MOB_DAMAGE);
    }
    
    public static boolean canEntityDamage(Entity entity, Location location) {
        return entity instanceof Player ? canPvp((Player) entity, location)
                                        : canMobDamage(location);
    }
    
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static enum Flag {
        BLOCK_PLACE(true),
        BLOCK_BREAK(true),
        USE(true),
        SCHEMATIC_PASTE(true),
        PVP(true),
        MOB_DAMAGE(false);
        
        private final boolean checkBuildFlag;
        
        public String getWorldGuardFlagName() {
            return this.name().toLowerCase().replace('_', '-');
        }
    }
}
