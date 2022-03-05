package me.i2000c.newalb.utils2;

import java.util.Objects;
import java.util.Random;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.LangLoader;
import me.i2000c.newalb.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OtherUtils{
    public static boolean checkPermission(Player player, String permissionPATH){
        if((ConfigManager.getConfig().getBoolean(permissionPATH + ".required-permission")) && (!player.hasPermission(ConfigManager.getConfig().getString(permissionPATH + ".permission")))){
            Logger.sendMessage(LangLoader.getMessages().get("need-permission"), player);
            return false;
        }else{
            return true;
        }
    }
    
    public static boolean checkItemStack(ItemStack toCheck, ItemStack reference){
        if(toCheck == null || reference == null){
            return false;
        }
        
        if(toCheck.getType() == reference.getType()){
            if(Objects.equals(toCheck.getItemMeta().getDisplayName(), reference.getItemMeta().getDisplayName())){
                return true;
            }
        }
        
        return false;
    }
    
    public static ItemStack parseMaterial(String materialNameAndDurability){
        ItemStack stack;
        String[] splitted = materialNameAndDurability.split(":");
        String materialName = splitted[0];
        
        try{
            int materialID = Integer.parseInt(materialName);
            stack = new ItemStack(materialID);
        }catch(NumberFormatException ex){
            stack = new ItemStack(Material.valueOf(materialName));
        }        
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion() && splitted.length == 2){
            short materialDurability = Short.parseShort(splitted[1]);
            stack.setDurability(materialDurability);
        }
        
        return stack;
    }
    public static String parseItemStack(ItemStack stack){
        String materialNameAndDurability = stack.getType().name();
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion() && stack.getDurability() > 0){
            materialNameAndDurability += ":" + stack.getDurability();
        }
        return materialNameAndDurability;
    }
    
    public static int generateRandomInt(int min, int max){
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
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
