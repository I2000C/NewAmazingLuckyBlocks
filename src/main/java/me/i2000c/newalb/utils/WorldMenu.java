package me.i2000c.newalb.utils;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIPagesAdapter;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WorldMenu{
    private static final int MENU_SIZE = 36;
    private static final int PREVIOUS_PAGE_SLOT = 42;
    private static final int CURRENT_PAGE_SLOT = 43;
    private static final int NEXT_PAGE_SLOT = 44;
    private static GUIPagesAdapter<String> adapter;
    
    private static boolean inventoriesRegistered = false;
    
    private static Map<String, Boolean> worlds;
    
    public static void reset(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.WORLD_MENU, WORLD_MENU_FUNCTION);
            
            adapter = new GUIPagesAdapter<>(
                    MENU_SIZE,
                    (worldName, index) -> {
                        boolean worldType = worlds.get(worldName);
                        Material material = getMaterialByType(worldType);
                        return ItemBuilder
                                .newItem(XMaterial.matchXMaterial(material))
                                .withDisplayName("&d" + worldName)
                                .withLore(getLoreByType(worldType))
                                .addLoreLine("&3Click to toggle")
                                .build();
                    }
            );
            adapter.setPreviousPageSlot(PREVIOUS_PAGE_SLOT);
            adapter.setCurrentPageSlot(CURRENT_PAGE_SLOT);
            adapter.setNextPageSlot(NEXT_PAGE_SLOT);
            
            inventoriesRegistered = true;
        }
        
        adapter.goToMainPage();
        
        worlds = new LinkedHashMap<>(WorldConfig.getWorlds());
        adapter.setItemList(new ArrayList<>(worlds.keySet()));
//</editor-fold>
    }
    
    public static void openWorldsMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        WorldConfig.updateWorlds(false);
        
        Menu menu = GUIFactory.newMenu(CustomInventoryType.WORLD_MENU, 54, "&3&lWorlds Menu");
        
        ItemStack exit = ItemBuilder.newItem(XMaterial.BARRIER)
                .withDisplayName("&cExit")
                .build();
        
        ItemStack save = ItemBuilder.newItem(XMaterial.MAGMA_CREAM)
                .withDisplayName("&6Save and exit")
                .build();
        
        ItemStack allNormal = ItemBuilder
                .newItem(XMaterial.LIME_STAINED_GLASS_PANE)
                .withDisplayName("&3Set all worlds to &aENABLED")
                .build();
        
        ItemStack allDisabled = ItemBuilder
                .newItem(XMaterial.RED_STAINED_GLASS_PANE)
                .withDisplayName("&3Set all worlds to &cDISABLED")
                .build();
        
        ItemStack toggleAllWorlds = ItemBuilder
                .newItem(XMaterial.ORANGE_STAINED_GLASS_PANE)
                .withDisplayName("&3Toggle all worlds")
                .build();
        
        menu.setItem(49, allNormal);
        menu.setItem(50, toggleAllWorlds);
        menu.setItem(51, allDisabled);
        menu.setItem(52, save);
        menu.setItem(53, exit);
        
        adapter.updateMenu(menu);
        menu.openToPlayer(player, false);
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
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case PREVIOUS_PAGE_SLOT:
                    // Go to previous page
                    if(adapter.goToPreviousPage()){
                        openWorldsMenu(p);
                    }                    
                    break;
                case CURRENT_PAGE_SLOT:
                    // Go to main page
                    if(adapter.goToMainPage()){
                        openWorldsMenu(p);
                    }
                    break;
                case NEXT_PAGE_SLOT:
                    // Go to next page
                    if(adapter.goToNextPage()){
                        openWorldsMenu(p);
                    }
                    break;
                case 49:
                    //Set all worlds to ENABLED
                    worlds.replaceAll((worldName, enabled) -> true);
                    openWorldsMenu(p);
                    break;
                case 50:
                    //Toggle all worlds
                    worlds.replaceAll((worldName, enabled) -> !enabled);
                    openWorldsMenu(p);
                    break;
                case 51:
                    //Set all worlds to DISABLED
                    worlds.replaceAll((worldName, enabled) -> false);
                    openWorldsMenu(p);
                    break;
                case 52:
                    //Save worlds to config
                    WorldConfig.setWorlds(worlds);
                    WorldConfig.saveWorlds();
                    p.closeInventory();
                    Logger.sendMessage("&aWorlds list has been saved", p);
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
