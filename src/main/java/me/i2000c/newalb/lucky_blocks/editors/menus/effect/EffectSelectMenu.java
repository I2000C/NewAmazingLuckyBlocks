package me.i2000c.newalb.lucky_blocks.editors.menus.effect;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;

import me.i2000c.newalb.api.gui.MenuItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.PaginatedEditorMenu;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.lucky_blocks.rewards.types.EffectReward;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class EffectSelectMenu extends PaginatedEditorMenu<XPotion, XPotion> {
    
    private final boolean showClearEffectsItem;
    
    public EffectSelectMenu(boolean showClearEffectsItem) {
        super("&d&lEffect List", MenuSize.SIZE_6_ROWS, true, MenuSize.SIZE_5_ROWS);
        this.showClearEffectsItem = showClearEffectsItem;
    }
    
    @Override
    protected XPotion createNewItem() {
        return null;
    }

    @Override
    public List<XPotion> getItemList() {
        List<XPotion> effects = new ArrayList<>();
        if(showClearEffectsItem) {
            effects.add(null);
        }
        
        XPotion.REGISTRY.getValues()
                        .stream()
                        .filter(XPotion::isSupported)
                        .sorted(Comparator.comparing(XPotion::name))
                        .forEach(effects::add);
        return effects;
    }
    
    @Override
    public MenuItem mapItemToPage(XPotion effect, int index) {
        ItemStackWrapper wrapper;
        Consumer<MenuClickEvent> action;
        if(effect == null) {
            wrapper = ItemStackWrapper.newItem(XMaterial.MILK_BUCKET)
                                      .setDisplayName("&d" + EffectReward.CLEAR_EFFECTS_TAG);
            action = e -> onNext(e.getPlayer(), null);
        } else {
            PotionEffect potionEffect = new PotionEffect(effect.getPotionEffectType(), 0, 0);
            wrapper = ItemStackWrapper.newItem(XMaterial.POTION)
                                      .setDisplayName("&d" + effect.name())
                                      .addPotionEffect(potionEffect);
            action = e -> onNext(e.getPlayer(), effect);
        }
        
        MenuItem menuItem = new MenuItem(wrapper, action);
        return menuItem;
    }

    @Override
    protected void buildMenu(Player player) {
        setBackItem(45);
    }
}
