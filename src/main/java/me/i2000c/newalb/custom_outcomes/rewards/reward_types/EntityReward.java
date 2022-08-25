package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import me.i2000c.newalb.custom_outcomes.rewards.Equipment;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.RewardType;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.textures.InvalidTextureException;
import me.i2000c.newalb.utils.textures.Texture;
import me.i2000c.newalb.utils.textures.TextureException;
import me.i2000c.newalb.utils.textures.URLTextureException;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.Offset;
import me.i2000c.newalb.utils2.OtherUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Zombie;
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
    
    private Age age;
    private boolean isTamed;
    
    private Equipment equipment;
    
    protected Entity lastSpawnedEntity = null;
    
    public EntityReward(Outcome outcome){
        super(outcome);
        this.entityID = -1;
        this.age = Age.ADULT;
        this.isTamed = false;
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
    public Age getAge(){
        return this.age;
    }
    public void setAge(Age age){
        this.age = age;
    }
    public boolean isTamed(){
        return this.isTamed;
    }
    public void setIsTamed(boolean isTamed){
        this.isTamed = isTamed;
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
        
        builder.addLoreLine("&bage: &e" + age.name());
        builder.addLoreLine("&bisTamed: &e" + isTamed);
        
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
            config.set(path + ".age", this.age.name());
            config.set(path + ".isTamed", this.isTamed);
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
            this.age = Age.valueOf(config.getString(path + ".age", Age.ADULT.name()));
            this.isTamed = config.getBoolean(path + ".isTamed");
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
                                Logger.err("Item at " + fullPath + " contains an invalid HeadTexture");
                            }catch(URLTextureException ex){
                                Logger.err("Couldn't load texture of item at " + fullPath + ":");
                                Logger.err(ex);
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
        this.offset = new Offset(config, path + ".offset");
//</editor-fold>
    }

    @Override
    public void execute(Player player, Location location){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Location target = this.offset.applyToLocation(location.clone());
        try{
            this.lastSpawnedEntity = target.getWorld().spawnEntity(target, this.type);
        }catch(Exception ex){
            Logger.err("Entity " + this.type.name() + " couldn't be spawned due to:");
            Logger.err(ex);
            return;
        }
        
        
        if(this.custom_name != null){
            this.lastSpawnedEntity.setCustomName(this.custom_name);
            this.lastSpawnedEntity.setCustomNameVisible(true);
        }
        
        if(this.lastSpawnedEntity instanceof LivingEntity){
            LivingEntity le = (LivingEntity) this.lastSpawnedEntity;
            
            this.age.setAge(le);
            
            if(this.isTamed && this.lastSpawnedEntity instanceof Tameable){
                ((Tameable) this.lastSpawnedEntity).setTamed(true);
                ((Tameable) this.lastSpawnedEntity).setOwner(player);
                if(this.lastSpawnedEntity instanceof Horse){
                    ((Horse) this.lastSpawnedEntity)
                            .getInventory()
                            .addItem(new ItemStack[]{new ItemStack(XMaterial.SADDLE.parseItem())});
                }else if(this.lastSpawnedEntity instanceof Ocelot){
                    int randomType = OtherUtils.generateRandomInt(1, 3);
                    Ocelot.Type catType = Ocelot.Type.getType(randomType);
                    ((Ocelot) this.lastSpawnedEntity).setCatType(catType);
                }
            }
            
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
    
    public static enum Age{
        //<editor-fold defaultstate="collapsed" desc="Code">
        BABY,
        ADULT,
        RANDOM;
        
        private static final Age[] vals = values();
        
        public void setAge(Entity entity){
            switch(this){
                case BABY:
                    if(entity instanceof Ageable){
                        ((Ageable) entity).setBaby();
                    }else if(entity instanceof Zombie){
                        ((Zombie) entity).setBaby(true);
                    }
                    break;
                case ADULT:
                    if(entity instanceof Ageable){
                        ((Ageable) entity).setAdult();
                    }else if(entity instanceof Zombie){
                        ((Zombie) entity).setBaby(false);
                    }
                    break;
                case RANDOM:
                    Random random = new Random();
                    if(random.nextBoolean()){
                        BABY.setAge(entity);
                    }else{
                        ADULT.setAge(entity);
                    }
                    break;
            }
        }
        
        public Age next(){
            return vals[(this.ordinal() + 1) % vals.length];
        }
        
        public static boolean isAgeable(EntityType entityType){
            return Ageable.class.isAssignableFrom(entityType.getEntityClass())
                    || entityType == EntityType.ZOMBIE;
        }
//</editor-fold>
    }
}
