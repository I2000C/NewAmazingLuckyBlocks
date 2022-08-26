package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
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
import org.bukkit.entity.Slime;
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
    private String customName;
    private boolean customNameVisible;
    private List<String> effects;
    
    private int health;
    private Age age;
    private boolean isTamed;
    private int slimeSize;
    
    private Equipment equipment;
    
    protected Entity lastSpawnedEntity = null;
    
    public EntityReward(Outcome outcome){
        super(outcome);
        this.entityID = -1;
        this.health = -1;
        this.slimeSize = -1;
        this.age = Age.ADULT;
        this.isTamed = false;
        this.type = null;
        this.customName = null;
        this.customNameVisible = true;
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
    
    public static XMaterial getXMaterialFromEntityType(EntityType type){        
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(type == null){
            return XMaterial.GHAST_SPAWN_EGG;
        }
        
        try{
            String materialName = type.name() + "_SPAWN_EGG";
            return XMaterial.valueOf(materialName);
        }catch(Exception ex){
            switch(type){
                case PRIMED_TNT: return XMaterial.TNT;
                case THROWN_EXP_BOTTLE: return XMaterial.EXPERIENCE_BOTTLE;
                case WITHER_SKULL: return XMaterial.WITHER_SKELETON_SKULL;
                case ARROW: return XMaterial.ARROW;
                case BOAT: return XMaterial.OAK_BOAT;
                case ENDER_PEARL: return XMaterial.ENDER_PEARL;
                case ENDER_SIGNAL: return XMaterial.ENDER_EYE;
                
                case SMALL_FIREBALL:
                case FIREBALL: return XMaterial.FIRE_CHARGE;
                case SNOWBALL: return XMaterial.SNOWBALL;
                
                case GIANT: return XMaterial.ZOMBIE_SPAWN_EGG;
                case MUSHROOM_COW: return XMaterial.MOOSHROOM_SPAWN_EGG;
                
                case MINECART: return XMaterial.MINECART;
                case MINECART_CHEST: return XMaterial.CHEST_MINECART;
                case MINECART_COMMAND: return XMaterial.COMMAND_BLOCK_MINECART;
                case MINECART_FURNACE: return XMaterial.FURNACE_MINECART;
                case MINECART_HOPPER: return XMaterial.HOPPER_MINECART;
                case MINECART_TNT: return XMaterial.TNT_MINECART;
                
                default: return XMaterial.GHAST_SPAWN_EGG;
            }
        }
//</editor-fold>
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        XMaterial material = getXMaterialFromEntityType(type);
        ItemBuilder builder = ItemBuilder.newItem(material);
        builder.withDisplayName("&2Entity");
        builder.addLoreLine("&bID: &r" + entityID);
        builder.addLoreLine("&btype: &e" + Logger.stripColor(type.name()));
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
        
        if(Age.isAgeable(type)){
            builder.addLoreLine("&bAge: &e" + age.name());
        }
        if(isTameable(type)){
            builder.addLoreLine("&bIs tamed: &e" + isTamed);
        }
        
        if(type.isAlive()){
            if(health >= 0){
                builder.addLoreLine("&bHealth: &d" + health);
            }else{
                builder.addLoreLine("&bHealth: &dDEFAULT");
            }
            
            if(type == EntityType.SLIME){
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
            config.set(path + ".isTamed", this.isTamed);
            if(type == EntityType.SLIME){
                config.set(path + ".slimeSize", this.slimeSize);
            }
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
        this.customName = config.getString(path + ".custom_name");
        this.customNameVisible = config.getBoolean(path + ".custom_name_visible");
        if(this.type.isAlive()){
            this.health = config.getInt(path + ".health", -1);
            this.age = Age.valueOf(config.getString(path + ".age", Age.ADULT.name()));
            this.isTamed = config.getBoolean(path + ".isTamed");
            this.slimeSize = config.getInt(path + ".slimeSize", -1);
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
            EntityType targetType = this.type;
            if(this.type == EntityType.OCELOT && this.isTamed){
                // Since Minecraft 1.14 tamed Ocelots are cats
                if(NewAmazingLuckyBlocks.getMinecraftVersion().compareTo(MinecraftVersion.v1_14) >= 0){
                    targetType = EntityType.valueOf("CAT");
                }
            }
            
            this.lastSpawnedEntity = target.getWorld().spawnEntity(target, targetType);
        }catch(Exception ex){
            Logger.err("Entity " + this.type.name() + " couldn't be spawned due to:");
            Logger.err(ex);
            return;
        }
        
        
        if(this.customName != null){
            this.lastSpawnedEntity.setCustomName(Logger.color(this.customName));
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
                            .addItem(new ItemStack[]{new ItemStack(XMaterial.SADDLE.parseItem())});
                }else if(this.lastSpawnedEntity instanceof Ocelot){
                    if(NewAmazingLuckyBlocks.getMinecraftVersion().compareTo(MinecraftVersion.v1_13) < 0){
                        int randomType = OtherUtils.generateRandomInt(1, 3);
                        Ocelot.Type catType = Ocelot.Type.getType(randomType);
                        ((Ocelot) this.lastSpawnedEntity).setCatType(catType);
                    }                    
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
            return entityType != null &&
                    (Ageable.class.isAssignableFrom(entityType.getEntityClass())
                    || entityType == EntityType.ZOMBIE);
        }
//</editor-fold>
    }
    
    public static boolean isTameable(EntityType entityType){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return entityType != null
                && Tameable.class.isAssignableFrom(entityType.getEntityClass());
//</editor-fold>
    }
}
