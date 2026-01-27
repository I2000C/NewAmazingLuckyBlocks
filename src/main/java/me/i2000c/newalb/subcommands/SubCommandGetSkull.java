package me.i2000c.newalb.subcommands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.textures.Texture;

public class SubCommandGetSkull implements SubCommand {
    
    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if(!checkHasPermission(sender, "Commands.GetSkull-permission")) {
            return false;
        }
        
        if(!checkNotConsole(sender)) {
            return false;
        }
        
        if(args.size() != 1){
            Logger.sendMessage("&cUsage: &7/alb getSkull <textureID>", sender);
            return false;
        }
        
        Player player = (Player) sender;
        Texture texture = Texture.of(args.get(0));
        ItemStack textureItem = ItemStackWrapper.fromItem(texture.createItem(), false)
                                                .setDisplayName("Custom head")
                                                .toItemStack();
        player.getInventory().addItem(textureItem);
        return true;
    }
}
