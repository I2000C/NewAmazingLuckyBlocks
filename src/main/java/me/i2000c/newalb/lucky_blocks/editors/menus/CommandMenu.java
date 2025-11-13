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
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.types.CommandReward;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandMenu extends Editor<CommandReward>{
    public CommandMenu(){
        InventoryListener.registerInventory(CustomInventoryType.COMMAND_MENU, COMMAND_MENU_FUNCTION);
    }
    
    @Override
    protected void newItem(Player player){
        Outcome outcome = RewardListMenu.getCurrentOutcome();
        item = new CommandReward(outcome);
        openCommandMenu(player);
    }

    @Override
    protected void editItem(Player player){
        openCommandMenu(player);
    }
    
    public void openCommandMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemStack glass = GUIItem.getGlassItem(GlassColor.CYAN);
        
        ItemStackWrapper builder = ItemStackWrapper.newItem(XMaterial.OAK_SIGN);
        if(item.getCommand() == null){
            builder.setDisplayName("&6Command selected: &cnull");
        }else{
            builder.setDisplayName("&6Command selected: &r/" + item.getCommand());
        }
        builder.addLoreLine("&7Click here and then, write the command");
        builder.addLoreLine("");
        builder.addLoreLine("&7Use &a%player% &7if you want to use");
        builder.addLoreLine("&7  the player's name in the command,");
        builder.addLoreLine("&7&a%x%&7, &a%y%, &a%z% &7if you want");
        builder.addLoreLine("&7  to use the player's coordinates");
        builder.addLoreLine("&7or &a%bx%&7, &a%by%, &a%bz% &7if you want");
        builder.addLoreLine("&7  to use the LuckyBlock's coordinates.");
        builder.addLoreLine("&7You can use &a%world% &7to get the player's world too");
        ItemStack cmd_item = builder.toItemStack();
                
        if(item.isSendFromPlayer()){
            builder = ItemStackWrapper.newItem(XMaterial.PLAYER_HEAD);
            builder.setDisplayName("&dSender: &2Player");
        }else{
            builder = ItemStackWrapper.newItem(XMaterial.COMMAND_BLOCK);
            builder.setDisplayName("&dSender: &8Console");
        }
        builder.addLoreLine("&3Click to toggle");
        ItemStack sender_item = builder.toItemStack();
        
        Menu menu = GUIFactory.newMenu(CustomInventoryType.COMMAND_MENU, 27, "&7&lCommand Reward");
        for(int i=0;i<9;i++){
            menu.setItem(i, glass);
        }
        for(int i=18;i<27;i++){
            menu.setItem(i, glass);
        }
        menu.setItem(9, glass);
        menu.setItem(17, glass);
        
        menu.setItem(11, sender_item);
        menu.setItem(13, cmd_item);
        menu.setItem(10, GUIItem.getBackItem());
        menu.setItem(16, GUIItem.getNextItem());
        
        menu.openToPlayer(player);
//</editor-fold>
    }    
    
    private final InventoryFunction COMMAND_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 10:
                    onBack.accept(player);
                    break;
                case 11:
                    item.setSendFromPlayer(!item.isSendFromPlayer());
                    openCommandMenu(player);
                    break;
                case 13:
                    ChatListener.registerPlayer(player, message -> {
                        item.setCommand(message);
                        openCommandMenu(player);
                    });
                    player.closeInventory();
                    break;
                case 16:
                    if(item.getCommand() != null){
                        onNext.accept(player, item);
                    }
                    break;
            }
        }
//</editor-fold>
    };
}
