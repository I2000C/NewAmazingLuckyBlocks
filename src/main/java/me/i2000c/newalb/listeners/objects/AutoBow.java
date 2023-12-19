package me.i2000c.newalb.listeners.objects;

import com.cryptomorin.xseries.XMaterial;
import java.util.Optional;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.listeners.objects.utils.BowUtils;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class AutoBow extends SpecialItem {
    
    private double multiplier;
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR){
            e.setCancelled(true);
            
            Player player = e.getPlayer();
            ItemStack bowStack = e.getItem();
            
            boolean isFireBow = BowUtils.isFireBow(bowStack);
            boolean isInfiniteBow = BowUtils.isInfiniteBow(player, bowStack);
            
            ItemStack arrowStack;
            Optional<ItemStack> arrowStackOptional = BowUtils.getArrowFromPlayerInventory(player, !isInfiniteBow);
            if(arrowStackOptional.isPresent()) {
                arrowStack = arrowStackOptional.get();
            } else if(player.getGameMode() == GameMode.CREATIVE) {
                arrowStack = ItemBuilder.newItem(XMaterial.ARROW).build();
            } else {
                return;
            }
            
            Vector velocity = player.getLocation().getDirection().multiply(multiplier);
            BowUtils.launchArrow(player, arrowStack, isFireBow, isInfiniteBow, velocity);
            BowUtils.applyDurability(player, bowStack);
            BowUtils.cancelBowCharging(player);
        }
//</editor-fold>
    }
    
    @Override
    public ItemStack buildItem(){
        this.multiplier = ConfigManager.getConfig().getDouble(super.itemPathKey + ".velocityMultiplier");
        
        return ItemBuilder.newItem(XMaterial.BOW)
                .addEnchantment(Enchantment.ARROW_DAMAGE, 1)
                .build();
    }
}
