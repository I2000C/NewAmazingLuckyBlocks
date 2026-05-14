package me.i2000c.newalb.lucky_blocks.editors.menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.CustomColor;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class ColorMenu extends EditorMenu<CustomColor> {
    
    public ColorMenu() {
        super("&6&lColor menu", MenuSize.SIZE_6_ROWS, true);
    }

    private static final int COLOR_ITEM_SLOTS[] = {10, 11, 12, 13,
                                                   19, 20, 21, 22,
                                                   28, 29, 30, 31,
                                                   37, 38, 39, 40};
    
    private static final String[] COLOR_NAMES = {
        "BLACK",
        "RED",
        "DARK GREEN",
        "BROWN",
        "DARK BLUE",
        "PURPLE",
        "CYAN",
        "LIGHT GREY",
        "DARK GREY",
        "PINK",
        "LIGHT GREEN",
        "YELLOW",
        "LIGHT BLUE",
        "MAGENTA",
        "ORANGE",
        "WHITE"
    };
    
    private static final String[] COLOR_HEX_VALUES = {
        "000000",
        "FF0000",
        "006622",
        "663300",
        "0000CC",
        "8000FF",
        "009999",
        "A6A6A6",
        "6B6B6B",
        "FF99FF",
        "33CC33",
        "FFFF00",
        "80CCFF",
        "FF00FF",
        "FF8000",
        "FFFFFF"
    };
    
    private static final XMaterial[] COLOR_MATERIALS = {
        XMaterial.INK_SAC,
        XMaterial.RED_DYE,
        XMaterial.GREEN_DYE,
        XMaterial.COCOA_BEANS,
        XMaterial.LAPIS_LAZULI,
        XMaterial.PURPLE_DYE,
        XMaterial.CYAN_DYE,
        XMaterial.LIGHT_GRAY_DYE,
        XMaterial.GRAY_DYE,
        XMaterial.PINK_DYE,
        XMaterial.LIME_DYE,
        XMaterial.YELLOW_DYE,
        XMaterial.LIGHT_BLUE_DYE,
        XMaterial.MAGENTA_DYE,
        XMaterial.ORANGE_DYE,
        XMaterial.BONE_MEAL
    };
    
    @Override
    protected CustomColor createNewItem() {
        return null;
    }
    
    @Override
    protected void buildMenu(Player player) {
        addGlassBorder(GlassColor.CYAN);
        
        for(int i=0; i<COLOR_ITEM_SLOTS.length; i++) {
            String colorHexValue = COLOR_HEX_VALUES[i];
            ItemStack sk = getColorItemStackFromDurability(i);
            setItem(COLOR_ITEM_SLOTS[i], sk, e -> {
                item = new CustomColor(colorHexValue);
                openToPlayer(player);
            });
        }
        
        ItemStackWrapper wrapper = ItemStackWrapper.newItem(XMaterial.LEATHER_CHESTPLATE);
        if(item == null) {
            wrapper.setDisplayName("&dChosen color: &b" + "null");
        } else {
            wrapper.setDisplayName("&dChosen color: &b" + item);
            wrapper.setColor(item.getBukkitColor());
        }
        ItemStack leather = wrapper.toItemStack();
        
        ItemStack chooseCustomColor = ItemStackWrapper.newItem(XMaterial.OAK_SIGN)
                                                      .setDisplayName("&3Choose custom color")
                                                      .toItemStack();
        
        ItemStack chooseRandomColor = ItemStackWrapper.newItem(XMaterial.BOOKSHELF)
                                                      .setDisplayName("&3Choose random color")
                                                      .toItemStack();
        
        setItem(16, leather);
        setItem(15, chooseCustomColor, e -> {
            player.closeInventory();
            Logger.sendMessage("&bWrite an hex color string in the chat using the format AABBCC", player);
            Logger.sendMessage("&bOr use &7/alb return &bif you don't know any valid color,", player);
            ChatListener.registerPlayer(player, message -> {
                try {
                    item = new CustomColor(message);
                    ChatListener.removePlayer(player);
                    openToPlayer(player);
                } catch(Exception ex) {
                    Logger.sendMessage("&cInvalid color string: &b" + message, player);
                    Logger.sendMessage("&bIf you don't know any valid color, use &7/alb return", player);
                }
            }, false);
        });
        setItem(25, chooseRandomColor, e -> {
            item = new CustomColor();
            openToPlayer(player);
        });
        setBackItem(42);
        setNextItem(43);
    }
    
    // 0 <= i <= 15
    private static ItemStack getColorItemStackFromDurability(int i) {
        if(i<0 || i>15) {
            return null;
        }
        
        return ItemStackWrapper.newItem(COLOR_MATERIALS[i])
                               .setDisplayName("&d" + COLOR_NAMES[i] + ": &b" + COLOR_HEX_VALUES[i])
                               .toItemStack();
    }
}
