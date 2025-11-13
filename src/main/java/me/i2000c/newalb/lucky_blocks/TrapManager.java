package me.i2000c.newalb.lucky_blocks;

import com.cryptomorin.xseries.XMaterial;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.integration.WorldGuardManager;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.types.TrapReward;
import me.i2000c.newalb.utils.locations.WorldManager;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.misc.NBTUtils;
import me.i2000c.newalb.utils.tasks.Task;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TrapManager implements Listener {
    
    @Getter private static final TrapManager manager = new TrapManager();
    
    private static final String HIDDEN_TAG = "NewAmazingLuckyBlocks.TrapReward";
    
    private static final String CONFIG_FILENAME = "data/traps.yml";
    private static final String TRAPS_KEY = "Traps";
    private static final Map<Location, Trap> traps = new HashMap<>();
    
    private static Config config = new Config();    
    
    public static void loadTraps(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        config.loadConfig(CONFIG_FILENAME);
        
        traps.clear();        
        ConfigurationSection section = config.getConfigurationSection(TRAPS_KEY, null);
        if(section == null){
            return;
        }
        
        for(String key : section.getKeys(false)){
            String path = TRAPS_KEY + "." + key;
            
            //Load trap Location
            Location trapLocation = config.getLocation(path + ".location");
            if(trapLocation == null) {
                continue;
            }
            
            XMaterial xmaterial = XMaterial.matchXMaterial(trapLocation.getBlock().getType());
            if(!TrapReward.getPressurePlateMaterials().contains(xmaterial)){
                continue;
            }
            
            //Load trap ItemStack
            XMaterial material = config.getMaterial(path + ".item.material");
            String name = config.getString(path + ".item.name");
            
            Outcome outcome;
            try {
                outcome = Outcome.fromString(config.getString(path + ".trapOutcome"));
            } catch(Exception ex) {
                ex.printStackTrace();
                continue;
            }
            
            ItemStackWrapper wrapper = ItemStackWrapper.newItem(material)
                                                       .setDisplayName(name);
            encryptOutcome(outcome, wrapper);
            
            Trap trap = new Trap();
            trap.trapOutcome = outcome;
            trap.trapItemStack = wrapper;
            
            traps.put(trapLocation, trap);
        }
//</editor-fold>
    }
    
    public static void saveTraps(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        config.clearConfig();
        
        int i = 0;
        for(Map.Entry<Location, Trap> entry : traps.entrySet()){
            Location trapLocation = entry.getKey();
            Trap trap = entry.getValue();
            
            String path = TRAPS_KEY + "." + i;
            
            // Save Location
            config.set(path + ".location", trapLocation);
            
            // Save ItemStack
            config.set(path + ".item.material", trap.trapItemStack.getMaterial());
            String displayName = trap.trapItemStack.getDisplayName();
            String name = Logger.deColor(displayName);
            
            config.set(path + ".item.name", name);
            config.set(path + ".trapOutcome", decryptOutcome(trap.trapItemStack.toItemStack()).toString());
            
            i++;
        }
        
        config.saveConfig(CONFIG_FILENAME);
//</editor-fold>
    }
    
    @EventHandler(priority = EventPriority.LOW)
    private static void onTrapActivated(PlayerInteractEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!WorldManager.isEnabled(e.getPlayer().getWorld().getName())){
            return;
        }
        
        if(e.getAction() != Action.PHYSICAL && e.getAction() != Action.RIGHT_CLICK_BLOCK){
            return;
        }
        
        Location loc = e.getClickedBlock().getLocation();
        Trap trap = traps.get(loc);
        if(trap == null){
            return;
        }
        
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK && !trap.trapItemStack.getMaterial().name().contains("CHEST")){
            return;
        }
        
        if(!WorldGuardManager.canUse(e.getPlayer(), loc)) {
            return;
        }
        
        Task.runTask(() -> {
            e.getClickedBlock().setType(Material.AIR);
            traps.remove(loc);
            saveTraps();
            trap.trapOutcome.execute(e.getPlayer(), loc);
        }, 1L);
