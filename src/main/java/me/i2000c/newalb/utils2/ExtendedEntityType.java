package me.i2000c.newalb.utils2;

import com.cryptomorin.xseries.XMaterial;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

public class ExtendedEntityType{
    private static enum ExtraEntityType{
        //<editor-fold defaultstate="collapsed" desc="Code">
        DONKEY(true, true, true, XMaterial.HORSE_SPAWN_EGG, null, MinecraftVersion.v1_10),
        MULE(true, true, true, XMaterial.HORSE_SPAWN_EGG, null, MinecraftVersion.v1_10),
        SKELETON_HORSE(true, true, true, XMaterial.SKELETON_SPAWN_EGG, null, MinecraftVersion.v1_10),
        ZOMBIE_HORSE(true, true, true, XMaterial.ZOMBIE_SPAWN_EGG, null, MinecraftVersion.v1_10),
        STRAY(false, false, true, XMaterial.SKELETON_SPAWN_EGG, MinecraftVersion.v1_10, MinecraftVersion.v1_10),
        WITHER_SKELETON(false, false, true, XMaterial.WITHER_SKELETON_SKULL, null, MinecraftVersion.v1_10),
        
        RABBIT_BROWN(false, true, true, XMaterial.RABBIT_SPAWN_EGG, null, null),
        RABBIT_WHITE(false, true, true, XMaterial.RABBIT_SPAWN_EGG, null, null),
        RABBIT_BLACK(false, true, true, XMaterial.RABBIT_SPAWN_EGG, null, null),
        RABBIT_BLACK_AND_WHITE(false, true, true, XMaterial.RABBIT_SPAWN_EGG, null, null),
        RABBIT_GOLD(false, true, true, XMaterial.RABBIT_SPAWN_EGG, null, null),
        RABBIT_SALT_AND_PEPPER(false, true, true, XMaterial.RABBIT_SPAWN_EGG, null, null),
        RABBIT_THE_KILLER_BUNNY(false, true, true, XMaterial.RABBIT_SPAWN_EGG, null, null);
        
        public boolean isTameable;
        public boolean isAgeable;
        public boolean isAlive;
        public XMaterial material;
        public MinecraftVersion minVersion;
        public MinecraftVersion maxVersion;
        
        private ExtraEntityType(
                boolean isTameable, 
                boolean isAgeable, 
                boolean isAlive, 
                XMaterial material,
                MinecraftVersion minVersion,
                MinecraftVersion maxVersion){
            this.isTameable = isTameable;
            this.isAgeable = isAgeable;
            this.isAlive = isAlive;
            this.material = material;
            if(minVersion == null){
                minVersion = MinecraftVersion.getOldestVersion();
            }
            if(maxVersion == null){
                maxVersion = MinecraftVersion.getLatestVersion();
            }
            this.minVersion = minVersion;
            this.maxVersion = maxVersion;
        }
        
