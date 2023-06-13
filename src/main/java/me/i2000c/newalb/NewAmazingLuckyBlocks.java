package me.i2000c.newalb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import me.i2000c.newalb.custom_outcomes.menus.RewardListMenu;
import me.i2000c.newalb.custom_outcomes.rewards.PackManager;
import me.i2000c.newalb.custom_outcomes.rewards.TypeManager;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.TrapManager;
import me.i2000c.newalb.listeners.BlockBreak;
import me.i2000c.newalb.listeners.BlockPlace;
import me.i2000c.newalb.listeners.ChunkEvent;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.interact.SpecialEventListener;
import me.i2000c.newalb.listeners.interact.SpecialItemManager;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.utils.BlockProtect;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.LangConfig;
import me.i2000c.newalb.utils.LocationManager;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.Timer;
import me.i2000c.newalb.utils.Updater;
import me.i2000c.newalb.utils.WorldConfig;
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
        
        Logger.initializeLogger("NewAmazingLuckyBlocks", false);
        
        if(minecraftVersion == null){
            Logger.warn("You are trying to use NewAmazingLuckyBlocks in an incompatible minecraft version");
            Logger.warn("NewAmazingLuckyBlocks is going to shut down");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        
        ConfigManager.initialize(this);
        ConfigManager.getManager().loadConfig();
        LangConfig.initialize(this);
        LangConfig.loadConfig();
        
        prefix = Logger.color(LangConfig.getMessage("InGamePrefix"));
        boolean coloredLogger = ConfigManager.getConfig().getBoolean("ColoredLogger");
        Logger.initializeLogger(prefix, coloredLogger);
        
        Logger.log(LangConfig.getMessage("Loading.plugin"));
        Updater.checkUpdates(name, version);
        
        Logger.log(LangConfig.getMessage("Loading.config"));
        Logger.log(LangConfig.getMessage("Loading.lang"));        
        
        initializeWorldEdit();
        
        Task.initializeTaskManager(this);
        WorldConfig.initialize(this);
        LocationManager.initialize(this);
        TrapManager.initialize(this);
        
        SpecialItemManager.loadSpecialItems();
        
        Logger.log(LangConfig.getMessage("Loading.packs"));
        PackManager.loadPacks();        
        TypeManager.loadTypes();
        
        Logger.log(LangConfig.getMessage("Loading.worlds"));

        WorldConfig.reloadWorlds();
        LocationManager.loadLocations();
        TrapManager.loadTraps();

        getCommand("alb").setExecutor(new CommandManager(this));
        getCommand("nalb").setExecutor(new CommandManager(this));
        registerEvents();

        Logger.log(LangConfig.getMessage("Enable.line1").replace("%version%", version));
    }
    
    private void registerEvents(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        PluginManager pm = getServer().getPluginManager();
        
        pm.registerEvents(Timer.getTimer(), this);
        
        pm.registerEvents(new Updater(), this);
        pm.registerEvents(new BlockBreak(), this);
        pm.registerEvents(new BlockPlace(), this);
        pm.registerEvents(new BlockProtect(), this);
        pm.registerEvents(new ChunkEvent(), this);
        
        pm.registerEvents(new InventoryListener(), this);
        pm.registerEvents(new ChatListener(), this);
        pm.registerEvents(new SpecialEventListener(), this);
        
        pm.registerEvents(TrapManager.getManager(), this);
//</editor-fold>
    }
    
    @Override
    public void onDisable(){
        if(minecraftVersion == null) {
            Logger.log("has been disabled");
        } else {
            RewardListMenu.testRewardsPlayerList.clear();
            LocationManager.saveLocations();
            Logger.log(LangConfig.getMessage("Disable.line1").replace("%prefix%", ""));
        }        
    }
    
    public void copyResource(String filename, File file){
        InputStream input = null;
        OutputStream output = null;
        try{
            input = getResource(filename);
            output = new FileOutputStream(file);
            copy(input, output);
        }catch(IOException ex){
            Logger.err("An error occurred while copying default file: " + '"' + filename + '"' + " to " + '"' + file.getName() + '"');
            Logger.err(ex);
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
