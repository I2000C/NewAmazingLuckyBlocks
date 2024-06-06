package me.i2000c.newalb.listeners.objects;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;

import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils.particles.Particles;
import me.i2000c.newalb.utils2.ItemStackWrapper;

public class EndermanSoup extends SpecialItem{
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
            e.setCancelled(true);
            super.getPlayerCooldown().updateCooldown(e.getPlayer());
            super.decreaseAmountOfItem(e);
            
            Player p = e.getPlayer();
            Vector v = p.getEyeLocation().getDirection();
            double multiplier = ConfigManager.getMainConfig().getDouble("Objects.EndermanSoup.speedMultiplier");
            p.setFallDistance(0.0f);
            p.setVelocity(v.multiply(multiplier));
            Location l = p.getLocation();
            
            XSound.ENTITY_FIREWORK_ROCKET_LAUNCH.play(l, 20, 1);
            Particles.VILLAGER_HAPPY.create().setCount(100).setPosition(l).display();
        }
    }
    
    @Override
    public ItemStack buildItem(){
        return ItemStackWrapper.newItem(XMaterial.RABBIT_STEW)
                               .toItemStack();
    }
}
