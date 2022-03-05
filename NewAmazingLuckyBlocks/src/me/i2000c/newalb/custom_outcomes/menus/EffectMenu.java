package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.utils.rewards.EffectReward;
import me.i2000c.newalb.utils.Logger;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EffectMenu{
    public static EffectReward reward = null;
    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.EFFECT_MENU, EFFECT_MENU_FUNCTION);
            InventoryListener.registerInventory(CustomInventoryType.EFFECT_MENU_2, EFFECT_MENU_2_FUNCTION);
            
            inventoriesRegistered = true;
        }
        
        reward = null;
    }
    
    //Effects inventory
    public static void openEffectMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(reward == null){
            reward = new EffectReward(FinishMenu.getCurrentOutcome());
        }
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.EFFECT_MENU, 45, Logger.color("&5&lEffect Reward"));
        
        ItemStack glass = XMaterial.MAGENTA_STAINED_GLASS_PANE.parseItem();
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        
        ItemStack time = XMaterial.CLOCK.parseItem();
        meta = time.getItemMeta();
        if(reward.getDuration() < 0){
            meta.setDisplayName(Logger.color("&6Effect time (seconds): &ainfinite"));
        }else{
            meta.setDisplayName(Logger.color("&6Effect time (seconds): &a" + reward.getDuration()));
        }
        meta.setLore(Arrays.asList(Logger.color("&3Click to reset")));
        time.setItemMeta(meta);
        
        ItemStack amplifier = new ItemStack(Material.BEACON);
        meta = amplifier.getItemMeta();
        meta.setDisplayName(Logger.color("&6Effect amplifier: &a" + reward.getAmplifier()));
        meta.setLore(Arrays.asList(Logger.color("&3Click to reset")));
        amplifier.setItemMeta(meta);
        
        
        ItemStack minus1 = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
        meta = minus1.getItemMeta();
        meta.setDisplayName(Logger.color("&c&l-1"));
        minus1.setItemMeta(meta);
        
        ItemStack minus10 = minus1.clone();
        meta = minus10.getItemMeta();
        meta.setDisplayName(Logger.color("&c&l-10"));
        minus10.setItemMeta(meta);
        
        ItemStack minus100 = minus1.clone();
        meta = minus100.getItemMeta();
        meta.setDisplayName(Logger.color("&c&l-100"));
        minus100.setItemMeta(meta);
        
        ItemStack plus1 = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
        meta = minus1.getItemMeta();
        meta.setDisplayName(Logger.color("&a&l+1"));
        plus1.setItemMeta(meta);
        
        ItemStack plus10 = plus1.clone();
        meta = plus10.getItemMeta();
        meta.setDisplayName(Logger.color("&a&l+10"));
        plus10.setItemMeta(meta);
        
        ItemStack plus100 = plus1.clone();
        meta = plus100.getItemMeta();
        meta.setDisplayName(Logger.color("&a&l+100"));
        plus100.setItemMeta(meta);
        
        ItemStack effectStack;
        if(reward.isClearEffects()){
            effectStack = new ItemStack(Material.MILK_BUCKET);
            meta = effectStack.getItemMeta();
            meta.setDisplayName(Logger.color("&bSelected effect: &d" + EffectReward.CLEAR_EFFECTS_TAG));
        }else{
            effectStack = new ItemStack(Material.POTION);
            meta = effectStack.getItemMeta();
            if(reward.getPotionEffect() == null){
                meta.setDisplayName(Logger.color("&bSelected effect: &dnull"));
            }else{
                meta.setDisplayName(Logger.color("&bSelected effect: &d" + reward.getPotionEffect().getName()));
                PotionMeta pm = (PotionMeta) meta;
                pm.addCustomEffect(new PotionEffect(reward.getPotionEffect(), 0, 0), true);
            }            
        }
        meta.setLore(Logger.color(Arrays.asList("&3Click to select")));
        effectStack.setItemMeta(meta);
        
        ItemStack ambientItem;
        if(reward.isAmbient()){
            ambientItem = XMaterial.GRASS_BLOCK.parseItem();
        }else{
            ambientItem = new ItemStack(Material.DIRT);
        }        
        meta = ambientItem.getItemMeta();
        if(reward.isAmbient()){
            meta.setDisplayName(Logger.color("&bIsAmbient: &atrue"));
        }else{
            meta.setDisplayName(Logger.color("&bIsAmbient: &cfalse"));
        }
        meta.setLore(Logger.color(Arrays.asList("&3Click to toggle")));
        ambientItem.setItemMeta(meta);
        
        ItemStack showParticlesItem;
        if(reward.isShowParticles()){
            showParticlesItem = new ItemStack(Material.MELON_SEEDS);
        }else{
            showParticlesItem = XMaterial.GLASS_PANE.parseItem();           
        }
        meta = showParticlesItem.getItemMeta();
        if(reward.isShowParticles()){
            meta.setDisplayName(Logger.color("&bShowParticles: &atrue"));
        }else{
            meta.setDisplayName(Logger.color("&bShowParticles: &cfalse"));
        }
        meta.setLore(Logger.color(Arrays.asList("&3Click to toggle")));
        showParticlesItem.setItemMeta(meta);
        
        
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName(Logger.color("&7Back"));
        back.setItemMeta(meta);
        
        ItemStack next = new ItemStack(Material.ANVIL);
        meta = next.getItemMeta();
        meta.setDisplayName(Logger.color("&bNext"));
        next.setItemMeta(meta);
        
        for(int i=0;i<9;i++){
            inv.setItem(i, glass);
        }
        for(int i=36;i<45;i++){
            inv.setItem(i, glass);
        }
        inv.setItem(9, glass);
        inv.setItem(17, glass);
        inv.setItem(18, glass);
        inv.setItem(26, glass);
        inv.setItem(27, glass);
        inv.setItem(35, glass);
        
        inv.setItem(10, back);
        inv.setItem(16, next);
        
        inv.setItem(13, effectStack);
        
        if(!reward.isClearEffects()){
            inv.setItem(12, ambientItem);
            inv.setItem(14, showParticlesItem);
            
            inv.setItem(19, minus100);
            inv.setItem(20, minus10);
            inv.setItem(21, minus1);
            inv.setItem(22, time);
            inv.setItem(23, plus1);
            inv.setItem(24, plus10);
            inv.setItem(25, plus100);

            inv.setItem(28, minus100);
            inv.setItem(29, minus10);
            inv.setItem(30, minus1);
            inv.setItem(31, amplifier);
            inv.setItem(32, plus1);
            inv.setItem(33, plus10);
            inv.setItem(34, plus100);
        }
            
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction EFFECT_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            if(reward.isClearEffects() && e.getSlot() != 10 && e.getSlot() != 16 && e.getSlot() != 12 && e.getSlot() != 13 && e.getSlot() != 14){
                return;
            }
            
            switch(e.getSlot()){
                case 10:
                    //Return to the previous menu
                    if(FinishMenu.editMode){
                        FinishMenu.openFinishInventory(p);
                    }else{
                        RewardTypesMenu.openRewardTypesMenu(p);
                    }
                    break;
                case 16:
                    //Open next menu
                    if(reward.isClearEffects() || reward.getPotionEffect() != null){
                        FinishMenu.addReward(reward);
                        reset();
                        FinishMenu.openFinishInventory(p);
                    }
                    break;
                case 13:
                    //Open effect types menu
                    openEffectMenu2(p);
                    break;
                case 12:
                    //Toggle effect ambient
                    reward.setAmbient(!reward.isAmbient());
                    openEffectMenu(p);
                    break;
                case 14:
                    //Toggle effect showParticles
                    reward.setShowParticles(!reward.isShowParticles());
                    openEffectMenu(p);
                    break;
                //<editor-fold defaultstate="collapsed" desc="EffectTime">
                case 19:
                    //Effect time -100
                    int effect_time = reward.getDuration();
                    effect_time -= 100;
                    if(effect_time < -1){
                        effect_time = -1;
                    }
                    reward.setDuration(effect_time);
                    openEffectMenu(p);
                    break;
                case 20:
                    //Effect time -10
                    effect_time = reward.getDuration();
                    effect_time -= 10;
                    if(effect_time < -1){
                        effect_time = -1;
                    }
                    reward.setDuration(effect_time);
                    openEffectMenu(p);
                    break;
                case 21:
                    //Effect time -1
                    effect_time = reward.getDuration();
                    effect_time--;
                    if(effect_time < -1){
                        effect_time = -1;
                    }
                    reward.setDuration(effect_time);
                    openEffectMenu(p);
                    break;
                case 22:
                    //Effect time = 30
                    effect_time = 30;
                    reward.setDuration(effect_time);
                    openEffectMenu(p);
                    break;
                case 23:
                    //Effect time +1
                    effect_time = reward.getDuration();
                    effect_time ++;
                    reward.setDuration(effect_time);
                    openEffectMenu(p);
                    break;
                case 24:
                    //Effect time +10
                    effect_time = reward.getDuration();
                    effect_time += 10;
                    reward.setDuration(effect_time);
                    openEffectMenu(p);
                    break;
                case 25:
                    //Effect time +100
                    effect_time = reward.getDuration();
                    effect_time += 100;
                    reward.setDuration(effect_time);
                    openEffectMenu(p);
                    break;
//</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="EffectAmplifier">
                case 28:
                    //Effect amplifier -100
                    int effect_amplifier = reward.getAmplifier();
                    effect_amplifier -= 100;
                    if(effect_amplifier < 0){
                        effect_amplifier = 0;
                    }
                    reward.setAmplifier(effect_amplifier);
                    openEffectMenu(p);
                    break;
                case 29:
                    //Effect amplifier -10
                    effect_amplifier = reward.getAmplifier();
                    effect_amplifier -= 10;
                    if(effect_amplifier < 0){
                        effect_amplifier = 0;
                    }
                    reward.setAmplifier(effect_amplifier);
                    openEffectMenu(p);
                    break;
                case 30:
                    //Effect amplifier -1
                    effect_amplifier = reward.getAmplifier();
                    effect_amplifier--;
                    if(effect_amplifier < 0){
                        effect_amplifier = 0;
                    }
                    reward.setAmplifier(effect_amplifier);
                    openEffectMenu(p);
                    break;
                case 31:
                    //Effect amplifier = 1
                    effect_amplifier = 1;
                    reward.setAmplifier(effect_amplifier);
                    openEffectMenu(p);
                    break;
                case 32:
                    //Effect amplifier +1
                    effect_amplifier = reward.getAmplifier();
                    effect_amplifier ++;
                    if(effect_amplifier > 255){
                        effect_amplifier = 255;
                    }
                    reward.setAmplifier(effect_amplifier);
                    openEffectMenu(p);
                    break;
                case 33:
                    //Effect amplifier +10
                    effect_amplifier = reward.getAmplifier();
                    effect_amplifier += 10;
                    if(effect_amplifier > 255){
                        effect_amplifier = 255;
                    }
                    reward.setAmplifier(effect_amplifier);
                    openEffectMenu(p);
                    break;
                case 34:
                    //Effect amplifier +100
                    effect_amplifier = reward.getAmplifier();
                    effect_amplifier += 100;
                    if(effect_amplifier > 255){
                        effect_amplifier = 255;
                    }
                    reward.setAmplifier(effect_amplifier);
                    openEffectMenu(p);
                    break;
//</editor-fold>

            }
        }
