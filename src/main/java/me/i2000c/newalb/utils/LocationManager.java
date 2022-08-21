package me.i2000c.newalb.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import me.i2000c.newalb.config.ReadWriteConfig;
import me.i2000c.newalb.custom_outcomes.rewards.TypeManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class LocationManager extends ReadWriteConfig{
    private LocationManager(Plugin plugin){
        super(plugin, null, "data/luckyblocks-locs.yml", false);
    }
    
    private static LocationManager manager;
    public static void initialize(Plugin plugin){
        manager = new LocationManager(plugin);
    }
    
    private static final String LOCATIONS_KEY = "Locations";
    private static final Set<Location> locations = new HashSet<>();
    
    public static void loadLocations(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        locations.clear();
        FileConfiguration config = manager.getBukkitConfig();

        if(config.isConfigurationSection(LOCATIONS_KEY)){
            for(String key : config.getConfigurationSection(LOCATIONS_KEY).getKeys(false)){
                String path = LOCATIONS_KEY + "." + key;

                World world = Bukkit.getWorld(config.getString(path + ".world"));
                int x = config.getInt(path + ".x");
                int y = config.getInt(path + ".y");
                int z = config.getInt(path + ".z");

                Location loc = new Location(world, x, y, z);
                locations.add(loc);
            }
        }
//</editor-fold>
    }
    
    public static void saveLocations(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        manager.clearConfig();
        FileConfiguration config = manager.getBukkitConfig();
        
        int i = 0;
        for(Location loc : locations){
            String path = LOCATIONS_KEY + "." + i;
            
            config.set(path + ".world", loc.getWorld().getName());
            config.set(path + ".x", loc.getBlockX());
            config.set(path + ".y", loc.getBlockY());
            config.set(path + ".z", loc.getBlockZ());
            
            i++;
        }
        
        manager.saveConfig();
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
