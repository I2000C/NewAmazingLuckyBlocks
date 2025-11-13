package me.i2000c.newalb.lucky_blocks.rewards.types;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;

import lombok.Getter;
import lombok.Setter;
import me.i2000c.newalb.api.version.MinecraftVersion;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.listeners.interact.SpecialItems;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.Reward;
import me.i2000c.newalb.lucky_blocks.rewards.RewardType;
import me.i2000c.newalb.utils.locations.Offset;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

@Getter
@Setter
public class ItemReward extends Reward{
    public static final int SURVIVAL_INV_SIZE = 36;    
    
    public static final int HELMET_SLOT = 36;
    public static final int CHESTPLATE_SLOT = 37;
    public static final int LEGGINGS_SLOT = 38;
    public static final int BOOTS_SLOT = 39;
    public static final int ITEM_IN_HAND_SLOT = 40;
    public static final int ITEM_IN_OFF_HAND_SLOT = 41;
    
    public static int getMaxSlot(){
        if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_9)){
            return ITEM_IN_OFF_HAND_SLOT;
        }else{
            return ITEM_IN_OFF_HAND_SLOT - 1;
        }
    }
    
    private ItemStack item;
    private ItemSpawnMode spawnMode;
    private int spawnInvSlot;
    private Offset offset;
    
    public ItemReward(Outcome outcome){
        super(outcome);
        item = null;
        spawnMode = ItemSpawnMode.DEFAULT;
        spawnInvSlot = 1;
        offset = new Offset();
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemStackWrapper builder = ItemStackWrapper.fromItem(item);
        
        builder.addLoreLine("");
        builder.addLoreLine("&bSpawn mode: &a" + spawnMode.name());
        builder.addLoreLine("&bSpawn inv slot: &a" + spawnInvSlot);
        builder.addLoreLine("&bOffset:");
        builder.addLoreLine("   &5X: &3" + offset.getOffsetX());
        builder.addLoreLine("   &5Y: &3" + offset.getOffsetY());
        builder.addLoreLine("   &5Z: &3" + offset.getOffsetZ());
        
        return builder.toItemStack();
//</editor-fold>
    }
    
    @Override
    public void loadRewardFromConfig(Config config, String path){
        //<editor-fold defaultstate="collapsed" desc="Code">
        item = config.getItemStackWrapper(path).toItemStack();
        spawnMode = config.getEnum(path + ".spawnMode", ItemSpawnMode.class, ItemSpawnMode.DEFAULT);
        spawnInvSlot = config.getInt(path + ".spawnInvSlot", 0);
        offset = new Offset(config, path + ".offset");
//</editor-fold>
    }
    
    @Override
    public void saveRewardIntoConfig(Config config, String path){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemStackWrapper wrapper = ItemStackWrapper.fromItem(item, false);
        config.set(path, wrapper);
        config.set(path + ".spawnMode", spawnMode);
        config.set(path + ".spawnInvSlot", spawnInvSlot);
        offset.saveToConfig(config, path + ".offset");
//</editor-fold>
    }
    
    @Override
    public void execute(Player player, Location location){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemStack stack = null;
        String displayName = ItemStackWrapper.fromItem(item, false).getDisplayName();
        if(displayName != null){
            String name = Logger.stripColor(displayName);
            if(name.charAt(0) == '%' && name.charAt(name.length()-1) == '%'){
                SpecialItem specialItem = SpecialItems.getByName(name.substring(1, name.length()-1));
                if(specialItem != null){
                    stack = specialItem.getItem();
                }
            }
        }
        
        if(stack == null){
            stack = item.clone();
        }else{
            // Set only amount and enchantments if the item is a special item
            stack.setAmount(item.getAmount());
            stack.addUnsafeEnchantments(item.getEnchantments());
        }
        
        location = offset.applyToLocation(location.clone());
        Inventory inv = player.getInventory();
        switch(spawnMode){
            case DEFAULT:
                location.getWorld().dropItemNaturally(location, stack);
                break;
            case ADD_TO_INV:
                if(!inv.addItem(stack).isEmpty()){
                    location.getWorld().dropItemNaturally(location, stack);
                }
                break;
            case SET_TO_INV:
                ItemStack invItem = getItemFromPlayer(player, spawnInvSlot);
                if(invItem != null && invItem.getType() != Material.AIR){
                    location.getWorld().dropItemNaturally(location, stack);
                }else{
                    setItemToPlayer(player, stack, spawnInvSlot);
                }
                break;
            case FORCE_SET_TO_INV:
                invItem = getItemFromPlayer(player, spawnInvSlot);
                if(invItem != null && invItem.getType() != Material.AIR){
                    Location playerLocation = player.getLocation();
                    playerLocation.getWorld().dropItemNaturally(playerLocation, invItem);
                }
                setItemToPlayer(player, stack, spawnInvSlot);
                break;
        }
//</editor-fold>
    }
    
    private static ItemStack getItemFromPlayer(Player player, int slot){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(slot < 0){
            Logger.warn("Spawn slot (" + slot + ") cannot be negative in ItemReward");
            return null;
        }
        
        if(slot < SURVIVAL_INV_SIZE){
            return player.getInventory().getItem(slot);
        }else{
            EntityEquipment equipment = player.getEquipment();
            switch(slot){
                case HELMET_SLOT: return equipment.getHelmet();
                case CHESTPLATE_SLOT: return equipment.getChestplate();
                case LEGGINGS_SLOT: return equipment.getLeggings();
                case BOOTS_SLOT: return equipment.getBoots();
                case ITEM_IN_HAND_SLOT: return equipment.getItemInHand();
                case ITEM_IN_OFF_HAND_SLOT:
                    if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_9)){
                        return equipment.getItemInOffHand();
                    }else{
                        Logger.warn("Invalid slot (" + slot + "). There is no off-hand in Minecraft 1.8");
                        return null;
                    }
                default:
                    Logger.warn("Invalid slot (" + slot + "). It must be less than or equal to " + getMaxSlot());
                    return null;
            }
        }
