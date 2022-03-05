package me.i2000c.newalb.custom_outcomes.utils.rewards;

import java.util.ArrayList;
import me.i2000c.newalb.listeners.objects.AutoBow;
import me.i2000c.newalb.listeners.objects.DarkHole;
import me.i2000c.newalb.listeners.objects.EndermanSoup;
import me.i2000c.newalb.listeners.objects.ExplosiveBow;
import me.i2000c.newalb.listeners.objects.HomingBow;
import me.i2000c.newalb.listeners.objects.IceBow;
import me.i2000c.newalb.listeners.objects.MiniVolcano;
import me.i2000c.newalb.listeners.objects.MultiBow;
import me.i2000c.newalb.listeners.objects.PlayerTracker;
import me.i2000c.newalb.listeners.wands.FireWand;
import me.i2000c.newalb.listeners.wands.FrostPathWand;
import me.i2000c.newalb.listeners.wands.InvWand;
import me.i2000c.newalb.listeners.wands.LightningWand;
import me.i2000c.newalb.listeners.wands.PotionWand;
import me.i2000c.newalb.listeners.wands.RegenWand;
import me.i2000c.newalb.listeners.wands.ShieldWand;
import me.i2000c.newalb.listeners.wands.SlimeWand;
import me.i2000c.newalb.listeners.wands.TntWand;
import me.i2000c.newalb.custom_outcomes.menus.ItemMenu;
import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import me.i2000c.newalb.utils.EnchantmentUtils;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.TextureManager;
import java.util.List;
import me.i2000c.newalb.listeners.objects.HookBow;
import me.i2000c.newalb.listeners.objects.HotPotato;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.menus.FireworkMenu;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
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
        this.item = new ItemStack(material, amount, durability);
        ItemMeta meta = this.item.getItemMeta();
        if(config.contains(path + ".name")){
            String displayName = Logger.color(config.getString(path + ".name"));
            meta.setDisplayName(displayName);
        }
        if(config.contains(path + ".lore")){
            List<String> lore = Logger.color(config.getStringList(path + ".lore"));
            meta.setLore(lore);
        }
        if(config.contains(path + ".enchantments")){
            List<String> enchantments = config.getStringList(path + ".enchantments");
            EnchantmentUtils.setEnchantments(meta, enchantments);
        }
        
        //Load armor color
        switch(this.item.getType()){
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                if(config.contains(path + ".armorColor")){
                    String hexColor = config.getString(path + ".armorColor");
                    hexColor = hexColor.substring(2); //Remove the '0x'
                    LeatherArmorMeta lam = (LeatherArmorMeta) meta;
                    lam.setColor(Color.fromRGB(FireworkMenu.getDecimalFromHex(hexColor)));
                }
                break;
        }
        
        //Load potion meta
        ItemMenu.PotionSplashType type = ItemMenu.PotionSplashType.getFromPotion(this.item);
        if(type != null){
            if(config.contains(path + ".potionSplashType")){
                type = ItemMenu.PotionSplashType.valueOf(config.getString(path + ".potionSplashType"));
                type.setToPotion(this.item);
            }
            if(config.contains(path + ".potionEffects")){
                PotionMeta potionMeta = (PotionMeta) meta;
                List<String> potionEffects = config.getStringList(path + ".potionEffects");
                potionEffects.forEach(string -> {
                    String[] splitted = string.split(";");
                    String name = splitted[0];
                    int duration = Integer.parseInt(splitted[1]);
                    int amplifier = Integer.parseInt(splitted[2]);
                    
                    if(duration < 0){
                        duration = Integer.MAX_VALUE / 20;
                    }
                    if(amplifier < 0){
                        amplifier = 0;
                    }
                    
                    potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(name), duration * 20, amplifier), true);
                });
            }
            if(NewAmazingLuckyBlocks.getMinecraftVersion().compareTo(MinecraftVersion.v1_11) >= 0){
                if(config.contains(path + ".potionColor")){
                    String hexColor = config.getString(path + ".potionColor");
                    hexColor = hexColor.substring(2); //Remove the '0x'
                    PotionMeta potionMeta = (PotionMeta) meta;
                    potionMeta.setColor(Color.fromRGB(FireworkMenu.getDecimalFromHex(hexColor)));
                }
            }
        }
        
        this.item.setItemMeta(meta);
        
        //Load texture ID
        if(config.contains(path + ".textureID")){
            String textureID = config.getString(path + ".textureID");
            try{
                TextureManager.Texture texture = new TextureManager.Texture(textureID);
                TextureManager.setTexture(this.item, texture);
            }catch(TextureManager.InvalidHeadException ex){
                Logger.log("ItemReward at \"" + path + "\" contains an invalid HeadTexture");
            }
        }
