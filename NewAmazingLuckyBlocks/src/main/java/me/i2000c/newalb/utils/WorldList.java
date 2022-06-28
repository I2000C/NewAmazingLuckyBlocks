package me.i2000c.newalb.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.Logger.LogLevel;
import org.bukkit.Bukkit;

public class WorldList{    
    private static Map<String, Boolean> WORLDS = new HashMap<>();
    
    public static Map<String, Boolean> getWorlds(){
        return WORLDS;
    }
    static void setWorlds(Map<String, Boolean> worlds){
        WORLDS = worlds;
    }
    
    private static boolean needToUpdateWorlds(List<String> serverWorlds){
        boolean reloadWorlds = false;
        if(WORLDS.size() == serverWorlds.size()){
            int i=0;
            for(String worldName : WORLDS.keySet()){
                if(!serverWorlds.get(i).equals(worldName)){
                    reloadWorlds = true;
                    break;
                }
                i++;
            }
        }else{
            reloadWorlds = true;
        }
        return reloadWorlds;
    }
    
    public static void updateWorlds(boolean force){
        List<String> serverWorlds = new ArrayList<>();
        Bukkit.getWorlds().forEach((w) -> {
            serverWorlds.add(w.getName());
        });
        
        boolean reloadWorlds = needToUpdateWorlds(serverWorlds);
        if(!force && !reloadWorlds){
            return;
        }
        
        WORLDS.clear();
        ConfigManager.getConfig().getStringList("Worlds-list").forEach((world) -> {
            try{
                String[] data = world.split(";");
                if(data[1].equals("true")){
                    WORLDS.put(data[0], true);
                }else{
                    WORLDS.put(data[0], false);
                }
            }catch(Exception ex){
                Logger.log("Couldn't load world \"" + world + "\" (incorrect format)");
            }
        });
        
        
        if(reloadWorlds){
            // Delete old worlds from list
            WORLDS.keySet().retainAll(serverWorlds);

            // Add existing worlds to list
            serverWorlds.forEach(worldName -> WORLDS.putIfAbsent(worldName, true));

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