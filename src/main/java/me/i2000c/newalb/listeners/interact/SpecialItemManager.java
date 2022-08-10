package me.i2000c.newalb.listeners.interact;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import me.i2000c.newalb.listeners.objects.AutoBow;
import me.i2000c.newalb.listeners.objects.DarkHole;
import me.i2000c.newalb.listeners.objects.EndermanSoup;
import me.i2000c.newalb.listeners.objects.ExplosiveBow;
import me.i2000c.newalb.listeners.objects.HomingBow;
import me.i2000c.newalb.listeners.objects.HookBow;
import me.i2000c.newalb.listeners.objects.HotPotato;
import me.i2000c.newalb.listeners.objects.IceBow;
import me.i2000c.newalb.listeners.objects.LuckyTool;
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
import me.i2000c.newalb.utils.BowItem;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class SpecialItemManager{
    private static final Map<SpecialItemName, SpecialItem> specialItems;
    private static List<SpecialItem> cachedWandsList;
    private static final List<SpecialItem> cachedObjectsList;
    private static final List<String> cachedNamesList;
    
    static{
        specialItems = new EnumMap(SpecialItemName.class);
        cachedWandsList = new ArrayList<>();
        cachedObjectsList = new ArrayList<>();
        cachedNamesList = new ArrayList<>();
    }
    
    public static void loadSpecialItems(PluginManager pm, Plugin plugin){
        specialItems.clear();
        
        SpecialItem item;
        
        //Load wands
        //<editor-fold defaultstate="collapsed" desc="Code">
        item = new RegenWand();
        specialItems.put(SpecialItemName.regen_wand, item);
        
        item = new InvWand();
        specialItems.put(SpecialItemName.inv_wand, item);
        
        item = new TntWand();
        specialItems.put(SpecialItemName.tnt_wand, item);
        
        item = new SlimeWand();
        specialItems.put(SpecialItemName.slime_wand, item);
        
        item = new FireWand();
        specialItems.put(SpecialItemName.fire_wand, item);
        
        item = new LightningWand();
        specialItems.put(SpecialItemName.lightning_wand, item);
        
        item = new ShieldWand();
        specialItems.put(SpecialItemName.shield_wand, item);
        
        item = new PotionWand();
        specialItems.put(SpecialItemName.potion_wand, item);
        
        item = new FrostPathWand();
        specialItems.put(SpecialItemName.frost_path_wand, item);
//</editor-fold>
        
        //Load objects
        //<editor-fold defaultstate="collapsed" desc="Code">
        item = new DarkHole();
        specialItems.put(SpecialItemName.dark_hole, item);
        
        item = new MiniVolcano();
        specialItems.put(SpecialItemName.mini_volcano, item);
        
        item = new PlayerTracker();
        specialItems.put(SpecialItemName.player_tracker, item);
        
        item = new EndermanSoup();
        specialItems.put(SpecialItemName.enderman_soup, item);
        
        item = new HotPotato();
        specialItems.put(SpecialItemName.hot_potato, item);
        
        item = new IceBow();
        specialItems.put(SpecialItemName.ice_bow, item);
        
        item = new AutoBow();
        specialItems.put(SpecialItemName.auto_bow, item);
        
        item = new MultiBow();
        specialItems.put(SpecialItemName.multi_bow, item);
        
        item = new ExplosiveBow();
        specialItems.put(SpecialItemName.explosive_bow, item);
        
        item = new HomingBow();
        specialItems.put(SpecialItemName.homing_bow, item);
        
        item = new HookBow();
        specialItems.put(SpecialItemName.hook_bow, item);
//</editor-fold>
        
        //Load LuckyTool
        item = new LuckyTool();
        specialItems.put(SpecialItemName.luckytool, item);
        
        //Register all events and load all special items
        specialItems.values().forEach(specialItem -> {
            specialItem.loadItem();
            if(specialItem instanceof BowItem){
                pm.registerEvents((BowItem) specialItem, plugin);
            }
        });
    }
    
    public static void reloadSpecialItems(){
        specialItems.values().forEach(specialItem -> specialItem.loadItem());
    }
    
    public static SpecialItem getSpecialItem(String name){
        SpecialItemName specialItemName = SpecialItemName.fromString(name);
        return specialItems.get(specialItemName);
    }    
    public static SpecialItem getSpecialItem(SpecialItemName specialItemName){
        return specialItems.get(specialItemName);
    }
    
    public static List<SpecialItem> getWands(){
        if(cachedWandsList.isEmpty()){
            specialItems.forEach((specialName, specialItem) -> {
                if(specialName.isWand()){
                    cachedWandsList.add(specialItem);
                }
            });
        }
            
        return cachedWandsList;
    }
    
    public static List<SpecialItem> getObjects(){
        if(cachedObjectsList.isEmpty()){
            specialItems.forEach((specialName, specialItem) -> {
                if(!specialName.isWand()){
                    cachedObjectsList.add(specialItem);
                }
            });
        }
            
        return cachedObjectsList;
    }
    
    public static SpecialItem getLuckyTool(){
        return specialItems.get(SpecialItemName.luckytool);
    }
    
    public static List<String> getSpecialNames(){
        if(cachedNamesList.isEmpty()){
            for(SpecialItemName specialName : SpecialItemName.values()){
                cachedNamesList.add(specialName.name());
            }
        }
            
        return cachedNamesList;
    }
}
