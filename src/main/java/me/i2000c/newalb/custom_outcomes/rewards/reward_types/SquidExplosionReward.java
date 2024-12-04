package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Getter
@Setter
public class SquidExplosionReward extends Reward{
    private int countdownTime;
    private int radius;
    
    @Setter(AccessLevel.NONE)
    private List<PotionEffect> effects;
    
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
            String name = effect.getType().getName();
            int duration = effect.getDuration();
            int amplifier = effect.getAmplifier();
            boolean isAmbient = effect.isAmbient();
            boolean showParticles = effect.hasParticles();
            builder.addLoreLine(String.format("  &d%s;%d;%d;%s;%s",
                    name, duration, amplifier, isAmbient, showParticles));
        });
        return builder.toItemStack();
    }
    
    @Override
    public void saveRewardIntoConfig(Config config, String path){
        config.set(path + ".countdownTime", countdownTime);
        config.set(path + ".radius", radius);
        
        List<String> effectsStringList = new ArrayList<>();
        effects.forEach(effect -> {
            String name = effect.getType().getName();
            int duration = effect.getDuration();
            int amplifier = effect.getAmplifier();
            boolean isAmbient = effect.isAmbient();
            boolean showParticles = effect.hasParticles();
            effectsStringList.add(String.format("%s;%d;%d;%s;%s",
                    name, duration, amplifier, isAmbient, showParticles));
        });
        config.set(path + ".effects", effectsStringList);
    }
    
    @Override
    public void loadRewardFromConfig(Config config, String path){
        this.countdownTime = config.getInt(path + ".countdownTime");
        this.radius = config.getInt(path + ".radius");
        
        List<String> effectsStringList = config.getStringList(path + ".effects");
        this.effects.clear();
        effectsStringList.forEach(effectString -> {
            String[] split = effectString.split(";");
            PotionEffectType name = PotionEffectType.getByName(split[0]);
            int duration = Integer.parseInt(split[1]);
            int amplifier = Integer.parseInt(split[2]);
            boolean isAmbient = false, showParticles = true;
            if(split.length == 5) {
                isAmbient = Boolean.parseBoolean(split[3]);
                showParticles = Boolean.parseBoolean(split[4]);
            }
            
            PotionEffect effect = new PotionEffect(name, duration, amplifier, isAmbient, showParticles);
            this.effects.add(effect);
        });
    }
    
    @Override
    public void execute(Player player, Location location){
        //<editor-fold defaultstate="collapsed" desc="Code">
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
                
                Location loc = squid.getLocation();
                if(time <= 0){
                    List<PotionEffect> effectiveEffects = effects.stream().map(effect -> {
                        PotionEffectType name = effect.getType();
                        int durationTicks = effect.getDuration()*20;
                        int amplifier = effect.getAmplifier();
                        boolean isAmbient = effect.isAmbient();
                        boolean showParticles = effect.hasParticles();
                        
                        PotionEffect effectiveEffect = new PotionEffect(name, durationTicks, amplifier, isAmbient, showParticles);
                        return effectiveEffect;
                    }).collect(Collectors.toList());
                    
                    cancel();
                    squid.getNearbyEntities(radius, radius, radius)
                            .forEach(entity -> {
                                if(entity instanceof LivingEntity){
                                    LivingEntity le = (LivingEntity) entity;
                                    effectiveEffects.forEach(effect -> le.addPotionEffect(effect, true));
                                }
                            });
                    squid.remove();
                    XSound.ENTITY_GENERIC_EXPLODE.play(loc, 5, 1);
                    Particles.EXPLOSION_HUGE.create().build().displayAt(loc);                    
                    return;
                }
                
                XSound.ENTITY_PLAYER_HURT.play(loc, 5, 0.5f);
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
