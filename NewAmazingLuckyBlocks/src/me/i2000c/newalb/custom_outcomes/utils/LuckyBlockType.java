package me.i2000c.newalb.custom_outcomes.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.OtherUtils;
import me.i2000c.newalb.utils2.TextureManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public class LuckyBlockType{
    private String typeName;
    
    private ItemStack luckyBlockItem;
    private boolean requireBreakPermission;
    private String breakPermission;
    private boolean requirePlacePermission;
    private String placePermission;
    
    private TextureManager.Texture texture;
    
    private List<ItemStack> crafting;
    private ShapedRecipe recipe;
    
    private HashMap<OutcomePack, Integer> packs;
    private int totalProbability;
    
    private TypeData data;
    
    private LuckyBlockType(){}
    
    
    public String getTypeName(){
        return typeName;
    }
    public void setTypeName(String typeName){
        this.typeName = typeName;
    }
    
    public ItemStack getItem(){
        return luckyBlockItem.clone();
    }
    
    public List<ItemStack> getCrafting(){
        return crafting;
    }
    public ShapedRecipe getRecipe(){
        return recipe;
    }    
    
    public boolean checkBreakPermission(Player player){
        return !requireBreakPermission || player.hasPermission(breakPermission);
    }
    public boolean checkPlacePermission(Player player){
        return !requirePlacePermission || player.hasPermission(placePermission);
    }
    
    public HashMap<OutcomePack, Integer> getPacks(){
        return packs;
    }
    
    public TypeData getTypeData(){
        return data;
    }
    
    
    public static LuckyBlockType loadFromConfig(FileConfiguration config, String key, String typeName){
        //<editor-fold defaultstate="collapsed" desc="Code">
        LuckyBlockType type = new LuckyBlockType();
        
        type.typeName = typeName;
        String path = key + "." + typeName;
        
        // Load permissions
        type.requireBreakPermission = config.getBoolean(path + ".permissions.break.enable");
        type.breakPermission = config.getString(path + ".permissions.break.permission");
        type.requirePlacePermission = config.getBoolean(path + ".permissions.place.enable");        
        type.placePermission = config.getString(path + ".permissions.place.permission");
        
        // Load item
        String name = config.getString(path + ".name");
        List<String> lore = config.getStringList(path + ".lore");
        String materialName = config.getString(path + ".material");
        String textureID = config.getString(path + ".textureID");
        
        if(textureID.isEmpty() && !materialName.isEmpty()){
            type.luckyBlockItem = OtherUtils.parseMaterial(materialName);
            type.texture = null;
        }else if(!textureID.isEmpty() && materialName.isEmpty()){
            try{
                type.texture = new TextureManager.Texture(textureID);
                type.luckyBlockItem = TextureManager.getItemSkullStack();
                TextureManager.setTexture(type.luckyBlockItem, type.texture);
            }catch(TextureManager.InvalidHeadException ex){
                Logger.log(String.format("Invalid texture for LuckyBlockType \"%s\"", 
                        type.typeName), Logger.LogLevel.WARN);
                return null;
            }
        }else{
            return null;
        }
        
        ItemMeta meta = type.luckyBlockItem.getItemMeta();
        if(!name.isEmpty()){
            meta.setDisplayName(Logger.color(name));
        }
        if(!lore.isEmpty()){
            meta.setLore(Logger.color(lore));
        }        
        type.luckyBlockItem.setItemMeta(meta);
        
        // Load crafting
        List<String> recipeMaterialNames = config.getStringList(path + ".crafting");
        String[] materialNames = (recipeMaterialNames.get(0) + " " +
                                  recipeMaterialNames.get(1) + " " +
                                  recipeMaterialNames.get(2)).split(" ");
        
        type.crafting = new ArrayList<>(9);
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
            type.recipe = new ShapedRecipe(type.luckyBlockItem);
        }else{
            type.recipe = new ShapedRecipe(new NamespacedKey(NewAmazingLuckyBlocks.getInstance(), TypeManager.getNextRecipeID()), type.luckyBlockItem);
        }        
        
        String char0 = materialNames[0].startsWith("AIR") ? " " : "A";
        String char1 = materialNames[1].startsWith("AIR") ? " " : "B";
        String char2 = materialNames[2].startsWith("AIR") ? " " : "C";
        String char3 = materialNames[3].startsWith("AIR") ? " " : "D";
        String char4 = materialNames[4].startsWith("AIR") ? " " : "E";
        String char5 = materialNames[5].startsWith("AIR") ? " " : "F";
        String char6 = materialNames[6].startsWith("AIR") ? " " : "G";
        String char7 = materialNames[7].startsWith("AIR") ? " " : "H";
        String char8 = materialNames[8].startsWith("AIR") ? " " : "I";
        
        type.recipe.shape(char0 + char1 + char2,
                          char3 + char4 + char5,
                          char6 + char7 + char8);
        
        char ingredientChar = 'A';
        for(String materialAndData : materialNames){
            ItemStack item = OtherUtils.parseMaterial(materialAndData);
            if(item.getType() != Material.AIR){
                if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                    type.recipe.setIngredient(ingredientChar, item.getType(), item.getDurability());
                }else{
                    type.recipe.setIngredient(ingredientChar, item.getType());
                }
            }
            type.crafting.add(item);
            ingredientChar++;
        }
        
        //Remove previous recipe if exists
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
            for(Iterator<Recipe> iter = Bukkit.recipeIterator(); iter.hasNext();){
                Recipe recipe = iter.next();
                if(recipe.getResult().equals(type.recipe.getResult())){
                    iter.remove();
                    break;
                }
            }
        }else{
            for(Iterator<Recipe> iter = Bukkit.recipeIterator(); iter.hasNext();){
                Recipe recipe = iter.next();
                if(recipe instanceof ShapedRecipe){
                    ShapedRecipe sr = (ShapedRecipe) recipe;
                    if(sr.getKey().equals(type.recipe.getKey())){
                        iter.remove();
                        break;
                    }
                }
            }
        }
        
        boolean emptyCrafting = true;
        for(int i=0; emptyCrafting && i<type.crafting.size(); i++){
            emptyCrafting = type.crafting.get(i).getType() == Material.AIR;
        }
        if(!emptyCrafting){
            Bukkit.addRecipe(type.recipe);
        }
        
        // Load outcome packs
        type.packs = new HashMap<>();
        type.totalProbability = 0;
        List<String> packsProbList = config.getStringList(path + ".outcome_packs");
        for(String packProb : packsProbList){
            String[] splitted = packProb.split(";");
            String packName = splitted[0];
            int packProbability;
            try{
                packProbability = Integer.parseInt(splitted[1]);
            }catch(Exception ex){
                packProbability = 0;
            }
            
            OutcomePack pack = PackManager.getManager().getPack(packName);
            if(pack == null){
                Logger.log(String.format("Pack \"%s\" doesn't exist", packName));
            }else{
                pack.addLuckyBlockTypeToNotify(type);
                type.totalProbability += packProbability;
                type.packs.put(pack, packProbability);
            }            
        }
        if(type.packs.isEmpty()){
            Logger.log(String.format("LuckyBlockType \"%s\" doesn't contain any valid outcome pack", 
                    type.typeName), Logger.LogLevel.WARN);
        }else if(type.totalProbability <= 0){
            Logger.log(String.format("Total probability of LuckyBlockType \"%s\" must be positive", 
                    type.typeName), Logger.LogLevel.WARN);
        }
        
        type.data = new TypeData(type.luckyBlockItem);
        
        return type;
