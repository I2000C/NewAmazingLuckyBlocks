package me.i2000c.newalb.listeners.wands;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class FireWand extends SpecialItem{
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
            e.setCancelled(true);
            
            Player player = e.getPlayer();
            if(!super.decreaseWandUses(e.getItem(), e.getPlayer())){
                e.setCancelled(true);
                return;
            }
            
            super.getPlayerCooldown().updateCooldown(player);            
            Vector direction = player.getEyeLocation().getDirection().multiply(4.0);
            Vector speed = player.getEyeLocation().getDirection().multiply(2.8);
            Location location = player.getEyeLocation().add(direction);
            float radius = (float) ConfigManager.getConfig().getDouble("Wands.FireWand.fire-radius");
            for(float x=0-radius;x<=0+radius;x++){
                for(float z=0-radius;z<=0+radius;z++){
                    Location spawnLocation = location.clone().add(x, 0, z);
                    FallingBlock fallingBlock = e.getPlayer().getWorld()
                            .spawnFallingBlock(spawnLocation, Material.FIRE, (byte)0);
                    fallingBlock.setVelocity(speed);
                }
            }
        }
    }
    
    @Override
    public ItemStack buildItem(){
        return ItemBuilder.newItem(XMaterial.MUSIC_DISC_MALL)
                .withLore(super.getLoreOfWand())
                .build();
    }
}
