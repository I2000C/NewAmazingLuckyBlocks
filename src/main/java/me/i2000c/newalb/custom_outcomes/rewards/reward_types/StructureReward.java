package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import com.cryptomorin.xseries.XMaterial;
import java.io.File;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.RewardType;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.Schematic;
import me.i2000c.newalb.utils2.Task;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StructureReward extends Reward{
    public static final File schematicsFolder = new File(NewAmazingLuckyBlocks.getInstance().getDataFolder(), "schematics");
    
    private String filename;
    private boolean fromPlayer;
    private boolean replaceBlocks;
    private boolean placeAirBlocks;
    
    public StructureReward(Outcome outcome){
        super(outcome);
        this.filename = null;
        this.fromPlayer = true;
        this.replaceBlocks = false;
        this.placeAirBlocks = true;
    }
    
    public String getSchematicName(){
        return this.filename;
    }
    public void setSchematicName(String filename){
        this.filename = filename;
    }
    public boolean isFromPlayer(){
        return this.fromPlayer;
    }
    public void setFromPlayer(boolean fromPlayer){
        this.fromPlayer = fromPlayer;
    }
    public boolean isReplaceBlocks(){
        return this.replaceBlocks;
    }
    public void setReplaceBlocks(boolean replaceBlocks){
        this.replaceBlocks = replaceBlocks;
    }
    public boolean isPlaceAirBlocks(){
        return this.placeAirBlocks;
    }
    public void setPlaceAirBlocks(boolean placeAirBlocks){
        this.placeAirBlocks = placeAirBlocks;
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.BRICKS);
        builder.withDisplayName("&6Structure Reward");
        builder.addLoreLine("&3Schematic name: &b" + this.filename);
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
        
        return builder.build();
    }
    
    @Override
    public void saveRewardIntoConfig(FileConfiguration config, String path){
        config.set(path + ".filename", this.filename);
        config.set(path + ".fromPlayer", this.fromPlayer);
        config.set(path + ".replaceBlocks", this.replaceBlocks);
        config.set(path + ".placeAirBlocks", this.placeAirBlocks);
    }
    
    @Override
    public void loadRewardFromConfig(FileConfiguration config, String path){
        this.filename = config.getString(path + ".filename");
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
        
        File schematicFile = new File(schematicsFolder, this.filename);
        if(!schematicFile.exists()){
            Logger.err("Error: file \"" + this.filename + "\" doesn't exist");
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
                    Logger.err("An error occurred while executing structure reward " + this.filename + ":");
                    ex.printStackTrace();
                }                
            }, 1L);            
        }catch(Exception ex){
            Logger.err("An error occurred while executing structure reward " + this.filename + ":");
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