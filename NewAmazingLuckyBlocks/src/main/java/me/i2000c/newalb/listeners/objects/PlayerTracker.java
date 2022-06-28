package me.i2000c.newalb.listeners.objects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.UUID;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.LangLoader;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.SpecialItem;
import me.i2000c.newalb.utils2.OtherUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.i2000c.newalb.utils.WorldList;
import me.i2000c.newalb.utils2.ActionBarUtils;
import org.bukkit.inventory.EquipmentSlot;


import org.bukkit.GameMode;

public class PlayerTracker extends SpecialItem{
    private final HashMap<UUID, Long> trackercooldown = new HashMap();
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        Player player = e.getPlayer();
        Action action = e.getAction();
        
        ItemStack object = getItem();
        String trackerName = object.getItemMeta().getDisplayName();
    
        if(NewAmazingLuckyBlocks.getMinecraftVersion() != MinecraftVersion.v1_8){
            if(e.getHand() == EquipmentSlot.OFF_HAND){
                return;
            }
        }
        
        ItemStack stack = player.getItemInHand();
        int lw = ConfigManager.getConfig().getInt("Objects.PlayerTracker.cooldown-time");
        if(stack.getType() == Material.COMPASS){
            if(!WorldList.isRegistered(player.getWorld().getName())){
                return;
            }
            if(!OtherUtils.checkPermission(player, "Objects.PlayerTracker")){
                return;
            }
            
            if(MaterialChecker.check(e)){
                return;
            }else{
                e.setCancelled(true);
            }
            
            if((stack.hasItemMeta()) && (stack.getItemMeta().getDisplayName().equals(trackerName))){
                switch(e.getAction()){
                    case LEFT_CLICK_AIR:
                    case LEFT_CLICK_BLOCK:
                        player.setCompassTarget(player.getWorld().getSpawnLocation());
                        return;
                    case RIGHT_CLICK_AIR:
                    case RIGHT_CLICK_BLOCK:
                        break;
                    default:
                        return;
                }
          
                if((this.trackercooldown.containsKey(player.getUniqueId())) && ((this.trackercooldown.get(player.getUniqueId())) > System.currentTimeMillis())){
                    e.setCancelled(true);
                    long remainingTime = (this.trackercooldown.get(player.getUniqueId())) - System.currentTimeMillis();
                    String cmsg = Logger.color(LangLoader.getMessages().getString("Cooldown-message").replace("%time%", String.valueOf(remainingTime / 1000L)));
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
        }
    }
    
    @Override
    public ItemStack buildItem(){
        ItemStack stack = new ItemStack(Material.COMPASS);
        
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Logger.color(LangLoader.getMessages().getString("Objects.PlayerTracker.name")));
        stack.setItemMeta(meta);
        
        return stack;
    }
}
