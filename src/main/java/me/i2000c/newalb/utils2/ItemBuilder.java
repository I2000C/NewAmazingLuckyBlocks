package me.i2000c.newalb.utils2;

import com.cryptomorin.xseries.XMaterial;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.textures.Texture;
import me.i2000c.newalb.utils.textures.TextureException;
import me.i2000c.newalb.utils.textures.TextureManager;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
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

public class ItemBuilder{
    private ItemStack item;
    
    private ItemBuilder(XMaterial material){
        this.item = material.parseItem();
    }
    private ItemBuilder(ItemStack item){
        this.item = item;
    }
    private ItemBuilder(Block block) {
        this.item = new ItemStack(block.getType());
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()) {
            this.item.setDurability(block.getData());
        }
    }
    
    public static ItemBuilder newItem(XMaterial material){
        return new ItemBuilder(material);
    }
    public static ItemBuilder newItem(String materialNameAndDurability){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String[] splitted = materialNameAndDurability.split(":");
        String materialName = splitted[0];
        int materialID = -1;
        
        try{
            materialID = Integer.parseInt(materialName);
            Logger.warn("Using material IDs is deprecated and not recommended (materialID: " + materialID + ")");
        }catch(Exception ex){}
        
        Optional<XMaterial> optionalXMaterial;
        if(materialID == -1){
            optionalXMaterial = XMaterial.matchXMaterial(materialNameAndDurability);
        }else{
            if(splitted.length == 1){
                optionalXMaterial = XMaterial.matchXMaterial(materialID, (byte) 0);
            }else{
                optionalXMaterial = XMaterial.matchXMaterial(materialID, Byte.parseByte(splitted[1]));
            }
        }
        
        if(optionalXMaterial.isPresent()){
            return ItemBuilder.newItem(optionalXMaterial.get());
        }else{
            throw new IllegalArgumentException("Invalid ItemStack detected: " + materialNameAndDurability);
        }
//</editor-fold>
    }
    public static ItemBuilder fromItem(ItemStack item, boolean clone){
        return new ItemBuilder(clone ? item.clone() : item);
    }
    public static ItemBuilder fromItem(ItemStack item){
        return ItemBuilder.fromItem(item, true);
    }
    public static ItemBuilder fromBlock(Block block) {
        return new ItemBuilder(block);
    }
    public static ItemBuilder fromBlock(Location loc) {
        return new ItemBuilder(loc.getBlock());
    }
    
    public ItemBuilder withMaterial(XMaterial material){
        material.setType(item);
        return this;
    }
    public XMaterial getXMaterial(){
        return XMaterial.matchXMaterial(item);
    }
    
    public ItemBuilder withAmount(int amount){
        item.setAmount(amount);
        return this;
    }
    public int getAmount(){
        return item.getAmount();
    }
    
    public ItemBuilder withDurability(int durability){
        if(durability >= 0 && durability <= item.getType().getMaxDurability()){
            item.setDurability((short) durability);
        }
        
        return this;
    }
    public short getDurability(){
        return item.getDurability();
    }
    
    public ItemBuilder withDisplayName(String displayName){
        ItemMeta meta = item.getItemMeta();
        if(displayName == null || displayName.isEmpty()){
            meta.setDisplayName(null);
        }else{
            meta.setDisplayName(Logger.color(displayName));
        }        
        item.setItemMeta(meta);
        return this;
    }
    public String getDisplayName(){
        ItemMeta meta = item.getItemMeta();
        if(meta.hasDisplayName()){
            return meta.getDisplayName();
        }else{
            return null;
        }
    }
    public boolean hasDisplayName(){
        return item.getItemMeta().hasDisplayName();
    }
    
    public ItemBuilder addLoreLine(String loreLine){
        ItemMeta meta = item.getItemMeta();
        List<String> lore;
        if(meta.hasLore()){
            lore = meta.getLore();
        }else{
            lore = new ArrayList<>();
        }
        lore.add(Logger.color(loreLine));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return this;
    }
    public ItemBuilder addLore(List<String> loreLines){
        ItemMeta meta = item.getItemMeta();
        List<String> lore;
        if(meta.hasLore()){
            lore = meta.getLore();
        }else{
            lore = new ArrayList<>();
        }
        lore.addAll(Logger.color(loreLines));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return this;
    }
    public ItemBuilder withLore(String... lore){
        if(lore == null || lore.length == 0){
            return withLore((List<String>) null);
        }else{
            return withLore(Arrays.asList(lore));
        }        
    }
    public ItemBuilder withLore(List<String> lore){
        ItemMeta meta = item.getItemMeta();
        if(lore == null || lore.isEmpty()){
            meta.setLore(null);
        }else{
            meta.setLore(Logger.color(lore));
        }        
        item.setItemMeta(meta);
        return this;
    }
    public List<String> getLore(){
        ItemMeta meta = item.getItemMeta();
        if(meta.hasLore()){
            return meta.getLore();
        }else{
            return null;
        }
    }
    public boolean hasLore(){
        return item.getItemMeta().hasLore();
    }
    
    public ItemBuilder addEnchantment(Enchantment enchantment, int level){
        Objects.requireNonNull(enchantment);
        
        item.addUnsafeEnchantment(enchantment, level);
        return this;
    }
    public ItemBuilder withEnchantments(Map<Enchantment, Integer> enchantments){
        clearEnchantments();
        item.addUnsafeEnchantments(enchantments);
        return this;
    }
    public ItemBuilder withEnchantments(List<String> enchantments){
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
    public Map<Enchantment, Integer> getEnchantments(){
        return item.getEnchantments();
    }
    public List<String> getEnchantmentsIntoStringList(){
        List<String> enchantments = new ArrayList<>();
        item.getEnchantments().forEach((enchantment, level) -> {
            enchantments.add(enchantment.getName() + ";" + level);
        });
        return enchantments;
    }
    public boolean hasEnchantments(){
        return item.getItemMeta().hasEnchants();
    }
    public ItemBuilder clearEnchantments(){
        item.getEnchantments().forEach((enchantment, level) -> item.removeEnchantment(enchantment));
        return this;
    }
    
    public ItemBuilder addBookEnchantment(Enchantment enchantment, int level){
        Objects.requireNonNull(enchantment);
        
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta esm = (EnchantmentStorageMeta) meta;
            esm.addStoredEnchant(enchantment, level, true);
            item.setItemMeta(esm);
        }
        
        return this;
    }
    public ItemBuilder withBookEnchantments(Map<Enchantment, Integer> enchantments){
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
    public ItemBuilder withBookEnchantments(List<String> enchantments){
        clearBookEnchantments();
        
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof EnchantmentStorageMeta) {
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
    public Map<Enchantment, Integer> getBookEnchantments(){
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta esm = (EnchantmentStorageMeta) meta;
            return esm.getStoredEnchants();
        } else {
            return Collections.EMPTY_MAP;
        }
    }
    public List<String> getBookEnchantmentsIntoStringList(){
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
    public boolean hasBookEnchantments(){
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta esm = (EnchantmentStorageMeta) meta;
            return esm.hasStoredEnchants();
        } else {
            return false;
        }
    }
    public ItemBuilder clearBookEnchantments(){
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta esm = (EnchantmentStorageMeta) meta;
            esm.getStoredEnchants().forEach((enchantment, level) -> esm.removeStoredEnchant(enchantment));
            item.setItemMeta(esm);
        }
        
        return this;
    }
    
    public ItemBuilder withOwner(String playerName){
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof SkullMeta){
            ((SkullMeta) meta).setOwner(playerName);
            item.setItemMeta(meta);
        }
        return this;
    }
    public ItemBuilder withOwner(Player player){
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof SkullMeta){
            try {
                ((SkullMeta) meta).setOwningPlayer(player);
            } catch(NoSuchMethodError ex) {
                ((SkullMeta) meta).setOwner(player.getName());
            }
            
            item.setItemMeta(meta);
        }
        return this;
    }
    public ItemBuilder withTextureID(String textureID){
        try{
            Texture texture = new Texture(textureID);
            return withTexture(texture);
        }catch(TextureException ex){
            Logger.err("An error occurred while setting texture of item:");
            Logger.err(ex);
            return this;
        }
    }
    public ItemBuilder withTexture(Texture texture){
        TextureManager.setTexture(item, texture);
        return this;
    }
    public Texture getTexture(){
        return TextureManager.getTexture(item);
    }
    public boolean hasTexture(){
        return getTexture() != null;
    }
    
    public ItemBuilder addPotionEffect(PotionEffect potionEffect){
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof PotionMeta){
            ((PotionMeta) meta).addCustomEffect(potionEffect, true);
            item.setItemMeta(meta);
        }
        return this;
    }
    public List<PotionEffect> getPotionEffects(){
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof PotionMeta){
            return ((PotionMeta) meta).getCustomEffects();
        }else{
            return null;
        }
    }
    public ItemBuilder clearPotionEffects(){
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof PotionMeta){
            ((PotionMeta) meta).clearCustomEffects();
            item.setItemMeta(meta);
        }
        return this;
    }
    
    public ItemBuilder withColor(Color color){
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof LeatherArmorMeta){
            ((LeatherArmorMeta) meta).setColor(color);
        }else if(meta instanceof PotionMeta){
            ((PotionMeta) meta).setColor(color);
        }
        item.setItemMeta(meta);
        return this;
    }
    public Color getColor(){
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof LeatherArmorMeta){
            return ((LeatherArmorMeta) meta).getColor();
        }else if(meta instanceof PotionMeta){
            if(MinecraftVersion.CURRENT_VERSION.compareTo(MinecraftVersion.v1_11) >= 0){
                return ((PotionMeta) meta).getColor();
            }else{
                return null;
            }            
        }else{
            return null;
        }
    }
    public boolean hasColor(){
        return getColor() != null;
    }
    
    public ItemBuilder addItemFlags(ItemFlag... itemFlags){
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(itemFlags);
        item.setItemMeta(meta);
        return this;
    }
    public ItemBuilder removeItemFlags(ItemFlag... itemFlags){
        ItemMeta meta = item.getItemMeta();
        meta.removeItemFlags(itemFlags);
        item.setItemMeta(meta);
        return this;
    }
    public boolean hasItemFlag(ItemFlag itemFlag){
        return item.getItemMeta().hasItemFlag(itemFlag);
    }
    
    public ItemBuilder setNbtTag(Object value, Object... tags) {
        item = NBTEditor.set(item, value, tags);
        return this;
    }
    
    public void placeAt(Block block) {
        block.setType(item.getType());
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()) {
            block.setData((byte) item.getDurability());
        }
    }
    public void placeAt(Location loc) {
        placeAt(loc.getBlock());
    }
    
    public FallingBlock spawnFallingBlock(Location loc) {
        Material material = item.getType();
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()) {
            byte data = (byte) item.getDurability();
            return loc.getWorld().spawnFallingBlock(loc, material, data);
        } else {
            return loc.getWorld().spawnFallingBlock(loc, material, (byte) 0);
        }        
    }
    
    public ItemStack build(){
        return item;
    }
    
    @Override
    public String toString(){
        return this.getXMaterial().name();
    }
}
