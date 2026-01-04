package me.i2000c.newalb.utils.random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.cryptomorin.xseries.messages.ActionBar;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import me.i2000c.newalb.integration.WorldGuardManager;
import me.i2000c.newalb.lucky_blocks.rewards.TypeManager;
import me.i2000c.newalb.utils.locations.LocationManager;
import me.i2000c.newalb.utils.locations.WorldManager;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.OtherUtils;
import me.i2000c.newalb.utils.tasks.Task;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RandomBlocks {
    
    private static final BlockFace[] BLOCK_FACES = {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
    private static RandomBlocks mainTask = null;
    private static List<RandomBlocks> extraTasks = new ArrayList<>();
    
    private Set<Location> blocksPlaced = new HashSet<>();
    private List<Location> cachedSafeLocations = new ArrayList<>();
    private Task task = null;
    @Nonnull private final RandomBlocksOptions options;
    
    public static int getExtraTasksNumber() {
        return extraTasks.size();
    }
    
    @Synchronized
    public static boolean placeRandomBlocks(@NonNull RandomBlocksOptions options) {
        Objects.requireNonNull(options.getLocation(), "Location can't be null");
        if(!options.isExtraTask()) {
            if(mainTask != null && mainTask.task != null && mainTask.task.isStarted()) {
                Logger.sendMessage("&cThere already is a randomblocks task running", options.getSenderToNotify());
                Logger.sendMessage("&cWait until it finish or use: &a/alb randomblocks stop", options.getSenderToNotify());
                return false;
            }
        }
        
        RandomBlocks randomBlocks = new RandomBlocks(options);
        if(options.isExtraTask()) {
            extraTasks.add(randomBlocks);
        } else {
            mainTask = randomBlocks;
        }
        randomBlocks.start();
        return true;
    }
    
    @Synchronized
    public static boolean stopRandomBlocksMainTask(@NonNull CommandSender sender) {
        if(mainTask != null && mainTask.task != null && mainTask.task.isStarted()) {
            mainTask.task.cancel();
            LocationManager.registerLocations(mainTask.blocksPlaced);
            Logger.sendMessage("&cRandomblock task has been cancelled", sender);
            Logger.sendMessage("&b" + mainTask.blocksPlaced.size() + " &aBlocks have been placed", sender);
            mainTask.blocksPlaced.clear();
            mainTask = null;
            return true;
        } else {
            Logger.sendMessage("&cThere isn't any randomblocks task running at the moment", sender);
            return false;
        }
    }
    
    private void start() {
        task = new Task() {
            final int totalBlocks = options.getBlocks();
            int blocksLeft = totalBlocks;
            
            @Override
            public void run() {
                if(options.isShowPercentage() && options.getSenderToNotify() instanceof Player) {
                    int currentBlocks;
                    if(blocksLeft > 0) {
                        currentBlocks = totalBlocks - blocksLeft;
                    } else {
                        currentBlocks = totalBlocks;
                    }
                    
                    String percentageString = OtherUtils.getProgressBar(currentBlocks, totalBlocks, 40, '|', ChatColor.GREEN, ChatColor.GRAY);
                    int percentageValue = currentBlocks * 100 / totalBlocks;
                    String message = String.format("%s %3d %%", percentageString, percentageValue);
                    ActionBar.sendActionBar((Player) options.getSenderToNotify(), Logger.color(message));
                }
                
                if(blocksLeft > 0) {
                    int blocksToPlace = Math.min(blocksLeft, options.getPacketSize());
                    generateRandomBlocks(blocksToPlace);
                    blocksLeft -= blocksToPlace;
                } else {
                    LocationManager.registerLocations(blocksPlaced);
                    if(options.isSendFinishMessage()) {
                        Logger.sendMessage("&b" + blocksPlaced.size() + " &aBlocks have been placed", options.getSenderToNotify());
                    }
                    blocksPlaced.clear();
                    cancel();
                    
                    if(options.isExtraTask()) {
                        RandomBlocks.extraTasks.remove(RandomBlocks.this);
                    }
                }
            }
        };
        task.runTask(0L, 1L);
    }
    
    private void generateRandomBlocks(int blocksToPlace) {
        if(options.isAllowFloatingBlocks()) {
            for(int i=0; i<blocksToPlace; i++) {
                Location loc = generateRandomLocation(options.getLocation(), options.getRadx(), options.getRady(), options.getRadz());
                placeLuckyBlock(loc);
            }
        } else if(!options.isPreScanSafeLocations()) {
            int y = options.getLocation().getBlockY();
            int minY = y - options.getRady();
            int maxY = y + options.getRady();
            for(int i=0; i<blocksToPlace; i++) {
                Location loc = generateRandomLocation(options.getLocation(), options.getRadx(), options.getRady(), options.getRadz());
                loc = tryMakeSafe(loc, minY, maxY);
                if(loc != null) {
                    placeLuckyBlock(loc);
                }
            }
        } else {
            if(cachedSafeLocations.isEmpty()) {
                for(int x=-options.getRadx(); x<=options.getRadx(); x++) {
                    for(int y=-options.getRady(); y<=options.getRady(); y++) {
                        for(int z=-options.getRadz(); z<=options.getRadz(); z++) {
                            Location loc = options.getLocation().clone().add(x, y, z);
                            if(isSafe(loc)) {
                                cachedSafeLocations.add(loc);
                            }
                        }
                    }
                }
                
                Collections.shuffle(cachedSafeLocations);
            }
            
            Iterator<Location> iterator = cachedSafeLocations.iterator();
            for(int i=0; i<blocksToPlace && iterator.hasNext(); i++) {
                Location loc = iterator.next();
                placeLuckyBlock(loc);
                iterator.remove();
            }
        }
    }
    
    private void placeLuckyBlock(Location location) {
        if(!WorldManager.isEnabled(location.getWorld().getName())) return;
        
        Player player = null;
        if(options.getSenderToNotify() instanceof Player) {
            player = (Player) options.getSenderToNotify();
        }
        if(!WorldGuardManager.canBuild(player, location)) return;

        if(options.isAvoidWater() && isNextToWater(location.getBlock())) return;
        
        TypeManager.getRandomLuckyBlockType().getItem().placeAt(location);
        blocksPlaced.add(location);
    }
    
    private static boolean isNextToWater(Block block) {
        for(BlockFace face : BLOCK_FACES) {
            Block neighbor = block.getRelative(face);
            if(neighbor.isLiquid()) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean isSafe(Location loc) {
        Block current = loc.getBlock();
        Block above = current.getRelative(BlockFace.UP);
        Block below = current.getRelative(BlockFace.DOWN);
        return current.isEmpty() && above.isEmpty() && !below.isEmpty();
    }
    
    private static Location tryMakeSafe(Location loc, int minY, int maxY) {
        // Case 1: Location already safe
        if(isSafe(loc)) {
            return loc;
        }
        
        Block current = loc.getBlock();
        Block above = current.getRelative(BlockFace.UP);
        Block below = current.getRelative(BlockFace.DOWN);
        
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        
        // Case 2: Buried location
        if(!current.isEmpty() && !above.isEmpty()) {
            for(int ny = y+1; ny <= maxY; ny++) {
                Block block = loc.getWorld().getBlockAt(x, ny, z);
                if(isSafe(block.getLocation())) {
                    return block.getLocation();
                }
            }
            return null;
        }
        
        // Case 3: Floating location
        if(current.isEmpty() && below.isEmpty()) {
            for(int ny = y-1; ny <= minY; ny--) {
                Block block = loc.getWorld().getBlockAt(x, ny, z);
                if(isSafe(block.getLocation())) {
                    return block.getLocation();
                }
            }
            return null;
        }
        
        // Other cases: return null
        return null;
    }
    
    private static Location generateRandomLocation(@Nonnull Location baseLocation, int radx, int rady, int radz) {
        World world = baseLocation.getWorld();
        int x = baseLocation.getBlockX();
        int y = baseLocation.getBlockY();
        int z = baseLocation.getBlockZ();
        int randomX = RandomUtils.getInt(x - radx, x + radx);
        int randomY = RandomUtils.getInt(y - rady, y + rady);
        int randomZ = RandomUtils.getInt(z - radz, z + radz);
        Location randomLocation = new Location(world, randomX, randomY, randomZ);
        return randomLocation;
    }
}
