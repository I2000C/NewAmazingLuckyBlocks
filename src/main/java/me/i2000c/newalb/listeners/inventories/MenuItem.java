package me.i2000c.newalb.listeners.inventories;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.utils.TypeManager;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.inventory.ItemStack;

public class MenuItem{
    public static ItemStack getGlassItem(XMaterial glassMaterial){
        return ItemBuilder.newItem(glassMaterial)
                .withDisplayName(" ")
                .build();
    }
    
    public static ItemStack getBackItem(){
        return ItemBuilder.newItem(XMaterial.ENDER_PEARL)
                .withDisplayName("&2Back")
                .build();
    }
    
    public static ItemStack getNextItem(){
        return ItemBuilder.newItem(XMaterial.ENDER_EYE)
                .withDisplayName("&bNext")
                .build();
    }
    
    public static ItemStack getUsePlayerLocItem(boolean isPlayerLoc){
        if(isPlayerLoc){
            return ItemBuilder.newItem(XMaterial.PLAYER_HEAD)
                    .withDisplayName("&aUse player location")
                    .build();
        }else{
            return ItemBuilder.fromItem(TypeManager.getMenuItemStack(), false)
                    .withDisplayName("&6Use lucky block location")
                    .build();
        }
    }
}
