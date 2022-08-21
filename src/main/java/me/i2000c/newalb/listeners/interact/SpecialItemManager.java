package me.i2000c.newalb.listeners.interact;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import me.i2000c.newalb.utils.logger.LogLevel;
import me.i2000c.newalb.utils.logger.Logger;

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
    
    public static void loadSpecialItems(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        specialItems.clear();
        
        for(SpecialItemName specialItemName : SpecialItem.SPECIAL_ITEM_NAMES){
            String[] splittedName = specialItemName.name().split("_");
            StringBuilder stringBuilder = new StringBuilder();
            for(String string : splittedName){
                stringBuilder.append(string.substring(0, 1).toUpperCase());
                stringBuilder.append(string.substring(1));
            }
            
            String packageName;
            if(specialItemName.isWand()){
                packageName = "me.i2000c.newalb.listeners.wands.";
            }else{
                packageName = "me.i2000c.newalb.listeners.objects.";
            }
            stringBuilder.insert(0, packageName);
            
            String className = stringBuilder.toString();
            try{
                Class clazz = Class.forName(className);
                SpecialItem specialItem = (SpecialItem) clazz.newInstance();
                specialItem.loadItem();
                specialItems.put(specialItemName, specialItem);
            }catch(Exception ex){
                Logger.log("An error occurred while loading special items:", LogLevel.ERROR);
                Logger.log(ex, LogLevel.ERROR);
                return;
            }
        }
//</editor-fold>
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
        return specialItems.get(SpecialItemName.lucky_tool);
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
