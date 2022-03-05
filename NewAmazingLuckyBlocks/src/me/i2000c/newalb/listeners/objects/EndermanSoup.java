package me.i2000c.newalb.listeners.objects;

import com.darkblade12.particleeffect.ParticleEffect;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.LangLoader;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.OtherUtils;
import me.i2000c.newalb.utils.WorldList;
import java.util.HashMap;
import java.util.UUID;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.utils.SpecialItem;
import net.ArtlieX.Utilities.XSound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class EndermanSoup extends SpecialItem{
    
    private final HashMap<UUID, Long> cooldown = new HashMap();
    
    @EventHandler
    public void playerInteraction(PlayerInteractEvent e){
        Player player = e.getPlayer();       
        Action action = e.getAction();
    
        if(NewAmazingLuckyBlocks.getMinecraftVersion() != MinecraftVersion.v1_8){
            if(e.getHand() == EquipmentSlot.OFF_HAND){
                return;
            }
        }
        
        ItemStack object = getItem();
        ItemStack stack = player.getItemInHand();
        if((action.equals(Action.RIGHT_CLICK_AIR))){
            if(!OtherUtils.checkItemStack(object, stack)){
                return;
            }
            
            if(!WorldList.isRegistered(player.getWorld().getName())){
                return;
            }
            if(!OtherUtils.checkPermission(player, "Objects.EndermanSoup")){
                return;
            }
            
            if(MaterialChecker.check(e)){
                return;
            }
            
            e.setCancelled(true);
            int dw = ConfigManager.getConfig().getInt("Objects.EndermanSoup.cooldown-time");
            if((this.cooldown.containsKey(player.getUniqueId())) && ((this.cooldown.get(player.getUniqueId())) > System.currentTimeMillis())){
                long remainingTime = (this.cooldown.get(player.getUniqueId())) - System.currentTimeMillis();
                String cmsg = Logger.color(LangLoader.getMessages().getString("Cooldown-message").replace("%time%", String.valueOf(remainingTime / 1000L)));
                player.sendMessage(cmsg);
                return;
            }else{
                this.cooldown.put(player.getUniqueId(), System.currentTimeMillis() + dw * 1000);
            }

            int amt = stack.getAmount() - 1;
            if(amt == 0){
                player.setItemInHand(null);
            }else{
                stack.setAmount(amt);
            }

            Player p = e.getPlayer();
            Vector v = p.getEyeLocation().getDirection();
            double multiplier = ConfigManager.getConfig().getDouble("Objects.EndermanSoup.speedMultiplier");
            p.setFallDistance(0.0f);
            p.setVelocity(v.multiply(multiplier));
            Location l = p.getLocation();
            
            Sound sound = XSound.FIREWORK_LAUNCH.playSound();
            l.getWorld().playSound(l, sound, 20, 1);
            if(NewAmazingLuckyBlocks.getMinecraftVersion() == MinecraftVersion.v1_8){                
                ParticleEffect.VILLAGER_HAPPY.display(1.0f, 1.0f, 1.0f, 1, 100, l, 50);
            }else if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                l.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, l, 5);
            }else{
                l.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, l, 100, 1.0, 1.0, 1.0);
            }
        }
    }
    
    @Override
    public ItemStack buildItem(){
        ItemStack stack = new ItemStack(Material.RABBIT_STEW);
        
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Logger.color(LangLoader.getMessages().getString("Objects.EndermanSoup.name")));
        stack.setItemMeta(meta);
        
        return stack;
    }
}
