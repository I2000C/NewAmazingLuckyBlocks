package me.i2000c.newalb.utils;

import me.i2000c.newalb.listeners.interact.SpecialItem;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class BowItem extends SpecialItem implements Listener{
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){}
    
}
