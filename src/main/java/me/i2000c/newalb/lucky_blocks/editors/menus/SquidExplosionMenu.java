package me.i2000c.newalb.lucky_blocks.editors.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.effect.EffectMenu;
import me.i2000c.newalb.lucky_blocks.rewards.RewardType;
import me.i2000c.newalb.lucky_blocks.rewards.types.EffectReward;
import me.i2000c.newalb.lucky_blocks.rewards.types.SquidExplosionReward;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.misc.OtherUtils;

public class SquidExplosionMenu extends EditorMenu<SquidExplosionReward> {
    
    private static final int PREVIOUS_SLOT = 19;
    private static final int NEXT_SLOT = 25;
    
    private static final int RESET_EFFECT_LIST_SLOT = 21;
    private static final int EFFECT_LIST_SLOT = 22;
    private static final int ADD_EFFECT_SLOT = 23;
    
    private static final int RESET_TIME_SLOT = 13;
    private static final int RESET_RADIUS_SLOT = 31;
    
    private static final int[] VALUES = {-10, -1, 0, +1, +10};
    private static final int[] VALUES2 = {-100, -10, -1, 0, +1, +10, +100};
    
    private final List<Consumer<Integer>> squidExplosionRewardPropertyList = new ArrayList<>();
    
    public SquidExplosionMenu() {
        super("&8&lSquid Explosion Reward", MenuSize.SIZE_5_ROWS, true);
        
        squidExplosionRewardPropertyList.add(value -> {
            if(value == 0) {
                item.setCountdownTime(5);
            } else {
                int time = OtherUtils.clamp(item.getCountdownTime() + value, 0, Integer.MAX_VALUE);
                item.setCountdownTime(time);
            }
        });
        squidExplosionRewardPropertyList.add(value -> {
            if(value == 0) {
                item.setRadius(5);
            } else {
                int radius = OtherUtils.clamp(item.getRadius() + value, 0, Integer.MAX_VALUE);
                item.setRadius(radius);
            }
        });
    }
    
    @Override
    protected void buildMenu(Player player) {
        addGlassBorder(GlassColor.GRAY);
        
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
        
        setBackItem(PREVIOUS_SLOT);
        setNextItem(NEXT_SLOT);
        
        setItem(RESET_EFFECT_LIST_SLOT, resetEffectsStack, e -> {
            item.getEffects().clear();
            openToPlayer(player);
        });
        setItem(ADD_EFFECT_SLOT, addEffectStack, e -> {
            EffectReward reward = (EffectReward) RewardType.effect.createReward(item.getOutcome());
            EffectMenu menu = new EffectMenu(false);
            menu.setItemToEdit(reward);
            menu.setOnBack(this::openToPlayer);
            menu.setOnNext((p, effectReward) -> {
                PotionEffectType effectType = effectReward.getPotionEffect().getPotionEffectType();
                int effectDuration = effectReward.getDuration();
                int effectAmplifier = effectReward.getAmplifier();
                boolean isAmbient = effectReward.isAmbient();
                boolean showParticles = effectReward.isShowParticles();
                
                PotionEffect potionEffect = new PotionEffect(effectType, effectDuration, effectAmplifier, isAmbient, showParticles);
                item.getEffects().add(potionEffect);
                openToPlayer(player);
            });
            menu.openToPlayer(player);
        });
        
        setItem(EFFECT_LIST_SLOT, effectListStack);
        
        ItemStack[] resetItems = {timeStack, radiusStack};
        
        for(int row=0; row<squidExplosionRewardPropertyList.size(); row++) {
            int slot;
            int[] values;
            if(row == 0) {
                slot = RESET_TIME_SLOT - 2;
                values = VALUES;
            } else {
                slot = RESET_RADIUS_SLOT - 3;
                values = VALUES2;
            }
            
            for(int value : values) {
                ItemStack stack = value == 0 ? resetItems[row] : GUIItem.getPlusLessItem(value);
                Consumer<Integer> propertyModifier = squidExplosionRewardPropertyList.get(row);
                setItem(slot++, stack, e -> {
                    propertyModifier.accept(value);
                    openToPlayer(e.getPlayer());
                });
            }
        }
    }
}
