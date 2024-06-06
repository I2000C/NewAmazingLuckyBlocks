package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.DarkHoleReward;
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

public class DarkHoleMenu extends Editor<DarkHoleReward>{
    public DarkHoleMenu(){
        InventoryListener.registerInventory(CustomInventoryType.DARK_HOLE_MENU, DARK_HOLE_MENU_FUNCTION);
    }
    
    @Override
    public void newItem(Player player){
        Outcome outcome = RewardListMenu.getCurrentOutcome();
        item = new DarkHoleReward(outcome);
        openDarkHoleMenu(player);
    }
    
    @Override
    public void editItem(Player player){
        openDarkHoleMenu(player);
    }
    
    private void openDarkHoleMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.DARK_HOLE_MENU, 45, "&0&lDarkHole Rewards");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.GRAY);
        
        for(int i=0;i<9;i++){
            menu.setItem(i, glass);
        }
        for(int i=36;i<45;i++){
            menu.setItem(i, glass);
        }
        menu.setItem(9, glass);
        menu.setItem(17, glass);
        menu.setItem(27, glass);
        menu.setItem(35, glass);
        
        ItemStackWrapper builder;
        if(item.isSquared()){
            builder = ItemStackWrapper.newItem(XMaterial.SNOW_BLOCK);
            builder.setDisplayName("&bSquared: &atrue");
        }else{
            builder = ItemStackWrapper.newItem(XMaterial.SNOWBALL);
            builder.setDisplayName("&bSquared: &7false");
        }
        builder.addLoreLine("&3Click to toggle");
        ItemStack squaredStack = builder.toItemStack();
        
        builder = ItemStackWrapper.newItem(XMaterial.BEDROCK);
        if(item.getDepth() < 0){
            builder.setDisplayName("&3Depth: &binfinite");
        }else{
            builder.setDisplayName("&3Depth: &b" + item.getDepth());
        }
        ItemStack depthStack = builder.toItemStack();
        
        ItemStack radiusStack = ItemStackWrapper.newItem(XMaterial.HOPPER)
                                                .setDisplayName("&6Radius: &2" + item.getRadius())
                                                .addLoreLine("&3Click to reset")
                                                .toItemStack();
        
        ItemStack ticksStack = ItemStackWrapper.newItem(XMaterial.CLOCK)
                                               .setDisplayName("&5Ticks between blocks: &6" + item.getTicks())
                                               .addLoreLine("&3Click to reset")
                                               .toItemStack();
        
        menu.setItem(18, GUIItem.getBackItem());
        menu.setItem(26, GUIItem.getNextItem());
        
        menu.setItem(10, GUIItem.getPlusLessItem(-10));
        menu.setItem(11, GUIItem.getPlusLessItem(-1));
        menu.setItem(12, depthStack);
        menu.setItem(13, GUIItem.getPlusLessItem(+1));
        menu.setItem(14, GUIItem.getPlusLessItem(+10));
        
        menu.setItem(19, GUIItem.getPlusLessItem(-5));
        menu.setItem(20, GUIItem.getPlusLessItem(-1));
        menu.setItem(21, radiusStack);
        menu.setItem(22, GUIItem.getPlusLessItem(+1));
        menu.setItem(23, GUIItem.getPlusLessItem(+5));
        
        menu.setItem(24, squaredStack);
        
        menu.setItem(28, GUIItem.getPlusLessItem(-10));
        menu.setItem(29, GUIItem.getPlusLessItem(-1));
        menu.setItem(30, ticksStack);
        menu.setItem(31, GUIItem.getPlusLessItem(+1));
        menu.setItem(32, GUIItem.getPlusLessItem(+10));
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction DARK_HOLE_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 18:
                    //Back
                    onBack.accept(player);
                    break;
                case 26:
                    //Next
                    onNext.accept(player, item);
                    break;
                //<editor-fold defaultstate="collapsed" desc="Depth">
                case 10:
                    //Depth -10
                    int depth = item.getDepth() - 10;
                    if(depth < -1){
                        item.setDepth(-1);
                    }else{
                        item.setDepth(depth);
                    }
                    openDarkHoleMenu(player);
                    break;
                case 11:
                    //Depth -1
                    depth = item.getDepth() - 1;
                    if(depth < -1){
                        item.setDepth(-1);
                    }else{
                        item.setDepth(depth);
                    }
                    openDarkHoleMenu(player);
                    break;
                case 12:
                    //Depth = 1
                    item.setDepth(1);
                    openDarkHoleMenu(player);
                    break;
                case 13:
                    //Depath +1
                    item.setDepth(item.getDepth() + 1);
                    openDarkHoleMenu(player);
                    break;
                case 14:
                    //Depth +10
                    item.setDepth(item.getDepth() + 10);
                    openDarkHoleMenu(player);
                    break;
//</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Radius">
                case 19:
                    //Radius -5
                    int radius = item.getRadius() - 5;
                    if(radius < 1){
                        item.setRadius(1);
                    }else{
                        item.setRadius(radius);
                    }
                    openDarkHoleMenu(player);
                    break;                        
                case 20:
                    //Radius -1
                    radius = item.getRadius() - 1;
                    if(radius < 1){
                        item.setRadius(1);
                    }else{
                        item.setRadius(radius);
                    }
                    openDarkHoleMenu(player);
                    break;
                case 21:
                    //Radius = 1
                    item.setRadius(1);
                    openDarkHoleMenu(player);
                    break;
                case 22:
                    //Radius +1
                    item.setRadius(item.getRadius() + 1);
                    openDarkHoleMenu(player);
                    break;
                case 23:
                    //Radius +5
                    item.setRadius(item.getRadius() + 5);
                    openDarkHoleMenu(player);
                    break;
//</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Ticks">
                case 28:
                    //Ticks -10
                    long ticks = item.getTicks() - 10;
                    if(ticks < 0){
                        item.setTicks(0);
                    }else{
                        item.setTicks(ticks);
                    }
                    openDarkHoleMenu(player);
                    break;
                case 29:
                    //Ticks -1
                    ticks = item.getTicks() - 1;
                    if(ticks < 0){
                        item.setTicks(0);
                    }else{
                        item.setTicks(ticks);
                    }
                    openDarkHoleMenu(player);
                    break;
                case 30:
                    //Ticks = 0
                    item.setTicks(0);
                    openDarkHoleMenu(player);
                    break;
                case 31:
                    //Depath +1
                    item.setTicks(item.getTicks() + 1);
                    openDarkHoleMenu(player);
                    break;
                case 32:
                    //Ticks +10
                    item.setTicks(item.getTicks() + 10);
                    openDarkHoleMenu(player);
                    break;
//</editor-fold>
                case 24:
                    //Toggle isSquared
                    item.setSquared(!item.isSquared());
                    openDarkHoleMenu(player);
                    break;
            }
        }
        //</editor-fold>
    };
}
