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

public class DarkHole extends SpecialItem{
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
            e.setCancelled(true);
            
            super.decreaseAmountOfItem(e);
            
            Location location = e.getClickedBlock().getLocation();
            Timer.getTimer().executeDarkHole(e.getPlayer(), location);
        }
    }
    
    @Override
    public ItemStack buildItem(){
        return ItemBuilder.newItem(XMaterial.BUCKET)
                .withDisplayName(getDisplayName())
                .build();
    }
    
    @Override
    public SpecialItemName getSpecialItemName(){
        return SpecialItemName.dark_hole;
    }
}
