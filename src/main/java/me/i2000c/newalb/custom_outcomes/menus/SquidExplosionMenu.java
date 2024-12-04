package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.editor.EditorType;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.EffectReward;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.SquidExplosionReward;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SquidExplosionMenu extends Editor<SquidExplosionReward>{
    public SquidExplosionMenu(){
        InventoryListener.registerInventory(CustomInventoryType.SQUID_EXPLOSION_MENU, SQUID_EXPLOSION_MENU_FUNCTION);
    }
    
    private static final int PREVIOUS_SLOT = 19;
    private static final int NEXT_SLOT = 25;    
    
    private static final int RESET_EFFECT_LIST_SLOT = 21;
    private static final int EFFECT_LIST_SLOT = 22;
    private static final int ADD_EFFECT_SLOT = 23;
    
    private static final int RESET_TIME_SLOT = 13;
    private static final int RESET_RADIUS_SLOT = 31;    

    @Override
    protected void newItem(Player player){
        Outcome outcome = RewardListMenu.getCurrentOutcome();
        item = new SquidExplosionReward(outcome);
        openSquidExplosionMenu(player);
    }

    @Override
    protected void editItem(Player player){
        openSquidExplosionMenu(player);
    }
    
    private void openSquidExplosionMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.SQUID_EXPLOSION_MENU, 45, "&8&lSquid Explosion Reward");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.GRAY);
        for(int i=0;i<9;i++){
            menu.setItem(i, glass);
        }
        for(int i=36;i<45;i++){
            menu.setItem(i, glass);
        }
        for(int i=0;i<45;i+=9){
            menu.setItem(i, glass);
        }
        for(int i=8;i<45;i+=9){
            menu.setItem(i, glass);
        }
        
        ItemStack timeStack = ItemStackWrapper.newItem(XMaterial.CLOCK)
                                              .setDisplayName("&eExplosion countdown time: &b" + item.getCountdownTime())
                                              .addLoreLine("&3Click to reset")
                                              .toItemStack();
        
        ItemStack radiusStack = ItemStackWrapper.newItem(XMaterial.COMPASS)
                                                .setDisplayName("&6Explosion radius: &b" + item.getRadius())
                                                .addLoreLine("&3Click to reset")
                                                .toItemStack();
        
        ItemStack addEffectStack = ItemStackWrapper.newItem(XMaterial.POTION)
                                                   .setDisplayName("&aClick to add potion effects")
                                                   .toItemStack();
        
        ItemStack resetEffectsStack = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                                      .setDisplayName("&cClick to remove all potion effects")
                                                      .toItemStack();
        
        ItemStackWrapper builder = ItemStackWrapper.newItem(XMaterial.ENCHANTING_TABLE);
        builder.setDisplayName("&3Current potion effects:");
        item.getEffects().forEach(effect -> {
            String name = effect.getType().getName();
            int duration = effect.getDuration();
            int amplifier = effect.getAmplifier();
            boolean isAmbient = effect.isAmbient();
            boolean showParticles = effect.hasParticles();
            builder.addLoreLine(String.format("  &d%s;%d;%d;%s;%s",
                    name, duration, amplifier, isAmbient, showParticles));
        });
        ItemStack effectListStack = builder.toItemStack();
        
        menu.setItem(19, GUIItem.getBackItem());
        menu.setItem(25, GUIItem.getNextItem());
        
        menu.setItem(RESET_EFFECT_LIST_SLOT, resetEffectsStack);
        menu.setItem(ADD_EFFECT_SLOT, addEffectStack);
        menu.setItem(EFFECT_LIST_SLOT, effectListStack);
        
        menu.setItem(RESET_TIME_SLOT, timeStack);
        
        for(int i=1, multiplier=1; i<=2; i++, multiplier *= 10){
            menu.setItem(RESET_TIME_SLOT-i, GUIItem.getPlusLessItem(-1*multiplier));
            menu.setItem(RESET_TIME_SLOT+i, GUIItem.getPlusLessItem(+1*multiplier));
        }
        
        menu.setItem(RESET_RADIUS_SLOT, radiusStack);
        
        for(int i=1, multiplier=1; i<=3; i++, multiplier *= 10){
            menu.setItem(RESET_RADIUS_SLOT-i, GUIItem.getPlusLessItem(-1*multiplier));
            menu.setItem(RESET_RADIUS_SLOT+i, GUIItem.getPlusLessItem(+1*multiplier));
        }
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction SQUID_EXPLOSION_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case PREVIOUS_SLOT:
                    // Go to previous menu
                    onBack.accept(player);
                    break;
                case NEXT_SLOT:
                    // Go to next menu
                    onNext.accept(player, item);
                    break;
                case ADD_EFFECT_SLOT:
                    Editor<EffectReward> editor = EditorType.EFFECT_REWARD.getEditor();
                    EffectMenu.setShowClearEffectsItem(false);
                    editor.createNewItem(
                            player, 
                            p -> openSquidExplosionMenu(p), 
                            (p, effectReward) -> {
                                PotionEffectType effectType = effectReward.getPotionEffect();
                                int effectDuration = effectReward.getDuration();
                                int effectAmplifier = effectReward.getAmplifier();
                                boolean isAmbient = effectReward.isAmbient();
                                boolean showParticles = effectReward.isShowParticles();
                                
                                PotionEffect potionEffect = new PotionEffect(effectType, effectDuration, effectAmplifier, isAmbient, showParticles);
                                item.getEffects().add(potionEffect);
                                
                                openSquidExplosionMenu(p);
                            });
                    break;
                case RESET_EFFECT_LIST_SLOT:
                    // Reset effect list
                    item.getEffects().clear();
                    openSquidExplosionMenu(player);
                    break;
                    //<editor-fold defaultstate="collapsed" desc="CountdownTime">
                case RESET_TIME_SLOT:
                    // Reset countdownTime
                    item.setCountdownTime(5);
                    openSquidExplosionMenu(player);
                    break;
                case RESET_TIME_SLOT-1:
                    // countdownTime - 1
                    int countdownTime = item.getCountdownTime() - 1;
                    if(countdownTime <= 0){
                        countdownTime = 0;
                    }
                    item.setCountdownTime(countdownTime);
                    openSquidExplosionMenu(player);
                    break;
                case RESET_TIME_SLOT-2:
                    // countdownTime - 10
                    countdownTime = item.getCountdownTime() - 10;
                    if(countdownTime <= 0){
                        countdownTime = 0;
                    }
                    item.setCountdownTime(countdownTime);
                    openSquidExplosionMenu(player);
                    break;
                case RESET_TIME_SLOT+1:
                    // countdownTime + 1
                    countdownTime = item.getCountdownTime();
                    item.setCountdownTime(countdownTime + 1);
                    openSquidExplosionMenu(player);
                    break;
                case RESET_TIME_SLOT+2:
                    // countdownTime + 10
                    countdownTime = item.getCountdownTime();
                    item.setCountdownTime(countdownTime + 10);
                    openSquidExplosionMenu(player);
                    break;
