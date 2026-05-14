package me.i2000c.newalb.api.gui.menus;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.bukkit.entity.Player;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Tolerate;
import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.MenuItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.misc.OtherUtils;

@RequiredArgsConstructor
public class Paginator<T> {
    
    private static final int ROW_SIZE = MenuSize.SIZE_1_ROW.getSize();
    
    private final Menu menu;
    private final MenuSize paginatedMenuSize;
    private final int previousPageSlot;
    private final int currentPageSlot;
    private final int nextPageSlot;
    
    private int pageIndex = 0;
    private int numPages = 1;
    
    private BitSet cachedIgnoredColumns;
    private List<T> cachedItemList;
    private int itemsPerPage;
    private boolean showPageItems;
    
    @Setter
    private Supplier<List<T>> itemListSupplier = () -> Collections.emptyList();
    
    @Setter
    private BiFunction<T, Integer, MenuItem> itemMapper = (item, index) -> null;
    
    @Setter
    private Predicate<Integer> columnsToIgnore = column -> false;
    
    @Tolerate
    public Paginator(Menu menu, MenuSize paginatedMenuSize) {
        this(menu, paginatedMenuSize, 
                paginatedMenuSize.getSize() + MenuSize.SIZE_1_ROW.getSize() - 3,
                paginatedMenuSize.getSize() + MenuSize.SIZE_1_ROW.getSize() - 2,
                paginatedMenuSize.getSize() + MenuSize.SIZE_1_ROW.getSize() - 1);
    }
    
    public void goToNextPage() {
        pageIndex = OtherUtils.addInRange(pageIndex, 1, 0, numPages);
    }
    
    public void goToPreviousPage() {
        pageIndex = OtherUtils.subInRange(pageIndex, 1, 0, numPages);
    }
    
    public void goToMainPage() {
        pageIndex = 0;
    }
    
    public void resetPagination() {
        this.cachedItemList = null;
        this.cachedIgnoredColumns = null;
    }
    
    public void buildPagination(Player player) {
        if(paginatedMenuSize.getSize() >= menu.getMenuSize().getSize()) {
            throw new IllegalArgumentException("Paginated menu size must be less than menu size");
        }
        
        if(cachedItemList == null || cachedIgnoredColumns == null) {
            cachedItemList = itemListSupplier.get();
            
            cachedIgnoredColumns = new BitSet(ROW_SIZE);
            for(int i=0; i<ROW_SIZE; i++) {
                if(columnsToIgnore.test(i)) {
                    cachedIgnoredColumns.set(i);
                }
            }
            
            int rowNumber = paginatedMenuSize.getSize() / ROW_SIZE;
            itemsPerPage = rowNumber * (ROW_SIZE - cachedIgnoredColumns.cardinality());
            showPageItems = cachedItemList.size() > itemsPerPage;
            
            numPages = cachedItemList.size() / itemsPerPage;
            if(numPages == 0 || cachedItemList.size() % itemsPerPage != 0) {
                numPages++;
            }
            
            if(pageIndex >= numPages) {
                // Set page index to last page
                pageIndex = numPages - 1;
            }
        }
        
        int firstItemIndex = itemsPerPage * pageIndex;
        if(firstItemIndex < 0 || firstItemIndex >= cachedItemList.size()) {
            return;
        }
        
        ListIterator<T> iterator = cachedItemList.listIterator(firstItemIndex);
        for(int slot=0; slot<paginatedMenuSize.getSize() && iterator.hasNext(); slot++) {
            int column = slot % ROW_SIZE;
            if(!cachedIgnoredColumns.get(column)) {
                int index = iterator.nextIndex();
                T item = iterator.next();
                MenuItem menuItem = itemMapper.apply(item, index);
                if(menuItem != null) {
                    ItemStackWrapper wrapper = menuItem.getWrapper();
                    menu.setItem(slot, wrapper.toItemStack());
                    if(menuItem.getAction().isPresent()) {
                        menu.setClickAction(slot, menuItem.getAction().get());
                    }
                }
            }
        }
        
        if(showPageItems) {
            menu.setItem(previousPageSlot, GUIItem.getPreviousPageItem(), e -> {
                if(e.isEmptyCursor()) {
                    goToPreviousPage();
                    menu.openToPlayer(player);
                }
            });
            menu.setItem(currentPageSlot, GUIItem.getCurrentPageItem(pageIndex + 1, numPages), e -> {
                if(e.isEmptyCursor()) {
                    goToMainPage();
                    menu.openToPlayer(player);
                    
                }
            });
            menu.setItem(nextPageSlot, GUIItem.getNextPageItem(), e -> {
                if(e.isEmptyCursor()) {
                    goToNextPage();
                    menu.openToPlayer(player);
                }
            });
        }
    }
}
