package me.i2000c.newalb.listeners.objects;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.Task;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class HomingBow extends SpecialItem{
  
    @Override
    public void onArrowShooted(EntityShootBowEvent e){
        if(e.getEntity() instanceof Player){
            executeTask((Player) e.getEntity(), e.getProjectile());
        }
    }
    
    private static void executeTask(Player player, Entity projectile){
        //<editor-fold defaultstate="collapsed" desc="Code">
        double radius = ConfigManager.getConfig().getDouble("Objects.HomingBow.arrowRadius");
        double radiusS = radius * radius;
        double multiplier = ConfigManager.getConfig().getDouble("Objects.HomingBow.velocityMultiplier");
        boolean playersOnly = ConfigManager.getConfig().getBoolean("Objects.HomingBow.followPlayersOnly");
        boolean followInvisibleEntities = ConfigManager.getConfig().getBoolean("Objects.HomingBow.followInvisibleEntities");
        
        Task task = new Task(){
            private double minDistanceSquared = -1.0;
            private LivingEntity nearestEnt = null;
            private final Vector zeroVector = new Vector();
            
            @Override
            public void run(){
                if(projectile.isOnGround() || projectile.isDead() || projectile.getVelocity().equals(zeroVector)){
                    this.cancel();
                    return;
                }
                
                if(nearestEnt == null){
                    if(playersOnly){
                        for(Player p : projectile.getWorld().getPlayers()){
                            if(p.equals(player)){
                                continue;
                            }

                            GameMode gamemode = p.getGameMode();
                            if(gamemode == GameMode.CREATIVE || gamemode == GameMode.SPECTATOR) {
                                continue;
                            }
                            
                            if(!followInvisibleEntities
                                    && p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                continue;
                            }
                            
                            LivingEntity le = p;
                            double distanceSquared = projectile.getLocation().distanceSquared(le.getEyeLocation());
                            if(nearestEnt == null || distanceSquared < minDistanceSquared){
                                if(distanceSquared <= radiusS){
                                    nearestEnt = le;
                                    minDistanceSquared = distanceSquared;
                                }
                            }
                        }
                    }else{
                        for(Entity ent : projectile.getNearbyEntities(radius, radius, radius)){
                            if(!(ent instanceof LivingEntity)){
                                continue;
                            }
                            
                            if(ent.equals(player)){
                                continue;
                            }
                            
                            LivingEntity le = (LivingEntity) ent;
                            if(!followInvisibleEntities
                                    && le.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                continue;
                            }
                            
                            double distanceSAux = projectile.getLocation().distanceSquared(le.getEyeLocation());
                            if(nearestEnt == null || distanceSAux < minDistanceSquared){
                                nearestEnt = le;
                                minDistanceSquared = distanceSAux;
                            }
                        }
                    }
                } else {
                    if(nearestEnt.isDead()){
                        this.cancel();
                    }else{
                        Vector v = nearestEnt.getEyeLocation().toVector().subtract(projectile.getLocation().toVector());
                        if(minDistanceSquared < 1.5){
                            projectile.setVelocity(v);
                            this.cancel();
                            //https://www.spigotmc.org/threads/homing-arrows.435839/
                            //https://www.google.com/search?q=homing+arrow+spigot&ei=enjjYMvLMNGcjLsPvvWCmAE&oq=homing+arrow+spigot&gs_lcp=Cgdnd3Mtd2l6EAMyBAgAEBM6CAgAELEDEIMBOg4ILhCxAxCDARDHARCjAjoFCAAQsQM6CwguELEDEMcBEKMCOgIILjoKCAAQsQMQgwEQQzoECAAQQzoECC4QQzoHCAAQsQMQQzoHCC4QsQMQQzoKCC4QsQMQQxCTAjoGCAAQChBDOgIIADoGCAAQFhAeOggIABAWEB4QEzoECAAQDToGCAAQDRAeSgQIQRgAUM9xWKuMAWCsjQFoAHACeACAAY0BiAG2DZIBBDE5LjGYAQCgAQGqAQdnd3Mtd2l6wAEB&sclient=gws-wiz&ved=0ahUKEwjL8P7o7szxAhVRDmMBHb66ABMQ4dUDCBg&uact=5
                        }else{
                            projectile.setVelocity(v.multiply(multiplier));
                        }
                    }
                }
            }
        };
        task.runTask(0L, 1L);
//</editor-fold>
    }
    
    @Override
    public ItemStack buildItem(){
        return ItemBuilder.newItem(XMaterial.BOW)
                .addEnchantment(Enchantment.ARROW_DAMAGE, 1)
                .build();
    }
}
