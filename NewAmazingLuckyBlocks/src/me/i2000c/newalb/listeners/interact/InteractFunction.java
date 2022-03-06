package me.i2000c.newalb.listeners.interact;

import org.bukkit.event.player.PlayerInteractEvent;

@FunctionalInterface
public interface InteractFunction{
    public void execute(PlayerInteractEvent e);
}
