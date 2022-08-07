package me.i2000c.newalb.custom_outcomes.utils.rewards;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.List;
import me.i2000c.newalb.custom_outcomes.menus.EntityMenu;
import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import me.i2000c.newalb.utils.EnchantmentUtils;
import me.i2000c.newalb.utils.logger.LogLevel;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils.textures.InvalidTextureException;
import me.i2000c.newalb.utils.textures.Texture;
import me.i2000c.newalb.utils.textures.TextureManager;
import me.i2000c.newalb.utils2.Offset;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
    
    public EntityReward(Outcome outcome, int entityID){
        super(outcome);
        this.entityID = entityID;
        this.type = null;
        this.custom_name = null;
        this.effects = new ArrayList();
        
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
    
    @Override
    public ItemStack getItemToDisplay(){
        List<String> lore = new ArrayList();
        lore.add("&bID: &r" + entityID);
        lore.add("&btype: &e" + Logger.stripColor(this.type.name()));
        lore.add("&bcustom-name: &r" + this.custom_name);
        if(this.effects.isEmpty()){
            lore.add("&beffects: &rnull");
        }else{
            lore.add("&beffects: &r");
            this.effects.forEach((str) -> {
                lore.add("   " + str);
            });
        }                   

        if(this.equipment.isEmpty()){
            lore.add("&bequipment: &rnull");
        }else{
            lore.add("&bequipment: &r");
            try{
                lore.add("   &6Helmet: &d" + this.equipment.helmet.getType().name());
            }catch(Exception ex){}
            try{
                lore.add("   &6Chestplate: &d" + this.equipment.chestplate.getType().name());
            }catch(Exception ex){}    
            try{
                lore.add("   &6Leggings: &d" + this.equipment.leggings.getType().name());
            }catch(Exception ex){}
            try{
                lore.add("   &6Boots: &d" + this.equipment.boots.getType().name());
            }catch(Exception ex){}
            try{
                lore.add("   &6Item in hand: &d" + this.equipment.itemInHand.getType().name());
            }catch(Exception ex){}
        }
        
        lore.add("&dOffset:");
        lore.add("   &5X: &3" + offset.getOffsetX());
        lore.add("   &5Y: &3" + offset.getOffsetY());
        lore.add("   &5Z: &3" + offset.getOffsetZ());

        ItemStack stack = XMaterial.GHAST_SPAWN_EGG.parseItem();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("&2Entity");                        
        meta.setLore(lore);
        stack.setItemMeta(meta);
        
        return stack;
    }

    @Override
    public void saveRewardIntoConfig(FileConfiguration config, String path){
        config.set(path + ".type", this.type.name());
        config.set(path + ".custom_name", this.custom_name);
        if(this.type.isAlive()){
            config.set(path + ".effects", this.effects);
            if(!equipment.isEmpty()){
                List<ItemStack> equip = equipment.asArrayList();
                for(int i=0;i<EQUIP.length;i++){
                    if(equip.get(i) != null && equip.get(i).getType() != Material.AIR){
                        String fullFullPath = path + ".equipment." + EQUIP[i];
                        ItemStack sk = equip.get(i);
                        config.set(fullFullPath + ".material", sk.getType().name());
                        config.set(fullFullPath + ".durability", sk.getDurability());
                        if(!sk.hasItemMeta()){
                            continue;
                        }
                        ItemMeta meta = sk.getItemMeta();
                        if(meta.hasDisplayName()){
                            config.set(fullFullPath + ".name", Logger.deColor(meta.getDisplayName()));
                        }
                        if(meta.hasLore()){
                            config.set(fullFullPath + ".lore", Logger.deColor(meta.getLore()));
                        }
                        if(meta.hasEnchants()){
                            config.set(fullFullPath + ".enchantments", EnchantmentUtils.getEnchantments(meta));
                        }
                        Texture texture = TextureManager.getTexture(sk);
                        if(texture != null){
                            config.set(fullFullPath + ".textureID", texture.getID());
                        }
                    }
                }
            }
        }        
        offset.saveToConfig(config, path + ".offset");
    }
    
    @Override
    public void loadRewardFromConfig(FileConfiguration config, String path){
        this.type = EntityType.valueOf(config.getString(path + ".type"));
        this.custom_name = config.getString(path + ".custom_name");
        if(this.type.isAlive()){
            this.effects = config.getStringList(path + ".effects");
            List<ItemStack> equipmentList = new ArrayList();
            if(config.contains(path + ".equipment")){
                for(int i=0;i<EQUIP.length;i++){
                    String fullPath = path + ".equipment." + EQUIP[i];
                    if(config.contains(fullPath)){
                        Material material = Material.valueOf(config.getString(fullPath + ".material"));
                        short durability = (short) config.getInt(fullPath + ".durability");
                        ItemStack stack = new ItemStack(material, 1, durability);
                        ItemMeta meta = stack.getItemMeta();
                        if(config.contains(fullPath + ".name")){
                            meta.setDisplayName(config.getString(fullPath + ".name"));
                        }
                        if(config.contains(fullPath + ".lore")){
                            meta.setLore(config.getStringList(fullPath + ".lore"));
                        }
                        if(config.contains(fullPath + ".enchantments")){
                            List<String> enchantments = config.getStringList(fullPath + ".enchantments");
                            EnchantmentUtils.setEnchantments(meta, enchantments);
                        }
                        stack.setItemMeta(meta);
                        if(config.contains(fullPath + ".textureID")){
                            String textureID = config.getString(fullPath + ".textureID");
                            try{
                                Texture texture = new Texture(textureID);
                                TextureManager.setTexture(stack, texture);
                            }catch(InvalidTextureException ex){
                                Logger.log("Item at " + fullPath + " contains an invalid HeadTexture");
                            }                            
                        }                    
                        equipmentList.add(stack);
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
    }

    @Override
    public void execute(Player player, Location location){
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
    }
    
    @Override
    public void edit(Player player){
        EntityMenu.reset();
        EntityMenu.reward = this;
        EntityMenu.equipment = this.getEquipment().cloneEquipment();
        EntityMenu.openEntityMenu(player);
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.entity;
    }
    
    @Override
    public Reward cloneReward(){
        EntityReward reward = new EntityReward(this.getOutcome(), this.entityID);
        reward.type = this.type;
        reward.custom_name = this.custom_name;
        reward.setEffects(this.effects);
        reward.setEquipment(this.equipment);
        reward.setOffset(this.offset.clone());
        
        reward.setDelay(this.getDelay());
        return reward;
    }
    
    
    public static class Equipment{
        public ItemStack helmet = null;
        public ItemStack chestplate = null;
        public ItemStack leggings = null;
        public ItemStack boots = null;
        public ItemStack itemInHand = null;
        
        public Equipment(){
        }
        
        public Equipment(List<ItemStack> equip){
            if(equip.size() == 5){
                if(equip.get(0) != null){
                    this.helmet = equip.get(0).clone();
                }
                if(equip.get(1) != null){
                    this.chestplate = equip.get(1).clone();
                }
                if(equip.get(2) != null){
                    this.leggings = equip.get(2).clone();
                }
                if(equip.get(3) != null){
                     this.boots = equip.get(3).clone();
                }
                if(equip.get(4) != null){
                    this.itemInHand = equip.get(4).clone();
                }
            }else{
                throw new IllegalArgumentException("Equipment size must be equals to 5");
            }
        }
        
        public boolean isEmpty(){
            return this.helmet == null && this.chestplate == null && this.leggings == null &&
                    this.boots == null && this.itemInHand == null;
        }
        
        public List<ItemStack> asArrayList(){
            List<ItemStack> list = new ArrayList();
            list.add(this.helmet);
            list.add(this.chestplate);
            list.add(this.leggings);
            list.add(this.boots);
            list.add(this.itemInHand);
            return list;
        }
        
        public Equipment cloneEquipment(){
            Equipment equip = new Equipment();
            
            if(this.helmet != null){
                equip.helmet = this.helmet.clone();
            }
            if(this.chestplate != null){
                equip.chestplate = this.chestplate.clone();
            }
            if(this.leggings != null){
                equip.leggings = this.leggings.clone();
            }
            if(this.boots != null){
                equip.boots = this.boots.clone(); 
            }
            if(this.itemInHand != null){
                equip.itemInHand = this.itemInHand.clone();
            }
            
            return equip;
        }
        
        public void resetEquipment(){
            this.helmet = null;
            this.chestplate = null;
            this.leggings = null;
            this.boots = null;
            this.itemInHand = null;
        }
    }
}
