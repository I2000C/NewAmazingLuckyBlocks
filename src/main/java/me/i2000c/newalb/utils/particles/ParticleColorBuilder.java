package me.i2000c.newalb.utils.particles;

import com.github.fierioziy.particlenativeapi.api.packet.ParticlePacket;
import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleTypeColorable;
import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleTypeNote;
import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleTypeRedstone;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.Tolerate;
import org.bukkit.Color;

@Accessors(chain = true)
@Getter
@Setter
public class ParticleColorBuilder extends ParticleBuilder {    
    private Color color;
    
    public ParticleColorBuilder(Object particleType) {
        super(particleType);
    }
    
    @Tolerate public ParticleColorBuilder setColor(int r, int g, int b) {
        return this.setColor(Color.fromRGB(r, g, b));
    }
    
    @Override
    protected ParticlePacket generatePacket() {
        ParticlePacket packet;
        if(color == null) {
            packet = super.generatePacket();
        } else {
            if(super.particleType instanceof ParticleTypeRedstone) {
                ParticleTypeRedstone type = (ParticleTypeRedstone) super.particleType;
                packet = type.packetColored(true, position, color);
            } else if(super.particleType instanceof ParticleTypeColorable) {
                ParticleTypeColorable type = (ParticleTypeColorable) super.particleType;
                packet = type.packetColored(true, position, color);
            } else if(super.particleType instanceof ParticleTypeNote) {
                ParticleTypeNote type = (ParticleTypeNote) super.particleType;
                packet = type.packetNote(true, position, color);
            } else {
                throw new IllegalArgumentException("Selected type is not colorable: " + super.particleType);
            }
        }
        
        return packet;
    }
}
