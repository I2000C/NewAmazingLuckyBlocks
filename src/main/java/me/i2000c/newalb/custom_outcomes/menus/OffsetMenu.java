package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import me.i2000c.newalb.utils2.Offset;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OffsetMenu extends Editor<Offset>{
    public OffsetMenu(){
        InventoryListener.registerInventory(CustomInventoryType.OFFSET_MENU, OFFSET_MENU_FUNCTION);
    }
    
    @Override
    public void newItem(Player player){
        item = new Offset();
        openOffsetMenu(player);
    }
    
    @Override
    public void editItem(Player player){
        openOffsetMenu(player);
    }
        
    private void openOffsetMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.OFFSET_MENU, 54, "&dOffset menu");
        
        for(int i=3;i<=48;i+=9){
            menu.setItem(i, GUIItem.getPlusLessItem(-1));
        }        
        for(int i=2;i<=47;i+=9){
            menu.setItem(i, GUIItem.getPlusLessItem(-10));
        }        
        for(int i=1;i<=46;i+=9){
            menu.setItem(i, GUIItem.getPlusLessItem(-100));
        }
        
        for(int i=5;i<=50;i+=9){
            menu.setItem(i, GUIItem.getPlusLessItem(+1));
        }
        for(int i=6;i<=51;i+=9){
            menu.setItem(i, GUIItem.getPlusLessItem(+10));
        }
        for(int i=7;i<=52;i+=9){
            menu.setItem(i, GUIItem.getPlusLessItem(+100));
        }
        
        ItemStack minX = ItemStackWrapper.newItem(XMaterial.GRASS_BLOCK)
                                         .setDisplayName("&cMin X: &6" + item.getOffsetX().getMin())
                                         .addLoreLine("&3Click to reset")
                                         .toItemStack();
        
        ItemStack maxX = ItemStackWrapper.newItem(XMaterial.GRASS_BLOCK)
                                         .setDisplayName("&cMax X: &6" + item.getOffsetX().getMax())
                                         .addLoreLine("&3Click to reset")
                                         .toItemStack();
        
        ItemStack minY = ItemStackWrapper.newItem(XMaterial.DIRT)
                                         .setDisplayName("&aMin Y: &6" + item.getOffsetY().getMin())
                                         .addLoreLine("&3Click to reset")
                                         .toItemStack();
        
        ItemStack maxY = ItemStackWrapper.newItem(XMaterial.DIRT)
                                         .setDisplayName("&aMax Y: &6" + item.getOffsetY().getMax())
                                         .addLoreLine("&3Click to reset")
                                         .toItemStack();        
        
        ItemStack minZ = ItemStackWrapper.newItem(XMaterial.STONE)
                                         .setDisplayName("&bMin Z: &6" + item.getOffsetZ().getMin())
                                         .addLoreLine("&3Click to reset")
                                         .toItemStack();
        
        ItemStack maxZ = ItemStackWrapper.newItem(XMaterial.STONE)
                                         .setDisplayName("&bMax Z: &6" + item.getOffsetZ().getMax())
                                         .addLoreLine("&3Click to reset")
                                         .toItemStack();
        
        menu.setItem(4, minX);
        menu.setItem(13, maxX);
        menu.setItem(22, minY);
        menu.setItem(31, maxY);
        menu.setItem(40, minZ);
        menu.setItem(49, maxZ);
        
        menu.setItem(18, GUIItem.getBackItem());
        menu.setItem(26, GUIItem.getNextItem());
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction OFFSET_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 18:
                    // Go to the previous menu
                    onBack.accept(player);
                    break;
                case 26:
                    // Go to the next menu
                    onNext.accept(player, item);
                    break;
                    //<editor-fold defaultstate="collapsed" desc="Min X">
                case 1:
                    int minX = item.getOffsetX().getMin();
                    if(!item.getOffsetX().setMin(minX - 100)){
                        item.getOffsetX().setMin(item.getOffsetX().getMax());
                    }
                    break;
                case 2:
                    minX = item.getOffsetX().getMin();
                    if(!item.getOffsetX().setMin(minX - 10)){
                        item.getOffsetX().setMin(item.getOffsetX().getMax());
                    }
                    break;
                case 3:
                    minX = item.getOffsetX().getMin();
                    if(!item.getOffsetX().setMin(minX - 1)){
                        item.getOffsetX().setMin(item.getOffsetX().getMax());
                    }
                    break;
                case 4:
                    if(!item.getOffsetX().setMin(0)){
                        item.getOffsetX().setMin(item.getOffsetX().getMax());
                    }
                    break;
                case 5:
                    minX = item.getOffsetX().getMin();
                    if(!item.getOffsetX().setMin(minX + 1)){
                        item.getOffsetX().setMin(item.getOffsetX().getMax());
                    }
                    break;
                case 6:
                    minX = item.getOffsetX().getMin();
                    if(!item.getOffsetX().setMin(minX + 10)){
                        item.getOffsetX().setMin(item.getOffsetX().getMax());
                    }
                    break;
                case 7:
                    minX = item.getOffsetX().getMin();
                    if(!item.getOffsetX().setMin(minX + 100)){
                        item.getOffsetX().setMin(item.getOffsetX().getMax());
                    }
                    break;
