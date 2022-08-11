package me.i2000c.newalb.listeners.wands;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.Timer;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class FrostPathWand extends SpecialItem{
    
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
            player.playSound(player.getLocation(), XSound.BLOCK_GLASS_BREAK.parseSound(), 2.0f, 1.0f);
            Location loc1 = player.getLocation();
            Location loc2 = player.getTargetBlock(null, 120).getLocation();
            int distance = (int) loc1.distance(loc2);

            float minPitch = (float) ConfigManager.getConfig().getDouble("Wands.FrostPathWand.minPitch");
            float maxPitch = (float) ConfigManager.getConfig().getDouble("Wands.FrostPathWand.maxPitch");

            if(loc1.getPitch() > maxPitch || loc1.getPitch() < minPitch){
                return;
            }
            Timer.getTimer().executeFrostPathWand(player, distance, loc2);
        }
    }
    
    @Override
    public ItemStack buildItem(){
        return ItemBuilder.newItem(XMaterial.MUSIC_DISC_WAIT)
                .withDisplayName(getDisplayName())
                .withLore(super.getLoreOfWand())
                .build();
    }
}
