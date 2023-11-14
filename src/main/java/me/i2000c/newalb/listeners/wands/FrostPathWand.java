package me.i2000c.newalb.listeners.wands;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import java.util.EnumSet;
import java.util.Set;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.Task;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class FrostPathWand extends SpecialItem{
    
    private static final Set<Material> PASSABLE_BLOCKS = EnumSet.of(XMaterial.AIR.parseMaterial(), 
                                                                    XMaterial.WATER.parseMaterial(), 
                                                                    XMaterial.LAVA.parseMaterial(), 
                                                                    XMaterial.GRASS.parseMaterial());
    
    private float minPitch;
    private float maxPitch;
    private ItemBuilder frostItem;
    private int rowsOfBlocksEachTime;
    private int maxBlocks;
    private int rowWidth;
    private int rowHalfWidth;
    private long ticks;
    private long beforeTicks;
    
    @Override
    public ItemStack buildItem(){
        this.minPitch = (float) ConfigManager.getConfig().getDouble(super.itemPathKey + ".minPitch");
        this.maxPitch = (float) ConfigManager.getConfig().getDouble(super.itemPathKey + ".maxPitch");
        this.frostItem = ItemBuilder.newItem(ConfigManager.getConfig().getString(super.itemPathKey + ".frostMaterial"));
        this.rowsOfBlocksEachTime = ConfigManager.getConfig().getInt(super.itemPathKey + ".rows-of-blocks-each-time");
        this.maxBlocks = ConfigManager.getConfig().getInt(super.itemPathKey + ".maxBlocks");
        this.rowWidth = ConfigManager.getConfig().getInt(super.itemPathKey + ".row-width");
        this.ticks = ConfigManager.getConfig().getLong(super.itemPathKey + ".time-between-one-block-and-the-next");
        this.beforeTicks = ConfigManager.getConfig().getLong(super.itemPathKey + ".time-before-frostpath");
        
        if(this.rowWidth < 2) {
            this.rowHalfWidth = 1;
        } else {
            this.rowHalfWidth = (this.rowWidth-1) / 2;
        }
        
        return ItemBuilder.newItem(XMaterial.MUSIC_DISC_WAIT)
                .withDisplayName(getDisplayName())
                .withLore(super.getLoreOfWand())
                .setNbtTag(getCustomModelData(), CUSTOM_MODEL_DATA_TAG)
                .build();
    }
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
            e.setCancelled(true);
            
            Player player = e.getPlayer();
            if(!super.decreaseWandUses(e.getItem(), e.getPlayer())){
                e.setCancelled(true);
                return;
            }
            
            super.updatePlayerCooldown(player);
            execute(e.getPlayer());
        }
    }
    
    public void execute(Player player) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Location loc1 = player.getLocation();
        Location loc2 = player.getTargetBlock(null, 120).getLocation();
        
        XSound sound = XSound.BLOCK_GLASS_BREAK;
        sound.play(player);
        
        if(loc1.getPitch() > maxPitch || loc1.getPitch() < minPitch) {
            return;
        }
        
        Task task = new Task() {
            final double distance = loc1.distance(loc2);
            final Vector direction = loc1.getDirection();
            final double dirX = direction.getX();
            final double dirZ = direction.getZ();
            final boolean isAxisX = Math.abs(dirX) >= Math.abs(dirZ);
            
            Location baseLoc = loc1.clone();
            int i = 1;
            
            @Override
            public void run() {
                if(i > maxBlocks || i > distance) {
                    cancel();
                    return;
                }
                
                for(int j=0; j<rowsOfBlocksEachTime && i <= maxBlocks && i <= distance; i++, j++) {
                    Block block = baseLoc.add(direction).getBlock();
                    
                    if(PASSABLE_BLOCKS.contains(block.getType())) {
                        frostItem.placeAt(baseLoc);
                        sound.play(baseLoc);
                    }
                    
                    for(int k=1; k<=rowHalfWidth; k++) {
                        Block b1, b2;
                        if(isAxisX) {
                            b1 = block.getRelative(0, 0, +k);
                            b2 = block.getRelative(0, 0, -k);
                        } else {
                            b1 = block.getRelative(+k, 0, 0);
                            b2 = block.getRelative(-k, 0, 0);
                        }
                        
                        if(PASSABLE_BLOCKS.contains(b1.getType())) {
                            frostItem.placeAt(b1);
                            sound.play(b1.getLocation());
                        }
                        if(PASSABLE_BLOCKS.contains(b2.getType())) {
                            frostItem.placeAt(b2);
                            sound.play(b2.getLocation());
                        }
                    }
                }
            }
        };
        task.runTask(beforeTicks, ticks);
//</editor-fold>
    }
}