//</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Max X">
                case 10:
                    int maxX = item.getOffsetX().getMax();
                    if(!item.getOffsetX().setMax(maxX - 100)){
                        item.getOffsetX().setMax(item.getOffsetX().getMin());
                    }
                    break;
                case 11:
                    maxX = item.getOffsetX().getMax();
                    if(!item.getOffsetX().setMax(maxX - 10)){
                        item.getOffsetX().setMax(item.getOffsetX().getMin());
                    }
                    break;
                case 12:
                    maxX = item.getOffsetX().getMax();
                    if(!item.getOffsetX().setMax(maxX - 1)){
                        item.getOffsetX().setMax(item.getOffsetX().getMin());
                    }
                    break;
                case 13:
                    if(!item.getOffsetX().setMax(0)){
                        item.getOffsetX().setMax(item.getOffsetX().getMin());
                    }
                    break;
                case 14:
                    maxX = item.getOffsetX().getMax();
                    if(!item.getOffsetX().setMax(maxX + 1)){
                        item.getOffsetX().setMax(item.getOffsetX().getMin());
                    }
                    break;
                case 15:
                    maxX = item.getOffsetX().getMax();
                    if(!item.getOffsetX().setMax(maxX + 10)){
                        item.getOffsetX().setMax(item.getOffsetX().getMin());
                    }
                    break;
                case 16:
                    maxX = item.getOffsetX().getMax();
                    if(!item.getOffsetX().setMax(maxX + 100)){
                        item.getOffsetX().setMax(item.getOffsetX().getMin());
                    }
                    break;
//</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Min Y">
                case 19:
                    int minY = item.getOffsetY().getMin();
                    if(!item.getOffsetY().setMin(minY - 100)){
                        item.getOffsetY().setMin(item.getOffsetY().getMax());
                    }
                    break;
                case 20:
                    minY = item.getOffsetY().getMin();
                    if(!item.getOffsetY().setMin(minY - 10)){
                        item.getOffsetY().setMin(item.getOffsetY().getMax());
                    }
                    break;
                case 21:
                    minY = item.getOffsetY().getMin();
                    if(!item.getOffsetY().setMin(minY - 1)){
                        item.getOffsetY().setMin(item.getOffsetY().getMax());
                    }
                    break;
                case 22:
                    if(!item.getOffsetY().setMin(0)){
                        item.getOffsetY().setMin(item.getOffsetY().getMax());
                    }
                    break;
                case 23:
                    minY = item.getOffsetY().getMin();
                    if(!item.getOffsetY().setMin(minY + 1)){
                        item.getOffsetY().setMin(item.getOffsetY().getMax());
                    }
                    break;
                case 24:
                    minY = item.getOffsetY().getMin();
                    if(!item.getOffsetY().setMin(minY + 10)){
                        item.getOffsetY().setMin(item.getOffsetY().getMax());
                    }
                    break;
                case 25:
                    minY = item.getOffsetY().getMin();
                    if(!item.getOffsetY().setMin(minY + 100)){
                        item.getOffsetY().setMin(item.getOffsetY().getMax());
                    }
                    break;
