package me.i2000c.newalb.listeners.objects;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.NonNull;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.reflection.ReflectionManager;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import me.i2000c.newalb.utils2.Task;
import me.i2000c.newalb.utils2.WorldGuardManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ItemStealer extends SpecialItem {
    
    private static final int HELMET_SLOT = 100;
    private static final int CHESTPLATE_SLOT = 101;
    private static final int LEGGINGS_SLOT = 102;
    private static final int BOOTS_SLOT = 103;
    private static final int ITEM_IN_MAIN_HAND_SLOT = 104;
    private static final int ITEM_IN_OFF_HAND_SLOT = 105;
    
    private static final BlockFace[] BLOCK_FACES = BlockFace.values();
    
    private int numberOfItemsToSteal;
    private int stealRadius;
    private double speedOfItems;
    private double minDistanceToStealer;
    private int minPickupSeconds;
    private int maxFlySeconds;
    private boolean allowStealingItemsFromBlocks;
    
    @Override
    public void onPlayerFish(PlayerFishEvent e) {
        Player player = e.getPlayer();
        
        Entity hook;
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()) {
            hook = e.getHook();
        } else {
            // Since Minecraft 1.13 the type of getHook() is 'FishHook' instead of 'Fish'
            hook = ReflectionManager.callMethod(e, "getHook");
        }
        Location location = hook.getLocation();
        if(!WorldGuardManager.canUse(player, location)) {
            return;
        }
        
        switch(e.getState()) {
            case CAUGHT_ENTITY:
                if(execute(player, e.getCaught())) {
                    super.getPlayerCooldown().updateCooldown(player);
                }
                break;
            default:
                // In Minecraft 1.14 a new PlayerFishEvent.State was added: REEL_IN
                // It is similar to FAILED_ATTEMPT or IN_GROUND
                if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_14)) {
                    if(!e.getState().name().equals("REEL_IN")) {
                        break;
                    }
                }
            case FAILED_ATTEMPT:
            case IN_GROUND:
                if(execute(player, location)) {
                    super.getPlayerCooldown().updateCooldown(player);
                }
                break;
        }
    }
    
    @Override
    public ItemStack buildItem() {
        numberOfItemsToSteal = ConfigManager.getMainConfig().getInt(super.itemPathKey + ".number-of-items-to-steal");
        stealRadius = ConfigManager.getMainConfig().getInt(super.itemPathKey + ".steal-radius");
        speedOfItems = ConfigManager.getMainConfig().getDouble(super.itemPathKey + ".speed-of-items");
        minDistanceToStealer = ConfigManager.getMainConfig().getDouble(super.itemPathKey + ".min-distance-to-stealer");
        minPickupSeconds = ConfigManager.getMainConfig().getInt(super.itemPathKey + ".min-pickup-seconds");
        maxFlySeconds = ConfigManager.getMainConfig().getInt(super.itemPathKey + ".max-fly-seconds");
        allowStealingItemsFromBlocks = ConfigManager.getMainConfig().getBoolean(super.itemPathKey + ".allow-stealing-items-from-blocks");
        return ItemStackWrapper.newItem(XMaterial.FISHING_ROD)
                               .addEnchantment(XEnchantment.LURE, 1)
                               .toItemStack();
    }
    
    public boolean execute(Player player, Entity entity) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(entity instanceof Item) {
            return execute(player, entity.getLocation());
        }
        
        if(!(entity instanceof LivingEntity)) {
            return false;
        }
        
        LivingEntity livingEntity = (LivingEntity) entity;
        List<Map.Entry<Integer, ItemStack>> items = getEntityItems(livingEntity);
        if(items.isEmpty()) {
            return false;
        }
        
        Collections.shuffle(items);
        
        Location loc = entity.getLocation();
        
        int min = Math.min(items.size(), numberOfItemsToSteal);
        items.subList(0, min).forEach(entry -> {
            int slot = entry.getKey();
            ItemStack item = entry.getValue();                
            setEntityItem(livingEntity, slot, null);
            flyItemToPlayer(item, loc, player);
        });
        
        return true;
