package me.i2000c.newalb.lucky_blocks.editors.menus.nbt;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import de.tr7zw.changeme.nbtapi.NBTType;
import lombok.Data;
import lombok.NonNull;
import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class ItemNbtTagMenu extends EditorMenu<ItemNbtTagMenu.Tag> {
	
    public ItemNbtTagMenu() {
		super("&e&lEdit NBT tag", MenuSize.SIZE_3_ROWS, true);
	}

	private static final int SELECT_NAME_SLOT = 11;
    private static final int SELECT_TYPE_SLOT = 13;
    private static final int SELECT_VALUE_SLOT = 15;
    
    private static final int BACK_SLOT = 9;
    private static final int NEXT_SLOT = 17;
    
    @Override
    protected ItemNbtTagMenu.Tag createNewItem() {
    	setTitle("&a&lCreate NBT tag");
    	return new Tag();
    }
    
    @Override
    protected void buildMenu(Player player) {
        addGlassBorder(GlassColor.ORANGE, true);
        
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
        
        setItem(SELECT_NAME_SLOT, selectName, e -> {
        	player.closeInventory();
            Logger.sendMessage("&aEnter the name of the tag in the chat and press ENTER.", player, false);
            Logger.sendMessage("&aIf you don't want to change the name, use &b/alb return", player, false);
            ChatListener.registerPlayer(player, message -> {
                item.name = message;
                openToPlayer(player);
            });
        });
        
        setItem(SELECT_TYPE_SLOT, selectType, e -> {
        	ItemNbtTypeMenu menu = new ItemNbtTypeMenu();
        	menu.setItemToEdit(item.type);
        	menu.setOnBack(this::openToPlayer);
        	menu.setOnNext((p, type) -> {
        		item.setType(type);
        		item.value = null;
        		openToPlayer(player);
        	});
        	menu.openToPlayer(player);
        });
        
        if(item.type != NBTType.NBTTagEnd && item.type != NBTType.NBTTagCompound) {
            setItem(SELECT_VALUE_SLOT, selectValue, e -> {
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
                        openToPlayer(player);
                    } catch(IllegalArgumentException ex) {
                        Logger.sendMessage("&c" + ex.getMessage(), player, false);
                    }
                }, false);
            });
        }
        
        setBackItem(BACK_SLOT);
        setNextItem(NEXT_SLOT, e -> {
        	if(item.name != null && item.type != NBTType.NBTTagEnd) {
                if(item.type == NBTType.NBTTagCompound || item.value != null) {
                    // Go to next menu
                    onNext(player, item);
                }
            }
        });
    }
    
    @Data
    public static class Tag {
        private String name;
        @NonNull private NBTType type = NBTType.NBTTagEnd;
        private Object value;
    }
}
