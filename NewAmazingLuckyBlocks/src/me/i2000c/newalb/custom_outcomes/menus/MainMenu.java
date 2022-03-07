package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.CommandManager;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.utils2.ItemStackBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MainMenu{    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.MAIN_MENU, MAIN_MENU_FUNCTION);
            
            inventoriesRegistered = true;
        }
        
        CommandManager.confirmMenu = false;
    }
    
    public static void openMainMenu(Player p){
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.MAIN_MENU, 9, "&a&lMain menu");
        
        ItemStack exit = ItemStackBuilder
                .createNewItem(XMaterial.IRON_DOOR)
                .withDisplayName("&cExit")
                .build();
        
        ItemStack packsItem = ItemStackBuilder
                .createNewItem(XMaterial.CRAFTING_TABLE)
                .withDisplayName("&aManage outcome packs")
                .build();
        
        ItemStack typesItem = ItemStackBuilder
                .createNewItem(XMaterial.SPONGE)
                .withDisplayName("&6Manage lucky block types")
                .build();
        
        inv.setItem(3, packsItem);
        inv.setItem(5, typesItem);
        inv.setItem(8, exit);
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
    }
    
    private static final InventoryFunction MAIN_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            switch(e.getSlot()){
                case 3:
                    //Open outcome packs menu
                    GUIPackManager.reset();
                    GUIPackManager.openMainMenu(p);
                    break;
                case 5:
                    //Open lucky block types menu
                    LuckyBlockTypesMenu.reset();
                    LuckyBlockTypesMenu.openMainMenu(p);
                    break;
                case 8:
                    //Exit
                    reset();
                    GUIManager.setCurrentInventory(null);
                    p.closeInventory();
                    break;
            }
        }
//</editor-fold>
    };
}