//</editor-fold>
    };
    
    //Effects2 inventory
    private static void openEffectMenu2(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.EFFECT_MENU_2, 54, Logger.color("&d&lEffect List"));
        
        List<String> effectTypeNames = new ArrayList<>();
        for(PotionEffectType pe : PotionEffectType.values()){
            try{
                effectTypeNames.add(pe.getName());
            }catch(Exception ex){
                
            }
        }
        Collections.sort(effectTypeNames);
        
        ItemStack clearEffects = new ItemStack(Material.MILK_BUCKET);
        ItemMeta meta = clearEffects.getItemMeta();
        meta.setDisplayName(Logger.color("&d" + EffectReward.CLEAR_EFFECTS_TAG));
        clearEffects.setItemMeta(meta);
        
        inv.setItem(0, clearEffects);
        
        for(int i=1;i<effectTypeNames.size();i++){
            String typeName = effectTypeNames.get(i-1);
            
            ItemStack sk = new ItemStack(Material.POTION);
            meta = sk.getItemMeta();
            meta.setDisplayName(Logger.color("&d" + typeName));
            PotionMeta pm = (PotionMeta) meta;
            pm.addCustomEffect(new PotionEffect(PotionEffectType.getByName(typeName), 0, 0), true);
            sk.setItemMeta(pm);
            
            inv.setItem(i, sk);
        }
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName(Logger.color("&7Back"));
        back.setItemMeta(meta);
        
        inv.setItem(45, back);
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction EFFECT_MENU_2_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){

            if(e.getSlot() == 45){
                openEffectMenu(p);
                return;
            }
            if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
                String effect_name = Logger.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                if(effect_name.equals(EffectReward.CLEAR_EFFECTS_TAG)){
                    reward.setClearEffects(true);
                    reward.setPotionEffect(null);
                }else{
                    reward.setClearEffects(false);
                    reward.setPotionEffect(PotionEffectType.getByName(effect_name));
                }
                openEffectMenu(p);
            }
        }
//</editor-fold>
    };
}
