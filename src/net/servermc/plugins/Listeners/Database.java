package net.servermc.plugins.Listeners;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import me.arcaniax.hdb.api.PlayerClickHeadEvent;
import net.minecraft.server.v1_13_R2.BlockPosition;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import net.minecraft.server.v1_13_R2.TileEntitySkull;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import static org.bukkit.Bukkit.getLogger;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;


public class Database implements Listener{
   public static ItemStack item = new ItemStack(Material.STONE);
   @EventHandler
   public void onDatabaseLoad(DatabaseLoadEvent e){
       HeadDatabaseAPI api = new HeadDatabaseAPI();
       try{
           getLogger().info("Database has been loaded");
           item = api.getItemHead("22751");
           getLogger().info( api.getItemID(item) );
       }
       catch(NullPointerException nullpointer){
          getLogger().info( "could not find the head you were looking for" );
       }
   }
   @EventHandler
   public void onBreak(BlockBreakEvent b){
       //HeadDatabaseAPI api = new HeadDatabaseAPI();
       //getLogger().info( api.getItemID(item) );
        Block bl = b.getBlock();
        Player p = b.getPlayer();
        
        //NBTTagCompound nbt = new NBTTagCompound();
        
        Block block = p.getLocation().getBlock();
        block.setType(bl.getType());/*
        block.setBlockData(bl.getBlockData(), true);*/
        setSkullUrl("http://textures.minecraft.net/texture/86fd37aaf22730e0dcae98dfca8b8e77771c6a18bb9efbf73387e4e2f1bdc", block);
        
   }
   public static void setSkullUrl(String skinUrl, Block block) {
    block.setType(Material.valueOf("SKULL"));
    Skull skullData = (Skull)block.getState();
    skullData.setSkullType(SkullType.PLAYER);
    //TileEntitySkull skullTile = (TileEntitySkull)((CraftWorld)block.getWorld()).getHandle().getTileEntity(new BlockPosition(block.getX(), block.getY(), block.getZ()));
    //skullTile.setGameProfile(getNonPlayerProfile(skinUrl));
    block.getState().update(true);
   }
    public static GameProfile getNonPlayerProfile(String skinURL) {
    GameProfile newSkinProfile = new GameProfile(UUID.randomUUID(), null);
    newSkinProfile.getProperties().put("textures", new Property("textures", Base64Coder.encodeString("{textures:{SKIN:{url:\"" + skinURL + "\"}}}")));
    return newSkinProfile;
    }   
}
