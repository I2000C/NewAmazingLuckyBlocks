package me.i2000c.newalb.api.gui;

import java.util.Optional;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.NonNull;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

@Getter
public class MenuItem {
    @NonNull
    private final ItemStackWrapper wrapper;
    
    @NonNull
    private final Optional<Consumer<MenuClickEvent>> action;
    
    public MenuItem(ItemStackWrapper wrapper, Consumer<MenuClickEvent> event) {
        this.wrapper = wrapper;
        this.action = Optional.ofNullable(event);
    }
    
    public MenuItem(ItemStackWrapper wrapper) {
        this(wrapper, null);
    }
}
