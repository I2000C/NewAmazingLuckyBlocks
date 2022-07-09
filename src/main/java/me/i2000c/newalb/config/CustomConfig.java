package me.i2000c.newalb.config;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomConfig{
    private static final String VERSION_KEY = "Version";
    
    private final JavaPlugin plugin;
    private final File file;
    private final String resourceName;
    private FileConfiguration config;
    
    public CustomConfig(JavaPlugin plugin, String filename, String resourceName){
        this.plugin = plugin;
        this.file = new File(filename);
        this.resourceName = resourceName;
    }
    
    public CustomConfig(JavaPlugin plugin, String filename){
        this(plugin, filename, filename);
    }
    
    public File getFile(){
        return this.file;
    }
    
    public FileConfiguration getBukkitConfig(){
        return this.config;
    }
}
