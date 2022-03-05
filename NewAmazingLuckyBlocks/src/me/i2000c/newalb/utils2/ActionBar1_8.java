package me.i2000c.newalb.utils2;

import me.i2000c.newalb.utils.Logger;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

class ActionBar1_8{
    public static void sendMessage(Player p, String message){
        //https://www.spigotmc.org/threads/how-to-send-action-bar-in-spigot-1-7-x-1-8-x.93800/#post-1029740
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(Logger.color(message)), (byte)2);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }
    
    //Other way to do the same without using NMS:
    /*public static void sendMessage(Player p, String message){
        try{
            Class<?> chatBaseComponent = getNMSClass("IChatBaseComponent");
            Class<?> chatComponentText = getNMSClass("ChatComponentText");
            Constructor<?> chatComponentTextConstructor = chatComponentText.getConstructor(String.class);
            Class<?> packetPlayOutChat = getNMSClass("PacketPlayOutChat");
            Constructor<?> packetPlayOutChatConstructor = packetPlayOutChat.getConstructor(chatBaseComponent, byte.class);
            
            Object packet = packetPlayOutChatConstructor.newInstance(chatComponentTextConstructor.newInstance(color(message)), (byte) 2);
            
            Method getHandle = getCraftClass("entity.CraftPlayer").getMethod("getHandle");
            Object player = getHandle.invoke(p);
            Field playerConnection = getNMSClass("EntityPlayer").getField("playerConnection");
            
            Class<?> Packet = getNMSClass("Packet");
            
            Method sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", Packet);
            sendPacket.invoke(playerConnection.get(player), packet);
        }catch(Exception ex){
            ex.printStackTrace();
        }        
    }
    
    private static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try{
            return Class.forName("net.minecraft.server." + version + "." + name);
        }catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }    
    private static Class<?> getCraftClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try{
            return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
        }catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }*/
}
