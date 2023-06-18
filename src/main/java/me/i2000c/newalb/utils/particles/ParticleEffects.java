package me.i2000c.newalb.utils.particles;

/**
 * Source code from: https://github.com/CryptoMorin/XSeries/blob/master/src/main/java/com/cryptomorin/xseries/particles/XParticle.java
 *  adapted to support 1.8 particles
 */
public class ParticleEffects {
    private static final double PII = 2 * Math.PI;
    
    /**
     * Spawn a sphere.
     * Tutorial: https://www.spigotmc.org/threads/146338/
     * Also uses its own unique directional pattern.
     *
     * @param radius the circle radius.
     * @param rate   the rate of cirlce points/particles.
     *
     * @see #circle(double, double, ParticleBuilder)
     * @since 1.0.0
     */
    public static void sphere(double radius, double rate, ParticleBuilder pb) {
        // Cache
        double rateDiv = Math.PI / rate;

        // To make a sphere we're going to generate multiple circles
        // next to each other.
        for (double phi = 0; phi <= Math.PI; phi += rateDiv) {
            // Cache
            double y1 = radius * Math.cos(phi);
            double y2 = radius * Math.sin(phi);

            for (double theta = 0; theta <= PII; theta += rateDiv) {
                double x = Math.cos(theta) * y2;
                double z = Math.sin(theta) * y2;

                if (pb.isDirectional()) {
                    // We're going to do the same thing from spreading circle.
                    // Since this is a 3D shape we'll need to get the y value as well.
                    // I'm not sure if this is the right way to do it.
                    double omega = Math.atan2(z, x);
                    double directionX = Math.cos(omega);
                    double directionY = Math.sin(Math.atan2(y2, y1));
                    double directionZ = Math.sin(omega);

                    pb.setOffset(directionX, directionY, directionZ);
                }

                pb.display(x, y1, z);
            }
        }
    }
}
