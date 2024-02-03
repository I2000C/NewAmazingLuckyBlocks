package me.i2000c.newalb.listeners.objects.utils;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.reflection.RefClass;
import me.i2000c.newalb.reflection.ReflectionManager;
import me.i2000c.newalb.utils2.Task;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.TippedArrow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.util.Vector;

public class BowUtils {
    
    private static final int DURABILITY_PER_USE = 6;
    private static final int DURABILITY_RECOVERED_PER_LEVEL = -2;
    private static final int MAX_DURABILITY_VALUE_BOW = 384;
    
    private static final int ARROW_FIRE_TICKS = 2000;
    
    private static final EnumSet ARROW_MATERIALS = EnumSet.of(XMaterial.ARROW, 
                                                              XMaterial.TIPPED_ARROW, 
                                                              XMaterial.SPECTRAL_ARROW);
    
    public static boolean isFireBow(ItemStack bowStack) {
        return bowStack.getEnchantments().containsKey(Enchantment.ARROW_FIRE);
    }
    
    public static boolean isInfiniteBow(Player player, ItemStack bowStack) {
        return player.getGameMode() == GameMode.CREATIVE
                || bowStack.getEnchantments().containsKey(Enchantment.ARROW_INFINITE);
    }
    
    public static boolean applyDurability(Player player, ItemStack bowStack) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        return applyDurability(player, bowStack, DURABILITY_PER_USE, DURABILITY_RECOVERED_PER_LEVEL);
//</editor-fold>
    }
    public static boolean applyDurability(Player player, ItemStack bowStack, 
                                          int durabilityPerUse, int durabilityRecoveredPerLevel) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(player.getGameMode() == GameMode.CREATIVE) {
            return true;
        }
        
        int durability = bowStack.getDurability();
        int level;
        if(bowStack.getEnchantments().containsKey(Enchantment.DURABILITY)) {
            level = bowStack.getEnchantments().get(Enchantment.DURABILITY);            
        } else {
            level = 0;
        }
        
        durability = durability + durabilityPerUse - level*durabilityRecoveredPerLevel;
        
        // Durability must be greater o equal than previous durability
        if(durability < bowStack.getDurability()) {
            durability = bowStack.getDurability();
        }
        
        if(durability >= MAX_DURABILITY_VALUE_BOW) {
            XSound.ENTITY_ITEM_BREAK.play(player);
            player.setItemInHand(null);
            return false;
        } else {
            bowStack.setDurability((short) durability);
            return true;
        }
//</editor-fold>
    }
    
    public static Arrow launchArrow(Player player, 
                                    ItemStack arrowStack, 
                                    boolean isFireBow, boolean isInfiniteBow, 
                                    Vector velocity) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        
        // Check if X, Y or Z in the velocity vector are valid values
        if(Double.isInfinite(velocity.getX()) || Double.isNaN(velocity.getX())) {
            velocity.setX(0.0);
        }        
        if(Double.isInfinite(velocity.getY()) || Double.isNaN(velocity.getY())) {
            velocity.setY(0.0);
        }        
        if(Double.isInfinite(velocity.getZ()) || Double.isNaN(velocity.getZ())) {
            velocity.setZ(0.0);
        }        
        
        Arrow arrow;
        XMaterial xmaterial = XMaterial.matchXMaterial(arrowStack);
        switch(xmaterial) {
            case SPECTRAL_ARROW:
                arrow = player.launchProjectile(SpectralArrow.class, velocity);
                break;
            case TIPPED_ARROW:
                PotionData potionData = null;
                if(arrowStack.getItemMeta() instanceof PotionMeta) {
                    PotionMeta meta = (PotionMeta) arrowStack.getItemMeta();
                    potionData = meta.getBasePotionData();
                }
                
                if(MinecraftVersion.CURRENT_VERSION.compareTo(MinecraftVersion.v1_16) >= 0) {
                    // Since Minecraft 1.16, tipped arrows are normal arrows with effects
                    arrow = player.launchProjectile(Arrow.class, velocity);
                    if(potionData != null) {
                        ReflectionManager.callMethod(arrow, "setBasePotionMeta", potionData);
                    }
                } else {
                    // From Minecraft 1.9 to 1.15, tipped arrows are instances of TippedArrow
                    arrow = player.launchProjectile(TippedArrow.class, velocity);
                    if(potionData != null) {
                        ((TippedArrow) arrow).setBasePotionData(potionData);
                    }
                }
                break;
            default:
                arrow = player.launchProjectile(Arrow.class, velocity);
        }
        
        if(isFireBow) {
            arrow.setFireTicks(ARROW_FIRE_TICKS);
        }
        
        if(isInfiniteBow) {
            ArrowPickupStatus.CREATIVE_ONLY.setToArrow(arrow);
        }
        
        XSound.ENTITY_ARROW_SHOOT.play(player);
        
        return arrow;
