package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.MessageReward;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MessageMenu extends Editor<MessageReward>{
    public MessageMenu(){
        InventoryListener.registerInventory(CustomInventoryType.MESSAGE_MENU, MESSAGE_MENU_FUNCTION);
    }
    
    @Override
    protected void newItem(Player player){
        Outcome outcome = RewardListMenu.getCurrentOutcome();
        item = new MessageReward(outcome);
        openMessageMenu(player);
    }
    
    @Override
    protected void editItem(Player player){
        openMessageMenu(player);
    }
    
    private void openMessageMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.MESSAGE_MENU, 27, "&7&lMessage Reward");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.CYAN);
        
        ItemStack titleItem = ItemBuilder.newItem(XMaterial.BOOK)
                .withDisplayName("&bSelect title")
                .addLoreLine("&3Selected title: &r\"" + item.getTitle() + "&r\"")
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
                .addLoreLine("&3Selected subtitle: &r\"" + item.getSubtitle() + "&r\"")
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
        switch(item.getMessageType()){
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
        builder.withLore("&3Selected message type: &5" + item.getMessageType().name());
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
        
        for(int i=0;i<9;i++){
            menu.setItem(i, glass);
        }
        for(int i=18;i<27;i++){
            menu.setItem(i, glass);
        }
        menu.setItem(9, glass);
        menu.setItem(17, glass);
        
        menu.setItem(12, titleItem);
        menu.setItem(13, subtitleItem);
        menu.setItem(14, typeItem);
        
        menu.setItem(21, deleteTitle);
        menu.setItem(22, deleteSubtitle);
        
        menu.setItem(10, back);
        menu.setItem(16, next);
        
        menu.openToPlayer(player);
//</editor-fold>
    }    
    
    private final InventoryFunction MESSAGE_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 10:
                    // Go to previous menu
                    onBack.accept(player);
                    break;
                case 16:
                    // Go to next menu
                    onNext.accept(player, item);
                    break;
                case 12:
                    ChatListener.registerPlayer(player, message -> {
                        item.setTitle(message);
                        openMessageMenu(player);
                    });
                    player.closeInventory();
                    Logger.sendMessage("&3Write the title in the chat and press ENTER", player);
                    break;
                case 13:
                    ChatListener.registerPlayer(player, message -> {
                        item.setSubtitle(message);
                        openMessageMenu(player);
                    });
                    player.closeInventory();
                    Logger.sendMessage("&3Write the subtitle in the chat and press ENTER", player);
                    break;
                case 14:
                    MessageReward.MessageType type = item.getMessageType();
                    item.setMessageType(type.getNextType());
                    openMessageMenu(player);
                    break;
                case 21:
                    item.setTitle("");
                    openMessageMenu(player);
                    break;
                case 22:
                    item.setSubtitle("");
                    openMessageMenu(player);
                    break;
            }
        }
//</editor-fold>
    };
}
