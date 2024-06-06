package me.i2000c.newalb.listeners.wands;

import org.bukkit.Location;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils2.ItemStackWrapper;

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
            double radius = ConfigManager.getMainConfig().getDouble("Wands.FireWand.fire-radius");
            ItemStackWrapper wrapper = ItemStackWrapper.newItem(XMaterial.FIRE);
            for(double x=-radius;x<=radius;x++){
                for(double z=-radius;z<radius;z++){
                    Location spawnLocation = location.clone().add(x, 0, z);
                    FallingBlock fallingBlock = wrapper.spawnFallingBlock(spawnLocation);
                    fallingBlock.setVelocity(speed);
                }
            }
        }
    }
    
    @Override
    public ItemStack buildItem(){
        return ItemStackWrapper.newItem(XMaterial.MUSIC_DISC_MALL)
                               .setLore(super.getLoreOfWand())
                               .toItemStack();
    }
}
