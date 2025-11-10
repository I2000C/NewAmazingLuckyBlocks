package me.i2000c.newalb.config.serializators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Color;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.ItemReward.PotionSplashType;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.textures.Texture;
import me.i2000c.newalb.utils2.CustomColor;
import me.i2000c.newalb.utils2.ItemStackWrapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemStackWrapperSerializator implements ConfigSerializerDeserializer<ItemStackWrapper> {
    
    @Getter private static final ItemStackWrapperSerializator instance = new ItemStackWrapperSerializator();
    
    @Override
    public void serialize(Config config, String path, ItemStackWrapper value) {
        config.set(path + ".material", value.getMaterial());
        config.set(path + ".amount", value.getAmount());
        config.set(path + ".durability", value.getDurability());
        config.set(path + ".unbreakable", value.isUnbreakable());
        if(value.hasDisplayName()) {
            config.set(path + ".name", Logger.deColor(value.getDisplayName()));
        }
        if(value.hasLore()) {
            config.set(path + ".lore", Logger.deColor(value.getLore()));
        }
        if(value.hasEnchantments()) {
            config.set(path + ".enchantments", value.getEnchantmentsIntoStringList());
        }
        if(value.hasBookEnchantments()) {
            config.set(path + ".bookEnchantments", value.getBookEnchantmentsIntoStringList());
        }
        
        // Save texture
        Texture texture = value.getTexture();
        if(texture != null) {
            config.set(path + ".textureID", texture.getId());
        }
        
        // Save leather armor meta
        switch(value.getMaterial()) {
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                String hexColor = new CustomColor(value.getColor()).getHexColorString();
                config.set(path + ".armorColor", hexColor);
        }
        
        // Save potion meta
        XMaterial material = value.getMaterial();
        switch(material) {
            case POTION:
            case SPLASH_POTION:
            case LINGERING_POTION:
            case TIPPED_ARROW:
            case SUSPICIOUS_STEW:
                // Save potion splash type
                if(material.name().contains("POTION")) {
                    PotionSplashType type = PotionSplashType.getFromPotion(value.toItemStack());
                    config.set(path + ".material" , XMaterial.POTION);
                    config.set(path + ".potionSplashType", type.name());
                }
                
                // Save potion color
                if(material != XMaterial.SUSPICIOUS_STEW) {                    
                    if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_11)) {
                        if(value.hasColor()){
                            String hexColor = new CustomColor(value.getColor()).getHexColorString();
                            config.set(path + ".potionColor", hexColor);
                        }
                    }
                }
                
                // Save potion effects
                List<String> effectList = new ArrayList<>();
                value.getPotionEffects().forEach(potionEffect -> {
                    String name = XPotion.matchXPotion(potionEffect.getType()).name();
                    int duration = potionEffect.getDuration() / 20;
                    int amplifier = potionEffect.getAmplifier();

                    if(duration < 0){
                        duration = 0;
                    }
                    if(amplifier < 0){
                        amplifier = 0;
                    }

                    effectList.add(name + ";" + duration + ";" + amplifier);
                });

                config.set(path + ".potionEffects", effectList);
        }
        
        // Save NBT tags
        String nbt = value.exportTagsToString();
        config.set(path + ".nbt", nbt);
        
        // Save item flags
        List<String> flags = value.getItemFlags().stream()
                                                 .map(ItemFlag::name)
                                                 .collect(Collectors.toList());
        if(!flags.isEmpty()) {
            config.set(path + ".flags", flags);
        }
    }
    
    @Override
    public ItemStackWrapper deserialize(Config config, String path) {
        XMaterial material = config.getMaterial(path + ".material");
        ItemStackWrapper value = ItemStackWrapper.newItem(material);
        
        int amount = config.getInt(path + ".amount", 1);
        value.setAmount(amount);
        
        short durability = config.getShort(path + ".durability", (short)0);
        value.setDurability(durability);
        
        boolean unbreakable = config.getBoolean(path + ".unbreakable", false);
        value.setUnbreakable(unbreakable);
        
        String displayName = config.getString(path + ".name", null);
        value.setDisplayName(displayName);
        
        List<String> lore = config.getStringList(path + ".lore", null);
        value.setLore(lore);
        
        List<String> enchantments = config.getStringList(path + ".enchantments", Collections.emptyList());
        value.setEnchantments(enchantments);
        List<String> bookEnchantments = config.getStringList(path + ".bookEnchantments", Collections.emptyList());
        value.setBookEnchantments(bookEnchantments);
        
        // Load armor color
        switch(material){
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                String hexColor = config.getString(path + ".armorColor", null);
                CustomColor color = hexColor != null ? new CustomColor(hexColor) : new CustomColor((Color) null);
                value.setColor(color.getBukkitColor());
                break;
        }
        
        // Load potion meta
        switch(material) {
            case POTION:
            case SPLASH_POTION:
            case LINGERING_POTION:
            case TIPPED_ARROW:
            case SUSPICIOUS_STEW:
                // Load potion splash type
                if(material.name().contains("POTION")) {
                    if(config.existsPath(path + ".potionSplashType")) {
                        PotionSplashType type = config.getEnum(path + ".potionSplashType", PotionSplashType.class);
                        type.setToPotion(value.toItemStack());
                    }
                }
                
                // Load potion color
                if(material != XMaterial.SUSPICIOUS_STEW) {
                    if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_11)) {
                        String hexColor = config.getString(path + ".potionColor", null);
                        CustomColor color = hexColor != null ? new CustomColor(hexColor) : new CustomColor((Color) null);
                        value.setColor(color.getBukkitColor());
                    }
                }
                
                // Load potion effects
                if(config.existsPath(path + ".potionEffects")) {
                    List<String> potionEffects = config.getStringList(path + ".potionEffects");
                    potionEffects.forEach(string -> {
                        String[] splitted = string.split(";");
                        PotionEffectType type = XPotion.matchXPotion(splitted[0]).get().getPotionEffectType();
                        int duration = Integer.parseInt(splitted[1]) * 20;
                        int amplifier = Integer.parseInt(splitted[2]);

                        if(duration < 0) {
                            duration = Integer.MAX_VALUE;
                        }
                        if(amplifier < 0) {
                            amplifier = 0;
                        }

                        value.addPotionEffect(new PotionEffect(type, duration, amplifier));
                    });
                }
        }
        
        PotionSplashType potionSplashType = PotionSplashType.getFromPotion(value.toItemStack());
        if(potionSplashType != null) {
            if(config.existsPath(path + ".potionSplashType")) {
            	potionSplashType = config.getEnum(path + ".potionSplashType", PotionSplashType.class);
            	potionSplashType.setToPotion(value.toItemStack());
            }
            
            if(config.existsPath(path + ".potionEffects")) {
                List<String> potionEffects = config.getStringList(path + ".potionEffects");
                potionEffects.forEach(string -> {
                    String[] splitted = string.split(";");
                    PotionEffectType type = XPotion.matchXPotion(splitted[0]).get().getPotionEffectType();
                    int duration = Integer.parseInt(splitted[1]) * 20;
                    int amplifier = Integer.parseInt(splitted[2]);
                    
                    if(duration < 0) {
                        duration = Integer.MAX_VALUE;
                    }
                    if(amplifier < 0) {
                        amplifier = 0;
                    }
                    
                    value.addPotionEffect(new PotionEffect(type, duration, amplifier));
                });
            }
            if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_11)) {
                String hexColor = config.getString(path + ".armorColor", null);
                CustomColor color = hexColor != null ? new CustomColor(hexColor) : new CustomColor((Color) null);
                value.setColor(color.getBukkitColor());
            }
        }
        
        // Load texture ID
        if(config.existsPath(path + ".textureID")) {
            String textureID = config.getString(path + ".textureID");
            Texture texture = Texture.of(textureID);
            value.setTexture(texture);
        }
        
        // Load NBT tags
        String nbt = config.getString(path + ".nbt", null);
        value.setTagsFromString(nbt);
        
        // Load item flags
        ItemFlag[] flags = config.getStringList(path + ".flags", Collections.emptyList())
                                 .stream()
                                 .map(flagName -> {
                                     try {
                                         return ItemFlag.valueOf(flagName);
                                     } catch(Exception ex) {
                                         return null;
                                     }
                                 })
                                 .filter(flag -> flag != null)
                                 .toArray(ItemFlag[]::new);
        value.setItemFlags(flags);
        
        return value;
    }
}
