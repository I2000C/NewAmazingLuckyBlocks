package me.i2000c.newalb.utils.menus.preview;

import java.util.List;

import org.bukkit.entity.Player;

import me.i2000c.newalb.api.gui.MenuItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.PaginatedMenu;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.Reward;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

class OutcomePreviewMenu extends PaginatedMenu<Reward<?>> {
    
    private final Outcome outcome;
    
    public OutcomePreviewMenu(Outcome outcome) {
        super("&b&lReward list", MenuSize.SIZE_6_ROWS, false, MenuSize.SIZE_5_ROWS);
        this.outcome = outcome;
    }
    
    @Override
    public List<Reward<?>> getItemList() {
        return outcome.getRewards();
    }
    
    @Override
    public MenuItem mapItemToPage(Reward<?> reward, int index) {
        ItemStackWrapper wrapper = ItemStackWrapper.fromItem(reward.getItemToDisplay(), false);
        return new MenuItem(wrapper);
    }
    
    @Override
    protected void buildMenu(Player player) {
        setBackItem(45);
    }
}
