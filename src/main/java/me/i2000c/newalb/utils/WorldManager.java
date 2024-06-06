package me.i2000c.newalb.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.config.ConfigManager;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WorldManager {
    private static final String CONFIG_FILENAME = "data/worlds.yml";
    private static final String WORLD_LIST_KEY = "Worlds-list";
    private static final String WORLD_LIST_MODE_KEY = "WorldListMode";
    
    private static Set<String> worlds = new HashSet<>();
    private static WorldListMode worldListMode;
    
    private static Config config = new Config();
    
    private static void saveWorlds(){
        List<String> worldsList = new ArrayList<>(worlds);
        Collections.sort(worldsList);
        config.set(WORLD_LIST_MODE_KEY, worldListMode);
        config.set(WORLD_LIST_KEY, worldsList);        
        config.saveConfig(CONFIG_FILENAME);
    }
    
    public static WorldListMode getWorldListMode() {
        return worldListMode;
    }
    public static void setWorldListMode(WorldListMode worldListMode) {
        WorldManager.worldListMode = worldListMode;
        saveWorlds();
    }
    
    public static Set<String> getWorlds(){
        return worlds;
    }
    
    public static boolean addWorld(String worldName) {
        if(worlds.add(worldName)) {
            saveWorlds();        
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean deleteWorld(String worldName) {
        if(worlds.remove(worldName)) {
            saveWorlds();
            return true;
        } else {
            return false;
        }
    }
    
    public static void addAllWorlds() {
        Bukkit.getWorlds().forEach(world -> worlds.add(world.getName()));
        saveWorlds();
    }
    
    public static void deleteAllWorlds() {
        worlds.clear();
        saveWorlds();
    }
    
    public static void toggleAllWorlds() {
        List<String> serverWorlds = new ArrayList<>();
        Bukkit.getWorlds().forEach(world -> {
            if(!worlds.contains(world.getName())) {
                serverWorlds.add(world.getName());
            }
        });
        
        worlds.clear();
        worlds.addAll(serverWorlds);
        
        saveWorlds();
    }
    
    public static boolean isEnabled(String worldName){
        switch(worldListMode) {
            case DISABLED: return true;
            case WHITELIST: return worlds.contains(worldName);
            case BLACKLIST: return !worlds.contains(worldName);
            default: return false;
        }
    }
    public static boolean isContained(String worldName) {
        return worlds.contains(worldName);
    }
   
    public static void reloadWorlds(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        config.loadConfig(CONFIG_FILENAME);
        
        try {
            worldListMode = config.getEnum(WORLD_LIST_MODE_KEY, WorldListMode.class);
        } catch(Exception ex) {
            worldListMode = WorldListMode.DISABLED;
        }
        
        worlds.clear();
        List<String> worldsList = config.getStringList(WORLD_LIST_KEY, Collections.emptyList());
        worldsList.forEach(worldName -> {
            int semicolonIndex = worldName.indexOf(';');
            if(semicolonIndex > 0) {
                worldName = worldName.substring(0, semicolonIndex);
            }
            
            worlds.add(worldName);
        });
        
        String message = ConfigManager.getLangMessage("World-loading")
                                      .replace("%worlds%", worlds.size() + "");
        Logger.log(message);
//</editor-fold>
    }
    
    
    
    public static enum WorldListMode {
        //<editor-fold defaultstate="collapsed" desc="Code">
        DISABLED,
        WHITELIST,
        BLACKLIST;
        
        public WorldListMode next() {
            int index = ordinal() + 1;
            if(index >= VALUES.length) {
                index = 0;
            }
            
            return VALUES[index];
        }
        
        @Override
        public String toString() {
            switch(this) {
                case DISABLED: return "&c&lDISABLED";
                case WHITELIST: return "&f&lWHITELIST";
                case BLACKLIST: return "&8&lBLACKLIST";
                default: return "";
            }
        }
        
        private static final WorldListMode[] VALUES = values();
//</editor-fold>
    }
}