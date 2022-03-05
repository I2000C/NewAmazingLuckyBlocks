package me.i2000c.newalb.listeners.inventories;

import java.util.EnumMap;
import java.util.Map;
import me.i2000c.newalb.utils.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

//https://www.spigotmc.org/threads/detecting-custom-inventories-without-using-titles.517234/
public class InventoryListener implements Listener{    
    private static final Map<CustomInventoryType, InventoryFunction> inventoriesToNotify = new EnumMap<>(CustomInventoryType.class);
    
    public static void registerInventory(CustomInventoryType type, InventoryFunction function){
        inventoriesToNotify.put(type, function);
    }
    
    public static void removeInventory(CustomInventoryType type){
        inventoriesToNotify.remove(type);
    }
    
    @EventHandler
    private static void onInventoryClick(InventoryClickEvent e){
        if(e.getView() == null || e.getView().getTitle() == null || e.getClickedInventory() == null){
            return;
        }
        
        InventoryHolder topHolder = e.getView().getTopInventory().getHolder();
        if(topHolder != null && topHolder instanceof GUIFactory){
            GUIFactory holder = (GUIFactory) topHolder;
            CustomInventoryType inventoryType = holder.getInventoryType();
            InventoryFunction function = inventoriesToNotify.get(inventoryType);
            if(function != null){
                function.execute(e);
            }
        }
    }
}
