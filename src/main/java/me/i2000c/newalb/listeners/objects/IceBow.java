package me.i2000c.newalb.listeners.objects;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import me.i2000c.newalb.listeners.interact.CustomProjectileHitEvent;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.MetadataManager;
import me.i2000c.newalb.utils2.Task;
import me.i2000c.newalb.utils2.WorldGuardManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

public class IceBow extends SpecialItem{
    
    private static final int ICE_CAGE_HEIGHT = 3;
    
    private ItemBuilder iceItem;
    private boolean protectStructures;
    private long ticks;
    private long beforeTicks;
    private int snowRadius;
    private boolean generateSnow;
    private boolean disableArrowKnockback;
    
    @Override
    public ItemStack buildItem(){
        this.iceItem = ItemBuilder.newItem(ConfigManager.getConfig().getString(super.itemPathKey + ".freeze-material"));
        this.protectStructures = ConfigManager.getConfig().getBoolean(super.itemPathKey + ".protect-structures");
        this.ticks = ConfigManager.getConfig().getLong(super.itemPathKey + ".time-between-one-block-and-the-next");
        this.beforeTicks = ConfigManager.getConfig().getLong(super.itemPathKey + ".time-before-freezing");
        this.snowRadius = ConfigManager.getConfig().getInt(super.itemPathKey + ".snowRadius");
        this.generateSnow = ConfigManager.getConfig().getBoolean(super.itemPathKey + ".generateSnow");
        this.disableArrowKnockback = ConfigManager.getConfig().getBoolean(super.itemPathKey + ".disableArrowKnockback");
        
        return ItemBuilder.newItem(XMaterial.BOW)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .build();
    }
    
    @Override
    public void onArrowHit(CustomProjectileHitEvent e){
        Projectile projectile = e.getProjectile();
        if(projectile instanceof Arrow){
            Arrow arrow = (Arrow) projectile;
            Entity hitEntity = e.getHitEntity();
            Block hitBlock = e.getHitBlock();
            
            if(hitEntity != null){
                if(disableArrowKnockback){
                    // Remove knockback but not damage
                    e.setCancelled(true);
                    ((Damageable) hitEntity).damage(e.getDamage(), arrow);
                    
                    // Teleport entity to the center of its block
                    Location loc = hitEntity.getLocation();
                    loc.setX(loc.getBlockX() + 0.5);
                    loc.setZ(loc.getBlockZ() + 0.5);
                    hitEntity.teleport(loc);
                }
                
                hitEntity.setFireTicks(0);
                execute((Player) e.getShooter(), hitEntity);
            }else if(hitBlock != null) {
                if(generateSnow) {
                    simulateSnow((Player) e.getShooter(), hitBlock.getLocation(), snowRadius);
                }
            }
            
            arrow.remove();
        }
    }
    
    @Override
    public void onArrowShooted(EntityShootBowEvent e){
        MetadataManager.setClassMetadata(e.getProjectile(), this);
    }
    
    public void execute(Player player, Entity entity) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        final Location baseLocation = entity.getLocation().clone();
        
        Task task = new Task() {
            int currentHeight = 0;
            
            @Override
            public void run() {
                Location center = baseLocation.clone().add(0, currentHeight, 0);
                
                Location[] locs;
                if(currentHeight >= ICE_CAGE_HEIGHT-1) {
                    locs = new Location[] {center};
                } else {
                    locs = new Location[] {
                        center.clone().add(+1, 0, 0),
                        center.clone().add(-1, 0, 0),
                        center.clone().add(0, 0, +1),
                        center.clone().add(0, 0, -1),
                    };
                }
                
                for(Location loc : locs) {
                    Block block = loc.getBlock();
                    if(!protectStructures || block.getType() == Material.AIR) {
                        if(WorldGuardManager.canBuild(player, loc)) {
                            XSound.BLOCK_GLASS_BREAK.play(loc);
                            iceItem.placeAt(block);
                        }
                    }
                }
                
                if(++currentHeight >= ICE_CAGE_HEIGHT) {
                    cancel();
                }
            }
        };
        task.runTask(beforeTicks, ticks);
//</editor-fold>
    }
    
    private static void simulateSnow(Player player, Location loc, int radius){
        //<editor-fold defaultstate="collapsed" desc="Code">
        double radiusSq = radius * radius;
        int ox = loc.getBlockX();
        int oy = loc.getBlockY();
        int oz = loc.getBlockZ();
        
        for(int x = ox - radius; x <= ox + radius; x++){
            for(int z = oz - radius; z <= oz + radius; z++){
                Location l = new Location(loc.getWorld(), x, oy, z);
                if(loc.distanceSquared(l) <= radiusSq){
                    for(int y = oy - radius; y <= oy + radius; y++){
                        l.setY(y);
                        Block b = l.getBlock();
                        
                        if(!WorldGuardManager.canBuild(player, l)) {
                            continue;
                        }
                        
                        switch(b.getType()){
                            case AIR:
                                Material material = b.getRelative(0, -1, 0).getType();
                                if(material.isSolid()){
                                    b.setType(Material.SNOW);
                                }
                                break;
                            case WATER:
                            case STATIONARY_WATER:
                                b.setType(Material.ICE);
                                break;
                            case LAVA:
                            case STATIONARY_LAVA:
                                b.setType(Material.OBSIDIAN);
                                break;
                            case FIRE:
                                b.setType(Material.SNOW);
                                break;
                        }
                    }
                }
            }
        }
//</editor-fold>
    }
}
