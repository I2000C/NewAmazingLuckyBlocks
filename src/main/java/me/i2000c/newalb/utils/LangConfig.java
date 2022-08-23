package me.i2000c.newalb.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.logger.LogLevel;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.config.YamlConfigurationUTF8;
import org.bukkit.configuration.file.FileConfiguration;

public class LangConfig{
    private static FileConfiguration langCfg = null;
    private static File langFile = null;
    
    public static FileConfiguration getMessages(){
        if(langCfg == null){
            reloadMessages();
        }
        return langCfg;
    }
    
    public static void reloadMessages(){
        langFile = new File(NewAmazingLuckyBlocks.getInstance().getDataFolder(), "lang.yml");
        if(langFile.exists()){
            langCfg = YamlConfigurationUTF8.loadConfiguration(langFile);
        }else{
            langCfg = new YamlConfigurationUTF8();
            try{
                langFile.createNewFile();
            }catch(IOException ex){
                Logger.log("An error occurred:", LogLevel.INFO);
                ex.printStackTrace();
            }
        }
        Reader defConfigStream;
        try{
            defConfigStream = new InputStreamReader(NewAmazingLuckyBlocks.getInstance().getResource("lang.yml"), "UTF8");
            langCfg.setDefaults(YamlConfigurationUTF8.loadConfiguration(defConfigStream));
        }catch(UnsupportedEncodingException ex){
            Logger.log("An error occurred:", LogLevel.INFO);
            ex.printStackTrace();
        }
        langCfg.options().copyDefaults(true);
        saveMessages();
    }
    
    private static void saveMessages(){
        try{
            langCfg.save(langFile);
        }catch(IOException ex){
            Logger.log("An error occurred", LogLevel.INFO);
            ex.printStackTrace();
        }
    }
}
