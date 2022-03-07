package me.i2000c.newalb.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import me.i2000c.newalb.utils.Logger.LogLevel;

public class Updater implements Listener{
    private static String latestversion;
    private static Boolean update;
    
    public static void checkUpdates(String name, String version){
        if(ConfigManager.getConfig().getBoolean("Update-checker")){
            Logger.log("&6Checking latest version...");
            try{
                HttpURLConnection con = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=62644").openConnection();
                int timed_out = 1500;
                con.setConnectTimeout(timed_out);
                con.setReadTimeout(timed_out);
                latestversion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
                if(!version.equalsIgnoreCase(latestversion)){
                    if(Integer.parseInt(version.split("_")[version.split("_").length-1]) < Integer.parseInt(latestversion.split("_")[latestversion.split("_").length-1])){
                        Logger.log("&cThere is a new version available: &e(&7" + latestversion + "&e)");
                        Logger.log("&cYou can download it at: &fhttps://www.spigotmc.org/resources/62644/");
                        update = true;  
                    }else{
                        Logger.log("&aYou are using the latest version");
                        update = false;
                    }               
                }else{
                    Logger.log("&aYou are using the latest version");
                    update = false;
                }
            }catch (Exception ex){
                Logger.log("&cError while checking update", LogLevel.ERROR);
            }
        }      
    }
    
    @EventHandler
    private static void onEnter(PlayerJoinEvent event){
        if(update == null){
            return;
        }
        
        Player player = event.getPlayer();
        if((player.hasPermission(ConfigManager.getConfig().getString("Commands.Update-message-permission")))&&(update == true)){
            
            player.sendMessage(Logger.color("&a========================================"));
            player.sendMessage(Logger.color("&6New Amazing Lucky Blocks"));
            player.sendMessage(Logger.color("&cThere is a new version available: &e("+ChatColor.GRAY+latestversion+"&e)"));
            player.sendMessage(Logger.color("&cYou can download it at:&f https://www.spigotmc.org/resources/62644/"));
            player.sendMessage(Logger.color("&a========================================"));
        }
    }
}