package me.i2000c.newalb.api.gui.menus;

import java.util.function.Consumer;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import me.i2000c.newalb.api.functions.EditorNextFunction;
import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;

public abstract class EditorMenu<T> extends Menu {
    
    protected boolean isNewItem;
    
    protected T item;
    
    @Getter
    @Setter
    private EditorNextFunction<T> onNext;

    public EditorMenu(String title, MenuSize menuSize, boolean trackLastMenu) {
        super(title, menuSize, trackLastMenu);
        this.isNewItem = true;
        this.item = null;
        this.onNext = null;
    }
    
    public final void setItemToEdit(T item) {
        this.item = item;
        this.isNewItem = this.item == null;
    }
    
    @Override
    protected void onPreOpen(Player player) {
        if(item == null) {
            item = createNewItem();
        }
    }
    
    protected T createNewItem() {
        throw new UnsupportedOperationException();
    }
    
    protected final void onNext(Player player, T item) {
        if(onNext != null) {
            onNext.accept(player, item);
        }
    }
    
    protected final void setNextItem(int slot) {
        setItem(slot, GUIItem.getNextItem(), e -> {
            if(item != null) {
                if(e.isEmptyCursor()) {
                    onNext(e.getPlayer(), item);
                }
            }
        });
    }
    
    protected final void setNextItem(int slot, Consumer<MenuClickEvent> action) {
        setItem(slot, GUIItem.getNextItem(), e -> {
            if(e.isEmptyCursor()) {
                action.accept(e);
            }
        });
    }
}
