package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.List;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.custom_outcomes.utils.rewards.CommandReward;
import me.i2000c.newalb.utils.Logger;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.Player;

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
        
        List<String> lore = new ArrayList<>();
        lore.add(Logger.color("&7Click here and then, write the command"));
        lore.add(Logger.color("&7in the chat without the first '&b&l/&7'&r"));
        lore.add("");
        lore.add(Logger.color("&7Use &a%player% &7if you want to use the player's name in a command,"));
        lore.add(Logger.color("&7    &a%x%&7, &a%y%, &a%z% &7if you want to use the player's coordinates"));
        lore.add(Logger.color("&7    or &a%bx%&7, &a%by%, &a%bz% &7if you want to use the LuckyBlock's coordinates"));
        
        ItemStack glass = XMaterial.CYAN_STAINED_GLASS_PANE.parseItem();
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        
        ItemStack item = XMaterial.OAK_SIGN.parseItem();
        item.setDurability((short) 0);
        meta = item.getItemMeta();
        if(reward.getCommand() == null){
            meta.setDisplayName(Logger.color("&6Write the command"));
        }else{
            meta.setDisplayName(Logger.color("&6Command selected: &r/" + reward.getCommand()));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        ItemStack sender_item;
        if(reward.getSender().equals("Console")){
            sender_item = XMaterial.COMMAND_BLOCK.parseItem();
        }else{
            sender_item = XMaterial.PLAYER_HEAD.parseItem();
        }
        String senderName;
        if(reward.getSender().equals("Console")){
            senderName = "&8Console";
        }else{
            senderName = "&2Player";
        }
        meta = sender_item.getItemMeta();
        meta.setDisplayName(Logger.color("&dSender: " + senderName));
        sender_item.setItemMeta(meta);
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName(Logger.color("&7Back"));
        back.setItemMeta(meta);
        
        ItemStack next = new ItemStack(Material.ANVIL);
        meta = next.getItemMeta();
        meta.setDisplayName(Logger.color("&bNext"));
        next.setItemMeta(meta);
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.COMMAND_MENU, 27, Logger.color("&7&lCommand Reward"));
        for(int i=0;i<9;i++){
            inv.setItem(i, glass);
        }
        for(int i=18;i<27;i++){
            inv.setItem(i, glass);
        }
        inv.setItem(9, glass);
        inv.setItem(17, glass);
        
        inv.setItem(11, sender_item);
        inv.setItem(13, item);
        inv.setItem(10, back);
        inv.setItem(16, next);
        
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
                    if(reward.getSender().equalsIgnoreCase("Console")){
                        reward.setSender("Player");
                    }else{
                        reward.setSender("Console");
                    }
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
                        if(!reward.getCommand().equals(Logger.color("&6Write the command"))){
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
