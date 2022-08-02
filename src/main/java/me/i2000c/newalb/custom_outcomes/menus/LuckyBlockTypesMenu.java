package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import me.i2000c.newalb.custom_outcomes.utils.LuckyBlockType;
import me.i2000c.newalb.custom_outcomes.utils.OutcomePack;
import me.i2000c.newalb.custom_outcomes.utils.PackManager;
import me.i2000c.newalb.custom_outcomes.utils.TypeManager;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.TextureManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LuckyBlockTypesMenu{
    private static final int[] CRAFTING_SLOTS = {21, 22, 23, 30, 31, 32, 39, 40, 41};
    
    private static boolean deleteMode;
    
    private static boolean deletePackMode;
    private static boolean editPackMode;
    
    private static LuckyBlockType currentType;
    private static final Map<OutcomePack, Integer> auxPacks;
    
    private static boolean inventoriesRegistered = false;
    
    static{
        auxPacks = new LinkedHashMap<>();
    }
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.LUCKY_BLOCK_TYPES_MENU, LUCKY_BLOCK_TYPES_MENU_FUNCTION);
            InventoryListener.registerInventory(CustomInventoryType.LUCKY_BLOCK_TYPE_EDIT_MENU, LUCKY_BLOCK_TYPE_EDIT_MENU_FUNCTION);
            InventoryListener.registerInventory(CustomInventoryType.PACK_MANAGE_MENU, PACK_MANAGE_MENU_FUNCTION);
            InventoryListener.registerInventory(CustomInventoryType.PACK_SELECT_MENU, PACK_SELECT_MENU_FUNCTION);
            
            inventoriesRegistered = true;
        }
        
        deleteMode = false;
        
        deletePackMode = false;
        editPackMode = false;
        
        currentType = null;
    }
    
    public static void openMainMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String placePermission = TypeManager.getGlobalPlacePermission();
        String breakPermission = TypeManager.getGlobalBreakPermission();
        boolean placePermissionEnabled = TypeManager.isGlobalPlacePermissionEnabled();
        boolean breakPermissionEnabled = TypeManager.isGlobalBreakPermissionEnabled();
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.LUCKY_BLOCK_TYPES_MENU, 54, "&6&lLucky Block Types");
        
        for(int i=0; i<27 && i<TypeManager.getTypes().size(); i++){
            ItemStack packItem = TypeManager.getTypes().get(i).getItemToDisplay();
            inv.setItem(i, packItem);
        }
        
        ItemStack glass = ItemBuilder
                .newItem(XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE)
                .withDisplayName(" ")
                .build();
        
        for(int i=27; i<36; i++){
            inv.setItem(i, glass);
        }
        
        ItemStack back = ItemBuilder
                .newItem(XMaterial.ENDER_PEARL)
                .withDisplayName("&2Back")
                .build();
        
        ItemStack createType = ItemBuilder
                .newItem(XMaterial.SLIME_BALL)
                .withDisplayName("&aCreate new lucky block type")
                .build();
        
        ItemStack deleteType;
        ItemBuilder builder = ItemBuilder
                .newItem(XMaterial.BARRIER)
                .withDisplayName("&cDelete existing lucky block type");
        if(deleteMode){
            builder.addLoreLine("&6Delete mode: &aenabled");
            builder.addLoreLine("");
            builder.addLoreLine("&4&lWARNING: &cIf this mode is enabled,");
            builder.addLoreLine("&cwhen you click on a lucky block type,");
            builder.addLoreLine("&cit will be deleted permanently");
        }else{
            builder.addLoreLine("&6Delete mode: &7disabled");
        }
        deleteType = builder.build();
        
        
        ItemStack placePermissionItem = ItemBuilder
                .newItem(XMaterial.STONE)
                .withDisplayName("&dCurrent global place permission:")
                .addLoreLine("   &b" + placePermission)
                .addLoreLine(" ")
                .addLoreLine("&3Click to change")
                .build();
        
        ItemStack breakPermissionItem = ItemBuilder
                .newItem(XMaterial.IRON_PICKAXE)
                .withDisplayName("&dCurrent global break permission:")
                .addLoreLine("   &b" + breakPermission)
                .addLoreLine(" ")
                .addLoreLine("&3Click to change")
                .build();
        
        ItemStack requirePlacePermission;
        if(placePermissionEnabled){
            requirePlacePermission = ItemBuilder
                    .newItem(XMaterial.LIME_DYE)
                    .withDisplayName("&dRequire global place permission: &atrue")
                    .addLoreLine(" ")
                    .addLoreLine("&3Click to toggle")
                    .build();
        }else{
            requirePlacePermission = ItemBuilder
                    .newItem(XMaterial.GRAY_DYE)
                    .withDisplayName("&dRequire global place permission: &7false")
                    .addLoreLine(" ")
                    .addLoreLine("&3Click to toggle")
                    .build();
        }
        
        ItemStack requireBreakPermission;
        if(breakPermissionEnabled){
            requireBreakPermission = ItemBuilder
                    .newItem(XMaterial.LIME_DYE)
                    .withDisplayName("&dRequire global break permission: &atrue")
                    .addLoreLine(" ")
                    .addLoreLine("&3Click to toggle")
                    .build();
        }else{
            requireBreakPermission = ItemBuilder
                    .newItem(XMaterial.GRAY_DYE)
                    .withDisplayName("&dRequire global break permission: &7false")
                    .addLoreLine(" ")
                    .addLoreLine("&3Click to toggle")
                    .build();
        }
        
        inv.setItem(45, back);
        inv.setItem(47, deleteType);
        
        if(!deleteMode){
            inv.setItem(41, placePermissionItem);
            inv.setItem(42, breakPermissionItem);        

            inv.setItem(46, createType);        

            inv.setItem(50, requirePlacePermission);
            inv.setItem(51, requireBreakPermission);
        }
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction LUCKY_BLOCK_TYPES_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            switch(e.getSlot()){
                case 45:
                    //Back to main menu
                    MainMenu.reset();
                    MainMenu.openMainMenu(p);
                    break;
                case 46:
                    //Create new Lucky Block type
                    if(deleteMode){
                        break;
                    }
                    p.closeInventory();
                    Logger.sendMessage("&3Enter the new lucky block type identifier and press ENTER", p);
                    
                    ChatListener.registerPlayer(p, message -> {
                        if(TypeManager.getType(message) != null){
                            Logger.sendMessage("&cThat identifier already exists", p, false);
                        }else{
                            ChatListener.removePlayer(p);
                            currentType = new LuckyBlockType(message);
                            openEditMenu(p);
                        }
                    }, false);
                    break;
                case 47:
                    //Toggle delete types mode
                    deleteMode = !deleteMode;
                    openMainMenu(p);
                    break;
                    
                case 41:
                    //Change global place permission
                    if(deleteMode){
                        break;
                    }
                    
                    p.closeInventory();
                    Logger.sendMessage("&3Enter the new global &dplace &3permission and then press ENTER.", p);
                    Logger.sendMessage("&bTo return without change the permission, type &a/alb return", p);
                    ChatListener.registerPlayer(p, message -> {
                        TypeManager.setGlobalPlacePermission(message);
                        TypeManager.saveTypes();
                        openMainMenu(p);
                    });
                    break;
                case 42:
                    //Change global break permission
                    if(deleteMode){
                        break;
                    }
                    
                    p.closeInventory();
                    Logger.sendMessage("&3Enter the new global &dbreak &3permission and then press ENTER.", p);
                    Logger.sendMessage("&bTo return without change the permission, type &a/alb return", p);
                    ChatListener.registerPlayer(p, message -> {
                        TypeManager.setGlobalBreakPermission(message);
                        TypeManager.saveTypes();
                        openMainMenu(p);
                    });
                    break;
                case 50:
                    //Toggle global place permission
                    if(deleteMode){
                        break;
                    }
                    
                    boolean requirePermission = TypeManager.isGlobalPlacePermissionEnabled();
                    TypeManager.setEnableGlobalPlacePermission(!requirePermission);
                    TypeManager.saveTypes();
                    openMainMenu(p);
                    break;
                case 51:
                    //Toggle global break permission
                    if(deleteMode){
                        break;
                    }
                    
                    requirePermission = TypeManager.isGlobalBreakPermissionEnabled();
                    TypeManager.setEnableGlobalBreakPermission(!requirePermission);
                    TypeManager.saveTypes();
                    openMainMenu(p);
                    break;
                default:
                    if(e.getSlot() < 16){
                        if(e.getSlot() < TypeManager.getTypes().size()){
                            if(deleteMode){
                                //Delete selected type
                                TypeManager.removeType(e.getSlot());
                                openMainMenu(p);
                            }else{
                                //Clone and edit selected type
                                currentType = TypeManager.getType(e.getSlot());
                                if(currentType != null){
                                    currentType = currentType.cloneType();
                                    openEditMenu(p);
                                }
                            }
                        }
                    }
            }
        }
