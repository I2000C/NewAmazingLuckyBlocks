package me.i2000c.newalb.utils.particles;

import com.cryptomorin.xseries.XMaterial;
import com.github.fierioziy.particlenativeapi.api.packet.ParticlePacket;
import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleType;
import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleTypeBlockMotion;
import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleTypeItemMotion;
import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleTypeMotion;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

@Accessors(chain = true)
@Getter
@Setter
public class ParticleMaterialBuilder extends ParticleMotionBuilder {
    private XMaterial material;
    
    public ParticleMaterialBuilder(Object particleType) {
        super(particleType);
    }
    
    @Override
    public ParticleMaterialBuilder setDirection(Vector direction) {
        super.setDirection(direction);
        return this;
    }
    @Override
    public ParticleMaterialBuilder setDirection(double x, double y, double z) {
        super.setDirection(x, y, z);
        return this.setDirection(new Vector(x, y, z));
    }
    
    @Override
    public ParticleMaterialBuilder setOffset(Vector offset) {
        super.setOffset(offset);
        return this;
    }
    @Override
    public ParticleMaterialBuilder setOffset(double x, double y, double z) {
        super.setOffset(x, y, z);
        return this;
    }
    @Override
    public ParticleMaterialBuilder setCount(int count) {
        super.setCount(count);
        return this;
    }
    @Override
    public ParticleMaterialBuilder setSpeed(double speed) {
        super.setSpeed(speed);
        return this;
    }
    
    @Override
    public ParticleMaterialBuilder setPosition(Location position) {
        super.setPosition(position);
        return this;
    }
    @Override
    public ParticleMaterialBuilder setPosition(World w, double x, double y, double z) {
        super.setPosition(w, x, y, z);
        return this;
    }
    
    @Override
    public ParticleMaterialBuilder setRepeatAmount(int repeatAmount) {
        super.setRepeatAmount(repeatAmount);
        return this;
    }
    
    @Override
    protected ParticlePacket generatePacket() {
        ParticleType type;
        if(super.particleType instanceof ParticleTypeItemMotion) {
            type = ((ParticleTypeItemMotion) super.particleType)
                    .of(material.parseMaterial());
        } else if(super.particleType instanceof ParticleTypeBlockMotion) {
            type = ((ParticleTypeBlockMotion) super.particleType)
                    .of(material.parseMaterial(), material.getData());
        } else {
            throw new IllegalArgumentException("Selected type is not a material particle: " + super.particleType);
        }
        
        return type.packet(true, position, offset.getX(), offset.getY(), offset.getZ(), speed, count);
    }
    
    @Override
    protected ParticlePacket generatePacketMotion() {
        ParticleTypeMotion type;
        if(super.particleType instanceof ParticleTypeItemMotion) {
            type = ((ParticleTypeItemMotion) super.particleType)
                    .of(material.parseMaterial());
        } else if(super.particleType instanceof ParticleTypeBlockMotion) {
            type = ((ParticleTypeBlockMotion) super.particleType)
                    .of(material.parseMaterial(), material.getData());
        } else {
            throw new IllegalArgumentException("Selected type is not a material particle: " + super.particleType);
        }
        
        return type.packetMotion(true, position, direction.getX(), direction.getY(), direction.getZ());
    }
}
