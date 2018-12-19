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

public class dragonWand
  implements Listener
{
  private final HashMap<UUID, Long> dragoncooldown = new HashMap();
  
  public String color(String str)
  {
    return ChatColor.translateAlternateColorCodes('&', str);
  }
  
  @EventHandler
  public void dragonInteraction(PlayerInteractEvent dragon)
  {
    Player player = dragon.getPlayer();
    Action action = dragon.getAction();
    String dragonName = color(LangLoader.LangCfg.getString("Wands.Dragon-breath.name"));
    int dw = CLBManager.getManager().getConfig().getInt("Wands.Dragon-breath.cooldown-time");
    if (((action.equals(Action.RIGHT_CLICK_AIR)) || (action.equals(Action.RIGHT_CLICK_BLOCK))) && 
      (player.getItemInHand().getType() == Material.valueOf("RECORD_6")))
    {
      ItemStack stack = player.getItemInHand();
      if (!WorldList.instance.worlds.contains(player.getWorld().getName())) {
        return;
      }
      if ((CLBManager.getManager().getConfig().getBoolean("Wands.Dragon-breath.required-permission")) && (!player.hasPermission(CLBManager.getManager().getConfig().getString("Wands.Dragon-breath.permission"))))
      {
        player.sendMessage(color(LangLoader.LangCfg.getString("need-permission")));
        return;
      }
      if ((stack.hasItemMeta()) && (stack.getItemMeta().getDisplayName().equals(dragonName))) {
        if ((this.dragoncooldown.containsKey(player.getUniqueId())) && (((Long)this.dragoncooldown.get(player.getUniqueId())).longValue() > System.currentTimeMillis()))
        {
          dragon.setCancelled(true);
          long remainingTime = ((Long)this.dragoncooldown.get(player.getUniqueId())).longValue() - System.currentTimeMillis();
          String cmsg = color(LangLoader.LangCfg.getString("Cooldown-message").replace("%time%", String.valueOf(remainingTime / 1000L)));
          player.sendMessage(cmsg);
        }
        else
        {
          this.dragoncooldown.put(player.getUniqueId(), Long.valueOf(System.currentTimeMillis() + dw * 1000));
          Vector vector = player.getLocation().getDirection();
          Vector vector2 = vector.multiply(3.0D);
          FallingBlock fallingBlock1 = dragon.getPlayer().getWorld().spawnFallingBlock(dragon.getPlayer().getLocation().add(2.0D, 1.0D, 2.0D), Material.FIRE, (byte)0);
          fallingBlock1.setVelocity(vector2);
          FallingBlock fallingBlock2 = dragon.getPlayer().getWorld().spawnFallingBlock(dragon.getPlayer().getLocation().add(2.0D, 1.0D, 3.0D), Material.FIRE, (byte)0);
          fallingBlock2.setVelocity(vector2);
          FallingBlock fallingBlock3 = dragon.getPlayer().getWorld().spawnFallingBlock(dragon.getPlayer().getLocation().add(2.0D, 1.0D, 4.0D), Material.FIRE, (byte)0);
          fallingBlock3.setVelocity(vector2);
          FallingBlock fallingBlock4 = dragon.getPlayer().getWorld().spawnFallingBlock(dragon.getPlayer().getLocation().add(2.0D, 1.0D, 5.0D), Material.FIRE, (byte)0);
          fallingBlock4.setVelocity(vector2);
          FallingBlock fallingBlock5 = dragon.getPlayer().getWorld().spawnFallingBlock(dragon.getPlayer().getLocation().add(3.0D, 1.0D, 2.0D), Material.FIRE, (byte)0);
          fallingBlock5.setVelocity(vector2);
          FallingBlock fallingBlock6 = dragon.getPlayer().getWorld().spawnFallingBlock(dragon.getPlayer().getLocation().add(3.0D, 1.0D, 3.0D), Material.FIRE, (byte)0);
          fallingBlock6.setVelocity(vector2);
          FallingBlock fallingBlock7 = dragon.getPlayer().getWorld().spawnFallingBlock(dragon.getPlayer().getLocation().add(3.0D, 1.0D, 4.0D), Material.FIRE, (byte)0);
          fallingBlock7.setVelocity(vector2);
          FallingBlock fallingBlock8 = dragon.getPlayer().getWorld().spawnFallingBlock(dragon.getPlayer().getLocation().add(3.0D, 1.0D, 5.0D), Material.FIRE, (byte)0);
          fallingBlock8.setVelocity(vector2);
          FallingBlock fallingBlock9 = dragon.getPlayer().getWorld().spawnFallingBlock(dragon.getPlayer().getLocation().add(4.0D, 1.0D, 2.0D), Material.FIRE, (byte)0);
          fallingBlock9.setVelocity(vector2);
          FallingBlock fallingBlock10 = dragon.getPlayer().getWorld().spawnFallingBlock(dragon.getPlayer().getLocation().add(4.0D, 1.0D, 3.0D), Material.FIRE, (byte)0);
          fallingBlock10.setVelocity(vector2);
          FallingBlock fallingBlock11 = dragon.getPlayer().getWorld().spawnFallingBlock(dragon.getPlayer().getLocation().add(4.0D, 1.0D, 4.0D), Material.FIRE, (byte)0);
          fallingBlock11.setVelocity(vector2);
          FallingBlock fallingBlock12 = dragon.getPlayer().getWorld().spawnFallingBlock(dragon.getPlayer().getLocation().add(4.0D, 1.0D, 5.0D), Material.FIRE, (byte)0);
          fallingBlock12.setVelocity(vector2);
          FallingBlock fallingBlock13 = dragon.getPlayer().getWorld().spawnFallingBlock(dragon.getPlayer().getLocation().add(5.0D, 1.0D, 2.0D), Material.FIRE, (byte)0);
          fallingBlock13.setVelocity(vector2);
          FallingBlock fallingBlock14 = dragon.getPlayer().getWorld().spawnFallingBlock(dragon.getPlayer().getLocation().add(5.0D, 1.0D, 3.0D), Material.FIRE, (byte)0);
          fallingBlock14.setVelocity(vector2);
          FallingBlock fallingBlock15 = dragon.getPlayer().getWorld().spawnFallingBlock(dragon.getPlayer().getLocation().add(5.0D, 1.0D, 4.0D), Material.FIRE, (byte)0);
          fallingBlock15.setVelocity(vector2);
          FallingBlock fallingBlock16 = dragon.getPlayer().getWorld().spawnFallingBlock(dragon.getPlayer().getLocation().add(5.0D, 1.0D, 5.0D), Material.FIRE, (byte)0);
          fallingBlock16.setVelocity(vector2);
        }
      }
    }
  }
}
