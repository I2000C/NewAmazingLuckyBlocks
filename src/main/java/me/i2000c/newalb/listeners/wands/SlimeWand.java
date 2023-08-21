package me.i2000c.newalb.listeners.wands;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class SlimeWand extends SpecialItem{
    
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
            Location eyeLocation = player.getEyeLocation();
            Vector direction = eyeLocation.getDirection().multiply(4.5);
            Slime slime = (Slime) player.getWorld().spawn(eyeLocation.add(direction), Slime.class);
            slime.setVelocity(eyeLocation.getDirection().multiply(1.8));
        }
    }
    
    @Override
    public ItemStack buildItem(){
        return ItemBuilder.newItem(XMaterial.MUSIC_DISC_CHIRP)
                .withDisplayName(getDisplayName())
                .withLore(super.getLoreOfWand())
                .setNbtTag(getCustomModelData(), CUSTOM_MODEL_DATA_TAG)
                .build();
    }
}
