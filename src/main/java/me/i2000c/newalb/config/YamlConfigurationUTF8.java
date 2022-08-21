package me.i2000c.newalb.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.logger.LogLevel;
import me.i2000c.newalb.utils.logger.Logger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class YamlConfigurationUTF8 extends YamlConfiguration{
    private static final String UNICODE_REGEX = "\\\\u([0-9a-f]{4})";
    private static final Pattern UNICODE_PATTERN = Pattern.compile(UNICODE_REGEX);
    
    private static String decodeString(String message){
        //<editor-fold defaultstate="collapsed" desc="Code">
        message = message.replaceAll("\\\\x", "\\\\u00");
        Matcher matcher = UNICODE_PATTERN.matcher(message);
        StringBuffer decodedMessage = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(
                    decodedMessage, String.valueOf((char) Integer.parseInt(matcher.group(1), 16)));
        }
        matcher.appendTail(decodedMessage);
        String returnString = decodedMessage.toString();
        returnString = returnString.replaceAll("\\\\\n *", "");
        returnString = returnString.replaceAll("\\\\ ", " ");
        return returnString;
//</editor-fold>
    }
    
    public void load(InputStream input) throws IOException, InvalidConfigurationException{
        load(new InputStreamReader(input));
    }
    
    @Override
    public void load(File file){
        //<editor-fold defaultstate="collapsed" desc="Code">
        try{
            if(NewAmazingLuckyBlocks.getMinecraftVersion() == MinecraftVersion.v1_8){
                InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
                super.load(reader);
            }else{
                super.load(file);
            }
        }catch(Exception ex){
            Logger.log("An error occurred while loading config from file " + file.getName() + ":", LogLevel.ERROR);
            Logger.log(ex, LogLevel.ERROR);
        }
//</editor-fold>
    }
    
    @Override
    public void save(File file){
        //<editor-fold defaultstate="collapsed" desc="Code">
        try{
            if(NewAmazingLuckyBlocks.getMinecraftVersion() == MinecraftVersion.v1_8){
                try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))){
                    writer.append(decodeString(saveToString()));
                }
            }else{
                super.save(file);
            }
        }catch(Exception ex){
            Logger.log("An error occurred while saving config to file " + file.getName() + ":", LogLevel.ERROR);
            Logger.log(ex, LogLevel.ERROR);
        }
//</editor-fold>
    }
}
