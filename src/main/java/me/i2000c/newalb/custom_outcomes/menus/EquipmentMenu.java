package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.rewards.Equipment;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EquipmentMenu extends Editor<Equipment>{
    public EquipmentMenu(){
        InventoryListener.registerInventory(CustomInventoryType.EQUIPMENT_MENU, EQUIPMENT_MENU_FUNCTION);
    }
    
    private int selectedEquipmentSlot;
    
    @Override
    protected void reset(){
        this.selectedEquipmentSlot = 0;
    }
    
    @Override
    protected void newItem(Player player){
        item = new Equipment();
        openEquipmentInventory(player);
    }
    
    @Override
    protected void editItem(Player player){
        openEquipmentInventory(player);
    }
    
    private void openEquipmentInventory(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.EQUIPMENT_MENU, 54, "&e&lEquipment Config");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.BLACK);
        
        ItemStack creative = ItemBuilder.newItem(XMaterial.CRAFTING_TABLE)
                .withDisplayName("&3Close menu to pick items from creative mode")
                .build();
        
        ItemStack helmet = ItemBuilder.newItem(XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE)
                .withDisplayName("&bSelect helmet")
                .build();
        
        ItemStack chestplate = ItemBuilder.newItem(XMaterial.LIME_STAINED_GLASS_PANE)
                .withDisplayName("&aSelect chestplate")
                .build();
        
        ItemStack leggings = ItemBuilder.newItem(XMaterial.YELLOW_STAINED_GLASS_PANE)
                .withDisplayName("&eSelect leggings")
                .build();
        
        ItemStack boots = ItemBuilder.newItem(XMaterial.ORANGE_STAINED_GLASS_PANE)
                .withDisplayName("&6Select boots")
                .build();
        
        ItemStack itemInHand = ItemBuilder.newItem(XMaterial.RED_STAINED_GLASS_PANE)
                .withDisplayName("&cSelect item in hand")
                .build();
        
        for(int i=0;i<54;i++){
            menu.setItem(i, glass);
        }
        
        if(item.helmet == null){
            menu.setItem(13, helmet);
        }else{
            menu.setItem(13, item.helmet);
        }
        if(item.chestplate == null){
            menu.setItem(22, chestplate);
        }else{
            menu.setItem(22, item.chestplate);
        }
        if(item.leggings == null){
            menu.setItem(31, leggings);
        }else{
            menu.setItem(31, item.leggings);
        }
        if(item.boots == null){
            menu.setItem(40, boots);
        }else{
            menu.setItem(40, item.boots);
        }
        if(item.itemInHand == null){
            menu.setItem(49, itemInHand);
        }else{
            menu.setItem(49, item.itemInHand);
        }
        
        menu.setItem(27, GUIItem.getBackItem());
        menu.setItem(35, GUIItem.getNextItem());
        
        menu.setItem(45, creative);
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction EQUIPMENT_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            //Change the 'equipment_slot' depending on what slot has been clicked from {13, 22, 31, 40, 49}
            switch(e.getSlot()){
                case 45:
                    //Close menu
                    player.closeInventory();
                    Logger.sendMessage("&6Use &b/alb return &6to return to the menu", player);
                    break;
                case 13:
                    selectedEquipmentSlot = 13;
                    break;
                case 22:
                    selectedEquipmentSlot = 22;
                    break;
                case 31:
                    selectedEquipmentSlot = 31;
                    break;
                case 40:
                    selectedEquipmentSlot = 40;
                    break;
                case 49:
                    selectedEquipmentSlot = 49;
                    break;
                case 27:
                    // Go to previous menu
                    onBack.accept(player);
                    break;
                case 35:
                    // Go to next menu
                    onNext.accept(player, item);
                    break;
            }
        }else if(e.getLocation() == InventoryLocation.BOTTOM){
            if(selectedEquipmentSlot != 0){
                switch(selectedEquipmentSlot){
                    case 13:
                        if(e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)){
                            item.helmet = null;
                        }else{
                            item.helmet = e.getCurrentItem().clone();
                        }
                        break;
                    case 22:
                        if(e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)){
                            item.chestplate = null;
                        }else{
                            item.chestplate = e.getCurrentItem().clone();
                        }
                        break;
                    case 31:
                        if(e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)){
                            item.leggings = null;
                        }else{
                            item.leggings = e.getCurrentItem().clone();
                        }
                        break;
                    case 40:
                        if(e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)){
                            item.boots = null;
                        }else{
                            item.boots = e.getCurrentItem().clone();
                        }
                        break;
                    case 49:
                        if(e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)){
                            item.itemInHand = null;
                        }else{
                            item.itemInHand = e.getCurrentItem().clone();
                        }
                        break;
                }

                //Clone the selected item to the item in the 'currentEquipmentSlot' slot
                openEquipmentInventory(player);
            }
        }
//</editor-fold>
    };
}
