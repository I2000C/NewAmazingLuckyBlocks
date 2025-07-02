package me.i2000c.newalb.utils.particles.spawning;

import me.i2000c.newalb.utils.particles.Particles;
import me.i2000c.newalb.utils.particles.data.ParticleData;
import org.bukkit.Location;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.ParticlePacket;
import xyz.xenondevs.particle.utils.ReflectionUtils;

public class ParticleLibSpawningStrategy extends ParticleSpawningStrategy {
    
    private final ParticleEffect particleEffect;
    
    public ParticleLibSpawningStrategy(Particles particleType) {
        super(particleType);
        this.particleEffect = ParticleEffect.valueOf(particleType.name());
    }
    
    @Override
    public void spawnParticle(Location loc, int count, double offsetX, double offsetY, double offsetZ, double extra, ParticleData data) {
        xyz.xenondevs.particle.data.ParticleData convertedData = data == null ? null : data.convertToParticleLibData();
        if(!particleEffect.isCorrectData(convertedData)) {
            return;
        }
        
        if(convertedData != null) {
            convertedData.setEffect(particleEffect);
        }
        
        ParticlePacket packet = new ParticlePacket(particleEffect, (float) offsetX, (float) offsetY, (float) offsetZ, (float) extra, count, convertedData);
        Object nmsPacket = packet.createPacket(loc);
        loc.getWorld().getPlayers().forEach(p -> ReflectionUtils.sendPacket(p, nmsPacket));
    }
    
}
