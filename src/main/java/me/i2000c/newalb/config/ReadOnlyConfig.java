package me.i2000c.newalb.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import me.i2000c.newalb.utils.logger.LogLevel;
import me.i2000c.newalb.utils.logger.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public abstract class ReadOnlyConfig{
    protected static final int INVALID_CONFIG_VERSION = -1;
    protected static final String VERSION_KEY = "ConfigVersion";
    
    private final Plugin plugin;
    private final String resourceName;
    private File configFile;
    private final boolean saveComments;
    private YamlConfigurationUTF8 config;
        
    public ReadOnlyConfig(Plugin plugin, String resourceName, String filename, boolean saveComments){
        this.plugin = plugin;
        this.resourceName = resourceName;
        this.configFile = new File(plugin.getDataFolder(), filename);
        this.saveComments = saveComments;
    }
    
    public ReadOnlyConfig(Plugin plugin, String resourceName, boolean saveComments){
        this(plugin, resourceName, resourceName, saveComments);
    }
    
    public ReadOnlyConfig(Plugin plugin, File file){
        this.plugin = plugin;
        this.resourceName = null;
        this.configFile = file;
        this.config = null;
        this.saveComments = false;        
    }
    
    public final void setConfigFile(String filename){
        this.configFile = new File(plugin.getDataFolder(), filename);
    }
    public final File getConfigFile(){
        return this.configFile;
    }
    
    public final YamlConfigurationUTF8 getBukkitConfig(){
        return this.config;
    }
    
    public final void loadConfig(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        clearConfig();
        
        if(configFile.exists()){
            config.load(configFile);
        }else if(resourceName != null){
            copyResource();
            config.load(configFile);
        }
        
        updateConfig();
//</editor-fold>
    }
    
    protected void saveConfig(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        try{
            config.save(configFile);
        }catch(Exception ex){
            Logger.log("An error occurred while saving configuration in file \"" + configFile.getAbsolutePath() + "\"", LogLevel.ERROR);
            Logger.log(ex, LogLevel.ERROR);
        }
//</editor-fold>
    }
    
    public void clearConfig(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(saveComments){
            config = new CommentedConfig();
        }else{
            config = new YamlConfigurationUTF8();
        }
//</editor-fold>
    }
    
    public abstract int getConfigVersion();
    
    private boolean updateConfig(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(resourceName == null){
            return false;
        }
        
        int currentVersion = getConfigVersion();
        if(currentVersion == INVALID_CONFIG_VERSION){
            return false;
        }
        
        int fileVersion = config.getInt(VERSION_KEY, Integer.MIN_VALUE);
        if(fileVersion >= currentVersion){
            return false;
        }
        
        // Update config file
        File bakFile = new File(configFile.getParentFile(), configFile.getName() + ".bak");
        try{
            Path configFilePath = Paths.get(configFile.getAbsolutePath());
            Path bakFilePath = Paths.get(bakFile.getAbsolutePath());
            Files.move(configFilePath, bakFilePath, StandardCopyOption.REPLACE_EXISTING);
        }catch(IOException ex){
            Logger.log("Couldn't create backup file of " + configFile.getName(), LogLevel.ERROR);
            return false;
        }
        
        try{
            clearConfig();
            config.load(plugin.getResource(resourceName));
            FileConfiguration oldConfig = new YamlConfigurationUTF8();
            oldConfig.load(bakFile);
            
            for(String key : config.getKeys(true)){
                if(config.isConfigurationSection(key)){
                    continue;
                }
                
                if(key.equals(VERSION_KEY)){
                    continue;
                }
                
                Object object = oldConfig.get(key);
                if(object == null || oldConfig.isConfigurationSection(key)){
                    continue;
                }
                
                if(oldConfig.isList(key) && (config instanceof CommentedConfig)){
                    int oldListSize = config.getList(key).size();
                    int newListSize = oldConfig.getList(key).size();
                    if(oldListSize != newListSize){
                        ((CommentedConfig) config).updateCommentLines(key, newListSize - oldListSize);
                    }
                }
                
                config.set(key, object);
            }
            
            // Update config version
            config.set(VERSION_KEY, currentVersion);
            
            saveConfig();
            return true;
        }catch(Exception ex){
            Logger.log("An error occurred while updating configuration in file \"" + configFile.getAbsolutePath() + "\"", LogLevel.ERROR);
            Logger.log(ex, LogLevel.ERROR);
            return false;
        }
//</editor-fold>
    }
    
    private void copyResource(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        configFile.getParentFile().mkdirs();
        try(InputStream input = plugin.getResource(resourceName);
                OutputStream output = new FileOutputStream(configFile)){
            byte[] buffer = new byte[1024];
            int bytesReaded;
            while((bytesReaded = input.read(buffer)) > 0){
                output.write(buffer, 0, bytesReaded);
            }
        }catch(Exception ex){
            Logger.log(String.format("An error occurred while copying resource %s to %s",
                    resourceName, configFile.getAbsolutePath()), LogLevel.ERROR);
            Logger.log(ex, LogLevel.ERROR);
            Logger.log("Plugin is going to shutdown", LogLevel.WARN);
            Bukkit.getPluginManager().disablePlugin(plugin);
            throw new Error(ex);
        }
//</editor-fold>
    }
}
