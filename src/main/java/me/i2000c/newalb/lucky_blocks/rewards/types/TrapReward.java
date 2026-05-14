package me.i2000c.newalb.lucky_blocks.rewards.types;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.lucky_blocks.TrapManager;
import me.i2000c.newalb.lucky_blocks.editors.menus.TrapMenu;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.OutcomePack;
import me.i2000c.newalb.lucky_blocks.rewards.PackManager;
import me.i2000c.newalb.lucky_blocks.rewards.Reward;
import me.i2000c.newalb.lucky_blocks.rewards.RewardType;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.misc.OtherUtils;
import me.i2000c.newalb.utils.misc.XMaterialUtils;

@Getter
@Setter
public class TrapReward extends Reward<TrapReward> {
    
    private XMaterial trapMaterial;
    private String trapName;
    
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Outcome trapOutcome;
    
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String trapOutcomePackName;
    
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private int trapOutcomeID;
    
    public TrapReward(Outcome outcome) {
        super(outcome);
        trapMaterial = XMaterial.OAK_PRESSURE_PLATE;
        trapName = null;
        
        trapOutcomePackName = null;
        trapOutcomeID = -1;
        trapOutcome = null;
    }
    
    public Outcome getTrapOutcome() {
        if(this.trapOutcome == null && this.trapOutcomePackName != null) {
            OutcomePack pack = PackManager.getPack(trapOutcomePackName);
            trapOutcome = pack.getOutcome(trapOutcomeID);
        }
        return this.trapOutcome;
    }
    public void setTrapOutcome(Outcome trapOutcome) {
        this.trapOutcome = trapOutcome;
        trapOutcomePackName = trapOutcome.getPack().getPackname();
        trapOutcomeID = trapOutcome.getID();
    }
    
    @Override
    public ItemStack getItemToDisplay() {
        ItemStackWrapper builder = ItemStackWrapper.newItem(trapMaterial);
        builder.setDisplayName("&5Trap");
        builder.addLoreLine("&3Name: &r" + trapName);
        builder.addLoreLine("&3Pressure Plate Material: &b" + builder.toString());
        builder.addLoreLine("&3Trap Outcome: &b" + this.trapOutcomePackName + "/" + this.trapOutcomeID);
        
        return builder.toItemStack();
    }
    
    private static List<XMaterial> TRAP_MATERIALS;
    public static List<XMaterial> getPressurePlateMaterials() {
        if(TRAP_MATERIALS == null) {
            TRAP_MATERIALS = Arrays.stream(XMaterial.VALUES)
                                    .filter(material -> 
                                        material.name().endsWith("_PLATE") || material.name().endsWith("_CHEST") || material == XMaterial.CHEST)
                                    .filter(material -> material != XMaterial.ENDER_CHEST)
                                    .filter(XMaterial::isSupported)
                                    .sorted(Comparator.comparing((XMaterial material) -> material.name().contains("PLATE"))
                                                      .thenComparing(XMaterial::name))
                                    .collect(Collectors.toList());
        }
        return TRAP_MATERIALS;
    }
    
    @Override
    public void saveRewardIntoConfig(Config config, String path) {
        if(trapOutcomePackName.endsWith(".yml")) {
            trapOutcomePackName = OtherUtils.removeExtension(trapOutcomePackName);
        }
        
        config.set(path + ".pressurePlateMaterial", trapMaterial.name());
        config.set(path + ".trapName", trapName);
        config.set(path + ".trapOutcome", trapOutcomePackName + "/" + trapOutcomeID);
    }
    
    @Override
    public void loadRewardFromConfig(Config config, String path) {
        String trapMaterialName = config.getString(path + ".trapMaterial", null);
        if(trapMaterialName == null) {
            trapMaterialName = config.getString(path + ".pressurePlateMaterial");
        }
        this.trapMaterial = XMaterialUtils.parseXMaterial(trapMaterialName);
        
        this.trapName = config.getString(path + ".trapName");
        String[] aux = config.getString(path + ".trapOutcome").split("\\/");
        this.trapOutcomePackName = aux[0];
        this.trapOutcomeID = Integer.parseInt(aux[1]);
        this.trapOutcome = null;
    }
    
    private ItemStack getItemToDrop() {
        ItemStackWrapper wrapper = ItemStackWrapper.newItem(trapMaterial)
                                                   .setDisplayName(trapName);
        
        TrapManager.encryptOutcome(trapOutcomePackName, trapOutcomeID, wrapper);
        return wrapper.toItemStack();
    }
    
    @Override
    public void execute(Player player, Location location) {
        location.getWorld().dropItemNaturally(location, getItemToDrop());
    }
    
    @Override
    public RewardType getRewardType() {
        return RewardType.trap;
    }
    
    @Override
    public EditorMenu<TrapReward> getEditor() {
        return new TrapMenu();
    }
    
    @Override
    public TrapReward clone() {
        TrapReward copy = (TrapReward) super.clone();
        return copy;
    }
}
