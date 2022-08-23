package me.i2000c.newalb.utils2;

import com.cryptomorin.xseries.XMaterial;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import me.i2000c.newalb.custom_outcomes.rewards.Displayable;
import me.i2000c.newalb.utils.Logger;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

@SerializableAs("Offset")
public class Offset implements ConfigurationSerializable, Displayable, Cloneable{
    private Range offsetX;
    private Range offsetY;
    private Range offsetZ;
    
    public Offset(FileConfiguration config, String path){
        Range rangeX = Range.fromString(config.getString(path + ".offsetX"));
        Range rangeY = Range.fromString(config.getString(path + ".offsetY"));
        Range rangeZ = Range.fromString(config.getString(path + ".offsetZ"));
        
        this.offsetX = rangeX;
        this.offsetY = rangeY;
        this.offsetZ = rangeZ;
    }
    public void saveToConfig(FileConfiguration config, String path){
        config.set(path + ".offsetX", this.offsetX.toString());
        config.set(path + ".offsetY", this.offsetY.toString());
        config.set(path + ".offsetZ", this.offsetZ.toString());
    }
    
    public Offset(String offsetX, String offsetY, String offsetZ){
        this.offsetX = Range.fromString(offsetX);
        this.offsetY = Range.fromString(offsetY);
        this.offsetZ = Range.fromString(offsetZ);
    }
    
    public Offset(Range offsetX, Range offsetY, Range offsetZ){
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }
    
    public Offset(){
        this(new Range(), new Range(), new Range());
    }
    
    public void setOffsetX(Range offsetX){
        this.offsetX = offsetX;
    }
    public Range getOffsetX(){
        return this.offsetX;
    }
    
    public void setOffsetY(Range offsetY){
        this.offsetY = offsetY;
    }
    public Range getOffsetY(){
        return this.offsetY;
    }
    
    public void setOffsetZ(Range offsetZ){
        this.offsetZ = offsetZ;
    }
    public Range getOffsetZ(){
        return this.offsetZ;
    }
    
    public Location addToLocation(Location loc){
        return loc.add(offsetX.getRandomInt(), offsetY.getRandomInt(), offsetZ.getRandomInt());
    }

    @Override
    public Map<String, Object> serialize(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("offsetX", offsetX.toString());
        map.put("offsetY", offsetY.toString());
        map.put("offsetZ", offsetZ.toString());
        
        return map;
    }
    
    public static Offset deserialize(Map<String, Object> map){
        Range offsetX;
        Range offsetY;
        Range offsetZ;
        try{
            offsetX = Range.fromString(map.get("offsetX").toString());
        }catch(IllegalArgumentException ex){
            Logger.warn("Invalid offset: " + map.get("offsetX").toString());
            Logger.warn("It will be converted to '0'");
            offsetX = new Range();
        }
        
        try{
            offsetY = Range.fromString(map.get("offsetY").toString());
        }catch(IllegalArgumentException ex){
            Logger.err("Invalid offset: " + map.get("offsetY").toString());
            Logger.err("It will be converted to '0'");
            offsetY = new Range();
        }
        
        try{
            offsetZ = Range.fromString(map.get("offsetZ").toString());
        }catch(IllegalArgumentException ex){
            Logger.err("Invalid offset: " + map.get("offsetZ").toString());
            Logger.err("It will be converted to '0'");
            offsetZ = new Range();
        }
        
        return new Offset(offsetX, offsetY, offsetZ);
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        return ItemBuilder.newItem(XMaterial.PISTON)
                .withDisplayName("&3Configure offset")
                .addLoreLine("&dCurrent offset:")
                .addLoreLine("   &5X: &3" + offsetX)
                .addLoreLine("   &5Y: &3" + offsetY)
                .addLoreLine("   &5Z: &3" + offsetZ)
                .build();
    }
    
    @Override
    public Offset clone(){
        try{
            Offset copy = (Offset) super.clone();
            copy.offsetX = this.offsetX.clone();
            copy.offsetY = this.offsetY.clone();
            copy.offsetZ = this.offsetZ.clone();
            return copy;
        }catch(CloneNotSupportedException ex){
            return null;
        }            
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.offsetX);
        hash = 67 * hash + Objects.hashCode(this.offsetY);
        hash = 67 * hash + Objects.hashCode(this.offsetZ);
        return hash;
    }
    
    @Override
    public boolean equals(Object object){
        if(object == null || !(object instanceof Offset)){
            return false;
        }else{
            Offset offset = (Offset) object;
            return this.offsetX.equals(offset.offsetX) && this.offsetY.equals(offset.offsetY) && this.offsetZ.equals(offset.offsetZ);
        }
    }
}
