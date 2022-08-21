package me.i2000c.newalb.config;

import org.bukkit.plugin.Plugin;

public class ReadWriteConfig extends ReadOnlyConfig{    
    public ReadWriteConfig(Plugin plugin, String resourceName, boolean saveComments){
        super(plugin, resourceName, saveComments);
    }
    
    public ReadWriteConfig(Plugin plugin, String resourceName, String filename, boolean saveComments){
        super(plugin, resourceName, filename, saveComments);
    }
    
    @Override
    public void saveConfig(){
        super.saveConfig();
    }
    
    @Override
    public int getConfigVersion(){
        return INVALID_CONFIG_VERSION;
    }
}
