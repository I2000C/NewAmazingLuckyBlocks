package me.i2000c.newalb.listeners;

import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.RandomBlocks;
import me.i2000c.newalb.utils.WorldConfig;

import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.block.Block;

public class ChunkEvent implements Listener{
    
    @EventHandler
    private void onChunkCreated(ChunkPopulateEvent e){
        if(ConfigManager.getConfig().getBoolean("GenerateRandomblocks-OnChunkCreated.enable")){
            if(WorldConfig.isRegistered(e.getWorld().getName())){
                int x = ConfigManager.getConfig().getInt("GenerateRandomblocks-OnChunkCreated.radx");
                int y = ConfigManager.getConfig().getInt("GenerateRandomblocks-OnChunkCreated.rady");
                int z = ConfigManager.getConfig().getInt("GenerateRandomblocks-OnChunkCreated.radz");
                int blocks = ConfigManager.getConfig().getInt("GenerateRandomblocks-OnChunkCreated.blocks");
                boolean floating_blocks = ConfigManager.getConfig().getBoolean("GenerateRandomblocks-OnChunkCreated.floating-blocks");
                boolean send_finish_message = ConfigManager.getConfig().getBoolean("GenerateRandomblocks-OnChunkCreated.send-finish-message");
                
                int maxTasks = ConfigManager.getConfig().getInt("GenerateRandomblocks-OnChunkCreated.maxTasks");
                
                if(RandomBlocks.numTasks < maxTasks){
                    if(!e.getChunk().isLoaded()){
                        e.getChunk().load(true);
                    }
                    Block b = e.getChunk().getBlock(8, 64, 8);
                    
                    RandomBlocks rb = new RandomBlocks(x, y, z, blocks, floating_blocks, b.getLocation(), send_finish_message);
                    rb.generatePackets();
                }                
            }
        }
    }
}
