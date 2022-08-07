package me.i2000c.newalb.utils;

import me.i2000c.newalb.utils.logger.Logger;
import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WorldMenu{
    private static int index;
    private static final int MENU_SIZE = 47;
    private static int max_pages;
    
    private static boolean inventoriesRegistered = false;
    
    private static Map<String, Boolean> worlds;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.WORLD_MENU, WORLD_MENU_FUNCTION);
            
            inventoriesRegistered = true;
        }
        
        index = 0;
        
        worlds = new HashMap<>(WorldList.getWorlds());
    }
    
    public static void openWorldsMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        WorldList.updateWorlds(false);
        
        if(WorldList.getWorlds().size() % MENU_SIZE == 0){
            max_pages = WorldList.getWorlds().size() / MENU_SIZE;
        }else{
            max_pages = WorldList.getWorlds().size() / MENU_SIZE + 1;
        }
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.WORLD_MENU, 54, "&3&lWorlds Menu");
        
        ItemStack exit = XMaterial.BARRIER.parseItem();
        ItemMeta meta = exit.getItemMeta();
        meta.setDisplayName("&cExit");
        exit.setItemMeta(meta);
        
        ItemStack save = XMaterial.MAGMA_CREAM.parseItem();
        meta = exit.getItemMeta();
        meta.setDisplayName("&6Save and exit");
        save.setItemMeta(meta);
        
        ItemStack allNormal = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
        meta = allNormal.getItemMeta();
        meta.setDisplayName("&3Set all worlds to &aENABLED");
        allNormal.setItemMeta(meta);
        
        ItemStack allDisabled = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
        meta = allDisabled.getItemMeta();
        meta.setDisplayName("&3Set all worlds to &cDISABLED");
        allDisabled.setItemMeta(meta);
        
        inv.setItem(50, allNormal);
        inv.setItem(51, allDisabled);
        inv.setItem(52, save);
        inv.setItem(53, exit);
        
        if(worlds.size() > MENU_SIZE){
            ItemStack back = XMaterial.ENDER_PEARL.parseItem();
            meta = back.getItemMeta();
            meta.setDisplayName("&2Previous page");
            back.setItemMeta(meta);
            
            ItemStack currentPage = new ItemStack(Material.BOOK, index+1);
            meta = currentPage.getItemMeta();
            meta.setDisplayName("&6Page &3" + (index+1) + " &a/ &3" + max_pages);
            currentPage.setItemMeta(meta);
            
            ItemStack next = XMaterial.ENDER_EYE.parseItem();
            meta = next.getItemMeta();
            meta.setDisplayName("&2Next page");
            next.setItemMeta(meta);
            
            inv.setItem(46, back);
            inv.setItem(47, currentPage);
            inv.setItem(48, next);
        }
        
        int n = Integer.min((WorldList.getWorlds().size()-MENU_SIZE*index),MENU_SIZE);
        List<String> worldsAux = new ArrayList<>();
        worlds.keySet().stream().sorted().forEachOrdered(w -> worldsAux.add(w));
        for(int i=0;i<n;i++){
            String worldName = worldsAux.get(i+index*MENU_SIZE);
            boolean worldType = worlds.get(worldName);
            
            ItemStack w = new ItemStack(getMaterialByType(worldType));
            meta = w.getItemMeta();
            meta.setDisplayName("&d" + worldName);
            meta.setLore(getLoreByType(worldType));
            w.setItemMeta(meta);
            
            inv.setItem(i, w);
        }
        
        p.openInventory(inv);
//</editor-fold>
    }

    private static Material getMaterialByType(boolean worldType){
        if(worldType){
            return XMaterial.EMERALD_BLOCK.parseMaterial();
        }else{
            return XMaterial.REDSTONE_BLOCK.parseMaterial();
        }
    }

    private static List<String> getLoreByType(boolean worldType){
        List<String> lore = new ArrayList<>();
        if(worldType){
            lore.add("&aENABLED WORLD");
        }else{
            lore.add("&cDISABLED WORLD");
        }
        
        return lore;
    }
    
    private static final InventoryFunction WORLD_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){

            switch(e.getSlot()){
                case 46:
                    //Back
                    if(worlds.size() > MENU_SIZE){
                        index = (index-1);
                        if(index < 0){
                            index = max_pages-1;
                        }
                        openWorldsMenu(p);
                    }
                    break;
                case 47:
                    //Go to main page
                    if(worlds.size() > MENU_SIZE){
                        index = 0;
                        openWorldsMenu(p);
                    }
                    break;
                case 48:
                    //Next
                    if(worlds.size() > MENU_SIZE){
                        index = (index+1);
                        if(index >= max_pages){
                            index = 0;
                        }
                        openWorldsMenu(p);
                    }
                    break;
                case 50:
                    //Set all worlds to ENABLED
                    worlds.replaceAll((worldName, enabled) -> true);
                    openWorldsMenu(p);
                    break;
                case 51:
                    //Set all worlds to DISABLED
                    worlds.replaceAll((worldName, enabled) -> false);
                    openWorldsMenu(p);
                    break;
                case 52:
                    //Save worlds to config
                    WorldList.setWorlds(worlds);
                    WorldList.saveWorlds();
                    p.closeInventory();
                    p.sendMessage(Logger.color("&aWorlds list has been saved"));
                    break;
                case 53:
                    p.closeInventory();
                    break;
                default:
                    if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
                        String old_type = Logger.stripColor(e.getCurrentItem().getItemMeta().getLore().get(0));
                        boolean new_type;
                        switch(old_type){
                            case "ENABLED WORLD":
                                new_type = false;
                                break;
                            default:
                                new_type = true;
                        }

                        String worldName = Logger.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                        worlds.put(worldName, new_type);
                        openWorldsMenu(p);
                    }
            }
        }
//</editor-fold>
    };
}
