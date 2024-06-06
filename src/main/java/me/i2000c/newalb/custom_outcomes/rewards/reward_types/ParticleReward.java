package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.RewardType;

public class ParticleReward extends Reward{
    public ParticleReward(Outcome outcome){
        super(outcome);
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        
        return null;
    }
    
    @Override
    public void saveRewardIntoConfig(Config config, String path){
        
    }
    
    @Override
    public void loadRewardFromConfig(Config config, String path){
        
    }
    
    @Override
    public void execute(Player player, Location location){
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.particle;
    }

    @Override
    public Reward clone(){
        ParticleReward copy = (ParticleReward) super.clone();
        return copy;
    }
}
