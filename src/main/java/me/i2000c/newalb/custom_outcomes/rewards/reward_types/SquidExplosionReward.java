package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.RewardType;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.particles.Particles;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import me.i2000c.newalb.utils2.Task;

@Getter
@Setter
public class SquidExplosionReward extends Reward{
    private int countdownTime;
    private int radius;
    
    @Setter(AccessLevel.NONE)
    private List<String> effects;
    
    public SquidExplosionReward(Outcome outcome){
        super(outcome);
        countdownTime = 5;
        radius = 5;
        effects = new ArrayList<>();
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        ItemStackWrapper builder =  ItemStackWrapper.newItem(XMaterial.INK_SAC);
        builder.setDisplayName("&7Squid explosion");
        builder.addLoreLine("&3Countdown time: &d" + countdownTime);
        builder.addLoreLine("&3Radius: &d" + radius);
        builder.addLoreLine("&3Effects:");
        effects.forEach(effect -> {
            builder.addLoreLine("  &d" + effect);
        });
        return builder.toItemStack();
    }
    
    @Override
    public void saveRewardIntoConfig(Config config, String path){
        config.set(path + ".countdownTime", countdownTime);
        config.set(path + ".radius", radius);
        config.set(path + ".effects", effects);
    }
    
    @Override
    public void loadRewardFromConfig(Config config, String path){
        this.countdownTime = config.getInt(path + ".countdownTime");
        this.radius = config.getInt(path + ".radius");
        this.effects = config.getStringList(path + ".effects");
    }
    
    @Override
    public void execute(Player player, Location location){
        //<editor-fold defaultstate="collapsed" desc="Code">
        List<PotionEffect> potionEffectList = effects.stream()
                .map(effectString -> {
                    String[] splitted = effectString.split(";");
                    PotionEffectType type = PotionEffectType.getByName(splitted[0]);
                    int duration = Integer.parseInt(splitted[1])*20;
                    int amplifier = Integer.parseInt(splitted[2]);
                    
                    return new PotionEffect(type, duration, amplifier, false, true);
                })
                .collect(Collectors.toList());
        
        Squid squid = location.getWorld().spawn(location, Squid.class);
        squid.setCustomNameVisible(true);
        
        Task task = new Task(){
            int time = countdownTime;
            
            @Override
            public void run(){
                if(squid.isDead()){
                    cancel();
                    return;
                }
                
                if(time <= 0){
                    cancel();
                    Location loc = squid.getLocation();
                    squid.getNearbyEntities(radius, radius, radius)
                            .forEach(entity -> {
                                if(entity instanceof LivingEntity){
                                    LivingEntity le = (LivingEntity) entity;
                                    potionEffectList.forEach(potionEffect -> {
                                        le.addPotionEffect(potionEffect, true);
                                    });                                    
                                }
                            });
                    squid.remove();
                    XSound.ENTITY_GENERIC_EXPLODE.play(loc, 5, 1);
                    Particles.EXPLOSION_HUGE.create().setPosition(loc).display();
                    return;
                }
                
                squid.setCustomName(Logger.color("&6&l" + time));
                squid.damage(0);
                time--;
            }
        };
        task.runTask(0L, 20L);
//</editor-fold>
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.squid_explosion;
    }
    
    @Override
    public Reward clone(){
        SquidExplosionReward copy = (SquidExplosionReward) super.clone();
        copy.effects = new ArrayList<>(this.effects);
        return copy;
    }
}
