package me.i2000c.newalb.custom_outcomes.rewards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.textures.InvalidTextureException;
import me.i2000c.newalb.utils.textures.Texture;
import me.i2000c.newalb.utils.textures.TextureException;
import me.i2000c.newalb.utils.textures.TextureManager;
import me.i2000c.newalb.utils.textures.URLTextureException;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class LuckyBlockType implements Displayable, Executable{
    private int ID;
    
    private String typeName;
    
    private ItemStack luckyBlockItem;
    
    private boolean requireBreakPermission;
    private String breakPermission;
    private boolean requirePlacePermission;
    private String placePermission;
    
    private Texture texture;
    
    private List<ItemStack> crafting;
    private ShapedRecipe recipe;
    
    private Map<OutcomePack, Integer> packs;
    private int totalProbability;
    
    private TypeData data;
    
    
    public String getTypeName(){
        return typeName;
    }
    public void setTypeName(String typeName){
        this.typeName = typeName;
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemBuilder builder = ItemBuilder.fromItem(this.luckyBlockItem);
        builder.withDisplayName("&bIdentifier: &6" + this.typeName);
        builder.withLore();
        builder.addLoreLine("&5Material: &a" + luckyBlockItem.getType().name());
        
        if(this.texture == null){
           builder.addLoreLine("&5Texture: &cnull"); 
        }else{
            String textureString = this.texture.toString().substring(0, 8) + "...";
            builder.addLoreLine("&5Texture: &b" + textureString);
        }
        
        ItemBuilder builder2 = ItemBuilder.fromItem(luckyBlockItem, false);
        String displayName = builder2.getDisplayName();
        if(displayName != null){
            builder.addLoreLine(String.format("&5Item name: &r%s", displayName));
        }else{
            builder.addLoreLine("&5Item name: &cnull");
        }
        
        List<String> lore = builder2.getLore();
        if(lore != null){
            builder.addLoreLine("&5Item lore:");
            lore.forEach(line -> builder.addLoreLine("    " + line));
        }else{
            builder.addLoreLine("&5Item lore: &cnull");
        }
        
        builder.addLoreLine("&5Place permission:");
        builder.addLoreLine("    &b" + this.placePermission);
        builder.addLoreLine("&5Break permission:");
        builder.addLoreLine("    &b" + this.breakPermission);
        builder.addLoreLine("&5Place permission required: " + (this.requirePlacePermission ? "&atrue" : "&7false"));
        builder.addLoreLine("&5Break permission required: " + (this.requireBreakPermission ? "&atrue" : "&7false"));
        
        builder.addLoreLine("&5Crafting:");
        builder.addLoreLine(String.format("    &e%s %s %s", 
                ItemBuilder.fromItem(this.crafting.get(0), false).toString(), 
                ItemBuilder.fromItem(this.crafting.get(1), false).toString(), 
                ItemBuilder.fromItem(this.crafting.get(2), false).toString()));
        builder.addLoreLine(String.format("    &e%s %s %s", 
                ItemBuilder.fromItem(this.crafting.get(3), false).toString(), 
                ItemBuilder.fromItem(this.crafting.get(4), false).toString(), 
                ItemBuilder.fromItem(this.crafting.get(5), false).toString()));
        builder.addLoreLine(String.format("    &e%s %s %s", 
                ItemBuilder.fromItem(this.crafting.get(6), false).toString(), 
                ItemBuilder.fromItem(this.crafting.get(7), false).toString(), 
                ItemBuilder.fromItem(this.crafting.get(8), false).toString()));
        
        builder.addLoreLine("&5Pack list:");
        this.packs.forEach((pack, probability) -> {
            builder.addLoreLine("    &2" + pack.getPackname() + ";" + probability);
        });
        
        return builder.build();
//</editor-fold>
    }
    public ItemStack getItem(){
        return luckyBlockItem.clone();
    }
    public void setItem(ItemStack item){
        this.luckyBlockItem = item;
        this.texture = TextureManager.getTexture(item);
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
    
    public String getPlacePermission(){
        return placePermission;
    }
    public void setPlacePermission(String permission){
        placePermission = permission;
    }
    public String getBreakPermission(){
        return breakPermission;
    }
    public void setBreakPermission(String permission){
        breakPermission = permission;
    }
    
    public boolean requirePlacePermission(){
        return requirePlacePermission;
    }
    public void setRequirePlacePermission(boolean requirePermission){
        requirePlacePermission = requirePermission;
    }    
    public boolean requireBreakPermission(){
        return requireBreakPermission;
    }
    public void setRequireBreakPermission(boolean requirePermission){
        requireBreakPermission = requirePermission;
    }
    
    public Map<OutcomePack, Integer> getPacks(){
        return packs;
    }
    
    public TypeData getTypeData(){
        return data;
    }
    
    
    public LuckyBlockType(String typeName){
        //<editor-fold defaultstate="collapsed" desc="Code">
        this.typeName = typeName;
        this.ID = -1;
        
        luckyBlockItem = new ItemStack(Material.SPONGE);
        
        requireBreakPermission = false;
        breakPermission = "amazinglb.lucky_block." + typeName + "break";
        requirePlacePermission = false;
        placePermission = "amazinglb.lucky_block." + typeName + "place";
        
        texture = null;
        
        crafting = new ArrayList<>(9);
        ItemStack air = new ItemStack(Material.AIR);
        for(int i=0; i<9; i++){
            crafting.add(air);
        }
        
        recipe = null;
        
        packs = new LinkedHashMap<>();
        totalProbability = 0;
        
        data = null;
//</editor-fold>
    }
    
    private LuckyBlockType(){}    
    
    public static LuckyBlockType loadFromConfig(FileConfiguration config, String typeName){
        //<editor-fold defaultstate="collapsed" desc="Code">
        LuckyBlockType type = new LuckyBlockType();
        
        type.typeName = typeName;
        type.ID = TypeManager.getNextTypeID();
        
        // Load permissions
        type.requireBreakPermission = config.getBoolean("permissions.break.enable");
        type.breakPermission = config.getString("permissions.break.permission");
        type.requirePlacePermission = config.getBoolean("permissions.place.enable");
        type.placePermission = config.getString("permissions.place.permission");
        
        // Load item
        String name = config.getString("name");
        List<String> lore = config.getStringList("lore");
        String materialName = config.getString("material");
        String textureID = config.getString("textureID");
        
        if(textureID.isEmpty() && !materialName.isEmpty()){
            type.luckyBlockItem = ItemBuilder.newItem(materialName).build();
            type.texture = null;
        }else if(!textureID.isEmpty() && materialName.isEmpty()){
            try{
                type.texture = new Texture(textureID);
                type.luckyBlockItem = TextureManager.getItemSkullStack();
                TextureManager.setTexture(type.luckyBlockItem, type.texture);
            }catch(InvalidTextureException ex){
                Logger.err(String.format("Invalid texture for LuckyBlock type \"%s\"", 
                        type.typeName));
                return null;
            }catch(URLTextureException ex){
                Logger.err(String.format("An error occured while loading texture for LuckyBlock type \"%s\":", 
                        type.typeName));
                Logger.err(ex);
                return null;
            }catch(TextureException ex){}
        }else{
            return null;
        }
        
        ItemBuilder.fromItem(type.luckyBlockItem, false)
                .withDisplayName(name)
                .withLore(lore);
        
        // Load crafting
        List<String> recipeMaterialNames = config.getStringList("crafting");
        String[] materialNames = (recipeMaterialNames.get(0) + " " +
                                  recipeMaterialNames.get(1) + " " +
                                  recipeMaterialNames.get(2)).split(" ");
        
        type.crafting = new ArrayList<>(9);
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
            type.recipe = new ShapedRecipe(type.luckyBlockItem);
        }else{
            type.recipe = new ShapedRecipe(new NamespacedKey(NewAmazingLuckyBlocks.getInstance(), "NewAmazingLuckyBlocks." + type.ID), type.luckyBlockItem);
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
            ItemStack item = ItemBuilder.newItem(materialAndData).build();
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
        TypeManager.removeRecipe(type.recipe);
        
        if(type.crafting.stream().anyMatch(item -> item.getType() != Material.AIR)){
            Bukkit.addRecipe(type.recipe);
        }
        
        // Load outcome packs
        type.packs = new HashMap<>();
        type.totalProbability = 0;
        List<String> packsProbList = config.getStringList("outcome_packs");
        for(String packProb : packsProbList){
            String[] splitted = packProb.split(";");
            String packName = splitted[0];
            int packProbability;
            try{
                packProbability = Integer.parseInt(splitted[1]);
            }catch(Exception ex){
                packProbability = 0;
            }
            
            OutcomePack pack = PackManager.getPack(packName);
            if(pack == null){
                Logger.log(String.format("Pack \"%s\" doesn't exist", packName));
            }else{
                pack.addLuckyBlockTypeToNotify(type);
                type.totalProbability += packProbability;
                type.packs.put(pack, packProbability);
            }            
        }
        if(type.packs.isEmpty()){
            Logger.warn(String.format("LuckyBlockType \"%s\" doesn't contain any valid outcome pack", 
                    type.typeName));
        }else if(type.totalProbability <= 0){
            Logger.warn(String.format("Total probability of LuckyBlockType \"%s\" must be positive", 
                    type.typeName));
        }
        
        type.data = new TypeData(type.luckyBlockItem);
        
        return type;
//</editor-fold>
    }
    
    public void saveToConfig(FileConfiguration config){
        //<editor-fold defaultstate="collapsed" desc="Code">
        
        // Save permissions
        config.set("permissions.break.enable", requireBreakPermission);
        config.set("permissions.break.permission", breakPermission);
        config.set("permissions.place.enable", requirePlacePermission);
        config.set("permissions.place.permission", placePermission);
        
        // Save item
        ItemBuilder builder = ItemBuilder.fromItem(luckyBlockItem, false);
        String name = builder.getDisplayName();
        List<String> lore = builder.getLore();
        
        config.set("name", name != null ? Logger.deColor(name) : "");
        config.set("lore", lore != null ? Logger.deColor(lore) : Collections.EMPTY_LIST);
        config.set("material", texture == null ? ItemBuilder.fromItem(luckyBlockItem, false).toString() : "");
        config.set("textureID", texture != null ? texture.toString() : "");
        
        // Save crafting recipe
        String row0 = ItemBuilder.fromItem(crafting.get(0), false).toString() + " " +
                      ItemBuilder.fromItem(crafting.get(1), false).toString() + " " +
                      ItemBuilder.fromItem(crafting.get(2), false).toString();
        String row1 = ItemBuilder.fromItem(crafting.get(3), false).toString() + " " +
                      ItemBuilder.fromItem(crafting.get(4), false).toString() + " " +
                      ItemBuilder.fromItem(crafting.get(5), false).toString();
        String row2 = ItemBuilder.fromItem(crafting.get(6), false).toString() + " " +
                      ItemBuilder.fromItem(crafting.get(7), false).toString() + " " +
                      ItemBuilder.fromItem(crafting.get(8), false).toString();
        config.set("crafting", Arrays.asList(row0, row1, row2));
        
        // Save outcome packs
        List<String> packNames = new ArrayList<>();
        packs.forEach((pack, probability) -> packNames.add(pack.getPackname() + ";" + probability));
        config.set("outcome_packs", packNames);
//</editor-fold>       
    }
    
    public Integer getProbabilityPack(OutcomePack pack){
        return packs.get(pack);
    }
    public void addProbabilityPack(OutcomePack pack, int probability){
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
    
    @Override
    public void execute(Player player, Location location){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(totalProbability <= 0){
            Logger.warn(String.format("Total probability of LuckyBlockType \"%s\" must be positive", 
                    typeName));
        }else{
            Random r = new Random();
            int randomNumber = r.nextInt(totalProbability);
            for(Map.Entry<OutcomePack, Integer> entry : packs.entrySet()){
                OutcomePack pack = entry.getKey();
                Integer probability = entry.getValue();
                randomNumber -= probability;
                if(randomNumber < 0){
                    pack.execute(player, location);
                    break;
                }
            }
        }
//</editor-fold>
    }
    

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + this.ID;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LuckyBlockType other = (LuckyBlockType) obj;
        return this.ID == other.ID;
    }
    
    public LuckyBlockType cloneType(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        LuckyBlockType clone = new LuckyBlockType(typeName);
        
        clone.ID = this.ID;
        
        clone.luckyBlockItem = this.getItem();
        
        clone.requireBreakPermission = this.requireBreakPermission;
        clone.breakPermission = this.breakPermission;
        clone.requirePlacePermission = this.requirePlacePermission;
        clone.placePermission = this.placePermission;
        
        clone.texture = this.texture;
        
        clone.crafting = new ArrayList<>(this.crafting);
        clone.recipe = this.recipe;
        
        clone.packs = new HashMap<>(this.packs);
        clone.totalProbability = this.totalProbability;
        
        clone.data = this.data;
        
        return clone;
//</editor-fold>
    }
    
}
