package net.servermc.plugins.utils;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.Material;

import net.servermc.plugins.AmazingLuckyBlocks;


import org.bukkit.Effect;
import org.bukkit.Sound;


public class Timer {
    int taskID;
    private AmazingLuckyBlocks plugin;
    int bloques;
    long ticks;
    private Player player;
    Location location;
    
    public Timer(AmazingLuckyBlocks plugin,int bloques,long ticks,Player player, Location l){
    this.plugin = plugin;
    this.bloques = bloques;
    this.player = player;
    this.ticks = ticks;
    this.location = l;
    }    
    public void darkhole(){
        Sound sound;
        Effect effect;
        try {
            sound = Sound.valueOf("WITHER_IDLE"); // pre 1.9 sound //pre 1.9 sounds are here: http://docs.codelanx.com/Bukkit/1.8/org/bukkit/Sound.html
        } catch(IllegalArgumentException e) {
            sound = Sound.valueOf("ENTITY_WITHER_AMBIENT"); // post 1.9 sound //post 1.9 sounds are here: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html
        }
        player.playSound(player.getLocation(), sound, 2.0F, 1.0F);
        
        effect = Effect.ENDER_SIGNAL;
        location.getWorld().playEffect(location, effect, 100); // pre 1.9 effects are here: https://www.spigotmc.org/wiki/effect-list-1-8-8/
        
        
        long before_ticks = CLBManager.getManager().getConfig().getInt("Objects.DarkHole.time-before-darkhole");
        
        BukkitScheduler sh = Bukkit.getServer().getScheduler();
        taskID = sh.scheduleSyncRepeatingTask(plugin,new Runnable(){
            
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        
            @Override
            public void run(){
                    if(bloques == 0){
                        Bukkit.getScheduler().cancelTask(taskID);
                        return;
                    }else{
                        player.getWorld().getBlockAt(x, y, z).setType(Material.AIR);
                        player.getWorld().getBlockAt(x+1, y, z).setType(Material.AIR);
                        player.getWorld().getBlockAt(x-1, y, z).setType(Material.AIR);
                        player.getWorld().getBlockAt(x, y, z+1).setType(Material.AIR);
                        player.getWorld().getBlockAt(x, y, z-1).setType(Material.AIR);
                        player.getWorld().getBlockAt(x+1, y, z+1).setType(Material.AIR);
                        player.getWorld().getBlockAt(x-1, y, z-1).setType(Material.AIR);
                        player.getWorld().getBlockAt(x+1, y, z-1).setType(Material.AIR);
                        player.getWorld().getBlockAt(x-1, y, z+1).setType(Material.AIR);
                        y--;
                        bloques--; //tambien se puede poner bloques = bloques -1;
                        
                    }          
            } 
        },before_ticks,ticks);      //},"Tiempo de espera antes de que se ejecute en ticks","Cada cuanto tiempo se repite en ticks");
    }
    public void minivolcano(){
        Sound sound;
        Effect effect;
        try {
            sound = Sound.valueOf("FUSE"); // pre 1.9 sound //pre 1.9 sounds are here: http://docs.codelanx.com/Bukkit/1.8/org/bukkit/Sound.html
        } catch(IllegalArgumentException e) {
            sound = Sound.valueOf("ENTITY_TNT_PRIMED"); // post 1.9 sound //post 1.9 sounds are here: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html
        }
        player.playSound(player.getLocation(), sound, 2.0F, 1.0F);
        
         
        try{
            effect = Effect.valueOf("LARGE_SMOKE"); // pparticle effects are here: https://www.digminecraft.com/lists/particle_list_pc.php
            location.getWorld().playEffect(location, effect, 100);
        }catch(IllegalArgumentException e){
            }
            
        
        long before_ticks = CLBManager.getManager().getConfig().getInt("Objects.MiniVolcano.time-before-minivolcano");
                
        BukkitScheduler sh = Bukkit.getServer().getScheduler();
        taskID = sh.scheduleSyncRepeatingTask(plugin,new Runnable(){
            
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        
            @Override
            public void run(){
                    if(bloques == 5){
                        Bukkit.getScheduler().cancelTask(taskID);
                        return;
                    }else{
                        if(bloques == 4){
                            location.getWorld().getBlockAt(x, y+1, z).setType(Material.LAVA);
                            
                            location.getWorld().getBlockAt(x+1, y, z).setType(Material.OBSIDIAN);
                            location.getWorld().getBlockAt(x-1, y, z).setType(Material.OBSIDIAN);
                            location.getWorld().getBlockAt(x, y, z+1).setType(Material.OBSIDIAN);
                            location.getWorld().getBlockAt(x, y, z-1).setType(Material.OBSIDIAN);
                            location.getWorld().getBlockAt(x+1, y, z+1).setType(Material.OBSIDIAN);
                            location.getWorld().getBlockAt(x-1, y, z-1).setType(Material.OBSIDIAN);
                            location.getWorld().getBlockAt(x+1, y, z-1).setType(Material.OBSIDIAN);
                            location.getWorld().getBlockAt(x-1, y, z+1).setType(Material.OBSIDIAN);
                        }
                        if(bloques == 3){
                            location.getWorld().getBlockAt(x, y+1, z).setType(Material.LAVA);
                            
                            location.getWorld().getBlockAt(x+1, y, z).setType(Material.OBSIDIAN);
                            location.getWorld().getBlockAt(x-1, y, z).setType(Material.OBSIDIAN);
                            location.getWorld().getBlockAt(x, y, z+1).setType(Material.OBSIDIAN);
                            location.getWorld().getBlockAt(x, y, z-1).setType(Material.OBSIDIAN);
                            location.getWorld().getBlockAt(x+1, y, z+1).setType(Material.OBSIDIAN);
                            location.getWorld().getBlockAt(x-1, y, z-1).setType(Material.OBSIDIAN);
                            location.getWorld().getBlockAt(x+1, y, z-1).setType(Material.OBSIDIAN);
                            location.getWorld().getBlockAt(x-1, y, z+1).setType(Material.OBSIDIAN);
                            y++;
                        }
                        if(bloques == 2){
                            location.getWorld().getBlockAt(x, y+1, z).setType(Material.LAVA);
                            
                            location.getWorld().getBlockAt(x+1, y, z).setType(Material.OBSIDIAN);
                            location.getWorld().getBlockAt(x-1, y, z).setType(Material.OBSIDIAN);
                            location.getWorld().getBlockAt(x, y, z+1).setType(Material.OBSIDIAN);
                            location.getWorld().getBlockAt(x, y, z-1).setType(Material.OBSIDIAN);
                            location.getWorld().getBlockAt(x+1, y, z+1).setType(Material.OBSIDIAN);
                            location.getWorld().getBlockAt(x-1, y, z-1).setType(Material.OBSIDIAN);
                            location.getWorld().getBlockAt(x+1, y, z-1).setType(Material.OBSIDIAN);
                            location.getWorld().getBlockAt(x-1, y, z+1).setType(Material.OBSIDIAN);
                            y++;
                        }
                        if(bloques == 1){
                            while(!location.getWorld().getBlockAt(x, y, z).getType().equals(Material.AIR)){
                                y++;
                            }
                            location.getWorld().getBlockAt(x, y, z).setType(Material.LAVA);
                        }
                          bloques++;
                    }          
            } 
        },before_ticks,ticks);
    }
}
