package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.menus.utils.SoundTreeNode;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GUIPagesAdapter;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class SoundTypeMenu extends Editor<XSound> {
    
    private static final int SOUND_LIST_MENU_SIZE = 54;
    private static final int SOULD_LIST_MENU_PAGE_SIZE = SOUND_LIST_MENU_SIZE - 9;
    private static final int PREVIOUS_PAGE_SLOT = 51;
    private static final int CURRENT_PAGE_SLOT = 52;
    private static final int NEXT_PAGE_SLOT = 53;    
    private static final List<XSound> sounds = new ArrayList<>(XSound.getValues());
    private static GUIPagesAdapter<SoundTreeNode> treeNodeListAdapter;
    
    private SoundTreeNode currentNode = null;    
    private Stack<Integer> pages = new Stack();
    
    public SoundTypeMenu() {        
        //<editor-fold defaultstate="collapsed" desc="Code">
        InventoryListener.registerInventory(CustomInventoryType.SOUND_TYPE_MENU, SOUND_TYPE_MENU_FUNCTION);
        
        treeNodeListAdapter = new GUIPagesAdapter<>(SOULD_LIST_MENU_PAGE_SIZE, (treeNode, index) -> {
            ItemStackWrapper wrapper = ItemStackWrapper.fromItem(treeNode.getItemToDisplay(), false);
            
            if(item != null) {
                SoundTreeNode soundNode = SoundTreeNode.getNode(item);
                if(soundNode.isParentNode(treeNode)) {
                    String selectedText;
                    if(treeNode.isLeafNode()) {
                        selectedText = "&3Selected sound";
                    }else switch(treeNode.getLevel()) {
                        case 1: selectedText = "&3Selected sound category"; break;
                        case 2: selectedText = "&3Selected sound subcategory"; break;
                        default: selectedText = "&3Selected sound subsubcategory"; break;
                    }
                    wrapper.addLoreLine(selectedText);
                    wrapper.addEnchantment(XEnchantment.UNBREAKING, 1);
                    wrapper.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
            }
            
            return wrapper.toItemStack();
        });
        treeNodeListAdapter.setPreviousPageSlot(PREVIOUS_PAGE_SLOT);
        treeNodeListAdapter.setCurrentPageSlot(CURRENT_PAGE_SLOT);
        treeNodeListAdapter.setNextPageSlot(NEXT_PAGE_SLOT);
//</editor-fold>
    }
    
    @Override
    protected void reset() {
        pages.clear();
        updateCurrentNode(null, SoundTreeNode.getRootNode());
    }
    
    @Override
    protected void newItem(Player player) {
        item = null;
        openSoundTypeMenu(player);
    }
    
    @Override
    protected void editItem(Player player) {
        openSoundTypeMenu(player);
    }
    
    private void openSoundTypeMenu(Player player) {
        String title = "&3&lSound Type";
        if(currentNode.getLevel() > 0) {
            title += " &6(Tree level: &e" + currentNode.getLevel() + "&6)";
        }
        
        Menu menu = GUIFactory.newMenu(CustomInventoryType.SOUND_TYPE_MENU, SOUND_LIST_MENU_SIZE, title);
        
        if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_10)) {
            ItemStack stop = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                             .setDisplayName("&cStop all sounds")
                                             .toItemStack();
            
            menu.setItem(47, stop);
        }
        
        menu.setItem(45, GUIItem.getBackItem());
        menu.setItem(49, GUIItem.getNextItem());
        
        treeNodeListAdapter.updateMenu(menu);
        menu.openToPlayer(player);
    }
    
    private final InventoryFunction SOUND_TYPE_MENU_FUNCTION = e -> {
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() != InventoryLocation.TOP) {
            return;
        }
        
        switch(e.getSlot()) {
            case 45:
                if(currentNode.isRootNode()) {
                    onBack.accept(player);
                } else {
                    Integer page = pages.pop();
                    if(page != null) {
                        treeNodeListAdapter.setPageIndex(page);
                    }
                    updateCurrentNode(player, currentNode.getParentNode());
                }
                break;
            case 47:
                //Stop sounds if minecraft version >= 1.10
                if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_10)){
                    for(XSound sound : sounds){
                        sound.stopSound(player);
                    }
                }
                break;
            case 49:
                if(item != null) {
                    onNext.accept(player, item);
                }
                break;
            case 51:
                if(treeNodeListAdapter.goToPreviousPage()) {
                    openSoundTypeMenu(player);
                }
                break;
            case 52:
                if(treeNodeListAdapter.goToMainPage()) {
                    openSoundTypeMenu(player);
                }
                break;
            case 53:
                if(treeNodeListAdapter.goToNextPage()) {
                    openSoundTypeMenu(player);
                }
                break;
            default:
                ItemStack stack = e.getCurrentItem();
                if(stack == null || stack.getType() == Material.AIR) {
                    return;
                }
                
                String nodeName = Logger.stripColor(ItemStackWrapper.fromItem(stack, false).getDisplayName());
                SoundTreeNode node = currentNode.getChildren().get(nodeName);
                if(node == null) {
                    return;
                }
                
                if(node.isLeafNode()) {
                    XSound sound = node.getSound();
                    item = sound;
                    sound.play(player);
                    openSoundTypeMenu(player);
                } else {
                    int page = treeNodeListAdapter.getPageIndex();
                    pages.push(page);
                    updateCurrentNode(player, node);
                }
        }
    };
    
    private void updateCurrentNode(Player player, SoundTreeNode newNode) {
        this.currentNode = newNode;
        treeNodeListAdapter.setItemList(new ArrayList<>(newNode.getChildren().values()));
        if(player != null) {
            openSoundTypeMenu(player);
        }
    }
}
