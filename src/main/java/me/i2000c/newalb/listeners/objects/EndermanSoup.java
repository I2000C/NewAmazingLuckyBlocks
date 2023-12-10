package me.i2000c.newalb.listeners.objects;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.particles.Particles;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class EndermanSoup extends SpecialItem{
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
            e.setCancelled(true);
            super.updatePlayerCooldown(e.getPlayer());
            super.decreaseAmountOfItem(e);
            
            Player p = e.getPlayer();
            Vector v = p.getEyeLocation().getDirection();
            double multiplier = ConfigManager.getConfig().getDouble("Objects.EndermanSoup.speedMultiplier");
            p.setFallDistance(0.0f);
            p.setVelocity(v.multiply(multiplier));
            Location l = p.getLocation();
            
            Sound sound = XSound.ENTITY_FIREWORK_ROCKET_LAUNCH.parseSound();
            l.getWorld().playSound(l, sound, 20, 1);
            Particles.VILLAGER_HAPPY.create().setOffset(1, 1, 1).setAmount(100).display(l);
        }
    }
    
    @Override
    public ItemStack buildItem(){
        return ItemBuilder.newItem(XMaterial.RABBIT_STEW)
                .build();
    }
}
