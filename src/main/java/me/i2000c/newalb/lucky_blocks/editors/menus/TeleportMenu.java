package me.i2000c.newalb.lucky_blocks.editors.menus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.lucky_blocks.rewards.TypeManager;
import me.i2000c.newalb.lucky_blocks.rewards.types.TeleportReward;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class TeleportMenu extends EditorMenu<TeleportReward> {
    
    public TeleportMenu() {
        super("&e&lTeleport Reward", MenuSize.SIZE_3_ROWS, true);
    }
    
    @Override
    protected void buildMenu(Player player) {
        addGlassBorder(GlassColor.YELLOW);
        
        ItemStackWrapper builder;
        switch(item.getTeleportSource()) {
            case RELATIVE_TO_PLAYER:
                builder = ItemStackWrapper.newItem(XMaterial.PLAYER_HEAD);
                break;
            case RELATIVE_TO_LUCKY_BLOCK:
                builder = ItemStackWrapper.fromItem(TypeManager.getMenuItemStack(), false);
                break;
            default:
                builder = ItemStackWrapper.newItem(XMaterial.GRASS_BLOCK);
        }
        builder.setDisplayName("&bTeleport source: &a" + item.getTeleportSource().name());
        builder.addLoreLine("&3Click to change");
        ItemStack teleportSourceStack = builder.toItemStack();
        
        ItemStack worldNameStack = ItemStackWrapper.newItem(XMaterial.OAK_SIGN)
                                                   .setDisplayName("&bSelected world: &a" + item.getWorldName())
                                                   .addLoreLine("&3Click to change")
                                                   .addLoreLine("")
                                                   .addLoreLine("&6This world is only used")
                                                   .addLoreLine("&6  if teleport source is &b&lABSOLUTE&6.")
                                                   .addLoreLine("&6You can use &a%world% &6to select")
                                                   .addLoreLine("&6  the world of the player")
                                                   .toItemStack();
        
        setItem(12, teleportSourceStack, e -> {
            item.setTeleportSource(item.getTeleportSource().next());
            openToPlayer(player);
        });
        
        setItem(13, worldNameStack, e -> {
            ChatListener.registerPlayer(player, message -> {
                if(!message.equals(TeleportReward.PLAYER_WORLD_PATTERN)) {
                    if(Bukkit.getWorld(message) == null) {
                        Logger.sendMessage("&cWorld &b" + message + " &cdoesn't exist", player, true);
                        Logger.sendMessage("&cUse &b/alb return &cif you want to return to the menu", player, true);
                        return;
                    }
                }
                
                item.setWorldName(message);
                openToPlayer(player);
            }, false);
        });
        
        setItem(14, item.getOffset().getItemToDisplay(), e -> {
            OffsetMenu menu = new OffsetMenu();
            menu.setItemToEdit(item.getOffset().clone());
            menu.setOnBack(this::openToPlayer);
            menu.setOnNext((p, offset) -> {
                item.setOffset(offset);
                openToPlayer(player);
            });
            menu.openToPlayer(player);
        });
        
        setBackItem(10);
        setNextItem(16);
    }
}
