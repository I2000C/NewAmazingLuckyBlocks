package me.i2000c.newalb.lucky_blocks.editors.menus;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;

import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.api.gui.MenuItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.PaginatedEditorMenu;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.Reward;
import me.i2000c.newalb.lucky_blocks.rewards.RewardType;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class RewardTypesMenu extends PaginatedEditorMenu<Reward<?>, RewardType> {
    
    private final Outcome outcome;
    
    public RewardTypesMenu(Outcome outcome) {
        super("&2&lSelect reward type", MenuSize.SIZE_3_ROWS, true, MenuSize.SIZE_2_ROWS);
        this.outcome = outcome;
    }
    
    @Override
    public List<RewardType> getItemList() {
        return Arrays.asList(RewardType.VALUES);
    }
    
    @Override
    public MenuItem mapItemToPage(RewardType rewardType, int index) {
        ItemStackWrapper wrapper = ItemStackWrapper.newItem(rewardType.getRewardItem());
        wrapper.setDisplayName(rewardType.getRewardDisplayName());
        boolean allowCreateReward = true;
        if(rewardType == RewardType.tower_entity && outcome.getEntityRewardsNumber() < 1) {
            wrapper.addLoreLine("&cYou need to have created at least 1 entity");
            wrapper.addLoreLine("  &cin order to use this reward");
            allowCreateReward = false;
        } else if(rewardType == RewardType.structure && NewAmazingLuckyBlocks.getWorldEditPlugin() == null) {
            wrapper.addLoreLine("&cYou need WorldEdit in order to use this reward");
            allowCreateReward = false;
        }
        
        Consumer<MenuClickEvent> action = e -> {
            Reward<?> reward = rewardType.createReward(outcome);
            reward.edit(e.getPlayer(), this::openToPlayer, this::onNext);
        };
        
        MenuItem menuItem;
        if(allowCreateReward) {
            menuItem = new MenuItem(wrapper, action);
        } else {
            menuItem = new MenuItem(wrapper);
        }
        
        return menuItem;
    }
    
    @Override
    protected Reward<?> createNewItem() {
        return null;
    }
    
    @Override
    protected void buildMenu(Player player) {
        setBackItem(18);
    }
}
