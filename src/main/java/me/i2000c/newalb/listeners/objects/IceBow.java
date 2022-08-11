package me.i2000c.newalb.listeners.objects;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.listeners.interact.CustomProjectileHitEvent;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.listeners.interact.SpecialItemName;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.Timer;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

public class IceBow extends SpecialItem{
    
    @Override
    public void onArrowHit(CustomProjectileHitEvent e){
        Entity projectile = e.getDamager();
        if(projectile instanceof Arrow){
            Arrow arrow = (Arrow) projectile;
            Entity hitEntity = e.getHitEntity();
            Block hitBlock = e.getHitBlock();
            
            if(hitEntity != null){
                Entity damagee = e.getEntity();
                if(ConfigManager.getConfig().getBoolean("Objects.IceBow.disableArrowKnockback")){
                    e.setCancelled(true);
                    ((Damageable) damagee).damage(e.getDamage(), arrow);
                    arrow.remove();
                }
                damagee.setFireTicks(0);
                Timer.getTimer().executeIceBow(damagee);
                
                if(!ConfigManager.getConfig().getBoolean("Objects.IceBow.generateSnow")){
                    e.getEntity().removeMetadata("NewAmazingLuckyBlocks", NewAmazingLuckyBlocks.getInstance());
                }
            }else if(hitBlock != null){
                double radius = ConfigManager.getConfig().getDouble("Objects.IceBow.snowRadius");
                simulateSnow(hitBlock.getLocation(), radius);
            }
        }
    }
    
    @Override
    public void onArrowShooted(EntityShootBowEvent e){
        super.setClassMetadata(e.getProjectile());
    }
    
    private static void simulateSnow(Location loc, double radius){
        //<editor-fold defaultstate="collapsed" desc="Code">
        double radiusSq = radius * radius;
        int ox = loc.getBlockX();
        int oy = loc.getBlockY();
        int oz = loc.getBlockZ();
        
        int ceilRadius = (int) Math.ceil(radius);
        for(int x = ox - ceilRadius; x <= ox + ceilRadius; x++){
            for(int z = oz - ceilRadius; z <= oz + ceilRadius; z++){
                Location l = new Location(loc.getWorld(), x, oy, z);
                if(loc.distanceSquared(l) <= radiusSq){
                    for(int y = oy - ceilRadius; y <= oy + ceilRadius; y++){
                        l.setY(y);
                        Block b = l.getBlock();
                        switch(b.getType()){
                            case AIR:
                                Material material = b.getRelative(0, -1, 0).getType();
                                if(material.isSolid()){
                                    b.setType(Material.SNOW);
                                }
                                break;
                            case WATER:
                            case STATIONARY_WATER:
                                b.setType(Material.ICE);
                                break;
                            case LAVA:
                            case STATIONARY_LAVA:
                                b.setType(Material.OBSIDIAN);
                                break;
                            case FIRE:
                                b.setType(Material.SNOW);
                                break;
                        }
                    }
                }
            }
        }
//</editor-fold>
    }
    
    @Override
    public ItemStack buildItem(){
        return ItemBuilder.newItem(XMaterial.BOW)
                .withDisplayName(getDisplayName())
                .addEnchantment(Enchantment.DURABILITY, 5)
                .build();
    }
    
    @Override
    public SpecialItemName getSpecialItemName(){
        return SpecialItemName.ice_bow;
    }
}
