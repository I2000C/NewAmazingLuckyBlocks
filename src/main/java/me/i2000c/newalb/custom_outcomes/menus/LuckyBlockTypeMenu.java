package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.rewards.LuckyBlockType;
import me.i2000c.newalb.custom_outcomes.rewards.OutcomePack;
import me.i2000c.newalb.custom_outcomes.rewards.PackManager;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.textures.TextureManager;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LuckyBlockTypeMenu extends Editor<LuckyBlockType>{
    public LuckyBlockTypeMenu(){
        InventoryListener.registerInventory(CustomInventoryType.LUCKY_BLOCK_TYPE_EDIT_MENU, LUCKY_BLOCK_TYPE_EDIT_MENU_FUNCTION);
        InventoryListener.registerInventory(CustomInventoryType.PACK_MANAGE_MENU, PACK_MANAGE_MENU_FUNCTION);
        InventoryListener.registerInventory(CustomInventoryType.PACK_SELECT_MENU, PACK_SELECT_MENU_FUNCTION);
    }
    
    private boolean editPackMode;
    private boolean deletePackMode;    
    
    private final Map<OutcomePack, Integer> auxPacks = new LinkedHashMap<>();
    private static final int[] CRAFTING_SLOTS = {21, 22, 23, 30, 31, 32, 39, 40, 41};
    
    @Override
    protected void reset(){
        editPackMode = false;
        deletePackMode = false;
        auxPacks.clear();
    }
    
    @Override
    protected void newItem(Player player){
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected void editItem(Player player){
        openEditMenu(player);
    }
    
    private void openEditMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.LUCKY_BLOCK_TYPE_EDIT_MENU, 54, "&eEdit LuckyBlock type");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.MAGENTA);
        
        for(int i=11;i<=15;i++){
            menu.setItem(i, glass);
        }
        for(int i=47;i<=51;i++){
            menu.setItem(i, glass);
        }
        menu.setItem(20, glass);
        menu.setItem(29, glass);
        menu.setItem(38, glass);
        menu.setItem(24, glass);
        menu.setItem(33, glass);
        menu.setItem(42, glass);
        
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
        if(displayName == null){
            wrapper.setDisplayName("&bCurrent item name:");
        }else{
            wrapper.setDisplayName("&bCurrent item name: &r" + displayName);
        }
        wrapper.addLoreLine("&3Click to change");
        ItemStack typeItemName = wrapper.toItemStack();
        
        wrapper = ItemStackWrapper.newItem(XMaterial.OAK_SIGN);
        wrapper.setDisplayName("&bClick to add lore line");
        wrapper.addLoreLine("&2Current item lore:");
        wrapper.addLoreLine("");
        if(lore != null){
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
        
        menu.setItem(18, GUIItem.getBackItem());
        menu.setItem(26, GUIItem.getNextItem());
        
        menu.setItem(46, crafting);
        
        menu.setItem(2, typeItem);
        menu.setItem(4, typeItemName);
        menu.setItem(5, typeItemLore);
        menu.setItem(6, removeItemLore);
        menu.setItem(8, typePacks);
        
        menu.setItem(43, placePermissionItem);
        menu.setItem(44, breakPermissionItem);
        menu.setItem(52, requirePlacePermission);
        menu.setItem(53, requireBreakPermission);
        
        for(int i=0; i<9; i++){
            menu.setItem(CRAFTING_SLOTS[i], item.getCrafting().get(i));
        }
        
        menu.openToPlayer(player);
//</editor-fold>
    }    
    
    private void saveCrafting(Inventory inv){
        //<editor-fold defaultstate="collapsed" desc="Code">        
        for(int i=0; i<CRAFTING_SLOTS.length; i++){
            ItemStack stack = inv.getItem(CRAFTING_SLOTS[i]);
            item.getCrafting().set(i, stack != null ? stack : new ItemStack(Material.AIR));
        }
//</editor-fold>
    }
    
    private final InventoryFunction LUCKY_BLOCK_TYPE_EDIT_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 18:
                    // Go to previous menu
                    onBack.accept(player);
                    break;
                case 26:
                    //Save current type
                    saveCrafting(e.getClickedInventory());
                    onNext.accept(player, item);
                    break;
                case 2:
                    //Set type item
                    if(e.getCursor() != null){
                        ItemStack cursor = e.getCursor();
                        if(cursor.getType() != Material.AIR && 
                                (cursor.getType().isBlock() || TextureManager.isSkull(cursor.getType()))){
                            ItemStackWrapper wrapper = item.getItem();
                            String displayName = wrapper.getDisplayName();
                            List<String> lore = wrapper.getLore();
                            
                            item.setItem(ItemStackWrapper.fromItem(cursor)
                                                         .setAmount(1)
                                                         .setDisplayName(displayName)
                                                         .setLore(lore)
                                                         .clearEnchantments());
                            
                            ItemStack typeItem = item.getItem()
                                                     .setDisplayName("&5LuckyBlock item")
                                                     .setLore("&3Click with an item to change material/texture")
                                                     .toItemStack();
                            
                            e.getClickedInventory().setItem(e.getSlot(), typeItem);
                        }
                    }
                    break;
                case 4:
                    //Set type item name
                    saveCrafting(e.getClickedInventory());
                    player.closeInventory();
                    Logger.sendMessage("&3Enter the new item name for this lucky block type.", player);
                    Logger.sendMessage("  &3You can use color codes.", player, false);
                    Logger.sendMessage("  &3To remove the display name, type &cnull&3.", player, false);
                    Logger.sendMessage("  &3If you don't want to change it, use &a/alb return", player, false);
                    ChatListener.registerPlayer(player, message -> {
                        item.setItem(item.getItem().setDisplayName(message.equals("null") ? null : message));
                        openEditMenu(player);
                    });
                    break;
                case 5:
                    //Add type item lore
                    saveCrafting(e.getClickedInventory());
                    player.closeInventory();
                    Logger.sendMessage("&3Enter the new lore line for this lucky block type.", player);
                    Logger.sendMessage("  &3You can use color codes.", player, false);
                    Logger.sendMessage("  &3If you don't want add it, use &a/alb return", player, false);
                    ChatListener.registerPlayer(player, message -> {
                        item.setItem(item.getItem().addLoreLine(message));
                        openEditMenu(player);
                    });
                    break;
                case 6:
                    //Remove type item lore
                    saveCrafting(e.getClickedInventory());
                    item.setItem(item.getItem().setLore());
                    openEditMenu(player);
                    break;
                case 8:
                    //Open pack manage menu
                    saveCrafting(e.getClickedInventory());
                    auxPacks.clear();
                    auxPacks.putAll(item.getPacks());
                    openPackManageMenu(player);
                    break;
                case 43:
                    //Change place permission
                    saveCrafting(e.getClickedInventory());
                    player.closeInventory();
                    Logger.sendMessage("&3Enter the new &dplace &3permission and then press ENTER", player);
                    Logger.sendMessage("&bTo return without change the permission, type &a/alb return", player, false);
                    ChatListener.registerPlayer(player, message -> {
                        item.setPlacePermission(message);
                        openEditMenu(player);
                    });
                    break;
                case 44:
                    //Change break permission
                    saveCrafting(e.getClickedInventory());
                    player.closeInventory();
                    Logger.sendMessage("&3Enter the new &dbreak &3permission and then press ENTER", player);
                    Logger.sendMessage("&bTo return without change the permission, type &a/alb return", player, false);
                    ChatListener.registerPlayer(player, message -> {
                        item.setBreakPermission(message);
                        openEditMenu(player);
                    });
                    break;
                case 52:
                    //Toggle place permission
                    saveCrafting(e.getClickedInventory());
                    item.setRequirePlacePermission(!item.isRequirePlacePermission());
                    openEditMenu(player);
                    break;
                case 53:
                    //Toggle break permission
                    saveCrafting(e.getClickedInventory());
                    item.setRequireBreakPermission(!item.isRequireBreakPermission());
                    openEditMenu(player);
                    break;
                case 21:
                case 22:
                case 23:
                case 30:
                case 31:
                case 32:
                case 39:
                case 40:
                case 41:
                    //Set crafting item
                    e.setCancelled(false);
                    break;
            }
        }else if(e.getLocation() == InventoryLocation.BOTTOM){
            // Click player inventory
            e.setCancelled(false);
        }
