package me.i2000c.newalb.listeners.wands;

import com.cryptomorin.xseries.XMaterial;
import java.util.HashSet;
import java.util.Set;
import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.integration.WorldGuardManager;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ShieldWand extends SpecialItem {
    private ItemStackWrapper shieldItemStackWrapper;
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            e.setCancelled(true);
            
            Player player = e.getPlayer();
            if(!super.decreaseWandUses(e.getItem(), e.getPlayer())) {
                e.setCancelled(true);
                return;
            }
            
            super.getPlayerCooldown().updateCooldown(player);
            World w = player.getWorld();
            boolean protect = ConfigManager.getMainConfig().getBoolean("Wands.ShieldWand.protect-structures");
            double radius = ConfigManager.getMainConfig().getDouble("Wands.ShieldWand.radius");

            for(Location l : this.generateHollowSphere(player.getLocation().add(0, 1D, 0), radius)) {
                Block b = w.getBlockAt(l);
                if(!protect || protect && b.getType() == Material.AIR){
                    if(WorldGuardManager.canBuild(player, b.getLocation())) {
                        shieldItemStackWrapper.placeAt(b);
                    }
                }
            }
        }
    }
  
    public Set<Location> generateHollowSphere(Location center, double radius) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        //code by: https://www.youtube.com/watch?v=oKpgn38mj8Y
        Set<Location> locations = new HashSet<>();
        double radiusSquared = radius * radius;
        double radiusSquared2 = (radius-1) * (radius-1);
        
        for(double y=-radius; y<=radius; y++) {
            for(double x=-radius; x<=radius; x++) {
                for(double z=-radius; z<=radius; z++) {
                    Location loc = center.clone().add(x, y, z);
                    double distanceSquared = center.distanceSquared(loc);
                    
                    if(distanceSquared < radiusSquared && distanceSquared >= radiusSquared2) {
                        locations.add(loc);
                    }
                }
            }
        }

        boolean withfloor = ConfigManager.getMainConfig().getBoolean("Wands.ShieldWand.withfloor");
        if(!withfloor && !locations.isEmpty()){
            int minY = locations.stream().min((loc1, loc2) -> loc1.getBlockY() - loc2.getBlockY()).get().getBlockY();
            locations.removeIf(loc -> loc.getBlockY() == minY);
        }
        
        return locations;
        //</editor-fold>
    }
    
    @Override
    public ItemStack buildItem() {
        try {
            XMaterial material = ConfigManager.getMainConfig().getMaterial("Wands.ShieldWand.ShieldWandBlock");
            shieldItemStackWrapper = ItemStackWrapper.newItem(material);
        } catch(Exception ex) {
            Logger.log("&cInvalid block in config at Wands.ShieldWand.ShieldWandBlock");
        }
        
        return ItemStackWrapper.newItem(XMaterial.MUSIC_DISC_STRAD)
                               .setLore(super.getLoreOfWand())
                               .toItemStack();
    }
}