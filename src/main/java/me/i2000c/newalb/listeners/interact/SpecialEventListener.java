package me.i2000c.newalb.listeners.interact;

import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.utils.WorldManager;
import me.i2000c.newalb.utils2.MetadataManager;
import me.i2000c.newalb.utils2.Task;
import me.i2000c.newalb.utils2.WorldGuardManager;
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

public class SpecialEventListener implements Listener{
    
    @EventHandler(priority = EventPriority.HIGHEST)
    private static void onPlayerInteract(PlayerInteractEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemStack stack = e.getItem();
        if(stack != null){
            Player player = e.getPlayer();
            
            if(!MinecraftVersion.CURRENT_VERSION.is_1_8()){
                if(e.getHand() == EquipmentSlot.OFF_HAND){
                    return;
                }
            }
            
            if(MaterialChecker.check(e)){
                return;
            }
            
            if(!WorldManager.isEnabled(player.getWorld().getName())){
                return;
            }
            
            SpecialItem specialItem = SpecialItems.getByItemStack(stack);
            if(specialItem != null){
                if(!specialItem.checkPermission(player)){
                    e.setCancelled(true);
                    return;
                }
                
                if(!specialItem.getPlayerCooldown().isCooldownExpired(player)){
                    specialItem.sendRemainingSecondsMessage(player);
                    e.setCancelled(true);
                    return;
                }
                
                if(!WorldGuardManager.canUse(player, player.getLocation())) {
                    e.setCancelled(true);
                    return;
                }
                
                if(e.getClickedBlock() != null) {
                    if(!WorldGuardManager.canUse(player, e.getClickedBlock().getLocation())) {
                        e.setCancelled(true);
                        return;
                    }
                }
                
                specialItem.onPlayerInteract(e);
            }
        }
//</editor-fold>
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    private static void onItemPickup(PlayerPickupItemEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Item item = e.getItem();
        
        if(!WorldManager.isEnabled(item.getWorld().getName())) {
            return;
        }
        
        SpecialItem specialItem = MetadataManager.getClassMetadata(item);        
        if(specialItem != null) {
            specialItem.onItemPickup(e);
        }
//</editor-fold>
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    private static void onEntityDamaged(EntityDamageByEntityEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Entity damager = e.getDamager();
        
        if(!WorldManager.isEnabled(damager.getWorld().getName())) {
            return;
        }

        SpecialItem specialItem = MetadataManager.getClassMetadata(damager);
        if(specialItem != null){
            if(damager instanceof Projectile){
                // Set custom metadata to avoid generating ProjectileHitEvent with block
                MetadataManager.setCustomMetadata(damager, new Object());
                
                CustomProjectileHitEvent event = new CustomProjectileHitEvent(e);
                specialItem.onArrowHit(event);
            }
            specialItem.onEntityDamaged(e);
        }
//</editor-fold>
    }    
    @EventHandler(priority = EventPriority.HIGHEST)
    private static void onArrowHit(ProjectileHitEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Entity projectile = e.getEntity();
        if(projectile instanceof Arrow){
            Arrow arrow = (Arrow) projectile;
            
            if(!WorldManager.isEnabled(arrow.getWorld().getName())) {
                return;
            }
            
            SpecialItem specialItem = MetadataManager.getClassMetadata(arrow);
            if(specialItem != null){
                Task.runTask(() -> {
                    // If custom metadata has been set, it means that
                    //      the projectile hit an entity so,
                    //      it shouldn't generate a ProjectileHitEvent with block
                    if(!MetadataManager.hasCustomMetadata(projectile)){
                        Block block = arrow.getLocation().getBlock();
                        CustomProjectileHitEvent event = new CustomProjectileHitEvent(e);
                        specialItem.onArrowHit(event);
                    }
                });
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
        
        if(!WorldManager.isEnabled(player.getWorld().getName())){
            return;
        }
        
        SpecialItem specialItem = SpecialItems.getByItemStack(e.getBow());
        if(specialItem != null){
            if(!specialItem.checkPermission(player)){
                e.setCancelled(true);
                return;
            }

            if(!specialItem.getPlayerCooldown().isCooldownExpired(player)){
                specialItem.sendRemainingSecondsMessage(player);
                e.setCancelled(true);
                return;
            }
            
            if(!WorldGuardManager.canUse(player, player.getLocation())) {
                e.setCancelled(true);
                return;
            }

            specialItem.onArrowShooted(e);
        }
//</editor-fold>
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    private static void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Entity entity = e.getRightClicked();
        
        if(!WorldManager.isEnabled(entity.getWorld().getName())) {
            return;
        }
        
        SpecialItem specialItem = MetadataManager.getClassMetadata(entity);
        if(specialItem != null) {
            specialItem.onPlayerInteractAtEntity(e);
        }
//</editor-fold>
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    private static void onFallingBlockConvert(EntityChangeBlockEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Entity entity = e.getEntity();
        
        if(!WorldManager.isEnabled(entity.getWorld().getName())) {
            return;
        }
        
        SpecialItem specialItem = MetadataManager.getClassMetadata(entity);
        if(specialItem != null) {
            specialItem.onFallingBlockConvert(e);
        }
//</editor-fold>
    }
}
