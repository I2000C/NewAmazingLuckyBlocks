package me.i2000c.newalb.utils2;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTReflectionUtil;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.Nullable;
import lombok.NonNull;
import me.i2000c.newalb.MinecraftVersion;
import org.bukkit.inventory.ItemStack;

public class NBTUtils {
    
    static {
        // Change logging level of Item-NBT-API to Warning
        de.tr7zw.changeme.nbtapi.utils.MinecraftVersion.getLogger().setLevel(Level.WARNING);
    }
    
    public static void set(@NonNull ItemStack stack, @NonNull String tag, @NonNull String value) {
        NBT.modify(stack, nbt -> {nbt.setString(tag, value);});
    }
    public static void set(@NonNull ItemStack stack, @NonNull String tag, @NonNull Integer value) {
        NBT.modify(stack, nbt -> {nbt.setInteger(tag, value);});
    }
    
    public static String getString(@NonNull ItemStack stack, @NonNull String tag) {
        return NBT.get(stack, nbt -> {return nbt.getString(tag);});
    }
    public static Integer getInt(@NonNull ItemStack stack, @NonNull String tag) {
        return NBT.get(stack, nbt -> {return nbt.getInteger(tag);});
    }
    
    public static boolean contains(@NonNull ItemStack stack, @NonNull String tag) {
        return NBT.get(stack, nbt -> {return nbt.hasTag(tag);});
    }
    
    public static void removeTag(@NonNull ItemStack stack, @NonNull String tag) {
        NBT.modify(stack, nbt -> {nbt.removeKey(tag);});
    }
    
    public static void clearCustomTags(@NonNull ItemStack stack) {
        if(MinecraftVersion.CURRENT_VERSION.isLessThan(MinecraftVersion.v1_20_5)) {
            // Delete only custom tags
            Set<String> customKeys = getCustomNbtKeys(stack);
            NBT.modify(stack, nbt -> {
                nbt.getKeys()
                   .stream()
                   .filter(customKeys::contains)
                   .forEach(nbt::removeKey);
            });
        } else {
            // Delete all tags
            // See: https://github.com/tr7zw/Item-NBT-API/wiki/Using-the-NBT-API#changing-vanilla-item-nbt-on-1205
            NBT.modify(stack, nbt -> {nbt.clearNBT();});
        }
    }
    
    public static Set<String> getCustomNbtKeys(@NonNull ItemStack stack) {
        if(MinecraftVersion.CURRENT_VERSION.isLessThan(MinecraftVersion.v1_20_5)) {
            // Return only custom tags
            return NBTReflectionUtil.getUnhandledNBTTags(stack.getItemMeta()).keySet();
        } else {
            // Since 1.20.5 NBT only has custom tags
            // See: https://github.com/tr7zw/Item-NBT-API/wiki/Using-the-NBT-API#changing-vanilla-item-nbt-on-1205
            return NBT.get(stack, nbt -> {return nbt.getKeys();});
        }
    }
    
    public static @Nullable String customNbtToString(@NonNull ItemStack stack) {
        if(MinecraftVersion.CURRENT_VERSION.isLessThan(MinecraftVersion.v1_20_5)) {
            // Delete all vanilla tags (amount, display name, enchantments, etc)
            Set<String> customKeys = getCustomNbtKeys(stack);
            return NBT.modify(stack.clone(), nbt -> {
                nbt.getKeys()
                    .stream()
                    .filter(OtherUtils.not(customKeys::contains))
                    .forEach(nbt::removeKey);
                
                if(nbt.getKeys().isEmpty()) {
                    return null;
                } else {
                    return nbt.toString();
                }
            });
        } else {
            // Since 1.20.5 NBT doesn't have vanilla tags
            // See: https://github.com/tr7zw/Item-NBT-API/wiki/Using-the-NBT-API#changing-vanilla-item-nbt-on-1205
            return NBT.get(stack, nbt -> {
                if(nbt.getKeys().isEmpty()) {
                    return null;
                } else {
                    return nbt.toString();
                }
            });
        }
    }
    
    public static void applyCustomNbtToItem(@Nullable String nbtString, ItemStack stack) {
        if(nbtString != null) {
            ReadableNBT customNbt = NBT.parseNBT(nbtString);
            NBT.modify(stack, nbt -> {nbt.mergeCompound(customNbt);});
        }
    }
}
