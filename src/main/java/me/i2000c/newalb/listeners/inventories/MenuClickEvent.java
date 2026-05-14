package me.i2000c.newalb.listeners.inventories;

import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.experimental.Delegate;
import me.i2000c.newalb.api.gui.InventoryLocation;

public class MenuClickEvent {
    
    @Delegate private final InventoryClickEvent event;
    @Getter private final InventoryLocation location;
    @Getter private final boolean topInventory;
    @Getter private final boolean bottomInventory;
    @Getter private final boolean noneInventory;
    @Getter private final boolean emptyItem;
    @Getter private final boolean emptyCursor;
    @Getter private final Player player;
    
    public MenuClickEvent(InventoryClickEvent event) {
        this.event = event;
        
        InventoryView view = event.getView();        
        if(view == null) {
            this.location = InventoryLocation.NONE;
        } else {
            Inventory inventory = event.getClickedInventory();
            if(Objects.equals(inventory, view.getTopInventory())) {
                this.location = InventoryLocation.TOP;
            } else if(Objects.equals(inventory, view.getBottomInventory())) {
                this.location = InventoryLocation.BOTTOM;
            } else {
                this.location = InventoryLocation.NONE;
            }
        }
        
        this.topInventory = this.location == InventoryLocation.TOP;
        this.bottomInventory = this.location == InventoryLocation.BOTTOM;
        this.noneInventory = this.location == InventoryLocation.NONE;
        
        ItemStack currentItem = event.getCurrentItem();
        this.emptyItem = currentItem == null || currentItem.getType() == Material.AIR;
        
        ItemStack cursor = event.getCursor();
        this.emptyCursor = cursor == null || cursor.getType() == Material.AIR;
        
        this.player = (Player) event.getWhoClicked();
    }
}
