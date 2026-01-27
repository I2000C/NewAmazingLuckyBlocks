package me.i2000c.newalb.subcommands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.lucky_blocks.editors.Editor;
import me.i2000c.newalb.lucky_blocks.editors.EditorType;
import me.i2000c.newalb.lucky_blocks.editors.menus.GUIManager;
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
        if(GUIManager.isConfirmMenu() && ConfigManager.getMainConfig().getBoolean("Enable-openMenu-confirmation")) {
            Logger.sendMessage(ConfigManager.getLangMessage("MenuConfirmation.line1"), sender);
            Logger.sendMessage(ConfigManager.getLangMessage("MenuConfirmation.line2"), sender, false);
            Logger.sendMessage(ConfigManager.getLangMessage("MenuConfirmation.line3"), sender, false);
            GUIManager.setConfirmMenu(false);
            return false;
        }
        
        GUIManager.setConfirmMenu(true);
        RewardListMenu.testRewardsPlayerList.remove(player);
        ChatListener.removePlayer(player);
                
        // Open main menu
        Editor editor = EditorType.MAIN_MENU.getEditor();
        editor.createNewItem(player, p -> {
            GUIManager.setCurrentMenu(null);
            p.closeInventory();
        }, null);
        
        return true;
    }
}