//</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Radius">
                case RESET_RADIUS_SLOT:
                    // Reset radius
                    item.setRadius(5);
                    openSquidExplosionMenu(player);
                    break;
                case RESET_RADIUS_SLOT-1:
                    // Reset radius - 1
                    int radius = item.getRadius() - 1;
                    if(radius <= 0){
                        radius = 0;
                    }
                    item.setRadius(radius);
                    openSquidExplosionMenu(player);
                    break;
                case RESET_RADIUS_SLOT-2:
                    // Reset radius - 10
                    radius = item.getRadius() - 10;
                    if(radius <= 0){
                        radius = 0;
                    }
                    item.setRadius(radius);
                    openSquidExplosionMenu(player);
                    break;
                case RESET_RADIUS_SLOT-3:
                    // Reset radius - 100
                    radius = item.getRadius() - 100;
                    if(radius <= 0){
                        radius = 0;
                    }
                    item.setRadius(radius);
                    openSquidExplosionMenu(player);
                    break;
                case RESET_RADIUS_SLOT+1:
                    // Reset radius + 1
                    radius = item.getRadius();
                    item.setRadius(radius + 1);
                    openSquidExplosionMenu(player);
                    break;
                case RESET_RADIUS_SLOT+2:
                    // Reset radius + 10
                    radius = item.getRadius();
                    item.setRadius(radius + 10);
                    openSquidExplosionMenu(player);
                    break;
                case RESET_RADIUS_SLOT+3:
                    // Reset radius + 100
                    radius = item.getRadius();
                    item.setRadius(radius + 100);
                    openSquidExplosionMenu(player);
                    break;
//</editor-fold>
            }
        }
//</editor-fold>
    };
}
