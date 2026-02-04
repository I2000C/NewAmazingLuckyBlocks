package me.i2000c.newalb.utils.textures;

import java.util.Base64;

import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.exceptions.ProfileException;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import com.google.gson.Gson;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

@Getter
@EqualsAndHashCode(of = "id")
public final class Texture {
    
    public static final Texture DEFAULT_TEXTURE = new Texture(null);
    private static final Gson GSON = new Gson();
    
    private String id;
    private Profileable profileable;
    
    private Texture(Profileable profileable) {
        if(profileable == null || profileable.getProfileValue() == null) {
            this.id = null;
            this.profileable = null;
        } else {
            if(ConfigManager.getMainConfig().getBoolean("CheckIfTexturesAreValid")) {
                ProfileException ex = profileable.test();
                if(ex != null) {
                    throw ex;
                }
            }
            
            String profileValue = profileable.getProfileValue();
            try {
                String profileValueJson = new String(Base64.getDecoder().decode(profileValue));
                TextureProfile textureProfile = GSON.fromJson(profileValueJson, TextureProfile.class);
                String id = textureProfile.getSkinId();
                if(id != null) {
                    this.id = id;
                    this.profileable = profileable;
                } else {
                    this.id = null;
                    this.profileable = null;
                }
            } catch(Exception ex) {
                this.id = profileValue;
                this.profileable = profileable;
            }
        }
    }
    
    public ItemStack createItem() {
        ItemStack item = XSkull.createItem().removeProfile();
        this.apply(item);
        return item;
    }
    
    public void apply(ItemStack item) {
        if(!isSkull(item)) {
            return;
        }
        
        if(profileable != null) {
            try {
                XSkull.of(item).profile(profileable).apply();
            } catch(Exception ex) {
                XSkull.of(item).removeProfile();
            }
        } else {
            XSkull.of(item).removeProfile();
        }
    }
    
    public void apply(Block block, boolean force) {
        if(!isSkull(block)) {
            if(force) {
                ItemStackWrapper.newItem(XMaterial.PLAYER_HEAD).placeAt(block);
            } else {
                return;
            }
        }
        
        if(profileable != null) {
            try {
                XSkull.of(block).profile(profileable).apply();
            } catch(Exception ex) {
                XSkull.of(block).removeProfile();
            }
        } else {
            XSkull.of(block).removeProfile();
        }
    }
    
    private static Texture of(Profileable profileable) {
        if(profileable == null) {
            return null;
        }
        
        Texture texture = new Texture(profileable);
        if(texture == null || texture.getId() == null) {
            return null;
        } else {
            return texture;
        }
    }
    
    public static Texture of(String id) {
        if(id == null) {
            return null;
        } else {
            Texture texture = new Texture(Profileable.detect(id));
            if(texture == null || texture.getId() == null) {
                return null;
            } else {
                return texture;
            }
        }
    }
    
    public static Texture of(Player player) {
        return Texture.of(Profileable.of(player));
    }
    
    public static Texture of(ItemStack item) {
        if(isSkull(item)) {
            return Texture.of(Profileable.of(item));
        } else {
            return null;
        }
    }
    
    public static Texture of(Block block) {
        if(isSkull(block)) {
            return Texture.of(Profileable.of(block));
        } else {
            return null;
        }
    }
    
    public static boolean isSkull(ItemStack item) {
        if(item == null) {
            return false;
        }
        
        if(item.getItemMeta() instanceof SkullMeta) {
            return true;
        }
        
        String materialName = item.getType().name().toUpperCase();
        return materialName.equals("SKULL_ITEM") || materialName.equals("PLAYER_HEAD");
    }
    
    public static boolean isSkull(Block block) {
        return block != null && block.getState() instanceof Skull;
    }
}
