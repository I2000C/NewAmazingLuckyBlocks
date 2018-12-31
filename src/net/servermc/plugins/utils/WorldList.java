package net.servermc.plugins.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Server;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
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
    String line1 = color(LangLoader.LangCfg.getString("World-loading.line1").replaceAll("%prefix%", AmazingLuckyBlocks.getInstance().prefix));
    String line2 = color(LangLoader.LangCfg.getString("World-loading.line2").replaceAll("%prefix%", AmazingLuckyBlocks.getInstance().prefix));
    String line3 = color(LangLoader.LangCfg.getString("World-loading.line3").replaceAll("%prefix%", AmazingLuckyBlocks.getInstance().prefix));
    String line4 = color(LangLoader.LangCfg.getString("World-loading.line4").replaceAll("%prefix%", AmazingLuckyBlocks.getInstance().prefix));
    String line5 = color(LangLoader.LangCfg.getString("World-loading.line5").replaceAll("%prefix%", AmazingLuckyBlocks.getInstance().prefix));
    String line6 = color(LangLoader.LangCfg.getString("World-loading.line6").replaceAll("%prefix%", AmazingLuckyBlocks.getInstance().prefix));
    String line7 = color(LangLoader.LangCfg.getString("World-loading.line7").replaceAll("%prefix%", AmazingLuckyBlocks.getInstance().prefix));
    String line8 = color(LangLoader.LangCfg.getString("World-loading.line8").replaceAll("%prefix%", AmazingLuckyBlocks.getInstance().prefix));
    this.worlds.clear();
    for (String world : CLBManager.getManager().getConfig().getStringList("Worlds-list")) {
      if (Bukkit.getWorld(world) == null) {
      Bukkit.getConsoleSender().sendMessage(line1 + ChatColor.AQUA + " " + world);
      } else {
      this.worlds.add(world);}
      }
    if(this.worlds.isEmpty()){
        CLBManager.getManager().setup(AmazingLuckyBlocks.instance);
        
        Bukkit.getConsoleSender().sendMessage(line2);
        Bukkit.getConsoleSender().sendMessage(line3);
        Bukkit.getConsoleSender().sendMessage(line4);
        Bukkit.getConsoleSender().sendMessage(line5);
        Bukkit.getLogger().log(Level.WARNING, "NewAmazingLuckyBlocks has loaded 0 worlds");
        Bukkit.getConsoleSender().sendMessage(line6);
        
    }else{
        Bukkit.getConsoleSender().sendMessage(line7 + " " + ChatColor.LIGHT_PURPLE + this.worlds.size() + " " + line8);
    }
  }
}