//</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Max Y">
                case 28:
                    int maxY = item.getOffsetY().getMax();
                    if(!item.getOffsetY().setMax(maxY - 100)){
                        item.getOffsetY().setMax(item.getOffsetY().getMin());
                    }
                    break;
                case 29:
                    maxY = item.getOffsetY().getMax();
                    if(!item.getOffsetY().setMax(maxY - 10)){
                        item.getOffsetY().setMax(item.getOffsetY().getMin());
                    }
                    break;
                case 30:
                    maxY = item.getOffsetY().getMax();
                    if(!item.getOffsetY().setMax(maxY - 1)){
                        item.getOffsetY().setMax(item.getOffsetY().getMin());
                    }
                    break;
                case 31:
                    if(!item.getOffsetY().setMax(0)){
                        item.getOffsetY().setMax(item.getOffsetY().getMin());
                    }
                    break;
                case 32:
                    maxY = item.getOffsetY().getMax();
                    if(!item.getOffsetY().setMax(maxY + 1)){
                        item.getOffsetY().setMax(item.getOffsetY().getMin());
                    }
                    break;
                case 33:
                    maxY = item.getOffsetY().getMax();
                    if(!item.getOffsetY().setMax(maxY + 10)){
                        item.getOffsetY().setMax(item.getOffsetY().getMin());
                    }
                    break;
                case 34:
                    maxY = item.getOffsetY().getMax();
                    if(!item.getOffsetY().setMax(maxY + 100)){
                        item.getOffsetY().setMax(item.getOffsetY().getMin());
                    }
                    break;
//</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Min Z">
                case 37:
                    int minZ = item.getOffsetZ().getMin();
                    if(!item.getOffsetZ().setMin(minZ - 100)){
                        item.getOffsetZ().setMin(item.getOffsetZ().getMax());
                    }
                    break;
                case 38:
                    minZ = item.getOffsetZ().getMin();
                    if(!item.getOffsetZ().setMin(minZ - 10)){
                        item.getOffsetZ().setMin(item.getOffsetZ().getMax());
                    }
                    break;
                case 39:
                    minZ = item.getOffsetZ().getMin();
                    if(!item.getOffsetZ().setMin(minZ - 1)){
                        item.getOffsetZ().setMin(item.getOffsetZ().getMax());
                    }
                    break;
                case 40:
                    if(!item.getOffsetZ().setMin(0)){
                        item.getOffsetZ().setMin(item.getOffsetZ().getMax());
                    }
                    break;
                case 41:
                    minZ = item.getOffsetZ().getMin();
                    if(!item.getOffsetZ().setMin(minZ + 1)){
                        item.getOffsetZ().setMin(item.getOffsetZ().getMax());
                    }
                    break;
                case 42:
                    minZ = item.getOffsetZ().getMin();
                    if(!item.getOffsetZ().setMin(minZ + 10)){
                        item.getOffsetZ().setMin(item.getOffsetZ().getMax());
                    }
                    break;
                case 43:
                    minZ = item.getOffsetZ().getMin();
                    if(!item.getOffsetZ().setMin(minZ + 100)){
                        item.getOffsetZ().setMin(item.getOffsetZ().getMax());
                    }
                    break;
//</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Max Z">
                case 46:
                    int maxZ = item.getOffsetZ().getMax();
                    if(!item.getOffsetZ().setMax(maxZ - 100)){
                        item.getOffsetZ().setMax(item.getOffsetZ().getMin());
                    }
                    break;
                case 47:
                    maxZ = item.getOffsetZ().getMax();
                    if(!item.getOffsetZ().setMax(maxZ - 10)){
                        item.getOffsetZ().setMax(item.getOffsetZ().getMin());
                    }
                    break;
                case 48:
                    maxZ = item.getOffsetZ().getMax();
                    if(!item.getOffsetZ().setMax(maxZ - 1)){
                        item.getOffsetZ().setMax(item.getOffsetZ().getMin());
                    }
                    break;
                case 49:
                    if(!item.getOffsetZ().setMax(0)){
                        item.getOffsetZ().setMax(item.getOffsetZ().getMin());
                    }
                    break;
                case 50:
                    maxZ = item.getOffsetZ().getMax();
                    if(!item.getOffsetZ().setMax(maxZ + 1)){
                        item.getOffsetZ().setMax(item.getOffsetZ().getMin());
                    }
                    break;
                case 51:
                    maxZ = item.getOffsetZ().getMax();
                    if(!item.getOffsetZ().setMax(maxZ + 10)){
                        item.getOffsetZ().setMax(item.getOffsetZ().getMin());
                    }
                    break;
                case 52:
                    maxZ = item.getOffsetZ().getMax();
                    if(!item.getOffsetZ().setMax(maxZ + 100)){
                        item.getOffsetZ().setMax(item.getOffsetZ().getMin());
                    }
                    break;
//</editor-fold>
            }
            
            if(e.getSlot() != 18 && e.getSlot() != 26){
                openOffsetMenu(player);
            }
        }
//</editor-fold>
    };
}
