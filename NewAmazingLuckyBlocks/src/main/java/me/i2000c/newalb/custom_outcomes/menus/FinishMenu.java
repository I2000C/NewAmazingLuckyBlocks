package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.utils.rewards.EntityReward;
import me.i2000c.newalb.custom_outcomes.utils.rewards.EntityTowerReward;
import java.util.ArrayList;
import java.util.List;
import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import me.i2000c.newalb.custom_outcomes.utils.OutcomePack;
import me.i2000c.newalb.custom_outcomes.utils.rewards.Reward;
import me.i2000c.newalb.utils.Logger;
import java.util.HashMap;
import java.util.Map;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.custom_outcomes.utils.Executable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.Player;

public class FinishMenu{
    private static int index;
    private static final int MENU_SIZE = 36;
    private static int max_pages;
    
    
    public static final Map<Player, Executable> testRewardsPlayerList = new HashMap<>();
    
    private static int rewardEditID = -1;
    
    private static final int PREVIOUS_PAGE_SLOT = 42;
    private static final int CURRENT_PAGE_SLOT = 43;
    private static final int NEXT_PAGE_SLOT = 44;
    
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
            
            inventoriesRegistered = true;
        }
        
        index = 0;
        
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
        
        if(currentOutcome.getNumberOfRewards() % MENU_SIZE == 0){
            max_pages = currentOutcome.getNumberOfRewards() / MENU_SIZE;
        }else{
            max_pages = currentOutcome.getNumberOfRewards() / MENU_SIZE + 1;
        }
        
        int n = Integer.min(currentOutcome.getNumberOfRewards() - MENU_SIZE*index, MENU_SIZE);
        for(int i=0;i<n;i++){
            Reward reward = currentOutcome.getReward(i + index*MENU_SIZE);
            ItemStack itemToDisplay = reward.getItemToDisplay();
            if(delayMode){
                ItemMeta meta = itemToDisplay.getItemMeta();
                List<String> lore;
                if(meta.hasLore()){
                    lore = meta.getLore();
                }else{
                    lore = new ArrayList<>();
                }
                lore.add("");
                lore.add(Logger.color("&6Reward Delay: &a" + reward.getDelay()));
                meta.setLore(lore);
                itemToDisplay.setItemMeta(meta);
            }
            inv.setItem(i, itemToDisplay);
        }
        
        List<String> lore = new ArrayList<>();
        lore.add(Logger.color("&6The plugin will reload the custom outcomes list"));
        lore.add(Logger.color("&6after you click this option"));
        
        ItemStack saveAndExit = new ItemStack(Material.MAGMA_CREAM);
        ItemMeta meta = saveAndExit.getItemMeta();
        meta.setDisplayName(Logger.color("&dSave and exit"));
        meta.setLore(lore);
        saveAndExit.setItemMeta(meta);
        
        ItemStack add = new ItemStack(Material.SLIME_BALL);
        meta = add.getItemMeta();
        meta.setDisplayName(Logger.color("&aCreate new reward"));
        add.setItemMeta(meta);
        
        ItemStack testOutcome = new ItemStack(Material.BEACON);
        meta = testOutcome.getItemMeta();
        meta.setDisplayName(Logger.color("&bTest the outcome"));
        testOutcome.setItemMeta(meta);
        
        lore.clear();
        lore.add("");
        if(testMode){
            lore.add(Logger.color("&6TestMode: &aenabled"));
        }else{
            lore.add(Logger.color("&6TestMode: &7disabled"));
        }
        ItemStack testReward = new ItemStack(Material.PAPER);
        meta = testReward.getItemMeta();
        meta.setDisplayName(Logger.color("&eTest rewards"));
        meta.setLore(lore);
        testReward.setItemMeta(meta);
        
        lore.clear();
        lore.add("");
        lore.add(Logger.color("&5Click here to toggle reward deleteMode"));
        lore.add(Logger.color("&5If this mode is enabled, you will be able"));
        lore.add(Logger.color("&5to delete the reward which you click"));
        lore.add("");
        if(deleteMode){
            lore.add(Logger.color("&4DeleteMode: &aenabled"));
        }else{
            lore.add(Logger.color("&4DeleteMode: &7disabled"));
        }
        ItemStack del = new ItemStack(Material.BARRIER);
        meta = del.getItemMeta();
        meta.setDisplayName(Logger.color("&cDelete rewards"));
        meta.setLore(lore);
        del.setItemMeta(meta);
        
        ItemStack exit = new ItemStack(Material.IRON_DOOR);
        meta = exit.getItemMeta();
        meta.setDisplayName(Logger.color("&cExit without saving"));
        exit.setItemMeta(meta);
        
        lore.clear();
        lore.add("");
        if(editMode){
            lore.add(Logger.color("&dEditMode: &aenabled"));
        }else{
            lore.add(Logger.color("&dEditMode: &7disabled"));
        }
        ItemStack edit = new ItemStack(Material.ANVIL);
        meta = edit.getItemMeta();
        meta.setDisplayName(Logger.color("&6Edit rewards"));
        meta.setLore(lore);
        edit.setItemMeta(meta);
        
        lore.clear();
        lore.add("");
        lore.add(Logger.color("&6You can clone rewards using this option."));
        lore.add(Logger.color("&6However, you cannot clone &7EntityTowerRewards"));
        lore.add("");
        if(cloneMode){
            lore.add(Logger.color("&eCloneMode: &aenabled"));
        }else{
            lore.add(Logger.color("&eCloneMode: &7disabled"));
        }
        ItemStack clone = XMaterial.REPEATER.parseItem();
        meta = clone.getItemMeta();
        meta.setDisplayName(Logger.color("&3Clone Rewards"));
        meta.setLore(lore);
        clone.setItemMeta(meta);
        
        lore.clear();
        lore.add("");
        lore.add(Logger.color("&3Click here to configure the delay of a reward"));
        lore.add("");
        if(delayMode){
            lore.add(Logger.color("&5DelayMode: &aenabled"));
        }else{
            lore.add(Logger.color("&5DelayMode: &7disabled"));
        }
        ItemStack delay = XMaterial.CLOCK.parseItem();
        meta = delay.getItemMeta();
        meta.setDisplayName(Logger.color("&6Configure delay of Rewards"));
        meta.setLore(lore);
        delay.setItemMeta(meta);
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName(Logger.color("&2Previous page"));
        back.setItemMeta(meta);

        ItemStack currentPage = new ItemStack(Material.BOOK, index+1);
        meta = currentPage.getItemMeta();
        meta.setDisplayName(Logger.color("&6Page &3" + (index+1) + " &a/ &3" + max_pages));
        currentPage.setItemMeta(meta);

        ItemStack next = XMaterial.ENDER_EYE.parseItem();
        meta = next.getItemMeta();
        meta.setDisplayName(Logger.color("&2Next page"));
        next.setItemMeta(meta);

        inv.setItem(PREVIOUS_PAGE_SLOT, back);
        inv.setItem(CURRENT_PAGE_SLOT, currentPage);
        inv.setItem(NEXT_PAGE_SLOT, next);
        
        if(!deleteMode && !testMode && !editMode && !cloneMode && !delayMode){
            inv.setItem(NEW_REWARD_SLOT, add);
            inv.setItem(CHANGE_DELAY_SLOT, delay);
            inv.setItem(TEST_OUTCOME_SLOT, testOutcome);
            inv.setItem(TEST_REWARD_SLOT, testReward);
            inv.setItem(CLONE_REWARD_SLOT, clone);
            inv.setItem(EDIT_REWARD_SLOT, edit);
            inv.setItem(REMOVE_REWARD_SLOT, del);
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
            inv.setItem(REMOVE_REWARD_SLOT, del);
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
                index--;
                if(index < 0){
                    index = max_pages - 1;
                }
                openFinishInventory(p);
                break;
            case CURRENT_PAGE_SLOT:
                index = 0;
                openFinishInventory(p);
                break;
            case NEXT_PAGE_SLOT:
                index++;
                if(index >= max_pages){
                    index = 0;
                }
                openFinishInventory(p);
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
                p.sendMessage(Logger.color("&6Every block that you break from now will behave as a LuckyBlock with the selected outcome"));
                p.sendMessage(Logger.color("&3To leave testing mode, use &7/alb return&r"));
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
                int rewardID = e.getSlot() + index*MENU_SIZE;
                if(deleteMode){
                    if(currentOutcome.removeReward(rewardID)){
                        openFinishInventory(p);
                    }
                }else if(testMode){
                    if(rewardID < currentOutcome.getNumberOfRewards()){
                        Reward reward = currentOutcome.getReward(rewardID);
                        testRewardsPlayerList.put(p, reward);
                        p.sendMessage(Logger.color("&6Every block that you break from now will behave as a LuckyBlock with the selected reward"));
                        p.sendMessage(Logger.color("&5Selected reward: &e" + e.getSlot()));
                        p.sendMessage(Logger.color("&3To leave testing mode, use &7/alb return&r"));
                        p.closeInventory();
                    }
                }else if(editMode){
                    try{
                        Reward r = currentOutcome.getReward(rewardID).cloneReward();
                        rewardEditID = rewardID;
                        r.edit(p);
                    }catch(IndexOutOfBoundsException ex){
                    }
                }else if(cloneMode){
                    try{
                        Reward r = currentOutcome.getReward(rewardID);
                        if(r instanceof EntityTowerReward){
                            
                        }else if(r instanceof EntityReward){
                            EntityReward er = (EntityReward) r.cloneReward();
                            er.setID(currentOutcome.getEntityRewardList().size());
                            FinishMenu.currentOutcome.addReward(er);
                            openFinishInventory(p);
                        }else{
                            FinishMenu.currentOutcome.addReward(r.cloneReward());
                            openFinishInventory(p);
                        }
                    }catch(IndexOutOfBoundsException ex){
                    }
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
