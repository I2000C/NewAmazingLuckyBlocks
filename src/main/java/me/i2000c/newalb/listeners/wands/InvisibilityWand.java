package me.i2000c.newalb.listeners.wands;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InvisibilityWand extends SpecialItem{
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
            e.setCancelled(true);
            
            Player player = e.getPlayer();            
            if(!super.decreaseWandUses(e.getItem(), e.getPlayer())){
                e.setCancelled(true);
                return;
            }
            
            super.updatePlayerCooldown(player);
            int effectTime = ConfigManager.getConfig().getInt("Wands.InvisibilityWand.effect-time") * 20;
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, effectTime, 0), true);
        }
    }
    
    @Override
    public ItemStack buildItem(){
        return ItemBuilder.newItem(XMaterial.MUSIC_DISC_MELLOHI)
                .withDisplayName(getDisplayName())
                .withLore(super.getLoreOfWand())
                .setNbtTag(getCustomModelData(), CUSTOM_MODEL_DATA_TAG)
                .build();
    }
}
