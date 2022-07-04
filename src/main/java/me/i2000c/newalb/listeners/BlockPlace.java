package me.i2000c.newalb.listeners;

import me.i2000c.newalb.custom_outcomes.utils.TypeManager;
import me.i2000c.newalb.lang_utils.LangLoader;
import me.i2000c.newalb.utils.LocationManager;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.WorldList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockPlace implements Listener{
    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent e){
        Player p = e.getPlayer();
        if(!WorldList.isRegistered(p.getWorld().getName())){
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
                Logger.sendMessage(LangLoader.getMessages().getString("NoPermission"), p);
                e.setCancelled(true);
                break;
        }
    }
}