package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import me.i2000c.newalb.custom_outcomes.utils.OutcomePack;
import me.i2000c.newalb.custom_outcomes.utils.PackManager;
import me.i2000c.newalb.custom_outcomes.utils.rewards.TrapReward;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GUIPagesAdapter;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class TrapMenu{
    public static TrapReward reward = null;
    
    private static OutcomePack auxPack = null;
    
    private static final int PACK_LIST_MENU_SIZE = 45;
    private static final int PREVIOUS_PAGE_SLOT = 51;
    private static final int CURRENT_PAGE_SLOT = 52;
    private static final int NEXT_PAGE_SLOT = 53;
    private static GUIPagesAdapter<Outcome> packListAdapter;
    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.TRAP_MENU, TRAP_MENU_FUNCTION);
            InventoryListener.registerInventory(CustomInventoryType.TRAP_TYPE_MENU, TRAP_TYPE_MENU_FUNCTION);
            InventoryListener.registerInventory(CustomInventoryType.TRAP_PACKS_MENU, TRAP_PACKS_MENU_FUNCTION);
            InventoryListener.registerInventory(CustomInventoryType.TRAP_OUTCOMES_MENU, TRAP_OUTCOMES_MENU_FUNCTION);
            
            packListAdapter = new GUIPagesAdapter<>(
                    PACK_LIST_MENU_SIZE,
                    (outcome, index) -> {
                        ItemBuilder builder = ItemBuilder.fromItem(outcome.getItemToDisplay(), false);
            
                        if(reward.getTrapOutcome() != null && reward.getTrapOutcome().equals(outcome)){
                            builder.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                            builder.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        }
                        
                        return builder.build();
                    }
            );
            packListAdapter.setPreviousPageSlot(PREVIOUS_PAGE_SLOT);
            packListAdapter.setCurrentPageSlot(CURRENT_PAGE_SLOT);
            packListAdapter.setNextPageSlot(NEXT_PAGE_SLOT);
            
            inventoriesRegistered = true;
        }
        
        reward = null;
        auxPack = null;
        
        packListAdapter.goToMainPage();
    }
    
    //Trap inventory
    public static void openTrapMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(reward == null){
            reward = new TrapReward(RewardListMenu.getCurrentOutcome());
        }
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.TRAP_MENU, 27, "&5&lTrap Reward");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.PURPLE);
        
        ItemStack trapMaterialItem = ItemBuilder
                .newItem(XMaterial.matchXMaterial(reward.getPressurePlateMaterial()))
                .withDisplayName("&2Selected pressure plate material: &a" + reward.getPressurePlateMaterial().name())
                .addLoreLine("&3Click to change")
                .build();
        
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.NAME_TAG);        
        if(reward.getTrapName() == null){
            builder.withDisplayName("&2Trap name: &cnull");
        }else{
            builder.withDisplayName("&2Trap name: &r" + reward.getTrapName());
        }        
        builder.addLoreLine("&3Click to change");
        ItemStack trapNameItem = builder.build();
        
        Outcome outcome = reward.getTrapOutcome();
        if(outcome == null){
            builder = ItemBuilder.newItem(XMaterial.CHEST);
            builder.withDisplayName("&2Selected trap outcome: &cnull");
        }else{
            builder = ItemBuilder.newItem(XMaterial.matchXMaterial(outcome.getIcon()));
            builder.withDisplayName("&2Selected trap outcome: &a" + reward.getTrapOutcome());
        }
        builder.addLoreLine("&3Click to select");
        ItemStack trapOutcomeItem = builder.build();
        
        for(int i=0;i<=9;i++){
            inv.setItem(i, glass);
        }
        for(int i=17;i<27;i++){
            inv.setItem(i, glass);
        }
        
        inv.setItem(10, GUIItem.getBackItem());
        inv.setItem(16, GUIItem.getNextItem());
        
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
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 10:
                    //Return to the previous menu
                    if(RewardListMenu.editMode){
                        RewardListMenu.openFinishInventory(p);
                    }else{
                        RewardTypesMenu.openRewardTypesMenu(p);
                    }
                    break;
                case 16:
                    //Open next menu
                    if(reward.getTrapName() != null && reward.getTrapOutcome() != null){
                        RewardListMenu.addReward(reward);
                        reset();
                        RewardListMenu.openFinishInventory(p);
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
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.TRAP_TYPE_MENU, 27, "&2&lTrap materials");
                
        inv.setItem(0, GUIItem.getBackItem());
        
        for(int i=0;i<TrapReward.getPressurePlateMaterials().size();i++){
            Material material = TrapReward.getPressurePlateMaterials().get(i);
            ItemBuilder builder = ItemBuilder.newItem(XMaterial.matchXMaterial(material));
            if(material == reward.getPressurePlateMaterial()){                
                builder.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                builder.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            inv.setItem(i+2, builder.build());
        }
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction TRAP_TYPE_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
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
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.TRAP_PACKS_MENU, 54, "&3&lPack list");
        
        inv.setItem(0, GUIItem.getBackItem());
        
        int i=0;
        for(OutcomePack pack : PackManager.getPacks()){
            if(i >= 54){
                break;
            }
            
            ItemBuilder builder = ItemBuilder.fromItem(pack.getItemToDisplay(), false);
            if(reward.getTrapOutcome() != null && reward.getTrapOutcome().getPack().equals(pack)){
                builder.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                builder.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            inv.setItem(++i, builder.build());
        }
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction TRAP_PACKS_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            if(e.getSlot() == 0){
                //Return to previous inventory
                openTrapMenu(p);
            }else if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
                //Open select outcome menu
                String displayName = ItemBuilder.fromItem(e.getCurrentItem(), false)
                        .getDisplayName();
                String packName = Logger.stripColor(displayName);
                auxPack = PackManager.getPack(packName);
                
                packListAdapter.setItemList(auxPack.getSortedOutcomes());
                packListAdapter.goToMainPage();
                openTrapOutcomesMenu(p);
            }
        }            
//</editor-fold>
    };
    
    //Trap outcomes inventory
    private static void openTrapOutcomesMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.TRAP_OUTCOMES_MENU, 54, "&3&lOutcomes list");
        
        inv.setItem(45, GUIItem.getBackItem());
        
        packListAdapter.updateMenu(inv);
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction TRAP_OUTCOMES_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 45:
                    // Return to previous inventory
                    openTrapPacksMenu(p);
                    break;
                case 51:
                    // Go to previous page
                    if(packListAdapter.goToPreviousPage()){
                        openTrapOutcomesMenu(p);
                    }
                    break;
                case 52:
                    // Go to main page
                    if(packListAdapter.goToMainPage()){
                        openTrapOutcomesMenu(p);
                    }
                    break;
                case 53:
                    // Go to next page
                    if(packListAdapter.goToNextPage()){
                        openTrapOutcomesMenu(p);
                    }
                    break;
            default:
                if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
                    //Open trap menu
                    String displayName = ItemBuilder.fromItem(e.getCurrentItem(), false)
                            .getDisplayName();
                    String itemName = Logger.stripColor(displayName);
                    int outcomeID = Integer.parseInt(itemName.split(" ")[1]);
                    Outcome outcome = auxPack.getOutcome(outcomeID);
                    if(outcome != null){
                        reward.setTrapOutcome(outcome);
                        openTrapMenu(p);
                        packListAdapter.goToMainPage();
                    }
                }
            }
        }            
//</editor-fold>
    };
}
