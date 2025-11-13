package me.i2000c.newalb.lucky_blocks.editors.nbt;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTType;
import de.tr7zw.changeme.nbtapi.handler.NBTHandlers;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.i2000c.newalb.api.functions.InventoryFunction;
import me.i2000c.newalb.api.gui.CustomInventoryType;
import me.i2000c.newalb.api.gui.GUIFactory;
import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.Menu;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.lucky_blocks.editors.Editor;
import me.i2000c.newalb.lucky_blocks.editors.EditorType;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemNbtMenu extends Editor<ItemStack> {
    
    public ItemNbtMenu() {
        InventoryListener.registerInventory(CustomInventoryType.ITEM_NBT_MENU, ITEM_NBT_MENU_FUNCTION);
    }
    
    static final String NBT_TYPE_TAG = "Type";
    private static final String NBT_NAME_TAG = "Name";
    private static final String NBT_VALUE_TAG = "Value";
    
    private static final String CURRENT_TAG_COLOR = "&b";
    private static final String NOT_CURRENT_TAG_COLOR = "&6";
    private static final int TREE_SPACES_PER_LEVEL = 4;
    
    private static final int BACK_SLOT = 27;
    private static final int NEXT_SLOT = 35;
    
    private static final int GO_TO_PARENT_TAG_SLOT = 28;
    private static final int TREE_ITEM_SLOT = 29;
    
    private static final int ADD_NBT_TAG_SLOT = 31;
    private static final int EDIT_NBT_TAG_SLOT = 32;
    private static final int DELETE_NBT_TAG_SLOT = 33;
    
    private CustomNBTItem rootTag;
    private ReadWriteNBT currentTag;
    private boolean editMode;
    private boolean deleteMode;
    
    @Override
    protected void newItem(Player player) {
        throw new UnsupportedOperationException("Method not implemented");
    }
    
    @Override
    protected void editItem(Player player) {
        openItemNbtMenu(player);
    }
    
    @Override
    protected void reset() {
        if(this.rootTag != null) {
            this.rootTag.finalizeChanges();
        }
        
        this.rootTag = new CustomNBTItem(item.clone());
        this.currentTag = this.rootTag;
        this.editMode = false;
        this.deleteMode = false;
    }
    
    private void openItemNbtMenu(Player player) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.ITEM_NBT_MENU, 36, "&3&lNBT menu");
        
        ReadWriteNBT nbt = currentTag;
        Set<String> keys = new TreeSet<>(nbt.getKeys()); 
            
        Iterator<String> iterator = keys.iterator();
        for(int i=0; i<BACK_SLOT && iterator.hasNext(); i++) {
            String key = iterator.next();
            ItemStack stack = getTagItem(nbt, key);
            menu.setItem(i, stack);
        }
        
        String asciiTree = toAsciiTree(rootTag, "", false);
        
        ItemStack treeItem = ItemStackWrapper.newItem(XMaterial.OAK_SAPLING)
                                             .setDisplayName("&3NBT tree")
                                             .addLoreLine("")
                                             .addLore(Arrays.asList(asciiTree.split("\n")))
                                             .toItemStack();
        
        ItemStack goToParentTagItem = ItemStackWrapper.newItem(XMaterial.MAGMA_CREAM)
                                                      .setDisplayName("&2Go to parent tag")
                                                      .toItemStack();
        
        ItemStack add = ItemStackWrapper.newItem(XMaterial.SLIME_BALL)
                                        .setDisplayName("&aAdd NBT tag")
                                        .toItemStack();
        
        ItemStack edit = GUIItem.getEnabledDisabledItem(
                editMode, 
                "&eEdit NBT tags", 
                "&6EditMode", 
                XMaterial.IRON_PICKAXE, 
                XMaterial.IRON_PICKAXE);
        ItemStackWrapper.fromItem(edit, false)
                .addLoreLine("")
                .addLoreLine("&5If this mode is enabled, you will be able")
                .addLoreLine("&5to edit NBT tags by clicking on them");
        
        ItemStack delete = GUIItem.getEnabledDisabledItem(
                deleteMode, 
                "&cDelete NBT tags", 
                "&4DeleteMode", 
                XMaterial.BARRIER, 
                XMaterial.BARRIER);
        ItemStackWrapper.fromItem(delete, false)
                .addLoreLine("")
                .addLoreLine("&5If this mode is enabled, you will be able")
                .addLoreLine("&5to delete NBT tags by clicking on them");
        
        menu.setItem(TREE_ITEM_SLOT, treeItem);
        
        if(Objects.equals(rootTag, currentTag)) {
            menu.setItem(BACK_SLOT, GUIItem.getBackItem());
            menu.setItem(NEXT_SLOT, GUIItem.getNextItem());
        } else {
            menu.setItem(GO_TO_PARENT_TAG_SLOT, goToParentTagItem);
        }
        
        if(!editMode && !deleteMode) {
            menu.setItem(ADD_NBT_TAG_SLOT, add);
        }
        
        if(!deleteMode) {
            menu.setItem(EDIT_NBT_TAG_SLOT, edit);
        }
        
        if(!editMode) {
            menu.setItem(DELETE_NBT_TAG_SLOT, delete);
        }
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction ITEM_NBT_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        switch(e.getSlot()) {
            case BACK_SLOT:
                if(Objects.equals(rootTag, currentTag)) {
                    // Go to previous menu
                    rootTag = null;
                    currentTag = null;
                    onBack.accept(player);
                }
                break;
            case NEXT_SLOT:
                if(Objects.equals(rootTag, currentTag)) {
                    // Go to next menu
                    if(rootTag != null) {
                        rootTag.finalizeChanges();
                        ItemStackWrapper.fromItem(item, false).clearTags();
                        NBT.modify(item, nbt -> {nbt.mergeCompound(rootTag);});
                    }
                    
                    rootTag = null;
                    currentTag = null;
                    onNext.accept(player, item);
                }
                break;
            case GO_TO_PARENT_TAG_SLOT:
                if(!Objects.equals(rootTag, currentTag)) {
                    // Go to parent tag
                    currentTag = findParentTag(currentTag);
                    openItemNbtMenu(player);
                }
                break;
            case ADD_NBT_TAG_SLOT:
                if(!editMode && !deleteMode) {
                    Editor<ItemNbtTagMenu.Tag> editor = EditorType.ITEM_NBT_TAG.getEditor();
                    editor.createNewItem(player, this::openItemNbtMenu, (p, tag) -> {
                        setTag(tag);
                        openItemNbtMenu(p);
                    });
                }
                break;
            case EDIT_NBT_TAG_SLOT:
                if(!deleteMode) {
                    editMode = !editMode;
                    openItemNbtMenu(player);
                }
                break;
            case DELETE_NBT_TAG_SLOT:
                if(!editMode) {
                    deleteMode = !deleteMode;
                    openItemNbtMenu(player);
                }
                break;
            default:
                ItemStack stack = e.getCurrentItem();
                if(stack == null || stack.getType() == Material.AIR) {
                    break;
                }
                
                NBT.get(stack, nbt -> {
                    if(!nbt.hasTag(NBT_TYPE_TAG)) {
                        return;
                    }
                    
                    NBTType nbtType = nbt.getEnum(NBT_TYPE_TAG, NBTType.class);
                    String key = nbt.getString(NBT_NAME_TAG);
                    
                    if(editMode) {
                        ItemNbtTagMenu.Tag tag = new ItemNbtTagMenu.Tag();
                        tag.setName(key);
                        tag.setType(nbtType);
                        tag.setValue(getValue(currentTag, key));
                        
                        Editor<ItemNbtTagMenu.Tag> editor = EditorType.ITEM_NBT_TAG.getEditor();
                        editor.editExistingItem(tag, player, this::openItemNbtMenu, (p, edittedTag) -> {
                            setTag(edittedTag);
                            openItemNbtMenu(p);
                        });
                    } else if(deleteMode) {
                        currentTag.removeKey(key);
                        openItemNbtMenu(player);
                    } else {
                        if(nbtType == NBTType.NBTTagCompound) {
                            // Value is the NBT key
                            currentTag = currentTag.getCompound(key);
                            openItemNbtMenu(player);
                        }
                    }
                });
        }
