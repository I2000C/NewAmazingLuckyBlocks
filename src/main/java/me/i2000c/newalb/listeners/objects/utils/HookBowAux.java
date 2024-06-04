package me.i2000c.newalb.listeners.objects.utils;

import java.util.List;
import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.reflection.RefClass;
import me.i2000c.newalb.reflection.ReflectionManager;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.Task;

public class HookBowAux {
    
    public static Chicken createEntityChicken(Location loc) {
        if(MinecraftVersion.CURRENT_VERSION.is_1_8()) {
            return createEntityChicken_1_8(loc, true, true, false, true);
        } else {
            return createEntityChicken(loc, true, true, false, true);
        }
    }
    
    private static Chicken createEntityChicken(Location loc, 
                                                  boolean isInvisible, boolean isInvulnerable, 
                                                  boolean withAI, boolean isSilent) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Chicken entity;
        if(isInvisible){
            entity = loc.getWorld().spawn(loc.clone().add(0, 200, 0), Chicken.class);
            PotionEffect invisibility = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 255, false, false);
            entity.addPotionEffect(invisibility, true);
            Task.runTask(() -> entity.teleport(loc), 2L);
        }else{
            entity = loc.getWorld().spawn(loc, Chicken.class);
        }
        entity.setAI(withAI);
        entity.setSilent(isSilent);
        entity.setInvulnerable(isInvulnerable);
        return entity;
//</editor-fold>
    }    
    private static Chicken createEntityChicken_1_8(Location loc, 
                                                  boolean isInvisible, boolean isInvulnerable, 
                                                  boolean withAI, boolean isSilent) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Chicken entity;
        if(isInvisible){
            entity = loc.getWorld().spawn(loc.clone().add(0, 200, 0), Chicken.class);
            PotionEffect invisibility = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 255, false, false);
            entity.addPotionEffect(invisibility, true);
            Task.runTask(() -> entity.teleport(loc), 1L);
        }else{
            entity = loc.getWorld().spawn(loc, Chicken.class);
        }
        
        //https://wiki.vg/Protocol
        //https://www.spigotmc.org/threads/attach-lead-to-arrow.509913/
        //https://bukkit.org/threads/send-packets-to-a-player.117055/
        
        //https://www.spigotmc.org/threads/how-to-make-a-horse-invulnerable-unable-to-move-invisible-and-silent.500750/
        Object nmsEntity = ReflectionManager.callMethod(entity, "getHandle");
        Object nbtTagCompound = ReflectionManager.callMethod(nmsEntity, "getNBTTag");
        if(nbtTagCompound == null){
            nbtTagCompound = ReflectionManager.getCachedNMSClass(null, "NBTTagCompound").callConstructor();
        }
        
        ReflectionManager.callMethod(nmsEntity, "c", nbtTagCompound);
        if(!withAI) ReflectionManager.callMethod(nbtTagCompound, "setInt", "NoAI", 1);
        if(isSilent) ReflectionManager.callMethod(nbtTagCompound, "setInt", "Silent", 1);
        ReflectionManager.callMethod(nmsEntity, "f", nbtTagCompound);
        
        if(isInvulnerable) ReflectionManager.getCachedNMSClass(null, "Entity").setFieldValue("invulnerable", nmsEntity, true);
        
        return entity;
//</editor-fold>
    }
    
    /**
     * Sends a LeashAttachPlayer packet to <b>targetPlayers</b>
     * @param leashPlayer Player to attach one of the ends of the leash
     * @param entityToLeash Entity to attach the other end of the leash (must be an animal)
     * @param targetPlayers List of players to send the packet
     */
    public static void sendLeashAttachPacket(Player leashPlayer, Animals entityToLeash, Player... targetPlayers){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Objects.requireNonNull(leashPlayer);
        Objects.requireNonNull(entityToLeash);
        Objects.requireNonNull(targetPlayers);
        
        if(targetPlayers.length == 0){
            return;
        }
        
        Object leashPlayerEntity = ReflectionManager.callMethod(leashPlayer, "getHandle");
        Object entityToLeashEntity = ReflectionManager.callMethod(entityToLeash, "getHandle");
        
        // Build the packet
        RefClass packetClass = ReflectionManager.getCachedNMSClass("net.minecraft.network.protocol.game", "PacketPlayOutAttachEntity");
        Object packet;
        if(MinecraftVersion.CURRENT_VERSION.is_1_8()) {
            packet = packetClass.callConstructor(1, entityToLeashEntity, leashPlayerEntity);
        } else {
            packet = packetClass.callConstructor(entityToLeashEntity, leashPlayerEntity);
        }
        
        // Send the packet to every target player
        for(Player player : targetPlayers) {
            Object targetPlayerEntity = ReflectionManager.callMethod(player, "getHandle");
            
            Object playerConnection;
            if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_20_5)) {
                // Since Minecraft 1.20.5, the field is called "connection"
                playerConnection = ReflectionManager.getFieldValue(targetPlayerEntity, "connection");
            } else if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_20)) {
                // Since Minecraft 1.20, the field is called "c"
                playerConnection = ReflectionManager.getFieldValue(targetPlayerEntity, "c");
            } else if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_17)) {
                // From Minecraft 1.17 to Minecraft 1.19, the field is called "b"
                playerConnection = ReflectionManager.getFieldValue(targetPlayerEntity, "b");
            } else {
                // From Minecraft 1.8 to Minecraft 1.16, the field is called "playerConnection"
                playerConnection = ReflectionManager.getFieldValue(targetPlayerEntity, "playerConnection");
            }
            
            if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_20_5)) {
                // Since 1.20.5, the method is called "send"
                ReflectionManager.callMethod(playerConnection, "send", packet, null);
            } else if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_20)) {
                // More info here: https://bukkit.org/threads/sending-packets-in-1-20-2.502472/
                ReflectionManager.callMethod(playerConnection, "a", packet, null);
            } else if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_18)) {
                // Since Minecraft 1.18, the method is called "a"
                ReflectionManager.callMethod(playerConnection, "a", packet);
            } else {
                // From Minecraft 1.8 to Minecraft 1.17, the method is called "sendPacket"
                ReflectionManager.callMethod(playerConnection, "sendPacket", packet);                
            }
        }
//</editor-fold>
    }
    
    public static void sendLeashAttachPacket(Player leashPlayer, Animals entityToLeash, List<Player> targetPlayers){
        sendLeashAttachPacket(leashPlayer, entityToLeash, targetPlayers.toArray(new Player[0]));
    }
}
