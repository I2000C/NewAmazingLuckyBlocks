package me.i2000c.newalb.utils.logger;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Logger{
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
    public static String stripColor(String str){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(str == null){
            return str;
        }else{
            return ChatColor.stripColor(str);
        }
//</editor-fold>
    }
    
    private static final NewAmazingLuckyBlocks PLUGIN = NewAmazingLuckyBlocks.getInstance();
    
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
    
    public static void log(Object object, boolean withPrefix){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String prefix = withPrefix ? PLUGIN.prefix + " " : "";
        
        if(ConfigManager.getConfig().getBoolean("ColoredLogger")){
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
    public static void log(Object object, LogLevel level, boolean withPrefix){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String prefix = withPrefix ? PLUGIN.prefix + " " : "";
        
        switch(level){
            case INFO:
                log(object, withPrefix);
                break;
            case WARN:
                Bukkit.getLogger().log(Level.WARNING, ChatColor.stripColor(Logger.color(prefix + object)));
                break;
            case ERROR:
                Bukkit.getLogger().log(Level.SEVERE, ChatColor.stripColor(Logger.color(prefix + object)));
                break;
        }
//</editor-fold>
    }
    public static void log(Object object, LogLevel level){
        //<editor-fold defaultstate="collapsed" desc="Code">
        log(object, level, true);
//</editor-fold>
    }
    
    public static void sendMessage(Object object, CommandSender sender, boolean withPrefix){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(withPrefix){
            sendMessage(object, sender);
        }else{
            if(ConfigManager.getConfig().getBoolean("ColoredLogger") || sender instanceof Player){
                sender.sendMessage(Logger.color(object.toString()));
            }else{
                sender.sendMessage(ChatColor.stripColor(Logger.color(object.toString())));
            }
        }
//</editor-fold>
    }
    
    public static void sendMessage(Object object, CommandSender sender){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(ConfigManager.getConfig().getBoolean("ColoredLogger") || sender instanceof Player){
            sender.sendMessage(Logger.color(PLUGIN.prefix + " " + object.toString()));
        }else{
            sender.sendMessage(ChatColor.stripColor(Logger.color(PLUGIN.prefix + " " + object.toString())));
        }
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
    
    public static void logAndMessage(Object object, CommandSender sender, LogLevel level){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(sender instanceof Player){
            Logger.sendMessage(object, sender);
        }
        Logger.log(object, level);
//</editor-fold>
    }
}
