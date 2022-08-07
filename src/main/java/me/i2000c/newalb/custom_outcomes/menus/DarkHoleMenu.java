package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.utils.rewards.DarkHoleReward;
import me.i2000c.newalb.utils.Logger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DarkHoleMenu{
    public static DarkHoleReward reward;
    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.DARK_HOLE_MENU, DARK_HOLE_MENU_FUNCTION);
            
            inventoriesRegistered = true;
        }
        
        reward = null;
    }
    
    public static void openDarkHoleMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(reward == null){
            reward = new DarkHoleReward(FinishMenu.getCurrentOutcome());
        }
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.DARK_HOLE_MENU, 45, "&0&lDarkHole Rewards");
        
        ItemMeta meta;
        
        ItemStack glass = XMaterial.GRAY_STAINED_GLASS_PANE.parseItem();
        meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        
        for(int i=0;i<9;i++){
            inv.setItem(i, glass);
        }
        for(int i=36;i<45;i++){
            inv.setItem(i, glass);
        }
        inv.setItem(9, glass);
        inv.setItem(17, glass);
        inv.setItem(27, glass);
        inv.setItem(35, glass);
        
        ItemStack plus1 = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
        meta = plus1.getItemMeta();
        meta.setDisplayName("&a&l+1");
        plus1.setItemMeta(meta);
        
        ItemStack plus10 = plus1.clone();
        meta = plus10.getItemMeta();
        meta.setDisplayName("&a&l+10");
        plus10.setItemMeta(meta);
        
        ItemStack plus0_5 = plus1.clone();
        meta = plus0_5.getItemMeta();
        meta.setDisplayName("&a&l+0.5");
        plus0_5.setItemMeta(meta);
        
        ItemStack minus1 = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
        meta = minus1.getItemMeta();
        meta.setDisplayName("&c&l-1");
        minus1.setItemMeta(meta);
        
        ItemStack minus10 = minus1.clone();
        meta = minus10.getItemMeta();
        meta.setDisplayName("&c&l-10");
        minus10.setItemMeta(meta);
        
        ItemStack minus0_5 = minus1.clone();
        meta = minus0_5.getItemMeta();
        meta.setDisplayName("&c&l-0.5");
        minus0_5.setItemMeta(meta);
        
        
        ItemStack squaredStack;
        if(reward.isSquared()){
            squaredStack = new ItemStack(Material.SNOW_BLOCK);
            meta = squaredStack.getItemMeta();
            meta.setDisplayName("&bSquared: &atrue");
        }else{
            squaredStack = XMaterial.SNOWBALL.parseItem();
            meta = squaredStack.getItemMeta();
            meta.setDisplayName("&bSquared: &7false");
        }
        squaredStack.setItemMeta(meta);
        
        ItemStack depthStack = new ItemStack(Material.BEDROCK);
        meta = depthStack.getItemMeta();
        if(reward.getDepth() < 0){
            meta.setDisplayName("&3Depth: &binfinite");
        }else{
            meta.setDisplayName("&3Depth: &b" + reward.getDepth());
        }
        depthStack.setItemMeta(meta);
        
        ItemStack radiusStack = new ItemStack(Material.HOPPER);
        meta = radiusStack.getItemMeta();
        meta.setDisplayName("&6Radius: &2" + reward.getRadius());
        radiusStack.setItemMeta(meta);
        
        ItemStack ticksStack = XMaterial.CLOCK.parseItem();
        meta = ticksStack.getItemMeta();
        meta.setDisplayName("&5Ticks between blocks: &6" + reward.getTicks());
        ticksStack.setItemMeta(meta);
        
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName("&2Back");
        back.setItemMeta(meta);
        
        ItemStack next = new ItemStack(Material.ANVIL);
        meta = next.getItemMeta();
        meta.setDisplayName("&bNext");
        next.setItemMeta(meta);
        
        inv.setItem(18, back);
        inv.setItem(26, next);
        
        inv.setItem(10, minus10);
        inv.setItem(11, minus1);
        inv.setItem(12, depthStack);
        inv.setItem(13, plus1);
        inv.setItem(14, plus10);
        
        inv.setItem(19, minus1);
        inv.setItem(20, minus0_5);
        inv.setItem(21, radiusStack);
        inv.setItem(22, plus0_5);
        inv.setItem(23, plus1);
        
        inv.setItem(24, squaredStack);
        
        inv.setItem(28, minus10);
        inv.setItem(29, minus1);
        inv.setItem(30, ticksStack);
        inv.setItem(31, plus1);
        inv.setItem(32, plus10);
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction DARK_HOLE_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            switch(e.getSlot()){
                case 18:
                    //Back
                    reset();
                    if(FinishMenu.editMode){
                        FinishMenu.openFinishInventory(p);
                    }else{
                        RewardTypesMenu.openRewardTypesMenu(p);
                    }
                    break;
                case 26:
                    //Next
                    FinishMenu.addReward(reward);
                    reset();
                    FinishMenu.openFinishInventory(p);
                    break;
                //<editor-fold defaultstate="collapsed" desc="Depth">
                case 10:
                    //Depth -10
                    int depth = reward.getDepth() - 10;
                    if(depth < -1){
                        reward.setDepth(-1);
                    }else{
                        reward.setDepth(depth);
                    }
                    openDarkHoleMenu(p);
                    break;
                case 11:
                    //Depth -1
                    depth = reward.getDepth() - 1;
                    if(depth < -1){
                        reward.setDepth(-1);
                    }else{
                        reward.setDepth(depth);
                    }
                    openDarkHoleMenu(p);
                    break;
                case 12:
                    //Depth = 1
                    reward.setDepth(1);
                    openDarkHoleMenu(p);
                    break;
                case 13:
                    //Depath +1
                    reward.setDepth(reward.getDepth() + 1);
                    openDarkHoleMenu(p);
                    break;
                case 14:
                    //Depth +10
                    reward.setDepth(reward.getDepth() + 10);
                    openDarkHoleMenu(p);
                    break;
//</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Radius">
                case 19:
                    //Radius -1
                    double radius = reward.getRadius() - 1.0;
                    if(radius < 1.0){
                        reward.setRadius(1.0);
                    }else{
                        reward.setRadius(radius);
                    }
                    openDarkHoleMenu(p);
                    break;                        
                case 20:
                    //Radius -0.5
                    radius = reward.getRadius() - 0.5;
                    if(radius < 1.0){
                        reward.setRadius(1.0);
                    }else{
                        reward.setRadius(radius);
                    }
                    openDarkHoleMenu(p);
                    break;
                case 21:
                    //Radius = 1
                    reward.setRadius(1.0);
                    openDarkHoleMenu(p);
                    break;
                case 22:
                    //Radius +0.5
                    reward.setRadius(reward.getRadius() + 0.5);
                    openDarkHoleMenu(p);
                    break;
                case 23:
                    //Radius +1.0
                    reward.setRadius(reward.getRadius() + 1.0);
                    openDarkHoleMenu(p);
                    break;
//</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Ticks">
                case 28:
                    //Ticks -10
                    long ticks = reward.getTicks() - 10;
                    if(ticks < 0){
                        reward.setTicks(0);
                    }else{
                        reward.setTicks(ticks);
                    }
                    openDarkHoleMenu(p);
                    break;
                case 29:
                    //Ticks -1
                    ticks = reward.getTicks() - 1;
                    if(ticks < 0){
                        reward.setTicks(0);
                    }else{
                        reward.setTicks(ticks);
                    }
                    openDarkHoleMenu(p);
                    break;
                case 30:
                    //Ticks = 0
                    reward.setTicks(0);
                    openDarkHoleMenu(p);
                    break;
                case 31:
                    //Depath +1
                    reward.setTicks(reward.getTicks() + 1);
                    openDarkHoleMenu(p);
                    break;
                case 32:
                    //Ticks +10
                    reward.setTicks(reward.getTicks() + 10);
                    openDarkHoleMenu(p);
                    break;
//</editor-fold>
                case 24:
                    //Toggle isSquared
                    reward.setSquared(!reward.isSquared());
                    openDarkHoleMenu(p);
                    break;
            }
        }
        //</editor-fold>
    };
}
