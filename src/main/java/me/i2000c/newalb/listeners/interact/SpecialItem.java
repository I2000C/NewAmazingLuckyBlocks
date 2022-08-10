package me.i2000c.newalb.listeners.interact;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.i2000c.newalb.lang_utils.LangLoader;
import me.i2000c.newalb.listeners.interact.PlayerInteractListener;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public abstract class SpecialItem{
    private final String itemPathKey;
    private final Map<UUID, Long> cooldownMap;
    
    private ItemStack item;
    private int id = -1;    
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public SpecialItem(){
        if(getSpecialItemName().isWand()){
            itemPathKey = "Wands." + this.getClass().getSimpleName();
        }else{
            itemPathKey = "Objects." + this.getClass().getSimpleName();
        }
        
        cooldownMap = new HashMap<>();
    }
    
    public ItemStack getItem(){
        return this.item.clone();
    }
    
    public void loadItem(){
        this.item = buildItem();
        if(id == -1){
            id = PlayerInteractListener.registerSpecialtem(this);
        }
        
        this.item = PlayerInteractListener.setSpecialtemID(this.item, id);
        this.cooldownMap.clear();
    }
    
    public String getPermissionPath(){
        return this.itemPathKey;
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
        cooldownMap.put(player.getUniqueId(), System.currentTimeMillis() + cooldownSeconds*1000);
    }
    
    protected boolean isCooldownExpired(Player player){
        if(cooldownMap.isEmpty()){
            return false;
        }else{
            long cooldownValue = cooldownMap.getOrDefault(player, 0L);
            return System.currentTimeMillis() > cooldownValue;
        }            
    }
    
    protected long getRemainingSeconds(Player player){
        long cooldownValue = cooldownMap.getOrDefault(player, 0L);
        long remainingTime = cooldownValue - System.currentTimeMillis();
        if(remainingTime < 0){
            remainingTime = 0;
        }
        return remainingTime / 1000L;
    }
    
    protected void sendRemainingSecondsMessage(Player player){
        String message = LangLoader.getMessages().getString("Cooldown-messsage");
        message = message.replace("%time%", String.valueOf(getRemainingSeconds(player)));
        Logger.sendMessage(message, player, false);
    }
}
