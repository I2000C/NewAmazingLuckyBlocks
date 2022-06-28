package me.i2000c.newalb.custom_outcomes.utils.rewards;

import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import me.i2000c.newalb.utils.Logger;
import java.util.Arrays;
import me.i2000c.newalb.custom_outcomes.menus.ExplosionMenu;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        ItemStack stack = new ItemStack(Material.TNT);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Logger.color("&4Explosion"));
        String withFireString = this.withFire ? "&atrue" : "&cfalse";
        String breakBlocksString = this.breakBlocks ? "&atrue" : "&cfalse";
        meta.setLore(Logger.color(Arrays.asList("&6Power: &e" + this.power,
                                                "&6Generate fire: " + withFireString,
                                                "&6Break blocks: " + breakBlocksString)));
        stack.setItemMeta(meta);
        
        return stack;
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
    public void edit(Player player){
        ExplosionMenu.reset();
        ExplosionMenu.reward = this;
        ExplosionMenu.openExplosionMenu(player);
    }
    
    @Override
    public Reward.RewardType getRewardType(){
        return Reward.RewardType.explosion;
    }
    
    @Override
    public Reward cloneReward(){
        ExplosionReward reward = new ExplosionReward(this.getOutcome());
        reward.setDelay(this.getDelay());
        
        reward.power = this.power;
        reward.withFire = this.withFire;
        reward.breakBlocks = this.breakBlocks;
        return reward;
    }
}
