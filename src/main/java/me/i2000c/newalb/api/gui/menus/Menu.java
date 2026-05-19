package me.i2000c.newalb.api.gui.menus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.i2000c.newalb.api.functions.PlayerConsumer;
import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuManager;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.utils.logging.Logger;

public abstract class Menu implements InventoryHolder {
    
    public static final int MAX_TITLE_LENGTH = 32;
    
    private boolean firstOpened = true;
    
    @NonNull
    private final Map<Integer, Consumer<MenuClickEvent>> actions;
    
    @NonNull
    private final ItemStack[] contents;
    
    @NonNull
    @Setter
    private String title;
    
    @NonNull
    @Getter
    private final MenuSize menuSize;
    
    private final boolean trackLastMenu;
    
    @Getter
    @Setter
    private PlayerConsumer onBack;
    
    @Getter
    private Inventory inventory;
    
    private String processTitle(String title) {
        title = Logger.color(Objects.toString(title, ""));
        if(title.length() > MAX_TITLE_LENGTH) {
            title = title.substring(0, MAX_TITLE_LENGTH-3).concat("...");
        }
        return title;
    }
    
    public Menu(@NonNull String title, @NonNull MenuSize menuSize, boolean trackLastMenu) {
        this.title = title;
        this.menuSize = menuSize;
        this.trackLastMenu = trackLastMenu;
        this.actions = new HashMap<>();
        this.contents = new ItemStack[menuSize.getSize()];
    }
    
    protected abstract void buildMenu(Player player);
    
    protected boolean onClickStart(MenuClickEvent event) {
        event.setCancelled(true);
        return true;
    }
    
    protected void onClickEnd(MenuClickEvent event) {
        if(!event.isEmptyItem()) {
            event.getPlayer().updateInventory();
        }
    }
    
    protected void onClickDefault(MenuClickEvent event) { }
    
    protected void addGlassRow(GlassColor glassColor, int row) {
        ItemStack glassItem = GUIItem.getGlassItem(glassColor);
        int firstSlot = row*MenuSize.SIZE_1_ROW.getSize();
        int lastSlot = firstSlot + MenuSize.SIZE_1_ROW.getSize();
        for(int slot=firstSlot; slot<lastSlot; slot++) {
            if(getItem(slot) == null) {
                setItem(slot, glassItem);
            }
        }
    }
    
    protected void addGlassColumn(GlassColor glassColor, int column) {
        ItemStack glassItem = GUIItem.getGlassItem(glassColor);
        int rowSize = MenuSize.SIZE_1_ROW.getSize();
        for(int slot=column; slot<menuSize.getSize(); slot+=rowSize) {
            if(getItem(slot) == null) {
                setItem(slot, glassItem);
            }
        }
    }
    
    protected void addGlassFill(GlassColor glassColor) {
        ItemStack glassItem = GUIItem.getGlassItem(glassColor);
        for(int slot=0; slot<menuSize.getSize(); slot++) {
            if(getItem(slot) == null) {
                setItem(slot, glassItem);
            }
        }
    }
    
    protected void addHollowGlassSquare(GlassColor glassColor, int initSlot, int size) {
        ItemStack glassItem = GUIItem.getGlassItem(glassColor);
        int rowSize = MenuSize.SIZE_1_ROW.getSize();
        
        int firstRow = initSlot / rowSize;
        int firstCol = initSlot % rowSize;
        int lastRow = firstRow + size - 1;
        int lastCol = firstCol + size - 1;
        
        IntStream.range(0, menuSize.getSize())
                 .filter(slot -> {
                     int row = slot / rowSize;
                     int col = slot % rowSize;
                     
                     if(row == firstRow || row == lastRow) {
                         return col >= firstCol && col <= lastCol;
                     } else if(col == firstCol || col == lastCol) {
                         return row >= firstRow && row <= lastRow;
                     } else {
                         return false;
                     }
                 })
                 .filter(slot -> getItem(slot) == null)
                 .forEach(slot -> setItem(slot, glassItem));
    }
    
    protected void addGlassBorder(GlassColor glassColor) {
        addGlassBorder(glassColor, false);
    }
    
    protected void addGlassBorder(GlassColor glassColor, boolean onlyFirstAndLastRows) {
        ItemStack glassItem = GUIItem.getGlassItem(glassColor);
        
        int rowSize = MenuSize.SIZE_1_ROW.getSize();
        int firstRow = 0;
        int lastRow = (menuSize.getSize() / rowSize) - 1;
        int firstColumn = 0;
        int lastColumn = rowSize - 1;
        IntStream.range(0, menuSize.getSize())
                 .filter(slot -> {
                     int row = slot / MenuSize.SIZE_1_ROW.getSize(); 
                     if(onlyFirstAndLastRows) {
                         return row == firstRow || row == lastRow;
                     } else {
                         int column = slot % MenuSize.SIZE_1_ROW.getSize();
                         return row == firstRow || row == lastRow || column == firstColumn || column == lastColumn;                         
                     }
                 })
                 .filter(slot -> getItem(slot) == null)
                 .forEach(slot -> setItem(slot, glassItem));
    }
    
    protected ItemStack getItem(int slot) {
        return contents[slot];
    }
    
    protected void setItem(int slot, ItemStack item) {
        if(slot < 0 || slot > contents.length) {
            throw new IllegalArgumentException("Invalid slot: " + slot + ". It must be between 0 and " + contents.length);
        }
        contents[slot] = item;
    }
    
    protected void setItem(int slot, ItemStack item, Consumer<MenuClickEvent> action) {
        setItem(slot, item);
        setClickAction(slot, action);
    }
    
    protected void setClickAction(int slot, Consumer<MenuClickEvent> action) {
        actions.put(slot, action);
    }
    
    protected void setBackItem(int slot) {
        setItem(slot, GUIItem.getBackItem(), e -> {
            if(e.isEmptyCursor()) {
                onBack(e.getPlayer());
            }
        });
    }
    
    protected void onBack(Player player) {
        if(onBack != null) {
            onBack.accept(player);
        }
    }
    
    protected void onFirstOpen(Player player) { }
    
    protected void onPreOpen(Player player) { }
    
    public final void openToPlayer(@NonNull Player player) {
        openToPlayer(player.getUniqueId());
    }
    
    public final void openToPlayer(@NonNull UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if(player == null) {
            throw new IllegalArgumentException("Player with uuid " + uuid + " is offline");
        }
        
        actions.clear();
        if(firstOpened) {
            onFirstOpen(player);
            firstOpened = false;
        }
        onPreOpen(player);
        buildMenu(player);
        inventory = Bukkit.createInventory(this, menuSize.getSize(), processTitle(title));
        inventory.setContents(contents);
        Arrays.fill(contents, null);
        player.openInventory(inventory);
        
        if(trackLastMenu) {
        	MenuManager.trackLastMenu(player, this);
        }
    }
    
    public void onClick(MenuClickEvent event) {
        if(!onClickStart(event)) {
            return;
        }
        
        Consumer<MenuClickEvent> action = actions.get(event.getSlot());
        if(event.isTopInventory() && action != null) {
            action.accept(event);
        } else {
            onClickDefault(event);
        }
        
        onClickEnd(event);
    }
}
