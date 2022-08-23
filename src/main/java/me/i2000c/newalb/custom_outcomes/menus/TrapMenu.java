package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.OutcomePack;
import me.i2000c.newalb.custom_outcomes.rewards.PackManager;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.TrapReward;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GUIPagesAdapter;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class TrapMenu extends Editor<TrapReward>{
    public TrapMenu(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        InventoryListener.registerInventory(CustomInventoryType.TRAP_MENU, TRAP_MENU_FUNCTION);
        InventoryListener.registerInventory(CustomInventoryType.TRAP_TYPE_MENU, TRAP_TYPE_MENU_FUNCTION);
        InventoryListener.registerInventory(CustomInventoryType.TRAP_PACKS_MENU, TRAP_PACKS_MENU_FUNCTION);
        InventoryListener.registerInventory(CustomInventoryType.TRAP_OUTCOMES_MENU, TRAP_OUTCOMES_MENU_FUNCTION);
        
        packListAdapter = new GUIPagesAdapter<>(
                PACK_LIST_MENU_SIZE,
                (outcome, index) -> {
                    ItemBuilder builder = ItemBuilder.fromItem(outcome.getItemToDisplay(), false);
                    
                    if(item.getTrapOutcome() != null && item.getTrapOutcome().equals(outcome)){
                        builder.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                        builder.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    }
                    
                    return builder.build();
                }
        );
        packListAdapter.setPreviousPageSlot(PREVIOUS_PAGE_SLOT);
        packListAdapter.setCurrentPageSlot(CURRENT_PAGE_SLOT);
        packListAdapter.setNextPageSlot(NEXT_PAGE_SLOT);
//</editor-fold>
    }
    
    private OutcomePack auxPack = null;
    
    private static final int PACK_LIST_MENU_SIZE = 45;
    private static final int PREVIOUS_PAGE_SLOT = 51;
    private static final int CURRENT_PAGE_SLOT = 52;
    private static final int NEXT_PAGE_SLOT = 53;
    private static GUIPagesAdapter<Outcome> packListAdapter;
    
    @Override
    protected void reset(){
        this.auxPack = null;
    }
    
    @Override
    protected void newItem(Player player){
        Outcome outcome = RewardListMenu.getCurrentOutcome();
        item = new TrapReward(outcome);
        openTrapMenu(player);
    }
    
    @Override
    protected void editItem(Player player){
        openTrapMenu(player);
    }
    
    //Trap inventory
    private void openTrapMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.TRAP_MENU, 27, "&5&lTrap Reward");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.PURPLE);
        
        ItemStack trapMaterialItem = ItemBuilder
                .newItem(XMaterial.matchXMaterial(item.getPressurePlateMaterial()))
                .withDisplayName("&2Selected pressure plate material: &a" + item.getPressurePlateMaterial().name())
                .addLoreLine("&3Click to change")
                .build();
        
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.NAME_TAG);        
        if(item.getTrapName() == null){
            builder.withDisplayName("&2Trap name: &cnull");
        }else{
            builder.withDisplayName("&2Trap name: &r" + item.getTrapName());
        }        
        builder.addLoreLine("&3Click to change");
        ItemStack trapNameItem = builder.build();
        
        Outcome outcome = item.getTrapOutcome();
        if(outcome == null){
            builder = ItemBuilder.newItem(XMaterial.CHEST);
            builder.withDisplayName("&2Selected trap outcome: &cnull");
        }else{
            builder = ItemBuilder.newItem(XMaterial.matchXMaterial(outcome.getIcon()));
            builder.withDisplayName("&2Selected trap outcome: &a" + item.getTrapOutcome());
        }
        builder.addLoreLine("&3Click to select");
        ItemStack trapOutcomeItem = builder.build();
        
        for(int i=0;i<=9;i++){
            menu.setItem(i, glass);
        }
        for(int i=17;i<27;i++){
            menu.setItem(i, glass);
        }
        
        menu.setItem(10, GUIItem.getBackItem());
        menu.setItem(16, GUIItem.getNextItem());
        
        menu.setItem(12, trapMaterialItem);
        menu.setItem(13, trapNameItem);
        menu.setItem(14 ,trapOutcomeItem);
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction TRAP_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 10:
                    //Return to the previous menu
                    onBack.accept(player);
                    break;
                case 16:
                    //Open next menu
                    if(item.getTrapName() != null && item.getTrapOutcome() != null){
                        onNext.accept(player, item);
                    }
                    break;
                case 12:
                    //Open trap type menu
                    openTrapTypeMenu(player);
                    break;
                case 13:
                    //Close inventory
                    ChatListener.registerPlayer(player, message -> {
                        item.setTrapName(message);
                        openTrapMenu(player);
                    });
                    player.closeInventory();
                    Logger.sendMessage("&3Enter the trap name in the chat and then press ENTER", player);
                    break;
                case 14:
                    //Open trap packs menu
                    openTrapPacksMenu(player);
                    break;
            }
        }
