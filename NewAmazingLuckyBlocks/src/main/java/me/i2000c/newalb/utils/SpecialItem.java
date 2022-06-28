package me.i2000c.newalb.utils;

import me.i2000c.newalb.listeners.interact.PlayerInteractListener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public abstract class SpecialItem{
    private ItemStack item;
    private int id = -1;
    
    public ItemStack getItem(){
        return this.item.clone();
    }
    
    public void loadItem(){
        this.item = buildItem();
        if(id == -1){
            id = PlayerInteractListener.registerSpecialtem(this);
        }
        
        this.item = PlayerInteractListener.setSpecialtemID(this.item, id);
    }
    
    public abstract ItemStack buildItem();
    
    public abstract void onPlayerInteract(PlayerInteractEvent e);
}
