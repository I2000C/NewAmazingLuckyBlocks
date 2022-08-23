package me.i2000c.newalb.listeners.inventories;

import me.i2000c.newalb.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class GUIFactory implements InventoryHolder{
    
    private final CustomInventoryType inventoryType;
    private final Inventory inventory;

    public GUIFactory(CustomInventoryType inventoryType, int size, String title){
        this.inventory = Bukkit.createInventory(this, size, Logger.color(title));
        this.inventoryType = inventoryType;
    }
    
    public static Menu newMenu(CustomInventoryType inventoryType, int size, String title){
        GUIFactory factory = new GUIFactory(inventoryType, size, title);
        return new Menu(factory.getInventory());
    }

    @Override
    public Inventory getInventory(){
        return this.inventory;
    }

    public CustomInventoryType getInventoryType(){
        return this.inventoryType;
    }
}
