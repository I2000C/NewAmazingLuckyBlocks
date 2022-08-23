package me.i2000c.newalb.utils2;

import java.lang.reflect.Method;
import java.util.Random;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.World;

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
    
    public static int generateRandomInt(int min, int max){
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }
    
    private static Method getMinHeightMethod = null;
    public static int getMinWorldHeight(World world){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(NewAmazingLuckyBlocks.getMinecraftVersion().compareTo(MinecraftVersion.v1_18) >= 0){
            // In Minecraft 1.18+ the min height can be negative
            try{
                if(getMinHeightMethod == null){
                    getMinHeightMethod = world.getClass().getMethod("getMinHeight");
                }
                return (Integer) getMinHeightMethod.invoke(world);
            }catch(Exception ex){
                Logger.err("An error occurred while getting min world height:");
                Logger.err(ex);
                return 0;
            }
        }else{
            // In Minecraft 1.8-1.17 the min height is 0
            return 0;
        }
//</editor-fold>
    }
    
    //Reflection Utils
    public static Class<?> getNMSClass(String nmsPackage, String name) throws ClassNotFoundException{
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isNewNMS()){
            return Class.forName(nmsPackage + "." + name);
        }else{
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            return Class.forName("net.minecraft.server." + version + "." + name);
        }        
    }
    
    public static Class<?> getCraftClass(String name) throws ClassNotFoundException{
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
    }
}
