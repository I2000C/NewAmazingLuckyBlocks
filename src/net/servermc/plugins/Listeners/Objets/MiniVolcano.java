package net.servermc.plugins.Listeners.Objets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitScheduler;

public class MiniVolcano
  implements Listener
{
  HashSet<Material> transparent = new HashSet();
  
  private AmazingLuckyBlocks plugin;
  
  public MiniVolcano(AmazingLuckyBlocks plugin){
         this.plugin = plugin;
  }
  
  public String color(String str)
  {
    return ChatColor.translateAlternateColorCodes('&', str);
  }
  
  
  int taskID;
  long ticks = CLBManager.getManager().getConfig().getInt("Objects.MiniVolcano.time-between-one-block-and-the-next");
  
  
  
 
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
  public void regenInteraction(PlayerInteractEvent miniV)
  {
    Player player = miniV.getPlayer();
    Action action = miniV.getAction();
    String miniVName = color(LangLoader.LangCfg.getString("Objects.MiniVolcano.name"));
    
    if(AmazingLuckyBlocks.instance.minecraftVersion.equals("1.13")){
        if(miniV.getHand() == EquipmentSlot.OFF_HAND){
        return;
        }
    }
    
    if ((action.equals(Action.RIGHT_CLICK_BLOCK)) && (player.getItemInHand().getType() == Material.valueOf(CLBManager.getManager().getConfig().getString("Objects.MiniVolcano.block-material"))))
    {
      ItemStack stack = player.getItemInHand();
      if (!WorldList.instance.worlds.contains(player.getWorld().getName())) {
        return;
      }
      if ((CLBManager.getManager().getConfig().getBoolean("Objects.MiniVolcano.required-permission")) && (!player.hasPermission(CLBManager.getManager().getConfig().getString("Objects.MiniVolcano.permission"))))
      {
        player.sendMessage(color(LangLoader.LangCfg.getString("need-permission")));
        return;
      }
      if ((stack.hasItemMeta()) && (stack.getItemMeta().getDisplayName().equals(miniVName))) {
          
          this.transparent.add(Material.AIR);
          Block block;
          if(AmazingLuckyBlocks.instance.minecraftVersion.equals("1.13")){
            block = player.getTargetBlock((Set<Material>) null, 120);
          }else{
            block = player.getTargetBlock(this.transparent, 120); 
          }
          Location l = block.getLocation();
          ItemStack stack3 = player.getItemInHand();
          ticks = CLBManager.getManager().getConfig().getInt("Objects.MiniVolcano.time-between-one-block-and-the-next");
          
          int amt = stack3.getAmount() - 1;
          if(amt == 0){
              
              for(int i = 0; i <= 8; i++){
                if(((player.getInventory().getItem(i) != null)) && (player.getInventory().getItem(i).hasItemMeta())){
                   String item_name = player.getInventory().getItem(i).getItemMeta().getDisplayName();
                   //player.sendMessage("" + i);
                   
                   if((item_name.matches(miniVName)) && (player.getInventory().getItem(i).getAmount() == 1)){
                      //player.sendMessage(item_name);
                      player.getInventory().clear(i);
                      i = 9;   
                   }
                }  
              }
          }else{
          stack3.setAmount(amt);
          }
          
          Timer c = new Timer(plugin, 1, ticks, player, l);
          c.minivolcano();

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
