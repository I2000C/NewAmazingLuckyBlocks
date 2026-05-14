package me.i2000c.newalb.lucky_blocks.editors.menus.sound;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;

import me.i2000c.newalb.api.gui.MenuItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.PaginatedEditorMenu;
import me.i2000c.newalb.api.version.MinecraftVersion;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.lucky_blocks.editors.utils.SoundTreeNode;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class SoundTypeMenu extends PaginatedEditorMenu<SoundTreeNode, SoundTreeNode> {
    
    private final SoundTypeMenu previousMenu;
    private SoundTreeNode currentNode = SoundTreeNode.getRootNode();
    
    private SoundTypeMenu(SoundTypeMenu previousMenu) {
        super("", MenuSize.SIZE_6_ROWS, true, MenuSize.SIZE_5_ROWS, 50, 51, 52);
        this.previousMenu = previousMenu;
    }
    
    public SoundTypeMenu() {
        this(null);
    }
    
    @Override
    protected SoundTreeNode createNewItem() {
        return SoundTreeNode.getRootNode();
    }
    
    @Override
    public List<SoundTreeNode> getItemList() {
        return new ArrayList<>(currentNode.getChildren().values());
    }
    
    @Override
    public MenuItem mapItemToPage(SoundTreeNode node, int index) {
        ItemStackWrapper wrapper = ItemStackWrapper.fromItem(node.getItemToDisplay(), false);
        if(node.isParentOf(item) || node == item) {
            String selectedText;
            if(node.isLeafNode()) {
                selectedText = "&3Selected sound";
            } else switch(node.getLevel()) {
                case 1: selectedText = "&3Selected sound category"; break;
                case 2: selectedText = "&3Selected sound subcategory"; break;
                default: selectedText = "&3Selected sound subsubcategory"; break;
            }
            wrapper.addLoreLine(selectedText);
            wrapper.addEnchantment(XEnchantment.UNBREAKING, 1);
            wrapper.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        
        Consumer<MenuClickEvent> action = e -> {
            if(node.isLeafNode()) {
                for(SoundTypeMenu menu = this; menu != null; menu = menu.previousMenu) {
                    menu.item = node;
                }
                XSound sound = node.getSound();
                sound.play(e.getPlayer());
                openToPlayer(e.getPlayer());
            } else {
                SoundTypeMenu menu = new SoundTypeMenu(this);
                menu.currentNode = node;
                menu.setItemToEdit(item);
                menu.setOnBack(this::openToPlayer);
                menu.setOnNext(this::onNext);
                menu.openToPlayer(e.getPlayer());
            }
        };
        
        return new MenuItem(wrapper, action);
    }
    
    @Override
    protected void buildMenu(Player player) {
        String title = "&3&lSound Type";
        if(currentNode.getLevel() > 0) {
            title += " &6(Level: &e" + currentNode.getLevel() + "&6)";
        }
        setTitle(title);
        
        if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_10)) {
            ItemStack stop = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                             .setDisplayName("&cStop all sounds")
                                             .toItemStack();
            
            setItem(47, stop, e -> XSound.getValues().forEach(sound -> sound.stopSound(player)));
        }
        
        setBackItem(45);
        setNextItem(53);
    }
}
