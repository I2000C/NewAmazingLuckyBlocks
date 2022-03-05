package me.i2000c.newalb.listeners.wands;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.listeners.objects.MaterialChecker;
import java.util.HashSet;
import java.util.Set;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.LangLoader;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.SpecialItem;
import me.i2000c.newalb.utils2.OtherUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.i2000c.newalb.utils.WorldList;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;

public class ShieldWand extends SpecialItem{
    private final HashMap<UUID, Long> shieldcooldown = new HashMap<>();
    private ItemStack shieldItemStack;
    
    @EventHandler
    public void playerInteraction(PlayerInteractEvent e){
        Player player = e.getPlayer();
        Action action = e.getAction();
        
        ItemStack wand = getItem();
        String shieldName = wand.getItemMeta().getDisplayName();

        if(NewAmazingLuckyBlocks.getMinecraftVersion() != MinecraftVersion.v1_8){
            if(e.getHand() == EquipmentSlot.OFF_HAND){
                return;
            }
        }
        
        ItemStack stack = player.getItemInHand();
        int dw = ConfigManager.getConfig().getInt("Wands.Shield.cooldown-time");
        if(((action == Action.RIGHT_CLICK_AIR) || (action == Action.RIGHT_CLICK_BLOCK)) && (stack.getType() == wand.getType())){
            if(!WorldList.isRegistered(player.getWorld().getName())){
                return;
            }
            if(!OtherUtils.checkPermission(player, "Wands.Shield")){
                return;
            }
            
            if(MaterialChecker.check(e)){
                return;
            }else{
                e.setCancelled(true);
            }
            
            if((stack.hasItemMeta()) && (stack.getItemMeta().getDisplayName().equals(shieldName))){
                if((this.shieldcooldown.containsKey(player.getUniqueId())) && ((this.shieldcooldown.get(player.getUniqueId())) > System.currentTimeMillis())){
                    long remainingTime = (this.shieldcooldown.get(player.getUniqueId())) - System.currentTimeMillis();
                    String cmsg = Logger.color(LangLoader.getMessages().getString("Cooldown-message").replace("%time%", String.valueOf(remainingTime / 1000L)));
                    player.sendMessage(cmsg);
                }else{
                    ItemMeta meta = stack.getItemMeta();
                    if(ConfigManager.getConfig().getBoolean("Wands.Shield.limited-uses.enable")){
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
                            int uses = ConfigManager.getConfig().getInt("Wands.Shield.limited-uses.uses");
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

                    this.shieldcooldown.put(player.getUniqueId(), System.currentTimeMillis() + dw * 1000);
                    
                    World w = player.getWorld();
                    boolean protect = ConfigManager.getConfig().getBoolean("Wands.Shield.protect-structures");
                    float radius = (float) ConfigManager.getConfig().getDouble("Wands.Shield.radius");
                    
                    for(Location l : this.generateSphere(player.getLocation().add(0, 1D, 0), radius, true)){
                        Block b = w.getBlockAt(l);
                        if(!protect || protect && b.getType() == Material.AIR){
                            BlockPlaceEvent e2 = new BlockPlaceEvent(b, b.getState(), b, stack, player, true);
                            Bukkit.getPluginManager().callEvent(e2);
                            if(!e2.isCancelled()){
                                b.setType(shieldItemStack.getType());
                                if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                                    b.setData((byte) shieldItemStack.getDurability());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
  
    public Set<Location> generateSphere(Location center, float radius, boolean hollow){
        //code by: https://www.youtube.com/watch?v=oKpgn38mj8Y

        Set<Location> circleBlocks = new HashSet<>();
        int bx = center.getBlockX();
        int by = center.getBlockY();
        int bz = center.getBlockZ();

        for(float x=bx-radius;x<=bx+radius;x++){
            for(float y=by-radius;y<=by+radius;y++){
                for(float z=bz-radius;z<=bz+radius;z++){                    
                    double distance = ((bx - x) * (bx - x)) + ((by - y) * (by - y)) + ((bz - z) * (bz - z));
                    
                    if(distance < radius * radius && !(hollow && distance < ((radius - 1) * (radius - 1)))){
                        Location l = new Location(center.getWorld(), x, y, z);
                        circleBlocks.add(l);
                    }
                }
            }        
        }
      
        boolean withfloor = ConfigManager.getConfig().getBoolean("Wands.Shield.withfloor");
        if(!withfloor){
            Set<Location> circleBlocks2 = new HashSet<>();
            circleBlocks2.addAll(circleBlocks);
            for(Location loc : circleBlocks2){
                if(loc.getBlockY() == by-radius+1){                      
                    circleBlocks.remove(loc);
                }
            }         
        }
        return circleBlocks;     
    }
    
    @Override
    public ItemStack buildItem(){
        ItemStack stack = XMaterial.MUSIC_DISC_STRAD.parseItem();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Logger.color(LangLoader.getMessages().getString("Wands.Shield.name")));
        if(ConfigManager.getConfig().getBoolean("Wands.Shield.limited-uses.enable")){
            int uses = ConfigManager.getConfig().getInt("Wands.Shield.limited-uses.uses");
            List<String> loreList = new ArrayList();
            loreList.add("Uses left:");
            loreList.add(String.valueOf(uses));
            meta.setLore(loreList);
        }
        stack.setItemMeta(meta);
        
        try{
            shieldItemStack = OtherUtils.parseMaterial(ConfigManager.getConfig().getString("Wands.Shield.ShieldWandBlock"));
        }catch(Exception ex){
            Logger.log(Logger.color("&cInvalid block in config at Wands.Shield.ShieldWandBlock"));
        }
        
        return stack;
    }
}