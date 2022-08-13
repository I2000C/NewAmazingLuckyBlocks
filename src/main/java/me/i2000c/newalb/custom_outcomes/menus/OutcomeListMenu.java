package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import me.i2000c.newalb.custom_outcomes.utils.OutcomePack;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GUIPagesAdapter;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class OutcomeListMenu{
    private static OutcomePack currentPack;
    
    private static final int OUTCOME_LIST_MENU_SIZE = 45;
    private static final int PREVIOUS_PAGE_SLOT = 51;    
    private static final int CURRENT_PAGE_SLOT = 52;    
    private static final int NEXT_PAGE_SLOT = 53;    
    private static final String OUTCOME_ID_TAG = "outcome_id";
    private static GUIPagesAdapter<Outcome> outcomeListAdapter;
    
    private static boolean editMode;
    private static boolean cloneMode;
    private static boolean deleteMode;
    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.OUTCOME_LIST_MENU, OUTCOME_LIST_MENU_FUNCTION);
            
            outcomeListAdapter = new GUIPagesAdapter<>(
                    OUTCOME_LIST_MENU_SIZE,
                    (outcome, index) -> {
                        ItemStack stack = outcome.getItemToDisplay();
                        return NBTEditor.set(stack, index, OUTCOME_ID_TAG);
                    }
            );
            outcomeListAdapter.setPreviousPageSlot(PREVIOUS_PAGE_SLOT);
            outcomeListAdapter.setCurrentPageSlot(CURRENT_PAGE_SLOT);
            outcomeListAdapter.setNextPageSlot(NEXT_PAGE_SLOT);
            
            inventoriesRegistered = true;
        }
        
        currentPack = null;
        
        outcomeListAdapter.goToMainPage();
        
        editMode = false;
        cloneMode = false;
        deleteMode = false;
    }
    
    public static void openOutcomeListMenu(Player p, OutcomePack pack){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(currentPack == null){
            currentPack = pack;
        }
        
        outcomeListAdapter.setItemList(currentPack.getSortedOutcomes());
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.OUTCOME_LIST_MENU, 54, "&3&lOutcomes list");
        
        outcomeListAdapter.updateMenu(inv);
        
        ItemStack createOutcome = ItemBuilder.newItem(XMaterial.SLIME_BALL)
                .withDisplayName("&aCreate new outcome")
                .build();
        
        ItemStack editOutcome = GUIItem.getEnabledDisabledItem(
                editMode, 
                "&6Edit outcomes", 
                "&dEdit mode", 
                XMaterial.ANVIL, 
                XMaterial.ANVIL);
        
        ItemStack cloneOutcome = GUIItem.getEnabledDisabledItem(
                cloneMode, 
                "&3Clone outcomes", 
                "&dClone mode", 
                XMaterial.REPEATER, 
                XMaterial.REPEATER);
        
        ItemStack deleteOutcome = GUIItem.getEnabledDisabledItem(
                deleteMode,
                "&cDelete outcomes",
                "&dDelete mode", 
                XMaterial.BARRIER,
                XMaterial.BARRIER);
        ItemBuilder.fromItem(deleteOutcome, false)
                .addLoreLine("")
                .addLoreLine("&4&lWARNING: &cIf this mode is enabled,")
                .addLoreLine("&cwhen you click on an outcome,")
                .addLoreLine("&cit will be deleted permanently");
        
        inv.setItem(45, GUIItem.getBackItem());
        
        if(!editMode && !cloneMode && !deleteMode){
            inv.setItem(46, createOutcome);
        }
        /*if(!cloneMode && !deleteMode){
            inv.setItem(47, editOutcome);
        }*/
        if(!editMode && !deleteMode){
            inv.setItem(47, cloneOutcome);
        }
        if(!editMode && !cloneMode){
            inv.setItem(48, deleteOutcome);
        }
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction OUTCOME_LIST_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
                switch(e.getSlot()){
                    case 45:
                        //Back
                        reset();
                        GUIPackManager.openMainMenu(p);
                        break;
                    case 46:
                        //Create outcome
                        if(!editMode && !cloneMode && !deleteMode){
                            FinishMenu.reset();
                            FinishMenu.setCurrentPack(currentPack);
                            GUIManager.reset();
                            GUIManager.editMode = false;
                            GUIManager.newOutcome(p);
                        }
                        break;
                    case 47:
                        //Toggle clone outcome mode
                        if(!editMode && !deleteMode){
                            cloneMode = !cloneMode;
                            openOutcomeListMenu(p, currentPack);
                        }
                        break;
                    case 48:
                        //Toggle delete outcome mode
                        if(!editMode && !cloneMode){
                            deleteMode = !deleteMode;
                            openOutcomeListMenu(p, currentPack);
                        }
                        break;
                    case 51:
                        // Go to previous page
                        if(outcomeListAdapter.goToPreviousPage()){
                            openOutcomeListMenu(p, currentPack);
                        }
                        break;
                    case 52:
                        // Go to main page
                        if(outcomeListAdapter.goToMainPage()){
                            openOutcomeListMenu(p, currentPack);
                        }
                        break;
                    case 53:
                        // Go to next page
                        if(outcomeListAdapter.goToNextPage()){
                            openOutcomeListMenu(p, currentPack);
                        }
                        break;
                    default:
                        ItemStack stack = e.getCurrentItem();
                        if(stack == null || stack.getType() == Material.AIR
                                || !NBTEditor.contains(stack, OUTCOME_ID_TAG)){
                            return;
                        }
                        
                        int outcomeID = NBTEditor.getInt(stack, OUTCOME_ID_TAG);
                        Outcome outcome = currentPack.getOutcome(outcomeID);
                        if(editMode){
                            //Edit outcome
                            /*FinishMenu.reset();
                            FinishMenu.setCurrentPack(currentPack);
                            FinishMenu.setCurrentOutcome(outcome.cloneOutcome());
                            GUIManager.reset();
                            GUIManager.outcomeName = outcome.getName();
                            GUIManager.outcomeProb = outcome.getProbability() + "";
                            GUIManager.editMode = true;
                            GUIManager.newOutcome(p);*/
                        }else if(cloneMode){
                            //Clone outcome
                            Outcome clone = outcome.clone();
                            clone.setName(clone.getName() + " (clone)");
                            currentPack.addOutcome(clone, true);
                            openOutcomeListMenu(p, currentPack);
                        }else if(deleteMode){
                            //Delete outcome
                            currentPack.removeOutcome(outcome);
                            currentPack.saveOutcomes();
                            openOutcomeListMenu(p, currentPack);
                        }else{
                            //Edit outcome
                            FinishMenu.reset();
                            FinishMenu.setCurrentPack(currentPack);
                            FinishMenu.setCurrentOutcome(outcome.clone());
                            GUIManager.reset();
                            GUIManager.outcomeName = outcome.getName();
                            GUIManager.outcomeProb = outcome.getProbability();
                            GUIManager.outcomeIcon = outcome.getIcon();
                            GUIManager.editMode = true;
                            GUIManager.newOutcome(p);
                        }
                }
            }
        }
//</editor-fold>
    };
}
