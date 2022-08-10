package me.i2000c.newalb.listeners.objects;

import java.util.HashMap;
import java.util.UUID;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.utils.LuckyBlockType;
import me.i2000c.newalb.custom_outcomes.utils.TypeManager;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.lang_utils.LangLoader;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils.WorldList;
import me.i2000c.newalb.utils2.OtherUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LuckyTool extends SpecialItem{
    private final HashMap<UUID, Long> luckyToolCooldown = new HashMap();
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        Player player = e.getPlayer();       
        Action action = e.getAction();
    
        if(NewAmazingLuckyBlocks.getMinecraftVersion() != MinecraftVersion.v1_8){
            if(e.getHand() == EquipmentSlot.OFF_HAND){
                return;
            }
        }
        
        ItemStack stack = player.getItemInHand();
        if((action.equals(Action.RIGHT_CLICK_BLOCK))){
            ItemStack object = getItem();
            if(!OtherUtils.checkItemStack(object, stack)){
                return;
            }
            
            if(!WorldList.isRegistered(player.getWorld().getName())){
                return;
            }
            
            TypeManager.Result result = TypeManager.canBreakBlock(player, e.getClickedBlock().getLocation());
            LuckyBlockType type;
            switch(result.resultCode){
                case TypeManager.RESULT_NOT_LUCKYBLOCK:
                    return;
                case TypeManager.RESULT_NO_GLOBAL_PERMISSION:
                case TypeManager.RESULT_NO_LOCAL_PERMISSION:
                    Logger.sendMessage(LangLoader.getMessages().getString("NoPermission"), player);
                    return;
                default:
                    type = result.resultType;
            }
            
            if(!ConfigManager.getConfig().getBoolean("LuckyTool.enable")){
                Logger.sendMessage(LangLoader.getMessages().getString("Objects.LuckyTool.disabled"), player);
                return;
            }
            
            if(!OtherUtils.checkPermission(player, "LuckyTool")){
                return;
            }
            
            if((this.luckyToolCooldown.containsKey(player.getUniqueId())) && ((this.luckyToolCooldown.get(player.getUniqueId())) > System.currentTimeMillis())){
                long remainingTime = this.luckyToolCooldown.get(player.getUniqueId()) - System.currentTimeMillis();
                String cmsg = LangLoader.getMessages().getString("Cooldown-message").replace("%time%", String.valueOf(remainingTime / 1000L));
                player.sendMessage(cmsg);
                return;
            }
            
            //Break the Lucky Block
            Block target = e.getClickedBlock();
            target.setType(Material.AIR);
            type.executeRandomPack(e.getPlayer(), target.getLocation().add(0.5, 0, 0.5));
            
            
            int tw = ConfigManager.getConfig().getInt("LuckyTool.cooldown-time");
            this.luckyToolCooldown.put(player.getUniqueId(), System.currentTimeMillis() + tw * 1000);
        }
    }
    
    @Override
    public ItemStack buildItem(){
        ItemStack stack = new ItemStack(Material.STICK);
        
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(LangLoader.getMessages().getString("Objects.LuckyTool.name"));
        stack.setItemMeta(meta);
        
        return stack;
    }
}
