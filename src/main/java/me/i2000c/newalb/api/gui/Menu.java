package me.i2000c.newalb.api.gui;

import java.util.Objects;
import lombok.experimental.Delegate;
import me.i2000c.newalb.lucky_blocks.editors.menus.GUIManager;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Menu {
    
    @Delegate
    private final Inventory inventory;
    
    Menu(Inventory inventory) {
        this.inventory = Objects.requireNonNull(inventory);
    }
    
    public void openToPlayer(Player player) {
        openToPlayer(player, true);
    }    
    public void openToPlayer(Player player, boolean updateGUIManager) {
        if(updateGUIManager) {
            GUIManager.setCurrentMenu(this);
        }            
        player.openInventory(inventory);
    }
}
