package me.i2000c.newalb.api.gui.menus;

import java.util.List;

import me.i2000c.newalb.api.gui.MenuItem;

public interface Pageable<T> {

    public void goToNextPage();
    
    public void goToPreviousPage();
    
    public void goToMainPage();
    
    public void resetPagination();
    
    public List<T> getItemList();
    
    public MenuItem mapItemToPage(T item, int index);
    
    public default boolean ignoreColumn(int column) {
        return false;
    }
}
