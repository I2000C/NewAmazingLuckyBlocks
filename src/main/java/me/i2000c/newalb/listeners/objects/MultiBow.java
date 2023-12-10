package me.i2000c.newalb.listeners.objects;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.OtherUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.TippedArrow;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.util.Vector;

public class MultiBow extends SpecialItem{
    
    @Override
    public void onArrowShooted(EntityShootBowEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!(e.getEntity() instanceof Player)){
            return;
        }
        e.setCancelled(true);
        
        Player player = (Player) e.getEntity();
        ItemStack stack = e.getBow();
        
        boolean withFire = stack.getEnchantments().containsKey(Enchantment.ARROW_FIRE);
        
        int slot = -1;
        for(int i=0;i<player.getInventory().getContents().length;i++){
            ItemStack sk = player.getInventory().getItem(i);
            if(sk == null){
                continue;
            }
            if(sk.getType().name().contains("ARROW")){
                slot = i;
                break;
            }
        }
        
        ItemStack arrowItem;
        if(slot != -1){
            arrowItem = player.getInventory().getItem(slot).clone();
        }else{
            arrowItem = new ItemStack(Material.ARROW);
        }
        
        if(player.getGameMode() == GameMode.CREATIVE){
            launchMultipleArrows(player, true, withFire, arrowItem, e);
            return;
        }
        
