package me.i2000c.newalb.custom_outcomes.utils.rewards;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.List;
import me.i2000c.newalb.custom_outcomes.menus.EntityTowerMenu;
import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EntityTowerReward extends Reward{
    public static final int PLAYER_ENTITY_ID = -1;
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
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.ARMOR_STAND);
        builder.withDisplayName("&eEntityTower");
        
        StringBuilder stringBuilder = new StringBuilder();
        if(!entityList.isEmpty()){
            stringBuilder.append(entityList.get(0));            
            for(int i=1; i<this.entityList.size(); i++){
                stringBuilder.append(", ").append(entityList.get(i));
            }
        }
        
        builder.addLoreLine("&3Entities: &r" + stringBuilder.toString());
        
        return builder.build();
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
            if(entityID == PLAYER_ENTITY_ID){
                continue;
            }
            
            EntityReward er = this.getOutcome().getEntityRewardList().get(entityID);
            er.execute(player, location);
        }
        for(int i=0;i<this.entityList.size()-1;i++){
            int entityID1 = this.entityList.get(i);
            int entityID2 = this.entityList.get(i+1);
            Entity ent1, ent2;
            
            if(entityID1 == PLAYER_ENTITY_ID){
                ent1 = player;
            }else{
                EntityReward er1 = this.getOutcome().getEntityRewardList().get(entityID1);
                ent1 = er1.lastSpawnedEntity;
            }
            
            if(entityID2 == PLAYER_ENTITY_ID){
                ent2 = player;
            }else{
                EntityReward er2 = this.getOutcome().getEntityRewardList().get(entityID2);
                ent2 = er2.lastSpawnedEntity;
            }
            
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
    public Reward clone(){
        EntityTowerReward copy = (EntityTowerReward) super.clone();
        copy.entityList = new ArrayList<>(this.entityList);
        return copy;
    }
}
