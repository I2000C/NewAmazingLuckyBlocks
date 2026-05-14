package me.i2000c.newalb.lucky_blocks.editors.menus;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.Equipment;
import me.i2000c.newalb.utils.misc.EquipmentSlot;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.misc.OtherUtils;

public class EquipmentMenu extends EditorMenu<Equipment> {
    
    private static final int[] VALUES = {-10, -1, 0, +1, +10};
    
    private final List<Consumer<Integer>> equipmentPropertyList = new ArrayList<>();
    
    public EquipmentMenu() {
        super("&e&lEquipment Config", MenuSize.SIZE_6_ROWS, true);
        
        SLOT_MAP.keySet().forEach(equipmentSlot -> {
            equipmentPropertyList.add(value -> {
                int chance = item.getDropChance(equipmentSlot);
                chance = OtherUtils.clamp(chance + value, 0, 100);
                item.setDropChance(equipmentSlot, chance);
            });
        });
    }
    
    private static final int HELMET_SLOT = 13;
    private static final int CHESTPLATE_SLOT = 22;
    private static final int LEGGINGS_SLOT = 31;
    private static final int BOOTS_SLOT = 40;
    private static final int ITEM_IN_HAND_SLOT = 49;
    
    private static final Map<EquipmentSlot, Integer> SLOT_MAP;
    
    static{
        SLOT_MAP = new EnumMap<>(EquipmentSlot.class);
        SLOT_MAP.put(EquipmentSlot.HELMET, HELMET_SLOT);
        SLOT_MAP.put(EquipmentSlot.CHESTPLATE, CHESTPLATE_SLOT);
        SLOT_MAP.put(EquipmentSlot.LEGGINGS, LEGGINGS_SLOT);
        SLOT_MAP.put(EquipmentSlot.BOOTS, BOOTS_SLOT);
        SLOT_MAP.put(EquipmentSlot.ITEM_IN_HAND, ITEM_IN_HAND_SLOT);
    }
    
    @Override
    protected Equipment createNewItem() {
        return new Equipment();
    }
    
    @SuppressWarnings("deprecation")
    @Override
    protected void buildMenu(Player player) {
        addGlassFill(GlassColor.BLACK);
        
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
        
        ItemStackWrapper wrapper = ItemStackWrapper.newItem(XMaterial.IRON_BOOTS);
        wrapper.setDisplayName("&6Current drop chances");
        String spaces;
        for(EquipmentSlot equipmentSlot : EquipmentSlot.VALUES) {
            switch(equipmentSlot) {
                case HELMET:
                case BOOTS:
                    spaces = "   "; break;
                case CHESTPLATE:
                case LEGGINGS:
                    spaces = "  "; break;
                default:
                    spaces = " "; break;
            }
            
            wrapper.addLoreLine(String.format("  &b %-12s" + spaces + "&5%4d%%", 
                    equipmentSlot.getConfigKey() + ":", 
                    item.getDropChance(equipmentSlot)));
        }
        wrapper.addLoreLine("");
        wrapper.addLoreLine("&3Click to reset all drop chances");
        ItemStack dropChances = wrapper.toItemStack();
        
        Iterator<Map.Entry<EquipmentSlot, Integer>> iterator = SLOT_MAP.entrySet().iterator();
        for(int row=0; row<equipmentPropertyList.size() && iterator.hasNext(); row++) {
            Map.Entry<EquipmentSlot, Integer> entry = iterator.next();
            EquipmentSlot equipmentSlot = entry.getKey();
            int equipmentItemSlot = entry.getValue();
            ItemStack equipmentItem = item.getItem(equipmentSlot);
            int slot = equipmentItemSlot - 2;
            for(int value : VALUES) {
                if(value == 0) {
                    setItem(slot++, equipmentItem, e -> {
                        ItemStack cursor = e.getCursor();
                        ItemStack currentItem = e.getCurrentItem();
                        item.setItem(equipmentSlot, cursor);
                        e.setCursor(currentItem);
                        e.getInventory().setItem(e.getSlot(), cursor);
                    });
                } else {
                    ItemStack stack = GUIItem.getPlusLessItem(value);
                    wrapper = ItemStackWrapper.fromItem(stack, false);
                    if(value > 0) {
                        wrapper.addLoreLine("&aIncrease &l" + equipmentSlot.getConfigKey() + " &r&adrop chance");
                    } else {
                        wrapper.addLoreLine("&cDecrease &l" + equipmentSlot.getConfigKey() + " &r&cdrop chance");
                    }
                    Consumer<Integer> propertyModifier = equipmentPropertyList.get(row);
                    setItem(slot++, stack, e -> {
                        propertyModifier.accept(value);
                        openToPlayer(e.getPlayer());
                    });
                }
            }
        }
        
        setBackItem(27);
        setNextItem(35);
        
        setItem(45, creative, e -> {
            if(e.isEmptyCursor()) {
                player.closeInventory();
                Logger.sendMessage("&6Use &b/alb return &6to return to the menu", player);
            }
        });
        
        setItem(53, dropChances, e -> {
            if(e.isEmptyCursor()) {
                item.resetDropChances();
                openToPlayer(player);
            }
        });
    }
    
    @SuppressWarnings("deprecation")
    @Override
    protected void onClickDefault(MenuClickEvent event) {
        if(event.isBottomInventory()) {
            event.setCursor(event.getCurrentItem());
        } else {
            event.setCursor(null);
        }
    }
}
