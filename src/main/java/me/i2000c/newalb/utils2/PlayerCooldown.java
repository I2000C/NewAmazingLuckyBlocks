package me.i2000c.newalb.utils2;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

public class PlayerCooldown {
    private final long cooldownSeconds;
    private final Map<UUID, Long> cooldownMap;
    
    public PlayerCooldown(long cooldownSeconds) {
        this.cooldownSeconds = cooldownSeconds;
        this.cooldownMap = new HashMap<>();
    }
    
    public void updateCooldown(Player player) {
        if(cooldownSeconds <= 0) {
            return;
        }
        
        long cooldownExpireTimeMS = System.currentTimeMillis() + cooldownSeconds*1000;
        cooldownMap.put(player.getUniqueId(), cooldownExpireTimeMS);
    }
    
    public boolean isCooldownExpired(Player player) {
        long cooldownValue = cooldownMap.getOrDefault(player.getUniqueId(), 0L);
        return System.currentTimeMillis() > cooldownValue;
    }
    
    public void clear() {
        cooldownMap.clear();
    }
    
    public double getRemainingSeconds(Player player) {
        long cooldownValue = cooldownMap.getOrDefault(player.getUniqueId(), 0L);
        long remainingTime = cooldownValue - System.currentTimeMillis();
        if(remainingTime < 0){
            remainingTime = 0L;
        }
        
        return remainingTime / 1000.0;
    }
}
