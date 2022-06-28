package me.i2000c.newalb.utils;

import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import me.i2000c.newalb.NewAmazingLuckyBlocks;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager{
    private static File cfile;
    private static File dataFolder;
    private static File pluginFolder;
    private static FileConfiguration config;

    public static void setup(Plugin plugin){
        cfile = new File(plugin.getDataFolder(), "config.yml");
        config = plugin.getConfig();
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdir();
        }
        pluginFolder = plugin.getDataFolder();
        if(!cfile.exists()){
            try{
                cfile.createNewFile();
            }catch (IOException e){
                System.out.println(ChatColor.RED + "Amazing Lucky Blocks: Could not create config. The plugin is shutting down. Error:");
                e.printStackTrace();
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        }else{
            //checkConfig();
        }
        config = plugin.getConfig();
        config.options().copyDefaults(true);
        saveConfig();
        checkConfig();
    }
  
    public static void saveConfig(){
        try{
            config.save(cfile);
        }catch (IOException ex){
            System.out.println(ChatColor.RED + "Amazing Lucky Blocks Error: Could not save config. Error:");
            ex.printStackTrace();
        }
    }
    
    public static void reloadConfig(){
        config = YamlConfiguration.loadConfiguration(cfile);
    }
  
    public static FileConfiguration getConfig(){
        return config;
    }
  
    public static File getPluginFolder(){
        return pluginFolder;
    }
  
    public static File getDataFolder(){
        return dataFolder;
    }
  
    private static void checkConfig(){
        FileConfiguration file = YamlConfiguration.loadConfiguration(cfile);
        if(file.getString("Objects.DarkHole.block-material") != null){
            file.set("Objects.DarkHole.item-material", file.getString("Objects.DarkHole.block-material"));
            file.set("Objects.DarkHole.block-material", null);
            try{
                file.save(cfile);
            }catch(IOException ex){
            }
        }
        if(file.getString("Objects.MiniVolcano.block-material") != null){
            file.set("Objects.MiniVolcano.item-material", file.getString("Objects.MiniVolcano.block-material"));
            file.set("Objects.MiniVolcano.block-material", null);
            try{
                file.save(cfile);
            }catch(IOException ex){              
            }
        }
        config = YamlConfiguration.loadConfiguration(cfile);
    }
  
    private static void backupConfig(){
        FileConfiguration CLBConfigBackup;
        int checker = 1;
        CLBConfigBackup = YamlConfiguration.loadConfiguration(cfile);
        File dir = new File(NewAmazingLuckyBlocks.getInstance().getDataFolder(),"backups"); 
        dir.mkdir(); 
        
        File CLBFileBackup = new File(dir.getPath(),"config_backup" + checker + ".yml");
        
        while(checker<=100){
            if(CLBFileBackup.exists()){ 
                checker ++;
                CLBFileBackup = new File(dir.getPath(),"config_backup" + checker + ".yml");
            }else{
                checker = 200;
            }
            
        }
        try{
            CLBConfigBackup.save(CLBFileBackup);           
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}