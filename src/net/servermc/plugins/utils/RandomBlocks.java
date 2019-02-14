package net.servermc.plugins.utils;

import net.servermc.plugins.AmazingLuckyBlocks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.World;
import java.util.Random;
import net.servermc.plugins.Listeners.Database;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class RandomBlocks {
    int radx;
    int rady;
    int radz;
    int blocks;
    boolean floating_blocks;
    private AmazingLuckyBlocks plugin;
    private final Player player;
    Location location;
    Block block;
    boolean isPlayer;
    
    public RandomBlocks(int radx, int rady, int radz, int blocks, boolean floating_blocks, Player player, boolean isPlayer){
        this.radx = radx;
        this.rady = rady;
        this.radz = radz;
        this.blocks = blocks;
        this.floating_blocks = floating_blocks;
        this.player = player;
        this.isPlayer = isPlayer;
    }
    
    public void generateRandomBlocks(){
        
        if(CLBManager.getManager().getConfig().getString("LuckyBlock.Material").equals("SKULL")){
            if(AmazingLuckyBlocks.getInstance().minecraftVersion.equals("1.13")){
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "For the moment, this command does not support skull material in 1.13");
                return;
            }
        }
        
        location = player.getLocation();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        int randomx;
        int randomy;
        int randomz;
        World w = player.getWorld();
        for(int i = 0; i < blocks; i++){
            //r.nextInt((max - min) + 1) + min;
            randomx = new Random().nextInt(((x+radx) - (x-radx))+1)+(x-radx);
            randomy = new Random().nextInt(((y+rady) - (y-rady))+1)+(y-rady);
            randomz = new Random().nextInt(((z+radz) - (z-radz))+1)+(z-radz);
            location.setX(randomx);
            location.setY(randomy);
            location.setZ(randomz);
            if(floating_blocks){
                Block b = w.getBlockAt(location);
                placeBlock(b);
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
                                    placeBlock(b);
                                }else{
                                    Block b = w.getBlockAt(randomx, randomy, randomz);
                                    placeBlock(b);
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
                                    placeBlock(b);
                                }else{
                                    Block b = w.getBlockAt(randomx, randomy2, randomz);
                                    placeBlock(b);
                                }
                           randomy2 = y+rady+1;
                        }
                    }
                }
                
            }
        }
        
        
    }
    
    public void placeBlock(Block block){
        ItemStack lucky;
        if(Database.headMode && Database.isDatabaseLoaded){
                lucky = Database.item;
            }else if(Database.headMode && !Database.isDatabaseLoaded){
                return;
            }else{
                lucky = new ItemStack(Material.valueOf(CLBManager.getManager().getConfig().getString("LuckyBlock.Material")));
            }
        if(CLBManager.getManager().getConfig().getString("LuckyBlock.Material").equals("SKULL")){
            block.setType(lucky.getType());
        }else{
            block.setType(lucky.getType()); 
        }
        if(Database.headMode){
            Database.setSkullSkin(block, CLBManager.getManager().getConfig().getString("LuckyBlock.HeadMode.skull-ID"));
        }
        Location block_loc = block.getLocation();
        LocationManager lm = new LocationManager();
        LocationManager.loc_list.add(block_loc);
        if(isPlayer){
            lm.getLocations().set("Blocks." + "(" + block_loc.getBlockX() + block_loc.getBlockY() + block_loc.getBlockZ() + ")" + ".player-name", player.getName());
        }else{
            lm.getLocations().set("Blocks." + "(" + block_loc.getBlockX() + block_loc.getBlockY() + block_loc.getBlockZ() + ")" + ".player-name", "Console");
        }
        lm.getLocations().set("Blocks." + "(" + block_loc.getBlockX() + block_loc.getBlockY() + block_loc.getBlockZ() + ")" + ".world", block_loc.getWorld().getName());
        lm.getLocations().set("Blocks." + "(" + block_loc.getBlockX() + block_loc.getBlockY() + block_loc.getBlockZ() + ")" + ".x", block_loc.getBlockX());
        lm.getLocations().set("Blocks." + "(" + block_loc.getBlockX() + block_loc.getBlockY() + block_loc.getBlockZ() + ")" + ".y", block_loc.getBlockY());
        lm.getLocations().set("Blocks." + "(" + block_loc.getBlockX() + block_loc.getBlockY() + block_loc.getBlockZ() + ")" + ".z", block_loc.getBlockZ());

        int blocks_number = lm.getLocations().getInt("Blocks-placed");
        blocks_number++;
        lm.getLocations().set("Blocks-placed", blocks_number);
        lm.saveLocations();
    }
}
