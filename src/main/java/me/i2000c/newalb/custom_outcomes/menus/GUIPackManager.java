package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import java.io.File;
import java.util.List;
import me.i2000c.newalb.CommandManager;
import me.i2000c.newalb.custom_outcomes.utils.OutcomePack;
import me.i2000c.newalb.custom_outcomes.utils.PackManager;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUIPackManager{
    private static boolean renameMode;
    private static boolean cloneMode;
    private static boolean deleteMode;
    
    private static String currentPackName;
    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.GUI_PACK_MANAGER_MENU, GUI_PACK_MANAGER_MENU_FUNCTION);
            
            inventoriesRegistered = true;
        }
        
        renameMode = false;
        cloneMode = false;
        deleteMode = false;
    }
    
    public static void openMainMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        CommandManager.confirmMenu = true;
        currentPackName = null;
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.GUI_PACK_MANAGER_MENU, 54, "&3&lPack menu");
        
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
        
        inv.setItem(45, GUIItem.getBackItem());
        if(!renameMode && !cloneMode && !deleteMode){
            inv.setItem(46, createPack);
        }
        if(!cloneMode && !deleteMode){
            inv.setItem(47, renamePack);
        }
        if(!renameMode && !deleteMode){
            inv.setItem(48, clonePack);
        }
        if(!renameMode && !cloneMode){
            inv.setItem(49, deletePack);
        }        
        
        //Not required for the moment
        //inv.setItem(51, GUIItem.getPreviousPageItem());
        //inv.setItem(52, GUIItem.getCurrentPageItem(0, 0));
        //inv.setItem(53, GUIItem.getNextPageItem());
        
        List<OutcomePack> packList = PackManager.getPacks();
        packList.sort((OutcomePack pack1, OutcomePack pack2) -> pack1.getFilename().compareTo(pack2.getFilename()));
        
        int i = 0;
        for(OutcomePack pack : packList){
            if(i >= 45){
                break;
            }
            inv.setItem(i, pack.getItemToDisplay());
            i++;
        }
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction GUI_PACK_MANAGER_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 45:
                    //Back to main menu
                    MainMenu.reset();
                    MainMenu.openMainMenu(p);
                    break;
                case 46:
                    //Create new pack
                    if(!renameMode && !cloneMode && !deleteMode){
                        ChatListener.registerPlayer(p, message -> {
                            String filename = message;
                            if(!filename.endsWith(".yml")){
                                filename += ".yml";
                            }
                            File newFile = new File(PackManager.OUTCOMES_FOLDER, filename);
                            OutcomePack pack = new OutcomePack(newFile);
                            pack.saveOutcomes();
                            PackManager.addNewPack(pack, p);
                            openMainMenu(p);
                        });
                        p.closeInventory();
                        Logger.sendMessage("&3Write the new pack name in the chat", p);
                    }
                    break;
                case 47:
                    //Toggle rename mode
                    if(!cloneMode && !deleteMode){
                        renameMode = !renameMode;
                        openMainMenu(p);
                    }
                    break;
                case 48:
                    //Toggle clone mode
                    if(!renameMode && !deleteMode){
                        cloneMode = !cloneMode;
                        openMainMenu(p);
                    }
                    break;
                case 49:
                    //Toggle delete mode
                    if(!renameMode && !cloneMode){
                        deleteMode = !deleteMode;
                        openMainMenu(p);
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

                        /*if(probabilityMode){
                            //Set pack probability
                            chat3 = true;
                            currentPackName = packName;
                            p.closeInventory();
                            Logger.sendMessage("&3Write the new pack probability in the chat"), p;
                        }else */
                        if(renameMode){
                            //Rename pack
                            ChatListener.registerPlayer(p, message -> {
                                String filename = message;
                                if(!filename.endsWith(".yml")){
                                    filename += ".yml";
                                }
                                PackManager.renamePack(packName, filename, p);
                                openMainMenu(p);
                            });
                            p.closeInventory();
                            Logger.sendMessage(("&3Write the new pack name in the chat"), p);
                        }else if(cloneMode){
                            //Clone pack
                            PackManager.clonePack(packName, p);
                            openMainMenu(p);
                        }else if(deleteMode){
                            //Delete pack
                            PackManager.removePack(packName, p);
                            openMainMenu(p);
                        }else{
                            //Edit pack
                            /*FinishMenu.reset();
                            FinishMenu.setCurrentPack(PackManager.getPack(packName));
                            GUIManager.reset();
                            GUIManager.openMainMenu(p);*/
                            OutcomePack pack = PackManager.getPack(packName);
                            OutcomeListMenu.reset();
                            OutcomeListMenu.openOutcomeListMenu(p, pack);
                        }
                    }
            }
        }
//</editor-fold>
    };
}
