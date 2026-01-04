package me.i2000c.newalb.listeners.blocks;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;

import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.utils.locations.WorldManager;
import me.i2000c.newalb.utils.random.RandomBlocks;
import me.i2000c.newalb.utils.random.RandomBlocksOptions;
import me.i2000c.newalb.utils.random.RandomUtils;

public class ChunkLoadListener implements Listener{
    
    @EventHandler
    private void onChunkCreated(ChunkPopulateEvent e){
        if(ConfigManager.getMainConfig().getBoolean("GenerateRandomblocks-OnChunkCreated.enable")){
            if(WorldManager.isEnabled(e.getWorld().getName())){
                int chance = ConfigManager.getMainConfig().getInt("GenerateRandomblocks-OnChunkCreated.chance");
                if(RandomUtils.getInt(100) < chance) {
                    int radx = ConfigManager.getMainConfig().getInt("GenerateRandomblocks-OnChunkCreated.radx");
                    int rady = ConfigManager.getMainConfig().getInt("GenerateRandomblocks-OnChunkCreated.rady");
                    int radz = ConfigManager.getMainConfig().getInt("GenerateRandomblocks-OnChunkCreated.radz");
                    int blocks = ConfigManager.getMainConfig().getInt("GenerateRandomblocks-OnChunkCreated.blocks");
                    boolean allowfloatingBlocks = ConfigManager.getMainConfig().getBoolean("GenerateRandomblocks-OnChunkCreated.floating-blocks");
                    boolean avoidWater = ConfigManager.getMainConfig().getBoolean("GenerateRandomblocks-OnChunkCreated.avoid-water");
                    boolean sendFinishMessage = ConfigManager.getMainConfig().getBoolean("GenerateRandomblocks-OnChunkCreated.send-finish-message");

                    int maxTasks = ConfigManager.getMainConfig().getInt("GenerateRandomblocks-OnChunkCreated.maxTasks");

                    if(RandomBlocks.getExtraTasksNumber() < maxTasks){
                        if(!e.getChunk().isLoaded()){
                            e.getChunk().load(true);
                        }
                        Block b = e.getChunk().getBlock(8, 64, 8);
                        
                        RandomBlocksOptions options = RandomBlocksOptions.builder()
                                                                         .radx(radx)
                                                                         .rady(rady)
                                                                         .radz(radz)
                                                                         .blocks(blocks)
                                                                         .allowFloatingBlocks(allowfloatingBlocks)
                                                                         .avoidWater(avoidWater)
                                                                         .sendFinishMessage(sendFinishMessage)
                                                                         .location(b.getLocation())
                                                                         .extraTask(true)
                                                                         .build();
                        RandomBlocks.placeRandomBlocks(options);
                    }
                }
            }
        }
    }
}
