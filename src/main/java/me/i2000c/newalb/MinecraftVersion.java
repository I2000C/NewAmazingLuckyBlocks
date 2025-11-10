package me.i2000c.newalb;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Bukkit;

@EqualsAndHashCode(of = {"major", "minor", "patch"})
public class MinecraftVersion implements Comparable<MinecraftVersion> {
    
    private static final String VERSION_SEPARATOR = ".";
    private static final String VERSION_SEPARATOR_ESC = "\\" + VERSION_SEPARATOR;
    
    public static final int VERSION_8 =   8;
    public static final int VERSION_9 =   9;
    public static final int VERSION_10 = 10;
    public static final int VERSION_11 = 11;
    public static final int VERSION_12 = 12;
    public static final int VERSION_13 = 13;
    public static final int VERSION_14 = 14;
    public static final int VERSION_15 = 15;
    public static final int VERSION_16 = 16;
    public static final int VERSION_17 = 17;
    public static final int VERSION_18 = 18;
    public static final int VERSION_19 = 19;
    public static final int VERSION_20 = 20;
    public static final int VERSION_21 = 21;
    
    public static final MinecraftVersion v1_8    = new MinecraftVersion(1, VERSION_8, 0);
    public static final MinecraftVersion v1_9    = new MinecraftVersion(1, VERSION_9, 0);
    public static final MinecraftVersion v1_10   = new MinecraftVersion(1, VERSION_10, 0);
    public static final MinecraftVersion v1_11   = new MinecraftVersion(1, VERSION_11, 0);
    public static final MinecraftVersion v1_12   = new MinecraftVersion(1, VERSION_12, 0);
    public static final MinecraftVersion v1_13   = new MinecraftVersion(1, VERSION_13, 0);
    public static final MinecraftVersion v1_14   = new MinecraftVersion(1, VERSION_14, 0);
    public static final MinecraftVersion v1_15   = new MinecraftVersion(1, VERSION_15, 0);
    public static final MinecraftVersion v1_16   = new MinecraftVersion(1, VERSION_16, 0);
    public static final MinecraftVersion v1_17   = new MinecraftVersion(1, VERSION_17, 0);
    public static final MinecraftVersion v1_18   = new MinecraftVersion(1, VERSION_18, 0);
    public static final MinecraftVersion v1_20   = new MinecraftVersion(1, VERSION_20, 0);
    public static final MinecraftVersion v1_20_2 = new MinecraftVersion(1, VERSION_20, 2);
    public static final MinecraftVersion v1_20_5 = new MinecraftVersion(1, VERSION_20, 5);
    public static final MinecraftVersion v1_20_6 = new MinecraftVersion(1, VERSION_20, 6);
    public static final MinecraftVersion v1_21   = new MinecraftVersion(1, VERSION_21, 0);
    public static final MinecraftVersion v1_21_1 = new MinecraftVersion(1, VERSION_21, 1);
    public static final MinecraftVersion v1_21_3 = new MinecraftVersion(1, VERSION_21, 3);
    public static final MinecraftVersion v1_21_4 = new MinecraftVersion(1, VERSION_21, 4);
    public static final MinecraftVersion v1_21_7 = new MinecraftVersion(1, VERSION_21, 7);
    public static final MinecraftVersion v1_21_8 = new MinecraftVersion(1, VERSION_21, 8);
    public static final MinecraftVersion v1_21_9 = new MinecraftVersion(1, VERSION_21, 9);
    public static final MinecraftVersion v1_21_10 = new MinecraftVersion(1, VERSION_21, 10);
    
    public static final MinecraftVersion OLDEST_VERSION = v1_8;
    public static final MinecraftVersion LATEST_VERSION = v1_21_10;
    public static final MinecraftVersion CURRENT_VERSION = getCurrentVersion();
    public static final String CURRENT_BUKKIT_VERSION = getCurrentBukkitVersion();
    
    private final int major;
    private final int minor;
    private final int patch;
    
    @Getter private final boolean legacyVersion;
    @Getter private final boolean newNMS;
    @Getter private final boolean newCraftBukkit;
    @Getter private final boolean _1_8;
    
    private MinecraftVersion(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        
        this.legacyVersion = this.minor <= VERSION_12;
        this.newNMS = this.minor >= VERSION_17;
        this.newCraftBukkit = this.minor > VERSION_20 || (this.minor == VERSION_20 && this.patch >= 5);
        this._1_8 = this.minor == VERSION_8;
    }
    
    private static MinecraftVersion getCurrentVersion() {
        // Bukkit.getServer().getBukkitVersion() returns a value like this: 1.8.8-R0.1-SNAPSHOT
        String version = Bukkit.getServer().getBukkitVersion().split("-")[0];
        return MinecraftVersion.fromString(version);
    }
    
    private static String getCurrentBukkitVersion() {
        if(CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_20_5)) {
            return null;
        } else {
            return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        }
    }
    
    @Override
    public int compareTo(MinecraftVersion other) {
        int majorDifference = this.major - other.major;
        if(majorDifference != 0) return majorDifference;
        
        int minorDifference = this.minor - other.minor;
        if(minorDifference != 0) return minorDifference;
        
        int patchDifference = this.patch - other.patch;
        return patchDifference;
    }
    
    public boolean isLessThan(MinecraftVersion other) {return this.compareTo(other) < 0;}
    public boolean isLessThanOrEqual(MinecraftVersion other) {return this.compareTo(other) <= 0;}
    public boolean isGreaterThan(MinecraftVersion other) {return this.compareTo(other) > 0;}
    public boolean isGreaterThanOrEqual(MinecraftVersion other) {return this.compareTo(other) >= 0;}
    public boolean isEqualTo(MinecraftVersion other) {return this.compareTo(other) == 0;}    
    
    public static MinecraftVersion fromString(String version) {
        if(version == null) {
            return null;
        }
        
        int major, minor, patch;
        String[] split = version.split(VERSION_SEPARATOR_ESC);
        try {
            switch(split.length) {
                case 2:
                    major = Integer.parseInt(split[0]);
                    minor = Integer.parseInt(split[1]);
                    patch = 0;
                    break;
                case 3:
                    major = Integer.parseInt(split[0]);
                    minor = Integer.parseInt(split[1]);
                    patch = Integer.parseInt(split[2]);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid Minecraft version: " + version);
            }
        } catch(NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid Minecraft version: " + version, ex);
        }
        
        MinecraftVersion mcVersion = new MinecraftVersion(major, minor, patch);
        return mcVersion;
    }
    
    @Override
    public String toString() {
        return this.major + VERSION_SEPARATOR + this.minor + (this.patch > 0 ? (VERSION_SEPARATOR + this.patch) : "");
    }
}