//</editor-fold>
    }
    private static void setItemToPlayer(Player player, ItemStack stack, int slot){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(slot < 0){
            Logger.warn("Spawn slot (" + slot + ") cannot be negative in ItemReward");
            return;
        }
        
        if(slot < SURVIVAL_INV_SIZE){
            player.getInventory().setItem(slot, stack);
        }else{
            EntityEquipment equipment = player.getEquipment();
            switch(slot){
                case HELMET_SLOT: equipment.setHelmet(stack); break;
                case CHESTPLATE_SLOT: equipment.setChestplate(stack); break;
                case LEGGINGS_SLOT: equipment.setLeggings(stack); break;
                case BOOTS_SLOT: equipment.setBoots(stack); break;
                case ITEM_IN_HAND_SLOT: equipment.setItemInHand(stack); break;
                case ITEM_IN_OFF_HAND_SLOT:
                    if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_9)){
                        equipment.setItemInOffHand(stack);
                    }else{
                        Logger.log("Invalid slot (" + slot + "). There is no off-hand in Minecraft 1.8");
                    }
                    break;
                default:
                    Logger.log("Invalid slot (" + slot + "). It must be less than or equal to " + getMaxSlot());
            }
        }
//</editor-fold>
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.item;
    }
    
    @Override
    public Reward clone(){
        ItemReward copy = (ItemReward) super.clone();
        copy.item = this.item.clone();
        copy.offset = this.offset.clone();
        return copy;
    }
    
    public static enum PotionSplashType{
        //<editor-fold defaultstate="collapsed" desc="Code">
        NORMAL,
        SPLASH,
        LINGERING;
        
        public static PotionSplashType getFromPotion(ItemStack stack){
            if(MinecraftVersion.CURRENT_VERSION.is_1_8()){
                if(stack.getType() != Material.POTION){
                    return null;
                }
                
                Potion potion = Potion.fromItemStack(stack);
                if(potion.isSplash()){
                    return SPLASH;
                }else{
                    return NORMAL;
                }
            }else switch(stack.getType()){
                case POTION:
                    return NORMAL;
                case SPLASH_POTION:
                    return SPLASH;
                case LINGERING_POTION:
                    return LINGERING;
                default:
                    return null;
            }
        }
        
        public static void clearPotionSplashType(ItemStack stack){
            NORMAL.setToPotion(stack);
        }
        
        public void setToPotion(ItemStack stack){
            if(MinecraftVersion.CURRENT_VERSION.is_1_8()){
                Potion potion = Potion.fromItemStack(stack);
                potion.setSplash(this != NORMAL);
                potion.apply(stack);
            }else{
                switch(this){
                    case NORMAL:
                        stack.setType(Material.POTION);
                        break;
                    case SPLASH:
                        stack.setType(Material.SPLASH_POTION);
                        break;
                    case LINGERING:
                        stack.setType(Material.LINGERING_POTION);
                        break;
                }
            }
        }
        
        public PotionSplashType next(){
            if(MinecraftVersion.CURRENT_VERSION.is_1_8()){
                return this == NORMAL ? SPLASH : NORMAL;
            }else{
                switch(this){
                    case NORMAL:
                        return SPLASH;
                    case SPLASH:
                        return LINGERING;
                    default:
                        return NORMAL;
                }
            }
        }
        
        @Override
        public String toString(){
            return name().toLowerCase();
        }
//</editor-fold>
    }
    
    public static enum ItemSpawnMode{
        //<editor-fold defaultstate="collapsed" desc="Code">
        DEFAULT,
        ADD_TO_INV,
        SET_TO_INV,
        FORCE_SET_TO_INV;
        
        private static final ItemSpawnMode[] vals = values();
        
        public ItemSpawnMode next(){
            return vals[(this.ordinal() + 1) % vals.length];
        }
//</editor-fold>
    }
}
