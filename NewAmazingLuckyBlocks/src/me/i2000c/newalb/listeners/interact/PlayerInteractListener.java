package me.i2000c.newalb.listeners.interact;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener{
    private static final String ITEM_TAG = "NewAmazingLuckyBlocks.SpecialItem";
    private static int FUNCTION_ID = -1;
    private static final Map<Integer, InteractFunction> EVENTS = new HashMap<>();
    
    public static void registerEvent(InteractFunction function){
        EVENTS.put(++FUNCTION_ID, function);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private static void onPlayerInteract(PlayerInteractEvent e){
        ItemStack stack = e.getItem();
        if(stack != null && NBTEditor.contains(stack, ITEM_TAG)){
            int specialItemID = NBTEditor.getInt(stack, ITEM_TAG);
            InteractFunction function = EVENTS.get(specialItemID);
            if(function != null){
                function.execute(e);
            }
        }
    }
}
