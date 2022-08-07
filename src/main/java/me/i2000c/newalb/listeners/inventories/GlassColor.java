package me.i2000c.newalb.listeners.inventories;

import com.cryptomorin.xseries.XMaterial;

public enum GlassColor{
    BLACK(XMaterial.BLACK_STAINED_GLASS_PANE),
    BLUE(XMaterial.BLUE_STAINED_GLASS_PANE),
    BROWN(XMaterial.BROWN_STAINED_GLASS_PANE),
    CYAN(XMaterial.CYAN_STAINED_GLASS_PANE),
    GRAY(XMaterial.GRAY_STAINED_GLASS_PANE),
    GREEN(XMaterial.GREEN_STAINED_GLASS_PANE),
    LIGHT_BLUE(XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE),
    LIGHT_GRAY(XMaterial.LIGHT_GRAY_STAINED_GLASS_PANE),
    LIME(XMaterial.LIME_STAINED_GLASS_PANE),
    MAGENTA(XMaterial.MAGENTA_STAINED_GLASS_PANE),
    ORANGE(XMaterial.ORANGE_STAINED_GLASS_PANE),
    PINK(XMaterial.PINK_STAINED_GLASS_PANE),
    PURPLE(XMaterial.PURPLE_STAINED_GLASS_PANE),
    RED(XMaterial.RED_STAINED_GLASS_PANE),
    WHITE(XMaterial.WHITE_STAINED_GLASS_PANE),
    YELLOW(XMaterial.YELLOW_STAINED_GLASS_PANE);
    
    private final XMaterial material;
    
    private GlassColor(XMaterial material){
        this.material = material;
    }
    
    public XMaterial toMaterial(){
        return material;
    }
}
