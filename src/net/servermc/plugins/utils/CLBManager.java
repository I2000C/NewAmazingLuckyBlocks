package net.servermc.plugins.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import net.servermc.plugins.AmazingLuckyBlocks;

public class CLBManager
{private AmazingLuckyBlocks plugin;
  private static final CLBManager manager = new CLBManager();
  File cfile;
  File dataFolder;
  File pluginFolder;
  FileConfiguration config;
  
  public static CLBManager getManager()
  {
    return manager;
  }

  public void setup(Plugin plugin)
  {
    this.cfile = new File(plugin.getDataFolder(), "config.yml");
    this.config = plugin.getConfig();
    if (!plugin.getDataFolder().exists()) {
      plugin.getDataFolder().mkdir();
    }
    this.pluginFolder = plugin.getDataFolder();
    if (!this.cfile.exists()) {
        try
        {
        this.cfile.createNewFile();
        }
        catch (IOException e)
        {
        System.out.println(ChatColor.RED + "Amazing Lucky Blocks: Could not create config. The plugin is shutting down. Error:");
        e.printStackTrace();
        Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }
    this.config.options().copyDefaults(true);
    getManager().saveConfig();
  }
  
  public void saveConfig()
  {
    try
    {
      this.config.save(this.cfile);
    }
    catch (IOException e)
    {
      System.out.println(ChatColor.RED + "Amazing Lucky Blocks Error: Could not save config. Error:");
      e.printStackTrace();
    }
  }
  
  public FileConfiguration getConfig()
  {
    return this.config;
  }
  
  public File getPluginFolder()
  {
    return this.pluginFolder;
  }
  
  public File getDataFolder()
  {
    return this.dataFolder;
  }
}
