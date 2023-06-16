package me.i2000c.newalb.utils.particles;

// Info about particles: https://github.com/ByteZ1337/ParticleLib/blob/master/src/main/java/xyz/xenondevs/particle/ParticleEffect.java

import com.cryptomorin.xseries.ReflectionUtils;
import com.cryptomorin.xseries.particles.XParticle;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import static me.i2000c.newalb.utils.particles.ParticleProperty.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.util.NumberConversions;

public enum Particles {
    EXPLOSION_NORMAL(DIRECTIONAL),
    EXPLOSION_LARGE,
    EXPLOSION_HUGE,
    FIREWORKS_SPARK(DIRECTIONAL),
    WATER_BUBBLE(DIRECTIONAL, REQUIRES_WATER),
    WATER_SPLASH(DIRECTIONAL),
    WATER_WAKE(DIRECTIONAL),
    SUSPENDED(REQUIRES_WATER),
    SUSPENDED_DEPTH(DIRECTIONAL),
    CRIT(DIRECTIONAL),
    CRIT_MAGIC(DIRECTIONAL),
    SMOKE_NORMAL(DIRECTIONAL),
    SMOKE_LARGE(DIRECTIONAL),
    SPELL,
    SPELL_INSTANT,
    SPELL_MOB(COLORABLE),
    SPELL_MOB_AMBIENT(COLORABLE),
    SPELL_WITCH,
    DRIP_WATER,
    DRIP_LAVA,
    VILLAGER_ANGRY,
    VILLAGER_HAPPY(DIRECTIONAL),
    TOWN_AURA(DIRECTIONAL),
    NOTE(COLORABLE),
    PORTAL(DIRECTIONAL),
    ENCHANTMENT_TABLE(DIRECTIONAL),
    FLAME(DIRECTIONAL),
    LAVA,
    CLOUD(DIRECTIONAL),
    REDSTONE(COLORABLE),
    SNOWBALL,
    SNOW_SHOVEL(DIRECTIONAL),
    SLIME,
    HEART,
    ITEM_CRACK(DIRECTIONAL, REQUIRES_BLOCK),
    BLOCK_CRACK(REQUIRES_BLOCK),
    BLOCK_DUST(DIRECTIONAL, REQUIRES_BLOCK),
    WATER_DROP,
    MOB_APPEARANCE;
    
    private final List<ParticleProperty> properties;
    private final Object bukkitParticle;
    
    private Particles(ParticleProperty... properties) {
        this.properties = Arrays.asList(properties);
        Object bukkitParticle;
        try {
            bukkitParticle = XParticle.getParticle(this.name());
        } catch(Throwable ex) {
            bukkitParticle = null;
        }
        this.bukkitParticle = bukkitParticle;
    }
    
    public boolean isColorable() {
        return this.properties.contains(COLORABLE);
    }    
    public boolean isDirectional() {
        return this.properties.contains(DIRECTIONAL);
    }
    public boolean requiresBlock() {
        return this.properties.contains(REQUIRES_BLOCK);
    }
    public boolean requiresWater() {
        return this.properties.contains(REQUIRES_WATER);
    }
        
    private static Map<Particles, Object> enumParticles;
    private static Class enumParticleClass;
    private static Constructor packetConstructor;
    
    // Source: https://github.com/CryptoMorin/XSeries/wiki
    static {
        try {
            enumParticles = new EnumMap<>(Particles.class);
            enumParticleClass = ReflectionUtils.getNMSClass("EnumParticle");
            for(Object particle : enumParticleClass.getEnumConstants()) {
                try {
                    enumParticles.put(Particles.valueOf(particle.toString()), particle);
                } catch(Exception ex) {}
            }
            
            Class packetClass = ReflectionUtils.getNMSClass("PacketPlayOutWorldParticles");
            packetConstructor = packetClass.getConstructor(enumParticleClass,
                            // Long Distance: If true, particle distance increases from 256 to 65536
                            boolean.class,
                            // x, y, z
                            float.class, float.class, float.class,
                            // Offset x, y, z
                            float.class, float.class, float.class,
                            // Particle Data
                            float.class,
                            // Amount  // Data https://wiki.vg/Protocol#Particle
                            int.class, int[].class);
        } catch(Exception ex) {}
    }
    
    public <T> void spawn(Location loc, double offsetX, double offsetY, double offsetZ, double extra, int count, T datas, Player... players) {
        try {
            int[] packetExtraData;
            if(datas instanceof MaterialData) {
                MaterialData materialData = (MaterialData) datas;
                int id = materialData.getItemTypeId();
                byte data = materialData.getData();
                packetExtraData = new int[]{id, data};
            } else if(datas instanceof Float) {
                float floatData = (Float) datas;
                packetExtraData = new int[]{(int)floatData};
            } else {
                packetExtraData = new int[]{0};
            }
            
            Object particle = enumParticles.get(this);
            float x = (float) loc.getX();
            float y = (float) loc.getY();
            float z = (float) loc.getZ();
            Object packet = packetConstructor.newInstance(particle, false, 
                    x, y, z, 
                    (float)offsetX, (float)offsetY, (float)offsetZ,
                    (float)extra, count, packetExtraData);
            
            List<Player> targetPlayers;
            if(players == null || players.length == 0) {
                targetPlayers = loc.getWorld().getPlayers();
            } else {
                targetPlayers = Arrays.asList(players);
            }
            targetPlayers.forEach((player) -> {
                Location first = player.getLocation();
                double distanceSquared =
                        NumberConversions.square(first.getX() - loc.getX()) +
                        NumberConversions.square(first.getY() - loc.getY()) +
                        NumberConversions.square(first.getZ() - loc.getZ());
                if(distanceSquared < 32 * 32) {
                    ReflectionUtils.sendPacket(player, packet);
                }
            });
        } catch(Exception ex) {}
    }
    
    public Object getBukkitParticle() {
        return this.bukkitParticle;
    }
}
