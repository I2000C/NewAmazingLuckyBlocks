package me.i2000c.newalb.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.i2000c.newalb.config.ConfigManager;


public class Updater implements Listener{
    private static String latestversion;
    private static Boolean update;
    
    public static void checkUpdates(String name, String version){
        if(ConfigManager.getMainConfig().getBoolean("Update-checker")){
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
            }catch(Exception ex){
                Logger.err("&cAn error while checking update:");
                Logger.err(ex);
            }
        }      
    }
    
    @EventHandler
    private static void onEnter(PlayerJoinEvent event){
        if(update == null){
            return;
        }
        
        Player player = event.getPlayer();
        if(update && player.hasPermission(ConfigManager.getMainConfig().getString("Commands.Update-message-permission"))){
            
            Logger.sendMessage("&a========================================", player);
            Logger.sendMessage("&6New Amazing Lucky Blocks", player);
            Logger.sendMessage("&cThere is a new version available: &e(&7" + latestversion + "&e)", player);
            Logger.sendMessage("&cYou can download it at:&f https://www.spigotmc.org/resources/62644/", player);
            Logger.sendMessage("&a========================================", player);
        }
    }
}