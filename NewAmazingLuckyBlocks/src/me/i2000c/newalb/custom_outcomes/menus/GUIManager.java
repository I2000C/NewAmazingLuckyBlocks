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
import me.i2000c.newalb.utils.Logger;
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
    
    protected static String outcomeName = "&7Write name";
    protected static String outcomeProb = "&6Select probability";
    protected static ItemStack outcomeIcon;
    
    protected static boolean editMode = false;
    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.NEW_OUTCOME_MENU, NEW_OUTCOME_MENU_FUNCTION);
            
            inventoriesRegistered = true;
        }
        
        outcomeName = "&7Write name";
        outcomeProb = "&6Select probability";
        outcomeIcon = Outcome.getDefaultIcon();
        
        editMode = false;
    }    
    
    static void newOutcome(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory inv;
        if(editMode){
            inv = GUIFactory.createInventory(CustomInventoryType.NEW_OUTCOME_MENU, 27, "&e&lEdit outcome");
        }else{
            inv = GUIFactory.createInventory(CustomInventoryType.NEW_OUTCOME_MENU, 27, "&a&lCreate new outcome");
        }
        
        ItemStack glass = XMaterial.CYAN_STAINED_GLASS_PANE.parseItem();
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        
        ItemStack name = XMaterial.OAK_SIGN.parseItem();
        meta = name.getItemMeta();
        if(outcomeName.equals("&7Write name")){
            meta.setDisplayName(Logger.color(outcomeName));
        }else{
            meta.setDisplayName(Logger.color("&7Outcome name: &r" + outcomeName));
        }
        name.setItemMeta(meta);
        
        ItemStack icon = outcomeIcon.clone();
        meta = icon.getItemMeta();
        meta.setDisplayName(Logger.color("&dOutcome icon"));
        meta.setLore(Logger.color(Arrays.asList("&bClick on an item of your inventory", "&b   to select the icon of the outcome.", "&bBy default it's CHEST")));
        icon.setItemMeta(meta);
        
        ItemStack prob = new ItemStack(Material.GLOWSTONE_DUST);
        meta = prob.getItemMeta();
        if(outcomeProb.equals("&6Select probability") || outcomeProb.equals("&cProbability must be a positive integer")){
            meta.setDisplayName(Logger.color(outcomeProb));
        }else{
            meta.setDisplayName(Logger.color("&6Outcome probability: &r" + outcomeProb));
        }
        prob.setItemMeta(meta);
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName(Logger.color("&2Back"));
        back.setItemMeta(meta);
        
        ItemStack next = new ItemStack(Material.ANVIL);
        meta = next.getItemMeta();
        meta.setDisplayName(Logger.color("&bNext"));
        next.setItemMeta(meta);
        
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
                            if(Integer.parseInt(message) > 0){
                                outcomeProb = message;
                            }else{
                                outcomeProb = "&cProbability must be a positive integer";
                            }
                        }catch(NumberFormatException ex){
                            outcomeProb = "&cProbability must be a positive integer";
                        }
                        newOutcome(p);
                    });
                    p.closeInventory();
                    break;
                case 16:
                    if(!outcomeName.equals("&7Write name") && !(outcomeProb.equals("&6Select probability") || outcomeProb.equals("&cProbability must be a positive integer"))){
                        //Open next inventory
                        if(editMode){
                            editMode = false;
                            FinishMenu.getCurrentOutcome().setName(outcomeName);
                            FinishMenu.getCurrentOutcome().setProbability(Integer.parseInt(outcomeProb));
                        }else{
                            if(FinishMenu.getCurrentOutcome() == null){
                                FinishMenu.setCurrentOutcome(new Outcome(outcomeName, Integer.parseInt(outcomeProb), -1, FinishMenu.getCurrentPack()));
                            }else{
                                FinishMenu.getCurrentOutcome().setName(outcomeName);
                                FinishMenu.getCurrentOutcome().setProbability(Integer.parseInt(outcomeProb));
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
