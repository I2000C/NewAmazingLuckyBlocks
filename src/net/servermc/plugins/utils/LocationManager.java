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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.v1_13_R2.WorldGenVillagePieces;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import org.bukkit.Bukkit;

import net.servermc.plugins.AmazingLuckyBlocks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class LocationManager implements Listener
{
    
    public String color(String str)
    {
    return ChatColor.translateAlternateColorCodes('&', str);
    }
    
    private AmazingLuckyBlocks plugin;
    private static final LocationManager manager = new LocationManager();
    
    public static FileConfiguration LocCfg = null;
    public static File LocFile = null;
    File dir = new File(AmazingLuckyBlocks.getInstance().getDataFolder(),"data");
    public static List<Location> loc_list = new ArrayList(); 
    
    public static LocationManager getManager()
  {
    return manager;
  }
    
    
    public void registerLocations(){
        dir.mkdir();
        LocFile = new File(dir.getPath(), "luckyblocks-locs.yml");
        LocCfg = YamlConfiguration.loadConfiguration(LocFile);
        if(LocFile.exists()){
            if(LocCfg.get("Blocks") != null && LocCfg.get("Blocks-placed") != null){
                /*
                Set<String> section_list = LocCfg.getConfigurationSection("Blocks").getKeys(false);
                for(String string : section_list){
                
                Location l = new Location(Bukkit.getServer().getWorld(string + ".world"), Double.parseDouble(string + ".x"), Double.parseDouble(string + ".y"), Double.parseDouble(string + ".z"));
                loc_list.add(l);
                
                }*/
            }else{
                LocCfg.set("Blocks-placed", 0);
                LocCfg.set("Blocks", "{}");
            }
            /*checkLocations();
            LocCfg = YamlConfiguration.loadConfiguration(LocFile);
            if(LocCfg.get("something") != null){
            LocationManager.LocCfg.set("something", null);
            }else{
            for(int i = 0; i <= 10; i++){
            LocCfg.set("something", "true");
            LocCfg.set("PRUEBA", dir.getAbsolutePath());
            }
            }*/
        }else{
            try {
                LocFile.createNewFile();
                registerLocations();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        saveLocations();
    }
    
    public FileConfiguration getLocations(){
        if(LocCfg == null){
            registerLocations();
        }
        return LocCfg;
    }
    
    public void saveLocations(){
        try{
            LocCfg.save(LocFile);           
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent e){
        Player p = e.getPlayer();
        ItemStack stack = p.getItemInHand();
        String block_name = CLBManager.getManager().getConfig().getString(color("LuckyBlock.Name"));
        
        List<String> configLore = CLBManager.getManager().getConfig().getStringList("LuckyBlock.Lore");
        List<String> block_lore = new ArrayList();
        for(int i = 0; i < configLore.size(); i++){
            block_lore.add(color(configLore.get(i)));
        }

        Material material;
        if(CLBManager.getManager().getConfig().getString("LuckyBlock.Material").equals("SKULL")){
            material = Material.valueOf("SKULL_ITEM");
        }else{
            material = Material.valueOf(CLBManager.getManager().getConfig().getString("LuckyBlock.Material"));
        }
        
        if(stack.getItemMeta().hasDisplayName() && stack.getItemMeta().hasLore()){
            if(stack.getItemMeta().getDisplayName().equals(block_name) || stack.getItemMeta().getLore().equals(block_lore)){
                if(stack.getType().equals(material)){
                    if(CLBManager.getManager().getConfig().getBoolean("LuckyBlock.Permissions.place.enable") && 
                            !p.hasPermission(CLBManager.getManager().getConfig().getString("LuckyBlock.Permissions.place.permission"))){
                        p.sendMessage(color(LangLoader.LangCfg.getString("NoPermission")));
                        e.setCancelled(true);
                        return;
                    }
                Location block_loc = e.getBlock().getLocation();
                loc_list.add(block_loc);
                
                LocCfg.set("Blocks." + "(" + block_loc.getBlockX() + block_loc.getBlockY() + block_loc.getBlockZ() + ")" + ".player-name", p.getName());
                LocCfg.set("Blocks." + "(" + block_loc.getBlockX() + block_loc.getBlockY() + block_loc.getBlockZ() + ")" + ".world", block_loc.getWorld().getName());
                LocCfg.set("Blocks." + "(" + block_loc.getBlockX() + block_loc.getBlockY() + block_loc.getBlockZ() + ")" + ".x", block_loc.getBlockX());
                LocCfg.set("Blocks." + "(" + block_loc.getBlockX() + block_loc.getBlockY() + block_loc.getBlockZ() + ")" + ".y", block_loc.getBlockY());
                LocCfg.set("Blocks." + "(" + block_loc.getBlockX() + block_loc.getBlockY() + block_loc.getBlockZ() + ")" + ".z", block_loc.getBlockZ());
                
                int blocks_number = loc_list.size();
                LocCfg.set("Blocks-placed", blocks_number);
                saveLocations();
                }
            }
        }
    }
    
    public void removeBlockFromListAndConfig(Block block){
        Location block_loc = block.getLocation();
        loc_list.remove(block_loc);
        LocCfg.set("Blocks." + "(" + block_loc.getBlockX() + block_loc.getBlockY() + block_loc.getBlockZ() + ")", null);
        int blocks_number = loc_list.size();
        LocCfg.set("Blocks-placed", blocks_number);
        saveLocations();
    }
    
    /*public void reloadLocations(){
    if(LocCfg == null){
    dir.mkdir();
    LocFile = new File(dir.getPath(), "luckyblocks-locs.yml");
    }
    LocCfg = YamlConfiguration.loadConfiguration(LocFile);
    Reader defConfigStream;
    try{
    defConfigStream = new InputStreamReader(AmazingLuckyBlocks.instance.getResource("luckyblocks-locs"),"UTF8");
    if(defConfigStream != null){
    YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
    LocCfg.setDefaults(defConfig);
    }
    }catch(UnsupportedEncodingException e){
    e.printStackTrace();
    }
    }*/
   
    
    
    //public void checkLocations(){
        //To modify an existing section:
        /*if(!LocationManager.LocCfg.getString("Helpmenu.line4").contains("[")){
            LocationManager.LocCfg.set("Helpmenu.line4", "&c/alb objects [amount] &7- Gives you all the fantastic objects");
            }*/
        /*if(LocationManager.LocCfg.contains("Helpmenu.line1")){
            backupMessages();
            LocationManager.LocFile.delete();
            registerMessages();
        }*/
        //To add a new section:
        /*LocFileRoute = LocFile.getPath();
        Path archivo = Paths.get(LocFileRoute);
        try {
        String texto = new String(Files.readAllBytes(archivo));
        if(!texto.contains("nuevo-mensaje:")){
        getMessages().set("Messages.nuevo-mensaje", "loquesea");
        }
        } catch (IOException e) {
        e.printStackTrace();
        }
                OR
        getMessages().set("SECTION","VALUE");*/
        //To delete an existing section:
        /*
        getMessages().set("SECTION",null);
        */
        //}
    
   /* public void backupLocations(){
        int checker = 1;
        LocCfg = YamlConfiguration.loadConfiguration(LocFile);
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
            LocCfg.save(LangBackup);           
        }catch(IOException e){
            e.printStackTrace();
        }
        LocCfg = null;
    }*/

}