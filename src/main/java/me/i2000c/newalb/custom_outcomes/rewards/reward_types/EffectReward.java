package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import lombok.Setter;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.menus.EffectMenu;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.RewardType;
import me.i2000c.newalb.functions.EditorBackFunction;
import me.i2000c.newalb.functions.EditorNextFunction;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Getter
@Setter
public class EffectReward extends Reward{
    public static final String CLEAR_EFFECTS_TAG = "CLEAR_EFFECTS";
    
    private PotionEffectType potionEffect;
    private int duration;
    private int amplifier;
    private boolean ambient;
    private boolean showParticles;
    private boolean clearEffects;
    
    public EffectReward(Outcome outcome){
        super(outcome);
        potionEffect = null;
        duration = 30;
        amplifier = 0;
        ambient = true;
        showParticles = true;
        clearEffects = false;
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        ItemStackWrapper builder = ItemStackWrapper.newItem(XMaterial.POTION);
        builder.setDisplayName("&5Effect");
        if(this.clearEffects){
            builder.addLoreLine("&dEffect name: &b" + CLEAR_EFFECTS_TAG);
        }else{
            builder.addLoreLine("&dEffect name: &b" + this.potionEffect.getName());
            if(duration >= 0){
                builder.addLoreLine("&dDuration: &b" + duration + " &dseconds");
            }else{
                builder.addLoreLine("&dDuration: &binfinite &dseconds");
            }
            builder.addLoreLine("&dAmplifier: &b" + this.amplifier);
            builder.addLoreLine("&dIsAmbient: &b" + this.ambient);
            builder.addLoreLine("&dShowParticles: &b" + this.showParticles);
        }
        
        return builder.toItemStack();
    }
    
    @Override
    public void saveRewardIntoConfig(Config config, String path){
        if(this.clearEffects){
            config.set(path + ".effectName", CLEAR_EFFECTS_TAG);
            config.set(path + ".duration", null);
            config.set(path + ".amplifier", null);
            config.set(path + ".ambient", null);
            config.set(path + ".showParticles", null);
        }else{
            config.set(path + ".effectName", this.potionEffect.getName());
            config.set(path + ".duration", this.duration);
            config.set(path + ".amplifier", this.amplifier);
            config.set(path + ".ambient", this.ambient);
            config.set(path + ".showParticles", this.showParticles);
        }
    }
    
    @Override
    public void loadRewardFromConfig(Config config, String path){
        String effectName = config.getString(path + ".effectName");
        if(effectName.equals(CLEAR_EFFECTS_TAG)){
            this.clearEffects = true;
            this.potionEffect = null;
            this.duration = 30;
            this.amplifier = 1;
            this.ambient = true;
            this.showParticles = true;
        }else{
            this.clearEffects = false;
            this.potionEffect = PotionEffectType.getByName(effectName);
            this.duration = config.getInt(path + ".duration");
            this.amplifier = config.getInt(path + ".amplifier");
            this.ambient = config.getBoolean(path + ".ambient");
            this.showParticles = config.getBoolean(path + ".showParticles");
        }
    }
    
    @Override
    public void execute(Player player, Location location){
        if(this.clearEffects){
            player.getActivePotionEffects().forEach(pe -> player.removePotionEffect(pe.getType()));
        }else{
            int durationAux = duration * 20;
            if(durationAux < 0){
                durationAux = Integer.MAX_VALUE;
            }

            player.addPotionEffect(new PotionEffect(potionEffect, durationAux, amplifier, ambient, showParticles), true);
        }            
    }
    
    @Override
    public void edit(Player player, 
            EditorBackFunction onBack, 
            EditorNextFunction onNext){
        Editor editor = this.getRewardType().getEditorType().getEditor();
        EffectMenu.setShowClearEffectsItem(true);
        editor.editExistingItem(this.clone(), player, onBack, onNext);
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.effect;
    }
    
    @Override
    public Reward clone(){
        EffectReward copy = (EffectReward) super.clone();
        return copy;
    }
}
