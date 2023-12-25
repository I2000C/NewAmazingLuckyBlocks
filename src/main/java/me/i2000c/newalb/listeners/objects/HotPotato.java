package me.i2000c.newalb.listeners.objects;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.MetadataManager;
import me.i2000c.newalb.utils2.Task;
import me.i2000c.newalb.utils2.WorldGuardManager;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class HotPotato extends SpecialItem{
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">        
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
            e.setCancelled(true);
            
            super.decreaseAmountOfItem(e);
            
            Location loc = e.getPlayer().getEyeLocation();
            Item item = loc.getWorld().dropItem(loc, getItem());
            MetadataManager.setClassMetadata(item, this);
            item.setVelocity(e.getPlayer().getLocation().getDirection());
            
            long delayTicks = ConfigManager.getConfig().getLong("Objects.HotPotato.ticksBeforeExplosion");
            float power = (float) ConfigManager.getConfig().getDouble("Objects.HotPotato.explosionPower");
            boolean withFire = ConfigManager.getConfig().getBoolean("Objects.HotPotato.createFire");
            boolean breakBlocks = ConfigManager.getConfig().getBoolean("Objects.HotPotato.breakBlocks");
            
            Task.runTask(() -> {
                if(item.isDead()) {
                    return;
                }
                
                int amount = item.getItemStack().getAmount();
                Location targetLoc = item.getLocation();
                if(WorldGuardManager.canBreak(e.getPlayer(), targetLoc)) {
                    for(int i=0; i<amount; i++) {
                        targetLoc.getWorld().createExplosion(targetLoc.getX(), targetLoc.getBlockY(), targetLoc.getZ(), power, withFire, breakBlocks);
                    }
                }
                
                item.remove();
            }, delayTicks);
        }
//</editor-fold>
    }
    
    @Override
    public void onItemPickup(PlayerPickupItemEvent e){
        e.setCancelled(true);
    }
        
    @Override
    public ItemStack buildItem(){
        return ItemBuilder.newItem(XMaterial.BAKED_POTATO)
                .addEnchantment(Enchantment.FIRE_ASPECT, 1)
                .build();
    }
}
