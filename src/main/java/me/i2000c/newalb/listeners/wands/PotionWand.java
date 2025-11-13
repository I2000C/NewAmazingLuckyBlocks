package me.i2000c.newalb.listeners.wands;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.version.MinecraftVersion;
import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.random.RandomUtils;

public class PotionWand extends SpecialItem{
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
            e.setCancelled(true);
            
            Player player = e.getPlayer();
            if(!super.decreaseWandUses(e.getItem(), e.getPlayer())){
                e.setCancelled(true);
                return;
            }
            
            super.getPlayerCooldown().updateCooldown(player);
            Vector vector = player.getLocation().getDirection().multiply(0.5);
            ItemStack iStack;
            if(MinecraftVersion.CURRENT_VERSION.is_1_8()){
                iStack = new ItemStack(Material.POTION);
            }else{
                iStack = new ItemStack(Material.SPLASH_POTION);
            }
            
            
            PotionMeta pm = (PotionMeta) iStack.getItemMeta();
            addRandomEffects(pm);
            iStack.setItemMeta(pm);

            ThrownPotion thrownPotion = player.launchProjectile(ThrownPotion.class);
            thrownPotion.setItem(iStack);
            thrownPotion.setVelocity(vector);
        }
    }
  
    private static void addRandomEffects(PotionMeta pm){
        //<editor-fold defaultstate="collapsed" desc="Code">
        List<String> effects = ConfigManager.getMainConfig().getStringList("Wands.PotionWand.effects");
        int weightSum = 0;
        int r;
        for(int i=0;i<effects.size();i++){
            String effect[] = effects.get(i).split(";");
            int prob = Integer.parseInt(effect[3]);
            weightSum += prob;
        }
        weightSum--;
        
        if(weightSum == 0){
            r = 0;
        }else if(weightSum < 0){
            Logger.warn("&cTotal probability must be &a>0 in potionWandEffects");
            return;
        }else{
            r = RandomUtils.getInt(weightSum);
        }
        
        int selection = r;
        for(int i=0;i<=effects.size();i++){
            String effect[] = effects.get(i).split(";");
            int prob = Integer.parseInt(effect[3]);
            
            selection -= prob;
            if(selection < 0){
                
                String effectName = effect[0];
                int time = Integer.parseInt(effect[1])*20;
                int amplifier = Integer.parseInt(effect[2]);
                
                pm.addCustomEffect(new PotionEffect(PotionEffectType.getByName(effectName), time, amplifier), true);
                break;
            }
        }
//</editor-fold>
    }
    
    @Override
    public ItemStack buildItem(){
        return ItemStackWrapper.newItem(XMaterial.MUSIC_DISC_11)
                               .setLore(super.getLoreOfWand())
                               .toItemStack();
    }
}
