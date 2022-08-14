package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.utils.rewards.Reward;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DelayerMenu{
    public static Reward reward;
    private static int delayValue;
    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.DELAYER_MENU, DELAYER_MENU_FUNCTION);
            
            inventoriesRegistered = true;
        }
        
        delayValue = -1;
        reward = null;
    }
        
    public static void openDelayerMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.DELAYER_MENU, 27, "&5Configure Delay");
        GUIManager.setCurrentInventory(inv);
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.BLUE);
        
        if(delayValue < 0){
            delayValue = reward.getDelay();
        }
        ItemStack delay = ItemBuilder.newItem(XMaterial.CLOCK)
                .withDisplayName("&6Delay: &b" + delayValue + " &dtick(s)")
                .addLoreLine("        &a" + delayValue/20.0 + " &dsecond(s)")
                .addLoreLine("&3Click to reset")
                .build();
        
        for(int i=0;i<=9;i++){
            inv.setItem(i, glass);
        }
        for(int i=17;i<27;i++){
            inv.setItem(i, glass);
        }
        
        inv.setItem(10, GUIItem.getBackItem());
        inv.setItem(16, GUIItem.getNextItem());
        
        inv.setItem(13, delay);
        
        inv.setItem(3, GUIItem.getPlusLessItem(+1));
        inv.setItem(4, GUIItem.getPlusLessItem(+10));
        inv.setItem(5, GUIItem.getPlusLessItem(+100));
        
        inv.setItem(21, GUIItem.getPlusLessItem(-1));
        inv.setItem(22, GUIItem.getPlusLessItem(-10));
        inv.setItem(23, GUIItem.getPlusLessItem(-100));
        
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction DELAYER_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 3:
                    delayValue++;
                    openDelayerMenu(p);
                    break;
                case 4:
                    delayValue += 10;
                    openDelayerMenu(p);
                    break;
                case 5:
                    delayValue += 100;
                    openDelayerMenu(p);
                    break;
                case 13:
                    delayValue = 0;
                    openDelayerMenu(p);
                    break;
                case 21:
                    delayValue--;
                    if(delayValue < 0){
                        delayValue = 0;
                    }
                    openDelayerMenu(p);
                    break;
                case 22:
                    delayValue -= 10;
                    if(delayValue < 0){
                        delayValue = 0;
                    }
                    openDelayerMenu(p);
                    break;
                case 23:
                    delayValue -= 100;
                    if(delayValue < 0){
                        delayValue = 0;
                    }
                    openDelayerMenu(p);
                    break;
                case 16:
                    reward.setDelay(delayValue);
                case 10:
                    reset();
                    RewardListMenu.openFinishInventory(p);
                    break;
            }
        }
//</editor-fold>
    };
}
