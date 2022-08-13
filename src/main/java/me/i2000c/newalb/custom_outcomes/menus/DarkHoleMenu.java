package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.utils.rewards.DarkHoleReward;
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
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.GRAY);
        
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
        
        ItemBuilder builder;
        if(reward.isSquared()){
            builder = ItemBuilder.newItem(XMaterial.SNOW_BLOCK);
            builder.withDisplayName("&bSquared: &atrue");
        }else{
            builder = ItemBuilder.newItem(XMaterial.SNOWBALL);
            builder.withDisplayName("&bSquared: &7false");
        }
        builder.addLoreLine("&3Click to toggle");
        ItemStack squaredStack = builder.build();
        
        builder = ItemBuilder.newItem(XMaterial.BEDROCK);
        if(reward.getDepth() < 0){
            builder.withDisplayName("&3Depth: &binfinite");
        }else{
            builder.withDisplayName("&3Depth: &b" + reward.getDepth());
        }
        ItemStack depthStack = builder.build();
        
        ItemStack radiusStack = ItemBuilder.newItem(XMaterial.HOPPER)
                .withDisplayName("&6Radius: &2" + reward.getRadius())
                .addLoreLine("&3Click to reset")
                .build();
        
        ItemStack ticksStack = ItemBuilder.newItem(XMaterial.CLOCK)
                .withDisplayName("&5Ticks between blocks: &6" + reward.getTicks())
                .addLoreLine("&3Click to reset")
                .build();
        
        inv.setItem(18, GUIItem.getBackItem());
        inv.setItem(26, GUIItem.getNextItem());
        
        inv.setItem(10, GUIItem.getPlusLessItem(-10));
        inv.setItem(11, GUIItem.getPlusLessItem(-1));
        inv.setItem(12, depthStack);
        inv.setItem(13, GUIItem.getPlusLessItem(+1));
        inv.setItem(14, GUIItem.getPlusLessItem(+10));
        
        inv.setItem(19, GUIItem.getPlusLessItem(-1));
        inv.setItem(20, GUIItem.getPlusLessItem(-0.5f));
        inv.setItem(21, radiusStack);
        inv.setItem(22, GUIItem.getPlusLessItem(+0.5f));
        inv.setItem(23, GUIItem.getPlusLessItem(+1));
        
        inv.setItem(24, squaredStack);
        
        inv.setItem(28, GUIItem.getPlusLessItem(-10));
        inv.setItem(29, GUIItem.getPlusLessItem(-1));
        inv.setItem(30, ticksStack);
        inv.setItem(31, GUIItem.getPlusLessItem(+1));
        inv.setItem(32, GUIItem.getPlusLessItem(+10));
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction DARK_HOLE_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
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
