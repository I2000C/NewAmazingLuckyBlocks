package me.i2000c.newalb.utils2;

import java.util.logging.Level;

import org.bukkit.inventory.ItemStack;

import de.tr7zw.changeme.nbtapi.NBT;
import lombok.NonNull;

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
}
