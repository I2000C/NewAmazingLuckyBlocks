package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.List;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.RewardType;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EntityTowerReward extends Reward{
    public static final int PLAYER_ENTITY_ID = -1;
    public static final int INVALID_ENTITY_ID = -2;
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
    
    public static ItemStack getPlayerItem(){
        return ItemBuilder.newItem(XMaterial.PLAYER_HEAD)
                .withDisplayName("&2Player")
                .build();
    }
    public static EntityReward getPlayerEntityReward(){
        EntityReward playerEntityReward = new EntityReward(null){
            @Override
            public ItemStack getItemToDisplay(){
                return getPlayerItem();
            }
        };
        playerEntityReward.setID(PLAYER_ENTITY_ID);
        playerEntityReward.setType(EntityType.PLAYER);
        
        return playerEntityReward;
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.ARMOR_STAND);
        builder.withDisplayName("&eEntityTower");
        
        StringBuilder stringBuilder = new StringBuilder();
        if(!entityList.isEmpty()){        
            this.entityList.forEach(entityID -> {                
                if(entityID == PLAYER_ENTITY_ID){
                    stringBuilder.append("&2Player&r");
                }else{
                    stringBuilder.append(entityID);
                }
                stringBuilder.append(", ");
            });
            
            stringBuilder.setLength(stringBuilder.length() - 2);
        }
        
        builder.addLoreLine("&3Entities: &r" + stringBuilder.toString());
        
        return builder.build();
    }

    @Override
    public void saveRewardIntoConfig(FileConfiguration config, String path){
        StringBuilder stringBuilder = new StringBuilder();
        this.entityList.forEach(entityID -> {
            stringBuilder.append(entityID).append(",");
        });
        if(stringBuilder.length() > 0){
            stringBuilder.setLength(stringBuilder.length() - 1);
        }
        
        config.set(path, stringBuilder.toString());
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
        List<EntityReward> entityRewardList = this.getOutcome().getEntityRewards();
        List<Entity> entities = new ArrayList<>(this.entityList.size());
        for(int entityID : this.entityList){
            if(entityID == PLAYER_ENTITY_ID){
                entities.add(player);
            }else{
                EntityReward entityReward = entityRewardList.get(entityID);
                entityReward.execute(player, location);
                entities.add(entityReward.lastSpawnedEntity);
            }
        }
        
        for(int i=0;i<this.entityList.size()-1;i++){
            Entity entity1 = entities.get(i);
            Entity entity2 = entities.get(i+1);
            
            entity1.setPassenger(entity2);
        }
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.tower_entity;
    }
    
    @Override
    public Reward clone(){
        EntityTowerReward copy = (EntityTowerReward) super.clone();
        copy.entityList = new ArrayList<>(this.entityList);
        return copy;
    }
}
