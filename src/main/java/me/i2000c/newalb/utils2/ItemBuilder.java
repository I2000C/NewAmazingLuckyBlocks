package me.i2000c.newalb.utils2;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import me.i2000c.newalb.utils.logger.LogLevel;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils.textures.Texture;
import me.i2000c.newalb.utils.textures.TextureException;
import me.i2000c.newalb.utils.textures.TextureManager;
import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

public class ItemBuilder{
    private final ItemStack item;
    
    private ItemBuilder(XMaterial material){
        this.item = material.parseItem();
    }
    private ItemBuilder(ItemStack item){
        this.item = item;
    }
    
    public static ItemBuilder newItem(XMaterial material){
        return new ItemBuilder(material);
    }
    public static ItemBuilder fromItem(ItemStack item, boolean clone){
        return new ItemBuilder(clone ? item.clone() : item);
    }
    public static ItemBuilder fromItem(ItemStack item){
        return ItemBuilder.fromItem(item, true);
    }
    
    public ItemBuilder withMaterial(XMaterial material){
        material.setType(item);
        return this;
    }
    public XMaterial getMaterial(){
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
        item.setDurability((short) durability);
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
        lore.addAll(loreLines);
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
    
    public ItemBuilder addEnchantment(Enchantment enchantment, int level){
        item.addUnsafeEnchantment(enchantment, level);
        return this;
    }
    public ItemBuilder withEnchantments(Map<Enchantment, Integer> enchantments){
        item.getEnchantments().forEach((enchantment, level) -> item.removeEnchantment(enchantment));
        item.addUnsafeEnchantments(enchantments);
        return this;
    }
    public Map<Enchantment, Integer> getEnchantments(){
        return item.getEnchantments();
    }
    
    public ItemBuilder withTextureID(String textureID){
        try{
            Texture texture = new Texture(textureID);
            return withTexture(texture);
        }catch(TextureException ex){
            Logger.log("An error occurred while setting texture of item:");
            Logger.log(ex, LogLevel.ERROR);
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
    
    public ItemBuilder addPotionEffect(PotionEffect potionEffect){
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof PotionMeta){
            ((PotionMeta) meta).addCustomEffect(potionEffect, true);
            item.setItemMeta(meta);
        }
        return this;
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
            return ((PotionMeta) meta).getColor();
        }else{
            return null;
        }
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
    
    public ItemStack build(){
        return item;
    }
}
