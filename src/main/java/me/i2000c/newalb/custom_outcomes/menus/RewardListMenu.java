package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import java.util.HashMap;
import java.util.Map;
import me.i2000c.newalb.custom_outcomes.utils.Executable;
import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import me.i2000c.newalb.custom_outcomes.utils.OutcomePack;
import me.i2000c.newalb.custom_outcomes.utils.rewards.EntityReward;
import me.i2000c.newalb.custom_outcomes.utils.rewards.Reward;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GUIPagesAdapter;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RewardListMenu{
    public static final Map<Player, Executable> testRewardsPlayerList = new HashMap<>();
    
    private static int rewardEditID = -1;
    
    private static final int REWARD_LIST_MENU_SIZE = 36;
    private static final int PREVIOUS_PAGE_SLOT = 42;
    private static final int CURRENT_PAGE_SLOT = 43;
    private static final int NEXT_PAGE_SLOT = 44;
    private static final String REWARD_ID_TAG = "reward_id";
    private static GUIPagesAdapter<Reward> rewardListAdapter;
    
    private static final int NEW_REWARD_SLOT = 45;
    private static final int EDIT_REWARD_SLOT = 46;
    private static final int CLONE_REWARD_SLOT = 47;
    private static final int CHANGE_DELAY_SLOT = 48;
    private static final int REMOVE_REWARD_SLOT = 49;
    private static final int TEST_REWARD_SLOT = 50;
    private static final int TEST_OUTCOME_SLOT = 51;
    private static final int EXIT_SLOT = 52;
    private static final int SAVE_AND_EXIT_SLOT = 53;
    
    private static OutcomePack currentPack = null;
    private static Outcome currentOutcome = null;
    private static boolean deleteMode = false;
    private static boolean testMode = false;
    protected static boolean editMode = false;
    private static boolean cloneMode = false;
    private static boolean delayMode = false;
    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
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
            
            inventoriesRegistered = true;
        }
        
        rewardListAdapter.goToMainPage();
        
        testRewardsPlayerList.clear();
        rewardEditID = -1;
        currentPack = null;
        currentOutcome = null;
        deleteMode = false;
        testMode = false;
        editMode = false;
        cloneMode = false;
        delayMode = false;
        GUIManager.setCurrentInventory(null);
    }
    
    public static Outcome getCurrentOutcome(){
        return currentOutcome;
    }
    public static void setCurrentOutcome(Outcome outcome){
        currentOutcome = outcome;
    }
    
    public static OutcomePack getCurrentPack(){
        return currentPack;
    }
    public static void setCurrentPack(OutcomePack pack){
        currentPack = pack;
    }
    
    public static void addReward(Reward r){
        if(editMode){
            currentOutcome.setReward(rewardEditID, r);
        }else{
            currentOutcome.addReward(r);
        }
    }
    
    public static void openFinishInventory(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.FINISH_MENU, 54, "&bRewards list");
        currentOutcome.sortRewards();
        
        rewardListAdapter.setItemList(currentOutcome.getRewards());
        rewardListAdapter.updateMenu(inv);
        
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
        
        ItemStack exit = ItemBuilder.newItem(XMaterial.IRON_DOOR)
                .withDisplayName("&cExit without saving")
                .build();
        
        ItemStack edit = GUIItem.getEnabledDisabledItem(
                editMode, 
                "&6Edit rewards", 
                "&dEditMode", 
                XMaterial.ANVIL, 
                XMaterial.ANVIL);
        
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
        
        if(!deleteMode && !testMode && !editMode && !cloneMode && !delayMode){
            inv.setItem(NEW_REWARD_SLOT, add);
            inv.setItem(CHANGE_DELAY_SLOT, delay);
            inv.setItem(TEST_OUTCOME_SLOT, testOutcome);
            inv.setItem(TEST_REWARD_SLOT, testReward);
            inv.setItem(CLONE_REWARD_SLOT, clone);
            inv.setItem(EDIT_REWARD_SLOT, edit);
            inv.setItem(REMOVE_REWARD_SLOT, delete);
            inv.setItem(EXIT_SLOT, exit);
            inv.setItem(SAVE_AND_EXIT_SLOT, saveAndExit);
        }else if(delayMode){
            inv.setItem(CHANGE_DELAY_SLOT, delay);
        }else if(testMode){
            inv.setItem(TEST_REWARD_SLOT, testReward);
        }else if(cloneMode){
            inv.setItem(CLONE_REWARD_SLOT, clone);
        }else if(editMode){
            inv.setItem(EDIT_REWARD_SLOT, edit);
        }else if(deleteMode){
            inv.setItem(REMOVE_REWARD_SLOT, delete);
        }
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction FINISH_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        switch(e.getSlot()){
            case PREVIOUS_PAGE_SLOT:
                if(rewardListAdapter.goToPreviousPage()){
                    openFinishInventory(p);
                }
                break;
            case CURRENT_PAGE_SLOT:
                if(rewardListAdapter.goToMainPage()){
                    openFinishInventory(p);
                }
                break;
            case NEXT_PAGE_SLOT:
                if(rewardListAdapter.goToNextPage()){
                    openFinishInventory(p);
                }
                break;
            case NEW_REWARD_SLOT:
                if(deleteMode || testMode || editMode || cloneMode || delayMode){
                    break;
                }
                //Create new reward
                RewardTypesMenu.openRewardTypesMenu(p);
                break;
            case CHANGE_DELAY_SLOT:
                if(deleteMode || testMode || editMode || cloneMode){
                    break;
                }
                //Clone-rewards mode
                delayMode = !delayMode;
                openFinishInventory(p);
                break;
            case TEST_OUTCOME_SLOT:
                if(deleteMode || editMode || testMode || cloneMode || delayMode){
                    break;
                }
                p.closeInventory();
                testRewardsPlayerList.put(p, currentOutcome);
                Logger.sendMessage("&6Every block that you break from now will behave as a LuckyBlock with the selected outcome", p, false);
                Logger.sendMessage("&3To leave testing mode, use &7/alb return&r", p, false);
                break;
            case TEST_REWARD_SLOT:
                if(deleteMode || editMode || cloneMode || delayMode){
                    break;
                }
                //Test-rewards mode
                testMode = !testMode;
                openFinishInventory(p);
                break;
            case CLONE_REWARD_SLOT:
                if(deleteMode || testMode || editMode || delayMode){
                    break;
                }
                //Clone-rewards mode
                cloneMode = !cloneMode;
                openFinishInventory(p);
                break;
            case EDIT_REWARD_SLOT:
                if(deleteMode || testMode || cloneMode || delayMode){
                    break;
                }
                //Edit-rewards mode
                editMode = !editMode;
                openFinishInventory(p);
                break;
            case REMOVE_REWARD_SLOT:
                if(testMode || editMode || cloneMode || delayMode){
                    break;
                }
                //Delete-rewards mode
                deleteMode = !deleteMode;
                openFinishInventory(p);
                break;
            case EXIT_SLOT:
                if(deleteMode || testMode || editMode || cloneMode || delayMode){
                    break;
                }
                //Exit without saving
                OutcomeListMenu.openOutcomeListMenu(p, currentPack);
                reset();
                break;
            case SAVE_AND_EXIT_SLOT:
                if(deleteMode || testMode || editMode || cloneMode || delayMode){
                    break;
                }
                if(currentOutcome.getNumberOfRewards() == 0){
                    return;
                }
                //Save outcome
                try{
                    currentOutcome.saveOutcome();
                    Logger.sendMessage("&aOutcome saved with ID &b" + currentOutcome.getID(), p);
                }catch(Exception ex){
                    Logger.sendMessage("&cError while saving outcome with ID &b" + currentOutcome.getID(), p);
                    ex.printStackTrace();
                }
                
                p.closeInventory();
                Logger.log("&6Reloading pack " + currentOutcome.getPack().getFilename() + "...");
                currentOutcome.getPack().loadPack();
                Logger.log("&aPack reloaded");
                reset();
                
                break;
            default:
                ItemStack stack = e.getCurrentItem();
                if(stack == null || stack.getType() == Material.AIR 
                        || !NBTEditor.contains(stack, REWARD_ID_TAG)){
                    return;
                }
                
                int rewardID = NBTEditor.getInt(e.getCurrentItem(), REWARD_ID_TAG);
                if(deleteMode){
                    if(currentOutcome.removeReward(rewardID)){
                        openFinishInventory(p);
                    }
                }else if(testMode){
                    if(rewardID < currentOutcome.getNumberOfRewards()){
                        Reward reward = currentOutcome.getReward(rewardID);
                        testRewardsPlayerList.put(p, reward);
                        Logger.sendMessage("&6Every block that you break from now will behave as a LuckyBlock with the selected reward", p, false);
                        Logger.sendMessage("&5Selected reward: &e" + e.getSlot(), p, false);
                        Logger.sendMessage("&3To leave testing mode, use &7/alb return&r", p, false);
                        p.closeInventory();
                    }
                }else if(editMode){
                    Reward r = currentOutcome.getReward(rewardID).clone();
                    rewardEditID = rewardID;
                    r.edit(p);
                }else if(cloneMode){
                    Reward reward = currentOutcome.getReward(rewardID).clone();
                    if(reward instanceof EntityReward){
                        EntityReward entityReward = (EntityReward) reward;
                        entityReward.setID(currentOutcome.getEntityRewardsNumber());
                    }
                    currentOutcome.addReward(reward);
                    openFinishInventory(p);
                }else if(delayMode){
                    if(rewardID < currentOutcome.getNumberOfRewards()){
                        DelayerMenu.reset();
                        DelayerMenu.reward = currentOutcome.getReward(rewardID);
                        DelayerMenu.openDelayerMenu(p);
                    }
                }
        }
//</editor-fold>
    };
}
