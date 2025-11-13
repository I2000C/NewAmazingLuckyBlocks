package me.i2000c.newalb.listeners.objects;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.integration.WorldGuardManager;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.misc.OtherUtils;
import me.i2000c.newalb.utils.tasks.Task;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@Getter
public class DarkHole extends SpecialItem {
    
    private final Set<XMaterial> noBreakBlockMaterials = EnumSet.noneOf(XMaterial.class);
    private boolean enableBlockStopMode;
    private int defaultDepth;
    private int defaultRadius;
    private long defaultTicks;
    private long defaultBeforeTicks;
    private boolean defaultSquared;
    
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
        this.defaultDepth = ConfigManager.getMainConfig().getInt(super.itemPathKey + ".depth");
        this.defaultRadius = ConfigManager.getMainConfig().getInt(super.itemPathKey + ".radius");
        this.defaultTicks = ConfigManager.getMainConfig().getLong(super.itemPathKey + ".time-between-one-block-and-the-next");
        this.defaultBeforeTicks = ConfigManager.getMainConfig().getLong(super.itemPathKey + ".time-before-darkhole");
        this.defaultSquared = ConfigManager.getMainConfig().getBoolean(super.itemPathKey + ".squared");
        
        this.enableBlockStopMode = ConfigManager.getMainConfig().getBoolean(super.itemPathKey + ".block-stop-mode.enable");
        this.noBreakBlockMaterials.clear();
        List<String> materials = ConfigManager.getMainConfig().getStringList(super.itemPathKey + ".block-stop-mode.block-list");
        materials.forEach(material -> {
            Optional<XMaterial> xmaterial = XMaterial.matchXMaterial(material);
            if(xmaterial.isPresent()) {
                this.noBreakBlockMaterials.add(xmaterial.get());
            } else {
                Logger.warn("Invalid material in config: " + material);
            }
        });
        
        return ItemStackWrapper.newItem(XMaterial.BUCKET).toItemStack();
    }
    
    public void execute(Player player, Location location) {
        execute(player, location, 
                defaultDepth, defaultRadius, 
                defaultTicks, defaultBeforeTicks, 
                defaultSquared);
    }
    
    public void execute(Player player, Location location, 
                        int depth, int radius, 
                        long ticks, long beforeTicks, 
                        boolean squared) {
        
        //<editor-fold defaultstate="collapsed" desc="Code">
        XSound.ENTITY_WITHER_AMBIENT.play(location, 2.0f, 1.0f);
        
        // pre 1.9 effects are here: https://www.spigotmc.org/wiki/effect-list-1-8-8/
        location.getWorld().playEffect(location, Effect.ENDER_SIGNAL, 100);
        
        Task task = new Task() {
            final int minY = OtherUtils.getMinWorldHeight(location.getWorld());
            final Location srcLoc = location.clone();
            final int radiusSquared = radius*radius;
            
            int currentDepth = 0;
            
            @Override
            public void run() {
                Location center = srcLoc.clone().add(0, -currentDepth, 0);
                if((currentDepth >= depth && depth > 0) || center.getBlockY() < minY) {
                    cancel();
                    return;
                }
                
                for(int bx=-radius; bx<=+radius; bx++) {
                    for(int bz=-radius; bz<=+radius; bz++) {
                        Location loc = center.clone().add(bx, 0, bz);
                        if(squared || loc.distanceSquared(center) <= radiusSquared) {
                            Block b = loc.getBlock();
                            XMaterial xmaterial = XMaterial.matchXMaterial(b.getType());
                            if(enableBlockStopMode && noBreakBlockMaterials.contains(xmaterial)) {
                                cancel();
                                return;
                            } else {
                                if(WorldGuardManager.canBreak(player, loc)) {
                                    b.setType(XMaterial.AIR.parseMaterial());
                                }
                            }
                        }
                    }
                }
                
                currentDepth++;
            }
        };
        task.runTask(beforeTicks, ticks);
//</editor-fold>
    }
}
