package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import org.bukkit.Location;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import lombok.Getter;
import lombok.Setter;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.RewardType;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import me.i2000c.newalb.utils2.Offset;
import me.i2000c.newalb.utils2.OtherUtils;
import me.i2000c.newalb.utils2.Task;
import me.i2000c.newalb.utils2.WorldGuardManager;

@Getter
@Setter
public class BlockReward extends Reward{
    private boolean usePlayerLoc;
    private boolean isFallingBlock;
    private XMaterial blockMaterial;
    private Offset offset;
    
    public BlockReward(Outcome outcome){
        super(outcome);
        usePlayerLoc = false;
        isFallingBlock = false;
        blockMaterial = null;
        offset = new Offset();
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        ItemStackWrapper wrapper = ItemStackWrapper.newItem(blockMaterial);
        switch(wrapper.getMaterial()){
            case WATER: wrapper.setMaterial(XMaterial.WATER_BUCKET); break;
            case LAVA:  wrapper.setMaterial(XMaterial.LAVA_BUCKET);  break;
            case FIRE:  wrapper.setMaterial(XMaterial.FIRE_CHARGE);  break;
        }
        
        wrapper.setDisplayName("&9Block");
        
        if(usePlayerLoc){
            wrapper.addLoreLine("&bTarget location: &2player");
        }else{
            wrapper.addLoreLine("&bTarget location: &6lucky block");
        }
        
        if(isFallingBlock){
            wrapper.addLoreLine("&6IsFallingBlock: &atrue");
        }else{
            wrapper.addLoreLine("&6IsFallingBlock: &7false");
        }
        
        wrapper.addLoreLine("&dOffset:");
        wrapper.addLoreLine("   &5X: &3" + offset.getOffsetX());
        wrapper.addLoreLine("   &5Y: &3" + offset.getOffsetY());
        wrapper.addLoreLine("   &5Z: &3" + offset.getOffsetZ());
        
        return wrapper.toItemStack();
    }
    
    @Override
    public void saveRewardIntoConfig(Config config, String path){
        config.set(path + ".usePlayerLoc", usePlayerLoc);
        config.set(path + ".isFallingBlock", isFallingBlock);
        config.set(path + ".material", blockMaterial);
        offset.saveToConfig(config, path + ".offset");
    }
    
    @Override
    public void loadRewardFromConfig(Config config, String path){
        this.usePlayerLoc = config.getBoolean(path + ".usePlayerLoc");
        this.isFallingBlock = config.getBoolean(path + ".isFallingBlock");
        String materialName = config.getString(path + ".material", null);
        if(materialName == null){
            // Support for old system
            materialName = config.getString(path + ".blockItem.material");
            int durability = config.getInt(path + ".blockItem.durability");
            materialName += ":" + durability;
        }
        this.blockMaterial = OtherUtils.parseXMaterial(materialName);
        this.offset = new Offset(config, path + ".offset");
    }
    
    @Override
    public void execute(Player player, Location location) {
        Location loc = usePlayerLoc ? player.getLocation().clone() : location.clone();
        offset.applyToLocation(loc);
        
        if(!WorldGuardManager.canBuild(player, loc)) {
            return;
        }
        
        ItemStackWrapper wrapper = ItemStackWrapper.newItem(blockMaterial);
        if(isFallingBlock) {
            FallingBlock fb = wrapper.spawnFallingBlock(loc);
            fb.setDropItem(false);
        } else {
            Task.runTask(() -> wrapper.placeAt(loc), 1L);             
        }        
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.block;
    }

    @Override
    public Reward clone(){
        BlockReward copy = (BlockReward) super.clone();
        copy.offset = this.offset.clone();
        return copy;
    }
}
