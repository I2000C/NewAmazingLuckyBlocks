package me.i2000c.newalb.lucky_blocks.rewards.types;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import lombok.Getter;
import lombok.Setter;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.Reward;
import me.i2000c.newalb.lucky_blocks.rewards.RewardType;
import me.i2000c.newalb.utils.locations.Offset;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

@Getter
@Setter
public class LightningReward extends Reward{
    private boolean usePlayerLoc;
    private boolean causeDamage;
    private Offset offset;
    
    public LightningReward(Outcome outcome){
        super(outcome);
        usePlayerLoc = false;
        causeDamage = true;
        offset = new Offset();
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        ItemStackWrapper builder = ItemStackWrapper.newItem(XMaterial.WHITE_WOOL);
        builder.setDisplayName("&eLightning");
        if(usePlayerLoc){
            builder.addLoreLine("&bTarget location: &2player");
        }else{
            builder.addLoreLine("&bTarget location: &6lucky block");
        }
        if(causeDamage){
            builder.addLoreLine("&cCause damage: &atrue");
        }else{
           builder.addLoreLine("&cCause damage: &7false");
        }
        builder.addLoreLine("&dOffset:");
        builder.addLoreLine("   &5X: &3" + offset.getOffsetX());
        builder.addLoreLine("   &5Y: &3" + offset.getOffsetY());
        builder.addLoreLine("   &5Z: &3" + offset.getOffsetZ());
        
        return builder.toItemStack();
    }
    
    @Override
    public void saveRewardIntoConfig(Config config, String path){
        config.set(path + ".usePlayerLoc", usePlayerLoc);
        config.set(path + ".causeDamage", causeDamage);
        offset.saveToConfig(config, path + ".offset");
    }
    
    @Override
    public void loadRewardFromConfig(Config config, String path){
        this.usePlayerLoc = config.getBoolean(path + ".usePlayerLoc");
        this.causeDamage = config.getBoolean(path + ".causeDamage");
        this.offset = new Offset(config, path + ".offset");
    }
    
    @Override
    public void execute(Player player, Location location){
        Location loc;
        if(usePlayerLoc){
            loc = player.getLocation().clone();
        }else{
            loc = location.clone().subtract(0.5, 0, 0.5);
        }
        
        offset.applyToLocation(loc);
        if(causeDamage){
            loc.getWorld().strikeLightning(loc);
        }else{
            loc.getWorld().strikeLightningEffect(loc);
        }        
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.lightning;
    }

    @Override
    public Reward clone(){
        LightningReward copy = (LightningReward) super.clone();
        copy.offset = this.offset.clone();
        return copy;
    }
}
