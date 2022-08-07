package me.i2000c.newalb.custom_outcomes.utils.rewards;

import me.i2000c.newalb.utils2.Offset;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.menus.BlockMenu;
import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import me.i2000c.newalb.utils.logger.Logger;
import java.util.ArrayList;
import java.util.List;
import me.i2000c.newalb.utils2.Task;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

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
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
            this.blockItem.setData(itemBlock.getData());
        }
    }
    public ItemStack getItemBlock(){
        return blockItem;
    }
    
    public Offset getOffset(){
        return this.offset;
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        ItemStack stack = this.blockItem.clone();
        if(stack.getType() == Material.WATER){
            stack.setType(Material.WATER_BUCKET);
        }else if(stack.getType() == Material.LAVA){
            stack.setType(Material.LAVA_BUCKET);
        }
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("&9Block");
        List<String> loreList = new ArrayList<>();
        if(usePlayerLoc){
            loreList.add("&bTarget location: &2player");
        }else{
            loreList.add("&bTarget location: &6lucky block");
        }
        if(isFallingBlock){
            loreList.add("&6IsFallingBlock: &atrue");
        }else{
            loreList.add("&6IsFallingBlock: &7false");
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
        config.set(path + ".isFallingBlock", isFallingBlock);
        config.set(path + ".blockItem.material", blockItem.getType().name());
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
            config.set(path + ".blockItem.durability", blockItem.getDurability());
        }
        offset.saveToConfig(config, path + ".offset");
    }
    
    @Override
    public void loadRewardFromConfig(FileConfiguration config, String path){
        this.usePlayerLoc = config.getBoolean(path + ".usePlayerLoc");
        this.isFallingBlock = config.getBoolean(path + ".isFallingBlock");
        this.blockItem = new ItemStack(Material.valueOf(config.getString(path + ".blockItem.material")));
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
            this.blockItem.setDurability((short) config.getInt(path + ".blockItem.durability", 0));
        }
        this.offset = new Offset(config, path + ".offset");
    }
    
    @Override
    public void execute(Player player, Location location){
        Location loc = usePlayerLoc ? player.getLocation().clone() : location.clone();
        offset.addToLocation(loc);
        if(isFallingBlock){
            MaterialData md;
            if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                md = blockItem.getData();
            }else{
                md = new MaterialData(blockItem.getType());
            }
            
            FallingBlock fb;
            try{
                fb = loc.getWorld().spawnFallingBlock(loc, md);
            }catch(NoSuchMethodError ex){
                fb = loc.getWorld().spawnFallingBlock(loc, blockItem.getType(), md.getData());
            }
            
            fb.setDropItem(false);
        }else{
            Task.runTask(() -> {
                loc.getWorld().getBlockAt(loc).setType(blockItem.getType());
                if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                    loc.getWorld().getBlockAt(loc).setData(blockItem.getData().getData());
                }
            }, 1L);             
        }        
    }
    
    @Override
    public void edit(Player player){
        BlockMenu.reset();
        BlockMenu.reward = this;
        BlockMenu.openBlockMenu(player);
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.block;
    }

    @Override
    public Reward cloneReward(){
        BlockReward reward = new BlockReward(this.getOutcome());
        reward.usePlayerLoc = this.usePlayerLoc;
        reward.isFallingBlock = this.isFallingBlock;
        reward.blockItem = this.blockItem.clone();
        reward.offset = this.offset.cloneOffset();
        
        reward.setDelay(this.getDelay());
        return reward;
    }
}
