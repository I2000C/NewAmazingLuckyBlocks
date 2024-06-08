package me.i2000c.newalb.listeners.interact;

import java.util.ArrayList;
import java.util.List;
import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import me.i2000c.newalb.utils2.PlayerCooldown;
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
    private final PlayerCooldown playerCooldown;
    
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
        
        int cooldownTime = ConfigManager.getMainConfig().getInt(itemPathKey + ".cooldown-time", -1);
        playerCooldown = new PlayerCooldown(cooldownTime);
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
        int customModelData = ConfigManager.getMainConfig().getInt(this.itemPathKey + ".custom-model-data");
        String displayName = ConfigManager.getLangMessage(this.itemPathKey + ".name");
        
        this.item = ItemStackWrapper.fromItem(buildItem())
                                    .setDisplayName(displayName)
                                    .setNbtTag(ITEM_TAG, this.id)
                                    .setNbtTag(CUSTOM_MODEL_DATA_TAG, customModelData)
                                    .toItemStack();
        
        int cooldownTime = ConfigManager.getMainConfig().getInt(itemPathKey + ".cooldown-time", -1);
        this.playerCooldown.setCooldownTime(cooldownTime);
        this.playerCooldown.clear();
    }
    
    public final PlayerCooldown getPlayerCooldown() {
        return this.playerCooldown;
    }
    
    public final boolean checkPermission(Player player){
        boolean requiredPermission = ConfigManager.getMainConfig().getBoolean(itemPathKey + ".required-permission");
        String permission = ConfigManager.getMainConfig().getString(itemPathKey + ".permission");
        
        if(requiredPermission && !player.hasPermission(permission)){
            Logger.sendMessage(ConfigManager.getLangMessage("need-permission"), player, false);
            return false;
        }else{
            return true;
        }
    }
    
    protected void sendRemainingSecondsMessage(Player player){
        String message = ConfigManager.getLangMessage("Cooldown-message");
        message = message.replace("%time%", String.format("%.2f", playerCooldown.getRemainingSeconds(player)));
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
        if(ConfigManager.getMainConfig().getBoolean(itemPathKey + ".limited-uses.enable")){
            int uses = ConfigManager.getMainConfig().getInt(itemPathKey + ".limited-uses.uses");
            lore.add("&5Uses left:");
            lore.add(getChatcolorString(uses) + uses);
        }        
        return lore;
    }
    // Decrease uses of wands
    protected boolean decreaseWandUses(ItemStack stack, Player player){
        ItemStackWrapper wrapper = ItemStackWrapper.fromItem(stack, false);
        if(ConfigManager.getMainConfig().getBoolean(itemPathKey + ".limited-uses.enable")){
            List<String> lore = wrapper.getLore();
            if(lore == null){
                wrapper.setLore(getLoreOfWand());
                return true;
            }else{
                GameMode gamemode = player.getGameMode();
                if(gamemode == GameMode.SURVIVAL || gamemode == GameMode.ADVENTURE) {
                    int usesLeft = Integer.parseInt(Logger.stripColor(lore.get(1))) - 1;
                    if(usesLeft >= 0){
                        lore.set(1, getChatcolorString(usesLeft) + usesLeft);
                        wrapper.setLore(lore);
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
            wrapper.setLore();
            return true;
        }
    }
    
    // Methods to implement
    protected abstract ItemStack buildItem();
    
    // Overridable events
    public void onPlayerInteract(PlayerInteractEvent e){}
    
    public void onItemPickup(PlayerPickupItemEvent e){}
    
    public void onArrowHit(CustomProjectileHitEvent e){}
    
    public void onEntityDamaged(EntityDamageByEntityEvent e){}
    
    public void onArrowShooted(EntityShootBowEvent e){}
    
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e){}
    
    public void onFallingBlockConvert(EntityChangeBlockEvent e){}
}
