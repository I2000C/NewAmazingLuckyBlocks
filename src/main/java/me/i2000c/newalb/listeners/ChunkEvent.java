package me.i2000c.newalb.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;

import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.utils.RandomBlocks;
import me.i2000c.newalb.utils.WorldManager;

public class ChunkEvent implements Listener{
    
    @EventHandler
    private void onChunkCreated(ChunkPopulateEvent e){
        if(ConfigManager.getMainConfig().getBoolean("GenerateRandomblocks-OnChunkCreated.enable")){
            if(WorldManager.isEnabled(e.getWorld().getName())){
                int x = ConfigManager.getMainConfig().getInt("GenerateRandomblocks-OnChunkCreated.radx");
                int y = ConfigManager.getMainConfig().getInt("GenerateRandomblocks-OnChunkCreated.rady");
                int z = ConfigManager.getMainConfig().getInt("GenerateRandomblocks-OnChunkCreated.radz");
                int blocks = ConfigManager.getMainConfig().getInt("GenerateRandomblocks-OnChunkCreated.blocks");
                boolean floating_blocks = ConfigManager.getMainConfig().getBoolean("GenerateRandomblocks-OnChunkCreated.floating-blocks");
                boolean send_finish_message = ConfigManager.getMainConfig().getBoolean("GenerateRandomblocks-OnChunkCreated.send-finish-message");
                
                int maxTasks = ConfigManager.getMainConfig().getInt("GenerateRandomblocks-OnChunkCreated.maxTasks");
                
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
