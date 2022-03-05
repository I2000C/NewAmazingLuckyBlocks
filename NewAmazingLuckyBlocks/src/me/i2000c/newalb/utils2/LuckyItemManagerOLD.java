package me.i2000c.newalb.utils2;

import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.Logger;
import java.util.List;
import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.block.Block;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class LuckyItemManagerOLD{
    private static HeadMode headMode;
    private static ItemStack itemHeadMode;
    private static ItemStack itemNoHeadMode;
    
    private static TextureManager.Texture luckyBlockTexture;
    
    public static ItemStack getItem(){
        return (headMode == HeadMode.DISABLED) ? itemNoHeadMode.clone() : itemHeadMode.clone();
    }
    
    public static void loadItem(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        headMode = HeadMode.loadFromConfig();
        if(headMode == HeadMode.ENABLED || headMode == HeadMode.BOTH){
            String ID = ConfigManager.getConfig().getString("LuckyBlock.HeadMode.skull-ID");
            try{
                luckyBlockTexture = new TextureManager.Texture(ID);
            }catch(TextureManager.InvalidHeadException ex){
                Logger.log("&cAn invalid head has been detected in config");
            }            
            itemHeadMode = new ItemStack(TextureManager.getItemSkullMaterial());
            if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                itemHeadMode.setDurability((short) 3);
            }
            ItemMeta meta = itemHeadMode.getItemMeta();
            meta.setDisplayName(Logger.color(ConfigManager.getConfig().getString("LuckyBlock.Name")));
            meta.setLore(Logger.color(ConfigManager.getConfig().getStringList("LuckyBlock.Lore")));
            itemHeadMode.setItemMeta(meta);
            TextureManager.setTexture(itemHeadMode, luckyBlockTexture);
            
            if(headMode == HeadMode.BOTH){
                itemNoHeadMode = new ItemStack(Material.valueOf(ConfigManager.getConfig().getString("LuckyBlock.Material")));
                meta = itemNoHeadMode.getItemMeta();
                meta.setDisplayName(Logger.color(ConfigManager.getConfig().getString("LuckyBlock.Name")));
                meta.setLore(Logger.color(ConfigManager.getConfig().getStringList("LuckyBlock.Lore")));
                itemNoHeadMode.setItemMeta(meta);
            }
        }else if(headMode == HeadMode.DISABLED){
            luckyBlockTexture = null;
            itemNoHeadMode = new ItemStack(Material.valueOf(ConfigManager.getConfig().getString("LuckyBlock.Material")));
            ItemMeta meta = itemNoHeadMode.getItemMeta();
            meta.setDisplayName(Logger.color(ConfigManager.getConfig().getString("LuckyBlock.Name")));
            meta.setLore(Logger.color(ConfigManager.getConfig().getStringList("LuckyBlock.Lore")));
            itemNoHeadMode.setItemMeta(meta);
        }
        
        if(headMode == null){
            sendMessage("&4HeadMode is unknown. Check the config");
        }else switch(headMode){
            case ENABLED:
                sendMessage("&6HeadMode is &aenabled");
                break;
            case DISABLED:
                sendMessage("&6HeadMode is &cdisabled");
                break;
            case BOTH:
                sendMessage("&6HeadMode is &bboth mode");
                break;
        }
//</editor-fold>
    }
    
    public static boolean checkBlock(Block block){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(headMode == HeadMode.ENABLED || headMode == HeadMode.BOTH){
            if(Objects.equals(luckyBlockTexture, TextureManager.getTexture(block))){
                return true;
            }
        }
        
        if(headMode == HeadMode.DISABLED || headMode == HeadMode.BOTH){
            Material material = itemNoHeadMode.getType();
            Material blockMaterial = block.getType();
            switch(material.name()){
                case "SKULL_ITEM":
                    return blockMaterial.name().equals("SKULL");
                case "PLAYER_HEAD":
                    return blockMaterial.name().equals("PLAYER_HEAD") || blockMaterial.name().equals("PLAYER_WALL_HEAD");
                default:
                    return blockMaterial.equals(material);
            }
        }
        
        return false;
    //</editor-fold>
    }
    public static boolean checkItem(ItemStack stack){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemStack item;
        if(headMode == HeadMode.DISABLED){
            item = itemNoHeadMode;
        }else{
            item = itemHeadMode;
        }
        
        if(item.hasItemMeta() != stack.hasItemMeta()){
            return false;
        }

        String displayName1 = item.getItemMeta().getDisplayName();
        String displayName2 = stack.getItemMeta().getDisplayName();
        if(!Objects.equals(displayName1, displayName2)){
            return false;
        }

        List<String> lore1 = item.getItemMeta().getLore();
        List<String> lore2 = item.getItemMeta().getLore();
        if(Objects.equals(lore1, lore2)){
            return false;
        }

        Material material = item.getType();
        Material material2 = stack.getType();
        if(headMode == HeadMode.DISABLED){
            return material2.equals(material);
        }else{
            TextureManager.Texture texture1 = TextureManager.getTexture(item);
            TextureManager.Texture texture2 = TextureManager.getTexture(stack);
            return Objects.equals(texture1, texture2);
        }
    //</editor-fold>
    }
    
    public static void sendMessage(String m){
        if(ConfigManager.getConfig().getBoolean("LuckyBlock.HeadMode.show-enabled-disabled-info")){
            Logger.log(m);
        }
    }
    
    public static void replaceBlock(Block b){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String materialName;
        if(headMode == HeadMode.DISABLED){
            materialName = itemNoHeadMode.getType().name();
        }else{
            materialName = itemHeadMode.getType().name();
        }
        
        if(materialName.equals("SKULL_ITEM")){
            b.setType(Material.valueOf("SKULL"));
        }else{
            b.setType(Material.valueOf(materialName));
        }
        
        if(headMode != HeadMode.DISABLED){
            if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                b.setData((byte) 1);
            }
            
            TextureManager.setTexture(b, luckyBlockTexture, true);
        }
    //</editor-fold>
    }
    
    
    
    private static enum HeadMode{
        ENABLED,
        DISABLED,
        BOTH;
        
        public static HeadMode loadFromConfig(){
            String headMode = ConfigManager.getConfig().getString("LuckyBlock.HeadMode.enable");
            switch(headMode){
                case "true":
                    return ENABLED;
                case "false":
                    return DISABLED;
                case "both":
                    return BOTH;
                default:
                    return null;
            }
        }
    }
}
