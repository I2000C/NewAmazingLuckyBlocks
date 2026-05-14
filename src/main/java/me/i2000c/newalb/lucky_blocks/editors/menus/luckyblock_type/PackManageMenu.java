package me.i2000c.newalb.lucky_blocks.editors.menus.luckyblock_type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.PaginatedEditorMenu;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.lucky_blocks.rewards.LuckyBlockType;
import me.i2000c.newalb.lucky_blocks.rewards.OutcomePack;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

class PackManageMenu extends PaginatedEditorMenu<LuckyBlockType, Map.Entry<OutcomePack, Integer>> {
    
    private boolean editPackMode;
    private boolean deletePackMode;
    
    public PackManageMenu() {
        super("&bCurrent packs", MenuSize.SIZE_6_ROWS, true, MenuSize.SIZE_5_ROWS);
    }
    
    @Override
    public List<Map.Entry<OutcomePack, Integer>> getItemList() {
        return new ArrayList<>(item.getPacks().entrySet());
    }
    
    @Override
    public MenuItem mapItemToPage(Map.Entry<OutcomePack, Integer> entry, int index) {
        OutcomePack pack = entry.getKey();
        int probability = entry.getValue();
        ItemStackWrapper wrapper = ItemStackWrapper.fromItem(pack.getItemToDisplay(), false)
                                                   .addLoreLine("")
                                                   .addLoreLine("&eProbability: &2" + probability);
        Consumer<MenuClickEvent> action = e -> {
            Player player = e.getPlayer();
            if(deletePackMode) {
                item.removePack(pack);
                resetPagination();
                openToPlayer(player);
            } else if(editPackMode) {
                player.closeInventory();
                Logger.sendMessage(String.format("&3Enter the new pack probability for the pack &e%s&r", pack.getPackname()), player);
                ChatListener.registerPlayer(player, message -> {
                    try {
                        int prob = Integer.parseInt(message);
                        if(prob < 0) {
                            Logger.sendMessage("&cThe probability cannot be negative", player, false);
                        } else {
                            ChatListener.removePlayer(player);
                            item.addProbabilityPack(pack, prob);
                            resetPagination();
                            openToPlayer(player);
                        }
                    } catch(NumberFormatException ex) {
                        Logger.sendMessage("&cThe probability must be a number not: &6" + message, player, false);
                    }
                }, false);
            }
        };
        return new MenuItem(wrapper, action);
    }
    
    @Override
    protected void buildMenu(Player player) {
        addGlassRow(GlassColor.ORANGE, 4);
        
        ItemStack addPack = ItemStackWrapper.newItem(XMaterial.SLIME_BALL)
                .setDisplayName("&aAdd outcome pack")
                .toItemStack();
        
        ItemStack editPack = GUIItem.getEnabledDisabledItem(
                editPackMode, 
                "&eEdit pack probability", 
                "&6Edit probability mode", 
                XMaterial.GLOWSTONE_DUST, 
                XMaterial.GLOWSTONE_DUST);
        ItemStackWrapper.fromItem(editPack, false)
                .addLoreLine("")
                .addLoreLine("&3If this mode is enabled,")
                .addLoreLine("&3you will be able to change")
                .addLoreLine("&3the probability of a pack")
                .addLoreLine("&3if you click on it");
        
        ItemStack deletePack = GUIItem.getEnabledDisabledItem(
                deletePackMode, 
                "&cDelete packs", 
                "&6Delete mode", 
                XMaterial.BARRIER, 
                XMaterial.BARRIER);
        ItemStackWrapper.fromItem(deletePack, false)
                .addLoreLine("")
                .addLoreLine("&4&lWARNING: &cIf this mode is enabled,")
                .addLoreLine("&cwhen you click on a pack,")
                .addLoreLine("&cit will be removed from the pack list")
                .addLoreLine("&cof the selected lucky block type");
        
        setBackItem(45);
        setNextItem(53);
        
        if(!deletePackMode && !editPackMode) {
            setItem(48, addPack, e -> {
                PackSelectMenu menu = new PackSelectMenu();
                menu.setOnBack(this::openToPlayer);
                menu.setOnNext((p, outcomePack) -> {
                    item.addProbabilityPack(outcomePack, 100);
                    resetPagination();
                    openToPlayer(player);
                });
                menu.openToPlayer(player);
            });
        }
        if(!deletePackMode) {
            setItem(49, editPack, e -> {
                editPackMode = !editPackMode;
                openToPlayer(player);
            });
        }
        if(!editPackMode) {
            setItem(50, deletePack, e -> {
                deletePackMode = !deletePackMode;
                openToPlayer(player);
            });
        }
    }
}
