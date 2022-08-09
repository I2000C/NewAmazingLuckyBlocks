package me.i2000c.newalb.custom_outcomes.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils2.YamlConfigurationUTF8;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

public class TypeManager{
    static{
        luckyBlockTypes = new ArrayList<>();
        luckyBlockTypesAux = new HashMap<>();
    }
    
    public static final int RESULT_OK = 0;
    public static final int RESULT_NO_GLOBAL_PERMISSION = 1;
    public static final int RESULT_NO_LOCAL_PERMISSION = 2;
    public static final int RESULT_NOT_LUCKYBLOCK = 3;
    
    private static final NewAmazingLuckyBlocks PLUGIN = NewAmazingLuckyBlocks.getInstance();
    private static final File LUCKY_BLOCK_TYPES_FILE = new File(PLUGIN.getDataFolder(), "luckyBlockTypes.yml");
    
    private static final List<LuckyBlockType> luckyBlockTypes;
    private static final Map<TypeData, LuckyBlockType> luckyBlockTypesAux;
    
    private static boolean requireBreakPermissionGlobal;
    private static String breakPermissionGlobal;
    private static boolean requirePlacePermissionGlobal;
    private static String placePermissionGlobal;
    
    private static int currentRecipeID;
    
    static int getNextTypeID(){
        return ++currentRecipeID;
    }
    
    public static String getGlobalBreakPermission(){
        return breakPermissionGlobal;
    }
    public static void setGlobalBreakPermission(String permission){
        breakPermissionGlobal = permission;
    }
    public static String getGlobalPlacePermission(){
        return placePermissionGlobal;
    }
    public static void setGlobalPlacePermission(String permission){
        placePermissionGlobal = permission;
    }
    
    public static boolean isGlobalBreakPermissionEnabled(){
        return requireBreakPermissionGlobal;
    }
    public static void setEnableGlobalBreakPermission(boolean enable){
        requireBreakPermissionGlobal = enable;
    }
    public static boolean isGlobalPlacePermissionEnabled(){
        return requirePlacePermissionGlobal;
    }
    public static void setEnableGlobalPlacePermission(boolean enable){
        requirePlacePermissionGlobal = enable;
    }
    
    private static boolean checkBreakPermissionGlobal(Player player){
        return !requireBreakPermissionGlobal || player.hasPermission(breakPermissionGlobal);
    }
    private static boolean checkPlacePermissionGlobal(Player player){
        return !requirePlacePermissionGlobal || player.hasPermission(placePermissionGlobal);
    }
    
    public static ItemStack getMenuItemStack(){
        return new ItemStack(Material.SPONGE);
    }
    
    public static List<LuckyBlockType> getTypes(){
        return luckyBlockTypes;
    }
    
    public static LuckyBlockType getRandomLuckyBlockType(){
        Random r = new Random();
        return luckyBlockTypes.get(r.nextInt(luckyBlockTypes.size()));
    }
    
    public static LuckyBlockType getType(ItemStack stack){
        TypeData data = new TypeData(stack);
        return luckyBlockTypesAux.get(data);
    }
    public static LuckyBlockType getType(Block block){
        TypeData data = new TypeData(block);
        return luckyBlockTypesAux.get(data);
    }    
    
    
    public static class Result{
        //<editor-fold defaultstate="collapsed" desc="Code">
        public int resultCode;
        public LuckyBlockType resultType;
        
        public Result(int resultCode, LuckyBlockType resultType){
            this.resultCode = resultCode;
            this.resultType = resultType;
        }
//</editor-fold>
    }
    
    public static Result canPlaceBlock(Player player, ItemStack stack){
        //<editor-fold defaultstate="collapsed" desc="Code">
        LuckyBlockType type = getType(stack);
        if(type == null){
            return new Result(RESULT_NOT_LUCKYBLOCK, null);
        }
        
        if(!checkPlacePermissionGlobal(player)){
            return new Result(RESULT_NO_GLOBAL_PERMISSION, null);
        }
        
        if(!type.checkPlacePermission(player)){
            return new Result(RESULT_NO_LOCAL_PERMISSION, null);
        }
        
        return new Result(RESULT_OK, type);
//</editor-fold>
    }
    
    public static Result canBreakBlock(Player player, Location location){
        //<editor-fold defaultstate="collapsed" desc="Code">
        LuckyBlockType type = getType(location.getBlock());
        if(type == null){
            return new Result(RESULT_NOT_LUCKYBLOCK, null);
        }
        
        if(!checkBreakPermissionGlobal(player)){
            return new Result(RESULT_NO_GLOBAL_PERMISSION, null);
        }
        
        if(!type.checkBreakPermission(player)){
            return new Result(RESULT_NO_LOCAL_PERMISSION, null);
        }
        
        return new Result(RESULT_OK, type);
//</editor-fold>
    }
    
