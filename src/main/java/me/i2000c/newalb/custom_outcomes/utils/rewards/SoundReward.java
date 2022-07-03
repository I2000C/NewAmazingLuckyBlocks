package me.i2000c.newalb.custom_outcomes.utils.rewards;

import me.i2000c.newalb.custom_outcomes.menus.SoundMenu;
import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import me.i2000c.newalb.utils.Logger;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SoundReward extends Reward{
    private String type;
    private double volume;
    private double pitch;
    
    public SoundReward(Outcome outcome){
        super(outcome);
        type = null;
        volume = 20.0;
        pitch = 1.0;
    }

    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type = type;
    }
    public double getVolume(){
        return volume;
    }
    public void setVolume(double volume){
        this.volume = volume;
    }
    public double getPitch(){
        return pitch;
    }
    public void setPitch(double pitch){
        this.pitch = pitch;
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        List<String> lore = new ArrayList();
        double truncated = BigDecimal.valueOf(this.volume).setScale(3, RoundingMode.HALF_UP).doubleValue();
        lore.add(Logger.color("&3Volume: &6" + truncated));
        truncated = BigDecimal.valueOf(this.pitch).setScale(3, RoundingMode.HALF_UP).doubleValue();
        lore.add(Logger.color("&3Pitch: &6" + truncated));
        
        ItemStack stack = new ItemStack(Material.NOTE_BLOCK);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Logger.color("&dSound: &e" + this.type));
        meta.setLore(lore);
        stack.setItemMeta(meta);
        
        return stack;
    }
    
    @Override
    public void saveRewardIntoConfig(FileConfiguration config, String path){
        config.set(path + ".type", this.type);
        double truncated = BigDecimal.valueOf(this.volume).setScale(3, RoundingMode.HALF_UP).doubleValue();
        config.set(path + ".volume", truncated);
        truncated = BigDecimal.valueOf(this.pitch).setScale(3, RoundingMode.HALF_UP).doubleValue();
        config.set(path + ".pitch", truncated);
    }
    
    @Override
    public void loadRewardFromConfig(FileConfiguration config, String path){
        this.type = config.getString(path + ".type");
        this.volume = config.getDouble(path + ".volume");
        this.pitch = config.getDouble(path + ".pitch");
    }
    
    @Override
    public void execute(Player player, Location location){
        location.getWorld().playSound(location, Sound.valueOf(this.type), (float) this.volume, (float) pitch);
    }
    
    @Override
    public void edit(Player player){
        SoundMenu.reset();
        SoundMenu.reward = this;
        SoundMenu.openSoundMenu(player);
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.sound;
    }

    @Override
    public Reward cloneReward(){
        SoundReward reward = new SoundReward(this.getOutcome());
        reward.type = this.type;
        reward.volume = this.volume;
        reward.pitch = this.pitch;
        
        reward.setDelay(this.getDelay());
        return reward;
    }
}
