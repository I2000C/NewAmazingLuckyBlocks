package me.i2000c.newalb.custom_outcomes.utils.rewards;

import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ParticleReward extends Reward{
    public ParticleReward(Outcome outcome){
        super(outcome);
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        
        return null;
    }
    
    @Override
    public void saveRewardIntoConfig(FileConfiguration config, String path){
        
    }
    
    @Override
    public void loadRewardFromConfig(FileConfiguration config, String path){
        
    }
    
    @Override
    public void execute(Player player, Location location){
    }
    
    @Override
    public void edit(Player player){
        
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.particle;
    }

    @Override
    public Reward cloneReward(){
        ParticleReward reward = new ParticleReward(this.getOutcome());
        
        reward.setDelay(this.getDelay());
        return reward;
    }
}
