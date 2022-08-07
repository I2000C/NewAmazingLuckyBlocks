package me.i2000c.newalb.custom_outcomes.utils.rewards;

import me.i2000c.newalb.custom_outcomes.menus.MiniVolcanoMenu;
import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils.Timer;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MiniVolcanoReward extends Reward{
    private int height;
    private Material baseMaterial;
    private Material lavaMaterial;
    private long ticks;
    private boolean squared;    
    private boolean throwBlocks;
    
    public MiniVolcanoReward(Outcome outcome){
        super(outcome);
        height = ConfigManager.getConfig().getInt("Objects.MiniVolcano.height");
        baseMaterial = Material.getMaterial(ConfigManager.getConfig().getString("Objects.MiniVolcano.base-material"));
        lavaMaterial = Material.getMaterial(ConfigManager.getConfig().getString("Objects.MiniVolcano.lava-material"));
        ticks = ConfigManager.getConfig().getLong("Objects.MiniVolcano.time-between-one-block-and-the-next");
        squared = false;
        throwBlocks = ConfigManager.getConfig().getBoolean("Objects.MiniVolcano.throwBlocks.enable");
    }

    public int getHeight(){
        return height;
    }
    public void setHeight(int height){
        this.height = height;
    }
    
    public Material getBaseMaterial(){
        return baseMaterial;
    }
    public void setBaseMaterial(Material baseMaterial){
        this.baseMaterial = baseMaterial;
    }
    
    public Material getLavaMaterial(){
        return lavaMaterial;
    }
    public void setLavaMaterial(Material lavaMaterial){
        this.lavaMaterial = lavaMaterial;
    }

    public long getTicks(){
        return ticks;
    }
    public void setTicks(long ticks){
        this.ticks = ticks;
    }

    public boolean isSquared(){
        return squared;
    }
    public void setSquared(boolean squared) {
        this.squared = squared;
    }

    public boolean isThrowBlocks(){
        return throwBlocks;
    }
    public void setThrowBlocks(boolean throwBlocks){
        this.throwBlocks = throwBlocks;
    }
    
    
    
    @Override
    public ItemStack getItemToDisplay(){        
        ItemStack stack = new ItemStack(Material.LAVA_BUCKET);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("&cMiniVolcano");
        List<String> loreList = new ArrayList<>();
        loreList.add("&bHeight: &6" + this.height);
        loreList.add("&bBase material: &6" + this.baseMaterial);
        loreList.add("&bLava material: &6" + this.lavaMaterial);
        loreList.add("&bTicks between blocks: &6" + this.ticks);
        loreList.add("&bIs squared: &6" + this.squared);
        loreList.add("&bThrow blocks: &6" + this.throwBlocks);
        meta.setLore(loreList);
        stack.setItemMeta(meta);
        
        return stack;
    }
    
    @Override
    public void saveRewardIntoConfig(FileConfiguration config, String path){
        config.set(path + ".height", height);
        config.set(path + ".baseMaterial", baseMaterial.name());
        config.set(path + ".lavaMaterial", lavaMaterial.name());
        config.set(path + ".ticks_between_blocks", ticks);
        config.set(path + ".squared", squared);
        config.set(path + ".throw_blocks", throwBlocks);
    }
    
    @Override
    public void loadRewardFromConfig(FileConfiguration config, String path){
        height = config.getInt(path + ".height");
        baseMaterial = Material.valueOf(config.getString(path + ".baseMaterial"));
        lavaMaterial = Material.valueOf(config.getString(path + ".lavaMaterial"));
        ticks = config.getLong(path + ".ticks_between_blocks");
        squared = config.getBoolean(path + ".squared");
        throwBlocks = config.getBoolean(path + ".throw_blocks");
    }

    @Override
    public void execute(Player player, Location location){
        Timer.getTimer().executeMiniVolcano(player, location, height, baseMaterial, lavaMaterial, ticks, 0L, squared, throwBlocks);
    }

    @Override
    public void edit(Player p){
        MiniVolcanoMenu.reset();
        MiniVolcanoMenu.reward = this;
        MiniVolcanoMenu.openMiniVolcanoMenu(p);
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.mini_volcano;
    }
    
    @Override
    public Reward cloneReward(){
        MiniVolcanoReward reward = new MiniVolcanoReward(this.getOutcome());
        reward.height = this.height;
        reward.baseMaterial = this.baseMaterial;
        reward.lavaMaterial = this.lavaMaterial;
        reward.ticks = this.ticks;
        reward.squared = this.squared;
        reward.throwBlocks = this.throwBlocks;
        
        reward.setDelay(this.getDelay());
        return reward;
    }
}
