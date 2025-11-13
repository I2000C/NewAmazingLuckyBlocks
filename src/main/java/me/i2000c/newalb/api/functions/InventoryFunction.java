package me.i2000c.newalb.api.functions;

import java.util.function.Consumer;
import me.i2000c.newalb.listeners.inventories.CustomInventoryClickEvent;

@FunctionalInterface
public interface InventoryFunction extends Consumer<CustomInventoryClickEvent>{}
