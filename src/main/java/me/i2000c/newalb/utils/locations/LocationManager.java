package me.i2000c.newalb.utils.locations;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Synchronized;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.lucky_blocks.rewards.TypeManager;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.tasks.Task;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationManager {
    private static final String DATABASE_FILENAME = "luckyblocks-locs.db";
    private static final String OLD_CONFIG_FILENAME = "luckyblocks-locs.yml";
    private static final String OLD_LOCATIONS_KEY = "Locations";
    private static final String DATA_FOLDER_NAME = "data";
    private static final int LOCATION_BATCH_SIZE = 100;
    private static final long LOCATION_BATCH_DELAY = 5L;
    
    private static boolean initialized = false;
    private static Plugin plugin = null;
    private static Connection connection = null;
    private static Task deleteLocationsTask = null;
    
    private static boolean isSaveLocations() {
        return ConfigManager.getMainConfig().getBoolean("LuckyBlock.SaveLocations");
    }
    
    public static void initialize(Plugin plugin) {
        if(initialized) return;
        
        LocationManager.plugin = plugin;
        
        File dataFolder = new File(plugin.getDataFolder(), DATA_FOLDER_NAME);
        dataFolder.mkdirs();
        File databaseFile = new File(dataFolder, DATABASE_FILENAME);
        
        String url = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
        try {
            connection = DriverManager.getConnection(url);
            createTable();
        } catch(Exception ex) {
            Logger.err("An error occurred while creating locations database: ");
            ex.printStackTrace();
        }
        
        migrateYamlToSQLite();
        initialized = true;
    }
    
    private static void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS locations ("
                       + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                       + "world TEXT NOT NULL, "
                       + "x INTEGER NOT NULL, "
                       + "y INTEGER NOT NULL, "
                       + "z INTEGER NOT NULL, "
                       + "chunk_x INTEGER NOT NULL, "
                       + "chunk_z INTEGER NOT NULL"
                   + ");"
                   + ""
                   + "CREATE INDEX IF NOT EXISTS idx_locations_world ON locations(world);"
                   + "CREATE INDEX IF NOT EXISTS idx_locations_world_chunk ON locations(world, chunk_x, chunk_z);"
                   + "CREATE UNIQUE INDEX IF NOT EXISTS idx_locations_unique_location ON locations(world, x, y, z);";
        
        @Cleanup PreparedStatement statement = connection.prepareStatement(sql);
        statement.execute();
    }
    
    private static void migrateYamlToSQLite() {
        File dataFolder = new File(plugin.getDataFolder(), DATA_FOLDER_NAME);
        File yamlFile = new File(dataFolder, OLD_CONFIG_FILENAME);
        if(yamlFile.exists()) {
            Logger.log("Migrating LuckyBlock locations from yml to SQLite ...");
            Logger.log("This can take several minutes to complete and the server won't respond while that");
            Config config = new Config();
            config.loadConfig(yamlFile);
            
            ConfigurationSection section = config.getConfigurationSection(OLD_LOCATIONS_KEY, null);
            Set<Location> locations = new HashSet<>();
            if(section != null) {
                section.getKeys(false).forEach(key -> {
                    Location loc = config.getLocation(OLD_LOCATIONS_KEY + "." + key);
                    locations.add(loc);
                });
            }
            
            LocationManager.registerLocations(locations);
            yamlFile.delete();
            Logger.log("LuckyBlock locations migration was completed");
        }
    }
    
    public static void releaseDatabaseConnection() {
        try {
            if(connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @SneakyThrows
    public static void registerLocation(Location location) {
        if(!isSaveLocations()) {
            return;
        }
        
        String sql = "INSERT OR IGNORE INTO locations(world, x, y, z, chunk_x, chunk_z) VALUES(?, ?, ?, ?, ?, ?)";
        @Cleanup PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, location.getWorld().getName());
        statement.setInt(2, location.getBlockX());
        statement.setInt(3, location.getBlockY());
        statement.setInt(4, location.getBlockZ());
        statement.setInt(5, location.getBlockX() >> 4);
        statement.setInt(6, location.getBlockZ() >> 4);
        statement.executeUpdate();
    }
    
    public static void registerLocations(Collection<Location> locations) {
        if(!isSaveLocations()) {
            return;
        }
        
        String sql = "INSERT OR IGNORE INTO locations(world, x, y, z, chunk_x, chunk_z) VALUES(?, ?, ?, ?, ?, ?)";
        try {
            @Cleanup PreparedStatement statement = connection.prepareStatement(sql);
            connection.setAutoCommit(false);
            for(Location loc : locations) {
                statement.setString(1, loc.getWorld().getName());
                statement.setInt(2, loc.getBlockX());
                statement.setInt(3, loc.getBlockY());
                statement.setInt(4, loc.getBlockZ());
                statement.setInt(5, loc.getBlockX() >> 4);
                statement.setInt(6, loc.getBlockZ() >> 4);
                statement.addBatch();
            }
            
            statement.executeBatch();
            connection.commit();
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException ex) { }
        }
    }
    
    public static Set<Location> getLocations() {
        return getLocations(null);
    }
    
    @SneakyThrows
    public static Set<Location> getLocations(@Nullable String world) {
        String sql = "SELECT world, x, y, z FROM locations";
        if(world != null) {
            sql += " WHERE world = ?";
        }
        sql += " ORDER BY world, chunk_x, chunk_z, x, y, z";
        
        @Cleanup PreparedStatement statement = connection.prepareStatement(sql);
        if(world != null) {
            statement.setString(1, world);
        }
        
        Set<Location> result = new HashSet<>();
        @Cleanup ResultSet rs = statement.executeQuery();
        while(rs.next()) {
            String worldName = rs.getString("world");
            World bukkitWorld = Bukkit.getWorld(worldName);
            if(bukkitWorld != null) {
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");
                Location location = new Location(bukkitWorld, x, y, z);
                result.add(location);            
            }
        }
        
        return result;
    }
    
    @SneakyThrows
    public static void removeLocation(@NonNull Location location) {
    	if(!isSaveLocations()) {
            return;
        }
        
        String sql = "DELETE FROM locations WHERE world=? AND x=? AND y=? AND z=?";
        @Cleanup PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, location.getWorld().getName());
        statement.setInt(2, location.getBlockX());
        statement.setInt(3, location.getBlockY());
        statement.setInt(4, location.getBlockZ());
        statement.executeUpdate();
    }
    
    public static void removeLocations(@Nullable Runnable onLocationsRemoved) {
        removeLocations(null, onLocationsRemoved);
    }
    
    @SneakyThrows
    @Synchronized
    public static void removeLocations(@Nullable String worldName, @Nullable Runnable onLocationsRemoved) {
        if(!isSaveLocations()) {
            return;
        }
        
        if(deleteLocationsTask != null && deleteLocationsTask.isStarted()) {
            return;
        }
        
        deleteLocationsTask = new Task() {
            final Set<Location> locations = getLocations(worldName);
            final Iterator<Location> iterator = locations.iterator();
            int locationsRemoved = 0;
            
            @Override
            public void run() {
                if(locations.isEmpty()) {
                    cancel();
                    if(onLocationsRemoved != null) {
                        onLocationsRemoved.run();
                    }
                    return;
                }
                
                if(locationsRemoved == 0) {
                    Logger.log("Removing " + locations.size() + " locations ...");
                    Logger.log(String.format(" [%d/%d] (%.2f %%)", locationsRemoved, locations.size(), locationsRemoved * 100.0 / locations.size()));
                }
                
                int counter = 0;
                while(iterator.hasNext() && counter < LOCATION_BATCH_SIZE) {
                    Location loc = iterator.next();
                    if(TypeManager.getType(loc.getBlock()) != null){
                        loc.getBlock().setType(Material.AIR);                
                    }
                    counter++;
                    locationsRemoved++;
                }
                
                Logger.log(String.format(" [%d/%d] (%.2f %%)", locationsRemoved, locations.size(), locationsRemoved * 100.0 / locations.size()));
                
                if(!iterator.hasNext()) {
                    this.cancel();
                    
                    String sql = "DELETE FROM locations";
                    if(worldName != null) {
                        sql += " WHERE world = ?";
                    }
                    
                    try {
                        @Cleanup PreparedStatement statement = connection.prepareStatement(sql);
                        if(worldName != null) {
                            statement.setString(1, worldName);
                        }
                        statement.executeUpdate();
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                    
                    if(onLocationsRemoved != null) {
                        onLocationsRemoved.run();
                    }
                }
            }
        };
        deleteLocationsTask.runTask(0L, LOCATION_BATCH_DELAY);
    }
}
