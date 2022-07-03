package me.i2000c.newalb.utils2;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import me.i2000c.newalb.utils.Logger;
import org.bukkit.entity.Player;

class ActionBar1_8{
    private static Constructor chatComponentTextConstructor;
    private static Constructor packetPlayOutChatConstructor;
    
    private static Method getHandle;
    private static Field playerConnection;
    
    private static Method sendPacket;
    
    static{
        //<editor-fold defaultstate="collapsed" desc="Code">
        try{
            Class chatBaseComponentClass = OtherUtils.getNMSClass(null, "IChatBaseComponent");
            Class chatComponentText = OtherUtils.getNMSClass(null, "ChatComponentText");
            chatComponentTextConstructor = chatComponentText.getConstructor(String.class);
            
            Class packetPlayOutChatClass = OtherUtils.getNMSClass(null, "PacketPlayOutChat");
            packetPlayOutChatConstructor = packetPlayOutChatClass.getConstructor(chatBaseComponentClass, byte.class);
            
            getHandle = OtherUtils.getCraftClass("entity.CraftPlayer").getMethod("getHandle");
            playerConnection = OtherUtils.getNMSClass(null, "EntityPlayer").getField("playerConnection");
            
            Class packetClass = OtherUtils.getNMSClass(null, "Packet");
            sendPacket = OtherUtils.getNMSClass(null, "PlayerConnection").getMethod("sendPacket", packetClass);
        }catch(Exception ex){
            ex.printStackTrace();
        }
//</editor-fold>
    }
    
    public static void sendMessage(Player p, String message){
        try{
            Object component = chatComponentTextConstructor.newInstance(Logger.color(message));
            Object packet = packetPlayOutChatConstructor.newInstance(component, (byte) 2);            
            Object nmsPlayer = getHandle.invoke(p);            
            sendPacket.invoke(playerConnection.get(nmsPlayer), packet);            
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
