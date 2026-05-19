package me.i2000c.newalb.utils.menus.preview;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;

import me.i2000c.newalb.api.gui.MenuItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.PaginatedMenu;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.OutcomePack;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

class OutcomePackPreviewMenu extends PaginatedMenu<Outcome> {
    
    private final OutcomePack outcomePack;
    
    public OutcomePackPreviewMenu(OutcomePack outcomePack) {
        super("&3&lOutcomes list", MenuSize.SIZE_6_ROWS, false, MenuSize.SIZE_5_ROWS);
        this.outcomePack = outcomePack;
    }
    
    @Override
    public List<Outcome> getItemList() {
        return outcomePack.getSortedOutcomes();
    }
    
    @Override
    public MenuItem mapItemToPage(Outcome outcome, int index) {
        ItemStackWrapper wrapper = ItemStackWrapper.fromItem(outcome.getItemToDisplay(), false);
        wrapper.addLoreLine("");
        wrapper.addLoreLine("&3Click to see reward list");
        Consumer<MenuClickEvent> action = e -> {
            OutcomePreviewMenu menu = new OutcomePreviewMenu(outcome);
            menu.setOnBack(this::openToPlayer);
            menu.openToPlayer(e.getPlayer());
        };
        return new MenuItem(wrapper, action);
    }
    
    @Override
    protected void buildMenu(Player player) {
        setBackItem(45);
    }
}
