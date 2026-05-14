package me.i2000c.newalb.lucky_blocks.editors.menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.lucky_blocks.rewards.types.LightningReward;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class LightningMenu extends EditorMenu<LightningReward> {
    
    public LightningMenu() {
        super("&e&lLightning Reward", MenuSize.SIZE_3_ROWS, true);
    }
    
    @Override
    protected void buildMenu(Player player) {
        addGlassBorder(GlassColor.WHITE);

        ItemStack usePlayerLocStack = GUIItem.getUsePlayerLocItem(item.isUsePlayerLoc());
        
        ItemStackWrapper builder;
        if(item.isCauseDamage()) {
            builder = ItemStackWrapper.newItem(XMaterial.LAVA_BUCKET);
            builder.setDisplayName("&cCause damage");
        } else {
            builder = ItemStackWrapper.newItem(XMaterial.WATER_BUCKET);
            builder.setDisplayName("&bDon't cause damage");            
        }
        ItemStack damagePlayerStack = builder.toItemStack();

        ItemStack offsetStack = item.getOffset().getItemToDisplay();
        
        setBackItem(10);
        setNextItem(16);

        setItem(12, usePlayerLocStack, e -> {
            item.setUsePlayerLoc(!item.isUsePlayerLoc());
            openToPlayer(player);
        });
        setItem(13, damagePlayerStack, e -> {
            item.setCauseDamage(!item.isCauseDamage());
            openToPlayer(player);
        });
        setItem(14, offsetStack, e -> {
            OffsetMenu menu = new OffsetMenu();
            menu.setItemToEdit(item.getOffset());
            menu.setOnBack(this::openToPlayer);
            menu.setOnNext((p, offset) -> {
                item.setOffset(offset);
                openToPlayer(player);
            });
            menu.openToPlayer(player);
        });
    }
}
