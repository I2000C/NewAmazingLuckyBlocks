package me.i2000c.newalb.utils.particles.spawning;

import lombok.RequiredArgsConstructor;
import me.i2000c.newalb.utils.particles.Particles;
import me.i2000c.newalb.utils.particles.data.ParticleData;
import org.bukkit.Location;

@RequiredArgsConstructor
public abstract class ParticleSpawningStrategy {
    
    protected final Particles partileType;
    
    public abstract void spawnParticle(Location loc, int count, double offsetX, double offsetY, double offsetZ, double extra, ParticleData data);
    
}
