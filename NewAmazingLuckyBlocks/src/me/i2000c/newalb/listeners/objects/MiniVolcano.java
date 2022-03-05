package me.i2000c.newalb.listeners.objects;

import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.LangLoader;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.OtherUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.i2000c.newalb.utils.WorldList;
import me.i2000c.newalb.utils.Timer;
import org.bukkit.inventory.EquipmentSlot;

public class MiniVolcano implements Listener{
    private final NewAmazingLuckyBlocks plugin;
  
    public MiniVolcano(NewAmazingLuckyBlocks plugin){
        this.plugin = plugin;
    }
  
    @EventHandler
    public void playerInteraction(PlayerInteractEvent e){
        Player player = e.getPlayer();
        Action action = e.getAction();
        String miniVName = object.getItemMeta().getDisplayName();

        if(NewAmazingLuckyBlocks.getMinecraftVersion() != MinecraftVersion.v1_8){
            if(e.getHand() == EquipmentSlot.OFF_HAND){
                return;
            }
        }
    
        ItemStack stack = player.getItemInHand();
        if((action.equals(Action.RIGHT_CLICK_BLOCK)) && (stack.getType() == object.getType())){
            if(!WorldList.isRegistered(player.getWorld().getName())){
                return;
            }
            if(!OtherUtils.checkPermission(player, "Objects.MiniVolcano")){
                return;
            }
        
            if(MaterialChecker.check(e)){
                return;
            }
        
            if((stack.hasItemMeta()) && (stack.getItemMeta().getDisplayName().equals(miniVName))){
                e.setCancelled(true);
                Location l = e.getClickedBlock().getLocation();          
                int amt = stack.getAmount() - 1;
                if(amt == 0){
                    player.setItemInHand(null);
                }else{
                    stack.setAmount(amt);
                }

                Timer.getTimer().executeMiniVolcano(player, l);           
            }
        }
    }
    
    private static ItemStack object;
    
    public static ItemStack getObject(){
        return object.clone();
    }
    
    public static void loadObject(){
        ItemStack stack = new ItemStack(Material.LAVA_BUCKET);
        
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Logger.color(LangLoader.getMessages().getString("Objects.MiniVolcano.name")));
        stack.setItemMeta(meta);
        
        object = stack;
    }
}
