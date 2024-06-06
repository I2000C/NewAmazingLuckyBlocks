package me.i2000c.newalb.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.custom_outcomes.rewards.TypeManager;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationManager {    
    private static final String CONFIG_FILENAME = "data/luckyblocks-locs.yml";
    private static final String LOCATIONS_KEY = "Locations";
    private static final Set<Location> locations = new HashSet<>();
    
    private static Config config = new Config();
    
    public static void loadLocations(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        config.loadConfig(CONFIG_FILENAME);

        locations.clear();
        ConfigurationSection section = config.getConfigurationSection(LOCATIONS_KEY, null);
        if(section != null) {
            section.getKeys(false).forEach(key -> {
                Location loc = config.getLocation(LOCATIONS_KEY + "." + key);
                locations.add(loc);
            });
        }
//</editor-fold>
    }
    
    public static void saveLocations(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        config.clearConfig();
        
        int i = 0;
        for(Location loc : locations){
            config.set(LOCATIONS_KEY + "." + i, loc);
            i++;
        }
        
        config.saveConfig(CONFIG_FILENAME);
//</editor-fold>
    }
    
    public static void registerLocation(Location location){
        //<editor-fold defaultstate="collapsed" desc="Code">
        locations.add(location);
//</editor-fold>
    }
    
    public static void removeLocations(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        for(Iterator<Location> iter = locations.iterator(); iter.hasNext();){
            Location next = iter.next();
            if(TypeManager.getType(next.getBlock()) != null){
                next.getBlock().setType(Material.AIR);                
            }
            iter.remove();
        }
//</editor-fold>
    }
    
    public static void removeLocations(World world){
        //<editor-fold defaultstate="collapsed" desc="Code">
        for(Iterator<Location> iter = locations.iterator(); iter.hasNext();){
            Location next = iter.next();
            if(next.getWorld().equals(world)){
                if(TypeManager.getType(next.getBlock()) != null){
                    next.getBlock().setType(Material.AIR);
                }
                iter.remove();
            }
        }
//</editor-fold>
    }
}
