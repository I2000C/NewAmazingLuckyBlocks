package me.i2000c.newalb.utils2;

import java.util.HashMap;
import java.util.Map;
import me.i2000c.newalb.utils.logger.LogLevel;
import me.i2000c.newalb.utils.logger.Logger;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("Offset")
public class Offset implements ConfigurationSerializable{
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
            Logger.log("Invalid offset: " + map.get("offsetX").toString(), LogLevel.INFO);
            Logger.log("It will be converted to '0'", LogLevel.INFO);
            offsetX = new Range();
        }
        
        try{
            offsetY = Range.fromString(map.get("offsetY").toString());
        }catch(IllegalArgumentException ex){
            Logger.log("Invalid offset: " + map.get("offsetY").toString(), LogLevel.INFO);
            Logger.log("It will be converted to '0'", LogLevel.INFO);
            offsetY = new Range();
        }
        
        try{
            offsetZ = Range.fromString(map.get("offsetZ").toString());
        }catch(IllegalArgumentException ex){
            Logger.log("Invalid offset: " + map.get("offsetZ").toString(), LogLevel.INFO);
            Logger.log("It will be converted to '0'", LogLevel.INFO);
            offsetZ = new Range();
        }
        
        return new Offset(offsetX, offsetY, offsetZ);
    }
    
    public Offset cloneOffset(){
        Offset offset = new Offset(this.offsetX.cloneRange(), this.offsetY.cloneRange(), this.offsetZ.cloneRange());
        return offset;
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
