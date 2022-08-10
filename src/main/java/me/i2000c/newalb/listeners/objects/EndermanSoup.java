package me.i2000c.newalb.listeners.objects;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.listeners.interact.SpecialItemName;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleEffect;

public class EndermanSoup extends SpecialItem{
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.getAction() == Action.RIGHT_CLICK_AIR){            
            e.setCancelled(true);
            super.updatePlayerCooldown(e.getPlayer());

            SpecialItem.decreaseAmountOfItem(e);
            
            Player p = e.getPlayer();
            Vector v = p.getEyeLocation().getDirection();
            double multiplier = ConfigManager.getConfig().getDouble("Objects.EndermanSoup.speedMultiplier");
            p.setFallDistance(0.0f);
            p.setVelocity(v.multiply(multiplier));
            Location l = p.getLocation();
            
            Sound sound = XSound.ENTITY_FIREWORK_ROCKET_LAUNCH.parseSound();
            l.getWorld().playSound(l, sound, 20, 1);
            ParticleEffect.VILLAGER_HAPPY.display(l, 1.0f, 1.0f, 1.0f, 1, 100, null, pl -> pl.getWorld().equals(l.getWorld()));
        }
    }
    
    @Override
    public ItemStack buildItem(){
        return ItemBuilder.newItem(XMaterial.RABBIT_STEW)
                .withDisplayName(getDisplayName())
                .build();
    }
    
    @Override
    public SpecialItemName getSpecialItemName(){
        return SpecialItemName.enderman_soup;
    }
}
