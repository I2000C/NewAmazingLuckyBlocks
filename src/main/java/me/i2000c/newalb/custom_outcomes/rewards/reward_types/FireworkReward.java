package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.List;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.RewardType;
import me.i2000c.newalb.utils2.CustomColor;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.Offset;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkReward extends Reward{
    private int amount;
    private int power;
    private boolean trail;
    private boolean flicker;
    private FireworkEffect.Type type;
    private List<String> colorHEX;
    private List<String> fadeHEX;
    private Offset offset;
    
    public FireworkReward(Outcome outcome){
        super(outcome);
        this.amount = 1;
        this.power = 0;
        this.trail = false;
        this.flicker = false;
        this.type = FireworkEffect.Type.BALL;
        this.colorHEX = new ArrayList<>();
        this.fadeHEX = new ArrayList<>();
        this.offset = new Offset();
    }
    
    public int getAmount(){
        return this.amount;
    }
    public void setAmount(int amount){
        this.amount = amount;
    }
    public int getPower(){
        return this.power;
    }
    public void setPower(int power){
        this.power = power;
    }
    public boolean withTrail(){
        return this.trail;
    }
    public void setWithTrail(boolean trail){
        this.trail = trail;
    }
    public boolean withFlicker(){
        return this.flicker;
    }
    public void setWithFlicker(boolean flicker){
        this.flicker = flicker;
    }
    public FireworkEffect.Type getType(){
        return this.type;
    }
    public void setType(FireworkEffect.Type type){
        this.type = type;
    }
    public List<String> getHEXMainColors(){
        return this.colorHEX;
    }
    public void setHEXColors(List<String> colorHEX){
        this.colorHEX = new ArrayList<>(colorHEX);
    }
    public List<String> getHEXFadeColors(){
        return this.fadeHEX;
    }
    public void setHEXFadeColors(List<String> fadeHEX){
        this.fadeHEX = new ArrayList<>(fadeHEX);
    }
    public Offset getOffset(){
        return this.offset;
    }
    public void setOffset(Offset offset){
        this.offset = offset;
    }

    @Override
    public ItemStack getItemToDisplay(){
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.FIREWORK_ROCKET);
        builder.withDisplayName("&6Firework");
        
        builder.addLoreLine("&bAmount: &r" + amount);
        builder.addLoreLine("&bPower: &r" + power);
        builder.addLoreLine("&bTrail: &r" + trail);
        builder.addLoreLine("&bFlicker: &r" + flicker);
        builder.addLoreLine("&bType: &r" + type);
        
        builder.addLoreLine("&bColorList:");
        colorHEX.forEach(str -> {
            builder.addLoreLine("  &r" + str);
        });
        
        builder.addLoreLine("&bFadeColorList:");
        fadeHEX.forEach(str -> {
            builder.addLoreLine("  &r" + str);
        });
        
        builder.addLoreLine("&bOffset:");
        builder.addLoreLine("   &5X: &3" + offset.getOffsetX());
        builder.addLoreLine("   &5Y: &3" + offset.getOffsetY());
        builder.addLoreLine("   &5Z: &3" + offset.getOffsetZ());
        
        return builder.build();
    }

    @Override
    public void saveRewardIntoConfig(FileConfiguration config, String path){
        config.set(path + ".amount", this.amount);
        config.set(path + ".power", this.power);
        config.set(path + ".trail", this.trail);
        config.set(path + ".flicker", this.flicker);
        config.set(path + ".type", this.type.name());
        config.set(path + ".color", this.colorHEX);
        config.set(path + ".fade", this.fadeHEX);
        offset.saveToConfig(config, path + ".offset");
    }    
    
    @Override
    public void loadRewardFromConfig(FileConfiguration config, String path){
        this.amount = config.getInt(path + ".amount");
        this.power = config.getInt(path + ".power");
        this.trail = config.getBoolean(path + ".trail");
        this.flicker = config.getBoolean(path + ".flicker");
        this.type = FireworkEffect.Type.valueOf(config.getString(path + ".type"));
        this.colorHEX = config.getStringList(path + ".color");
        this.fadeHEX = config.getStringList(path + ".fade");
        if(config.isConfigurationSection(path + ".offset")){
            this.offset = new Offset(config, path + ".offset");
        }        
    }
    
    @Override
    public void execute(Player player, Location location){
        List<Color> mainColorList = new ArrayList<>();
        for(String str : colorHEX){
            CustomColor color = new CustomColor(str);
            mainColorList.add(color.getBukkitColor());
        }
        List<Color> fadeColorList = new ArrayList<>();
        for(String str : fadeHEX){
            CustomColor color = new CustomColor(str);
            fadeColorList.add(color.getBukkitColor());
        }
            
        for(int i=0;i<amount;i++){
            Location loc = offset.addToLocation(location.clone());
            Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();
            fwm.setPower(power);
            FireworkEffect fireworkEffect = FireworkEffect.builder()
                    .with(type)
                    .withColor(mainColorList)
                    .withFade(fadeColorList)
                    .trail(trail)
                    .flicker(flicker)
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
        copy.colorHEX = new ArrayList<>(this.colorHEX);
        copy.fadeHEX = new ArrayList<>(this.fadeHEX);
        copy.offset = this.offset.clone();
        return copy;
    }
}
