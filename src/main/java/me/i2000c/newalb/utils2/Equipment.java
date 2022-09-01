package me.i2000c.newalb.utils2;

import java.util.List;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.textures.InvalidTextureException;
import me.i2000c.newalb.utils.textures.Texture;
import me.i2000c.newalb.utils.textures.TextureException;
import me.i2000c.newalb.utils.textures.URLTextureException;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public final class Equipment implements Cloneable{
    public static final String[] EQUIPMENT_KEYS = {
        "Helmet", "Chestplate", "Leggings", "Boots", "ItemInHand"};
    
    public static final int HELMET_ID = 0;
    public static final int CHESTPLATE_ID = 1;
    public static final int LEGGINGS_ID = 2;
    public static final int BOOTS_ID = 3;
    public static final int ITEM_IN_HAND_ID = 4;
    
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack itemInHand;
    
    private int helmetDropChance;
    private int chestplateDropChance;
    private int leggingsDropChance;
    private int bootsDropChance;
    private int itemInHandDropChance;

    public Equipment(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        reset();
//</editor-fold>
    }
    
    public ItemStack getEquipmentItem(int slot){
        //<editor-fold defaultstate="collapsed" desc="Code">
        switch(slot){
            case HELMET_ID: return helmet;
            case CHESTPLATE_ID: return chestplate;
            case LEGGINGS_ID: return leggings;
            case BOOTS_ID: return boots;
            case ITEM_IN_HAND_ID: return itemInHand;
            default: throw new IllegalArgumentException("Invalid equipment slot: " + slot);
        }
//</editor-fold>
    }
    public void setEquipmentItem(int slot, ItemStack stack){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(stack != null && stack.getType() == Material.AIR){
            stack = null;
        }
        
        switch(slot){
            case HELMET_ID:
                if(stack != helmet){
                    if(stack == null){
                        helmet = null;
                    }else{
                        helmet = stack.clone();
                    }
                }
                break;
            case CHESTPLATE_ID:
                if(stack != chestplate){
                    if(stack == null){
                        chestplate = null;
                    }else{
                        chestplate = stack.clone();
                    }
                }
                break;
            case LEGGINGS_ID:
                if(stack != leggings){
                    if(stack == null){
                        leggings = null;
                    }else{
                        leggings = stack.clone();
                    }
                }
                break;
            case BOOTS_ID:
                if(stack != boots){
                    if(stack == null){
                        boots = null;
                    }else{
                        boots = stack.clone();
                    }
                }
                break;
            case ITEM_IN_HAND_ID:
                if(stack != itemInHand){
                    if(stack == null){
                        itemInHand = null;
                    }else{
                        itemInHand = stack.clone();
                    }
                }
                break;
            default: throw new IllegalArgumentException("Invalid equipment slot: " + slot);
        }
//</editor-fold>
    }
    public int getEquipmentDropChance(int slot){
        //<editor-fold defaultstate="collapsed" desc="Code">
        switch(slot){
            case HELMET_ID: return helmetDropChance;
            case CHESTPLATE_ID: return chestplateDropChance;
            case LEGGINGS_ID: return leggingsDropChance;
            case BOOTS_ID: return bootsDropChance;
            case ITEM_IN_HAND_ID: return itemInHandDropChance;
            default: throw new IllegalArgumentException("Invalid equipment slot: " + slot);
        }
//</editor-fold>
    }
    public void setEquipmentDropChance(int slot, int dropChance){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(dropChance < 0 || dropChance > 100){
            throw new IllegalArgumentException("Invalid drop chance: " + dropChance);
        }
        
        switch(slot){
            case HELMET_ID: helmetDropChance = dropChance; break;
            case CHESTPLATE_ID: chestplateDropChance = dropChance; break;
            case LEGGINGS_ID: leggingsDropChance = dropChance; break;
            case BOOTS_ID: bootsDropChance = dropChance; break;
            case ITEM_IN_HAND_ID: itemInHandDropChance = dropChance; break;
            default: throw new IllegalArgumentException("Invalid equipment slot: " + slot);
        }
//</editor-fold>
    }
    
    public Equipment(FileConfiguration config, String path){
        //<editor-fold defaultstate="collapsed" desc="Code">
        this();
        
        if(!config.isConfigurationSection(path)){
            return;
        }
        
        for(int i=0; i<EQUIPMENT_KEYS.length; i++){
            String fullPath = path + "." + EQUIPMENT_KEYS[i];
            if(config.contains(fullPath)){
                ItemBuilder builder = ItemBuilder.newItem(config.getString(fullPath + ".material"));
                
                short durability = (short) config.getInt(fullPath + ".durability");
                int amount = config.getInt(fullPath + ".amount", 1);
                
                builder.withAmount(amount);
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
                
                int dropChance = config.getInt(fullPath + ".dropChance", 50);
                
                setEquipmentItem(i, builder.build());
                setEquipmentDropChance(i, dropChance);
            }
        }
//</editor-fold>
    }
    
    public void saveToConfig(FileConfiguration config, String path){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!isEmpty()){
            for(int i=0; i<EQUIPMENT_KEYS.length; i++){
                ItemStack stack = getEquipmentItem(i);
                int dropChance = getEquipmentDropChance(i);
                
                if(stack != null && stack.getType() != Material.AIR){
                    String fullPath = path + "." + EQUIPMENT_KEYS[i];
                    ItemBuilder builder = ItemBuilder.fromItem(stack, false);
                    config.set(fullPath + ".material", builder.toString());
                    config.set(fullPath + ".durability", builder.getDurability());
                    config.set(fullPath + ".amount", builder.getAmount());
                    
                    if(builder.hasDisplayName()){
                        config.set(fullPath + ".name", Logger.deColor(builder.getDisplayName()));
                    }
                    if(builder.hasLore()){
                        config.set(fullPath + ".lore", Logger.deColor(builder.getLore()));
                    }
                    if(builder.hasEnchantments()){
                        config.set(fullPath + ".enchantments", builder.getEnchantmentsIntoStringList());
                    }
                    Texture texture = builder.getTexture();
                    if(texture != null){
                        config.set(fullPath + ".textureID", texture.getID());
                    }
                    
                    config.set(fullPath + ".dropChance", dropChance);
                }
            }
        }
//</editor-fold>
    }

    public boolean isEmpty(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return helmet == null
                && chestplate == null
                && leggings == null
                && boots == null
                && itemInHand == null;
//</editor-fold>
    }
    
    public void applyToEntity(LivingEntity le){
        //<editor-fold defaultstate="collapsed" desc="Code">
        EntityEquipment equipment = le.getEquipment();
        if(helmet != null){
            equipment.setHelmet(helmet.clone());
            if(le.getType() != EntityType.ARMOR_STAND){
                equipment.setHelmetDropChance(helmetDropChance / 100f);
            }            
        }
        if(chestplate != null){
            equipment.setChestplate(chestplate.clone());
            if(le.getType() != EntityType.ARMOR_STAND){
                equipment.setChestplateDropChance(chestplateDropChance / 100f);
            }
        }
        if(leggings != null){
            equipment.setLeggings(leggings.clone());
            if(le.getType() != EntityType.ARMOR_STAND){
                equipment.setLeggingsDropChance(leggingsDropChance / 100f);
            }
        }
        if(boots != null){
            equipment.setBoots(boots.clone());
            if(le.getType() != EntityType.ARMOR_STAND){
                equipment.setBootsDropChance(bootsDropChance / 100f);
            }
        }
        if(itemInHand != null){
            equipment.setItemInHand(itemInHand.clone());
            if(le.getType() != EntityType.ARMOR_STAND){
                equipment.setItemInHandDropChance(itemInHandDropChance / 100f);
            }else{
                ArmorStand armorStand = (ArmorStand) le;
                armorStand.setArms(true);
                armorStand.setBasePlate(false);
            }
        }
//</editor-fold>
    }

    public void reset(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        for(int i=0; i<EQUIPMENT_KEYS.length; i++){
            setEquipmentItem(i, null);
            setEquipmentDropChance(i, 50);
        }
//</editor-fold>
    }
    public void resetEquipmentItems(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        for(int i=0; i<EQUIPMENT_KEYS.length; i++){
            setEquipmentItem(i, null);
        }
//</editor-fold>
    }
    public void resetEquipmentDropChances(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        for(int i=0; i<EQUIPMENT_KEYS.length; i++){
            setEquipmentDropChance(i, 50);
        }
//</editor-fold>
    }

    @Override
    @SuppressWarnings("CloneDeclaresCloneNotSupported")
    public Equipment clone(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        try{
            Equipment copy = (Equipment) super.clone();
            
            if(this.helmet != null){
                copy.helmet = this.helmet.clone();
            }
            if(this.chestplate != null){
                copy.chestplate = this.chestplate.clone();
            }
            if(this.leggings != null){
                copy.leggings = this.leggings.clone();
            }
            if(this.boots != null){
                copy.boots = this.boots.clone();
            }
            if(this.itemInHand != null){
                copy.itemInHand = this.itemInHand.clone();
            }
            
            return copy;
        }catch(CloneNotSupportedException ex){
            return null;
        }
//</editor-fold>
    }
}
