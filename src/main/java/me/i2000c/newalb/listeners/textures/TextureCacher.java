package me.i2000c.newalb.listeners.textures;

import java.time.Duration;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.i2000c.newalb.utils.tasks.Task;
import me.i2000c.newalb.utils.textures.Texture;

public class TextureCacher implements Listener {
    
    /**
     * Cache refreshing period plus some seconds to avoid race conditions.<br>
     * XSeries caches player textures for a duration of 6h before invalidate them.<br>
     * Texture caching is only used if using offline mode servers
     * 
     * @see {@link com.cryptomorin.xseries.profiles.objects.cache.TimedCacheableProfileable}
     * @see {@link com.cryptomorin.xseries.profiles.objects.Profileable.PlayerProfileable}
     */
    private static final Duration CACHE_REFRESH_PERIOD = Duration.ofHours(6).plusSeconds(10);
    
    private static void cacheTexture(Player player) {
        Texture.of(player).createItem();
    }
    
    public static void scheduleCaching() {
        if(!Bukkit.getOnlineMode()) {
            long periodTicks = CACHE_REFRESH_PERIOD.getSeconds() * 20;
            Task.runTaskAsynchronously(() -> Bukkit.getOnlinePlayers().forEach(TextureCacher::cacheTexture), 0L, periodTicks);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private static void onPlayerJoin(PlayerJoinEvent event) {
        if(!Bukkit.getOnlineMode()) {
            Task.runTaskAsynchronously(() -> cacheTexture(event.getPlayer()));
        }
    }
}
