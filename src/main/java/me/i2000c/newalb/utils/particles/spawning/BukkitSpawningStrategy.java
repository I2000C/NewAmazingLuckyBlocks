package me.i2000c.newalb.utils.particles.spawning;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.utils.particles.Particles;
import me.i2000c.newalb.utils.particles.data.ColoredParticleData;
import me.i2000c.newalb.utils.particles.data.MaterialParticleData;
import me.i2000c.newalb.utils.particles.data.ParticleData;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

/**
 * @see https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Particle.html
 * @see https://www.spigotmc.org/threads/comprehensive-particle-spawning-guide-1-13-1-19.343001/
 * @see https://www.spigotmc.org/threads/particles-excel-list-from-1-9-to-1-20-6.650456/
 * @see https://github.com/Fierioziy/ParticleNativeAPI/blob/master/ParticleNativeAPI-api/src/main/java/com/github/fierioziy/particlenativeapi/api/particle/ParticleList_1_13.java
 */
public class BukkitSpawningStrategy extends ParticleSpawningStrategy {
    
    private final Particle bukkitParticle;
    
    public BukkitSpawningStrategy(Particles particleType) {
        super(particleType);
        
        Exception lastException = null;
        Particle particle = null;
        for(String name : particleType.getAlternativeNames()) {
            try {
                particle = Particle.valueOf(name);
            } catch(IllegalArgumentException ex) {
                lastException = ex;
            }
        }
        
        if(particle == null) {
            throw new InternalError(lastException);
        } else {
            this.bukkitParticle = particle;
        }
    }

    @Override
    public void spawnParticle(Location loc, int count, double offsetX, double offsetY, double offsetZ, double extra, ParticleData data) {
        if(super.partileType == Particles.BARRIER) {
            data = new MaterialParticleData(XMaterial.BARRIER, true);
        }
        
        Object convertedData = null;
        if(data != null) {
            switch(super.partileType) {
                case SPELL_MOB:
                case NOTE:
                    ColoredParticleData cpd = (ColoredParticleData) data;
                    offsetX = cpd.getRed() / 255.0;
                    offsetY = cpd.getGreen() / 255.0;
                    offsetZ = cpd.getBlue() / 255.0;
                    count = 0;
                    extra = 1;
                    if(super.partileType == Particles.SPELL_MOB) {
                        // Particle type SPELL_MOB requires a color data despite of not using it
                        convertedData = Color.fromRGB(0);
                    }
                    break;
                default:
                    convertedData = data.convertToBukkitParticleData();
            }
        }
        
        loc.getWorld().spawnParticle(bukkitParticle, loc, count, offsetX, offsetY, offsetZ, extra, convertedData);
    }
    
}
