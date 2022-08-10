package me.i2000c.newalb.listeners.interact;

import java.util.EnumMap;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.listeners.objects.MaterialChecker;
import me.i2000c.newalb.utils.WorldList;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener{
    private static final EnumMap<SpecialItemName, SpecialItem> EVENTS = new EnumMap<>(SpecialItemName.class);
    private static final SpecialItemName[] SPECIAL_ITEM_NAMES = SpecialItemName.values();
    
    public static void registerSpecialtem(SpecialItem specialItem){
        EVENTS.putIfAbsent(specialItem.getSpecialItemName(), specialItem);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    private static void onPlayerInteract(PlayerInteractEvent e){
        ItemStack stack = e.getItem();
        if(stack != null){
            Player player = e.getPlayer();
            
            if(NewAmazingLuckyBlocks.getMinecraftVersion() != MinecraftVersion.v1_8){
                if(e.getHand() == EquipmentSlot.OFF_HAND){
                    return;
                }
            }
            
            if(MaterialChecker.check(e)){
                return;
            }
            
            if(!WorldList.isRegistered(player.getWorld().getName())){
                return;
            }
            
            int specialItemID = SpecialItem.getSpecialItemID(stack);
            if(specialItemID >= 0 && specialItemID < SPECIAL_ITEM_NAMES.length){
                SpecialItemName specialItemName = SPECIAL_ITEM_NAMES[specialItemID];
                SpecialItem specialItem = EVENTS.get(specialItemName);
                if(specialItem != null){                    
                    if(!specialItem.checkPermission(player)){
                        return;
                    }
                    
                    if(!specialItem.isCooldownExpired(player)){
                        specialItem.sendRemainingSecondsMessage(player);
                        return;
                    }
                    
                    specialItem.onPlayerInteract(e);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    private static void onItemPickup(PlayerPickupItemEvent e){
        Item item = e.getItem();
        if(item.hasMetadata(SpecialItem.METADATA_TAG)){
            SpecialItem specialItem = SpecialItem.getMetadata(item);
            if(specialItem != null){
                specialItem.onItemPickup(e);
            }                
        }
    }
}
