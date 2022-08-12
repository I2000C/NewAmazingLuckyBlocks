package me.i2000c.newalb.listeners.inventories;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.utils.TypeManager;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.inventory.ItemStack;

public class GUIItem{
    public static ItemStack getGlassItem(GlassColor glassColor){
        return ItemBuilder.newItem(glassColor.toMaterial())
                .withDisplayName(" ")
                .build();
    }
    
    public static ItemStack getBackItem(){
        return ItemBuilder.newItem(XMaterial.ENDER_PEARL)
                .withDisplayName("&2Back")
                .build();
    }
    
    public static ItemStack getNextItem(){
        return ItemBuilder.newItem(XMaterial.ANVIL)
                .withDisplayName("&bNext")
                .build();
    }
    
    public static ItemStack getUsePlayerLocItem(boolean isPlayerLoc){
        if(isPlayerLoc){
            return ItemBuilder.newItem(XMaterial.PLAYER_HEAD)
                    .withDisplayName("&aUse player location")
                    .addLoreLine("&3Click to toggle")
                    .build();
        }else{
            return ItemBuilder.fromItem(TypeManager.getMenuItemStack(), false)
                    .withDisplayName("&eUse LuckyBlock location")
                    .addLoreLine("&3Click to toggle")
                    .build();
        }
    }
    
    public static ItemStack getPlusLessItem(float amount){
        if(amount >= 0){
            if(amount % 1 == 0){
                return ItemBuilder.newItem(GlassColor.LIME.toMaterial())
                    .withDisplayName(String.format("&a&l+%.0f", amount))
                    .build();
            }else{
                return ItemBuilder.newItem(GlassColor.LIME.toMaterial())
                    .withDisplayName(String.format("&a&l+%.2f", amount))
                    .build();
            }            
        }else{
            if((-amount) % 1 == 0){
                return ItemBuilder.newItem(GlassColor.RED.toMaterial())
                    .withDisplayName(String.format("&c&l%.0f", amount))
                    .build();
            }else{
                return ItemBuilder.newItem(GlassColor.RED.toMaterial())
                    .withDisplayName(String.format("&c&l%.2f", amount))
                    .build();
            } 
        }
    }
    
    public static ItemStack getBooleanItem(
            boolean value, 
            String displayText,
            XMaterial materialIfTrue,
            XMaterial materialIfFalse){
        
        ItemBuilder builder;
        if(value){
            builder = ItemBuilder.newItem(materialIfTrue);
            builder.withDisplayName(displayText + ": &atrue");
        }else{
            builder = ItemBuilder.newItem(materialIfFalse);
            builder.withDisplayName(displayText + ": &cfalse");
        }
        builder.addLoreLine("&3Click to toggle");
        return builder.build();
    }
    public static ItemStack getEnabledDisabledItem(
            boolean value, 
            String displayText, 
            String modeText,
            XMaterial materialIfTrue,
            XMaterial materialIfFalse){
        
        ItemBuilder builder;
        if(value){
            builder = ItemBuilder.newItem(materialIfTrue);
            builder.addLoreLine("");
            builder.addLoreLine(modeText + ": &aenabled");
        }else{
            builder = ItemBuilder.newItem(materialIfFalse);
            builder.addLoreLine("");
            builder.addLoreLine(modeText + ": &7disabled");
        }
        builder.addLoreLine("&3Click to toggle");
        builder.withDisplayName(displayText);
        return builder.build();
    }
    
    public static ItemStack getPreviousPageItem(){
        return ItemBuilder.newItem(XMaterial.ENDER_EYE)
                .withDisplayName("&2Previous page")
                .build();
    }
    public static ItemStack getNextPageItem(){
        return ItemBuilder.newItem(XMaterial.MAGMA_CREAM)
                .withDisplayName("&2Next page")
                .build();
    }
    public static ItemStack getCurrentPageItem(int currentPage, int maxPages){
        return ItemBuilder.newItem(XMaterial.BOOK)
                .withAmount(currentPage)
                .withDisplayName("&6Page &3" + currentPage + " &a/ &3" + maxPages)
                .addLoreLine("&bClick to go to page 1")
                .build();
    }
}
