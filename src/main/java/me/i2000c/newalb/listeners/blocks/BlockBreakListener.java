package me.i2000c.newalb.listeners.blocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.integration.WorldGuardManager;
import me.i2000c.newalb.lucky_blocks.LuckyBlockDropper;
import me.i2000c.newalb.lucky_blocks.editors.menus.RewardListMenu;
import me.i2000c.newalb.lucky_blocks.rewards.Executable;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.OutcomePack;
import me.i2000c.newalb.lucky_blocks.rewards.PackManager;
import me.i2000c.newalb.lucky_blocks.rewards.TypeManager;
import me.i2000c.newalb.utils.locations.LocationManager;
import me.i2000c.newalb.utils.locations.WorldManager;
import me.i2000c.newalb.utils.logging.Logger;

public class BlockBreakListener implements Listener {
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e){
        Block b = e.getBlock();
        Location loc = b.getLocation().add(0.5, 0, 0.5);
        Player p = e.getPlayer();
        World w = p.getWorld();
        
        if(!WorldManager.isEnabled(w.getName())) {
            return;
        }
        
        if(!WorldGuardManager.canBreak(p, loc)) {
            return;
        }
        
        Executable exec = RewardListMenu.testRewardsPlayerList.get(p);
        if(exec != null){
        	LocationManager.removeLocation(loc);
            b.setType(Material.AIR);
            exec.execute(p, loc);
            return;
        }
        
        TypeManager.Result result = TypeManager.canBreakBlock(p, loc);
        switch(result.resultCode){
            case TypeManager.RESULT_NOT_LUCKYBLOCK:
                LuckyBlockDropper.dropLuckyBlock(e);
                break;
            case TypeManager.RESULT_NO_GLOBAL_PERMISSION:
            case TypeManager.RESULT_NO_LOCAL_PERMISSION:
                Logger.sendMessage(ConfigManager.getLangMessage("NoPermission"), p);
                e.setCancelled(true);
                break;
            case TypeManager.RESULT_OK:
                if(PackManager.IS_LOADING_PACKS()) {
                    Logger.sendMessage(ConfigManager.getLangMessage("Loading.not-fully-loaded"), p);
                    e.setCancelled(true);
                    break;
                }
                
                boolean requireLuckyTool = ConfigManager.getMainConfig().getBoolean("Objects.LuckyTool.enable");
                boolean canOnlyBreakWithLuckyTool = ConfigManager.getMainConfig().getBoolean("Objects.LuckyTool.onlyCanBreakLuckyBlocksWithLuckyTool");
                if(requireLuckyTool && canOnlyBreakWithLuckyTool){
                    Logger.sendMessage(ConfigManager.getLangMessage("Objects.LuckyTool.need"), p);
                    e.setCancelled(true);
                }else{
                    b.setType(Material.AIR);
                    
                    boolean debugMode = ConfigManager.getMainConfig().getBoolean("LuckyBlock.DebugMode");
                    if(debugMode) {
                        OutcomePack randomPack = result.resultType.getRandomPack();
                        Outcome randomOutcome = randomPack.getRandomOutcome();
                        
                        String playerName = p.getName();
                        Location pLoc = p.getLocation();
                        String pWorldName = pLoc.getWorld().getName();
                        int x = pLoc.getBlockX();
                        int y = pLoc.getBlockY();
                        int z = pLoc.getBlockZ();
                        Location bLoc = b.getLocation();
                        String bWorldName = bLoc.getWorld().getName();
                        int bx = bLoc.getBlockX();
                        int by = bLoc.getBlockY();
                        int bz = bLoc.getBlockZ();
                        String typeName = result.resultType.getTypeName();
                        String outcomePackName = randomPack.getFilename();
                        int outcomeID = randomOutcome.getID();
                        String outcomeName = randomOutcome.getName();
                        
                        String message = String.format("Player %s ('%s' %d %d %d) broke a LuckyBlock at ('%s' %d %d %d) of type '%s'. Selected outcome pack: %s, selected outcome ID: %d, selected outcome name: %s",
                                                        playerName, pWorldName, x, y, z, bWorldName, bx, by, bz, typeName, outcomePackName, outcomeID, outcomeName);
                        Logger.log(message);
                        
                        randomOutcome.execute(p, loc);
                    } else {
                        result.resultType.execute(p, loc);
                    }
                }
                break;
        }
    }
}
