package me.i2000c.newalb.custom_outcomes.rewards;

import com.cryptomorin.xseries.XMaterial;
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
import java.util.Set;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import me.i2000c.newalb.utils2.OtherUtils;
import me.i2000c.newalb.utils2.RandomUtils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OutcomePack implements Displayable, Executable{
    private final Map<Integer, Outcome> outcomes;
    private final Config outcomeConfig;
    private File outcomeFile;
    private ItemStack icon;
    
    private int totalProbability;
    
    private final Set<LuckyBlockType> luckyBlockTypesToNotify;
    
    void addLuckyBlockTypeToNotify(LuckyBlockType type){
        //<editor-fold defaultstate="collapsed" desc="Code">
        luckyBlockTypesToNotify.add(type);
//</editor-fold>
    }
    void removeLuckyBlockTypeFromNotify(LuckyBlockType type){
        //<editor-fold defaultstate="collapsed" desc="Code">
        luckyBlockTypesToNotify.remove(type);
//</editor-fold>
    }
    
    public OutcomePack(File file){
        //<editor-fold defaultstate="collapsed" desc="Code">
        luckyBlockTypesToNotify = new HashSet<>();
        outcomeFile = file;
        outcomeConfig = new Config();
        outcomes = new HashMap<>();
        icon = XMaterial.CRAFTING_TABLE.parseItem();
        loadPack();
//</editor-fold>
    }
    
    public final void loadPack(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        outcomeConfig.loadConfig(outcomeFile);
        
        totalProbability = 0;
        luckyBlockTypesToNotify.clear();
        outcomes.clear();
        
        String packVersionName = outcomeConfig.getString("MinMinecraftVersion", null);
        MinecraftVersion packVersion;
        if(packVersionName == null){
            packVersion = MinecraftVersion.CURRENT_VERSION;
        } else {
            packVersion = MinecraftVersion.fromString(packVersionName);
        }
        
        MinecraftVersion currentVersion = MinecraftVersion.CURRENT_VERSION;
        if(currentVersion.isLessThan(packVersion)){
            Logger.warn("Pack \"" + getPackname() + "\" requires at least Minecraft " + packVersion);
            Logger.warn("However, you are using Minecraft " + currentVersion);
            Logger.warn("In order to avoid errors, the outcomes of this pack won't be loaded");
            return;
        }
        
        ConfigurationSection section = outcomeConfig.getConfigurationSection("Outcomes", null);
        if(section != null){
            List<String> keys = new ArrayList<>(section.getKeys(false));
            Collections.sort(keys, COMPARATOR);
            for(String key : keys){
                Outcome outcome = new Outcome(outcomeConfig, "Outcomes." + key, Integer.parseInt(key), this);
                totalProbability += outcome.getProbability();
                outcomes.put(outcome.getID(), outcome);
            }
        }
        
        XMaterial iconMaterial = outcomeConfig.getMaterial("Icon", null);
        if(iconMaterial != null){
            icon = iconMaterial.parseItem();
        }
        
        if(!outcomes.isEmpty()) {
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
//</editor-fold>
    }
    
    public String getPackname(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return OtherUtils.removeExtension(this.outcomeFile.getName());
//</editor-fold>
    }
    
    public String getFilename(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return this.outcomeFile.getName();
//</editor-fold>
    }
    
    public void setIcon(ItemStack icon){
        //<editor-fold defaultstate="collapsed" desc="Code">
        this.icon = new ItemStack(icon.getType());
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()){
            this.icon.setDurability(icon.getDurability());
        }
//</editor-fold>
    }
    public ItemStack getIcon(){
        return this.icon;
    }
    
    public Map<Integer, Outcome> getOutcomes(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return this.outcomes;
//</editor-fold>
    }
    
    public List<Outcome> getSortedOutcomes(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        List<Outcome> list = new ArrayList<>(this.outcomes.values());
        list.sort((outcome1, outcome2) -> outcome1.getID() - outcome2.getID());
        return list;
//</editor-fold>
    }
    
    public Outcome getOutcome(int i){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return outcomes.get(i);
//</editor-fold>
    }
    
    private static final Comparator<String> COMPARATOR = (String s1, String s2) -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        try{
            int value1 = Integer.parseInt(s1);
            int value2 = Integer.parseInt(s2);
            
            return value1 - value2;
        }catch(NumberFormatException ex){
            return 1;
        }
//</editor-fold>
    };
    
    public void addOutcome(Outcome outcome, boolean isNewOutcome){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(isNewOutcome || outcome.getID() == -1){
            outcome.setID(outcomes.size());
        }
        outcomes.put(outcome.getID(), outcome);
        saveOutcomes();
//</editor-fold>
    }
    
    public void removeOutcome(Outcome outcome){
        //<editor-fold defaultstate="collapsed" desc="Code">
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
//</editor-fold>
    }
    public void removeOutcome(int outcomeID){
        //<editor-fold defaultstate="collapsed" desc="Code">
        outcomes.remove(outcomeID);
//</editor-fold>
    }
    
    public void saveOutcomes(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        outcomeConfig.clearConfig();
        
        outcomeConfig.set("MinMinecraftVersion", MinecraftVersion.CURRENT_VERSION.toString());
        outcomeConfig.set("Icon", ItemStackWrapper.fromItem(icon, false).getMaterial());
        outcomes.values().stream()
                         .sorted((outcome1, outcome2) -> outcome1.getID() - outcome2.getID())
                         .forEachOrdered((outcome) -> outcome.saveOutcome(outcomeConfig, "Outcomes." + outcome.getID()));
        
        outcomeConfig.saveConfig(outcomeFile);
//</editor-fold>
    }
    
    public void renamePack(String newName){
        //<editor-fold defaultstate="collapsed" desc="Code">
        newName = OtherUtils.removeExtension(newName);
        File newFile = new File(outcomeFile.getParentFile(), newName + ".yml");
        outcomeFile.renameTo(newFile);
        outcomeFile = newFile;
        TypeManager.saveTypes();
//</editor-fold>
    }
    
    public Outcome getRandomOutcome() {
        //<editor-fold defaultstate="collapsed" desc="Code">
        int randomNumber = RandomUtils.getInt(totalProbability);
        for(int i=0; i<outcomes.size(); i++){
            Outcome outcome = outcomes.get(i);
            randomNumber = randomNumber - outcome.getProbability();
            if(randomNumber < 0){
                return outcome;
            }
        }
        
        return null;
//</editor-fold>
    }
    
    @Override
    public void execute(Player player, Location location){
        Outcome outcome = getRandomOutcome();
        if(outcome != null) {
            outcome.execute(player, location);
        }
    }
    
    public void delete(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        luckyBlockTypesToNotify.forEach(type -> type.removePack(this));
        TypeManager.saveTypes();
        outcomeFile.delete();
//</editor-fold>
    }
    
    public OutcomePack clonePack(String filename){
        //<editor-fold defaultstate="collapsed" desc="Code">
        filename = OtherUtils.removeExtension(filename);
        try{
            File newFile = new File(outcomeFile.getParentFile(), filename + ".yml");
            Files.copy(Paths.get(outcomeFile.getPath()), Paths.get(newFile.getPath()));
            OutcomePack newPack = new OutcomePack(newFile);
            return newPack;
        }catch(IOException ex){
            Logger.err("Couln't create file \"" + filename + "\"");
            Logger.err(ex);
            return null;
        }
//</editor-fold>
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return ItemStackWrapper.fromItem(icon)
                               .setDisplayName("&6" + getPackname())
                               .addLoreLine("&aOutcome number: &d" + getOutcomes().size())
                               .toItemStack();
//</editor-fold>
    }
}
