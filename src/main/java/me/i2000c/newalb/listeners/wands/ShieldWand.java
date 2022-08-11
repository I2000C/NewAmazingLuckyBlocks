package me.i2000c.newalb.listeners.wands;

import com.cryptomorin.xseries.XMaterial;
import java.util.HashSet;
import java.util.Set;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.OtherUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ShieldWand extends SpecialItem{
    private ItemStack shieldItemStack;
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
            e.setCancelled(true);
            
            Player player = e.getPlayer();
            if(!super.decreaseWandUses(e.getItem(), e.getPlayer())){
                e.setCancelled(true);
                return;
            }
            
            super.updatePlayerCooldown(player);
            World w = player.getWorld();
            boolean protect = ConfigManager.getConfig().getBoolean("Wands.ShieldWand.protect-structures");
            float radius = (float) ConfigManager.getConfig().getDouble("Wands.ShieldWand.radius");

            for(Location l : this.generateSphere(player.getLocation().add(0, 1D, 0), radius, true)){
                Block b = w.getBlockAt(l);
                if(!protect || protect && b.getType() == Material.AIR){
                    BlockPlaceEvent e2 = new BlockPlaceEvent(b, b.getState(), b, e.getItem(), player, true);
                    Bukkit.getPluginManager().callEvent(e2);
                    if(!e2.isCancelled()){
                        b.setType(shieldItemStack.getType());
                        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                            b.setData((byte) shieldItemStack.getDurability());
                        }
                    }
                }
            }
        }
    }
  
    public Set<Location> generateSphere(Location center, float radius, boolean hollow){
        //<editor-fold defaultstate="collapsed" desc="Code">
        //code by: https://www.youtube.com/watch?v=oKpgn38mj8Y

        Set<Location> circleBlocks = new HashSet<>();
        int bx = center.getBlockX();
        int by = center.getBlockY();
        int bz = center.getBlockZ();

        for(float x=bx-radius;x<=bx+radius;x++){
            for(float y=by-radius;y<=by+radius;y++){
                for(float z=bz-radius;z<=bz+radius;z++){
                    double distance = ((bx - x) * (bx - x)) + ((by - y) * (by - y)) + ((bz - z) * (bz - z));

                    if(distance < radius * radius && !(hollow && distance < ((radius - 1) * (radius - 1)))){
                        Location l = new Location(center.getWorld(), x, y, z);
                        circleBlocks.add(l);
                    }
                }
            }
        }

        boolean withfloor = ConfigManager.getConfig().getBoolean("Wands.Shield.withfloor");
        if(!withfloor){
            Set<Location> circleBlocks2 = new HashSet<>();
            circleBlocks2.addAll(circleBlocks);
            for(Location loc : circleBlocks2){
                if(loc.getBlockY() == by-radius+1){
                    circleBlocks.remove(loc);
                }
            }
        }
        return circleBlocks;
        //</editor-fold>
    }
    
    @Override
    public ItemStack buildItem(){
        try{
            shieldItemStack = OtherUtils.parseMaterial(ConfigManager.getConfig().getString("Wands.ShieldWand.ShieldWandBlock"));
        }catch(Exception ex){
            Logger.log("&cInvalid block in config at Wands.ShieldWand.ShieldWandBlock");
        }
        
        return ItemBuilder.newItem(XMaterial.MUSIC_DISC_STRAD)
                .withDisplayName(getDisplayName())
                .withLore(super.getLoreOfWand())
                .build();
    }
}