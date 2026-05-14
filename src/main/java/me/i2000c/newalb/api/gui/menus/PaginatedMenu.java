package me.i2000c.newalb.api.gui.menus;

import org.bukkit.entity.Player;

import lombok.experimental.Delegate;
import me.i2000c.newalb.api.gui.MenuSize;

public abstract class PaginatedMenu<T> extends Menu implements Pageable<T> {
    
    @Delegate
    private final Paginator<T> paginator;
    
    public PaginatedMenu(String menuTitle, MenuSize menuSize, boolean trackLastMenu, 
    		MenuSize paginatedMenuSize, int previousPageSlot, int currentPageSlot, int nextPageSlot) {
        super(menuTitle, menuSize, trackLastMenu);
        if(previousPageSlot > 0 && currentPageSlot > 0 && nextPageSlot > 0) {
            this.paginator = new Paginator<>(this, paginatedMenuSize, previousPageSlot, currentPageSlot, nextPageSlot);
        } else {
            this.paginator = new Paginator<>(this, paginatedMenuSize);
        }
        
        this.paginator.setItemListSupplier(this::getItemList);
        this.paginator.setItemMapper(this::mapItemToPage);
        this.paginator.setColumnsToIgnore(this::ignoreColumn);
    }
    
    public PaginatedMenu(String menuTitle, MenuSize menuSize, boolean trackLastMenu, MenuSize paginatedMenuSize) {
        this(menuTitle, menuSize, trackLastMenu, paginatedMenuSize, -1, -1, -1);
    }
    
    @Override
    protected void onPreOpen(Player player) {
        super.onPreOpen(player);
        paginator.buildPagination(player);
    }
}