//</editor-fold>
    };
    
    private static void openEditMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.LUCKY_BLOCK_TYPE_EDIT_MENU, 54, "&eEdit Lucky Block type");
        
        ItemStack glass = ItemBuilder
                .newItem(XMaterial.MAGENTA_STAINED_GLASS_PANE)
                .withDisplayName(" ")
                .build();
        
        for(int i=11;i<=15;i++){
            inv.setItem(i, glass);
        }
        for(int i=47;i<=51;i++){
            inv.setItem(i, glass);
        }
        inv.setItem(20, glass);
        inv.setItem(29, glass);
        inv.setItem(38, glass);
        inv.setItem(24, glass);
        inv.setItem(33, glass);
        inv.setItem(42, glass);
        
        
        ItemStack back = ItemBuilder
                .newItem(XMaterial.ENDER_PEARL)
                .withDisplayName("&2Back")
                .build();
        
        ItemStack next = ItemBuilder
                .newItem(XMaterial.ANVIL)
                .withDisplayName("&bNext")
                .build();
        
        ItemStack crafting = ItemBuilder
                .newItem(XMaterial.CRAFTING_TABLE)
                .withDisplayName("&aLuckyType crafting")
                .addLoreLine("&3Drag and drop items from your inventory")
                .addLoreLine("  &3into the crafting area")
                .build();
        
        ItemStack typeItem = ItemBuilder
                .fromItem(currentType.getItem())
                .withDisplayName("&5LuckyBlock item")
                .withLore("&3Click with an item to change material/texture")
                .build();
        
        ItemStack typeName = ItemBuilder
                .newItem(XMaterial.WRITABLE_BOOK)
                .withDisplayName("&bCurrent identifier: &6" + currentType.getTypeName())
                .addLoreLine("&3Click to change")
                .build();
        
        String displayName;
        List<String> lore;
        if(currentType.getItem().hasItemMeta()){
            ItemMeta meta = currentType.getItem().getItemMeta();
            if(meta.hasDisplayName()){
                displayName = meta.getDisplayName();
            }else{
                displayName = "";
            }
            if(meta.hasLore()){
                lore = meta.getLore();
            }else{
                lore = new ArrayList<>();
            }
        }else{
            displayName = "";
            lore = new ArrayList<>();
        }
        
        ItemStack typeItemName = ItemBuilder
                .newItem(XMaterial.NAME_TAG)
                .withDisplayName("&bCurrent item name: &r" + displayName)
                .addLoreLine("&3Click to change")
                .build();
        
        ItemStack typeItemLore = ItemBuilder
                .newItem(XMaterial.OAK_SIGN)
                .withDisplayName("&bClick to add lore line")
                .addLoreLine("&2Current item lore:")
                .addLoreLine("")
                .addLore(lore)
                .build();
        
        ItemStack removeItemLore = ItemBuilder
                .newItem(XMaterial.BARRIER)
                .withDisplayName("&cClick to remove item lore")
                .build();
        
        ItemBuilder builder = ItemBuilder
                .newItem(XMaterial.BOOKSHELF)
                .withDisplayName("&bCurrent pack list:");
        currentType.getPacks().forEach((pack, probability) -> builder.addLoreLine("  &2" + pack.getFilename() + ";" + probability));
        builder.addLoreLine("");
        builder.addLoreLine("&3Click to change");
        ItemStack typePacks = builder.build();
        
        
        ItemStack placePermissionItem = ItemBuilder
                .newItem(XMaterial.STONE)
                .withDisplayName("&dCurrent place permission:")
                .addLoreLine("   &b" + currentType.getPlacePermission())
                .addLoreLine(" ")
                .addLoreLine("&3Click to change")
                .build();
        
        ItemStack breakPermissionItem = ItemBuilder
                .newItem(XMaterial.IRON_PICKAXE)
                .withDisplayName("&dCurrent break permission:")
                .addLoreLine("   &b" + currentType.getBreakPermission())
                .addLoreLine(" ")
                .addLoreLine("&3Click to change")
                .build();
        
        ItemStack requirePlacePermission;
        if(currentType.requirePlacePermission()){
            requirePlacePermission = ItemBuilder
                    .newItem(XMaterial.LIME_DYE)
                    .withDisplayName("&dRequire place permission: &atrue")
                    .addLoreLine(" ")
                    .addLoreLine("&3Click to toggle")
                    .build();
        }else{
            requirePlacePermission = ItemBuilder
                    .newItem(XMaterial.GRAY_DYE)
                    .withDisplayName("&dRequire place permission: &7false")
                    .addLoreLine(" ")
                    .addLoreLine("&3Click to toggle")
                    .build();
        }
        
        ItemStack requireBreakPermission;
        if(currentType.requireBreakPermission()){
            requireBreakPermission = ItemBuilder
                    .newItem(XMaterial.LIME_DYE)
                    .withDisplayName("&dRequire break permission: &atrue")
                    .addLoreLine(" ")
                    .addLoreLine("&3Click to toggle")
                    .build();
        }else{
            requireBreakPermission = ItemBuilder
                    .newItem(XMaterial.GRAY_DYE)
                    .withDisplayName("&dRequire break permission: &7false")
                    .addLoreLine(" ")
                    .addLoreLine("&3Click to toggle")
                    .build();
        }
        
        
        
        inv.setItem(18, back);
        inv.setItem(26, next);
        
        inv.setItem(46, crafting);
        
        inv.setItem(2, typeItem);
        inv.setItem(3, typeName);
        inv.setItem(4, typeItemName);
        inv.setItem(5, typeItemLore);
        inv.setItem(6, removeItemLore);
        inv.setItem(8, typePacks);
        
        inv.setItem(43, placePermissionItem);
        inv.setItem(44, breakPermissionItem);
        inv.setItem(52, requirePlacePermission);
        inv.setItem(53, requireBreakPermission);
        
        for(int i=0; i<9; i++){
            ItemStack item = currentType.getCrafting().get(i);
            inv.setItem(CRAFTING_SLOTS[i], item);
        }
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }    
    
    private static void saveCrafting(LuckyBlockType type, Inventory inv){
        //<editor-fold defaultstate="collapsed" desc="Code">        
        for(int i=0; i<9; i++){
            ItemStack item = inv.getItem(CRAFTING_SLOTS[i]);
            type.getCrafting().set(i, item != null ? item : new ItemStack(Material.AIR));
        }
//</editor-fold>
    }
    
    private static final InventoryFunction LUCKY_BLOCK_TYPE_EDIT_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            switch(e.getSlot()){
                case 18:
                    //Back to previous menu
                    openMainMenu(p);
                    break;
                case 26:
                    //Save current type
                    saveCrafting(currentType, e.getClickedInventory());
                    TypeManager.addType(currentType);
                    reset();
                    openMainMenu(p);
                    break;
                case 2:
                    //Set type item
                    if(e.getCursor() != null){
                        ItemStack cursor = e.getCursor();
                        if(cursor.getType() != Material.AIR && 
                                (cursor.getType().isBlock() || TextureManager.isSkull(cursor.getType()))){
                            String displayName = "";
                            List<String> lore = new ArrayList<>();
                            if(currentType.getItem().hasItemMeta()){
                                ItemMeta meta = currentType.getItem().getItemMeta();
                                if(meta.hasDisplayName()){
                                    displayName = meta.getDisplayName();
                                }
                                if(meta.hasLore()){
                                    lore = meta.getLore();
                                }
                            }
                            
                            currentType.setItem(ItemBuilder.fromItem(cursor.clone())
                                .withAmount(1)
                                .withDisplayName(displayName)
                                .withLore(lore)
                                .withEnchantments(Collections.EMPTY_MAP)
                                .build());
                            
                            ItemStack typeItem = ItemBuilder
                                .fromItem(currentType.getItem())
                                .withDisplayName("&5LuckyBlock item")
                                .withLore("&3Click with an item to change material/texture")
                                .build();
                            
                            e.getClickedInventory().setItem(e.getSlot(), typeItem);
                        }
                    }
                    break;
                case 3:
                    //Set type name
                    saveCrafting(currentType, e.getClickedInventory());
                    p.closeInventory();
                    Logger.sendMessage("&3Enter the new identifier for this lucky block type.", p);
                    Logger.sendMessage("  &3If you don't want to change it, use &a/alb return", p);
                    ChatListener.registerPlayer(p, message -> {
                        if(TypeManager.getType(message) != null){
                            Logger.sendMessage("&cThat identifier already exists", p, false);
                        }else{
                            ChatListener.removePlayer(p);
                            currentType.setTypeName(message);
                            openEditMenu(p);
                        }
                    }, false);
                    break;
                case 4:
                    //Set type item name
                    saveCrafting(currentType, e.getClickedInventory());
                    p.closeInventory();
                    Logger.sendMessage("&3Enter the new item name for this lucky block type.", p);
                    Logger.sendMessage("  &3You can use color codes.", p);
                    Logger.sendMessage("  &3To remove the display name, type &cnull&3.", p);
                    Logger.sendMessage("  &3If you don't want to change it, use &a/alb return", p);
                    ChatListener.registerPlayer(p, message -> {
                        currentType.setItem(ItemBuilder
                                .fromItem(currentType.getItem())
                                .withDisplayName(message.equals("null") ? null : message)
                                .build());
                        openEditMenu(p);
                    });
                    break;
                case 5:
                    //Add type item lore
                    saveCrafting(currentType, e.getClickedInventory());
                    p.closeInventory();
                    Logger.sendMessage("&3Enter the new lore line for this lucky block type.", p);
                    Logger.sendMessage("  &3You can use color codes.", p);
                    Logger.sendMessage("  &3If you don't want add it, use &a/alb return", p);
                    ChatListener.registerPlayer(p, message -> {
                        currentType.setItem(ItemBuilder
                                .fromItem(currentType.getItem())
                                .addLoreLine(message)
                                .build());
                        openEditMenu(p);
                    });
                    break;
                case 6:
                    //Remove type item lore
                    saveCrafting(currentType, e.getClickedInventory());
                    currentType.setItem(ItemBuilder
                            .fromItem(currentType.getItem())
                            .withLore()
                            .build());
                    openEditMenu(p);
                    break;
                case 8:
                    //Open pack manage menu
                    saveCrafting(currentType, e.getClickedInventory());
                    auxPacks.clear();
                    auxPacks.putAll(currentType.getPacks());
                    openPackManageMenu(p);
                    break;
                case 43:
                    //Change place permission
                    saveCrafting(currentType, e.getClickedInventory());
                    p.closeInventory();
                    Logger.sendMessage("&3Enter the new &dplace &3permission and then press ENTER", p);
                    Logger.sendMessage("&bTo return without change the permission, type &a/alb return", p);
                    ChatListener.registerPlayer(p, message -> {
                        currentType.setPlacePermission(message);
                        openEditMenu(p);
                    });
                    break;
                case 44:
                    //Change break permission
                    saveCrafting(currentType, e.getClickedInventory());
                    p.closeInventory();
                    Logger.sendMessage("&3Enter the new &dbreak &3permission and then press ENTER", p);
                    Logger.sendMessage("&bTo return without change the permission, type &a/alb return", p);
                    ChatListener.registerPlayer(p, message -> {
                        currentType.setBreakPermission(message);
                        openEditMenu(p);
                    });
                    break;
                case 52:
                    //Toggle place permission
                    saveCrafting(currentType, e.getClickedInventory());
                    currentType.setRequirePlacePermission(!currentType.requirePlacePermission());
                    openEditMenu(p);
                    break;
                case 53:
                    //Toggle break permission
                    saveCrafting(currentType, e.getClickedInventory());
                    currentType.setRequireBreakPermission(!currentType.requireBreakPermission());
                    openEditMenu(p);
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
        }else{
            // Click player inventory
            e.setCancelled(false);
        }
