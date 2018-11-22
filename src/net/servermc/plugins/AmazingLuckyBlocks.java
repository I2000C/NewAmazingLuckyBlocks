package net.servermc.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import net.servermc.plugins.Listeners.BlockBreak;
import net.servermc.plugins.Listeners.PlayerInteract;
import net.servermc.plugins.Listeners.Wands.dragonWand;
import net.servermc.plugins.Listeners.Wands.invWand;
import net.servermc.plugins.Listeners.Wands.lightningWand;
import net.servermc.plugins.Listeners.Wands.regenWand;
import net.servermc.plugins.Listeners.Wands.slimeWand;
import net.servermc.plugins.Listeners.Wands.tntWand;
import net.servermc.plugins.Listeners.Wands.shieldWand;

import net.servermc.plugins.Listeners.Objets.DarkHole;
import net.servermc.plugins.Listeners.Objets.MiniVolcano;

import net.servermc.plugins.utils.CLBManager;
import net.servermc.plugins.utils.CommandManager;
import net.servermc.plugins.utils.LangLoader;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.servermc.plugins.utils.WorldList;
import net.servermc.plugins.utils.Updater;

public class AmazingLuckyBlocks
  extends JavaPlugin

  implements Listener
{
  public static AmazingLuckyBlocks instance = null;
        
  public AmazingLuckyBlocks()
  {
    instance = this;
  }
  
  public static AmazingLuckyBlocks getInstance()
  {
    return instance;
  }
  
  public void onLoad()
  {
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Loading " + name + ChatColor.GREEN + "...");
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Loading lang...");
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Loading wands...");
  }
  public String rutaConfig;
  PluginDescriptionFile pdffile = getDescription();
  public String version = pdffile.getVersion();
  public String name = ChatColor.GOLD + pdffile.getName();
  
  public void onEnable()
  {
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[name " + ChatColor.GOLD + version + ChatColor.GREEN +"] is now enabled");
    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Plugin by RewKun (Galactic Networks)" + ChatColor.GREEN + " and edited by I2000C");
    new PlayerInteract(this);
    CLBManager.getManager().setup(this);
    LangLoader.mkdir();
    LangLoader.MessageFile();
    
    getServer().getPluginManager().registerEvents(new BlockBreak(), this);
    getServer().getPluginManager().registerEvents(new regenWand(), this);
    getServer().getPluginManager().registerEvents(new dragonWand(), this);
    getServer().getPluginManager().registerEvents(new invWand(), this);
    getServer().getPluginManager().registerEvents(new tntWand(), this);
    getServer().getPluginManager().registerEvents(new slimeWand(), this);
    getServer().getPluginManager().registerEvents(new lightningWand(), this);
    getServer().getPluginManager().registerEvents(new shieldWand(), this);
    getServer().getPluginManager().registerEvents(new DarkHole(instance), this);
    getServer().getPluginManager().registerEvents(new MiniVolcano(instance), this);
    
    getCommand("alb").setExecutor(new CommandManager(this));
    
    //Updater updater = new Updater();
    //updater.checkUpdates(name, version);
    
    WorldList wl = new WorldList();
    wl.ReloadAll();
  }
  
  
  public void onDisable()
  {
    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + name + " is now disabled");
  }
}
