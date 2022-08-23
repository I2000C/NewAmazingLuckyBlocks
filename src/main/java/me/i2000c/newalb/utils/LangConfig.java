package me.i2000c.newalb.utils;

import java.io.File;
import java.util.Arrays;
import me.i2000c.newalb.config.ReadOnlyConfig;
import me.i2000c.newalb.utils.logger.LogLevel;
import me.i2000c.newalb.utils.logger.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class LangConfig{
    private static Plugin plugin;
    private static final String LANG_FILE_KEY = "LangFile";
    private static final int LANG_VERSION = 1;
    
    private static ReadOnlyConfig config;
    public static void initialize(Plugin plugin){
        //<editor-fold defaultstate="collapsed" desc="Code">
        // Update all lang files
        Arrays.stream(Language.getValues()).forEach(language -> {
            ReadOnlyConfig langConfig = new ReadOnlyConfig(plugin, language.getLangFileName(), true) {
                @Override
                public int getConfigVersion(){
                    return LANG_VERSION;
                }
            };
            langConfig.loadConfig();
        });
        
        config = new ReadOnlyConfig(plugin, null, "null", true){
            @Override
            public int getConfigVersion(){
                return LANG_VERSION;
            }
        };
        
        LangConfig.plugin = plugin;
//</editor-fold>
    }
    
    public static void loadConfig(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        // Load lang file specified in config.yml
        String filename = ConfigManager.getConfig().getString(LANG_FILE_KEY);
        String langFileName = Language.LANG_FOLDER_NAME + '/' + filename;
        config.setConfigFile(langFileName);

        File file = config.getConfigFile();
        if(file.exists()){
            config.loadConfig();
        }else{
            Logger.log("Lang file \"" + filename + "\" doesn't exist", LogLevel.WARN, false);
            Logger.log("Using English language to avoid errors", LogLevel.WARN, false);
            config.clearConfig();
            try{
                config.getBukkitConfig().load(plugin.getResource(Language.EN.getLangFileName()));
            }catch(Exception ex){
                Logger.log("An error occurred while loading English language:", LogLevel.ERROR, false);
                Logger.log(ex, LogLevel.ERROR);
            }
        }
//</editor-fold>
    }
    
    public static FileConfiguration getMessages(){
        return config.getBukkitConfig();
    }
}