        if(slot != -1){
            if(!stack.getEnchantments().containsKey(Enchantment.DURABILITY)){
                stack.setDurability((short)(stack.getDurability()+6));
            }else{
                int level = stack.getEnchantments().get(Enchantment.DURABILITY);
                if(level < 3){
                    stack.setDurability((short)(stack.getDurability()+6-level*2));
                }
            }
            if(384-stack.getDurability() <= 0){
                player.playSound(player.getLocation(), XSound.ENTITY_ITEM_BREAK.parseSound(), 20, 1);
                player.setItemInHand(null);
                return;
            }
            
            if(stack.getEnchantments().containsKey(Enchantment.ARROW_INFINITE)){
                launchMultipleArrows(player, true, withFire, arrowItem, e);
            }else{
                launchMultipleArrows(player, false, withFire, arrowItem, e);
            }
        }
//</editor-fold>
    }
    
    private void launchMultipleArrows(Player player, boolean infinityBow, boolean fireBow, ItemStack arrowStack, EntityShootBowEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        //Source: https://bukkit.org/threads/arrows-aiming-off-depending-on-direction.179547/#post-1889651
        int arrowCount = ConfigManager.getConfig().getInt("Objects.MultiBow.numberOfArrows");
        arrowCount--;
        if(arrowCount < 1){
            arrowCount = 1;
        }
        // you can tune the following value for different spray. Higher number means less spray.
        double spray = ConfigManager.getConfig().getDouble("Objects.MultiBow.spread");

        Vector velocity = e.getProjectile().getVelocity();
        double speed = velocity.length();
        Vector direction = new Vector(velocity.getX() / speed, velocity.getY() / speed, velocity.getZ() / speed);

        boolean firstArrow = true;
        if(infinityBow){
            for(int i=0; i<arrowCount; i++){
                Vector newSpeed;
                if(firstArrow){
                    newSpeed = velocity;
                    firstArrow = false;
                }else{
                    newSpeed = new Vector(direction.getX() + (Math.random() - 0.5) / spray, direction.getY() + (Math.random() - 0.5) / spray, direction.getZ() + (Math.random() - 0.5) / spray).normalize().multiply(speed);
                }
                Arrow arrow = this.launchArrow(player, infinityBow, fireBow, arrowStack, newSpeed);
            }
        }else{
            Inventory inv = player.getInventory();
            for(int i=0,j=0; i<arrowCount && j<inv.getSize(); j++){
                ItemStack sk = inv.getItem(j);
                if(sk != null && sk.getType().name().contains("ARROW")){
                    ItemStack sk2 = sk.clone();
                    int amount = sk.getAmount();
                    int neededArrows = arrowCount - i;
                    int arrowsToShoot;
                    if(amount - neededArrows >= 0){
                        arrowsToShoot = neededArrows;
                        sk.setAmount(amount - neededArrows);
                        if(sk.getAmount() == 0){
                            inv.setItem(j, null);
                        }
                        i = i + neededArrows;
                    }else{
                        arrowsToShoot = amount;
                        inv.setItem(j, null);
                        i = i + amount;
                    }

                    for(int k=0; k<arrowsToShoot; k++){
                        Vector newSpeed;
                        if(firstArrow){
                            newSpeed = velocity;
                            firstArrow = false;
                        }else{
                            newSpeed = new Vector(direction.getX() + (Math.random() - 0.5) / spray, direction.getY() + (Math.random() - 0.5) / spray, direction.getZ() + (Math.random() - 0.5) / spray).normalize().multiply(speed);
                        }
                        Arrow arrow = this.launchArrow(player, infinityBow, fireBow, sk2, newSpeed);
                    }
                }
            }
        }
//</editor-fold>
    }
    
    private static Method setBasePotionMeta;
    
    private static Method getArrowHandle;
    private static Class<?> entityArrow;
    private static Field fromPlayer;
    private static Object pickupStatus = null;
    private static boolean initialized = false;
    
    private Arrow launchArrow(Player player, boolean infinityBow, boolean fireBow, ItemStack arrowStack, Vector newSpeed){
        //<editor-fold defaultstate="collapsed" desc="Code">
        try{
            if(!initialized){
                if(!NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                    if(NewAmazingLuckyBlocks.getMinecraftVersion() != MinecraftVersion.v1_13){
                        setBasePotionMeta = Arrow.class.getMethod("setBasePotionData", PotionData.class);
                    }
                }
                
                getArrowHandle = OtherUtils.getCraftClass("entity.CraftArrow").getMethod("getHandle");
                entityArrow = OtherUtils.getNMSClass("net.minecraft.world.entity.projectile", "EntityArrow");
                try{
                    fromPlayer = entityArrow.getField("fromPlayer");
                }catch(NoSuchFieldException ex){
                    //Since Minecraft 1.17, "fromPlayer" field has been called "d"
                    fromPlayer = entityArrow.getField("d");
                }
                
                initialized = true;
            }
        }catch(Exception ex){
        }
        
        Arrow arrow;
        switch(arrowStack.getType()){
            case SPECTRAL_ARROW:
                SpectralArrow sa = player.launchProjectile(SpectralArrow.class, newSpeed);
                if(fireBow){
                    //In 1.16 SpectralArrow cannot be casted to Arrow
                    sa.setFireTicks(2000);
                }
                arrow = (Arrow) sa;
                break;
            case TIPPED_ARROW:
                try{
                    EntityType et = EntityType.TIPPED_ARROW;
                    TippedArrow ta = (TippedArrow) player.launchProjectile(TippedArrow.class, newSpeed);
                    PotionMeta meta = (PotionMeta) arrowStack.getItemMeta();
                    ta.setBasePotionData(meta.getBasePotionData());
                    arrow = (Arrow) ta;
                    if(fireBow){
                        arrow.setFireTicks(2000);
                    }
                }catch(NoSuchFieldError | ClassCastException ex){
                    arrow = (Arrow) player.launchProjectile(Arrow.class, newSpeed);
                    //In 1.16 tipped arrow is a normal arrow with effects
                    //Set effects
                    try{
                        PotionMeta meta = (PotionMeta) arrowStack.getItemMeta();
                        setBasePotionMeta.invoke(arrow, meta.getBasePotionData());
                        if(fireBow){
                            arrow.setFireTicks(2000);
                        }
                    }catch(Exception ex2){
                    }
                }
                break;
            default:
                arrow = player.launchProjectile(Arrow.class, newSpeed);
                if(fireBow){
                    arrow.setFireTicks(2000);
                }
        }
        
        if(infinityBow){
            try{
                Object craftArrow = getArrowHandle.invoke(arrow);
                
                if(NewAmazingLuckyBlocks.getMinecraftVersion() == MinecraftVersion.v1_8){
                    fromPlayer.set(craftArrow, 2);
                }else{
                    if(pickupStatus == null){
                        Class pickupStatusClass = OtherUtils.getNMSClass("net.minecraft.world.entity.projectile", "EntityArrow$PickupStatus");
                        pickupStatus = pickupStatusClass.getMethod("valueOf", String.class).invoke(pickupStatusClass, "CREATIVE_ONLY");
                    }
                    
                    fromPlayer.set(craftArrow, pickupStatus);
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        
        return arrow;
//</editor-fold>
    }
    
    @Override
    public ItemStack buildItem(){
        return ItemBuilder.newItem(XMaterial.BOW)
                .addEnchantment(Enchantment.ARROW_DAMAGE, 1)
                .build();
    }
}

