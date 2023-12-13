package me.i2000c.newalb.listeners.objects;

import me.i2000c.newalb.reflection.ReflectionManager;
import me.i2000c.newalb.utils2.Task;
import org.bukkit.Location;
import org.bukkit.entity.Chicken;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HookBowAux1_8 {
    
    public static Chicken createEntityChicken(Location loc, boolean isInvisible, boolean isInvulnerable, boolean withAI, boolean isSilent) {
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
    }
}