//</editor-fold>
    }
    
    public boolean execute(Player player, Location loc) {
        List<Item> items = loc.getWorld().getNearbyEntities(loc, stealRadius, stealRadius, stealRadius)
                                         .stream()
                                         .filter(entity -> entity instanceof Item)
                                         .map(entity -> (Item) entity)
                                         .collect(Collectors.toList());
        
        if(!items.isEmpty()) {
            int min = Math.min(items.size(), numberOfItemsToSteal);
            items.subList(0, min).forEach(item -> flyItemToPlayer(item, player));
            return true;
        } else if(allowStealingItemsFromBlocks) {
            BlockState state = null;
            for(BlockFace blockFace : BLOCK_FACES) {
                Block block = loc.getBlock().getRelative(blockFace);
                BlockState stateAux = block.getState();
                if(stateAux instanceof InventoryHolder && WorldGuardManager.canUse(player, block.getLocation())) {
                    state = stateAux;
                    break;
                }
            }
            
            if(state == null) {
                return false;
            }
            
            InventoryHolder container = (InventoryHolder) state;
            Inventory inv = container.getInventory();
            
            List<Map.Entry<Integer, ItemStack>> blockItems = new ArrayList<>();
            for(int i=0; i<inv.getSize(); i++) {
                ItemStack stack = inv.getItem(i);
                if(stack != null && stack.getType() != Material.AIR) {
                    blockItems.add(new AbstractMap.SimpleEntry<>(i, stack));
                }
            }
            
            if(blockItems.isEmpty()) {
                return false;
            }
            
            Collections.shuffle(blockItems);
            Location blockLoc = state.getBlock().getLocation().add(0.5, 0.5, 0.5);
            
            int min = Math.min(blockItems.size(), numberOfItemsToSteal);
            blockItems.subList(0, min).forEach(entry -> {
                int slot = entry.getKey();
                ItemStack item = entry.getValue();                
                inv.setItem(slot, null);
                flyItemToPlayer(item, blockLoc, player);
            });
            
            return true;
        }
        
        return false;
    }
    
    private void flyItemToPlayer(@NonNull ItemStack itemStack, @NonNull Location fromLocation, @NonNull Player player) {
        Item item = fromLocation.getWorld().dropItem(fromLocation, itemStack);
        item.setPickupDelay(minPickupSeconds * 20);
        flyItemToPlayer(item, player);
    }
    private void flyItemToPlayer(@NonNull Item item, @NonNull Player player) {
        final double minDistanceSquared = minDistanceToStealer*minDistanceToStealer;        
        final long expireFlyTimeMS = System.currentTimeMillis() + maxFlySeconds*1000;
        
        Task task = new Task() {
            @Override
            public void run() {
                if(System.currentTimeMillis() > expireFlyTimeMS) {
                    cancel(); return;
                }
                
                if(!item.isValid() || !player.isOnline()) {
                    cancel(); return;
                }
                
                Location itemLoc = item.getLocation();
                Location playerLoc = player.getLocation();
                if(!Objects.equals(itemLoc.getWorld(), playerLoc.getWorld())) {
                    cancel(); return;
                }
                
                double distanceSquared = itemLoc.distanceSquared(playerLoc);
                if(distanceSquared <= minDistanceSquared) {
                    cancel(); return;
                }
                
                Vector direction = playerLoc.toVector().subtract(itemLoc.toVector()).normalize();
                item.setVelocity(direction.multiply(speedOfItems));
            }
        };
        task.runTask(0L, 1L);
    }
    
    private static ItemStack getEntityItem(LivingEntity entity, int slot) {
        if(slot < HELMET_SLOT && entity instanceof InventoryHolder) {
            Inventory inv = ((InventoryHolder) entity).getInventory();
            if(slot >= 0 && slot < inv.getSize()) {
                return inv.getItem(slot);
            }
        } else switch(slot) {
            case HELMET_SLOT:               return entity.getEquipment().getHelmet();
            case CHESTPLATE_SLOT:           return entity.getEquipment().getChestplate();
            case LEGGINGS_SLOT:             return entity.getEquipment().getLeggings();
            case BOOTS_SLOT:                return entity.getEquipment().getBoots();
            case ITEM_IN_MAIN_HAND_SLOT:    return entity.getEquipment().getItemInHand();
            case ITEM_IN_OFF_HAND_SLOT:
                if(!MinecraftVersion.CURRENT_VERSION.is_1_8()) {
                    return entity.getEquipment().getItemInOffHand();
                }
        }
        
        return null;
    }
    private static void setEntityItem(LivingEntity entity, int slot, ItemStack stack) {
        if(slot < HELMET_SLOT && entity instanceof InventoryHolder) {
            Inventory inv = ((InventoryHolder) entity).getInventory();
            if(slot >= 0 && slot < inv.getSize()) {
                inv.setItem(slot, stack);
            }
        } else switch(slot) {
            case HELMET_SLOT:               entity.getEquipment().setHelmet(stack);     break;
            case CHESTPLATE_SLOT:           entity.getEquipment().setChestplate(stack); break;
            case LEGGINGS_SLOT:             entity.getEquipment().setLeggings(stack);   break;
            case BOOTS_SLOT:                entity.getEquipment().setBoots(stack);      break;
            case ITEM_IN_MAIN_HAND_SLOT:    entity.getEquipment().setItemInHand(stack); break;
            case ITEM_IN_OFF_HAND_SLOT:
                if(!MinecraftVersion.CURRENT_VERSION.is_1_8()) {
                    entity.getEquipment().setItemInOffHand(stack);
                }
                break;
        }
    }
    
    private static List<Map.Entry<Integer, ItemStack>> getEntityItems(LivingEntity entity) {
        List<Map.Entry<Integer, ItemStack>> items = new ArrayList<>();
        
        // Add entity inventory
        if(entity instanceof InventoryHolder) {
            Inventory inv = ((InventoryHolder) entity).getInventory();
            for(int i=0; i<inv.getSize(); i++) {
                ItemStack stack = inv.getItem(i);
                if(stack != null && stack.getType() != Material.AIR) {
                    Map.Entry<Integer, ItemStack> entry = new AbstractMap.SimpleEntry<>(i, stack);
                    items.add(entry);
                }
            }
        }
        
        // Add entity equipment
        for(int i=HELMET_SLOT; i<=ITEM_IN_OFF_HAND_SLOT; i++) {
            if(i == ITEM_IN_MAIN_HAND_SLOT && entity instanceof Player) {
                continue;
            }
            
            ItemStack stack = getEntityItem(entity, i);
            if(stack != null && stack.getType() != Material.AIR) {
                Map.Entry<Integer, ItemStack> entry = new AbstractMap.SimpleEntry<>(i, stack);
                items.add(entry);
            }
        }
        
        return items;
    }
}
