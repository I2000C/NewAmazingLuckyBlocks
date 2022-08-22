package me.i2000c.newalb.custom_outcomes.rewards;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.config.ReadWriteConfig;
import me.i2000c.newalb.utils2.OtherUtils;
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
    private static final File LUCKY_BLOCK_TYPES_FOLDER = new File(PLUGIN.getDataFolder(), "luckyblock_types");
    public static final String PERMISSIONS_FILENAME = "global_luckyblock_permissions.yml";
    private static ReadWriteConfig permissionsConfig;
    
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
    
    //<editor-fold defaultstate="collapsed" desc="Global permissions methods">
    private static void loadGlobalPermissions(){
        if(permissionsConfig == null){
            permissionsConfig = new ReadWriteConfig(
                    PLUGIN, 
                    new File(LUCKY_BLOCK_TYPES_FOLDER, PERMISSIONS_FILENAME));
        }
        
        permissionsConfig.loadConfig();
        
        FileConfiguration config = permissionsConfig.getBukkitConfig();
        requireBreakPermissionGlobal = config.getBoolean("GlobalPermissions.break.enable");
        breakPermissionGlobal = config.getString("GlobalPermissions.break.permission");
        requirePlacePermissionGlobal = config.getBoolean("GlobalPermissions.place.enable");
        placePermissionGlobal = config.getString("GlobalPermissions.place.permission");
    }
    private static void saveGlobalPermissions(){
        if(permissionsConfig == null){
            permissionsConfig = new ReadWriteConfig(
                    PLUGIN, 
                    new File(LUCKY_BLOCK_TYPES_FOLDER, PERMISSIONS_FILENAME));
        }
        
        FileConfiguration config = permissionsConfig.getBukkitConfig();
        config.set("GlobalPermissions.break.enable", requireBreakPermissionGlobal);
        config.set("GlobalPermissions.break.permission", breakPermissionGlobal);
        config.set("GlobalPermissions.place.enable", requirePlacePermissionGlobal);
        config.set("GlobalPermissions.place.permission", placePermissionGlobal);
        
        permissionsConfig.saveConfig();
    }
    
    public static String getGlobalBreakPermission(){
        return breakPermissionGlobal;
    }
    public static void setGlobalBreakPermission(String permission){
        breakPermissionGlobal = permission;
        saveGlobalPermissions();
    }
    public static String getGlobalPlacePermission(){
        return placePermissionGlobal;
    }
    public static void setGlobalPlacePermission(String permission){
        placePermissionGlobal = permission;
        saveGlobalPermissions();
    }
    
    public static boolean isGlobalBreakPermissionEnabled(){
        return requireBreakPermissionGlobal;
    }
    public static void setEnableGlobalBreakPermission(boolean enable){
        requireBreakPermissionGlobal = enable;
        saveGlobalPermissions();
    }
    public static boolean isGlobalPlacePermissionEnabled(){
        return requirePlacePermissionGlobal;
    }
    public static void setEnableGlobalPlacePermission(boolean enable){
        requirePlacePermissionGlobal = enable;
        saveGlobalPermissions();
    }
    
    private static boolean checkBreakPermissionGlobal(Player player){
        return !requireBreakPermissionGlobal || player.hasPermission(breakPermissionGlobal);
    }
    private static boolean checkPlacePermissionGlobal(Player player){
        return !requirePlacePermissionGlobal || player.hasPermission(placePermissionGlobal);
    }
//</editor-fold>
    
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

        if(!LUCKY_BLOCK_TYPES_FOLDER.exists()){
            LUCKY_BLOCK_TYPES_FOLDER.mkdirs();
            copyDefaultTypes();
        }
        
        // Load global permissions
        loadGlobalPermissions();

        // Load LuckyBlock types
        luckyBlockTypes.clear();
        luckyBlockTypesAux.clear();
        
        for(File file : LUCKY_BLOCK_TYPES_FOLDER.listFiles()){
            String name = file.getName();
            if(!name.endsWith(".yml")){
                continue;
            }
            
            if(name.equals(PERMISSIONS_FILENAME)){
                continue;
            }
            
            ReadWriteConfig config = new ReadWriteConfig(PLUGIN, file);
            config.loadConfig();
            String typeName = OtherUtils.removeExtension(name);
            LuckyBlockType type = LuckyBlockType.loadFromConfig(config.getBukkitConfig(), typeName);
            
            luckyBlockTypes.add(type);
            luckyBlockTypesAux.put(type.getTypeData(), type);
        }
        //</editor-fold>
    }
    
    public static void saveTypes(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        luckyBlockTypes.forEach(type -> {
            File typeFile = new File(LUCKY_BLOCK_TYPES_FOLDER, type.getTypeName() + ".yml");
            ReadWriteConfig config = new ReadWriteConfig(PLUGIN, typeFile);
            type.saveToConfig(config.getBukkitConfig());
            config.saveConfig();
        });
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
            removeType(type);
        }
//</editor-fold>
    }
    private static void removeType(LuckyBlockType type){
        //<editor-fold defaultstate="collapsed" desc="Code">
        luckyBlockTypesAux.remove(type.getTypeData());
        removeRecipe(type.getRecipe());
        File typeFile = new File(LUCKY_BLOCK_TYPES_FOLDER, type.getTypeName() + ".yml");
        typeFile.delete();
//</editor-fold>
    }
    
    public static void addType(LuckyBlockType type){
        //<editor-fold defaultstate="collapsed" desc="Code">
        int typeID = luckyBlockTypes.indexOf(type);
        if(typeID != -1){
            removeRecipe(luckyBlockTypes.get(typeID).getRecipe());
            LuckyBlockType oldType = luckyBlockTypes.set(typeID, type);
            removeType(oldType);
        }else{
            luckyBlockTypes.add(type);
        }
        
        File typeFile = new File(LUCKY_BLOCK_TYPES_FOLDER, type.getTypeName() + ".yml");
        ReadWriteConfig config = new ReadWriteConfig(PLUGIN, typeFile);
        type.saveToConfig(config.getBukkitConfig());
        config.saveConfig();
        loadTypes();
//</editor-fold>
    }
    
    public static void renameType(int typeID, String newName){
        //<editor-fold defaultstate="collapsed" desc="Code">
        LuckyBlockType type = luckyBlockTypes.get(typeID);
        File oldTypeFile = new File(LUCKY_BLOCK_TYPES_FOLDER, type.getTypeName() + ".yml");
        File newTypeFile = new File(LUCKY_BLOCK_TYPES_FOLDER, newName + ".yml");
        ReadWriteConfig config = new ReadWriteConfig(PLUGIN, newTypeFile);
        type.saveToConfig(config.getBukkitConfig());
        config.saveConfig();
        oldTypeFile.delete();
        loadTypes();        
//</editor-fold>
    }
    
    
    private static void copyDefaultTypes(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        for(String filename : Arrays.asList("default.yml", "default2.yml", "other.yml", PERMISSIONS_FILENAME)){
            File file = new File(LUCKY_BLOCK_TYPES_FOLDER, filename);
            NewAmazingLuckyBlocks.getInstance().copyResource("luckyblock_types/" + filename, file);
        }
//</editor-fold>
    }
}
