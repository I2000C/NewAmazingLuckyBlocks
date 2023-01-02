package me.i2000c.newalb.utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.config.ReadWriteConfig;
import org.bukkit.plugin.Plugin;

public class WorldConfig extends ReadWriteConfig{
    private static final String WORLD_LIST_KEY = "Worlds-list";
    private WorldConfig(Plugin plugin){
        super(plugin, null, "data/worlds.yml", false);
    }
    
    private List<String> loadWorlds(){
        loadConfig();
        return getBukkitConfig().getStringList(WORLD_LIST_KEY);
    }
    private void saveWorlds(List<String> worlds){
        getBukkitConfig().set(WORLD_LIST_KEY, worlds);
        saveConfig();
    }
    
    private static WorldConfig worldConfig;
    private static HashSet<String> worlds;
    private static TreeSet<String> sortedWorlds;
    private static WorldListType worldListType;
    public static void initialize(Plugin plugin){
        worldConfig = new WorldConfig(plugin);
        worlds = new HashSet<>();
        sortedWorlds = new TreeSet<>();
    }
    
    public static Set<String> getWorlds(){
        return worlds;
    }
    
    /*public static void updateWorlds(boolean force){
        //<editor-fold defaultstate="collapsed" desc="Code">
        List<String> serverWorlds = Bukkit.getWorlds()
                .stream()
                .sorted((world1, world2) -> world1.getName().compareTo(world2.getName()))
                .map(world -> world.getName())
                .collect(Collectors.toList());
        
        boolean reloadWorlds = needToUpdateWorlds(serverWorlds);
        if(!force && !reloadWorlds){
            return;
        }
        
        worlds.clear();
        worldConfig.loadWorlds()
                .forEach(worldName -> {
                    try{
                        String[] splitted = worldName.split(";");
                        if(splitted[1].equals("true")){
                            worlds.put(splitted[0], true);
                        }else{
                            worlds.put(splitted[0], false);
                        }
                    }catch(Exception ex){
                        Logger.warn("Couldn't load world \"" + worldName + "\" (incorrect format)");
                    }
                }
                );
        
        if(reloadWorlds){
            Map<String, Boolean> oldWorlds = worlds;
            worlds = new LinkedHashMap<>();
            
            // Delete old worlds from list
            //  and add existing worlds to list
            serverWorlds.forEach(worldName -> {
                boolean state = oldWorlds.getOrDefault(worldName, true);
                worlds.put(worldName, state);
            });
            
            // Save worlds into config
            saveWorlds();
        }
//</editor-fold>
    }*/
    
    static void saveWorlds(){
        List<String> worldsList = new LinkedList<>(sortedWorlds);
        worldConfig.saveWorlds(worldsList);
    }
    
    public static boolean addWorld(String worldName) {
        if(worlds.add(worldName)) {
            saveWorlds();        
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean deleteWorld(String worldName) {
        if(worlds.remove(worldName)) {
            saveWorlds();
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean isEnabled(String worldName){
        switch(worldListType) {
            case DISABLED: return true;
            case WHITELIST: return worlds.contains(worldName);
            case BLACKLIST: return !worlds.contains(worldName);
            default: return false;
        }
    }
   
    public static void reloadAll(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String prefix = NewAmazingLuckyBlocks.getInstance().prefix;
        
        String line1 = LangConfig.getMessage("World-loading.line1").replace("%prefix%", "");
        String line2 = LangConfig.getMessage("World-loading.line2").replace("%prefix%", "");
        String line3 = LangConfig.getMessage("World-loading.line3").replace("%prefix%", "");
        String line4 = LangConfig.getMessage("World-loading.line4").replace("%prefix%", "");
        String line5 = LangConfig.getMessage("World-loading.line5").replace("%prefix%", "");
        String line6 = LangConfig.getMessage("World-loading.line6").replace("%prefix%", "");
        String line7 = LangConfig.getMessage("World-loading.line7").replace("%prefix%", "");
        String line8 = LangConfig.getMessage("World-loading.line8").replace("%prefix%", "");
        
        // Update and load worlds
        updateWorlds(true);
        
        if(worlds.isEmpty()){
            Logger.log(line2);
            Logger.log(line3);
            //Logger.log(line4);
            Logger.log(line5);
            Logger.warn("NewAmazingLuckyBlocks has loaded 0 worlds");
            Logger.log(line6);
        }else{
            Logger.log(line7 + " &d" + worlds.size() + " " + line8 + "&r");
        }
//</editor-fold>
    }
    
    public static enum WorldListType {
        DISABLED,
        WHITELIST,
        BLACKLIST;
    }
}