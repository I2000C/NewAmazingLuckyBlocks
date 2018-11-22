package net.servermc.plugins.Listeners.Objets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import net.servermc.plugins.AmazingLuckyBlocks;
import net.servermc.plugins.utils.CLBManager;
import net.servermc.plugins.utils.LangLoader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.servermc.plugins.utils.WorldList;

import net.servermc.plugins.utils.Timer;
import org.bukkit.scheduler.BukkitScheduler;

public class DarkHole
  implements Listener
{
  HashSet<Material> transparent = new HashSet();
  
  private AmazingLuckyBlocks plugin;
  
  public DarkHole(AmazingLuckyBlocks plugin){
         this.plugin = plugin;
  }
  
  public String color(String str)
  {
    return ChatColor.translateAlternateColorCodes('&', str);
  }
  
  
  int taskID;
  int bloques = CLBManager.getManager().getConfig().getInt("Objects.DarkHole.number-of-blocks");  
  long ticks = CLBManager.getManager().getConfig().getInt("Objects.DarkHole.time-between-one-block-and-the-next");
  
  
  
 
  @EventHandler
  /*public void ejecucion(PlayerInteractEvent lightning){
        Player player = lightning.getPlayer();
        BukkitScheduler sh = Bukkit.getServer().getScheduler();
        taskID = sh.scheduleSyncRepeatingTask(plugin,new Runnable(){
            public void run(){
                    if(tiempo == 0){
                        player.sendMessage("Tiempo agotado");
                        return;
                    }else{
                        player.sendMessage("Bloque colocado");
                        tiempo--; //tambien se puede poner tiempo = tiempo -1;
                        
                    }          
            } 
        },0L,20);
    }*/
  public void regenInteraction(PlayerInteractEvent dark)
  {
    Player player = dark.getPlayer();
    Action action = dark.getAction();
    String darkName = color(LangLoader.LangCfg.getString("Objects.DarkHole.name"));
    if ((action.equals(Action.RIGHT_CLICK_BLOCK)) && (player.getItemInHand().getType() == Material.valueOf(CLBManager.getManager().getConfig().getString("Objects.DarkHole.block-material"))))
    {
      ItemStack stack = player.getItemInHand();
      if (!WorldList.instance.worlds.contains(player.getWorld().getName())) {
        return;
      }
      if ((CLBManager.getManager().getConfig().getBoolean("Objects.DarkHole.required-permission")) && (!player.hasPermission(CLBManager.getManager().getConfig().getString("Objects.DarkHole.permission"))))
      {
        player.sendMessage(color(LangLoader.LangCfg.getString("need-permission")));
        return;
      }
      if ((stack.hasItemMeta()) && (stack.getItemMeta().getDisplayName().equals(darkName))) {

          this.transparent.add(Material.AIR);
          Block block = player.getTargetBlock(this.transparent, 120);
          Location l = block.getLocation();
          ItemStack stack2 = player.getItemInHand();
          bloques = CLBManager.getManager().getConfig().getInt("Objects.DarkHole.number-of-blocks");  
          ticks = CLBManager.getManager().getConfig().getInt("Objects.DarkHole.time-between-one-block-and-the-next");
          
          int amt = stack2.getAmount() - 1;
          if(amt == 0){
              int i = 0;
              
              for(i = 0; i <= 8; i++){
                if(((player.getInventory().getItem(i) != null)) && (player.getInventory().getItem(i).hasItemMeta())){
                   String item_name = player.getInventory().getItem(i).getItemMeta().getDisplayName();
                   //player.sendMessage("" + i);
                   
                   if((item_name.matches(darkName)) && (player.getInventory().getItem(i).getAmount() == 1)){
                      //player.sendMessage(item_name);
                      player.getInventory().clear(i);
                      i = 9;   
                   }
                }  
              }
          }else{
          stack2.setAmount(amt);
          }
          
          Timer c = new Timer(plugin, bloques, ticks, player, l);
          c.darkhole();

           // for(int i = 0; i < player.getInventory().getSize(); i++){
           // ItemStack itm = player.getInventory().getItem(i);
           //  if((itm != null) && (itm.getItemMeta().getDisplayName().equals(lightName))){
           //int amt = itm.getAmount() - 1;
           //itm.setAmount(amt);
           //player.getInventory().setItem(i, amt > 0 ? itm : null);
           //player.updateInventory(); 
           
            }
          }
        }
      }
