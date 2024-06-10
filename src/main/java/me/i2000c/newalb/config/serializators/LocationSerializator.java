package me.i2000c.newalb.config.serializators;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.i2000c.newalb.config.Config;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationSerializator implements ConfigSerializerDeserializer<Location> {
    
    @Getter private static final LocationSerializator instance = new LocationSerializator();
    
    @Override
    public void serialize(Config config, String path, Location value) {
        config.set(path + ".world", value.getWorld().getName());
        config.set(path + ".x", value.getX());
        config.set(path + ".y", value.getY());
        config.set(path + ".z", value.getZ());
        config.set(path + ".pitch", value.getPitch());
        config.set(path + ".yaw", value.getYaw());
    }

    @Override
    public Location deserialize(Config config, String path) {
        String worldName = config.getString(path + ".world");
        double x = config.getDouble(path + ".x");
        double y = config.getDouble(path + ".y");
        double z = config.getDouble(path + ".z");
        float pitch = config.getFloat(path + ".pitch", 0F);
        float yaw = config.getFloat(path + ".yaw", 0F);
        
        World world = Bukkit.getWorld(worldName);
        if(world == null) {
            return null;
        }
        
        Location loc = new Location(world, x, y, z, yaw, pitch);
        return loc;
    }    
}
