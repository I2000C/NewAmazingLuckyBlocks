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

public class ItemStackBuilder{
    private final ItemStack item;
    
    private ItemStackBuilder(XMaterial material){
        this.item = material.parseItem();
    }
    private ItemStackBuilder(ItemStack item){
        this.item = item;
    }
    
    public static ItemStackBuilder createNewItem(XMaterial material){
        return new ItemStackBuilder(material);
    }
    public static ItemStackBuilder fromItem(ItemStack item){
        return new ItemStackBuilder(item);
    }
    
    public ItemStackBuilder withAmount(int amount){
        item.setAmount(amount);
        
        return this;
    }
    
    public ItemStackBuilder withDurability(int durability){
        item.setDurability((short) durability);
        
        return this;
    }
    
    public ItemStackBuilder withDisplayName(String displayName){
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Logger.color(displayName));
        item.setItemMeta(meta);
        
        return this;
    }
    
    public ItemStackBuilder addLoreLine(String loreLine){
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
    public ItemStackBuilder addLore(List<String> loreLines){
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
    public ItemStackBuilder withLore(String... lore){
        return withLore(Arrays.asList(lore));
    }
    public ItemStackBuilder withLore(List<String> lore){
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Logger.color(lore));
        item.setItemMeta(meta);
        
        return this;
    }
    
    public ItemStackBuilder addEnchantmnet(Enchantment enchantment, int level){
        item.addUnsafeEnchantment(enchantment, level);
        
        return this;
    }
    public ItemStackBuilder withEnchantments(Map<Enchantment, Integer> enchantments){
        item.getEnchantments().forEach((enchantment, level) -> item.removeEnchantment(enchantment));
        item.addUnsafeEnchantments(enchantments);
        
        return this;
    }
    
    public ItemStackBuilder withTextureID(String textureID){
        try{
            TextureManager.Texture texture = new TextureManager.Texture(textureID);
            return withTexture(texture);
        }catch(TextureManager.InvalidHeadException ex){
            return this;
        }
    }
    public ItemStackBuilder withTexture(TextureManager.Texture texture){
        TextureManager.setTexture(item, texture);
        
        return this;
    }
    
    public ItemStack build(){
        return item;
    }
}
