package me.i2000c.newalb.utils.particles;

import com.cryptomorin.xseries.particles.ParticleDisplay;
import com.cryptomorin.xseries.particles.XParticle;
import java.awt.Color;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.reflection.ReflectionManager;
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
    private static final boolean IS_MINECRAFT_1_8 = MinecraftVersion.CURRENT_VERSION == MinecraftVersion.v1_8;
    
    public static ParticleBuilder newParticle(Particles particles, Location loc) {
        return new ParticleBuilder(particles, loc);
    }
    public static ParticleBuilder newParticle(Particles particles) {
        return new ParticleBuilder(particles, null);
    }
    
    
    
    private final ParticleDisplay pd;
        
    private ParticleBuilder(Particles particle, Location loc) {
        super(particle.getParticleEffect(), loc);
        if(IS_MINECRAFT_1_8) {
            this.pd = null;
        } else {
            this.pd = ParticleDisplay.simple(loc, (Particle) particle.getBukkitParticle());
        }
    }

    @Override
    public ParticleBuilder setColor(Color color) {
        super.setColor(color);
        if(!IS_MINECRAFT_1_8) {
            pd.withColor(color, 1);
        }
        return this;
    }
    
    @Override
    public ParticleBuilder setAmount(int amount) {
        super.setAmount(amount);
        if(!IS_MINECRAFT_1_8) {
            pd.withCount(amount);
        }
        return this;
    }
    
    public ParticleBuilder setDirectional() {
        return this.setAmount(0);
    }
    public boolean isDirectional() {
        return this.getAmount() == 0;
    }

    @Override
    public ParticleBuilder setSpeed(float speed) {
        super.setSpeed(speed);
        if(!IS_MINECRAFT_1_8) {
            pd.withExtra(speed);
        }
        return this;
    }
    
    @Override
    public ParticleBuilder setOffset(Vector offset) {
        super.setOffset(offset);
        if(!IS_MINECRAFT_1_8) {
            pd.offset(offset);
        }
        return this;
    }
    @Override
    public ParticleBuilder setOffset(float offsetX, float offsetY, float offsetZ) {
        super.setOffset(offsetX, offsetY, offsetZ);
        if(!IS_MINECRAFT_1_8) {
            pd.offset(offsetX, offsetY, offsetZ);
        }
        return this;
    }
    @Override
    public ParticleBuilder setOffsetX(float offsetX) {
        super.setOffsetX(offsetX);
        if(!IS_MINECRAFT_1_8) {
            pd.offset(super.getOffsetX(), super.getOffsetY(), super.getOffsetZ());
        }
        return this;
    }
    @Override
    public ParticleBuilder setOffsetY(float offsetY) {
        super.setOffsetY(offsetY);
        if(!IS_MINECRAFT_1_8) {
            pd.offset(super.getOffsetX(), super.getOffsetY(), super.getOffsetZ());
        }
        return this;
    }
    @Override
    public ParticleBuilder setOffsetZ(float offsetZ) {
        super.setOffsetZ(offsetZ);
        if(!IS_MINECRAFT_1_8) {
            pd.offset(super.getOffsetX(), super.getOffsetY(), super.getOffsetZ());
        }
        return this;
    }
    public ParticleBuilder setOffset(double offsetX, double offsetY, double offsetZ) {
        return setOffset((float) offsetX, (float) offsetY, (float) offsetZ);
    }
    public ParticleBuilder setOffsetX(double offsetX) {
        return setOffsetX((float) offsetX);
    }
    public ParticleBuilder setOffsetY(double offsetY) {
        return setOffsetY((float) offsetY);
    }
    public ParticleBuilder setOffsetZ(double offsetZ) {
        return setOffsetZ((float) offsetZ);
    }

    @Override
    public ParticleBuilder setLocation(Location location) {
        super.setLocation(location);
        if(!IS_MINECRAFT_1_8) {
            pd.withLocation(location);
        }
        return this;
    }
    
    @Override
    public ParticleBuilder setParticleData(ParticleData particleData) {
        super.setParticleData(particleData);
        if(!IS_MINECRAFT_1_8) {
            if(particleData instanceof ParticleColor) {
                float red = ((ParticleColor) particleData).getRed() * 255f;
                float green = ((ParticleColor) particleData).getGreen() * 255f;
                float blue = ((ParticleColor) particleData).getBlue() * 255f;
                float size = 2f;
                if(particleData instanceof DustData) {
                    size = ((DustData) particleData).getSize();
                }
                pd.withColor((int) red, (int) green, (int) blue, size);
                
                // If the particle has color, set the amount to 0
                this.setAmount(0);
            } else if(particleData instanceof ParticleTexture) {
                MaterialData md;
                Material material = ((ParticleTexture) particleData).getMaterial();
                if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()) {
                    byte data = ((ParticleTexture) particleData).getData();
                    md = new MaterialData(material, data);
                } else {
                    md = new MaterialData(material);
                }
                if(super.getParticle().hasProperty(PropertyType.REQUIRES_ITEM)) {
                    ItemStack item = new ItemStack(md.getItemType());
                    if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()) {
                        item.setData(md);
                    }
                    pd.withItem(item);
                } else if(super.getParticle().hasProperty(PropertyType.REQUIRES_BLOCK)) {
                    if(!IS_MINECRAFT_1_8) {
                        ReflectionManager.callMethod(pd, "withBlock", md);
                    }
                }
            }
        }
        return this;
    }
    
    @Override
    public void display() {
        if(IS_MINECRAFT_1_8) {
            super.display();
        } else {
            pd.spawn();
        }
    }    
    public void display(Location loc) {
        if(IS_MINECRAFT_1_8) {
            Location oldLoc = super.getLocation();
            super.setLocation(loc);
            super.display();
            super.setLocation(oldLoc);
        } else {
            pd.spawn(loc);
        }
    }
    public void display(double x, double y, double z) {        
        Location loc = super.getLocation();
        if(loc == null) {
            throw new IllegalArgumentException("Spawn location cannot be null");
        }
        display(rotate(loc, x, y, z));
    }
    
    ///////////////////////
    // Rotation code from:
    //   https://github.com/CryptoMorin/XSeries/blob/master/src/main/java/com/cryptomorin/xseries/particles/ParticleDisplay.java
    ///////////////////////
    
    private Vector rotation = null;
    
    /**
     * Rotates the particle position based on this vector.
     *
     * @param vector the vector to rotate from. The xyz values of this vector must be radians.
     * @see #rotate(double, double, double)
     * @since 1.0.0
     */
    @Nonnull
    public ParticleBuilder rotate(@Nonnull Vector vector) {
        Objects.requireNonNull(vector, "Cannot rotate ParticleDisplay with null vector");
        if (rotation == null) rotation = vector;
        else rotation.add(vector);
        return this;
    }

    /**
     * Rotates the particle position based on the xyz radians.
     * Rotations are only supported for some shapes in {@link XParticle}.
     * Rotating some of them can result in weird shapes.
     *
     * @see #rotate(Vector)
     * @since 3.0.0
     */
    @Nonnull
    public ParticleBuilder rotate(double x, double y, double z) {
        return rotate(new Vector(x, y, z));
    }

    /**
     * Rotates the given xyz with the given rotation radians and
     * adds them to the specified location.
     *
     * @param location the location to add the rotated axis.
     * @return a cloned rotated location.
     * @since 3.0.0
     */
    @Nonnull
    public Location rotate(@Nonnull Location location, double x, double y, double z) {
        if (location == null) throw new IllegalStateException("Attempting to spawn particle when no location is set");
        if (rotation == null) return cloneLocation(location).add(x, y, z);

        Vector rotate = new Vector(x, y, z);
        rotateAround(rotate, Axis.X, rotation);
        rotateAround(rotate, Axis.Y, rotation);
        rotateAround(rotate, Axis.Z, rotation);

        return cloneLocation(location).add(rotate);
    }
    
    /**
     * Rotates the given location vector around a certain axis.
     *
     * @param location the location to rotate.
     * @param axis     the axis to rotate the location around.
     * @param rotation the rotation vector that contains the degrees of the rotation. The number is taken from this vector according to the given axis.
     * @since 7.0.0
     */
    public static Vector rotateAround(@Nonnull Vector location, @Nonnull Axis axis, @Nonnull Vector rotation) {
        Objects.requireNonNull(axis, "Cannot rotate around null axis");
        Objects.requireNonNull(rotation, "Rotation vector cannot be null");

        switch (axis) {
            case X:
                return rotateAround(location, axis, rotation.getX());
            case Y:
                return rotateAround(location, axis, rotation.getY());
            case Z:
                return rotateAround(location, axis, rotation.getZ());
            default:
                throw new AssertionError("Unknown rotation axis: " + axis);
        }
    }

    /**
     * Rotates the given location vector around a certain axis.
     *
     * @param location the location to rotate.
     * @since 7.0.0
     */
    public static Vector rotateAround(@Nonnull Vector location, double x, double y, double z) {
        rotateAround(location, Axis.X, x);
        rotateAround(location, Axis.Y, y);
        rotateAround(location, Axis.Z, z);
        return location;
    }

    /**
     * Rotates the given location vector around a certain axis.
     *
     * @param location the location to rotate.
     * @param axis     the axis to rotate the location around.
     * @since 7.0.0
     */
    public static Vector rotateAround(@Nonnull Vector location, @Nonnull Axis axis, double angle) {
        Objects.requireNonNull(location, "Cannot rotate a null location");
        Objects.requireNonNull(axis, "Cannot rotate around null axis");
        if (angle == 0) return location;

        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        switch (axis) {
            case X: {
                double y = location.getY() * cos - location.getZ() * sin;
                double z = location.getY() * sin + location.getZ() * cos;
                return location.setY(y).setZ(z);
            }
            case Y: {
                double x = location.getX() * cos + location.getZ() * sin;
                double z = location.getX() * -sin + location.getZ() * cos;
                return location.setX(x).setZ(z);
            }
            case Z: {
                double x = location.getX() * cos - location.getY() * sin;
                double y = location.getX() * sin + location.getY() * cos;
                return location.setX(x).setY(y);
            }
            default:
                throw new AssertionError("Unknown rotation axis: " + axis);
        }
    }
    
    /**
     * Clones the location of this particle display and adds xyz.
     *
     * @param x the x to add to the location.
     * @param y the y to add to the location.
     * @param z the z to add to the location.
     * @return the cloned location.
     * @see #clone()
     * @since 1.0.0
     */
    @Nullable
    public Location cloneLocation(double x, double y, double z) {
        Location loc = super.getLocation();
        return loc == null ? null : cloneLocation(loc).add(x, y, z);
    }
    /**
     * We don't want to use {@link Location#clone()} since it doesn't copy to constructor and Java's clone method
     * is known to be inefficient and broken.
     *
     * @since 3.0.3
     */
    @Nonnull
    private static Location cloneLocation(@Nonnull Location location) {
        return new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
    
    /**
     * As an alternative to {@link org.bukkit.Axis} because it doesn't exist in 1.12
     *
     * @since 7.0.0
     */
    private enum Axis {X, Y, Z}
}
