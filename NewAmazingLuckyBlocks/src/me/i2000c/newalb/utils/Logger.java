package me.i2000c.newalb.utils;

import me.i2000c.newalb.NewAmazingLuckyBlocks;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Logger{
    public static String color(String str){
        return ChatColor.translateAlternateColorCodes('&', str);
    }
    public static List<String> color(List<String> list){
        List<String> coloredList = new ArrayList();
        for(String str : list){
            coloredList.add(Logger.color(str));
        }
        return coloredList;
    }
    public static String deColor(String str){
        //https://www.spigotmc.org/threads/solved-itemstack-chatcolor-to-string.52095/
        return str.replace(ChatColor.COLOR_CHAR, '&');
    }    
    public static List<String> deColor(List<String> list){
        List<String> decolored_list = new ArrayList();
        for(String str : list){
            decolored_list.add(Logger.deColor(str));
        }
        return decolored_list;
    }
    public static String stripColor(String str){
        return ChatColor.stripColor(str);
    }
    
    private static final NewAmazingLuckyBlocks PLUGIN = NewAmazingLuckyBlocks.getInstance();
    
    public static void log(Object object){
        if(ConfigManager.getConfig().getBoolean("ColoredLogger")){
            Bukkit.getConsoleSender().sendMessage(Logger.color(PLUGIN.prefix + " " + object));
        }else{
            Bukkit.getConsoleSender().sendMessage(ChatColor.stripColor(Logger.color(PLUGIN.prefix + " " + object)));
        }
    }
    public static void log(Object object, LogLevel level){
        switch(level){
            case INFO:
                log(object);
                break;
            case WARN:
                Bukkit.getLogger().log(Level.WARNING, ChatColor.stripColor(Logger.color(PLUGIN.prefix + " " + object)));
                break;
            case ERROR:
                Bukkit.getLogger().log(Level.SEVERE, ChatColor.stripColor(Logger.color(PLUGIN.prefix + " " + object)));
                break;
        }
    }
    
    public static void sendMessage(Object object, CommandSender sender, boolean withPrefix){
        if(withPrefix){
            sendMessage(object, sender);
        }else{
            if(ConfigManager.getConfig().getBoolean("ColoredLogger") || sender instanceof Player){
                sender.sendMessage(Logger.color(object.toString()));
            }else{
                sender.sendMessage(ChatColor.stripColor(Logger.color(object.toString())));
            }
        }
    }
    
    public static void sendMessage(Object object, CommandSender sender){
        if(ConfigManager.getConfig().getBoolean("ColoredLogger") || sender instanceof Player){
            sender.sendMessage(Logger.color(PLUGIN.prefix + " " + object.toString()));
        }else{
            sender.sendMessage(ChatColor.stripColor(Logger.color(PLUGIN.prefix + " " + object.toString())));
        }        
    }
    
    public static void logAndMessage(Object object, CommandSender sender){
        if(sender instanceof Player){
            Logger.sendMessage(object, sender);
        }
        Logger.log(object);        
    }
    
    public static void logAndMessage(Object object, CommandSender sender, LogLevel level){
        if(sender instanceof Player){
            Logger.sendMessage(object, sender);
        }
        Logger.log(object, level);        
    }    

    public static enum LogLevel{
        INFO,
        WARN,
        ERROR;
    }
}
