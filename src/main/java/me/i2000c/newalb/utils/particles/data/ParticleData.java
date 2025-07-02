package me.i2000c.newalb.utils.particles.data;

public abstract class ParticleData {
    
    public abstract xyz.xenondevs.particle.data.ParticleData convertToParticleLibData();
    
    public abstract <T> T convertToBukkitParticleData();
}
