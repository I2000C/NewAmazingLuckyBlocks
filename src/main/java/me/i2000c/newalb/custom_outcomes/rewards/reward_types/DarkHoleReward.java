package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.RewardType;
import me.i2000c.newalb.listeners.interact.SpecialItems;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DarkHoleReward extends Reward{
    private int depth;
    private double radius;
    private long ticks;
    private boolean squared;

    public int getDepth(){
        return depth;
    }
    public void setDepth(int depth){
        this.depth = depth;
    }
    
    public double getRadius(){
        return radius;
    }
    public void setRadius(double radius){
        this.radius = radius;
    }
    
    public long getTicks(){
        return ticks;
    }
    public void setTicks(long ticks){
        this.ticks = ticks;
    }
    
    public boolean isSquared(){
        return squared;
    }
    public void setSquared(boolean squared){
        this.squared = squared;
    }    
    
    
    public DarkHoleReward(Outcome outcome){
        super(outcome);
        depth = ConfigManager.getConfig().getInt("Objects.DarkHole.number-of-blocks");
        radius = ConfigManager.getConfig().getDouble("Objects.DarkHole.radius");
        ticks = ConfigManager.getConfig().getLong("Objects.DarkHole.time-between-one-block-and-the-next");
        squared = true;
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.BUCKET);
        builder.withDisplayName("&7DarkHole");
        if(this.depth < 0){
            builder.addLoreLine("&3Depth: &6infinite");
        }else{
            builder.addLoreLine("&3Depth: &6" + this.depth);
        }
        builder.addLoreLine("&3Radius: &6" + this.radius);
        builder.addLoreLine("&3Ticks between blocks: &6" + this.ticks);
        builder.addLoreLine("&3Is squared: &6" + this.squared);
        
        return builder.build();
    }
    
    @Override
    public void saveRewardIntoConfig(FileConfiguration config, String path){
        config.set(path + ".depth", depth);
        config.set(path + ".radius", radius);
        config.set(path + ".ticks_between_blocks", ticks);
        config.set(path + ".squared", squared);
    }
    
    @Override
    public void loadRewardFromConfig(FileConfiguration config, String path){
        depth = config.getInt(path + ".depth");
        radius = config.getDouble(path + ".radius");
        ticks = config.getLong(path + ".ticks_between_blocks");
        squared = config.getBoolean(path + ".squared");
    }

    @Override
    public void execute(Player player, Location location){
        SpecialItems.dark_hole.execute(location, depth, depth, ticks, 0L, squared);
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
