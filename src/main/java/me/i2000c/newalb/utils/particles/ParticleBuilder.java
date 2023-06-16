//package me.i2000c.newalb.utils.particles;
//
//import com.cryptomorin.xseries.particles.ParticleDisplay;
//import java.lang.reflect.Method;
//import me.i2000c.newalb.utils2.CustomColor;
//import org.bukkit.Color;
//import org.bukkit.Location;
//import org.bukkit.Particle;
//import org.bukkit.World;
//import org.bukkit.material.MaterialData;
//import org.bukkit.util.Vector;
//
//public class ParticleBuilder {
//    private static boolean isMinecraft_1_8;
//    private static Method withBlock;
//    static {
//        try {
//            ParticleDisplay pd = new ParticleDisplay();
//            withBlock = pd.getClass().getMethod("withBlock", MaterialData.class);
//            isMinecraft_1_8 = false;
//        } catch(Throwable ex) {
//            isMinecraft_1_8 = true;
//        }
//    }
//    
//    public static ParticleBuilder newParticle(Location location, Particles particles) {
//        return new ParticleBuilder(location, particles);
//    }
//    public static ParticleBuilder newParticle(Particles particles) {
//        return new ParticleBuilder(null, particles);
//    }
//    
//    private Location loc;
//    private Particles particles;
//    private CustomColor color;
//    private int count;
//    private double extra;
//    private Vector offset;
//    private Vector rotation;
//    private MaterialData materialData;
//    private ParticleDisplay cachedPD;
//    
//    private ParticleBuilder(Location loc, Particles particles) {
//        this.loc = loc;
//        this.particles = particles;
//        this.color = null;
//        this.count = 1;
//        this.extra = 0;
//        this.offset = new Vector();
//        this.rotation = new Vector();
//        this.materialData = null;
//        this.cachedPD = null;
//    }
//    
//    public ParticleBuilder withLocation(Location loc) {
//        this.loc = loc.clone();
//        return this;
//    }
//    public Location getLocation() {
//        return this.loc.clone();
//    }
//    public ParticleBuilder withParticles(Particles particles) {
//        this.particles = particles;
//        return this;
//    }
//    public Particles getParticles() {
//        return this.particles;
//    }
//    
//    public ParticleBuilder withRandomColor() {
//        this.color = new CustomColor();
//        return this;
//    }
//    public ParticleBuilder withColor(Color color) {
//        this.color = new CustomColor(color);
//        return this;
//    }
//    public ParticleBuilder withColor(CustomColor color) {
//        this.color = color;
//        return this;
//    }
//    public ParticleBuilder withColor(int r, int g, int b) {
//        return withColor(Color.fromRGB(r, g, b));
//    }
//    public ParticleBuilder withColor(String hex) {
//        return withColor(new CustomColor(hex));
//    }
//    public CustomColor getColor() {
//        return color;
//    }
//    
//    public ParticleBuilder withCount(int count) {
//        this.count = count >= 0 ? count : 1;
//        return this;
//    }
//    public int getCount() {
//        return this.count;
//    }
//    public ParticleBuilder setDirectional() {
//        this.count = 0;
//        return this;
//    }
//    public boolean isDirectional() {
//        return this.count == 0;
//    }
//    
//    public ParticleBuilder withOffset(double offsetX, double offsetY, double offsetZ) {
//        this.offset = new Vector(offsetX, offsetY, offsetZ);
//        return this;
//    }
//    public Vector getOffset() {
//        return this.offset;
//    }
//    
//    public ParticleBuilder withExtra(double extra) {
//        this.extra = extra;
//        return this;
//    }
//    public double getExtra() {
//        return this.extra;
//    }
//    
//    public ParticleBuilder withMaterialData(MaterialData materialData) {
//        this.materialData = materialData;
//        return this;
//    }
//    public MaterialData getMaterialData() {
//        return this.materialData;
//    }
//    
//    public void resetCachedParticleDisplay() {
//        this.cachedPD = null;
//    }
//    
//    public void spawn() {
//        if(this.loc == null) {
//            throw new IllegalArgumentException("Location cannot be null");
//        }
//        spawn(this.loc);
//    }
//    public void spawn(World w, double x, double y, double z) {
//        spawn(new Location(w, x, y, z));
//    }
//    public void spawn(double x, double y, double z) {
//        if(this.loc == null) {
//            throw new IllegalArgumentException("Location cannot be null");
//        }
//        spawn(new Location(this.loc.getWorld(), x, y, z));
//    }
//    public void spawn(Location loc) {        
//        if(isMinecraft_1_8) {
//            if(this.particles.isColorable() && this.color != null) {
//                int r = this.color.getBukkitColor().getRed();
//                int g = this.color.getBukkitColor().getGreen();
//                int b = this.color.getBukkitColor().getBlue();
//                this.particles.spawn(loc, r/255.0, g/255.0, b/255.0, 2, count, 0);
//            } else if(this.particles.requiresBlock() && this.materialData != null) {
//                this.particles.spawn(loc, offsetX, offsetY, offsetZ, extra, count, materialData.getItemTypeId(), materialData.getData());
//            } else {
//                this.particles.spawn(loc, offsetX, offsetY, offsetZ, extra, count, 0);
//            }
//        } else {
//            if(cachedPD == null) {
//                ParticleDisplay pd = ParticleDisplay
//                    .simple(loc, Particle.valueOf(this.particles.name()))
//                    .withExtra(this.extra)
//                    .offset(this.offset)
//                    .rotate(this.rotation)
//                    .withCount(this.count);
//                if(this.color != null) {
//                    int r = this.color.getBukkitColor().getRed();
//                    int g = this.color.getBukkitColor().getGreen();
//                    int b = this.color.getBukkitColor().getBlue();
//                    pd.withColor(r, g, b, 1);
//                }
//                if(this.materialData != null) {
//                    try {
//                        withBlock.invoke(pd, this.materialData);
//                    } catch(Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }
//                cachedPD = pd;
//            }
//            
//            cachedPD.spawn();
//        }
//    }
//
//    @Override
//    public String toString() {
//        return "ParticleBuilder{" + "loc=" + loc + ", particles=" + particles + 
//                ", color=" + color + ", count=" + count + ", extra=" + extra + 
//                ", offsetX=" + offsetX + ", offsetY=" + offsetY + ", offsetZ=" + offsetZ + 
//                ", materialData=" + materialData + '}';
//    }    
//}
