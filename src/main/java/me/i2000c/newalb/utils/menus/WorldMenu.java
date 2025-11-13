package me.i2000c.newalb.utils.menus;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.List;

import me.i2000c.newalb.api.functions.InventoryFunction;
import me.i2000c.newalb.api.gui.CustomInventoryType;
import me.i2000c.newalb.api.gui.GUIFactory;
import me.i2000c.newalb.api.gui.GUIPagesAdapter;
import me.i2000c.newalb.api.gui.InventoryLocation;
import me.i2000c.newalb.api.gui.Menu;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.utils.locations.WorldManager;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WorldMenu{
    private static final int MENU_SIZE = 36;
    private static final int PREVIOUS_PAGE_SLOT = 40;
    private static final int CURRENT_PAGE_SLOT = 41;
    private static final int NEXT_PAGE_SLOT = 42;
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
                        boolean isWorldInList = WorldManager.isContained(worldName);
                        
                        ItemStackWrapper builder;
                        if(isWorldInList){
                            builder = ItemStackWrapper.newItem(XMaterial.EMERALD_BLOCK);
                            builder.addLoreLine("");
                            builder.addLoreLine("&6This world &ais contained &6in the");
                            builder.addLoreLine("&6worlds list of NewAmazingLuckyBlocks");
                            builder.addLoreLine("");
                            builder.addLoreLine("&3Click to &cdelete &3it from the list");
                        }else{
                            builder = ItemStackWrapper.newItem(XMaterial.REDSTONE_BLOCK);
                            builder.addLoreLine("");
                            builder.addLoreLine("&6This world &cisn't contained &6in the");
                            builder.addLoreLine("&6worlds list of NewAmazingLuckyBlocks");
                            builder.addLoreLine("");
                            builder.addLoreLine("&3Click to &aadd &3it to the list");
                        }
                        
                        builder.setDisplayName("&d" + worldName);
                        return builder.toItemStack();
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
        
        ItemStack exit = ItemStackWrapper.newItem(XMaterial.IRON_DOOR)
                                         .setDisplayName("&cExit")
                                         .toItemStack();
        
        ItemStack allNormal = ItemStackWrapper.newItem(XMaterial.LIME_STAINED_GLASS_PANE)
                                              .setDisplayName("&aAdd &3all worlds to list")
                                              .toItemStack();
        
        ItemStack allDisabled = ItemStackWrapper.newItem(XMaterial.RED_STAINED_GLASS_PANE)
                                                .setDisplayName("&cRemove &3all worlds from list")
                                                .toItemStack();
        
        ItemStack toggleAllWorlds = ItemStackWrapper.newItem(XMaterial.ORANGE_STAINED_GLASS_PANE)
                                                    .setDisplayName("&6Toggle &3all worlds")
                                                    .toItemStack();
        
        WorldManager.WorldListMode mode = WorldManager.getWorldListMode();
        ItemStackWrapper builder;
        switch(mode) {
            case DISABLED:
                builder = ItemStackWrapper.newItem(XMaterial.RED_WOOL);
                builder.addLoreLine("");
                builder.addLoreLine("&dIn this mode, the list is");
                builder.addLoreLine("&d  disabled, so LuckyBlocks");
                builder.addLoreLine("&d  are enabled in all worlds");
                break;
            case WHITELIST:
                builder = ItemStackWrapper.newItem(XMaterial.WHITE_WOOL);
                builder.addLoreLine("");
                builder.addLoreLine("&dIn this mode, LuckyBlocks");
                builder.addLoreLine("&d  will be enabled only");
                builder.addLoreLine("&d  in the worlds of the list");
                break;
            default: //case BLACKLIST:
                builder = ItemStackWrapper.newItem(XMaterial.BLACK_WOOL);
                builder.addLoreLine("");
                builder.addLoreLine("&dIn this mode, LuckyBlocks");
                builder.addLoreLine("&d  will be enabled only");
                builder.addLoreLine("&d  in the worlds that aren't");
                builder.addLoreLine("&d  in the list");
                break;
        }
        builder.setDisplayName("&6Current list mode: " + mode);
        builder.addLoreLine("");
        builder.addLoreLine("&3Click to change");
        
        ItemStack worldListMode = builder.toItemStack();
        
        menu.setItem(46, worldListMode);
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
                case 46:
                    // Change world list mode
                    WorldManager.setWorldListMode(WorldManager.getWorldListMode().next());
                    openWorldsMenu(p);
                    break;
                case 49:
                    // Add all worlds to list
                    WorldManager.addAllWorlds();
                    openWorldsMenu(p);
                    break;
                case 50:
                    // Toggle all worlds
                    WorldManager.toggleAllWorlds();
                    openWorldsMenu(p);
                    break;
                case 51:
                    // Delete all worlds from the list
                    WorldManager.deleteAllWorlds();
                    openWorldsMenu(p);
                    break;
                case 53:
                    p.closeInventory();
                    break;
                default:
                    if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
                        ItemStackWrapper builder = ItemStackWrapper.fromItem(e.getCurrentItem(), false);
                        
                        String worldName = Logger.stripColor(builder.getDisplayName());
                        if(WorldManager.isContained(worldName)) {
                            WorldManager.deleteWorld(worldName);
                        } else {
                            WorldManager.addWorld(worldName);
                        }
                        
                        openWorldsMenu(p);
                    }
            }
        }
//</editor-fold>
    };
}
