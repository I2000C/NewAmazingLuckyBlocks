package net.servermc.plugins.utils;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import net.servermc.plugins.utils.CLBManager;
import net.servermc.plugins.utils.LangLoader;
import net.servermc.plugins.AmazingLuckyBlocks;

public class WorldList {
    
   public final List<String> worlds = new ArrayList();
   public static WorldList instance = null;
    
   public WorldList()
   {
       instance = this;
   }
   
   public String color(String str)
  {
    return ChatColor.translateAlternateColorCodes('&', str);
  }
   
   
   public void ReloadAll(){
    String line1 = color(LangLoader.LangCfg.getString("World-loading.line1"));
    String line2 = color(LangLoader.LangCfg.getString("World-loading.line2"));
    String line3 = color(LangLoader.LangCfg.getString("World-loading.line3"));
    String line4 = color(LangLoader.LangCfg.getString("World-loading.line4"));
    String line5 = color(LangLoader.LangCfg.getString("World-loading.line5"));
    String line6 = color(LangLoader.LangCfg.getString("World-loading.line6"));
    String line7 = color(LangLoader.LangCfg.getString("World-loading.line7"));
    String line8 = color(LangLoader.LangCfg.getString("World-loading.line8"));
    this.worlds.clear();
    for (String world : CLBManager.getManager().getConfig().getStringList("Worlds-list")) {
      if (Bukkit.getWorld(world) == null) {
      Bukkit.getConsoleSender().sendMessage(line1 + ChatColor.AQUA + world);
      } else {
      this.worlds.add(world);}
      }
    if(this.worlds.isEmpty()){
        CLBManager.getManager().setup(AmazingLuckyBlocks.instance);
        
        Bukkit.getConsoleSender().sendMessage(line2);
        Bukkit.getConsoleSender().sendMessage(line3);
        Bukkit.getConsoleSender().sendMessage(line4);
        Bukkit.getConsoleSender().sendMessage(line5);
        Bukkit.getConsoleSender().sendMessage(line6);
    }
    
    Bukkit.getConsoleSender().sendMessage(line7 + " " + ChatColor.LIGHT_PURPLE + this.worlds.size() + " " + line8);
        
    }
}