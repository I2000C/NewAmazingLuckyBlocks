package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import java.util.Arrays;
import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import me.i2000c.newalb.custom_outcomes.utils.OutcomePack;
import me.i2000c.newalb.custom_outcomes.utils.PackManager;
import me.i2000c.newalb.custom_outcomes.utils.rewards.TrapReward;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.utils.Logger;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TrapMenu{
    public static TrapReward reward = null;
    
    private static OutcomePack auxPack = null;
    
    private static final int MENU_SIZE = 45;
    private static int index;    
    private static int max_pages;
    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.TRAP_MENU, TRAP_MENU_FUNCTION);
            InventoryListener.registerInventory(CustomInventoryType.TRAP_TYPE_MENU, TRAP_TYPE_MENU_FUNCTION);
            InventoryListener.registerInventory(CustomInventoryType.TRAP_PACKS_MENU, TRAP_PACKS_MENU_FUNCTION);
            InventoryListener.registerInventory(CustomInventoryType.TRAP_OUTCOMES_MENU, TRAP_OUTCOMES_MENU_FUNCTION);
            
            inventoriesRegistered = true;
        }
        
        reward = null;
        auxPack = null;
        
        index = 0;
        max_pages = 0;
    }
    
    //Trap inventory
    public static void openTrapMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(reward == null){
            reward = new TrapReward(FinishMenu.getCurrentOutcome());
        }
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.TRAP_MENU, 27, Logger.color("&5&lTrap Reward"));
        
        ItemStack glass = XMaterial.PURPLE_STAINED_GLASS_PANE.parseItem();
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        
        ItemStack trapMaterialItem = new ItemStack(reward.getPressurePlateMaterial());
        meta = trapMaterialItem.getItemMeta();
        meta.setDisplayName(Logger.color("&2Selected pressure plate material: &a" + reward.getPressurePlateMaterial().name()));
        meta.setLore(Logger.color(Arrays.asList("&3Click to change")));
        trapMaterialItem.setItemMeta(meta);
        
        ItemStack trapNameItem = new ItemStack(Material.NAME_TAG);
        meta = trapNameItem.getItemMeta();
        if(reward.getTrapName() == null){
            meta.setDisplayName(Logger.color("&2Trap name: &cnull"));
        }else{
            meta.setDisplayName(Logger.color("&2Trap name: &r" + reward.getTrapName()));
        }        
        meta.setLore(Logger.color(Arrays.asList("&3Click to change")));
        trapNameItem.setItemMeta(meta);
        
        ItemStack trapOutcomeItem = new ItemStack(Material.CHEST);
        meta = trapOutcomeItem.getItemMeta();
        if(reward.getTrapOutcome() == null){
            meta.setDisplayName(Logger.color("&2Selected trap outcome: &cnull"));
        }else{
            meta.setDisplayName(Logger.color("&2Selected trap outcome: &a" + reward.getTrapOutcome()));
        }
        meta.setLore(Logger.color(Arrays.asList("&3Click to select")));
        trapOutcomeItem.setItemMeta(meta);
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName(Logger.color("&7Back"));
        back.setItemMeta(meta);
        
        ItemStack next = new ItemStack(Material.ANVIL);
        meta = next.getItemMeta();
        meta.setDisplayName(Logger.color("&bNext"));
        next.setItemMeta(meta);
        
        for(int i=0;i<=9;i++){
            inv.setItem(i, glass);
        }
        for(int i=17;i<27;i++){
            inv.setItem(i, glass);
        }
        
        inv.setItem(10, back);
        inv.setItem(16, next);
        
        inv.setItem(12, trapMaterialItem);
        inv.setItem(13, trapNameItem);
        inv.setItem(14 ,trapOutcomeItem);
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction TRAP_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            switch(e.getSlot()){
                case 10:
                    //Return to the previous menu
                    if(FinishMenu.editMode){
                        FinishMenu.openFinishInventory(p);
                    }else{
                        RewardTypesMenu.openRewardTypesMenu(p);
                    }
                    break;
                case 16:
                    //Open next menu
                    if(reward.getTrapName() != null && reward.getTrapOutcome() != null){
                        FinishMenu.addReward(reward);
                        reset();
                        FinishMenu.openFinishInventory(p);
                    }
                    break;
                case 12:
                    //Open trap type menu
                    openTrapTypeMenu(p);
                    break;
                case 13:
                    //Close inventory
                    ChatListener.registerPlayer(p, message -> {
                        reward.setTrapName(message);
                        openTrapMenu(p);
                    });
                    p.closeInventory();
                    Logger.sendMessage("&3Enter the trap name in the chat and then press ENTER", p);
                    break;
                case 14:
                    //Open trap packs menu
                    openTrapPacksMenu(p);
                    break;
            }
        }