        public Entity spawnEntity(Location location){
            World world = location.getWorld();
            switch(this){
                case DONKEY:
                    Horse horse = world.spawn(location, Horse.class);
                    horse.setVariant(Horse.Variant.DONKEY);
                    return horse;
                case MULE:
                    horse = world.spawn(location, Horse.class);
                    horse.setVariant(Horse.Variant.MULE);
                    return horse;
                case SKELETON_HORSE:
                    horse = world.spawn(location, Horse.class);
                    horse.setVariant(Horse.Variant.SKELETON_HORSE);
                    return horse;
                case ZOMBIE_HORSE:
                    horse = world.spawn(location, Horse.class);
                    horse.setVariant(Horse.Variant.UNDEAD_HORSE);
                    return horse;
                case STRAY:
                    Skeleton skeleton = world.spawn(location, Skeleton.class);
                    skeleton.setSkeletonType(Skeleton.SkeletonType.STRAY);
                    return skeleton;
                case WITHER_SKELETON:
                    skeleton = world.spawn(location, Skeleton.class);
                    skeleton.setSkeletonType(Skeleton.SkeletonType.WITHER);
                    return skeleton;
                case RABBIT_BROWN:
                    Rabbit rabbit = world.spawn(location, Rabbit.class);
                    rabbit.setRabbitType(Rabbit.Type.BROWN);
                    return rabbit;
                case RABBIT_WHITE:
                    rabbit = world.spawn(location, Rabbit.class);
                    rabbit.setRabbitType(Rabbit.Type.WHITE);
                    return rabbit;
                case RABBIT_BLACK:
                    rabbit = world.spawn(location, Rabbit.class);
                    rabbit.setRabbitType(Rabbit.Type.BLACK);
                    return rabbit;
                case RABBIT_BLACK_AND_WHITE:
                    rabbit = world.spawn(location, Rabbit.class);
                    rabbit.setRabbitType(Rabbit.Type.BLACK_AND_WHITE);
                    return rabbit;
                case RABBIT_GOLD:
                    rabbit = world.spawn(location, Rabbit.class);
                    rabbit.setRabbitType(Rabbit.Type.GOLD);
                    return rabbit;
                case RABBIT_SALT_AND_PEPPER:
                    rabbit = world.spawn(location, Rabbit.class);
                    rabbit.setRabbitType(Rabbit.Type.SALT_AND_PEPPER);
                    return rabbit;
                case RABBIT_THE_KILLER_BUNNY:
                    rabbit = world.spawn(location, Rabbit.class);
                    rabbit.setRabbitType(Rabbit.Type.THE_KILLER_BUNNY);
                    return rabbit;
                default:
                    throw new Error("This should not happen");
            }
        }
//</editor-fold>
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
            if(entityType == null){
                return false;
            }
            
            if(entityType == EntityType.ZOMBIE){
                return true;
            }
            
            Class entityClass = entityType.getEntityClass();
            if(entityClass == null){
                return false;
            }
            
            return Ageable.class.isAssignableFrom(entityClass);
        }
//</editor-fold>
    }
    
    private static boolean isTameable(EntityType entityType){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(entityType == null){
            return false;
        }
        
        Class entityClass = entityType.getEntityClass();
        if(entityClass == null){
            return false;
        }

        return Tameable.class.isAssignableFrom(entityClass);
//</editor-fold>
    }
    
    private static boolean isAngryable(EntityType entityType) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        return entityType == EntityType.WOLF || entityType.name().equals("BEE");
