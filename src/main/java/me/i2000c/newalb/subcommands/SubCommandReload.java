package me.i2000c.newalb.subcommands;

import java.util.List;

import org.bukkit.command.CommandSender;

import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.listeners.interact.SpecialItems;
import me.i2000c.newalb.lucky_blocks.LuckyBlockDropper;
import me.i2000c.newalb.lucky_blocks.TrapManager;
import me.i2000c.newalb.lucky_blocks.editors.menus.GUIManager;
import me.i2000c.newalb.lucky_blocks.editors.menus.RewardListMenu;
import me.i2000c.newalb.lucky_blocks.rewards.PackManager;
import me.i2000c.newalb.lucky_blocks.rewards.TypeManager;
import me.i2000c.newalb.utils.locations.WorldManager;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.random.RandomBlocks;
import me.i2000c.newalb.utils.tasks.Task;

public class SubCommandReload implements SubCommand {
    
    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if(!checkHasPermission(sender, "Commands.Reload-permission")){
            return false;
        }
        
        if(PackManager.IS_LOADING_PACKS()) {
            Logger.sendMessage(ConfigManager.getLangMessage("Loading.not-fully-loaded"), sender);
            return false;
        }
        
        RandomBlocks.forceStopAllRandomBlocksTasks();
        
        GUIManager.setCurrentMenu(null);
        RewardListMenu.testRewardsPlayerList.clear();
        
        Logger.logAndMessage(ConfigManager.getLangMessage("Reload.config"), sender);
        Logger.logAndMessage(ConfigManager.getLangMessage("Reload.lang"), sender);
        ConfigManager.loadConfigs();
        
        SpecialItems.loadItems();
        
        boolean coloredLogger = ConfigManager.getMainConfig().getBoolean("ColoredLogger");
        Logger.initializeLogger(NewAmazingLuckyBlocks.getInstance().prefix, coloredLogger);        
        NewAmazingLuckyBlocks.getInstance().prefix = ConfigManager.getLangMessage("InGamePrefix");
        
        Logger.logAndMessage(ConfigManager.getLangMessage("Reload.worlds"), sender);
        WorldManager.reloadWorlds();
        
        Logger.logAndMessage(ConfigManager.getLangMessage("Reload.packs"), sender);
        TypeManager.loadTypes();
        LuckyBlockDropper.loadSettings();
        PackManager.loadPacksAsync(() -> {
            try {
                TypeManager.loadPacksFromCachedPacksProbList();
                TrapManager.loadTraps();
            } catch(Throwable t) {
                throw t;
            } finally {
                PackManager.SET_LOADING_PACKS(false);
            }
            
            String message = ConfigManager.getLangMessage("Packs-loading")
                                          .replace("%packs%", PackManager.getPacks().size() + "");
            Task.runTask(() -> {
                Logger.logAndMessage(message, sender);
                Logger.logAndMessage(ConfigManager.getLangMessage("Reload.reload-finished"), sender);
            });
        });
        
        return true;
    }
}
