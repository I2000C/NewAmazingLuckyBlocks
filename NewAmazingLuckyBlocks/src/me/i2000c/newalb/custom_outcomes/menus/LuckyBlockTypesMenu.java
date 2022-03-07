package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.utils.LuckyBlockType;
import me.i2000c.newalb.custom_outcomes.utils.TypeManager;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.utils2.ItemStackBuilder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LuckyBlockTypesMenu{
    
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
        
        ItemStack deleteType = ItemStackBuilder
                .createNewItem(XMaterial.BARRIER)
                .withDisplayName("&cDelete existing lucky block type")
                .build();
        
        
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
        
        inv.setItem(41, placePermissionItem);
        inv.setItem(42, breakPermissionItem);
        
        inv.setItem(45, back);
        inv.setItem(46, createType);
        inv.setItem(47, deleteType);
        
        inv.setItem(50, requirePlacePermission);
        inv.setItem(51, requireBreakPermission);
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction LUCKY_BLOCK_TYPES_MENU_FUNCTION = e -> {
        
    };
    
    private static void openEditMenu(Player p){
        
    }
    
    private static final InventoryFunction LUCKY_BLOCK_TYPE_EDIT_MENU_FUNCTION = e -> {
        
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
