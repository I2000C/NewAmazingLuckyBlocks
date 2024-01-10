package me.i2000c.newalb.utils.particles;

import com.github.fierioziy.particlenativeapi.api.packet.ParticlePacket;
import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleType;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.Tolerate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Accessors(chain = true)
@Getter
@Setter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ParticleBuilder {
    protected static final double PARTICLE_PACKET_RADIUS = 100.0;
    
    @Getter(AccessLevel.NONE)
    protected final Object particleType;
    
    protected Location position;
    protected Vector offset = new Vector(1, 1, 1);
    protected double speed = 1.0;    
    protected int count = 10;
    protected int repeatAmount = 1;
    
    @Tolerate public ParticleBuilder setPosition(World w, double x, double y, double z) {
        return this.setPosition(new Location(w, x, y, z));
    }
    
    @Tolerate public ParticleBuilder setOffset(double x, double y, double z) {
        return this.setOffset(new Vector(x, y, z));
    }
    
    protected ParticlePacket generatePacket() {
        ParticleType type = (ParticleType) particleType;
        return type.packet(true, position, offset.getX(), offset.getY(), offset.getZ(), speed, count);
    }
    
    protected ParticlePacket generatePacketMotion() {
        throw new IllegalArgumentException("This particle doesn't support display motion");
    }
    
    
    
    
    private List<Player> getActualPlayers(Player... players) {
        List<Player> targetPlayers;
        if(players == null || players.length == 0) {
            targetPlayers = position.getWorld().getPlayers();
        } else {
            targetPlayers = Arrays.asList(players);
        }
        return targetPlayers;
    }
    
    public final void display(Player... players) {
        ParticlePacket packet = generatePacket();
        for(int i=0; i<repeatAmount; i++) {
            packet.sendInRadiusTo(getActualPlayers(players), PARTICLE_PACKET_RADIUS);
        }
    }
    
    public final void displayMotion(Player... players) {
        ParticlePacket packet = generatePacketMotion();
        for(int i=0; i<repeatAmount; i++) {
            packet.sendInRadiusTo(getActualPlayers(players), PARTICLE_PACKET_RADIUS);
        }
    }
}
