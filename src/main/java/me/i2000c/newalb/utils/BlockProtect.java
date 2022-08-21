package me.i2000c.newalb.utils;

import me.i2000c.newalb.custom_outcomes.rewards.TypeManager;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.listeners.interact.SpecialItemName;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;


public class BlockProtect implements Listener{
    @EventHandler
    public void noSkullCrash(BlockFromToEvent event){
        if(WorldConfig.isRegistered(event.getToBlock().getWorld().getName()) && 
            TypeManager.getType(event.getToBlock()) != null){
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void noSkullCrash2(EntityExplodeEvent event){
        String worldName = event.getLocation().getWorld().getName();
        if(WorldConfig.isRegistered(worldName)){
            event.blockList().removeIf(block -> TypeManager.getType(block) != null);
        }
    }
    
    @EventHandler
    public void noSkullCrash3(BlockExplodeEvent event){
        String worldName = event.getBlock().getLocation().getWorld().getName();
        if(WorldConfig.isRegistered(worldName)){
            event.blockList().removeIf(block -> TypeManager.getType(block) != null);
        }
    }
    
    @EventHandler
    public void playerRenameItem(InventoryClickEvent event){
        if(event.getView().getType() == InventoryType.ANVIL) {
            ItemStack item = event.getView().getItem(0);
            if(event.getRawSlot() == 2) {                
                if(item.getType() != Material.AIR && event.getView().getItem(2).getType() != Material.AIR) {
                    if(TypeManager.getType(item) != null){
                        event.setCancelled(true);                                              
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void playerRenameItem2(InventoryClickEvent event){
        if(event.getView().getType() != InventoryType.ANVIL){
            return;
        }
        
        ItemStack sk0 = event.getView().getItem(0);
        ItemStack sk1 = event.getView().getItem(1);
        ItemStack sk2 = event.getView().getItem(2);
        
        if(sk0.getType() == Material.AIR || sk1.getType() == Material.AIR || sk2.getType() == Material.AIR){
            return;
        }
        
        SpecialItemName specialItemName = SpecialItem.getSpecialItemName(sk0);
        if(specialItemName == SpecialItemName.auto_bow || specialItemName == SpecialItemName.multi_bow){            
            String displayName = ItemBuilder.fromItem(sk0, false)
                    .getDisplayName();            
            ItemBuilder.fromItem(sk2, false)
                    .withDisplayName(displayName);
            event.getView().setItem(2, sk2);
        }
    }
}
