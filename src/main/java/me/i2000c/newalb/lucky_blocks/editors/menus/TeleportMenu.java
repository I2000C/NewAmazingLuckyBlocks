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
import me.i2000c.newalb.lucky_blocks.rewards.TypeManager;
import me.i2000c.newalb.lucky_blocks.rewards.types.TeleportReward;
import me.i2000c.newalb.utils.locations.Offset;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TeleportMenu extends Editor<TeleportReward>{
    public TeleportMenu(){
        InventoryListener.registerInventory(CustomInventoryType.TELEPORT_MENU, TELEPORT_MENU_FUNCTION);
    }
    
    @Override
    protected void newItem(Player player){
        Outcome outcome = RewardListMenu.getCurrentOutcome();
        item = new TeleportReward(outcome);
        openTeleportMenu(player);
    }
    
    @Override
    protected void editItem(Player player){
        openTeleportMenu(player);
    }
    
    private void openTeleportMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.TELEPORT_MENU, 27, "&e&lTeleport Reward");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.YELLOW);
        
        for(int i=0;i<=9;i++){
            menu.setItem(i, glass);
        }
        for(int i=17;i<27;i++){
            menu.setItem(i, glass);
        }
        
        ItemStackWrapper builder;
        switch(item.getTeleportSource()){
            case RELATIVE_TO_PLAYER:
                builder = ItemStackWrapper.newItem(XMaterial.PLAYER_HEAD);
                break;
            case RELATIVE_TO_LUCKY_BLOCK:
                builder = ItemStackWrapper.fromItem(TypeManager.getMenuItemStack(), false);
                break;
            default:
                builder = ItemStackWrapper.newItem(XMaterial.GRASS_BLOCK);
        }
        builder.setDisplayName("&bTeleport source: &a" + item.getTeleportSource().name());
        builder.addLoreLine("&3Click to change");
        ItemStack teleportSourceStack = builder.toItemStack();
        
        ItemStack worldNameStack = ItemStackWrapper.newItem(XMaterial.OAK_SIGN)
                                                   .setDisplayName("&bSelected world: &a" + item.getWorldName())
                                                   .addLoreLine("&3Click to change")
                                                   .addLoreLine("")
                                                   .addLoreLine("&6This world is only used")
                                                   .addLoreLine("&6  if teleport source is &b&lABSOLUTE&6.")
                                                   .addLoreLine("&6You can use &a%world% &6to select")
                                                   .addLoreLine("&6  the world of the player")
                                                   .toItemStack();
        
        menu.setItem(12, teleportSourceStack);
        menu.setItem(13, worldNameStack);
        menu.setItem(14, item.getOffset().getItemToDisplay());
        
        menu.setItem(10, GUIItem.getBackItem());
        menu.setItem(16, GUIItem.getNextItem());
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction TELEPORT_MENU_FUNCTION = e -> {
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
                    // Change teleport source
                    item.setTeleportSource(item.getTeleportSource().next());
                    openTeleportMenu(player);
                    break;
                case 13:
                    // Change world name
                    ChatListener.registerPlayer(player, message -> {
                        if(!message.equals(TeleportReward.PLAYER_WORLD_PATTERN)){
                            if(Bukkit.getWorld(message) == null){
                                Logger.sendMessage("&cWorld &b" + message + " &cdoesn't exist", player, true);
                                Logger.sendMessage("&cUse &b/alb return &cif you want to return to the menu", player, true);
                                return;
                            }
                        }
                        
                        item.setWorldName(message);
                        openTeleportMenu(player);
                    }, false);
                    player.closeInventory();
                    break;
                case 14:
                    // Open offset menu
                    Editor<Offset> editor = EditorType.OFFSET.getEditor();
                    editor.editExistingItem(
                            item.getOffset().clone(),
                            player,
                            p -> openTeleportMenu(p),
                            (p, offset) -> {
                                item.setOffset(offset);
                                openTeleportMenu(p);
                            });
                    break;
            }
//</editor-fold>
        }
    };
}
