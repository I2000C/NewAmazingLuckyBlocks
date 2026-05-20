package me.i2000c.newalb.listeners.inventories;

import java.util.Set;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

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
    
    @EventHandler
    private static void onInventoryDrag(InventoryDragEvent event) {
        if(event.getView() == null || event.getView().getTitle() == null) {
            return;
        }
        
        InventoryHolder topHolder = event.getView().getTopInventory().getHolder();
        if(topHolder != null && topHolder instanceof Menu) {
            Set<Integer> slots = event.getRawSlots();
            if(slots.size() != 1) {
                event.setCancelled(true);
            } else {
                // This is required because there are some Minecraft clients that
                //  sometimes send InventoryDrag events when they click on a single item
                int slot = slots.iterator().next();
                InventoryClickEvent fakeClickEvent = new InventoryClickEvent(event.getView(), null, slot, ClickType.LEFT, null) {
                    @Override
                    public ItemStack getCursor() {
                        return event.getOldCursor();
                    }
                    
                    @Override
                    public boolean isCancelled() {
                        return event.isCancelled();
                    }
                    
                    @Override
                    public void setCancelled(boolean cancelled) {
                        event.setCancelled(cancelled);
                    }
                };
                onInventoryClick(fakeClickEvent);
            }
        }
    }
}
