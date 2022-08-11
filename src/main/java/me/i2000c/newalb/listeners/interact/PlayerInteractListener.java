package me.i2000c.newalb.listeners.interact;

import java.util.EnumMap;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.listeners.objects.MaterialChecker;
import me.i2000c.newalb.utils.WorldList;
import me.i2000c.newalb.utils2.Task;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
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
        //<editor-fold defaultstate="collapsed" desc="Code">
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
                        e.setCancelled(true);
                        return;
                    }
                    
                    if(!specialItem.isCooldownExpired(player)){
                        specialItem.sendRemainingSecondsMessage(player);
                        e.setCancelled(true);
                        return;
                    }
                    
                    specialItem.onPlayerInteract(e);
                }
            }
        }
//</editor-fold>
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    private static void onItemPickup(PlayerPickupItemEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Item item = e.getItem();
        
        if(!WorldList.isRegistered(item.getWorld().getName())) {
            return;
        }
        
        if(item.hasMetadata(SpecialItem.CLASS_METADATA_TAG)){
            SpecialItem specialItem = SpecialItem.getClassMetadata(item);
            if(specialItem != null){
                specialItem.onItemPickup(e);
            }
        }
//</editor-fold>
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    private static void onProjectileHitEntity(EntityDamageByEntityEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Entity damager = e.getDamager();
        
        if(!WorldList.isRegistered(damager.getWorld().getName())) {
            return;
        }

        SpecialItem specialItem = SpecialItem.getClassMetadata(damager);
        if(specialItem != null){
            if(damager instanceof Projectile){
                boolean entityHit = true;
                specialItem.setCustomMetadata(damager, entityHit);
                
                CustomProjectileHitEvent event = new CustomProjectileHitEvent(e);
                specialItem.onArrowHit(event);
            }else{
                specialItem.onEntityDamaged(e);
            }
        }
//</editor-fold>
    }    
    @EventHandler(priority = EventPriority.HIGHEST)
    private static void onArrowHit(ProjectileHitEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Entity projectile = e.getEntity();
        if(projectile instanceof Arrow){
            Arrow arrow = (Arrow) projectile;
            
            if(!WorldList.isRegistered(arrow.getWorld().getName())) {
                return;
            }
            
            SpecialItem specialItem = SpecialItem.getClassMetadata(arrow);
            if(specialItem != null){
                specialItem.setCustomMetadata(arrow, false);
                
                Task.runTask(() -> {
                    Boolean entityHit = (Boolean) SpecialItem.getCustomMetadata(arrow);
                    
                    if(!entityHit){
                        Block block = arrow.getLocation().getBlock();
                        CustomProjectileHitEvent event = new CustomProjectileHitEvent(arrow, block);
                        specialItem.onArrowHit(event);
                    }
                    
                    SpecialItem.removeClassMetadata(arrow);
                }, 2L);
            }
        }
//</editor-fold>
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    private static void onArrowShooted(EntityShootBowEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(e.getEntity() == null || !(e.getEntity() instanceof Player)){
            return;
        }
        
        Player player = (Player) e.getEntity();
        
        if(!WorldList.isRegistered(player.getWorld().getName())){
            return;
        }
        
        int specialItemID = SpecialItem.getSpecialItemID(e.getBow());
        if(specialItemID >= 0 && specialItemID < SPECIAL_ITEM_NAMES.length){
            SpecialItemName specialItemName = SPECIAL_ITEM_NAMES[specialItemID];
            SpecialItem specialItem = EVENTS.get(specialItemName);
            if(specialItem != null){
                if(!specialItem.checkPermission(player)){
                    e.setCancelled(true);
                    return;
                }
                
                if(!specialItem.isCooldownExpired(player)){
                    specialItem.sendRemainingSecondsMessage(player);
                    e.setCancelled(true);
                    return;
                }
                
                specialItem.onArrowShooted(e);
            }
        }
//</editor-fold>
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    private static void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e){
        Entity entity = e.getRightClicked();
        
        if(!WorldList.isRegistered(entity.getWorld().getName())) {
            return;
        }
        
        if(entity.hasMetadata(SpecialItem.CLASS_METADATA_TAG)){
            SpecialItem specialItem = SpecialItem.getClassMetadata(entity);
            if(specialItem != null){
                specialItem.onPlayerInteractAtEntity(e);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    private static void onFallingBlockConvert(EntityChangeBlockEvent e){
        Entity entity = e.getEntity();
        
        if(!WorldList.isRegistered(entity.getWorld().getName())) {
            return;
        }
        
        if(entity.hasMetadata(SpecialItem.CLASS_METADATA_TAG)){
            SpecialItem specialItem = SpecialItem.getClassMetadata(entity);
            if(specialItem != null){
                specialItem.onFallingBlockConvert(e);
            }
        }
    }
}
