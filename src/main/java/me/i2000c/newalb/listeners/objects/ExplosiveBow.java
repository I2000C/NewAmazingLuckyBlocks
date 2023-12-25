package me.i2000c.newalb.listeners.objects;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.listeners.interact.CustomProjectileHitEvent;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.MetadataManager;
import me.i2000c.newalb.utils2.WorldGuardManager;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;


public class ExplosiveBow extends SpecialItem{
    
    @Override
    public void onArrowHit(CustomProjectileHitEvent e){
        Projectile projectile = e.getProjectile();
        float power = (float) ConfigManager.getConfig().getDouble("Objects.ExplosiveBow.explosionPower");
        boolean withFire = ConfigManager.getConfig().getBoolean("Objects.ExplosiveBow.createFire");
        boolean breakBlocks = ConfigManager.getConfig().getBoolean("Objects.ExplosiveBow.breakBlocks");
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
        return ItemBuilder.newItem(XMaterial.BOW)
                .addEnchantment(Enchantment.ARROW_DAMAGE, 1)
                .build();
    }
}
