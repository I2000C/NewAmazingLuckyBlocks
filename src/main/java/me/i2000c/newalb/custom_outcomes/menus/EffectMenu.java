package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import me.i2000c.newalb.custom_outcomes.utils.rewards.EffectReward;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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
            reward = new EffectReward(RewardListMenu.getCurrentOutcome());
        }
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.EFFECT_MENU, 45, "&5&lEffect Reward");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.MAGENTA);
        
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.CLOCK);
        if(reward.getDuration() < 0){
            builder.withDisplayName("&6Effect time (seconds): &ainfinite");
        }else{
            builder.withDisplayName("&6Effect time (seconds): &a" + reward.getDuration());
        }
        builder.addLoreLine("&3Click to reset");
        ItemStack time_item = builder.build();
        
        ItemStack amplifier = ItemBuilder.newItem(XMaterial.BEACON)
                .withDisplayName("&6Effect amplifier: &a" + reward.getAmplifier())
                .addLoreLine("&3Click to reset")
                .build();
        
        if(reward.isClearEffects()){
            builder = ItemBuilder.newItem(XMaterial.MILK_BUCKET);
            builder.withDisplayName("&bSelected effect: &d" + EffectReward.CLEAR_EFFECTS_TAG);
        }else{
            builder = ItemBuilder.newItem(XMaterial.POTION);
            if(reward.getPotionEffect() == null){
                builder.withDisplayName("&bSelected effect: &dnull");
            }else{
                builder.withDisplayName("&bSelected effect: &d" + reward.getPotionEffect().getName());
                builder.addPotionEffect(new PotionEffect(reward.getPotionEffect(), 0, 0));
            }
        }
        builder.addLoreLine("&3Click to select");
        ItemStack effectStack = builder.build();
        
        if(reward.isAmbient()){
            builder = ItemBuilder.newItem(XMaterial.GLASS_PANE);
            builder.withDisplayName("&bIs ambient: &atrue");
        }else{
            builder = ItemBuilder.newItem(XMaterial.WHITE_STAINED_GLASS_PANE);
            builder.withDisplayName("&bIs ambient: &cfalse");
        }        
        builder.addLoreLine("&3Click to toggle");
        ItemStack ambientItem = builder.build();
                
        if(reward.isShowParticles()){
            builder = ItemBuilder.newItem(XMaterial.MELON_SEEDS);
            builder.withDisplayName("&bShow particles: &atrue");
        }else{
            builder = ItemBuilder.newItem(XMaterial.GLASS_PANE);
            builder.withDisplayName("&bShow particles: &cfalse");
        }
        builder.addLoreLine("&3Click to toggle");
        ItemStack showParticlesItem = builder.build();
        
        
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
        
        inv.setItem(10, GUIItem.getBackItem());
        inv.setItem(16, GUIItem.getNextItem());
        
        inv.setItem(13, effectStack);
        
        if(!reward.isClearEffects()){
            inv.setItem(12, ambientItem);
            inv.setItem(14, showParticlesItem);
            
            inv.setItem(19, GUIItem.getPlusLessItem(-100));
            inv.setItem(20, GUIItem.getPlusLessItem(-10));
            inv.setItem(21, GUIItem.getPlusLessItem(-1));
            inv.setItem(22, time_item);
            inv.setItem(23, GUIItem.getPlusLessItem(+1));
            inv.setItem(24, GUIItem.getPlusLessItem(+10));
            inv.setItem(25, GUIItem.getPlusLessItem(+100));

            inv.setItem(28, GUIItem.getPlusLessItem(-100));
            inv.setItem(29, GUIItem.getPlusLessItem(-10));
            inv.setItem(30, GUIItem.getPlusLessItem(-1));
            inv.setItem(31, amplifier);
            inv.setItem(32, GUIItem.getPlusLessItem(+1));
            inv.setItem(33, GUIItem.getPlusLessItem(+10));
            inv.setItem(34, GUIItem.getPlusLessItem(+100));
        }
            
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction EFFECT_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            if(reward.isClearEffects() && e.getSlot() != 10 && e.getSlot() != 16 && e.getSlot() != 12 && e.getSlot() != 13 && e.getSlot() != 14){
                return;
            }
            
            switch(e.getSlot()){
                case 10:
                    //Return to the previous menu
                    if(RewardListMenu.editMode){
                        RewardListMenu.openFinishInventory(p);
                    }else{
                        RewardTypesMenu.openRewardTypesMenu(p);
                    }
                    break;
                case 16:
                    //Open next menu
                    if(reward.isClearEffects() || reward.getPotionEffect() != null){
                        RewardListMenu.addReward(reward);
                        reset();
                        RewardListMenu.openFinishInventory(p);
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
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.EFFECT_MENU_2, 54, "&d&lEffect List");
        
        List<PotionEffectType> effectTypes = Arrays.asList(PotionEffectType.values());
        effectTypes.sort((effectType1, effectType2) -> {
            String effectTypeName1 = effectType1.getName();
            String effectTypeName2 = effectType2.getName();
            return effectTypeName1.compareTo(effectTypeName2);
        });
        
        ItemStack clearEffects = ItemBuilder.newItem(XMaterial.MILK_BUCKET)
                .withDisplayName("&d" + EffectReward.CLEAR_EFFECTS_TAG)
                .build();
        
        inv.setItem(0, clearEffects);
        
        Iterator<PotionEffectType> iterator = effectTypes.iterator();
        for(int i=1; iterator.hasNext() && i<45; i++){
            PotionEffectType effectType = iterator.next();
            PotionEffect potionEffect = new PotionEffect(effectType, 0, 0);
            ItemStack effectItem = ItemBuilder.newItem(XMaterial.POTION)
                    .withDisplayName("&d" + effectType.getName())
                    .addPotionEffect(potionEffect)
                    .build();
            inv.setItem(i, effectItem);
        }
        
        inv.setItem(45, GUIItem.getBackItem());
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction EFFECT_MENU_2_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){

            if(e.getSlot() == 45){
                openEffectMenu(p);
                return;
            }
            if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
                String displayName = ItemBuilder.fromItem(e.getCurrentItem(), false)
                        .getDisplayName();
                if(displayName != null){
                    String effect_name = Logger.stripColor(displayName);
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
        }
//</editor-fold>
    };
}
