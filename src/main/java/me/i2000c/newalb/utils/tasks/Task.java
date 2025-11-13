package me.i2000c.newalb.utils.tasks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

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
    public static void cancelTask(int taskID){
        Bukkit.getScheduler().cancelTask(taskID);
    }
    
    public static Task runRepeatingTask(Runnable runnable, long preTicks, long periodTicks, int times) {
        return runRepeatingTask(runnable, preTicks, periodTicks, times, false);
    }
    public static Task runRepeatingTaskAsync(Runnable runnable, long preTicks, long periodTicks, int times) {
        return runRepeatingTask(runnable, preTicks, periodTicks, times, true);
    }
    public static Task runRepeatingTask(Runnable runnable, long preTicks, long periodTicks, int times, boolean isAsync) {
        Task task = new Task() {
            int i = 0;
            
            @Override
            public void run() {
                if(i < times) {
                    runnable.run();
                } else {
                    cancel();
                }
                
                i++;
            }
        };
        
        if(isAsync) {
            task.runTaskAsynchronously(preTicks, periodTicks);
        } else {
            task.runTask(preTicks, periodTicks);
        }
        
        return task;
    }
    
    
    //Non-static behaviour
    private static final int INVALID_TASKID = -1;
    private int taskID;
    
    public Task(){
        taskID = INVALID_TASKID;
    }
    
    public synchronized boolean isStarted(){
        return taskID != INVALID_TASKID;
    }

    public synchronized void cancel(){
        if(isStarted()){
            cancelTask(taskID);
            taskID = INVALID_TASKID;
        }
    }

    public synchronized void runTask(){
        taskID = Task.runTask(this);
    }

    public synchronized void runTaskAsynchronously(){
        taskID = Task.runTaskAsynchronously(this);
    }

    public synchronized void runTask(long delay){
        taskID = Task.runTask(this, delay);
    }

    public synchronized void runTaskAsynchronously(long delay){
        taskID = Task.runTaskAsynchronously(this, delay);
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public synchronized void runTask(long delay, long period){
        taskID = Task.runTask(() -> {
            try {
                this.run();
            } catch(Throwable ex) {
                ex.printStackTrace();
                this.cancel();
            }
        }, delay, period);
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public synchronized void runTaskAsynchronously(long delay, long period){
        taskID = Task.runTaskAsynchronously(() -> {
            try {
                this.run();
            } catch(Throwable ex) {
                ex.printStackTrace();
                this.cancel();
            }
        }, delay, period);
    }
    
    public synchronized int getTaskId(){
        return taskID;
    }
}
