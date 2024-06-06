package me.i2000c.newalb.listeners.objects;

import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.listeners.interact.CustomProjectileHitEvent;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import me.i2000c.newalb.utils2.MetadataManager;
import me.i2000c.newalb.utils2.WorldGuardManager;


public class ExplosiveBow extends SpecialItem{
    
    @Override
    public void onArrowHit(CustomProjectileHitEvent e){
        Projectile projectile = e.getProjectile();
        float power = ConfigManager.getMainConfig().getFloat("Objects.ExplosiveBow.explosionPower");
        boolean withFire = ConfigManager.getMainConfig().getBoolean("Objects.ExplosiveBow.createFire");
        boolean breakBlocks = ConfigManager.getMainConfig().getBoolean("Objects.ExplosiveBow.breakBlocks");
        Location loc = projectile.getLocation();
        
        if(WorldGuardManager.canBreak((Player) e.getShooter(), loc)) {
            loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, withFire, breakBlocks);
        }
        
        projectile.remove();
    }
  
    @Override
    public void onArrowShooted(EntityShootBowEvent e){
        MetadataManager.setClassMetadata(e.getProjectile(), this);
    }
    
    @Override
    public ItemStack buildItem(){
        return ItemStackWrapper.newItem(XMaterial.BOW)
                               .addEnchantment(Enchantment.ARROW_DAMAGE, 1)
                               .toItemStack();
    }
}
