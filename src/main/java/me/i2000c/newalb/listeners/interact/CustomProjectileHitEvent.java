package me.i2000c.newalb.listeners.interact;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

/**
 * Called when a projectile hits an object
 */
public class CustomProjectileHitEvent extends Event implements Cancellable {
    private static final String INVALID_EVENT_MESSAGE = "Can't use this method with ProjectileHitEvent";
    
    private final Projectile projectile;
    private final Entity hitEntity;
    private final Block hitBlock;
    
    private final EntityDamageByEntityEvent damageEvent;
    private final ProjectileHitEvent hitEvent;
    
    private EntityDamageByEntityEvent getSafeDamageEvent() {
        if(damageEvent == null) {
            throw new IllegalArgumentException(INVALID_EVENT_MESSAGE);
        }
        return damageEvent;
    }

    public CustomProjectileHitEvent(EntityDamageByEntityEvent e){
        this.hitEntity = e.getEntity();
        this.projectile = (Projectile) e.getDamager();
        this.hitBlock = null;
        this.damageEvent = e;
        this.hitEvent = null;
    }
    
    public CustomProjectileHitEvent(ProjectileHitEvent e){
        this.hitEntity = null;
        this.projectile = e.getEntity();
        this.hitBlock = e.getEntity().getLocation().getBlock();
        this.damageEvent = null;
        this.hitEvent = e;
    }

    /**
     * Gets the block that was hit, if it was a block that was hit.
     *
     * @return hit block or else null
     */
    public Block getHitBlock() {
        return hitBlock;
    }

    /**
     * Gets the entity that was hit, if it was an entity that was hit.
     *
     * @return hit entity or else null
     */
    public Entity getHitEntity() {
        return hitEntity;
    }
    
    /**
     * Gets the projectile involved in this event
     * 
     * @return the projectile
     */
    public Projectile getProjectile() {
        return projectile;
    }
    
    @Override
    public void setCancelled(boolean cancel) {
        getSafeDamageEvent().setCancelled(cancel);
    }
    
    @Override
    public boolean isCancelled() {
        return getSafeDamageEvent().isCancelled();
    }
    
    // Delegated methods
    public double getOriginalDamage(EntityDamageEvent.DamageModifier type) throws IllegalArgumentException {
        return getSafeDamageEvent().getOriginalDamage(type);
    }
    public void setDamage(EntityDamageEvent.DamageModifier type, double damage) throws IllegalArgumentException, UnsupportedOperationException {
        getSafeDamageEvent().setDamage(type, damage);
    }
    public double getDamage(EntityDamageEvent.DamageModifier type) throws IllegalArgumentException {
        return getSafeDamageEvent().getDamage(type);
    }
    public boolean isApplicable(EntityDamageEvent.DamageModifier type) throws IllegalArgumentException {
        return getSafeDamageEvent().isApplicable(type);
    }
    public double getDamage() {
        return getSafeDamageEvent().getDamage();
    }
    public final double getFinalDamage() {
        return getSafeDamageEvent().getFinalDamage();
    }
    public void setDamage(double damage) {
        getSafeDamageEvent().setDamage(damage);
    }
    public EntityDamageEvent.DamageCause getCause() {
        return getSafeDamageEvent().getCause();
    }
    
    
    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
