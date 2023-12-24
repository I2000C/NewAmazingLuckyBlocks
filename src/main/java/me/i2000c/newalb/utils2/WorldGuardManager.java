package me.i2000c.newalb.utils2;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.sk89q.worldguard.session.SessionManager;
import java.util.Objects;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.reflection.ReflectionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class WorldGuardManager {
    
    private static final String WORLDGUARD_PLUGIN_NAME = "WorldGuard";
    public static final boolean WORLDGUARD_ENABLED;
    
    static {
        PluginManager pm = Bukkit.getPluginManager();
        WORLDGUARD_ENABLED = pm.isPluginEnabled(WORLDGUARD_PLUGIN_NAME);
    }
    
    private static Object regionContainer = null;
    private static SessionManager sessionManager = null;
    
    private static Object getRegionContainer() {
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(regionContainer == null) {
            if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()) {
                regionContainer = ReflectionManager.callMethod(WorldGuardPlugin.inst(), "getRegionContainer");
            } else {
                regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
            }
        }
        return regionContainer;
//</editor-fold>
    }
    
    private static SessionManager getSessionManager() {
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(sessionManager == null) {
            if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()) {
                sessionManager = ReflectionManager.callMethod(WorldGuardPlugin.inst(), "getSessionManager");
            } else {
                sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
            }
        }
        return sessionManager;
//</editor-fold>
    }
        
    private static boolean checkFlag(Player player, Location location, Object flagsObject) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!WORLDGUARD_ENABLED) {
            return true;
        }
        
        Objects.requireNonNull(flagsObject);
        StateFlag[] flags = (StateFlag[]) flagsObject;
        
        if(flags.length == 0) {
            return true;
        }
        
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()) {
            boolean hasBypass = ReflectionManager.callMethod(getSessionManager(), "hasBypass", player, location.getWorld());
            if(!hasBypass) {
                Object query = ReflectionManager.callMethod(getRegionContainer(), "createQuery");
                return ReflectionManager.callMethod(query, "testState", location, player, flags);
            }
        } else {
            com.sk89q.worldedit.util.Location worldEditLocation = BukkitAdapter.adapt(location);
            com.sk89q.worldedit.world.World worldEditWorld = BukkitAdapter.adapt(location.getWorld());
            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            if(!getSessionManager().hasBypass(localPlayer, worldEditWorld)) {
                RegionQuery query = ((RegionContainer) getRegionContainer()).createQuery();
                return query.testState(worldEditLocation, localPlayer, flags);
            }
        }
        
        return true;
//</editor-fold>
    }    
    
    public static boolean canBuild(Player player, Location location) {
        if(player == null) return true;
        return checkFlag(player, location, Flag.BUILD_FLAGS);
    }
    
    public static boolean canBreak(Player player, Location location) {
        if(player == null) return true;
        return checkFlag(player, location, Flag.BREAK_FLAGS);
    }
    
    public static boolean canUse(Player player, Location location) {
        if(player == null) return true;
        return checkFlag(player, location, Flag.USE_FLAGS);
    }
    
    private static enum Flag {
        BUILD, 
        BLOCK_PLACE, 
        BLOCK_BREAK, 
        USE;
        
        private final Object worldGuardFlag;
        
        private Flag() {
            //<editor-fold defaultstate="collapsed" desc="Code">
            if(!WORLDGUARD_ENABLED) {
                this.worldGuardFlag = null;
                return;
            }
            
            if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()) {
                this.worldGuardFlag = ReflectionManager.getStaticFieldValue("com.sk89q.worldguard.protection.flags.DefaultFlag", this.name());
            } else {
                this.worldGuardFlag = ReflectionManager.getStaticFieldValue("com.sk89q.worldguard.protection.flags.Flags", this.name());
            }
//</editor-fold>
        }
        
        public static final Object BUILD_FLAGS;
        public static final Object BREAK_FLAGS;
        public static final Object USE_FLAGS;
        
        static {
            //<editor-fold defaultstate="collapsed" desc="Code">
            if(WORLDGUARD_ENABLED) {
                BUILD_FLAGS = new StateFlag[] {(StateFlag) Flag.BUILD.worldGuardFlag, (StateFlag) Flag.BLOCK_PLACE.worldGuardFlag};
                BREAK_FLAGS = new StateFlag[] {(StateFlag) Flag.BUILD.worldGuardFlag, (StateFlag) Flag.BLOCK_BREAK.worldGuardFlag};
                USE_FLAGS   = new StateFlag[] {(StateFlag) Flag.BUILD.worldGuardFlag, (StateFlag) Flag.USE.worldGuardFlag};
            } else {
                BUILD_FLAGS = null;
                BREAK_FLAGS = null;
                USE_FLAGS   = null;
            }
//</editor-fold>
        }
    }
}
