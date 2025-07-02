package me.i2000c.newalb.listeners.inventories;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.function.BiFunction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

public class GUIPagesAdapter<T>{
    private static final int MAX_MENU_SIZE = 54;
    private static final int DEFAULT_ROW_SIZE = 9;
    
    private final int menuSize;    
    private final BiFunction<T, Integer, ItemStack> itemFunction;
    private List<T> itemList;
    private int itemsPerPage;
    
    private Set<Integer> ignoredColumns;
    
    private int maxPages;
    private boolean showPageItems;
    
    @Getter
    @Setter
    private int pageIndex;
    
    private int previousPageSlot;
    private int currentPageSlot;
    private int nextPageSlot;
    
    public GUIPagesAdapter(
            int menuSize, 
            BiFunction<T, Integer, ItemStack> itemFunction){
        if(menuSize <= 0 || menuSize > MAX_MENU_SIZE){
            throw new IllegalArgumentException("Menu size " + menuSize + " is out of range [1, " + MAX_MENU_SIZE + "]");
        }
        if(menuSize % 9 != 0){
            throw new IllegalArgumentException("Menu size must be a multiple of 9, not " + menuSize);
        }
        
        this.menuSize = menuSize;        
        this.itemFunction = itemFunction;
        this.itemList = null;
        this.itemsPerPage = 0;
        
        this.ignoredColumns = Collections.EMPTY_SET;
        
        this.pageIndex = 0;
        this.previousPageSlot = -1;
        this.currentPageSlot = -1;
        this.nextPageSlot = -1;
    }
    
    public void setIgnoredColumns(Integer... ignoredColumns){
        for(int ignoredColumn : ignoredColumns){
            if(ignoredColumn < 0 || ignoredColumn >= DEFAULT_ROW_SIZE){
                throw new IllegalArgumentException(
                        "Ignored column " + ignoredColumn + " is out of range [0, " + DEFAULT_ROW_SIZE + "]");
            }
        }
        
        this.ignoredColumns = new HashSet<>(Arrays.asList(ignoredColumns));
        if(this.itemList != null){
            setItemList(itemList);
        }
    }
    
    public void setItemList(List<T> itemList){
        this.itemList = itemList;
        
        int numberOfRows = this.menuSize / DEFAULT_ROW_SIZE;
        this.itemsPerPage = numberOfRows * (DEFAULT_ROW_SIZE - this.ignoredColumns.size());
        this.showPageItems = itemList.size() > itemsPerPage;
        
        if(itemList.size() % itemsPerPage == 0){
            this.maxPages = itemList.size() / itemsPerPage;
        }else{
            this.maxPages = itemList.size() / itemsPerPage + 1;
        }
    }
    
    public int getPreviousPageSlot(){
        return this.previousPageSlot;
    }
    public void setPreviousPageSlot(int previousPageSlot){
        this.previousPageSlot = previousPageSlot;
    }
    public int getCurrentPageSlot(){
        return this.currentPageSlot;
    }
    public void setCurrentPageSlot(int currentPageSlot){
        this.currentPageSlot = currentPageSlot;
    }
    public int getNextPageSlot(){
        return this.nextPageSlot;
    }
    public void setNextPageSlot(int nextPageSlot){
        this.nextPageSlot = nextPageSlot;
    }
    
    public boolean goToNextPage(){
        if(this.maxPages > 1){
            this.pageIndex++;
            if(this.pageIndex >= this.maxPages){
                this.pageIndex = 0;
            }
            return true;
        }else{
            return false;
        }            
    }    
    public boolean goToPreviousPage(){
        if(this.maxPages > 1){
            this.pageIndex--;
            if(this.pageIndex < 0){
                this.pageIndex = this.maxPages - 1;
            }
            return true;
        }else{
            return false;
        }            
    }    
    public boolean goToMainPage(){
        if(this.pageIndex == 0){
            return false;
        }else{
            this.pageIndex = 0;
            return true;
        }        
    }
    
    public void updateMenu(Menu menu){
        if(previousPageSlot < 0){
            throw new IllegalArgumentException("Previous page slot is invalid");
        }
        if(currentPageSlot < 0){
            throw new IllegalArgumentException("Current page slot is invalid");
        }
        if(nextPageSlot < 0){
            throw new IllegalArgumentException("Next page slot is invalid");
        }
        
        if(menuSize > menu.getSize()){
            return;
        }
        
        if(pageIndex >= maxPages){
            // If pageIndex is greater than maxPages, go to last page
            pageIndex = maxPages - 1;
        }else if(pageIndex < 0){
            // If pageIndex is lower than 0, go to first page
            pageIndex = 0;
        }
        
        if(showPageItems){
            menu.setItem(previousPageSlot, GUIItem.getPreviousPageItem());
            menu.setItem(currentPageSlot, GUIItem.getCurrentPageItem(pageIndex+1, maxPages));
            menu.setItem(nextPageSlot, GUIItem.getNextPageItem());
        }
        
        int firstItemIndex = itemsPerPage*pageIndex;
        if(firstItemIndex < 0 || firstItemIndex >= itemList.size()){
            return;
        }
        
        ListIterator<T> iterator = itemList.listIterator(firstItemIndex);
        int row = 0, column = 0;
        for(int slot=0; slot<menuSize && iterator.hasNext(); slot++){
            if(!ignoredColumns.contains(column)){
                int index = iterator.nextIndex();
                T item = iterator.next();
                ItemStack stack = itemFunction.apply(item, index);
                menu.setItem(slot, stack);
            }
            
            column++;
            if(column >= DEFAULT_ROW_SIZE){
                column = 0;
                row++;
            }
        }
    }
}
