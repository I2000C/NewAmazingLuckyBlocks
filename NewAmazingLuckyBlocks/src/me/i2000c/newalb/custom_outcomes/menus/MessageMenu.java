package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import java.util.Arrays;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.utils.rewards.MessageReward;
import me.i2000c.newalb.utils.Logger;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.Player;

public class MessageMenu{
    public static MessageReward reward = null;
    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.MESSAGE_MENU, MESSAGE_MENU_FUNCTION);
            
            inventoriesRegistered = true;
        }
        
        reward = null;
    }
    
    public static void openMessageMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(reward == null){
            reward = new MessageReward(FinishMenu.getCurrentOutcome());
        }
        
        ItemStack glass = XMaterial.CYAN_STAINED_GLASS_PANE.parseItem();
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        
        ItemStack titleItem = new ItemStack(Material.BOOK);
        meta = titleItem.getItemMeta();
        meta.setDisplayName(Logger.color("&bSelect title"));
        meta.setLore(Logger.color(Arrays.asList("&3Selected title: &r\"" + reward.getTitle() + "&r\"")));
        titleItem.setItemMeta(meta);
        
        ItemStack subtitleItem = new ItemStack(Material.BOOK);
        meta = subtitleItem.getItemMeta();
        meta.setDisplayName(Logger.color("&bSelect subtitle"));
        meta.setLore(Logger.color(Arrays.asList("&3Selected subtitle: &r\"" + reward.getSubtitle() + "&r\"", "&6The subtitle only is used", "&6  when message type is TITLE")));
        subtitleItem.setItemMeta(meta);
        
        ItemStack typeItem;
        switch(reward.getMessageType()){
            case TITLE:
                typeItem = new ItemStack(Material.PAINTING);
                break;
            case ACTION_BAR:
                typeItem = new ItemStack(Material.NAME_TAG);
                break;
            default:
            //case CHAT:
                typeItem = XMaterial.OAK_SIGN.parseItem();
        }
        meta = typeItem.getItemMeta();
        meta.setDisplayName(Logger.color("&aSelect message type"));
        meta.setLore(Logger.color(Arrays.asList("&3Selected message type: &5" + reward.getMessageType().name())));
        typeItem.setItemMeta(meta);
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName(Logger.color("&7Back"));
        back.setItemMeta(meta);
        
        ItemStack next = new ItemStack(Material.ANVIL);
        meta = next.getItemMeta();
        meta.setDisplayName(Logger.color("&bNext"));
        next.setItemMeta(meta);
        
        ItemStack deleteTitle = new ItemStack(Material.BARRIER);
        meta = deleteTitle.getItemMeta();
        meta.setDisplayName(Logger.color("&cRemove title"));
        deleteTitle.setItemMeta(meta);
        
        ItemStack deleteSubtitle = new ItemStack(Material.BARRIER);
        meta = deleteSubtitle.getItemMeta();
        meta.setDisplayName(Logger.color("&cRemove subtitle"));
        deleteSubtitle.setItemMeta(meta);
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.MESSAGE_MENU, 27, Logger.color("&7&lMessage Reward"));
        for(int i=0;i<9;i++){
            inv.setItem(i, glass);
        }
        for(int i=18;i<27;i++){
            inv.setItem(i, glass);
        }
        inv.setItem(9, glass);
        inv.setItem(17, glass);
        
        inv.setItem(12, titleItem);
        inv.setItem(13, subtitleItem);
        inv.setItem(14, typeItem);
        
        inv.setItem(21, deleteTitle);
        inv.setItem(22, deleteSubtitle);
        
        inv.setItem(10, back);
        inv.setItem(16, next);
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    
    private static final InventoryFunction MESSAGE_MENU_FUNCTION = e -> {
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
                case 12:
                    ChatListener.registerPlayer(p, message -> {
                        reward.setTitle(message);
                        openMessageMenu(p);
                    });
                    p.closeInventory();
                    Logger.sendMessage("&3Write the title in the chat and press ENTER", p);
                    break;
                case 13:
                    ChatListener.registerPlayer(p, message -> {
                        reward.setSubtitle(message);
                        openMessageMenu(p);
                    });
                    p.closeInventory();
                    Logger.sendMessage("&3Write the subtitle in the chat and press ENTER", p);
                    break;
                case 14:
                    MessageReward.MessageType type = reward.getMessageType();
                    reward.setMessageType(type.getNextType());
                    openMessageMenu(p);
                    break;
                case 21:
                    reward.setTitle("");
                    openMessageMenu(p);
                    break;
                case 22:
                    reward.setSubtitle("");
                    openMessageMenu(p);
                    break;
                case 16:
                    //Open next inventory
                    FinishMenu.addReward(reward);
                    reset();
                    FinishMenu.openFinishInventory(p);
                    break;
            }
        }
//</editor-fold>
    };
}
