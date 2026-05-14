package me.i2000c.newalb.lucky_blocks.editors.menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.lucky_blocks.rewards.types.CommandReward;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class CommandMenu extends EditorMenu<CommandReward> {
    
    public CommandMenu() {
        super("&7&lCommand Reward", MenuSize.SIZE_3_ROWS, true);
    }
    
    @Override
    protected void buildMenu(Player player) {
        addGlassBorder(GlassColor.CYAN);
        
        ItemStackWrapper builder = ItemStackWrapper.newItem(XMaterial.OAK_SIGN);
        if(item.getCommand() == null) {
            builder.setDisplayName("&6Command selected: &cnull");
        } else {
            builder.setDisplayName("&6Command selected: &r/" + item.getCommand());
        }
        builder.addLoreLine("&7Click here and then, write the command in the chat");
        builder.addLoreLine("");
        builder.addLoreLine("&7Use &a%player% &7if you want to use");
        builder.addLoreLine("&7  the player's name in the command,");
        builder.addLoreLine("&7&a%x%&7, &a%y%, &a%z% &7if you want");
        builder.addLoreLine("&7  to use the player's coordinates");
        builder.addLoreLine("&7or &a%bx%&7, &a%by%, &a%bz% &7if you want");
        builder.addLoreLine("&7  to use the LuckyBlock's coordinates.");
        builder.addLoreLine("&7You can use &a%world% &7to get the player's world too");
        ItemStack cmdItem = builder.toItemStack();
                
        if(item.isSendFromPlayer()) {
            builder = ItemStackWrapper.newItem(XMaterial.PLAYER_HEAD);
            builder.setDisplayName("&dSender: &2Player");
        } else {
            builder = ItemStackWrapper.newItem(XMaterial.COMMAND_BLOCK);
            builder.setDisplayName("&dSender: &8Console");
        }
        builder.addLoreLine("&3Click to toggle");
        ItemStack senderItem = builder.toItemStack();
        
        setItem(11, senderItem, e -> {
            item.setSendFromPlayer(!item.isSendFromPlayer());
            openToPlayer(player);
        });
        setItem(13, cmdItem, e -> {
            ChatListener.registerPlayer(player, message -> {
                item.setCommand(message);
                openToPlayer(player);
            });
            player.closeInventory();
        });
        setBackItem(10);
        setNextItem(16);
    }
}
