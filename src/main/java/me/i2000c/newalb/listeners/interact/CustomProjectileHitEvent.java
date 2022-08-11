package me.i2000c.newalb.listeners.interact;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Called when a projectile hits an object
 */
public class CustomProjectileHitEvent extends EntityDamageByEntityEvent{
    private static final HandlerList handlers = new HandlerList();
    private final Entity hitEntity;
    private final Block hitBlock;

    public CustomProjectileHitEvent(EntityDamageByEntityEvent e){
        super(e.getDamager(), e.getEntity(), e.getCause(), e.getDamage());
        this.hitEntity = e.getEntity();
        this.hitBlock = null;
    }
    
    public CustomProjectileHitEvent(Projectile projectile, Block block){
        super(projectile, null, DamageCause.CUSTOM, 0.0);
        this.hitEntity = null;
        this.hitBlock = block;
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

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
