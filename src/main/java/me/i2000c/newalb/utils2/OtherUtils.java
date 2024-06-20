package me.i2000c.newalb.utils2;

import java.util.function.Predicate;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.reflection.ReflectionManager;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class OtherUtils{
    // Source: https://stackoverflow.com/a/990492
    // https://stackoverflow.com/questions/941272/how-do-i-trim-a-file-extension-from-a-string-in-java
    public static String removeExtension(String s) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        String separator = System.getProperty("file.separator");
        String filename;
        
        // Remove the path upto the filename.
        int lastSeparatorIndex = s.lastIndexOf(separator);
        if (lastSeparatorIndex == -1) {
            filename = s;
        } else {
            filename = s.substring(lastSeparatorIndex + 1);
        }
        
        // Remove the extension.
        int extensionIndex = filename.lastIndexOf(".");
        if (extensionIndex == -1)
            return filename;
        
        return filename.substring(0, extensionIndex);
//</editor-fold>
    }
    
    public static void removePlayerItems(Player player, int amount, Predicate<ItemStack> predicate){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(player == null || amount <= 0 || predicate == null){
            return;
        }
        
        Inventory inv = player.getInventory();
        ItemStack[] armorContents = player.getInventory().getArmorContents();
        for(int i=0; i<inv.getSize(); i++){
            ItemStack stack = inv.getItem(i);
            if(predicate.test(stack)){
                int itemAmount = stack.getAmount();
                int amountToRemove = Integer.min(itemAmount, amount);
                int newAmount = itemAmount - amountToRemove;
                if(newAmount > 0){
                    stack.setAmount(newAmount);
                }else{
                    inv.setItem(i, null);
                }
                amount -= amountToRemove;
                if(amount <= 0){
                    return;
                }
            }
        }
        for(int i=0; i<armorContents.length; i++){
            ItemStack stack = inv.getItem(i);
            if(predicate.test(stack)){
                int itemAmount = stack.getAmount();
                int amountToRemove = Integer.min(itemAmount, amount);
                int newAmount = itemAmount - amountToRemove;
                if(newAmount > 0){
                    stack.setAmount(newAmount);
                }else{
                    inv.setItem(i, null);
                }
                amount -= amountToRemove;
                if(amount <= 0){
                    return;
                }
            }
        }
//</editor-fold>
    }
    
    public static int getMinWorldHeight(World world){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_18)){
            // In Minecraft 1.18+ the min height can be negative
            return ReflectionManager.callMethod(world, "getMinHeight");
        }else{
            // In Minecraft 1.8-1.17 the min height is 0
            return 0;
        }
//</editor-fold>
    }
    
    public static <T extends Comparable<T>> T clamp(T value, T min, T max) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(value.compareTo(min) < 0) {
            return min;
        } else if(value.compareTo(max) > 0) {
            return max;
        } else {
            return value;
        }
//</editor-fold>
    }
}
