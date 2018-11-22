package net.servermc.plugins.Listeners.Wands;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.servermc.plugins.AmazingLuckyBlocks;
import net.servermc.plugins.utils.CLBManager;
import net.servermc.plugins.utils.LangLoader;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.servermc.plugins.utils.WorldList;

public class regenWand
  implements Listener
{
  private ItemStack displayItem;
  private int GametaskID;
  public static HashMap<UUID, Long> regencooldown = new HashMap();
  
  public String color(String str)
  {
    return ChatColor.translateAlternateColorCodes('&', str);
  }
  
  @EventHandler
  public void regenInteraction(PlayerInteractEvent regen)
  {
    Player player = regen.getPlayer();
    Action action = regen.getAction();
    String regenName = color(LangLoader.LangCfg.getString("Wands.Regen.name"));
    int rw = CLBManager.getManager().getConfig().getInt("Wands.Regen.cooldown-time");
    ItemStack stack = player.getItemInHand();
    if (((action.equals(Action.RIGHT_CLICK_AIR)) || (action.equals(Action.RIGHT_CLICK_BLOCK))) && 
      (player.getItemInHand().getType() == Material.RECORD_8))
    {
      if (!WorldList.instance.worlds.contains(player.getWorld().getName())) {
        return;
      }
      if ((CLBManager.getManager().getConfig().getBoolean("Wands.Regen.required-permission")) && (!player.hasPermission(CLBManager.getManager().getConfig().getString("Wands.Regen.permission"))))
      {
        player.sendMessage(color(LangLoader.LangCfg.getString("need-permission")));
        return;
      }
      if ((stack.hasItemMeta()) && (stack.getItemMeta().getDisplayName().equals(regenName))) {
        if ((regencooldown.containsKey(player.getUniqueId())) && (((Long)regencooldown.get(player.getUniqueId())).longValue() > System.currentTimeMillis()))
        {
          regen.setCancelled(true);
          long remainingTime = ((Long)regencooldown.get(player.getUniqueId())).longValue() - System.currentTimeMillis();
          String cmsg = color(LangLoader.LangCfg.getString("Cooldown-message").replace("%time%", String.valueOf(remainingTime / 1000L)));
          player.sendMessage(cmsg);
        }
        else
        {
          regencooldown.put(player.getUniqueId(), Long.valueOf(System.currentTimeMillis() + rw * 1000));
          player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 220, 0));
        }
      }
    }
  }
}
