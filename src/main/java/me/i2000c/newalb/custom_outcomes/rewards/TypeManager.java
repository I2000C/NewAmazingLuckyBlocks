package me.i2000c.newalb.custom_outcomes.rewards;

import com.cryptomorin.xseries.XMaterial;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.reflection.ReflectionManager;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.textures.Texture;
import me.i2000c.newalb.utils.textures.TextureManager;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import me.i2000c.newalb.utils2.OtherUtils;
import me.i2000c.newalb.utils2.RandomUtils;
import me.i2000c.newalb.utils2.XMaterialUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TypeManager{
    static{
        luckyBlockTypes = new ArrayList<>();
        luckyBlockTypesAux = new HashMap<>();
    }
    
    public static final int RESULT_OK = 0;
    public static final int RESULT_NO_GLOBAL_PERMISSION = 1;
    public static final int RESULT_NO_LOCAL_PERMISSION = 2;
    public static final int RESULT_NOT_LUCKYBLOCK = 3;
    
    private static final String LUCKY_BLOCK_TYPES_FOLDER = "luckyblock_types";
    public static final String PERMISSIONS_FILENAME = "global_luckyblock_permissions.yml";
    
    private static final Config globalPermissionsConfig = new Config();
    
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
        globalPermissionsConfig.loadConfig(LUCKY_BLOCK_TYPES_FOLDER + "/" + PERMISSIONS_FILENAME);        
        requireBreakPermissionGlobal = globalPermissionsConfig.getBoolean("GlobalPermissions.break.enable");
        breakPermissionGlobal = globalPermissionsConfig.getString("GlobalPermissions.break.permission");
        requirePlacePermissionGlobal = globalPermissionsConfig.getBoolean("GlobalPermissions.place.enable");
        placePermissionGlobal = globalPermissionsConfig.getString("GlobalPermissions.place.permission");
    }
    private static void saveGlobalPermissions(){
        globalPermissionsConfig.set("GlobalPermissions.break.enable", requireBreakPermissionGlobal);
        globalPermissionsConfig.set("GlobalPermissions.break.permission", breakPermissionGlobal);
        globalPermissionsConfig.set("GlobalPermissions.place.enable", requirePlacePermissionGlobal);
        globalPermissionsConfig.set("GlobalPermissions.place.permission", placePermissionGlobal);        
        globalPermissionsConfig.saveConfig(LUCKY_BLOCK_TYPES_FOLDER + "/" + PERMISSIONS_FILENAME);
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
        return luckyBlockTypes.get(RandomUtils.getInt(luckyBlockTypes.size()));
    }
    
    public static LuckyBlockType getType(ItemStack stack){
        if(stack == null || stack.getType() == Material.AIR) {
            return null;
        }
        
        ItemStackWrapper wrapper = ItemStackWrapper.fromItem(stack, false);
        TypeData data = new TypeData(wrapper);
        return luckyBlockTypesAux.get(data);
    }
    public static LuckyBlockType getType(Block block){
        XMaterial material = XMaterialUtils.getXMaterial(block);
        Texture texture = TextureManager.getTexture(block);
        TypeData data = new TypeData(material, texture);
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
        
        File luckyBlockTypesFolder = new File(ConfigManager.getDataFolder(), LUCKY_BLOCK_TYPES_FOLDER);
        if(!luckyBlockTypesFolder.exists()) {
            copyDefaultTypes();
        }
        
        // Load global permissions
        loadGlobalPermissions();

        // Load LuckyBlock types
        luckyBlockTypes.clear();
        luckyBlockTypesAux.clear();
        
        for(File file : luckyBlockTypesFolder.listFiles()){
            try {
                String name = file.getName();
                if(!name.endsWith(".yml")){
                    continue;
                }
                
                if(name.equals(PERMISSIONS_FILENAME)){
                    continue;
                }
                
                Config config = new Config();
                config.loadConfig(file);
                String typeName = OtherUtils.removeExtension(name);
                LuckyBlockType type = LuckyBlockType.loadFromConfig(config, typeName);
                
                luckyBlockTypes.add(type);
                luckyBlockTypesAux.put(type.getTypeData(), type);
            } catch(Throwable ex) {
                Logger.err("There has been an error while loading LuckyBlockType: " + file.getName());
                ex.printStackTrace();
            }
        }
        //</editor-fold>
    }
    
    public static void saveTypes(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        luckyBlockTypes.forEach(type -> {
            Config config = new Config();
            type.saveToConfig(config);
            config.saveConfig(LUCKY_BLOCK_TYPES_FOLDER + "/" + type.getTypeName() + ".yml");
        });
//</editor-fold>
    }
    
    static void removeRecipe(ShapedRecipe typeRecipe){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(typeRecipe == null){
            return;
        }
        
        Iterator<Recipe> iter = Bukkit.recipeIterator();
        if(MinecraftVersion.CURRENT_VERSION.isLessThan(MinecraftVersion.v1_12)) {
            while(iter.hasNext()){
                Recipe recipe = iter.next();
                if(recipe.getResult().equals(typeRecipe.getResult())){
                    iter.remove();
                    break;
                }
            }
        } else if(MinecraftVersion.CURRENT_VERSION.isLessThan(MinecraftVersion.v1_15)) {
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
        } else {
            // In Minecraft 1.15 the method org.bukkit.Bukkit.removeRecipe(NamespacedKey key) was added.
            // Source: https://helpch.at/docs/1.15.2/org/bukkit/Bukkit.html
            ReflectionManager.callStaticMethod(Bukkit.class, "removeRecipe", typeRecipe.getKey());
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
        String filename = LUCKY_BLOCK_TYPES_FOLDER + "/" + type.getTypeName() + ".yml";
        File typeFile = new File(ConfigManager.getDataFolder(), filename);
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
        
        Config config = new Config();
        type.saveToConfig(config);
        config.saveConfig(LUCKY_BLOCK_TYPES_FOLDER + "/" + type.getTypeName() + ".yml");
        loadTypes();
//</editor-fold>
    }
    
    public static void renameType(int typeID, String newName){
        //<editor-fold defaultstate="collapsed" desc="Code">
        LuckyBlockType type = luckyBlockTypes.get(typeID);
        String oldFilename = LUCKY_BLOCK_TYPES_FOLDER + "/" + type.getTypeName() + ".yml";
        String newFilename = LUCKY_BLOCK_TYPES_FOLDER + "/" + newName + ".yml";
        
        File oldTypeFile = new File(ConfigManager.getDataFolder(), oldFilename);
        File newTypeFile = new File(ConfigManager.getDataFolder(), newFilename);
        
        Config config = new Config();
        type.saveToConfig(config);
        config.saveConfig(newTypeFile);
        oldTypeFile.delete();
        loadTypes();        
//</editor-fold>
    }
    
    
    private static void copyDefaultTypes(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Arrays.asList(PERMISSIONS_FILENAME, "default.yml", "default2.yml").forEach(filename -> {
            String path = LUCKY_BLOCK_TYPES_FOLDER + "/" + filename;
            Config config = new Config();
            config.loadConfigFromResource(path);
            config.saveConfig(path);
        });//</editor-fold>
    }
}
