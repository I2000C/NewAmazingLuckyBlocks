package me.i2000c.newalb;

import static me.i2000c.newalb.api.version.MinecraftVersion.CURRENT_VERSION;
import static me.i2000c.newalb.api.version.MinecraftVersion.LATEST_VERSION;
import static me.i2000c.newalb.api.version.MinecraftVersion.OLDEST_VERSION;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.integration.WorldGuardManager;
import me.i2000c.newalb.listeners.blocks.BlockBreakListener;
import me.i2000c.newalb.listeners.blocks.BlockPlaceListener;
import me.i2000c.newalb.listeners.blocks.BlockProtectListener;
import me.i2000c.newalb.listeners.blocks.ChunkLoadListener;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.interact.SpecialEventListener;
import me.i2000c.newalb.listeners.interact.SpecialItems;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.updates.UpdateChecker;
import me.i2000c.newalb.lucky_blocks.LuckyBlockDropper;
import me.i2000c.newalb.lucky_blocks.TrapManager;
import me.i2000c.newalb.lucky_blocks.editors.menus.RewardListMenu;
import me.i2000c.newalb.lucky_blocks.rewards.PackManager;
import me.i2000c.newalb.lucky_blocks.rewards.TypeManager;
import me.i2000c.newalb.utils.locations.LocationManager;
import me.i2000c.newalb.utils.locations.WorldManager;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.random.RandomBlocks;
import me.i2000c.newalb.utils.tasks.Task;

public class NewAmazingLuckyBlocks extends JavaPlugin {
    
    @Getter
    private static NewAmazingLuckyBlocks instance = null;

    public NewAmazingLuckyBlocks(){
        instance = this;
    }
    
    private final PluginDescriptionFile pdffile = getDescription();
    public String version = pdffile.getVersion();
    public String name = ChatColor.GOLD + pdffile.getName() + ChatColor.RESET;
    public String prefix;
    public ExecutorService asyncPacksLoaderExecutorService;
    
    @Override
    public void onLoad() {
        Logger.initializeLogger("[NewAmazingLuckyBlocks]", false);
        WorldGuardManager.initialize();
        
        int cores = Runtime.getRuntime().availableProcessors();
        int threads = Math.max(1, Math.min(2, cores - 1));
        
        asyncPacksLoaderExecutorService = new ThreadPoolExecutor(threads, threads, 
                0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
                r -> {
                    Thread t = new Thread(r, "LuckyBlocks-OutcomePack-Loader");
                    t.setDaemon(true);
                    return t;
                });
    }
    
    @Override
    public void onEnable(){
        if(CURRENT_VERSION.isLessThan(OLDEST_VERSION) || CURRENT_VERSION.isGreaterThan(LATEST_VERSION)) {
            Logger.warn("You are trying to use NewAmazingLuckyBlocks in an incompatible minecraft version (" + CURRENT_VERSION + ")");
            Logger.warn("If you find any bugs, please report them on Spigot, Github or Discord");
        }
        
        ConfigManager.initialize(this);
        ConfigManager.loadConfigs();
        
        prefix = Logger.color(ConfigManager.getLangMessage("InGamePrefix"));
        boolean coloredLogger = ConfigManager.getMainConfig().getBoolean("ColoredLogger");
        Logger.initializeLogger(prefix, coloredLogger);
        
        Logger.log(ConfigManager.getLangMessage("Loading.plugin"));
        Task.initializeTaskManager(this);
        UpdateChecker.checkUpdates(version);
        
        Logger.log(ConfigManager.getLangMessage("Loading.config"));
        Logger.log(ConfigManager.getLangMessage("Loading.lang"));
        
        SpecialItems.loadItems();
        
        Logger.log(ConfigManager.getLangMessage("Loading.worlds"));
        WorldManager.reloadWorlds();
        LocationManager.initialize(instance);
        
        Logger.log(ConfigManager.getLangMessage("Loading.packs"));
        TypeManager.loadTypes();
        LuckyBlockDropper.loadSettings();
        PackManager.loadPacksAsync(() -> {
            try {
                TypeManager.loadPacksFromCachedPacksProbList();
                TrapManager.loadTraps();
            } catch(Throwable t) {
                throw t;
            } finally {
                PackManager.SET_LOADING_PACKS(false);
            }
            
            String message = ConfigManager.getLangMessage("Packs-loading")
                                          .replace("%packs%", PackManager.getPacks().size() + "");
            Task.runTask(() -> Logger.log(message));
        });
        
        initializeWorldEdit();
        if(WorldGuardManager.isWorldGuardEnabled()) {
            Logger.log("&dWorldGuard support: &aenabled");
        } else {
            Logger.log("&dWorldGuard support: &7disabled");
        }

        getCommand("alb").setExecutor(new CommandManager(this));
        getCommand("nalb").setExecutor(new CommandManager(this));
        registerEvents();

        Logger.log(ConfigManager.getLangMessage("Enable.line1").replace("%version%", version));
    }
    
    private void registerEvents(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        PluginManager pm = getServer().getPluginManager();
        
        pm.registerEvents(new UpdateChecker(), this);
        pm.registerEvents(new BlockBreakListener(), this);
        pm.registerEvents(new BlockPlaceListener(), this);
        pm.registerEvents(new BlockProtectListener(), this);
        pm.registerEvents(new ChunkLoadListener(), this);
        
        pm.registerEvents(new InventoryListener(), this);
        pm.registerEvents(new ChatListener(), this);
        pm.registerEvents(new SpecialEventListener(), this);
        
        pm.registerEvents(TrapManager.getManager(), this);
//</editor-fold>
    }
    
    @Override
    public void onDisable(){
        RandomBlocks.forceStopAllRandomBlocksTasks();
        RewardListMenu.testRewardsPlayerList.clear();
        LocationManager.releaseDatabaseConnection();
        
        asyncPacksLoaderExecutorService.shutdown();
        try {
            if(!asyncPacksLoaderExecutorService.awaitTermination(3, TimeUnit.SECONDS)) {
                asyncPacksLoaderExecutorService.shutdownNow();
            }
        } catch(InterruptedException ex) {
            asyncPacksLoaderExecutorService.shutdownNow();
        }
        
        Logger.log(ConfigManager.getLangMessage("Disable.line1").replace("%prefix%", ""));
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
