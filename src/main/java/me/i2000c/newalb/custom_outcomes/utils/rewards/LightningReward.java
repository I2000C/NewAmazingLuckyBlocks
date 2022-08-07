package me.i2000c.newalb.custom_outcomes.utils.rewards;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.utils2.Offset;
import me.i2000c.newalb.custom_outcomes.menus.LightningMenu;
import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import me.i2000c.newalb.utils.Logger;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        ItemStack stack = XMaterial.WHITE_WOOL.parseItem();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("&eLightning");
        List<String> loreList = new ArrayList<>();
        if(usePlayerLoc){
            loreList.add("&bTarget location: &2player");
        }else{
            loreList.add("&bTarget location: &6lucky block");
        }
        if(causeDamage){
            loreList.add("&cCause damage: &atrue");
        }else{
            loreList.add("&cCause damage: &7false");
        }
        loreList.add("&dOffset:");
        loreList.add("   &5X: &3" + offset.getOffsetX());
        loreList.add("   &5Y: &3" + offset.getOffsetY());
        loreList.add("   &5Z: &3" + offset.getOffsetZ());
        meta.setLore(loreList);
        stack.setItemMeta(meta);
        
        return stack;
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
    public Reward cloneReward(){
        LightningReward reward = new LightningReward(this.getOutcome());
        reward.usePlayerLoc = this.usePlayerLoc;
        reward.causeDamage = this.causeDamage;
        reward.offset = this.offset.cloneOffset();
        
        reward.setDelay(this.getDelay());
        return reward;
    }
}
