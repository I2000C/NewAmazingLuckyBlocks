package me.i2000c.newalb.utils.particles;

import com.github.fierioziy.particlenativeapi.api.ParticleNativeAPI;
import com.github.fierioziy.particlenativeapi.api.particle.ParticleList_1_8;
import com.github.fierioziy.particlenativeapi.core.ParticleNativeCore;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.reflection.RefClass;
import me.i2000c.newalb.reflection.RefField;
import me.i2000c.newalb.reflection.ReflectionManager;

public class Particles<T extends ParticleBuilder> {
    protected static final ParticleNativeAPI PARTICLE_API = ParticleNativeCore.loadAPI(NewAmazingLuckyBlocks.getInstance());
    protected static final ParticleList_1_8 PARTICLE_LIST = PARTICLE_API.LIST_1_8;
    
    public static final Particles<ParticleBuilder> BARRIER                 = new Particles<>(ParticleBuilder.class);
    public static final Particles<ParticleMaterialBuilder> BLOCK_CRACK     = new Particles<>(ParticleMaterialBuilder.class);
    public static final Particles<ParticleMaterialBuilder> BLOCK_DUST      = new Particles<>(ParticleMaterialBuilder.class);
    public static final Particles<ParticleMotionBuilder> CLOUD             = new Particles<>(ParticleMotionBuilder.class);
    public static final Particles<ParticleMotionBuilder> CRIT              = new Particles<>(ParticleMotionBuilder.class);
    public static final Particles<ParticleMotionBuilder> CRIT_MAGIC        = new Particles<>(ParticleMotionBuilder.class);
    public static final Particles<ParticleBuilder> DRIP_LAVA               = new Particles<>(ParticleBuilder.class);
    public static final Particles<ParticleBuilder> DRIP_WATER              = new Particles<>(ParticleBuilder.class);
    public static final Particles<ParticleMotionBuilder> ENCHANTMENT_TABLE = new Particles<>(ParticleMotionBuilder.class);
    public static final Particles<ParticleBuilder> EXPLOSION_HUGE         = new Particles<>(ParticleBuilder.class);
    public static final Particles<ParticleBuilder> EXPLOSION_LARGE        = new Particles<>(ParticleBuilder.class);
    public static final Particles<ParticleMotionBuilder> EXPLOSION_NORMAL = new Particles<>(ParticleMotionBuilder.class);
    public static final Particles<ParticleMotionBuilder> FIREWORKS_SPARK   = new Particles<>(ParticleMotionBuilder.class);
    public static final Particles<ParticleMotionBuilder> FLAME             = new Particles<>(ParticleMotionBuilder.class);
    public static final Particles<ParticleBuilder> HEART                   = new Particles<>(ParticleBuilder.class);
    public static final Particles<ParticleMaterialBuilder> ITEM_CRACK      = new Particles<>(ParticleMaterialBuilder.class);
    public static final Particles<ParticleBuilder> LAVA                    = new Particles<>(ParticleBuilder.class);
    public static final Particles<ParticleBuilder> MOB_APPEARANCE          = new Particles<>(ParticleBuilder.class);
    public static final Particles<ParticleColorBuilder> NOTE               = new Particles<>(ParticleColorBuilder.class);
    public static final Particles<ParticleMotionBuilder> PORTAL            = new Particles<>(ParticleMotionBuilder.class);
    public static final Particles<ParticleColorBuilder> REDSTONE           = new Particles<>(ParticleColorBuilder.class);
    public static final Particles<ParticleBuilder> SLIME                   = new Particles<>(ParticleBuilder.class);
    public static final Particles<ParticleMotionBuilder> SMOKE_LARGE       = new Particles<>(ParticleMotionBuilder.class);
    public static final Particles<ParticleMotionBuilder> SMOKE_NORMAL      = new Particles<>(ParticleMotionBuilder.class);
    public static final Particles<ParticleBuilder> SNOWBALL                = new Particles<>(ParticleBuilder.class);
    public static final Particles<ParticleMotionBuilder> SNOW_SHOVEL       = new Particles<>(ParticleMotionBuilder.class);
    public static final Particles<ParticleBuilder> SPELL                   = new Particles<>(ParticleBuilder.class);
    public static final Particles<ParticleBuilder> SPELL_INSTANT           = new Particles<>(ParticleBuilder.class);
    public static final Particles<ParticleColorBuilder> SPELL_MOB          = new Particles<>(ParticleColorBuilder.class);
    public static final Particles<ParticleColorBuilder> SPELL_MOB_AMBIENT  = new Particles<>(ParticleColorBuilder.class);
    public static final Particles<ParticleBuilder> SPELL_WITCH             = new Particles<>(ParticleBuilder.class);
    public static final Particles<ParticleBuilder> SUSPENDED               = new Particles<>(ParticleBuilder.class);
    public static final Particles<ParticleBuilder> TOWN_AURA               = new Particles<>(ParticleBuilder.class);
    public static final Particles<ParticleBuilder> VILLAGER_HAPPY          = new Particles<>(ParticleBuilder.class);
    public static final Particles<ParticleBuilder> VILLAGER_ANGRY          = new Particles<>(ParticleBuilder.class);
    public static final Particles<ParticleMotionBuilder> WATER_BUBBLE      = new Particles<>(ParticleMotionBuilder.class);
    public static final Particles<ParticleBuilder> WATER_SPLASH            = new Particles<>(ParticleBuilder.class);
    public static final Particles<ParticleMotionBuilder> WATER_WAKE        = new Particles<>(ParticleMotionBuilder.class);
    public static final Particles<ParticleBuilder> WATER_DROP              = new Particles<>(ParticleBuilder.class);
    
    private Object particleType;
    private final Class<T> builderClass;
    
    private Particles(Class<T> builderClass) {
        this.builderClass = builderClass;
    }
    
    public final T create() {
        return ReflectionManager.callConstructor(builderClass, particleType);
    }
    
    static {
        ReflectionManager.getCachedClass(Particles.class).getFields().forEach((refField) -> {
            String name = refField.getActualField().getName();
            RefField particleTypeField = RefClass.of(PARTICLE_LIST.getClass()).getField(name);
            if(particleTypeField != null) {
                Particles particles = (Particles) refField.getStaticValue();
                particles.particleType = particleTypeField.getValue(PARTICLE_LIST);
            }
        });
    }
}
