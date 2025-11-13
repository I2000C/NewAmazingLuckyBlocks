package me.i2000c.newalb.listeners.wands;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.integration.WorldGuardManager;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class LightningWand extends SpecialItem{
    
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
            Block block = player.getTargetBlock(null, 120);
            if(WorldGuardManager.canUse(player, block.getLocation())) {
                player.getWorld().strikeLightning(block.getLocation());
            }
        }        
    }
    
    @Override
    public ItemStack buildItem(){
        return ItemStackWrapper.newItem(XMaterial.MUSIC_DISC_FAR)
                               .setLore(super.getLoreOfWand())
                               .toItemStack();
    }
}