//</editor-fold>
    };
    
    //Trap type inventory
    private void openTrapTypeMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.TRAP_TYPE_MENU, 27, "&2&lTrap materials");
                
        menu.setItem(0, GUIItem.getBackItem());
        
        for(int i=0;i<TrapReward.getPressurePlateMaterials().size();i++){
            Material material = TrapReward.getPressurePlateMaterials().get(i);
            ItemBuilder builder = ItemBuilder.newItem(XMaterial.matchXMaterial(material));
            if(material == item.getPressurePlateMaterial()){                
                builder.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                builder.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            menu.setItem(i+2, builder.build());
        }
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction TRAP_TYPE_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            if(e.getSlot() == 0){
                //Return to previous inventory
                openTrapMenu(player);
            }else if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
                //Open trap inventory
                item.setPressurePlateMaterial(e.getCurrentItem().getType());
                openTrapMenu(player);
            }
        }
//</editor-fold>
    };
    
    //Trap packs inventory
    private void openTrapPacksMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.TRAP_PACKS_MENU, 54, "&3&lPack list");
        
        menu.setItem(0, GUIItem.getBackItem());
        
        int i=0;
        for(OutcomePack pack : PackManager.getPacks()){
            if(i >= 54){
                break;
            }
            
            ItemBuilder builder = ItemBuilder.fromItem(pack.getItemToDisplay(), false);
            if(item.getTrapOutcome() != null && item.getTrapOutcome().getPack().equals(pack)){
                builder.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                builder.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            menu.setItem(++i, builder.build());
        }
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction TRAP_PACKS_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            if(e.getSlot() == 0){
                //Return to previous inventory
                openTrapMenu(player);
            }else if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
                //Open select outcome menu
                String displayName = ItemBuilder.fromItem(e.getCurrentItem(), false)
                        .getDisplayName();
                String packName = Logger.stripColor(displayName);
                auxPack = PackManager.getPack(packName);
                
                packListAdapter.setItemList(auxPack.getSortedOutcomes());
                packListAdapter.goToMainPage();
                openTrapOutcomesMenu(player);
            }
        }            
//</editor-fold>
    };
    
    //Trap outcomes inventory
    private void openTrapOutcomesMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.TRAP_OUTCOMES_MENU, 54, "&3&lOutcomes list");
        
        menu.setItem(45, GUIItem.getBackItem());
        
        packListAdapter.updateMenu(menu);        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction TRAP_OUTCOMES_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 45:
                    // Return to previous inventory
                    openTrapPacksMenu(player);
                    break;
                case 51:
                    // Go to previous page
                    if(packListAdapter.goToPreviousPage()){
                        openTrapOutcomesMenu(player);
                    }
                    break;
                case 52:
                    // Go to main page
                    if(packListAdapter.goToMainPage()){
                        openTrapOutcomesMenu(player);
                    }
                    break;
                case 53:
                    // Go to next page
                    if(packListAdapter.goToNextPage()){
                        openTrapOutcomesMenu(player);
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
                        item.setTrapOutcome(outcome);
                        openTrapMenu(player);
                        packListAdapter.goToMainPage();
                    }
                }
            }
        }            
//</editor-fold>
    };
}
