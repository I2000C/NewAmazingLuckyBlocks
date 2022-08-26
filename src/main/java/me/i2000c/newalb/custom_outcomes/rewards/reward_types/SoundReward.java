package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import java.math.BigDecimal;
import java.math.RoundingMode;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.RewardType;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SoundReward extends Reward{
    private Sound type;
    private double volume;
    private double pitch;
    
    public SoundReward(Outcome outcome){
        super(outcome);
        type = null;
        volume = 10.0;
        pitch = 1.0;
    }

    public Sound getType(){
        return type;
    }
    public void setType(Sound type){
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
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.NOTE_BLOCK);
        builder.withDisplayName("&dSound: &e" + this.type);
        
        double value = BigDecimal.valueOf(this.volume).setScale(3, RoundingMode.HALF_UP).doubleValue();
        builder.addLoreLine("&3Volume: &6" + value);
        value = BigDecimal.valueOf(this.pitch).setScale(3, RoundingMode.HALF_UP).doubleValue();
        builder.addLoreLine("&3Pitch: &6" + value);
        
        return builder.build();
    }
    
    @Override
    public void saveRewardIntoConfig(FileConfiguration config, String path){
        XSound xsound = XSound.matchXSound(this.type);
        config.set(path + ".type", xsound.name());
        double truncated = BigDecimal.valueOf(this.volume).setScale(3, RoundingMode.HALF_UP).doubleValue();
        config.set(path + ".volume", truncated);
        truncated = BigDecimal.valueOf(this.pitch).setScale(3, RoundingMode.HALF_UP).doubleValue();
        config.set(path + ".pitch", truncated);
    }
    
    @Override
    public void loadRewardFromConfig(FileConfiguration config, String path){
        try{
            XSound xsound = XSound.valueOf(config.getString(path + ".type"));
            this.type = xsound.parseSound();
        }catch(IllegalArgumentException ex){
            this.type = Sound.valueOf(config.getString(path + ".type"));
        }            
        this.volume = config.getDouble(path + ".volume");
        this.pitch = config.getDouble(path + ".pitch");
    }
    
    @Override
    public void execute(Player player, Location location){
        location.getWorld().playSound(location, type, (float) volume, (float) pitch);
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.sound;
    }

    @Override
    public Reward clone(){
        SoundReward copy = (SoundReward) super.clone();
        return copy;
    }
}
