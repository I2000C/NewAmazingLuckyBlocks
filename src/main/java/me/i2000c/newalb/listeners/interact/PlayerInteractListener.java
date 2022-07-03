package me.i2000c.newalb.listeners.interact;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import java.util.ArrayList;
import me.i2000c.newalb.utils.SpecialItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener{
    private static final String ITEM_TAG = "NewAmazingLuckyBlocks.SpecialItem";
    private static int GLOBAL_ID = -1;
    private static final ArrayList<SpecialItem> EVENTS = new ArrayList<>();
    
    public static int registerSpecialtem(SpecialItem specialItem){
        EVENTS.add(specialItem);
        return ++GLOBAL_ID;
    }
    
    public static ItemStack setSpecialtemID(ItemStack stack, int specialItemID){
        return NBTEditor.set(stack, specialItemID, ITEM_TAG);
    }
    public static int getSpecialItemID(ItemStack stack){
        if(NBTEditor.contains(stack, ITEM_TAG)){
            return NBTEditor.getInt(stack, ITEM_TAG);
        }else{
            return -1;
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    private static void onPlayerInteract(PlayerInteractEvent e){
        ItemStack stack = e.getItem();
        if(stack != null){
            int specialItemID = getSpecialItemID(stack);
            if(specialItemID >= 0 && specialItemID < EVENTS.size()){
                SpecialItem specialItem = EVENTS.get(specialItemID);
                if(specialItem != null){
                    specialItem.onPlayerInteract(e);
                }
            }
        }
    }
}
