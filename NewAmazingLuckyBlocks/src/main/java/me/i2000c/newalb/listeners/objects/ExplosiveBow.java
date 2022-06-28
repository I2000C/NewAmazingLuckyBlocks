package me.i2000c.newalb.listeners.objects;

import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.BowItem;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.LangLoader;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.OtherUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;


public class ExplosiveBow extends BowItem{
    private static final String TAG = "explosive_bow";
    
    @EventHandler
    private void onArrowHit(ProjectileHitEvent e){
        if(e.getEntity().hasMetadata(TAG)){
            float power = (float) ConfigManager.getConfig().getDouble("Objects.ExplosiveBow.explosionPower");
            boolean withFire = ConfigManager.getConfig().getBoolean("Objects.ExplosiveBow.createFire");
            boolean breakBlocks = ConfigManager.getConfig().getBoolean("Objects.ExplosiveBow.breakBlocks");
            Location loc = e.getEntity().getLocation();
            e.getEntity().getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, withFire, breakBlocks);
            e.getEntity().removeMetadata("NewAmazingLuckyBlocks", NewAmazingLuckyBlocks.getInstance());
            e.getEntity().remove();
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
                        if(OtherUtils.checkPermission(shooter, "Objects.ExplosiveBow")){
                            Arrow arrow = (Arrow) e.getProjectile();
                            arrow.setMetadata(TAG, new FixedMetadataValue(NewAmazingLuckyBlocks.getInstance(), TAG));
                        }                           
                    }
                }
            }
        }
    }
    
    @Override
    public ItemStack buildItem(){
        ItemStack stack = new ItemStack(Material.BOW);
        
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Logger.color(LangLoader.getMessages().getString("Objects.ExplosiveBow.name")));
        meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        stack.setItemMeta(meta);
        
        return stack;
    }
}