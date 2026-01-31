package me.i2000c.newalb.listeners.textures;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.i2000c.newalb.utils.tasks.Task;
import me.i2000c.newalb.utils.textures.Texture;

public class TextureCacher implements Listener {
    
    private static void cacheTexture(Player player) {
        Texture.of(player).createItem();
    }
    
    public static void initialCaching() {
        if(!Bukkit.getOnlineMode()) {
            Task.runTaskAsynchronously(() -> Bukkit.getOnlinePlayers().forEach(TextureCacher::cacheTexture));
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private static void onPlayerJoin(PlayerJoinEvent event) {
        if(!Bukkit.getOnlineMode()) {
            Task.runTaskAsynchronously(() -> cacheTexture(event.getPlayer()));
        }
    }
}
