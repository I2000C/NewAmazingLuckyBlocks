package me.i2000c.newalb.listeners.interact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.LangConfig;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public abstract class SpecialItem {
    static final String ITEM_TAG = "NewAmazingLuckyBlocks.SpecialItem";    
    static final String CUSTOM_MODEL_DATA_TAG = "CustomModelData";
    
    protected final String itemPathKey;
    private Map<UUID, Long> cooldownMap;
    
    private final int id;
    private final String name;    
    private final boolean isWand;
    
    private ItemStack item;
    
    public SpecialItem(){
        String className = this.getClass().getSimpleName();
        id = SpecialItems.GLOBAL_ID++;
        name = className.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
        isWand = name.contains("wand");
        
        if(isWand){
            itemPathKey = "Wands." + className;
        }else{
            itemPathKey = "Objects." + className;
        }
        
        cooldownMap = null;
    }
    
    public final int getID() {
        return this.id;
    }
    
    public final String getName() {
        return this.name;
    }
    
    public final boolean isWand() {
        return this.isWand;
    }
    
    public final ItemStack getItem(){
        return this.item.clone();
    }
    
    public final void loadItem(){
        int customModelData = ConfigManager.getConfig().getInt(this.itemPathKey + ".custom-model-data");
        String displayName = LangConfig.getMessage(this.itemPathKey + ".name");
        
        this.item = ItemBuilder.fromItem(buildItem())
                        .withDisplayName(displayName)
                        .setNbtTag(this.id, ITEM_TAG)
                        .setNbtTag(customModelData, CUSTOM_MODEL_DATA_TAG)
                        .build();
        this.clearCooldownMap();
    }
    
    public final boolean checkPermission(Player player){
        boolean requiredPermission = ConfigManager.getConfig().getBoolean(itemPathKey + ".required-permission");
        String permission = ConfigManager.getConfig().getString(itemPathKey + ".permission");
        
        if(requiredPermission && !player.hasPermission(permission)){
            Logger.sendMessage(LangConfig.getMessage("need-permission"), player, false);
            return false;
        }else{
            return true;
        }
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
        String message = LangConfig.getMessage("Cooldown-message");
        message = message.replace("%time%", String.valueOf(getRemainingSeconds(player)));
        Logger.sendMessage(message, player, false);
    }
    
    // Decrease amount of special item (Used in some objects)
    protected void decreaseAmountOfItem(PlayerInteractEvent e){
        GameMode gamemode = e.getPlayer().getGameMode();
        if(gamemode == GameMode.SURVIVAL || gamemode == GameMode.ADVENTURE) {
            ItemStack itemInHand = e.getItem();
            int amount = itemInHand.getAmount() - 1;
            if(amount == 0){
                e.getPlayer().setItemInHand(null);
            }else{
                itemInHand.setAmount(amount);
            }
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
    // Decrease uses of wands
    protected boolean decreaseWandUses(ItemStack stack, Player player){
        ItemBuilder builder = ItemBuilder.fromItem(stack, false);
        if(ConfigManager.getConfig().getBoolean(itemPathKey + ".limited-uses.enable")){
            List<String> lore = builder.getLore();
            if(lore == null){
                builder.withLore(getLoreOfWand());
                return true;
            }else{
                GameMode gamemode = player.getGameMode();
                if(gamemode == GameMode.SURVIVAL || gamemode == GameMode.ADVENTURE) {
                    int usesLeft = Integer.parseInt(Logger.stripColor(lore.get(1))) - 1;
                    if(usesLeft >= 0){
                        lore.set(1, getChatcolorString(usesLeft) + usesLeft);
                        builder.withLore(lore);
                        return true;
                    }else{
                        Logger.sendMessage("&cThis wand has expired", player, false);
                        return false;
                    }
                } else {
                    return true;
                }
            }
        }else{
            builder.withLore();
            return true;
        }
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
