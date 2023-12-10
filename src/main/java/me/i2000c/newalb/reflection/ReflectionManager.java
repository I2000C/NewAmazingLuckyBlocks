package me.i2000c.newalb.reflection;

import java.util.HashMap;
import java.util.Map;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import org.bukkit.Bukkit;

public class ReflectionManager {
    // Spigot NMS info: https://www.spigotmc.org/wiki/spigot-nms-and-minecraft-versions-1-16/
    
    private static final String BUKKIT_VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    
    private static final Map<String, RefClass> CLASS_MAP = new HashMap<>();
    
    public static RefClass getClass(String name) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        try {
            Class clazz = Class.forName(name);
            return RefClass.of(clazz);
        } catch(Throwable ex) {
            return null;
        }
//</editor-fold>
    }    
    public static RefClass getCachedClass(String name) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        RefClass refClass = CLASS_MAP.get(name);
        if(refClass == null) {
            refClass = getClass(name);
            if(refClass == null) {
                String message = String.format("Could not find class with name %s", name);
                throw new NoClassDefFoundError(message);
            }
            
            CLASS_MAP.put(name, refClass);
        }
        return refClass;
//</editor-fold>
    }    
    public static RefClass getCachedClass(Class clazz) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        return getCachedClass(clazz.getName());
//</editor-fold>
    }
    public static RefClass getCachedClass(Object object) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        return getCachedClass(object.getClass());
//</editor-fold>
    }
    
    public static RefMethod getMethod(String className, String methodName, Object... params) {
        RefClass refClass = getClass(className);
        return refClass != null ? refClass.getMethod(methodName, params) : null;
    }
    public static RefConstructor getConstructor(String className, Object... params) {
        RefClass refClass = getClass(className);
        return refClass != null ? refClass.getConstructor(params) : null;
    }
    public static RefField getField(String className, String fieldName) {
        RefClass refClass = getClass(className);
        return refClass != null ? refClass.getField(fieldName) : null;
    }
    
    public static <T> T callMethod(Object object, String methodName, Object... params) {
        RefClass refClass = getCachedClass(object);
        return refClass.callMethod(methodName, object, params);
    }
    public static <T> T callConstructor(String className, Object... params) {
        RefClass refClass = getCachedClass(className);
        return refClass.callConstructor(params);
    }    
    public static <T> T getFieldValue(Object object, String name) {
        RefClass refClass = getCachedClass(object);
        return refClass.getFieldValue(name, object);
    }
    public static void setFieldValue(Object object, String name, Object value) {
        RefClass refClass = getCachedClass(object);
        refClass.setFieldValue(name, object, value);
    }
    
    public static <T> T callConstructor(Class clazz, Object... params) {
        RefClass refClass = getCachedClass(clazz);
        return refClass.callConstructor(params);
    } 
    public static <T> T callStaticMethod(String className, String methodName, Object... params) {
        RefClass refClass = getCachedClass(className);
        return refClass.callStaticMethod(methodName, params);
    }
    public static <T> T getStaticFieldValue(String className, String fieldName) {
        RefClass refClass = getCachedClass(className);
        return refClass.getStaticFieldValue(fieldName);
    }
    public static void setStaticFieldValue(String className, String fieldName, Object value) {
        RefClass refClass = getCachedClass(className);
        refClass.setStaticFieldValue(fieldName, value);
    }
    
    public static <T> T callStaticMethod(Class clazz, String methodName, Object... params) {
        return getCachedClass(clazz).callStaticMethod(methodName, params);
    }
    public static <T> T getStaticFieldValue(Class clazz, String fieldName) {
        return getCachedClass(clazz).getStaticFieldValue(fieldName);
    }
    public static void setStaticFieldValue(Class clazz, String fieldName, Object value) {
        getCachedClass(clazz).setStaticFieldValue(fieldName, value);
    }
    
    public static RefClass getNMSClass(String nmsPackage, String name) {
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isNewNMS()){
            return getClass(nmsPackage + "." + name);
        }else{
            return getClass("net.minecraft.server." + BUKKIT_VERSION + "." + name);
        }        
    }    
    public static RefClass getCraftClass(String name) {
        return getClass("org.bukkit.craftbukkit." + BUKKIT_VERSION + "." + name);
    }
    
    public static RefClass getCachedNMSClass(String nmsPackage, String name) {
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isNewNMS()){
            return getCachedClass(nmsPackage + "." + name);
        }else{
            return getCachedClass("net.minecraft.server." + BUKKIT_VERSION + "." + name);
        }        
    }    
    public static RefClass getCachedCraftClass(String name) {
        return getCachedClass("org.bukkit.craftbukkit." + BUKKIT_VERSION + "." + name);
    }
}
