package me.i2000c.newalb.custom_outcomes.utils;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.utils.rewards.BlockReplacingSphereReward;
import me.i2000c.newalb.custom_outcomes.utils.rewards.BlockReward;
import me.i2000c.newalb.custom_outcomes.utils.rewards.CommandReward;
import me.i2000c.newalb.custom_outcomes.utils.rewards.DarkHoleReward;
import me.i2000c.newalb.custom_outcomes.utils.rewards.EffectReward;
import me.i2000c.newalb.custom_outcomes.utils.rewards.EntityReward;
import me.i2000c.newalb.custom_outcomes.utils.rewards.EntityTowerReward;
import me.i2000c.newalb.custom_outcomes.utils.rewards.ExplosionReward;
import me.i2000c.newalb.custom_outcomes.utils.rewards.FireworkReward;
import me.i2000c.newalb.custom_outcomes.utils.rewards.ItemReward;
import me.i2000c.newalb.custom_outcomes.utils.rewards.LightningReward;
import me.i2000c.newalb.custom_outcomes.utils.rewards.MessageReward;
import me.i2000c.newalb.custom_outcomes.utils.rewards.MiniVolcanoReward;
import me.i2000c.newalb.custom_outcomes.utils.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.utils.rewards.SoundReward;
import me.i2000c.newalb.custom_outcomes.utils.rewards.StructureReward;
import me.i2000c.newalb.custom_outcomes.utils.rewards.TrapReward;
import me.i2000c.newalb.utils.logger.LogLevel;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.OtherUtils;
import me.i2000c.newalb.utils2.Task;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Outcome implements Displayable, Executable, Cloneable{
    private static final Comparator<Reward> REWARD_COMPARATOR = (Reward r1, Reward r2) -> r1.getRewardType().compareTo(r2.getRewardType());
    
    private final OutcomePack pack;
    
    private String name;
    private int probability;
    private int ID;
    private List<Reward> rewardList;
    private ItemStack icon;
    
    public ItemStack getIcon(){
        return this.icon;
    }
    public void setIcon(ItemStack icon){
        this.icon = new ItemStack(icon.getType());
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
            this.icon.setDurability(icon.getDurability());
        }
    }
    public static ItemStack getDefaultIcon(){
        return new ItemStack(Material.CHEST);
    }
    
    public Outcome(String name, int probability, int ID, OutcomePack pack){
        this.pack = pack;
        if(ID == -1){
            ID = pack.getOutcomes().size();
        }
        this.ID = ID;
        this.name = name;
        this.probability = probability;
        this.icon = getDefaultIcon();
        
        this.rewardList = new ArrayList<>();
    }
    
    public static Outcome fromString(String string){
        try{
            String[] splitted = string.split("\\/");
            String packName = splitted[0];
            int outcomeID = Integer.parseInt(splitted[1]);
            OutcomePack pack = PackManager.getPack(packName);
            Outcome outcome = pack.getOutcome(outcomeID);
            return outcome;
        }catch(Exception ex){
            return null;
        }
    }
    
    public OutcomePack getPack(){
        return this.pack;
    }
    
    public void setProbability(int probability){
        this.probability = probability;
    }
    public int getProbability(){
        return this.probability;
    }
    
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    
    public int getID(){
        return this.ID;
    }
    protected void setID(int ID){
        this.ID = ID;
    }
    
    public List<Reward> getRewards(){
        return this.rewardList;
    }
    public Reward getReward(int i){
        return this.rewardList.get(i);
    }
    public boolean addReward(Reward r){
        if(this.rewardList.contains(r)){
            this.rewardList.set(this.rewardList.indexOf(r), r);
            return true;
        }else{
            this.rewardList.add(r);
            return true;
        }
    }
    public boolean setReward(int i, Reward reward){
        if(i >= 0 && i < this.rewardList.size()){
            this.rewardList.set(i, reward);
            return true;
        }else{
            return false;
        }
    }
    public boolean removeReward(int i){
        if(i >= 0 && i < this.rewardList.size()){
            Reward reward = this.rewardList.get(i);
            if(reward instanceof EntityReward){
                EntityReward entityReward = (EntityReward) reward;
                int entityID = entityReward.getID();
                for(Reward r : this.rewardList){
                    if(r instanceof EntityTowerReward){
                        EntityTowerReward entityTowerReward = (EntityTowerReward) r;
                        List<Integer> entityList = entityTowerReward.getEntityList();
                        if(entityList.contains(entityID)){
                            return false;
                        }
                    }
                }
                
                getEntityRewards().stream()
                        .filter(r -> r.getID() > entityID)
                        .forEach(r -> r.setID(r.getID() - 1));
            }
            
            this.rewardList.remove(i);
            return true;
        }else{
            return false;
        }
    }
    public int getNumberOfRewards(){
        return this.rewardList.size();
    }
    public void sortRewards(){        
        this.rewardList.sort(REWARD_COMPARATOR);
    }
    
    public int getEntityRewardsNumber(){
        return (int) this.rewardList
                .stream()
                .filter(reward -> reward instanceof EntityReward)
                .count();
    }
    public List<EntityReward> getEntityRewards(){
        return this.rewardList
                .stream()
                .filter(reward -> reward instanceof EntityReward)
                .map(reward -> (EntityReward) reward)
                .collect(Collectors.toList());
    }
    public EntityReward getEntityReward(int entityRewardID){
        return getEntityRewards().get(entityRewardID);
    }
    
    @Override
    public void execute(Player player, Location location){
        try{
            List<Integer> entitiesInTowerRewards = new ArrayList<>();
            this.rewardList.stream()
                    .filter(reward -> reward instanceof EntityTowerReward)
                    .forEach(reward -> {
                        List<Integer> entityList = ((EntityTowerReward)reward).getEntityList();
                        entitiesInTowerRewards.addAll(entityList);
                    });
            
            for(Reward reward : this.rewardList){
                if(reward instanceof EntityReward){
                    int entityID = ((EntityReward) reward).getID();
                    if(entitiesInTowerRewards.contains(entityID)){
                        continue;
                    }
                }
                
                int delay = reward.getDelay();
                if(delay <= 0){
                    reward.execute(player, location);
                }else{
                    Task.runTask(() -> reward.execute(player, location), delay);
                }
            }
        }catch(Exception ex){
            Logger.log("An error occurred while excuting outcome " + this.ID, LogLevel.ERROR);
            ex.printStackTrace();
        }
    }
    
    public Outcome(FileConfiguration config, String path, int ID, OutcomePack pack){
        //<editor-fold defaultstate="collapsed" desc="Code">
        this.pack = pack;
        this.ID = ID;
        this.name = config.getString(path + ".outcome-name");
        this.probability = config.getInt(path + ".probability");
        this.icon = OtherUtils.parseMaterial(config.getString(path + ".icon", getDefaultIcon().getType().name()));
        this.rewardList = new ArrayList<>();
        
        if(config.isConfigurationSection(path + ".rewards")){
            for(String mainKey : config.getConfigurationSection(path + ".rewards").getKeys(false)){
                for(String key : config.getConfigurationSection(path + ".rewards." + mainKey).getKeys(false)){
                    String fullPath = path + ".rewards." + mainKey + "." + key;
                    try{
                        Reward r;
                        Reward.RewardType rt = Reward.RewardType.valueOf(mainKey);
                        switch(rt){
                            case item:
                                r = new ItemReward(this);
                                r.loadRewardFromConfig(config, fullPath);
                                this.rewardList.add(r);
                                break;
                            case command:
                                r = new CommandReward(this);
                                r.loadRewardFromConfig(config, fullPath);
                                this.rewardList.add(r);
                                break;
                            case entity:
                                r = new EntityReward(this, Integer.parseInt(key));
                                r.loadRewardFromConfig(config, fullPath);
                                this.rewardList.add(r);
                                break;
                            case tower_entity:
                                r = new EntityTowerReward(this);
                                r.loadRewardFromConfig(config, fullPath);
                                this.rewardList.add(r);
                                break;
                            case firework:
                                r = new FireworkReward(this);
                                r.loadRewardFromConfig(config, fullPath);
                                this.rewardList.add(r);
                                break;
                            case sound:
                                r = new SoundReward(this);
                                r.loadRewardFromConfig(config, fullPath);
                                this.rewardList.add(r);
                                break;
                            case structure:
                                r = new StructureReward(this);
                                r.loadRewardFromConfig(config, fullPath);
                                this.rewardList.add(r);
                                break;
                            case block:
                                r = new BlockReward(this);
                                r.loadRewardFromConfig(config, fullPath);
                                this.rewardList.add(r);
                                break;
                            case lightning:
                                r = new LightningReward(this);
                                r.loadRewardFromConfig(config, fullPath);
                                this.rewardList.add(r);
                                break;
                            case dark_hole:
                                r = new DarkHoleReward(this);
                                r.loadRewardFromConfig(config, fullPath);
                                this.rewardList.add(r);
                                break;
                            case mini_volcano:
                                r = new MiniVolcanoReward(this);
                                r.loadRewardFromConfig(config, fullPath);
                                this.rewardList.add(r);
                                break;
                            case message:
                                r = new MessageReward(this);
                                r.loadRewardFromConfig(config, fullPath);
                                this.rewardList.add(r);
                                break;
                            case effect:
                                r = new EffectReward(this);
                                r.loadRewardFromConfig(config, fullPath);
                                this.rewardList.add(r);
                                break;
                            case explosion:
                                r = new ExplosionReward(this);
                                r.loadRewardFromConfig(config, fullPath);
                                this.rewardList.add(r);
                                break;
                            case block_replacing_sphere:
                                r = new BlockReplacingSphereReward(this);
                                r.loadRewardFromConfig(config, fullPath);
                                this.rewardList.add(r);
                                break;
                            case trap:
                                r = new TrapReward(this);
                                r.loadRewardFromConfig(config, fullPath);
                                this.rewardList.add(r);
                                break;
                        }
                    }catch(Exception ex){
                        Logger.log("There has been an error while loading outcome: " + fullPath + " in pack " + pack.getFilename(), LogLevel.ERROR);
                        ex.printStackTrace();
                    }
                }
            }
            
            if(config.isList(path + ".delayer")){
                List<String> delays = config.getStringList(path + ".delayer");
                for(String str : delays){
                    int rewardID = Integer.parseInt(str.split(";")[0]);
                    int delay = Integer.parseInt(str.split(";")[1]);
                    this.rewardList.get(rewardID).setDelay(delay);
                }
            }
        }else{
            Logger.log("Outcome " + this.name + " doesn't have any rewards", LogLevel.INFO);
        }
//</editor-fold>
    }
    
    public void saveOutcome(){
        pack.addOutcome(this, false);
    }
    protected void saveOutcome(FileConfiguration config, String path){
        //<editor-fold defaultstate="collapsed" desc="Code">
        config.set(path, null);
        
        config.set(path + ".outcome-name", this.name);
        config.set(path + ".probability", this.probability);
        config.set(path + ".icon", OtherUtils.parseItemStack(this.icon));
        
        List<String> delays = new ArrayList();
        int i=0;
        for(Reward reward : this.rewardList){
            int rewardID = 0;
            if(config.isConfigurationSection(path + ".rewards." + reward.getRewardType().name())){
                rewardID = config.getConfigurationSection(path + ".rewards." + reward.getRewardType().name()).getKeys(false).size();
            }
            reward.saveRewardIntoConfig(config, path + ".rewards." + reward.getRewardType().name() + "." + rewardID);
            
            if(reward.getDelay() > 0){
                delays.add(i + ";" + reward.getDelay());
            }
            i++;
        }
        
        config.set(path + ".delayer", delays);
//</editor-fold>
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        return ItemBuilder.newItem(XMaterial.matchXMaterial(icon))
                .withDisplayName("&3Outcome " + ID)
                .addLoreLine("&aName: &d" + this.name)
                .addLoreLine("&6Probability: &b" + this.probability)
                .build();
    }
    
    @Override
    @SuppressWarnings("CloneDeclaresCloneNotSupported")
    public Outcome clone(){
        try{
            Outcome copy = (Outcome) super.clone();
            copy.rewardList = new ArrayList<>();            
            this.rewardList.forEach(reward -> {
                copy.rewardList.add(reward.clone());
            });
            return copy;
        }catch(CloneNotSupportedException ex){
            return null;
        }
    }
    
    @Override
    public String toString(){
        return pack.getFilename() + "/" + ID;
    }
}
