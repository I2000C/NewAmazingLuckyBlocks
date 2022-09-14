package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import com.cryptomorin.xseries.XMaterial;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import java.util.ArrayList;
import java.util.List;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.OutcomePack;
import me.i2000c.newalb.custom_outcomes.rewards.PackManager;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.RewardType;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.OtherUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TrapReward extends Reward{
    public static final String HIDDEN_TAG = "NewAmazingLuckyBlocks.TrapReward";
    
    private XMaterial trapMaterial;
    private String trapName;
    
    private Outcome trapOutcome;
    private String trapOutcomePackName;
    private int trapOutcomeID;
    
    public TrapReward(Outcome outcome){
        super(outcome);
        trapMaterial = XMaterial.OAK_PRESSURE_PLATE;
        trapName = null;
        
        trapOutcomePackName = null;
        trapOutcomeID = -1;
        trapOutcome = null;
    }
    
    public XMaterial getTrapMaterial(){
        return this.trapMaterial;
    }
    public void setTrapMaterial(XMaterial xmaterial){
        this.trapMaterial = xmaterial;
    }
    public String getTrapName(){
        return this.trapName;
    }
    public void setTrapName(String trapName){
        this.trapName = trapName;
    }
    public Outcome getTrapOutcome(){
        if(this.trapOutcome == null && this.trapOutcomePackName != null){
            OutcomePack pack = PackManager.getPack(trapOutcomePackName);
            trapOutcome = pack.getOutcome(trapOutcomeID);
        }
        return this.trapOutcome;
    }
    public void setTrapOutcome(Outcome trapOutcome){
        this.trapOutcome = trapOutcome;
        trapOutcomePackName = trapOutcome.getPack().getPackname();
        trapOutcomeID = trapOutcome.getID();
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        ItemBuilder builder = ItemBuilder.newItem(trapMaterial);
        builder.withDisplayName("&5Trap");
        builder.addLoreLine("&3Name: &r" + trapName);
        builder.addLoreLine("&3Pressure Plate Material: &b" + builder.toString());
        builder.addLoreLine("&3Trap Outcome: &b" + this.trapOutcomePackName + "/" + this.trapOutcomeID);
        
        return builder.build();
    }
    
    private static List<XMaterial> TRAP_MATERIALS;
    public static List<XMaterial> getPressurePlateMaterials(){
        if(TRAP_MATERIALS == null){
            TRAP_MATERIALS = new ArrayList<>();
            for(Material material : Material.values()){
                if(material.isBlock() && material.name().contains("PLATE")){
                    TRAP_MATERIALS.add(XMaterial.matchXMaterial(material));
                }
            }
            TRAP_MATERIALS.add(XMaterial.CHEST);
            TRAP_MATERIALS.add(XMaterial.TRAPPED_CHEST);
        }
        return TRAP_MATERIALS;
    }
    
    @Override
    public void saveRewardIntoConfig(FileConfiguration config, String path){
        if(trapOutcomePackName.endsWith(".yml")){
            trapOutcomePackName = OtherUtils.removeExtension(trapOutcomePackName);
        }
        
        config.set(path + ".pressurePlateMaterial", trapMaterial.name());
        config.set(path + ".trapName", trapName);
        config.set(path + ".trapOutcome", trapOutcomePackName + "/" + trapOutcomeID);
    }
    
    @Override
    public void loadRewardFromConfig(FileConfiguration config, String path){
        String trapMaterialName = config.getString(path + ".trapMaterial");
        if(trapMaterialName == null){
            trapMaterialName = config.getString(path + ".pressurePlateMaterial");
        }
        this.trapMaterial = ItemBuilder.newItem(trapMaterialName).getMaterial();
        
        this.trapName = config.getString(path + ".trapName");
        String[] aux = config.getString(path + ".trapOutcome").split("\\/");
        this.trapOutcomePackName = aux[0];
        this.trapOutcomeID = Integer.parseInt(aux[1]);
        this.trapOutcome = null;
    }
    
    private ItemStack getItemToDrop(){
        ItemStack stack = ItemBuilder
                .newItem(trapMaterial)
                .withDisplayName(trapName)
                .build();
        
        return encryptOutcome(trapOutcomePackName, trapOutcomeID, stack);
    }
    
    static ItemStack encryptOutcome(Outcome outcome, ItemStack item){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return NBTEditor.set(item, outcome.toString(), HIDDEN_TAG);
//</editor-fold>
    }
    static ItemStack encryptOutcome(String packName, int outcomeID, ItemStack item){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return NBTEditor.set(item, packName + "/" + outcomeID, HIDDEN_TAG);
//</editor-fold>
    }
    static Outcome decryptOutcome(ItemStack item){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!NBTEditor.contains(item, HIDDEN_TAG)){
            return null;
        }
        
        try{
            return Outcome.fromString(NBTEditor.getString(item, HIDDEN_TAG));
        }catch(Exception ex){
            return null;
        }
//</editor-fold>
    }
    
    @Override
    public void execute(Player player, Location location){
        location.getWorld().dropItemNaturally(location, getItemToDrop());
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.trap;
    }
    
    @Override
    public Reward clone(){
        TrapReward copy = (TrapReward) super.clone();
        return copy;
    }
}
