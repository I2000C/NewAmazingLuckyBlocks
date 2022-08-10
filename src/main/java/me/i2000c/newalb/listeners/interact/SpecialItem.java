package me.i2000c.newalb.listeners.interact;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.lang_utils.LangLoader;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.logger.Logger;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public abstract class SpecialItem{
    protected static final String METADATA_TAG = "NewAmazingLuckyBlocks.MetadataTAG";
    protected static final String ITEM_TAG = "NewAmazingLuckyBlocks.SpecialItem";
    private final String itemPathKey;
    private Map<UUID, Long> cooldownMap;
    
    private ItemStack item;
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public SpecialItem(){
        if(getSpecialItemName().isWand()){
            itemPathKey = "Wands." + this.getClass().getSimpleName();
        }else{
            itemPathKey = "Objects." + this.getClass().getSimpleName();
        }
        
        cooldownMap = null;
    }
    
    public ItemStack getItem(){
        return this.item.clone();
    }
    
    public void loadItem(){
        this.item = buildItem();
        this.item = setSpecialItemID(this.item);
        this.clearCooldownMap();
        PlayerInteractListener.registerSpecialtem(this);
    }
    
    public boolean checkPermission(Player player){
        boolean requiredPermission = ConfigManager.getConfig().getBoolean(itemPathKey + ".required-permission");
        String permission = ConfigManager.getConfig().getString(itemPathKey + ".permission");
        
        if(requiredPermission && !player.hasPermission(permission)){
            Logger.sendMessage(LangLoader.getMessages().get("need-permission"), player);
            return false;
        }else{
            return true;
        }
    }
    
    public String getDisplayName(){
        return LangLoader.getMessages().getString(this.itemPathKey + ".name");
    }
    
    protected abstract ItemStack buildItem();
    
    public abstract void onPlayerInteract(PlayerInteractEvent e);
    
    public abstract SpecialItemName getSpecialItemName();
    
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
        String message = LangLoader.getMessages().getString("Cooldown-message");
        message = message.replace("%time%", String.valueOf(getRemainingSeconds(player)));
        Logger.sendMessage(message, player, false);
    }
    
    protected void decreaseAmountOfItem(PlayerInteractEvent e){
        ItemStack itemInHand = e.getItem();
        int amount = itemInHand.getAmount() - 1;
        if(amount == 0){
            e.getPlayer().setItemInHand(null);
        }else{
            itemInHand.setAmount(amount);
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
    
    // Metadata methods
    protected void addMetadata(Entity entity){
        Plugin plugin = NewAmazingLuckyBlocks.getInstance();
        entity.setMetadata(METADATA_TAG, new FixedMetadataValue(plugin, this));
    }
    
    protected static SpecialItem getMetadata(Entity entity){
        if(entity.hasMetadata(METADATA_TAG)){
            Object value = entity.getMetadata(METADATA_TAG).get(0).value();
            if(value instanceof SpecialItem){
                return (SpecialItem) value;
            }
        }
        
        return null;
    }
    
    // Overridable events
    public abstract void onPlayerInteract(PlayerInteractEvent e);
    
    public void onItemPickup(PlayerPickupItemEvent e){}
}
