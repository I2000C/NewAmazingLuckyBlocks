package me.i2000c.newalb.custom_outcomes.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
import me.i2000c.newalb.utils2.OtherUtils;
import me.i2000c.newalb.utils2.Task;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Outcome implements Displayable, Executable{
    private final OutcomePack pack;
    
    private String name;
    private int probability;
    private int ID;
    private List<Reward> rewardList;
    private ItemStack icon;
    
    private List<EntityReward> entityRewardList;
    private List<EntityTowerReward> entityTowerRewardList;
    
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
    
    public List<EntityReward> getEntityRewardList(){
        return this.entityRewardList;
    }
    public List<EntityTowerReward> getEntityTowerRewards(){
        return this.entityTowerRewardList;
    }
    
    public int getEntitiesInTowerRewards(){
        int entities = 0;
        for(EntityTowerReward etr : entityTowerRewardList){
            entities = entities + etr.getEntityList().size();
        }
        return entities;
    }
    public int getEntitiesNotInTowerRewards(){
        return this.entityRewardList.size() - getEntitiesInTowerRewards();
    }
    public List<EntityReward> getEntitiesInTowerRewardsList(){
        List<EntityReward> list = new ArrayList();
        boolean add;
        for(EntityReward er : this.entityRewardList){
            add = false;
            int entityID = er.getID();
            for(EntityTowerReward etr : entityTowerRewardList){
                if(etr.getEntityList().contains(entityID)){
                    add = true;
                    break;
                }
            }
            if(add){
                list.add(er);
            }
        }
        
        return list;
    }
    public List<EntityReward> getEntitiesNotInTowerRewardsList(){
        List<EntityReward> list = new ArrayList();
        boolean add;
        for(EntityReward er : this.entityRewardList){
            add = true;
            int entityID = er.getID();
            for(EntityTowerReward etr : entityTowerRewardList){
                if(etr.getEntityList().contains(entityID)){
                    add = false;
                    break;
                }
            }
            if(add){
                list.add(er);
            }
        }
        
        return list;
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
        
        this.rewardList = new ArrayList();
        this.entityRewardList = new ArrayList();
        this.entityTowerRewardList = new ArrayList();
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
    
    public Reward getReward(int i){
        return this.rewardList.get(i);
    }
    public boolean addReward(Reward r){
        if(this.rewardList.contains(r)){
            this.rewardList.set(this.rewardList.indexOf(r), r);
            return true;
        }else{
            this.rewardList.add(r);
            if(r instanceof EntityReward){
                this.entityRewardList.add((EntityReward) r);
            }else if(r instanceof EntityTowerReward){
                this.entityTowerRewardList.add((EntityTowerReward) r);
            }
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
            Reward r = this.rewardList.get(i);
            
            if(r instanceof EntityTowerReward){
                this.rewardList.remove(i);
                this.entityTowerRewardList.remove((EntityTowerReward) r);
                return true;
            }else if(r instanceof EntityReward){
                int entityID = ((EntityReward) r).getID();
                for(EntityTowerReward etr : this.entityTowerRewardList){
                    if(etr.getEntityList().contains(entityID)){
                        return false;
                    }
                }
                this.rewardList.remove(i);
                this.entityRewardList.remove((EntityReward) r);
                return true;
            }else{
                this.rewardList.remove(i);
                return true;
            }
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
    private static final Comparator<Reward> REWARD_COMPARATOR = (Reward r1, Reward r2) -> r1.getRewardType().compareTo(r2.getRewardType());
    
    @Override
    public void execute(Player player, Location location){
        try{
            List<EntityReward> list = this.getEntitiesInTowerRewardsList();
            int rewardID = 0;
            for(Reward r : this.rewardList){
                if(r instanceof EntityReward){
                    EntityReward er = (EntityReward) r;
                    if(list.contains(er)){
                        continue;
                    }
                }
                
                int delay = r.getDelay();
                if(delay <= 0){
                    r.execute(player, location);
                }else{
                    Task.runTask(() -> r.execute(player, location), delay);
                }
                rewardID++;
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
        this.entityRewardList = new ArrayList<>();
        this.entityTowerRewardList = new ArrayList<>();
        
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
                                this.entityRewardList.add((EntityReward) r);
                                break;
                            case tower_entity:
                                r = new EntityTowerReward(this);
                                r.loadRewardFromConfig(config, fullPath);
                                this.rewardList.add(r);
                                this.entityTowerRewardList.add((EntityTowerReward) r);
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
        ItemStack sk = this.icon.clone();
        ItemMeta meta = sk.getItemMeta();
        meta.setDisplayName("&3Outcome " + ID);
        List<String> lore = new ArrayList();
        lore.add("&aName: &d" + this.name);
        lore.add("&6Probability: &b" + this.probability);
        meta.setLore(lore);
        sk.setItemMeta(meta);
        
        return sk;
    }
    
    public Outcome cloneOutcome(){
        Outcome outcome = new Outcome(this.name, this.probability, this.ID, this.pack);
        outcome.icon = this.icon;
        
        outcome.entityRewardList = new ArrayList<>();
        outcome.entityTowerRewardList = new ArrayList<>();
        outcome.rewardList = new ArrayList<>();
        
        for(Reward reward : this.rewardList){
            Reward clone = reward.clone();
            outcome.rewardList.add(clone);
            if(reward instanceof EntityReward){
                outcome.entityRewardList.add((EntityReward) clone);
            }else if(reward instanceof EntityTowerReward){
                outcome.entityTowerRewardList.add((EntityTowerReward) clone);
            }
        }
        
        return outcome;
    }
    
    @Override
    public String toString(){
        return pack.getFilename() + "/" + ID;
    }
}
