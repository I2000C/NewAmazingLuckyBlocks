package me.i2000c.newalb.lucky_blocks.editors.menus.entity;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.MenuItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.PaginatedEditorMenu;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.utils.misc.ExtendedEntityType;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class EntitySelectMenu extends PaginatedEditorMenu<ExtendedEntityType, ExtendedEntityType> {

    public EntitySelectMenu() {
        super("&d&lEntity List", MenuSize.SIZE_6_ROWS, true, MenuSize.SIZE_5_ROWS);
    }
    
    protected ExtendedEntityType createNewItem() {
        return null;
    }
    
    @Override
    public List<ExtendedEntityType> getItemList() {
        return ExtendedEntityType.values();
    }
    
    @Override
    public MenuItem mapItemToPage(ExtendedEntityType extendedEntityType, int index) {
        XMaterial material = extendedEntityType.getMaterial();
        ItemStackWrapper wrapper = ItemStackWrapper.newItem(material);
        wrapper.setDisplayName("&3" + extendedEntityType.name());
        if(extendedEntityType.isAlive()) {
            wrapper.addLoreLine("&6Is living entity: &atrue");
        } else {
            wrapper.addLoreLine("&6Is living entity: &7false");
        }
        if(extendedEntityType.isAgeable()) {
            wrapper.addLoreLine("&6Is ageable entity: &atrue");
        } else {
            wrapper.addLoreLine("&6Is ageable entity: &7false");
        }
        if(extendedEntityType.isTameable()) {
            wrapper.addLoreLine("&6Is tameable entity: &atrue");
        } else {
            wrapper.addLoreLine("&6Is tameable entity: &7false");
        }
        if(extendedEntityType.isAngryable()) {
            wrapper.addLoreLine("&6Is angryable entity: &atrue");
        } else {
            wrapper.addLoreLine("&6Is angryable entity: &7false");
        }
        
        if(!isNewItem && item.equals(extendedEntityType)) {
            wrapper.addEnchantment(XEnchantment.UNBREAKING, 1);
            wrapper.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        
        Consumer<MenuClickEvent> action = e -> onNext(e.getPlayer(), extendedEntityType);
        return new MenuItem(wrapper, action);
    }
    
    @Override
    protected void buildMenu(Player player) {
        setBackItem(45);
    }
}
