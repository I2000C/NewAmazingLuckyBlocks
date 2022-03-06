package me.i2000c.newalb.listeners.objects;

import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.LangLoader;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.SpecialItem;
import me.i2000c.newalb.utils.WorldList;
import me.i2000c.newalb.utils2.OtherUtils;
import me.i2000c.newalb.utils2.Task;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class HotPotato extends SpecialItem{
    private static final String TAG = "hot_potato";
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemStack object = getItem();
        if(!OtherUtils.checkItemStack(object, e.getItem())){
            return;
        }
        
        Player player = e.getPlayer();
        if(!WorldList.isRegistered(player.getWorld().getName())){
            return;
        }
        
        if(!OtherUtils.checkPermission(player, "HotPotato")){
            return;
        }
        
        if(e.getAction() == Action.RIGHT_CLICK_AIR){
            e.setCancelled(true);
            if(e.getItem().getAmount() > 1){
                e.getItem().setAmount(e.getItem().getAmount() - 1);
            }else{
                e.getPlayer().getInventory().clear(e.getPlayer().getInventory().getHeldItemSlot());
            }
            
            Location loc = e.getPlayer().getEyeLocation();
            Item item = loc.getWorld().dropItem(loc, object);
            item.setMetadata(TAG, new FixedMetadataValue(NewAmazingLuckyBlocks.getInstance(), NewAmazingLuckyBlocks.getInstance()));
            item.setVelocity(e.getPlayer().getLocation().getDirection());
            
            long delayTicks = ConfigManager.getConfig().getLong("Objects.HotPotato.ticksBeforeExplosion");
            float power = (float) ConfigManager.getConfig().getDouble("Objects.HotPotato.explosionPower");
            boolean withFire = ConfigManager.getConfig().getBoolean("Objects.HotPotato.createFire");
            boolean breakBlocks = ConfigManager.getConfig().getBoolean("Objects.HotPotato.breakBlocks");
            
            Task.runTask(() -> {
                Location targetLoc = item.getLocation();
                targetLoc.getWorld().createExplosion(targetLoc.getX(), targetLoc.getBlockY(), targetLoc.getZ(), power, withFire, breakBlocks);
                item.remove();
            }, delayTicks);
        }
//</editor-fold>
    }
    
    @EventHandler
    private void onItemPickup(PlayerPickupItemEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(e.getItem().hasMetadata(TAG)){
            if(e.getItem().getMetadata(TAG).get(0).value() instanceof NewAmazingLuckyBlocks){
                e.setCancelled(true);
            }
        }
//</editor-fold>
    }
        
    @Override
    public ItemStack buildItem(){
        ItemStack stack = new ItemStack(Material.BAKED_POTATO);
        
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Logger.color(LangLoader.getMessages().getString("Objects.HotPotato.name")));
        meta.addEnchant(Enchantment.FIRE_ASPECT, 1, true);
        stack.setItemMeta(meta);
        
        return stack;
    }
}
