package me.i2000c.newalb.utils.menus;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.MenuItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.PaginatedMenu;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.utils.locations.WorldManager;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class WorldMenu extends PaginatedMenu<String> {
    
    public WorldMenu() {
        super("&3&lWorlds Menu", MenuSize.SIZE_6_ROWS, false, MenuSize.SIZE_4_ROWS, 40, 41, 42);
    }
    
    @Override
    public List<String> getItemList() {
        List<String> serverWorlds = Bukkit.getWorlds()
                                          .stream()
                                          .map(World::getName)
                                          .collect(Collectors.toList());
        return serverWorlds;
    }
    
    @Override
    public MenuItem mapItemToPage(String worldName, int index) {
        boolean isWorldInList = WorldManager.isContained(worldName);
        
        ItemStackWrapper wrapper;
        if(isWorldInList) {
            wrapper = ItemStackWrapper.newItem(XMaterial.EMERALD_BLOCK);
            wrapper.addLoreLine("");
            wrapper.addLoreLine("&6This world &ais contained &6in the");
            wrapper.addLoreLine("&6worlds list of NewAmazingLuckyBlocks");
            wrapper.addLoreLine("");
            wrapper.addLoreLine("&3Click to &cdelete &3it from the list");
        } else {
            wrapper = ItemStackWrapper.newItem(XMaterial.REDSTONE_BLOCK);
            wrapper.addLoreLine("");
            wrapper.addLoreLine("&6This world &cisn't contained &6in the");
            wrapper.addLoreLine("&6worlds list of NewAmazingLuckyBlocks");
            wrapper.addLoreLine("");
            wrapper.addLoreLine("&3Click to &aadd &3it to the list");
        }
        
        wrapper.setDisplayName("&d" + worldName);
        
        Consumer<MenuClickEvent> action = e -> {
            WorldManager.toggleWorld(worldName);
            openToPlayer(e.getPlayer());
        };
        
        return new MenuItem(wrapper, action);
    }
    
    @Override
    protected void buildMenu(Player player) {
        ItemStack exit = ItemStackWrapper.newItem(XMaterial.IRON_DOOR)
                                         .setDisplayName("&cExit")
                                         .toItemStack();
        
        ItemStack allEnabled = ItemStackWrapper.newItem(XMaterial.LIME_STAINED_GLASS_PANE)
                                              .setDisplayName("&aAdd &3all worlds to list")
                                              .toItemStack();
        
        ItemStack allDisabled = ItemStackWrapper.newItem(XMaterial.RED_STAINED_GLASS_PANE)
                                                .setDisplayName("&cRemove &3all worlds from list")
                                                .toItemStack();
        
        ItemStack toggleAllWorlds = ItemStackWrapper.newItem(XMaterial.ORANGE_STAINED_GLASS_PANE)
                                                    .setDisplayName("&6Toggle &3all worlds")
                                                    .toItemStack();
        
        WorldManager.WorldListMode mode = WorldManager.getWorldListMode();
        ItemStackWrapper wrapper;
        switch(mode) {
            case DISABLED:
                wrapper = ItemStackWrapper.newItem(XMaterial.RED_WOOL);
                wrapper.addLoreLine("");
                wrapper.addLoreLine("&dIn this mode, the list is");
                wrapper.addLoreLine("&d  disabled, so LuckyBlocks");
                wrapper.addLoreLine("&d  are enabled in all worlds");
                break;
            case WHITELIST:
                wrapper = ItemStackWrapper.newItem(XMaterial.WHITE_WOOL);
                wrapper.addLoreLine("");
                wrapper.addLoreLine("&dIn this mode, LuckyBlocks");
                wrapper.addLoreLine("&d  will be enabled only");
                wrapper.addLoreLine("&d  in the worlds of the list");
                break;
            default: //case BLACKLIST:
                wrapper = ItemStackWrapper.newItem(XMaterial.BLACK_WOOL);
                wrapper.addLoreLine("");
                wrapper.addLoreLine("&dIn this mode, LuckyBlocks");
                wrapper.addLoreLine("&d  will be enabled only");
                wrapper.addLoreLine("&d  in the worlds that aren't");
                wrapper.addLoreLine("&d  in the list");
                break;
        }
        wrapper.setDisplayName("&6Current list mode: " + mode);
        wrapper.addLoreLine("");
        wrapper.addLoreLine("&3Click to change");
        
        ItemStack worldListMode = wrapper.toItemStack();
        
        setItem(46, worldListMode, e -> {
            // Change world list mode
            WorldManager.setWorldListMode(WorldManager.getWorldListMode().next());
            openToPlayer(player);
        });
        
        setItem(49, allEnabled, e -> {
            // Add all worlds to list
            WorldManager.addAllWorlds();
            openToPlayer(player);
        });
        
        setItem(50, toggleAllWorlds, e -> {
            // Toggle all worlds
            WorldManager.toggleAllWorlds();
            openToPlayer(player);
        });
        
        setItem(51, allDisabled, e -> {
            // Delete all worlds from the list
            WorldManager.deleteAllWorlds();
            openToPlayer(player);
        });
        
        setItem(53, exit, e -> player.closeInventory());
    }
}
