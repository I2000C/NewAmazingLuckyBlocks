package net.servermc.plugins.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;

import net.servermc.plugins.AmazingLuckyBlocks;

public class Updater {
    public String latestversion;
    
    public String color(String str)
  {
    return ChatColor.translateAlternateColorCodes('&', str);
  }
    
    public void checkUpdates(String name, String version){        
          try {
              HttpURLConnection con = (HttpURLConnection) new URL(
                      "https://api.spigotmc.org/legacy/update.php?resource=22919").openConnection();
              int timed_out = 1250;
              con.setConnectTimeout(timed_out);
              con.setReadTimeout(timed_out);
              latestversion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
              if (latestversion.length() <= 30) {
                  if(!version.equals(latestversion)){
                      Bukkit.getConsoleSender().sendMessage(color("&cThere is a new version available. &e("+ChatColor.GRAY+latestversion+"&e)"));
                      Bukkit.getConsoleSender().sendMessage(color("&cYou can download it at: &f https://www.spigotmc.org/resources/22919/"));  
                  }          
              }
          } catch (Exception ex) {
              Bukkit.getConsoleSender().sendMessage(color(name + " &cError while checking update."));
              
              
          }
      }
    }
    
