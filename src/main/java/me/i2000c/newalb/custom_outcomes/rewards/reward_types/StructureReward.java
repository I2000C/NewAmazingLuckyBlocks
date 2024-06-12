package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import com.cryptomorin.xseries.XMaterial;
import java.io.File;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.RewardType;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import me.i2000c.newalb.utils2.Schematic;
import me.i2000c.newalb.utils2.Task;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

@Getter
@Setter
public class StructureReward extends Reward{
    public static final File schematicsFolder = new File(NewAmazingLuckyBlocks.getInstance().getDataFolder(), "schematics");
    
    private String schematicName;
    private boolean fromPlayer;
    private boolean replaceBlocks;
    private boolean placeAirBlocks;
    private boolean centerPlayerLocation;
    private boolean centerPlayerYaw;
    private boolean centerPlayerPitch;
    private boolean autorotate;
    private Axis schematicAxis;
    
    public StructureReward(Outcome outcome){
        super(outcome);
        this.schematicName = null;
        this.fromPlayer = true;
        this.replaceBlocks = false;
        this.placeAirBlocks = true;
        this.centerPlayerLocation = true;
        this.centerPlayerYaw = false;
        this.centerPlayerPitch = false;
        this.autorotate = false;
        this.schematicAxis = Axis.POSITIVE_Z;
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        ItemStackWrapper builder = ItemStackWrapper.newItem(XMaterial.BRICKS);
        builder.setDisplayName("&6Structure Reward");
        builder.addLoreLine("&3Schematic name: &b" + this.schematicName);
        if(this.fromPlayer){
            builder.addLoreLine("&3Source location: &2Player");
        }else{
            builder.addLoreLine("&3Source location: &eLuckyBlock");
        }
        if(this.replaceBlocks){
            builder.addLoreLine("&3Replace existing blocks: &atrue");
        }else{
            builder.addLoreLine("&3Replace existing blocks: &7false");
        }
        if(this.placeAirBlocks){
            builder.addLoreLine("&3Place air blocks: &atrue");
        }else{
            builder.addLoreLine("&3Place air blocks: &7false");
        }
        if(this.centerPlayerLocation) {
            builder.addLoreLine("&3Center player location: &atrue");
        } else {
            builder.addLoreLine("&3Center player location: &7false");
        }
        if(this.centerPlayerYaw) {
            builder.addLoreLine("&3Center player yaw: &atrue");
        } else {
            builder.addLoreLine("&3Center player yaw: &7false");
        }
        if(this.centerPlayerPitch) {
            builder.addLoreLine("&3Center player pitch: &atrue");
        } else {
            builder.addLoreLine("&3Center player pitch: &7false");
        }
        if(this.autorotate) {
            builder.addLoreLine("&3Auto-rotate schematic: &atrue");
        } else {
            builder.addLoreLine("&3Auto-rotate schematic: &7false");
        }
        builder.addLoreLine("&3Schematic axis: &5" + this.schematicAxis);
        
        return builder.toItemStack();
    }
    
    @Override
    public void saveRewardIntoConfig(Config config, String path){
        config.set(path + ".filename", this.schematicName);
        config.set(path + ".fromPlayer", this.fromPlayer);
        config.set(path + ".replaceBlocks", this.replaceBlocks);
        config.set(path + ".placeAirBlocks", this.placeAirBlocks);
        config.set(path + ".centerPlayerLocation", this.centerPlayerLocation);
        config.set(path + ".centerPlayerYaw", this.centerPlayerYaw);
        config.set(path + ".centerPlayerPitch", this.centerPlayerPitch);
        config.set(path + ".autorotate", this.autorotate);
        config.set(path + ".schematicAxis", this.schematicAxis);
    }
    
