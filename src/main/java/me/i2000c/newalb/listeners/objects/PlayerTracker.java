package me.i2000c.newalb.listeners.objects;

import com.cryptomorin.xseries.XMaterial;
import java.math.BigDecimal;
import java.math.RoundingMode;
import me.i2000c.newalb.lang_utils.LangLoader;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.listeners.interact.SpecialItemName;
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
                if(!super.isCooldownExpired(player)){
                    
                }else{
                    
                }
                break;
            default:
                return;
        }

        if((this.trackercooldown.containsKey(player.getUniqueId())) && ((this.trackercooldown.get(player.getUniqueId())) > System.currentTimeMillis())){
            e.setCancelled(true);
            long remainingTime = (this.trackercooldown.get(player.getUniqueId())) - System.currentTimeMillis();
            String cmsg = LangLoader.getMessages().getString("Cooldown-message").replace("%time%", String.valueOf(remainingTime / 1000L));
            player.sendMessage(cmsg);
        }else{
            this.trackercooldown.put(player.getUniqueId(), System.currentTimeMillis() + lw * 1000);

            int radius = ConfigManager.getConfig().getInt("Objects.PlayerTracker.radius");
            double distanceS = radius*radius;
            Entity nearestEntity = null;

            if(ConfigManager.getConfig().getBoolean("Objects.PlayerTracker.detect-players-only")){
                for(Player p : player.getWorld().getPlayers()){
                    if(p.equals(player) || p.getGameMode() == GameMode.SPECTATOR){
                        continue;
                    }

                    if(p.getLocation().distanceSquared(player.getLocation()) <= distanceS){
                        distanceS = p.getLocation().distanceSquared(player.getLocation());
                        nearestEntity = (Entity) p;
                    }
                }
            }else{
                for(Entity ent : player.getNearbyEntities(radius, radius, radius)){
                    if(ent.getWorld().equals(player.getWorld())){
                        if(ent.getLocation().distanceSquared(player.getLocation()) <= distanceS){
                            distanceS = ent.getLocation().distanceSquared(player.getLocation());
                            nearestEntity = ent;
                        }
                    }                            
                }
            }

            String message;
            if(nearestEntity == null){
                message = LangLoader.getMessages().getString("Objects.PlayerTracker.message3");
                ActionBarUtils.sendMessage(player, message);
            }else{
                player.setCompassTarget(nearestEntity.getLocation());

                double distance = Math.sqrt(distanceS);
                BigDecimal d = new BigDecimal(distance).setScale(2, RoundingMode.HALF_EVEN);
                distance = d.doubleValue();

                if(nearestEntity instanceof Player){
                    message = LangLoader.getMessages().getString("Objects.PlayerTracker.message1");
                    ActionBarUtils.sendMessage(player, message.replaceAll("%player%", ((Player)nearestEntity).getName()).replaceAll("%distance%", String.valueOf(distance)));
                }else{
                    message = LangLoader.getMessages().getString("Objects.PlayerTracker.message2");
                    ActionBarUtils.sendMessage(player, message.replaceAll("%entity%", nearestEntity.getName()).replaceAll("%distance%", String.valueOf(distance)));
                }
            }
        }
    }
    
    @Override
    public ItemStack buildItem(){
        return ItemBuilder.newItem(XMaterial.COMPASS)
                .withDisplayName(getDisplayName())
                .build();
    }

    @Override
    public SpecialItemName getSpecialItemName(){
        return SpecialItemName.player_tracker;
    }
}