//</editor-fold>
    };
    
    private void openPackManageMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.PACK_MANAGE_MENU, 54, "&bCurrent packs");
        
        int i = 0;
        for(Map.Entry<OutcomePack, Integer> entry : auxPacks.entrySet()){
            if(i >= 36){
                break;
            }
            
            OutcomePack pack = entry.getKey();
            int probability = entry.getValue();
            
            ItemStack packItem = ItemStackWrapper
                    .fromItem(pack.getItemToDisplay())
                    .addLoreLine("")
                    .addLoreLine("&eProbability: &2" + probability)
                    .toItemStack();
            
            menu.setItem(i, packItem);            
            i++;
        }
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.ORANGE);
        
        for(i=36; i<45; i++){
            menu.setItem(i, glass);
        }
        
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
        
        menu.setItem(45, GUIItem.getBackItem());
        menu.setItem(53, GUIItem.getNextItem());
        
        if(!deletePackMode && !editPackMode){
            menu.setItem(48, addPack);
        }
        if(!deletePackMode){
            menu.setItem(49, editPack);
        }
        if(!editPackMode){
            menu.setItem(50, deletePack);
        }
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction PACK_MANAGE_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 45:
                    //Back to edit LuckyBlock type menu
                    deletePackMode = false;
                    editPackMode = false;
                    auxPacks.clear();
                    openEditMenu(player);                    
                    break;
                case 53:
                    // Save auxPacks into item
                    item.getPacks().clear();
                    item.getPacks().putAll(auxPacks);
                    deletePackMode = false;
                    editPackMode = false;
                    auxPacks.clear();
                    openEditMenu(player);
                    break;
                case 48:
                    // Add new pack
                    if(!deletePackMode && !editPackMode){
                        openPackSelectMenu(player);
                    }
                    break;
                case 49:
                    // Toggle edit pack mode
                    if(!deletePackMode){
                        editPackMode = !editPackMode;
                        openPackManageMenu(player);
                    }
                    break;
                case 50:
                    // Togle delete pack mode
                    if(!editPackMode){
                        deletePackMode = !deletePackMode;
                        openPackManageMenu(player);
                    }
                    break;
                default:
                    if(e.getSlot() < 36){
                        if(e.getSlot() < auxPacks.size()){
                            if(deletePackMode || editPackMode){
                                int i = 0;
                                Iterator<OutcomePack> iterator = auxPacks.keySet().iterator();
                                while(iterator.hasNext()){
                                    OutcomePack pack = iterator.next();
                                    if(i == e.getSlot()){
                                        if(deletePackMode){
                                            iterator.remove();
                                            openPackManageMenu(player);
                                        }else{
                                            player.closeInventory();
                                            Logger.sendMessage(String.format("&3Enter the new pack probability for the pack &e%s&r", pack.getPackname()), player);
                                            ChatListener.registerPlayer(player, message -> {
                                                try{
                                                    int probability = Integer.parseInt(message);
                                                    if(probability < 0){
                                                        Logger.sendMessage("&cThe probability cannot be negative", player, false);
                                                    }else{
                                                        ChatListener.removePlayer(player);
                                                        auxPacks.put(pack, probability);
                                                        openPackManageMenu(player);
                                                    }
                                                }catch(NumberFormatException ex){
                                                    Logger.sendMessage("&cThe probability must be a number not: &6" + message, player, false);
                                                }
                                            }, false);
                                        }
                                        break;
                                    }else{
                                        i++;
                                    }
                                }
                            }
                        }
                    }
            }
        }
//</editor-fold>
    };
    
    private static void openPackSelectMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.PACK_SELECT_MENU, 54, "&3&lPack menu");
        List<OutcomePack> packList = PackManager.getPacks();
        packList.sort((OutcomePack pack1, OutcomePack pack2) -> pack1.getPackname().compareTo(pack2.getPackname()));
        
        menu.setItem(45, GUIItem.getBackItem());
        
        int i = 0;
        for(OutcomePack pack : packList){
            if(i >= 45){
                break;
            }
            menu.setItem(i, pack.getItemToDisplay());
            i++;
        }
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction PACK_SELECT_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            if(e.getSlot() == 45){
                // Back to previous menu
                openPackManageMenu(player);
            }else{
                // Add selected pack if it wasn't present in auxPacks
                ItemStack sk = e.getCurrentItem();
                if(sk != null && sk.getType() != Material.AIR){
                    String displayName = ItemStackWrapper.fromItem(sk).getDisplayName();
                    if(displayName != null){
                        String packName = Logger.stripColor(displayName);
                        OutcomePack pack = PackManager.getPack(packName);
                        auxPacks.putIfAbsent(pack, 100);
                        openPackManageMenu(player);
                    }                        
                }
            }
        }
//</editor-fold>
    };
}
