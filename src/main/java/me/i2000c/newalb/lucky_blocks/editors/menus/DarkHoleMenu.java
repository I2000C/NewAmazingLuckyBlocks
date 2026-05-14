package me.i2000c.newalb.lucky_blocks.editors.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.listeners.interact.SpecialItems;
import me.i2000c.newalb.lucky_blocks.rewards.types.DarkHoleReward;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.misc.OtherUtils;

public class DarkHoleMenu extends EditorMenu<DarkHoleReward> {
    
    private static final int[] VALUES = {-10, -1, 0, +1, +10};
    private static final int[] VALUES2 = {-5, -1, 0, +1, +5};
    
    private final List<Consumer<Integer>> darkHolePropertyList = new ArrayList<>();
    
    public DarkHoleMenu() {
        super("&0&lDarkHole Reward", MenuSize.SIZE_5_ROWS, true);
        
        darkHolePropertyList.add(value -> {
            if(value == 0) {
                item.setDepth(SpecialItems.dark_hole.getDefaultDepth());
            } else {
                int depth = OtherUtils.clamp(item.getDepth() + value, -1, Integer.MAX_VALUE);
                item.setDepth(depth);
            }
        });
        darkHolePropertyList.add(value -> {
            if(value == 0) {
                item.setRadius(SpecialItems.dark_hole.getDefaultRadius());
            } else {
                int radius = OtherUtils.clamp(item.getRadius() + value, 1, Integer.MAX_VALUE);
                item.setRadius(radius);
            }
        });
        darkHolePropertyList.add(value -> {
            if(value == 0) {
                item.setTicks(SpecialItems.dark_hole.getDefaultTicks());
            } else {
                long ticks = OtherUtils.clamp(item.getTicks() + value, 0L, Long.MAX_VALUE);
                item.setTicks(ticks);
            }
        });
    }
    
    @Override
    protected void buildMenu(Player player) {
        addGlassBorder(GlassColor.GRAY);
        
        ItemStack squaredStack = GUIItem.getBooleanItem(item.isSquared(), "&bSquared", XMaterial.SNOW_BLOCK, XMaterial.SNOWBALL);
        
        ItemStackWrapper wrapper = ItemStackWrapper.newItem(XMaterial.BEDROCK);
        if(item.getDepth() < 0) {
            wrapper.setDisplayName("&3Depth: &binfinite");
        } else {
            wrapper.setDisplayName("&3Depth: &b" + item.getDepth());
        }
        ItemStack depthStack = wrapper.toItemStack();
        
        ItemStack radiusStack = ItemStackWrapper.newItem(XMaterial.HOPPER)
                                                .setDisplayName("&6Radius: &2" + item.getRadius())
                                                .addLoreLine("&3Click to reset")
                                                .toItemStack();
        
        ItemStack ticksStack = ItemStackWrapper.newItem(XMaterial.CLOCK)
                                               .setDisplayName("&5Ticks between blocks: &6" + item.getTicks())
                                               .addLoreLine("&3Click to reset")
                                               .toItemStack();
        
        ItemStack[] resetItems = {depthStack, radiusStack, ticksStack};
        
        setBackItem(18);
        setNextItem(26);
        
        setItem(24, squaredStack, e -> {
            item.setSquared(!item.isSquared());
            openToPlayer(player);
        });
        
        for(int row=0; row<darkHolePropertyList.size(); row++) {
            int slot = row * 9 + 10;
            int[] values = row == 1 ? VALUES2 : VALUES;
            for(int value : values) {
                ItemStack stack = value == 0 ? resetItems[row] : GUIItem.getPlusLessItem(value);
                Consumer<Integer> propertyModifier = darkHolePropertyList.get(row);
                setItem(slot++, stack, e -> {
                    propertyModifier.accept(value);
                    openToPlayer(player);
                });
            }
        }
    }
}
