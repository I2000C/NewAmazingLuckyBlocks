package me.i2000c.newalb.listeners.objects;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.messages.ActionBar;

import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class PlayerTracker extends SpecialItem{
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        e.setCancelled(true);
        Player player = e.getPlayer();
        
        switch(e.getAction()){
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                player.setCompassTarget(player.getWorld().getSpawnLocation());
                break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                super.getPlayerCooldown().updateCooldown(player);
                
                int radius = ConfigManager.getMainConfig().getInt("Objects.PlayerTracker.radius");
                double minDistanceS = radius*radius;
                Entity nearestEntity = null;

                if(ConfigManager.getMainConfig().getBoolean("Objects.PlayerTracker.detect-players-only")){
                    for(Player p : player.getWorld().getPlayers()){
                        if(p.equals(player) || p.getGameMode() == GameMode.SPECTATOR){
                            continue;
                        }
                        
                        double distanceSquared = p.getLocation().distanceSquared(player.getLocation());
                        if(distanceSquared <= minDistanceS){
                            minDistanceS = distanceSquared;
                            nearestEntity = (Entity) p;
                        }
                    }
                }else{
                    for(Entity ent : player.getNearbyEntities(radius, radius, radius)){
                        if(ent.getWorld().equals(player.getWorld())){
                            double distanceSquared = ent.getLocation().distanceSquared(player.getLocation());
                            if(distanceSquared <= minDistanceS){
                                minDistanceS = distanceSquared;
                                nearestEntity = ent;
                            }
                        }                            
                    }
                }

                String message;
                if(nearestEntity == null){
                    message = ConfigManager.getLangMessage("Objects.PlayerTracker.message3");
                    ActionBar.sendActionBar(player, Logger.color(message));
                }else{
                    player.setCompassTarget(nearestEntity.getLocation());

                    double distance = Math.sqrt(minDistanceS);

                    if(nearestEntity instanceof Player){
                        message = ConfigManager.getLangMessage("Objects.PlayerTracker.message1")
                                .replace("%player%", ((Player)nearestEntity).getName())
                                .replace("%distance%", String.format("%.2f", distance));
                        ActionBar.sendActionBar(player, Logger.color(message));
                    }else{
                        message = ConfigManager.getLangMessage("Objects.PlayerTracker.message2")
                                .replace("%entity%", nearestEntity.getName())
                                .replace("%distance%", String.format("%.2f", distance));
                        ActionBar.sendActionBar(player, Logger.color(message));
                    }
                }
                break;
        }
    }
    
    @Override
    public ItemStack buildItem(){
        return ItemStackWrapper.newItem(XMaterial.COMPASS)
                .toItemStack();
    }
}
