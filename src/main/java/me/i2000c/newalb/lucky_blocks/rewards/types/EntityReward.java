package me.i2000c.newalb.lucky_blocks.rewards.types;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
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

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.api.version.MinecraftVersion;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.lucky_blocks.editors.menus.entity.EntityMenu;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.Reward;
import me.i2000c.newalb.lucky_blocks.rewards.RewardType;
import me.i2000c.newalb.utils.locations.Offset;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.Equipment;
import me.i2000c.newalb.utils.misc.EquipmentSlot;
import me.i2000c.newalb.utils.misc.ExtendedEntityType;
import me.i2000c.newalb.utils.misc.ExtendedEntityType.Age;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.random.RandomUtils;

@Getter
@Setter
public class EntityReward extends Reward<EntityReward> {
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
    
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    protected Entity lastSpawnedEntity = null;
    
    public EntityReward(Outcome outcome) {
        super(outcome);
        this.entityID = outcome != null ? outcome.getEntityRewardsNumber() : EntityTowerReward.INVALID_ENTITY_ID;
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
    
    @Override
    public ItemStack getItemToDisplay() {
        XMaterial material = type.getMaterial();
        ItemStackWrapper wrapper = ItemStackWrapper.newItem(material);
        wrapper.setDisplayName("&2Entity");
        wrapper.addLoreLine("&bID: &r" + entityID);
        wrapper.addLoreLine("&btype: &e" + type.name());
        if(customName == null) {
            wrapper.addLoreLine("&bCustom name: &cnull");
        } else {
            wrapper.addLoreLine("&bCustom name: &r" + customName);
            if(customNameVisible) {
                wrapper.addLoreLine("&bCustom name visible: &atrue");
            } else {
                wrapper.addLoreLine("&bCustom name visible: &cfalse");
            }
        }
        
        if(type.isAgeable()) {
            wrapper.addLoreLine("&bAge: &e" + age.name());
        }
        if(type.isTameable()) {
            wrapper.addLoreLine("&bIs tamed: &e" + isTamed);
        }
        if(type.isAngryable()) {
            wrapper.addLoreLine("&bIs angry: &e" + isAngry);
        }
        
        if(type.isAlive()) {
            if(health >= 0) {
                wrapper.addLoreLine("&bHealth: &d" + health);
            } else {
                wrapper.addLoreLine("&bHealth: &dDEFAULT");
            }
            
            if(type.isSlime()) {
                if(slimeSize >= 0) {
                    wrapper.addLoreLine("&bSlime size: &d" + slimeSize);
                } else {
                    wrapper.addLoreLine("&bSlime size: &dDEFAULT");
                }
            }
            
            if(effects.isEmpty()) {
                wrapper.addLoreLine("&bEffects: &cnull");
            } else {
                wrapper.addLoreLine("&bEffects: &r");
                effects.forEach((str) -> {
                    wrapper.addLoreLine("   " + str);
                });
            }

            if(equipment.isEmpty()) {
                wrapper.addLoreLine("&bEquipment: &cnull");
            } else {
                wrapper.addLoreLine("&bEquipment:");
                
                for(EquipmentSlot equipmentSlot : EquipmentSlot.VALUES) {
                    ItemStack stack = equipment.getItem(equipmentSlot);
                    if(stack == null) {
                        wrapper.addLoreLine(String.format("    &6%s: &cnull", equipmentSlot.getConfigKey()));
                    } else {
                        ItemStackWrapper wrapper2 = ItemStackWrapper.fromItem(stack, false);
                        String name = wrapper2.toString();
                        int amount = wrapper2.getAmount();
                        int dropChance = equipment.getDropChance(equipmentSlot);
                        wrapper.addLoreLine(String.format("    &6%s: &d%s x%d (%d %%)", equipmentSlot.getConfigKey(), name, amount, dropChance));
                    }
                }
            }
        }
        
        if(usePlayerLoc) {
            wrapper.addLoreLine("&bTarget location: &2player");
        } else {
            wrapper.addLoreLine("&bTarget location: &6lucky block");
        }
        
        wrapper.addLoreLine("&dOffset:");
        wrapper.addLoreLine("   &5X: &3" + offset.getOffsetX());
        wrapper.addLoreLine("   &5Y: &3" + offset.getOffsetY());
        wrapper.addLoreLine("   &5Z: &3" + offset.getOffsetZ());
        
        return wrapper.toItemStack();
    }

    @Override
    public void saveRewardIntoConfig(Config config, String path) {
        config.set(path + ".type", this.type.name());
        config.set(path + ".custom_name", this.customName);
        config.set(path + ".custom_name_visible", this.customNameVisible);
        if(this.type.isAlive()) {
            config.set(path + ".health", this.health);
            config.set(path + ".age", this.age.name());
            if(this.type.isTameable()) {
                config.set(path + ".isTamed", this.isTamed);
            }
            if(this.type.isAngryable()) {
                config.set(path + ".isAngry", this.isAngry);
            }            
            if(type.isSlime()) {
                config.set(path + ".slimeSize", this.slimeSize);
            }
            config.set(path + ".effects", this.effects);
            equipment.saveToConfig(config, path + ".equipment");
        }
        offset.saveToConfig(config, path + ".offset");
        config.set(path + ".usePlayerLoc", this.usePlayerLoc);
    }
    
    @Override
    public void loadRewardFromConfig(Config config, String path) {
        this.type = ExtendedEntityType.valueOf(config.getString(path + ".type"));
        this.customName = config.getString(path + ".custom_name", null);
        this.customNameVisible = config.getBoolean(path + ".custom_name_visible", true);
        if(this.type.isAlive()) {
            this.health = config.getInt(path + ".health", -1);
            this.age = config.getEnum(path + ".age", Age.class, Age.ADULT);
            this.isTamed = config.getBoolean(path + ".isTamed", false);
            this.isAngry = config.getBoolean(path + ".isAngry", false);
            this.slimeSize = config.getInt(path + ".slimeSize", -1);
            this.effects = config.getStringList(path + ".effects");
            this.equipment = new Equipment(config, path + ".equipment");
        } else {
            this.equipment = new Equipment();
        }
        this.offset = new Offset(config, path + ".offset");
        this.usePlayerLoc = config.getBoolean(path + ".usePlayerLoc", false);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void execute(Player player, Location location) {
        Location baseLocation = this.usePlayerLoc ? player.getLocation() : location;
        Location targetLocation = this.offset.applyToLocation(baseLocation.clone());
        try {
            ExtendedEntityType targetType = this.type;
            if(this.type.isOcelot() && this.isTamed) {
                // Since Minecraft 1.14 tamed Ocelots are cats
                if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_14)) {
                    targetType = ExtendedEntityType.valueOf("CAT");
                }
            }
            
            this.lastSpawnedEntity = targetType.spawnEntity(targetLocation);
        } catch(Exception ex) {
            Logger.err("Entity " + this.type.name() + " couldn't be spawned due to:");
            Logger.err(ex);
            return;
        }
        
        
        if(this.customName != null) {
            this.lastSpawnedEntity.setCustomName(Logger.color(this.customName.replace("%player%", player.getName())));
            this.lastSpawnedEntity.setCustomNameVisible(this.customNameVisible);
        }
        
        if(this.lastSpawnedEntity instanceof LivingEntity) {
            LivingEntity le = (LivingEntity) this.lastSpawnedEntity;
            if(le == null) {
                return;
            }
            
            if(le instanceof Slime && slimeSize >= 0) {
                ((Slime) le).setSize(slimeSize);
            }
            
            if(health >= 0) {                
                if(health == 0) {
                    le.damage(le.getMaxHealth());
                } else {
                    le.setMaxHealth(health);
                    le.setHealth(health);
                }
            }
                
            this.age.setAge(le);
            
            if(this.isTamed && this.lastSpawnedEntity instanceof Tameable) {
                ((Tameable) this.lastSpawnedEntity).setTamed(true);
                ((Tameable) this.lastSpawnedEntity).setOwner(player);
                if(this.lastSpawnedEntity instanceof Horse) {
                    ((Horse) this.lastSpawnedEntity)
                            .getInventory()
                            .addItem(XMaterial.SADDLE.parseItem());
                }else if(this.lastSpawnedEntity instanceof Ocelot) {
                    if(MinecraftVersion.CURRENT_VERSION.isLessThan(MinecraftVersion.v1_13)) {
                        int randomType = RandomUtils.getInt(1, 3);
                        Ocelot.Type catType = Ocelot.Type.getType(randomType);
                        ((Ocelot) this.lastSpawnedEntity).setCatType(catType);
                    }                    
                }
            }
            
            if(this.isAngry) {
                this.type.setAngry(this.lastSpawnedEntity, player);
            }
            
            for(String effect : this.effects) {
                String[] effectData = effect.split(";");
                XPotion effectType = XPotion.of(effectData[0]).get();
                if(effectType == XPotion.INVISIBILITY && le instanceof ArmorStand) {
                    ((ArmorStand) le).setVisible(false);
                    continue;
                }
                
                int time = Integer.parseInt(effectData[1]) * 20;
                if(time < 0) {
                    time = Integer.MAX_VALUE;
                }
                int amplifier = Integer.parseInt(effectData[2]);
                le.addPotionEffect(new PotionEffect(effectType.getPotionEffectType(), time, amplifier), true);
            }
            
            this.equipment.applyToEntity(le);
        }
    }
    
    @Override
    public RewardType getRewardType() {
        return RewardType.entity;
    }
    
    @Override
    public EditorMenu<EntityReward> getEditor() {
        return new EntityMenu();
    }
    
    @Override
    public EntityReward clone() {
        EntityReward copy = (EntityReward) super.clone();
        copy.effects = new ArrayList<>(this.effects);
        copy.equipment = this.equipment.clone();
        copy.offset = this.offset.clone();
        return copy;
    }
}
