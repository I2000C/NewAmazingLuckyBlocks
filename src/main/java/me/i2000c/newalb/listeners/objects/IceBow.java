package me.i2000c.newalb.listeners.objects;

import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.lang_utils.LangLoader;
import me.i2000c.newalb.utils.BowItem;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.Timer;
import me.i2000c.newalb.utils.WorldList;
import me.i2000c.newalb.utils2.OtherUtils;
import me.i2000c.newalb.utils2.Task;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;


public class IceBow extends BowItem{
    private static final String TAG = "ice_bow";
    
    @EventHandler
    public void onArrowHitEntity(EntityDamageByEntityEvent e){    
        Entity damagee = e.getEntity();
        Entity damager = e.getDamager();
        String iceName = Logger.color(LangLoader.getMessages().getString("Objects.IceBow.name"));

        if(damager instanceof Arrow){
            Arrow arrow = (Arrow) damager;
            if(!WorldList.isRegistered(arrow.getWorld().getName())) {
                return;
            }
            
            if(arrow.hasMetadata(TAG)){
                arrow.setMetadata(TAG, new FixedMetadataValue(NewAmazingLuckyBlocks.getInstance(), false));
                if(ConfigManager.getConfig().getBoolean("Objects.IceBow.disableArrowKnockback")){
                    e.setCancelled(true);
                    ((Damageable) damagee).damage(e.getDamage(), e.getDamager());
                    damager.remove();
                }
                damagee.setFireTicks(0);
                Timer.getTimer().executeIceBow(damagee);
                
                if(!ConfigManager.getConfig().getBoolean("Objects.IceBow.generateSnow")){
                    e.getEntity().removeMetadata("NewAmazingLuckyBlocks", NewAmazingLuckyBlocks.getInstance());
                }
            }
        }
    }
    
    @EventHandler
    private void onArrowHit(ProjectileHitEvent e){
        if(!ConfigManager.getConfig().getBoolean("Objects.IceBow.generateSnow")){
            return;
        }
        
        Entity entity = e.getEntity();
        if(entity.hasMetadata(TAG)){
            /*//https://www.spigotmc.org/threads/get-the-blockface-a-projectile-hit-a-block-at.77104/#post-850867
            Location loc = entity.getLocation();
            Vector vec = entity.getVelocity();
            Location loc2 = new Location(loc.getWorld(), loc.getX()+vec.getX(), loc.getY()+vec.getY(), loc.getZ()+vec.getZ());
            Block block = loc2.getBlock();
            if(!block.isEmpty()){
                if(block.getRelative(0, 1, 0).isEmpty()){
                    Block b = block.getRelative(0, 1, 0);
                    int radius = 2;
                    this.simulateSnow(b.getLocation(), radius);
                }
            }*/
            Task.runTask(() -> {
                if(entity.getMetadata(TAG).isEmpty()){
                    return;
                }
                
                boolean blockHit = entity.getMetadata(TAG).get(0).asBoolean();
                if(blockHit){
                    double radius = ConfigManager.getConfig().getDouble("Objects.IceBow.snowRadius");
                    simulateSnow(entity.getLocation().getBlock().getLocation(), radius);
                }
                entity.removeMetadata(TAG, NewAmazingLuckyBlocks.getInstance());
            }, 2L);
        }
    }
    
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
                        if(OtherUtils.checkPermission(shooter, "Objects.IceBow")){
                            Arrow arrow = (Arrow) e.getProjectile();
                            arrow.setMetadata(TAG, new FixedMetadataValue(NewAmazingLuckyBlocks.getInstance(), true));
                        }                           
                    }
                }
            }
        }
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
        ItemStack stack = new ItemStack(Material.BOW);
        
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Logger.color(LangLoader.getMessages().getString("Objects.IceBow.name")));
        meta.addEnchant(Enchantment.DURABILITY, 5, true);
        stack.setItemMeta(meta);
        
        return stack;
    }
}