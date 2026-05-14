package me.i2000c.newalb.lucky_blocks.editors.menus;

import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.lucky_blocks.rewards.types.ExplosionReward;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.misc.OtherUtils;

public class ExplosionMenu extends EditorMenu<ExplosionReward> {
    
    private static final int[] VALUES = {-100, -10, -1, 0, +1, +10, +100};
    
    public ExplosionMenu() {
        super("&4&lExplosion Reward", MenuSize.SIZE_4_ROWS, true);
    }
    
    @Override
    protected void buildMenu(Player player) {
        addGlassBorder(GlassColor.ORANGE);
        
        ItemStack tntItem = ItemStackWrapper.newItem(XMaterial.TNT)
                                            .setDisplayName("&6Explosion power: &e" + item.getPower())
                                            .addLoreLine("&3Click to reset")
                                            .toItemStack();
        
        ItemStack fireItem = GUIItem.getBooleanItem(
                item.isWithFire(), 
                "&6Generate fire", 
                XMaterial.FIRE_CHARGE, 
                XMaterial.FIREWORK_STAR);
        
        ItemStack breakBlocksItem = GUIItem.getBooleanItem(
                item.isBreakBlocks(), 
                "&6Break blocks", 
                XMaterial.IRON_PICKAXE, 
                XMaterial.STONE);
        
        setBackItem(19);
        setNextItem(25);
        
        setItem(21, fireItem, e -> {
            item.setWithFire(!item.isWithFire());
            openToPlayer(player);
        });
        setItem(23, breakBlocksItem, e -> {
            item.setBreakBlocks(!item.isBreakBlocks());
            openToPlayer(player);
        });
        
        int slot = 10;
        for(int value : VALUES) {
            ItemStack stack;
            Consumer<Integer> propertyModifier;
            if(value == 0) {
                stack = tntItem;
                propertyModifier = v -> item.setPower(4);
            } else {
                stack = GUIItem.getPlusLessItem(value);
                propertyModifier = v -> item.setPower(OtherUtils.clamp(item.getPower() + v, 0, Integer.MAX_VALUE));
            }
            setItem(slot++, stack, e -> {
                propertyModifier.accept(value);
                openToPlayer(player);
            });
        }
    }
}
