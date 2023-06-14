package me.i2000c.newalb.listeners.objects;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils2.OtherUtils;
import me.i2000c.newalb.utils2.Task;
import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HookBowAux{
    private static Class PacketPlayOutAttachEntityClass;
    private static Class PacketClass;
    private static Method getHandle;
    private static Constructor packetConstructor;
    
    private static Field playerConnectionField;
    private static Method sendPacketMethod;
    
    static{
        //<editor-fold defaultstate="collapsed" desc="Code">
        try{
            PacketPlayOutAttachEntityClass = OtherUtils.getNMSClass("net.minecraft.network.protocol.game", "PacketPlayOutAttachEntity");
            PacketClass = OtherUtils.getNMSClass("net.minecraft.network.protocol", "Packet");
            
            getHandle = OtherUtils.getCraftClass("entity.CraftEntity").getMethod("getHandle");
            
            try{
                packetConstructor = PacketPlayOutAttachEntityClass.getConstructor(int.class, getHandle.getReturnType(), getHandle.getReturnType());
            }catch(NoSuchMethodException ex){
                packetConstructor = PacketPlayOutAttachEntityClass.getConstructor(getHandle.getReturnType(), getHandle.getReturnType());
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
//</editor-fold>
    }
    
    public static Chicken createEntityChicken(Location loc){
        return createEntityChicken(loc, true, true, false, true);
    }
    
    public static Chicken createEntityChicken(Location loc, boolean isInvisible, boolean isInvulnerable, boolean withAI, boolean isSilent){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(NewAmazingLuckyBlocks.getMinecraftVersion() == MinecraftVersion.v1_8){
            try{
                return HookBowAux1_8.createEntityChicken(loc, isInvisible, isInvulnerable, withAI, isSilent);
            }catch(Exception ex){
                return null;
            }
        }else{
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
        }
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
        
        try{
            //Build the packet
            Object leashPlayerEntity = getHandle.invoke(leashPlayer);
            Object entityToLeashEntity = getHandle.invoke(entityToLeash);
            Object packet;
            try{
                packet = packetConstructor.newInstance(1, entityToLeashEntity, leashPlayerEntity);
            }catch(Exception ex){
                packet = packetConstructor.newInstance(entityToLeashEntity, leashPlayerEntity);
            }
            
            //Send the packet to every target player
            for(Player player : targetPlayers){
                Object targetPlayerEntity = getHandle.invoke(player);
                if(playerConnectionField == null){
                    try{
                        playerConnectionField = targetPlayerEntity.getClass().getField("playerConnection");
                    }catch(NoSuchFieldException ex){
                        try{
                            // Up to Minecraft 1.19.4, the field is called "b"
                            playerConnectionField = targetPlayerEntity.getClass().getField("b");
                        }catch(NoSuchFieldException ex2){
                            // From Minecraft 1.20, the field is called "c"
                            playerConnectionField = targetPlayerEntity.getClass().getField("c");
                        }
                    }
                }
                
                Object playerConnection = playerConnectionField.get(targetPlayerEntity);
                if(sendPacketMethod == null){
                    try{
                        sendPacketMethod = playerConnection.getClass().getMethod("sendPacket", PacketClass);
                    }catch(NoSuchMethodException ex){
                        sendPacketMethod = playerConnection.getClass().getMethod("a", PacketClass);
                    }
                }
                
                sendPacketMethod.invoke(playerConnection, packet);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
//</editor-fold>
    }
    
    public static void sendLeashAttachPacket(Player leashPlayer, Animals entityToLeash, List<Player> targetPlayers){
        sendLeashAttachPacket(leashPlayer, entityToLeash, targetPlayers.toArray(new Player[1]));
    }
}
