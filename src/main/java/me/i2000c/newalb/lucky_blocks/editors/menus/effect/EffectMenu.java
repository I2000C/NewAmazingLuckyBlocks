package me.i2000c.newalb.lucky_blocks.editors.menus.effect;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.lucky_blocks.rewards.types.EffectReward;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.misc.OtherUtils;

public class EffectMenu extends EditorMenu<EffectReward> {
    
    private static final int[] VALUES = {-100, -10, -1, 0, +1, +10, +100};
    
    private final List<Consumer<Integer>> effectPropertyList = new ArrayList<>();
    
    private final boolean showClearEffectsItem;
    
    public EffectMenu(boolean showClearEffectsItem) {
        super("&5&lEffect Menu", MenuSize.SIZE_5_ROWS, true);
        this.showClearEffectsItem = showClearEffectsItem;
        
        effectPropertyList.add(value -> {
            if(value == 0) {
                item.setDuration(30);
            } else {
                int duration = OtherUtils.clamp(item.getDuration() + value, -1, Integer.MAX_VALUE);
                item.setDuration(duration);
            }
        });
        
        effectPropertyList.add(value -> {
            if(value == 0) {
                item.setAmplifier(0);
            } else {
                int amplifier = OtherUtils.clamp(item.getAmplifier() + value, 0, 255);
                item.setAmplifier(amplifier);
            }
        });
    }
    
    @Override
    protected void buildMenu(Player player) {
        if(showClearEffectsItem) {
            setTitle("&5&lEffect Reward");
        }
        
        addGlassBorder(GlassColor.MAGENTA);
        
        ItemStackWrapper wrapper = ItemStackWrapper.newItem(XMaterial.CLOCK);
        if(item.getDuration() < 0) {
            wrapper.setDisplayName("&6Effect time (seconds): &ainfinite");
        } else {
            wrapper.setDisplayName("&6Effect time (seconds): &a" + item.getDuration());
        }
        wrapper.addLoreLine("&3Click to reset");
        ItemStack timeItem = wrapper.toItemStack();
        
        ItemStack amplifierItem = ItemStackWrapper.newItem(XMaterial.BEACON)
                                                  .setDisplayName("&6Effect amplifier: &a" + item.getAmplifier())
                                                  .addLoreLine("&3Click to reset")
                                                  .toItemStack();
        
        if(item.isClearEffects()) {
            wrapper = ItemStackWrapper.newItem(XMaterial.MILK_BUCKET);
            wrapper.setDisplayName("&bSelected effect: &d" + EffectReward.CLEAR_EFFECTS_TAG);
        } else {
            wrapper = ItemStackWrapper.newItem(XMaterial.POTION);
            if(item.getPotionEffect() == null) {
                wrapper.setDisplayName("&bSelected effect: &dnull");
            } else {
                wrapper.setDisplayName("&bSelected effect: &d" + item.getPotionEffect().name());
                wrapper.addPotionEffect(new PotionEffect(item.getPotionEffect().getPotionEffectType(), 0, 0));
            }
        }
        wrapper.addLoreLine("&3Click to select");
        ItemStack effectStack = wrapper.toItemStack();
        
        if(item.isAmbient()) {
            wrapper = ItemStackWrapper.newItem(XMaterial.GLASS_PANE);
            wrapper.setDisplayName("&bIs ambient: &atrue");
        } else {
            wrapper = ItemStackWrapper.newItem(XMaterial.GRAY_STAINED_GLASS_PANE);
            wrapper.setDisplayName("&bIs ambient: &cfalse");
        }
        wrapper.addLoreLine("");
        wrapper.addLoreLine("&6If is ambient is &atrue&6,");        
        wrapper.addLoreLine("  &6effect particles will be more transparent");        
        wrapper.addLoreLine("&6Note that this option only works if");        
        wrapper.addLoreLine("  &6show particles is &atrue");
        wrapper.addLoreLine("");
        wrapper.addLoreLine("&3Click to toggle");
        ItemStack ambientItem = wrapper.toItemStack();
                
        if(item.isShowParticles()) {
            wrapper = ItemStackWrapper.newItem(XMaterial.MELON_SEEDS);
            wrapper.setDisplayName("&bShow particles: &atrue");
        } else {
            wrapper = ItemStackWrapper.newItem(XMaterial.GLASS_PANE);
            wrapper.setDisplayName("&bShow particles: &cfalse");
        }
        wrapper.addLoreLine("&3Click to toggle");
        ItemStack showParticlesItem = wrapper.toItemStack();
        
        setBackItem(10);
        setItem(16, GUIItem.getNextItem(), e -> {
            if(item.isClearEffects() || item.getPotionEffect() != null) {
                onNext(player, item);
            }
        });
        
        setItem(13, effectStack, e -> {
            EffectSelectMenu menu = new EffectSelectMenu(showClearEffectsItem);
            menu.setItemToEdit(item.getPotionEffect());
            menu.setOnBack(this::openToPlayer);
            menu.setOnNext((p, effect) -> {
                item.setPotionEffect(effect);
                openToPlayer(player);
            });
            menu.openToPlayer(player);
        });
        
        if(!item.isClearEffects()) {
            setItem(12, ambientItem, e -> {
                item.setAmbient(!item.isAmbient());
                openToPlayer(player);
            });
            
            setItem(14, showParticlesItem, e -> {
                item.setShowParticles(!item.isShowParticles());
                openToPlayer(player);
            });
            
            ItemStack[] resetItems = {timeItem, amplifierItem};
            
            for(int row=0; row<effectPropertyList.size(); row++) {
                int slot = row * 9 + 19;
                for(int value : VALUES) {
                    ItemStack stack = value == 0 ? resetItems[row] : GUIItem.getPlusLessItem(value);
                    Consumer<Integer> propertyModifier = effectPropertyList.get(row);
                    setItem(slot++, stack, e -> {
                        propertyModifier.accept(value);
                        openToPlayer(e.getPlayer());
                    });
                }
            }
        }
    }
}
