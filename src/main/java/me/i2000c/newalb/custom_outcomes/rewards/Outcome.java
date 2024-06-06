package me.i2000c.newalb.custom_outcomes.rewards;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.EntityReward;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.EntityTowerReward;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import me.i2000c.newalb.utils2.Task;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
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
        Material material = icon.getType();
        if(material.name().contains("POTION")){
            material = Material.POTION;
        }
        
        this.icon = new ItemStack(material);
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()
                && material != Material.POTION){
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
    
    public static Outcome fromString(String string) throws Exception{
        String[] splitted = string.split("\\/");
        String packName = splitted[0];
        int outcomeID = Integer.parseInt(splitted[1]);
        OutcomePack pack = PackManager.getPack(packName);
        Outcome outcome = pack.getOutcome(outcomeID);
        return outcome;
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
                int entityID = entityReward.getEntityID();
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
                        .filter(r -> r.getEntityID() > entityID)
                        .forEach(r -> r.setEntityID(r.getEntityID() - 1));
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
        //<editor-fold defaultstate="collapsed" desc="Code">
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
                    int entityID = ((EntityReward) reward).getEntityID();
                    if(entitiesInTowerRewards.contains(entityID)){
                        continue;
                    }
                }
                
                int delay = reward.getDelay();
                if(delay <= 0){
                    reward.execute(player, location);
                }else{
                    Task.runTask(() -> {
                        try {
                            reward.execute(player, location);
                        } catch(Throwable ex) {
                            Logger.err("An error occurred while executing outcome: " + this.ID + " of pack " + this.pack.getPackname());
                            ex.printStackTrace();
                        }
                    }, delay);
                }
            }
        }catch(Exception ex){
            Logger.err("An error occurred while executing outcome: " + this.ID + " of pack " + this.pack.getPackname());
            ex.printStackTrace();
        }
//</editor-fold>
    }
    
    public Outcome(Config config, String path, int ID, OutcomePack pack){
        //<editor-fold defaultstate="collapsed" desc="Code">
        this.pack = pack;
        this.ID = ID;
        this.name = config.getString(path + ".outcome-name");
        this.probability = config.getInt(path + ".probability");
        String itemName = config.getString(path + ".icon", getDefaultIcon().getType().name());
        this.icon = ItemStackWrapper.newItem(itemName).toItemStack();
        this.rewardList = new ArrayList<>();
        
        ConfigurationSection section = config.getConfigurationSection(path + ".rewards", null);
        if(section != null){
            for(String mainKey : section.getKeys(false)){
                ConfigurationSection subsection = config.getConfigurationSection(path + ".rewards." + mainKey);
                for(String key : subsection.getKeys(false)){
                    String fullPath = path + ".rewards." + mainKey + "." + key;
                    try{
                        RewardType rewardType = RewardType.valueOf(mainKey);
                        
                        @SuppressWarnings("LeakingThisInConstructor")
                        Reward reward = rewardType.createReward(this);
                        reward.loadRewardFromConfig(config, fullPath);
                        if(reward instanceof EntityReward){
                            int entityID = Integer.parseInt(key);
                            ((EntityReward) reward).setEntityID(entityID);
                        }
                        this.rewardList.add(reward);
                    }catch(Throwable ex){
                        Logger.err("There has been an error while loading outcome: " + fullPath + " in pack " + pack.getPackname());
                        ex.printStackTrace();
                    }
                }
            }
            
            List<String> delays = config.getStringList(path + ".delayer", null);
            if(delays != null){
                for(String str : delays){
                    int rewardID = Integer.parseInt(str.split(";")[0]);
                    int delay = Integer.parseInt(str.split(";")[1]);
                    this.rewardList.get(rewardID).setDelay(delay);
                }
            }
        }else{
            Logger.warn("Outcome " + this.name + " doesn't have any rewards");
        }
//</editor-fold>
    }
    
    public void saveOutcome(){
        pack.addOutcome(this, false);
    }
    protected void saveOutcome(Config config, String path){
        //<editor-fold defaultstate="collapsed" desc="Code">
        config.set(path, null);
        
        config.set(path + ".outcome-name", this.name);
        config.set(path + ".probability", this.probability);
        config.set(path + ".icon", ItemStackWrapper.fromItem(this.icon, false).getMaterial());
        
        List<String> delays = new ArrayList<>();
        int i=0;
        for(Reward reward : this.rewardList){
            int rewardID = 0;
            ConfigurationSection section = config.getConfigurationSection(path + ".rewards." + reward.getRewardType().name(), null);
            if(section != null) {
                rewardID = section.getKeys(false).size();
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
        return ItemStackWrapper.fromItem(icon)
                               .setDisplayName("&3Outcome " + ID)
                               .addLoreLine("&aName: &d" + this.name)
                               .addLoreLine("&6Probability: &b" + this.probability)
                               .toItemStack();
    }
    
    @Override
    @SuppressWarnings("CloneDeclaresCloneNotSupported")
    public Outcome clone(){
        try{
            Outcome copy = (Outcome) super.clone();
            copy.rewardList = new ArrayList<>();            
            this.rewardList.forEach(reward -> {
                Reward rewardCopy = reward.clone();
                rewardCopy.setOutcome(copy);
                copy.rewardList.add(rewardCopy);
            });
            return copy;
        }catch(CloneNotSupportedException ex){
            return null;
        }
    }
    
    @Override
    public String toString(){
        return pack.getPackname() + "/" + ID;
    }
}
