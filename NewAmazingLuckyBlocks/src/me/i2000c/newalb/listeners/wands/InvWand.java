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
import me.i2000c.newalb.utils.SpecialItem;
import me.i2000c.newalb.utils2.OtherUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.i2000c.newalb.utils.WorldList;
import org.bukkit.inventory.EquipmentSlot;

public class InvWand extends SpecialItem{
    private final HashMap<UUID, Long> invcooldown = new HashMap();
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        Player player = e.getPlayer();
        Action action = e.getAction();
        
        ItemStack wand = getItem();
        String invName = wand.getItemMeta().getDisplayName();
    
        if(NewAmazingLuckyBlocks.getMinecraftVersion() != MinecraftVersion.v1_8){
            if(e.getHand() == EquipmentSlot.OFF_HAND){
                return;
            }
        }
        
        ItemStack stack = player.getItemInHand();
        int iw = ConfigManager.getConfig().getInt("Wands.Invisibility.cooldown-time");
        int effectTime = ConfigManager.getConfig().getInt("Wands.Invisibility.effect-time");
        effectTime *= 20; //effectTime = effectTime * 20
        if(((action == Action.RIGHT_CLICK_AIR) || (action == Action.RIGHT_CLICK_BLOCK)) && (player.getItemInHand().getType() == wand.getType())){
            if(!WorldList.isRegistered(player.getWorld().getName())){
                return;
            }
            if(!OtherUtils.checkPermission(player, "Wands.Invisibility")){
                return;
            }
            
            if(MaterialChecker.check(e)){
                return;
            }else{
                e.setCancelled(true);
            }
            
            if((stack.hasItemMeta()) && (stack.getItemMeta().getDisplayName().equals(invName))){
                if((this.invcooldown.containsKey(player.getUniqueId())) && ((this.invcooldown.get(player.getUniqueId())) > System.currentTimeMillis())){
                    long remainingTime = (this.invcooldown.get(player.getUniqueId())) - System.currentTimeMillis();
                    String cmsg = Logger.color(LangLoader.getMessages().getString("Cooldown-message").replace("%time%", String.valueOf(remainingTime / 1000L)));
                    player.sendMessage(cmsg);
                }else{
                    ItemMeta meta = stack.getItemMeta();
                    if(ConfigManager.getConfig().getBoolean("Wands.Invisibility.limited-uses.enable")){
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
                            int uses = ConfigManager.getConfig().getInt("Wands.Invisibility.limited-uses.uses");
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
          
                    this.invcooldown.put(player.getUniqueId(), System.currentTimeMillis() + iw * 1000);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, effectTime, 0));
                }
            }
        }
    }
    
    @Override
    public ItemStack buildItem(){
        ItemStack stack = XMaterial.MUSIC_DISC_MELLOHI.parseItem();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Logger.color(LangLoader.getMessages().getString("Wands.Invisibility.name")));
        if(ConfigManager.getConfig().getBoolean("Wands.Invisibility.limited-uses.enable")){
            int uses = ConfigManager.getConfig().getInt("Wands.Invisibility.limited-uses.uses");
            List<String> loreList = new ArrayList();
            loreList.add("Uses left:");
            loreList.add(String.valueOf(uses));
            meta.setLore(loreList);
        }
        stack.setItemMeta(meta);
        
        return stack;
    }
}
