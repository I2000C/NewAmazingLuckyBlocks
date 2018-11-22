package net.servermc.plugins.Listeners.Wands;

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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import net.servermc.plugins.utils.WorldList;

public class tntWand
  implements Listener
{
  private final HashMap<UUID, Long> tntcooldown = new HashMap();
  
  public String color(String str)
  {
    return ChatColor.translateAlternateColorCodes('&', str);
  }
  
  @EventHandler
  public void tntInteraction(PlayerInteractEvent tnte)
  {
    Player player = tnte.getPlayer();
    Action action = tnte.getAction();
    String tntName = color(LangLoader.LangCfg.getString("Wands.TNT.name"));
    int tw = CLBManager.getManager().getConfig().getInt("Wands.TNT.cooldown-time");
    double v = 4.5D;
    if (((action.equals(Action.RIGHT_CLICK_AIR)) || (action.equals(Action.RIGHT_CLICK_BLOCK))) && 
      (player.getItemInHand().getType() == Material.RECORD_3))
    {
      ItemStack stack = player.getItemInHand();
      if (!WorldList.instance.worlds.contains(player.getWorld().getName())) {
        return;
      }
      if ((CLBManager.getManager().getConfig().getBoolean("Wands.TNT.required-permission")) && (!player.hasPermission(CLBManager.getManager().getConfig().getString("Wands.TNT.permission"))))
      {
        player.sendMessage(color(LangLoader.LangCfg.getString("need-permission")));
        return;
      }
      if ((stack.hasItemMeta()) && (stack.getItemMeta().getDisplayName().equals(tntName))) {
        if ((this.tntcooldown.containsKey(player.getUniqueId())) && (((Long)this.tntcooldown.get(player.getUniqueId())).longValue() > System.currentTimeMillis()))
        {
          tnte.setCancelled(true);
          long remainingTime = ((Long)this.tntcooldown.get(player.getUniqueId())).longValue() - System.currentTimeMillis();
          String cmsg = color(LangLoader.LangCfg.getString("Cooldown-message").replace("%time%", String.valueOf(remainingTime / 1000L)));
          player.sendMessage(cmsg);
        }
        else
        {
          this.tntcooldown.put(player.getUniqueId(), Long.valueOf(System.currentTimeMillis() + tw * 1000));
          Vector direction = player.getEyeLocation().getDirection().multiply(v);
          TNTPrimed tnt = (TNTPrimed)player.getWorld().spawn(player.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), TNTPrimed.class);
          tnt.setVelocity(player.getLocation().getDirection());
        }
      }
    }
  }
}
