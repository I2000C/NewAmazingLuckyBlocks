package me.i2000c.newalb.listeners.inventories;

import org.bukkit.event.inventory.InventoryClickEvent;

@FunctionalInterface
public interface InventoryFunction{
    public void execute(InventoryClickEvent e);
}
