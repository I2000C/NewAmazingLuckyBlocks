package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemFlagMenu extends Editor<ItemStack> {
    
    public ItemFlagMenu() {
        InventoryListener.registerInventory(CustomInventoryType.ITEM_FLAGS_MENU, ITEM_FLAGS_MENU_FUNCTION);
    }
    
    private static final int BACK_SLOT = 18;
    private static final int NEXT_SLOT = 26;
    
    private static final ItemFlag[] ITEM_FLAGS =  ItemFlag.values();
    
    private ItemStack copy;
    
    @Override
    protected void newItem(Player player) {
        throw new UnsupportedOperationException("Method not implemented");
    }
    
    @Override
    protected void editItem(Player player) {
        openItemFlagsMenu(player);
    }
    
    @Override
    protected void reset() {
        this.copy = item.clone();
    }
    
    private void openItemFlagsMenu(Player player) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.ITEM_FLAGS_MENU, 27, "&5&lItem flags menu");
        
        ItemStack aux = this.copy.clone();
        
        ItemMeta meta = this.copy.getItemMeta();
        
        int maxSlot = Math.min(BACK_SLOT, ITEM_FLAGS.length);
        for(int i=0; i<maxSlot; i++) {
            ItemFlag flag = ITEM_FLAGS[i];
            
            ItemStack flagItem = GUIItem.getEnabledDisabledItem(meta.hasItemFlag(flag),
                    "&b" + flag.name(),
                    "&dCurrent status",
                    XMaterial.GREEN_BANNER,
                    XMaterial.RED_BANNER);
            
            ItemStackWrapper wrapper = ItemStackWrapper.fromItem(flagItem, false);
            
            if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_20_5)) {
                // Check if the flag is available for this item
                // See: https://forums.papermc.io/threads/paper-velocity-1-20-6.1152/ (ItemFlag behavioral changes​)
                meta.addItemFlags(flag);
                aux.setItemMeta(meta);
                if(!aux.getItemMeta().hasItemFlag(flag)) {
                    wrapper.setMaterial(XMaterial.ORANGE_BANNER);
                    wrapper.setLore();
                    wrapper.addLoreLine("");
                    wrapper.addLoreLine("&dCurrent status: &6Not available for this item");
                }
            }
            
            menu.setItem(i, flagItem);
        }
        
        menu.setItem(BACK_SLOT, GUIItem.getBackItem());
        menu.setItem(NEXT_SLOT, GUIItem.getNextItem());
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction ITEM_FLAGS_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        switch(e.getSlot()) {
            case BACK_SLOT:
                // Go to previous menu
                onBack.accept(player);
                break;
            case NEXT_SLOT:
                // Go to next menu
                ItemStackWrapper itemWrapper = ItemStackWrapper.fromItem(item, false);
                ItemStackWrapper copyWrapper = ItemStackWrapper.fromItem(copy, false);
                ItemFlag[] flags = copyWrapper.getItemFlags().stream().toArray(ItemFlag[]::new);
                itemWrapper.setItemFlags(flags);
                onNext.accept(player, item);
                break;
            default:
                ItemStack stack = e.getInventory().getItem(e.getSlot());
                if(stack == null || stack.getType() == Material.AIR) {
                    break;
                }
                
                String flagName = ItemStackWrapper.fromItem(stack, false).getDisplayName();
                flagName = Logger.stripColor(flagName);
                ItemFlag flag = ItemFlag.valueOf(flagName);
                
                XMaterial material = XMaterial.matchXMaterial(stack);
                switch(material) {
                    case GREEN_BANNER:
                        ItemStackWrapper.fromItem(copy, false).removeItemFlags(flag);
                        openItemFlagsMenu(player);
                        break;
                    case RED_BANNER:
                        ItemStackWrapper.fromItem(copy, false).addItemFlags(flag);
                        openItemFlagsMenu(player);
                        break;
                }
        }
//</editor-fold>
    };
}
