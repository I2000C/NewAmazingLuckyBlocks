package me.i2000c.newalb.custom_outcomes.utils.rewards;

import me.i2000c.newalb.custom_outcomes.menus.EntityTowerMenu;
import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import me.i2000c.newalb.utils.Logger;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EntityTowerReward extends Reward{
    private List<Integer> entityList;
    
    public EntityTowerReward(Outcome outcome){
        super(outcome);
        this.entityList = new ArrayList();
    }
    
    public List<Integer> getEntityList(){
        return this.entityList;
    }
    public void setEntityList(List<Integer> entityList){
        this.entityList = new ArrayList(entityList);
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        ItemStack stack = new ItemStack(Material.ARMOR_STAND);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Logger.color("&eEntityTower"));

        List<String> lore = new ArrayList();
        //lore.add(color("&bID: &r" + (entityTower_list.size()-1)));
        String data = "";
        for(int i=0;i<this.entityList.size();i++){
            data += this.entityList.get(i);
            if(i < this.entityList.size()-1){
                data += ",";
            }
        }
        lore.add(Logger.color("&3Entities: &r" + data));

        meta.setLore(lore);
        stack.setItemMeta(meta);
        
        return stack;
    }

    @Override
    public void saveRewardIntoConfig(FileConfiguration config, String path){
        String data = "";
        for(int i=0;i<this.entityList.size();i++){
            data += this.entityList.get(i);
            if(i < this.entityList.size()-1){
                data += ",";
            }
        }
        
        config.set(path, data);
    }
    
    @Override
    public void loadRewardFromConfig(FileConfiguration config, String path){
        this.entityList = new ArrayList();
        
        String[] data = config.getString(path).split(",");
        for(String id : data){
            this.entityList.add(Integer.parseInt(id));
        }
    }
    
    @Override
    public void execute(Player player, Location location){
        for(int entityID : this.entityList){
            EntityReward er = this.getOutcome().getEntityRewardList().get(entityID);
            er.execute(player, location);
        }
        for(int i=0;i<this.entityList.size()-1;i++){
            int entityID1 = this.entityList.get(i);
            int entityID2 = this.entityList.get(i+1);
            EntityReward er1 = this.getOutcome().getEntityRewardList().get(entityID1);
            EntityReward er2 = this.getOutcome().getEntityRewardList().get(entityID2);
            
            Entity ent1 = er1.lastSpawnedEntity;
            Entity ent2 = er2.lastSpawnedEntity;
            ent1.setPassenger(ent2);
        }
    }
    
    @Override
    public void edit(Player player){
        EntityTowerMenu.reset();
        EntityTowerMenu.reward = this;
        EntityTowerMenu.openEntityTowerMenu(player);
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.tower_entity;
    }
    
    @Override
    public Reward cloneReward(){
        EntityTowerReward reward = new EntityTowerReward(this.getOutcome());
        reward.setEntityList(this.entityList);
        
        reward.setDelay(this.getDelay());
        return reward;
    }
}