//</editor-fold>
    }
    
    @Override
    public void saveRewardIntoConfig(FileConfiguration config, String path){
        //<editor-fold defaultstate="collapsed" desc="Code">
        config.set(path + ".material" , this.item.getType().name());
        config.set(path + ".amount" , this.item.getAmount());
        config.set(path + ".durability" , this.item.getDurability());
        if(this.item.hasItemMeta()){
            ItemMeta meta = this.item.getItemMeta();
            if(meta.hasDisplayName()){
                config.set(path + ".name", Logger.deColor(meta.getDisplayName()));
            }
            if(meta.hasLore()){
                config.set(path + ".lore", Logger.deColor(meta.getLore()));
            }
            if(meta.hasEnchants()){
                config.set(path + ".enchantments", EnchantmentUtils.getEnchantments(meta));
            }
            
            //Save texture
            TextureManager.Texture texture = TextureManager.getTexture(this.item);
            if(texture != null){
                config.set(path + ".textureID", texture.getID());
            }
            
            //Save leather armor color
            switch(this.item.getType()){
                case LEATHER_HELMET:
                case LEATHER_CHESTPLATE:
                case LEATHER_LEGGINGS:
                case LEATHER_BOOTS:
                    LeatherArmorMeta lam = (LeatherArmorMeta) meta;
                    String hexColor = FireworkMenu.getHexFromDecimal(lam.getColor().asRGB());
                    config.set(path + ".armorColor", "0x" + hexColor);
            }
            
            ItemMenu.PotionSplashType type = ItemMenu.PotionSplashType.getFromPotion(this.item);
            if(type != null){
                config.set(path + ".material" , "POTION");
                config.set(path + ".potionSplashType", type.name());                
                PotionMeta potionMeta = (PotionMeta) meta;
                if(NewAmazingLuckyBlocks.getMinecraftVersion().compareTo(MinecraftVersion.v1_11) >= 0){
                    if(potionMeta.hasColor()){
                        String hexColor = FireworkMenu.getHexFromDecimal(potionMeta.getColor().asRGB());
                        config.set(path + ".potionColor", "0x" + hexColor);
                    }
                }
                List<String> effectList = new ArrayList<>();
                potionMeta.getCustomEffects().forEach(potionEffect -> {
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
        }
//</editor-fold>
    }
    
    private static enum SpecialName{
        //<editor-fold defaultstate="collapsed" desc="Code">
        regen_wand,
        inv_wand,
        tnt_wand,
        slime_wand,
        fire_wand,
        lightning_wand,
        shield_wand,
        potion_wand,
        frost_path_wand,
        
        dark_hole,
        mini_volcano,
        player_tracker,
        enderman_soup,
        hot_potato,
        ice_bow,
        auto_bow,
        multi_bow,
        explosive_bow,
        homing_bow,
        hook_bow;
        
        public static SpecialName fromString(String str){
            try{
                return SpecialName.valueOf(str.replace("%", ""));
            }catch(IllegalArgumentException ex){
                return null;
            }
        }
//</editor-fold>
    }
    
    @Override
    public void execute(Player player, Location location){
        ItemStack stack;
        if(this.item.hasItemMeta() && this.item.getItemMeta().hasDisplayName()){
            //<editor-fold defaultstate="collapsed" desc="Code">
            String name = Logger.stripColor(this.item.getItemMeta().getDisplayName());
            SpecialName sn = SpecialName.fromString(name);
            if(sn == null){
                stack = this.item;
            }else switch(sn){
                //Wands
                case regen_wand:
                    stack = RegenWand.getWand();
                    break;
                case inv_wand:
                    stack = InvWand.getWand();
                    break;
                case tnt_wand:
                    stack = TntWand.getWand();
                    break;
                case slime_wand:
                    stack = SlimeWand.getWand();
                    break;
                case fire_wand:
                    stack = FireWand.getWand();
                    break;
                case lightning_wand:
                    stack = LightningWand.getWand();
                    break;
                case shield_wand:
                    stack = ShieldWand.getWand();
                    break;
                case potion_wand:
                    stack = PotionWand.getWand();
                    break;
                case frost_path_wand:
                    stack = FrostPathWand.getWand();
                    break;


                    //Objects
                case dark_hole:
                    stack = DarkHole.getObject();
                    break;
                case mini_volcano:
                    stack = MiniVolcano.getObject();
                    break;
                case player_tracker:
                    stack = PlayerTracker.getObject();
                    break;
                case enderman_soup:
                    stack = EndermanSoup.getObject();
                    break;
                case hot_potato:
                    stack = HotPotato.getObject();
                    break;
                case ice_bow:
                    stack = IceBow.getObject();
                    break;
                case auto_bow:
                    stack = AutoBow.getObject();
                    break;
                case multi_bow:
                    stack = MultiBow.getObject();
                    break;
                case explosive_bow:
                    stack = ExplosiveBow.getObject();
                    break;
                case homing_bow:
                    stack = HomingBow.getObject();
                    break;
                case hook_bow:
                    stack = HookBow.getObject();
                    break;
                default:
                    stack = this.item;
            }
        }else{
            stack = this.item;
//</editor-fold>
        }
        
        if(stack != this.item){
            stack.setAmount(item.getAmount());
            stack.addUnsafeEnchantments(item.getEnchantments());
        }
        location.getWorld().dropItemNaturally(location, stack.clone());
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
    public Reward cloneReward(){
        ItemReward reward = new ItemReward(this.getOutcome());
        reward.item = this.item.clone();
        
        reward.setDelay(this.getDelay());
        return reward;
    }
}
