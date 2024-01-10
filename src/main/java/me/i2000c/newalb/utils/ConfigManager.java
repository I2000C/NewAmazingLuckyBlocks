package me.i2000c.newalb.utils;

import me.i2000c.newalb.config.ReadOnlyConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigManager extends ReadOnlyConfig{
    private static final double CONFIG_VERSION = 1.9;
    
    private ConfigManager(Plugin plugin){
        super(plugin, "config.yml", true);
    }

    @Override
    public double getConfigVersion(){
        return CONFIG_VERSION;
    }
    
    private static ConfigManager manager;
    public static void initialize(Plugin plugin){
        manager = new ConfigManager(plugin);
    }
    public static ConfigManager getManager(){
        return manager;
    }
  
    public static FileConfiguration getConfig(){
        return manager.getBukkitConfig();
    }
}
