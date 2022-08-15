package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.RewardType;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ExplosionReward extends Reward{
    private int power;
    private boolean withFire;
    private boolean breakBlocks;
    
    public ExplosionReward(Outcome outcome){
        super(outcome);
        power = 4;
        withFire = true;
        breakBlocks = true;
    }

    public int getPower(){
        return power;
    }
    public void setPower(int power){
        this.power = power;
    }
    public boolean isWithFire(){
        return withFire;
    }
    public void setWithFire(boolean withFire){
        this.withFire = withFire;
    }
    public boolean isBreakBlocks(){
        return breakBlocks;
    }
    public void setBreakBlocks(boolean breakBlocks){
        this.breakBlocks = breakBlocks;
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.TNT);
        builder.withDisplayName("&4Explosion");
        builder.addLoreLine("&6Power: &e" + this.power);
        if(breakBlocks){
            builder.addLoreLine("&6Generate fire: &atrue");
        }else{
            builder.addLoreLine("&6Generate fire: &cfalse");
        }
        if(breakBlocks){
            builder.addLoreLine("&6Break blocks: &atrue");
        }else{
            builder.addLoreLine("&6Break blocks: &cfalse");
        }
        
        return builder.build();
    }
    
    @Override
    public void saveRewardIntoConfig(FileConfiguration config, String path){
        config.set(path + ".power", this.power);
        config.set(path + ".withFire", this.withFire);
        config.set(path + ".breakBlocks", this.breakBlocks);
    }
    
    @Override
    public void loadRewardFromConfig(FileConfiguration config, String path){
        this.power = config.getInt(path + ".power");
        this.withFire = config.getBoolean(path + ".withFire");
        this.breakBlocks = config.getBoolean(path + ".breakBlocks");
    }
    
    @Override
    public void execute(Player player, Location location){
        location.getWorld().createExplosion(location.getX(),
                                            location.getY(),
                                            location.getZ(),
                                            power, withFire, breakBlocks);
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.explosion;
    }
    
    @Override
    public Reward clone(){
        ExplosionReward copy = (ExplosionReward) super.clone();
        return copy;
    }
}
