package me.i2000c.newalb.config;

import java.io.File;
import org.bukkit.plugin.Plugin;

public class ReadWriteConfig extends ReadOnlyConfig{    
    public ReadWriteConfig(Plugin plugin, String resourceName, boolean saveComments){
        super(plugin, resourceName, saveComments);
    }
    
    public ReadWriteConfig(Plugin plugin, String resourceName, String filename, boolean saveComments){
        super(plugin, resourceName, filename, saveComments);
    }
    
    public ReadWriteConfig(Plugin plugin, File file){
        super(plugin, file);
    }
    
    @Override
    public void saveConfig(){
        super.saveConfig();
    }
    
    @Override
    public double getConfigVersion(){
        return INVALID_CONFIG_VERSION;
    }
}
