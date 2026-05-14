package me.i2000c.newalb.lucky_blocks.editors.menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.Menu;
import me.i2000c.newalb.lucky_blocks.editors.menus.luckyblock_type.LuckyBlockTypeListMenu;
import me.i2000c.newalb.lucky_blocks.rewards.TypeManager;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class MainMenu extends Menu {
    
    public MainMenu() {
        super("&a&lMain menu", MenuSize.SIZE_1_ROW, true);
    }
    
    @Override
    protected void buildMenu(Player player) {
        ItemStack exit = ItemStackWrapper.newItem(XMaterial.IRON_DOOR)
                                         .setDisplayName("&cExit")
                                         .toItemStack();
        
        ItemStack packsItem = ItemStackWrapper.newItem(XMaterial.CRAFTING_TABLE)
                                              .setDisplayName("&3Manage outcome packs")
                                              .toItemStack();
        
        ItemStack typesItem = ItemStackWrapper.fromItem(TypeManager.getMenuItemStack())
                                              .setDisplayName("&6Manage lucky block types")
                                              .toItemStack();
        
        setItem(3, packsItem, e -> {
            PackListMenu menu = new PackListMenu();
            menu.setOnBack(this::openToPlayer);
            menu.openToPlayer(player);
        });
        setItem(5, typesItem, e -> {
            LuckyBlockTypeListMenu menu = new LuckyBlockTypeListMenu();
            menu.setOnBack(this::openToPlayer);
            menu.openToPlayer(player);
        });
        setItem(8, exit, e -> onBack(player));
    }
}
