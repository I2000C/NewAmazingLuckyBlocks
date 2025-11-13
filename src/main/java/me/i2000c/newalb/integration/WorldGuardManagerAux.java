package me.i2000c.newalb.integration;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.sk89q.worldguard.session.SessionManager;
import java.util.EnumMap;
import javax.annotation.Nullable;

import me.i2000c.newalb.api.version.MinecraftVersion;
import me.i2000c.newalb.integration.WorldGuardManager.Flag;
import me.i2000c.newalb.utils.reflection.ReflectionManager;

import org.bukkit.Location;
import org.bukkit.entity.Player;

class WorldGuardManagerAux {
    
    private static final RegionAssociable NON_MEMBER_REGION_ASSOCIABLE = regions -> Association.NON_MEMBER;
    
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
        
    public static boolean checkFlag(@Nullable Player player, Location location, @Nullable Flag flag) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(flag == null) {
            return true;
        }
        
        final StateFlag[] flags = {flagsMap.get(flag)};
        
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()) {
            LocalPlayer localPlayer = player != null ? WorldGuardPlugin.inst().wrapPlayer(player) : null;
            boolean hasBypass = player != null && (boolean) ReflectionManager.callMethod(getSessionManager(), "hasBypass", player, location.getWorld());
            if(!hasBypass) {
                Object query = ReflectionManager.callMethod(getRegionContainer(), "createQuery");
                RegionAssociable ra = localPlayer != null ? localPlayer : NON_MEMBER_REGION_ASSOCIABLE;
                if(flag.isCheckBuildFlag()) {
                    return ReflectionManager.callMethod(query, "testBuild", location, ra, flags);
                } else {
                    return ReflectionManager.callMethod(query, "testState", location, ra, flags);
                }
            }
        } else {
            com.sk89q.worldedit.util.Location worldEditLocation = BukkitAdapter.adapt(location);
            com.sk89q.worldedit.world.World worldEditWorld = BukkitAdapter.adapt(location.getWorld());
            
            LocalPlayer localPlayer = player != null ? WorldGuardPlugin.inst().wrapPlayer(player) : null;
            boolean hasBypass = localPlayer != null && getSessionManager().hasBypass(localPlayer, worldEditWorld);
            if(!hasBypass) {
                RegionQuery query = ((RegionContainer) getRegionContainer()).createQuery();
                RegionAssociable ra = localPlayer != null ? localPlayer : NON_MEMBER_REGION_ASSOCIABLE;
                if(flag.isCheckBuildFlag()) {
                    return query.testBuild(worldEditLocation, ra, flags);
                } else {
                    return query.testState(worldEditLocation, ra, flags);
                }
            }
        }
        
        return true;
//</editor-fold>
    }
}
