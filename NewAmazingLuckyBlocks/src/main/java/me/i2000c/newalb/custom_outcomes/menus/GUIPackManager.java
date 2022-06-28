package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.CommandManager;
import me.i2000c.newalb.custom_outcomes.utils.OutcomePack;
import me.i2000c.newalb.custom_outcomes.utils.PackManager;
import me.i2000c.newalb.utils.Logger;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        
        ItemMeta meta;
        String lore;
        
        ItemStack createPack = new ItemStack(Material.SLIME_BALL);
        meta = createPack.getItemMeta();
        meta.setDisplayName(Logger.color("&aCreate new pack"));
        createPack.setItemMeta(meta);
        
        ItemStack renamePack = new ItemStack(Material.NAME_TAG);
        meta = renamePack.getItemMeta();
        meta.setDisplayName(Logger.color("&3Rename packs"));
        if(renameMode){
            lore = Logger.color("&6Rename mode: &aenabled");
        }else{
            lore = Logger.color("&6Rename mode: &7disabled");
        }
        meta.setLore(Arrays.asList(lore));
        renamePack.setItemMeta(meta);
        
        ItemStack clonePack = XMaterial.REPEATER.parseItem();
        meta = clonePack.getItemMeta();
        meta.setDisplayName(Logger.color("&bClone packs"));
        if(cloneMode){
            lore = Logger.color("&6Clone mode: &aenabled");
        }else{
            lore = Logger.color("&6Clone mode: &7disabled");
        }
        meta.setLore(Arrays.asList(lore));
        clonePack.setItemMeta(meta);
        
        ItemStack deletePack = new ItemStack(Material.BARRIER);
        meta = deletePack.getItemMeta();
        meta.setDisplayName(Logger.color("&cRemove packs"));
        List<String> loreList = new ArrayList();
        if(deleteMode){
            loreList.add(Logger.color("&6Delete mode: &aenabled"));
            loreList.add("");
            loreList.add(Logger.color("&4&lWARNING: &cIf this mode is enabled,"));
            loreList.add(Logger.color("&cwhen you click on a pack,"));
            loreList.add(Logger.color("&cit will be deleted permanently"));
        }else{
            loreList.add(Logger.color("&6Delete mode: &7disabled"));
        }
        meta.setLore(loreList);
        deletePack.setItemMeta(meta);
        
        
        ItemStack exit = new ItemStack(Material.ENDER_PEARL);
        meta = exit.getItemMeta();
        meta.setDisplayName(Logger.color("&2Back"));
        exit.setItemMeta(meta);
        
        ItemStack pages = new ItemStack(Material.BOOK);
        meta = pages.getItemMeta();
        meta.setDisplayName(Logger.color("&6Page ? / ?"));
        pages.setItemMeta(meta);
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName(Logger.color("&2Previous page"));
        back.setItemMeta(meta);
        
        ItemStack next = XMaterial.ENDER_EYE.parseItem();
        meta = next.getItemMeta();
        meta.setDisplayName(Logger.color("&aNext page"));
        next.setItemMeta(meta);
        
        
        inv.setItem(45, exit);
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
        //inv.setItem(51, back);
        //inv.setItem(52, pages);
        //inv.setItem(53, next);
        
        List<OutcomePack> packList = PackManager.getManager().getPacks();
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
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){

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
                            PackManager.getManager().addNewPack(pack, p);
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
                    if(sk != null && sk.getType() != Material.AIR
                            && sk.hasItemMeta() && sk.getItemMeta().hasDisplayName()){
                        String packName = Logger.stripColor(sk.getItemMeta().getDisplayName());

                        /*if(probabilityMode){
                            //Set pack probability
                            chat3 = true;
                            currentPackName = packName;
                            p.closeInventory();
                            Logger.sendMessage(Logger.color("&3Write the new pack probability in the chat"), p);
                        }else */
                        if(renameMode){
                            //Rename pack
                            ChatListener.registerPlayer(p, message -> {
                                String filename = message;
                                if(!filename.endsWith(".yml")){
                                    filename += ".yml";
                                }
                                PackManager.getManager().renamePack(packName, filename, p);
                                openMainMenu(p);
                            });
                            p.closeInventory();
                            Logger.sendMessage(Logger.color("&3Write the new pack name in the chat"), p);
                        }else if(cloneMode){
                            //Clone pack
                            PackManager.getManager().clonePack(packName, p);
                            openMainMenu(p);
                        }else if(deleteMode){
                            //Delete pack
                            PackManager.getManager().removePack(packName, p);
                            openMainMenu(p);
                        }else{
                            //Edit pack
                            /*FinishMenu.reset();
                            FinishMenu.setCurrentPack(PackManager.getManager().getPack(packName));
                            GUIManager.reset();
                            GUIManager.openMainMenu(p);*/
                            OutcomePack pack = PackManager.getManager().getPack(packName);
                            OutcomeListMenu.reset();
                            OutcomeListMenu.openOutcomeListMenu(p, pack);
                        }
                    }
            }
        }
//</editor-fold>
    };
}
