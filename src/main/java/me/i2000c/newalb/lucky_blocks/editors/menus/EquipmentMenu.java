package me.i2000c.newalb.lucky_blocks.editors.menus;

import com.cryptomorin.xseries.XMaterial;

import java.util.HashMap;
import java.util.Map;

import me.i2000c.newalb.api.functions.InventoryFunction;
import me.i2000c.newalb.api.gui.CustomInventoryType;
import me.i2000c.newalb.api.gui.GUIFactory;
import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.InventoryLocation;
import me.i2000c.newalb.api.gui.Menu;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.lucky_blocks.editors.Editor;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.Equipment;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EquipmentMenu extends Editor<Equipment>{
    public EquipmentMenu(){
        InventoryListener.registerInventory(CustomInventoryType.EQUIPMENT_MENU, EQUIPMENT_MENU_FUNCTION);
    }
    
    private static final int HELMET_SLOT = 13;
    private static final int CHESTPLATE_SLOT = 22;
    private static final int LEGGINGS_SLOT = 31;
    private static final int BOOTS_SLOT = 40;
    private static final int ITEM_IN_HAND_SLOT = 49;
    
    private static final Map<Integer, Integer> SLOT_MAP;
    private static final int[] EQUIPMENT_SLOTS = {
                                    HELMET_SLOT, 
                                    CHESTPLATE_SLOT, 
                                    LEGGINGS_SLOT, 
                                    BOOTS_SLOT, 
                                    ITEM_IN_HAND_SLOT};
    
    static{
        //<editor-fold defaultstate="collapsed" desc="Code">
        SLOT_MAP = new HashMap<>();
        SLOT_MAP.put(HELMET_SLOT, Equipment.HELMET_ID);
        SLOT_MAP.put(CHESTPLATE_SLOT, Equipment.CHESTPLATE_ID);
        SLOT_MAP.put(LEGGINGS_SLOT, Equipment.LEGGINGS_ID);
        SLOT_MAP.put(BOOTS_SLOT, Equipment.BOOTS_ID);
        SLOT_MAP.put(ITEM_IN_HAND_SLOT, Equipment.ITEM_IN_HAND_ID);
//</editor-fold>
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
        
        for(int i=0;i<54;i++){
            menu.setItem(i, glass);
        }
        
        ItemStack creative = ItemStackWrapper.newItem(XMaterial.CRAFTING_TABLE)
                                             .setDisplayName("&aHow to edit equipment")
                                             .addLoreLine("&bYou can drag and drop items")
                                             .addLoreLine("&b  from your inventory into")
                                             .addLoreLine("&b  the equipment slots and")
                                             .addLoreLine("&b  vice versa.")
                                             .addLoreLine("")
                                             .addLoreLine("&3Click here to close this menu")
                                             .addLoreLine("&3  if you want to pick items")
                                             .addLoreLine("&3  from creative mode")
                                             .toItemStack();
        
        ItemStackWrapper builder = ItemStackWrapper.newItem(XMaterial.IRON_BOOTS);
        builder.setDisplayName("&6Current drop chances");
        for(int i=0; i<Equipment.EQUIPMENT_KEYS.length; i++){
            String spaces = " ";
            switch(i){
                case 0:
                case 3: spaces = "   "; break;
                case 1:
                case 2: spaces = "  "; break;
            }
            
            builder.addLoreLine(String.format("  &b %-12s" + spaces + "&5%4d%%", 
                    Equipment.EQUIPMENT_KEYS[i] + ":", 
                    item.getEquipmentDropChance(i)));
        }
        builder.addLoreLine("");
        builder.addLoreLine("&3Click to reset all drop chances");
        ItemStack dropChances = builder.toItemStack();
        
        for(int slot : EQUIPMENT_SLOTS){
            ItemStack equipmentItem = item.getEquipmentItem(SLOT_MAP.get(slot));
            menu.setItem(slot, equipmentItem);
                for(int i=1, multiplier=1; i<=2; i++, multiplier*=10){
                menu.setItem(slot - i, GUIItem.getPlusLessItem(-multiplier));
                menu.setItem(slot + i, GUIItem.getPlusLessItem(+multiplier));
            }
        }
        
        menu.setItem(27, GUIItem.getBackItem());
        menu.setItem(35, GUIItem.getNextItem());
        
        menu.setItem(45, creative);
        menu.setItem(53, dropChances);
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction EQUIPMENT_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        ItemStack cursor = e.getCursor();
        
        if(e.getLocation() == InventoryLocation.TOP){
            //Change the 'equipment_slot' depending on what slot has been clicked from {13, 22, 31, 40, 49}
            switch(e.getSlot()){
                case 45:
                    //Close menu
                    if(cursor != null && cursor.getType() != Material.AIR){
                        break;
                    }
                    
                    player.closeInventory();
                    Logger.sendMessage("&6Use &b/alb return &6to return to the menu", player);
                    break;
                case HELMET_SLOT:
                case CHESTPLATE_SLOT:
                case LEGGINGS_SLOT:
                case BOOTS_SLOT:
                case ITEM_IN_HAND_SLOT:
                    e.setCancelled(false);
                    break;
                case 53:
                    if(cursor != null && cursor.getType() != Material.AIR){
                        break;
                    }
                    
                    saveEquipment(e.getInventory());
                    item.resetEquipmentDropChances();
                    openEquipmentInventory(player);
                    break;
                case 27:
                    // Go to previous menu
                    if(cursor != null && cursor.getType() != Material.AIR){
                        break;
                    }
                    
                    onBack.accept(player);
                    break;
                case 35:
                    // Go to next menu
                    if(cursor != null && cursor.getType() != Material.AIR){
                        break;
                    }
                    
                    saveEquipment(e.getInventory());
                    onNext.accept(player, item);
                    break;
                default:
                    if(cursor != null && cursor.getType() != Material.AIR){
                        break;
                    }
                    
                    boolean found = false;
                    int equipmentSlot = -1;
                    int dropChanceOffset = -1;
                    for(int slot : EQUIPMENT_SLOTS){
                        for(int i=1, multiplier=1; i<=2; i++, multiplier *=10){
                            if(e.getSlot() == slot - i){
                                equipmentSlot = SLOT_MAP.get(slot);
                                dropChanceOffset = -multiplier;
                                found = true;
                            }else if(e.getSlot() == slot + i){
                                equipmentSlot = SLOT_MAP.get(slot);
                                dropChanceOffset = +multiplier;
                                found = true;
                            }
                        }
                        
                        if(found){
                            break;
                        }
                    }
                    
                    if(found){
                        int dropChance = item.getEquipmentDropChance(equipmentSlot) + dropChanceOffset;
                        if(dropChance < 0){
                            dropChance = 100;
                        }else if(dropChance > 100){
                            dropChance = 0;
                        }
                        
                        item.setEquipmentDropChance(equipmentSlot, dropChance);
                        saveEquipment(e.getInventory());
                        openEquipmentInventory(player);
                    }
            }
        }else if(e.getLocation() == InventoryLocation.BOTTOM){
            e.setCancelled(false);
        }
//</editor-fold>
    };
    
    private void saveEquipment(Inventory inv){
        //<editor-fold defaultstate="collapsed" desc="Code">
        item.setEquipmentItem(Equipment.HELMET_ID, inv.getItem(HELMET_SLOT));
        item.setEquipmentItem(Equipment.CHESTPLATE_ID, inv.getItem(CHESTPLATE_SLOT));
        item.setEquipmentItem(Equipment.LEGGINGS_ID, inv.getItem(LEGGINGS_SLOT));
        item.setEquipmentItem(Equipment.BOOTS_ID, inv.getItem(BOOTS_SLOT));
        item.setEquipmentItem(Equipment.ITEM_IN_HAND_ID, inv.getItem(ITEM_IN_HAND_SLOT));
//</editor-fold>
    }
}
