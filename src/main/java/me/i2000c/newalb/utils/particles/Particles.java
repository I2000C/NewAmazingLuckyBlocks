package me.i2000c.newalb.utils.particles;

// Info about particles: https://github.com/ByteZ1337/ParticleLib/blob/master/src/main/java/xyz/xenondevs/particle/ParticleEffect.java

import com.cryptomorin.xseries.particles.XParticle;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.PropertyType;

public enum Particles {
    BLOCK_CRACK,
    BLOCK_DUST,
    CLOUD,
    CRIT,
    CRIT_MAGIC,
    DRIP_LAVA,
    DRIP_WATER,
    ENCHANTMENT_TABLE,
    EXPLOSION_HUGE,
    EXPLOSION_LARGE,
    EXPLOSION_NORMAL,
    FIREWORKS_SPARK,
    FLAME,
    HEART,
    ITEM_CRACK,
    LAVA,
    MOB_APPEARANCE,
    NOTE,
    PORTAL,
    REDSTONE,
    SLIME,
    SMOKE_LARGE,
    SMOKE_NORMAL,
    SNOWBALL,
    SNOW_SHOVEL,
    SPELL,
    SPELL_INSTANT,
    SPELL_MOB,
    SPELL_MOB_AMBIENT,
    SPELL_WITCH,
    SUSPENDED,
    TOWN_AURA,
    VILLAGER_HAPPY,
    VILLAGER_ANGRY,
    WATER_BUBBLE,
    WATER_SPLASH,
    WATER_WAKE;
    
    private final ParticleEffect particleEffect;
    private final Object bukkitParticle;
    
    private Particles() {
        this.particleEffect = ParticleEffect.valueOf(this.name());
        Object aux;
        try {
            aux = XParticle.getParticle(this.name());
        } catch(Throwable ex) {
            aux = null;
        }
        this.bukkitParticle = aux;
    }
    
    public boolean hasPropery(PropertyType property) {
        return this.particleEffect.hasProperty(property);
    }
    
    ParticleEffect getParticleEffect() {
        return this.particleEffect;
    }
    
    Object getBukkitParticle() {
        return this.bukkitParticle;
    }
}
