package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.editor.EditorType;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils2.ItemBuilder;
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
        
        ItemStack name = ItemBuilder.newItem(XMaterial.OAK_SIGN)
                .withDisplayName("&7Outcome name: &r" + item.getName())
                .withLore("&3Click to change")
                .build();
        
        ItemStack icon = ItemBuilder.fromItem(item.getIcon())
                .withDisplayName("&dOutcome icon")
                .withLore("&bClick on an item of your inventory",
                          "&b   to select the icon of the outcome.",
                          "&bBy default it's CHEST"
                ).build();
        
        
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.GLOWSTONE_DUST);
        if(item.getProbability() < 0){
            builder.withDisplayName("&cProbability must be a positive integer or 0");
        }else{
            builder.withDisplayName("&6Outcome probability: &r" + item.getProbability());
        }
        builder.withLore("&3Click to change");
        ItemStack prob = builder.build();
        
        for(int i=0;i<9;i++){
            menu.setItem(i, glass);
        }
        for(int i=18;i<27;i++){
            menu.setItem(i, glass);
        }
        menu.setItem(9, glass);
        menu.setItem(17, glass);
        
        menu.setItem(10, GUIItem.getBackItem());
        menu.setItem(16, GUIItem.getNextItem());
        
        menu.setItem(12, name);
        menu.setItem(13, icon);
        menu.setItem(14, prob);
        
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
            }
        }else{
            ItemStack stack = e.getCurrentItem();
            if(stack != null && stack.getType() != Material.AIR){
                item.setIcon(new ItemStack(stack.getType()));
                if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                    item.getIcon().setDurability(stack.getDurability());
                }
                
                openOutcomeMenu(player);
            }
        }
//</editor-fold>
    };
}
