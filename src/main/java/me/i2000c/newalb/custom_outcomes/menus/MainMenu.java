package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.editor.EditorType;
import me.i2000c.newalb.custom_outcomes.rewards.TypeManager;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils2.ItemBuilder;
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
        
        ItemStack exit = ItemBuilder
                .newItem(XMaterial.IRON_DOOR)
                .withDisplayName("&cExit")
                .build();
        
        ItemStack packsItem = ItemBuilder
                .newItem(XMaterial.CRAFTING_TABLE)
                .withDisplayName("&3Manage outcome packs")
                .build();
        
        ItemStack typesItem = ItemBuilder
                .fromItem(TypeManager.getMenuItemStack())
                .withDisplayName("&6Manage lucky block types")
                .build();
        
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
