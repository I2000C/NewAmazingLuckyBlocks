package me.i2000c.newalb.utils2;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import java.util.Optional;
import lombok.NonNull;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.reflection.ReflectionManager;
import me.i2000c.newalb.utils.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class XMaterialUtils {
    public static XMaterial parseXMaterial(String materialNameAndDurability) throws IllegalArgumentException {
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(materialNameAndDurability == null || materialNameAndDurability.trim().isEmpty()) {
            return null;
        }
        
        String[] splitted = materialNameAndDurability.split(":");
        String materialName = splitted[0];
        Integer materialID;
        try {
            materialID = Integer.parseInt(materialName);
        } catch(NumberFormatException ex) {
            materialID = null;
        }
        
        Optional<XMaterial> optionalXMaterial;
        if(materialID == null) {
            optionalXMaterial = XMaterial.matchXMaterial(materialNameAndDurability);
        } else {
            if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()) {
                Logger.warn(String.format("Using material IDs is deprecated and not recommended (materialID: %d)", materialID));
            } else {
                throw new IllegalArgumentException(String.format("Material IDs are only supported in legacy Minecraft versions (1.8-1.12.2) (materialID: %d)", materialID));
            }
            
            ItemStack stack;
            if(splitted.length == 1) {
                stack = new ItemStack(materialID);
            } else {
                stack = new ItemStack(materialID, 1, Short.parseShort(splitted[1]));
            }
            optionalXMaterial = Optional.ofNullable(XMaterial.matchXMaterial(stack));
        }
        
        if(optionalXMaterial.isPresent()) {
            return optionalXMaterial.get();
        } else {
            throw new IllegalArgumentException(String.format("Invalid ItemStack detected: \"%s\"", materialNameAndDurability));
        }
        //</editor-fold>
    }
    
    public static XMaterial getXMaterial(Block block) {
        XMaterial material;
        try {
            material = XMaterial.matchXMaterial(block.getType());
        } catch(IllegalArgumentException ex) {
            // Block is not present in XMaterial list.
            // It could be an externally added block in a Minecraft mod
            return XMaterial.AIR;
        }
        
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()) {
            ItemStack stack = new ItemStack(block.getType());
            BlockState state = block.getState();
            
            short durability;
            if(state instanceof Skull) {
                Skull skull = (Skull) state;
                durability = (short) skull.getSkullType().ordinal();
            } else {
                durability = block.getData();
            }
            
            stack.setDurability(durability);
            try {
                material = XMaterial.matchXMaterial(stack);
            } catch(IllegalArgumentException ex) { }
        }
        
        return material;
    }
    
    public static FallingBlock spawnFallingBlock(Location loc, XMaterial material) {
        Material mat = material.parseMaterial();
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()) {
            byte data = (byte) material.getData();
            return loc.getWorld().spawnFallingBlock(loc, mat, data);
        } else {
            MaterialData data = new MaterialData(mat);
            return loc.getWorld().spawnFallingBlock(loc, data);
        }        
    }
    
    public static boolean isItem(@NonNull XMaterial material) {
        Material mat = material.parseMaterial();
        if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_12)) {
            return mat.isItem();
        } switch (mat) {
            case ACACIA_DOOR:
            case BED_BLOCK:
            case BEETROOT_BLOCK:
            case BIRCH_DOOR:
            case BREWING_STAND:
            case BURNING_FURNACE:
            case CAKE_BLOCK:
            case CARROT:
            case CAULDRON:
            case COCOA:
            case CROPS:
            case DARK_OAK_DOOR:
            case DAYLIGHT_DETECTOR_INVERTED:
            case DIODE_BLOCK_OFF:
            case DIODE_BLOCK_ON:
            case DOUBLE_STEP:
            case DOUBLE_STONE_SLAB2:
            case ENDER_PORTAL:
            case END_GATEWAY:
            case FIRE:
            case FLOWER_POT:
            case FROSTED_ICE:
            case GLOWING_REDSTONE_ORE:
            case IRON_DOOR_BLOCK:
            case JUNGLE_DOOR:
            case LAVA:
            case MELON_STEM:
            case NETHER_WARTS:
            case PISTON_EXTENSION:
            case PISTON_MOVING_PIECE:
            case PORTAL:
            case POTATO:
            case PUMPKIN_STEM:
            case PURPUR_DOUBLE_SLAB:
            case REDSTONE_COMPARATOR_OFF:
            case REDSTONE_COMPARATOR_ON:
            case REDSTONE_LAMP_ON:
            case REDSTONE_TORCH_OFF:
            case REDSTONE_WIRE:
            case SIGN_POST:
            case SKULL:
            case SPRUCE_DOOR:
            case STANDING_BANNER:
            case STATIONARY_LAVA:
            case STATIONARY_WATER:
            case SUGAR_CANE_BLOCK:
            case TRIPWIRE:
            case WALL_BANNER:
            case WALL_SIGN:
            case WATER:
            case WOODEN_DOOR:
            case WOOD_DOUBLE_STEP:
                return false;
            default:
                return true;
        }
    }
    
    public static XEnchantment matchXEnchantment(Enchantment enchantment) {
        if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_21)) {
            // Since Minecraft 1.21 enchantments' names are weird, so their getName() method doesn't work as expected
            NamespacedKey key = ReflectionManager.callMethod(enchantment, "getKey");
            return XEnchantment.matchXEnchantment(key.getKey()).orElse(null);
        } else {
            return XEnchantment.matchXEnchantment(enchantment);
        }
    }
}
