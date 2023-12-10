package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.RewardType;
import me.i2000c.newalb.listeners.interact.SpecialItems;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MiniVolcanoReward extends Reward{
    private int height;
    private ItemBuilder baseMaterial;
    private ItemBuilder lavaMaterial;
    private long ticks;
    private boolean squared;    
    private boolean throwBlocks;
    
    public MiniVolcanoReward(Outcome outcome){
        super(outcome);
        height = ConfigManager.getConfig().getInt("Objects.MiniVolcano.height");
        baseMaterial = ItemBuilder.newItem(ConfigManager.getConfig().getString("Objects.MiniVolcano.base-material"));
        lavaMaterial = ItemBuilder.newItem(ConfigManager.getConfig().getString("Objects.MiniVolcano.lava-material"));
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
    
    public ItemStack getBaseMaterial(){
        return baseMaterial.build();
    }
    public void setBaseMaterial(ItemStack baseMaterial){
        this.baseMaterial = ItemBuilder.fromItem(baseMaterial);
    }
    
    public XMaterial getLavaMaterial(){
        return lavaMaterial.getXMaterial();
    }
    public void setLavaMaterial(XMaterial lavaMaterial){
        this.lavaMaterial = ItemBuilder.newItem(lavaMaterial);
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
        return ItemBuilder.newItem(XMaterial.LAVA_BUCKET)
                .withDisplayName("&cMiniVolcano")
                .addLoreLine("&bHeight: &6" + this.height)
                .addLoreLine("&bBase material: &6" + this.baseMaterial)
                .addLoreLine("&bLava material: &6" + this.lavaMaterial)
                .addLoreLine("&bTicks between blocks: &6" + this.ticks)
                .addLoreLine("&bIs squared: &6" + this.squared)
                .addLoreLine("&bThrow blocks: &6" + this.throwBlocks)
                .build();
    }
    
    @Override
    public void saveRewardIntoConfig(FileConfiguration config, String path){
        config.set(path + ".height", height);
        config.set(path + ".baseMaterial", baseMaterial.toString());
        config.set(path + ".lavaMaterial", lavaMaterial.toString());
        config.set(path + ".ticks_between_blocks", ticks);
        config.set(path + ".squared", squared);
        config.set(path + ".throw_blocks", throwBlocks);
    }
    
    @Override
    public void loadRewardFromConfig(FileConfiguration config, String path){
        height = config.getInt(path + ".height");
        baseMaterial = ItemBuilder.newItem(config.getString(path + ".baseMaterial"));
        lavaMaterial = ItemBuilder.newItem(config.getString(path + ".lavaMaterial"));
        ticks = config.getLong(path + ".ticks_between_blocks");
        squared = config.getBoolean(path + ".squared");
        throwBlocks = config.getBoolean(path + ".throw_blocks");
    }

    @Override
    public void execute(Player player, Location location){
        SpecialItems.mini_volcano.execute(location, height, baseMaterial, lavaMaterial, ticks, 0L, squared, throwBlocks);
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.mini_volcano;
    }
    
    @Override
    public Reward clone(){
        MiniVolcanoReward copy = (MiniVolcanoReward) super.clone();
        return copy;
    }
}
