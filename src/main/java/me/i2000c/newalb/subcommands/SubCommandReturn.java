package me.i2000c.newalb.subcommands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.i2000c.newalb.api.gui.Menu;
import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.lucky_blocks.editors.menus.GUIManager;
import me.i2000c.newalb.lucky_blocks.editors.menus.RewardListMenu;
import me.i2000c.newalb.lucky_blocks.rewards.PackManager;
import me.i2000c.newalb.utils.logging.Logger;

public class SubCommandReturn implements SubCommand {
    
    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if(!checkHasPermission(sender, "Commands.Menu-permission")) {
            return false;
        }
        
        if(!checkNotConsole(sender)) {
            return false;
        }
        
        if(PackManager.IS_LOADING_PACKS()) {
            Logger.sendMessage(ConfigManager.getLangMessage("Loading.not-fully-loaded"), sender);
            return false;
        }
        
        Player player = (Player) sender;
        RewardListMenu.testRewardsPlayerList.remove(player);
        ChatListener.removePlayer(player);
                
        // Open previous menu
        Menu currentMenu = GUIManager.getCurrentMenu();
        if(currentMenu == null) {
            Logger.sendMessage("&cYou haven't opened any menu recently", sender);
            return false;
        } else {
            GUIManager.setConfirmMenu(true);
            currentMenu.openToPlayer(player, false);
            return true;
        }
    }
}
