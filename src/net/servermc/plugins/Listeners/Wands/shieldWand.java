package net.servermc.plugins.Listeners.Wands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.servermc.plugins.AmazingLuckyBlocks;
import net.servermc.plugins.utils.CLBManager;
import net.servermc.plugins.utils.LangLoader;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import net.servermc.plugins.utils.WorldList;

public class shieldWand
  implements Listener
{
  private final HashMap<UUID, Long> shieldcooldown = new HashMap();
  
  public String color(String str)
  {
    return ChatColor.translateAlternateColorCodes('&', str);
  }
  
  @EventHandler
  public void shieldInteraction(PlayerInteractEvent shield)
  {
    Player player = shield.getPlayer();
    Action action = shield.getAction();
    String shieldName = color(LangLoader.LangCfg.getString("Wands.Shield.name"));
    int dw = CLBManager.getManager().getConfig().getInt("Wands.Shield.cooldown-time");
    if (((action.equals(Action.RIGHT_CLICK_AIR)) || (action.equals(Action.RIGHT_CLICK_BLOCK))) && 
      (player.getItemInHand().getType() == Material.valueOf("RECORD_9")))
    {
      ItemStack stack = player.getItemInHand();
      if (!WorldList.instance.worlds.contains(player.getWorld().getName())) {
        return;
      }
      if ((CLBManager.getManager().getConfig().getBoolean("Wands.Shield.required-permission")) && (!player.hasPermission(CLBManager.getManager().getConfig().getString("Wands.Shield.permission"))))
      {
        player.sendMessage(color(LangLoader.LangCfg.getString("need-permission")));
        return;
      }
      if ((stack.hasItemMeta()) && (stack.getItemMeta().getDisplayName().equals(shieldName))) {
        if ((this.shieldcooldown.containsKey(player.getUniqueId())) && (((Long)this.shieldcooldown.get(player.getUniqueId())).longValue() > System.currentTimeMillis()))
        {
          shield.setCancelled(true);
          long remainingTime = ((Long)this.shieldcooldown.get(player.getUniqueId())).longValue() - System.currentTimeMillis();
          String cmsg = color(LangLoader.LangCfg.getString("Cooldown-message").replace("%time%", String.valueOf(remainingTime / 1000L)));
          player.sendMessage(cmsg);
        }
        else
        {
            
            ItemStack stack2 = player.getItemInHand();
            ItemMeta meta2 = stack2.getItemMeta();
            if(CLBManager.getManager().getConfig().getBoolean("Wands.Shield.limited-uses.enable")){
                if(meta2.hasLore()){
                    List<String> loreList = meta2.getLore();
                    int Uses = Integer.parseInt(loreList.get(1));
                    if(Uses == 0){
                      player.sendMessage(color("&cThis wand has expired"));
                      return;
                    }else{
                      Uses--;
                    }
                    loreList.set(1, String.valueOf(Uses));
                    meta2.setLore(loreList);
                    stack2.setItemMeta(meta2);
                }else{
                    List<String> loreList = new ArrayList();
                    int uses = CLBManager.getManager().getConfig().getInt("Wands.Shield.limited-uses.uses");
                    loreList.add("Uses left:");
                    loreList.add(String.valueOf(uses));
                    meta2.setLore(loreList);
                    stack2.setItemMeta(meta2);
                } 
            }else{
                if(meta2.hasLore()){
                    List<String> loreList = meta2.getLore();
                    loreList.clear();
                    meta2.setLore(loreList);
                    stack2.setItemMeta(meta2);
                }
            }  
            
          this.shieldcooldown.put(player.getUniqueId(), Long.valueOf(System.currentTimeMillis() + dw * 1000));
          Material shield_material = Material.valueOf(CLBManager.getManager().getConfig().getString("Wands.Shield.ShieldWandBlock"));
          World w = player.getWorld();
          boolean withfloor = CLBManager.getManager().getConfig().getBoolean("Wands.Shield.withfloor");
          
          if(withfloor == true){
              
          Block block1 = w.getBlockAt(player.getLocation().add(2.0D, -1.0D, 0.0D));
          block1.setType(shield_material);
          Block block2 = w.getBlockAt(player.getLocation().add(-2.0D, -1.0D, 0.0D));
          block2.setType(shield_material);
          Block block3 = w.getBlockAt(player.getLocation().add(0.0D, -1.0D, 2.0D));
          block3.setType(shield_material);
          Block block4 = w.getBlockAt(player.getLocation().add(0.0D, -1.0D, -2.0D));
          block4.setType(shield_material);
          Block block5 = w.getBlockAt(player.getLocation().add(2.0D, -1.0D, 1.0D));
          block5.setType(shield_material);
          Block block6 = w.getBlockAt(player.getLocation().add(2.0D, -1.0D, -1.0D));
          block6.setType(shield_material);
          Block block7 = w.getBlockAt(player.getLocation().add(-2.0D, -1.0D, 1.0D));
          block7.setType(shield_material);
          Block block8 = w.getBlockAt(player.getLocation().add(-2.0D, -1.0D, -1.0D));
          block8.setType(shield_material);
          Block block9 = w.getBlockAt(player.getLocation().add(1.0D, -1.0D, 2.0D));
          block9.setType(shield_material);
          Block block10 = w.getBlockAt(player.getLocation().add(-1.0D, -1.0D, 2.0D));
          block10.setType(shield_material);
          Block block11 = w.getBlockAt(player.getLocation().add(1.0D, -1.0D, -2.0D));
          block11.setType(shield_material);
          Block block12 = w.getBlockAt(player.getLocation().add(-1.0D, -1.0D, -2.0D));
          block12.setType(shield_material);
          
          Block block13 = w.getBlockAt(player.getLocation().add(0.0D, -1.0D, 0.0D));
          block13.setType(shield_material);
          Block block14 = w.getBlockAt(player.getLocation().add(-1.0D, -1.0D, 0.0D));
          block14.setType(shield_material);
          Block block15 = w.getBlockAt(player.getLocation().add(0.0D, -1.0D, -1.0D));
          block15.setType(shield_material);
          Block block16 = w.getBlockAt(player.getLocation().add(0.0D, -1.0D, 1.0D));
          block16.setType(shield_material);
          Block block17 = w.getBlockAt(player.getLocation().add(1.0D, -1.0D, 0.0D));
          block17.setType(shield_material);
          Block block18 = w.getBlockAt(player.getLocation().add(1.0D, -1.0D, 1.0D));
          block18.setType(shield_material);
          Block block19 = w.getBlockAt(player.getLocation().add(-1.0D, -1.0D, -1.0D));
          block19.setType(shield_material);
          Block block20 = w.getBlockAt(player.getLocation().add(1.0D, -1.0D, -1.0D));
          block20.setType(shield_material);
          Block block21 = w.getBlockAt(player.getLocation().add(-1.0D, -1.0D, 1.0D));
          block21.setType(shield_material);
          }
          
          Block block1 = w.getBlockAt(player.getLocation().add(2.0D, 0.0D, 0.0D));
          block1.setType(shield_material);
          Block block2 = w.getBlockAt(player.getLocation().add(-2.0D, 0.0D, 0.0D));
          block2.setType(shield_material);
          Block block3 = w.getBlockAt(player.getLocation().add(0.0D, 0.0D, 2.0D));
          block3.setType(shield_material);
          Block block4 = w.getBlockAt(player.getLocation().add(0.0D, 0.0D, -2.0D));
          block4.setType(shield_material);
          Block block5 = w.getBlockAt(player.getLocation().add(2.0D, 0.0D, 1.0D));
          block5.setType(shield_material);
          Block block6 = w.getBlockAt(player.getLocation().add(2.0D, 0.0D, -1.0D));
          block6.setType(shield_material);
          Block block7 = w.getBlockAt(player.getLocation().add(-2.0D, 0.0D, 1.0D));
          block7.setType(shield_material);
          Block block8 = w.getBlockAt(player.getLocation().add(-2.0D, 0.0D, -1.0D));
          block8.setType(shield_material);
          Block block9 = w.getBlockAt(player.getLocation().add(1.0D, 0.0D, 2.0D));
          block9.setType(shield_material);
          Block block10 = w.getBlockAt(player.getLocation().add(-1.0D, 0.0D, 2.0D));
          block10.setType(shield_material);
          Block block11 = w.getBlockAt(player.getLocation().add(1.0D, 0.0D, -2.0D));
          block11.setType(shield_material);
          Block block12 = w.getBlockAt(player.getLocation().add(-1.0D, 0.0D, -2.0D));
          block12.setType(shield_material);
          
          
          Block block13 = w.getBlockAt(player.getLocation().add(2.0D, 1.0D, 0.0D));
          block13.setType(shield_material);
          Block block14 = w.getBlockAt(player.getLocation().add(-2.0D, 1.0D, 0.0D));
          block14.setType(shield_material);
          Block block15 = w.getBlockAt(player.getLocation().add(0.0D, 1.0D, 2.0D));
          block15.setType(shield_material);
          Block block16 = w.getBlockAt(player.getLocation().add(0.0D, 1.0D, -2.0D));
          block16.setType(shield_material);
          Block block17 = w.getBlockAt(player.getLocation().add(2.0D, 1.0D, 1.0D));
          block17.setType(shield_material);
          Block block18 = w.getBlockAt(player.getLocation().add(2.0D, 1.0D, -1.0D));
          block18.setType(shield_material);
          Block block19 = w.getBlockAt(player.getLocation().add(-2.0D, 1.0D, 1.0D));
          block19.setType(shield_material);
          Block block20 = w.getBlockAt(player.getLocation().add(-2.0D, 1.0D, -1.0D));
          block20.setType(shield_material);
          Block block21 = w.getBlockAt(player.getLocation().add(1.0D, 1.0D, 2.0D));
          block21.setType(shield_material);
          Block block22 = w.getBlockAt(player.getLocation().add(-1.0D, 1.0D, 2.0D));
          block22.setType(shield_material);
          Block block23 = w.getBlockAt(player.getLocation().add(1.0D, 1.0D, -2.0D));
          block23.setType(shield_material);
          Block block24 = w.getBlockAt(player.getLocation().add(-1.0D, 1.0D, -2.0D));
          block24.setType(shield_material);
          
          
          Block block25 = w.getBlockAt(player.getLocation().add(2.0D, 2.0D, 0.0D));
          block25.setType(shield_material);
          Block block26 = w.getBlockAt(player.getLocation().add(-2.0D, 2.0D, 0.0D));
          block26.setType(shield_material);
          Block block27 = w.getBlockAt(player.getLocation().add(0.0D, 2.0D, 2.0D));
          block27.setType(shield_material);
          Block block28 = w.getBlockAt(player.getLocation().add(0.0D, 2.0D, -2.0D));
          block28.setType(shield_material);
          Block block29 = w.getBlockAt(player.getLocation().add(2.0D, 2.0D, 1.0D));
          block29.setType(shield_material);
          Block block30 = w.getBlockAt(player.getLocation().add(2.0D, 2.0D, -1.0D));
          block30.setType(shield_material);
          Block block31 = w.getBlockAt(player.getLocation().add(-2.0D, 2.0D, 1.0D));
          block31.setType(shield_material);
          Block block32 = w.getBlockAt(player.getLocation().add(-2.0D, 2.0D, -1.0D));
          block32.setType(shield_material);
          Block block33 = w.getBlockAt(player.getLocation().add(1.0D, 2.0D, 2.0D));
          block33.setType(shield_material);
          Block block34 = w.getBlockAt(player.getLocation().add(-1.0D, 2.0D, 2.0D));
          block34.setType(shield_material);
          Block block35 = w.getBlockAt(player.getLocation().add(1.0D, 2.0D, -2.0D));
          block35.setType(shield_material);
          Block block36 = w.getBlockAt(player.getLocation().add(-1.0D, 2.0D, -2.0D));
          block36.setType(shield_material);
          
          
          Block block37 = w.getBlockAt(player.getLocation().add(2.0D, 3.0D, 0.0D));
          block37.setType(shield_material);
          Block block38 = w.getBlockAt(player.getLocation().add(-2.0D, 3.0D, 0.0D));
          block38.setType(shield_material);
          Block block39 = w.getBlockAt(player.getLocation().add(0.0D, 3.0D, 2.0D));
          block39.setType(shield_material);
          Block block40 = w.getBlockAt(player.getLocation().add(0.0D, 3.0D, -2.0D));
          block40.setType(shield_material);
          Block block41 = w.getBlockAt(player.getLocation().add(2.0D, 3.0D, 1.0D));
          block41.setType(shield_material);
          Block block42 = w.getBlockAt(player.getLocation().add(2.0D, 3.0D, -1.0D));
          block42.setType(shield_material);
          Block block43 = w.getBlockAt(player.getLocation().add(-2.0D, 3.0D, 1.0D));
          block43.setType(shield_material);
          Block block44 = w.getBlockAt(player.getLocation().add(-2.0D, 3.0D, -1.0D));
          block44.setType(shield_material);
          Block block45 = w.getBlockAt(player.getLocation().add(1.0D, 3.0D, 2.0D));
          block45.setType(shield_material);
          Block block46 = w.getBlockAt(player.getLocation().add(-1.0D, 3.0D, 2.0D));
          block46.setType(shield_material);
          Block block47 = w.getBlockAt(player.getLocation().add(1.0D, 3.0D, -2.0D));
          block47.setType(shield_material);
          Block block48 = w.getBlockAt(player.getLocation().add(-1.0D, 3.0D, -2.0D));
          block48.setType(shield_material);
          
          
          Block block49 = w.getBlockAt(player.getLocation().add(0.0D, 3.0D, 0.0D));
          block49.setType(shield_material);
          Block block50 = w.getBlockAt(player.getLocation().add(1.0D, 3.0D, 0.0D));
          block50.setType(shield_material);
          Block block51 = w.getBlockAt(player.getLocation().add(-1.0D, 3.0D, 0.0D));
          block51.setType(shield_material);
          Block block52 = w.getBlockAt(player.getLocation().add(0.0D, 3.0D, 1.0D));
          block52.setType(shield_material);
          Block block53 = w.getBlockAt(player.getLocation().add(0.0D, 3.0D, -1.0D));
          block53.setType(shield_material);
          Block block54 = w.getBlockAt(player.getLocation().add(1.0D, 3.0D, -1.0D));
          block54.setType(shield_material);
          Block block55 = w.getBlockAt(player.getLocation().add(-1.0D, 3.0D, 1.0D));
          block55.setType(shield_material);
          Block block56 = w.getBlockAt(player.getLocation().add(1.0D, 3.0D, 1.0D));
          block56.setType(shield_material);
          Block block57 = w.getBlockAt(player.getLocation().add(-1.0D, 3.0D, -1.0D));
          block57.setType(shield_material);
        }
      }
    }
  }
}
