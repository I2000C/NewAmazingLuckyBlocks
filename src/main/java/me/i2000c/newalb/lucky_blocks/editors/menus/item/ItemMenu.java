package me.i2000c.newalb.lucky_blocks.editors.menus.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.lucky_blocks.rewards.types.ItemReward;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.misc.OtherUtils;

public class ItemMenu extends EditorMenu<ItemReward> {
    public ItemMenu() {
        super("&b&lItemReward", MenuSize.SIZE_3_ROWS, true);
    }
    
    @Override
    protected void buildMenu(Player player) {
        addGlassBorder(GlassColor.CYAN);
        ItemStack selectFromInventoryItem = ItemStackWrapper.newItem(XMaterial.BRICKS)
                                                            .setDisplayName("&7Select an item from your inventory")
                                                            .toItemStack();
        
        ItemStackWrapper wrapper = ItemStackWrapper.newItem(XMaterial.BLACK_STAINED_GLASS_PANE);
        if(item.getItem() == null) {
            wrapper.setDisplayName("&bAmount: &r?");
        } else {
            wrapper.setDisplayName("&bAmount: &r" + item.getItem().getAmount());
        }
        ItemStack amountItem = wrapper.toItemStack();
        
        ItemStack creative = ItemStackWrapper.newItem(XMaterial.CRAFTING_TABLE)
                                             .setDisplayName("&3Close menu to pick items from creative mode")
                                             .toItemStack();

        setBackItem(10);
        setItem(11, selectFromInventoryItem);
        
        setItem(12, creative, e -> {
            player.closeInventory();
            Logger.sendMessage("&6Use &b/alb return &6to return to the menu", player);
        });
        
        setItem(13, GUIItem.getPlusLessItem(-1), e -> {
            if(item.getItem() != null) {
                int amount = OtherUtils.subInRange(item.getItem().getAmount(), 1, 1, 65);
                item.getItem().setAmount(amount);
                openToPlayer(player);
            }
        });
        
        if(item.getItem() == null) {
            setItem(14, amountItem);
        } else {
            setItem(14, item.getItem());
        }
        
        setItem(15, GUIItem.getPlusLessItem(+1), e -> {
            if(item.getItem() != null) {
                int amount = OtherUtils.addInRange(item.getItem().getAmount(), 1, 1, 65);
                item.getItem().setAmount(amount);
                openToPlayer(player);
            }
        });
        
        setNextItem(16, e -> {
            ItemMenu2 menu = new ItemMenu2();
            menu.setOnBack(this::openToPlayer);
            menu.setOnNext(this::onNext);
            menu.setItemToEdit(item);
            menu.openToPlayer(player);
        });
    }
    
    @Override
    public void onClickDefault(MenuClickEvent event) {
        if(event.isBottomInventory() && event.getClickedInventory().getType() == InventoryType.PLAYER) {
            if(!event.isEmptyItem()) {
                item.setItem(event.getCurrentItem().clone());
                openToPlayer(event.getPlayer());
            }
        }
    }
}
