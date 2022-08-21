package me.i2000c.newalb.custom_outcomes.rewards;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.logger.LogLevel;
import me.i2000c.newalb.utils.logger.Logger;
import org.bukkit.command.CommandSender;

public class PackManager{
    private PackManager(){
    }
    
    static{
        packList = new LinkedHashMap<>();
    }
    
    private static final NewAmazingLuckyBlocks PLUGIN = NewAmazingLuckyBlocks.getInstance();
    public static final File OUTCOMES_FOLDER = new File(PLUGIN.getDataFolder(), "outcome_packs");
    
    private static final Map<String, OutcomePack> packList;
    
    public static void loadPacks(){
        if(!OUTCOMES_FOLDER.exists()){
            OUTCOMES_FOLDER.mkdirs();
            copyDefaultPacks();
        }
        
        packList.clear();
        for(File file : OUTCOMES_FOLDER.listFiles()){
            if(!file.getName().endsWith(".yml")){
                continue;
            }
            try{
                OutcomePack pack = new OutcomePack(file);
                packList.put(file.getName(), pack);
            }catch(Exception ex){
                Logger.log("An error occurred while loading pack: \"" + file.getName() + "\"", LogLevel.INFO);
                ex.printStackTrace();
            }
        }
    }
    
    public static OutcomePack getPack(String filename){
        return packList.get(filename);
    }
    
    public static List<OutcomePack> getPacks(){
        return new ArrayList(packList.values());
    }
    
    public static void addNewPack(OutcomePack pack, CommandSender sender){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(packList.containsKey(pack.getFilename())){
            Logger.sendMessage("&cPack &6\"" + pack.getFilename() + "\" &calready exists", sender);
        }else{
            packList.put(pack.getFilename(), pack);
        }
//</editor-fold>
    }
    public static void clonePack(String name, CommandSender sender){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String nameWithoutYml = name.split("\\.yml")[0];
        name = nameWithoutYml + ".yml";
        OutcomePack pack = getPack(name);
        if(pack == null){
            Logger.sendMessage("&cPack &6\"" + name + "\" &cdoesn't exist", sender);
        }else{
            boolean finish = false;
            int i = 1;
            String newName = "";
            while(!finish){
                newName = nameWithoutYml + "_" + i + ".yml";
                finish = true;
                for(String packName : packList.keySet()){
                    if(packName.equals(newName)){
                        finish = false;
                        break;
                    }
                }
                ++i;
            }
            OutcomePack newPack = pack.clonePack(newName);
            packList.put(newName, newPack);
            Logger.sendMessage("&aPack &6\"" + name + "\" &ahas been &3cloned", sender);
        }
//</editor-fold>
    }
    public static void renamePack(String oldName, String newName, CommandSender sender){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!newName.endsWith(".yml")){
            newName += ".yml";
        }
        OutcomePack pack = getPack(oldName);
        if(pack == null){
            Logger.sendMessage("&cPack &6\"" + oldName + "\" &cdoesn't exist", sender);
        }else if(getPack(newName) != null){
            Logger.sendMessage("&cPack &6\"" + newName + "\" &calready exists", sender);
        }else{
            packList.remove(oldName);
            pack.renamePack(newName);
            packList.put(newName, pack);
            Logger.sendMessage("&aPack &6\"" + oldName + "\" &ahas been renamed to &b\"" + newName + "\"", sender);
        }
//</editor-fold>
    }    
    public static void removePack(String name, CommandSender sender){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!name.endsWith(".yml")){
            name += ".yml";
        }
        OutcomePack pack = getPack(name);
        if(pack == null){
            Logger.sendMessage("&cPack &6\"" + name + "\" &cdoesn't exist", sender);
        }else{
            packList.remove(name);
            pack.delete();
            Logger.sendMessage("&aPack &6\"" + name + "\" &ahas been &4deleted", sender);
        }
//</editor-fold>
    }
    
    
    private static void copyDefaultPacks(){
        File examplePackFile = new File(OUTCOMES_FOLDER, "example_pack.yml");
        NewAmazingLuckyBlocks.getInstance().copyResource("example_pack.yml", examplePackFile);
        
        File defaultPackFile = new File(OUTCOMES_FOLDER, "default_pack.yml");
        NewAmazingLuckyBlocks.getInstance().copyResource("default_pack.yml", defaultPackFile);
    }
}
