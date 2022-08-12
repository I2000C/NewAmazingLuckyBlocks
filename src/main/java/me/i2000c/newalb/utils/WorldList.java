package me.i2000c.newalb.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.lang_utils.LangLoader;
import me.i2000c.newalb.utils.logger.LogLevel;
import me.i2000c.newalb.utils.logger.Logger;
import org.bukkit.Bukkit;

public class WorldList{    
    private static Map<String, Boolean> WORLDS = new LinkedHashMap<>();
    
    public static Map<String, Boolean> getWorlds(){
        return WORLDS;
    }
    
    static void setWorlds(Map<String, Boolean> worlds){
        WORLDS = worlds;
    }
    
    private static boolean needToUpdateWorlds(List<String> serverWorlds){
        if(WORLDS.size() == serverWorlds.size()){            
            Iterator<String> worldsIterator = WORLDS.keySet().iterator();
            Iterator<String> serverWorldsIterator = serverWorlds.iterator();
            while(worldsIterator.hasNext()){
                String serverWorldName = serverWorldsIterator.next();
                String worldName = worldsIterator.next();
                if(!serverWorldName.equals(worldName)){
                    return true;
                }
            }
            
            return false;
        }else{
            return true;
        }
    }
    
    public static void updateWorlds(boolean force){
        List<String> serverWorlds = Bukkit.getWorlds()
                .stream()
                .sorted((world1, world2) -> world1.getName().compareTo(world2.getName()))
                .map(world -> world.getName())
                .collect(Collectors.toList());
        
        boolean reloadWorlds = needToUpdateWorlds(serverWorlds);
        if(!force && !reloadWorlds){
            return;
        }
        
        WORLDS.clear();
        ConfigManager.getConfig()
                .getStringList("Worlds-list")
                .forEach(worldName -> {
                    try{
                        String[] splitted = worldName.split(";");
                        if(splitted[1].equals("true")){
                            WORLDS.put(splitted[0], true);
                        }else{
                            WORLDS.put(splitted[0], false);
                        }
                    }catch(Exception ex){
                        Logger.log("Couldn't load world \"" + worldName + "\" (incorrect format)", LogLevel.WARN);
                    }
                }
            );        
        
        if(reloadWorlds){
            Map<String, Boolean> oldWorlds = WORLDS;
            WORLDS = new LinkedHashMap<>();
            
            // Delete old worlds from list
            //  and add existing worlds to list
            serverWorlds.forEach(worldName -> {
                boolean state = oldWorlds.getOrDefault(worldName, true);
                WORLDS.put(worldName, state);
            });

            // Save worlds into config
            saveWorlds();
        }
    }
    
    static void saveWorlds(){
        List<String> savedWorlds = new ArrayList<>();
        WORLDS.forEach((worldName, enabled) -> savedWorlds.add(worldName + ";" + enabled));
        Collections.sort(savedWorlds);
        ConfigManager.getConfig().set("Worlds-list", savedWorlds);
        ConfigManager.saveConfig();
    }
    
    public static void setWorldEnabled(String worldName, boolean enabled){
        WORLDS.put(worldName, enabled);
        saveWorlds();
        reloadAll();
    }
    
    public static void setAllWorldsEnabled(boolean enabled){
        WORLDS.replaceAll((k, v) -> enabled);
        saveWorlds();
        reloadAll();
    }
    
    public static void toggleAllWorlds(){
        WORLDS.replaceAll((worldName, enabled) -> !enabled);
        saveWorlds();
        reloadAll();
    }
   
    public static void reloadAll(){
        String prefix = NewAmazingLuckyBlocks.getInstance().prefix;   

        String line1 = LangLoader.getMessages().getString("World-loading.line1").replaceAll("%prefix%", "");
        String line2 = LangLoader.getMessages().getString("World-loading.line2").replaceAll("%prefix%", "");
        String line3 = LangLoader.getMessages().getString("World-loading.line3").replaceAll("%prefix%", "");
        String line4 = LangLoader.getMessages().getString("World-loading.line4").replaceAll("%prefix%", "");
        String line5 = LangLoader.getMessages().getString("World-loading.line5").replaceAll("%prefix%", "");
        String line6 = LangLoader.getMessages().getString("World-loading.line6").replaceAll("%prefix%", "");
        String line7 = LangLoader.getMessages().getString("World-loading.line7").replaceAll("%prefix%", "");
        String line8 = LangLoader.getMessages().getString("World-loading.line8").replaceAll("%prefix%", "");
        
        // Update and load worlds
        updateWorlds(true);
        
        if(WORLDS.isEmpty()){
            Logger.log(line2);
            Logger.log(line3);
            //Logger.log(line4);
            Logger.log(line5);
            Logger.log("NewAmazingLuckyBlocks has loaded 0 worlds", LogLevel.WARN);
            Logger.log(line6);
        }else{
            Logger.log(line7 + " &d" + WORLDS.size() + " " + line8 + "&r");
        }
    }
    
    public static boolean isRegistered(String worldName){
        return WORLDS.getOrDefault(worldName, false);
    }
}