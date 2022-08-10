package me.i2000c.newalb.utils;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
        
        ItemStack exit = ItemBuilder.newItem(XMaterial.BARRIER)
                .withDisplayName("&cExit")
                .build();
        
        ItemStack save = ItemBuilder.newItem(XMaterial.MAGMA_CREAM)
                .withDisplayName("&6Save and exit")
                .build();
        
        ItemStack allNormal = ItemBuilder.newItem(XMaterial.LIME_STAINED_GLASS_PANE)
                .withDisplayName("&3Set all worlds to &aENABLED")
                .build();
        
        ItemStack allDisabled = ItemBuilder.newItem(XMaterial.RED_STAINED_GLASS_PANE)
                .withDisplayName("&3Set all worlds to &cDISABLED")
                .build();
        
        inv.setItem(50, allNormal);
        inv.setItem(51, allDisabled);
        inv.setItem(52, save);
        inv.setItem(53, exit);
        
        if(worlds.size() > MENU_SIZE){            
            inv.setItem(46, GUIItem.getPreviousPageItem());
            inv.setItem(47, GUIItem.getCurrentPageItem(index+1, max_pages));
            inv.setItem(48, GUIItem.getNextPageItem());
        }
        
        int n = Integer.min((WorldList.getWorlds().size()-MENU_SIZE*index),MENU_SIZE);
        List<String> worldsAux = new ArrayList<>();
        worlds.keySet().stream().sorted().forEachOrdered(w -> worldsAux.add(w));
        for(int i=0;i<n;i++){
            String worldName = worldsAux.get(i+index*MENU_SIZE);
            boolean worldType = worlds.get(worldName);
            
            Material material = getMaterialByType(worldType);
            ItemStack worldItem = ItemBuilder.newItem(XMaterial.matchXMaterial(material))
                    .withDisplayName("&d" + worldName)
                    .withLore(getLoreByType(worldType))
                    .build();
            
            inv.setItem(i, worldItem);
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
                        ItemBuilder builder = ItemBuilder.fromItem(e.getCurrentItem(), false);
                        
                        String displayName = builder.getDisplayName();
                        List<String> lore = builder.getLore();
                        
                        String old_type = Logger.stripColor(lore.get(0));
                        boolean new_type;
                        switch(old_type){
                            case "ENABLED WORLD":
                                new_type = false;
                                break;
                            default:
                                new_type = true;
                        }

                        String worldName = Logger.stripColor(displayName);
                        worlds.put(worldName, new_type);
                        openWorldsMenu(p);
                    }
            }
        }
//</editor-fold>
    };
}
