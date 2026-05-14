package me.i2000c.newalb.lucky_blocks.editors.menus.nbt;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;

import de.tr7zw.changeme.nbtapi.NBTType;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.misc.OtherUtils;

public class ItemNbtTypeMenu extends EditorMenu<NBTType> {
	
    public ItemNbtTypeMenu() {
		super("&d&lSelect NBT type", MenuSize.SIZE_4_ROWS, true);
	}

	private static final List<NBTType> NBT_TYPES = Arrays.stream(NBTType.values())
                                                         .filter(OtherUtils.not(NBTType.NBTTagEnd::equals))
                                                         .filter(OtherUtils.not(NBTType.NBTTagList::equals))
                                                         .collect(Collectors.toList());
    
    private static final int BACK_SLOT = 27;
    
    @Override
    protected NBTType createNewItem() {
    	return NBTType.NBTTagEnd;
    }
    
    @Override
    protected void buildMenu(Player player) {
        for(int i=0; i<BACK_SLOT && i<NBT_TYPES.size(); i++) {
            NBTType type = NBT_TYPES.get(i);
            ItemStackWrapper wrapper = ItemStackWrapper.newItem(XMaterial.NAME_TAG);
            wrapper.setDisplayName("&3" + type.name());
            if(type == item) {
                wrapper.addEnchantment(XEnchantment.POWER, 1);
                wrapper.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            setItem(i, wrapper.toItemStack(), e -> onNext(player, type));
        }
        setBackItem(BACK_SLOT);
    }
}
