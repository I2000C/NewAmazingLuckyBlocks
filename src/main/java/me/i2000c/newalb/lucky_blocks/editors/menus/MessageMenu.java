package me.i2000c.newalb.lucky_blocks.editors.menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.lucky_blocks.rewards.types.MessageReward;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class MessageMenu extends EditorMenu<MessageReward> {
    
    public MessageMenu() {
        super("&7&lMessage Reward", MenuSize.SIZE_3_ROWS, true);
    }
    
    @Override
    protected void buildMenu(Player player) {
        addGlassBorder(GlassColor.CYAN);
        
        ItemStack titleItem = ItemStackWrapper.newItem(XMaterial.BOOK)
                                              .setDisplayName("&bSelect title")
                                              .addLoreLine("&3Selected title: &r\"" + item.getTitle() + "&r\"")
                                              .addLoreLine("&eClick to change")
                                              .addLoreLine("")
                                              .addLoreLine("&7Use &a%player% &7if you want to use")
                                              .addLoreLine("&7  the player's name in the message,")
                                              .addLoreLine("&7&a%x%&7, &a%y%, &a%z% &7if you want")
                                              .addLoreLine("&7  to use the player's coordinates")
                                              .addLoreLine("&7or &a%bx%&7, &a%by%, &a%bz% &7if you want")
                                              .addLoreLine("&7  to use the LuckyBlock's coordinates")
                                              .toItemStack();
        
        ItemStack subtitleItem = ItemStackWrapper.newItem(XMaterial.BOOK)
                                                 .setDisplayName("&bSelect subtitle")
                                                 .addLoreLine("&3Selected subtitle: &r\"" + item.getSubtitle() + "&r\"")
                                                 .addLoreLine("&eClick to change")
                                                 .addLoreLine("")
                                                 .addLoreLine("&6The subtitle only is used")
                                                 .addLoreLine("&6  when message type is TITLE")
                                                 .addLoreLine("")
                                                 .addLoreLine("&7Use &a%player% &7if you want to use")
                                                 .addLoreLine("&7  the player's name in the message,")
                                                 .addLoreLine("&7&a%x%&7, &a%y%, &a%z% &7if you want")
                                                 .addLoreLine("&7  to use the player's coordinates")
                                                 .addLoreLine("&7or &a%bx%&7, &a%by%, &a%bz% &7if you want")
                                                 .addLoreLine("&7  to use the LuckyBlock's coordinates")
                                                 .toItemStack();
        
        ItemStackWrapper wrapper;
        switch(item.getMessageType()) {
            case TITLE:
                wrapper = ItemStackWrapper.newItem(XMaterial.PAINTING);
                break;
            case ACTION_BAR:
                wrapper = ItemStackWrapper.newItem(XMaterial.NAME_TAG);
                break;
            default:
            //case CHAT:
                wrapper = ItemStackWrapper.newItem(XMaterial.OAK_SIGN);
        }        
        wrapper.setDisplayName("&aSelect message type");
        wrapper.setLore("&3Selected message type: &5" + item.getMessageType().name());
        ItemStack typeItem = wrapper.toItemStack();
        
        ItemStack deleteTitle = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                                .setDisplayName("&cRemove title")
                                                .toItemStack();
        
        ItemStack deleteSubtitle = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                                   .setDisplayName("&cRemove subtitle")
                                                   .toItemStack();
        
        setBackItem(10);
        setNextItem(16);
        
        setItem(12, titleItem, e -> {
            ChatListener.registerPlayer(player, message -> {
                item.setTitle(message);
                openToPlayer(player);
            });
            player.closeInventory();
            Logger.sendMessage("&3Write the title in the chat and press ENTER", player);
        });
        setItem(13, subtitleItem, e -> {
            ChatListener.registerPlayer(player, message -> {
                item.setSubtitle(message);
                openToPlayer(player);
            });
            player.closeInventory();
            Logger.sendMessage("&3Write the subtitle in the chat and press ENTER", player);
        });
        setItem(14, typeItem, e -> {
            item.setMessageType(item.getMessageType().next());
            openToPlayer(player);
        });
        
        setItem(21, deleteTitle, e -> {
            item.setTitle("");
            openToPlayer(player);
        });
        setItem(22, deleteSubtitle, e -> {
            item.setSubtitle("");
            openToPlayer(player);
        });
    }
}