//</editor-fold>
    }
    
    public void saveToConfig(FileConfiguration config, String key){
        //<editor-fold defaultstate="collapsed" desc="Code">
        
        String path = key + "." + typeName;
        
        // Save permissions
        config.set(path + ".permissions.break.enable", requireBreakPermission);
        config.set(path + ".permissions.break.permission", breakPermission);
        config.set(path + ".permissions.place.enable", requirePlacePermission);
        config.set(path + ".permissions.place.permission", placePermission);
        
        // Save item
        ItemMeta meta = luckyBlockItem.getItemMeta();
        String name = meta.hasDisplayName() ? Logger.deColor(meta.getDisplayName()) : "";
        List<String> lore = meta.hasLore() ? Logger.deColor(meta.getLore()) : new ArrayList<>();
        config.set(path + ".name", name);
        config.set(path + ".lore", lore);
        config.set(path + ".material", texture == null ? OtherUtils.parseItemStack(luckyBlockItem) : "");
        config.set(path + ".textureID", texture != null ? texture.toString() : "");
        
        // Save crafting recipe
        String row0 = OtherUtils.parseItemStack(crafting.get(0)) + " " +
                      OtherUtils.parseItemStack(crafting.get(1)) + " " +
                      OtherUtils.parseItemStack(crafting.get(2));
        String row1 = OtherUtils.parseItemStack(crafting.get(3)) + " " +
                      OtherUtils.parseItemStack(crafting.get(4)) + " " +
                      OtherUtils.parseItemStack(crafting.get(5));
        String row2 = OtherUtils.parseItemStack(crafting.get(6)) + " " +
                      OtherUtils.parseItemStack(crafting.get(7)) + " " +
                      OtherUtils.parseItemStack(crafting.get(8));
        config.set(path + ".crafting", Arrays.asList(row0, row1, row2));
        
        // Save outcome packs
        List<String> packNames = new ArrayList<>();
        packs.forEach((pack, probability) -> packNames.add(pack.getFilename() + ";" + probability));
        config.set(path + ".outcome_packs", packNames);
//</editor-fold>       
    }
    
    public Integer getProbabilityPack(OutcomePack pack){
        return packs.get(pack);
    }
    public void setProbabilityPack(OutcomePack pack, int probability){
        packs.put(pack, probability);
    }
    public void removePack(OutcomePack pack){
        packs.remove(pack);
    }
        
    public void replaceBlock(Block block){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String materialName = luckyBlockItem.getType().name();
        if(materialName.equals("SKULL_ITEM")){
            block.setType(Material.valueOf("SKULL"));
        }else{
            block.setType(luckyBlockItem.getType());
        }
        
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
            block.setData((byte) luckyBlockItem.getDurability());
        }
        
        if(texture != null){
            if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                block.setData((byte) 1);
            }
            
            TextureManager.setTexture(block, texture, true);
        }
//</editor-fold>
    }
    
    public void executeRandomPack(Player player, Location location){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(totalProbability <= 0){
            Logger.log(String.format("Total probability of LuckyBlockType \"%s\" must be positive", 
                    typeName), Logger.LogLevel.WARN);
        }else{
            Random r = new Random();
            int randomNumber = r.nextInt(totalProbability);
            for(Map.Entry<OutcomePack, Integer> entry : packs.entrySet()){
                OutcomePack pack = entry.getKey();
                Integer probability = entry.getValue();
                randomNumber -= probability;
                if(randomNumber < 0){
                    pack.executeRandomOutcome(player, location);
                    break;
                }
            }
        }
//</editor-fold>
    }
}
