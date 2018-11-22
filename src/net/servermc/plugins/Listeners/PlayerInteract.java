package net.servermc.plugins.Listeners;

import java.util.List;
import net.servermc.plugins.AmazingLuckyBlocks;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;

public class PlayerInteract
  implements Listener
{
  public PlayerInteract(AmazingLuckyBlocks plugin)
  {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }
  
  /*@EventHandler
  public void onPlayerInteract(PlayerInteractEvent event)
  {
    if (!AmazingLuckyBlocks.instance.worlds.contains(event.getPlayer().getWorld().getName())) {
      return;
    }
    if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) && 
      (event.getClickedBlock().getType() == Material.TRAPPED_CHEST))
    {
      event.getClickedBlock().setType(Material.AIR);
      event.getClickedBlock().getWorld().createExplosion(event.getClickedBlock().getLocation(), 2.0F);
    }
  }*/
}
