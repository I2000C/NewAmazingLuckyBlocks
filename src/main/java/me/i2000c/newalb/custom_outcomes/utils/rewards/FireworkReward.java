package me.i2000c.newalb.custom_outcomes.utils.rewards;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.menus.FireworkMenu;
import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import me.i2000c.newalb.utils.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class FireworkReward extends Reward{
    private int amount;
    private int power;
    private boolean trail;
    private boolean flicker;
    private String type;
    private List<String> colorHEX;
    private List<String> fadeHEX;
    
    public FireworkReward(Outcome outcome){
        super(outcome);
        this.amount = 1;
        this.power = 0;
        this.trail = false;
        this.flicker = false;
        this.type = "BALL";
        this.colorHEX = new ArrayList();
        this.fadeHEX = new ArrayList();
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
    public String getType(){
        return this.type;
    }
    public void setType(String type){
        this.type = type;
    }
    public List<String> getHEXColors(){
        return this.colorHEX;
    }
    public void setHEXColors(List<String> colorHEX){
        this.colorHEX = new ArrayList(colorHEX);
    }
    public List<String> getHEXFadeColors(){
        return this.fadeHEX;
    }
    public void setHEXFadeColors(List<String> fadeHEX){
        this.fadeHEX = new ArrayList(fadeHEX);
    }

    @Override
    public ItemStack getItemToDisplay(){
        ItemStack stack = XMaterial.FIREWORK_ROCKET.parseItem();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("&6Firework");

        List<String> lore = new ArrayList();
        //lore.add("&bID: &r" + fireworkID);
        lore.add("&bAmount: &r" + amount);
        lore.add("&bPower: &r" + power);
        lore.add("&bTrail: &r" + trail);
        lore.add("&bFlicker: &r" + flicker);
        lore.add("&bType: &r" + type);
        lore.add("&bColorList:");
        for(String str : colorHEX){
            lore.add("  &r" + str);
        }
        lore.add("&bFadeColorList:");
        for(String str : fadeHEX){
            lore.add("  &r" + str);
        }

        meta.setLore(lore);
        stack.setItemMeta(meta);
        
        return stack;
    }

    @Override
    public void saveRewardIntoConfig(FileConfiguration config, String path){
        config.set(path + ".amount", this.amount);
        config.set(path + ".power", this.power);
        config.set(path + ".trail", this.trail);
        config.set(path + ".flicker", this.flicker);
        config.set(path + ".type", this.type);
        config.set(path + ".color", this.colorHEX);
        config.set(path + ".fade", this.fadeHEX);
    }
    
    
    @Override
    public void loadRewardFromConfig(FileConfiguration config, String path){
        this.amount = config.getInt(path + ".amount");
        this.power = config.getInt(path + ".power");
        this.trail = config.getBoolean(path + ".trail");
        this.flicker = config.getBoolean(path + ".flicker");
        this.type = config.getString(path + ".type");
        this.colorHEX = config.getStringList(path + ".color");
        this.fadeHEX = config.getStringList(path + ".fade");
    }
    
    @Override
    public void execute(Player player, Location location){
        List<Color> color = new ArrayList();
        for(String str : colorHEX){
            color.add(Color.fromRGB(getDecimalFromHex(str)));
        }
        List<Color> fade = new ArrayList();
        for(String str : fadeHEX){
            fade.add(Color.fromRGB(getDecimalFromHex(str)));
        }
            
        for(int i=0;i<amount;i++){
            Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();        
            fwm.setPower(power);
            fwm.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.valueOf(type)).withColor(color).withFade(fade).trail(trail).flicker(flicker).build());
            fw.setFireworkMeta(fwm);
        }
        //BLACK: 000000
        //WHITE: FFFFFF
        //LIGHT GREY: A6A6A6
        //DARK GREY: 6B6B6B
        //RED: FF0000
        //PINK: FF99FF
        //MAGENTA: FF00FF
        //PURPLE: 8000FF
        //ORANGE: FF8000
        //YELLOW: FFFF00
        //LIGHT GREEN: 33CC33
        //DARK GREEN: 006622
        //LIGHT BLUE: 80CCFF
        //CYAN: 009999
        //DARK BLUE: 0000CC
        //BROWN: 663300
    }
    private static int getDecimalFromHex(String hex){  
        String digits = "0123456789ABCDEF";  
        hex = hex.toUpperCase();  
        int val = 0;  
        for(int i = 0; i < hex.length(); i++){  
            char c = hex.charAt(i);  
            int d = digits.indexOf(c);  
            val = 16*val + d;  
        }  
        return val;  
    }
    
    @Override
    public void edit(Player player){
        FireworkMenu.reset();
        FireworkMenu.reward = this;
        FireworkMenu.selectedType = Arrays.asList(FireworkMenu.type).indexOf(this.type);
        FireworkMenu.openFireworkMenu(player);
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.firework;
    }
    
    @Override
    public Reward cloneReward(){
        FireworkReward reward = new FireworkReward(this.getOutcome());
        reward.amount = this.amount;
        reward.power = this.power;
        reward.type = this.type;
        reward.trail = this.trail;
        reward.flicker = this.flicker;
        reward.setHEXColors(this.colorHEX);
        reward.setHEXFadeColors(this.fadeHEX);
        
        reward.setDelay(this.getDelay());
        return reward;
    }
}
