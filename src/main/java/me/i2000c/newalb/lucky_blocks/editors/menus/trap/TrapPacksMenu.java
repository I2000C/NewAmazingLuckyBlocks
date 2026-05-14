package me.i2000c.newalb.lucky_blocks.editors.menus.trap;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import com.cryptomorin.xseries.XEnchantment;

import me.i2000c.newalb.api.gui.MenuItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.PaginatedEditorMenu;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.OutcomePack;
import me.i2000c.newalb.lucky_blocks.rewards.PackManager;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class TrapPacksMenu extends PaginatedEditorMenu<Outcome, OutcomePack> {
    
    public TrapPacksMenu() {
        super("&3&lPack list", MenuSize.SIZE_4_ROWS, true, MenuSize.SIZE_3_ROWS);
    }
    
    @Override
    public List<OutcomePack> getItemList() {
        return PackManager.getSortedPacks();
    }
    
    @Override
    public MenuItem mapItemToPage(OutcomePack outcomePack, int index) {
        ItemStackWrapper wrapper = ItemStackWrapper.fromItem(outcomePack.getItemToDisplay(), false);
        if(item != null && outcomePack.equals(item.getPack())) {
            wrapper.addEnchantment(XEnchantment.UNBREAKING, 1);
            wrapper.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        Consumer<MenuClickEvent> action = e -> {
            TrapOutcomeMenu menu = new TrapOutcomeMenu(outcomePack);
            menu.setItemToEdit(item);
            menu.setOnBack(this::openToPlayer);
            menu.setOnNext(this::onNext);
            menu.openToPlayer(e.getPlayer());
        };
        return new MenuItem(wrapper, action);
    }
    
    @Override
    protected Outcome createNewItem() {
        return null;
    }
    
    @Override
    protected void buildMenu(Player player) {
        setBackItem(27);
    }
}
