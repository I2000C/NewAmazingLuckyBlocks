package me.i2000c.newalb.utils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import me.i2000c.newalb.config.ReadOnlyConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class LangConfig{
    private static Plugin plugin;
    private static final String LANG_FILE_KEY = "LangFile";
    private static final double LANG_VERSION = 1.2;
    
    private static ReadOnlyConfig englishConfig;
    private static ReadOnlyConfig config;
    public static void initialize(Plugin plugin){
        //<editor-fold defaultstate="collapsed" desc="Code">
        LangConfig.plugin = plugin;
        
        // Update all lang files
        Arrays.stream(Language.getValues()).forEach(language -> {
            ReadOnlyConfig langConfig = new ReadOnlyConfig(plugin, language.getLangFileName(), true) {
                @Override
                public double getConfigVersion(){
                    return LANG_VERSION;
                }
            };
            langConfig.loadConfig();
        });
        
        config = new ReadOnlyConfig(plugin, null, "null", true){
            @Override
            public double getConfigVersion(){
                return LANG_VERSION;
            }
        };
        
        // Load English lang
        englishConfig = new ReadOnlyConfig(plugin, null){
            @Override
            public double getConfigVersion(){
                return INVALID_CONFIG_VERSION;
            }
        };
        englishConfig.clearConfig();
        try{            
            englishConfig.getBukkitConfig().load(plugin.getResource(Language.EN.getLangFileName()));
        }catch(Exception ex){
            Logger.err("An error occurred while loading English language:", false);
            Logger.err(ex, false);
        }
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
            Logger.warn("Lang file \"" + filename + "\" doesn't exist", false);
            Logger.warn("Using English language to avoid errors", false);
            config.clearConfig();
            try{
                config.getBukkitConfig().load(plugin.getResource(Language.EN.getLangFileName()));
            }catch(Exception ex){
                Logger.err("An error occurred while loading English language:", false);
                Logger.err(ex, false);
            }
        }
//</editor-fold>
    }
    
    public static FileConfiguration getMessages(){        
        //<editor-fold defaultstate="collapsed" desc="Code">
        return config.getBukkitConfig();
//</editor-fold>
    }
    public static String getMessage(String path){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String message;
        if(config.getBukkitConfig().isString(path)){
            message = config.getBukkitConfig().getString(path);
        }else{
            Logger.warn("Key \"" + path + "\" wasn't found in lang config");
            Logger.warn("A value from lang_EN.yml will be used instead");
            message = englishConfig.getBukkitConfig().getString(path);
        }
        
        return message;
//</editor-fold>
    }
    public static List<String> getMessageList(String path){
        //<editor-fold defaultstate="collapsed" desc="Code">
        List<String> messageList;
        if(config.getBukkitConfig().isList(path)){
            messageList = config.getBukkitConfig().getStringList(path);
        }else{
            Logger.warn("Key \"" + path + "\" wasn't found in lang config");
            Logger.warn("A value from lang_EN.yml will be used instead");
            messageList = englishConfig.getBukkitConfig().getStringList(path);
        }
        
        return messageList;
//</editor-fold>
    }
}
