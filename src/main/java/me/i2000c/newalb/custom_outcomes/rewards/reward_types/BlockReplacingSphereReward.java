package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.RewardType;
import me.i2000c.newalb.custom_outcomes.rewards.TypeManager;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import me.i2000c.newalb.utils2.RandomUtils;
import me.i2000c.newalb.utils2.Task;
import me.i2000c.newalb.utils2.WorldGuardManager;
import me.i2000c.newalb.utils2.XMaterialUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
    private Map<XMaterial, Integer> materials;
    
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
        materials = new TreeMap<>();
        totalProbability = 0;
    }
    
    public void addMaterial(XMaterial material){
        int amount = this.materials.getOrDefault(material, 0);
        this.materials.put(material, amount+1);
        this.totalProbability++;
    }
    public void removeMaterial(XMaterial material){
        if(!this.materials.containsKey(material)){
            return;
        }
        
        int amount = this.materials.get(material);
        if(amount == 1){
            this.materials.remove(material);
            this.totalProbability--;
        }else if(amount > 1){
            this.materials.put(material, amount-1);
            this.totalProbability--;
        }
    }
    public void clearMaterials(){
        this.materials.clear();
        this.totalProbability = 0;
    }
    public boolean isEmptyMaterialList(){
        return this.materials.isEmpty();
    }
    
    public List<String> getSortedMaterialList(){
        List<String> sortedMaterials = new ArrayList<>();
        this.materials.forEach((material, amount) -> {
            String name = "   &3" + material.name() + " x" + amount;
            sortedMaterials.add(name);
        });
        return sortedMaterials;
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
        wrapper.addLore(getSortedMaterialList());
        
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
        materials.forEach((material, amount) -> aux.add(material.name() + ";" + amount));
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
        
        this.materials.clear();        
        List<String> aux = config.getStringList(path + ".materials");
        aux.forEach(materialString -> {
            String[] split = materialString.split(";");
            XMaterial material = XMaterialUtils.parseXMaterial(split[0]);
            int amount;
            try {
                amount = Integer.parseInt(split[1]);
            } catch(Exception ex) {
                amount = 1;
            }
            materials.put(material, amount);
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
                            if(block.isEmpty() || !block.getType().isBlock()){
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
                            
                            XMaterial material = getRandomMaterial();
                            if(material != null){
                                XBlock.setType(block, material);
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
    
    private XMaterial getRandomMaterial() {
        int randomNumber = RandomUtils.getInt(totalProbability);
        
        for(Map.Entry<XMaterial, Integer> entry : materials.entrySet()){
            XMaterial material = entry.getKey();
            Integer amount = entry.getValue();
            randomNumber -= amount;
            if(randomNumber < 0){
                return material;
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
