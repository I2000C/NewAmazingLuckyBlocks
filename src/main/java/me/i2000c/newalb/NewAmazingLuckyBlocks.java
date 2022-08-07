package me.i2000c.newalb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import me.i2000c.newalb.custom_outcomes.menus.FinishMenu;
import me.i2000c.newalb.custom_outcomes.menus.StructureMenu;
import me.i2000c.newalb.custom_outcomes.utils.PackManager;
import me.i2000c.newalb.custom_outcomes.utils.TypeManager;
import me.i2000c.newalb.custom_outcomes.utils.rewards.TrapManager;
import me.i2000c.newalb.lang_utils.LangLoader;
import me.i2000c.newalb.listeners.BlockBreak;
import me.i2000c.newalb.listeners.BlockPlace;
import me.i2000c.newalb.listeners.ChunkEvent;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.interact.PlayerInteractListener;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.utils.BlockProtect;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.LocationManager;
import me.i2000c.newalb.utils.SpecialItemManager;
import me.i2000c.newalb.utils.Timer;
import me.i2000c.newalb.utils.Updater;
import me.i2000c.newalb.utils.WorldList;
import me.i2000c.newalb.utils.logger.LogLevel;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils2.Task;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class NewAmazingLuckyBlocks extends JavaPlugin implements Listener{
    private static NewAmazingLuckyBlocks instance = null;

    public NewAmazingLuckyBlocks(){
        instance = this;
    }

    public static NewAmazingLuckyBlocks getInstance(){
        return instance;
    }
    
    public String rutaConfig;
    PluginDescriptionFile pdffile = getDescription();
    public String version = pdffile.getVersion();
    public String name = ChatColor.GOLD + pdffile.getName() + ChatColor.RESET;
    public String prefix;
    
    private static MinecraftVersion minecraftVersion;
    public static MinecraftVersion getMinecraftVersion(){
        return minecraftVersion;
    }
    
    @Override
    public void onEnable(){
        minecraftVersion = MinecraftVersion.getCurrentVersion();
        
        ConfigManager.setup(this);
        
        prefix = Logger.color(LangLoader.getMessages().getString("InGamePrefix"));      
        if(minecraftVersion == null){
            Logger.log("You are trying to use NewAmazingLuckyBlocks in an incompatible minecraft version", LogLevel.INFO);
            Logger.log("&cNewAmazingLuckyBlocks is going to shut down");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        
        Logger.log(LangLoader.getMessages().getString("Loading.line1"));
        Logger.log(LangLoader.getMessages().getString("Loading.line2"));
        Logger.log(LangLoader.getMessages().getString("Loading.line3"));
        
        Task.initializeTaskManager(this);
        
        registerEvents();
        getCommand("alb").setExecutor(new CommandManager(this));
        getCommand("nalb").setExecutor(new CommandManager(this));
        
        WorldList.reloadAll();
        
        PackManager.loadPacks();
        TrapManager.loadTraps();
        TypeManager.loadTypes();
        
        LocationManager.loadLocations(this);
        
        initializeWorldEdit();
        
        Logger.log(LangLoader.getMessages().getString("Enable.line1").replace("%version%", version));
        Logger.log(LangLoader.getMessages().getString("Enable.line2").replace("%prefix%", ""));
    }
    
    private void registerEvents(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        PluginManager pm = getServer().getPluginManager();
        
        pm.registerEvents(Timer.getTimer(), this);
        
        Updater.checkUpdates(name, version);
        pm.registerEvents(new Updater(), this);
        pm.registerEvents(new BlockBreak(), this);
        pm.registerEvents(new BlockPlace(), this);
        pm.registerEvents(new BlockProtect(), this);
        pm.registerEvents(new ChunkEvent(), this);
        
        pm.registerEvents(new StructureMenu(), this);
        
        pm.registerEvents(new InventoryListener(), this);
        pm.registerEvents(new ChatListener(), this);
        pm.registerEvents(new PlayerInteractListener(), this);
        
        pm.registerEvents(new TrapManager(), this);
        
        SpecialItemManager.loadSpecialItems(pm, this);
//</editor-fold>
    }
    
    @Override
    public void onDisable(){
        if(minecraftVersion != null){
            FinishMenu.testRewardsPlayerList.clear();
            LocationManager.saveLocations();
        }
        Logger.log(LangLoader.getMessages().getString("Disable.line1").replace("%prefix%", ""));
    }
    
    public void copyResource(String filename, File file){
        InputStream input = null;
        OutputStream output = null;
        try{
            input = getResource(filename);
            output = new FileOutputStream(file);
            copy(input, output);
        }catch(IOException ex){
            Logger.log("An error occurred while copying default file: " + '"' + filename + '"' + " to " + '"' + file.getName() + '"', LogLevel.INFO);
            Logger.log(ex, LogLevel.INFO);
        }finally{
            try{
                input.close();
            }catch(Exception ex){
            }
            try{
                output.close();
            }catch(Exception ex){
            }
        }
    }
    
    private void copy(InputStream input, OutputStream output) throws IOException{
        byte[] buffer = new byte[1024];
        int bytesReaded;
        while((bytesReaded = input.read(buffer)) > 0){
            output.write(buffer, 0, bytesReaded);
        }
    }
    
    private static Plugin wep = null;
    private void initializeWorldEdit(){
        File schematicFolder = new File(getDataFolder(), "schematics");
        schematicFolder.mkdirs();
        wep = Bukkit.getPluginManager().getPlugin("WorldEdit");
    }
    public static Plugin getWorldEditPlugin(){
        return wep;
    }
    
    private static Plugin ssw = null;
    public static Plugin getSuperSkywarsPlugin(){
        if(ssw == null){
            ssw = Bukkit.getPluginManager().getPlugin("SuperSkywars");
        }
        
        return ssw;
    }
}
