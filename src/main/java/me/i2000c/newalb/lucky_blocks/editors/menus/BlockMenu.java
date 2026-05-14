package me.i2000c.newalb.lucky_blocks.editors.menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.lucky_blocks.rewards.types.BlockReward;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class BlockMenu extends EditorMenu<BlockReward> {
    
    public BlockMenu() {
        super("&d&lBlock Reward", MenuSize.SIZE_5_ROWS, true);
    }
    
    @SuppressWarnings("incomplete-switch")
    @Override
    protected void buildMenu(Player player) {
        addGlassBorder(GlassColor.PURPLE);
        
        ItemStack usePlayerLocStack = GUIItem.getUsePlayerLocItem(item.isUsePlayerLoc());

        ItemStack isFallingBlockStack = GUIItem.getBooleanItem(
                item.isFallingBlock(), 
                "&5Is falling block", 
                XMaterial.SAND, 
                XMaterial.COBBLESTONE);
        
        ItemStack offsetStack = item.getOffset().getItemToDisplay();
        
        ItemStackWrapper wrapper;
        if(item.getBlockMaterial() != null) {
            XMaterial blockMaterial = item.getBlockMaterial();
            switch(blockMaterial) {
                case WATER: blockMaterial = XMaterial.WATER_BUCKET; break;
                case LAVA:  blockMaterial = XMaterial.LAVA_BUCKET;  break;
                case FIRE:  blockMaterial = XMaterial.FIRE_CHARGE;  break;
            }
            wrapper = ItemStackWrapper.newItem(blockMaterial);
            wrapper.setDisplayName("&3Selected block");
        } else {
            wrapper = ItemStackWrapper.newItem(XMaterial.BLACK_STAINED_GLASS_PANE)
                                      .setDisplayName("&3Select a &6&lblock &3from your inventory")
                                      .addLoreLine("")
                                      .addLoreLine("&bNote:")
                                      .addLoreLine("&b  - You can select a &6water bucket &bto generate a &6water block")
                                      .addLoreLine("&b  - You can select a &6lava bucket &bto generate a &6lava block")
                                      .addLoreLine("&b  - You can select a &6fire charge &bto generate a &6fire block");
        }
        
        ItemStack blockItem = wrapper.toItemStack();

        setBackItem(10);
        setNextItem(16, e -> {
            if(item.getBlockMaterial() != null) {
                onNext(player, item);
            }
        });

        setItem(12, usePlayerLocStack, e -> {
            item.setUsePlayerLoc(!item.isUsePlayerLoc());
            openToPlayer(player);
        });
        
        setItem(13, isFallingBlockStack, e -> {
            item.setFallingBlock(!item.isFallingBlock());
            openToPlayer(player);
        });
        
        setItem(14, offsetStack, e -> {
            OffsetMenu menu = new OffsetMenu();
            menu.setItemToEdit(item.getOffset().clone());
            menu.setOnBack(this::openToPlayer);
            menu.setOnNext((p, offset) -> {
                item.setOffset(offset);
                openToPlayer(player);
            });
            menu.openToPlayer(player);
        });
        
        setItem(31, blockItem);
    }
    
    @SuppressWarnings("incomplete-switch")
    @Override
    protected void onClickDefault(MenuClickEvent event) {
        if(event.isBottomInventory() && !event.isEmptyItem()) {
            Player player = event.getPlayer();
            ItemStack stack = event.getCurrentItem();
            
            if(stack.getType().isBlock()) {
                item.setBlockMaterial(XMaterial.matchXMaterial(stack));
                openToPlayer(player);
            } else switch(XMaterial.matchXMaterial(stack.getType())) {
                case WATER_BUCKET:
                    item.setBlockMaterial(XMaterial.WATER);
                    openToPlayer(player);
                    break;
                case LAVA_BUCKET:
                    item.setBlockMaterial(XMaterial.LAVA);
                    openToPlayer(player);
                    break;
                case FIRE_CHARGE:
                    item.setBlockMaterial(XMaterial.FIRE);
                    openToPlayer(player);
                    break;
            }
        }
    }
}