//</editor-fold>
    };
    
    
    
    
    
    
    
    
    
    private void setTag(ItemNbtTagMenu.Tag tag) {
        currentTag.removeKey(tag.getName());
        switch(tag.getType()) {
            case NBTTagByte:        currentTag.setByte(tag.getName(), (Byte) tag.getValue());           break;
            case NBTTagShort:       currentTag.setShort(tag.getName(), (Short) tag.getValue());         break;
            case NBTTagInt:         currentTag.setInteger(tag.getName(), (Integer) tag.getValue());     break;
            case NBTTagLong:        currentTag.setLong(tag.getName(), (Long) tag.getValue());           break;
            case NBTTagFloat:       currentTag.setFloat(tag.getName(), (Float) tag.getValue());         break;
            case NBTTagDouble:      currentTag.setDouble(tag.getName(), (Double) tag.getValue());       break;
            case NBTTagByteArray:   currentTag.setByteArray(tag.getName(), (byte[]) tag.getValue());    break;
            case NBTTagIntArray:    currentTag.setIntArray(tag.getName(), (int[]) tag.getValue());      break;
            case NBTTagLongArray:   currentTag.setLongArray(tag.getName(), (long[]) tag.getValue());    break;
            case NBTTagString:      currentTag.setString(tag.getName(), (String) tag.getValue());       break;
            case NBTTagCompound:
                ReadWriteNBT compound = NBT.createNBTObject();
                currentTag.set(tag.getName(), compound, NBTHandlers.STORE_READWRITE_TAG);
        }
    }
    
    static Object parseValue(String value, NBTType nbtType) throws IllegalArgumentException {
        try {
            switch(nbtType) {
                case NBTTagByte:    return Byte.valueOf(value);
                case NBTTagShort:   return Short.valueOf(value);
                case NBTTagInt:     return Integer.valueOf(value);
                case NBTTagLong:    return Long.valueOf(value);
                case NBTTagFloat:   return Float.valueOf(value);
                case NBTTagDouble:  return Double.valueOf(value);
                case NBTTagByteArray:
                case NBTTagIntArray:
                case NBTTagLongArray:
                    List<Object> numbers = new ArrayList<>();
                    
                    Pattern pattern = Pattern.compile("[+-]?\\d+");
                    Matcher matcher = pattern.matcher(value);
                    
                    switch(nbtType) {
                        case NBTTagByteArray:
                            while(matcher.find()) numbers.add(Byte.valueOf(matcher.group(0)));                    
                            return ArrayUtils.toPrimitive(numbers.stream().toArray(Byte[]::new));
                        case NBTTagIntArray:
                            while(matcher.find()) numbers.add(Integer.valueOf(matcher.group(0)));                    
                            return ArrayUtils.toPrimitive(numbers.stream().toArray(Integer[]::new));
                        case NBTTagLongArray:
                            while(matcher.find()) numbers.add(Long.valueOf(matcher.group(0)));                    
                            return ArrayUtils.toPrimitive(numbers.stream().toArray(Long[]::new));
                        default:
                            // Unreachable case
                            return null;
                    }
                case NBTTagString: return value;
                default: throw new Exception("Invalid NBT type: " + nbtType.name());
            }
        } catch(Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    static String getTypeName(NBTType nbtType) {
        switch(nbtType) {
            case NBTTagByte:        return "Byte";
            case NBTTagShort:       return "Short";
            case NBTTagInt:         return "Integer";
            case NBTTagLong:        return "Long";
            case NBTTagFloat:       return "Float";
            case NBTTagDouble:      return "Double";
            case NBTTagByteArray:   return "Byte array";
            case NBTTagIntArray:    return "Integer array";
            case NBTTagLongArray:   return "Long array";
            case NBTTagString:      return "String";
            case NBTTagCompound:    return "Compound";
            default:                return "Unknown";
        }
    }
    
    static Object getValue(ReadableNBT nbt, String key) {
        NBTType nbtType = nbt.getType(key);
        switch(nbtType) {
            case NBTTagByte:        return nbt.getByte(key);
            case NBTTagShort:       return nbt.getShort(key);
            case NBTTagInt:         return nbt.getInteger(key);
            case NBTTagLong:        return nbt.getLong(key);
            case NBTTagFloat:       return nbt.getFloat(key);
            case NBTTagDouble:      return nbt.getDouble(key);
            case NBTTagString:      return nbt.getString(key);
            case NBTTagByteArray:   return nbt.getByteArray(key);
            case NBTTagIntArray:    return nbt.getIntArray(key);
            case NBTTagLongArray:   return nbt.getLongArray(key);
            default:                return null;
        }
    }
    
    static String getValueAsString(ReadableNBT nbt, String key) {
        Object value;
        NBTType nbtType = nbt.getType(key);
        switch(nbtType) {
            case NBTTagByte:        value = nbt.getByte(key);       break;
            case NBTTagShort:       value = nbt.getShort(key);      break;
            case NBTTagInt:         value = nbt.getInteger(key);    break;
            case NBTTagLong:        value = nbt.getLong(key);       break;
            case NBTTagFloat:       value = nbt.getFloat(key);      break;
            case NBTTagDouble:      value = nbt.getDouble(key);     break;
            case NBTTagString:      value = nbt.getString(key);     break;
            case NBTTagByteArray:   value = Arrays.toString(nbt.getByteArray(key)); break;
            case NBTTagIntArray:    value = Arrays.toString(nbt.getIntArray(key));  break;
            case NBTTagLongArray:   value = Arrays.toString(nbt.getLongArray(key)); break;
            default:                value = "Unknown";
        }
        
        return value.toString();
    }
    
    static String getValueAsString(NBTType type, Object value) {
        switch(type) {
            case NBTTagByte:
            case NBTTagShort:
            case NBTTagInt:
            case NBTTagLong:
            case NBTTagFloat:
            case NBTTagDouble:
            case NBTTagString:
                return value.toString();
            case NBTTagByteArray:   return Arrays.toString((byte[]) value);
            case NBTTagIntArray:    return Arrays.toString((int[]) value);
            case NBTTagLongArray:   return Arrays.toString((long[]) value);
        }
        
        return "Unknown";
    }
    
    private ItemStack getTagItem(ReadWriteNBT nbt, String key) {
        ItemStackWrapper wrapper;
        NBTType nbtType = nbt.getType(key);
        if(nbtType == NBTType.NBTTagCompound) {
            wrapper = ItemStackWrapper.newItem(XMaterial.BOOK);
            wrapper.addLoreLine("");
            wrapper.addLoreLine("&bCurrent type: &3" + getTypeName(nbtType));
            wrapper.addLoreLine("&bCurrent keys:");
            ReadWriteNBT compound = nbt.getCompound(key);
            Set<String> keys = new TreeSet<>(compound.getKeys());
            keys.forEach(compoundKey -> wrapper.addLoreLine("  &d" + compoundKey));
            NBT.modify(wrapper.toItemStack(), nbtItem -> {
                nbtItem.setEnum(NBT_TYPE_TAG, nbtType);
                nbtItem.setString(NBT_NAME_TAG, key);
                nbtItem.setString(NBT_VALUE_TAG, null);
            });
            wrapper.addLoreLine("");
            wrapper.addLoreLine("&eClick to select");
        } else {
            wrapper = ItemStackWrapper.newItem(XMaterial.PAPER);
            wrapper.addLoreLine("");
            wrapper.addLoreLine("&bCurrent type: &3" + getTypeName(nbtType));
            String value = getValueAsString(nbt, key);
            wrapper.addLoreLine("&bCurrent value: &d" + value);
            NBT.modify(wrapper.toItemStack(), nbtItem -> {
                nbtItem.setEnum(NBT_TYPE_TAG, nbtType);
                nbtItem.setString(NBT_NAME_TAG, key);
                nbtItem.setString(NBT_VALUE_TAG, value);
            });
        }
        
        wrapper.setDisplayName("&6Tag name: &a" + key);
        
        return wrapper.toItemStack();
    }
    
    private ReadWriteNBT findParentTag(ReadWriteNBT nbt) {
        ReadWriteNBT parent = findParentTag(rootTag, nbt);
        return parent != null ? parent : rootTag;
    }
    private ReadWriteNBT findParentTag(ReadWriteNBT node, ReadWriteNBT nbt) {
        for(String key : node.getKeys()) {
            if(node.getType(key) == NBTType.NBTTagCompound) {
                ReadWriteNBT compound = node.getCompound(key);
                if(Objects.equals(compound, nbt)) {
                    return node;
                } else {
                    ReadWriteNBT parent = findParentTag(compound, nbt);
                    if(parent != null) {
                        return parent;
                    }
                }
            }
        }
        
        return null;
    }
    
    private String toAsciiTree(ReadableNBT nbt, String spaces, boolean selectedTag) {
        Set<String> keys = new TreeSet<>(nbt.getKeys());
        StringBuilder builder = new StringBuilder();
        for(String key : keys) {
            NBTType type = nbt.getType(key);
            if(type == NBTType.NBTTagCompound) {
                ReadableNBT compound = nbt.getCompound(key);
                boolean isCurrentTag = Objects.equals(currentTag, compound);
                
                String color = (selectedTag || isCurrentTag)
                                        ? CURRENT_TAG_COLOR
                                        : NOT_CURRENT_TAG_COLOR;
                
                builder.append(color)
                       .append(spaces)
                       .append(key)
                       .append(":\n");
                
                StringBuilder aux = new StringBuilder();
                for(int i=0; i<TREE_SPACES_PER_LEVEL; i++) aux.append(' ');
                builder.append(toAsciiTree(compound, spaces + aux.toString(), selectedTag || isCurrentTag));
            } else {
                String value = getValueAsString(nbt, key);
                String color = selectedTag ? CURRENT_TAG_COLOR : NOT_CURRENT_TAG_COLOR;
                
                builder.append(color)
                       .append(spaces)
                       .append(key)
                       .append(": ")
                       .append(value)
                       .append('\n');
            }
        }
        
        return builder.toString();
    }
}

class CustomNBTItem extends NBTItem {
    public CustomNBTItem(ItemStack stack) {
        super(stack, false, false, true);
    }
    
    @Override
    public void finalizeChanges() {
        super.finalizeChanges();
    }
}
