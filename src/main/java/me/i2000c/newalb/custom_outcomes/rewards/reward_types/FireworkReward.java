package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import com.cryptomorin.xseries.XMaterial;

import lombok.Getter;
import lombok.Setter;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.RewardType;
import me.i2000c.newalb.utils2.CustomColor;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import me.i2000c.newalb.utils2.Offset;

@Getter
@Setter
public class FireworkReward extends Reward{
    private int amount;
    private int power;
    private boolean withTrail;
    private boolean withFlicker;
    private FireworkEffect.Type type;
    private List<String> colorsHEX;
    private List<String> fadeColorsHEX;
    private Offset offset;
    
    public FireworkReward(Outcome outcome){
        super(outcome);
        this.amount = 1;
        this.power = 1;
        this.withTrail = false;
        this.withFlicker = false;
        this.type = FireworkEffect.Type.BALL;
        this.colorsHEX = new ArrayList<>();
        this.fadeColorsHEX = new ArrayList<>();
        this.offset = new Offset();
    }
    
    public void setColorsHEX(List<String> colorHEX){
        this.colorsHEX = new ArrayList<>(colorHEX);
    }
    public void setFadeColorsHEX(List<String> fadeHEX){
        this.fadeColorsHEX = new ArrayList<>(fadeHEX);
    }

    @Override
    public ItemStack getItemToDisplay(){
        ItemStackWrapper builder = ItemStackWrapper.newItem(XMaterial.FIREWORK_ROCKET);
        builder.setDisplayName("&6Firework");
        
        builder.addLoreLine("&bAmount: &r" + amount);
        builder.addLoreLine("&bPower: &r" + power);
        builder.addLoreLine("&bTrail: &r" + withTrail);
        builder.addLoreLine("&bFlicker: &r" + withFlicker);
        builder.addLoreLine("&bType: &r" + type);
        
        builder.addLoreLine("&bColorList:");
        colorsHEX.forEach(str -> {
            builder.addLoreLine("  &r" + str);
        });
        
        builder.addLoreLine("&bFadeColorList:");
        fadeColorsHEX.forEach(str -> {
            builder.addLoreLine("  &r" + str);
        });
        
        builder.addLoreLine("&bOffset:");
        builder.addLoreLine("   &5X: &3" + offset.getOffsetX());
        builder.addLoreLine("   &5Y: &3" + offset.getOffsetY());
        builder.addLoreLine("   &5Z: &3" + offset.getOffsetZ());
        
        return builder.toItemStack();
    }

    @Override
    public void saveRewardIntoConfig(Config config, String path){
        config.set(path + ".amount", this.amount);
        config.set(path + ".power", this.power);
        config.set(path + ".trail", this.withTrail);
        config.set(path + ".flicker", this.withFlicker);
        config.set(path + ".type", this.type.name());
        config.set(path + ".color", this.colorsHEX);
        config.set(path + ".fade", this.fadeColorsHEX);
        offset.saveToConfig(config, path + ".offset");
    }    
    
    @Override
    public void loadRewardFromConfig(Config config, String path){
        this.amount = config.getInt(path + ".amount");
        this.power = config.getInt(path + ".power");
        this.withTrail = config.getBoolean(path + ".trail");
        this.withFlicker = config.getBoolean(path + ".flicker");
        this.type = FireworkEffect.Type.valueOf(config.getString(path + ".type"));
        this.colorsHEX = config.getStringList(path + ".color");
        this.fadeColorsHEX = config.getStringList(path + ".fade");
        this.offset = new Offset(config, path + ".offset");
    }
    
    @Override
    public void execute(Player player, Location location){
        List<Color> mainColorList = new ArrayList<>();
        for(String str : colorsHEX){
            CustomColor color = new CustomColor(str);
            mainColorList.add(color.getBukkitColor());
        }
        List<Color> fadeColorList = new ArrayList<>();
        for(String str : fadeColorsHEX){
            CustomColor color = new CustomColor(str);
            fadeColorList.add(color.getBukkitColor());
        }
            
        for(int i=0;i<amount;i++){
            Location loc = offset.applyToLocation(location.clone());
            Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();
            fwm.setPower(power);
            FireworkEffect fireworkEffect = FireworkEffect.builder()
                    .with(type)
                    .withColor(mainColorList)
                    .withFade(fadeColorList)
                    .trail(withTrail)
                    .flicker(withFlicker)
                    .build();
            fwm.addEffect(fireworkEffect);
            fw.setFireworkMeta(fwm);
        }
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.firework;
    }
    
    @Override
    public Reward clone(){
        FireworkReward copy = (FireworkReward) super.clone();
        copy.colorsHEX = new ArrayList<>(this.colorsHEX);
        copy.fadeColorsHEX = new ArrayList<>(this.fadeColorsHEX);
        copy.offset = this.offset.clone();
        return copy;
    }
}
