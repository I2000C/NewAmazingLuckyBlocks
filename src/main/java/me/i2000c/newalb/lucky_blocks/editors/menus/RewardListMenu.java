package me.i2000c.newalb.lucky_blocks.editors.menus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.MenuItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.PaginatedEditorMenu;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.lucky_blocks.rewards.Executable;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.Reward;
import me.i2000c.newalb.lucky_blocks.rewards.types.EntityReward;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class RewardListMenu extends PaginatedEditorMenu<Outcome, Reward<?>> {
    
    private static final int PREVIOUS_PAGE_SLOT = 42;
    private static final int CURRENT_PAGE_SLOT = 43;
    private static final int NEXT_PAGE_SLOT = 44;
    
    private static final int BACK_SLOT = 45;
    private static final int NEW_REWARD_SLOT = 47;
    private static final int CLONE_REWARD_SLOT = 48;
    private static final int CHANGE_DELAY_SLOT = 49;
    private static final int REMOVE_REWARD_SLOT = 50;
    private static final int TEST_REWARD_SLOT = 51;
    private static final int TEST_OUTCOME_SLOT = 52;    
    private static final int NEXT_SLOT = 53;
    
    public static final Map<Player, Executable> testRewardsPlayerList = new HashMap<>();
    
    private boolean deleteMode;
    private boolean testMode;
    private boolean cloneMode;
    private boolean delayMode;
    
    public RewardListMenu() {
        super("&b&lRewards list", MenuSize.SIZE_6_ROWS, true, MenuSize.SIZE_4_ROWS, PREVIOUS_PAGE_SLOT, CURRENT_PAGE_SLOT, NEXT_PAGE_SLOT);
    }
    
    @Override
    public List<Reward<?>> getItemList() {
        item.sortRewards();
        return item.getRewards();
    }
    
    @Override
    public MenuItem mapItemToPage(Reward<?> reward, int index) {
        ItemStackWrapper wrapper = ItemStackWrapper.fromItem(reward.getItemToDisplay(), false);
        if(delayMode) {
            wrapper.addLoreLine("");
            wrapper.addLoreLine("&6Reward Delay: &a" + reward.getDelay() + " &dticks");
        }
        
        Consumer<MenuClickEvent> action = e -> {
            Player player = e.getPlayer();
            if(deleteMode) {
                if(item.removeReward(index)) {
                    resetPagination();
                    openToPlayer(player);
                }
            } else if(testMode) {
                testRewardsPlayerList.put(player, reward);
                testMode = false;
                player.closeInventory();
                Logger.sendMessage("&6Every block that you break from now will behave as a LuckyBlock with the selected reward", player, false);
                Logger.sendMessage("&5Selected reward: &e" + e.getSlot(), player, false);
                Logger.sendMessage("&3To leave testing mode, use &7/alb return&r", player, false);
            } else if(cloneMode) {
                Reward<?> clonedReward = reward.clone();
                if(clonedReward instanceof EntityReward) {
                    EntityReward entityReward = (EntityReward) clonedReward;
                    entityReward.setEntityID(item.getEntityRewardsNumber());
                }
                item.addReward(clonedReward);
                resetPagination();
                openToPlayer(player);
            } else if(delayMode) {
                DelayerMenu menu = new DelayerMenu();
                menu.setItemToEdit(reward.getDelay());
                menu.setOnBack(this::openToPlayer);
                menu.setOnNext((p, delay) -> {
                    reward.setDelay(delay);
                    openToPlayer(player);
                });
                menu.openToPlayer(player);
            } else {
                reward.edit(player, this::openToPlayer, (p, editedReward) -> {
                    item.setReward(index, editedReward);
                    resetPagination();
                    openToPlayer(player);
                });
            }
        };
        
        return new MenuItem(wrapper, action);
    }
    
    
    
    @Override
    protected void buildMenu(Player player) {
        ItemStack saveAndExit = ItemStackWrapper.newItem(XMaterial.DARK_OAK_DOOR)
                                                .setDisplayName("&dSave and exit")
                                                .addLoreLine("&6The plugin will reload the custom outcomes list")
                                                .addLoreLine("&6after you click this option")
                                                .toItemStack();
        
        ItemStack add = ItemStackWrapper.newItem(XMaterial.SLIME_BALL)
                                        .setDisplayName("&aCreate new reward")
                                        .toItemStack();
        
        ItemStack testOutcome = ItemStackWrapper.newItem(XMaterial.BEACON)
                                                .setDisplayName("&bTest the outcome")
                                                .toItemStack();
        
        ItemStack testReward = GUIItem.getEnabledDisabledItem(
                testMode, 
                "&eTest rewards", 
                "&6TestMode", 
                XMaterial.PAPER, 
                XMaterial.PAPER);
        
        ItemStack delete = GUIItem.getEnabledDisabledItem(
                deleteMode, 
                "&cDelete rewards", 
                "&4DeleteMode", 
                XMaterial.BARRIER, 
                XMaterial.BARRIER);
        ItemStackWrapper.fromItem(delete, false)
                .addLoreLine("")
                .addLoreLine("&5If this mode is enabled, you will be able")
                .addLoreLine("&5to delete the reward which you click");
        
        ItemStack clone = GUIItem.getEnabledDisabledItem(
                cloneMode,
                "&3Clone rewards",
                "&eCloneMode",
                XMaterial.REPEATER,
                XMaterial.REPEATER);
        ItemStackWrapper.fromItem(clone, false)
                .addLoreLine("")
                .addLoreLine("&6You can clone rewards using this option");
        
        ItemStack delay = GUIItem.getEnabledDisabledItem(
                delayMode,
                "&6Configure delay of Rewards",
                "&5DelayMode",
                XMaterial.CLOCK,
                XMaterial.CLOCK);
        ItemStackWrapper.fromItem(delay, false)
                .addLoreLine("")
                .addLoreLine("&eClick here to configure the delay of a reward");
        
        setBackItem(BACK_SLOT);
        if(!deleteMode && !testMode && !cloneMode && !delayMode) {
            setItem(NEW_REWARD_SLOT, add, e -> {
                RewardTypesMenu menu = new RewardTypesMenu(item);
                menu.setOnBack(this::openToPlayer);
                menu.setOnNext((p, reward) -> {
                    item.addReward(reward);
                    resetPagination();
                    openToPlayer(player);
                });
                menu.openToPlayer(player);
            });
            setItem(CHANGE_DELAY_SLOT, delay, e -> {
                delayMode = !delayMode;
                openToPlayer(player);
            });
            setItem(TEST_OUTCOME_SLOT, testOutcome, e -> {
                player.closeInventory();
                testRewardsPlayerList.put(player, item);
                Logger.sendMessage("&6Every block that you break from now will behave as a LuckyBlock with the selected outcome", player, false);
                Logger.sendMessage("&3To leave testing mode, use &7/alb return&r", player, false);
            });
            setItem(TEST_REWARD_SLOT, testReward, e -> {
                testMode = !testMode;
                openToPlayer(player);
            });
            setItem(CLONE_REWARD_SLOT, clone, e -> {
                cloneMode = !cloneMode;
                openToPlayer(player);
            });
            setItem(REMOVE_REWARD_SLOT, delete, e -> {
                deleteMode = !deleteMode;
                openToPlayer(player);
            });
            setItem(NEXT_SLOT, saveAndExit, e -> {
                if(item.getNumberOfRewards() > 0) {
                    onNext(player, item);
                }
            });
        } else if(delayMode) {
            setItem(CHANGE_DELAY_SLOT, delay, e -> {
                delayMode = !delayMode;
                openToPlayer(player);
            });
        } else if(testMode) {
            setItem(TEST_REWARD_SLOT, testReward, e -> {
                testMode = !testMode;
                openToPlayer(player);
            });
        } else if(cloneMode) {
            setItem(CLONE_REWARD_SLOT, clone, e -> {
                cloneMode = !cloneMode;
                openToPlayer(player);
            });
        } else if(deleteMode) {
            setItem(REMOVE_REWARD_SLOT, delete, e -> {
                deleteMode = !deleteMode;
                openToPlayer(player);
            });
        }
    }
}
