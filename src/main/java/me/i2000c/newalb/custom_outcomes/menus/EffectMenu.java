package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.EffectReward;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EffectMenu extends Editor<EffectReward>{
    public EffectMenu(){
        InventoryListener.registerInventory(CustomInventoryType.EFFECT_MENU, EFFECT_MENU_FUNCTION);
        InventoryListener.registerInventory(CustomInventoryType.EFFECT_MENU_2, EFFECT_MENU_2_FUNCTION);
        
        POTION_EFFECT_TYPES = new ArrayList<>(Arrays.asList(PotionEffectType.values()));
        POTION_EFFECT_TYPES.removeIf(potionEffectType -> potionEffectType == null);
        POTION_EFFECT_TYPES.sort((effectType1, effectType2) -> {
            String effectTypeName1 = effectType1.getName();
            String effectTypeName2 = effectType2.getName();
            return effectTypeName1.compareTo(effectTypeName2);
        });
    }
    
    private final List<PotionEffectType> POTION_EFFECT_TYPES;        
    
    private static boolean showClearEffectsItem;
    
    public static void setShowClearEffectsItem(boolean showItem){
        showClearEffectsItem = showItem;
    }
    
    @Override
    protected void newItem(Player player){
        Outcome outcome = RewardListMenu.getCurrentOutcome();
        item = new EffectReward(outcome);
        openEffectMenu(player);
    }
    
    @Override
    protected void editItem(Player player){
        openEffectMenu(player);
    }
    
    private void openEffectMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String invTitle = showClearEffectsItem ? "&5&lEffect Reward" : "&5&lEffect Menu";        
        Menu menu = GUIFactory.newMenu(CustomInventoryType.EFFECT_MENU, 45, invTitle);
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.MAGENTA);
        
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.CLOCK);
        if(item.getDuration() < 0){
            builder.withDisplayName("&6Effect time (seconds): &ainfinite");
        }else{
            builder.withDisplayName("&6Effect time (seconds): &a" + item.getDuration());
        }
        builder.addLoreLine("&3Click to reset");
        ItemStack time_item = builder.build();
        
        ItemStack amplifier = ItemBuilder.newItem(XMaterial.BEACON)
                .withDisplayName("&6Effect amplifier: &a" + item.getAmplifier())
                .addLoreLine("&3Click to reset")
                .build();
        
        if(item.isClearEffects()){
            builder = ItemBuilder.newItem(XMaterial.MILK_BUCKET);
            builder.withDisplayName("&bSelected effect: &d" + EffectReward.CLEAR_EFFECTS_TAG);
        }else{
            builder = ItemBuilder.newItem(XMaterial.POTION);
            if(item.getPotionEffect() == null){
                builder.withDisplayName("&bSelected effect: &dnull");
            }else{
                builder.withDisplayName("&bSelected effect: &d" + item.getPotionEffect().getName());
                builder.addPotionEffect(new PotionEffect(item.getPotionEffect(), 0, 0));
            }
        }
        builder.addLoreLine("&3Click to select");
        ItemStack effectStack = builder.build();
        
        if(item.isAmbient()){
            builder = ItemBuilder.newItem(XMaterial.GLASS_PANE);
            builder.withDisplayName("&bIs ambient: &atrue");
        }else{
            builder = ItemBuilder.newItem(XMaterial.WHITE_STAINED_GLASS_PANE);
            builder.withDisplayName("&bIs ambient: &cfalse");
        }        
        builder.addLoreLine("&3Click to toggle");
        ItemStack ambientItem = builder.build();
                
        if(item.isShowParticles()){
            builder = ItemBuilder.newItem(XMaterial.MELON_SEEDS);
            builder.withDisplayName("&bShow particles: &atrue");
        }else{
            builder = ItemBuilder.newItem(XMaterial.GLASS_PANE);
            builder.withDisplayName("&bShow particles: &cfalse");
        }
        builder.addLoreLine("&3Click to toggle");
        ItemStack showParticlesItem = builder.build();
        
        
        for(int i=0;i<9;i++){
            menu.setItem(i, glass);
        }
        for(int i=36;i<45;i++){
            menu.setItem(i, glass);
        }
        menu.setItem(9, glass);
        menu.setItem(17, glass);
        menu.setItem(18, glass);
        menu.setItem(26, glass);
        menu.setItem(27, glass);
        menu.setItem(35, glass);
        
        menu.setItem(10, GUIItem.getBackItem());
        menu.setItem(16, GUIItem.getNextItem());
        
        menu.setItem(13, effectStack);
        
        if(!item.isClearEffects()){
            menu.setItem(12, ambientItem);
            menu.setItem(14, showParticlesItem);
            
            menu.setItem(19, GUIItem.getPlusLessItem(-100));
            menu.setItem(20, GUIItem.getPlusLessItem(-10));
            menu.setItem(21, GUIItem.getPlusLessItem(-1));
            menu.setItem(22, time_item);
            menu.setItem(23, GUIItem.getPlusLessItem(+1));
            menu.setItem(24, GUIItem.getPlusLessItem(+10));
            menu.setItem(25, GUIItem.getPlusLessItem(+100));

            menu.setItem(28, GUIItem.getPlusLessItem(-100));
            menu.setItem(29, GUIItem.getPlusLessItem(-10));
            menu.setItem(30, GUIItem.getPlusLessItem(-1));
            menu.setItem(31, amplifier);
            menu.setItem(32, GUIItem.getPlusLessItem(+1));
            menu.setItem(33, GUIItem.getPlusLessItem(+10));
            menu.setItem(34, GUIItem.getPlusLessItem(+100));
        }
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction EFFECT_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            if(item.isClearEffects() && e.getSlot() != 10 && e.getSlot() != 16 && e.getSlot() != 12 && e.getSlot() != 13 && e.getSlot() != 14){
                return;
            }
            
            switch(e.getSlot()){
                case 10:
                    // Go to previous menu
                    onBack.accept(player);
                    break;
                case 16:
                    // Go to next menu
                    if(item.isClearEffects() || item.getPotionEffect() != null){
                        onNext.accept(player, item);
                    }
                    break;
                case 13:
                    //Open effect types menu
                    openEffectMenu2(player);
                    break;
                case 12:
                    //Toggle effect ambient
                    item.setAmbient(!item.isAmbient());
                    openEffectMenu(player);
                    break;
                case 14:
                    //Toggle effect showParticles
                    item.setShowParticles(!item.isShowParticles());
                    openEffectMenu(player);
                    break;
                //<editor-fold defaultstate="collapsed" desc="EffectTime">
                case 19:
                    //Effect time -100
                    int effect_time = item.getDuration();
                    effect_time -= 100;
                    if(effect_time < -1){
                        effect_time = -1;
                    }
                    item.setDuration(effect_time);
                    openEffectMenu(player);
                    break;
                case 20:
                    //Effect time -10
                    effect_time = item.getDuration();
                    effect_time -= 10;
                    if(effect_time < -1){
                        effect_time = -1;
                    }
                    item.setDuration(effect_time);
                    openEffectMenu(player);
                    break;
                case 21:
                    //Effect time -1
                    effect_time = item.getDuration();
                    effect_time--;
                    if(effect_time < -1){
                        effect_time = -1;
                    }
                    item.setDuration(effect_time);
                    openEffectMenu(player);
                    break;
                case 22:
                    //Effect time = 30
                    effect_time = 30;
                    item.setDuration(effect_time);
                    openEffectMenu(player);
                    break;
                case 23:
                    //Effect time +1
                    effect_time = item.getDuration();
                    effect_time ++;
                    item.setDuration(effect_time);
                    openEffectMenu(player);
                    break;
                case 24:
                    //Effect time +10
                    effect_time = item.getDuration();
                    effect_time += 10;
                    item.setDuration(effect_time);
                    openEffectMenu(player);
                    break;
                case 25:
                    //Effect time +100
                    effect_time = item.getDuration();
                    effect_time += 100;
                    item.setDuration(effect_time);
                    openEffectMenu(player);
                    break;
//</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="EffectAmplifier">
                case 28:
                    //Effect amplifier -100
                    int effect_amplifier = item.getAmplifier();
                    effect_amplifier -= 100;
                    if(effect_amplifier < 0){
                        effect_amplifier = 0;
                    }
                    item.setAmplifier(effect_amplifier);
                    openEffectMenu(player);
                    break;
                case 29:
                    //Effect amplifier -10
                    effect_amplifier = item.getAmplifier();
                    effect_amplifier -= 10;
                    if(effect_amplifier < 0){
                        effect_amplifier = 0;
                    }
                    item.setAmplifier(effect_amplifier);
                    openEffectMenu(player);
                    break;
                case 30:
                    //Effect amplifier -1
                    effect_amplifier = item.getAmplifier();
                    effect_amplifier--;
                    if(effect_amplifier < 0){
                        effect_amplifier = 0;
                    }
                    item.setAmplifier(effect_amplifier);
                    openEffectMenu(player);
                    break;
                case 31:
                    //Effect amplifier = 1
                    effect_amplifier = 1;
                    item.setAmplifier(effect_amplifier);
                    openEffectMenu(player);
                    break;
                case 32:
                    //Effect amplifier +1
                    effect_amplifier = item.getAmplifier();
                    effect_amplifier ++;
                    if(effect_amplifier > 255){
                        effect_amplifier = 255;
                    }
                    item.setAmplifier(effect_amplifier);
                    openEffectMenu(player);
                    break;
                case 33:
                    //Effect amplifier +10
                    effect_amplifier = item.getAmplifier();
                    effect_amplifier += 10;
                    if(effect_amplifier > 255){
                        effect_amplifier = 255;
                    }
                    item.setAmplifier(effect_amplifier);
                    openEffectMenu(player);
                    break;
                case 34:
                    //Effect amplifier +100
                    effect_amplifier = item.getAmplifier();
                    effect_amplifier += 100;
                    if(effect_amplifier > 255){
                        effect_amplifier = 255;
                    }
                    item.setAmplifier(effect_amplifier);
                    openEffectMenu(player);
                    break;
//</editor-fold>

            }
        }
