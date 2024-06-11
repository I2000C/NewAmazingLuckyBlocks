package me.i2000c.newalb.utils2;

import java.util.EnumMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.sk89q.worldguard.session.SessionManager;

import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.reflection.ReflectionManager;
import me.i2000c.newalb.utils2.WorldGuardManager.Flag;

class WorldGuardManagerAux {
    
    private static EnumMap<Flag, StateFlag> flagsMap;

    private static FlagRegistry flagRegistry = null;
    private static Object regionContainer = null;
    private static SessionManager sessionManager = null;
    
    public static void initialize() throws IllegalStateException {
        flagsMap = new EnumMap<>(Flag.class);
        for(Flag flag : Flag.values()) {
            String flagName = flag.getWorldGuardFlagName();
            StateFlag stateFlag = (StateFlag) getFlagRegistry().get(flagName);
            if(stateFlag == null) {
                stateFlag = new StateFlag(flagName, false);
                getFlagRegistry().register(stateFlag);
            }
            flagsMap.put(flag, stateFlag);
        }
    }
    
    private static FlagRegistry getFlagRegistry() {
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(flagRegistry == null) {
            if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()) {
                flagRegistry = ReflectionManager.callMethod(WorldGuardPlugin.inst(), "getFlagRegistry");
            } else {
                flagRegistry = WorldGuard.getInstance().getFlagRegistry();
            }
        }
        return flagRegistry;
//</editor-fold>
    }
    
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
        
    private static boolean checkFlag(Player player, Location location, StateFlag[] flags) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(flags == null || flags.length == 0) {
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
    
    public static boolean checkFlag(Player player, Location location, Flag... flags) {
        if(flags == null) return true;
        
        StateFlag[] stateFlags = new StateFlag[flags.length];
        for(int i=0; i<flags.length; i++) {
            stateFlags[i] = flagsMap.get(flags[i]);
        }
        
        return checkFlag(player, location, stateFlags);
    }
}
