package net.servermc.plugins.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import net.servermc.plugins.AmazingLuckyBlocks;

public class Updater implements Listener{
    public static String latestversion;
    public static boolean update;
    
    public String color(String str)
  {
    return ChatColor.translateAlternateColorCodes('&', str);
  }
    
    public void checkUpdates(String name, String version){         
          try {
              HttpURLConnection con = (HttpURLConnection) new URL(
                      "https://api.spigotmc.org/legacy/update.php?resource=62644").openConnection();
              int timed_out = 1250;
              con.setConnectTimeout(timed_out);
              con.setReadTimeout(timed_out);
              latestversion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
              if (latestversion.length() <= 30) {
                  if(!version.equals(latestversion)){
                      Bukkit.getConsoleSender().sendMessage(color("&cThere is a new version available: &e("+ChatColor.GRAY+latestversion+"&e)"));
                      Bukkit.getConsoleSender().sendMessage(color("&cYou can download it at: &f https://www.spigotmc.org/resources/62644/"));
                      update = true;
                  }else{
                      Bukkit.getConsoleSender().sendMessage(color("&aYou are using the latest version"));
                      update = false;
                  }          
              }
          } catch (Exception ex) {
              Bukkit.getConsoleSender().sendMessage(color(name + " &cError while checking update."));
              
              
          }
      }
    @EventHandler
    public void onEnter(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if((player.hasPermission(CLBManager.getManager().getConfig().getString("Commands.Update-message-permission")))&&(update == true)){
            
            player.sendMessage(color("&a========================================"));
            player.sendMessage(color("&6New Amazing Lucky Blocks"));
            player.sendMessage(color("&cThere is a new version available: &e("+ChatColor.GRAY+latestversion+"&e)"));
            player.sendMessage(color("&cYou can download it at:&f https://www.spigotmc.org/resources/62644/"));
            player.sendMessage(color("&a========================================"));
        }
      }
    
    
    }
    
