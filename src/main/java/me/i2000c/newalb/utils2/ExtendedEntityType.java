package me.i2000c.newalb.utils2;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Zombie;

public class ExtendedEntityType{
    private static enum ExtraEntityType{
        //<editor-fold defaultstate="collapsed" desc="Code">
        DONKEY(true, true, true, XMaterial.HORSE_SPAWN_EGG),
        MULE(true, true, true, XMaterial.HORSE_SPAWN_EGG),
        SKELETON_HORSE(true, true, true, XMaterial.SKELETON_SPAWN_EGG),
        ZOMBIE_HORSE(true, true, true, XMaterial.ZOMBIE_SPAWN_EGG),
        STRAY(false, false, true, XMaterial.SKELETON_SPAWN_EGG),
        WITHER_SKELETON(false, false, true, XMaterial.WITHER_SKELETON_SKULL);
        
        public boolean isTameable;
        public boolean isAgeable;
        public boolean isAlive;
        public XMaterial material;
        
        private ExtraEntityType(
                boolean isTameable, 
                boolean isAgeable, 
                boolean isAlive, 
                XMaterial material){
            this.isTameable = isTameable;
            this.isAgeable = isAgeable;
            this.isAlive = isAlive;
            this.material = material;
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
            if(minecraftVersion.compareTo(MinecraftVersion.v1_11) < 0){
                for(ExtraEntityType extraEntityType : ExtraEntityType.values()){
                    // Stray skeletons appeared for the first time in Minecraft 1.10
                    if(extraEntityType == ExtraEntityType.STRAY
                            && minecraftVersion.compareTo(MinecraftVersion.v1_10) != 0){
                        continue;
                    }
                    
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
            eet = new ExtendedEntityType(EntityType.valueOf(string));
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
        this.material = extraEntityType.material;
//</editor-fold>
    }
    
    private final EntityType entityType;
    private final ExtraEntityType extraEntityType;
    
    private final boolean isAlive;
    private final boolean isAgeable;
    private final boolean isTameable;
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
