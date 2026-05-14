package me.i2000c.newalb.lucky_blocks.editors.menus.luckyblock_type;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.lucky_blocks.rewards.LuckyBlockType;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.textures.Texture;

public class LuckyBlockTypeMenu extends EditorMenu<LuckyBlockType> {
    
    public LuckyBlockTypeMenu() {
        super("&eEdit LuckyBlock type", MenuSize.SIZE_6_ROWS, true);
    }
    
    private static final int[] CRAFTING_SLOTS = {21, 22, 23, 30, 31, 32, 39, 40, 41};
    
    @SuppressWarnings("deprecation")
    @Override
    protected void buildMenu(Player player) {
        addHollowGlassSquare(GlassColor.MAGENTA, 11, 5);
        
        ItemStack crafting = ItemStackWrapper.newItem(XMaterial.CRAFTING_TABLE)
                                             .setDisplayName("&aLuckyType crafting")
                                             .addLoreLine("&3Drag and drop items from your inventory")
                                             .addLoreLine("  &3into the crafting area")
                                             .toItemStack();
        
        ItemStack typeItem = item.getItem()
                                 .setDisplayName("&5LuckyBlock item")
                                 .setLore("&3Click with an item to change material/texture")
                                 .toItemStack();
        
        ItemStackWrapper wrapper = item.getItem();
        String displayName = wrapper.getDisplayName();
        List<String> lore = wrapper.getLore();
        
        wrapper = ItemStackWrapper.newItem(XMaterial.NAME_TAG);
        if(displayName == null) {
            wrapper.setDisplayName("&bCurrent item name:");
        } else {
            wrapper.setDisplayName("&bCurrent item name: &r" + displayName);
        }
        wrapper.addLoreLine("&3Click to change");
        ItemStack typeItemName = wrapper.toItemStack();
        
        wrapper = ItemStackWrapper.newItem(XMaterial.OAK_SIGN);
        wrapper.setDisplayName("&bClick to add lore line");
        wrapper.addLoreLine("&2Current item lore:");
        wrapper.addLoreLine("");
        if(lore != null) {
            wrapper.addLore(lore);
        }
        ItemStack typeItemLore = wrapper.toItemStack();
        
        ItemStack removeItemLore = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                                   .setDisplayName("&cClick to remove item lore")
                                                   .toItemStack();
        
        ItemStackWrapper wrapper2 = ItemStackWrapper.newItem(XMaterial.BOOKSHELF)
                                                    .setDisplayName("&bCurrent pack list:");
        item.getPacks().forEach((pack, probability) -> 
                wrapper2.addLoreLine("  &2" + pack.getPackname() + ";" + probability));
        wrapper2.addLoreLine("");
        wrapper2.addLoreLine("&3Click to change");
        ItemStack typePacks = wrapper2.toItemStack();
        
        
        ItemStack placePermissionItem = ItemStackWrapper.newItem(XMaterial.STONE)
                                                        .setDisplayName("&dCurrent place permission:")
                                                        .addLoreLine("   &b" + item.getPlacePermission())
                                                        .addLoreLine(" ")
                                                        .addLoreLine("&3Click to change")
                                                        .toItemStack();
        
        ItemStack breakPermissionItem = ItemStackWrapper.newItem(XMaterial.IRON_PICKAXE)
                                                        .setDisplayName("&dCurrent break permission:")
                                                        .addLoreLine("   &b" + item.getBreakPermission())
                                                        .addLoreLine(" ")
                                                        .addLoreLine("&3Click to change")
                                                        .toItemStack();
        
        ItemStack requirePlacePermission = GUIItem.getBooleanItem(
                item.isRequirePlacePermission(), 
                "&dRequire place permission", 
                XMaterial.LIME_DYE, 
                XMaterial.GRAY_DYE);
        
        ItemStack requireBreakPermission = GUIItem.getBooleanItem(
                item.isRequireBreakPermission(), 
                "&dRequire break permission", 
                XMaterial.LIME_DYE, 
                XMaterial.GRAY_DYE);
        
        setBackItem(18);
        setNextItem(26);
        
        setItem(46, crafting);
        
        setItem(2, typeItem, e -> {
            if(!e.isEmptyCursor()) {
                ItemStack cursor = e.getCursor();
                if(cursor.getType().isBlock() || Texture.isSkull(cursor)) {
                    item.setItem(ItemStackWrapper.fromItem(cursor)
                                                 .setAmount(1)
                                                 .setDisplayName(item.getItem().getDisplayName())
                                                 .setLore(item.getItem().getLore())
                                                 .clearEnchantments());
                    openToPlayer(player);
                }
            }
        });
        setItem(4, typeItemName, e -> {
            player.closeInventory();
            Logger.sendMessage("&3Enter the new item name for this lucky block type.", player);
            Logger.sendMessage("  &3You can use color codes.", player, false);
            Logger.sendMessage("  &3To remove the display name, type &cnull&3.", player, false);
            Logger.sendMessage("  &3If you don't want to change it, use &a/alb return", player, false);
            ChatListener.registerPlayer(player, message -> {
                item.getLuckyBlockItem().setDisplayName(message.equals("null") ? null : message);
                openToPlayer(player);
            });
        });
        setItem(5, typeItemLore, e -> {
            player.closeInventory();
            Logger.sendMessage("&3Enter the new lore line for this lucky block type.", player);
            Logger.sendMessage("  &3You can use color codes.", player, false);
            Logger.sendMessage("  &3If you don't want add it, use &a/alb return", player, false);
            ChatListener.registerPlayer(player, message -> {
                item.getLuckyBlockItem().addLoreLine(message);
                openToPlayer(player);
            });
        });
        setItem(6, removeItemLore, e -> {
            item.getLuckyBlockItem().setLore();
            openToPlayer(player);
        });
        setItem(8, typePacks, e -> {
            PackManageMenu menu = new PackManageMenu();
            menu.setItemToEdit(item.clone());
            menu.setOnBack(this::openToPlayer);
            menu.setOnNext((p, luckyBlockType) -> {
                item.setPacks(luckyBlockType.getPacks());
                openToPlayer(player);
            });
            menu.openToPlayer(player);
        });
        
        setItem(43, placePermissionItem, e -> {
            player.closeInventory();
            Logger.sendMessage("&3Enter the new &dplace &3permission and then press ENTER", player);
            Logger.sendMessage("&bTo return without change the permission, type &a/alb return", player, false);
            ChatListener.registerPlayer(player, message -> {
                item.setPlacePermission(message);
                openToPlayer(player);
            });
        });
        setItem(44, breakPermissionItem, e -> {
            player.closeInventory();
            Logger.sendMessage("&3Enter the new &dbreak &3permission and then press ENTER", player);
            Logger.sendMessage("&bTo return without change the permission, type &a/alb return", player, false);
            ChatListener.registerPlayer(player, message -> {
                item.setBreakPermission(message);
                openToPlayer(player);
            });
        });
        setItem(52, requirePlacePermission, e -> {
            item.setRequirePlacePermission(!item.isRequirePlacePermission());
            openToPlayer(player);
        });
        setItem(53, requireBreakPermission, e -> {
            item.setRequireBreakPermission(!item.isRequireBreakPermission());
            openToPlayer(player);
        });
        
        for(int i=0; i<CRAFTING_SLOTS.length; i++) {
            int craftingIndex = i;
            setItem(CRAFTING_SLOTS[craftingIndex], item.getCrafting().get(craftingIndex), e -> {
                ItemStack cursor = e.getCursor();
                ItemStack currentItem = e.getCurrentItem();
                item.getCrafting().set(craftingIndex, e.isEmptyCursor() ? XMaterial.AIR.parseItem() : cursor);
                e.setCursor(currentItem);
                if(!e.isEmptyCursor()) {
                    cursor = XMaterial.matchXMaterial(cursor).parseItem();
                }
                e.getInventory().setItem(e.getSlot(), cursor);
            });
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onClickDefault(MenuClickEvent event) {
        if(event.isBottomInventory()) {
            event.setCursor(event.getCurrentItem());
        } else {
            event.setCursor(null);
        }
    }
}
