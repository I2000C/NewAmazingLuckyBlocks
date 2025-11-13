package me.i2000c.newalb.lucky_blocks.editors.menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.functions.InventoryFunction;
import me.i2000c.newalb.api.gui.CustomInventoryType;
import me.i2000c.newalb.api.gui.GUIFactory;
import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.GUIPagesAdapter;
import me.i2000c.newalb.api.gui.InventoryLocation;
import me.i2000c.newalb.api.gui.Menu;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.lucky_blocks.editors.Editor;
import me.i2000c.newalb.lucky_blocks.editors.EditorType;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.OutcomePack;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.misc.NBTUtils;

public class OutcomeListMenu extends Editor<OutcomePack>{
    public OutcomeListMenu(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        InventoryListener.registerInventory(CustomInventoryType.OUTCOME_LIST_MENU, OUTCOME_LIST_MENU_FUNCTION);
        
        outcomeListAdapter = new GUIPagesAdapter<>(
                OUTCOME_LIST_MENU_SIZE,
                (outcome, index) -> {
                    ItemStack stack = outcome.getItemToDisplay();
                    NBTUtils.set(stack, OUTCOME_ID_TAG, index);
                    return stack;
                }
        );
        outcomeListAdapter.setPreviousPageSlot(PREVIOUS_PAGE_SLOT);
        outcomeListAdapter.setCurrentPageSlot(CURRENT_PAGE_SLOT);
        outcomeListAdapter.setNextPageSlot(NEXT_PAGE_SLOT);
//</editor-fold>
    }
    
    public static OutcomePack getCurrentPack(){
        Editor<OutcomePack> editor = EditorType.OUTCOME_LIST.getEditor();
        return ((OutcomeListMenu) editor).item;
    }
    
    private boolean cloneMode;
    private boolean deleteMode;
    
    private static final int OUTCOME_LIST_MENU_SIZE = 45;
    private static final int PREVIOUS_PAGE_SLOT = 51;    
    private static final int CURRENT_PAGE_SLOT = 52;    
    private static final int NEXT_PAGE_SLOT = 53;    
    private static final String OUTCOME_ID_TAG = "outcome_id";
    private static GUIPagesAdapter<Outcome> outcomeListAdapter;
    
    @Override
    protected void reset(){
        cloneMode = false;
        deleteMode = false;
        outcomeListAdapter.goToMainPage();
    }
    
    @Override
    protected void newItem(Player player){
        
    }
    
    @Override
    protected void editItem(Player player){
        openOutcomeListMenu(player);
    }
    
    private void openOutcomeListMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        outcomeListAdapter.setItemList(item.getSortedOutcomes());
        
        Menu menu = GUIFactory.newMenu(CustomInventoryType.OUTCOME_LIST_MENU, 54, "&3&lOutcomes list");
        
        outcomeListAdapter.updateMenu(menu);
        
        ItemStack createOutcome = ItemStackWrapper.newItem(XMaterial.SLIME_BALL)
                                                  .setDisplayName("&aCreate new outcome")
                                                  .toItemStack();
        
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
        ItemStackWrapper.fromItem(deleteOutcome, false)
                .addLoreLine("")
                .addLoreLine("&4&lWARNING: &cIf this mode is enabled,")
                .addLoreLine("&cwhen you click on an outcome,")
                .addLoreLine("&cit will be deleted permanently");
        
        menu.setItem(45, GUIItem.getBackItem());
        
        if(!cloneMode && !deleteMode){
            menu.setItem(46, createOutcome);
        }
        if(!deleteMode){
            menu.setItem(47, cloneOutcome);
        }
        if(!cloneMode){
            menu.setItem(48, deleteOutcome);
        }
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction OUTCOME_LIST_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
                switch(e.getSlot()){
                    case 45:
                        // Go to previous menu
                        onBack.accept(player);
                        break;
                    case 46:
                        //Create outcome
                        if(!cloneMode && !deleteMode){
                            Editor<Outcome> editor = EditorType.OUTCOME.getEditor();
                            editor.createNewItem(
                                    player, 
                                    p -> openOutcomeListMenu(p), 
                                    (p, outcome) -> {
                                        try{
                                            outcome.saveOutcome();
                                            Logger.logAndMessage("&aOutcome saved with ID &b" + outcome.getID(), p);
                                            Logger.log("&6Reloading pack " + item.getPackname()+ "...");
                                            item.loadPack();
                                            Logger.log("&aPack reloaded");
                                            openOutcomeListMenu(p);
                                        }catch(Exception ex){
                                            Logger.logAndMessage("&cError while saving outcome with ID &b" + outcome.getID(), p);
                                            ex.printStackTrace();
                                        }
                                    });
                        }
                        break;
                    case 47:
                        //Toggle clone outcome mode
                        if(!deleteMode){
                            cloneMode = !cloneMode;
                            openOutcomeListMenu(player);
                        }
                        break;
                    case 48:
                        //Toggle delete outcome mode
                        if(!cloneMode){
                            deleteMode = !deleteMode;
                            openOutcomeListMenu(player);
                        }
                        break;
                    case 51:
                        // Go to previous page
                        if(outcomeListAdapter.goToPreviousPage()){
                            openOutcomeListMenu(player);
                        }
                        break;
                    case 52:
                        // Go to main page
                        if(outcomeListAdapter.goToMainPage()){
                            openOutcomeListMenu(player);
                        }
                        break;
                    case 53:
                        // Go to next page
                        if(outcomeListAdapter.goToNextPage()){
                            openOutcomeListMenu(player);
                        }
                        break;
                    default:
                        ItemStack stack = e.getCurrentItem();
                        if(stack == null || stack.getType() == Material.AIR
                                || !NBTUtils.contains(stack, OUTCOME_ID_TAG)){
                            return;
                        }
                        
                        int outcomeID = NBTUtils.getInt(stack, OUTCOME_ID_TAG);
                        Outcome outcome = item.getOutcome(outcomeID);
                        if(cloneMode){
                            //Clone outcome
                            Outcome clone = outcome.clone();
                            clone.setName(clone.getName() + " (clone)");
                            item.addOutcome(clone, true);
                            openOutcomeListMenu(player);
                        }else if(deleteMode){
                            //Delete outcome
                            item.removeOutcome(outcome);
                            item.saveOutcomes();
                            openOutcomeListMenu(player);
                        }else{
                            //Edit outcome
                            Editor<Outcome> editor = EditorType.OUTCOME.getEditor();
                            editor.editExistingItem(
                                    outcome.clone(), 
                                    player, 
                                    p -> openOutcomeListMenu(p), 
                                    (p, _outcome) -> {
                                        try{
                                            _outcome.saveOutcome();
                                            Logger.logAndMessage("&aOutcome saved with ID &b" + _outcome.getID(), p);
                                            Logger.log("&6Reloading pack " + item.getPackname()+ "...");
                                            item.loadPack();
                                            Logger.log("&aPack reloaded");
                                            openOutcomeListMenu(p);
                                        }catch(Exception ex){
                                            Logger.logAndMessage("&cError while saving outcome with ID &b" + outcome.getID(), p);
                                            ex.printStackTrace();
                                        }
                                    });
                        }
                }
            }
        }
//</editor-fold>
    };
}
