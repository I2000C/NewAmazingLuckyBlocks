package me.i2000c.newalb.listeners.objects;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.listeners.interact.CustomProjectileHitEvent;
import me.i2000c.newalb.utils.BowItem;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;


public class ExplosiveBow extends BowItem{
    
    @Override
    public void onArrowHit(CustomProjectileHitEvent e){
        Entity projectile = e.getDamager();
        float power = (float) ConfigManager.getConfig().getDouble("Objects.ExplosiveBow.explosionPower");
        boolean withFire = ConfigManager.getConfig().getBoolean("Objects.ExplosiveBow.createFire");
        boolean breakBlocks = ConfigManager.getConfig().getBoolean("Objects.ExplosiveBow.breakBlocks");
        Location loc = projectile.getLocation();
        loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, withFire, breakBlocks);
        projectile.remove();
    }
  
    @Override
    public void onArrowShooted(EntityShootBowEvent e){
        super.setClassMetadata(e.getProjectile());
    }
    
    @Override
    public ItemStack buildItem(){
        return ItemBuilder.newItem(XMaterial.BOW)
                .withDisplayName(getDisplayName())
                .addEnchantment(Enchantment.ARROW_DAMAGE, 1)
                .build();
    }
}
