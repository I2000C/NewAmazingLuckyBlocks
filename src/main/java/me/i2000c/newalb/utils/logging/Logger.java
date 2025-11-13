package me.i2000c.newalb.utils.logging;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.i2000c.newalb.api.version.MinecraftVersion;
import me.i2000c.newalb.utils.reflection.ReflectionManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Logger{
    private static final Pattern HEX_COLOR_PATTERN =   Pattern.compile("&(#\\w{6})");
    private static final Pattern HEX_DECOLOR_PATTERN = Pattern.compile("&[xX]((&[0-9a-fA-F]){6})");
    
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
            String aux = ChatColor.translateAlternateColorCodes('&', str);
            
            if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_16)
                    && aux.contains("&#")) {
                
                // https://www.spigotmc.org/threads/hex-chat-class.449300/#post-3864987
                StringBuffer buffer = new StringBuffer();
                Matcher matcher = HEX_COLOR_PATTERN.matcher(aux);
                while(matcher.find()) {
                    // matchedHexColor is like #ABCDEF
                    String matchedHexColor = matcher.group(1);
                    Object hexColor = ReflectionManager.callStaticMethod("net.md_5.bungee.api.ChatColor", "of", matchedHexColor);
                    matcher.appendReplacement(buffer, hexColor.toString());
                }
                
                aux = matcher.appendTail(buffer).toString();
            }
            
            return aux;
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
            String aux = str.replace(ChatColor.COLOR_CHAR, '&');
            
            if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_16)
                    && aux.toLowerCase().contains("&x")) {
                
                StringBuffer buffer = new StringBuffer();
                Matcher matcher = HEX_DECOLOR_PATTERN.matcher(aux);
                while(matcher.find()) {
                    // hexColorData is like &A&B&C&D&E&F
                    String hexColorData = matcher.group(1);
                    String hexColor = "&#" + hexColorData.replaceAll("&", "");
                    matcher.appendReplacement(buffer, hexColor);
                }
                
                aux = matcher.appendTail(buffer).toString();
            }
            
            return aux;
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
        String title = Objects.toString(titleObject);
        if(title.isEmpty()){
            title = "&o";
        }
        
        String subtitle = Objects.toString(subtitleObject);
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
            sender.sendMessage(Logger.color(prefix + object));
        }else{
            sender.sendMessage(ChatColor.stripColor(Logger.color(prefix + object)));
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
