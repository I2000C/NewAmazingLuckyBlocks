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
import me.i2000c.newalb.lang_utils.LangLoader;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils.SpecialItem;
import me.i2000c.newalb.utils2.OtherUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.i2000c.newalb.utils.WorldList;
import org.bukkit.inventory.EquipmentSlot;

public class SlimeWand extends SpecialItem{
    private final HashMap<UUID, Long> slimecooldown = new HashMap();
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        Player player = e.getPlayer();
        Action action = e.getAction();
        
        ItemStack wand = getItem();
        String slimeName = wand.getItemMeta().getDisplayName();
        
        if(NewAmazingLuckyBlocks.getMinecraftVersion() != MinecraftVersion.v1_8){
            if(e.getHand() == EquipmentSlot.OFF_HAND){
                return;
            }
        }
        
        ItemStack stack = player.getItemInHand();
        int sw = ConfigManager.getConfig().getInt("Wands.Slime.cooldown-time");
        if(((action == Action.RIGHT_CLICK_AIR) || (action == Action.RIGHT_CLICK_BLOCK)) && (player.getItemInHand().getType() == wand.getType())){
            if(!WorldList.isRegistered(player.getWorld().getName())){
                return;
            }
            if(!OtherUtils.checkPermission(player, "Wands.Slime")){
                return;
            }
            
            if(MaterialChecker.check(e)){
                return;
            }else{
                e.setCancelled(true);
            }
            
            if((stack.hasItemMeta()) && (stack.getItemMeta().getDisplayName().equals(slimeName))){
                if((this.slimecooldown.containsKey(player.getUniqueId())) && ((this.slimecooldown.get(player.getUniqueId())) > System.currentTimeMillis())){
                    long remainingTime = (this.slimecooldown.get(player.getUniqueId())) - System.currentTimeMillis();
                        String cmsg = LangLoader.getMessages().getString("Cooldown-message").replace("%time%", String.valueOf(remainingTime / 1000L));
                        player.sendMessage(cmsg);
                }else{
                    ItemMeta meta = stack.getItemMeta();
                    if(ConfigManager.getConfig().getBoolean("Wands.Slime.limited-uses.enable")){
                        if(meta.hasLore()){
                            List<String> loreList = meta.getLore();
                            int uses = Integer.parseInt(loreList.get(1));
                            if(uses == 0){
                              player.sendMessage("&cThis wand has expired");
                              return;
                            }else{
                              uses--;
                            }
                            loreList.set(1, String.valueOf(uses));
                            meta.setLore(loreList);
                            stack.setItemMeta(meta);
                        }else{
                            List<String> loreList = new ArrayList();
                            int uses = ConfigManager.getConfig().getInt("Wands.Slime.limited-uses.uses");
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

                    this.slimecooldown.put(player.getUniqueId(), System.currentTimeMillis() + sw * 1000);
                    Slime slime = (Slime)e.getPlayer().getWorld().spawnEntity(player.getLocation().add(0.0D, 3.0D, 0.0D), EntityType.SLIME);
                    slime.setVelocity(player.getLocation().getDirection().multiply(1.8D));
                }
            }
        }
    }
    
    @Override
    public ItemStack buildItem(){
        ItemStack stack = XMaterial.MUSIC_DISC_CHIRP.parseItem();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(LangLoader.getMessages().getString("Wands.Slime.name"));
        if(ConfigManager.getConfig().getBoolean("Wands.Slime.limited-uses.enable")){
            int uses = ConfigManager.getConfig().getInt("Wands.Slime.limited-uses.uses");
            List<String> loreList = new ArrayList();
            loreList.add("Uses left:");
            loreList.add(String.valueOf(uses));
            meta.setLore(loreList);
        }
        stack.setItemMeta(meta);
        
        return stack;
    }
}
