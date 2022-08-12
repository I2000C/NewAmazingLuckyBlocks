package me.i2000c.newalb.listeners.inventories;

import java.util.List;
import java.util.ListIterator;
import java.util.function.BiFunction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUIPagesAdapter<T>{
    private final int menuSize;    
    private final BiFunction<T, Integer, ItemStack> itemFunction;
    private List<T> itemList;
    
    private int maxPages;
    private boolean showPageItems;
    private int pageIndex;
    
    private int previousPageSlot;
    private int currentPageSlot;
    private int nextPageSlot;
    
    public GUIPagesAdapter(
            int menuSize, 
            BiFunction<T, Integer, ItemStack> itemFunction){        
        this.menuSize = menuSize;        
        this.itemFunction = itemFunction;
        this.itemList = null;
        
        this.pageIndex = 0;
        this.previousPageSlot = -1;
        this.currentPageSlot = -1;
        this.nextPageSlot = -1;
    }
    
    public void setItemList(List<T> itemList){
        this.itemList = itemList;
        this.showPageItems = itemList.size() > this.menuSize;
        
        if(itemList.size() % menuSize == 0){
            this.maxPages = itemList.size() / menuSize;
        }else{
            this.maxPages = itemList.size() / menuSize + 1;
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
    
    public void updateMenu(Inventory inv){
        if(previousPageSlot < 0){
            throw new IllegalArgumentException("Previous page slot is invalid");
        }
        if(currentPageSlot < 0){
            throw new IllegalArgumentException("Current page slot is invalid");
        }
        if(nextPageSlot < 0){
            throw new IllegalArgumentException("Next page slot is invalid");
        }
        
        if(menuSize > inv.getSize()){
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
            inv.setItem(previousPageSlot, GUIItem.getPreviousPageItem());
            inv.setItem(currentPageSlot, GUIItem.getCurrentPageItem(pageIndex+1, maxPages));
            inv.setItem(nextPageSlot, GUIItem.getNextPageItem());
        }
        
        int firstItemIndex = menuSize*pageIndex;
        if(firstItemIndex < 0 || firstItemIndex >= itemList.size()){
            return;
        }
        
        ListIterator<T> iterator = itemList.listIterator(firstItemIndex);
        for(int i=0; i<menuSize && iterator.hasNext(); i++){
            int index = iterator.nextIndex();
            T item = iterator.next();
            ItemStack stack = itemFunction.apply(item, index);
            inv.setItem(i, stack);
        }
    }
}