//</editor-fold>
    }
    
    private static XMaterial getMaterialFromEntityType(EntityType type){        
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(type == null){
            return XMaterial.GHAST_SPAWN_EGG;
        }
        
        try{
            String materialName = type.name() + "_SPAWN_EGG";
            return XMaterial.valueOf(materialName);
        }catch(Exception ex){
            switch(type){
                case EGG: return XMaterial.EGG;
                
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
                case ENDER_DRAGON: return XMaterial.DRAGON_EGG;
                case ARMOR_STAND: return XMaterial.ARMOR_STAND;
                case PIG_ZOMBIE: return XMaterial.PIGLIN_SPAWN_EGG;
                case SNOWMAN: return XMaterial.SNOW_BLOCK;
                
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
    
    private static List<ExtendedEntityType> VALUES;    
    public static List<ExtendedEntityType> values(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(VALUES == null){
            VALUES = new ArrayList<>();
            for(EntityType entityType : EntityType.values()){
                if(!entityType.isSpawnable()
                        && entityType != EntityType.EGG
                        && entityType != EntityType.ARMOR_STAND){
                    continue;
                }
                
                if(entityType == EntityType.PLAYER){
                    continue;
                }
                
                VALUES.add(new ExtendedEntityType(entityType));
            }
            
            MinecraftVersion minecraftVersion = NewAmazingLuckyBlocks.getMinecraftVersion();
            for(ExtraEntityType extraEntityType : ExtraEntityType.values()){
                MinecraftVersion minMinecraftVersion = extraEntityType.minVersion;
                MinecraftVersion maxMinecraftVersion = extraEntityType.maxVersion;
                if(minecraftVersion.compareTo(minMinecraftVersion) >= 0
                        && minecraftVersion.compareTo(maxMinecraftVersion) <= 0){
                    VALUES.add(new ExtendedEntityType(extraEntityType));
                }
            }
            
            // Sort by name
            VALUES.sort(((eet1, eet2) -> eet1.name().compareTo(eet2.name())));
        }
        
        return VALUES;
//</editor-fold>
    }
    
    public static ExtendedEntityType valueOf(String string){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ExtendedEntityType eet;
        try{
            if(NewAmazingLuckyBlocks.getMinecraftVersion().compareTo(MinecraftVersion.v1_16) >= 0
                    && string.equals("PIG_ZOMBIE")){
                // Since Minecraft 1.16, pig zombies were changed to zombified piglins
                eet = new ExtendedEntityType(EntityType.valueOf("ZOMBIFIED_PIGLIN"));
            }else{
                eet = new ExtendedEntityType(EntityType.valueOf(string));
            }            
        }catch(IllegalArgumentException ex){
            eet = new ExtendedEntityType(ExtraEntityType.valueOf(string));
        }
        return eet;
//</editor-fold>
    }
    
    public ExtendedEntityType(EntityType entityType){
        //<editor-fold defaultstate="collapsed" desc="Code">
        this.entityType = entityType;
        this.extraEntityType = null;
        
        this.isAlive = entityType.isAlive();
        this.isAgeable = Age.isAgeable(entityType);
        this.isTameable = isTameable(entityType);
        this.isAngryable = isAngryable(entityType);
        this.material = getMaterialFromEntityType(entityType);
//</editor-fold>
    }
    private ExtendedEntityType(ExtraEntityType extraEntityType){
        //<editor-fold defaultstate="collapsed" desc="Code">
        this.entityType = null;
        this.extraEntityType = extraEntityType;
        
        this.isAlive = extraEntityType.isAlive;
        this.isAgeable = extraEntityType.isAgeable;
        this.isTameable = extraEntityType.isTameable;
        this.isAngryable = false; 
        this.material = extraEntityType.material;
//</editor-fold>
    }
    
    private final EntityType entityType;
    private final ExtraEntityType extraEntityType;
    
    private final boolean isAlive;
    private final boolean isAgeable;
    private final boolean isTameable;
    private final boolean isAngryable;
    private final XMaterial material;
    
    public Entity spawnEntity(Location location){
        //<editor-fold defaultstate="collapsed" desc="Code">
        World world = location.getWorld();
        if(entityType != null){
            return world.spawnEntity(location, entityType);
        }else if(extraEntityType != null){
            return extraEntityType.spawnEntity(location);
        }else{
            throw new NullPointerException("ExtendedEntityType not initialized yet");
        }
//</editor-fold>
    }
    
    public boolean isAlive(){
        return isAlive;
    }
    
    public boolean isAgeable(){
        return isAgeable;
    }
    
    public boolean isTameable(){
        return isTameable;
    }
    
    public boolean isAngryable() {
        return isAngryable;
    }
    
    public XMaterial getMaterial(){
        return material;
    }
    
    public boolean isSlime(){
        return entityType == EntityType.SLIME
                || entityType == EntityType.MAGMA_CUBE;
    }
    
    public boolean isOcelot(){
        return entityType == EntityType.OCELOT;
    }
    
    private static Method setAnger = null;
    
    public void setAngry(Entity entity, Player player) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(this.isAngryable()) {
            if(entity instanceof Wolf) {
                ((Wolf) entity).setAngry(true);
                ((Wolf) entity).setTarget(player);
            } else if(NewAmazingLuckyBlocks.getMinecraftVersion().compareTo(MinecraftVersion.v1_15) >= 0) {
                if(!this.name().equals("BEE")) {
                    return;
                }
                
                // Bees appeared for the first time in Minecraft 1.15
                try {
                    if(setAnger == null) {
                        setAnger = entity.getClass().getMethod("setAnger", int.class);
                    }
                    
                    setAnger.invoke(entity, Integer.MAX_VALUE);
                    ((Creature) entity).setTarget(player);
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
//</editor-fold>
    }
    
    public String name(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(entityType != null){
            return entityType.name();
        }else if(extraEntityType != null){
            return extraEntityType.name();
        }else{
            throw new NullPointerException("ExtendedEntityType not initialized yet");
        }
//</editor-fold>
    }
    
    @Override
    public String toString(){
        return name();
    }
}
