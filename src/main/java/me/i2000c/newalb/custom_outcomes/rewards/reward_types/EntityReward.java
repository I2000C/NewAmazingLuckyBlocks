package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.List;
import me.i2000c.newalb.custom_outcomes.rewards.Equipment;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.RewardType;
import me.i2000c.newalb.utils.logger.LogLevel;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils.textures.InvalidTextureException;
import me.i2000c.newalb.utils.textures.Texture;
import me.i2000c.newalb.utils.textures.TextureException;
import me.i2000c.newalb.utils.textures.URLTextureException;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.Offset;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EntityReward extends Reward{    
    private static final String[] EQUIP = {"Helmet", "Chestplate", "Leggings", "Boots", "ItemInHand"};
    
    private Offset offset;
    
    private int entityID;
    private EntityType type;
    private String custom_name;
    private List<String> effects;
    
    private Equipment equipment;
    
    protected Entity lastSpawnedEntity = null;
    
    public EntityReward(Outcome outcome){
        super(outcome);
        this.entityID = -1;
        this.type = null;
        this.custom_name = null;
        this.effects = new ArrayList<>();        
        this.equipment = new Equipment();        
        this.offset = new Offset();
    }
    
    public int getID(){
        return this.entityID;
    }
    public void setID(int entityID){
        this.entityID = entityID;
    }
    
    public void setOffset(Offset offset){
        this.offset = offset;
    }
    public Offset getOffset(){
        return this.offset;
    }
    
    public EntityType getType(){
        return this.type;
    }
    public void setType(EntityType type){
        this.type = type;
    }
    public String getCustom_name(){
        return custom_name;
    }
    public void setCustom_name(String custom_name) {
        this.custom_name = custom_name;
    }
    public List<String> getEffects(){
        return effects;
    }
    public void setEffects(List<String> effects){
        this.effects = new ArrayList(effects);
    }
    public Equipment getEquipment(){
        return equipment;
    }
    public void setEquipment(Equipment equipment){
        this.equipment = equipment;
    }
    
    public static XMaterial getXMaterialFromEntityType(EntityType type){        
        try{
            String materialName = type.name() + "_SPAWN_EGG";
            return XMaterial.valueOf(materialName);
        }catch(Exception ex){
            return XMaterial.GHAST_SPAWN_EGG;
        }
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        XMaterial material = getXMaterialFromEntityType(type);
        ItemBuilder builder = ItemBuilder.newItem(material);
        builder.withDisplayName("&2Entity");
        builder.addLoreLine("&bID: &r" + entityID);
        builder.addLoreLine("&btype: &e" + Logger.stripColor(type.name()));
        if(custom_name == null){
            builder.addLoreLine("&bcustom-name: &cnull");
        }else{
            builder.addLoreLine("&bcustom-name: &r" + custom_name);
        }
        if(effects.isEmpty()){
            builder.addLoreLine("&beffects: &cnull");
        }else{
            builder.addLoreLine("&beffects: &r");
            effects.forEach((str) -> {
                builder.addLoreLine("   " + str);
            });
        }
        
        if(equipment.isEmpty()){
            builder.addLoreLine("&bequipment: &cnull");
        }else{
            builder.addLoreLine("&bequipment:");
            if(equipment.helmet == null){
                builder.addLoreLine("    &6Helmet: &cnull");
            }else{
                builder.addLoreLine("    &6Helmet: &d" + this.equipment.helmet.getType().name());
            }
            
            if(equipment.chestplate == null){
                builder.addLoreLine("    &6Chestplate: &cnull");
            }else{
                builder.addLoreLine("    &6Chestplate: &d" + this.equipment.chestplate.getType().name());
            }
            
            if(equipment.leggings == null){
                builder.addLoreLine("    &6Leggings: &cnull");
            }else{
                builder.addLoreLine("    &6Leggings: &d" + this.equipment.leggings.getType().name());
            }
            
            if(equipment.boots == null){
                builder.addLoreLine("    &6Boots: &cnull");
            }else{
                builder.addLoreLine("    &6Boots: &d" + this.equipment.boots.getType().name());
            }
            
            if(equipment.itemInHand == null){
                builder.addLoreLine("    &6Item in hand: &cnull");
            }else{
                builder.addLoreLine("    &6Item in hand: &d" + this.equipment.itemInHand.getType().name());
            }
        }
        
        builder.addLoreLine("&dOffset:");
        builder.addLoreLine("   &5X: &3" + offset.getOffsetX());
        builder.addLoreLine("   &5Y: &3" + offset.getOffsetY());
        builder.addLoreLine("   &5Z: &3" + offset.getOffsetZ());
        
        return builder.build();
//</editor-fold>
    }

    @Override
    public void saveRewardIntoConfig(FileConfiguration config, String path){
        //<editor-fold defaultstate="collapsed" desc="Code">
        config.set(path + ".type", this.type.name());
        config.set(path + ".custom_name", this.custom_name);
        if(this.type.isAlive()){
            config.set(path + ".effects", this.effects);
            if(!equipment.isEmpty()){
                List<ItemStack> equip = equipment.asArrayList();
                for(int i=0;i<EQUIP.length;i++){
                    if(equip.get(i) != null && equip.get(i).getType() != Material.AIR){
                        String fullFullPath = path + ".equipment." + EQUIP[i];
                        ItemBuilder builder = ItemBuilder.fromItem(equip.get(i), false);
                        config.set(fullFullPath + ".material", builder.toString());
                        config.set(fullFullPath + ".durability", builder.getDurability());
                        
                        if(builder.hasDisplayName()){
                            config.set(fullFullPath + ".name", Logger.deColor(builder.getDisplayName()));
                        }
                        if(builder.hasLore()){
                            config.set(fullFullPath + ".lore", Logger.deColor(builder.getLore()));
                        }
                        if(builder.hasEnchantments()){
                            config.set(fullFullPath + ".enchantments", builder.getEnchantmentsIntoStringList());
                        }
                        Texture texture = builder.getTexture();
                        if(texture != null){
                            config.set(fullFullPath + ".textureID", texture.getID());
                        }
                    }
                }
            }
        }
        offset.saveToConfig(config, path + ".offset");
//</editor-fold>
    }
    
    @Override
    public void loadRewardFromConfig(FileConfiguration config, String path){
        //<editor-fold defaultstate="collapsed" desc="Code">
        this.type = EntityType.valueOf(config.getString(path + ".type"));
        this.custom_name = config.getString(path + ".custom_name");
        if(this.type.isAlive()){
            this.effects = config.getStringList(path + ".effects");
            List<ItemStack> equipmentList = new ArrayList();
            if(config.contains(path + ".equipment")){
                for(String equipItemName : EQUIP){
                    String fullPath = path + ".equipment." + equipItemName;
                    if(config.contains(fullPath)){
                        ItemBuilder builder = ItemBuilder.newItem(config.getString(fullPath + ".material"));
                        short durability = (short) config.getInt(fullPath + ".durability");
                        
                        builder.withAmount(1);
                        builder.withDurability(durability);
                        if(config.contains(fullPath + ".name")){
                            builder.withDisplayName(config.getString(fullPath + ".name"));
                        }
                        if(config.contains(fullPath + ".lore")){
                            builder.withLore(config.getStringList(fullPath + ".lore"));
                        }
                        if(config.contains(fullPath + ".enchantments")){
                            List<String> enchantments = config.getStringList(fullPath + ".enchantments");
                            builder.withEnchantments(enchantments);
                        }
                        
                        if(config.contains(fullPath + ".textureID")){
                            String textureID = config.getString(fullPath + ".textureID");
                            try{
                                Texture texture = new Texture(textureID);
                                builder.withTexture(texture);
                            }catch(InvalidTextureException ex){
                                Logger.log("Item at " + fullPath + " contains an invalid HeadTexture");
                            }catch(URLTextureException ex){
                                Logger.log("Couldn't load texture of item at " + fullPath + ":",
                                        LogLevel.ERROR);
                                Logger.log(ex, LogLevel.ERROR);
                            }catch(TextureException ex){}
                        }
                        
                        equipmentList.add(builder.build());
                    }else{
                        equipmentList.add(null);
                    }
                }
                this.equipment = new Equipment(equipmentList);
            }
        }else{
            this.equipment = new Equipment();
        }
        if(config.contains(path + ".offset")){
            this.offset = new Offset(config, path + ".offset");
        }
//</editor-fold>
    }

    @Override
    public void execute(Player player, Location location){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Location target = this.offset.addToLocation(location.clone());
        try{
            this.lastSpawnedEntity = target.getWorld().spawnEntity(target, this.type);
        }catch(Exception ex){
            Logger.log("Entity " + this.type.name() + " couldn't be spawned", LogLevel.INFO);
            return;
        }
        
        
        if(this.custom_name != null){
            this.lastSpawnedEntity.setCustomName(this.custom_name);
            this.lastSpawnedEntity.setCustomNameVisible(true);
        }
        
        if(this.lastSpawnedEntity instanceof LivingEntity){
            LivingEntity le = (LivingEntity) this.lastSpawnedEntity;
            
            for(String effect : this.effects){
                String[] effectData = effect.split(";");
                PotionEffectType effectType = PotionEffectType.getByName(effectData[0]);
                int time = Integer.parseInt(effectData[1]) * 20;
                if(time < 0){
                    time = Integer.MAX_VALUE;
                }
                int amplifier = Integer.parseInt(effectData[2]);
                le.addPotionEffect(new PotionEffect(effectType, time, amplifier), true);
            }
            
            if(!this.equipment.isEmpty()){
                if(this.equipment.helmet != null){
                    le.getEquipment().setHelmet(this.equipment.helmet.clone());
                }
                if(this.equipment.chestplate != null){
                    le.getEquipment().setChestplate(this.equipment.chestplate.clone());
                }
                if(this.equipment.leggings != null){
                    le.getEquipment().setLeggings(this.equipment.leggings.clone());
                }
                if(this.equipment.boots != null){
                    le.getEquipment().setBoots(this.equipment.boots.clone());
                }
                if(this.equipment.itemInHand != null){
                    le.getEquipment().setItemInHand(this.equipment.itemInHand.clone());
                }
            }
        }
//</editor-fold>
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.entity;
    }
    
    @Override
    public Reward clone(){
        EntityReward copy = (EntityReward) super.clone();
        copy.effects = new ArrayList<>(this.effects);
        copy.equipment = this.equipment.clone();
        copy.offset = this.offset.clone();
        return copy;
    }
}
