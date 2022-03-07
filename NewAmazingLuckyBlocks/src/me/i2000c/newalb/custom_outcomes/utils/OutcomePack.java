package me.i2000c.newalb.custom_outcomes.utils;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import me.i2000c.newalb.utils2.YamlConfigurationUTF8;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class OutcomePack implements Displayable{
    private Map<Integer, Outcome> outcomes;
    private FileConfiguration outcomeConfig;
    private File outcomeFile;
    
    private int totalProbability;
    
    private final Set<LuckyBlockType> luckyBlockTypesToNotify;
    
    void addLuckyBlockTypeToNotify(LuckyBlockType type){
        luckyBlockTypesToNotify.add(type);
    }
    void removeLuckyBlockTypeFromNotify(LuckyBlockType type){
        luckyBlockTypesToNotify.remove(type);
    }
    
    public OutcomePack(File file){
        luckyBlockTypesToNotify = new HashSet<>();
        outcomeFile = file;
        outcomes = new HashMap<>();
        loadPack();        
    }
    
    public final void loadPack(){
        totalProbability = 0;
        luckyBlockTypesToNotify.clear();
        
        if(!outcomeFile.exists()){
            try{
                outcomeFile.createNewFile();
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
        
        outcomeConfig = YamlConfigurationUTF8.loadConfiguration(outcomeFile);
        outcomes.clear();
        if(outcomeConfig.isConfigurationSection("Outcomes")){
            List<String> keys = new ArrayList(outcomeConfig.getConfigurationSection("Outcomes").getKeys(false));
            Collections.sort(keys, COMPARATOR);
            for(String key : keys){
                Outcome outcome = new Outcome(outcomeConfig, "Outcomes." + key, Integer.parseInt(key), this);
                totalProbability += outcome.getProbability();
                outcomes.put(outcome.getID(), outcome);
            }
        }
    }
    
    private void saveConfig() throws IOException{
        this.outcomeConfig.save(outcomeFile);
    }
    
    public String getFilename(){
        return this.outcomeFile.getName();
    }
    
    public Map<Integer, Outcome> getOutcomes(){
        return this.outcomes;
    }
    
    public List<Outcome> getSortedOutcomes(){
        List<Outcome> list = new ArrayList<>(this.outcomes.values());
        list.sort((outcome1, outcome2) -> outcome1.getID() - outcome2.getID());
        return list;
    }
    
    public Outcome getOutcome(int i){
        return outcomes.get(i);
    }
    
    private static final Comparator<String> COMPARATOR = (String s1, String s2) -> {
        try{
            int value1 = Integer.parseInt(s1);
            int value2 = Integer.parseInt(s2);
            
            return value1 - value2;
        }catch(NumberFormatException ex){
            return 1;
        }
    };
    
    public void addOutcome(Outcome outcome, boolean isNewOutcome){
        if(isNewOutcome || outcome.getID() == -1){
            outcome.setID(outcomes.size());
        }
        outcomes.put(outcome.getID(), outcome);
        saveOutcomes();
    }
    
    public void removeOutcome(Outcome outcome){
        if(!outcomes.containsKey(outcome.getID())){
            return;
        }
        
        outcomes.remove(outcome.getID());
        totalProbability = totalProbability - outcome.getProbability();
        
        if(outcomes.isEmpty()){
            return;
        }
        
        //List with all outcome IDs
        List<Integer> keyList = new ArrayList<>(outcomes.keySet());
        Collections.sort(keyList);
        int max = keyList.get(keyList.size() - 1);
        int nextCorrectID = 0;
        
        //Check if all outcomes' IDs are 1, 2, 3, etc and change the incorrect IDs
        for(int i=0;i<=max;i++){
            Outcome aux = outcomes.get(i);
            if(aux != null){
                if(aux.getID() != nextCorrectID){
                    int previousID = aux.getID();
                    aux.setID(nextCorrectID);
                    outcomes.remove(previousID);
                    outcomes.put(nextCorrectID, aux);
                }
                nextCorrectID++;
            }
        }
    }
    public void removeOutcome(int outcomeID){
        outcomes.remove(outcomeID);
    }
    
    public void saveOutcomes(){
        outcomeConfig.set("Outcomes", null);
        outcomes.values().stream()
                .sorted((outcome1, outcome2) -> outcome1.getID() - outcome2.getID())
                .forEachOrdered((outcome) -> outcome.saveOutcome(outcomeConfig, "Outcomes." + outcome.getID())
        );
        try{
            saveConfig();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    
    public void renamePack(String newName){
        if(!newName.endsWith(".yml")){
            newName += ".yml";
        }
        File newFile = new File(outcomeFile.getParentFile(), newName);
        outcomeFile.renameTo(newFile);
        outcomeFile = newFile;
        TypeManager.saveTypes();
    }
    
    public void executeRandomOutcome(Player player, Location location){
        Random r = new Random();
        int randomNumber = r.nextInt(totalProbability);
        for(int i=0;i<outcomes.size();i++){
            Outcome outcome = outcomes.get(i);
            randomNumber = randomNumber - outcome.getProbability();
            if(randomNumber < 0){
                outcome.execute(player, location);
                break;
            }
        }
    }
    
    public void delete(){
        luckyBlockTypesToNotify.forEach(type -> type.removePack(this));
        TypeManager.saveTypes();
        outcomeFile.delete();
    }
    
    public OutcomePack clonePack(String filename){
        if(!filename.endsWith(".yml")){
            filename += ".yml";
        }
        try{            
            File newFile = new File(outcomeFile.getParentFile(), filename);
            Files.copy(Paths.get(outcomeFile.getPath()), Paths.get(newFile.getPath()));
            OutcomePack newPack = new OutcomePack(newFile);
            return newPack;
        }catch(IOException ex){
            Logger.log("Couln't create file \"" + filename + "\"", Logger.LogLevel.ERROR);
            Logger.log(ex, Logger.LogLevel.ERROR);
            return null;
        }        
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        ItemStack sk = XMaterial.CRAFTING_TABLE.parseItem();
        ItemMeta meta = sk.getItemMeta();
        meta.setDisplayName(Logger.color("&6" + getFilename()));
        List<String> lore = new ArrayList<>();
        lore.add(Logger.color("&aOutcome number: &d" + getOutcomes().size()));
        meta.setLore(lore);
        sk.setItemMeta(meta);
        
        return sk;
    }
}