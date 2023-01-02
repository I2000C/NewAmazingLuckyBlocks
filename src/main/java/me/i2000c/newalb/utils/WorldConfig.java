package me.i2000c.newalb.utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import me.i2000c.newalb.config.ReadWriteConfig;
import org.bukkit.plugin.Plugin;

public class WorldConfig extends ReadWriteConfig{
    private static final String WORLD_LIST_KEY = "Worlds-list";
    private static final String WORLD_LIST_MODE_KEY = "WorldListMode";
    private WorldConfig(Plugin plugin){
        super(plugin, null, "data/worlds.yml", false);
    }
    
    private static WorldConfig worldConfig;
    private static HashSet<String> worlds;
    private static TreeSet<String> sortedWorlds;
    private static WorldListMode worldListMode;
    public static void initialize(Plugin plugin){
        worldConfig = new WorldConfig(plugin);
        worlds = new HashSet<>();
        sortedWorlds = new TreeSet<>();
    }
    
    private static void saveWorlds(){
        List<String> worldsList = new LinkedList<>(sortedWorlds);
        worldConfig.getBukkitConfig().set(WORLD_LIST_MODE_KEY, worldListMode.name());
        worldConfig.getBukkitConfig().set(WORLD_LIST_KEY, worldsList);        
        worldConfig.saveConfig();
    }
    
    public static WorldListMode getWorldListMode() {
        return worldListMode;
    }
    public static void setWorldListMode(WorldListMode worldListMode) {
        WorldConfig.worldListMode = worldListMode;
        saveWorlds();
    }
    
    public static Set<String> getWorlds(){
        return sortedWorlds;
    }
    
    public static boolean addWorld(String worldName) {
        if(worlds.add(worldName)) {
            sortedWorlds.add(worldName);
            saveWorlds();        
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean deleteWorld(String worldName) {
        if(worlds.remove(worldName)) {
            sortedWorlds.remove(worldName);
            saveWorlds();
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean isEnabled(String worldName){
        switch(worldListMode) {
            case DISABLED: return true;
            case WHITELIST: return worlds.contains(worldName);
            case BLACKLIST: return !worlds.contains(worldName);
            default: return false;
        }
    }
   
    public static void reloadWorlds(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        worldConfig.loadConfig();
        
        String worldListTypeString = worldConfig.getBukkitConfig().getString(WORLD_LIST_MODE_KEY);
        try {
            worldListMode = WorldListMode.valueOf(worldListTypeString);
        } catch(Exception ex) {
            worldListMode = WorldListMode.DISABLED;
        }
        
        worlds.clear();
        sortedWorlds.clear();
        List<String> worldsList = worldConfig.getBukkitConfig().getStringList(WORLD_LIST_KEY);
        worldsList.forEach(worldName -> {
            int semicolonIndex = worldName.indexOf(';');
            if(semicolonIndex > 0) {
                worldName = worldName.substring(0, semicolonIndex);
            }
            
            worlds.add(worldName);
            sortedWorlds.add(worldName);
        });
        
        String message = LangConfig.getMessage("World-loading")
                .replace("%worlds%", worlds.size() + "");
        Logger.log(message);
//</editor-fold>
    }
    
    
    
    public static enum WorldListMode {
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
    }
}