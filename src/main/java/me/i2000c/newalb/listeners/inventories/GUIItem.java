package me.i2000c.newalb.listeners.inventories;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.rewards.TypeManager;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import org.bukkit.inventory.ItemStack;

public class GUIItem{
    public static ItemStack getGlassItem(GlassColor glassColor){
        return ItemStackWrapper.newItem(glassColor.toMaterial())
                               .setDisplayName(" ")
                               .toItemStack();
    }
    
    public static ItemStack getBackItem(){
        return ItemStackWrapper.newItem(XMaterial.ENDER_PEARL)
                               .setDisplayName("&2Back")
                               .toItemStack();
    }
    
    public static ItemStack getNextItem(){
        return ItemStackWrapper.newItem(XMaterial.ANVIL)
                               .setDisplayName("&bNext")
                               .toItemStack();
    }
    
    public static ItemStack getUsePlayerLocItem(boolean isPlayerLoc){
        if(isPlayerLoc){
            return ItemStackWrapper.newItem(XMaterial.PLAYER_HEAD)
                                   .setDisplayName("&aUse player location")
                                   .addLoreLine("&3Click to toggle")
                                   .toItemStack();
        }else{
            return ItemStackWrapper.fromItem(TypeManager.getMenuItemStack(), false)
                                   .setDisplayName("&eUse LuckyBlock location")
                                   .addLoreLine("&3Click to toggle")
                                   .toItemStack();
        }
    }
    
    public static ItemStack getPlusLessItem(float amount){
        if(amount >= 0){
            if(amount % 1 == 0){
                return ItemStackWrapper.newItem(GlassColor.LIME.toMaterial())
                                       .setDisplayName(String.format("&a&l+%.0f", amount))
                                       .toItemStack();
            }else{
                return ItemStackWrapper.newItem(GlassColor.LIME.toMaterial())
                                       .setDisplayName(String.format("&a&l+%.2f", amount))
                                       .toItemStack();
            }            
        }else{
            if((-amount) % 1 == 0){
                return ItemStackWrapper.newItem(GlassColor.RED.toMaterial())
                                       .setDisplayName(String.format("&c&l%.0f", amount))
                                       .toItemStack();
            }else{
                return ItemStackWrapper.newItem(GlassColor.RED.toMaterial())
                                       .setDisplayName(String.format("&c&l%.2f", amount))
                                       .toItemStack();
            } 
        }
    }
    
    public static ItemStack getBooleanItem(
            boolean value, 
            String displayText,
            XMaterial materialIfTrue,
            XMaterial materialIfFalse){
        
        ItemStackWrapper wrapper;
        if(value){
            wrapper = ItemStackWrapper.newItem(materialIfTrue);
            wrapper.setDisplayName(displayText + ": &atrue");
        }else{
            wrapper = ItemStackWrapper.newItem(materialIfFalse);
            wrapper.setDisplayName(displayText + ": &cfalse");
        }
        wrapper.addLoreLine("&3Click to toggle");
        return wrapper.toItemStack();
    }
    public static ItemStack getEnabledDisabledItem(
            boolean value, 
            String displayText, 
            String modeText,
            XMaterial materialIfTrue,
            XMaterial materialIfFalse){
        
        ItemStackWrapper builder;
        if(value){
            builder = ItemStackWrapper.newItem(materialIfTrue);
            builder.addLoreLine("");
            builder.addLoreLine(modeText + ": &aenabled");
        }else{
            builder = ItemStackWrapper.newItem(materialIfFalse);
            builder.addLoreLine("");
            builder.addLoreLine(modeText + ": &7disabled");
        }
        builder.addLoreLine("&3Click to toggle");
        builder.setDisplayName(displayText);
        return builder.toItemStack();
    }
    
    public static ItemStack getPreviousPageItem(){
        return ItemStackWrapper.newItem(XMaterial.ENDER_EYE)
                               .setDisplayName("&2Previous page")
                               .toItemStack();
    }
    public static ItemStack getNextPageItem(){
        return ItemStackWrapper.newItem(XMaterial.MAGMA_CREAM)
                               .setDisplayName("&2Next page")
                               .toItemStack();
    }
    public static ItemStack getCurrentPageItem(int currentPage, int maxPages){
        return ItemStackWrapper.newItem(XMaterial.BOOK)
                               .setAmount(currentPage)
                               .setDisplayName("&6Page &3" + currentPage + " &a/ &3" + maxPages)
                               .addLoreLine("&bClick to go to page 1")
                               .toItemStack();
    }
}
