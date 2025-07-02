package me.i2000c.newalb.utils.particles;

import lombok.Data;
import lombok.SneakyThrows;
import me.i2000c.newalb.utils.particles.data.ParticleData;
import org.bukkit.Location;

@Data
public class Particle implements Cloneable {
    
    private final Particles particleType;
    
    private double offsetX = 0;
    private double offsetY = 0;
    private double offsetZ = 0;
    
    private double speed = 1;
    private int count = 0;
    
    private ParticleData data = null;
    
    public void displayAt(Location loc) {
        particleType.getSpawningStrategy().spawnParticle(loc, count, offsetX, offsetY, offsetZ, speed, data);
    }
    
    @SneakyThrows(CloneNotSupportedException.class)
    @SuppressWarnings("CloneDeclaresCloneNotSupported")
    @Override
    public Particle clone() {
        return (Particle) super.clone();
    }
}
