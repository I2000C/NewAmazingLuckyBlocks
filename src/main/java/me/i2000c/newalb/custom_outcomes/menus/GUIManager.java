package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import java.util.Arrays;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import me.i2000c.newalb.CommandManager;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;

public class GUIManager{
    private static Inventory currentINV = null;
    
    public static void setCurrentInventory(Inventory inv){
        currentINV = inv;        
        CommandManager.confirmMenu = (inv != null);
    }
    public static Inventory getCurrentInventory(){
        return currentINV;
    }
    
    protected static String outcomeName;
    protected static int outcomeProb;
    protected static ItemStack outcomeIcon;
    
    protected static boolean editMode = false;
    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.NEW_OUTCOME_MENU, NEW_OUTCOME_MENU_FUNCTION);
            
            inventoriesRegistered = true;
        }
        
        outcomeName = "New outcome " + FinishMenu.getCurrentPack().getOutcomes().size();
        outcomeProb = 100;
        outcomeIcon = Outcome.getDefaultIcon();
        
        editMode = false;
    }    
    
    static void newOutcome(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String inventoryName;
        if(editMode){
            inventoryName = "&e&lEdit outcome";
        }else{
            inventoryName = "&a&lCreate new outcome";
        }
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.NEW_OUTCOME_MENU, 27, inventoryName);
        
        ItemStack glass = ItemBuilder.newItem(XMaterial.CYAN_STAINED_GLASS_PANE)
                .withDisplayName(" ").build();
        
        ItemStack name = ItemBuilder.newItem(XMaterial.OAK_SIGN)
                .withDisplayName("&7Outcome name: &r" + outcomeName)
                .withLore("&3Click to change")
                .build();
        
        ItemStack icon = ItemBuilder.fromItem(outcomeIcon)
                .withDisplayName("&dOutcome icon")
                .withLore("&bClick on an item of your inventory",
                          "&b   to select the icon of the outcome.",
                          "&bBy default it's CHEST"
                ).build();
        
        
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.GLOWSTONE_DUST);
        if(outcomeProb < 0){
            builder.withDisplayName("&cProbability must be a positive integer or 0");
        }else{
            builder.withDisplayName("&6Outcome probability: &r" + outcomeProb);
        }
        builder.withLore("&3Click to change");
        ItemStack prob = builder.build();
        
        ItemStack back = ItemBuilder.newItem(XMaterial.ENDER_PEARL)
                .withDisplayName("&2Back")
                .build();
        
        ItemStack next = ItemBuilder.newItem(XMaterial.ANVIL)
                .withDisplayName("&bNext")
                .build();
        
        for(int i=0;i<9;i++){
            inv.setItem(i, glass);
        }
        for(int i=18;i<27;i++){
            inv.setItem(i, glass);
        }
        inv.setItem(9, glass);
        inv.setItem(17, glass);
        
        inv.setItem(10, back);
        
        inv.setItem(12, name);
        inv.setItem(13, icon);
        inv.setItem(14, prob);
        inv.setItem(16, next);
        
        GUIManager.currentINV = inv;
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction NEW_OUTCOME_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            switch(e.getSlot()){
                case 10:
                    //Back
                    reset();
                    OutcomeListMenu.openOutcomeListMenu(p, FinishMenu.getCurrentPack());
                    FinishMenu.reset();
                    break;
                case 12:
                    ChatListener.registerPlayer(p, message -> {
                        outcomeName = message;
                        newOutcome(p);
                    });
                    p.closeInventory();
                    break;
                case 14:
                    ChatListener.registerPlayer(p, message -> {
                        try{
                            int prob = Integer.parseInt(message);
                            if(prob >= 0){
                                outcomeProb = prob;
                            }else{
                                outcomeProb = -1;
                            }
                        }catch(NumberFormatException ex){
                            outcomeProb = -2;
                        }
                        newOutcome(p);
                    });
                    p.closeInventory();
                    break;
                case 16:
                    if(outcomeProb >= 0){
                        //Open next inventory
                        if(editMode){
                            editMode = false;
                            FinishMenu.getCurrentOutcome().setName(outcomeName);
                            FinishMenu.getCurrentOutcome().setProbability(outcomeProb);
                        }else{
                            if(FinishMenu.getCurrentOutcome() == null){
                                FinishMenu.setCurrentOutcome(new Outcome(outcomeName, outcomeProb, -1, FinishMenu.getCurrentPack()));
                            }else{
                                FinishMenu.getCurrentOutcome().setName(outcomeName);
                                FinishMenu.getCurrentOutcome().setProbability(outcomeProb);
                            }
                        }
                        FinishMenu.getCurrentOutcome().setIcon(outcomeIcon);
                        FinishMenu.openFinishInventory(p);
                        reset();
                    }
                    break;
            }
        }else{
            if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
                outcomeIcon = new ItemStack(e.getCurrentItem().getType());
                if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                    outcomeIcon.setDurability(e.getCurrentItem().getDurability());
                }
                newOutcome(p);
            }
        }
//</editor-fold>
    };
}