    @Override
    public void loadRewardFromConfig(Config config, String path){
        this.schematicName = config.getString(path + ".filename");
        this.fromPlayer = config.getBoolean(path + ".fromPlayer");
        this.replaceBlocks = config.getBoolean(path + ".replaceBlocks");
        this.placeAirBlocks = config.getBoolean(path + ".placeAirBlocks");
        this.centerPlayerLocation = config.getBoolean(path + ".centerPlayerLocation", fromPlayer);
        this.centerPlayerYaw = config.getBoolean(path + ".centerPlayerYaw", false);
        this.centerPlayerPitch = config.getBoolean(path + ".centerPlayerPitch", false);
        this.autorotate = config.getBoolean(path + ".autorotate", false);
        this.schematicAxis = config.getEnum(path + ".schematicAxis", Axis.class, Axis.POSITIVE_Z);
    }
    
    private Schematic schematic = null;
    private long lastModified = 0L;
    
    @Override
    public void execute(Player player, Location location){
        if(NewAmazingLuckyBlocks.getWorldEditPlugin() == null){
            Logger.warn("You cannot execute Structure Rewards unless you have installed WorldEdit");
            return;
        }
        
        File schematicFile = new File(schematicsFolder, this.schematicName);
        if(!schematicFile.exists()){
            Logger.err("Error: file \"" + this.schematicName + "\" doesn't exist");
            return;
        }
        try{
            if(centerPlayerYaw) {
                Location loc = player.getLocation();
                loc.setYaw(0);
                player.teleport(loc);
            }
            if(centerPlayerPitch) {
                Location loc = player.getLocation();
                loc.setPitch(0);
                player.teleport(loc);
            }
            if(centerPlayerLocation) {
                Location loc = player.getLocation();
                Location blockLoc = loc.getBlock().getLocation();
                blockLoc.setYaw(loc.getYaw());
                blockLoc.setPitch(loc.getPitch());
                player.teleport(blockLoc.add(0.5, 0, 0.5));
            }
            
            final Location targetLocation = fromPlayer ? player.getLocation() : location;
            
            if(schematic == null || schematicFile.lastModified() != lastModified){
                lastModified = schematicFile.lastModified();
                schematic = new Schematic();
                schematic.loadFromFile(schematicFile, targetLocation.getWorld());
            }
            
            Task.runTask(() -> {
                try{
                    int rotation = 0;
                    if(autorotate) {
                        Axis playerAxis = Axis.fromDirection(player.getLocation().getDirection());
                        rotation = playerAxis != null ? schematicAxis.getAngleDistanceTo(playerAxis) : 0;
                    }
                    schematic.pasteAt(player, targetLocation, replaceBlocks, placeAirBlocks, rotation);
                }catch(Exception ex){
                    Logger.err("An error occurred while executing structure reward " + this.schematicName + ":");
                    ex.printStackTrace();
                }                
            }, 1L);            
        }catch(Exception ex){
            Logger.err("An error occurred while executing structure reward " + this.schematicName + ":");
            ex.printStackTrace();
        }
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.structure;
    }
    
    @Override
    public Reward clone(){
        StructureReward copy = (StructureReward) super.clone();
        return copy;
    }
    
    @Getter
    @RequiredArgsConstructor
    public static enum Axis {
        POSITIVE_Z(0),
        NEGATIVE_X(90),
        NEGATIVE_Z(180),        
        POSITIVE_X(270);
        
        private final int rotation;
        
        public Axis next() {
            return VALUES[(this.ordinal() + 1) % VALUES.length];
        }
        
        public int getAngleDistanceTo(@NonNull Axis axis) {
            return axis.rotation - this.rotation;
        }
        
        public static Axis fromDirection(@NonNull Vector direction) {
            double x = direction.getX();
            double z = direction.getZ();
            if(x == 0 && z == 0) {
                return null;
            } else {
                if(Math.abs(x) > Math.abs(z)) {
                    return x > 0 ? POSITIVE_X : NEGATIVE_X;
                } else {
                    return z > 0 ? POSITIVE_Z : NEGATIVE_Z;
                }
            }
        }
        
        private static final Axis[] VALUES = values();
    }
}