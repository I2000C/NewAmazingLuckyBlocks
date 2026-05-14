package me.i2000c.newalb.lucky_blocks.editors.menus.enchantment;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.MenuItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.PaginatedEditorMenu;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class EnchantmentSelectMenu extends PaginatedEditorMenu<XEnchantment, XEnchantment> {
    
    // https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/enchantments/Enchantment.html
    // https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Registry.html
    // Since Minecraft 1.21, Enchantment.name() doesn't work as expected --> Use Enchantment.getKey()
    
    public EnchantmentSelectMenu() {
        super("&5&lSelect enchantment", MenuSize.SIZE_6_ROWS, true, MenuSize.SIZE_5_ROWS);
    }
    
    @Override
    protected XEnchantment createNewItem() {
        return null;
    }
    
    @Override
    public List<XEnchantment> getItemList() {
        List<XEnchantment> enchantments = XEnchantment.REGISTRY.getValues()
                                                               .stream()
                                                               .filter(XEnchantment::isSupported)
                                                               .sorted(Comparator.comparing(XEnchantment::name))
                                                               .collect(Collectors.toList());
        return enchantments;
    }
    
    @Override
    public MenuItem mapItemToPage(XEnchantment enchantment, int index) {
        ItemStackWrapper wrapper = ItemStackWrapper.newItem(XMaterial.ENCHANTED_BOOK)
                                                   .setDisplayName("&d" + enchantment.name())
                                                   .addEnchantment(enchantment, 1);
        Consumer<MenuClickEvent> action = e -> onNext(e.getPlayer(), enchantment);
        MenuItem menuItem = new MenuItem(wrapper, action);
        return menuItem;
    }
    
    @Override
    protected void buildMenu(Player player) {
        setBackItem(45);
    }
}
