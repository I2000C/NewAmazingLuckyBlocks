package me.i2000c.newalb.lucky_blocks.editors.menus;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.functions.InventoryFunction;
import me.i2000c.newalb.api.gui.CustomInventoryType;
import me.i2000c.newalb.api.gui.GUIFactory;
import me.i2000c.newalb.api.gui.InventoryLocation;
import me.i2000c.newalb.api.gui.Menu;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.lucky_blocks.editors.Editor;
import me.i2000c.newalb.lucky_blocks.editors.EditorType;
import me.i2000c.newalb.lucky_blocks.rewards.TypeManager;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MainMenu extends Editor{
    public MainMenu(){
        InventoryListener.registerInventory(CustomInventoryType.MAIN_MENU, MAIN_MENU_FUNCTION);
    }
    
    @Override
    protected void newItem(Player player){
        openMainMenu(player);
    }
    
    @Override
    protected void editItem(Player player){
        openMainMenu(player);
    }
    
    private void openMainMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.MAIN_MENU, 9, "&a&lMain menu");
        
        ItemStack exit = ItemStackWrapper.newItem(XMaterial.IRON_DOOR)
                                         .setDisplayName("&cExit")
                                         .toItemStack();
        
        ItemStack packsItem = ItemStackWrapper.newItem(XMaterial.CRAFTING_TABLE)
                                              .setDisplayName("&3Manage outcome packs")
                                              .toItemStack();
        
        ItemStack typesItem = ItemStackWrapper.fromItem(TypeManager.getMenuItemStack())
                                              .setDisplayName("&6Manage lucky block types")
                                              .toItemStack();
        
        menu.setItem(3, packsItem);
        menu.setItem(5, typesItem);
        menu.setItem(8, exit);
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction MAIN_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 3:
                    //Open outcome packs menu
                    Editor editor = EditorType.PACK_LIST.getEditor();
                    editor.createNewItem(
                            player, 
                            p -> openMainMenu(p), 
                            null);
                    break;
                case 5:
                    //Open lucky block types menu
                    editor = EditorType.LUCKY_BLOCK_TYPE_LIST.getEditor();
                    editor.createNewItem(
                            player, 
                            p -> openMainMenu(p), 
                            null);
                    break;
                case 8:
                    //Exit
                    onBack.accept(player);
                    break;
            }
        }
//</editor-fold>
    };
}
