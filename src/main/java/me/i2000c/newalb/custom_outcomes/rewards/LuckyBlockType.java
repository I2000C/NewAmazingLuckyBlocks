package me.i2000c.newalb.custom_outcomes.rewards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import com.cryptomorin.xseries.XMaterial;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.textures.InvalidTextureException;
import me.i2000c.newalb.utils.textures.Texture;
import me.i2000c.newalb.utils.textures.TextureException;
import me.i2000c.newalb.utils.textures.TextureManager;
import me.i2000c.newalb.utils.textures.URLTextureException;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import me.i2000c.newalb.utils2.RandomUtils;

@Data
@EqualsAndHashCode(of = "ID")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LuckyBlockType implements Displayable, Executable {
    
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private int ID;
    
    private String typeName;
    
    private ItemStackWrapper luckyBlockItem;
    
    private boolean requireBreakPermission;
    private String breakPermission;
    private boolean requirePlacePermission;
    private String placePermission;
    
    @Setter(AccessLevel.NONE)
    private List<ItemStack> crafting;
    
    @Setter(AccessLevel.NONE)
    private ShapedRecipe recipe;
    
    @Setter(AccessLevel.NONE)
    private Map<OutcomePack, Integer> packs;
    
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private int totalProbability;
    
    private TypeData typeData;
    
    @Override
    public ItemStack getItemToDisplay(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemStackWrapper wrapper = this.luckyBlockItem.clone();
        wrapper.setDisplayName("&bIdentifier: &6" + this.typeName);
        wrapper.setLore();
        wrapper.addLoreLine("&5Material: &a" + luckyBlockItem.getMaterial().name());
        
        Texture texture = wrapper.getTexture();
        if(texture == null){
           wrapper.addLoreLine("&5Texture: &cnull"); 
        }else{
            String textureString = texture.toString().substring(0, 8) + "...";
            wrapper.addLoreLine("&5Texture: &b" + textureString);
        }
        
        String displayName = this.luckyBlockItem.getDisplayName();
        if(displayName != null){
            wrapper.addLoreLine(String.format("&5Item name: &r%s", displayName));
        }else{
            wrapper.addLoreLine("&5Item name: &cnull");
        }
        
        List<String> lore = this.luckyBlockItem.getLore();
        if(lore != null){
            wrapper.addLoreLine("&5Item lore:");
            lore.forEach(line -> wrapper.addLoreLine("    " + line));
        }else{
            wrapper.addLoreLine("&5Item lore: &cnull");
        }
        
        wrapper.addLoreLine("&5Place permission:");
        wrapper.addLoreLine("    &b" + this.placePermission);
        wrapper.addLoreLine("&5Break permission:");
        wrapper.addLoreLine("    &b" + this.breakPermission);
        wrapper.addLoreLine("&5Place permission required: " + (this.requirePlacePermission ? "&atrue" : "&7false"));
        wrapper.addLoreLine("&5Break permission required: " + (this.requireBreakPermission ? "&atrue" : "&7false"));
        
        wrapper.addLoreLine("&5Crafting:");
        wrapper.addLoreLine(String.format("    &e%s %s %s", 
                ItemStackWrapper.fromItem(this.crafting.get(0), false).toString(), 
                ItemStackWrapper.fromItem(this.crafting.get(1), false).toString(), 
                ItemStackWrapper.fromItem(this.crafting.get(2), false).toString()));
        wrapper.addLoreLine(String.format("    &e%s %s %s", 
                ItemStackWrapper.fromItem(this.crafting.get(3), false).toString(), 
                ItemStackWrapper.fromItem(this.crafting.get(4), false).toString(), 
                ItemStackWrapper.fromItem(this.crafting.get(5), false).toString()));
        wrapper.addLoreLine(String.format("    &e%s %s %s", 
                ItemStackWrapper.fromItem(this.crafting.get(6), false).toString(), 
                ItemStackWrapper.fromItem(this.crafting.get(7), false).toString(), 
                ItemStackWrapper.fromItem(this.crafting.get(8), false).toString()));
        
        wrapper.addLoreLine("&5Pack list:");
        this.packs.forEach((pack, probability) -> {
            wrapper.addLoreLine("    &2" + pack.getPackname() + ";" + probability);
        });
        
        return wrapper.toItemStack();
//</editor-fold>
    }
    
    public ItemStackWrapper getItem(){
        return luckyBlockItem.clone();
    }
    public void setItem(ItemStackWrapper item){
        this.luckyBlockItem = item.clone();
    }
    
    public boolean checkBreakPermission(Player player){
        return !requireBreakPermission || player.hasPermission(breakPermission);
    }
    public boolean checkPlacePermission(Player player){
        return !requirePlacePermission || player.hasPermission(placePermission);
    }    
    
    public LuckyBlockType(String typeName){
        //<editor-fold defaultstate="collapsed" desc="Code">
        this.typeName = typeName;
        this.ID = -1;
        
        luckyBlockItem = ItemStackWrapper.newItem(XMaterial.SPONGE);
        
        requireBreakPermission = false;
        breakPermission = "alb.lucky_block." + typeName + ".break";
        requirePlacePermission = false;
        placePermission = "alb.lucky_block." + typeName + ".place";
        
        crafting = new ArrayList<>(9);
        ItemStack air = new ItemStack(Material.AIR);
        for(int i=0; i<9; i++){
            crafting.add(air);
        }
        
        recipe = null;
        
        packs = new LinkedHashMap<>();
        totalProbability = 0;
        
        typeData = null;
//</editor-fold>
    } 
    
    public static LuckyBlockType loadFromConfig(Config config, String typeName){
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
        XMaterial material = config.getMaterial("material", null);
        String textureID = config.getString("textureID", null);
        
        boolean validMaterial = material != null;
        boolean validTexture = textureID != null && !textureID.trim().isEmpty();
        
        if(!validTexture && validMaterial) {
            type.luckyBlockItem = ItemStackWrapper.newItem(material);
        } else if(validTexture && !validMaterial) {
            try {
                Texture texture = new Texture(textureID);
                type.luckyBlockItem = ItemStackWrapper.fromItem(TextureManager.getItemSkullStack())
                                                      .setTexture(texture);
            } catch(InvalidTextureException ex) {
                Logger.err(String.format("Invalid texture for LuckyBlock type \"%s\"", type.typeName));
                return null;
            } catch(URLTextureException ex) {
                Logger.err(String.format("An error occured while loading texture for LuckyBlock type \"%s\":", type.typeName));
                Logger.err(ex);
                return null;
            } catch(TextureException ex) { }
        } else {
            return null;
        }
        
        type.luckyBlockItem.setDisplayName(name).setLore(lore);
        
        // Load crafting
        List<String> recipeMaterialNames = config.getStringList("crafting");
        String[] materialNames = (recipeMaterialNames.get(0) + " " +
                                  recipeMaterialNames.get(1) + " " +
                                  recipeMaterialNames.get(2)).split(" ");
        
        type.crafting = new ArrayList<>(9);
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()){
            type.recipe = new ShapedRecipe(type.luckyBlockItem.toItemStack());
        }else{
            NamespacedKey namespacedKey = new NamespacedKey(NewAmazingLuckyBlocks.getInstance(), "NewAmazingLuckyBlocks." + type.ID);
            type.recipe = new ShapedRecipe(namespacedKey, type.luckyBlockItem.toItemStack());
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
            ItemStack item = ItemStackWrapper.newItem(materialAndData).toItemStack();
            if(item.getType() != Material.AIR){
                if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()){
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
            Logger.warn(String.format("LuckyBlockType \"%s\" doesn't contain any valid outcome pack", type.typeName));
        }else if(type.totalProbability <= 0){
            Logger.warn(String.format("Total probability of LuckyBlockType \"%s\" must be positive", type.typeName));
        }
        
        type.typeData = new TypeData(type.luckyBlockItem);
        
        return type;
//</editor-fold>
    }
    
    public void saveToConfig(Config config){
        //<editor-fold defaultstate="collapsed" desc="Code">
        
        // Save permissions
        config.set("permissions.break.enable", requireBreakPermission);
        config.set("permissions.break.permission", breakPermission);
        config.set("permissions.place.enable", requirePlacePermission);
        config.set("permissions.place.permission", placePermission);
        
        // Save item
        String name = luckyBlockItem.getDisplayName();
        List<String> lore = luckyBlockItem.getLore();
        
        XMaterial material = luckyBlockItem.getMaterial();
        Texture texture = luckyBlockItem.getTexture();
        
        config.set("name", name != null ? Logger.deColor(name) : "");
        config.set("lore", lore != null ? Logger.deColor(lore) : Collections.EMPTY_LIST);
        if(texture == null) {
            config.set("material", material);
        } else {
            config.set("textureID", texture.toString());
        }
        
        // Save crafting recipe
        String row0 = ItemStackWrapper.fromItem(crafting.get(0), false).toString() + " " +
                      ItemStackWrapper.fromItem(crafting.get(1), false).toString() + " " +
                      ItemStackWrapper.fromItem(crafting.get(2), false).toString();
        String row1 = ItemStackWrapper.fromItem(crafting.get(3), false).toString() + " " +
                      ItemStackWrapper.fromItem(crafting.get(4), false).toString() + " " +
                      ItemStackWrapper.fromItem(crafting.get(5), false).toString();
        String row2 = ItemStackWrapper.fromItem(crafting.get(6), false).toString() + " " +
                      ItemStackWrapper.fromItem(crafting.get(7), false).toString() + " " +
                      ItemStackWrapper.fromItem(crafting.get(8), false).toString();
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
    
    @Override
    public void execute(Player player, Location location){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(totalProbability <= 0){
            Logger.warn(String.format("Total probability of LuckyBlockType \"%s\" must be positive", typeName));
        }else{
            int randomNumber = RandomUtils.getInt(totalProbability);
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
    
    @SneakyThrows
    @Override
    public LuckyBlockType clone(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        LuckyBlockType clone = (LuckyBlockType) super.clone();
        
        clone.luckyBlockItem = this.getItem();
        clone.crafting = new ArrayList<>(this.crafting);
        clone.packs = new HashMap<>(this.packs);
        
        return clone;
//</editor-fold>
    }
    
}
