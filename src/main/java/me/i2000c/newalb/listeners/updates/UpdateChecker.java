package me.i2000c.newalb.listeners.updates;

import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import lombok.Cleanup;
import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.tasks.Task;


public class UpdateChecker implements Listener {
    private static final int RESOURCE_ID = 62644;
    private static String latestVersion = null;
    private static boolean updateDetected = false;
    
    public static void checkUpdates(String currentVersion) {
        if(!ConfigManager.getMainConfig().getBoolean("Update-checker")) {
            return;
        }
        
        Logger.log("&6Checking latest version...");
        Task.runTaskAsynchronously(() -> {
            try {
                URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + RESOURCE_ID);
                @Cleanup InputStream input = url.openStream();
                @Cleanup Scanner scanner = new Scanner(input);
                if(scanner.hasNext()) {
                    latestVersion = scanner.next();
                    if(currentVersion.equals(latestVersion)) {
                        Logger.log("&aYou are using the latest version");
                        updateDetected = false;
                        return;
                    }
                    
                    String[] versionSplit = currentVersion.split("_");
                    String[] latestVersionSplit = latestVersion.split("_");
                    int versionNumber = Integer.parseInt(versionSplit[versionSplit.length - 1]);
                    int latestVersionNumber = Integer.parseInt(latestVersionSplit[latestVersionSplit.length - 1]);
                    if(latestVersionNumber > versionNumber) {
                        Logger.log("&cThere is a new version available: &e(&7" + latestVersion + "&e)");
                        Logger.log("&cYou can download it at: &fhttps://www.spigotmc.org/resources/" + RESOURCE_ID);
                        updateDetected = true;
                    } else {
                        Logger.log("&aYou are using the latest version");
                        updateDetected = false;
                    }
                }
            } catch(Exception ex) {
                Logger.err("&cAn error occurred while checking update:");
                Logger.err(ex);
            }
        });
    }
    
    @EventHandler
    private static void onEnter(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(updateDetected && player.hasPermission(ConfigManager.getMainConfig().getString("Commands.Update-message-permission"))) {
            Logger.sendMessage("&a========================================", player);
            Logger.sendMessage("&6New Amazing Lucky Blocks", player);
            Logger.sendMessage("&cThere is a new version available: &e(&7" + latestVersion + "&e)", player);
            Logger.sendMessage("&cYou can download it at:&f https://www.spigotmc.org/resources/" + RESOURCE_ID, player);
            Logger.sendMessage("&a========================================", player);
        }
    }
}