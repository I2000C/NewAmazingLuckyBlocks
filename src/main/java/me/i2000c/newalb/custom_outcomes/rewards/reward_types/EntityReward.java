package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.List;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.RewardType;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.Equipment;
import me.i2000c.newalb.utils2.ExtendedEntityType;
import me.i2000c.newalb.utils2.ExtendedEntityType.Age;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.Offset;
import me.i2000c.newalb.utils2.RandomUtils;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EntityReward extends Reward{
    private Offset offset;
    private boolean usePlayerLoc;
    
    private int entityID;
    private ExtendedEntityType type;
    private String customName;
    private boolean customNameVisible;
    private List<String> effects;
    
    private int health;
    private Age age;
    private boolean isTamed;
    private int slimeSize;
    private boolean isAngry;
    
    private Equipment equipment;
    
    protected Entity lastSpawnedEntity = null;
    
    public EntityReward(Outcome outcome){
        super(outcome);
        this.entityID = -1;
        this.health = -1;
        this.slimeSize = -1;
        this.age = Age.ADULT;
        this.isTamed = false;
        this.isAngry = false;
        this.type = null;
        this.customName = null;
        this.customNameVisible = true;
        this.effects = new ArrayList<>();
        this.equipment = new Equipment();
        this.offset = new Offset();
        this.usePlayerLoc = false;
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
    
    public void setUsePlayerLoc(boolean usePlayerLoc){
        this.usePlayerLoc = usePlayerLoc;
    }
    public boolean getUsePlayerLoc(){
        return this.usePlayerLoc;
    }
    
    public ExtendedEntityType getType(){
        return this.type;
    }
    public void setType(ExtendedEntityType type){
        this.type = type;
    }
    public String getCustomName(){
        return customName;
    }
    public void setCustomName(String customName) {
        this.customName = customName;
    }
    public boolean isCustomNameVisible(){
        return this.customNameVisible;
    }
    public void setCustomNameVisible(boolean customNameVisible){
        this.customNameVisible = customNameVisible;
    }
    public List<String> getEffects(){
        return effects;
    }
    public void setEffects(List<String> effects){
        this.effects = new ArrayList(effects);
    }
    public int getHealth(){
        return this.health;
    }
    public void setHealth(int health){
        this.health = health;
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
    public boolean isAngry() {
        return this.isAngry;
    }
    public void setIsAngry(boolean isAngry) {
        this.isAngry = isAngry;
    }
    public Equipment getEquipment(){
        return equipment;
    }
    public void setEquipment(Equipment equipment){
        this.equipment = equipment;
    }
    public int getSlimeSize(){
        return this.slimeSize;
    }
    public void setSlimeSize(int slimeSize){
        this.slimeSize = slimeSize;
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        XMaterial material = type.getMaterial();
        ItemBuilder builder = ItemBuilder.newItem(material);
        builder.withDisplayName("&2Entity");
        builder.addLoreLine("&bID: &r" + entityID);
        builder.addLoreLine("&btype: &e" + type.name());
        if(customName == null){
            builder.addLoreLine("&bCustom name: &cnull");
        }else{
            builder.addLoreLine("&bCustom name: &r" + customName);
            if(customNameVisible){
                builder.addLoreLine("&bCustom name visible: &atrue");
            }else{
                builder.addLoreLine("&bCustom name visible: &cfalse");
            }
        }
        
        if(type.isAgeable()){
            builder.addLoreLine("&bAge: &e" + age.name());
        }
        if(type.isTameable()){
            builder.addLoreLine("&bIs tamed: &e" + isTamed);
        }
        if(type.isAngryable()) {
            builder.addLoreLine("&bIs angry: &e" + isAngry);
        }
        
        if(type.isAlive()){
            if(health >= 0){
                builder.addLoreLine("&bHealth: &d" + health);
            }else{
                builder.addLoreLine("&bHealth: &dDEFAULT");
            }
            
            if(type.isSlime()){
                if(slimeSize >= 0){
                    builder.addLoreLine("&bSlime size: &d" + slimeSize);
                }else{
                    builder.addLoreLine("&bSlime size: &dDEFAULT");
                }
            }
            
            if(effects.isEmpty()){
                builder.addLoreLine("&bEffects: &cnull");
            }else{
                builder.addLoreLine("&bEffects: &r");
                effects.forEach((str) -> {
                    builder.addLoreLine("   " + str);
                });
            }

            if(equipment.isEmpty()){
                builder.addLoreLine("&bEquipment: &cnull");
            }else{
                builder.addLoreLine("&bEquipment:");
                
                ItemStack helmet = equipment.getEquipmentItem(Equipment.HELMET_ID);
                ItemStack chestplate = equipment.getEquipmentItem(Equipment.CHESTPLATE_ID);
                ItemStack leggings = equipment.getEquipmentItem(Equipment.LEGGINGS_ID);
                ItemStack boots = equipment.getEquipmentItem(Equipment.BOOTS_ID);
                ItemStack itemInHand = equipment.getEquipmentItem(Equipment.ITEM_IN_HAND_ID);
                
                if(helmet == null){
                    builder.addLoreLine("    &6Helmet: &cnull");
                }else{
                    ItemBuilder builder2 = ItemBuilder.fromItem(helmet, false);
                    String name = builder2.toString();
                    int amount = builder2.getAmount();
                    builder.addLoreLine("    &6Helmet: &d" + name + " x" + amount);
                }

                if(chestplate == null){
                    builder.addLoreLine("    &6Chestplate: &cnull");
                }else{
                    ItemBuilder builder2 = ItemBuilder.fromItem(chestplate, false);
                    String name = builder2.toString();
                    int amount = builder2.getAmount();
                    builder.addLoreLine("    &6Chestplate: &d" + name + " x" + amount);
                }

                if(leggings == null){
                    builder.addLoreLine("    &6Leggings: &cnull");
                }else{
                    ItemBuilder builder2 = ItemBuilder.fromItem(leggings, false);
                    String name = builder2.toString();
                    int amount = builder2.getAmount();
                    builder.addLoreLine("    &6Leggings: &d" + name + " x" + amount);
                }

                if(boots == null){
                    builder.addLoreLine("    &6Boots: &cnull");
                }else{
                    ItemBuilder builder2 = ItemBuilder.fromItem(boots, false);
                    String name = builder2.toString();
                    int amount = builder2.getAmount();
                    builder.addLoreLine("    &6Boots: &d" + name + " x" + amount);
                }

                if(itemInHand == null){
                    builder.addLoreLine("    &6Item in hand: &cnull");
                }else{
                    ItemBuilder builder2 = ItemBuilder.fromItem(itemInHand, false);
                    String name = builder2.toString();
                    int amount = builder2.getAmount();
                    builder.addLoreLine("    &6Item in hand: &d" + name + " x" + amount);
                }
            }
        }
        
        if(usePlayerLoc){
            builder.addLoreLine("&bTarget location: &2player");
        }else{
            builder.addLoreLine("&bTarget location: &6lucky block");
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
        config.set(path + ".custom_name", this.customName);
        config.set(path + ".custom_name_visible", this.customNameVisible);
        if(this.type.isAlive()){
            config.set(path + ".health", this.health);
            config.set(path + ".age", this.age.name());
            if(this.type.isTameable()) {
                config.set(path + ".isTamed", this.isTamed);
            }
            if(this.type.isAngryable()) {
                config.set(path + ".isAngry", this.isAngry);
            }            
            if(type.isSlime()){
                config.set(path + ".slimeSize", this.slimeSize);
            }
            config.set(path + ".effects", this.effects);
            equipment.saveToConfig(config, path + ".equipment");
        }
        offset.saveToConfig(config, path + ".offset");
        config.set(path + ".usePlayerLoc", this.usePlayerLoc);
//</editor-fold>
    }
    
    @Override
    public void loadRewardFromConfig(FileConfiguration config, String path){
        //<editor-fold defaultstate="collapsed" desc="Code">
        this.type = ExtendedEntityType.valueOf(config.getString(path + ".type"));
        this.customName = config.getString(path + ".custom_name");
        this.customNameVisible = config.getBoolean(path + ".custom_name_visible");
        if(this.type.isAlive()){
            this.health = config.getInt(path + ".health", -1);
            this.age = Age.valueOf(config.getString(path + ".age", Age.ADULT.name()));
            this.isTamed = config.getBoolean(path + ".isTamed");
            this.isAngry = config.getBoolean(path + ".isAngry");
            this.slimeSize = config.getInt(path + ".slimeSize", -1);
            this.effects = config.getStringList(path + ".effects");
            this.equipment = new Equipment(config, path + ".equipment");
        }else{
            this.equipment = new Equipment();
        }
        this.offset = new Offset(config, path + ".offset");
        this.usePlayerLoc = config.getBoolean(path + ".usePlayerLoc");
//</editor-fold>
    }

    @Override
    public void execute(Player player, Location location){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Location baseLocation = this.usePlayerLoc ? player.getLocation() : location;
        Location targetLocation = this.offset.applyToLocation(baseLocation.clone());
        try{
            ExtendedEntityType targetType = this.type;
            if(this.type.isOcelot() && this.isTamed){
                // Since Minecraft 1.14 tamed Ocelots are cats
                if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_14)){
                    targetType = ExtendedEntityType.valueOf("CAT");
                }
            }
            
            this.lastSpawnedEntity = targetType.spawnEntity(targetLocation);
        }catch(Exception ex){
            Logger.err("Entity " + this.type.name() + " couldn't be spawned due to:");
            Logger.err(ex);
            return;
        }
        
        
        if(this.customName != null){
            this.lastSpawnedEntity.setCustomName(Logger.color(this.customName.replace("%player%", player.getName())));
            this.lastSpawnedEntity.setCustomNameVisible(this.customNameVisible);
        }
        
        if(this.lastSpawnedEntity instanceof LivingEntity){
            LivingEntity le = (LivingEntity) this.lastSpawnedEntity;
            
            if(le instanceof Slime && slimeSize >= 0){
                ((Slime) le).setSize(slimeSize);
            }
            
            if(health >= 0){                
                if(health == 0){
                    le.damage(le.getMaxHealth());
                }else{
                    le.setMaxHealth(health);
                    le.setHealth(health);
                }
            }
                
            this.age.setAge(le);
            
            if(this.isTamed && this.lastSpawnedEntity instanceof Tameable){
                ((Tameable) this.lastSpawnedEntity).setTamed(true);
                ((Tameable) this.lastSpawnedEntity).setOwner(player);
                if(this.lastSpawnedEntity instanceof Horse){
                    ((Horse) this.lastSpawnedEntity)
                            .getInventory()
                            .addItem(XMaterial.SADDLE.parseItem());
                }else if(this.lastSpawnedEntity instanceof Ocelot){
                    if(MinecraftVersion.CURRENT_VERSION.isLessThan(MinecraftVersion.v1_13)){
                        int randomType = RandomUtils.getInt(1, 3);
                        Ocelot.Type catType = Ocelot.Type.getType(randomType);
                        ((Ocelot) this.lastSpawnedEntity).setCatType(catType);
                    }                    
                }
            }
            
            if(this.isAngry) {
                this.type.setAngry(this.lastSpawnedEntity, player);
            }
            
            for(String effect : this.effects){
                String[] effectData = effect.split(";");
                PotionEffectType effectType = PotionEffectType.getByName(effectData[0]);
                if(effectType.equals(PotionEffectType.INVISIBILITY)
                        && le instanceof ArmorStand){
                    ((ArmorStand) le).setVisible(false);
                    continue;
                }
                
                int time = Integer.parseInt(effectData[1]) * 20;
                if(time < 0){
                    time = Integer.MAX_VALUE;
                }
                int amplifier = Integer.parseInt(effectData[2]);
                le.addPotionEffect(new PotionEffect(effectType, time, amplifier), true);
            }
            
            this.equipment.applyToEntity(le);
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
