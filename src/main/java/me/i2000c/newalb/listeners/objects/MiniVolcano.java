package me.i2000c.newalb.listeners.objects;

import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.lang_utils.LangLoader;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.SpecialItem;
import me.i2000c.newalb.utils2.OtherUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.i2000c.newalb.utils.WorldList;
import me.i2000c.newalb.utils.Timer;
import org.bukkit.inventory.EquipmentSlot;

public class MiniVolcano extends SpecialItem{
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        Player player = e.getPlayer();
        Action action = e.getAction();
        
        ItemStack object = getItem();
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
    
    @Override
    public ItemStack buildItem(){
        ItemStack stack = new ItemStack(Material.LAVA_BUCKET);
        
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Logger.color(LangLoader.getMessages().getString("Objects.MiniVolcano.name")));
        stack.setItemMeta(meta);
        
        return stack;
    }
}
