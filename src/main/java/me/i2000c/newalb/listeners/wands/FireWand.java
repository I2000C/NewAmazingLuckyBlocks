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
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils2.OtherUtils;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import me.i2000c.newalb.utils.WorldList;
import org.bukkit.inventory.EquipmentSlot;

public class FireWand extends SpecialItem{
    private final HashMap<UUID, Long> firecooldown = new HashMap();
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        Player player = e.getPlayer();
        Action action = e.getAction();
        
        ItemStack wand = getItem();
        String fireName = wand.getItemMeta().getDisplayName();
    
        if(NewAmazingLuckyBlocks.getMinecraftVersion() != MinecraftVersion.v1_8){
            if(e.getHand() == EquipmentSlot.OFF_HAND){
                return;
            }
        }
        
        ItemStack stack = player.getItemInHand();
        int dw = ConfigManager.getConfig().getInt("Wands.Fire.cooldown-time");
        if((action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) && (player.getItemInHand().getType() == wand.getType())){
            if(!WorldList.isRegistered(player.getWorld().getName())){
                return;
            }
            if(!OtherUtils.checkPermission(player, "Wands.Fire")){
                return;
            }
            
            if(MaterialChecker.check(e)){
                return;
            }else{
                e.setCancelled(true);
            }
            
            if((stack.hasItemMeta()) && (stack.getItemMeta().getDisplayName().equals(fireName))){
                if((this.firecooldown.containsKey(player.getUniqueId())) && ((this.firecooldown.get(player.getUniqueId())) > System.currentTimeMillis())){
                    long remainingTime = (this.firecooldown.get(player.getUniqueId())) - System.currentTimeMillis();
                    String cmsg = LangLoader.getMessages().getString("Cooldown-message").replace("%time%", String.valueOf(remainingTime / 1000L));
                    player.sendMessage(cmsg);
                }else{
                    ItemMeta meta = stack.getItemMeta();
                    if(ConfigManager.getConfig().getBoolean("Wands.Fire.limited-uses.enable")){
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
                            int uses = ConfigManager.getConfig().getInt("Wands.Fire.limited-uses.uses");
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



                    this.firecooldown.put(player.getUniqueId(), System.currentTimeMillis() + dw * 1000);
                    Vector vector = player.getLocation().getDirection();
                    Vector vector2 = vector.multiply(3.0D);
                    float radius = (float) ConfigManager.getConfig().getDouble("Wands.Fire.fire-radius");
                    for(float x=0-radius;x<=0+radius;x++){
                        for(float z=0-radius;z<=0+radius;z++){
                            FallingBlock fallingBlock = e.getPlayer().getWorld().spawnFallingBlock(e.getPlayer().getLocation().add(x, 2.0D, z), Material.FIRE, (byte)0);
                            fallingBlock.setVelocity(vector2);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public ItemStack buildItem(){
        ItemStack stack = XMaterial.MUSIC_DISC_MALL.parseItem();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(LangLoader.getMessages().getString("Wands.Fire.name"));
        if(ConfigManager.getConfig().getBoolean("Wands.Fire.limited-uses.enable")){
            int uses = ConfigManager.getConfig().getInt("Wands.Fire.limited-uses.uses");
            List<String> loreList = new ArrayList<>();
            loreList.add("Uses left:");
            loreList.add(String.valueOf(uses));
            meta.setLore(loreList);
        }
        stack.setItemMeta(meta);
        
        return stack;
    }
}
