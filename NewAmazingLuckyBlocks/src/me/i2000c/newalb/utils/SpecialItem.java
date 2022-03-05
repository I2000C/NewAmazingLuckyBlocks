package me.i2000c.newalb.utils;

import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public abstract class SpecialItem implements Listener{
    private ItemStack item;
    
    public ItemStack getItem(){
        return this.item.clone();
    }
    
    public void loadItem(){
        this.item = buildItem();
    }
    
    public abstract ItemStack buildItem();
}
