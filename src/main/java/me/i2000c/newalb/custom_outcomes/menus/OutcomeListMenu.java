package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import me.i2000c.newalb.custom_outcomes.utils.OutcomePack;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class OutcomeListMenu{
    private static OutcomePack currentPack;
    
    private static final int MENU_SIZE = 45;
    private static int index;    
    private static int max_pages;
    
    private static boolean editMode;
    private static boolean cloneMode;
    private static boolean deleteMode;
    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.OUTCOME_LIST_MENU, OUTCOME_LIST_MENU_FUNCTION);
            
            inventoriesRegistered = true;
        }
        
        currentPack = null;
        
        index = 0;
        max_pages = 0;
        
        editMode = false;
        cloneMode = false;
        deleteMode = false;
    }
    
    public static void openOutcomeListMenu(Player p, OutcomePack pack){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(currentPack == null){
            currentPack = pack;
            
            int numberOfOutcomes = pack.getOutcomes().size();
            if(numberOfOutcomes > 0 && numberOfOutcomes % MENU_SIZE == 0){
                max_pages = numberOfOutcomes / MENU_SIZE;
            }else{
                max_pages = numberOfOutcomes / MENU_SIZE + 1;
            }
        }
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.OUTCOME_LIST_MENU, 54, "&3&lOutcomes list");
        
        int n = Integer.min((currentPack.getOutcomes().size() - MENU_SIZE*index), MENU_SIZE);
        for(int i=0;i<n;i++){
            inv.setItem(i, currentPack.getOutcome(i + index*MENU_SIZE).getItemToDisplay());
        }
        
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
        
        inv.setItem(51, GUIItem.getPreviousPageItem());
        inv.setItem(52, GUIItem.getCurrentPageItem(index+1, max_pages));
        inv.setItem(53, GUIItem.getNextPageItem());
        
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
                        //Previous page
                        if(max_pages > 1){
                            index--;
                            if(index < 0){
                                index = max_pages - 1;
                            }
                            openOutcomeListMenu(p, currentPack);
                        }
                        break;
                    case 52:
                        //Go to home page
                        if(max_pages > 1){
                            index = 0;
                            openOutcomeListMenu(p, currentPack);
                        }
                        break;
                    case 53:
                        //Next page
                        if(max_pages > 1){
                            index++;
                            if(index >= max_pages){
                                index = 0;
                            }
                            openOutcomeListMenu(p, currentPack);
                        }
                        break;
                    default:
                        Outcome outcome = currentPack.getOutcome(e.getSlot() + index*MENU_SIZE);
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
                            Outcome clone = outcome.cloneOutcome();
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
                            FinishMenu.setCurrentOutcome(outcome.cloneOutcome());
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
