package me.i2000c.newalb.lucky_blocks.editors.menus.luckyblock_type;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.PaginatedMenu;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.lucky_blocks.rewards.LuckyBlockType;
import me.i2000c.newalb.lucky_blocks.rewards.TypeManager;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.misc.OtherUtils;

public class LuckyBlockTypeListMenu extends PaginatedMenu<LuckyBlockType> {
    
    private boolean renameMode;
    private boolean deleteMode;
    
    public LuckyBlockTypeListMenu() {
        super("&6&lLucky Block Types", MenuSize.SIZE_6_ROWS, true, MenuSize.SIZE_3_ROWS, 30, 31, 32);
    }
    
    @Override
    public List<LuckyBlockType> getItemList() {
        return TypeManager.getTypes();
    }
    
    @Override
    public MenuItem mapItemToPage(LuckyBlockType luckyBlockType, int index) {
        ItemStackWrapper wrapper = ItemStackWrapper.fromItem(luckyBlockType.getItemToDisplay(), false);
        Consumer<MenuClickEvent> action = e -> {
            Player player = e.getPlayer();
            if(renameMode) {
                //Change type name of selected type
                renameMode = false;
                player.closeInventory();
                Logger.sendMessage("&3Enter the new identifier for this lucky block type.", player);
                Logger.sendMessage("  &3If you don't want to change it, use &a/alb return", player, false);
                ChatListener.registerPlayer(player, message -> {
                    message = OtherUtils.removeExtension(message);
                    if(TypeManager.PERMISSIONS_FILENAME.equals(message + ".yml")) {
                        Logger.sendMessage("&cInvalid LuckyBlock identifier", player, false);
                    } else if(TypeManager.getType(message) != null) {
                        Logger.sendMessage("&cThat identifier already exists", player, false);
                    } else {
                        ChatListener.removePlayer(player);
                        TypeManager.renameType(e.getSlot(), message);
                        resetPagination();
                        openToPlayer(player);
                    }
                }, false);
            } else if(deleteMode) {
                //Delete selected type
                TypeManager.removeType(luckyBlockType);
                resetPagination();
                openToPlayer(player);
            } else {
                //Clone and edit selected type
                LuckyBlockTypeMenu menu = new LuckyBlockTypeMenu();
                menu.setItemToEdit(luckyBlockType.clone());
                menu.setOnBack(this::openToPlayer);
                menu.setOnNext((p, type) -> {
                    TypeManager.addType(type);
                    resetPagination();
                    openToPlayer(player);
                });
                menu.openToPlayer(player);
            }
        };
        
        return new MenuItem(wrapper, action);
    }
    
