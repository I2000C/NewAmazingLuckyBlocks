package me.i2000c.newalb.custom_outcomes.utils.rewards;

import com.cryptomorin.xseries.XMaterial;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.menus.StructureMenu;
import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import me.i2000c.newalb.utils.logger.LogLevel;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils2.Schematic;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        ItemStack stack = XMaterial.BRICKS.parseItem();
        
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("&6Structure Reward");
        List<String> lore = new ArrayList();
        lore.add("&3Schematic name: &b" + this.filename);
        if(this.fromPlayer){
            lore.add("&3Source location: &2Player");
        }else{
            lore.add("&3Source location: &eLuckyBlock");
        }
        if(this.replaceBlocks){
            lore.add("&3Replace existing blocks: &atrue");
        }else{
            lore.add("&3Replace existing blocks: &7false");
        }
        if(this.placeAirBlocks){
            lore.add("&3Place air blocks: &atrue");
        }else{
            lore.add("&3Place air blocks: &7false");
        }        
        meta.setLore(lore);
        stack.setItemMeta(meta);
        
        return stack;
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
            Logger.log("You cannot execute Structure Rewards unless you have installed WorldEdit", LogLevel.INFO);
            return;
        }
        
        File schematicFile = new File(schematicsFolder, this.filename);
        if(!schematicFile.exists()){
            Logger.log("Error: file \"" + this.filename + "\" doesn't exist", LogLevel.INFO);
            return;
        }
        try{
            if(fromPlayer){
                location = player.getLocation().clone();
            }
            if(schematic == null || schematicFile.lastModified() != lastModified){
                lastModified = schematicFile.lastModified();
                schematic = new Schematic();
                schematic.loadFromFile(schematicFile, location.getWorld());
            }            
            schematic.pasteAt(location, replaceBlocks, placeAirBlocks);
        }catch(Exception ex){
            Logger.log("An error occurred while executing structure reward " + this.filename + ":", LogLevel.INFO);
            ex.printStackTrace();
        }
    }
    
    @Override
    public void edit(Player player){
        StructureMenu.reset();
        StructureMenu.reward = this;
        StructureMenu.openStructureMenu(player);
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.structure;
    }
    
    @Override
    public Reward cloneReward(){
        StructureReward reward = new StructureReward(this.getOutcome());
        reward.filename = this.filename;
        reward.fromPlayer = this.fromPlayer;
        reward.replaceBlocks = this.replaceBlocks;
        reward.placeAirBlocks = this.placeAirBlocks;
        
        reward.setDelay(this.getDelay());
        return reward;
    }
}