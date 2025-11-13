package me.i2000c.newalb.utils.misc;

import java.util.Objects;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;

public class MetadataManager {
    
    private static final String CLASS_METADATA_TAG = "NewAmazingLuckyBlocks.ClassMetadata";
    private static final String CUSTOM_METADATA_TAG = "NewAmazingLuckyBlocks.CustomMetadata";
    private static final String CUSTOM_METADATA2_TAG = "NewAmazingLuckyBlocks.CustomMetadata2";
    private static final String CUSTOM_METADATA3_TAG = "NewAmazingLuckyBlocks.CustomMetadata3";
    private static final Plugin PLUGIN = NewAmazingLuckyBlocks.getInstance();
    
    public static void setMetadata(Metadatable metadatable, String key, Object value) {
        metadatable.setMetadata(key, new FixedMetadataValue(PLUGIN, Objects.requireNonNull(value)));
    }    
    public static boolean hasMetadata(Metadatable metadatable, String key) {
        return metadatable.hasMetadata(key) && !metadatable.getMetadata(key).isEmpty();
    }
    public static <T> T getMetadata(Metadatable metadatable, String key) {
        if(hasMetadata(metadatable, key)) {
            return (T) metadatable.getMetadata(key).get(0).value();
        } else {
            return null;
        }
    }
    public static void removeMetadata(Metadatable metadatable, String key) {
        metadatable.removeMetadata(key, PLUGIN);
    }
    
    public static void setClassMetadata(Metadatable metadatable, SpecialItem specialItem) {
        setMetadata(metadatable, CLASS_METADATA_TAG, specialItem);
    }    
    public static SpecialItem getClassMetadata(Metadatable metadatable){
        return getMetadata(metadatable, CLASS_METADATA_TAG);
    }
    public static boolean hasClassMetadata(Metadatable metadatable){
        return hasMetadata(metadatable, CLASS_METADATA_TAG);
    }
    public static void removeClassMetadata(Metadatable metadatable){
        removeMetadata(metadatable, CLASS_METADATA_TAG);
    }
    
    public static void setCustomMetadata(Metadatable metadatable, Object value) {
        setMetadata(metadatable, CUSTOM_METADATA_TAG, value);
    }    
    public static <T> T getCustomMetadata(Metadatable metadatable){
        return getMetadata(metadatable, CUSTOM_METADATA_TAG);
    }
    public static boolean hasCustomMetadata(Metadatable metadatable){
        return hasMetadata(metadatable, CUSTOM_METADATA_TAG);
    }
    public static void removeCustomMetadata(Metadatable metadatable){
        removeMetadata(metadatable, CUSTOM_METADATA_TAG);
    }
    
    public static void setCustomMetadata2(Metadatable metadatable, Object value) {
        setMetadata(metadatable, CUSTOM_METADATA2_TAG, value);
    }    
    public static <T> T getCustomMetadata2(Metadatable metadatable){
        return getMetadata(metadatable, CUSTOM_METADATA2_TAG);
    }
    public static boolean hasCustomMetadata2(Metadatable metadatable){
        return hasMetadata(metadatable, CUSTOM_METADATA2_TAG);
    }
    public static void removeCustomMetadata2(Metadatable metadatable){
        removeMetadata(metadatable, CUSTOM_METADATA2_TAG);
    }
    
    public static void setCustomMetadata3(Metadatable metadatable, Object value) {
        setMetadata(metadatable, CUSTOM_METADATA3_TAG, value);
    }    
    public static <T> T getCustomMetadata3(Metadatable metadatable){
        return getMetadata(metadatable, CUSTOM_METADATA3_TAG);
    }
    public static boolean hasCustomMetadata3(Metadatable metadatable){
        return hasMetadata(metadatable, CUSTOM_METADATA3_TAG);
    }
    public static void removeCustomMetadata3(Metadatable metadatable){
        removeMetadata(metadatable, CUSTOM_METADATA3_TAG);
    }
}
