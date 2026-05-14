package me.i2000c.newalb.utils.misc;

import java.nio.file.Path;
import java.util.function.Predicate;

import me.i2000c.newalb.api.version.MinecraftVersion;
import me.i2000c.newalb.utils.reflection.ReflectionManager;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Strings;

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
    
    public static String getExtension(Path path) {
        String name = path.getFileName().toString();
        int dot = name.lastIndexOf('.');
        return (dot == -1) ? "" : name.substring(dot + 1);
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
    
    public static <T> Predicate<T> not(Predicate<T> t) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        return t.negate();
//</editor-fold>
    }
    
    public static String getProgressBar(int current, int max, int totalBars, char symbol, ChatColor completedColor, ChatColor notCompletedColor) {
        //https://www.spigotmc.org/threads/progress-bars-and-percentages.276020/?__cf_chl_jschl_tk__=45c123d7886626b0cecd2cc530219da427d5214c-1580209641-0-AR6ljkF-Q6swj-OSROtke2TrSS-uy_MbFGPOgZ8lNPedOUjqxEiNa9ETsYqFS64G9TsBUlQyxRxAGCuGToSPXGUOA6orDyEN74TvJ1IXUHEWal6-5wBdbMyMaBC6_Y_YvW2sOQzrlt8PIFNgCJMpCqvLHdzf5FW8kjWSLfsL4FJu41b44pJNsgWD_4mWJnjSDKs4tru_U9cjwayifVseAspUUKUxu5B89HqV3thQpsjogf_kui1K5UGgweNH1cQAqwsYsv51bc4rmzyEdzWlg_6lXYx6GgWPedTgPW6VGo1vnwYVGAuv80G8yovQ4S2_Xw
        
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);

        return Strings.repeat("" + completedColor + symbol, progressBars)
                + Strings.repeat("" + notCompletedColor + symbol, totalBars - progressBars);
    }
    
    /**
     * Normalizes a value into the range [min, max).
     * The result will always be greater than or equal to min,
     * and strictly less than max.
     *
     * @param value the input value to normalize
     * @param min the lower bound (inclusive)
     * @param max the upper bound (exclusive), must be greater than min
     * @return the normalized value within [min, max)
     * @throws IllegalArgumentException if max <= min
     */
    public static int normalize(int value, int min, int max) {
        if (max <= min) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        
        int range = max - min;
        int result = (value - min) % range;
        if (result < 0) {
            result += range;
        }
        return result + min;
    }
    
    /**
     * Adds two values and wraps the result into the range [min, max).
     *
     * @param a the first operand
     * @param b the second operand
     * @param min the lower bound (inclusive)
     * @param max the upper bound (exclusive), must be greater than min
     * @return the result of (a + b) normalized within [min, max)
     */
    public static int addInRange(int a, int b, int min, int max) {
        return normalize(a + b, min, max);
    }

    /**
     * Subtracts two values and wraps the result into the range [min, max).
     *
     * @param a the first operand
     * @param b the second operand (subtracted from a)
     * @param min the lower bound (inclusive)
     * @param max the upper bound (exclusive), must be greater than min
     * @return the result of (a - b) normalized within [min, max)
     */
    public static int subInRange(int a, int b, int min, int max) {
        return normalize(a - b, min, max);
    }
}
