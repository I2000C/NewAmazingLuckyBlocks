package me.i2000c.newalb.listeners.inventories;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

import me.i2000c.newalb.api.gui.menus.Menu;

// https://www.spigotmc.org/threads/detecting-custom-inventories-without-using-titles.517234/
public class InventoryListener implements Listener {
    
    @EventHandler
    private static void onInventoryClick(InventoryClickEvent event) {
        if(event.getView() == null || event.getView().getTitle() == null) {
            return;
        }
        
        InventoryHolder topHolder = event.getView().getTopInventory().getHolder();
        if(topHolder != null && topHolder instanceof Menu) {
            Menu menu = (Menu) topHolder;
            menu.onClick(new MenuClickEvent(event));
        }
    }
}
