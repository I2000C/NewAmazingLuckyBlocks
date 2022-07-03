package me.i2000c.newalb.custom_outcomes.utils.rewards;

import me.i2000c.newalb.custom_outcomes.menus.DarkHoleMenu;
import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.Timer;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        ItemStack stack = new ItemStack(Material.BUCKET);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Logger.color("&7DarkHole"));
        List<String> loreList = new ArrayList<>();
        if(this.depth < 0){
            loreList.add(Logger.color("&3Depth: &6infinite"));
        }else{
            loreList.add(Logger.color("&3Depth: &6" + this.depth));
        }
        loreList.add(Logger.color("&3Radius: &6" + this.radius));
        loreList.add(Logger.color("&3Ticks between blocks: &6" + this.ticks));
        loreList.add(Logger.color("&3Is squared: &6" + this.squared));
        meta.setLore(loreList);
        stack.setItemMeta(meta);
        
        return stack;
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
        Timer.getTimer().executeDarkHole(player, location, depth, radius, ticks, 0L, squared);
    }

    @Override
    public void edit(Player p){
        DarkHoleMenu.reset();
        DarkHoleMenu.reward = this;
        DarkHoleMenu.openDarkHoleMenu(p);
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.dark_hole;
    }
    
    @Override
    public Reward cloneReward(){
        DarkHoleReward reward = new DarkHoleReward(this.getOutcome());
        reward.depth = this.depth;
        reward.radius = this.radius;
        reward.ticks = this.ticks;
        reward.squared = this.squared;
        
        reward.setDelay(this.getDelay());
        return reward;
    }
}
