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
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.lucky_blocks.rewards.types.MiniVolcanoReward;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.misc.OtherUtils;

public class MiniVolcanoMenu extends EditorMenu<MiniVolcanoReward> {
    
    private static final int[] VALUES = {-10, -1, 0, +1, +10};
    
    private final List<Consumer<Integer>> miniVolcanoPropertyList = new ArrayList<>();
    
    public MiniVolcanoMenu() {
        super("&c&lMiniVolcano Reward", MenuSize.SIZE_5_ROWS, true);
        
        miniVolcanoPropertyList.add(value -> {
            if(value == 0) {
                item.setHeight(SpecialItems.mini_volcano.getDefaultHeight());
            } else {
                int height = OtherUtils.clamp(item.getHeight() + value, 1, Integer.MAX_VALUE);
                item.setHeight(height);
            }
        });
        miniVolcanoPropertyList.add(value -> {
            if(value == 0) {
                item.setTicks(SpecialItems.mini_volcano.getDefaultTicks());
            } else {
                long ticks = OtherUtils.clamp(item.getTicks() + value, 0L, Long.MAX_VALUE);
                item.setTicks(ticks);
            }
        });
    }
    
    @Override
    protected void buildMenu(Player player) {
        addGlassBorder(GlassColor.ORANGE);       
        
        ItemStack squaredStack = GUIItem.getBooleanItem(
                item.isSquared(), 
                "&bSquared", 
                XMaterial.SNOW_BLOCK, 
                XMaterial.SNOWBALL);
        
        ItemStack heightStack = ItemStackWrapper.newItem(XMaterial.LADDER)
                                                .setDisplayName("&3Height: &b" + item.getHeight())
                                                .addLoreLine("&2Click to reset")
                                                .toItemStack();
        
        ItemStack ticksStack = ItemStackWrapper.newItem(XMaterial.CLOCK)
                                               .setDisplayName("&5Ticks between blocks: &6" + item.getTicks())
                                               .addLoreLine("&3Click to reset")
                                               .toItemStack();
        
        ItemStackWrapper wrapper = ItemStackWrapper.newItem(item.getBaseMaterial());
        wrapper.setDisplayName("&6Base material: &b" + item.getBaseMaterial().name());
        wrapper.addLoreLine("&3Click on a &3&lblock &3of your inventory");
        wrapper.addLoreLine("&3to change it");
        ItemStack baseMaterialStack = wrapper.toItemStack();
        
        ItemStack lavaMaterialStack;
        switch(item.getLavaMaterial()) {
            case LAVA:  lavaMaterialStack = XMaterial.LAVA_BUCKET.parseItem(); break;
            case WATER: lavaMaterialStack = XMaterial.WATER_BUCKET.parseItem(); break;
            default:    lavaMaterialStack = item.getLavaMaterial().parseItem(); break;
        }
        ItemStackWrapper.fromItem(lavaMaterialStack, false)
                        .setDisplayName("&cLava material: &b" + item.getLavaMaterial().name())
                        .addLoreLine("&3Click to change");
        
        ItemStack throwBlocksStack = GUIItem.getBooleanItem(
                item.isThrowBlocks(), 
                "&cThrow blocks", 
                XMaterial.FIRE_CHARGE, 
                XMaterial.LAPIS_BLOCK);
        
        ItemStack[] resetItems = {heightStack, ticksStack};
        
        setBackItem(18);
        setNextItem(26);
        
        for(int row=0; row<miniVolcanoPropertyList.size(); row++) {
            int slot = row * 9 + 11;
            for(int value : VALUES) {
                ItemStack stack = value == 0 ? resetItems[row] : GUIItem.getPlusLessItem(value);
                Consumer<Integer> propertyModifier = miniVolcanoPropertyList.get(row);
                setItem(slot++, stack, e -> {
                    propertyModifier.accept(value);
                    openToPlayer(player);
                });
            }
        }
        
        setItem(29, baseMaterialStack);
        setItem(30, lavaMaterialStack, e -> {
            if(item.getLavaMaterial() == XMaterial.LAVA) {
                item.setLavaMaterial(XMaterial.WATER);
            } else {
                item.setLavaMaterial(XMaterial.LAVA);
            }
            openToPlayer(player);
        });
        
        setItem(32, squaredStack, e -> {
            item.setSquared(!item.isSquared());
            openToPlayer(player);
        });
        
        setItem(33, throwBlocksStack, e -> {
            item.setThrowBlocks(!item.isThrowBlocks());
            openToPlayer(player);
        });
    }
    
    @Override
    protected void onClickDefault(MenuClickEvent event) {
        if(event.isBottomInventory() && !event.isEmptyItem()) {
            ItemStack stack = event.getCurrentItem();
            if(stack.getType().isSolid()) {
                item.setBaseMaterial(XMaterial.matchXMaterial(stack));
                openToPlayer(event.getPlayer());
            }
        }
    }
}
