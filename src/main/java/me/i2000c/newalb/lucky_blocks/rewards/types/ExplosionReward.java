package me.i2000c.newalb.lucky_blocks.rewards.types;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import lombok.Getter;
import lombok.Setter;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.integration.WorldGuardManager;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.Reward;
import me.i2000c.newalb.lucky_blocks.rewards.RewardType;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

@Getter
@Setter
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
    
    @Override
    public ItemStack getItemToDisplay(){
        ItemStackWrapper builder = ItemStackWrapper.newItem(XMaterial.TNT);
        builder.setDisplayName("&4Explosion");
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
        
        return builder.toItemStack();
    }
    
    @Override
    public void saveRewardIntoConfig(Config config, String path){
        config.set(path + ".power", this.power);
        config.set(path + ".withFire", this.withFire);
        config.set(path + ".breakBlocks", this.breakBlocks);
    }
    
    @Override
    public void loadRewardFromConfig(Config config, String path){
        this.power = config.getInt(path + ".power");
        this.withFire = config.getBoolean(path + ".withFire");
        this.breakBlocks = config.getBoolean(path + ".breakBlocks");
    }
    
    @Override
    public void execute(Player player, Location location){
        if(!WorldGuardManager.canBreak(player, location)) {
            return;
        }
        
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
