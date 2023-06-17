package me.i2000c.newalb.utils.particles;

import com.cryptomorin.xseries.particles.ParticleDisplay;
import java.awt.Color;
import java.lang.reflect.Method;
import me.i2000c.newalb.MinecraftVersion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.PropertyType;
import xyz.xenondevs.particle.data.ParticleData;
import xyz.xenondevs.particle.data.color.DustData;
import xyz.xenondevs.particle.data.color.ParticleColor;
import xyz.xenondevs.particle.data.texture.ParticleTexture;

public class ParticleBuilder extends xyz.xenondevs.particle.ParticleBuilder {
    private static boolean isMinecraft_1_8;
    private static Method withBlock;
    static {
        try {
            ParticleDisplay pd = new ParticleDisplay();
            withBlock = pd.getClass().getMethod("withBlock", MaterialData.class);
            isMinecraft_1_8 = false;
        } catch(Throwable ex) {
            isMinecraft_1_8 = true;
        }
    }
    
    public static ParticleBuilder newParticle(Particles particles, Location loc) {
        return new ParticleBuilder(particles, loc);
    }
    public static ParticleBuilder newParticle(Particles particles) {
        return new ParticleBuilder(particles, null);
    }
    
    
    
    private ParticleDisplay pd;
        
    private ParticleBuilder(Particles particle, Location loc) {
        super(particle.getParticleEffect(), loc);
        if(isMinecraft_1_8) {
            this.pd = null;
        } else {
            this.pd = ParticleDisplay.simple(loc, (Particle) particle.getBukkitParticle());
        }
    }

    @Override
    public ParticleBuilder setColor(Color color) {
        super.setColor(color);
        if(!isMinecraft_1_8) {
            pd.withColor(color, 1);
        }
        return this;
    }
    
    @Override
    public ParticleBuilder setAmount(int amount) {
        super.setAmount(amount);
        if(!isMinecraft_1_8) {
            pd.withCount(amount);
        }
        return this;
    }

    @Override
    public ParticleBuilder setSpeed(float speed) {
        super.setSpeed(speed);
        if(!isMinecraft_1_8) {
            pd.withExtra(speed);
        }
        return this;
    }
    
    @Override
    public ParticleBuilder setOffset(Vector offset) {
        super.setOffset(offset);
        if(!isMinecraft_1_8) {
            pd.offset(offset);
        }
        return this;
    }
    @Override
    public ParticleBuilder setOffset(float offsetX, float offsetY, float offsetZ) {
        super.setOffset(offsetX, offsetY, offsetZ);
        if(!isMinecraft_1_8) {
            pd.offset(offsetX, offsetY, offsetZ);
        }
        return this;
    }
    @Override
    public ParticleBuilder setOffsetX(float offsetX) {
        super.setOffsetX(offsetX);
        if(!isMinecraft_1_8) {
            pd.offset(super.getOffsetX(), super.getOffsetY(), super.getOffsetZ());
        }
        return this;
    }
    @Override
    public ParticleBuilder setOffsetY(float offsetY) {
        super.setOffsetY(offsetY);
        if(!isMinecraft_1_8) {
            pd.offset(super.getOffsetX(), super.getOffsetY(), super.getOffsetZ());
        }
        return this;
    }
    @Override
    public ParticleBuilder setOffsetZ(float offsetZ) {
        super.setOffsetZ(offsetZ);
        if(!isMinecraft_1_8) {
            pd.offset(super.getOffsetX(), super.getOffsetY(), super.getOffsetZ());
        }
        return this;
    }

    @Override
    public ParticleBuilder setLocation(Location location) {
        super.setLocation(location);
        if(!isMinecraft_1_8) {
            pd.withLocation(location);
        }
        return this;
    }
    
    @Override
    public ParticleBuilder setParticleData(ParticleData particleData) {
        super.setParticleData(particleData);
        if(!isMinecraft_1_8) {
            if(particleData instanceof ParticleColor) {
                float red = ((ParticleColor) particleData).getRed() * 255f;
                float green = ((ParticleColor) particleData).getGreen() * 255f;
                float blue = ((ParticleColor) particleData).getBlue() * 255f;
                float size = 2f;
                if(particleData instanceof DustData) {
                    size = ((DustData) particleData).getSize();
                }
                pd.withColor((int) red, (int) green, (int) blue, size);
            } else if(particleData instanceof ParticleTexture) {
                MaterialData md;
                Material material = ((ParticleTexture) particleData).getMaterial();
                if(MinecraftVersion.getCurrentVersion().isLegacyVersion()) {
                    byte data = ((ParticleTexture) particleData).getData();
                    md = new MaterialData(material, data);
                } else {
                    md = new MaterialData(material);
                }
                if(super.getParticle().hasProperty(PropertyType.REQUIRES_ITEM)) {
                    ItemStack item = new ItemStack(md.getItemType());
                    if(MinecraftVersion.getCurrentVersion().isLegacyVersion()) {
                        item.setData(md);
                    }
                    pd.withItem(item);
                } else if(super.getParticle().hasProperty(PropertyType.REQUIRES_BLOCK)) {
                    try {
                        withBlock.invoke(pd, md);
                    } catch(Exception ex) {
                        throw new Error(ex);
                    }
                }
            }
        }
        return this;
    }
    
    @Override
    public void display() {
        if(isMinecraft_1_8) {
            super.display();
        } else {
            pd.spawn();
        }
    }    
    public void display(Location loc) {
        if(isMinecraft_1_8) {
            Location oldLoc = super.getLocation();
            super.setLocation(loc);
            super.display();
            super.setLocation(oldLoc);
        } else {
            pd.spawn(loc);
        }
    }
}
