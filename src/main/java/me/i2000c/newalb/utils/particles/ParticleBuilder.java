package me.i2000c.newalb.utils.particles;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.utils.particles.data.ColoredParticleData;
import me.i2000c.newalb.utils.particles.data.MaterialParticleData;
import me.i2000c.newalb.utils.particles.data.NoteParticleData;
import me.i2000c.newalb.utils.particles.data.ParticleData;
import me.i2000c.newalb.utils2.CustomColor;
import me.i2000c.newalb.utils2.RandomUtils;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;

public class ParticleBuilder {
    
    private final Particles particleType;
    private final Particle particle;
    
    public ParticleBuilder(Particles particleType) {
        this.particleType = particleType;
        this.particle = new Particle(particleType);
    }
    
    public ParticleBuilder withOffsetX(double offsetX) {
        this.particle.setOffsetX(offsetX);
        return this;
    }
    public ParticleBuilder withOffsetY(double offsetY) {
        this.particle.setOffsetY(offsetY);
        return this;
    }
    public ParticleBuilder withOffsetZ(double offsetZ) {
        this.particle.setOffsetZ(offsetZ);
        return this;
    }
    public ParticleBuilder withOffset(double offsetX, double offsetY, double offsetZ) {
        this.particle.setOffsetX(offsetX);
        this.particle.setOffsetY(offsetX);
        this.particle.setOffsetZ(offsetX);
        return this;
    }
    
    public ParticleBuilder withCount(int count) {
        this.particle.setCount(count);
        return this;
    }
    
    public ParticleBuilder withSpeed(double speed) {
        this.particle.setSpeed(speed);
        return this;
    }
    
    public ParticleBuilder withDirectional() {
        if(this.particleType.getProperties().contains(ParticleProperty.DIRECTIONAL)) {
            this.particle.setCount(0);
        }
        return this;
    }
    
    public ParticleBuilder withColor(int r, int g, int b) {
        if(this.particleType.getProperties().contains(ParticleProperty.COLORABLE)) {
            ParticleData data = new ColoredParticleData(r, g, b);
            this.particle.setData(data);
        }        
        return this;
    }
    public ParticleBuilder withColor(CustomColor color) {
        Color bukkitColor = color.getBukkitColor();
        return this.withColor(bukkitColor.getRed(), bukkitColor.getGreen(), bukkitColor.getBlue());
    }
    public ParticleBuilder withRandomColor() {
        return this.withColor(new CustomColor());
    }
    
    public ParticleBuilder withNoteColor(int note) {
        if(this.particleType.getProperties().contains(ParticleProperty.NOTE_COLORABLE)) {
            ParticleData data = new NoteParticleData(note);
            this.particle.setData(data);
            this.particle.setCount(1);
        }
        return this;
    }
    public ParticleBuilder withRandomNoteColor() {
        int randomNote = RandomUtils.getInt(0, 24);
        return this.withNoteColor(randomNote);
    }
    
    public ParticleBuilder withItemTexture(ItemStack item) {
        ParticleData data = null;
        if(item != null) {
            if(this.particleType.getProperties().contains(ParticleProperty.REQUIRES_ITEM)) {
                data = new MaterialParticleData(item, false);
            } else if(this.particleType.getProperties().contains(ParticleProperty.REQUIRES_BLOCK) && item.getType().isBlock()) {
                data = new MaterialParticleData(item, true);
            }
        }
        this.particle.setData(data);        
        return this;
    }
    public ParticleBuilder withItemTexture(XMaterial material) {
        return withItemTexture(material.parseItem());
    }
    
    public Particle build() {
        return this.particle.clone();
    }
}
