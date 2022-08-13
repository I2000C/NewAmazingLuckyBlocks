package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.Offset;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class OffsetMenu{
    private static Offset oldOffset;
    private static Offset newOffset;
    
    private static PlayerFunction playerFunction;
    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.OFFSET_MENU, OFFSET_MENU_FUNCTION);
            
            inventoriesRegistered = true;
        }
        
        oldOffset = null;
        newOffset = null;
        
        playerFunction = null;
    }
    
    public static void setCurrentData(Offset offset, PlayerFunction function){
        oldOffset = offset;
        newOffset = oldOffset.clone();
        playerFunction = function;
    }
    
    public static void openOffsetMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.OFFSET_MENU, 54, "&dOffset menu");
        
        for(int i=3;i<=48;i+=9){
            inv.setItem(i, GUIItem.getPlusLessItem(-1));
        }        
        for(int i=2;i<=47;i+=9){
            inv.setItem(i, GUIItem.getPlusLessItem(-10));
        }        
        for(int i=1;i<=46;i+=9){
            inv.setItem(i, GUIItem.getPlusLessItem(-100));
        }
        
        for(int i=5;i<=50;i+=9){
            inv.setItem(i, GUIItem.getPlusLessItem(+1));
        }
        for(int i=6;i<=51;i+=9){
            inv.setItem(i, GUIItem.getPlusLessItem(+10));
        }
        for(int i=7;i<=52;i+=9){
            inv.setItem(i, GUIItem.getPlusLessItem(+100));
        }
        
        ItemStack minX = ItemBuilder.newItem(XMaterial.GRASS_BLOCK)
                .withDisplayName("&cMin X: &6" + newOffset.getOffsetX().getMin())
                .addLoreLine("&3Click to reset")
                .build();
        
        ItemStack maxX = ItemBuilder.newItem(XMaterial.GRASS_BLOCK)
                .withDisplayName("&cMax X: &6" + newOffset.getOffsetX().getMax())
                .addLoreLine("&3Click to reset")
                .build();
        
        ItemStack minY = ItemBuilder.newItem(XMaterial.DIRT)
                .withDisplayName("&aMin Y: &6" + newOffset.getOffsetY().getMin())
                .addLoreLine("&3Click to reset")
                .build();
        
        ItemStack maxY = ItemBuilder.newItem(XMaterial.DIRT)
                .withDisplayName("&aMax Y: &6" + newOffset.getOffsetY().getMax())
                .addLoreLine("&3Click to reset")
                .build();        
        
        ItemStack minZ = ItemBuilder.newItem(XMaterial.STONE)
                .withDisplayName("&bMin Z: &6" + newOffset.getOffsetZ().getMin())
                .addLoreLine("&3Click to reset")
                .build();
        
        ItemStack maxZ = ItemBuilder.newItem(XMaterial.STONE)
                .withDisplayName("&bMax Z: &6" + newOffset.getOffsetZ().getMax())
                .addLoreLine("&3Click to reset")
                .build();
        
        inv.setItem(4, minX);
        inv.setItem(13, maxX);
        inv.setItem(22, minY);
        inv.setItem(31, maxY);
        inv.setItem(40, minZ);
        inv.setItem(49, maxZ);
        
        inv.setItem(18, GUIItem.getBackItem());
        inv.setItem(26, GUIItem.getNextItem());
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction OFFSET_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 26:
                    oldOffset.setOffsetX(newOffset.getOffsetX());
                    oldOffset.setOffsetY(newOffset.getOffsetY());
                    oldOffset.setOffsetZ(newOffset.getOffsetZ());
                case 18:
                    if(playerFunction == null){
                        p.closeInventory();
                    }else{
                        playerFunction.execute(p);
                    }
                    reset();
                    break;
                    //<editor-fold defaultstate="collapsed" desc="Min X">
                case 1:
                    int minX = newOffset.getOffsetX().getMin();
                    if(!newOffset.getOffsetX().setMin(minX - 100)){
                        newOffset.getOffsetX().setMin(newOffset.getOffsetX().getMax());
                    }
                    break;
                case 2:
                    minX = newOffset.getOffsetX().getMin();
                    if(!newOffset.getOffsetX().setMin(minX - 10)){
                        newOffset.getOffsetX().setMin(newOffset.getOffsetX().getMax());
                    }
                    break;
                case 3:
                    minX = newOffset.getOffsetX().getMin();
                    if(!newOffset.getOffsetX().setMin(minX - 1)){
                        newOffset.getOffsetX().setMin(newOffset.getOffsetX().getMax());
                    }
                    break;
                case 4:
                    if(!newOffset.getOffsetX().setMin(0)){
                        newOffset.getOffsetX().setMin(newOffset.getOffsetX().getMax());
                    }
                    break;
                case 5:
                    minX = newOffset.getOffsetX().getMin();
                    if(!newOffset.getOffsetX().setMin(minX + 1)){
                        newOffset.getOffsetX().setMin(newOffset.getOffsetX().getMax());
                    }
                    break;
                case 6:
                    minX = newOffset.getOffsetX().getMin();
                    if(!newOffset.getOffsetX().setMin(minX + 10)){
                        newOffset.getOffsetX().setMin(newOffset.getOffsetX().getMax());
                    }
                    break;
                case 7:
                    minX = newOffset.getOffsetX().getMin();
                    if(!newOffset.getOffsetX().setMin(minX + 100)){
                        newOffset.getOffsetX().setMin(newOffset.getOffsetX().getMax());
                    }
                    break;
