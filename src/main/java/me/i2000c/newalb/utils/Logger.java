package me.i2000c.newalb.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Logger{
    private static String pluginPrefix;
    private static boolean coloredLogger;
    
    public static void initializeLogger(String pluginPrefix, boolean coloredLogger){
        Logger.pluginPrefix = pluginPrefix;
        Logger.coloredLogger = coloredLogger;
    }
    
    // String color and deColor
    public static String color(String str){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(str == null){
            return str;
        }else{
            return ChatColor.translateAlternateColorCodes('&', str);
        }
//</editor-fold>
    }
    public static List<String> color(List<String> list){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(list == null){
            return list;
        }else{
            List<String> coloredList = new ArrayList<>();
            for(String str : list){
                coloredList.add(Logger.color(str));
            }
            return coloredList;
        }
//</editor-fold>
    }
    // List color and deColor
    public static String deColor(String str){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(str == null){
            return str;
        }else{
            //https://www.spigotmc.org/threads/solved-itemstack-chatcolor-to-string.52095/
            return str.replace(ChatColor.COLOR_CHAR, '&');
        }
//</editor-fold>
    }    
    public static List<String> deColor(List<String> list){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(list == null){
            return null;
        }else{
            List<String> decolored_list = new ArrayList<>();
            for(String str : list){
                decolored_list.add(Logger.deColor(str));
            }
            return decolored_list;
        }
//</editor-fold>
    }
    // String stripColor (Remove color codes)
    public static String stripColor(String str){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(str == null){
            return str;
        }else{
            return ChatColor.stripColor(str);
        }
//</editor-fold>
    }
    
    // Titles
    public static void sendTitle(Object titleObject, Object subtitleObject, Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String title = titleObject.toString();
        if(title.isEmpty()){
            title = "&o";
        }
        
        String subtitle = subtitleObject.toString();
        if(subtitle.isEmpty()){
            subtitle = "&o";
        }
        
        player.sendTitle(color(title), color(subtitle));
//</editor-fold>
    }
    public static void removeTitle(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        sendTitle("", "", player);
//</editor-fold>
    }
    
    // Log and messages
    public static void log(Object object, boolean withPrefix){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String prefix = withPrefix ? pluginPrefix + " " : "";
        
        if(coloredLogger){
            Bukkit.getConsoleSender().sendMessage(Logger.color(prefix + object));
        }else{
            Bukkit.getConsoleSender().sendMessage(ChatColor.stripColor(Logger.color(prefix + object)));
        }
//</editor-fold>
    }
    public static void log(Object object){
        //<editor-fold defaultstate="collapsed" desc="Code">
        log(object, true);
//</editor-fold>
    }
    
    public static void warn(Object object, boolean withPrefix){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String prefix = withPrefix ? pluginPrefix + " " : "";
        Bukkit.getLogger().log(Level.WARNING, ChatColor.stripColor(Logger.color(prefix + object)));
//</editor-fold>
    }
    public static void warn(Object object){
        //<editor-fold defaultstate="collapsed" desc="Code">
        warn(object, true);
//</editor-fold>
    }
    
    public static void err(Object object, boolean withPrefix){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String prefix = withPrefix ? pluginPrefix + " " : "";
        Bukkit.getLogger().log(Level.SEVERE, ChatColor.stripColor(Logger.color(prefix + object)));
//</editor-fold>
    }
    public static void err(Object object){
        //<editor-fold defaultstate="collapsed" desc="Code">
        err(object, true);
//</editor-fold>
    }
        
    public static void sendMessage(Object object, CommandSender sender, boolean withPrefix){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String prefix = withPrefix ? pluginPrefix + " " : "";
        if(coloredLogger || sender instanceof Player){
            sender.sendMessage(Logger.color(prefix + object.toString()));
        }else{
            sender.sendMessage(ChatColor.stripColor(Logger.color(prefix + object.toString())));
        }
//</editor-fold>
    }    
    public static void sendMessage(Object object, CommandSender sender){
        //<editor-fold defaultstate="collapsed" desc="Code">
        sendMessage(object, sender, true);
//</editor-fold>
    }
    
    public static void logAndMessage(Object object, CommandSender sender){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(sender instanceof Player){
            Logger.sendMessage(object, sender);
        }
        Logger.log(object);
//</editor-fold>
    }
    public static void warnAndMessage(Object object, CommandSender sender){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(sender instanceof Player){
            Logger.sendMessage(object, sender);
        }
        Logger.warn(object);
//</editor-fold>
    }
    public static void errAndMessage(Object object, CommandSender sender){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(sender instanceof Player){
            Logger.sendMessage(object, sender);
        }
        Logger.err(object);
//</editor-fold>
    }
}
