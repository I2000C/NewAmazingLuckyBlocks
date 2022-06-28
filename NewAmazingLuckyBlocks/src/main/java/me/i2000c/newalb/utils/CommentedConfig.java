package me.i2000c.newalb.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class CommentedConfig{
    private String configName;
    private FileConfiguration config = null;
    
    private final String SEPARATOR = "´";
    private List<String> comments = new ArrayList();
    
    private File configFile;
    
    public CommentedConfig(JavaPlugin plugin, String configName){
        this.configName = configName;
        this.configFile = new File(plugin.getDataFolder(), configName);
    }
    
    public FileConfiguration getConfig(){
        return this.config;
    }
    
    public void loadConfig(){
        try{
            comments.clear();
            
            if(!configFile.exists()){
                configFile.createNewFile();
            }
            
            BufferedReader input = new BufferedReader(new FileReader(configFile));
            String line;
            int lineNumber = 0;
            while((line = input.readLine()) != null){
                if(line.contains("#") || line.isEmpty()){
                    comments.add(line + SEPARATOR + lineNumber);
                }
                lineNumber++;
            }
            input.close();
            
            config = YamlConfiguration.loadConfiguration(configFile);
            
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    
    public boolean saveConfig(){
        try{
            List<String> configLines = Arrays.asList(config.saveToString().split("\n"));
            int maxLines = comments.size() + configLines.size();
            List<String> lines = new ArrayList();
            int j = 0;
            for(int i=0;i<maxLines;i++){
                String line = null;
                for(String str : comments){
                    int lineNumber = Integer.valueOf(str.split(SEPARATOR)[1]);
                    if(lineNumber == i){
                        line = str.split(SEPARATOR)[0];
                        break;
                    }
                }
                
                if(line == null){
                    lines.add(configLines.get(j));
                    j++;
                }else{
                    lines.add(line);
                }
            }
            
            configFile.delete();
            configFile.createNewFile();
            
            BufferedWriter output = new BufferedWriter(new FileWriter(configFile));
            for(String str : lines){
                output.write(str + "\n");
            }
            output.close();
            
            return true;
        }catch(IOException ex){
            ex.printStackTrace();
            return false;
        }        
    }
}
