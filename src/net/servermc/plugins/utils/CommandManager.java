package net.servermc.plugins.utils;

import net.servermc.plugins.utils.WorldList;
import net.servermc.plugins.utils.RandomBlocks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.servermc.plugins.AmazingLuckyBlocks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class CommandManager
  implements CommandExecutor
{
  private final AmazingLuckyBlocks plugin;
  
  public CommandManager(AmazingLuckyBlocks instance)
  {
    this.plugin = instance;
  }
  
  public String color(String str)
  {
    return ChatColor.translateAlternateColorCodes('&', str);
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
    if ((cmd.getName().equalsIgnoreCase("alb")) & (args.length == 0))
    {
      String line1 = color(LangLoader.LangCfg.getString("Helpmenu.line1") + " " + ChatColor.RED + (plugin.version));
      String line2 = color(LangLoader.LangCfg.getString("Helpmenu.line2"));
      String line3 = color(LangLoader.LangCfg.getString("Helpmenu.line3"));
      String line4 = color(LangLoader.LangCfg.getString("Helpmenu.line4"));
      String line5 = color(LangLoader.LangCfg.getString("Helpmenu.line5"));
      String line6 = color(LangLoader.LangCfg.getString("Helpmenu.line6"));
      String line7 = color(LangLoader.LangCfg.getString("Helpmenu.line7"));
      String line8 = color(LangLoader.LangCfg.getString("Helpmenu.line8"));
      sender.sendMessage (line1);
      sender.sendMessage (line2);
      sender.sendMessage (line3);
      sender.sendMessage (line4);
      sender.sendMessage (line5);
      sender.sendMessage (line6);
      sender.sendMessage (line7);
      sender.sendMessage (line8);
    }
    
     else if ((args.length == 1) && 
        (args[0].equalsIgnoreCase("wands")))
      {
        String wandsperm = CLBManager.getManager().getConfig().getString("Commands.Wands-permission");
        if (sender.hasPermission(wandsperm))
        {
            if(!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage(plugin.name+ChatColor.RED+" You can't send this command from the console");
			return false;
            }else{
        Player p = (Player) sender;
        
        //Regen wand
            
        ItemStack stack6 = new ItemStack(Material.valueOf("RECORD_8"));
        ItemMeta meta2 = stack6.getItemMeta();
        meta2.setDisplayName(color(LangLoader.LangCfg.getString("Wands.Regen.name")));
        stack6.setItemMeta(meta2);
        
        //Invisibility wand
        
        ItemStack stack7 = new ItemStack(Material.valueOf("RECORD_7"));
        ItemMeta meta3 = stack7.getItemMeta();
        meta3.setDisplayName(color(LangLoader.LangCfg.getString("Wands.Invisibility.name")));
        stack7.setItemMeta(meta3);
        
        //Tnt wand
        
        ItemStack stack21 = new ItemStack(Material.valueOf("RECORD_3"));
        ItemMeta meta4 = stack21.getItemMeta();
        meta4.setDisplayName(color(LangLoader.LangCfg.getString("Wands.TNT.name")));
        stack21.setItemMeta(meta4);
        
        //Slime wand
        
        ItemStack stack22 = new ItemStack(Material.valueOf("RECORD_4"));
        ItemMeta meta5 = stack22.getItemMeta();
        meta5.setDisplayName(color(LangLoader.LangCfg.getString("Wands.Slime.name")));
        stack22.setItemMeta(meta5);
        
        //Dragon-breath wand
        
        ItemStack stack23 = new ItemStack(Material.valueOf("RECORD_6"));
        ItemMeta meta6 = stack23.getItemMeta();
        meta6.setDisplayName(color(LangLoader.LangCfg.getString("Wands.Dragon-breath.name")));
        stack23.setItemMeta(meta6);
        
        //Lightning wand
        
        ItemStack stack24 = new ItemStack(Material.valueOf("RECORD_5"));
        ItemMeta meta7 = stack24.getItemMeta();
        meta7.setDisplayName(color(LangLoader.LangCfg.getString("Wands.Lightning.name")));
        stack24.setItemMeta(meta7);
        
        //Shield wand
        
        ItemStack stack29 = new ItemStack(Material.valueOf("RECORD_9"));
        ItemMeta meta8 = stack29.getItemMeta();
        meta8.setDisplayName(color(LangLoader.LangCfg.getString("Wands.Shield.name")));
        stack29.setItemMeta(meta8);

        p.getInventory().addItem(stack6);
        p.getInventory().addItem(stack7);
        p.getInventory().addItem(stack21);
        p.getInventory().addItem(stack22);
        p.getInventory().addItem(stack23);
        p.getInventory().addItem(stack24);
        p.getInventory().addItem(stack29);
        
        String loadwands = color(LangLoader.LangCfg.getString("LoadingWands"));
        sender.sendMessage (loadwands);
            }
        }
        else 
        {
          String noperm = color(LangLoader.LangCfg.getString("NoPermission"));
          sender.sendMessage (noperm);
        }
      }else if (args[0].equalsIgnoreCase("objects")){
              String wandsperm = CLBManager.getManager().getConfig().getString("Commands.Objects-permission");
        if (sender.hasPermission(wandsperm))
        {
            if(!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage(plugin.name+ChatColor.RED+" You can't send this command from the console");
			return false;
            }else{
        int amount = 0;
        Player p = (Player) sender;
        if(args.length > 1)
        {
        try{
            amount = Integer.parseInt(args[1]);
        }catch(IllegalArgumentException e){
            amount = 1;
            }
        }else{
            amount = 1;
        }
        //Dark Hole
            
        ItemStack stack24 = new ItemStack(Material.valueOf(CLBManager.getManager().getConfig().getString("Objects.DarkHole.block-material")));
        ItemMeta meta7 = stack24.getItemMeta();
        meta7.setDisplayName(color(LangLoader.LangCfg.getString("Objects.DarkHole.name")));
        stack24.setAmount(amount);
        stack24.setItemMeta(meta7);
        
        //Mini Volcano
        
        ItemStack stack25 = new ItemStack(Material.valueOf(CLBManager.getManager().getConfig().getString("Objects.MiniVolcano.block-material")));
        ItemMeta meta8 = stack25.getItemMeta();
        meta8.setDisplayName(color(LangLoader.LangCfg.getString("Objects.MiniVolcano.name")));
        stack25.setAmount(amount);
        stack25.setItemMeta(meta8);
        
        
        p.getInventory().addItem(stack24);
        p.getInventory().addItem(stack25);
        
        String loadobjects = color(LangLoader.LangCfg.getString("LoadingObjects"));
        sender.sendMessage(loadobjects);
            }
        }else 
        {
          String noperm = color(LangLoader.LangCfg.getString("NoPermission"));
          sender.sendMessage (noperm);
        }
      }else if ((args.length == 1) && 
        (args[0].equalsIgnoreCase("reload")))
      {
        String rlperm = CLBManager.getManager().getConfig().getString("Commands.Reload-permission");
        if (sender.hasPermission(rlperm))
        {
          plugin.reloadConfig();
          File cfile = CLBManager.getManager().cfile;
          //LangLoader.mkdir();
          //LangLoader.MessageFile();
          //LangLoader.loadLang();
          CLBManager.getManager().config = YamlConfiguration.loadConfiguration(cfile);
          CLBManager.getManager().saveConfig();
          LangLoader.getManager().reloadMessages();
          LangLoader.getManager().getMessages();
          
          WorldList.instance.ReloadAll();
          
          String reload1 = color(LangLoader.LangCfg.getString("Reload.line1"));
          String reload2 = color(LangLoader.LangCfg.getString("Reload.line2"));
          String reload3 = color(LangLoader.LangCfg.getString("Reload.line3"));
          sender.sendMessage(reload1);
          sender.sendMessage(reload2);
          sender.sendMessage(reload3);
        }
        else
        {
          String noperm2 = color(LangLoader.LangCfg.getString("NoPermission"));
          sender.sendMessage (noperm2);
        }
        return true;
      }else if((args.length == 6) && 
        (args[0].equalsIgnoreCase("randomblock"))){
          String rbperm = CLBManager.getManager().getConfig().getString("Commands.RandomBlocks-permission");
          if(!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage(plugin.name+ChatColor.RED+" You can't send this command from the console");
			return false;
            }else{
          if(sender.hasPermission(rbperm)){
          Player player = (Player) sender;
          int radx = Integer.parseInt(args[1]);
          int rady = Integer.parseInt(args[2]);
          int radz = Integer.parseInt(args[3]);
          int blocks = Integer.parseInt(args[4]);
          int floating_blocks;
                if(args[5].equals("true")){
              floating_blocks = 1;
                }else{
              floating_blocks = 0;
                }
          
          RandomBlocks rb = new RandomBlocks(radx,rady,radz,blocks,floating_blocks,player);
          rb.generateRandomBlocks();
          }else{
                String noperm = color(LangLoader.LangCfg.getString("NoPermission"));
                sender.sendMessage(plugin.name + " " +noperm);
            }
          }
      }else {
          String unknowncommand = color(LangLoader.LangCfg.getString("UnknownCommand"));
          sender.sendMessage (unknowncommand);
      }
      return false;
    }
    
  }

