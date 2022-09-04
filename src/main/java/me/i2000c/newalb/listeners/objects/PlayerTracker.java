package me.i2000c.newalb.listeners.objects;

import com.cryptomorin.xseries.XMaterial;
import java.math.BigDecimal;
import java.math.RoundingMode;
import me.i2000c.newalb.utils.LangConfig;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils2.ActionBarUtils;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

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
                super.updatePlayerCooldown(player);
                
                int radius = ConfigManager.getConfig().getInt("Objects.PlayerTracker.radius");
                double minDistanceS = radius*radius;
                Entity nearestEntity = null;

                if(ConfigManager.getConfig().getBoolean("Objects.PlayerTracker.detect-players-only")){
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
                    message = LangConfig.getMessage("Objects.PlayerTracker.message3");
                    ActionBarUtils.sendMessage(message, player);
                }else{
                    player.setCompassTarget(nearestEntity.getLocation());

                    double distance = Math.sqrt(minDistanceS);
                    BigDecimal d = new BigDecimal(distance).setScale(2, RoundingMode.HALF_EVEN);
                    distance = d.doubleValue();

                    if(nearestEntity instanceof Player){
                        message = LangConfig.getMessage("Objects.PlayerTracker.message1");
                        ActionBarUtils.sendMessage(message
                                .replace("%player%", ((Player)nearestEntity).getName())
                                .replace("%distance%", String.valueOf(distance)), player);
                    }else{
                        message = LangConfig.getMessage("Objects.PlayerTracker.message2");
                        ActionBarUtils.sendMessage(message
                                .replace("%entity%", nearestEntity.getName())
                                .replace("%distance%", String.valueOf(distance)), player);
                    }
                }
                break;
        }
    }
    
    @Override
    public ItemStack buildItem(){
        return ItemBuilder.newItem(XMaterial.COMPASS)
                .withDisplayName(getDisplayName())
                .build();
    }
}
