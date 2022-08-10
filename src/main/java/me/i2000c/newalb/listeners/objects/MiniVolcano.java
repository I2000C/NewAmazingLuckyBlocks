package me.i2000c.newalb.listeners.objects;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.listeners.interact.SpecialItemName;
import me.i2000c.newalb.utils.Timer;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class MiniVolcano extends SpecialItem{
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
            SpecialItem.decreaseAmountOfItem(e);
            
            Location location = e.getClickedBlock().getLocation();
            Timer.getTimer().executeMiniVolcano(e.getPlayer(), location);
        }
    }
    
    @Override
    public ItemStack buildItem(){
        return ItemBuilder.newItem(XMaterial.LAVA_BUCKET)
                .withDisplayName(getDisplayName())
                .build();
    }
    
    @Override
    public SpecialItemName getSpecialItemName(){
        return SpecialItemName.mini_volcano;
    }
}
