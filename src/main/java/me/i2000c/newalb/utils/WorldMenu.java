package me.i2000c.newalb.utils;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.List;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIPagesAdapter;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WorldMenu{
    private static final int MENU_SIZE = 36;
    private static final int PREVIOUS_PAGE_SLOT = 42;
    private static final int CURRENT_PAGE_SLOT = 43;
    private static final int NEXT_PAGE_SLOT = 44;
    private static GUIPagesAdapter<String> adapter;
    
    private static List<String> serverWorlds;
    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.WORLD_MENU, WORLD_MENU_FUNCTION);
            
            serverWorlds = new ArrayList<>();
            
            adapter = new GUIPagesAdapter<>(
                    MENU_SIZE,
                    (worldName, index) -> {
                        boolean isWorldInList = WorldConfig.isContained(worldName);
                        
                        ItemBuilder builder;
                        if(isWorldInList){
                            builder = ItemBuilder.newItem(XMaterial.EMERALD_BLOCK);
                            builder.addLoreLine("");
                            builder.addLoreLine("&6This world &ais contained &6in the");
                            builder.addLoreLine("&6worlds list of NewAmazingLuckyBlocks");
                            builder.addLoreLine("");
                            builder.addLoreLine("&3Click to &cdelete &3it from the list");
                        }else{
                            builder = ItemBuilder.newItem(XMaterial.REDSTONE_BLOCK);
                            builder.addLoreLine("");
                            builder.addLoreLine("&6This world &cisn't contained &6in the");
                            builder.addLoreLine("&6worlds list of NewAmazingLuckyBlocks");
                            builder.addLoreLine("");
                            builder.addLoreLine("&3Click to &aadd &3it to the list");
                        }
                        
                        builder.withDisplayName("&d" + worldName);
                        return builder.build();
                    }
            );
            adapter.setPreviousPageSlot(PREVIOUS_PAGE_SLOT);
            adapter.setCurrentPageSlot(CURRENT_PAGE_SLOT);
            adapter.setNextPageSlot(NEXT_PAGE_SLOT);
            
            serverWorlds.clear();
            Bukkit.getWorlds().forEach(world -> {
                serverWorlds.add(world.getName());
            });
            
            inventoriesRegistered = true;
        }
        
        adapter.goToMainPage();
//</editor-fold>
    }
    
    public static void openWorldsMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.WORLD_MENU, 54, "&3&lWorlds Menu");
        
        ItemStack exit = ItemBuilder.newItem(XMaterial.IRON_DOOR)
                .withDisplayName("&cExit")
                .build();
        
        ItemStack allNormal = ItemBuilder
                .newItem(XMaterial.LIME_STAINED_GLASS_PANE)
                .withDisplayName("&aAdd &3all worlds to list")
                .build();
        
        ItemStack allDisabled = ItemBuilder
                .newItem(XMaterial.RED_STAINED_GLASS_PANE)
                .withDisplayName("&cRemove &3all worlds from list")
                .build();
        
        ItemStack toggleAllWorlds = ItemBuilder
                .newItem(XMaterial.ORANGE_STAINED_GLASS_PANE)
                .withDisplayName("&6Toggle &3all worlds")
                .build();
        
        menu.setItem(49, allNormal);
        menu.setItem(50, toggleAllWorlds);
        menu.setItem(51, allDisabled);
        menu.setItem(53, exit);
        
        adapter.setItemList(serverWorlds);
        adapter.updateMenu(menu);
        menu.openToPlayer(player, false);
//</editor-fold>
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
                    // Add all worlds to list
                    WorldConfig.addAllWorlds();
                    openWorldsMenu(p);
                    break;
                case 50:
                    // Toggle all worlds
                    WorldConfig.toggleAllWorlds();
                    openWorldsMenu(p);
                    break;
                case 51:
                    // Delete all worlds from the list
                    WorldConfig.deleteAllWorlds();
                    openWorldsMenu(p);
                    break;
                case 53:
                    p.closeInventory();
                    break;
                default:
                    if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
                        ItemBuilder builder = ItemBuilder.fromItem(e.getCurrentItem(), false);
                        
                        String worldName = Logger.stripColor(builder.getDisplayName());
                        if(WorldConfig.isContained(worldName)) {
                            WorldConfig.deleteWorld(worldName);
                        } else {
                            WorldConfig.addWorld(worldName);
                        }
                        
                        openWorldsMenu(p);
                    }
            }
        }
//</editor-fold>
    };
}