    public static void loadTypes(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        currentRecipeID = -1;
        
        // Remove all previously used recipes
        Iterator<LuckyBlockType> iter = luckyBlockTypes.iterator();
        while(iter.hasNext()){
            LuckyBlockType type = iter.next();
            removeRecipe(type.getRecipe());
            iter.remove();
        }

        if(!LUCKY_BLOCK_TYPES_FILE.exists()){
            if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                NewAmazingLuckyBlocks.getInstance().copyResource("luckyBlockTypes_legacy.yml", LUCKY_BLOCK_TYPES_FILE);
            }else{
                NewAmazingLuckyBlocks.getInstance().copyResource("luckyBlockTypes_no_legacy.yml", LUCKY_BLOCK_TYPES_FILE);
            }
        }

        FileConfiguration luckyBlockTypesConfig = YamlConfigurationUTF8.loadConfiguration(LUCKY_BLOCK_TYPES_FILE);

        // Load global permissions
        requireBreakPermissionGlobal = luckyBlockTypesConfig.getBoolean("GlobalPermissions.break.enable");
        breakPermissionGlobal = luckyBlockTypesConfig.getString("GlobalPermissions.break.permission");
        requirePlacePermissionGlobal = luckyBlockTypesConfig.getBoolean("GlobalPermissions.place.enable");
        placePermissionGlobal = luckyBlockTypesConfig.getString("GlobalPermissions.place.permission");

        // Load LuckyBlock types
        luckyBlockTypes.clear();
        luckyBlockTypesAux.clear();
        Set<String> keyList = luckyBlockTypesConfig.getConfigurationSection("LuckyBlockTypes").getKeys(false);
        for(String key : keyList){
            LuckyBlockType type = LuckyBlockType.loadFromConfig(luckyBlockTypesConfig, "LuckyBlockTypes", key);
            if(type != null){
                luckyBlockTypes.add(type);
                luckyBlockTypesAux.put(type.getTypeData(), type);
            }                
        }
        //</editor-fold>
    }
    
    public static void saveTypes(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        FileConfiguration luckyBlockTypesConfig = new YamlConfigurationUTF8();
        
        // Save global permissions
        luckyBlockTypesConfig.set("GlobalPermissions.break.enable", requireBreakPermissionGlobal);
        luckyBlockTypesConfig.set("GlobalPermissions.break.permission", breakPermissionGlobal);
        luckyBlockTypesConfig.set("GlobalPermissions.place.enable", requirePlacePermissionGlobal);
        luckyBlockTypesConfig.set("GlobalPermissions.place.permission", placePermissionGlobal);
        
        // Save LuckyBlock types
        
        luckyBlockTypes.forEach(type -> type.saveToConfig(luckyBlockTypesConfig, "LuckyBlockTypes"));
        try{
            luckyBlockTypesConfig.save(LUCKY_BLOCK_TYPES_FILE);
        }catch(IOException ex){
            Logger.log("An error occurred while saving lucky block types config:");
            ex.printStackTrace();
        }
//</editor-fold>
    }
    
    static void removeRecipe(ShapedRecipe typeRecipe){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(typeRecipe == null){
            return;
        }
        
        Iterator<Recipe> iter = Bukkit.recipeIterator();
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
            while(iter.hasNext()){
                Recipe recipe = iter.next();
                if(recipe.getResult().equals(typeRecipe.getResult())){
                    iter.remove();
                    break;
                }
            }
        }else{
            while(iter.hasNext()){
                Recipe recipe = iter.next();
                if(recipe instanceof ShapedRecipe){
                    ShapedRecipe sr = (ShapedRecipe) recipe;
                    if(sr.getKey().equals(typeRecipe.getKey())){
                        iter.remove();
                        break;
                    }
                }
            }
        }
//</editor-fold>
    }
    
    public static LuckyBlockType getType(int typeID){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(typeID >= 0 && typeID < luckyBlockTypes.size()){
            return luckyBlockTypes.get(typeID);
        }else{
            return null;
        }
//</editor-fold>
    }
    
    public static LuckyBlockType getType(String typeName){
        //<editor-fold defaultstate="collapsed" desc="Code">
        for(LuckyBlockType type : luckyBlockTypes){
            if(type.getTypeName().equals(typeName)){
                return type;
            }
        }
        return null;
//</editor-fold>
    }
    
    public static void removeType(int typeID){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(typeID >= 0 && typeID < luckyBlockTypes.size()){
            LuckyBlockType type = luckyBlockTypes.remove(typeID);
            luckyBlockTypesAux.remove(type.getTypeData());
            removeRecipe(type.getRecipe());
            saveTypes();
        }
//</editor-fold>
    }
    
    public static void addType(LuckyBlockType type){
        //<editor-fold defaultstate="collapsed" desc="Code">
        int typeID = luckyBlockTypes.indexOf(type);
        if(typeID != -1){
            removeRecipe(luckyBlockTypes.get(typeID).getRecipe());
            luckyBlockTypes.set(typeID, type);
        }else{
            luckyBlockTypes.add(type);
        }
        saveTypes();
        loadTypes();
//</editor-fold>
    }
}
