package me.i2000c.newalb.listeners.interact;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.LangConfig;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public abstract class SpecialItem{
    protected static final SpecialItemName[] SPECIAL_ITEM_NAMES = SpecialItemName.values();
    protected static final String CLASS_METADATA_TAG = "NewAmazingLuckyBlocks.ClassMetadata";
    protected static final String CUSTOM_METADATA_TAG = "NewAmazingLuckyBlocks.CustomMetadata";
    protected static final String ITEM_TAG = "NewAmazingLuckyBlocks.SpecialItem";
    private final String itemPathKey;
    private final SpecialItemName specialItemName;
    private Map<UUID, Long> cooldownMap;
    
    private ItemStack item;
    
    public SpecialItem(){
        String className = this.getClass().getSimpleName();
        
        String[] splitted_name = className.split("(?=[A-Z])");
        StringBuilder stringBuilder = new StringBuilder();
        for(String string : splitted_name){
            stringBuilder.append(string.toLowerCase()).append('_');
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        specialItemName = SpecialItemName.valueOf(stringBuilder.toString());
        
        if(specialItemName.isWand()){
            itemPathKey = "Wands." + className;
        }else{
            itemPathKey = "Objects." + className;
        }
        
        cooldownMap = null;
    }
    
    public final SpecialItemName getSpecialItemName(){
        return this.specialItemName;
    }
    
    public final ItemStack getItem(){
        return this.item.clone();
    }
    
    public final void loadItem(){
        this.item = buildItem();
        this.item = setSpecialItemID(this.item);
        this.clearCooldownMap();
        SpecialEventListener.registerSpecialtem(this);
    }
    
    public final boolean checkPermission(Player player){
        boolean requiredPermission = ConfigManager.getConfig().getBoolean(itemPathKey + ".required-permission");
        String permission = ConfigManager.getConfig().getString(itemPathKey + ".permission");
        
        if(requiredPermission && !player.hasPermission(permission)){
            Logger.sendMessage(LangConfig.getMessages().get("need-permission"), player, false);
            return false;
        }else{
            return true;
        }
    }
    
    public final String getDisplayName(){
        return LangConfig.getMessages().getString(this.itemPathKey + ".name");
    }
    
    protected abstract ItemStack buildItem();
    
    // Cooldown map methods
    protected void updatePlayerCooldown(Player player){
        int cooldownSeconds = ConfigManager.getConfig().getInt(itemPathKey + ".cooldown-time");
        if(cooldownSeconds <= 0){
            return;
        }
        
        if(cooldownMap == null){
            cooldownMap = new HashMap<>();
        }
        
        cooldownMap.put(player.getUniqueId(), System.currentTimeMillis() + cooldownSeconds*1000);
    }
    
    protected boolean isCooldownExpired(Player player){
        if(cooldownMap == null){
            return true;
        }
        
        long cooldownValue = cooldownMap.getOrDefault(player.getUniqueId(), 0L);
        return System.currentTimeMillis() > cooldownValue;
    }
    
    protected void clearCooldownMap(){
        if(cooldownMap != null){
            cooldownMap.clear();
        }
    }
    
    protected int getRemainingSeconds(Player player){
        if(cooldownMap == null){
            return 0;
        }
        
        long cooldownValue = cooldownMap.getOrDefault(player.getUniqueId(), 0L);
        long remainingTime = cooldownValue - System.currentTimeMillis();
        if(remainingTime < 0){
            remainingTime = 0;
        }
        return (int)remainingTime / 1000;
    }
    
    protected void sendRemainingSecondsMessage(Player player){
        String message = LangConfig.getMessages().getString("Cooldown-message");
        message = message.replace("%time%", String.valueOf(getRemainingSeconds(player)));
        Logger.sendMessage(message, player, false);
    }
    
    // Decrease amount of special item (Used in some objects)
    protected void decreaseAmountOfItem(PlayerInteractEvent e){
        ItemStack itemInHand = e.getItem();
        int amount = itemInHand.getAmount() - 1;
        if(amount == 0){
            e.getPlayer().setItemInHand(null);
        }else{
            itemInHand.setAmount(amount);
        }
    }
    
    private static String getChatcolorString(int uses){
        String intColor;  
        if(uses >= 10){
          intColor = "&a";  
        }else if(uses >= 5){
          intColor = "&6";    
        }else if(uses >= 0){
          intColor = "&4";    
        }else{
          intColor = "&8";    
        }
        return intColor;
    }
    protected List<String> getLoreOfWand(){
        List<String> lore = new ArrayList<>();        
        if(ConfigManager.getConfig().getBoolean(itemPathKey + ".limited-uses.enable")){
            int uses = ConfigManager.getConfig().getInt(itemPathKey + ".limited-uses.uses");
            lore.add("&5Uses left:");
            lore.add(getChatcolorString(uses) + uses);
        }        
        return lore;
    }
    protected boolean decreaseWandUses(ItemStack stack, Player player){
        ItemBuilder builder = ItemBuilder.fromItem(stack, false);
        if(ConfigManager.getConfig().getBoolean(itemPathKey + ".limited-uses.enable")){
            List<String> lore = builder.getLore();
            if(lore == null){
                builder.withLore(getLoreOfWand());
                return true;
            }else{
                int usesLeft = Integer.parseInt(Logger.stripColor(lore.get(1))) - 1;
                if(usesLeft >= 0){
                    lore.set(1, getChatcolorString(usesLeft) + usesLeft);
                    builder.withLore(lore);
                    return true;
                }else{
                    Logger.sendMessage("&cThis wand has expired", player, false);
                    return false;
                }                
            }
        }else{
            builder.withLore();
            return true;
        }
    }
    
    // Special item ID methods
    private ItemStack setSpecialItemID(ItemStack stack){
        return NBTEditor.set(stack, getSpecialItemName().ordinal(), ITEM_TAG);
    }
    protected static int getSpecialItemID(ItemStack stack){
        if(NBTEditor.contains(stack, ITEM_TAG)){
            return NBTEditor.getInt(stack, ITEM_TAG);
        }else{
            return -1;
        }
    }
    protected static boolean hasSpecialItemID(ItemStack stack){
        return NBTEditor.contains(stack, ITEM_TAG);
    }
    public static SpecialItemName getSpecialItemName(ItemStack stack){
        int specialItemID = getSpecialItemID(stack);
        if(specialItemID >= 0 && specialItemID < SPECIAL_ITEM_NAMES.length){
            return SPECIAL_ITEM_NAMES[specialItemID];
        }else{
            return null;
        }
    }
    
    // Metadata methods
    protected void setClassMetadata(Entity entity){
        Plugin plugin = NewAmazingLuckyBlocks.getInstance();
        entity.setMetadata(CLASS_METADATA_TAG, new FixedMetadataValue(plugin, this));
    }    
    protected static SpecialItem getClassMetadata(Entity entity){
        if(entity.hasMetadata(CLASS_METADATA_TAG)){
            Object value = entity.getMetadata(CLASS_METADATA_TAG).get(0).value();
            if(value instanceof SpecialItem){
                return (SpecialItem) value;
            }
        }        
        return null;
    }
    protected static boolean hasClassMetadata(Entity entity){
        return entity.hasMetadata(CLASS_METADATA_TAG) &&
                !entity.getMetadata(CLASS_METADATA_TAG).isEmpty();
    }
    protected static void removeClassMetadata(Entity entity){
        entity.removeMetadata(CLASS_METADATA_TAG, NewAmazingLuckyBlocks.getInstance());
    }
    
    protected void setCustomMetadata(Entity entity, Object value){
        Plugin plugin = NewAmazingLuckyBlocks.getInstance();
        entity.setMetadata(CUSTOM_METADATA_TAG, new FixedMetadataValue(plugin, value));
    }
    protected static Object getCustomMetadata(Entity entity){
        if(entity.hasMetadata(CUSTOM_METADATA_TAG)){
            Object value = entity.getMetadata(CUSTOM_METADATA_TAG).get(0).value();
            return value;
        }        
        return null;
    }
    protected static boolean hasCustomMetadata(Entity entity){
        return entity.hasMetadata(CUSTOM_METADATA_TAG);
    }
    protected static void removeCustomMetadata(Entity entity){
        entity.removeMetadata(CUSTOM_METADATA_TAG, NewAmazingLuckyBlocks.getInstance());
    }
    
    // Overridable events
    public void onPlayerInteract(PlayerInteractEvent e){}
    
    public void onItemPickup(PlayerPickupItemEvent e){}
    
    public void onArrowHit(CustomProjectileHitEvent e){}
    
    public void onEntityDamaged(EntityDamageByEntityEvent e){}
    
    public void onArrowShooted(EntityShootBowEvent e){}
    
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e){}
    
    public void onFallingBlockConvert(EntityChangeBlockEvent e){}
}
