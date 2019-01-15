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
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.entity.Entity;
import org.bukkit.Effect;

import net.servermc.plugins.utils.WorldList;


public class IceBow
    implements Listener
{
  HashSet<Material> transparent = new HashSet();
  
  public String color(String str)
  {
    return ChatColor.translateAlternateColorCodes('&', str);
  }
  
  
  @EventHandler
  public void regenInteraction(EntityDamageByEntityEvent ice){
    
    Entity damagee = ice.getEntity();
    Entity damager = ice.getDamager();
    String iceName = color(LangLoader.LangCfg.getString("Objects.IceBow.name"));
    Material iceMaterial = Material.valueOf(CLBManager.getManager().getConfig().getString("Objects.IceBow.freeze-material"));
    
    if(damager instanceof Arrow){
        Entity hurt = damagee;
        //Player hurt = (Player) e1;
        Player shooter;
        try{
          if(((Arrow)damager).getShooter() instanceof Player){
            Arrow arrow = (Arrow) damager;
            shooter = (Player) arrow.getShooter();
          }else{
            return;
          }
        }catch(ClassCastException e){
            return;
        }
        
            
            if (!WorldList.instance.worlds.contains(shooter.getWorld().getName())) {
                return;
            }
            if ((CLBManager.getManager().getConfig().getBoolean("Objects.IceBow.required-permission")) && (!shooter.hasPermission(CLBManager.getManager().getConfig().getString("Objects.IceBow.permission"))))
            {
                shooter.sendMessage(color(LangLoader.LangCfg.getString("need-permission")));
                return;
            }
            if(shooter.getItemInHand().getType().equals(Material.BOW)){
                  ItemStack hand = shooter.getItemInHand();
                  if(hand != null && hand.hasItemMeta() && hand.getItemMeta().hasDisplayName()){
                      if(hand.getItemMeta().getDisplayName().equals(iceName)){
                          Location hurt_loc = hurt.getLocation();
                          int x = hurt_loc.getBlockX();
                          int y = hurt_loc.getBlockY();
                          int z = hurt_loc.getBlockZ();                          
                          Block b1 = hurt.getWorld().getBlockAt(x+1, y, z);
                          Block b2 = hurt.getWorld().getBlockAt(x-1, y, z);
                          Block b3 = hurt.getWorld().getBlockAt(x, y, z+1);
                          Block b4 = hurt.getWorld().getBlockAt(x, y, z-1);
                          Block b5 = hurt.getWorld().getBlockAt(x+1, y+1, z);
                          Block b6 = hurt.getWorld().getBlockAt(x-1, y+1, z);
                          Block b7 = hurt.getWorld().getBlockAt(x, y+1, z+1);
                          Block b8 = hurt.getWorld().getBlockAt(x, y+1, z-1);
                          Block b9 = hurt.getWorld().getBlockAt(x, y+2, z);
                          b1.setType(iceMaterial);
                          b2.setType(iceMaterial);
                          b3.setType(iceMaterial);
                          b4.setType(iceMaterial);
                          b5.setType(iceMaterial);
                          b5.setType(iceMaterial);
                          b6.setType(iceMaterial);
                          b7.setType(iceMaterial);
                          b8.setType(iceMaterial);
                          b9.setType(iceMaterial);
                          
                          /*Effect effect;
                          try{
                            effect = Effect.valueOf("EXPLOSION_HUGE");
                          }catch(IllegalArgumentException e){
                            effect = Effect.valueOf("SMOKE"); 
                          }
                          hurt_loc.getWorld().playEffect(hurt_loc, effect, 100);*/
                        }
                    }
            }
        }
    }
}