//</editor-fold>
    }
    
    public static List<ItemStack> getArrowsFromPlayerInventory(Player player, int requiredAmount, boolean removeItems) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        List<ItemStack> arrowItemList = new ArrayList<>();
        ItemStack[] items = player.getInventory().getContents();
        
        for(int i=0; i<items.length && requiredAmount > 0; i++) {
            ItemStack item = items[i];
            if(item == null) {
                continue;
            }
            
            XMaterial xmaterial = XMaterial.matchXMaterial(item);
            if(!ARROW_MATERIALS.contains(xmaterial)) {
                continue;
            }
            
            ItemStack copy = item.clone();
            copy.setAmount(1);
            
            int itemAmount = item.getAmount();
            if(itemAmount > requiredAmount) {
                for(int j=0; j<requiredAmount; j++) {
                    arrowItemList.add(copy);
                }
                
                if(removeItems) item.setAmount(itemAmount - requiredAmount);
            } else {
                for(int j=0; j<itemAmount; j++) {
                    arrowItemList.add(copy);
                }
                if(removeItems) player.getInventory().setItem(i, null);
            }
            
            requiredAmount -= itemAmount;
        }
        
        return arrowItemList;
//</editor-fold>
    }
    public static Optional<ItemStack> getArrowFromPlayerInventory(Player player, boolean removeItems) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        List<ItemStack> arrowItems = getArrowsFromPlayerInventory(player, 1, removeItems);
        return arrowItems.stream().findFirst();
//</editor-fold>
    }
    
    public static void cancelBowCharging(Player player) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemStack stack = player.getItemInHand();
        if(stack == null) {
            return;
        }
        
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()) {
            player.setItemInHand(stack);
        } else {
            player.setItemInHand(null);
            Task.runTask(() -> player.setItemInHand(stack), 2L);
        }
//</editor-fold>
    }    
    
    public static enum ArrowPickupStatus {
        DISALLOWED, ALLOWED, CREATIVE_ONLY;
        
        public void setToArrow(Arrow arrow) {
            //<editor-fold defaultstate="collapsed" desc="Code">
            Object craftArrow = ReflectionManager.callMethod(arrow, "getHandle");
            if(MinecraftVersion.CURRENT_VERSION == MinecraftVersion.v1_8) {
                // In Minecraft 1.8, the field is called "fromPlayer" and requires an int (0, 1 or 2)
                ReflectionManager.setFieldValue(craftArrow, "fromPlayer", this.ordinal());
            } else {
                RefClass refClass = ReflectionManager.getNMSClass("net.minecraft.world.entity.projectile", "EntityArrow$PickupStatus");
                Object pickupStatus = refClass.callStaticMethod("valueOf", this.name());
                if(MinecraftVersion.CURRENT_VERSION.compareTo(MinecraftVersion.v1_17) >= 0) {
                    // Since Minecraft 1.17, the field is called "d"
                    ReflectionManager.setFieldValue(craftArrow, "d", pickupStatus);
                } else {
                    // From Minecraft 1.9 to Minecraft 1.16, the field is called "fromPlayer", but requires an enum
                    ReflectionManager.setFieldValue(craftArrow, "fromPlayer", pickupStatus);
                }
            }
//</editor-fold>
        }
    }
}
