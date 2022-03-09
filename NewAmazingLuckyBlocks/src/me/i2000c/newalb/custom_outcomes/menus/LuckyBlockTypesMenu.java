package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.i2000c.newalb.custom_outcomes.utils.LuckyBlockType;
import me.i2000c.newalb.custom_outcomes.utils.TypeManager;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.ItemStackBuilder;
import me.i2000c.newalb.utils2.TextureManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LuckyBlockTypesMenu{
    private static final int[] CRAFTING_SLOTS = {21, 22, 23, 30, 31, 32, 39, 40, 41};
    
    private static boolean deleteMode;
    
    private static LuckyBlockType currentType;
    
    private static boolean inventoriesRegistered = false;
    
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
            ItemStack packItem = TypeManager.getTypes().get(i).getItem();
            inv.setItem(i, packItem);
        }
        
        ItemStack glass = ItemStackBuilder
                .createNewItem(XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE)
                .withDisplayName(" ")
                .build();
        
        for(int i=27; i<36; i++){
            inv.setItem(i, glass);
        }
        
        ItemStack back = ItemStackBuilder
                .createNewItem(XMaterial.ENDER_PEARL)
                .withDisplayName("&2Back")
                .build();
        
        ItemStack createType = ItemStackBuilder
                .createNewItem(XMaterial.SLIME_BALL)
                .withDisplayName("&aCreate new lucky block type")
                .build();
        
        ItemStack deleteType;
        ItemStackBuilder builder = ItemStackBuilder
                .createNewItem(XMaterial.BARRIER)
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
        
        
        ItemStack placePermissionItem = ItemStackBuilder
                .createNewItem(XMaterial.STONE)
                .withDisplayName("&dCurrent place permission:")
                .addLoreLine("   &b" + placePermission)
                .addLoreLine(" ")
                .addLoreLine("&3Click to change")
                .build();
        
        ItemStack breakPermissionItem = ItemStackBuilder
                .createNewItem(XMaterial.IRON_PICKAXE)
                .withDisplayName("&dCurrent break permission:")
                .addLoreLine("   &b" + breakPermission)
                .addLoreLine(" ")
                .addLoreLine("&3Click to change")
                .build();
        
        ItemStack requirePlacePermission;
        if(placePermissionEnabled){
            requirePlacePermission = ItemStackBuilder
                    .createNewItem(XMaterial.LIME_DYE)
                    .withDisplayName("&dRequire place permission: &atrue")
                    .addLoreLine(" ")
                    .addLoreLine("&3Click to toggle")
                    .build();
        }else{
            requirePlacePermission = ItemStackBuilder
                    .createNewItem(XMaterial.GRAY_DYE)
                    .withDisplayName("&dRequire place permission: &7false")
                    .addLoreLine(" ")
                    .addLoreLine("&3Click to toggle")
                    .build();
        }
        
        ItemStack requireBreakPermission;
        if(breakPermissionEnabled){
            requireBreakPermission = ItemStackBuilder
                    .createNewItem(XMaterial.LIME_DYE)
                    .withDisplayName("&dRequire break permission: &atrue")
                    .addLoreLine(" ")
                    .addLoreLine("&3Click to toggle")
                    .build();
        }else{
            requireBreakPermission = ItemStackBuilder
                    .createNewItem(XMaterial.GRAY_DYE)
                    .withDisplayName("&dRequire break permission: &7false")
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
        
        ItemStack glass = ItemStackBuilder
                .createNewItem(XMaterial.MAGENTA_STAINED_GLASS_PANE)
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
        
        
        ItemStack back = ItemStackBuilder
                .createNewItem(XMaterial.ENDER_PEARL)
                .withDisplayName("&2Back")
                .build();
        
        ItemStack next = ItemStackBuilder
                .createNewItem(XMaterial.ANVIL)
                .withDisplayName("&bNext")
                .build();
        
        ItemStack crafting = ItemStackBuilder
                .createNewItem(XMaterial.CRAFTING_TABLE)
                .withDisplayName("&aLuckyType crafting")
                .addLoreLine("&3Drag and drop items from your inventory")
                .addLoreLine("  &3into the crafting area")
                .build();
        
        ItemStack typeItem = ItemStackBuilder
                .fromItem(currentType.getItem())
                .withDisplayName("&5LuckyBlock item")
                .withLore("&3Click with an item to change material/texture")
                .build();
        
        ItemStack typeName = ItemStackBuilder
                .createNewItem(XMaterial.WRITABLE_BOOK)
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
        
        ItemStack typeItemName = ItemStackBuilder
                .createNewItem(XMaterial.NAME_TAG)
                .withDisplayName("&bCurrent item name: &r" + displayName)
                .addLoreLine("&3Click to change")
                .build();
        
        ItemStack typeItemLore = ItemStackBuilder
                .createNewItem(XMaterial.OAK_SIGN)
                .withDisplayName("&bClick to add lore line")
                .addLoreLine("&2Current item lore:")
                .addLoreLine("")
                .addLore(lore)
                .build();
        
        ItemStack removeItemLore = ItemStackBuilder
                .createNewItem(XMaterial.BARRIER)
                .withDisplayName("&cClick to remove item lore")
                .build();
        
        ItemStackBuilder builder = ItemStackBuilder
                .createNewItem(XMaterial.BOOKSHELF)
                .withDisplayName("&bCurrent pack list:");
        currentType.getPacks().forEach((pack, probability) -> builder.addLoreLine("  &2" + pack.getFilename() + ";" + probability));
        ItemStack typePacks = builder.build();
        
        
        ItemStack placePermissionItem = ItemStackBuilder
                .createNewItem(XMaterial.STONE)
                .withDisplayName("&dCurrent place permission:")
                .addLoreLine("   &b" + currentType.getPlacePermission())
                .addLoreLine(" ")
                .addLoreLine("&3Click to change")
                .build();
        
        ItemStack breakPermissionItem = ItemStackBuilder
                .createNewItem(XMaterial.IRON_PICKAXE)
                .withDisplayName("&dCurrent break permission:")
                .addLoreLine("   &b" + currentType.getBreakPermission())
                .addLoreLine(" ")
                .addLoreLine("&3Click to change")
                .build();
        
        ItemStack requirePlacePermission;
        if(currentType.requirePlacePermission()){
            requirePlacePermission = ItemStackBuilder
                    .createNewItem(XMaterial.LIME_DYE)
                    .withDisplayName("&dRequire place permission: &atrue")
                    .addLoreLine(" ")
                    .addLoreLine("&3Click to toggle")
                    .build();
        }else{
            requirePlacePermission = ItemStackBuilder
                    .createNewItem(XMaterial.GRAY_DYE)
                    .withDisplayName("&dRequire place permission: &7false")
                    .addLoreLine(" ")
                    .addLoreLine("&3Click to toggle")
                    .build();
        }
        
        ItemStack requireBreakPermission;
        if(currentType.requireBreakPermission()){
            requireBreakPermission = ItemStackBuilder
                    .createNewItem(XMaterial.LIME_DYE)
                    .withDisplayName("&dRequire break permission: &atrue")
                    .addLoreLine(" ")
                    .addLoreLine("&3Click to toggle")
                    .build();
        }else{
            requireBreakPermission = ItemStackBuilder
                    .createNewItem(XMaterial.GRAY_DYE)
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
                            
                            currentType.setItem(ItemStackBuilder.fromItem(cursor.clone())
                                .withAmount(1)
                                .withDisplayName(displayName)
                                .withLore(lore)
                                .withEnchantments(Collections.EMPTY_MAP)
                                .build());
                            
                            ItemStack typeItem = ItemStackBuilder
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
                        currentType.setItem(ItemStackBuilder
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
                        currentType.setItem(ItemStackBuilder
                                .fromItem(currentType.getItem())
                                .addLoreLine(message)
                                .build());
                        openEditMenu(p);
                    });
                    break;
                case 6:
                    //Remove type item lore
                    saveCrafting(currentType, e.getClickedInventory());
                    currentType.setItem(ItemStackBuilder
                            .fromItem(currentType.getItem())
                            .withLore()
                            .build());
                    openEditMenu(p);
                    break;
                case 8:
                    //Open pack manage menu
                    saveCrafting(currentType, e.getClickedInventory());
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
            e.setCancelled(false);
        }
//</editor-fold>
    };
    
    private static void openPackManageMenu(Player p){
        
    }
    
    private static final InventoryFunction PACK_MANAGE_MENU_FUNCTION = e -> {
        
    };
    
    private static void openPackSelectMenu(Player p){
        
    }
    
    private static final InventoryFunction PACK_SELECT_MENU_FUNCTION = e -> {
        
    };
}
