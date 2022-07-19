package me.i2000c.newalb.utils2;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import me.i2000c.newalb.utils.Logger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder{
    private final ItemStack item;
    
    private ItemBuilder(XMaterial material){
        this.item = material.parseItem();
    }
    private ItemBuilder(ItemStack item){
        this.item = item.clone();
    }
    
    public static ItemBuilder newItem(XMaterial material){
        return new ItemBuilder(material);
    }
    public static ItemBuilder fromItem(ItemStack item){
        return new ItemBuilder(item);
    }
    
    public ItemBuilder withAmount(int amount){
        item.setAmount(amount);
        
        return this;
    }
    
    public ItemBuilder withDurability(int durability){
        item.setDurability((short) durability);
        
        return this;
    }
    
    public ItemBuilder withDisplayName(String displayName){
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Logger.color(displayName));
        item.setItemMeta(meta);
        
        return this;
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
        return withLore(Arrays.asList(lore));
    }
    public ItemBuilder withLore(List<String> lore){
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Logger.color(lore));
        item.setItemMeta(meta);
        
        return this;
    }
    
    public ItemBuilder addEnchantmnet(Enchantment enchantment, int level){
        item.addUnsafeEnchantment(enchantment, level);
        
        return this;
    }
    public ItemBuilder withEnchantments(Map<Enchantment, Integer> enchantments){
        item.getEnchantments().forEach((enchantment, level) -> item.removeEnchantment(enchantment));
        item.addUnsafeEnchantments(enchantments);
        
        return this;
    }
    
    public ItemBuilder withTextureID(String textureID){
        try{
            TextureManager.Texture texture = new TextureManager.Texture(textureID);
            return withTexture(texture);
        }catch(TextureManager.InvalidHeadException ex){
            return this;
        }
    }
    public ItemBuilder withTexture(TextureManager.Texture texture){
        TextureManager.setTexture(item, texture);
        
        return this;
    }
    
    public ItemStack build(){
        return item;
    }
}
