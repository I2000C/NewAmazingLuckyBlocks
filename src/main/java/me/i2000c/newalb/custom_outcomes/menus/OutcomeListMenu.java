package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import me.i2000c.newalb.custom_outcomes.utils.OutcomePack;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.utils.Logger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = back.getItemMeta();
        meta.setDisplayName(Logger.color("&2Back"));
        back.setItemMeta(meta);
        
        ItemStack currentPage = new ItemStack(Material.BOOK, (index+1));
        meta = currentPage.getItemMeta();
        meta.setDisplayName(Logger.color("&6Page &3" + (index+1) + " &a/ &3" + max_pages));
        currentPage.setItemMeta(meta);
        
        ItemStack previousPage = XMaterial.ENDER_EYE.parseItem();
        meta = previousPage.getItemMeta();
        meta.setDisplayName(Logger.color("&2Previous page"));
        previousPage.setItemMeta(meta);
        
        ItemStack nextPage = new ItemStack(Material.MAGMA_CREAM);
        meta = nextPage.getItemMeta();
        meta.setDisplayName(Logger.color("&2Next page"));
        nextPage.setItemMeta(meta);
        
        ItemStack createOutcome = new ItemStack(Material.SLIME_BALL);
        meta = createOutcome.getItemMeta();
        meta.setDisplayName(Logger.color("&aCreate new outcome"));
        createOutcome.setItemMeta(meta);
        
        ItemStack editOutcome = new ItemStack(Material.ANVIL);
        meta = editOutcome.getItemMeta();
        meta.setDisplayName(Logger.color("&6Edit outcomes"));
        if(editMode){
            meta.setLore(Arrays.asList(Logger.color("&dEdit mode: &aenabled")));
        }else{
            meta.setLore(Arrays.asList(Logger.color("&dEdit mode: &7disabled")));
        }
        editOutcome.setItemMeta(meta);
        
        ItemStack cloneOutcome = XMaterial.REPEATER.parseItem();
        meta = cloneOutcome.getItemMeta();
        meta.setDisplayName(Logger.color("&3Clone outcomes"));
        if(cloneMode){
            meta.setLore(Arrays.asList(Logger.color("&dClone mode: &aenabled")));
        }else{
            meta.setLore(Arrays.asList(Logger.color("&dClone mode: &7disabled")));
        }
        cloneOutcome.setItemMeta(meta);
        
        ItemStack deleteOutcome = new ItemStack(Material.BARRIER);
        meta = deleteOutcome.getItemMeta();
        meta.setDisplayName(Logger.color("&cDelete outcomes"));
        List<String> loreList = new ArrayList();
        if(deleteMode){
            loreList.add(Logger.color("&4&lWARNING: &cIf this mode is enabled,"));
            loreList.add(Logger.color("&cwhen you click on an outcome,"));
            loreList.add(Logger.color("&cit will be deleted permanently"));
            loreList.add("");
            loreList.add(Logger.color("&dDelete mode: &aenabled"));
        }else{
            loreList.add(Logger.color("&dDelete mode: &7disabled"));
        }
        meta.setLore(loreList);
        deleteOutcome.setItemMeta(meta);
        
        inv.setItem(45, back);
        
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
        
        inv.setItem(51, previousPage);
        inv.setItem(52, currentPage);
        inv.setItem(53, nextPage);
        
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
