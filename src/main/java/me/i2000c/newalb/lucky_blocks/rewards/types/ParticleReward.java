package me.i2000c.newalb.lucky_blocks.rewards.types;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.Reward;
import me.i2000c.newalb.lucky_blocks.rewards.RewardType;

public class ParticleReward extends Reward<ParticleReward> {
    public ParticleReward(Outcome outcome) {
        super(outcome);
    }
    
    @Override
    public ItemStack getItemToDisplay() {
        
        return null;
    }
    
    @Override
    public void saveRewardIntoConfig(Config config, String path) {
        
    }
    
    @Override
    public void loadRewardFromConfig(Config config, String path) {
        
    }
    
    @Override
    public void execute(Player player, Location location) {
    }
    
    @Override
    public RewardType getRewardType() {
        return null;
    }
    
    @Override
    public EditorMenu<ParticleReward> getEditor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ParticleReward clone() {
        ParticleReward copy = (ParticleReward) super.clone();
        return copy;
    }
}
