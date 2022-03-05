package me.i2000c.newalb.listeners.wands;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.listeners.objects.MaterialChecker;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.LangLoader;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.OtherUtils;
import me.i2000c.newalb.utils.Timer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.i2000c.newalb.utils.WorldList;
import org.bukkit.Sound;
import org.bukkit.inventory.EquipmentSlot;

public class FrostPathWand implements Listener{
    private final HashMap<UUID, Long> frostcooldown = new HashMap();
    
    @EventHandler
    public void playerInteraction(PlayerInteractEvent e){
        Player player = e.getPlayer();
        Action action = e.getAction();
        String frostName = wand.getItemMeta().getDisplayName();
    
        if(NewAmazingLuckyBlocks.getMinecraftVersion() != MinecraftVersion.v1_8){
            if(e.getHand() == EquipmentSlot.OFF_HAND){
                return;
            }
        }
        
        ItemStack stack = player.getItemInHand();
        int dw = ConfigManager.getConfig().getInt("Wands.Frost.cooldown-time");
        if(((action == Action.RIGHT_CLICK_AIR) || (action == Action.RIGHT_CLICK_BLOCK)) && (stack.getType() == wand.getType())){
            if(!WorldList.isRegistered(player.getWorld().getName())){
                return;
            }
            if(!OtherUtils.checkPermission(player, "Wands.Frost")){
                return;
            }
            
            if(MaterialChecker.check(e)){
                return;
            }else{
                e.setCancelled(true);
            }
            
            if((stack.hasItemMeta()) && (stack.getItemMeta().getDisplayName().equals(frostName))){
                if((this.frostcooldown.containsKey(player.getUniqueId())) && ((this.frostcooldown.get(player.getUniqueId())) > System.currentTimeMillis())){
                    long remainingTime = (this.frostcooldown.get(player.getUniqueId())) - System.currentTimeMillis();
                    String cmsg = Logger.color(LangLoader.getMessages().getString("Cooldown-message").replace("%time%", String.valueOf(remainingTime / 1000L)));
                    player.sendMessage(cmsg);
                }else{
                    ItemMeta meta = stack.getItemMeta();
                    if(ConfigManager.getConfig().getBoolean("Wands.Frost.limited-uses.enable")){
                        if(meta.hasLore()){
                            List<String> loreList = meta.getLore();
                            int uses = Integer.parseInt(loreList.get(1));
                            if(uses == 0){
                              player.sendMessage(Logger.color("&cThis wand has expired"));
                              return;
                            }else{
                              uses--;
                            }
                            loreList.set(1, String.valueOf(uses));
                            meta.setLore(loreList);
                            stack.setItemMeta(meta);
                        }else{
                            List<String> loreList = new ArrayList();
                            int uses = ConfigManager.getConfig().getInt("Wands.Frost.limited-uses.uses");
                            loreList.add("Uses left:");
                            loreList.add(String.valueOf(uses));
                            meta.setLore(loreList);
                            stack.setItemMeta(meta);
                        } 
                    }else{
                        if(meta.hasLore()){
                            List<String> loreList = meta.getLore();
                            loreList.clear();
                            meta.setLore(loreList);
                            stack.setItemMeta(meta);
                        }
                    }
            
            

                    this.frostcooldown.put(player.getUniqueId(), System.currentTimeMillis() + dw * 1000);
            
                    try{
                        player.playSound(player.getLocation(), Sound.valueOf("GLASS"), 2.0F, 1.0F);
                    }catch(Exception ex){
                        player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 2.0F, 1.0F);
                    }
                    Location l = player.getLocation();
                    Location loc = player.getTargetBlock(null, 120).getLocation();
                    int distance = (int) Math.sqrt(loc.distanceSquared(l));
                    
                    float minPitch = (float) ConfigManager.getConfig().getDouble("Wands.Frost.minPitch");
                    float maxPitch = (float) ConfigManager.getConfig().getDouble("Wands.Frost.maxPitch");
                    
                    if(l.getPitch() > maxPitch || l.getPitch() < minPitch){
                        return;
                    }
                    Timer.getTimer().executeFrostPathWand(player, distance, loc);
                }
            }
        }
    }
    
    private static ItemStack wand;
    
    public static ItemStack getWand(){
        return wand.clone();
    }
  
    public static void loadWand(){
        ItemStack stack = XMaterial.MUSIC_DISC_WAIT.parseItem();        
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Logger.color(LangLoader.getMessages().getString("Wands.Frost.name")));
        if(ConfigManager.getConfig().getBoolean("Wands.Frost.limited-uses.enable")){
            int uses = ConfigManager.getConfig().getInt("Wands.Frost.limited-uses.uses");
            List<String> loreList = new ArrayList();
            loreList.add("Uses left:");
            loreList.add(String.valueOf(uses));
            meta.setLore(loreList);
        }
        stack.setItemMeta(meta);
        
        wand = stack;
    }
}
