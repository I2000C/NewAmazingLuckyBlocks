package me.i2000c.newalb.lucky_blocks.editors.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.utils.locations.Offset;
import me.i2000c.newalb.utils.locations.Range;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class OffsetMenu extends EditorMenu<Offset> {
    
    private static final int VALUES[] = {-100, -10, -1, 0, +1, +10, +100};
    
    private final List<Consumer<Integer>> offsetProperyList = new ArrayList<>();
    
    public OffsetMenu() {
        super("&dOffset menu", MenuSize.SIZE_6_ROWS, true);
        
        offsetProperyList.add(value -> addToRangeMin(item.getOffsetX(), value));
        offsetProperyList.add(value -> addToRangeMax(item.getOffsetX(), value));
        offsetProperyList.add(value -> addToRangeMin(item.getOffsetY(), value));
        offsetProperyList.add(value -> addToRangeMax(item.getOffsetY(), value));
        offsetProperyList.add(value -> addToRangeMin(item.getOffsetZ(), value));
        offsetProperyList.add(value -> addToRangeMax(item.getOffsetZ(), value));
    }
    
    @Override
    protected Offset createNewItem() {
        return new Offset();
    }

    @Override
    protected void buildMenu(Player player) {
        ItemStack minX = ItemStackWrapper.newItem(XMaterial.GRASS_BLOCK)
                                         .setDisplayName("&cMin X: &6" + item.getOffsetX().getMin())
                                         .addLoreLine("&3Click to reset")
                                         .toItemStack();
        
        ItemStack maxX = ItemStackWrapper.newItem(XMaterial.GRASS_BLOCK)
                                         .setDisplayName("&cMax X: &6" + item.getOffsetX().getMax())
                                         .addLoreLine("&3Click to reset")
                                         .toItemStack();
        
        ItemStack minY = ItemStackWrapper.newItem(XMaterial.DIRT)
                                         .setDisplayName("&aMin Y: &6" + item.getOffsetY().getMin())
                                         .addLoreLine("&3Click to reset")
                                         .toItemStack();
        
        ItemStack maxY = ItemStackWrapper.newItem(XMaterial.DIRT)
                                         .setDisplayName("&aMax Y: &6" + item.getOffsetY().getMax())
                                         .addLoreLine("&3Click to reset")
                                         .toItemStack();        
        
        ItemStack minZ = ItemStackWrapper.newItem(XMaterial.STONE)
                                         .setDisplayName("&bMin Z: &6" + item.getOffsetZ().getMin())
                                         .addLoreLine("&3Click to reset")
                                         .toItemStack();
        
        ItemStack maxZ = ItemStackWrapper.newItem(XMaterial.STONE)
                                         .setDisplayName("&bMax Z: &6" + item.getOffsetZ().getMax())
                                         .addLoreLine("&3Click to reset")
                                         .toItemStack();
        
        ItemStack[] resetItems = {minX, maxX, minY, maxY, minZ, maxZ};
        
        for(int row=0; row<offsetProperyList.size(); row++) {
            int slot = row * 9 + 1;
            for(int value : VALUES) {
            	ItemStack stack = value == 0 ? resetItems[row] : GUIItem.getPlusLessItem(value);
            	Consumer<Integer> propertyModifier = offsetProperyList.get(row);
            	setItem(slot++, stack, e -> {
            		propertyModifier.accept(value);
            		openToPlayer(e.getPlayer());
            	});
            }
        }
        
        setBackItem(18);
        setNextItem(26);
    }
    
    private static void addToRangeMin(Range range, int value) {
        if(value == 0) {
            value = -range.getMin();
        }
        
        if(!range.setMin(range.getMin() + value)) {
            range.setMin(range.getMax());
        }
    }
    
    private static void addToRangeMax(Range range, int value) {
        if(value == 0) {
            value = -range.getMax();
        }
        
        if(!range.setMax(range.getMax() + value)) {
            range.setMax(range.getMin());
        }
    }
}
