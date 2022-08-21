package me.i2000c.newalb.utils2;

import java.lang.reflect.Method;
import java.util.Random;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.logger.LogLevel;
import me.i2000c.newalb.utils.logger.Logger;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class OtherUtils{    
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
                Logger.log("An error occurred while getting min world height:", LogLevel.INFO);
                Logger.log(ex, LogLevel.INFO);
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
