package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import java.util.HashMap;
import java.util.Map;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.editor.EditorType;
import me.i2000c.newalb.custom_outcomes.rewards.Executable;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.EntityReward;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GUIPagesAdapter;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RewardListMenu extends Editor<Outcome>{
    public RewardListMenu(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        InventoryListener.registerInventory(CustomInventoryType.FINISH_MENU, FINISH_MENU_FUNCTION);
        
        rewardListAdapter = new GUIPagesAdapter<>(
                REWARD_LIST_MENU_SIZE,
                (reward, index) -> {
                    ItemBuilder builder = ItemBuilder
                            .fromItem(reward.getItemToDisplay(), false);
                    if(delayMode){
                        builder.addLoreLine("");
                        builder.addLoreLine("&6Reward Delay: &a" + reward.getDelay());
                    }
                    return NBTEditor.set(builder.build(), index, REWARD_ID_TAG);
                }
        );
        rewardListAdapter.setPreviousPageSlot(PREVIOUS_PAGE_SLOT);
        rewardListAdapter.setCurrentPageSlot(CURRENT_PAGE_SLOT);
        rewardListAdapter.setNextPageSlot(NEXT_PAGE_SLOT);
//</editor-fold>
    }
    
    public static Outcome getCurrentOutcome(){
        Editor<Outcome> editor = EditorType.REWARD_LIST.getEditor();
        return ((RewardListMenu) editor).item;
    }
    
    private boolean deleteMode;
    private boolean testMode;
    private boolean cloneMode;
    private boolean delayMode;    
    
    public static final Map<Player, Executable> testRewardsPlayerList = new HashMap<>();
    
    private static final int REWARD_LIST_MENU_SIZE = 36;
    private static final int PREVIOUS_PAGE_SLOT = 42;
    private static final int CURRENT_PAGE_SLOT = 43;
    private static final int NEXT_PAGE_SLOT = 44;
    private static final String REWARD_ID_TAG = "reward_id";
    private static GUIPagesAdapter<Reward> rewardListAdapter;
    
    private static final int BACK_SLOT = 45;
    private static final int NEW_REWARD_SLOT = 47;
    private static final int CLONE_REWARD_SLOT = 48;
    private static final int CHANGE_DELAY_SLOT = 49;
    private static final int REMOVE_REWARD_SLOT = 50;
    private static final int TEST_REWARD_SLOT = 51;
    private static final int TEST_OUTCOME_SLOT = 52;    
    private static final int NEXT_SLOT = 53;
    
    @Override
    protected void reset(){
        deleteMode = false;
        testMode = false;
        cloneMode = false;
        delayMode = false;
        rewardListAdapter.goToMainPage();
    }
    
    @Override
    protected void newItem(Player player){
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected void editItem(Player player){
        openRewardListMenu(player);
    }
    
    private void openRewardListMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.FINISH_MENU, 54, "&bRewards list");
        item.sortRewards();
        
        rewardListAdapter.setItemList(item.getRewards());
        rewardListAdapter.updateMenu(menu);
        
        ItemStack saveAndExit = ItemBuilder.newItem(XMaterial.DARK_OAK_DOOR)
                .withDisplayName("&dSave and exit")
                .addLoreLine("&6The plugin will reload the custom outcomes list")
                .addLoreLine("&6after you click this option")
                .build();
        
        ItemStack add = ItemBuilder.newItem(XMaterial.SLIME_BALL)
                .withDisplayName("&aCreate new reward")
                .build();
        
        ItemStack testOutcome = ItemBuilder.newItem(XMaterial.BEACON)
                .withDisplayName("&bTest the outcome")
                .build();
        
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
        ItemBuilder.fromItem(delete, false)
                .addLoreLine("")
                .addLoreLine("&5If this mode is enabled, you will be able")
                .addLoreLine("&5to delete the reward which you click");
        
        ItemStack clone = GUIItem.getEnabledDisabledItem(
                cloneMode,
                "&3Clone rewards",
                "&eCloneMode",
                XMaterial.REPEATER,
                XMaterial.REPEATER);
        ItemBuilder.fromItem(clone, false)
                .addLoreLine("")
                .addLoreLine("&6You can clone rewards using this option.")
                .addLoreLine("&6However, you cannot clone &7EntityTowerRewards");
        
        ItemStack delay = GUIItem.getEnabledDisabledItem(
                delayMode,
                "&6Configure delay of Rewards",
                "&5DelayMode",
                XMaterial.CLOCK,
                XMaterial.CLOCK);
        ItemBuilder.fromItem(delay, false)
                .addLoreLine("")
                .addLoreLine("&3Click here to configure the delay of a reward");
        
        menu.setItem(BACK_SLOT, GUIItem.getBackItem());
        if(!deleteMode && !testMode && !cloneMode && !delayMode){
            menu.setItem(NEW_REWARD_SLOT, add);
            menu.setItem(CHANGE_DELAY_SLOT, delay);
            menu.setItem(TEST_OUTCOME_SLOT, testOutcome);
            menu.setItem(TEST_REWARD_SLOT, testReward);
            menu.setItem(CLONE_REWARD_SLOT, clone);
            menu.setItem(REMOVE_REWARD_SLOT, delete);
            menu.setItem(NEXT_SLOT, saveAndExit);
        }else if(delayMode){
            menu.setItem(CHANGE_DELAY_SLOT, delay);
        }else if(testMode){
            menu.setItem(TEST_REWARD_SLOT, testReward);
        }else if(cloneMode){
            menu.setItem(CLONE_REWARD_SLOT, clone);
        }else if(deleteMode){
            menu.setItem(REMOVE_REWARD_SLOT, delete);
        }
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction FINISH_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        switch(e.getSlot()){
            case PREVIOUS_PAGE_SLOT:
                if(rewardListAdapter.goToPreviousPage()){
                    openRewardListMenu(player);
                }
                break;
            case CURRENT_PAGE_SLOT:
                if(rewardListAdapter.goToMainPage()){
                    openRewardListMenu(player);
                }
                break;
            case NEXT_PAGE_SLOT:
                if(rewardListAdapter.goToNextPage()){
                    openRewardListMenu(player);
                }
                break;
            case NEW_REWARD_SLOT:
                if(deleteMode || testMode || cloneMode || delayMode){
                    break;
                }
                //Create new reward
                Editor<Reward> editor = EditorType.REWARD_TYPES.getEditor();
                editor.createNewItem(
                        player, 
                        p -> openRewardListMenu(p), 
                        (p, reward) -> {
                            if(reward instanceof EntityReward){
                                int entityID = item.getEntityRewardsNumber();
                                ((EntityReward) reward).setID(entityID);
                            }
                            
                            item.addReward(reward);
                            openRewardListMenu(p);
                        });
                break;
            case CHANGE_DELAY_SLOT:
                if(deleteMode || testMode || cloneMode){
                    break;
                }
                //Clone-rewards mode
                delayMode = !delayMode;
                openRewardListMenu(player);
                break;
            case TEST_OUTCOME_SLOT:
                if(deleteMode || testMode || cloneMode || delayMode){
                    break;
                }
                player.closeInventory();
                testRewardsPlayerList.put(player, item);
                Logger.sendMessage("&6Every block that you break from now will behave as a LuckyBlock with the selected outcome", player, false);
                Logger.sendMessage("&3To leave testing mode, use &7/alb return&r", player, false);
                break;
            case TEST_REWARD_SLOT:
                if(deleteMode || cloneMode || delayMode){
                    break;
                }
                //Test-rewards mode
                testMode = !testMode;
                openRewardListMenu(player);
                break;
            case CLONE_REWARD_SLOT:
                if(deleteMode || testMode || delayMode){
                    break;
                }
                //Clone-rewards mode
                cloneMode = !cloneMode;
                openRewardListMenu(player);
                break;
            case REMOVE_REWARD_SLOT:
                if(testMode || cloneMode || delayMode){
                    break;
                }
                //Delete-rewards mode
                deleteMode = !deleteMode;
                openRewardListMenu(player);
                break;
            case BACK_SLOT:                
                // Go to previous menu
                onBack.accept(player);
                break;
            case NEXT_SLOT:
                if(deleteMode || testMode || cloneMode || delayMode){
                    break;
                }
                if(item.getNumberOfRewards() == 0){
                    break;
                }
                
                //Go to next menu
                onNext.accept(player, item);
                break;
            default:
                ItemStack stack = e.getCurrentItem();
                if(stack == null || stack.getType() == Material.AIR 
                        || !NBTEditor.contains(stack, REWARD_ID_TAG)){
                    return;
                }
                
                int rewardID = NBTEditor.getInt(e.getCurrentItem(), REWARD_ID_TAG);
                Reward reward = item.getReward(rewardID);
                if(deleteMode){
                    if(item.removeReward(rewardID)){
                        openRewardListMenu(player);
                    }
                }else if(testMode){
                    testRewardsPlayerList.put(player, reward);
                    Logger.sendMessage("&6Every block that you break from now will behave as a LuckyBlock with the selected reward", player, false);
                    Logger.sendMessage("&5Selected reward: &e" + e.getSlot(), player, false);
                    Logger.sendMessage("&3To leave testing mode, use &7/alb return&r", player, false);
                    
                    testMode = false;
                    openRewardListMenu(player);
                    
                    player.closeInventory();                    
                }else if(cloneMode){
                    Reward reward2 = reward.clone();
                    if(reward2 instanceof EntityReward){
                        EntityReward entityReward = (EntityReward) reward2;
                        entityReward.setID(item.getEntityRewardsNumber());
                    }
                    item.addReward(reward2);
                    openRewardListMenu(player);
                }else if(delayMode){
                    Editor<Integer> editor2 = EditorType.DELAYER.getEditor();
                    editor2.editExistingItem(
                            reward.getDelay(), 
                            player, 
                            p -> openRewardListMenu(p), 
                            (p, delay) -> {
                                reward.setDelay(delay);
                                openRewardListMenu(p);
                            });
                }else{
                    reward.edit(
                            player, 
                            p -> openRewardListMenu(p), 
                            (p, r) -> {
                                item.setReward(rewardID, (Reward) r);
                                openRewardListMenu((Player) p);
                            });
                }
        }
//</editor-fold>
    };
}
