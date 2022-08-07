package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.utils.rewards.Reward;
import me.i2000c.newalb.utils.logger.Logger;
import java.util.Arrays;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        
        ItemStack glass = XMaterial.BLUE_STAINED_GLASS_PANE.parseItem();
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        
        if(delayValue < 0){
            delayValue = reward.getDelay();
        }
        ItemStack delay = XMaterial.CLOCK.parseItem();
        meta = delay.getItemMeta();
        meta.setDisplayName("&6Delay: &b" + delayValue + " &dtick(s)");
        meta.setLore(Arrays.asList("        &a" + delayValue/20.0 + " &dsecond(s)", "&3Click to reset"));
        delay.setItemMeta(meta);
        
        ItemStack plus = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
        meta = plus.getItemMeta();
        meta.setDisplayName("&a&l+1");
        plus.setItemMeta(meta);
        ItemStack plus10 = plus.clone();
        meta = plus10.getItemMeta();
        meta.setDisplayName("&a&l+10");
        plus10.setItemMeta(meta);
        ItemStack plus100 = plus.clone();
        meta = plus100.getItemMeta();
        meta.setDisplayName("&a&l+100");
        plus100.setItemMeta(meta);
        
        ItemStack less = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
        meta = less.getItemMeta();
        meta.setDisplayName("&c&l-1");
        less.setItemMeta(meta);
        ItemStack less10 = less.clone();
        meta = less10.getItemMeta();
        meta.setDisplayName("&c&l-10");
        less10.setItemMeta(meta);
        ItemStack less100 = less.clone();
        meta = less100.getItemMeta();
        meta.setDisplayName("&c&l-100");
        less100.setItemMeta(meta);
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName("&7Back");
        back.setItemMeta(meta);
        
        ItemStack next = new ItemStack(Material.ANVIL);
        meta = next.getItemMeta();
        meta.setDisplayName("&bNext");
        next.setItemMeta(meta);
        
        for(int i=0;i<=9;i++){
            inv.setItem(i, glass);
        }
        for(int i=17;i<27;i++){
            inv.setItem(i, glass);
        }
        
        inv.setItem(10, back);
        inv.setItem(16, next);
        
        inv.setItem(13, delay);
        
        inv.setItem(3, plus);
        inv.setItem(4, plus10);
        inv.setItem(5, plus100);
        
        inv.setItem(21, less);
        inv.setItem(22, less10);
        inv.setItem(23, less100);
        
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction DELAYER_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
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
                    FinishMenu.openFinishInventory(p);
                    break;
            }
        }
//</editor-fold>
    };
}
