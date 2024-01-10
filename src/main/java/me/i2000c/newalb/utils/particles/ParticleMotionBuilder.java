package me.i2000c.newalb.utils.particles;

import com.github.fierioziy.particlenativeapi.api.packet.ParticlePacket;
import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleTypeMotion;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.util.Vector;

@Accessors(chain = true)
public class ParticleMotionBuilder extends ParticleBuilder {
    
    @Getter
    protected boolean isDirectional;
    
    @Getter
    protected Vector direction;
    
    public ParticleMotionBuilder(Object particleType) {
        super(particleType);
    }
    
    public ParticleMotionBuilder setDirection(Vector direction) {
        this.isDirectional = true;
        this.direction = direction.clone();
        return this;
    }
    public ParticleMotionBuilder setDirection(double x, double y, double z) {
        this.isDirectional = true;
        return this.setDirection(new Vector(x, y, z));
    }
    
    @Override
    public ParticleMotionBuilder setOffset(Vector offset) {
        this.isDirectional = false;
        super.setOffset(offset);
        return this;
    }
    @Override
    public ParticleMotionBuilder setOffset(double x, double y, double z) {
        this.isDirectional = false;
        super.setOffset(x, y, z);
        return this;
    }
    @Override
    public ParticleMotionBuilder setCount(int count) {
        this.isDirectional = false;
        super.setCount(count);
        return this;
    }
    @Override
    public ParticleMotionBuilder setSpeed(double speed) {
        this.isDirectional = false;
        super.setSpeed(speed);
        return this;
    }
    
    @Override
    protected ParticlePacket generatePacketMotion() {
        ParticleTypeMotion type = (ParticleTypeMotion) particleType;
        ParticlePacket packet = type.packetMotion(true, position, direction.getX(), direction.getY(), direction.getZ());
        return packet;
    }
}
