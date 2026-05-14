package me.i2000c.newalb.lucky_blocks.editors.menus.enchantment;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.EnchantmentWithLevel;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class EnchantmentMenu extends EditorMenu<EnchantmentWithLevel> {
    
    public EnchantmentMenu() {
        super("&d&lEnchantments Menu", MenuSize.SIZE_3_ROWS, true);
    }
    
    @Override
    protected EnchantmentWithLevel createNewItem() {
        return new EnchantmentWithLevel(null, 1);
    }
    
    @Override
    protected void buildMenu(Player player) {
        addGlassBorder(GlassColor.PURPLE);
        
        ItemStackWrapper builder = ItemStackWrapper.newItem(XMaterial.ENCHANTED_BOOK);
        if(item.enchantment == null) {
            builder.setDisplayName("&5Select enchantment");
        } else {
            builder.setDisplayName("&5Selected enchantment: &b" + item.enchantment.name());
            if(item.level > 0) {
                builder.addEnchantment(item.enchantment, item.level);
            } else {
                builder.addEnchantment(item.enchantment, 1);
            }
        }
        ItemStack enchantmentItem = builder.toItemStack();
        
        ItemStack levelItem = ItemStackWrapper.newItem(XMaterial.EXPERIENCE_BOTTLE)
                                          .setAmount(item.level)
                                          .setDisplayName("&aSelected level: &b" + item.level)
                                          .addLoreLine("&3Click to select")
                                          .toItemStack();
        
        setBackItem(10);
        
        setItem(12, enchantmentItem, e -> {
            EnchantmentSelectMenu menu = new EnchantmentSelectMenu();
            menu.setOnBack(this::openToPlayer);
            menu.setOnNext(((p, enchantment) -> {
                item.enchantment = enchantment;
                openToPlayer(p);
            }));
            menu.openToPlayer(player);
        });
        setItem(14, levelItem, e -> {
            player.closeInventory();
            Logger.sendMessage("&bWrite the enchantment level in the chat", player);
            Logger.sendMessage("&bOr use &7/alb return &bif you want to return", player);
            ChatListener.registerPlayer(player, message -> {
                try {
                    int level = Integer.parseInt(message);
                    if(level <= 0) {
                        throw new NumberFormatException();
                    }
                    
                    item.level = level;
                    ChatListener.removePlayer(player);
                    openToPlayer(player);
                } catch(NumberFormatException ex) {
                    Logger.sendMessage("&cInvalid enchantment level: &b" + message, player);
                    Logger.sendMessage("&bIf you want to return, use &7/alb return", player);
                }                    
            }, false);
        });
        
        setItem(5, GUIItem.getPlusLessItem(+1), e -> {
            item.level++;
            openToPlayer(player);
        });
        setItem(23, GUIItem.getPlusLessItem(-1), e -> {
            item.level--;
            if(item.level < 1) {
                item.level = 1;
            }
            openToPlayer(player);
        });
        
        setNextItem(16);
    }
}
