package me.i2000c.newalb.utils.misc;

import org.bukkit.Color;

import me.i2000c.newalb.utils.random.RandomUtils;

public class CustomColor{
    private final Color color;
    
    public CustomColor(){
        int red = RandomUtils.getInt(0, 255);
        int green = RandomUtils.getInt(0, 255);
        int blue = RandomUtils.getInt(0, 255);
        this.color = Color.fromRGB(red, green, blue);
    }
    
    public CustomColor(Color color){
        this.color = color;
    }
    
    public CustomColor(String hexColorString){
        try{
            int rgbColor = getDecimalFromHex(hexColorString);
            this.color = Color.fromRGB(rgbColor);
        }catch(Exception ex){
            throw new IllegalArgumentException("Invalid hexColorString: " + hexColorString);
        }
    }
    
    public Color getBukkitColor(){
        return this.color;
    }
    
    public String getHexColorString(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        int r = this.color.getRed();
        int g = this.color.getGreen();
        int b = this.color.getBlue();
        
        return new StringBuilder()
            .append("0x")
            .append(String.format("%02X", r))
            .append(String.format("%02X", g))
            .append(String.format("%02X", b))
            .toString();
//</editor-fold>        
    }
    
    @Override
    public String toString(){
        return getHexColorString();
    }
    
    private static int getDecimalFromHex(String hex){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(hex.startsWith("0x")){
            hex = hex.substring(2); // Remove '0x' prefix
        }
        
        String digits = "0123456789ABCDEF";
        hex = hex.toUpperCase();
        int val = 0;
        for (int i=0;i<hex.length();i++){
            char c = hex.charAt(i);
            int d = digits.indexOf(c);
            val = 16 * val + d;
        }
        return val;
//</editor-fold>
    }
}
