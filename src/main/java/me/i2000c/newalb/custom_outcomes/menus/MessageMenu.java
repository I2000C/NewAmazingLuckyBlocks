package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.custom_outcomes.utils.rewards.MessageReward;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.inventory.ItemStack;
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
        
        ItemStack glass = ItemBuilder.newItem(XMaterial.CYAN_STAINED_GLASS_PANE)
                .withDisplayName(" ").build();
        
        ItemStack titleItem = ItemBuilder.newItem(XMaterial.BOOK)
                .withDisplayName("&bSelect title")
                .addLoreLine("&3Selected title: &r\"" + reward.getTitle() + "&r\"")
                .addLoreLine("&eClick to change")
                .addLoreLine("")
                .addLoreLine("&7Use &a%player% &7if you want to use")
                .addLoreLine("&7  the player's name in the message,")
                .addLoreLine("&7&a%x%&7, &a%y%, &a%z% &7if you want")
                .addLoreLine("&7  to use the player's coordinates")
                .addLoreLine("&7or &a%bx%&7, &a%by%, &a%bz% &7if you want")
                .addLoreLine("&7  to use the LuckyBlock's coordinates")
                .build();
        
        ItemStack subtitleItem = ItemBuilder.newItem(XMaterial.BOOK)
                .withDisplayName("&bSelect subtitle")
                .addLoreLine("&3Selected subtitle: &r\"" + reward.getSubtitle() + "&r\"")
                .addLoreLine("&eClick to change")
                .addLoreLine("")
                .addLoreLine("&6The subtitle only is used")
                .addLoreLine("&6  when message type is TITLE")
                .addLoreLine("")
                .addLoreLine("&7Use &a%player% &7if you want to use")
                .addLoreLine("&7  the player's name in the message,")
                .addLoreLine("&7&a%x%&7, &a%y%, &a%z% &7if you want")
                .addLoreLine("&7  to use the player's coordinates")
                .addLoreLine("&7or &a%bx%&7, &a%by%, &a%bz% &7if you want")
                .addLoreLine("&7  to use the LuckyBlock's coordinates")
                .build();
        
        ItemBuilder builder;
        switch(reward.getMessageType()){
            case TITLE:
                builder = ItemBuilder.newItem(XMaterial.PAINTING);
                break;
            case ACTION_BAR:
                builder = ItemBuilder.newItem(XMaterial.NAME_TAG);
                break;
            default:
            //case CHAT:
                builder = ItemBuilder.newItem(XMaterial.OAK_SIGN);
        }        
        builder.withDisplayName("&aSelect message type");
        builder.withLore("&3Selected message type: &5" + reward.getMessageType().name());
        ItemStack typeItem = builder.build();
        
        ItemStack back = ItemBuilder.newItem(XMaterial.ENDER_PEARL)
                .withDisplayName("&7Back")
                .build();
        
        ItemStack next = ItemBuilder.newItem(XMaterial.ANVIL)
                .withDisplayName("&bNext")
                .build();
        
        ItemStack deleteTitle = ItemBuilder.newItem(XMaterial.BARRIER)
                .withDisplayName("&cRemove title")
                .build();
        
        ItemStack deleteSubtitle = ItemBuilder.newItem(XMaterial.BARRIER)
                .withDisplayName("&cRemove subtitle")
                .build();
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.MESSAGE_MENU, 27, "&7&lMessage Reward");
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
