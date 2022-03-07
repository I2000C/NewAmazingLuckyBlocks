package me.i2000c.newalb.utils2;

import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.Logger;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.entity.Player;

public class ActionBarUtils{
    public static void sendMessage(Player p, String message){
        if(NewAmazingLuckyBlocks.getMinecraftVersion() == MinecraftVersion.v1_8){
            ActionBar1_8.sendMessage(p, message);
        }else{
            //https://www.spigotmc.org/threads/tutorial-send-actionbar-messages-without-nms.257845/
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Logger.color(message)));
        }
    }
}