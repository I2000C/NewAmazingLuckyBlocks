package me.i2000c.newalb.custom_outcomes.menus.nbt;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import me.i2000c.newalb.utils2.OtherUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class ItemNbtTypeMenu extends Editor<NBTType> {
    
    public ItemNbtTypeMenu() {
        InventoryListener.registerInventory(CustomInventoryType.ITEM_NBT_TYPE_MENU, ITEM_NBT_TYPE_MENU_FUNCTION);
    }
    
    private static final List<NBTType> NBT_TYPES = Arrays.stream(NBTType.values())
                                                         .filter(OtherUtils.not(NBTType.NBTTagEnd::equals))
                                                         .filter(OtherUtils.not(NBTType.NBTTagList::equals))
                                                         .collect(Collectors.toList());
    
    private static final int BACK_SLOT = 27;
    
    @Override
    public void newItem(Player player) {
        this.item = NBTType.NBTTagEnd;
        openNbtTypeMenu(player);
    }
    
    @Override
    public void editItem(Player player) {
        openNbtTypeMenu(player);
    }
    
    private void openNbtTypeMenu(Player player) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.ITEM_NBT_TYPE_MENU, 36, "&d&lSelect NBT type");
        
        for(int i=0; i<BACK_SLOT && i<NBT_TYPES.size(); i++) {
            NBTType type = NBT_TYPES.get(i);
            ItemStackWrapper wrapper = ItemStackWrapper.newItem(XMaterial.NAME_TAG);
            wrapper.setDisplayName("&3" + type.name());
            NBT.modify(wrapper.toItemStack(), nbt -> {nbt.setEnum(ItemNbtMenu.NBT_TYPE_TAG, type);});
            if(type == item) {
                wrapper.addEnchantment(XEnchantment.POWER, 1);
                wrapper.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            
            menu.setItem(i, wrapper.toItemStack());
        }
        
        menu.setItem(BACK_SLOT, GUIItem.getBackItem());
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction ITEM_NBT_TYPE_MENU_FUNCTION = e -> {
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        switch(e.getSlot()) {
            case BACK_SLOT:
                // Go to previous menu
                onBack.accept(player);
                break;
            default:
                ItemStack stack = e.getCurrentItem();
                if(stack == null || stack.getType() == Material.AIR) {
                    break;
                }
                
                NBT.get(stack, nbt -> {
                    if(!nbt.hasTag(ItemNbtMenu.NBT_TYPE_TAG)) {
                        return;
                    }
                    
                    NBTType type = nbt.getEnum(ItemNbtMenu.NBT_TYPE_TAG, NBTType.class);
                    onNext.accept(player, type);
                });
        }
    };
}
