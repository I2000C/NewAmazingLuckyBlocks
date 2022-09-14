package me.i2000c.newalb.custom_outcomes.rewards;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.OtherUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

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
                packList.put(pack.getPackname(), pack);
            }catch(Exception ex){
                Logger.err("An error occurred while loading pack: \"" + file.getName() + "\"");
                ex.printStackTrace();
            }
        }
    }
    
    public static OutcomePack getPack(String filename){
        if(filename.endsWith(".yml")){
            filename = OtherUtils.removeExtension(filename);
        }
        return packList.get(filename);
    }
    
    public static List<OutcomePack> getPacks(){
        return new ArrayList(packList.values());
    }
    public static List<OutcomePack> getSortedPacks(){
        List<OutcomePack> list = PackManager.getPacks();
        list.sort((OutcomePack pack1, OutcomePack pack2) -> pack1.getPackname().compareTo(pack2.getPackname()));
        return list;
    }
    
    public static void addNewPack(OutcomePack pack, CommandSender sender){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(packList.containsKey(pack.getPackname())){
            Logger.sendMessage("&cPack &6\"" + pack.getPackname() + "\" &calready exists", sender);
        }else{
            packList.put(pack.getPackname(), pack);
        }
//</editor-fold>
    }
    public static void clonePack(String name, CommandSender sender){
        //<editor-fold defaultstate="collapsed" desc="Code">
        name = OtherUtils.removeExtension(name);
        OutcomePack pack = getPack(name);
        if(pack == null){
            Logger.sendMessage("&cPack &6\"" + name + "\" &cdoesn't exist", sender);
        }else{
            String newName = "";
            for(int i=1; i<Integer.MAX_VALUE; i++){
                newName = name + "_" + i + ".yml";
                File file = new File(OUTCOMES_FOLDER, newName);
                if(!file.exists()){
                    break;
                }                    
            }
            OutcomePack newPack = pack.clonePack(newName);
            packList.put(OtherUtils.removeExtension(newName), newPack);
            Logger.sendMessage("&aPack &6\"" + name + "\" &ahas been &3cloned", sender);
        }
//</editor-fold>
    }
    public static void renamePack(String oldName, String newName, CommandSender sender){
        //<editor-fold defaultstate="collapsed" desc="Code">
        newName = OtherUtils.removeExtension(newName);
        OutcomePack pack = getPack(OtherUtils.removeExtension(oldName));
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
    public static void changePackIcon(String name, ItemStack icon, CommandSender sender){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Material material = icon.getType();
        if(material.name().contains("POTION")){
            material = Material.POTION;
        }
        
        ItemStack newIcon = new ItemStack(material);
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()
                && material != Material.POTION){
            newIcon.setDurability(icon.getDurability());
        }
        
        OutcomePack pack = getPack(OtherUtils.removeExtension(name));
        pack.setIcon(newIcon);
        String iconString = ItemBuilder.fromItem(newIcon, false).toString();
        pack.saveOutcomes();
        Logger.sendMessage("&aIcon of pack &6\"" + name + "\" &ahas been changed to &b" + iconString, sender);
//</editor-fold>
    }
    public static void removePack(String name, CommandSender sender){
        //<editor-fold defaultstate="collapsed" desc="Code">
        name = OtherUtils.removeExtension(name);
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
        NewAmazingLuckyBlocks.getInstance().copyResource("outcome_packs/example_pack.yml", examplePackFile);
        
        File defaultPackFile = new File(OUTCOMES_FOLDER, "default_pack.yml");
        NewAmazingLuckyBlocks.getInstance().copyResource("outcome_packs/default_pack.yml", defaultPackFile);
    }
}
