package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DelayerMenu extends Editor<Integer>{
    public DelayerMenu(){
        InventoryListener.registerInventory(CustomInventoryType.DELAYER_MENU, DELAYER_MENU_FUNCTION);
    }

    @Override
    protected void newItem(Player player){
        item = 0;
        openDelayerMenu(player);
    }

    @Override
    protected void editItem(Player player){
        openDelayerMenu(player);
    }
        
    private void openDelayerMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.DELAYER_MENU, 27, "&5Configure Delay");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.BLUE);
        
        ItemStack delay = ItemStackWrapper.newItem(XMaterial.CLOCK)
                                          .setDisplayName("&6Delay: &b" + item + " &dtick(s)")
                                          .addLoreLine("        &a" + item/20.0 + " &dsecond(s)")
                                          .addLoreLine("&3Click to reset")
                                          .toItemStack();
        
        for(int i=0;i<=9;i++){
            menu.setItem(i, glass);
        }
        for(int i=17;i<27;i++){
            menu.setItem(i, glass);
        }
        
        menu.setItem(10, GUIItem.getBackItem());
        menu.setItem(16, GUIItem.getNextItem());
        
        menu.setItem(13, delay);
        
        menu.setItem(3, GUIItem.getPlusLessItem(+1));
        menu.setItem(4, GUIItem.getPlusLessItem(+10));
        menu.setItem(5, GUIItem.getPlusLessItem(+100));
        
        menu.setItem(21, GUIItem.getPlusLessItem(-1));
        menu.setItem(22, GUIItem.getPlusLessItem(-10));
        menu.setItem(23, GUIItem.getPlusLessItem(-100));
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction DELAYER_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 3:
                    item++;
                    openDelayerMenu(player);
                    break;
                case 4:
                    item += 10;
                    openDelayerMenu(player);
                    break;
                case 5:
                    item += 100;
                    openDelayerMenu(player);
                    break;
                case 13:
                    item = 0;
                    openDelayerMenu(player);
                    break;
                case 21:
                    item--;
                    if(item < 0){
                        item = 0;
                    }
                    openDelayerMenu(player);
                    break;
                case 22:
                    item -= 10;
                    if(item < 0){
                        item = 0;
                    }
                    openDelayerMenu(player);
                    break;
                case 23:
                    item -= 100;
                    if(item < 0){
                        item = 0;
                    }
                    openDelayerMenu(player);
                    break;
                case 10:
                    // Go to previous menu
                    onBack.accept(player);
                    break;
                case 16:
                    // Go to next menu
                    onNext.accept(player, item);
                    break;                
            }
        }
//</editor-fold>
    };
}
