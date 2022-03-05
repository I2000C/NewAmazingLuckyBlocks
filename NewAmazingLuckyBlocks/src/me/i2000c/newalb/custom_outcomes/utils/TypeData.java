package me.i2000c.newalb.custom_outcomes.utils;

import java.util.Objects;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils2.TextureManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class TypeData{
    private final Material material;
    private final short durability;
    private final TextureManager.Texture texture;
    
    public TypeData(ItemStack stack){
        //<editor-fold defaultstate="collapsed" desc="Code">
        this.texture = TextureManager.getTexture(stack);        
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
            if(this.texture == null){
                this.material = processMaterial(stack.getType());
                this.durability = stack.getDurability();                
            }else{
                this.material = null;
                this.durability = 0;
            }
        }else{
            this.material = processMaterial(stack.getType());
            this.durability = 0;
        }
//</editor-fold>
    }    
    public TypeData(Block block){
        //<editor-fold defaultstate="collapsed" desc="Code">
        this.texture = TextureManager.getTexture(block);        
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
            if(this.texture == null){
                this.material = processMaterial(block.getType());
                this.durability = block.getData();                
            }else{
                this.material = null;
                this.durability = 0;
            }
        }else{
            this.material = processMaterial(block.getType());
            this.durability = 0;
        }
//</editor-fold>
    }
    
    private static Material processMaterial(Material material){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String materialName = material.name();
        switch(materialName){
            case "SKULL":
                return Material.valueOf("SKULL_ITEM");
            case "PLAYER_WALL_HEAD":
                return Material.valueOf("PLAYER_HEAD");
            default:
                return material;
        }
//</editor-fold>
    }

    @Override
    public int hashCode(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.material);
        hash = 37 * hash + this.durability;
        hash = 37 * hash + Objects.hashCode(this.texture);
        return hash;
//</editor-fold>
    }

    @Override
    public boolean equals(Object obj){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(this == obj){
            return true;
        }
        
        if(obj == null){
            return false;
        }
        
        if(getClass() != obj.getClass()){
            return false;
        }
        
        final TypeData other = (TypeData) obj;
        
        if(this.durability != other.durability){
            //Durability always be equals (0) in no-legacy versions
            return false;
        }
        
        if(this.material != other.material){
            return false;
        }
        
        return Objects.equals(this.texture, other.texture);
//</editor-fold>
    }
    
    @Override
    public String toString(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return String.format("TypeData{material=%s, durability=%d, texture=%s}",
                material.name(),
                durability,
                texture);
//</editor-fold>
    }
}
