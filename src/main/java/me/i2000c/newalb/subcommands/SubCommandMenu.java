package me.i2000c.newalb.subcommands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.i2000c.newalb.api.gui.MenuManager;
import me.i2000c.newalb.api.gui.menus.Menu;
import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.lucky_blocks.editors.menus.MainMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.RewardListMenu;
import me.i2000c.newalb.lucky_blocks.rewards.PackManager;
import me.i2000c.newalb.utils.logging.Logger;

public class SubCommandMenu implements SubCommand {
    
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
        Menu lastMenu = MenuManager.getLastMenu(player);
        boolean confirmation = MenuManager.getConfirmation(player);
        if(lastMenu != null && !confirmation && ConfigManager.getMainConfig().getBoolean("Enable-openMenu-confirmation")) {
            Logger.sendMessage(ConfigManager.getLangMessage("MenuConfirmation.line1"), sender);
            Logger.sendMessage(ConfigManager.getLangMessage("MenuConfirmation.line2"), sender, false);
            Logger.sendMessage(ConfigManager.getLangMessage("MenuConfirmation.line3"), sender, false);
            MenuManager.setConfirmation(player, true);
            return false;
        }
        
        MenuManager.setConfirmation(player, false);
        RewardListMenu.testRewardsPlayerList.remove(player);
        ChatListener.removePlayer(player);
                
        // Open main menu
        MainMenu menu = new MainMenu();
        menu.setOnBack(p -> {
            MenuManager.removeLastMenu(p);
            p.closeInventory();
        });
        menu.openToPlayer(player);
        
        return true;
    }
}
