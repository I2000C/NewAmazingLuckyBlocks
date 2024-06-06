package me.i2000c.newalb.utils.textures;

import com.mojang.authlib.GameProfile;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.reflection.RefClass;
import me.i2000c.newalb.reflection.ReflectionManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class TextureManager{
    
    public static Texture getTexture(ItemStack stack){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(stack == null || !stack.hasItemMeta()){
            return null;
        }
        
        ItemMeta meta = stack.getItemMeta();
        if(!(meta instanceof SkullMeta)){
            return null;
        }
        
        GameProfile profile = ReflectionManager.getFieldValue(meta, "profile");
        return new Texture(profile);
//</editor-fold>
    }
    public static boolean setTexture(ItemStack stack, Texture texture){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemMeta meta = stack.getItemMeta();
        if(!(meta instanceof SkullMeta)){
            return false;
        }
        
        GameProfile profile = texture != null ? texture.getProfile() : null;
        if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_20_2)) {
            ReflectionManager.callMethod(meta, "setProfile", profile);
        } else {
            ReflectionManager.setFieldValue(meta, "profile", profile);
        }
        
        stack.setItemMeta(meta);
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()) {
            stack.setDurability((short) 3);
        }
        
        return true;
//</editor-fold>
    }
    public static Texture getTexture(Block block){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!isSkull(block.getType())) {
            return null;
        }
        
        Object tileEntitySkull = block.getState();
        GameProfile profile = ReflectionManager.getFieldValue(tileEntitySkull, "profile");
        
        return new Texture(profile);
//</editor-fold>
    }
    public static boolean setTexture(Block block, Texture texture, boolean force){
        //<editor-fold defaultstate="collapsed" desc="Code">
        boolean isSkull = isSkull(block.getType());
        if(!isSkull) {
            if(texture == null) {
                return false;
            } else if(!force) {
                return false;
            } else {
                block.setType(getBlockSkullMaterial());
                if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()){
                    block.setData((byte) 1);
                }
            }
        }
        
        GameProfile profile = texture != null ? texture.getProfile() : null;
        if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_17)) {
            Object tileEntitySkull = block.getState();
            ReflectionManager.setFieldValue(tileEntitySkull, "profile", profile);
            ReflectionManager.callMethod(tileEntitySkull, "update", true);
        } else {
            RefClass blockPositionClass = ReflectionManager.getCachedNMSClass("net.minecraft.core", "BlockPosition");
            RefClass craftWorldClass = ReflectionManager.getCachedCraftClass("CraftWorld");
            RefClass worldServerClass = ReflectionManager.getCachedNMSClass("net.minecraft.world.level", "World");
            
            Object blockPosition = blockPositionClass.callConstructor(block.getX(), block.getY(), block.getZ());
            Object craftWorld = craftWorldClass.callMethod("getHandle", block.getWorld());
            Object tileEntitySkull = worldServerClass.callMethod("getTileEntity", craftWorld, blockPosition);
            
            ReflectionManager.callMethod(tileEntitySkull, "setGameProfile", profile);
        }
        
        return true;
//</editor-fold>
    }
    
    public static boolean setOwningPlayer(ItemStack stack, Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemMeta meta = stack.getItemMeta();
        if(!(meta instanceof SkullMeta)){
            return false;
        }
        
        SkullMeta sk = (SkullMeta) meta;        
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()){
            sk.setOwner(player.getName());
        }else{
            sk.setOwningPlayer(player);
        }
        
        stack.setItemMeta(sk);
        
        return true;
//</editor-fold>
    }
    
    public static ItemStack getTexturedHead(Texture texture){
        ItemStack stack = getItemSkullStack();
        setTexture(stack, texture);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("&bCustom head");
        stack.setItemMeta(meta);
        return stack;
    }
    public static boolean isSkull(Material m){
        String name = m.name();
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()){
            return name.equals("SKULL") || name.equals("SKULL_ITEM");
        }else{
            return name.equals("PLAYER_HEAD") || name.equals("PLAYER_WALL_HEAD");
        }
    }
    public static Material getBlockSkullMaterial(){
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()){
            return Material.SKULL;
        }else{
            return Material.valueOf("PLAYER_HEAD");
        }
    }
    public static Material getItemSkullMaterial(){
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()){
            return Material.SKULL_ITEM;
        }else{
            return Material.valueOf("PLAYER_HEAD");
        }
    }
    public static ItemStack getItemSkullStack(){
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()){
            return new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        }else{
            return new ItemStack(Material.valueOf("PLAYER_HEAD"));
        }
    }
    public static BlockFace getRotation(Block b){
        BlockState state = b.getState();
        if(state instanceof Skull){
            Skull skull = (Skull) state;
            return skull.getRotation();
        }else{
            return null;
        }
    }
}
