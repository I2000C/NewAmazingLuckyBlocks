package me.i2000c.newalb.listeners.inventories;

import com.google.common.base.Objects;

import me.i2000c.newalb.api.gui.InventoryLocation;

import java.util.List;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class CustomInventoryClickEvent{
    private final InventoryClickEvent event;
    private final InventoryLocation location;
    
    public CustomInventoryClickEvent(InventoryClickEvent e){
        this.event = e;
        InventoryView view = e.getView();        
        if(view == null){
            this.location = InventoryLocation.NONE;
        }else{
            Inventory inventory = e.getClickedInventory();
            if(Objects.equal(inventory, view.getTopInventory())){
                this.location = InventoryLocation.TOP;
            }else if(Objects.equal(inventory, view.getBottomInventory())){
                this.location = InventoryLocation.BOTTOM;
            }else{
                this.location = InventoryLocation.NONE;
            }
        }
    }
    
    public InventoryLocation getLocation(){
        return this.location;
    }

    public Inventory getClickedInventory() {
        return event.getClickedInventory();
    }

    public org.bukkit.event.inventory.InventoryType.SlotType getSlotType() {
        return event.getSlotType();
    }

    public ItemStack getCursor() {
        return event.getCursor();
    }

    public ItemStack getCurrentItem() {
        return event.getCurrentItem();
    }

    public boolean isRightClick() {
        return event.isRightClick();
    }

    public boolean isLeftClick() {
        return event.isLeftClick();
    }

    public boolean isShiftClick() {
        return event.isShiftClick();
    }

    public void setCursor(ItemStack stack) {
        event.setCursor(stack);
    }

    public void setCurrentItem(ItemStack stack) {
        event.setCurrentItem(stack);
    }

    public int getSlot() {
        return event.getSlot();
    }

    public int getRawSlot() {
        return event.getRawSlot();
    }

    public int getHotbarButton() {
        return event.getHotbarButton();
    }

    public InventoryAction getAction() {
        return event.getAction();
    }

    public ClickType getClick() {
        return event.getClick();
    }

    public HandlerList getHandlers() {
        return event.getHandlers();
    }

    public static HandlerList getHandlerList() {
        return InventoryClickEvent.getHandlerList();
    }

    public HumanEntity getWhoClicked() {
        return event.getWhoClicked();
    }

    public void setResult(Event.Result newResult) {
        event.setResult(newResult);
    }

    public Event.Result getResult() {
        return event.getResult();
    }

    public boolean isCancelled() {
        return event.isCancelled();
    }

    public void setCancelled(boolean toCancel) {
        event.setCancelled(toCancel);
    }

    public Inventory getInventory() {
        return event.getInventory();
    }

    public List<HumanEntity> getViewers() {
        return event.getViewers();
    }

    public InventoryView getView() {
        return event.getView();
    }

    public String getEventName() {
        return event.getEventName();
    }

    public final boolean isAsynchronous() {
        return event.isAsynchronous();
    }

    @Override
    public int hashCode() {
        return event.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return event.equals(obj);
    }

    @Override
    public String toString() {
        return event.toString();
    }
    
    
}
