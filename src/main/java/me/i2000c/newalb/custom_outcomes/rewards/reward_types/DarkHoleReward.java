package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import lombok.Getter;
import lombok.Setter;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.RewardType;
import me.i2000c.newalb.listeners.interact.SpecialItems;
import me.i2000c.newalb.utils2.ItemStackWrapper;

@Getter
@Setter
public class DarkHoleReward extends Reward{
    private int depth;
    private int radius;
    private long ticks;
    private boolean squared;    
    
    public DarkHoleReward(Outcome outcome){
        super(outcome);
        depth = SpecialItems.dark_hole.getDefaultDepth();
        radius = SpecialItems.dark_hole.getDefaultRadius();
        ticks = SpecialItems.dark_hole.getDefaultTicks();
        squared = SpecialItems.dark_hole.isDefaultSquared();
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        ItemStackWrapper builder = ItemStackWrapper.newItem(XMaterial.BUCKET);
        builder.setDisplayName("&7DarkHole");
        if(this.depth < 0){
            builder.addLoreLine("&3Depth: &6infinite");
        }else{
            builder.addLoreLine("&3Depth: &6" + this.depth);
        }
        builder.addLoreLine("&3Radius: &6" + this.radius);
        builder.addLoreLine("&3Ticks between blocks: &6" + this.ticks);
        builder.addLoreLine("&3Is squared: &6" + this.squared);
        
        return builder.toItemStack();
    }
    
    @Override
    public void saveRewardIntoConfig(Config config, String path){
        config.set(path + ".depth", depth);
        config.set(path + ".radius", radius);
        config.set(path + ".ticks_between_blocks", ticks);
        config.set(path + ".squared", squared);
    }
    
    @Override
    public void loadRewardFromConfig(Config config, String path){
        depth = config.getInt(path + ".depth", SpecialItems.dark_hole.getDefaultDepth());
        radius = config.getInt(path + ".radius", SpecialItems.dark_hole.getDefaultRadius());
        ticks = config.getLong(path + ".ticks_between_blocks", SpecialItems.dark_hole.getDefaultTicks());
        squared = config.getBoolean(path + ".squared", SpecialItems.dark_hole.isDefaultSquared());
    }

    @Override
    public void execute(Player player, Location location){
        SpecialItems.dark_hole.execute(player, location, depth, radius, ticks, 0L, squared);
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.dark_hole;
    }
    
    @Override
    public Reward clone(){
        DarkHoleReward copy = (DarkHoleReward) super.clone();
        return copy;
    }
}
