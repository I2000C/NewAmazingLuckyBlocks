package me.i2000c.newalb.listeners.objects;

import java.lang.reflect.Field;
import me.i2000c.newalb.utils2.Task;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Chicken;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HookBowAux1_8{
    public static Chicken createEntityChicken(Location loc, boolean isInvisible, boolean isInvulnerable, boolean withAI, boolean isSilent) throws Exception{
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
        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        NBTTagCompound tag = nmsEntity.getNBTTag();
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        nmsEntity.c(tag);
        if(!withAI){
            tag.setInt("NoAI", 1);
        }
        if(isSilent){
            tag.setInt("Silent", 1);
        }        
        nmsEntity.f(tag);          
        
        if(isInvulnerable){
            Field f = net.minecraft.server.v1_8_R3.Entity.class.getDeclaredField("invulnerable");
            f.setAccessible(true);
            f.set(nmsEntity, true);
        }
        
        return entity;
    }
}
