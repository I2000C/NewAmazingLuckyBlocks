package me.i2000c.newalb.listeners.chat;

import java.util.HashMap;
import java.util.Map;

import me.i2000c.newalb.api.functions.ChatFunction;
import me.i2000c.newalb.utils.tasks.Task;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener{
    private static final Map<Player, ChatFunction> players = new HashMap<>();
    
    public static void registerPlayer(Player player, ChatFunction function){
        registerPlayer(player, function, true);
    }
    
    public static void registerPlayer(Player player, ChatFunction function, boolean autoRemove){
        if(autoRemove){
            players.put(player, (message) -> {
                players.remove(player);
                function.accept(message);
            });
        }else{
            players.put(player, function);
        }
    }
    
    public static void removePlayer(Player player){
        players.remove(player);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    private static void onPlayerChat(AsyncPlayerChatEvent e){
        if(players.isEmpty()){
            return;
        }
        
        ChatFunction function = players.get(e.getPlayer());
        if(function != null){
            e.setCancelled(true);
            Task.runTask(() -> function.accept(e.getMessage()));
        }
    }
}
