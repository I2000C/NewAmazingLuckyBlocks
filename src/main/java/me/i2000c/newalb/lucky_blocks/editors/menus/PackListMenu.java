package me.i2000c.newalb.lucky_blocks.editors.menus;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.MenuItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.PaginatedMenu;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.lucky_blocks.rewards.OutcomePack;
import me.i2000c.newalb.lucky_blocks.rewards.PackManager;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.misc.OtherUtils;

public class PackListMenu extends PaginatedMenu<OutcomePack> {
    
    private boolean renameMode;
    private boolean changeIconMode;
    private boolean cloneMode;
    private boolean deleteMode;
    
    public PackListMenu() {
        super("&3&lPack menu", MenuSize.SIZE_6_ROWS, true, MenuSize.SIZE_5_ROWS);
    }
    
    @Override
    public List<OutcomePack> getItemList() {
        return PackManager.getSortedPacks();
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public MenuItem mapItemToPage(OutcomePack outcomePack, int index) {
        ItemStackWrapper wrapper = ItemStackWrapper.fromItem(outcomePack.getItemToDisplay(), false);
        Consumer<MenuClickEvent> action = e -> {
            Player player = e.getPlayer();
            String packName = outcomePack.getPackname();
            if(renameMode) {
                //Rename pack
                ChatListener.registerPlayer(player, message -> {
                    String newPackName = OtherUtils.removeExtension(message);
                    File newFile = new File(PackManager.OUTCOMES_FOLDER, newPackName + ".yml");
                    if(newFile.exists()) {
                        Logger.sendMessage("&cPack &6\"" + packName + "\" &calready exists", player);
                        Logger.sendMessage("&cUse &b/alb return &cto return to the menu", player, false);
                        return;
                    }
                    
                    ChatListener.removePlayer(player);
                    PackManager.renamePack(packName, newPackName, player);
                    openToPlayer(player);
                });
                player.closeInventory();
                Logger.sendMessage(("&3Write the new pack name in the chat"), player);
            } else if(changeIconMode) {
                //Change pack icon
                if(!e.isEmptyCursor()) {
                    PackManager.changePackIcon(packName, e.getCursor(), player);
                    e.setCursor(null);
                    resetPagination();
                    openToPlayer(player);
                }
            } else if(cloneMode) {
                //Clone pack
                PackManager.clonePack(packName, player);
                resetPagination();
                openToPlayer(player);
            } else if(deleteMode) {
                //Delete pack
                PackManager.removePack(packName, player);
                resetPagination();
                openToPlayer(player);
            } else {
                //Edit pack
                int outcomeNumber = outcomePack.getOutcomes().size();
                OutcomeListMenu menu = new OutcomeListMenu();
                menu.setItemToEdit(outcomePack);
                menu.setOnBack(p -> {
                    int newOutcomeNumber = outcomePack.getOutcomes().size();
                    if(outcomeNumber != newOutcomeNumber) {
                        resetPagination();
                    }
                    openToPlayer(player);
                });
                menu.openToPlayer(player);
            }
        };
        return new MenuItem(wrapper, action);
    }
    
    @Override
    protected void buildMenu(Player player) {
        ItemStack createPack = ItemStackWrapper.newItem(XMaterial.SLIME_BALL)
                                               .setDisplayName("&aCreate new pack")
                                               .toItemStack();
        
        ItemStack renamePack = GUIItem.getEnabledDisabledItem(
                renameMode, 
                "&3Rename packs", 
                "&6Rename mode", 
                XMaterial.NAME_TAG, 
                XMaterial.NAME_TAG);
        
        ItemStack clonePack = GUIItem.getEnabledDisabledItem(
                cloneMode, 
                "&bClone packs", 
                "&6Clone mode", 
                XMaterial.REPEATER, 
                XMaterial.REPEATER);
        
        ItemStack deletePack = GUIItem.getEnabledDisabledItem(
                deleteMode, 
                "&cRemove packs", 
                "&6Delete mode", 
                XMaterial.BARRIER, 
                XMaterial.BARRIER);
        ItemStackWrapper.fromItem(deletePack, false)
                .addLoreLine("")
                .addLoreLine("&4&lWARNING: &cIf this mode is enabled,")
                .addLoreLine("&cwhen you click on a pack,")
                .addLoreLine("&cit will be deleted permanently");
        
        ItemStack changeIcon = GUIItem.getEnabledDisabledItem(
                changeIconMode, 
                "&9Change icon", 
                "&6Change icon mode", 
                XMaterial.CHEST, 
                XMaterial.CHEST);
        ItemStackWrapper.fromItem(changeIcon, false)
                .addLoreLine("")
                .addLoreLine("&bIf this mode is enabled,")
                .addLoreLine("&byou can click on an item of")
                .addLoreLine("&byour inventory and then click")
                .addLoreLine("&bon a pack to change its icon");
        
        setBackItem(45);
        
        if(!renameMode && !changeIconMode && !cloneMode && !deleteMode) {
            setItem(46, createPack, e -> {
                ChatListener.registerPlayer(player, message -> {
                    String packName = OtherUtils.removeExtension(message);
                    File newFile = new File(PackManager.OUTCOMES_FOLDER, packName + ".yml");
                    if(newFile.exists()) {
                        Logger.sendMessage("&cPack &6\"" + packName + "\" &calready exists", player);
                        Logger.sendMessage("&cUse &b/alb return &cto return to the menu", player, false);
                        return;
                    }
                    
                    ChatListener.removePlayer(player);
                    OutcomePack pack = new OutcomePack(newFile);
                    pack.saveOutcomes();
                    PackManager.addNewPack(pack, player);
                    resetPagination();
                    openToPlayer(player);
                }, false);
                player.closeInventory();
                Logger.sendMessage("&3Write the new pack name in the chat", player);
            });
        }
        if(!changeIconMode && !cloneMode && !deleteMode) {
            setItem(47, renamePack, e -> {
                renameMode = !renameMode;
                openToPlayer(player);
            });
        }
        if(!renameMode && !cloneMode && !deleteMode) {
            setItem(48, changeIcon, e -> {
                if(e.isEmptyCursor()) {
                    changeIconMode = !changeIconMode;
                    openToPlayer(player);
                }
            });
        }
        if(!renameMode && !changeIconMode && !deleteMode) {
            setItem(49, clonePack, e -> {
                cloneMode = !cloneMode;
                openToPlayer(player);
            });
        }
        if(!renameMode && !changeIconMode && !cloneMode) {
            setItem(50, deletePack, e -> {
                deleteMode = !deleteMode;
                openToPlayer(player);
            });
        }
    }
    
    @SuppressWarnings("deprecation")
    @Override
    protected void onClickDefault(MenuClickEvent event) {
        if(changeIconMode && event.isBottomInventory() && !event.isEmptyItem()) {
            event.setCursor(event.getCurrentItem());
        } else {
            event.setCursor(null);
        }
    }
}
