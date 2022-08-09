package me.i2000c.newalb.listeners.objects;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import me.i2000c.newalb.utils2.OtherUtils;
import me.i2000c.newalb.utils2.Task;
import org.bukkit.Location;
import org.bukkit.entity.Chicken;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HookBowAux1_8{
    
    private static Method getHandle;
    
    private static Method getNBTTag;
    private static Field invulnerableField;
    private static Method c;
    private static Method f;
    
    private static Constructor nbtTagCompoundConstructor;
    private static Method setInt;
    
    static{
        //<editor-fold defaultstate="collapsed" desc="Code">
        try{
            getHandle = OtherUtils.getCraftClass("entity.CraftEntity").getMethod("getHandle");
            
            Class nmsEntityClass = OtherUtils.getNMSClass(null, "Entity");
            getNBTTag = nmsEntityClass.getMethod("getNBTTag");
            invulnerableField = nmsEntityClass.getDeclaredField("invulnerable");
            
            Class nbtTagCompoundClass = OtherUtils.getNMSClass(null, "NBTTagCompound");
            nbtTagCompoundConstructor = nbtTagCompoundClass.getConstructor();
            
            setInt = nbtTagCompoundClass.getMethod("setInt", String.class, int.class);
            c = nmsEntityClass.getMethod("c", nbtTagCompoundClass);
            f = nmsEntityClass.getMethod("f", nbtTagCompoundClass);
        }catch(Exception ex){
            ex.printStackTrace();
        }
//</editor-fold>
    }
    
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
        Object nmsEntity = getHandle.invoke(entity);
        Object nbtTagCompound = getNBTTag.invoke(nmsEntity);
        if(nbtTagCompound == null){
            nbtTagCompound = nbtTagCompoundConstructor.newInstance();
        }
        
        c.invoke(nmsEntity, nbtTagCompound);
        if(!withAI){
            setInt.invoke(nbtTagCompound, "NoAI", 1);
        }
        if(isSilent){
            setInt.invoke(nbtTagCompound, "Silent", 1);
        }
        f.invoke(nmsEntity, nbtTagCompound);
        
        if(isInvulnerable){
            invulnerableField.setAccessible(true);
            invulnerableField.set(nmsEntity, true);
        }
        
        return entity;
    }
}
