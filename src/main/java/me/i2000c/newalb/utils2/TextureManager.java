package me.i2000c.newalb.utils2;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.Logger;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.i2000c.newalb.MinecraftVersion;
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
    private static Constructor blockPostitionConstructor;
    private static Method getWorldHandle;
    private static Method getTileEntity;
    
    private static Method getGameProfile;
    private static Method setGameProfile;
    
    private static Field profileFieldItem;
    private static Field profileFieldBlock;
    
    private static Method update;
    
    static{
        try{
            //https://www.spigotmc.org/threads/tutorial-reflection.147407/
            Class blockPositionClass = OtherUtils.getNMSClass("net.minecraft.core", "BlockPosition");
            blockPostitionConstructor = blockPositionClass.getConstructor(int.class, int.class, int.class);

            getWorldHandle = OtherUtils.getCraftClass("CraftWorld").getMethod("getHandle");
            
            Class worldServerClass = OtherUtils.getNMSClass("net.minecraft.world.level", "World");
            //Class worldServerClass = OtherUtils.getNMSClass("net.minecraft.server.level", "WorldServer");
            if(NewAmazingLuckyBlocks.getMinecraftVersion().compareTo(MinecraftVersion.v1_18) >= 0){
                // In Minecraft 1.18 these classes' name have been changed
                // Go to "net.minecraft.world.level" and search "c_"
                getTileEntity = worldServerClass.getMethod("c_", blockPositionClass);
            }else{
                getTileEntity = worldServerClass.getMethod("getTileEntity", blockPositionClass);
            }
            
            try{
                getGameProfile = OtherUtils.getNMSClass("", "TileEntitySkull").getMethod("getGameProfile");
                setGameProfile = OtherUtils.getNMSClass("", "TileEntitySkull").getMethod("setGameProfile", GameProfile.class);
            }catch(ClassNotFoundException | NoSuchMethodException | SecurityException ex){
                getGameProfile = null;
                setGameProfile = null;
            }
            
            profileFieldItem = null;
            profileFieldBlock = null;
            
            update = null;
        }catch(Exception ex){
            Logger.log("An error ocurred while enabling TextureManager:", Logger.LogLevel.ERROR);
            ex.printStackTrace();
        }        
    }
    
    public static Texture getTexture(ItemStack stack){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemMeta meta = stack.getItemMeta();
        if(!(meta instanceof SkullMeta)){
            return null;
        }
        
        SkullMeta skMeta = (SkullMeta) meta;
        
        try{
            if(profileFieldItem == null){
                long timeA = System.nanoTime();
                profileFieldItem = meta.getClass().getDeclaredField("profile");
                profileFieldItem.setAccessible(true);
                long timeB = System.nanoTime();
                Logger.log((timeB - timeA) + " ns");
            }
            GameProfile profile = (GameProfile) profileFieldItem.get(skMeta);
            return new Texture(profile);
        }catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex){
            return null;
        }
//</editor-fold>
    }
    public static boolean setTexture(ItemStack stack, Texture texture){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemMeta meta = stack.getItemMeta();
        if(!(meta instanceof SkullMeta)){
            return false;
        }
        
        try{
            SkullMeta sk = (SkullMeta) meta;            
            if(profileFieldItem == null){
                profileFieldItem = meta.getClass().getDeclaredField("profile");
                profileFieldItem.setAccessible(true);
            }
            profileFieldItem.set(meta, texture == null ? null : texture.getProfile());
            
            stack.setItemMeta(sk);
            if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                stack.setDurability((short) 3);
            }
            
            return true;
        }catch (Exception ex){
            return false;
        }
