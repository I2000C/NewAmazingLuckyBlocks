package me.i2000c.newalb.listeners.blocks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.lucky_blocks.rewards.TypeManager;
import me.i2000c.newalb.utils.locations.LocationManager;
import me.i2000c.newalb.utils.locations.WorldManager;
import me.i2000c.newalb.utils.logging.Logger;

public class BlockPlaceListener implements Listener{
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlaced(BlockPlaceEvent e){
        Player p = e.getPlayer();
        if(!WorldManager.isEnabled(p.getWorld().getName())){
            return;
        }
        
        ItemStack stack = e.getItemInHand();
        TypeManager.Result result = TypeManager.canPlaceBlock(p, stack);
        switch(result.resultCode){
            case TypeManager.RESULT_OK:
                LocationManager.registerLocation(e.getBlock().getLocation());
            case TypeManager.RESULT_NOT_LUCKYBLOCK:
                break;
            case TypeManager.RESULT_NO_GLOBAL_PERMISSION:
            case TypeManager.RESULT_NO_LOCAL_PERMISSION:
                Logger.sendMessage(ConfigManager.getLangMessage("NoPermission"), p);
                e.setCancelled(true);
                break;
        }
    }
}
