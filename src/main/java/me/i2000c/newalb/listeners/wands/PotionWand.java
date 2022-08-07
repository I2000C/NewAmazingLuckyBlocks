package me.i2000c.newalb.listeners.wands;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Random;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.listeners.objects.MaterialChecker;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.lang_utils.LangLoader;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.Logger.LogLevel;
import me.i2000c.newalb.utils.SpecialItem;
import me.i2000c.newalb.utils2.OtherUtils;
import me.i2000c.newalb.utils.WorldList;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.bukkit.Location;

import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionWand extends SpecialItem{   
    private final HashMap<UUID, Long> potioncooldown = new HashMap();
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        Player player = e.getPlayer();
        Action action = e.getAction();
        
        ItemStack wand = getItem();
        String potionName = wand.getItemMeta().getDisplayName();  

        if(NewAmazingLuckyBlocks.getMinecraftVersion() != MinecraftVersion.v1_8){
            if(e.getHand() == EquipmentSlot.OFF_HAND){
                return;
            }
        }
        
        ItemStack stack = player.getItemInHand();
        int tw = ConfigManager.getConfig().getInt("Wands.Potion.cooldown-time");
        if(((action == Action.RIGHT_CLICK_AIR) || (action == Action.RIGHT_CLICK_BLOCK)) && (player.getItemInHand().getType() == wand.getType())){
            if(!WorldList.isRegistered(player.getWorld().getName())){
                return;
            }
            if(!OtherUtils.checkPermission(player, "Wands.Potion")){
                return;
            }
            
            if(MaterialChecker.check(e)){
                return;
            }else{
                e.setCancelled(true);
            }
            
            if((stack.hasItemMeta()) && (stack.getItemMeta().getDisplayName().equals(potionName))){
                if((this.potioncooldown.containsKey(player.getUniqueId())) && ((this.potioncooldown.get(player.getUniqueId())) > System.currentTimeMillis())){
                    long remainingTime = (this.potioncooldown.get(player.getUniqueId())) - System.currentTimeMillis();
                    String cmsg = LangLoader.getMessages().getString("Cooldown-message").replace("%time%", String.valueOf(remainingTime / 1000L));
                    player.sendMessage(cmsg);
                }else{
                    ItemMeta meta = stack.getItemMeta();
                    if(ConfigManager.getConfig().getBoolean("Wands.Potion.limited-uses.enable")){
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
                            int uses = ConfigManager.getConfig().getInt("Wands.Potion.limited-uses.uses");
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

                    this.potioncooldown.put(player.getUniqueId(), System.currentTimeMillis() + tw * 1000);
                    Vector vector = player.getLocation().getDirection().multiply(0.5);
                    Location location = player.getEyeLocation();
                    ItemStack iStack;
                    if(NewAmazingLuckyBlocks.getMinecraftVersion() == MinecraftVersion.v1_8){
                        iStack = new ItemStack(Material.POTION);
                    }else{
                        iStack = new ItemStack(Material.SPLASH_POTION);
                    }


                    PotionMeta pm = (PotionMeta) iStack.getItemMeta();
                    this.potionMeta(pm);
                    iStack.setItemMeta(pm);
                    
                    ThrownPotion thrownPotion = player.launchProjectile(ThrownPotion.class);
                    thrownPotion.setItem(iStack);
                    thrownPotion.setVelocity(vector);
                }
            }
        }
    }
  
    private static PotionMeta potionMeta(PotionMeta pm){
        List<String> effects = ConfigManager.getConfig().getStringList("Wands.Potion.effects");
        int weightSum = 0;
        int r;
        for(int i=0;i<effects.size();i++){
            String effect[] = effects.get(i).split(";");
            int prob = Integer.parseInt(effect[3]);
            weightSum += prob;
        }
        weightSum--;

        if(weightSum == 0){
            r = 0;
        }else if(weightSum < 0){
            Logger.log("&cTotal probability must be &a>0 in potionWandEffects", LogLevel.WARN);
            return null;
        }else{
            r = new Random().nextInt(weightSum);
        }

        int selection = r;
        for(int i=0;i<=effects.size();i++){
            String effect[] = effects.get(i).split(";");
            int prob = Integer.parseInt(effect[3]);

            selection -= prob;
            if(selection < 0){

            String effectName = effect[0];
            int time = Integer.parseInt(effect[1])*20;
            int amplifier = Integer.parseInt(effect[2]);

            pm.addCustomEffect(new PotionEffect(PotionEffectType.getByName(effectName), time, amplifier), true);
                break;
            }
        }
        return pm;
    }
    
    @Override
    public ItemStack buildItem(){
        ItemStack stack = XMaterial.MUSIC_DISC_11.parseItem();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(LangLoader.getMessages().getString("Wands.Potion.name"));
        if(ConfigManager.getConfig().getBoolean("Wands.Potion.limited-uses.enable")){
            int uses = ConfigManager.getConfig().getInt("Wands.Potion.limited-uses.uses");
            List<String> loreList = new ArrayList();
            loreList.add("Uses left:");
            loreList.add(String.valueOf(uses));
            meta.setLore(loreList);
        }
        stack.setItemMeta(meta);
        
        return stack;
    }
}
