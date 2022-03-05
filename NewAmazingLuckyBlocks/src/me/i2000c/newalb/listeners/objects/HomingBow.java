package me.i2000c.newalb.listeners.objects;

import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.LangLoader;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.SpecialItem;
import me.i2000c.newalb.utils2.OtherUtils;
import me.i2000c.newalb.utils2.Task;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class HomingBow extends SpecialItem{
  
    @EventHandler
    private void onArrowShooted(EntityShootBowEvent e){
        if(e.getEntity() != null && e.getEntity() instanceof Player){
            Player shooter = (Player) e.getEntity();
            ItemStack item = e.getBow();
            if(item != null && item.hasItemMeta()){
                ItemMeta meta = item.getItemMeta();
                if(meta.hasDisplayName()){
                    ItemStack object = getItem();
                    String name = object.getItemMeta().getDisplayName();
                    if(item.getType().equals(Material.BOW) && meta.getDisplayName().equalsIgnoreCase(name)){                        
                        if(OtherUtils.checkPermission(shooter, "Objects.HomingBow")){
                            executeTask(shooter, e.getProjectile());
                        }
                    }
                }
            }
        }
    }
    
    public void executeTask(Player player, Entity projectile){
        double radius = ConfigManager.getConfig().getDouble("Objects.HomingBow.arrowRadius");
        double radiusS = radius * radius;
        double multiplier = ConfigManager.getConfig().getDouble("Objects.HomingBow.velocityMultiplier");
        boolean playersOnly = ConfigManager.getConfig().getBoolean("Objects.HomingBow.followPlayersOnly");
        
        Task task = new Task(){
            private double distanceS = -1.0;
            private LivingEntity nearestEnt = null;
            
            @Override
            public void run(){
                if(projectile.isOnGround() || projectile.isDead() || projectile.getVelocity().equals(new Vector())){
                    this.cancel();
                    return;
                }
                
                if(nearestEnt == null){
                    if(playersOnly){
                        for(Player p : projectile.getWorld().getPlayers()){
                            if(p.equals(player)){
                                continue;
                            }
                            
                            LivingEntity le = p;
                            double distanceSAux = projectile.getLocation().distanceSquared(le.getEyeLocation());
                            if(nearestEnt == null || distanceSAux < distanceS){
                                if(distanceSAux <= radiusS){
                                    nearestEnt = le;
                                    distanceS = distanceSAux;
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
                            double distanceSAux = projectile.getLocation().distanceSquared(le.getEyeLocation());
                            if(nearestEnt == null || distanceSAux < distanceS){
                                nearestEnt = le;
                                distanceS = distanceSAux;
                            }
                        }
                    }
                }                    
                if(nearestEnt != null){
                    if(nearestEnt.isDead()){
                        this.cancel();
                    }else{
                        Vector v = nearestEnt.getEyeLocation().toVector().subtract(projectile.getLocation().toVector());                    
                        if(distanceS < 1.5){
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
    }
    
    @Override
    public ItemStack buildItem(){
        ItemStack stack = new ItemStack(Material.BOW);
        
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Logger.color(LangLoader.getMessages().getString("Objects.HomingBow.name")));
        meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        stack.setItemMeta(meta);
        
        return stack;       
    }
}