//</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Max X">
                case 10:
                    int maxX = newOffset.getOffsetX().getMax();
                    if(!newOffset.getOffsetX().setMax(maxX - 100)){
                        newOffset.getOffsetX().setMax(newOffset.getOffsetX().getMin());
                    }
                    break;
                case 11:
                    maxX = newOffset.getOffsetX().getMax();
                    if(!newOffset.getOffsetX().setMax(maxX - 10)){
                        newOffset.getOffsetX().setMax(newOffset.getOffsetX().getMin());
                    }
                    break;
                case 12:
                    maxX = newOffset.getOffsetX().getMax();
                    if(!newOffset.getOffsetX().setMax(maxX - 1)){
                        newOffset.getOffsetX().setMax(newOffset.getOffsetX().getMin());
                    }
                    break;
                case 13:
                    if(!newOffset.getOffsetX().setMax(0)){
                        newOffset.getOffsetX().setMax(newOffset.getOffsetX().getMin());
                    }
                    break;
                case 14:
                    maxX = newOffset.getOffsetX().getMax();
                    if(!newOffset.getOffsetX().setMax(maxX + 1)){
                        newOffset.getOffsetX().setMax(newOffset.getOffsetX().getMin());
                    }
                    break;
                case 15:
                    maxX = newOffset.getOffsetX().getMax();
                    if(!newOffset.getOffsetX().setMax(maxX + 10)){
                        newOffset.getOffsetX().setMax(newOffset.getOffsetX().getMin());
                    }
                    break;
                case 16:
                    maxX = newOffset.getOffsetX().getMax();
                    if(!newOffset.getOffsetX().setMax(maxX + 100)){
                        newOffset.getOffsetX().setMax(newOffset.getOffsetX().getMin());
                    }
                    break;
//</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Min Y">
                case 19:
                    int minY = newOffset.getOffsetY().getMin();
                    if(!newOffset.getOffsetY().setMin(minY - 100)){
                        newOffset.getOffsetY().setMin(newOffset.getOffsetY().getMax());
                    }
                    break;
                case 20:
                    minY = newOffset.getOffsetY().getMin();
                    if(!newOffset.getOffsetY().setMin(minY - 10)){
                        newOffset.getOffsetY().setMin(newOffset.getOffsetY().getMax());
                    }
                    break;
                case 21:
                    minY = newOffset.getOffsetY().getMin();
                    if(!newOffset.getOffsetY().setMin(minY - 1)){
                        newOffset.getOffsetY().setMin(newOffset.getOffsetY().getMax());
                    }
                    break;
                case 22:
                    if(!newOffset.getOffsetY().setMin(0)){
                        newOffset.getOffsetY().setMin(newOffset.getOffsetY().getMax());
                    }
                    break;
                case 23:
                    minY = newOffset.getOffsetY().getMin();
                    if(!newOffset.getOffsetY().setMin(minY + 1)){
                        newOffset.getOffsetY().setMin(newOffset.getOffsetY().getMax());
                    }
                    break;
                case 24:
                    minY = newOffset.getOffsetY().getMin();
                    if(!newOffset.getOffsetY().setMin(minY + 10)){
                        newOffset.getOffsetY().setMin(newOffset.getOffsetY().getMax());
                    }
                    break;
                case 25:
                    minY = newOffset.getOffsetY().getMin();
                    if(!newOffset.getOffsetY().setMin(minY + 100)){
                        newOffset.getOffsetY().setMin(newOffset.getOffsetY().getMax());
                    }
                    break;