//</editor-fold>
    }
    
    @EventHandler(priority = EventPriority.LOW)
    private static void onTrapPlaced(BlockPlaceEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemStack stack = e.getItemInHand();
        if(stack == null){
            return;
        }
        
        Outcome outcome = decryptOutcome(stack);
        if(outcome != null){
            if(!WorldGuardManager.canBuild(e.getPlayer(), e.getBlock().getLocation())) {
                return;
            }
            
            //Add location to traps
            Trap trap = new Trap();
            trap.trapItemStack = ItemStackWrapper.fromItem(stack, true);
            trap.trapItemStack.setAmount(1);
            trap.trapOutcome = outcome;
            traps.put(e.getBlock().getLocation(), trap);
            saveTraps();
        }
//</editor-fold>
    }
    
    @EventHandler(priority = EventPriority.LOW)
    private static void onPressurePlateBroken(BlockBreakEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Location loc = e.getBlock().getLocation();
        Trap trap = traps.get(loc);
        if(trap != null){
            if(!WorldGuardManager.canBreak(e.getPlayer(), loc)) {
                return;
            }
            
            e.setCancelled(true);
            e.getBlock().setType(Material.AIR);
            traps.remove(loc);
            trap.trapItemStack.dropAtLocation(loc.add(0.5, 0, 0.5), false);
            saveTraps();
        }else if((trap = traps.get(loc.add(0, 1, 0))) != null){
            XMaterial material = trap.trapItemStack.getMaterial();
            if(!material.name().contains("CHEST")){
                if(!WorldGuardManager.canBreak(e.getPlayer(), loc)) {
                    return;
                }
                
                loc.getBlock().setType(Material.AIR);
                traps.remove(loc);
                trap.trapItemStack.dropAtLocation(loc.add(0.5, 0, 0.5), false);
                saveTraps();
            }
        }
//</editor-fold>
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private static void onBlockExplode(BlockExplodeEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        boolean changedTraps = false;
        for(Iterator<Block> iterator = e.blockList().iterator(); iterator.hasNext();) {
            Block block = iterator.next();
            Location loc = block.getLocation();
            Trap trap = traps.get(loc);
            if(trap != null){
                traps.remove(loc);
                iterator.remove();
                block.setType(Material.AIR);
                trap.trapItemStack.dropAtLocation(loc);
                changedTraps = true;
            }
        }
        if(changedTraps){
            saveTraps();
        }
//</editor-fold>
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private static void onEntityExplode(EntityExplodeEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        boolean changedTraps = false;
        for(Iterator<Block> iterator = e.blockList().iterator(); iterator.hasNext();) {
            Block block = iterator.next();
            Location loc = block.getLocation();
            Trap trap = traps.get(loc);
            if(trap != null){
                traps.remove(loc);
                iterator.remove();
                block.setType(Material.AIR);
                trap.trapItemStack.dropAtLocation(loc);
                changedTraps = true;
            }
        }
        if(changedTraps){
            saveTraps();
        }
//</editor-fold>
    }
    
    public static void encryptOutcome(Outcome outcome, ItemStackWrapper wrapper){
        //<editor-fold defaultstate="collapsed" desc="Code">
        wrapper.setNbtTag(HIDDEN_TAG, outcome.toString());
//</editor-fold>
    }
    public static void encryptOutcome(String packName, int outcomeID, ItemStackWrapper wrapper){
        //<editor-fold defaultstate="collapsed" desc="Code">
        wrapper.setNbtTag(HIDDEN_TAG, packName + "/" + outcomeID);
//</editor-fold>
    }
    public static Outcome decryptOutcome(ItemStack item){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!NBTUtils.contains(item, HIDDEN_TAG)){
            return null;
        }
        
        try{
            return Outcome.fromString(NBTUtils.getString(item, HIDDEN_TAG));
        }catch(Exception ex){
            return null;
        }
//</editor-fold>
    }
    
    private static class Trap {
        public Outcome trapOutcome;
        public ItemStackWrapper trapItemStack;
    }
}
