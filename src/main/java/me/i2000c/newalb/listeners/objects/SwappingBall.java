package me.i2000c.newalb.listeners.objects;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import me.i2000c.newalb.utils2.MetadataManager;
import me.i2000c.newalb.utils2.Task;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SwappingBall extends SpecialItem {
    
    private boolean preserveYawPitch;
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        e.setCancelled(true);        
        super.decreaseAmountOfItem(e);
        
        Player player = e.getPlayer();
        Snowball snowball = player.launchProjectile(Snowball.class);
        if(MinecraftVersion.CURRENT_VERSION.is_1_8()) {
            // https://www.spigotmc.org/threads/snowball-throw-sound.394803/
            XSound.ENTITY_ARROW_SHOOT.play(player, 1f, 0.5f);
        } else {
            XSound.ENTITY_SNOWBALL_THROW.play(player);
        }
        MetadataManager.setClassMetadata(snowball, this);
    }
    
    @Override
    public void onEntityDamaged(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        Snowball snowball = (Snowball) e.getDamager();
        Player player = (Player) snowball.getShooter();
        
        if(player == null || player == entity) {
            return;
        }
        
        e.setCancelled(true);
        super.getPlayerCooldown().updateCooldown(player);
        
        Location loc1 = entity.getLocation();
        Location loc2 = player.getLocation();
        
        Task.runTask(() -> {
            // Play effects
            player.getWorld().playEffect(loc1, Effect.ENDER_SIGNAL, 100);
            player.getWorld().playEffect(loc2, Effect.ENDER_SIGNAL, 100);            
            
            // Play sounds
            XSound.ENTITY_ENDERMAN_TELEPORT.play(loc1);
            XSound.ENTITY_ENDERMAN_TELEPORT.play(loc2);
        }, 1L);
        
        // Teleport entities
        if(preserveYawPitch) {
            float yaw1 = loc1.getYaw();
            float pitch1 = loc1.getPitch();
            float yaw2 = loc2.getYaw();
            float pitch2 = loc2.getPitch();
            loc1.setYaw(yaw2);
            loc1.setPitch(pitch2);
            loc2.setYaw(yaw1);
            loc2.setPitch(pitch1);
        }        
        entity.teleport(loc2);
        player.teleport(loc1);
    }
    
    @Override
    public ItemStack buildItem() {
        preserveYawPitch = ConfigManager.getMainConfig().getBoolean(super.itemPathKey + ".preserve-yaw-pitch");
        return ItemStackWrapper.newItem(XMaterial.SNOWBALL)
                               .addEnchantment(XEnchantment.KNOCKBACK, 1)
                               .toItemStack();
    }
}
