package net.servermc.plugins;

import org.bukkit.plugin.Plugin;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
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
import net.servermc.plugins.Listeners.Objets.IceBow;

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
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.UnknownDependencyException;

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
  public String prefix;
  
  public String serverVersion;
  public String minecraftVersion;
  
  public void onEnable()
  {
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[" + name + " " + ChatColor.AQUA + version + ChatColor.GREEN +"] is now enabled");
    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Plugin by RewKun (Galactic Networks)" + ChatColor.GREEN + " and edited by I2000C");
    new PlayerInteract(this);
    CLBManager.getManager().setup(this);
    
    LangLoader.getManager().registerMessages();
    LangLoader.getManager().getMessages();
    
    prefix = LangLoader.LangCfg.getString("InGamePrefix");
    
    //LangLoader.mkdir();
    //LangLoader.MessageFile();
    serverVersion = getServer().getVersion();
    if(serverVersion.contains("1.8")){
        minecraftVersion = "1.8";
    }else if(serverVersion.contains("1.9")){
        minecraftVersion = "1.9";
    }else if(serverVersion.contains("1.10")){
        minecraftVersion = "1.10";
    }else if(serverVersion.contains("1.11")){
        minecraftVersion = "1.11";
    }else if(serverVersion.contains("1.12")){
        minecraftVersion = "1.12";
    }else if(serverVersion.contains("1.13")){
        minecraftVersion = "1.13";
    }
    
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
    getServer().getPluginManager().registerEvents(new IceBow(), this);
    
    getCommand("alb").setExecutor(new CommandManager(this));
    
    Updater updater = new Updater();
    updater.checkUpdates(name, version);
    getServer().getPluginManager().registerEvents(new Updater(), this);
    
    WorldList wl = new WorldList();
    
    boolean force_enable_plugins = false;
    Plugin[] plugins = getServer().getPluginManager().getPlugins();
    List<Plugin> plugin_list = Arrays.asList(plugins);    
    for(int i=0;i<plugin_list.size();i++){
        String plugin = plugin_list.get(i).toString();
        force_enable_plugins = plugin.contains("Multiverse"); 
    }
    
    if((!getServer().getPluginManager().isPluginEnabled("Multiverse-Core")) && force_enable_plugins){
        Plugin pluginz = getServer().getPluginManager().getPlugin("Multiverse-Core");
        getServer().getPluginManager().enablePlugin(pluginz);
    }
    
    wl.ReloadAll();
  }
  
  
  public void onDisable()
  {
    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + name + " is now disabled");
  }
}
