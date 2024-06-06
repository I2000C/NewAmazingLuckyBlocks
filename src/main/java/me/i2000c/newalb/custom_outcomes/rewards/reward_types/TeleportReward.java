package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import lombok.Getter;
import lombok.Setter;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.RewardType;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import me.i2000c.newalb.utils2.Offset;

@Getter
@Setter
public class TeleportReward extends Reward{
    public static final String PLAYER_WORLD_PATTERN = "%world%";
    
    private TeleportSource teleportSource;
    private String worldName;
    private Offset offset;    
    
    public static enum TeleportSource{
        RELATIVE_TO_PLAYER,
        RELATIVE_TO_LUCKY_BLOCK,
        ABSOLUTE;
        
        private static final TeleportSource[] vals = values();
        public TeleportSource next(){
            return vals[(this.ordinal()+1) % vals.length];
        }
    }
    
    public TeleportReward(Outcome outcome){
        super(outcome);
        teleportSource = TeleportSource.RELATIVE_TO_PLAYER;
        worldName = PLAYER_WORLD_PATTERN;
        offset = new Offset();        
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        return ItemStackWrapper.newItem(XMaterial.COMPASS)
                .setDisplayName("&eTeleport")
                .addLoreLine("&bSource: &3" + teleportSource.name())
                .addLoreLine("&bWorld name: &3" + worldName)
                .addLoreLine("&dOffset:")
                .addLoreLine("   &5X: &3" + offset.getOffsetX())
                .addLoreLine("   &5Y: &3" + offset.getOffsetY())
                .addLoreLine("   &5Z: &3" + offset.getOffsetZ())
                .toItemStack();
    }
    
    @Override
    public void saveRewardIntoConfig(Config config, String path){
        config.set(path + ".source", teleportSource.name());
        config.set(path + ".worldName", worldName);
        offset.saveToConfig(config, path + ".offset");        
    }
    
    @Override
    public void loadRewardFromConfig(Config config, String path){
        this.teleportSource = config.getEnum(path + ".source", TeleportSource.class);
        this.worldName = config.getString(path + ".worldName");
        this.offset = new Offset(config, path + ".offset");        
    }
    
    @Override
    public void execute(Player player, Location location){
        Location playerLocation = player.getLocation();
        float pitch = playerLocation.getPitch();
        float yaw = playerLocation.getYaw();
        
        Location teleportLocation;
        switch(teleportSource){
            case RELATIVE_TO_PLAYER:
                teleportLocation = offset.applyToLocation(playerLocation);
                break;
            case RELATIVE_TO_LUCKY_BLOCK:
                teleportLocation = offset.applyToLocation(location.clone());
                break;
            default:
                World world;
                if(worldName.equals(PLAYER_WORLD_PATTERN)){
                    world = player.getWorld();
                }else{
                    world = Bukkit.getWorld(worldName);
                    if(world == null){
                        Logger.warn("Cannot execute teleport reward");
                        Logger.warn("World " + worldName + " doesn't exist");
                        return;
                    }
                }
                teleportLocation = offset.applyToLocation(new Location(world, 0, 0, 0));
        }
        
        teleportLocation.setPitch(pitch);
        teleportLocation.setYaw(yaw);
        player.teleport(teleportLocation);
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.teleport;
    }
    
    @Override
    public Reward clone(){
        TeleportReward copy = (TeleportReward) super.clone();
        copy.offset = this.offset.clone();
        return copy;
    }
}
