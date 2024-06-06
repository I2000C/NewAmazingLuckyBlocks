package me.i2000c.newalb.listeners.objects;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.custom_outcomes.rewards.LuckyBlockType;
import me.i2000c.newalb.custom_outcomes.rewards.TypeManager;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.ItemStackWrapper;

public class LuckyTool extends SpecialItem{
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        Player player = e.getPlayer();
        Action action = e.getAction();
        
        if(action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK){
            TypeManager.Result result = TypeManager.canBreakBlock(player, e.getClickedBlock().getLocation());
            LuckyBlockType type;
            switch(result.resultCode){
                case TypeManager.RESULT_NOT_LUCKYBLOCK:
                    return;
                case TypeManager.RESULT_NO_GLOBAL_PERMISSION:
                case TypeManager.RESULT_NO_LOCAL_PERMISSION:
                    Logger.sendMessage(ConfigManager.getLangMessage("NoPermission"), player);
                    return;
                default:
                    e.setCancelled(true);
                    type = result.resultType;
            }
            
            if(!ConfigManager.getMainConfig().getBoolean("Objects.LuckyTool.enable")){
                Logger.sendMessage(ConfigManager.getLangMessage("Objects.LuckyTool.disabled"), player);
                return;
            }
            
            // Break the Lucky Block
            Block target = e.getClickedBlock();
            target.setType(Material.AIR);
            if(action == Action.RIGHT_CLICK_BLOCK){
                // Execute LuckyBlock type
                type.execute(e.getPlayer(), target.getLocation().add(0.5, 0, 0.5));
            }
            
            super.getPlayerCooldown().updateCooldown(player);
        }
    }
    
    @Override
    public ItemStack buildItem(){
        return ItemStackWrapper.newItem(XMaterial.STICK)
                               .setLore(ConfigManager.getLangConfig().getStringList("Objects.LuckyTool.lore"))
                               .toItemStack();
    }
}
