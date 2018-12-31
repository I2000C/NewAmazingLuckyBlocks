/*package net.servermc.plugins.utils;

import java.io.File;
import java.io.IOException;
import net.servermc.plugins.AmazingLuckyBlocks;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LangLoader
{
    
  private AmazingLuckyBlocks plugin;
  
  public static File LangFile = new File(AmazingLuckyBlocks.getInstance().getDataFolder(), "lang.yml");
  public static FileConfiguration LangCfg = YamlConfiguration.loadConfiguration(LangFile);
  
  
  
  public static void mkdir()
  {
    if (!AmazingLuckyBlocks.getInstance().getDataFolder().exists()) {
      AmazingLuckyBlocks.getInstance().getDataFolder().mkdir();
    }
  }
  
  public static void MessageFile()
  {
    try
    {
      if (!LangFile.exists())
      {
        LangFile.createNewFile();
        LangCfg.set("Wands.Regen.name", "&dRegen wand &7- &6Right Click");
        LangCfg.set("Wands.Dragon-breath.name", "&cDragon breath wand &7- &6Right Click");
        LangCfg.set("Wands.Invisibility.name", "&8Invisibility wand &7- &6Right Click");
        LangCfg.set("Wands.TNT.name", "&4TNT wand &7- &6Right Click");
        LangCfg.set("Wands.Slime.name", "&2Slime wand &7- &6Right Click");
        LangCfg.set("Wands.Lightning.name", "&eLightning wand &7- &6Right Click");
        LangCfg.set("Wands.Shield.name", "&3Shield wand &7- &6Right Click");
        LangCfg.set("Objects.DarkHole.name", "&8Dark Hole &7- &6Right Click");
        LangCfg.set("Objects.MiniVolcano.name", "&cMini Volcano &7- &6Right Click");
        LangCfg.set("Cooldown-message", "&cYou cannot use this wand for %time% seconds");
        LangCfg.set("need-permission", "&cYou not have permission for use this wand/object");
        
        LangCfg.set("Helpmenu.line1", "&cNew Amazing Lucky Blocks");
        LangCfg.set("Helpmenu.line2", "");
        LangCfg.set("Helpmenu.line3", "&c/alb wands  &7-  Gives you all the magic wands");
        LangCfg.set("Helpmenu.line4", "&c/alb objects &7- Gives you all the fantastic objects");
        LangCfg.set("Helpmenu.line5", "&c/alb randomblock <radx> <rady> <radz> <blocks> <floating_blocks: true/false> &7- Places random lucky blocks");
        LangCfg.set("Helpmenu.line6", "&c/alb reload  &7-  Reload config and lang files");
        LangCfg.set("Helpmenu.line7", "&c/alb  &7-  Show help page");
        LangCfg.set("Helpmenu.line8", "&7--------&cNewAmazingHelp&7--------");
        
        LangCfg.set("World-loading.line1", "&cInvalid world in config:");
        LangCfg.set("World-loading.line2", "******************************");
        LangCfg.set("World-loading.line3", "&cWorlds-list in config is empty");
        LangCfg.set("World-loading.line4", "&cTo avoid errors, the world 'world' has been added to the config");
        LangCfg.set("World-loading.line5", "&3Add some worlds to the Worlds-list in config and then use &2/alb reload");
        LangCfg.set("World-loading.line6", "******************************");
        LangCfg.set("World-loading.line7", "&eNewAmazingLuckyBlocks has loaded:");
        LangCfg.set("World-loading.line8", "&bworld(s)");
        
        LangCfg.set("NoPermission", "&cYou don't have permissions enough");
        LangCfg.set("LoadingWands", "&aLoading wands...");
        LangCfg.set("LoadingObjects", "&bLoading objects...");
        LangCfg.set("Reload.line1", "&aReloading lang...");
        LangCfg.set("Reload.line2", "&aReloading config...");
        LangCfg.set("Reload.line3", "&cReload finished");
        LangCfg.set("UnknownCommand", "&cUnknown command. Type &7/alb &cfor help");
        LangCfg.save(LangFile);
      }
      LangCfg = YamlConfiguration.loadConfiguration(LangFile);
    }
    catch (IOException var0) {}
  }
  
  public static FileConfiguration loadLang()
  {
    return LangCfg;
  }
}*/

package net.servermc.plugins.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import org.bukkit.Bukkit;

import net.servermc.plugins.AmazingLuckyBlocks;

public class LangLoader
{
    private AmazingLuckyBlocks plugin;
    private static final LangLoader manager = new LangLoader();
    
    public static FileConfiguration LangCfg = null;
    public static File LangFile = null;
    public static String LangFileRoute;
    
    public static LangLoader getManager()
  {
    return manager;
  }
    
public FileConfiguration getMessages(){
        if(LangCfg == null){
            reloadMessages();
        }
        return LangCfg;
    }
   
    public void reloadMessages(){
        if(LangCfg == null){
            LangFile = new File(AmazingLuckyBlocks.instance.getDataFolder(),"lang.yml");
        }
        LangCfg = YamlConfiguration.loadConfiguration(LangFile);
        Reader defConfigStream;
        try{
            defConfigStream = new InputStreamReader(AmazingLuckyBlocks.instance.getResource("lang.yml"),"UTF8");
            if(defConfigStream != null){
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                LangCfg.setDefaults(defConfig);
            }          
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }
   
    public void saveMessages(){
        try{
            LangCfg.save(LangFile);           
        }catch(IOException e){
            e.printStackTrace();
        }
    }
   
    public void registerMessages(){
        LangFile = new File(AmazingLuckyBlocks.instance.getDataFolder(),"lang.yml");
        this.getMessages().options().copyDefaults(true);
        if(LangFile.exists()){
            checkMessages();
        }
        saveMessages();
    }
    
    public void checkMessages(){
        //To modify an existing section:
        /*if(!LangLoader.LangCfg.getString("Helpmenu.line4").contains("[")){
            LangLoader.LangCfg.set("Helpmenu.line4", "&c/alb objects [amount] &7- Gives you all the fantastic objects");
            }*/
        /*if(LangLoader.LangCfg.contains("Helpmenu.line1")){
            backupMessages();
            LangLoader.LangFile.delete();
            registerMessages();
        }*/
        //To add a new section:
        /*LangFileRoute = LangFile.getPath();
        Path archivo = Paths.get(LangFileRoute);
        try {
        String texto = new String(Files.readAllBytes(archivo));
        if(!texto.contains("nuevo-mensaje:")){
        getMessages().set("Messages.nuevo-mensaje", "loquesea");
        }
        } catch (IOException e) {
        e.printStackTrace();
        }*/
        }
    
    public void backupMessages(){
        int checker = 1;
        LangCfg = YamlConfiguration.loadConfiguration(LangFile);
        File dir = new File(AmazingLuckyBlocks.getInstance().getDataFolder(),"backups"); 
        dir.mkdir(); 
        
        File LangBackup = new File(dir.getPath(),"lang_backup" + checker + ".yml");
        
        while(checker<=100){
            if(LangBackup.exists()){ 
                checker ++;
                LangBackup = new File(dir.getPath(),"lang_backup" + checker + ".yml");
            }else{
                checker = 200;
            }
            
        }
        try{
            LangCfg.save(LangBackup);           
        }catch(IOException e){
            e.printStackTrace();
        }
        LangCfg = null;
    }

}