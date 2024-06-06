package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import lombok.Getter;
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

@Getter
@Setter
public class StructureReward extends Reward{
    public static final File schematicsFolder = new File(NewAmazingLuckyBlocks.getInstance().getDataFolder(), "schematics");
    
    private String schematicName;
    private boolean fromPlayer;
    private boolean replaceBlocks;
    private boolean placeAirBlocks;
    
    public StructureReward(Outcome outcome){
        super(outcome);
        this.schematicName = null;
        this.fromPlayer = true;
        this.replaceBlocks = false;
        this.placeAirBlocks = true;
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
        
        return builder.toItemStack();
    }
    
    @Override
    public void saveRewardIntoConfig(Config config, String path){
        config.set(path + ".filename", this.schematicName);
        config.set(path + ".fromPlayer", this.fromPlayer);
        config.set(path + ".replaceBlocks", this.replaceBlocks);
        config.set(path + ".placeAirBlocks", this.placeAirBlocks);
    }
    
    @Override
    public void loadRewardFromConfig(Config config, String path){
        this.schematicName = config.getString(path + ".filename");
        this.fromPlayer = config.getBoolean(path + ".fromPlayer");
        this.replaceBlocks = config.getBoolean(path + ".replaceBlocks");
        this.placeAirBlocks = config.getBoolean(path + ".placeAirBlocks");
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
            final Location targetLocation;
            if(fromPlayer){
                Location playerLocation = player.getLocation();
                Location playerBlockLocation = playerLocation.getBlock().getLocation();
                playerBlockLocation.setPitch(playerLocation.getPitch());
                playerBlockLocation.setYaw(playerLocation.getYaw());
                player.teleport(playerBlockLocation.clone().add(0.5, 0, 0.5));
                targetLocation = playerBlockLocation;
            }else{
                targetLocation = location;
            }
            if(schematic == null || schematicFile.lastModified() != lastModified){
                lastModified = schematicFile.lastModified();
                schematic = new Schematic();
                schematic.loadFromFile(schematicFile, targetLocation.getWorld());
            }
            Task.runTask(() -> {
                try{
                    schematic.pasteAt(player, targetLocation, replaceBlocks, placeAirBlocks);
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
}