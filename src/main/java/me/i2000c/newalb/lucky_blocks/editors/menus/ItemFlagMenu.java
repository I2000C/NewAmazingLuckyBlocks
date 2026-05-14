package me.i2000c.newalb.lucky_blocks.editors.menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.api.version.MinecraftVersion;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class ItemFlagMenu extends EditorMenu<ItemStack> {

    public ItemFlagMenu() {
        super("&5&lItem flags menu", MenuSize.SIZE_3_ROWS, true);
    }
    
    private static final int BACK_SLOT = 18;
    private static final int NEXT_SLOT = 26;
    
    private static final ItemFlag[] ITEM_FLAGS = ItemFlag.values();
    
    private ItemStack copy;
    
    @Override
    protected void buildMenu(Player player) {
        if(copy == null) {
            copy = item.clone();
        }
        
        ItemMeta meta = this.copy.getItemMeta();
        ItemStack aux = this.copy.clone();
        
        int maxSlot = Math.min(BACK_SLOT, ITEM_FLAGS.length);
        for(int slot=0; slot<maxSlot; slot++) {
            ItemFlag flag = ITEM_FLAGS[slot];
            
            ItemStack flagItem = GUIItem.getEnabledDisabledItem(meta.hasItemFlag(flag),
                    "&b" + flag.name(),
                    "&dCurrent status",
                    XMaterial.GREEN_BANNER,
                    XMaterial.RED_BANNER);
            
            ItemStackWrapper wrapper = ItemStackWrapper.fromItem(flagItem, false);
            boolean isFlagAvailable = true;
            
            if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_20_5)) {
                // Check if the flag is available for this item
                // See: https://forums.papermc.io/threads/paper-velocity-1-20-6.1152/ (ItemFlag behavioral changes​)
                ItemMeta auxMeta = meta.clone();
                auxMeta.addItemFlags(flag);
                aux.setItemMeta(auxMeta);
                if(!aux.getItemMeta().hasItemFlag(flag)) {
                    wrapper.setMaterial(XMaterial.ORANGE_BANNER);
                    wrapper.setLore();
                    wrapper.addLoreLine("");
                    wrapper.addLoreLine("&dCurrent status: &6Not available for this item");
                    isFlagAvailable = false;
                }
            }
            
            setItem(slot, flagItem);
            if(isFlagAvailable) {
                setClickAction(slot, e -> {
                    ItemStackWrapper wrapper2 = ItemStackWrapper.fromItem(copy, false);
                    if(wrapper2.hasItemFlag(flag)) {
                        wrapper2.removeItemFlags(flag);
                    } else {
                        wrapper2.addItemFlags(flag);
                    }
                    openToPlayer(e.getPlayer());
                });
            }
        }
        
        setBackItem(BACK_SLOT);
        setNextItem(NEXT_SLOT, e -> {
            // Go to next menu
            ItemStackWrapper itemWrapper = ItemStackWrapper.fromItem(item, false);
            ItemStackWrapper copyWrapper = ItemStackWrapper.fromItem(copy, false);
            ItemFlag[] flags = copyWrapper.getItemFlags().stream().toArray(ItemFlag[]::new);
            itemWrapper.setItemFlags(flags);
            onNext(player, item);
        });
    }
}