//</editor-fold>
    };
    
    private void openEffectMenu2(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.EFFECT_MENU_2, 54, "&d&lEffect List");
        
        int initialSlot;
        if(showClearEffectsItem){
            ItemStack clearEffects = ItemBuilder.newItem(XMaterial.MILK_BUCKET)
                .withDisplayName("&d" + EffectReward.CLEAR_EFFECTS_TAG)
                .build();
            menu.setItem(0, clearEffects);
            initialSlot = 1;
        }else{
            initialSlot = 0;
        }        
        
        Iterator<PotionEffectType> iterator = POTION_EFFECT_TYPES.iterator();
        for(int i=initialSlot; iterator.hasNext() && i<45; i++){
            PotionEffectType effectType = iterator.next();
            PotionEffect potionEffect = new PotionEffect(effectType, 0, 0);
            ItemStack effectItem = ItemBuilder.newItem(XMaterial.POTION)
                    .withDisplayName("&d" + effectType.getName())
                    .addPotionEffect(potionEffect)
                    .build();
            menu.setItem(i, effectItem);
        }
        
        menu.setItem(45, GUIItem.getBackItem());
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction EFFECT_MENU_2_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            if(e.getSlot() == 45){
                openEffectMenu(player);
                return;
            }
            
            if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
                String displayName = ItemBuilder.fromItem(e.getCurrentItem(), false)
                        .getDisplayName();
                if(displayName != null){
                    String effect_name = Logger.stripColor(displayName);
                    if(effect_name.equals(EffectReward.CLEAR_EFFECTS_TAG)){
                        item.setClearEffects(true);
                        item.setPotionEffect(null);
                    }else{
                        item.setClearEffects(false);
                        item.setPotionEffect(PotionEffectType.getByName(effect_name));
                    }
                    openEffectMenu(player);
                }                    
            }
        }
//</editor-fold>
    };
}
