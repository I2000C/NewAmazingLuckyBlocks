package net.servermc.plugins.Listeners;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.servermc.plugins.AmazingLuckyBlocks;
import net.servermc.plugins.utils.CLBManager;
import net.servermc.plugins.utils.LangLoader;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import net.servermc.plugins.utils.WorldList;

public class BlockBreak
  implements Listener
{
  public String color(String str)
  {
    return ChatColor.translateAlternateColorCodes('&', str);
  }

  @EventHandler
  public void onBreak(BlockBreakEvent e)
  {
    Block b = e.getBlock();
    Location loc = b.getLocation();
    Player p = e.getPlayer();
    World w = p.getWorld();
    if (b.getType() == Material.valueOf(CLBManager.getManager().getConfig().getString("LuckyBlock")))
    {
      if (!WorldList.instance.worlds.contains(p.getWorld().getName())) {
        return;
      }
      b.setType(Material.AIR);
      int r = new Random().nextInt(36);
      Entity blaze;
      Entity c9;
      switch (r)
      {
      case 0: 
        ItemStack stack1 = new ItemStack(Material.DIAMOND_SWORD);
        Firework f = (Firework)p.getWorld().spawnEntity(b.getLocation(), EntityType.FIREWORK);
        FireworkMeta fm = f.getFireworkMeta();
        fm.addEffect(FireworkEffect.builder().flicker(true).trail(true).with(FireworkEffect.Type.BALL).withColor(Color.RED).withFade(Color.RED).build());
        
        fm.setPower(2);
        f.setFireworkMeta(fm);
        
        Firework f2 = (Firework)p.getWorld().spawnEntity(b.getLocation(), EntityType.FIREWORK);
        FireworkMeta fm2 = f2.getFireworkMeta();
        fm.addEffect(FireworkEffect.builder().flicker(true).trail(true).with(FireworkEffect.Type.BALL).withColor(Color.RED).withFade(Color.RED).build());
        
        fm2.setPower(2);
        f2.setFireworkMeta(fm2);
        
        Firework f3 = (Firework)p.getWorld().spawnEntity(b.getLocation(), EntityType.FIREWORK);
        FireworkMeta fm3 = f3.getFireworkMeta();
        fm.addEffect(FireworkEffect.builder().flicker(true).trail(true).with(FireworkEffect.Type.BALL).withColor(Color.RED).withFade(Color.RED).build());
        
        fm3.setPower(2);
        f3.setFireworkMeta(fm3);
        
        loc.getWorld().dropItemNaturally(b.getLocation(), stack1);
        break;
      case 1: 
        ItemStack stack2 = new ItemStack(Material.EMERALD, 32);
        Entity v1 = b.getWorld().spawnEntity(b.getLocation(), EntityType.VILLAGER);
        Entity v2 = v1.getWorld().spawnEntity(v1.getLocation(), EntityType.VILLAGER);
        Entity v3 = v2.getWorld().spawnEntity(v2.getLocation(), EntityType.VILLAGER);
        Entity v4 = v3.getWorld().spawnEntity(v3.getLocation(), EntityType.VILLAGER);
        Entity v5 = v4.getWorld().spawnEntity(v4.getLocation(), EntityType.VILLAGER);
        
        v1.setPassenger(v2);
        v2.setPassenger(v3);
        v3.setPassenger(v4);
        v4.setPassenger(v5);
        loc.getWorld().dropItemNaturally(b.getLocation(), stack2);
        break;
      case 2: 
        ItemStack stack3 = new ItemStack(Material.STONE_PICKAXE);
        ItemStack stack4 = new ItemStack(Material.STONE_AXE);
        ItemStack stack5 = new ItemStack(Material.STONE_SPADE);
        
        loc.getWorld().dropItemNaturally(b.getLocation(), stack3);
        loc.getWorld().dropItemNaturally(b.getLocation(), stack4);
        loc.getWorld().dropItemNaturally(b.getLocation(), stack5);
        break;
      case 3: 
        ItemStack stack6 = new ItemStack(Material.RECORD_8);
        ItemMeta meta2 = stack6.getItemMeta();
        meta2.setDisplayName(color(LangLoader.LangCfg.getString("Wands.Regen.name")));
        stack6.setItemMeta(meta2);
        
        loc.getWorld().dropItemNaturally(b.getLocation(), stack6);
        break;
      case 4: 
        Block lucky = w.getBlockAt(b.getLocation().add(1.0D, 0.0D, 0.0D));
        lucky.setType(Material.SPONGE);
        break;
      case 5: 
        ItemStack stack7 = new ItemStack(Material.RECORD_7);
        ItemMeta meta3 = stack7.getItemMeta();
        meta3.setDisplayName(color(LangLoader.LangCfg.getString("Wands.Invisibility.name")));
        stack7.setItemMeta(meta3);
        
        loc.getWorld().dropItemNaturally(b.getLocation(), stack7);
        break;
      case 6: 
        ItemStack stack8 = new ItemStack(Material.IRON_PICKAXE);
        ItemStack stack9 = new ItemStack(Material.IRON_AXE);
        ItemStack stack10 = new ItemStack(Material.IRON_SPADE);
        
        loc.getWorld().dropItemNaturally(b.getLocation(), stack8);
        loc.getWorld().dropItemNaturally(b.getLocation(), stack9);
        loc.getWorld().dropItemNaturally(b.getLocation(), stack10);
        break;
      case 7: 
        Block water = w.getBlockAt(b.getLocation().add(1.0D, 0.0D, 0.0D));
        water.setType(Material.WATER);
        break;
      case 8: 
        Block lava = w.getBlockAt(b.getLocation().add(1.0D, 0.0D, 0.0D));
        lava.setType(Material.LAVA);
        break;
      case 9: 
        ItemStack stack11 = new ItemStack(Material.CARROT_ITEM, 2);
        
        loc.getWorld().dropItemNaturally(b.getLocation(), stack11);
        break;
      case 10: 
        blaze = b.getWorld().spawnEntity(b.getLocation(), EntityType.BLAZE);
        break;
      case 11: 
        Entity c1 = b.getWorld().spawnEntity(b.getLocation().add(1.0D, 0.0D, 0.0D), EntityType.CHICKEN);
        Entity c2 = b.getWorld().spawnEntity(b.getLocation().add(0.0D, 0.0D, 1.0D), EntityType.CHICKEN);
        Entity c3 = b.getWorld().spawnEntity(b.getLocation(), EntityType.CHICKEN);
        Entity c4 = b.getWorld().spawnEntity(b.getLocation().add(1.0D, 0.0D, 1.0D), EntityType.CHICKEN);
        Entity c5 = b.getWorld().spawnEntity(b.getLocation().add(1.0D, 0.0D, 1.0D), EntityType.CHICKEN);
        Entity c6 = b.getWorld().spawnEntity(b.getLocation().add(1.0D, 0.0D, 1.0D), EntityType.CHICKEN);
        Entity c7 = b.getWorld().spawnEntity(b.getLocation().add(1.0D, 0.0D, 1.0D), EntityType.CHICKEN);
        Entity c8 = b.getWorld().spawnEntity(b.getLocation().add(1.0D, 0.0D, 1.0D), EntityType.CHICKEN);
        c9 = b.getWorld().spawnEntity(b.getLocation().add(1.0D, 0.0D, 1.0D), EntityType.CHICKEN);
        break;
      case 12: 
        ItemStack stack12 = new ItemStack(Material.DIAMOND_PICKAXE);
        
        loc.getWorld().dropItemNaturally(b.getLocation(), stack12);
        break;
      case 13: 
        ItemStack stack13 = new ItemStack(Material.ENDER_PEARL, 2);
        
        loc.getWorld().dropItemNaturally(b.getLocation(), stack13);
        break;
      case 14: 
        ItemStack stack14 = new ItemStack(Material.SANDSTONE, 38);
        
        loc.getWorld().dropItemNaturally(b.getLocation(), stack14);
        break;
      case 15: 
        ItemStack stack15 = new ItemStack(Material.STONE, 30);
        
        loc.getWorld().dropItemNaturally(b.getLocation(), stack15);
        break;
      case 16: 
        ItemStack stack16 = new ItemStack(Material.BOW);
        ItemStack stack17 = new ItemStack(Material.ARROW, 12);
        
        loc.getWorld().dropItemNaturally(b.getLocation(), stack16);
        loc.getWorld().dropItemNaturally(b.getLocation(), stack17);
        break;
      case 17: 
        ItemStack stack18 = new ItemStack(Material.APPLE, 4);
        
        loc.getWorld().dropItemNaturally(b.getLocation(), stack18);
        break;
      case 18: 
        ItemStack stack19 = new ItemStack(Material.BREAD, 2);
        
        loc.getWorld().dropItemNaturally(b.getLocation(), stack19);
        break;
      case 19: 
        ItemStack stack20 = new ItemStack(Material.DIRT, 37);
        
        loc.getWorld().dropItemNaturally(b.getLocation(), stack20);
        break;
      case 20: 
        Zombie z1 = (Zombie)b.getWorld().spawn(b.getLocation(), Zombie.class);
        z1.getEquipment().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1));
        z1.getEquipment().setChestplateDropChance(1.0F);
        z1.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS, 1));
        z1.getEquipment().setLeggingsDropChance(1.0F);
        z1.getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS, 1));
        z1.getEquipment().setBootsDropChance(1.0F);
        break;
      case 21: 
        ItemStack stack21 = new ItemStack(Material.RECORD_3);
        ItemMeta meta4 = stack21.getItemMeta();
        meta4.setDisplayName(color(LangLoader.LangCfg.getString("Wands.TNT.name")));
        stack21.setItemMeta(meta4);
        
        loc.getWorld().dropItemNaturally(b.getLocation(), stack21);
        break;
      case 22: 
        ItemStack stack22 = new ItemStack(Material.RECORD_4);
        ItemMeta meta5 = stack22.getItemMeta();
        meta5.setDisplayName(color(LangLoader.LangCfg.getString("Wands.Slime.name")));
        stack22.setItemMeta(meta5);
        
        loc.getWorld().dropItemNaturally(b.getLocation(), stack22);
        break;
      case 23: 
        Zombie z2 = (Zombie)b.getWorld().spawn(b.getLocation(), Zombie.class);
        z2.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET, 1));
        z2.getEquipment().setHelmetDropChance(1.0F);
        z2.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE, 1));
        z2.getEquipment().setChestplateDropChance(1.0F);
        z2.getEquipment().getHelmet().setDurability((short)1);
        z2.getEquipment().getChestplate().setDurability((short)1);
        break;
      case 24: 
        ItemStack stack23 = new ItemStack(Material.RECORD_6);
        ItemMeta meta6 = stack23.getItemMeta();
        meta6.setDisplayName(color(LangLoader.LangCfg.getString("Wands.Dragon-breath.name")));
        stack23.setItemMeta(meta6);
        
        loc.getWorld().dropItemNaturally(b.getLocation(), stack23);
        break;
      case 25: 
        ItemStack stack24 = new ItemStack(Material.RECORD_5);
        ItemMeta meta7 = stack24.getItemMeta();
        meta7.setDisplayName(color(LangLoader.LangCfg.getString("Wands.Lightning.name")));
        stack24.setItemMeta(meta7);
        
        loc.getWorld().dropItemNaturally(b.getLocation(), stack24);
        break;
      case 26: 
        Zombie z3 = (Zombie)b.getWorld().spawn(b.getLocation(), Zombie.class);
        z3.getEquipment().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1));
        z3.getEquipment().setChestplateDropChance(1.0F);
        z3.getEquipment().getChestplate().setDurability((short)1);
        break;
      case 27: 
        Zombie z4 = (Zombie)b.getWorld().spawn(b.getLocation(), Zombie.class);
        z4.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS, 1));
        z4.getEquipment().setLeggingsDropChance(1.0F);
        z4.getEquipment().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET, 1));
        z4.getEquipment().setHelmetDropChance(1.0F);
        z4.getEquipment().getLeggings().setDurability((short)1);
        z4.getEquipment().getHelmet().setDurability((short)1);
        break;
      case 28: 
        int blockx = b.getLocation().getBlockX();
        int blocky = b.getLocation().getBlockY();
        int blockz = b.getLocation().getBlockZ();
        b.getWorld().spawnFallingBlock(new Location(b.getWorld(), blockx, blocky + 3, blockz), Material.IRON_BLOCK, (byte)0);
        b.getWorld().strikeLightning(b.getLocation());
        break;
      case 29: 
        ItemStack stack25 = new ItemStack(Material.SANDSTONE, 32);
        
        loc.getWorld().dropItemNaturally(b.getLocation(), stack25);
        break;
      case 30: 
        ItemStack stack26 = new ItemStack(Material.DIRT, 20);
        
        loc.getWorld().dropItemNaturally(b.getLocation(), stack26);
        break;
      case 31: 
        ItemStack stack27 = new ItemStack(Material.APPLE, 2);
        
        loc.getWorld().dropItemNaturally(b.getLocation(), stack27);
        break;
      case 32: 
        ItemStack stack28 = new ItemStack(Material.STONE_SWORD);
        ItemMeta im = stack28.getItemMeta();
        im.addEnchant(Enchantment.DAMAGE_ALL, 2, true);
        stack28.setItemMeta(im);
        
        p.getWorld().dropItemNaturally(b.getLocation(), stack28);
        break;
      case 33: 
        Block diamond_ore = w.getBlockAt(b.getLocation().add(1.0D, 0.0D, 0.0D));
        diamond_ore.setType(Material.DIAMOND_ORE);
        break;
      case 34:
        ItemStack stack29 = new ItemStack(Material.RECORD_9);
        ItemMeta meta8 = stack29.getItemMeta();
        meta8.setDisplayName(color(LangLoader.LangCfg.getString("Wands.Shield.name")));
        stack29.setItemMeta(meta8);
        
        loc.getWorld().dropItemNaturally(b.getLocation(), stack29);
        break;
      case 35:
        ItemStack stack30 = new ItemStack(Material.valueOf(CLBManager.getManager().getConfig().getString("Objects.DarkHole.block-material")));
        ItemMeta meta9 = stack30.getItemMeta();
        meta9.setDisplayName(color(LangLoader.LangCfg.getString("Objects.DarkHole.name")));
        stack30.setItemMeta(meta9);
              
        loc.getWorld().dropItemNaturally(b.getLocation(), stack30);      
          break;
      case 36:
        ItemStack stack31 = new ItemStack(Material.valueOf(CLBManager.getManager().getConfig().getString("Objects.MiniVolcano.block-material")));
        ItemMeta meta10 = stack31.getItemMeta();
        meta10.setDisplayName(color(LangLoader.LangCfg.getString("Objects.MiniVolcano.name")));
        stack31.setItemMeta(meta10);
              
        loc.getWorld().dropItemNaturally(b.getLocation(), stack31);      
          break;
      /*case 37:
      p.playSound(p.getLocation(), Sound.WITHER_IDLE, 100.0F, 1.0F);
      e.getBlock().getWorld().playEffect(e.getBlock().getLocation(), Effect.ENDER_SIGNAL, 100);
      
      
          int x = p.getLocation().getBlockX();
          int y = p.getLocation().getBlockY();
          int z = p.getLocation().getBlockZ();
          
          p.getWorld().getBlockAt(x + 1, y, z).setType(Material.FENCE);
          p.getWorld().getBlockAt(x, y, z - 1).setType(Material.FENCE);
          p.getWorld().getBlockAt(x, y, z + 1).setType(Material.FENCE);
          p.getWorld().getBlockAt(x - 1, y, z - 1).setType(Material.FENCE);
          p.getWorld().getBlockAt(x + 1, y, z + 1).setType(Material.FENCE);
          p.getWorld().getBlockAt(x - 1, y, z + 1).setType(Material.FENCE);
          p.getWorld().getBlockAt(x + 1, y, z - 1).setType(Material.FENCE);
          p.getWorld().getBlockAt(x - 1, y + 1, z).setType(Material.FENCE);
          p.getWorld().getBlockAt(x + 1, y + 1, z).setType(Material.FENCE);
          p.getWorld().getBlockAt(x, y + 1, z - 1).setType(Material.FENCE);
          p.getWorld().getBlockAt(x, y + 1, z + 1).setType(Material.FENCE);
          p.getWorld().getBlockAt(x - 1, y + 1, z - 1).setType(Material.FENCE);
          p.getWorld().getBlockAt(x + 1, y + 1, z + 1).setType(Material.FENCE);
          p.getWorld().getBlockAt(x - 1, y + 1, z + 1).setType(Material.FENCE);
          p.getWorld().getBlockAt(x + 1, y + 1, z - 1).setType(Material.FENCE);
          p.getWorld().getBlockAt(x + 1, y + 2, z - 1).setType(Material.FENCE);
          p.getWorld().getBlockAt(x + 1, y + 2, z - 1).setType(Material.FENCE);
          p.getWorld().getBlockAt(x, y - 1, z).setType(Material.GLASS);
          p.playSound(p.getLocation(), Sound.LEVEL_UP, 100.0F, 1.0F);
          
          p.getWorld().getBlockAt(x, y + 33, z).setType(Material.ANVIL);
          break;*/
        

      }
    }
  }
}
