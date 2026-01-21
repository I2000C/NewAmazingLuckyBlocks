package me.i2000c.newalb.config;

import java.io.File;
import lombok.Getter;
import me.i2000c.newalb.utils.logging.Logger;

import org.bukkit.plugin.Plugin;

public class ConfigManager {
    
    private static final String LANG_FILE_KEY = "LangFile";
    private static final String MAIN_CONFIG_FILENAME = "config.yml";
    private static final String LANG_FOLDER_NAME = "lang";
    
    @Getter private static boolean initialized = false;
    @Getter private static Plugin plugin;
    @Getter private static Config mainConfig;
    @Getter private static Config langConfig;
    
    public static void initialize(Plugin plugin) {
        ConfigManager.initialized = true;
        ConfigManager.plugin = plugin;
        ConfigManager.mainConfig = new Config();
        ConfigManager.langConfig = new Config();
    }
    
    public static File getDataFolder() {
        return plugin.getDataFolder();
    }
    
    public static void loadConfigs() {
        mainConfig.clearConfig();
        langConfig.clearConfig();
        
        // Load and update main config
        File mainConfigFile = new File(getDataFolder(), MAIN_CONFIG_FILENAME);
        if(!mainConfigFile.exists()) {
            mainConfig.loadConfigFromResource(MAIN_CONFIG_FILENAME);
            mainConfig.saveConfig(mainConfigFile);
        } else {
            mainConfig.loadConfig(mainConfigFile);
            mainConfig.updateConfig(MAIN_CONFIG_FILENAME, mainConfigFile, 
                    "LuckyBlock.DropOnBlockBreak.locationFiltering",
                    "LuckyBlock.DropOnBlockBreak.enabledBlocks",
                    "Objects.ItemStealer.item-filters");
        }
        
        // Update all lang configs
        File langFolder = new File(getDataFolder(), LANG_FOLDER_NAME);
        if(!langFolder.exists()) {
            for(Language lang : Language.VALUES) {
                Config config = new Config();
                config.loadConfigFromResource(lang.getLangFileName());
                config.saveConfig(lang.getLangFileName());
            }
        } else {
            for(Language lang : Language.VALUES) {
                Config config = new Config();
                config.loadConfig(lang.getLangFileName());
                config.updateConfig(lang.getLangFileName(), lang.getLangFileName());
            }
        }
        
        // Load lang config
        String langFileName = mainConfig.getString(LANG_FILE_KEY);
        File langConfigFile = new File(getDataFolder(), String.format("%s/%s", LANG_FOLDER_NAME, langFileName));
        try {
            if(!langConfigFile.exists()) {
                throw new InternalError("Lang file \"" + langFileName + "\" doesn't exist");
            }
            langConfig.loadConfig(langConfigFile);
        } catch(InternalError ex) {
            Logger.warn("An error occurred while loading lang file \"" + langConfigFile + "\":");
            ex.printStackTrace();
            Logger.warn("Using internal English language file to avoid more errors");
            langConfig.loadConfigFromResource(Language.EN.getLangFileName());
        }
    }
    
    public static String getLangMessage(String path) {
        try {
            return getLangConfig().getString(path);
        } catch(IllegalArgumentException ex) {
            Logger.warn("Lang message \"" + path + "\" doesn't exist in selected lang file. Using internal English file entry");
            Config config = new Config();
            config.loadConfigFromResource(Language.EN.getLangFileName());
            return config.getString(path);
        }
    }
    
    
    @Getter
    public static enum Language{
        EN,
        ES,
        HU,
        NL,
        TR,
        CH_TW;
        
        public static final Language[] VALUES = values();  
        
        private final String langFileName;
        
        private Language() {
            this.langFileName = String.format("%s/lang_%s.yml", LANG_FOLDER_NAME, this.name());
        }
    }
}
