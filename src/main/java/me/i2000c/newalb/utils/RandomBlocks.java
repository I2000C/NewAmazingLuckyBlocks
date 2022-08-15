package me.i2000c.newalb.utils;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.rewards.TypeManager;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils2.ActionBarUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class RandomBlocks {
    int radx;
    int rady;
    int radz;
    int blocks;
    boolean floating_blocks;
    boolean forceMode;
    private final NewAmazingLuckyBlocks plugin = NewAmazingLuckyBlocks.getInstance();
    private final Player player;
    Location targetLocation;
    Block block;
    boolean isPlayer;
    CommandSender sender;
    
    boolean send_finish_message;
    
    public static int blocks_placed = 0;
    
    public static int taskID;
    private int taskID2;
    
    public static int numTasks = 0;
    
    public RandomBlocks(int radx, int rady, int radz, int blocks, boolean floating_blocks, boolean forceMode, Player player, boolean isPlayer, CommandSender sender){
        this.radx = radx;
        this.rady = rady;
        this.radz = radz;
        this.blocks = blocks;
        this.floating_blocks = floating_blocks;
        this.forceMode = forceMode;
        this.player = player;
        this.isPlayer = isPlayer;
        this.sender = sender;
        
        this.send_finish_message = true;
    }
    
    public RandomBlocks(int radx, int rady, int radz, int blocks, boolean floating_blocks, Location targetLocation, boolean send_finish_message){
        this.radx = radx;
        this.rady = rady;
        this.radz = radz;
        this.blocks = blocks;
        this.floating_blocks = floating_blocks;
        
        
        this.targetLocation = targetLocation;
        
        this.send_finish_message = send_finish_message;
        
        this.player = null;
        this.isPlayer = false;
        this.sender = Bukkit.getConsoleSender();
    }
    
    public void generatePackets(){
        if(player != null){
            targetLocation = player.getLocation();
        }
        
        if(!WorldList.isRegistered(targetLocation.getWorld().getName())){
            return;
        }
        
        RandomBlocks.blocks_placed = 0;
        
        int size;
        if(ConfigManager.getConfig().getInt("RandomBlocks-PacketSize") < 1){
           size = 1;
        }else{
            size = ConfigManager.getConfig().getInt("RandomBlocks-PacketSize");
        }
        
        int packets = blocks / size;
        int looseBlocks = blocks % size;
        
        final int totalBlocks = packets*size + looseBlocks;
        
        final String playerName;
        if(isPlayer){
            playerName = ((Player) sender).getName();
        }else{
            playerName = null;
        }
        
        BukkitScheduler sh = Bukkit.getServer().getScheduler();
        if(player == null){
            RandomBlocks.numTasks++;
            taskID2 = sh.scheduleSyncRepeatingTask(plugin, new Runnable(){
                int restBlocks = blocks;
                @Override
                public void run(){

                    if(ConfigManager.getConfig().getBoolean("Enable-randomBlocks-percentaje")){
                        try{
                            String percentaje = getProgressBar((totalBlocks-restBlocks), totalBlocks, 40, '|', ChatColor.GREEN, ChatColor.GRAY);

                            int percentajeValue = (totalBlocks-restBlocks)*100/totalBlocks;
                            String message = percentaje + " " + percentajeValue + "%";
                            
                            ActionBarUtils.sendMessage(message, Bukkit.getPlayer(playerName));
                        }catch(Exception ex){
                        }
                    }                    

                    if(restBlocks == 0){
                        if(send_finish_message){
                            try{
                                if(isPlayer && !Bukkit.getPlayer(playerName).isOnline()){
                                    sender = Bukkit.getConsoleSender();
                                }
                            }catch(Exception ex){
                                sender = Bukkit.getConsoleSender();
                            }

                            Logger.sendMessage("&b" + blocks_placed + " &aBlocks have been placed", sender);
                        }                        

                        sh.cancelTask(taskID2);
                        taskID = 0;
                        
                        RandomBlocks.numTasks--;
                        return;
                    }

                    if(restBlocks / size > 0){
                        blocks = size;
                    }else{
                        blocks = restBlocks;
                    }
                    generateRandomBlocks();

                    restBlocks -= blocks;
                }
            },0,1);
        }else{
            targetLocation = player.getLocation();
            
            taskID = sh.scheduleSyncRepeatingTask(plugin, new Runnable(){
                int restBlocks = blocks;
                @Override
                public void run(){

                    if(ConfigManager.getConfig().getBoolean("Enable-randomBlocks-percentaje")){
                        try{
                            String percentaje = getProgressBar((totalBlocks-restBlocks), totalBlocks, 40, '|', ChatColor.GREEN, ChatColor.GRAY);

                            int percentajeValue = (totalBlocks-restBlocks)*100/totalBlocks;
                            String message = percentaje + " " + percentajeValue + "%";
                            
                            ActionBarUtils.sendMessage(message, Bukkit.getPlayer(playerName));
                        }catch(Exception ex){
                        }
                    }                    

                    if(restBlocks == 0){
                        if(send_finish_message){
                            try{
                                if(isPlayer && !Bukkit.getPlayer(playerName).isOnline()){
                                    sender = Bukkit.getConsoleSender();
                                }
                            }catch(Exception ex){
                                sender = Bukkit.getConsoleSender();
                            }

                            Logger.sendMessage("&b" + blocks_placed + " &aBlocks have been placed", sender);
                        }                        

                        sh.cancelTask(taskID);
                        taskID = 0;
                        return;
                    }

                    if(restBlocks / size > 0){
                        blocks = size;
                    }else{
                        blocks = restBlocks;
                    }
                    generateRandomBlocks();

                    restBlocks -= blocks;
                }
            },0,1);
        }
            
    }
    
    private void generateRandomBlocks(){
        Location location = this.targetLocation.clone();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        int randomx;
        int randomy;
        int randomz;
        World w = targetLocation.getWorld();
        
        if(!floating_blocks && forceMode){
            //Force mode enabled
            List<Location> locations = this.getValidLocations(radx, rady, radz, location);
            if(blocks >= locations.size()){
                for(Location loc : locations){
                    this.placeBlock(loc.getBlock());
                }
            }else{
                Random r = new Random();
                for(int i=0;i<blocks;i++){
                    Location l = locations.get(r.nextInt(locations.size()));
                    this.placeBlock(l.getBlock());
                    locations.remove(l);
                }
            }
            return;
        }
        
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
                if(TypeManager.getType(b) == null){
                    placeBlock(b);
                }
            }else{
                Block above = w.getBlockAt(randomx, randomy +1, randomz);
                Block below = w.getBlockAt(randomx, randomy -1, randomz);
                int randomy2 = randomy;
                if(above.getType().equals(Material.AIR) && !below.isEmpty()){
                    if(w.getBlockAt(location).isEmpty()){
                        Block b = w.getBlockAt(randomx, randomy, randomz);
                        placeBlock(b);
                    }else{
                        randomy++;
                        Block b = w.getBlockAt(randomx, randomy, randomz);
                        placeBlock(b);
                    }                 
                }else if(below.isEmpty()){
                    while(randomy >= (y-rady)){
                        below = w.getBlockAt(randomx, randomy-1, randomz);
                        if(below.isEmpty()){
                            randomy--;
                        }else{
                           Block air = w.getBlockAt(randomx, randomy, randomz);
                                if(!air.isEmpty()){
                                    randomy++;
                                    Block b = w.getBlockAt(randomx, randomy, randomz);
                                    placeBlock(b);
                                }else{
                                    Block b = w.getBlockAt(randomx, randomy, randomz);
                                    placeBlock(b);
                                }
                           randomy = y-rady-1; //Ends the WHILE loop
                        }
                    }
                }else if(!above.getType().equals(Material.AIR)){
                    while(randomy2 <= (y+rady)){
                        above = w.getBlockAt(randomx, randomy2+1, randomz);
                        if(!above.getType().equals(Material.AIR)){
                            randomy2++;
                        }else{
                            Block air = w.getBlockAt(randomx, randomy2, randomz);
                                if(!air.isEmpty()){
                                    randomy2++;
                                    Block b = w.getBlockAt(randomx, randomy2, randomz);
                                    placeBlock(b);
                                }else{
                                    Block b = w.getBlockAt(randomx, randomy2, randomz);
                                    placeBlock(b);
                                }
                           randomy2 = y+rady+1; //Ends the WHILE loop
                        }
                    }
                }                
            }
        }         
    }
    
    private void placeBlock(Block block){
        if(WorldList.isRegistered(block.getWorld().getName())){
            TypeManager.getRandomLuckyBlockType().replaceBlock(block);
            blocks_placed++;
            LocationManager.registerLocation(block.getLocation());
        }
    }
    
    
    private List<Location> getValidLocations(int radx, int rady, int radz, Location center){
        List<Location> locations = new ArrayList();
        
        World w = center.getWorld();
        int x0 = center.getBlockX();
        int y0 = center.getBlockY();
        int z0 = center.getBlockZ();
        for(int x=x0-radx;x<=x0+radx;x++){
            for(int y=y0-rady;y<=y0+rady;y++){
                for(int z=z0-radz;z<=z0+radz;z++){
                    Block b = w.getBlockAt(x, y, z);
                    if(b.isEmpty() && b.getRelative(0, 1, 0).isEmpty() && !b.getRelative(0, -1, 0).isEmpty()){
                        locations.add(b.getLocation());
                    }
                }
            }
        }
        
        return locations;
    }
    
    
    public String getProgressBar(int current, int max, int totalBars, char symbol, ChatColor completedColor, ChatColor notCompletedColor) {
        //https://www.spigotmc.org/threads/progress-bars-and-percentages.276020/?__cf_chl_jschl_tk__=45c123d7886626b0cecd2cc530219da427d5214c-1580209641-0-AR6ljkF-Q6swj-OSROtke2TrSS-uy_MbFGPOgZ8lNPedOUjqxEiNa9ETsYqFS64G9TsBUlQyxRxAGCuGToSPXGUOA6orDyEN74TvJ1IXUHEWal6-5wBdbMyMaBC6_Y_YvW2sOQzrlt8PIFNgCJMpCqvLHdzf5FW8kjWSLfsL4FJu41b44pJNsgWD_4mWJnjSDKs4tru_U9cjwayifVseAspUUKUxu5B89HqV3thQpsjogf_kui1K5UGgweNH1cQAqwsYsv51bc4rmzyEdzWlg_6lXYx6GgWPedTgPW6VGo1vnwYVGAuv80G8yovQ4S2_Xw
        
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);

        return Strings.repeat("" + completedColor + symbol, progressBars)
                + Strings.repeat("" + notCompletedColor + symbol, totalBars - progressBars);
    }
}
