package me.i2000c.newalb.utils.particles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.utils.particles.spawning.BukkitSpawningStrategy;
import me.i2000c.newalb.utils.particles.spawning.ParticleLibSpawningStrategy;
import me.i2000c.newalb.utils.particles.spawning.ParticleSpawningStrategy;
import xyz.xenondevs.particle.ParticleEffect;

/**
 * @see https://www.spigotmc.org/threads/particles-excel-list-from-1-9-to-1-20-6.650456/
 */
@Getter
public enum Particles {
    /** @see ParticleEffect#BARRIER */
    BARRIER(Arrays.asList("BLOCK_MARKER")),
    
    /** @see ParticleEffect#BLOCK_CRACK */
    BLOCK_CRACK(Arrays.asList("BLOCK"), ParticleProperty.REQUIRES_BLOCK),
    
    /** @see ParticleEffect#CLOUD */
    CLOUD(ParticleProperty.DIRECTIONAL),
    
    /** @see ParticleEffect#CRIT */
    CRIT(ParticleProperty.DIRECTIONAL),
    
    /** @see ParticleEffect#CRIT_MAGIC */
    CRIT_MAGIC(ParticleProperty.DIRECTIONAL),
    
    /** @see ParticleEffect#DRIP_LAVA */
    DRIP_LAVA,
    
    /** @see ParticleEffect#DRIP_WATER */
    DRIP_WATER,
    
    /** @see ParticleEffect#ENCHANTMENT_TABLE */
    ENCHANTMENT_TABLE(ParticleProperty.DIRECTIONAL),
    
    /** @see ParticleEffect#EXPLOSION_HUGE */
    EXPLOSION_HUGE,
    
    /** @see ParticleEffect#EXPLOSION_LARGE */
    EXPLOSION_LARGE,
    
    /** @see ParticleEffect#EXPLOSION_NORMAL */
    EXPLOSION_NORMAL(ParticleProperty.DIRECTIONAL),
    
    /** @see ParticleEffect#FIREWORKS_SPARK */
    FIREWORKS_SPARK(ParticleProperty.DIRECTIONAL),
    
    /** @see ParticleEffect#FLAME */
    FLAME(ParticleProperty.DIRECTIONAL),
    
    /** @see ParticleEffect#HEART */
    HEART,
    
    /** @see ParticleEffect#ITEM_CRACK */
    ITEM_CRACK(ParticleProperty.DIRECTIONAL, ParticleProperty.REQUIRES_ITEM),
    
    /** @see ParticleEffect#LAVA */
    LAVA,
    
    /** @see ParticleEffect#MOB_APPEARANCE */
    MOB_APPEARANCE,
    
    /** @see ParticleEffect#NOTE */
    NOTE(ParticleProperty.NOTE_COLORABLE),
    
    /** @see ParticleEffect#PORTAL */
    PORTAL(ParticleProperty.DIRECTIONAL),
    
    /** @see ParticleEffect#REDSTONE */
    REDSTONE(ParticleProperty.COLORABLE),
    
    /** @see ParticleEffect#SLIME */
    SLIME,
    
    /** @see ParticleEffect#SMOKE_LARGE */
    SMOKE_LARGE(ParticleProperty.DIRECTIONAL),
    
    /** @see ParticleEffect#SMOKE_NORMAL */
    SMOKE_NORMAL(ParticleProperty.DIRECTIONAL),
    
    /** @see ParticleEffect#SNOWBALL */
    SNOWBALL,
    
    /** @see ParticleEffect#SNOW_SHOVEL */
    SNOW_SHOVEL(Arrays.asList("POOF"), ParticleProperty.DIRECTIONAL),
    
    /** @see ParticleEffect#SPELL */
    SPELL,
    
    /** @see ParticleEffect#SPELL_INSTANT */
    SPELL_INSTANT,
    
    /** @see ParticleEffect#SPELL_MOB */
    SPELL_MOB(ParticleProperty.COLORABLE),
    
    /** @see ParticleEffect#SPELL_WITCH */
    SPELL_WITCH,
    
    /** @see ParticleEffect#SUSPENDED */
    SUSPENDED(ParticleProperty.REQUIRES_WATER),
    
    /** @see ParticleEffect#TOWN_AURA */
    TOWN_AURA(ParticleProperty.DIRECTIONAL),
    
    /** @see ParticleEffect#VILLAGER_ANGRY */
    VILLAGER_ANGRY,
    
    /** @see ParticleEffect#VILLAGER_HAPPY */
    VILLAGER_HAPPY(ParticleProperty.DIRECTIONAL),
    
    /** @see ParticleEffect#WATER_BUBBLE */
    WATER_BUBBLE(ParticleProperty.DIRECTIONAL, ParticleProperty.REQUIRES_WATER),
    
    /** @see ParticleEffect#WATER_DROP */
    WATER_DROP,
    
    /** @see ParticleEffect#WATER_SPLASH */
    WATER_SPLASH(ParticleProperty.DIRECTIONAL),
    
    /** @see ParticleEffect#WATER_WAKE */
    WATER_WAKE(ParticleProperty.DIRECTIONAL);
    
    
    
    private final List<String> alternativeNames;
    private final Set<ParticleProperty> properties;
    private final ParticleSpawningStrategy spawningStrategy;
    
    private Particles(ParticleProperty... properties) {
        this(Collections.emptyList(), properties);
    }
    
    private Particles(List<String> alternativeNames, ParticleProperty... properties) {
        this.alternativeNames = new ArrayList<>();
        this.alternativeNames.add(this.name());
        this.alternativeNames.addAll(alternativeNames);
        
        this.properties = EnumSet.noneOf(ParticleProperty.class);
        Arrays.stream(properties).forEach(this.properties::add);
        
        if(MinecraftVersion.CURRENT_VERSION.isLessThan(MinecraftVersion.v1_20)) {
            this.spawningStrategy = new ParticleLibSpawningStrategy(this);
        } else {
            this.spawningStrategy = new BukkitSpawningStrategy(this);
        }
    }
    
    public ParticleBuilder create() {
        return new ParticleBuilder(this);
    }
    
    public static Particles[] VALUES = values();
}
