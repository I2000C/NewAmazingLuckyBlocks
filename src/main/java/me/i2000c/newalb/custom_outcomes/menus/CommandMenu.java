package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.utils.rewards.CommandReward;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CommandMenu{ 
    public static CommandReward reward = null;
    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.COMMAND_MENU, COMMAND_MENU_FUNCTION);
            
            inventoriesRegistered = true;
        }
        
        reward = null;
    }
    
    public static void openCommandMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(reward == null){
            reward = new CommandReward(FinishMenu.getCurrentOutcome());
        }
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.CYAN);
        
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.OAK_SIGN);
        if(reward.getCommand() == null){
            builder.withDisplayName("&6Command selected: &cnull");
        }else{
            builder.withDisplayName("&6Command selected: &r/" + reward.getCommand());
        }
        builder.addLoreLine("&7Click here and then, write the command");
        builder.addLoreLine("");
        builder.addLoreLine("&7Use &a%player% &7if you want to use");
        builder.addLoreLine("&7  the player's name in the command,");
        builder.addLoreLine("&7&a%x%&7, &a%y%, &a%z% &7if you want");
        builder.addLoreLine("&7  to use the player's coordinates");
        builder.addLoreLine("&7or &a%bx%&7, &a%by%, &a%bz% &7if you want");
        builder.addLoreLine("&7  to use the LuckyBlock's coordinates");
        ItemStack cmd_item = builder.build();
                
        if(reward.getSenderIsPlayer()){
            builder = ItemBuilder.newItem(XMaterial.PLAYER_HEAD);
            builder.withDisplayName("&dSender: &2Player");
        }else{
            builder = ItemBuilder.newItem(XMaterial.COMMAND_BLOCK);
            builder.withDisplayName("&dSender: &8Console");
        }
        builder.addLoreLine("&3Click to toggle");
        ItemStack sender_item = builder.build();
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.COMMAND_MENU, 27, "&7&lCommand Reward");
        for(int i=0;i<9;i++){
            inv.setItem(i, glass);
        }
        for(int i=18;i<27;i++){
            inv.setItem(i, glass);
        }
        inv.setItem(9, glass);
        inv.setItem(17, glass);
        
        inv.setItem(11, sender_item);
        inv.setItem(13, cmd_item);
        inv.setItem(10, GUIItem.getBackItem());
        inv.setItem(16, GUIItem.getNextItem());
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    
    private static final InventoryFunction COMMAND_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            switch(e.getSlot()){
                case 10:
                    if(FinishMenu.editMode){
                        FinishMenu.openFinishInventory(p);
                    }else{
                        RewardTypesMenu.openRewardTypesMenu(p);
                    }
                    break;
                case 11:
                    reward.setSenderIsPlayer(!reward.getSenderIsPlayer());
                    openCommandMenu(p);
                    break;
                case 13:
                    ChatListener.registerPlayer(p, message -> {
                        reward.setCommand(message);
                        openCommandMenu(p);
                    });
                    p.closeInventory();
                    break;
                case 16:
                    try{
                        if(!reward.getCommand().equals("&6Write the command")){
                            //Open next inventory
                            FinishMenu.addReward(reward);
                            reset();
                            FinishMenu.openFinishInventory(p);
                        }
                    }catch(Exception ex){
                    }
                    break;
            }
        }
//</editor-fold>
    };
}