//</editor-fold>
    }
    public static Texture getTexture(Block block){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(isSkull(block.getType())){
            try{
                Object tileEntitySkull;
                GameProfile profile;
                if(getGameProfile != null){
                    Object blockPosition = blockPostitionConstructor.newInstance(block.getX(), block.getY(), block.getZ());
                    Object world = getWorldHandle.invoke(block.getWorld());
                    tileEntitySkull = getTileEntity.invoke(world, blockPosition);
                    
                    profile = (GameProfile) getGameProfile.invoke(tileEntitySkull);
                }else{
                    tileEntitySkull = block.getState();
                    if(profileFieldBlock == null){
                        try{
                            profileFieldBlock = tileEntitySkull.getClass().getDeclaredField("profile");
                        }catch(NoSuchFieldException ex){
                            profileFieldBlock = tileEntitySkull.getClass().getDeclaredField("gameProfile");
                        }
                        profileFieldBlock.setAccessible(true);
                    }
                    profile = (GameProfile) profileFieldBlock.get(tileEntitySkull);
                }
                return new Texture(profile);
            }catch(Exception ex){
                //block is not a head block
                return null;
            }
        }else{
            return null;
        }
//</editor-fold>
    }
    public static boolean setTexture(Block block, Texture texture, boolean force){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!isSkull(block.getType())){
            return false;
        }
        
        try{            
            if(texture != null && force && !isSkull(block.getType())){
                block.setType(getBlockSkullMaterial());
                if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                    block.setData((byte) 1);
                }
            }
            
            Object tileEntitySkull;
            if(setGameProfile != null){
                Object blockPosition = blockPostitionConstructor.newInstance(block.getX(), block.getY(), block.getZ());
                Object world = getWorldHandle.invoke(block.getWorld());
                tileEntitySkull = getTileEntity.invoke(world, blockPosition);
                
                setGameProfile.invoke(tileEntitySkull, texture == null ? null : texture.getProfile());
            }else{
                tileEntitySkull = block.getState();
                if(profileFieldBlock == null){
                    try{
                        profileFieldBlock = tileEntitySkull.getClass().getDeclaredField("profile");
                    }catch(NoSuchFieldException ex){
                        profileFieldBlock = tileEntitySkull.getClass().getDeclaredField("gameProfile");
                    }
                    profileFieldBlock.setAccessible(true);
                }
                profileFieldBlock.set(tileEntitySkull, texture == null ? null : texture.getProfile());
                
                if(update == null){
                    update = tileEntitySkull.getClass().getMethod("update", boolean.class);
                }                    
                update.invoke(tileEntitySkull, true);
            }
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
//</editor-fold>
    }
    
    public static boolean setOwningPlayer(ItemStack stack, Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemMeta meta = stack.getItemMeta();
        if(!(meta instanceof SkullMeta)){
            return false;
        }
        
        SkullMeta sk = (SkullMeta) meta;
        
        if(true || NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
            sk.setOwner(player.getName());
        }else{
            try{
                Class playerProfileClass = Class.forName("org.bukkit.profile.PlayerProfile");
                Method getPlayerProfile = player.getClass().getMethod("getPlayerProfile");
                Object playerProfile = getPlayerProfile.invoke(player);
                Method setPlayerProfile = sk.getClass().getMethod("setOwnerProfile", playerProfileClass);
                setPlayerProfile.setAccessible(true);
                setPlayerProfile.invoke(sk, playerProfile);
            }catch(Exception ex){
                ex.printStackTrace();
                for(Method method : sk.getClass().getMethods()){
                    if(method.getName().contains("setOwnerProfile")){
                        Logger.log(method.toGenericString());
                    }                   
                }
                return false;
            }                
        }
        
        stack.setItemMeta(sk);
        
        return true;
//</editor-fold>
    }
    
    public static ItemStack getTexturedHead(Texture texture){
        ItemStack stack = getItemSkullStack();
        setTexture(stack, texture);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Logger.color("&bCustom head"));
        stack.setItemMeta(meta);
        return stack;
    }
    public static boolean isSkull(Material m){
        String name = m.name();
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
            return name.equals("SKULL") || name.equals("SKULL_ITEM");
        }else{
            return name.equals("PLAYER_HEAD") || name.equals("PLAYER_WALL_HEAD");
        }
    }
    public static Material getBlockSkullMaterial(){
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
            return Material.SKULL;
        }else{
            return Material.valueOf("PLAYER_HEAD");
        }
    }
    public static Material getItemSkullMaterial(){
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
            return Material.SKULL_ITEM;
        }else{
            return Material.valueOf("PLAYER_HEAD");
        }
    }
    public static ItemStack getItemSkullStack(){
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
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
    
    public static class InvalidHeadException extends Exception{        
    }
    
    public static final class Texture{
        //<editor-fold defaultstate="collapsed" desc="Code">
        private String ID;
        private GameProfile profile;
        
        public Texture(String ID) throws InvalidHeadException{
            try{
                String textureURL = "http://textures.minecraft.net/texture/" + ID;
                URL url = new URL(textureURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int code = connection.getResponseCode();
                if(code == 404){
                    throw new InvalidHeadException();
                }
                
                this.ID = ID;
                this.profile = new GameProfile(UUID.randomUUID(), "CustomHead");
                byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", textureURL).getBytes());
                this.profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
            }catch(IOException ex){
                this.profile = null;
                this.ID = null;                
            }
        }
        
        public Texture(GameProfile profile){
            this.profile = profile;
            this.ID = getIDFromProfile(profile);
        }
        
        public GameProfile getProfile(){
            return this.profile;
        }
        
        public String getEncodedTexture(){
            try{
                return this.profile.getProperties().get("textures").iterator().next().getValue();
            }catch(Exception ex){
                return null;
            }
        }
        
        public String getURL(){
            try{
                String preUrl = new String(Base64.getDecoder().decode(this.getEncodedTexture().getBytes()));
                Pattern p = Pattern.compile("\"([^\"]*)\"");
                Matcher m = p.matcher(preUrl);
                if(m.find()){
                    return m.group(1);
                }else{
                    return null;
                }                
            }catch(Exception ex){
                return null;
            }            
        }
        
        public String getIDFromProfile(GameProfile profile){
            try{
                String url = this.getURL();
                Pattern p = Pattern.compile("/([^/]*)$");
                Matcher m = p.matcher(url);
                if(m.find()){
                    return m.group(1);
                }else{
                    return null;
                }
            }catch(Exception ex){
                return null;
            }
        }
        
        public String getID(){
            return this.ID;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Texture other = (Texture) obj;
            return Objects.equals(this.ID, other.ID);
        }
        
        @Override
        public int hashCode() {
            int hash = 5;
            hash = 47 * hash + Objects.hashCode(this.ID);
            return hash;
        }
        
        @Override
        public String toString(){
            return this.getID();
        }
//</editor-fold>
    }
}
