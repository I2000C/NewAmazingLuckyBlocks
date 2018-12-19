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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import net.servermc.plugins.utils.WorldList;

public class slimeWand
  implements Listener
{
  private final HashMap<UUID, Long> slimecooldown = new HashMap();
  
  public String color(String str)
  {
    return ChatColor.translateAlternateColorCodes('&', str);
  }
  
  @EventHandler
  public void slimeInteraction(PlayerInteractEvent slimee)
  {
    Player player = slimee.getPlayer();
    Action action = slimee.getAction();
    String slimeName = color(LangLoader.LangCfg.getString("Wands.Slime.name"));
    int sw = CLBManager.getManager().getConfig().getInt("Wands.Slime.cooldown-time");
    if (((action.equals(Action.RIGHT_CLICK_AIR)) || (action.equals(Action.RIGHT_CLICK_BLOCK))) && 
      (player.getItemInHand().getType() == Material.valueOf("RECORD_4")))
    {
      ItemStack stack = player.getItemInHand();
      if (!WorldList.instance.worlds.contains(player.getWorld().getName())) {
        return;
      }
      if ((CLBManager.getManager().getConfig().getBoolean("Wands.Slime.required-permission")) && (!player.hasPermission(CLBManager.getManager().getConfig().getString("Wands.Slime.permission"))))
      {
        player.sendMessage(color(LangLoader.LangCfg.getString("need-permission")));
        return;
      }
      if ((stack.hasItemMeta()) && (stack.getItemMeta().getDisplayName().equals(slimeName))) {
        if ((this.slimecooldown.containsKey(player.getUniqueId())) && (((Long)this.slimecooldown.get(player.getUniqueId())).longValue() > System.currentTimeMillis()))
        {
          slimee.setCancelled(true);
          long remainingTime = ((Long)this.slimecooldown.get(player.getUniqueId())).longValue() - System.currentTimeMillis();
          String cmsg = color(LangLoader.LangCfg.getString("Cooldown-message").replace("%time%", String.valueOf(remainingTime / 1000L)));
          player.sendMessage(cmsg);
        }
        else
        {
          this.slimecooldown.put(player.getUniqueId(), Long.valueOf(System.currentTimeMillis() + sw * 1000));
          Slime slime = (Slime)slimee.getPlayer().getWorld().spawnEntity(player.getLocation().add(0.0D, 2.0D, 0.0D), EntityType.SLIME);
          slime.setVelocity(player.getLocation().getDirection().multiply(1.8D));
        }
      }
    }
  }
}
