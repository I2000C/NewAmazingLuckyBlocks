package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import java.io.File;
import java.util.List;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.editor.EditorType;
import me.i2000c.newalb.custom_outcomes.rewards.OutcomePack;
import me.i2000c.newalb.custom_outcomes.rewards.PackManager;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.OtherUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PackListMenu extends Editor{
    public PackListMenu(){
        InventoryListener.registerInventory(CustomInventoryType.GUI_PACK_MANAGER_MENU, GUI_PACK_MANAGER_MENU_FUNCTION);
    }
    
    private boolean renameMode;
    private boolean cloneMode;
    private boolean deleteMode;
    
    @Override
    protected void reset(){
        renameMode = false;
        cloneMode = false;
        deleteMode = false;
    }
    
    @Override
    protected void newItem(Player player){
        openPackListMenu(player);
    }
    
    @Override
    protected void editItem(Player player){
        openPackListMenu(player);
    }
    
    private void openPackListMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.GUI_PACK_MANAGER_MENU, 54, "&3&lPack menu");
        
        ItemStack createPack = ItemBuilder.newItem(XMaterial.SLIME_BALL)
                .withDisplayName("&aCreate new pack")
                .build();
        
        ItemStack renamePack = GUIItem.getEnabledDisabledItem(
                renameMode, 
                "&3Rename packs", 
                "&6Rename mode", 
                XMaterial.NAME_TAG, 
                XMaterial.NAME_TAG);
        
        ItemStack clonePack = GUIItem.getEnabledDisabledItem(
                cloneMode, 
                "&bClone packs", 
                "&6Clone mode", 
                XMaterial.REPEATER, 
                XMaterial.REPEATER);
        
        ItemStack deletePack = GUIItem.getEnabledDisabledItem(
                deleteMode, 
                "&cRemove packs", 
                "&6Delete mode", 
                XMaterial.BARRIER, 
                XMaterial.BARRIER);
        ItemBuilder.fromItem(deletePack, false)
                .addLoreLine("")
                .addLoreLine("&4&lWARNING: &cIf this mode is enabled,")
                .addLoreLine("&cwhen you click on a pack,")
                .addLoreLine("&cit will be deleted permanently");
        
        menu.setItem(45, GUIItem.getBackItem());
        if(!renameMode && !cloneMode && !deleteMode){
            menu.setItem(46, createPack);
        }
        if(!cloneMode && !deleteMode){
            menu.setItem(47, renamePack);
        }
        if(!renameMode && !deleteMode){
            menu.setItem(48, clonePack);
        }
        if(!renameMode && !cloneMode){
            menu.setItem(49, deletePack);
        }        
        
        //Not required for the moment
        //menu.setItem(51, GUIItem.getPreviousPageItem());
        //menu.setItem(52, GUIItem.getCurrentPageItem(0, 0));
        //menu.setItem(53, GUIItem.getNextPageItem());
        
        List<OutcomePack> packList = PackManager.getPacks();
        packList.sort((OutcomePack pack1, OutcomePack pack2) -> pack1.getPackname().compareTo(pack2.getPackname()));
        
        int i = 0;
        for(OutcomePack pack : packList){
            if(i >= 45){
                break;
            }
            menu.setItem(i, pack.getItemToDisplay());
            i++;
        }
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction GUI_PACK_MANAGER_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 45:
                    // Go to previous menu
                    onBack.accept(player);
                    break;
                case 46:
                    //Create new pack
                    if(!renameMode && !cloneMode && !deleteMode){
                        ChatListener.registerPlayer(player, message -> {
                            String packName = OtherUtils.removeExtension(message);
                            File newFile = new File(PackManager.OUTCOMES_FOLDER, packName + ".yml");
                            if(newFile.exists()){
                                Logger.sendMessage("&cPack &6\"" + packName + "\" &calready exists", player);
                                Logger.sendMessage("&cUse &b/alb return &cto return to the menu", player, false);
                                return;
                            }
                            
                            ChatListener.removePlayer(player);
                            OutcomePack pack = new OutcomePack(newFile);
                            pack.saveOutcomes();
                            PackManager.addNewPack(pack, player);
                            openPackListMenu(player);
                        }, false);
                        player.closeInventory();
                        Logger.sendMessage("&3Write the new pack name in the chat", player);
                    }
                    break;
                case 47:
                    //Toggle rename mode
                    if(!cloneMode && !deleteMode){
                        renameMode = !renameMode;
                        openPackListMenu(player);
                    }
                    break;
                case 48:
                    //Toggle clone mode
                    if(!renameMode && !deleteMode){
                        cloneMode = !cloneMode;
                        openPackListMenu(player);
                    }
                    break;
                case 49:
                    //Toggle delete mode
                    if(!renameMode && !cloneMode){
                        deleteMode = !deleteMode;
                        openPackListMenu(player);
                    }
                    break;                
                default:
                    ItemStack sk = e.getCurrentItem();
                    if(sk != null && sk.getType() != Material.AIR){
                        String displayName = ItemBuilder.fromItem(e.getCurrentItem(), false)
                                .getDisplayName();
                        if(displayName == null){
                            return;
                        }
                        
                        String packName = Logger.stripColor(displayName);
                        if(renameMode){
                            //Rename pack
                            ChatListener.registerPlayer(player, message -> {
                                String newPackName = OtherUtils.removeExtension(message);
                                File newFile = new File(PackManager.OUTCOMES_FOLDER, newPackName + ".yml");
                                if(newFile.exists()){
                                    Logger.sendMessage("&cPack &6\"" + packName + "\" &calready exists", player);
                                    Logger.sendMessage("&cUse &b/alb return &cto return to the menu", player, false);
                                    return;
                                }
                                
                                ChatListener.removePlayer(player);
                                PackManager.renamePack(packName, newPackName, player);
                                openPackListMenu(player);
                            });
                            player.closeInventory();
                            Logger.sendMessage(("&3Write the new pack name in the chat"), player);
                        }else if(cloneMode){
                            //Clone pack
                            PackManager.clonePack(packName, player);
                            openPackListMenu(player);
                        }else if(deleteMode){
                            //Delete pack
                            PackManager.removePack(packName, player);
                            openPackListMenu(player);
                        }else{
                            //Edit pack
                            Editor<OutcomePack> editor = EditorType.OUTCOME_LIST.getEditor();
                            editor.editExistingItem(
                                    PackManager.getPack(packName), 
                                    player, 
                                    p -> openPackListMenu(p), 
                                    null);
                        }
                    }
            }
        }
//</editor-fold>
    };
}
