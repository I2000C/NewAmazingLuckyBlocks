package me.i2000c.newalb.api.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import me.i2000c.newalb.api.gui.menus.Menu;

public class MenuManager {
	
	private static final Map<UUID, Menu> lastOpenedMenusByPlayer = new HashMap<>();
	private static final Map<UUID, Boolean> confirmationByPlayer = new HashMap<>();
	
	public static void trackLastMenu(Player player, Menu menu) {
		lastOpenedMenusByPlayer.put(player.getUniqueId(), menu);
		confirmationByPlayer.put(player.getUniqueId(), false);
	}
	
	public static Menu getLastMenu(Player player) {
		return lastOpenedMenusByPlayer.get(player.getUniqueId());
	}
	
	public static boolean getConfirmation(Player player) {
		return confirmationByPlayer.computeIfAbsent(player.getUniqueId(), k -> false);
	}
	
	public static void setConfirmation(Player player, boolean confirmation) {
		confirmationByPlayer.put(player.getUniqueId(), confirmation);
	}
	
	public static void removeLastMenu(Player player) {
		lastOpenedMenusByPlayer.remove(player.getUniqueId());
		confirmationByPlayer.remove(player.getUniqueId());
	}
	
	public static void removeAllMenus() {
		lastOpenedMenusByPlayer.clear();
		confirmationByPlayer.clear();
	}
	
	public static Menu getOpenedMenu(Player player) {
	    InventoryView view = player.getOpenInventory();
        if(view == null) {
            return null;
        }
        
        Inventory topInventory = view.getTopInventory();
        if(topInventory != null && topInventory.getHolder() instanceof Menu) {
            return (Menu) topInventory.getHolder();
        } else {
            return null;
        }
	}
	
	public static void closeAllMenus() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			if(getOpenedMenu(player) != null) {
			    player.closeInventory();
			}
		});
	}
}
