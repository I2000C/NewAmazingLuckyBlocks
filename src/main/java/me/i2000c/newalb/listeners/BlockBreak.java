package me.i2000c.newalb.listeners;

import java.util.Random;
import me.i2000c.newalb.custom_outcomes.menus.FinishMenu;
import me.i2000c.newalb.custom_outcomes.utils.Executable;
import me.i2000c.newalb.custom_outcomes.utils.TypeManager;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.lang_utils.LangLoader;
import me.i2000c.newalb.utils.logger.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.i2000c.newalb.utils.WorldList;
import org.bukkit.GameMode;
import org.bukkit.event.EventPriority;

public class BlockBreak implements Listener{
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e){
        Block b = e.getBlock();
        Location loc = b.getLocation().add(0.5, 0, 0.5);
        Player p = e.getPlayer();
        World w = p.getWorld();
        
        Executable exec = FinishMenu.testRewardsPlayerList.get(p);
        if(exec != null){
            b.setType(Material.AIR);
            exec.execute(p, loc);
            return;
        }
        
        if(WorldList.isRegistered(w.getName())){
            TypeManager.Result result = TypeManager.canBreakBlock(p, loc);
            switch(result.resultCode){
                case TypeManager.RESULT_NOT_LUCKYBLOCK:
                    dropLuckyBlock(e);
                    break;
                case TypeManager.RESULT_NO_GLOBAL_PERMISSION:
                case TypeManager.RESULT_NO_LOCAL_PERMISSION:
                    Logger.sendMessage(LangLoader.getMessages().getString("NoPermission"), p);
                    e.setCancelled(true);
                    break;
                case TypeManager.RESULT_OK:
                    boolean requireLuckyTool = ConfigManager.getConfig().getBoolean("LuckyTool.enable");
                    boolean canOnlyBreakWithLuckyTool = ConfigManager.getConfig().getBoolean("LuckyTool.onlyCanBreakLuckyBlocksWithLuckyTool");
                    if(requireLuckyTool && canOnlyBreakWithLuckyTool){
                        Logger.sendMessage(LangLoader.getMessages().getString("Objects.LuckyTool.need"), p);
                        e.setCancelled(true);
                    }else{
                        b.setType(Material.AIR);
                        result.resultType.executeRandomPack(p, loc);
                    }
                    break;
            }
        }
    }
    
    private void dropLuckyBlock(BlockBreakEvent e){
        boolean drop = ConfigManager.getConfig().getBoolean("LuckyBlock.DropOnBlockBreak.enable");
        boolean survivalOnly = ConfigManager.getConfig().getBoolean("LuckyBlock.DropOnBlockBreak.survivalOnly");
        if(drop && (!survivalOnly || e.getPlayer().getGameMode() == GameMode.SURVIVAL)){
            int prob = ConfigManager.getConfig().getInt("LuckyBlock.DropOnBlockBreak.probability");
            Random r = new Random();
            if(r.nextInt(100) < prob){
                e.setCancelled(true);
                e.getBlock().setType(Material.AIR);
                e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation().add(0.5, 0, 0.5), TypeManager.getRandomLuckyBlockType().getItem());
            }
        }
    }
}
