package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import com.cryptomorin.xseries.XMaterial;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import me.i2000c.newalb.config.ReadWriteConfig;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.WorldConfig;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.Task;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
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
import org.bukkit.plugin.Plugin;

public class TrapManager extends ReadWriteConfig implements Listener{
    private TrapManager(Plugin plugin){
        super(plugin, null, "data/traps.yml", false);
    }
    
    private static TrapManager trapManager;
    public static TrapManager getManager(){
        return trapManager;
    }
    public static void initialize(Plugin plugin){
        trapManager = new TrapManager(plugin);
    }
    
    private static class Trap{
        public Outcome trapOutcome;
        public ItemStack trapItemStack;
    }
    
    private static final Map<Location, Trap> traps = new HashMap<>();
    
    public static void loadTraps(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        traps.clear();
        
        trapManager.loadConfig();
        FileConfiguration config = trapManager.getBukkitConfig();
        if(!config.isConfigurationSection("Traps")){
            return;
        }
        
        for(String key : config.getConfigurationSection("Traps").getKeys(false)){
            String path = "Traps." + key;
            //Load trap Location
            World world = Bukkit.getWorld(config.getString(path + ".location.world"));
            if(world == null){
                continue;
            }
            
            int x = config.getInt(path + ".location.x");
            int y = config.getInt(path + ".location.y");
            int z = config.getInt(path + ".location.z");
            Location trapLocation = new Location(world, x, y, z);
            
            XMaterial xmaterial = XMaterial.matchXMaterial(trapLocation.getBlock().getType());
            if(!TrapReward.getPressurePlateMaterials().contains(xmaterial)){
                continue;
            }
            
            //Load trap ItemStack
            String materialName = config.getString(path + ".item.material");
            String name = config.getString(path + ".item.name");
            
            Outcome outcome;
            try {
                outcome = Outcome.fromString(config.getString(path + ".trapOutcome"));
            } catch(Exception ex) {
                ex.printStackTrace();
                continue;
            }
            
            Material stackMaterial = Material.valueOf(materialName);
            ItemStack stack = ItemBuilder
                    .newItem(XMaterial.matchXMaterial(stackMaterial))
                    .withDisplayName(name)
                    .build();
            TrapReward.encryptOutcome(outcome, stack);
            
            Trap trap = new Trap();
            trap.trapOutcome = outcome;
            trap.trapItemStack = stack;
            
            traps.put(trapLocation, trap);
        }
//</editor-fold>
    }
    
    public static void saveTraps(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        trapManager.clearConfig();
        FileConfiguration config = trapManager.getBukkitConfig();
        
        int i = 0;
        for(Map.Entry<Location, Trap> entry : traps.entrySet()){
            Location trapLocation = entry.getKey();
            Trap trap = entry.getValue();
            
            String path = "Traps." + i;
            //save Location
            config.set(path + ".location.world", trapLocation.getWorld().getName());
            config.set(path + ".location.x", trapLocation.getBlockX());
            config.set(path + ".location.y", trapLocation.getBlockY());
            config.set(path + ".location.z", trapLocation.getBlockZ());
            
            //save ItemStack
            config.set(path + ".item.material", trap.trapItemStack.getType().name());
            String displayName = ItemBuilder.fromItem(trap.trapItemStack, false)
                    .getDisplayName();
            String name = Logger.deColor(displayName);
            
            config.set(path + ".item.name", name);
            config.set(path + ".trapOutcome", TrapReward.decryptOutcome(trap.trapItemStack) + "");
            
            i++;
        }
        
        trapManager.saveConfig();
//</editor-fold>
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private static void onTrapActivated(PlayerInteractEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!WorldConfig.isEnabled(e.getPlayer().getWorld().getName())){
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
        
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK
                && !trap.trapItemStack.getType().name().contains("CHEST")){
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
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private static void onTrapPlaced(BlockPlaceEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemStack stack = e.getItemInHand();
        if(stack == null){
            return;
        }
        
        Outcome outcome = TrapReward.decryptOutcome(stack);
        if(outcome != null){
            //Add location to traps
            Trap trap = new Trap();
            trap.trapItemStack = stack.clone();
            trap.trapItemStack.setAmount(1);
            trap.trapOutcome = outcome;
            traps.put(e.getBlock().getLocation(), trap);
            saveTraps();
        }
//</editor-fold>
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private static void onPressurePlateBroken(BlockBreakEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Location loc = e.getBlock().getLocation();
        Trap trap = traps.get(loc);
        if(trap != null){
            e.setCancelled(true);
            e.getBlock().setType(Material.AIR);
            traps.remove(loc);
            loc.getWorld().dropItemNaturally(loc, trap.trapItemStack.clone());
            saveTraps();
        }else if((trap = traps.get(loc.add(0, 1, 0))) != null){
            Material material = trap.trapItemStack.getType();
            if(!material.name().contains("CHEST")){
                loc.getBlock().setType(Material.AIR);
                traps.remove(loc);
                loc.getWorld().dropItemNaturally(loc, trap.trapItemStack.clone());
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
                loc.getWorld().dropItemNaturally(loc, trap.trapItemStack.clone());
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
                loc.getWorld().dropItemNaturally(loc, trap.trapItemStack.clone());
                changedTraps = true;
            }
        }
        if(changedTraps){
            saveTraps();
        }
//</editor-fold>
    }
}
