package me.i2000c.newalb.custom_outcomes.utils.rewards;

import me.i2000c.newalb.custom_outcomes.utils.Displayable;
import me.i2000c.newalb.custom_outcomes.utils.Executable;
import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public abstract class Reward implements Displayable, Executable, Cloneable{
    public abstract RewardType getRewardType();
    public abstract void loadRewardFromConfig(FileConfiguration config, String path);
    public abstract void saveRewardIntoConfig(FileConfiguration config, String path);
    public abstract void edit(Player p);

    private final Outcome outcome;
    private int delay;

    public Reward(Outcome outcome){
        this.outcome = outcome;
        this.delay = 0;
    }
    public Outcome getOutcome(){
        return this.outcome;
    }

    public int getDelay(){
        return this.delay;
    }
    public void setDelay(int ticks){
        this.delay = ticks;
    }
    
    @Override
    public Reward clone(){
        try{
            return (Reward) super.clone();
        }catch(CloneNotSupportedException ex){
            return null;
        }
    }


    public static enum RewardType{
        item,
        command,
        entity,
        tower_entity,
        firework,
        sound,
        particle,
        structure,
        block,
        lightning,
        dark_hole,
        mini_volcano,
        message,
        effect,
        explosion,
        block_replacing_sphere,
        trap;
    }
}


    

