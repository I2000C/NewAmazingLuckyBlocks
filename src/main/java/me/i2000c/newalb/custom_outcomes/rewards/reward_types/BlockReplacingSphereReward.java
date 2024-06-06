package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.RewardType;
import me.i2000c.newalb.custom_outcomes.rewards.TypeManager;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import me.i2000c.newalb.utils2.RandomUtils;
import me.i2000c.newalb.utils2.Task;
import me.i2000c.newalb.utils2.WorldGuardManager;

@Getter
@Setter
public class BlockReplacingSphereReward extends Reward{
    private int minRadius;
    private int maxRadius;
    private int ticksBetweenLayers;
    private boolean usePlayerLoc;
    private boolean replaceLiquids;
    
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Map<ItemStack, Integer> materials;
    
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private int totalProbability;
    
    public BlockReplacingSphereReward(Outcome outcome){
        super(outcome);
        minRadius = 0;
        maxRadius = 5;
        ticksBetweenLayers = 10;
        usePlayerLoc = false;
        replaceLiquids = false;
        materials = new LinkedHashMap<>();
        totalProbability = 0;
    }
    
    public void addItemStack(ItemStack stack){
        ItemStack item = new ItemStack(stack.getType());
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()){
            item.setDurability(stack.getDurability());
        }
        
        int amount = this.materials.getOrDefault(item, 0);
        this.materials.put(item, amount+1);
        this.totalProbability++;
    }
    public void removeItemStack(ItemStack stack){
        ItemStack item = new ItemStack(stack.getType());
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()){
            item.setDurability(stack.getDurability());
        }
        if(!this.materials.containsKey(stack)){
            return;
        }
        
        int amount = this.materials.get(item);
        if(amount == 1){
            this.materials.remove(item);
            this.totalProbability--;
        }else if(amount > 1){
            this.materials.put(item, amount-1);
            this.totalProbability--;
        }
    }
    public void clearMaterials(){
        this.materials.clear();
        this.totalProbability = 0;
    }
    public boolean emptyMaterialList(){
        return this.materials.isEmpty();
    }
    
    private static final Comparator<ItemStack> COMPARATOR = (item1, item2) -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        int compared = item1.getType().name().compareTo(item2.getType().name());
        if(compared == 0){
            if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()){
                Integer durability1 = Integer.valueOf(item1.getDurability());
                Integer durability2 = Integer.valueOf(item2.getDurability());
                
                return durability1.compareTo(durability2);
            }else{
                return compared;
            }
        }else{
            return compared;
        }
