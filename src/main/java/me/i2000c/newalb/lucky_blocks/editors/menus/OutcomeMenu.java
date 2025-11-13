package me.i2000c.newalb.lucky_blocks.editors.menus;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.functions.InventoryFunction;
import me.i2000c.newalb.api.gui.CustomInventoryType;
import me.i2000c.newalb.api.gui.GUIFactory;
import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.InventoryLocation;
import me.i2000c.newalb.api.gui.Menu;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.lucky_blocks.editors.Editor;
import me.i2000c.newalb.lucky_blocks.editors.EditorType;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OutcomeMenu extends Editor<Outcome>{
    public OutcomeMenu(){
        InventoryListener.registerInventory(CustomInventoryType.NEW_OUTCOME_MENU, NEW_OUTCOME_MENU_FUNCTION);
    }
    
    private boolean editMode;
    
    private static String getDefaultOutcomeName(){
        return "New outcome " + OutcomeListMenu.getCurrentPack().getOutcomes().size();
    }    
    private static int getDefaultOutcomeProbability(){
        return 100;
    }
    
    @Override
    public void newItem(Player player){
        editMode = false;
        item = new Outcome(
                getDefaultOutcomeName(), 
                getDefaultOutcomeProbability(), 
                -1,
                OutcomeListMenu.getCurrentPack());
        openOutcomeMenu(player);
    }
    
    @Override
    public void editItem(Player player){
        editMode = true;
        openOutcomeMenu(player);
    }
    
    private void openOutcomeMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String inventoryName;
        if(editMode){
            inventoryName = "&e&lEdit outcome";
        }else{
            inventoryName = "&a&lCreate new outcome";
        }
        Menu menu = GUIFactory.newMenu(CustomInventoryType.NEW_OUTCOME_MENU, 27, inventoryName);
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.CYAN);
        for(int i=0;i<9;i++){
            menu.setItem(i, glass);
        }
        for(int i=18;i<27;i++){
            menu.setItem(i, glass);
        }
        menu.setItem(9, glass);
        menu.setItem(17, glass);
        
        ItemStack name = ItemStackWrapper.newItem(XMaterial.OAK_SIGN)
                                         .setDisplayName("&7Outcome name: &r" + item.getName())
                                         .setLore("&3Click to change")
                                         .toItemStack();
        
        ItemStack icon = ItemStackWrapper.fromItem(item.getIcon())
                                         .setDisplayName("&dOutcome icon")
                                         .setLore("&bClick on an item of your inventory",
                                                   "&b   to select the icon of the outcome.",
                                                   "&bBy default it's CHEST")
                                         .toItemStack();
        
        ItemStack creative = ItemStackWrapper.newItem(XMaterial.CRAFTING_TABLE)
                                              .setDisplayName("&3Close menu to pick items from creative mode")
                                              .toItemStack();
        
        ItemStackWrapper builder = ItemStackWrapper.newItem(XMaterial.GLOWSTONE_DUST);
        if(item.getProbability() < 0){
            builder.setDisplayName("&cProbability must be a positive integer or 0");
        }else{
            builder.setDisplayName("&6Outcome probability: &r" + item.getProbability());
        }
        builder.setLore("&3Click to change");
        ItemStack prob = builder.toItemStack();
        
        menu.setItem(10, GUIItem.getBackItem());
        menu.setItem(16, GUIItem.getNextItem());
        
        menu.setItem(12, name);
        menu.setItem(13, icon);
        menu.setItem(14, prob);
        
        menu.setItem(22, creative);
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction NEW_OUTCOME_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 10:
                    // Go to previous menu
                    onBack.accept(player);
                    break;
                case 12:
                    ChatListener.registerPlayer(player, message -> {
                        item.setName(message);
                        openOutcomeMenu(player);
                    });
                    player.closeInventory();
                    break;
                case 14:
                    ChatListener.registerPlayer(player, message -> {
                        try{
                            int probability = Integer.parseInt(message);
                            if(probability >= 0){
                                item.setProbability(probability);
                            }else{
                                item.setProbability(-1);
                            }
                        }catch(NumberFormatException ex){
                            item.setProbability(-2);
                        }
                        openOutcomeMenu(player);
                    });
                    player.closeInventory();
                    break;
                case 16:
                    if(item.getProbability() >= 0){
                        // Go to next inventory
                        Editor<Outcome> editor = EditorType.REWARD_LIST.getEditor();
                        editor.editExistingItem(
                                item, 
                                player, 
                                p -> openOutcomeMenu(p), 
                                onNext);
                    }
                    break;
                case 22:
                    //Close menu
                    player.closeInventory();
                    Logger.sendMessage("&6Use &b/alb return &6to return to the menu", player);
                    break;
            }
        }else{
            ItemStack stack = e.getCurrentItem();
            if(stack != null && stack.getType() != Material.AIR){
                item.setIcon(stack);                
                openOutcomeMenu(player);
            }
        }
//</editor-fold>
    };
}
