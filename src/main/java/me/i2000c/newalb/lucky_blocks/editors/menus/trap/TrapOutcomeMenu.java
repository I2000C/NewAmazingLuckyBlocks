package me.i2000c.newalb.lucky_blocks.editors.menus.trap;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import com.cryptomorin.xseries.XEnchantment;

import lombok.NonNull;
import me.i2000c.newalb.api.gui.MenuItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.PaginatedEditorMenu;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.OutcomePack;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class TrapOutcomeMenu extends PaginatedEditorMenu<Outcome, Outcome> {
    
    private final OutcomePack outcomePack;
    
    public TrapOutcomeMenu(@NonNull OutcomePack outcomePack) {
        super("&3&lOutcomes list", MenuSize.SIZE_4_ROWS, true, MenuSize.SIZE_3_ROWS);
        this.outcomePack = outcomePack;
    }
    
    @Override
    public List<Outcome> getItemList() {
        return outcomePack.getSortedOutcomes();
    }
    
    @Override
    public MenuItem mapItemToPage(Outcome outcome, int index) {
        ItemStackWrapper wrapper = ItemStackWrapper.fromItem(outcome.getItemToDisplay(), false);
        if(outcome.equals(item)) {
            wrapper.addEnchantment(XEnchantment.UNBREAKING, 1);
            wrapper.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        Consumer<MenuClickEvent> action = e -> onNext(e.getPlayer(), outcome);
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
