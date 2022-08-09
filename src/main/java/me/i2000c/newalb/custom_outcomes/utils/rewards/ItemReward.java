package me.i2000c.newalb.custom_outcomes.utils.rewards;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.List;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.menus.FireworkMenu;
import me.i2000c.newalb.custom_outcomes.menus.ItemMenu;
import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import me.i2000c.newalb.utils.SpecialItem;
import me.i2000c.newalb.utils.SpecialItemManager;
import me.i2000c.newalb.utils.logger.LogLevel;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils.textures.InvalidTextureException;
import me.i2000c.newalb.utils.textures.Texture;
import me.i2000c.newalb.utils.textures.TextureException;
import me.i2000c.newalb.utils.textures.URLTextureException;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItemReward extends Reward{
    private ItemStack item;    
    public ItemReward(Outcome outcome){
        super(outcome);
        item = null;
    }
    
    public ItemStack getItem(){
        return this.item;
    }
    public void setItem(ItemStack sk){
        this.item = sk;
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        return getItem().clone();
    }
    
    @Override
    public void loadRewardFromConfig(FileConfiguration config, String path){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Material material = Material.valueOf(config.getString(path + ".material"));
        int amount = config.getInt(path + ".amount", 1);
        short durability = (short) config.getInt(path + ".durability", 0);
        
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.matchXMaterial(material));
        builder.withAmount(amount);
        builder.withDurability(durability);
        
        if(config.contains(path + ".name")){
            String displayName = config.getString(path + ".name");
            builder.withDisplayName(displayName);
        }
        if(config.contains(path + ".lore")){
            List<String> lore = config.getStringList(path + ".lore");
            builder.withLore(lore);
        }
        if(config.contains(path + ".enchantments")){
            List<String> enchantments = config.getStringList(path + ".enchantments");
            builder.withEnchantments(enchantments);
        }
        
        //Load armor color
        switch(material){
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                if(config.contains(path + ".armorColor")){
                    String hexColor = config.getString(path + ".armorColor");
                    hexColor = hexColor.substring(2); //Remove the '0x' prefix
                    builder.withColor(Color.fromRGB(FireworkMenu.getDecimalFromHex(hexColor)));
                }
                break;
        }
        
        //Load potion meta
        ItemMenu.PotionSplashType type = ItemMenu.PotionSplashType.getFromPotion(builder.build());
        if(type != null){
            if(config.contains(path + ".potionSplashType")){
                type = ItemMenu.PotionSplashType.valueOf(config.getString(path + ".potionSplashType"));
                type.setToPotion(builder.build());
            }
            if(config.contains(path + ".potionEffects")){
                List<String> potionEffects = config.getStringList(path + ".potionEffects");
                potionEffects.forEach(string -> {
                    String[] splitted = string.split(";");
                    String name = splitted[0];
                    int duration = Integer.parseInt(splitted[1]) * 20;
                    int amplifier = Integer.parseInt(splitted[2]);
                    
                    if(duration < 0){
                        duration = Integer.MAX_VALUE;
                    }
                    if(amplifier < 0){
                        amplifier = 0;
                    }
                    
                    builder.addPotionEffect(new PotionEffect(PotionEffectType.getByName(name), duration, amplifier));
                });
            }
            if(NewAmazingLuckyBlocks.getMinecraftVersion().compareTo(MinecraftVersion.v1_11) >= 0){
                if(config.contains(path + ".potionColor")){
                    String hexColor = config.getString(path + ".potionColor");
                    hexColor = hexColor.substring(2); //Remove the '0x' prefix
                    builder.withColor(Color.fromRGB(FireworkMenu.getDecimalFromHex(hexColor)));
                }
            }
        }
        
        //Load texture ID
        if(config.contains(path + ".textureID")){
            String textureID = config.getString(path + ".textureID");
            try{
                Texture texture = new Texture(textureID);
                builder.withTexture(texture);
            }catch(InvalidTextureException ex){
                Logger.log("ItemReward at \"" + path + "\" contains an invalid HeadTexture", 
                            LogLevel.WARN);
            }catch(URLTextureException ex){
                Logger.log("An error occurred while loading texture for ItemReward at \"" + path + "\":",
                            LogLevel.ERROR);
                Logger.log(ex, LogLevel.ERROR);
            }catch(TextureException ex){}
        }
        
        this.item = builder.build();
//</editor-fold>
    }
    
    @Override
    public void saveRewardIntoConfig(FileConfiguration config, String path){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemBuilder builder = ItemBuilder.fromItem(item, false);
        
        config.set(path + ".material" , builder.getMaterial().parseMaterial().name());
        config.set(path + ".amount" , builder.getAmount());
        config.set(path + ".durability" , builder.getDurability());
        
        if(builder.hasDisplayName()){
            config.set(path + ".name", Logger.deColor(builder.getDisplayName()));
        }
        if(builder.hasLore()){
            config.set(path + ".lore", Logger.deColor(builder.getLore()));
        }
        if(builder.hasEnchantments()){
            config.set(path + ".enchantments", builder.getEnchantmentsIntoStringList());
        }

        //Save texture
        Texture texture = builder.getTexture();
        if(texture != null){
            config.set(path + ".textureID", texture.getID());
        }

        //Save leather armor color
        switch(this.item.getType()){
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                String hexColor = FireworkMenu.getHexFromDecimal(builder.getColor().asRGB());
                config.set(path + ".armorColor", "0x" + hexColor);
        }

        ItemMenu.PotionSplashType type = ItemMenu.PotionSplashType.getFromPotion(this.item);
        if(type != null){
            config.set(path + ".material" , "POTION");
            config.set(path + ".potionSplashType", type.name());   
            if(NewAmazingLuckyBlocks.getMinecraftVersion().compareTo(MinecraftVersion.v1_11) >= 0){
                if(builder.hasColor()){
                    String hexColor = FireworkMenu.getHexFromDecimal(builder.getColor().asRGB());
                    config.set(path + ".potionColor", "0x" + hexColor);
                }
            }
            List<String> effectList = new ArrayList<>();
            builder.getPotionEffects().forEach(potionEffect -> {
                String name = potionEffect.getType().getName();
                int duration = potionEffect.getDuration() / 20;
                int amplifier = potionEffect.getAmplifier();

                if(duration < 0){
                    duration = 0;
                }
                if(amplifier < 0){
                    amplifier = 0;
                }

                effectList.add(name + ";" + duration + ";" + amplifier);
            });

            config.set(path + ".potionEffects", effectList);
        }
//</editor-fold>
    }
    
    @Override
    public void execute(Player player, Location location){
        ItemStack stack = null;
        String displayName = ItemBuilder.fromItem(item, false).getDisplayName();
        if(displayName != null){
            String name = Logger.stripColor(displayName);
            if(name.charAt(0) == '%' && name.charAt(name.length()-1) == '%'){
                SpecialItem specialItem = SpecialItemManager.getSpecialItem(name);
                if(specialItem != null){
                    stack = specialItem.getItem();
                }
            }
        }
        
        if(stack == null){
            stack = item.clone();
        }else{
            // Set only amount and enchantments if the item is a special item
            stack.setAmount(item.getAmount());
            stack.addUnsafeEnchantments(item.getEnchantments());
        }
        
        location.getWorld().dropItemNaturally(location, stack);
    }
    
    @Override
    public void edit(Player player){
        ItemMenu.reset();
        ItemMenu.reward = this;
        ItemMenu.amount = this.item.getAmount();
        ItemMenu.openItemMenu(player);
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.item;
    }
    
    @Override
    public Reward clone(){
        ItemReward copy = (ItemReward) super.clone();
        copy.item = this.item.clone();
        return copy;
    }
}
