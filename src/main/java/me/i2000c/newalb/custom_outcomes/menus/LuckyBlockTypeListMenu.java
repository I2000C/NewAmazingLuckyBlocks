package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.editor.EditorType;
import me.i2000c.newalb.custom_outcomes.rewards.LuckyBlockType;
import me.i2000c.newalb.custom_outcomes.rewards.TypeManager;
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
import me.i2000c.newalb.utils2.ItemStackWrapper;
import me.i2000c.newalb.utils2.OtherUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LuckyBlockTypeListMenu extends Editor{
    public LuckyBlockTypeListMenu(){
        InventoryListener.registerInventory(CustomInventoryType.LUCKY_BLOCK_TYPES_MENU, LUCKY_BLOCK_TYPES_MENU_FUNCTION);
    }
    
    private boolean renameMode;
    private boolean deleteMode;
    
    @Override
    protected void reset(){
        renameMode = false;
        deleteMode = false;
    }
    
    @Override
    protected void newItem(Player player){
        openLuckyBlockTypeListMenu(player);
    }
    
    @Override
    protected void editItem(Player player){
        openLuckyBlockTypeListMenu(player);
    }
    
    private void openLuckyBlockTypeListMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String placePermission = TypeManager.getGlobalPlacePermission();
        String breakPermission = TypeManager.getGlobalBreakPermission();
        boolean placePermissionEnabled = TypeManager.isGlobalPlacePermissionEnabled();
        boolean breakPermissionEnabled = TypeManager.isGlobalBreakPermissionEnabled();
        
        Menu menu = GUIFactory.newMenu(CustomInventoryType.LUCKY_BLOCK_TYPES_MENU, 54, "&6&lLucky Block Types");
        
        for(int i=0; i<27 && i<TypeManager.getTypes().size(); i++){
            ItemStack packItem = TypeManager.getTypes().get(i).getItemToDisplay();
            menu.setItem(i, packItem);
        }
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.LIGHT_BLUE);
        
        for(int i=27; i<36; i++){
            menu.setItem(i, glass);
        }
        
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
        
        menu.setItem(45, GUIItem.getBackItem());
        
        if(!deleteMode){
            menu.setItem(47, renameType);
        }
        
        if(!renameMode){
            menu.setItem(48, deleteType);
        }        
        
        if(!renameMode && !deleteMode){
            menu.setItem(41, placePermissionItem);
            menu.setItem(42, breakPermissionItem);

            menu.setItem(46, createType);

            menu.setItem(50, requirePlacePermission);
            menu.setItem(51, requireBreakPermission);
        }
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction LUCKY_BLOCK_TYPES_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 45:
                    // Go to previous menu
                    onBack.accept(player);
                    break;
                case 46:
                    //Create new Lucky Block type
                    if(deleteMode){
                        break;
                    }
                    player.closeInventory();
                    Logger.sendMessage("&3Enter the new lucky block type identifier and press ENTER", player);
                    
                    ChatListener.registerPlayer(player, message -> {
                        message = OtherUtils.removeExtension(message);
                        if(TypeManager.PERMISSIONS_FILENAME.equals(message + ".yml")){
                            Logger.sendMessage("&cInvalid LuckyBlock identifier", player, false);
                        }else if(TypeManager.getType(message) != null){
                            Logger.sendMessage("&cThat identifier already exists", player, false);
                        }else{
                            ChatListener.removePlayer(player);
                            LuckyBlockType luckyBlockType = new LuckyBlockType(message);
                            Editor<LuckyBlockType> editor = EditorType.LUCKY_BLOCK_TYPE.getEditor();
                            editor.editExistingItem(
                                    luckyBlockType, 
                                    player, 
                                    p -> openLuckyBlockTypeListMenu(p), 
                                    (p, type) -> {
                                        TypeManager.addType(type);
                                        openLuckyBlockTypeListMenu(p);
                                    });
                        }
                    }, false);
                    break;
                case 47:
                    //Toggle rename types mode
                    if(deleteMode){
                        break;
                    }
                    
                    renameMode = !renameMode;
                    openLuckyBlockTypeListMenu(player);
                    break;
                case 48:
                    //Toggle delete types mode
                    if(renameMode){
                        break;
                    }
                    
                    deleteMode = !deleteMode;
                    openLuckyBlockTypeListMenu(player);
                    break;
                case 41:
                    //Change global place permission
                    if(renameMode || deleteMode){
                        break;
                    }
                    
                    player.closeInventory();
                    Logger.sendMessage("&3Enter the new global &dplace &3permission and then press ENTER.", player);
                    Logger.sendMessage("&bTo return without change the permission, type &a/alb return", player, false);
                    ChatListener.registerPlayer(player, message -> {
                        TypeManager.setGlobalPlacePermission(message);
                        openLuckyBlockTypeListMenu(player);
                    });
                    break;
                case 42:
                    //Change global break permission
                    if(renameMode || deleteMode){
                        break;
                    }
                    
                    player.closeInventory();
                    Logger.sendMessage("&3Enter the new global &dbreak &3permission and then press ENTER.", player);
                    Logger.sendMessage("&bTo return without change the permission, type &a/alb return", player, false);
                    ChatListener.registerPlayer(player, message -> {
                        TypeManager.setGlobalBreakPermission(message);
                        openLuckyBlockTypeListMenu(player);
                    });
                    break;
                case 50:
                    //Toggle global place permission
                    if(renameMode || deleteMode){
                        break;
                    }
                    
                    boolean requirePermission = TypeManager.isGlobalPlacePermissionEnabled();
                    TypeManager.setEnableGlobalPlacePermission(!requirePermission);
                    openLuckyBlockTypeListMenu(player);
                    break;
                case 51:
                    //Toggle global break permission
                    if(renameMode || deleteMode){
                        break;
                    }
                    
                    requirePermission = TypeManager.isGlobalBreakPermissionEnabled();
                    TypeManager.setEnableGlobalBreakPermission(!requirePermission);
                    openLuckyBlockTypeListMenu(player);
                    break;
                default:
                    if(e.getSlot() < 27){
                        if(e.getSlot() < TypeManager.getTypes().size()){
                            if(renameMode){
                                //Change type name of selected type
                                renameMode = false;
                                openLuckyBlockTypeListMenu(player);
                                player.closeInventory();
                                Logger.sendMessage("&3Enter the new identifier for this lucky block type.", player);
                                Logger.sendMessage("  &3If you don't want to change it, use &a/alb return", player, false);
                                ChatListener.registerPlayer(player, message -> {
                                    message = OtherUtils.removeExtension(message);
                                    if(TypeManager.PERMISSIONS_FILENAME.equals(message + ".yml")){
                                        Logger.sendMessage("&cInvalid LuckyBlock identifier", player, false);
                                    }else if(TypeManager.getType(message) != null){
                                        Logger.sendMessage("&cThat identifier already exists", player, false);
                                    }else{
                                        ChatListener.removePlayer(player);
                                        TypeManager.renameType(e.getSlot(), message);
                                        openLuckyBlockTypeListMenu(player);
                                    }
                                }, false);
                            }else if(deleteMode){
                                //Delete selected type
                                TypeManager.removeType(e.getSlot());
                                openLuckyBlockTypeListMenu(player);
                            }else{
                                //Clone and edit selected type
                                LuckyBlockType luckyBlockType = TypeManager.getType(e.getSlot());
                                if(luckyBlockType != null){
                                    Editor<LuckyBlockType> editor = EditorType.LUCKY_BLOCK_TYPE.getEditor();
                                    editor.editExistingItem(
                                        luckyBlockType, 
                                        player, 
                                        p -> openLuckyBlockTypeListMenu(p), 
                                        (p, type) -> {
                                            TypeManager.addType(type);
                                            openLuckyBlockTypeListMenu(p);
                                        });
                                }
                            }
                        }
                    }
            }
        }
//</editor-fold>
    };
}
