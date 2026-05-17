package me.i2000c.newalb.utils.misc;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import lombok.SneakyThrows;
import me.i2000c.newalb.api.version.MinecraftVersion;
import me.i2000c.newalb.config.Config;

public final class Equipment implements Cloneable {
    
    public static final int DEFAULT_DROP_CHANCE = 50;
    public static final int MIN_DROP_CHANCE = 0;
    public static final int MAX_DROP_CHANCE = 100;
    
    private Map<EquipmentSlot, ItemStack> equipmentItems = new EnumMap<>(EquipmentSlot.class);
    private Map<EquipmentSlot, Integer> equipmentDropChances = new EnumMap<>(EquipmentSlot.class);
    
    public Equipment() {
        reset();
    }
    
    public void reset() {
        for(EquipmentSlot slot : EquipmentSlot.VALUES) {
            equipmentItems.put(slot, null);
            equipmentDropChances.put(slot, DEFAULT_DROP_CHANCE);
        }
    }
    
    public void resetItems() {
        for(EquipmentSlot slot : EquipmentSlot.VALUES) {
            equipmentItems.put(slot, null);
        }
    }
    
    public void resetDropChances() {
        for(EquipmentSlot slot : EquipmentSlot.VALUES) {
            equipmentDropChances.put(slot, DEFAULT_DROP_CHANCE);
        }
    }
    
    public ItemStack getItem(EquipmentSlot slot) {
        return equipmentItems.get(slot);
    }
    
    public void setItem(EquipmentSlot slot, ItemStack stack) {
        if(stack != null && stack.getType() == Material.AIR) {
            stack = null;
        }
        
        ItemStack item = equipmentItems.get(slot);
        if(item != stack) {
            equipmentItems.put(slot, stack != null ? stack.clone() : null);
        }
    }
    
    public int getDropChance(EquipmentSlot slot) {
        return equipmentDropChances.get(slot);
    }
    
    public void setDropChance(EquipmentSlot slot, int dropChance) {
        if(dropChance < MIN_DROP_CHANCE || dropChance > MAX_DROP_CHANCE) {
            throw new IllegalArgumentException("Invalid drop chance: " + dropChance);
        }
        equipmentDropChances.put(slot, dropChance);
    }
    
    public Equipment(Config config, String path) {
        reset();
        
        if(!config.existsPath(path)) {
            return;
        }
        
        for(EquipmentSlot slot : EquipmentSlot.VALUES) {
            String fullPath = path + "." + slot.getConfigKey();
            if(!config.existsPath(fullPath)) {
                continue;
            }
            
            ItemStackWrapper wrapper = config.getItemStackWrapper(fullPath);
            int dropChance = config.getInt(fullPath + ".dropChance", DEFAULT_DROP_CHANCE);
            
            setItem(slot, wrapper.toItemStack());
            setDropChance(slot, dropChance);
        }
    }
    
    public void saveToConfig(Config config, String path) {
        if(!isEmpty()) {
            for(EquipmentSlot slot : EquipmentSlot.VALUES) {
                ItemStack stack = getItem(slot);
                int dropChance = getDropChance(slot);
                
                if(stack != null && stack.getType() != Material.AIR) {
                    String fullPath = path + "." + slot.getConfigKey();
                    ItemStackWrapper wrapper = ItemStackWrapper.fromItem(stack, false);
                    config.set(fullPath, wrapper);                    
                    config.set(fullPath + ".dropChance", dropChance);
                }
            }
        }
    }

    public boolean isEmpty() {
        return equipmentItems.values()
                             .stream()
                             .allMatch(Objects::isNull);
    }
    
    public void applyToEntity(LivingEntity le) {
        if(le instanceof ArmorStand) {
            ArmorStand armorStand = (ArmorStand) le;
            boolean hasItemsInHands = getItem(EquipmentSlot.ITEM_IN_HAND) != null || getItem(EquipmentSlot.ITEM_IN_OFF_HAND) != null;
            armorStand.setArms(hasItemsInHands);
            armorStand.setBasePlate(true);
        }
        
        for(EquipmentSlot slot : EquipmentSlot.VALUES) {
            ItemStack item = getItem(slot);
            if(item != null) {
                item = item.clone();
            }
            float dropChance = getDropChance(slot) / 100f;
            setItem(le, slot, item);
            setDropChance(le, slot, dropChance);
        }
    }
    
    @SuppressWarnings("deprecation")
    private static void setItem(LivingEntity le, EquipmentSlot slot, ItemStack item) {
        EntityEquipment entityEquipment = le.getEquipment();
        switch(slot) {
            case HELMET:
                entityEquipment.setHelmet(item);
                break;
            case CHESTPLATE:
                entityEquipment.setChestplate(item);
                break;
            case LEGGINGS:
                entityEquipment.setLeggings(item);
                break;
            case BOOTS:
                entityEquipment.setBoots(item);
                break;
            case ITEM_IN_HAND:
                if(MinecraftVersion.CURRENT_VERSION.is_1_8()) {
                    entityEquipment.setItemInHand(item);
                } else {
                    entityEquipment.setItemInMainHand(item);
                }
                break;
            case ITEM_IN_OFF_HAND:
                if(!MinecraftVersion.CURRENT_VERSION.is_1_8()) {
                    entityEquipment.setItemInOffHand(item);
                }
                break;
        }
    }
    
    @SuppressWarnings("deprecation")
    private static void setDropChance(LivingEntity le, EquipmentSlot slot, float dropChance) {
        if(!(le instanceof Mob)) {
            return;
        }
        
        EntityEquipment entityEquipment = le.getEquipment();
        switch(slot) {
            case HELMET:
                entityEquipment.setHelmetDropChance(dropChance);
                break;
            case CHESTPLATE:
                entityEquipment.setChestplateDropChance(dropChance);
                break;
            case LEGGINGS:
                entityEquipment.setLeggingsDropChance(dropChance);
                break;
            case BOOTS:
                entityEquipment.setBootsDropChance(dropChance);
                break;
            case ITEM_IN_HAND:
                if(MinecraftVersion.CURRENT_VERSION.is_1_8()) {
                    entityEquipment.setItemInHandDropChance(dropChance);
                } else {
                    entityEquipment.setItemInMainHandDropChance(dropChance);
                }
                break;
            case ITEM_IN_OFF_HAND:
                if(!MinecraftVersion.CURRENT_VERSION.is_1_8()) {
                    entityEquipment.setItemInOffHandDropChance(dropChance);
                }
                break;
        }
    }
    
    @Override
    @SneakyThrows(CloneNotSupportedException.class)
    public Equipment clone() {
        Equipment copy = (Equipment) super.clone();
        copy.equipmentItems = new EnumMap<>(EquipmentSlot.class);
        copy.equipmentDropChances = new EnumMap<>(this.equipmentDropChances);
        
        this.equipmentItems.forEach((slot, item) -> 
            copy.equipmentItems.put(slot, item != null ? item.clone() : null));
        
        return copy;
    }
}
