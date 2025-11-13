package me.i2000c.newalb.lucky_blocks.rewards.types;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import lombok.Setter;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.listeners.interact.SpecialItems;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.Reward;
import me.i2000c.newalb.lucky_blocks.rewards.RewardType;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class MiniVolcanoReward extends Reward{
    private int height;
    private XMaterial baseMaterial;
    private XMaterial lavaMaterial;
    private long ticks;
    private boolean squared;    
    private boolean throwBlocks;
    
    public MiniVolcanoReward(Outcome outcome){
        super(outcome);
        height = SpecialItems.mini_volcano.getDefaultHeight();
        baseMaterial = SpecialItems.mini_volcano.getDefaultBaseMaterial();
        lavaMaterial = SpecialItems.mini_volcano.getDefaultLavaMaterial();
        ticks = SpecialItems.mini_volcano.getDefaultTicks();
        squared = SpecialItems.mini_volcano.isDefaultSquared();
        throwBlocks = SpecialItems.mini_volcano.isDefaultEnableThrowBlocks();
    }    
    
    @Override
    public ItemStack getItemToDisplay(){
        return ItemStackWrapper.newItem(XMaterial.LAVA_BUCKET)
                               .setDisplayName("&cMiniVolcano")
                               .addLoreLine("&bHeight: &6" + this.height)
                               .addLoreLine("&bBase material: &6" + this.baseMaterial)
                               .addLoreLine("&bLava material: &6" + this.lavaMaterial)
                               .addLoreLine("&bTicks between blocks: &6" + this.ticks)
                               .addLoreLine("&bIs squared: &6" + this.squared)
                               .addLoreLine("&bThrow blocks: &6" + this.throwBlocks)
                               .toItemStack();
    }
    
    @Override
    public void saveRewardIntoConfig(Config config, String path){
        config.set(path + ".height", height);
        config.set(path + ".baseMaterial", baseMaterial);
        config.set(path + ".lavaMaterial", lavaMaterial);
        config.set(path + ".ticks_between_blocks", ticks);
        config.set(path + ".squared", squared);
        config.set(path + ".throw_blocks", throwBlocks);
    }
    
    @Override
    public void loadRewardFromConfig(Config config, String path){
        height = config.getInt(path + ".height", SpecialItems.mini_volcano.getDefaultHeight());
        baseMaterial = config.getMaterial(path + ".baseMaterial", SpecialItems.mini_volcano.getDefaultBaseMaterial());
        lavaMaterial = config.getMaterial(path + ".lavaMaterial", SpecialItems.mini_volcano.getDefaultLavaMaterial());
        ticks = config.getLong(path + ".ticks_between_blocks", SpecialItems.mini_volcano.getDefaultTicks());
        squared = config.getBoolean(path + ".squared", SpecialItems.mini_volcano.isDefaultSquared());
        throwBlocks = config.getBoolean(path + ".throw_blocks", SpecialItems.mini_volcano.isDefaultEnableThrowBlocks());
    }

    @Override
    public void execute(Player player, Location location){
        SpecialItems.mini_volcano.execute(player, location.clone().add(0, -1, 0), height, baseMaterial, lavaMaterial, ticks, 0L, squared, throwBlocks);
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.mini_volcano;
    }
    
    @Override
    public Reward clone(){
        MiniVolcanoReward copy = (MiniVolcanoReward) super.clone();
        return copy;
    }
}
