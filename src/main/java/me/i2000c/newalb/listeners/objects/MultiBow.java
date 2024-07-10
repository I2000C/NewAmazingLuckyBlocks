package me.i2000c.newalb.listeners.objects;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.List;
import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.listeners.objects.utils.BowUtils;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import me.i2000c.newalb.utils2.OtherUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public class MultiBow extends SpecialItem {
    
    private static final double MIN_SPREAD_ANGLE = 0.0;
    private static final double MAX_SPREAD_ANGLE = 360.0;
    
    private double spreadAngle;
    private int numberOfArrows;
    
    @Override
    public void onArrowShooted(EntityShootBowEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        e.setCancelled(true);
        
        Entity shooter = e.getEntity();
        Player player = shooter instanceof Player ? (Player) shooter : null;
        
        ItemStack bowStack = e.getBow();
        boolean isFireBow = BowUtils.isFireBow(bowStack);
        boolean isInfiniteBow = BowUtils.isInfiniteBow(shooter, bowStack);
        
        List<ItemStack> arrowItems;
        if(player != null) {
            arrowItems = BowUtils.getArrowsFromPlayerInventory(player, numberOfArrows, !isInfiniteBow);
            if(isInfiniteBow) {
                int requiredArrows = numberOfArrows - arrowItems.size();
                ItemStack arrowItem = ItemStackWrapper.newItem(XMaterial.ARROW).toItemStack();
                for(int i=0; i<requiredArrows; i++) {
                    arrowItems.add(arrowItem);
                }
            }

            if(arrowItems.isEmpty()) {
                return;
            }
        } else {
            arrowItems = new ArrayList<>();
            for(int i=0; i<numberOfArrows; i++) {
                arrowItems.add(XMaterial.ARROW.parseItem());
            }
        }
        
        Vector direction = e.getProjectile().getVelocity();
        double speedMultiplier = direction.length();
        direction.normalize();
        double angleBetweenArrows = spreadAngle / (arrowItems.size() - 1);

        for(int i = 0; i < arrowItems.size(); i++) {
            double currentAngle = i * angleBetweenArrows - (spreadAngle / 2.0);
            Vector newDirection = rotateVector(direction, currentAngle);
            BowUtils.launchArrow(this, (ProjectileSource) shooter, bowStack, isFireBow, isInfiniteBow, newDirection.multiply(speedMultiplier));
        }
        
        if(player != null) {
            BowUtils.applyDurability(player, bowStack);
            BowUtils.cancelBowCharging(player);
        }
//</editor-fold>
    }
    
    private static Vector rotateVector(Vector vector, double angleRadians) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        double x = vector.getX() * Math.cos(angleRadians) - vector.getZ() * Math.sin(angleRadians);
        double z = vector.getX() * Math.sin(angleRadians) + vector.getZ() * Math.cos(angleRadians);
        return vector.clone().setX(x).setZ(z).normalize();
//</editor-fold>
    }
    
    @Override
    public ItemStack buildItem(){
        double angleDegrees = ConfigManager.getMainConfig().getDouble(super.itemPathKey + ".spreadAngle");
        this.spreadAngle = Math.toRadians(OtherUtils.clamp(angleDegrees, MIN_SPREAD_ANGLE, MAX_SPREAD_ANGLE));
        this.numberOfArrows = ConfigManager.getMainConfig().getInt(super.itemPathKey + ".numberOfArrows");
        
        return ItemStackWrapper.newItem(XMaterial.BOW)
                .addEnchantment(XEnchantment.POWER, 1)
                .toItemStack();
    }
}