//</editor-fold>
    };
    
    public List<String> getOrderedMaterialList(){
        List<String> orderedMaterials = new ArrayList<>();
        this.materials.keySet().stream()
                .sorted(COMPARATOR)
                .forEachOrdered(item -> orderedMaterials.add("   &3" + 
                        ItemStackWrapper.fromItem(item, false).toString() + 
                        " x" + this.materials.get(item)));
        return orderedMaterials;
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        ItemStackWrapper wrapper = ItemStackWrapper.newItem(XMaterial.DIAMOND_ORE);
        wrapper.setDisplayName("&bBlock Replacing Sphere");
        wrapper.addLoreLine("&dMin radius: &3" + this.minRadius);
        wrapper.addLoreLine("&dMax radius: &3" + this.maxRadius);
        wrapper.addLoreLine("&dTicks between layers: &3" + this.ticksBetweenLayers);
        if(this.usePlayerLoc){
            wrapper.addLoreLine("&dReplace liquids: &atrue");
        }else{
            wrapper.addLoreLine("&dReplace liquids: &cfalse");
        }
        if(this.replaceLiquids){
            wrapper.addLoreLine("&dUse player location: &atrue");
        }else{
            wrapper.addLoreLine("&dUse player location: &cfalse");
        }
        wrapper.addLoreLine("&dMaterials:");
        wrapper.addLore(getOrderedMaterialList());
        
        return wrapper.toItemStack();
    }
    
    @Override
    public void saveRewardIntoConfig(Config config, String path){
        config.set(path + ".minRadius", this.minRadius);
        config.set(path + ".maxRadius", this.maxRadius);
        config.set(path + ".ticksBetweenLayers", this.ticksBetweenLayers);
        config.set(path + ".usePlayerLoc", this.usePlayerLoc);
        config.set(path + ".replaceLiquids", this.replaceLiquids);
        
        List<String> aux = new ArrayList<>();
        materials.keySet().stream()
                .sorted(COMPARATOR)
                .forEachOrdered(item -> aux.add(ItemStackWrapper.fromItem(item, false).toString() + ";" + materials.get(item)));
        config.set(path + ".materials", aux);
    }
    
    @Override
    public void loadRewardFromConfig(Config config, String path){
        this.minRadius = config.getInt(path + ".minRadius");
        this.maxRadius = config.getInt(path + ".maxRadius");
        this.ticksBetweenLayers = config.getInt(path + ".ticksBetweenLayers");
        this.usePlayerLoc = config.getBoolean(path + ".usePlayerLoc");
        this.replaceLiquids = config.getBoolean(path + ".replaceLiquids");
        this.totalProbability = 0;
        
        if(this.materials == null){
            this.materials = new LinkedHashMap<>();
        }else{
            this.materials.clear();
        }
        List<String> aux = config.getStringList(path + ".materials");
        aux.stream().map(materialData -> materialData.split(";")).forEach(splitted -> {
            ItemStack item = ItemStackWrapper.newItem(splitted[0]).toItemStack();
            int amount;
            try{
                amount = Integer.parseInt(splitted[1]);
            }catch(Exception ex){
                amount = 1;
            }
            materials.put(item, amount);
            totalProbability += amount;
        });
    }
    
    @Override
    public void execute(Player player, Location location){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Location source = this.usePlayerLoc ? player.getLocation() : location;
        
        Task task = new Task(){
            int currentRadius = minRadius;
            Set<Location> placedLocations = new LinkedHashSet<>();
            
            @Override
            public void run(){
                if(currentRadius >= maxRadius){
                    cancel();
                    return;
                }
                
                double currentRadiusSquared = currentRadius * currentRadius;
                
                int minX = source.getBlockX() - currentRadius;
                int maxX = source.getBlockX() + currentRadius;
                int minY = source.getBlockY() - currentRadius;
                int maxY = source.getBlockY() + currentRadius;
                int minZ = source.getBlockZ() - currentRadius;
                int maxZ = source.getBlockZ() + currentRadius;
                for(int x=minX; x<=maxX; x++){
                    for(int y=minY; y<=maxY; y++){
                        for(int z=minZ; z<=maxZ; z++){
                            Block block = source.getWorld().getBlockAt(x, y, z);
                            if(block.getType() == Material.AIR || !block.getType().isBlock()){
                                continue;
                            }
                            
                            if(!replaceLiquids && block.isLiquid()){
                                continue;
                            }
                            
                            if(block.getLocation().distanceSquared(source) > currentRadiusSquared){
                                continue;
                            }
                            
                            if(placedLocations.contains(block.getLocation())){
                                continue;
                            }
                            
                            if(TypeManager.getType(block) != null){
                                continue;
                            }
                            
                            if(!WorldGuardManager.canBuild(player, block.getLocation())) {
                                continue;
                            }
                            
                            ItemStack item = getRandomItem();
                            if(item != null){
                                block.setType(item.getType());
                                if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()){
                                    block.setData((byte) item.getDurability());
                                }
                                placedLocations.add(block.getLocation());
                            }
                        }
                    }
                }
                currentRadius++;
            }
        };
        task.runTask(0L, ticksBetweenLayers);
//</editor-fold>
    }
    
    private ItemStack getRandomItem(){
        int randomNumber = RandomUtils.getInt(totalProbability);
        
        for(Map.Entry<ItemStack, Integer> entry : materials.entrySet()){
            ItemStack item = entry.getKey();
            Integer amount = entry.getValue();
            randomNumber -= amount;
            if(randomNumber < 0){
                return item;
            }
        }
        
        return null;
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.block_replacing_sphere;
    }
    
    @Override
    public Reward clone(){
        BlockReplacingSphereReward copy = (BlockReplacingSphereReward) super.clone();
        copy.materials = new LinkedHashMap<>(this.materials);
        return copy;
    }
}
