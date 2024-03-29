package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.RewardType;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.Offset;
import me.i2000c.newalb.utils2.Task;
import me.i2000c.newalb.utils2.WorldGuardManager;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BlockReward extends Reward{
    private boolean usePlayerLoc;
    private boolean isFallingBlock;
    private ItemStack blockItem;
    private Offset offset;
    
    public BlockReward(Outcome outcome){
        super(outcome);
        usePlayerLoc = false;
        isFallingBlock = false;
        blockItem = null;
        offset = new Offset();
    }
    
    public void setUsePlayerLoc(boolean usePlayerLoc){
        this.usePlayerLoc = usePlayerLoc;
    }
    public boolean getUsePlayerLoc(){
        return this.usePlayerLoc;
    }
    
    public void setIsFallingBlock(boolean isFallingBlock){
        this.isFallingBlock = isFallingBlock;
    }
    public boolean getIsFallingBlock(){
        return this.isFallingBlock;
    }
    
    public void setItemBlock(ItemStack itemBlock){
        this.blockItem = new ItemStack(itemBlock.getType());
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()){
            this.blockItem.setData(itemBlock.getData());
        }
    }
    public ItemStack getItemBlock(){
        return blockItem;
    }
    
    public void setOffset(Offset offset){
        this.offset = offset;
    }
    public Offset getOffset(){
        return this.offset;
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        ItemBuilder builder = ItemBuilder.fromItem(blockItem);
        switch(builder.getXMaterial()){
            case WATER:
                builder.withMaterial(XMaterial.WATER_BUCKET);
                break;
            case LAVA:
                builder.withMaterial(XMaterial.LAVA_BUCKET);
                break;
            case FIRE:
                builder.withMaterial(XMaterial.FIRE_CHARGE);
                break;
        }
        
        builder.withDisplayName("&9Block");
        
        if(usePlayerLoc){
            builder.addLoreLine("&bTarget location: &2player");
        }else{
            builder.addLoreLine("&bTarget location: &6lucky block");
        }
        
        if(isFallingBlock){
            builder.addLoreLine("&6IsFallingBlock: &atrue");
        }else{
            builder.addLoreLine("&6IsFallingBlock: &7false");
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
        config.set(path + ".isFallingBlock", isFallingBlock);
        config.set(path + ".material", ItemBuilder.fromItem(blockItem, false).toString());
        offset.saveToConfig(config, path + ".offset");
    }
    
    @Override
    public void loadRewardFromConfig(FileConfiguration config, String path){
        this.usePlayerLoc = config.getBoolean(path + ".usePlayerLoc");
        this.isFallingBlock = config.getBoolean(path + ".isFallingBlock");
        String materialName = config.getString(path + ".material");
        if(materialName == null){
            // Support for old system
            materialName = config.getString(path + ".blockItem.material");
            int durability = config.getInt(path + ".blockItem.durability");
            materialName += ":" + durability;
        }
        this.blockItem = ItemBuilder.newItem(materialName).build();
        this.offset = new Offset(config, path + ".offset");
    }
    
    @Override
    public void execute(Player player, Location location){
        Location loc = usePlayerLoc ? player.getLocation().clone() : location.clone();
        offset.applyToLocation(loc);
        
        if(!WorldGuardManager.canBuild(player, location)) {
            return;
        }
        
        if(isFallingBlock){
            byte data;
            if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()){
                data = (byte) blockItem.getDurability();
            }else{
                data = 0;
            }
            FallingBlock fb = loc.getWorld().spawnFallingBlock(loc, blockItem.getType(), data);
            fb.setDropItem(false);
        }else{
            Task.runTask(() -> {
                loc.getWorld().getBlockAt(loc).setType(blockItem.getType());
                if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()){
                    loc.getWorld().getBlockAt(loc).setData(blockItem.getData().getData());
                }
            }, 1L);             
        }        
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.block;
    }

    @Override
    public Reward clone(){
        BlockReward copy = (BlockReward) super.clone();
        copy.blockItem = this.blockItem.clone();
        copy.offset = this.offset.clone();
        return copy;
    }
}
