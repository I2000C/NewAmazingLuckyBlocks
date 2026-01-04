package me.i2000c.newalb.utils.random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;
import me.i2000c.newalb.config.ConfigManager;

@Value
@Builder(builderClassName = "Builder")
public class RandomBlocksOptions {
    @Default private int radx = 5;
    @Default private int rady = 5;
    @Default private int radz = 5;
    @Default private int blocks = 5;
    @Default private boolean allowFloatingBlocks = false;
    @Default private boolean preScanSafeLocations = false;
    @Default private boolean avoidWater = ConfigManager.getMainConfig().getBoolean("GenerateRandomblocks-OnChunkCreated.avoid-water");;
    @Default private boolean sendFinishMessage = false;
    @Default private boolean showPercentage = ConfigManager.getMainConfig().getBoolean("Enable-randomBlocks-percentage");
    @Default private int packetSize = ConfigManager.getMainConfig().getInt("RandomBlocks-PacketSize");
    @Default private Location location = null;
    @Default private CommandSender senderToNotify = null;
    @Default private boolean extraTask = false;
    
    public int getPacketSize() {
        return packetSize > 1 ? packetSize : 1;
    }
    
    public CommandSender getSenderToNotify() {
        CommandSender sender = senderToNotify;
        
        if(sender == null) {
            sender = Bukkit.getConsoleSender();
        } else if(sender instanceof Player) {
            Player player = (Player) sender;
            if(!player.isOnline()) {
                sender = Bukkit.getConsoleSender();
            }
        }
        
        return sender;
    }
}
