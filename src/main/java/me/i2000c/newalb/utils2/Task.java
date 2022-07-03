package me.i2000c.newalb.utils2;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public abstract class Task implements Runnable{
    //Static behaviour
    private static Plugin plugin;
    
    public static void initializeTaskManager(Plugin plugin){
        Task.plugin = plugin;
    }
    
    public static int runTask(Runnable runnable){
        return Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable);
    }    
    public static int runTask(Runnable runnable, long afterTicks){
        return Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable, afterTicks);
    }    
    public static int runTask(Runnable runnable, long preTicks, long periodTicks){
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, runnable, preTicks, periodTicks);
    }
    public static int runTaskAsynchronously(Runnable runnable){
        return Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, runnable);
    }    
    public static int runTaskAsynchronously(Runnable runnable, long afterTicks){
        return Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, runnable, afterTicks);
    }    
    public static int runTaskAsynchronously(Runnable runnable, long preTicks, long periodTicks){
        return Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, runnable, preTicks, periodTicks);
    }
    
    
    //Non-static behaviour
    private final BukkitRunnable br;
    
    public Task(){
        br = new BukkitRunnable(){
            @Override
            public void run(){
                Task.this.run();
            }
        };
    }

    public synchronized boolean isCancelled() throws IllegalStateException {
        return br.isCancelled();
    }

    public synchronized void cancel() throws IllegalStateException {
        br.cancel();
    }

    public synchronized BukkitTask runTask() throws IllegalArgumentException, IllegalStateException {
        return br.runTask(plugin);
    }

    public synchronized BukkitTask runTaskAsynchronously() throws IllegalArgumentException, IllegalStateException {
        return br.runTaskAsynchronously(plugin);
    }

    public synchronized BukkitTask runTask(long delay) throws IllegalArgumentException, IllegalStateException {
        return br.runTaskLater(plugin, delay);
    }

    public synchronized BukkitTask runTaskAsynchronously(long delay) throws IllegalArgumentException, IllegalStateException {
        return br.runTaskLaterAsynchronously(plugin, delay);
    }

    public synchronized BukkitTask runTask(long delay, long period) throws IllegalArgumentException, IllegalStateException {
        return br.runTaskTimer(plugin, delay, period);
    }

    public synchronized BukkitTask runTaskAsynchronously(long delay, long period) throws IllegalArgumentException, IllegalStateException {
        return br.runTaskTimerAsynchronously(plugin, delay, period);
    }
    
    public synchronized int getTaskId() throws IllegalStateException {
        return br.getTaskId();
    }
    
    
}
