package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils2.Offset;
import me.i2000c.newalb.utils.Logger;
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
        newOffset = oldOffset.cloneOffset();
        playerFunction = function;
    }
    
    public static void openOffsetMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.OFFSET_MENU, 54, "&dOffset menu");
        
        ItemMeta meta;
        
        ItemStack minus1 = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
        meta = minus1.getItemMeta();
        meta.setDisplayName("&c&l-1");
        minus1.setItemMeta(meta);
        for(int i=3;i<=48;i+=9){
            inv.setItem(i, minus1);
        }
        
        ItemStack minus10 = minus1.clone();
        meta = minus10.getItemMeta();
        meta.setDisplayName("&c&l-10");
        minus10.setItemMeta(meta);
        for(int i=2;i<=47;i+=9){
            inv.setItem(i, minus10);
        }
        
        ItemStack minus100 = minus1.clone();
        meta = minus100.getItemMeta();
        meta.setDisplayName("&c&l-100");
        minus100.setItemMeta(meta);
        for(int i=1;i<=46;i+=9){
            inv.setItem(i, minus100);
        }
        
        ItemStack plus1 = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
        meta = minus1.getItemMeta();
        meta.setDisplayName("&a&l+1");
        plus1.setItemMeta(meta);
        for(int i=5;i<=50;i+=9){
            inv.setItem(i, plus1);
        }
        
        ItemStack plus10 = plus1.clone();
        meta = plus10.getItemMeta();
        meta.setDisplayName("&a&l+10");
        plus10.setItemMeta(meta);
        for(int i=6;i<=51;i+=9){
            inv.setItem(i, plus10);
        }
        
        ItemStack plus100 = plus1.clone();
        meta = plus100.getItemMeta();
        meta.setDisplayName("&a&l+100");
        plus100.setItemMeta(meta);
        for(int i=7;i<=52;i+=9){
            inv.setItem(i, plus100);
        }
        
        ItemStack minX = XMaterial.GRASS_BLOCK.parseItem();
        meta = minX.getItemMeta();
        meta.setDisplayName("&cMin X: &6" + newOffset.getOffsetX().getMin());
        meta.setLore(Arrays.asList("&3Click to reset"));
        minX.setItemMeta(meta);
        
        ItemStack maxX = minX.clone();
        meta = maxX.getItemMeta();
        meta.setDisplayName("&cMax X: &6" + newOffset.getOffsetX().getMax());
        maxX.setItemMeta(meta);
        
        ItemStack minY = new ItemStack(Material.DIRT);
        meta = minY.getItemMeta();
        meta.setDisplayName("&aMin Y: &6" + newOffset.getOffsetY().getMin());
        meta.setLore(Arrays.asList("&3Click to reset"));
        minY.setItemMeta(meta);
        
        ItemStack maxY = minY.clone();
        meta = maxY.getItemMeta();
        meta.setDisplayName("&aMax Y: &6" + newOffset.getOffsetY().getMax());
        maxY.setItemMeta(meta);
        
        ItemStack minZ = new ItemStack(Material.STONE);
        meta = minZ.getItemMeta();
        meta.setDisplayName("&bMin Z: &6" + newOffset.getOffsetZ().getMin());
        meta.setLore(Arrays.asList("&3Click to reset"));
        minZ.setItemMeta(meta);
        
        ItemStack maxZ = minZ.clone();
        meta = maxZ.getItemMeta();
        meta.setDisplayName("&bMax Z: &6" + newOffset.getOffsetZ().getMax());
        maxZ.setItemMeta(meta);
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName("&2Back");
        back.setItemMeta(meta);
        
        ItemStack next = new ItemStack(Material.ANVIL);
        meta = next.getItemMeta();
        meta.setDisplayName("&bNext");
        next.setItemMeta(meta);
        
        inv.setItem(4, minX);
        inv.setItem(13, maxX);
        inv.setItem(22, minY);
        inv.setItem(31, maxY);
        inv.setItem(40, minZ);
        inv.setItem(49, maxZ);
        
        inv.setItem(18, back);
        inv.setItem(26, next);
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction OFFSET_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){

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
