package me.i2000c.newalb.utils2;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.Logger;
import org.bukkit.entity.Player;

public class ActionBarUtils{
    private static Constructor chatComponentTextConstructor;
    private static Constructor packetPlayOutChatConstructor;    
    private static Method getHandle;
    private static Field playerConnection;    
    private static Method sendPacket;
    private static boolean initialized = false;
    
    public static void sendMessage(String message, Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        message = Logger.color(message);
        if(NewAmazingLuckyBlocks.getMinecraftVersion() == MinecraftVersion.v1_8){
            try{
                if(!initialized){
                    Class chatBaseComponentClass = OtherUtils.getNMSClass(null, "IChatBaseComponent");
                    Class chatComponentText = OtherUtils.getNMSClass(null, "ChatComponentText");
                    chatComponentTextConstructor = chatComponentText.getConstructor(String.class);
                    
                    Class packetPlayOutChatClass = OtherUtils.getNMSClass(null, "PacketPlayOutChat");
                    packetPlayOutChatConstructor = packetPlayOutChatClass.getConstructor(chatBaseComponentClass, byte.class);
                    
                    getHandle = OtherUtils.getCraftClass("entity.CraftPlayer").getMethod("getHandle");
                    playerConnection = OtherUtils.getNMSClass(null, "EntityPlayer").getField("playerConnection");
                    
                    Class packetClass = OtherUtils.getNMSClass(null, "Packet");
                    sendPacket = OtherUtils.getNMSClass(null, "PlayerConnection").getMethod("sendPacket", packetClass);
                    
                    initialized = true;
                }
                
                Object component = chatComponentTextConstructor.newInstance(Logger.color(message));
                Object packet = packetPlayOutChatConstructor.newInstance(component, (byte) 2);
                Object nmsPlayer = getHandle.invoke(player);
                sendPacket.invoke(playerConnection.get(nmsPlayer), packet);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }else{
            //https://www.spigotmc.org/threads/tutorial-send-actionbar-messages-without-nms.257845/
            net.md_5.bungee.api.ChatMessageType type = net.md_5.bungee.api.ChatMessageType.ACTION_BAR;
            net.md_5.bungee.api.chat.BaseComponent[] component = net.md_5.bungee.api.chat.TextComponent.fromLegacyText(message);
            player.spigot().sendMessage(type, component);
        }
//</editor-fold>
    }
}