//</editor-fold>
    };
    
    private static void openPackManageMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.PACK_MANAGE_MENU, 54, "&bCurrent packs");
        
        int i = 0;
        for(Map.Entry<OutcomePack, Integer> entry : auxPacks.entrySet()){
            if(i >= 36){
                break;
            }
            
            OutcomePack pack = entry.getKey();
            int probability = entry.getValue();
            
            ItemStack packItem = ItemBuilder
                    .fromItem(pack.getItemToDisplay())
                    .addLoreLine("")
                    .addLoreLine("&eProbability: &2" + probability)
                    .build();
            
            inv.setItem(i, packItem);            
            i++;
        }
        
        ItemStack glass = ItemBuilder
                .newItem(XMaterial.ORANGE_STAINED_GLASS_PANE)
                .withDisplayName(" ")
                .build();
        
        for(i=36; i<45; i++){
            inv.setItem(i, glass);
        }
        
        ItemStack back = ItemBuilder
                .newItem(XMaterial.ENDER_PEARL)
                .withDisplayName("&2Back")
                .build();
        
        ItemStack next = ItemBuilder
                .newItem(XMaterial.ANVIL)
                .withDisplayName("&bNext")
                .build();
        
        ItemStack addPack = ItemBuilder
                .newItem(XMaterial.SLIME_BALL)
                .withDisplayName("&aAdd outcome pack")
                .build();
        
        ItemStack editPack;
        ItemBuilder builder = ItemBuilder
                .newItem(XMaterial.GLOWSTONE_DUST)
                .withDisplayName("&eEdit pack probability");
        if(editPackMode){
            builder.addLoreLine("&6Edit probability mode: &aenabled");
            builder.addLoreLine("");
            builder.addLoreLine("&3If this mode is enabled,");
            builder.addLoreLine("&3you will be able to change");
            builder.addLoreLine("&3the probability of a pack");
            builder.addLoreLine("&3if you click on it");
        }else{
            builder.addLoreLine("&6Edit probability mode: &7disabled");
        }
        editPack = builder.build();
        
        ItemStack deletePack;
        builder = ItemBuilder
                .newItem(XMaterial.BARRIER)
                .withDisplayName("&cDelete packs");
        if(deletePackMode){
            builder.addLoreLine("&6Delete mode: &aenabled");
            builder.addLoreLine("");
            builder.addLoreLine("&4&lWARNING: &cIf this mode is enabled,");
            builder.addLoreLine("&cwhen you click on a pack,");
            builder.addLoreLine("&cit will be removed from the pack list");
            builder.addLoreLine("&cof the selected lucky block type");
        }else{
            builder.addLoreLine("&6Delete mode: &7disabled");
        }
        deletePack = builder.build();
        
        inv.setItem(45, back);
        inv.setItem(53, next);
        
        if(!deletePackMode && !editPackMode){
            inv.setItem(48, addPack);
        }
        if(!deletePackMode){
            inv.setItem(49, editPack);
        }
        if(!editPackMode){
            inv.setItem(50, deletePack);
        }
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction PACK_MANAGE_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            switch(e.getSlot()){
                case 45:
                    //Back to edit LuckyBlock type menu
                    deletePackMode = false;
                    editPackMode = false;
                    auxPacks.clear();
                    openEditMenu(p);                    
                    break;
                case 53:
                    // Save auxPacks into currentType
                    currentType.getPacks().clear();
                    currentType.getPacks().putAll(auxPacks);
                    deletePackMode = false;
                    editPackMode = false;
                    auxPacks.clear();
                    openEditMenu(p);
                    break;
                case 48:
                    // Add new pack
                    if(!deletePackMode && !editPackMode){
                        openPackSelectMenu(p);
                    }
                    break;
                case 49:
                    // Toggle edit pack mode
                    if(!deletePackMode){
                        editPackMode = !editPackMode;
                        openPackManageMenu(p);
                    }
                    break;
                case 50:
                    // Togle delete pack mode
                    if(!editPackMode){
                        deletePackMode = !deletePackMode;
                        openPackManageMenu(p);
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
                                            openPackManageMenu(p);
                                        }else{
                                            p.closeInventory();
                                            Logger.sendMessage(String.format("&3Enter the new pack probability for the pack &e%s&r", pack.getFilename()), p);
                                            ChatListener.registerPlayer(p, message -> {
                                                try{
                                                    int probability = Integer.parseInt(message);
                                                    if(probability < 0){
                                                        Logger.sendMessage("&cThe probability cannot be negative", p);
                                                    }else{
                                                        ChatListener.removePlayer(p);
                                                        auxPacks.put(pack, probability);
                                                        openPackManageMenu(p);
                                                    }
                                                }catch(NumberFormatException ex){
                                                    Logger.sendMessage("&cThe probability must be a number not: &6" + message, p);
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
    
    private static void openPackSelectMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.PACK_SELECT_MENU, 54, "&3&lPack menu");
        List<OutcomePack> packList = PackManager.getPacks();
        packList.sort((OutcomePack pack1, OutcomePack pack2) -> pack1.getFilename().compareTo(pack2.getFilename()));
        
        ItemStack back = ItemBuilder
                .newItem(XMaterial.ENDER_PEARL)
                .withDisplayName("&2Back")
                .build();
        
        inv.setItem(45, back);
        
        int i = 0;
        for(OutcomePack pack : packList){
            if(i >= 45){
                break;
            }
            inv.setItem(i, pack.getItemToDisplay());
            i++;
        }
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);        
//</editor-fold>
    }
    
    private static final InventoryFunction PACK_SELECT_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            if(e.getSlot() == 45){
                // Back to previous menu
                openPackManageMenu(p);
            }else{
                // Add selected pack if it wasn't present in auxPacks
                ItemStack sk = e.getCurrentItem();
                if(sk != null && sk.getType() != Material.AIR
                        && sk.hasItemMeta() && sk.getItemMeta().hasDisplayName()){
                    String packName = Logger.stripColor(sk.getItemMeta().getDisplayName());
                    OutcomePack pack = PackManager.getPack(packName);
                    auxPacks.putIfAbsent(pack, 100);
                    openPackManageMenu(p);
                }
            }
        }
//</editor-fold>
    };
}
