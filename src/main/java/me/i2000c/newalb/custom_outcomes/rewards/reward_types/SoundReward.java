package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.RewardType;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import me.i2000c.newalb.utils2.OtherUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class SoundReward extends Reward {
    
    public static final BigDecimal MIN_VOLUME = BigDecimal.ZERO;
    public static final BigDecimal MAX_VOLUME = BigDecimal.ONE;
    public static final BigDecimal DEFAULT_VOLUME = BigDecimal.ONE;
    public static final BigDecimal MIN_PITCH = new BigDecimal("0.5");
    public static final BigDecimal MAX_PITCH = new BigDecimal("2.0");
    public static final BigDecimal DEFAULT_PITCH = BigDecimal.ONE;
    
    private XSound type;
    private BigDecimal volume;
    private BigDecimal pitch;
    
    public SoundReward(Outcome outcome){
        super(outcome);
        type = null;
        volume = DEFAULT_VOLUME;
        pitch = DEFAULT_PITCH;
    }
    
    public void setVolume(@NonNull BigDecimal volume) {
        this.volume = OtherUtils.clamp(volume, MIN_VOLUME, MAX_VOLUME);
    }
    public void setPitch(@NonNull BigDecimal pitch) {
        this.pitch = OtherUtils.clamp(pitch, MIN_PITCH, MAX_PITCH);
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        ItemStackWrapper builder = ItemStackWrapper.newItem(XMaterial.NOTE_BLOCK);
        builder.setDisplayName("&dSound: &e" + this.type.name());
        
        builder.addLoreLine("&3Volume: &6" + volume);
        builder.addLoreLine("&3Pitch: &6" + pitch);
        
        return builder.toItemStack();
    }
    
    @Override
    public void saveRewardIntoConfig(Config config, String path){
        config.set(path + ".type", type.name());
        config.set(path + ".volume", volume);
        config.set(path + ".pitch", pitch);
    }
    
    @Override
    public void loadRewardFromConfig(Config config, String path){
        String soundName = config.getString(path + ".type");
        
        XSound sound;
        Optional<XSound> soundOpt = XSound.matchXSound(soundName);
        if(!soundOpt.isPresent()) {
            Logger.warn("Sound '" + soundName + "' is not supported in this version. It will be replaced with entity.player.small_fall");
            sound = XSound.ENTITY_PLAYER_SMALL_FALL;
        } else {
            sound = soundOpt.get();
        }
        
        if(!sound.isSupported()) {
            Logger.warn("Sound '" + sound.name() + "' is not supported in this version. It will be replaced with entity.player.small_fall");
            sound = XSound.ENTITY_PLAYER_SMALL_FALL;
        }

        this.type = sound;
        this.volume = new BigDecimal(config.getString(path + ".volume"));
        this.pitch = new BigDecimal(config.getString(path + ".pitch"));
        
        this.volume = OtherUtils.clamp(this.volume, MIN_VOLUME, MAX_VOLUME);
        this.pitch = OtherUtils.clamp(this.pitch, MIN_PITCH, MAX_PITCH);
    }
    
    @Override
    public void execute(Player player, Location location){
        if(type == null) {
            return;
        }
        
        type.play(player, volume.floatValue(), pitch.floatValue());
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
