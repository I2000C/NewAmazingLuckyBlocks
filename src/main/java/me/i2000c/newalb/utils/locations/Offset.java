package me.i2000c.newalb.utils.locations;

import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.lucky_blocks.rewards.Displayable;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class Offset implements Displayable, Cloneable{
    private Range offsetX;
    private Range offsetY;
    private Range offsetZ;
    
    public Offset(Config config, String path){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ConfigurationSection section = config.getConfigurationSection(path, null);
        if(section != null){
            Range rangeX = Range.fromString(config.getString(path + ".offsetX"));
            Range rangeY = Range.fromString(config.getString(path + ".offsetY"));
            Range rangeZ = Range.fromString(config.getString(path + ".offsetZ"));
            
            this.offsetX = rangeX;
            this.offsetY = rangeY;
            this.offsetZ = rangeZ;
        }else{
            this.offsetX = new Range();
            this.offsetY = new Range();
            this.offsetZ = new Range();
        }
//</editor-fold>
    }
    public void saveToConfig(Config config, String path){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(this.offsetX.isZero() && this.offsetY.isZero() && this.offsetZ.isZero()){
            config.set(path, null);
        }else{
            config.set(path + ".offsetX", this.offsetX.toString());
            config.set(path + ".offsetY", this.offsetY.toString());
            config.set(path + ".offsetZ", this.offsetZ.toString());
        }
//</editor-fold>
    }
    
    public Offset(String offsetX, String offsetY, String offsetZ){
        //<editor-fold defaultstate="collapsed" desc="Code">
        this.offsetX = Range.fromString(offsetX);
        this.offsetY = Range.fromString(offsetY);
        this.offsetZ = Range.fromString(offsetZ);
//</editor-fold>
    }
    
    public Offset(Range offsetX, Range offsetY, Range offsetZ){
        //<editor-fold defaultstate="collapsed" desc="Code">
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
//</editor-fold>
    }
    
    public Offset(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        this(new Range(), new Range(), new Range());
//</editor-fold>
    }
    
    public void setOffsetX(Range offsetX){
        //<editor-fold defaultstate="collapsed" desc="Code">
        this.offsetX = offsetX;
//</editor-fold>
    }
    public Range getOffsetX(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return this.offsetX;
//</editor-fold>
    }
    
    public void setOffsetY(Range offsetY){
        //<editor-fold defaultstate="collapsed" desc="Code">
        this.offsetY = offsetY;
//</editor-fold>
    }
    public Range getOffsetY(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return this.offsetY;
//</editor-fold>
    }
    
    public void setOffsetZ(Range offsetZ){
        //<editor-fold defaultstate="collapsed" desc="Code">
        this.offsetZ = offsetZ;
//</editor-fold>
    }
    public Range getOffsetZ(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return this.offsetZ;
//</editor-fold>
    }
    
    public Location applyToLocation(Location loc){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return loc.add(offsetX.getRandomInt(), offsetY.getRandomInt(), offsetZ.getRandomInt());
//</editor-fold>
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return ItemStackWrapper.newItem(XMaterial.PISTON)
                               .setDisplayName("&3Configure offset")
                               .addLoreLine("&dCurrent offset:")
                               .addLoreLine("   &5X: &3" + offsetX)
                               .addLoreLine("   &5Y: &3" + offsetY)
                               .addLoreLine("   &5Z: &3" + offsetZ)
                               .toItemStack();
//</editor-fold>
    }
    
    @Override
    public Offset clone(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        try{
            Offset copy = (Offset) super.clone();
            copy.offsetX = this.offsetX.clone();
            copy.offsetY = this.offsetY.clone();
            copy.offsetZ = this.offsetZ.clone();
            return copy;
        }catch(CloneNotSupportedException ex){
            return null;
        }
//</editor-fold>
    }
    
    @Override
    public int hashCode() {
        //<editor-fold defaultstate="collapsed" desc="Code">
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.offsetX);
        hash = 67 * hash + Objects.hashCode(this.offsetY);
        hash = 67 * hash + Objects.hashCode(this.offsetZ);
        return hash;
//</editor-fold>
    }
    
    @Override
    public boolean equals(Object object){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(object == null || !(object instanceof Offset)){
            return false;
        }else{
            Offset offset = (Offset) object;
            return this.offsetX.equals(offset.offsetX) && this.offsetY.equals(offset.offsetY) && this.offsetZ.equals(offset.offsetZ);
        }
//</editor-fold>
    }
}
