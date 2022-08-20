package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.List;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.RewardType;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.listeners.interact.SpecialItemManager;
import me.i2000c.newalb.utils.logger.LogLevel;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils.textures.InvalidTextureException;
import me.i2000c.newalb.utils.textures.Texture;
import me.i2000c.newalb.utils.textures.TextureException;
import me.i2000c.newalb.utils.textures.URLTextureException;
import me.i2000c.newalb.utils2.CustomColor;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItemReward extends Reward{
    public static final int SURVIVAL_INV_SIZE = 36;    
    
    public static final int HELMET_SLOT = 36;
    public static final int CHESTPLATE_SLOT = 37;
    public static final int LEGGINGS_SLOT = 38;
    public static final int BOOTS_SLOT = 39;
    public static final int ITEM_IN_HAND_SLOT = 40;
    public static final int ITEM_IN_OFF_HAND_SLOT = 41;
    
    public static int getMaxSlot(){
        if(NewAmazingLuckyBlocks.getMinecraftVersion().compareTo(MinecraftVersion.v1_9) >= 0){
            return ITEM_IN_OFF_HAND_SLOT;
        }else{
            return ITEM_IN_OFF_HAND_SLOT - 1;
        }
    }
    
    private ItemStack item;
    private ItemSpawnMode spawnMode;
    private int spawnInvSlot;
    
    public ItemReward(Outcome outcome){
        super(outcome);
        item = null;
        spawnMode = ItemSpawnMode.DEFAULT;
        spawnInvSlot = 1;
    }
    
    public ItemStack getItem(){
        return this.item;
    }
    public void setItem(ItemStack sk){
        this.item = sk;
    }
    
    public ItemSpawnMode getSpawnMode(){
        return this.spawnMode;
    }
    public void setSpawnMode(ItemSpawnMode spawnMode){
        this.spawnMode = spawnMode;
    }
    
    public int getSpawnInvSlot(){
        return this.spawnInvSlot;
    }
    public void setSpawnInvSlot(int spawnInvSlot){
        this.spawnInvSlot = spawnInvSlot;
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        return getItem().clone();
    }
    
    @Override
    public void loadRewardFromConfig(FileConfiguration config, String path){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String spawnModeString = config.getString(path + ".spawnMode");
        spawnMode = spawnModeString != null ? 
                ItemSpawnMode.valueOf(spawnModeString) : 
                ItemSpawnMode.DEFAULT;
        spawnInvSlot = config.getInt(path + ".spawnInvSlot", 0);
        
        Material material = Material.valueOf(config.getString(path + ".material"));
        int amount = config.getInt(path + ".amount", 1);
        short durability = (short) config.getInt(path + ".durability", 0);
        
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.matchXMaterial(material));
        builder.withAmount(amount);
        builder.withDurability(durability);
        
        if(config.contains(path + ".name")){
            String displayName = config.getString(path + ".name");
            builder.withDisplayName(displayName);
        }
        if(config.contains(path + ".lore")){
            List<String> lore = config.getStringList(path + ".lore");
            builder.withLore(lore);
        }
        if(config.contains(path + ".enchantments")){
            List<String> enchantments = config.getStringList(path + ".enchantments");
            builder.withEnchantments(enchantments);
        }
        
        //Load armor color
        switch(material){
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                if(config.contains(path + ".armorColor")){
                    String hexColor = config.getString(path + ".armorColor");
                    CustomColor color = new CustomColor(hexColor);
                    builder.withColor(color.getBukkitColor());
                }
                break;
        }
        
        //Load potion meta
        PotionSplashType type = PotionSplashType.getFromPotion(builder.build());
        if(type != null){
            if(config.contains(path + ".potionSplashType")){
                type = PotionSplashType.valueOf(config.getString(path + ".potionSplashType"));
                type.setToPotion(builder.build());
            }
            if(config.contains(path + ".potionEffects")){
                List<String> potionEffects = config.getStringList(path + ".potionEffects");
                potionEffects.forEach(string -> {
                    String[] splitted = string.split(";");
                    String name = splitted[0];
                    int duration = Integer.parseInt(splitted[1]) * 20;
                    int amplifier = Integer.parseInt(splitted[2]);
                    
                    if(duration < 0){
                        duration = Integer.MAX_VALUE;
                    }
                    if(amplifier < 0){
                        amplifier = 0;
                    }
                    
                    builder.addPotionEffect(new PotionEffect(PotionEffectType.getByName(name), duration, amplifier));
                });
            }
            if(NewAmazingLuckyBlocks.getMinecraftVersion().compareTo(MinecraftVersion.v1_11) >= 0){
                if(config.contains(path + ".potionColor")){
                    String hexColor = config.getString(path + ".potionColor");
                    CustomColor color = new CustomColor(hexColor);
                    builder.withColor(color.getBukkitColor());
                }
            }
        }
        
        //Load texture ID
        if(config.contains(path + ".textureID")){
            String textureID = config.getString(path + ".textureID");
            try{
                Texture texture = new Texture(textureID);
                builder.withTexture(texture);
            }catch(InvalidTextureException ex){
                Logger.log("ItemReward at \"" + path + "\" contains an invalid HeadTexture", 
                            LogLevel.WARN);
            }catch(URLTextureException ex){
                Logger.log("An error occurred while loading texture for ItemReward at \"" + path + "\":",
                            LogLevel.ERROR);
                Logger.log(ex, LogLevel.ERROR);
            }catch(TextureException ex){}
        }
        
        this.item = builder.build();
//</editor-fold>
    }
    
    @Override
    public void saveRewardIntoConfig(FileConfiguration config, String path){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemBuilder builder = ItemBuilder.fromItem(item, false);
        
        config.set(path + ".material" , item.getType().name());
        
        config.set(path + ".spawnMode", spawnMode.name());
        config.set(path + ".spawnInvSlot", spawnInvSlot);
        
        config.set(path + ".amount" , builder.getAmount());
        config.set(path + ".durability" , builder.getDurability());
        
        if(builder.hasDisplayName()){
            config.set(path + ".name", Logger.deColor(builder.getDisplayName()));
        }
        if(builder.hasLore()){
            config.set(path + ".lore", Logger.deColor(builder.getLore()));
        }
        if(builder.hasEnchantments()){
            config.set(path + ".enchantments", builder.getEnchantmentsIntoStringList());
        }

        //Save texture
        Texture texture = builder.getTexture();
        if(texture != null){
            config.set(path + ".textureID", texture.getID());
        }

        //Save leather armor color
        switch(this.item.getType()){
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                String hexColor = new CustomColor(builder.getColor()).getHexColorString();
                config.set(path + ".armorColor", hexColor);
        }

        PotionSplashType type = PotionSplashType.getFromPotion(this.item);
        if(type != null){
            config.set(path + ".material" , "POTION");
            config.set(path + ".potionSplashType", type.name());   
            if(NewAmazingLuckyBlocks.getMinecraftVersion().compareTo(MinecraftVersion.v1_11) >= 0){
                if(builder.hasColor()){
                    String hexColor = new CustomColor(builder.getColor()).getHexColorString();
                    config.set(path + ".potionColor", hexColor);
                }
            }
            List<String> effectList = new ArrayList<>();
            builder.getPotionEffects().forEach(potionEffect -> {
                String name = potionEffect.getType().getName();
                int duration = potionEffect.getDuration() / 20;
                int amplifier = potionEffect.getAmplifier();

                if(duration < 0){
                    duration = 0;
                }
                if(amplifier < 0){
                    amplifier = 0;
                }

                effectList.add(name + ";" + duration + ";" + amplifier);
            });

            config.set(path + ".potionEffects", effectList);
        }
//</editor-fold>
    }
    
    @Override
    public void execute(Player player, Location location){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemStack stack = null;
        String displayName = ItemBuilder.fromItem(item, false).getDisplayName();
        if(displayName != null){
            String name = Logger.stripColor(displayName);
            if(name.charAt(0) == '%' && name.charAt(name.length()-1) == '%'){
                SpecialItem specialItem = SpecialItemManager.getSpecialItem(name);
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
            Logger.log("Spawn slot (" + slot + ") cannot be negative in ItemReward", LogLevel.WARN);
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
                    if(NewAmazingLuckyBlocks.getMinecraftVersion().compareTo(MinecraftVersion.v1_9) >= 0){
                        return equipment.getItemInOffHand();
                    }else{
                        Logger.log("Invalid slot (" + slot + "). There is no off-hand in Minecraft 1.8", LogLevel.WARN);
                        return null;
                    }
                default:
                    Logger.log("Invalid slot (" + slot + "). It must be less than or equal to " + getMaxSlot(), LogLevel.WARN);
                    return null;
            }
        }
//</editor-fold>
    }
    private static void setItemToPlayer(Player player, ItemStack stack, int slot){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(slot < 0){
            Logger.log("Spawn slot (" + slot + ") cannot be negative in ItemReward", LogLevel.WARN);
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
                    if(NewAmazingLuckyBlocks.getMinecraftVersion().compareTo(MinecraftVersion.v1_9) >= 0){
                        equipment.setItemInOffHand(stack);
                    }else{
                        Logger.log("Invalid slot (" + slot + "). There is no off-hand in Minecraft 1.8", LogLevel.WARN);
                    }
                    break;
                default:
                    Logger.log("Invalid slot (" + slot + "). It must be less than or equal to " + getMaxSlot(), LogLevel.WARN);
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
        return copy;
    }
    
    public static enum PotionSplashType{
        //<editor-fold defaultstate="collapsed" desc="Code">
        NORMAL,
        SPLASH,
        LINGERING;
        
        public static PotionSplashType getFromPotion(ItemStack stack){
            if(NewAmazingLuckyBlocks.getMinecraftVersion() == MinecraftVersion.v1_8){
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
            if(NewAmazingLuckyBlocks.getMinecraftVersion() == MinecraftVersion.v1_8){
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
            if(NewAmazingLuckyBlocks.getMinecraftVersion() == MinecraftVersion.v1_8){
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
