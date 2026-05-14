package me.i2000c.newalb.lucky_blocks.editors.menus.trap;

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
import me.i2000c.newalb.lucky_blocks.rewards.types.TrapReward;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class TrapTypeMenu extends PaginatedEditorMenu<XMaterial, XMaterial> {

    public TrapTypeMenu() {
        super("&2&lTrap materials", MenuSize.SIZE_3_ROWS, true, MenuSize.SIZE_2_ROWS);
    }
    
    @Override
    public List<XMaterial> getItemList() {
        return TrapReward.getPressurePlateMaterials();
    }
    
    @Override
    public MenuItem mapItemToPage(XMaterial material, int index) {
        ItemStackWrapper wrapper = ItemStackWrapper.newItem(material);
        if(material == item) {                
            wrapper.addEnchantment(XEnchantment.UNBREAKING, 1);
            wrapper.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        Consumer<MenuClickEvent> action = e -> onNext(e.getPlayer(), material);
        return new MenuItem(wrapper, action);
    }
    
    @Override
    protected XMaterial createNewItem() {
        return null;
    }
    
    @Override
    protected void buildMenu(Player player) {
        setBackItem(18);
    }
}
