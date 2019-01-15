package net.servermc.plugins.Listeners.Wands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.servermc.plugins.AmazingLuckyBlocks;
import net.servermc.plugins.utils.CLBManager;
import net.servermc.plugins.utils.LangLoader;
import org.bukkit.ChatColor;
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

public class lightningWand
  implements Listener
{
  private final HashMap<UUID, Long> lightningcooldown = new HashMap();
  HashSet<Material> transparent = new HashSet();
  
  public String color(String str)
  {
    return ChatColor.translateAlternateColorCodes('&', str);
  }
  
  @EventHandler
  public void regenInteraction(PlayerInteractEvent lightning)
  {
    Player player = lightning.getPlayer();
    Action action = lightning.getAction();
    String lightName = color(LangLoader.LangCfg.getString("Wands.Lightning.name"));
    int lw = CLBManager.getManager().getConfig().getInt("Wands.Lightning.cooldown-time");
    if (((action.equals(Action.RIGHT_CLICK_AIR)) || (action.equals(Action.RIGHT_CLICK_BLOCK))) && 
      (player.getItemInHand().getType() == Material.valueOf("RECORD_5")))
    {
      ItemStack stack = player.getItemInHand();
      if (!WorldList.instance.worlds.contains(player.getWorld().getName())) {
        return;
      }
      if ((CLBManager.getManager().getConfig().getBoolean("Wands.Lightning.required-permission")) && (!player.hasPermission(CLBManager.getManager().getConfig().getString("Wands.Lightning.permission"))))
      {
        player.sendMessage(color(LangLoader.LangCfg.getString("need-permission")));
        return;
      }
      if ((stack.hasItemMeta()) && (stack.getItemMeta().getDisplayName().equals(lightName))) {
        if ((this.lightningcooldown.containsKey(player.getUniqueId())) && (((Long)this.lightningcooldown.get(player.getUniqueId())).longValue() > System.currentTimeMillis()))
        {
          lightning.setCancelled(true);
          long remainingTime = ((Long)this.lightningcooldown.get(player.getUniqueId())).longValue() - System.currentTimeMillis();
          String cmsg = color(LangLoader.LangCfg.getString("Cooldown-message").replace("%time%", String.valueOf(remainingTime / 1000L)));
          player.sendMessage(cmsg);
        }
        else
        {
          this.lightningcooldown.put(player.getUniqueId(), Long.valueOf(System.currentTimeMillis() + lw * 1000));
          this.transparent.add(Material.AIR);
          Block block;
          if(AmazingLuckyBlocks.instance.minecraftVersion.equals("1.13")||AmazingLuckyBlocks.instance.minecraftVersion.equals("1.14")){
            block = player.getTargetBlock((Set<Material>) null, 120);
          }else{
            block = player.getTargetBlock(this.transparent, 120); 
          }
            ItemStack stack2 = player.getItemInHand();
            ItemMeta meta2 = stack2.getItemMeta();
            if(CLBManager.getManager().getConfig().getBoolean("Wands.Lightning.limited-uses.enable")){
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
                    int uses = CLBManager.getManager().getConfig().getInt("Wands.Lightning.limited-uses.uses");
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
          player.getWorld().strikeLightning(block.getLocation());
        }
      }
    }
  }
}
