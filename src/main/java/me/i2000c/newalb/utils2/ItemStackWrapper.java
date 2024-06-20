package me.i2000c.newalb.utils2;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.textures.Texture;
import me.i2000c.newalb.utils.textures.TextureException;
import me.i2000c.newalb.utils.textures.TextureManager;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemStackWrapper {
    private final ItemStack item;
    
    public static ItemStackWrapper newItem(@NonNull XMaterial material) {
        if(!XMaterialUtils.isItem(material)) {
            throw new IllegalArgumentException(String.format("Material \"%s\" is not an item", material.name()));
        }
        
        return new ItemStackWrapper(material.parseItem());
    }
    public static ItemStackWrapper newItem(@NonNull String materialNameAndDurability) {
        return newItem(XMaterialUtils.parseXMaterial(materialNameAndDurability));
    }
    public static ItemStackWrapper fromItem(@NonNull ItemStack stack, boolean clone) {
        return new ItemStackWrapper(clone ? stack.clone() : stack);
    }
    public static ItemStackWrapper fromItem(@NonNull ItemStack stack) {
        return fromItem(stack, true);
    }
    
    
    
    public ItemStackWrapper setMaterial(XMaterial material) {
        if(!XMaterialUtils.isItem(material)) {
            throw new IllegalArgumentException(String.format("Material \"%s\" is not an item", material.name()));
        }
        
        material.setType(item);
        return this;
    }
    public XMaterial getMaterial() {
        return XMaterial.matchXMaterial(item);
    }
    
    public ItemStackWrapper setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }
    public int getAmount() {
        return item.getAmount();
    }
    
    public ItemStackWrapper setDurability(int durability) {
        if(durability >= 0) {
            if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion() || durability <= item.getType().getMaxDurability()) {
                item.setDurability((short) durability);
            }
        }
        return this;
    }
    public short getDurability() {
        return item.getDurability();
    }
    
    public ItemStackWrapper setDisplayName(String displayName) {
        ItemMeta meta = item.getItemMeta();
        if(displayName == null || displayName.isEmpty()) {
            meta.setDisplayName(null);
        } else {
            meta.setDisplayName(Logger.color(displayName));
        }
        item.setItemMeta(meta);
        return this;
    }
    public String getDisplayName() {
        ItemMeta meta = item.getItemMeta();
        if(meta.hasDisplayName()) {
            return meta.getDisplayName();
        } else {
            return null;
        }
    }
    public boolean hasDisplayName() {
        return item.getItemMeta().hasDisplayName();
    }
    
    public ItemStackWrapper addLoreLine(String loreLine) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore;
        if(meta.hasLore()) {
            lore = meta.getLore();
        } else {
            lore = new ArrayList<>();
        }
        lore.add(Logger.color(loreLine));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return this;
    }
    public ItemStackWrapper addLore(List<String> loreLines) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore;
        if(meta.hasLore()) {
            lore = meta.getLore();
        } else {
            lore = new ArrayList<>();
        }
        lore.addAll(Logger.color(loreLines));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return this;
    }
    public ItemStackWrapper setLore(String... lore) {
        if(lore == null || lore.length == 0) {
            return setLore((List<String>) null);
        } else {
            return setLore(Arrays.asList(lore));
        }
    }
    public ItemStackWrapper setLore(List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        if(lore == null || lore.isEmpty()) {
            meta.setLore(null);
        } else {
            meta.setLore(Logger.color(lore));
        }
        item.setItemMeta(meta);
        return this;
    }
    public List<String> getLore() {
        ItemMeta meta = item.getItemMeta();
        return meta.hasLore() ? meta.getLore() : null;
    }
    public boolean hasLore() {
        return item.getItemMeta().hasLore();
    }
    
    public ItemStackWrapper addEnchantment(@NonNull Enchantment enchantment, int level) {
        item.addUnsafeEnchantment(enchantment, level);
        return this;
    }
    public ItemStackWrapper setEnchantments(@NonNull Map<Enchantment, Integer> enchantments) {
        clearEnchantments();
        item.addUnsafeEnchantments(enchantments);
        return this;
    }
    public ItemStackWrapper setEnchantments(List<String> enchantments) {
        clearEnchantments();
        enchantments.forEach(enchant -> {
            String[] splitted = enchant.split(";");
            Enchantment enchantment = Enchantment.getByName(splitted[0]);
            Objects.requireNonNull(enchantment);
            
            int level = Integer.parseInt(splitted[1]);
            item.addUnsafeEnchantment(enchantment, level);
        });
        return this;
    }
    public Map<Enchantment, Integer> getEnchantments() {
        return item.getEnchantments();
    }
    public List<String> getEnchantmentsIntoStringList() {
        List<String> enchantments = new ArrayList<>();
        item.getEnchantments().forEach((enchantment, level) -> {
            enchantments.add(enchantment.getName() + ";" + level);
        });
        return enchantments;
    }
    public boolean hasEnchantments() {
        return item.getItemMeta().hasEnchants();
    }
    public ItemStackWrapper clearEnchantments() {
        item.getEnchantments().forEach((enchantment, level) -> item.removeEnchantment(enchantment));
        return this;
    }

    public ItemStackWrapper addBookEnchantment(Enchantment enchantment, int level) {
        Objects.requireNonNull(enchantment);
        
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta esm = (EnchantmentStorageMeta) meta;
            esm.addStoredEnchant(enchantment, level, true);
            item.setItemMeta(esm);
        }
        
        return this;
    }
    public ItemStackWrapper setBookEnchantments(Map<Enchantment, Integer> enchantments) {
        Objects.requireNonNull(enchantments);
        clearBookEnchantments();
        
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta esm = (EnchantmentStorageMeta) meta;
            enchantments.forEach((enchantment, level) ->
                    esm.addStoredEnchant(enchantment, level, true));
            item.setItemMeta(esm);
        }
        
        return this;
    }
    public ItemStackWrapper setBookEnchantments(List<String> enchantments) {
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof EnchantmentStorageMeta) {
            clearBookEnchantments();
            
            EnchantmentStorageMeta esm = (EnchantmentStorageMeta) meta;
            enchantments.forEach(enchant -> {
                String[] splitted = enchant.split(";");
                Enchantment enchantment = Enchantment.getByName(splitted[0]);
                Objects.requireNonNull(enchantment);

                int level = Integer.parseInt(splitted[1]);
                esm.addStoredEnchant(enchantment, level, true);
            });
            item.setItemMeta(esm);
        }
        
        return this;
    }
    public Map<Enchantment, Integer> getBookEnchantments() {
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta esm = (EnchantmentStorageMeta) meta;
            return esm.getStoredEnchants();
        } else {
            return Collections.emptyMap();
        }
    }
    public List<String> getBookEnchantmentsIntoStringList() {
        List<String> enchantments = new ArrayList<>();
        
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta esm = (EnchantmentStorageMeta) meta;
            esm.getStoredEnchants().forEach((enchantment, level) -> {
                enchantments.add(enchantment.getName() + ";" + level);
            });
        }
        
        return enchantments;
    }
    public boolean hasBookEnchantments() {
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta esm = (EnchantmentStorageMeta) meta;
            return esm.hasStoredEnchants();
        } else {
            return false;
        }
    }
    public ItemStackWrapper clearBookEnchantments() {
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta esm = (EnchantmentStorageMeta) meta;
            esm.getStoredEnchants().forEach((enchantment, level) -> esm.removeStoredEnchant(enchantment));
            item.setItemMeta(esm);
        }
        
        return this;
    }
    
    public ItemStackWrapper setOwner(String playerName) {
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof SkullMeta) {
            ((SkullMeta) meta).setOwner(playerName);
            item.setItemMeta(meta);
        }
        return this;
    }
    public ItemStackWrapper setOwner(Player player) {
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof SkullMeta) {
            try {
                ((SkullMeta) meta).setOwningPlayer(player);
            } catch(NoSuchMethodError ex) {
                ((SkullMeta) meta).setOwner(player.getName());
            }
            item.setItemMeta(meta);
        }
        return this;
    }
    
    public ItemStackWrapper setTextureID(String textureID) {
        try{
            Texture texture = new Texture(textureID);
            return setTexture(texture);
        }catch(TextureException ex) {
            Logger.err("An error occurred while setting texture of item:");
            Logger.err(ex);
            return this;
        }
    }
    public ItemStackWrapper setTexture(Texture texture) {
        TextureManager.setTexture(item, texture);
        return this;
    }
    public Texture getTexture() {
        return TextureManager.getTexture(item);
    }
    public boolean hasTexture() {
        return getTexture() != null;
    }
    
    public ItemStackWrapper addPotionEffect(PotionEffect potionEffect) {
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof PotionMeta) {
            ((PotionMeta) meta).addCustomEffect(potionEffect, true);
            item.setItemMeta(meta);
        }
        return this;
    }
    public List<PotionEffect> getPotionEffects() {
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof PotionMeta) {
            return ((PotionMeta) meta).getCustomEffects();
        } else {
            return null;
        }
    }
    public ItemStackWrapper clearPotionEffects() {
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof PotionMeta) {
            ((PotionMeta) meta).clearCustomEffects();
            item.setItemMeta(meta);
        }
        return this;
    }
    
    public ItemStackWrapper setColor(Color color){
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) meta).setColor(color);
        } else if(meta instanceof PotionMeta && MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_11)) {
            ((PotionMeta) meta).setColor(color);
        }
        item.setItemMeta(meta);
        return this;
    }
    public Color getColor(){
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof LeatherArmorMeta) {
            return ((LeatherArmorMeta) meta).getColor();
        } else if(meta instanceof PotionMeta && MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_11)) {
            return ((PotionMeta) meta).getColor();
        }        
        return null;
    }
    public boolean hasColor(){
        return getColor() != null;
    }
    
    public ItemStackWrapper addItemFlags(ItemFlag... itemFlags) {
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(itemFlags);
        item.setItemMeta(meta);
        return this;
    }
    public ItemStackWrapper removeItemFlags(ItemFlag... itemFlags){
        ItemMeta meta = item.getItemMeta();
        meta.removeItemFlags(itemFlags);
        item.setItemMeta(meta);
        return this;
    }
    public boolean hasItemFlag(ItemFlag itemFlag) {
        return item.getItemMeta().hasItemFlag(itemFlag);
    }
    
    public ItemStackWrapper setNbtTag(String tag, String value) {
        NBTUtils.set(item, tag, value);
        return this;
    }
    public ItemStackWrapper setNbtTag(String tag, Integer value) {
        NBTUtils.set(item, tag, value);
        return this;
    }
    public String getStringNbtTag(String tag) {
        return NBTUtils.getString(item, tag);
    }
    public Integer getIntNbtTag(String tag) {
        return NBTUtils.getInt(item, tag);
    }
    public boolean hasNbtTag(String tag) {
        return NBTUtils.contains(item, tag);
    }
    
    public void dropAtLocation(Location loc) {
        dropAtLocation(loc, true);
    }
    public void dropAtLocation(Location loc, boolean dropNaturally) {
        if(dropNaturally) {
            loc.getWorld().dropItemNaturally(loc, item.clone());
        } else {
            loc.getWorld().dropItem(loc, item.clone());
        }
    }
    
    public void placeAt(Block block) {
        XBlock.setType(block, getMaterial());
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()) {
            block.setData((byte) item.getDurability());
        }
        Texture texture = getTexture();
        TextureManager.setTexture(block, texture, true);
    }
    public void placeAt(Location loc) {
        placeAt(loc.getBlock());
    }
    
    public FallingBlock spawnFallingBlock(Location loc) {
        return XMaterialUtils.spawnFallingBlock(loc, getMaterial());
    }
    
    public ItemStack toItemStack() {
        return this.item;
    }
    
    @Override
    public ItemStackWrapper clone() {
        return ItemStackWrapper.fromItem(this.item, true);
    }
    
    @Override
    public String toString() {
        return this.getMaterial().name();
    }
}