//</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Max Y">
                case 28:
                    int maxY = newOffset.getOffsetY().getMax();
                    if(!newOffset.getOffsetY().setMax(maxY - 100)){
                        newOffset.getOffsetY().setMax(newOffset.getOffsetY().getMin());
                    }
                    break;
                case 29:
                    maxY = newOffset.getOffsetY().getMax();
                    if(!newOffset.getOffsetY().setMax(maxY - 10)){
                        newOffset.getOffsetY().setMax(newOffset.getOffsetY().getMin());
                    }
                    break;
                case 30:
                    maxY = newOffset.getOffsetY().getMax();
                    if(!newOffset.getOffsetY().setMax(maxY - 1)){
                        newOffset.getOffsetY().setMax(newOffset.getOffsetY().getMin());
                    }
                    break;
                case 31:
                    if(!newOffset.getOffsetY().setMax(0)){
                        newOffset.getOffsetY().setMax(newOffset.getOffsetY().getMin());
                    }
                    break;
                case 32:
                    maxY = newOffset.getOffsetY().getMax();
                    if(!newOffset.getOffsetY().setMax(maxY + 1)){
                        newOffset.getOffsetY().setMax(newOffset.getOffsetY().getMin());
                    }
                    break;
                case 33:
                    maxY = newOffset.getOffsetY().getMax();
                    if(!newOffset.getOffsetY().setMax(maxY + 10)){
                        newOffset.getOffsetY().setMax(newOffset.getOffsetY().getMin());
                    }
                    break;
                case 34:
                    maxY = newOffset.getOffsetY().getMax();
                    if(!newOffset.getOffsetY().setMax(maxY + 100)){
                        newOffset.getOffsetY().setMax(newOffset.getOffsetY().getMin());
                    }
                    break;
//</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Min Z">
                case 37:
                    int minZ = newOffset.getOffsetZ().getMin();
                    if(!newOffset.getOffsetZ().setMin(minZ - 100)){
                        newOffset.getOffsetZ().setMin(newOffset.getOffsetZ().getMax());
                    }
                    break;
                case 38:
                    minZ = newOffset.getOffsetZ().getMin();
                    if(!newOffset.getOffsetZ().setMin(minZ - 10)){
                        newOffset.getOffsetZ().setMin(newOffset.getOffsetZ().getMax());
                    }
                    break;
                case 39:
                    minZ = newOffset.getOffsetZ().getMin();
                    if(!newOffset.getOffsetZ().setMin(minZ - 1)){
                        newOffset.getOffsetZ().setMin(newOffset.getOffsetZ().getMax());
                    }
                    break;
                case 40:
                    if(!newOffset.getOffsetZ().setMin(0)){
                        newOffset.getOffsetZ().setMin(newOffset.getOffsetZ().getMax());
                    }
                    break;
                case 41:
                    minZ = newOffset.getOffsetZ().getMin();
                    if(!newOffset.getOffsetZ().setMin(minZ + 1)){
                        newOffset.getOffsetZ().setMin(newOffset.getOffsetZ().getMax());
                    }
                    break;
                case 42:
                    minZ = newOffset.getOffsetZ().getMin();
                    if(!newOffset.getOffsetZ().setMin(minZ + 10)){
                        newOffset.getOffsetZ().setMin(newOffset.getOffsetZ().getMax());
                    }
                    break;
                case 43:
                    minZ = newOffset.getOffsetZ().getMin();
                    if(!newOffset.getOffsetZ().setMin(minZ + 100)){
                        newOffset.getOffsetZ().setMin(newOffset.getOffsetZ().getMax());
                    }
                    break;
//</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Max Z">
                case 46:
                    int maxZ = newOffset.getOffsetZ().getMax();
                    if(!newOffset.getOffsetZ().setMax(maxZ - 100)){
                        newOffset.getOffsetZ().setMax(newOffset.getOffsetZ().getMin());
                    }
                    break;
                case 47:
                    maxZ = newOffset.getOffsetZ().getMax();
                    if(!newOffset.getOffsetZ().setMax(maxZ - 10)){
                        newOffset.getOffsetZ().setMax(newOffset.getOffsetZ().getMin());
                    }
                    break;
                case 48:
                    maxZ = newOffset.getOffsetZ().getMax();
                    if(!newOffset.getOffsetZ().setMax(maxZ - 1)){
                        newOffset.getOffsetZ().setMax(newOffset.getOffsetZ().getMin());
                    }
                    break;
                case 49:
                    if(!newOffset.getOffsetZ().setMax(0)){
                        newOffset.getOffsetZ().setMax(newOffset.getOffsetZ().getMin());
                    }
                    break;
                case 50:
                    maxZ = newOffset.getOffsetZ().getMax();
                    if(!newOffset.getOffsetZ().setMax(maxZ + 1)){
                        newOffset.getOffsetZ().setMax(newOffset.getOffsetZ().getMin());
                    }
                    break;
                case 51:
                    maxZ = newOffset.getOffsetZ().getMax();
                    if(!newOffset.getOffsetZ().setMax(maxZ + 10)){
                        newOffset.getOffsetZ().setMax(newOffset.getOffsetZ().getMin());
                    }
                    break;
                case 52:
                    maxZ = newOffset.getOffsetZ().getMax();
                    if(!newOffset.getOffsetZ().setMax(maxZ + 100)){
                        newOffset.getOffsetZ().setMax(newOffset.getOffsetZ().getMin());
                    }
                    break;
//</editor-fold>
            }
            if(e.getSlot() != 18 && e.getSlot() != 26){
                openOffsetMenu(p);
            }
        }
//</editor-fold>
    };
    
    @FunctionalInterface
    public static interface PlayerFunction{
        public void execute(Player player);
    }
}
