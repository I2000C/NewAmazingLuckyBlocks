package me.i2000c.newalb.listeners.objects;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import java.awt.Color;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.Task;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.data.color.DustData;

public class EndermanSoup extends SpecialItem{
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.getAction() == Action.RIGHT_CLICK_AIR){            
            e.setCancelled(true);
            super.updatePlayerCooldown(e.getPlayer());
            super.decreaseAmountOfItem(e);
            
            Player p = e.getPlayer();
            Vector v = p.getEyeLocation().getDirection();
            double multiplier = ConfigManager.getConfig().getDouble("Objects.EndermanSoup.speedMultiplier");
            p.setFallDistance(0.0f);
            //p.setVelocity(v.multiply(multiplier));
            Location l = p.getLocation();
            
            Sound sound = XSound.ENTITY_FIREWORK_ROCKET_LAUNCH.parseSound();
            l.getWorld().playSound(l, sound, 20, 1);
            
            /*ParticleDisplay.simple(l, Particles.VILLAGER_HAPPY)
                    .offset(1, 1, 1)
                    .withExtra(1)
                    .withCount(100)
                    .spawn();*/
            
            //XParticle.atomic(NewAmazingLuckyBlocks.getInstance(), 5, 5, 60, ParticleDisplay.simple(l, Particles.VILLAGER_HAPPY));
            Task task = new Task() {
                int i = 0;
                @Override
                public void run() {
                    ItemStack item = new ItemStack(Material.DIAMOND_AXE);
                    new ParticleBuilder(ParticleEffect.REDSTONE, l)
                        .setParticleData(new DustData(Color.black, 4))
                        .display();
                    //XParticle.atom(5, 5, 30, ParticleDisplay.simple(l, Particles.VILLAGER_HAPPY), ParticleDisplay.simple(l, Particles.FLAME));
                    //ParticleDisplay pd = ParticleDisplay.simple(l, Particles.NOTE).directional().withColor(Color.blue, 2);
                    //XParticle.blackSun(3, 0.5, 50, 5, pd);
                    if(i++ > 20*5) {
                        cancel();
                    }
                }
            };
            task.runTask(0, 1);
            
            
        }
    }
    
    @Override
    public ItemStack buildItem(){
        return ItemBuilder.newItem(XMaterial.RABBIT_STEW)
                .withDisplayName(getDisplayName())
                .build();
    }
}
