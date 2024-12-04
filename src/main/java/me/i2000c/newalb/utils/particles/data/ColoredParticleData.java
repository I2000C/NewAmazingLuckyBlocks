package me.i2000c.newalb.utils.particles.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.i2000c.newalb.reflection.ReflectionManager;
import org.bukkit.Color;
import xyz.xenondevs.particle.data.color.RegularColor;

@Getter
@Setter
@AllArgsConstructor
public class ColoredParticleData extends ParticleData {
    
    private int red;
    private int green;
    private int blue;

    @Override
    public xyz.xenondevs.particle.data.ParticleData convertToParticleLibData() {
        return new RegularColor(red, green, blue);
    }

    @Override
    public <T> T convertToBukkitParticleData() {
        Object dustOptions = ReflectionManager.callConstructor("org.bukkit.Particle$DustOptions", Color.fromRGB(red, green, blue), 1.0f);
        return (T) dustOptions;
    }    
}
