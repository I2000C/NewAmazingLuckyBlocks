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
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.misc.OtherUtils;

public class DelayerMenu extends EditorMenu<Integer> {
    
    private static final int[] VALUES = {+1, +10, +100, -1, -10, -100};
    
    private final List<Consumer<Integer>> delayerPropertyList = new ArrayList<>();
    
    public DelayerMenu() {
        super("&5Configure Delay", MenuSize.SIZE_3_ROWS, true);
        
        delayerPropertyList.add(value -> {
            if(value == 0) {
                item = 0;
            } else {
                item = OtherUtils.clamp(item + value, 0, Integer.MAX_VALUE);
            }
        });
    }
    
    @Override
    protected Integer createNewItem() {
        return 0;
    }
    
    @Override
    protected void buildMenu(Player player) {
        addGlassBorder(GlassColor.BLUE);
        
        ItemStack delay = ItemStackWrapper.newItem(XMaterial.CLOCK)
                                          .setDisplayName("&6Delay: &b" + item + " &dtick(s)")
                                          .addLoreLine("        &a" + item/20.0 + " &dsecond(s)")
                                          .addLoreLine("&3Click to reset")
                                          .toItemStack();
        
        setBackItem(10);
        setNextItem(16);
        
        setItem(13, delay, e -> {
            item = 0;
            openToPlayer(player);
        });
        
        int slot = 3;
        for(int value : VALUES) {
            setItem(slot++, GUIItem.getPlusLessItem(value), e -> {
                delayerPropertyList.get(0).accept(value);
                openToPlayer(player);
            });
            
            if(slot == 6) {
                // +1, +10 and +100 items go in slots 3, 4 and 5
                // -1, -10 and -100 items go in slots 21, 22 and 23
                slot = 21;
            }
        }
    };
}
