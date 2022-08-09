package me.i2000c.newalb.custom_outcomes.utils.rewards;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.menus.LightningMenu;
import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.Offset;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
    
    public void setUsePlayerLoc(boolean usePlayerLoc){
        this.usePlayerLoc = usePlayerLoc;
    }
    public boolean getUsePlayerLoc(){
        return this.usePlayerLoc;
    }
    
    public void setCauseDamage(boolean causeDamage){
        this.causeDamage = causeDamage;
    }
    public boolean getCauseDamage(){
        return this.causeDamage;
    }
    
    public void setOffset(Offset offset){
        this.offset = offset;
    }
    public Offset getOffset(){
        return this.offset;
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.WHITE_WOOL);
        builder.withDisplayName("&eLightning");
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
        
        return builder.build();
    }
    
    @Override
    public void saveRewardIntoConfig(FileConfiguration config, String path){
        config.set(path + ".usePlayerLoc", usePlayerLoc);
        config.set(path + ".causeDamage", causeDamage);
        offset.saveToConfig(config, path + ".offset");
    }
    
    @Override
    public void loadRewardFromConfig(FileConfiguration config, String path){
        this.usePlayerLoc = config.getBoolean(path + ".usePlayerLoc");
        this.causeDamage = config.getBoolean(path + ".causeDamage");
        this.offset = new Offset(config, path + ".offset");
    }
    
    @Override
    public void execute(Player player, Location location){
        Location loc = usePlayerLoc ? player.getLocation().clone() : location.clone();
        offset.addToLocation(loc.clone());
        if(causeDamage){
            loc.getWorld().strikeLightning(loc);
        }else{
            loc.getWorld().strikeLightningEffect(loc);
        }        
    }
    
    @Override
    public void edit(Player player){
        LightningMenu.reset();
        LightningMenu.reward = this;
        LightningMenu.openLightningMenu(player);
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
