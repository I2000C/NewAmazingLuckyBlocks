package me.i2000c.newalb.listeners.objects;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.lang_utils.LangLoader;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils.WorldList;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.OtherUtils;
import me.i2000c.newalb.utils2.Task;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.TippedArrow;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;

public class AutoBow extends SpecialItem{
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        Player player = e.getPlayer();
        Action action = e.getAction();
        
        if(NewAmazingLuckyBlocks.getMinecraftVersion() != MinecraftVersion.v1_8){
            if(e.getHand() == EquipmentSlot.OFF_HAND){
                //return;
            }
        }
        
        ItemStack object = getItem();
        ItemStack stack = player.getItemInHand();
        if(!OtherUtils.checkItemStack(stack, object)){
            return;
        }
        
        if(action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR){
            if(!WorldList.isRegistered(player.getWorld().getName())){
                return;
            }
            if(!OtherUtils.checkPermission(player, "Objects.AutoBow")){
                return;
            }
            
            if(MaterialChecker.check(e)){
                return;
            }else{
                e.setCancelled(true);
            }
            
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
                launchArrow(player, true, withFire, arrowItem);                
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
                    launchArrow(player, true, withFire, arrowItem);
                }else{
                    ItemStack arrow = player.getInventory().getItem(slot);
                    int amt = arrow.getAmount();
                    if(amt > 1){
                        arrow.setAmount(amt - 1);
                    }else{
                        player.getInventory().setItem(slot, null);
                    }
                    launchArrow(player, false, withFire, arrowItem);
                } 
            }
        }
    }
    
    private static Method setBasePotionMeta;
    
    private static Method getArrowHandle;
    private static Class<?> entityArrow;
    private static Field fromPlayer;
    private static Object pickupStatus = null;
    private static boolean initialized = false;
    
    private static void launchArrow(Player player, boolean infinityBow, boolean fireBow, ItemStack arrowStack){
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
            ex.printStackTrace();
        }
        
        ItemStack sk = player.getItemInHand();
        player.setItemInHand(sk);
        if(!NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
            player.setItemInHand(null);
            Task.runTask(() -> player.setItemInHand(sk), 2);
        }
        
        Arrow arrow;
        switch(arrowStack.getType()){
            case SPECTRAL_ARROW:
                SpectralArrow sa = player.launchProjectile(SpectralArrow.class, player.getLocation().getDirection());
                if(fireBow){
                    //In 1.16 SpectralArrow cannot be casted to Arrow
                    sa.setFireTicks(2000);
                }
                arrow = (Arrow) sa;
                break;
            case TIPPED_ARROW:
                try{
                    EntityType et = EntityType.TIPPED_ARROW;
                    TippedArrow ta = (TippedArrow) player.launchProjectile(TippedArrow.class, player.getLocation().getDirection());
                    PotionMeta meta = (PotionMeta) arrowStack.getItemMeta();
                    ta.setBasePotionData(meta.getBasePotionData());
                    arrow = (Arrow) ta;
                    if(fireBow){
                        arrow.setFireTicks(2000);
                    }
                }catch(NoSuchFieldError | ClassCastException ex){
                    arrow = (Arrow) player.launchProjectile(Arrow.class, player.getLocation().getDirection());
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
                arrow = player.launchProjectile(Arrow.class, player.getLocation().getDirection());
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
    }
    
    @Override
    public ItemStack buildItem(){
        return ItemBuilder.newItem(XMaterial.BOW)
                .withDisplayName(LangLoader.getMessages().getString("Objects.AutoBow.name"))
                .addEnchantment(Enchantment.ARROW_DAMAGE, 1)
                .build();
    }
}
