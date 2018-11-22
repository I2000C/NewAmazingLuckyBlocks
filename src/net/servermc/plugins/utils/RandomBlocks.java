package net.servermc.plugins.utils;

import net.servermc.plugins.AmazingLuckyBlocks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.World;
import java.util.Random;

public class RandomBlocks {
    int radx;
    int rady;
    int radz;
    int blocks;
    int floating_blocks;
    private AmazingLuckyBlocks plugin;
    private final Player player;
    Location location;
    Block block;
    
    public RandomBlocks(int radx, int rady, int radz, int blocks, int floating_blocks, Player player){
        this.radx = radx;
        this.rady = rady;
        this.radz = radz;
        this.blocks = blocks;
        this.floating_blocks = floating_blocks;
        this.player = player;
    }
    
    public void generateRandomBlocks(){
        location = player.getLocation();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        int randomx;
        int randomy;
        int randomz;
        World w = player.getWorld();
        Material luckyblock = Material.valueOf(CLBManager.getManager().getConfig().getString("LuckyBlock"));
        for(int i = 0; i < blocks; i++){
            //r.nextInt((max - min) + 1) + min;
            randomx = new Random().nextInt(((x+radx) - (x-radx))+1)+(x-radx);
            randomy = new Random().nextInt(((y+rady) - (y-rady))+1)+(y-rady);
            randomz = new Random().nextInt(((z+radz) - (z-radz))+1)+(z-radz);
            location.setX(randomx);
            location.setY(randomy);
            location.setZ(randomz);
            if(floating_blocks == 1){
            Block b = w.getBlockAt(location);
            b.setType(luckyblock);
            }else{
                Block above = w.getBlockAt(randomx, randomy +1, randomz);
                Block below = w.getBlockAt(randomx, randomy -1, randomz);
                int randomy2 = randomy;
                if(below.getType().equals(Material.AIR)){
                    while(randomy >= (y-rady)){
                        below = w.getBlockAt(randomx, randomy, randomz);
                        if(below.getType().equals(Material.AIR)){
                            randomy--;
                        }else{
                           Block air = w.getBlockAt(randomx, randomy, randomz);
                                if(!air.getType().equals(Material.AIR)){
                                    randomy++;
                                    Block b = w.getBlockAt(randomx, randomy, randomz);
                                    b.setType(luckyblock); 
                                }else{
                                    Block b = w.getBlockAt(randomx, randomy, randomz);
                                    b.setType(luckyblock); 
                                }
                           randomy = y-rady-1;
                        }
                    }
                }
                if(!above.getType().equals(Material.AIR)){
                    while(randomy2 <= (y+rady)){
                        above = w.getBlockAt(randomx, randomy2, randomz);
                        if(!above.getType().equals(Material.AIR)){
                            randomy2++;
                        }else{
                            Block air = w.getBlockAt(randomx, randomy2, randomz);
                                if(!air.getType().equals(Material.AIR)){
                                    randomy2++;
                                    Block b = w.getBlockAt(randomx, randomy2, randomz);
                                    b.setType(luckyblock); 
                                }else{
                                    Block b = w.getBlockAt(randomx, randomy2, randomz);
                                    b.setType(luckyblock); 
                                }
                           randomy2 = y+rady+1;
                        }
                    }
                }
                
            }
        }
        
        
    }   
}
