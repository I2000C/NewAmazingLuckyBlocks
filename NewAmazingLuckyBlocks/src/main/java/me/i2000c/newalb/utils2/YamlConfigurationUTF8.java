package me.i2000c.newalb.utils2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import org.bukkit.configuration.file.YamlConfiguration;

public class YamlConfigurationUTF8 extends YamlConfiguration{
    public static YamlConfiguration loadConfiguration(File file){
        if(NewAmazingLuckyBlocks.getMinecraftVersion() == MinecraftVersion.v1_8){
            YamlConfigurationUTF8 config = new YamlConfigurationUTF8();
            InputStreamReader input = null;
            try{
                input = new InputStreamReader(new FileInputStream(file), "UTF-8");
                config.load(input);
            }catch(Exception ex){
                ex.printStackTrace();
                config = null;
            }finally{
                try{
                    input.close();
                }catch(Exception ex2){}
            }

            return config;
        }else{
            return YamlConfiguration.loadConfiguration(file);
        }        
    }
    
    private static final String UNICODE_REGEX = "\\\\u([0-9a-f]{4})";
    private static final Pattern UNICODE_PATTERN = Pattern.compile(UNICODE_REGEX);
    
    private static String decodeString(String message){
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
    }
    
    @Override
    public void save(File file) throws IOException{
        if(NewAmazingLuckyBlocks.getMinecraftVersion() == MinecraftVersion.v1_8){
            BufferedReader input = null;
            BufferedWriter output = null;
            try{
                input = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(this.saveToString().getBytes("UTF-8"))));
                String line;
                String str = "";
                while((line = input.readLine()) != null){
                    str += line + '\n';
                }

                output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
                output.write(decodeString(str));
            }catch(IOException ex){
                ex.printStackTrace();
                throw ex;
            }finally{
                try{
                    input.close();
                }catch(Exception ex){}
                try{
                    output.close();
                }catch(Exception ex){}
            }
        }else{
            super.save(file);
        }
    }
}
