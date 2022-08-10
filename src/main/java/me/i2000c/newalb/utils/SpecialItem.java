package me.i2000c.newalb.utils;

import me.i2000c.newalb.lang_utils.LangLoader;
import me.i2000c.newalb.listeners.interact.PlayerInteractListener;
import me.i2000c.newalb.utils.logger.Logger;
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
    
    public String getPermissionPath(){
        if(getSpecialItemName().isWand()){
            return "Wands." + this.getClass().getSimpleName();
        }else{
            return "Objects." + this.getClass().getSimpleName();
        }
    }
    
    public String getDisplayName(){
        String namePath;
        if(getSpecialItemName().isWand()){
            namePath = "Wands." + this.getClass().getSimpleName() + ".name";
        }else{
            namePath = "Objects." + this.getClass().getSimpleName() + ".name";
        }
        Logger.log(namePath);
        return LangLoader.getMessages().getString(namePath);
    }
    
    public abstract ItemStack buildItem();
    
    public abstract void onPlayerInteract(PlayerInteractEvent e);
    
    public abstract SpecialItemName getSpecialItemName();
}
