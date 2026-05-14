package me.i2000c.newalb.lucky_blocks.editors.menus.luckyblock_type;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import com.cryptomorin.xseries.XEnchantment;

import me.i2000c.newalb.api.gui.MenuItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.PaginatedEditorMenu;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.lucky_blocks.rewards.OutcomePack;
import me.i2000c.newalb.lucky_blocks.rewards.PackManager;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

class PackSelectMenu extends PaginatedEditorMenu<OutcomePack, OutcomePack> {
    
    public PackSelectMenu() {
        super("&3&lPack list", MenuSize.SIZE_4_ROWS, true, MenuSize.SIZE_3_ROWS);
    }
    
    @Override
    public List<OutcomePack> getItemList() {
        return PackManager.getSortedPacks();
    }
    
    @Override
    public MenuItem mapItemToPage(OutcomePack outcomePack, int index) {
        ItemStackWrapper wrapper = ItemStackWrapper.fromItem(outcomePack.getItemToDisplay(), false);
        if(item != null && outcomePack.equals(item)) {
            wrapper.addEnchantment(XEnchantment.UNBREAKING, 1);
            wrapper.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        Consumer<MenuClickEvent> action = e -> onNext(e.getPlayer(), outcomePack);
        return new MenuItem(wrapper, action);
    }
    
    @Override
    protected OutcomePack createNewItem() {
        return null;
    }
    
    @Override
    protected void buildMenu(Player player) {
        setBackItem(27);
    }
}
