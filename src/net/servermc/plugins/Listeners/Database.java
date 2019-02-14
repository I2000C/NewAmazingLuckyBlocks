package net.servermc.plugins.Listeners;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import me.arcaniax.hdb.api.PlayerClickHeadEvent;
import net.minecraft.server.v1_13_R2.BlockPosition;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import net.minecraft.server.v1_13_R2.TileEntitySkull;
import net.servermc.plugins.AmazingLuckyBlocks;
import net.servermc.plugins.utils.CLBManager;
import net.servermc.plugins.utils.LangLoader;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;


public class Database implements Listener{
    
    
    public String color(String str)
    {
    return ChatColor.translateAlternateColorCodes('&', str);
    }
    
    public static boolean headMode = false;
    public static boolean isDatabaseLoaded;    
    public static ItemStack item;
    public static boolean isSimilar;
    
    @EventHandler    
    public void onDatabaseLoad(DatabaseLoadEvent e){
        isDatabaseLoaded = true;
        this.getItemHead();
    }
    
    public void getItemHead(){
        HeadDatabaseAPI api = new HeadDatabaseAPI();
        try{
           item = api.getItemHead(CLBManager.getManager().getConfig().getString("LuckyBlock.HeadMode.skull-ID"));
           if(item == null){
               Bukkit.getConsoleSender().sendMessage(color(LangLoader.LangCfg.get("InGamePrefix") + " &cThe head that you have specified doesn't exists"));
           }
        }
        catch(NullPointerException nullpointer){
            Bukkit.getConsoleSender().sendMessage(color(LangLoader.LangCfg.get("InGamePrefix") + " &cThe head that you have specified doesn't exists"));
        }
    }
    
    public void checkHeadMode(){
        if(CLBManager.getManager().getConfig().getBoolean("LuckyBlock.HeadMode.enable")){
            Plugin hdb = getServer().getPluginManager().getPlugin("HeadDatabase");
            if(hdb != null){
                if(CLBManager.getManager().getConfig().getString("LuckyBlock.Material").equals("SKULL")){
                    Bukkit.getConsoleSender().sendMessage(color(LangLoader.LangCfg.get("InGamePrefix") + " &aHeadMode is enabled"));
                    headMode = true;
                    if(!isDatabaseLoaded){
                        getServer().getPluginManager().registerEvents(new Database(), AmazingLuckyBlocks.instance);
                    }else{
                        this.getItemHead();
                    }
                        
                }else{
                    Bukkit.getConsoleSender().sendMessage(color(LangLoader.LangCfg.get("InGamePrefix") + " &cIn order to use NewAmazingLuckyBlocks HeadMode,"));
                    Bukkit.getConsoleSender().sendMessage(color(LangLoader.LangCfg.get("InGamePrefix") + " &cyou need to set the LuckyBlock material to 'SKULL'"));
                    headMode = false;
                }
            }else{
                Bukkit.getConsoleSender().sendMessage(color(LangLoader.LangCfg.get("InGamePrefix") + " &cIn order to use NewAmazingLuckyBlocks HeadMode,"));
                Bukkit.getConsoleSender().sendMessage(color(LangLoader.LangCfg.get("InGamePrefix") + " &cyou need to install the HeadDatabase plugin"));
                headMode = false;
            }
        }else{            
            Bukkit.getConsoleSender().sendMessage(color(LangLoader.LangCfg.get("InGamePrefix") + " &cHeadMode is disabled"));
            headMode = false;
        }
    }
    
    public static void setSkullSkin(Block b, String ID){
        HeadDatabaseAPI api = new HeadDatabaseAPI();
        api.setBlockSkin(b, ID);
    }
    
   /*public static boolean bool = true;
   public static ItemStack stack,stack2;
   @EventHandler
   public void onBreak(BlockBreakEvent b){
       Player p = b.getPlayer();
       HeadDatabaseAPI api = new HeadDatabaseAPI();
       //getLogger().info( api.getItemID(item) );
        Block block = b.getBlock();
        Block bl = block.getLocation().add(0, 1, 0).getBlock();
        if(bool){
        List<ItemStack> drops;
        drops = (List<ItemStack>) block.getDrops();
        stack = drops.get(0);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(item.getItemMeta().getDisplayName());
        stack.setItemMeta(meta);
        p.sendMessage("Saved to 1");
        p.getInventory().addItem(stack);
        //bool = false;
        }else{
        List<ItemStack> drops2;
        drops2 = (List<ItemStack>) block.getDrops();
        stack2 = drops2.get(0);
        ItemMeta meta2 = stack2.getItemMeta();
        meta2.setDisplayName(item.getItemMeta().getDisplayName());
        stack2.setItemMeta(meta2);
        p.sendMessage("Saved to 2");
        p.getInventory().addItem(stack2);
        bool = true;
        }
        if(stack.isSimilar(item)){
            p.sendMessage("IT WORKS");
        }else{
            p.sendMessage("IT DOESN'T WORK");
        }/*
        /*if(item.equals(stack)){
        b.getPlayer().sendMessage("It works");
        }else{
        b.getPlayer().sendMessage("It doesn't work");
        b.getPlayer().getInventory().addItem(stack);
        b.getPlayer().getInventory().addItem(item);
        }*/
        
        /*if(block.getType().equals(Material.valueOf("SKULL"))){
           BlockState bss = block.getState();
           Skull sk = (Skull) bss;
           ItemStack k = api.getItemHead("1709");
           Skull xd = (Skull) block.getState();
           xd.setData(k.getData());
           sk.update();
           xd.update();
           if(!sk.equals(xd)){
               bl.setBlockData(xd.getBlockData());
               bl.getWorld().getBlockAt(bl.getLocation().add(0, 2, 0)).setBlockData(sk.getBlockData());
              return;  
           }
           bl.setType(Material.GRASS);
        }*/
        
        /*Player p = b.getPlayer();
        bl.setType(Material.valueOf("SKULL"));
        api.setBlockSkin(bl, "8514"); //This is used to set the texture of a placed player head block
        ItemStack k = api.getItemHead("1709");
        Skull xd = (Skull) bl.getState();
        xd.setData(k.getData());*/
        
        //BlockState bs = bl.getState();
        //Skull xd = (Skull) bs;
        //xd.setSkullType(SkullType.PLAYER);
        //xd.setOwner("I2000C");
        //xd.update();
        /*BlockState bs = b.getState();
        Skull xd = (Skull) bl;
        xd.setSkullType(SkullType.PLAYER);
        xd.setOwner("I2000C");
        xd.update();*/
        
        //NBTTagCompound nbt = new NBTTagCompound();
        
        //Block block = p.getLocation().getBlock();
        /*block.setType(bl.getType());
        block.setBlockData(bl.getBlockData(), true);*/
        //setSkullUrl("http://textures.minecraft.net/texture/86fd37aaf22730e0dcae98dfca8b8e77771c6a18bb9efbf73387e4e2f1bdc", block);
        //}
}
