package me.i2000c.newalb.listeners.objects;

import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.lang_utils.LangLoader;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils.SpecialItem;
import me.i2000c.newalb.utils2.OtherUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.EquipmentSlot;

import me.i2000c.newalb.utils.WorldList;
import me.i2000c.newalb.utils.Timer;

public class DarkHole extends SpecialItem{
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        Player player = e.getPlayer();       
        Action action = e.getAction();
        
        ItemStack object = getItem();
        String darkName = object.getItemMeta().getDisplayName();
    
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
            if(!OtherUtils.checkPermission(player, "Objects.DarkHole")){
                return;
            }
            
            if(MaterialChecker.check(e)){
                return;
            }

            if((stack.hasItemMeta()) && (stack.getItemMeta().getDisplayName().equals(darkName))){
                e.setCancelled(true);
                Location l = e.getClickedBlock().getLocation();
                int amt = stack.getAmount() - 1;
                if(amt == 0){
                    player.setItemInHand(null);
                }else{
                    stack.setAmount(amt);
                }

                Timer.getTimer().executeDarkHole(player, l);           
            }
        }
    }
    
    @Override
    public ItemStack buildItem(){
        ItemStack stack = new ItemStack(Material.BUCKET);
        
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(LangLoader.getMessages().getString("Objects.DarkHole.name"));
        stack.setItemMeta(meta);
        
        return stack;
    }
}