    @Override
    protected void buildMenu(Player player) {
        addGlassRow(GlassColor.LIGHT_BLUE, 3);
        
        String placePermission = TypeManager.getGlobalPlacePermission();
        String breakPermission = TypeManager.getGlobalBreakPermission();
        boolean placePermissionEnabled = TypeManager.isGlobalPlacePermissionEnabled();
        boolean breakPermissionEnabled = TypeManager.isGlobalBreakPermissionEnabled();
        
        ItemStack createType = ItemStackWrapper.newItem(XMaterial.SLIME_BALL)
                                               .setDisplayName("&aCreate new lucky block type")
                                               .toItemStack();
        
        ItemStack renameType = GUIItem.getEnabledDisabledItem(
                renameMode, 
                "&3Rename LuckyBlock types", 
                "&6Rename mode", 
                XMaterial.NAME_TAG, 
                XMaterial.NAME_TAG);
        
        ItemStack deleteType = GUIItem.getEnabledDisabledItem(
                deleteMode, 
                "&cDelete existing LuckyBlock type", 
                "&6Delete mode", 
                XMaterial.BARRIER, 
                XMaterial.BARRIER);
        ItemStackWrapper.fromItem(deleteType, false)
                .addLoreLine("")
                .addLoreLine("&4&lWARNING: &cIf this mode is enabled,")
                .addLoreLine("&cwhen you click on a LuckyBlock type,")
                .addLoreLine("&cit will be deleted permanently");
        
        ItemStack placePermissionItem = ItemStackWrapper.newItem(XMaterial.STONE)
                                                        .setDisplayName("&dCurrent global place permission:")
                                                        .addLoreLine("   &b" + placePermission)
                                                        .addLoreLine(" ")
                                                        .addLoreLine("&3Click to change")
                                                        .toItemStack();
        
        ItemStack breakPermissionItem = ItemStackWrapper.newItem(XMaterial.IRON_PICKAXE)
                                                        .setDisplayName("&dCurrent global break permission:")
                                                        .addLoreLine("   &b" + breakPermission)
                                                        .addLoreLine(" ")
                                                        .addLoreLine("&3Click to change")
                                                        .toItemStack();
        
        ItemStack requirePlacePermission = GUIItem.getBooleanItem(
                placePermissionEnabled, 
                "&dRequire global place permission",                 
                XMaterial.LIME_DYE, 
                XMaterial.GRAY_DYE);
        
        ItemStack requireBreakPermission = GUIItem.getBooleanItem(
                breakPermissionEnabled, 
                "&dRequire global break permission",                 
                XMaterial.LIME_DYE, 
                XMaterial.GRAY_DYE);
        
        setBackItem(45);
        
        if(!deleteMode) {
            setItem(47, renameType, e -> {
                renameMode = !renameMode;
                openToPlayer(player);
            });
        }
        
        if(!renameMode) {
            setItem(48, deleteType, e -> {
                deleteMode = !deleteMode;
                openToPlayer(player);
            });
        }        
        
        if(!renameMode && !deleteMode) {
            setItem(41, placePermissionItem, e -> {
                player.closeInventory();
                Logger.sendMessage("&3Enter the new global &dplace &3permission and then press ENTER.", player);
                Logger.sendMessage("&bTo return without change the permission, type &a/alb return", player, false);
                ChatListener.registerPlayer(player, message -> {
                    TypeManager.setGlobalPlacePermission(message);
                    openToPlayer(player);
                });
            });
            setItem(42, breakPermissionItem, e -> {
                player.closeInventory();
                Logger.sendMessage("&3Enter the new global &dbreak &3permission and then press ENTER.", player);
                Logger.sendMessage("&bTo return without change the permission, type &a/alb return", player, false);
                ChatListener.registerPlayer(player, message -> {
                    TypeManager.setGlobalBreakPermission(message);
                    openToPlayer(player);
                });
            });

            setItem(46, createType, e -> {
                player.closeInventory();
                Logger.sendMessage("&3Enter the new lucky block type identifier and press ENTER", player);
                ChatListener.registerPlayer(player, message -> {
                    message = OtherUtils.removeExtension(message);
                    if(TypeManager.PERMISSIONS_FILENAME.equals(message + ".yml")) {
                        Logger.sendMessage("&cInvalid LuckyBlock identifier", player, false);
                    } else if(TypeManager.getType(message) != null) {
                        Logger.sendMessage("&cThat identifier already exists", player, false);
                    } else {
                        ChatListener.removePlayer(player);
                        LuckyBlockType luckyBlockType = new LuckyBlockType(message);
                        LuckyBlockTypeMenu menu = new LuckyBlockTypeMenu();
                        menu.setItemToEdit(luckyBlockType);
                        menu.setOnBack(this::openToPlayer);
                        menu.setOnNext((p, type) -> {
                            TypeManager.addType(type);
                            resetPagination();
                            openToPlayer(player);
                        });
                        menu.openToPlayer(player);
                    }
                }, false);
            });

            setItem(50, requirePlacePermission, e -> {
                TypeManager.setEnableGlobalPlacePermission(!TypeManager.isGlobalPlacePermissionEnabled());
                openToPlayer(player);
            });
            setItem(51, requireBreakPermission, e -> {
                TypeManager.setEnableGlobalBreakPermission(!TypeManager.isGlobalBreakPermissionEnabled());
                openToPlayer(player);
            });
        }
    }
}
