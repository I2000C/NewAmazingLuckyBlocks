package me.i2000c.newalb.listeners.objects;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.particles.Particles;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.MetadataManager;
import me.i2000c.newalb.utils2.RandomUtils;
import me.i2000c.newalb.utils2.Task;
import me.i2000c.newalb.utils2.WorldGuardManager;
import org.bukkit.Location;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class MiniVolcano extends SpecialItem{
    
    private int defaultHeight;
    private ItemBuilder defaultBaseMaterial;
    private ItemBuilder defaultLavaMaterial;
    private long defaultTicks;
    private long defaultBeforeTicks;
    private boolean defaultSquared;
    
    private boolean defaultEnableThrowBlocks;
    private int throwBlocksNumber;
    private double throwBlocksHeight;
    private double throwBlocksRadius;
    private ItemBuilder throwBlocksMaterial;
    private long throwBlocksTicks;
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
            e.setCancelled(true);
            
            super.decreaseAmountOfItem(e);
            
            this.execute(e.getPlayer(), e.getClickedBlock().getLocation());
        }
    }
    
    @Override
    public ItemStack buildItem(){
        this.defaultHeight = ConfigManager.getConfig().getInt(super.itemPathKey + ".height");
        this.defaultBaseMaterial = ItemBuilder.newItem(ConfigManager.getConfig().getString(super.itemPathKey + ".base-material"));
        this.defaultLavaMaterial = ItemBuilder.newItem(ConfigManager.getConfig().getString(super.itemPathKey + ".lava-material"));
        this.defaultTicks = ConfigManager.getConfig().getLong(super.itemPathKey + ".time-between-one-block-and-the-next");
        this.defaultBeforeTicks = ConfigManager.getConfig().getLong(super.itemPathKey + ".time-before-minivolcano");
        this.defaultSquared = ConfigManager.getConfig().getBoolean(super.itemPathKey + ".squared");
        
        this.defaultEnableThrowBlocks = ConfigManager.getConfig().getBoolean(super.itemPathKey + ".throwBlocks.enable");
        this.throwBlocksNumber = ConfigManager.getConfig().getInt(super.itemPathKey + ".throwBlocks.number-of-blocks");
        this.throwBlocksHeight = ConfigManager.getConfig().getDouble(super.itemPathKey + ".throwBlocks.height");
        this.throwBlocksRadius = ConfigManager.getConfig().getDouble(super.itemPathKey + ".throwBlocks.radius");
        this.throwBlocksMaterial = ItemBuilder.newItem(ConfigManager.getConfig().getString(super.itemPathKey + ".throwBlocks.material"));
        this.throwBlocksTicks = ConfigManager.getConfig().getLong(super.itemPathKey + ".throwBlocks.time-between-blocks");
        
        return ItemBuilder.newItem(XMaterial.LAVA_BUCKET).build();
    }
    
    public void execute(Player player, Location location) {
        execute(player, location, 
                defaultHeight, 
                defaultBaseMaterial, defaultLavaMaterial, 
                defaultTicks, defaultBeforeTicks, 
                defaultSquared, defaultEnableThrowBlocks);
    }
    
    public void execute(Player player, Location location, 
                        int height, 
                        ItemBuilder baseMaterial, ItemBuilder lavaMaterial, 
                        long ticks, long beforeTicks, 
                        boolean squared, boolean throwBlocks) {
        
        //<editor-fold defaultstate="collapsed" desc="Code">
        XSound.ENTITY_TNT_PRIMED.play(location, 2.0f, 1.0f);
        
        Particles.SMOKE_LARGE.create()
                .setPosition(location)
                .setOffset(0.5, 0.5, 0.5)
                .setSpeed(0.5)
                .setCount(100)
                .display();
        
        Task task = new Task() {
            final Location center = location.clone();
            int currentHeight = 1;
            
            @Override
            public void run() {
                if(currentHeight > height) {
                    cancel();
                    Location loc = center.clone().add(0, currentHeight, 0);
                    lavaMaterial.placeAt(loc);
                    
                    if(throwBlocks) {
                        executeThrowBlocks(loc, lavaMaterial);
                    }
                    return;
                }
                
                for(int i=1; i<=currentHeight; i++) {
                    int lavaRadius = currentHeight-i;
                    int wallRadius = lavaRadius+1;
                    int lavaRadiusSquared = lavaRadius * lavaRadius;
                    int wallRadiusSquared = wallRadius * wallRadius;
                    
                    Location currentCenter = center.clone().add(0, i, 0);
                    for(int bx=-wallRadius; bx<=+wallRadius; bx++) {
                        for(int bz=-wallRadius; bz<=+wallRadius; bz++) {
                            Location loc = currentCenter.clone().add(bx, 0, bz);
                            double distanceSquared = loc.distanceSquared(currentCenter);
                            if(squared || distanceSquared <= wallRadiusSquared) {
                                if(WorldGuardManager.canBuild(player, loc)) {
                                    if(distanceSquared <= lavaRadiusSquared) {
                                        lavaMaterial.placeAt(loc);
                                    } else {
                                        baseMaterial.placeAt(loc);
                                    }
                                }
                            }
                        }
                    }
                }
                
                currentHeight++;
            }
        };
        task.runTask(beforeTicks, ticks);
//</editor-fold>
    }
    
    public void executeThrowBlocks(Location location, ItemBuilder lavaMaterial) {
        this.executeThrowBlocks(location, 
                                throwBlocksNumber, 
                                throwBlocksHeight, 
                                throwBlocksRadius, 
                                throwBlocksMaterial, lavaMaterial, 
                                throwBlocksTicks);
    }
    
    public void executeThrowBlocks(Location location, 
                                   int numberOfBlocks, 
                                   double height, 
                                   double radius, 
                                   ItemBuilder blocksMaterial, ItemBuilder lavaMaterial, 
                                   long ticks) {
        
        //<editor-fold defaultstate="collapsed" desc="Code">
        Task task = new Task() {
            final Location baseLoc = location.clone();
            final double angleIncrement = 360.0 / numberOfBlocks;
            
            int blocks = 0;
            double angle360 = RandomUtils.getInt(360);
            
            @Override
            public void run() {
                if(blocks >= numberOfBlocks) {
                    cancel();
                    return;
                }
                
                double angleRadians = Math.toRadians(angle360);
                angle360 += angleIncrement;
                
                double dx = radius * Math.sin(angleRadians);
                double dy = height;
                double dz = radius * Math.cos(angleRadians);
                Location loc = baseLoc.clone().add(dx, dy, dz);
                Vector speed = loc.toVector().subtract(baseLoc.toVector());
                
                FallingBlock fb = throwBlocksMaterial.spawnFallingBlock(loc);
                fb.setDropItem(false);
                fb.setVelocity(speed);
                MetadataManager.setClassMetadata(fb, MiniVolcano.this);
                MetadataManager.setCustomMetadata(fb, lavaMaterial);
                
                blocks++;
            }
        };
        task.runTask(0L, ticks);
//</editor-fold>
    }

    @Override
    public void onFallingBlockConvert(EntityChangeBlockEvent e) {
        if(MetadataManager.hasCustomMetadata(e.getEntity())) {
            ItemBuilder lavaMaterial = MetadataManager.getCustomMetadata(e.getEntity());
            e.setCancelled(true);
            lavaMaterial.placeAt(e.getBlock());
        }
    }
}
