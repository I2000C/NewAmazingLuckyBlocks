package me.i2000c.newalb.custom_outcomes.menus.nbt;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBTType;
import lombok.Data;
import lombok.NonNull;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.editor.EditorType;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemNbtTagMenu extends Editor<ItemNbtTagMenu.Tag> {
    
    public ItemNbtTagMenu() {
        InventoryListener.registerInventory(CustomInventoryType.ITEM_NBT_TAG_MENU, ITEM_NBT_TAG_MENU_FUNCTION);
    }
    
    private static final int SELECT_NAME_SLOT = 11;
    private static final int SELECT_TYPE_SLOT = 13;
    private static final int SELECT_VALUE_SLOT = 15;
    
    private static final int BACK_SLOT = 9;
    private static final int NEXT_SLOT = 17;
    
    private String menuTitle;
    
    @Override
    public void newItem(Player player) {
        this.item = new Tag();
        this.menuTitle = "&a&lCreate NBT tag";
        openNbtTagEditorMenu(player);
    }
    
    @Override
    public void editItem(Player player) {
        this.menuTitle = "&e&lEdit NBT tag";
        openNbtTagEditorMenu(player);
    }
    
    private void openNbtTagEditorMenu(Player player) {
        Menu menu = GUIFactory.newMenu(CustomInventoryType.ITEM_NBT_TAG_MENU, 27, this.menuTitle);
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.ORANGE);
        for(int i=0; i<9; i++) {
            menu.setItem(i, glass);
        }
        for(int i=18; i<27; i++) {
            menu.setItem(i, glass);
        }
        
        ItemStackWrapper wrapper = ItemStackWrapper.newItem(XMaterial.OAK_SIGN)
                                                   .setDisplayName("&bSelect tag name")
                                                   .addLoreLine("");
        if(item.name == null) wrapper.addLoreLine("&3Current tag name: &cnull");
        else wrapper.addLoreLine("&3Current tag name: &a" + item.name);
        ItemStack selectName = wrapper.toItemStack();
        
        wrapper = ItemStackWrapper.newItem(XMaterial.NAME_TAG)
                                  .setDisplayName("&6Select tag type")
                                  .addLoreLine("");
        if(item.type == NBTType.NBTTagEnd) wrapper.addLoreLine("&3Current tag type: &cnull");
        else wrapper.addLoreLine("&3Current tag type: &d" + ItemNbtMenu.getTypeName(item.type));
        ItemStack selectType = wrapper.toItemStack();
        
        wrapper = ItemStackWrapper.newItem(XMaterial.CHEST)
                                  .setDisplayName("&bSelect tag value")
                                  .addLoreLine("");
        if(item.value == null) wrapper.addLoreLine("&3Current tag value: &cnull");
        else wrapper.addLoreLine("&3Current tag value &d" + ItemNbtMenu.getValueAsString(item.type, item.value));
        ItemStack selectValue = wrapper.toItemStack();
        
        menu.setItem(SELECT_NAME_SLOT, selectName);
        menu.setItem(SELECT_TYPE_SLOT, selectType);
        if(item.type != NBTType.NBTTagEnd && item.type != NBTType.NBTTagCompound) {
            menu.setItem(SELECT_VALUE_SLOT, selectValue);
        }
        
        menu.setItem(BACK_SLOT, GUIItem.getBackItem());
        menu.setItem(NEXT_SLOT, GUIItem.getNextItem());
        
        menu.openToPlayer(player);
    }
    
    private final InventoryFunction ITEM_NBT_TAG_MENU_FUNCTION = e -> {
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        switch(e.getSlot()) {
            case BACK_SLOT:
                // Go to previous menu
                onBack.accept(player);
                break;
            case NEXT_SLOT:
                if(item.name != null && item.type != NBTType.NBTTagEnd) {
                    if(item.type != NBTType.NBTTagCompound && item.value == null) {
                        break;
                    }
                    
                    // Go to next menu
                    onNext.accept(player, item);
                }
                break;
            case SELECT_NAME_SLOT:
                player.closeInventory();
                Logger.sendMessage("&aEnter the name of the tag in the chat and press ENTER.", player, false);
                Logger.sendMessage("&aIf you don't want to change the name, use &b/alb return", player, false);
                ChatListener.registerPlayer(player, message -> {
                    item.name = message;
                    openNbtTagEditorMenu(player);
                });
                break;
            case SELECT_TYPE_SLOT:
                Editor<NBTType> editor = EditorType.ITEM_NBT_TYPE.getEditor();
                editor.editExistingItem(item.type,
                                        player,
                                        this::openNbtTagEditorMenu,
                                        (p, type) -> {
                                            item.setType(type);
                                            item.value = null;
                                            openNbtTagEditorMenu(p);
                                        });
                break;
            case SELECT_VALUE_SLOT:
                if(item.type != NBTType.NBTTagEnd && item.type != NBTType.NBTTagCompound) {
                    player.closeInventory();
                    Logger.sendMessage("&aEnter the value of the tag in the chat and press ENTER.", player, false);
                    Logger.sendMessage("&aIf you don't want to change the value, use &b/alb return", player, false);
                    if(item.value == null) {
                        Logger.sendMessage("&3Current value: &cnull", player, false);
                    } else {
                        Logger.sendMessage("&3Current value: &d" + ItemNbtMenu.getValueAsString(item.type, item.value), player, false);
                    }
                    
                    ChatListener.registerPlayer(player, message -> {
                        try {
                            item.value = ItemNbtMenu.parseValue(message, item.type);
                            ChatListener.removePlayer(player);
                            openNbtTagEditorMenu(player);
                        } catch(IllegalArgumentException ex) {
                            Logger.sendMessage("&c" + ex.getMessage(), player, false);
                        }
                    }, false);
                }
                break;
        }
    };
    
    @Data
    public static class Tag {
        private String name;
        @NonNull private NBTType type = NBTType.NBTTagEnd;
        private Object value;
    }
}
