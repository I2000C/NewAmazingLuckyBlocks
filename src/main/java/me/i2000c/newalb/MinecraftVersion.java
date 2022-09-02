package me.i2000c.newalb;

import org.bukkit.Bukkit;

public enum MinecraftVersion{
    v1_8(true, false),
    v1_9(true, false),
    v1_10(true, false),
    v1_11(true, false),
    v1_12(true, false),
    v1_13(false, false),
    v1_14(false, false),
    v1_15(false, false),
    v1_16(false, false),
    v1_17(false, true),
    v1_18(false, true),
    v1_19(false, true);
    
    private static final MinecraftVersion[] VALUES = values();
    
    public static MinecraftVersion getCurrentVersion(){
        String version = Bukkit.getServer().getClass().getPackage().getName();
        version = version.substring(version.lastIndexOf('.') + 1);
        version = version.substring(0, version.lastIndexOf('_'));
        return valueOf(version);
    }
    
    private MinecraftVersion(boolean isLegacyVersion, boolean isNewNMS){
        this.isLegacyVersion = isLegacyVersion;
        this.isNewNMS = isNewNMS;
    }
    
    private final boolean isLegacyVersion;    
    private final boolean isNewNMS;
    
    public boolean isLegacyVersion(){
        return isLegacyVersion;
    }    
    public boolean isNewNMS(){
        return isNewNMS;
    }
    
    public static MinecraftVersion getOldestVersion(){
        return VALUES[0];
    }
    public static MinecraftVersion getLatestVersion(){
        return VALUES[VALUES.length - 1];
    }
    
    public static MinecraftVersion fromDouble(double version){
        String versionName = String.valueOf(version);
        versionName = versionName.replace('.', '_');
        try{
            return valueOf("v" + versionName);
        }catch(IllegalArgumentException ex){
            throw new IllegalArgumentException("MinecraftVersion " + version + " doesn't exist");
        }        
    }
    
    public double toDouble(){
        return Double.parseDouble(this.name().replace("v", "").replace('_', '.'));
    }
    
    @Override
    public String toString(){
        return this.name().replace("v", "").replace('_', '.');
    }
}