//</editor-fold>
    };
    
    //Trap type inventory
    private static void openTrapTypeMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.TRAP_TYPE_MENU, 27, Logger.color("&2&lTrap materials"));
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = back.getItemMeta();
        meta.setDisplayName(Logger.color("&7Back"));
        back.setItemMeta(meta);
        
        inv.setItem(0, back);
        
        for(int i=0;i<TrapReward.getPressurePlateMaterials().size();i++){
            Material material = TrapReward.getPressurePlateMaterials().get(i);
            ItemStack stack = new ItemStack(material);
            if(material == reward.getPressurePlateMaterial()){
                stack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
                meta = stack.getItemMeta();
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                stack.setItemMeta(meta);
            }
            inv.setItem(i+2, stack);
        }
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction TRAP_TYPE_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            if(e.getSlot() == 0){
                //Return to previous inventory
                openTrapMenu(p);
            }else if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
                //Open trap inventory
                reward.setPressurePlateMaterial(e.getCurrentItem().getType());
                openTrapMenu(p);
            }
        }
//</editor-fold>
    };
    
    //Trap packs inventory
    private static void openTrapPacksMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.TRAP_PACKS_MENU, 54, Logger.color("&3&lPack list"));
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = back.getItemMeta();
        meta.setDisplayName(Logger.color("&7Back"));
        back.setItemMeta(meta);
        
        inv.setItem(0, back);
        
        int i=0;
        for(OutcomePack pack : PackManager.getPacks()){
            if(i >= 54){
                break;
            }
            
            ItemStack packItem = pack.getItemToDisplay();
            if(reward.getTrapOutcome() != null && reward.getTrapOutcome().getPack().equals(pack)){
                packItem.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
                meta = packItem.getItemMeta();
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                packItem.setItemMeta(meta);
            }
            inv.setItem(++i, packItem);
        }
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction TRAP_PACKS_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            if(e.getSlot() == 0){
                //Return to previous inventory
                openTrapMenu(p);
            }else if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
                //Open select outcome menu
                String packName = Logger.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                auxPack = PackManager.getPack(packName);
                if(auxPack != null){
                    index = 0;
                    if(auxPack.getOutcomes().size() % MENU_SIZE == 0){
                        max_pages = (auxPack.getOutcomes().size() / MENU_SIZE);
                    }else{
                        max_pages = (auxPack.getOutcomes().size() / MENU_SIZE) + 1;
                    }
                    openTrapOutcomesMenu(p);
                }
            }
        }            
//</editor-fold>
    };
    
    //Trap outcomes inventory
    private static void openTrapOutcomesMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.TRAP_OUTCOMES_MENU, 54, Logger.color("&3&lOutcomes list"));
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = back.getItemMeta();
        meta.setDisplayName(Logger.color("&7Back"));
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
        
        inv.setItem(45, back);
        inv.setItem(51, previousPage);
        inv.setItem(52, currentPage);
        inv.setItem(53, nextPage);
        
        int n = Integer.min((auxPack.getOutcomes().size() - MENU_SIZE*index), MENU_SIZE);
        for(int i=0;i<n;i++){
            Outcome outcome = auxPack.getOutcome(i + index*MENU_SIZE);
            ItemStack outcomeItem = outcome.getItemToDisplay();
            
            if(reward.getTrapOutcome() != null && reward.getTrapOutcome().equals(outcome)){
                outcomeItem.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
                meta = outcomeItem.getItemMeta();
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                outcomeItem.setItemMeta(meta);
            }
            inv.setItem(i, outcomeItem);
        }
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction TRAP_OUTCOMES_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            switch(e.getSlot()){
                case 45:
                    //Return to previous inventory
                    openTrapPacksMenu(p);
                    break;
                case 51:
                    //Previous page
                    if(max_pages > 1){
                        index--;
                        if(index < 0){
                            index = max_pages - 1;
                        }
                        openTrapOutcomesMenu(p);
                    }
                    break;
                case 52:
                    //Go to home page
                    if(max_pages > 1){
                        index = 0;
                        openTrapOutcomesMenu(p);
                    }
                    break;
                case 53:
                    //Next page
                    if(max_pages > 1){
                        index++;
                        if(index >= max_pages){
                            index = 0;
                        }
                        openTrapOutcomesMenu(p);
                    }
                    break;
            default:
                if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
                    //Open trap menu
                    String itemName = Logger.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                    int outcomeID = Integer.parseInt(itemName.split(" ")[1]);
                    Outcome outcome = auxPack.getOutcome(outcomeID);
                    if(outcome != null){
                        reward.setTrapOutcome(outcome);
                        openTrapMenu(p);
                    }
                }
            }
        }            
//</editor-fold>
    };
}
