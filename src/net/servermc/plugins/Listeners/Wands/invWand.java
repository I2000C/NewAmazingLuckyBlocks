package net.servermc.plugins.Listeners.Wands;

import java.util.ArrayList;
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

public class invWand
  implements Listener
{
  private final HashMap<UUID, Long> invcooldown = new HashMap();
  
  public String color(String str)
  {
    return ChatColor.translateAlternateColorCodes('&', str);
  }
  
  @EventHandler
  public void invInteraction(PlayerInteractEvent inv)
  {
    Player player = inv.getPlayer();
    Action action = inv.getAction();
    String invName = color(LangLoader.LangCfg.getString("Wands.Invisibility.name"));
    int iw = CLBManager.getManager().getConfig().getInt("Wands.Invisibility.cooldown-time");
    int effectTime = CLBManager.getManager().getConfig().getInt("Wands.Invisibility.effect-time");
    effectTime *= 20; //effectTime = effectTime * 20
    if (((action.equals(Action.RIGHT_CLICK_AIR)) || (action.equals(Action.RIGHT_CLICK_BLOCK))) && 
      (player.getItemInHand().getType() == Material.valueOf("RECORD_7")))
    {
      ItemStack stack1 = player.getItemInHand();
      if (!WorldList.instance.worlds.contains(player.getWorld().getName())) {
        return;
      }
      if ((CLBManager.getManager().getConfig().getBoolean("Wands.Invisibility.required-permission")) && (!player.hasPermission(CLBManager.getManager().getConfig().getString("Wands.Invisibility.permission"))))
      {
        player.sendMessage(color(LangLoader.LangCfg.getString("need.permission")));
        return;
      }
      if ((stack1.hasItemMeta()) && (stack1.getItemMeta().getDisplayName().equals(invName))) {
        if ((this.invcooldown.containsKey(player.getUniqueId())) && (((Long)this.invcooldown.get(player.getUniqueId())).longValue() > System.currentTimeMillis()))
        {
          inv.setCancelled(true);
          long remainingTime = ((Long)this.invcooldown.get(player.getUniqueId())).longValue() - System.currentTimeMillis();
          String cmsg = color(LangLoader.LangCfg.getString("Cooldown-message").replace("%time%", String.valueOf(remainingTime / 1000L)));
          player.sendMessage(cmsg);
        }else{
            
            ItemStack stack2 = player.getItemInHand();
            ItemMeta meta2 = stack2.getItemMeta();
            if(CLBManager.getManager().getConfig().getBoolean("Wands.Invisibility.limited-uses.enable")){
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
                    int uses = CLBManager.getManager().getConfig().getInt("Wands.Invisibility.limited-uses.uses");
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
          
          this.invcooldown.put(player.getUniqueId(), Long.valueOf(System.currentTimeMillis() + iw * 1000));
          player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, effectTime, 0));
        }
      }
    }
  }
}
