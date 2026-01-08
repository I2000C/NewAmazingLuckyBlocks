package me.i2000c.newalb.listeners.blocks;

import com.cryptomorin.xseries.XMaterial;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.i2000c.newalb.api.version.MinecraftVersion;
import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.integration.WorldGuardManager;
import me.i2000c.newalb.lucky_blocks.editors.menus.RewardListMenu;
import me.i2000c.newalb.lucky_blocks.rewards.Executable;
import me.i2000c.newalb.lucky_blocks.rewards.LuckyBlockType;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.OutcomePack;
import me.i2000c.newalb.lucky_blocks.rewards.PackManager;
import me.i2000c.newalb.lucky_blocks.rewards.TypeManager;
import me.i2000c.newalb.utils.locations.LocationManager;
import me.i2000c.newalb.utils.locations.WorldManager;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.XMaterialUtils;
import me.i2000c.newalb.utils.random.RandomUtils;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener implements Listener{
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e){
        Block b = e.getBlock();
        Location loc = b.getLocation().add(0.5, 0, 0.5);
        Player p = e.getPlayer();
        World w = p.getWorld();
        
        if(!WorldManager.isEnabled(w.getName())) {
            return;
        }
        
        if(!WorldGuardManager.canBreak(p, loc)) {
            return;
        }
        
        Executable exec = RewardListMenu.testRewardsPlayerList.get(p);
        if(exec != null){
        	LocationManager.removeLocation(loc);
            b.setType(Material.AIR);
            exec.execute(p, loc);
            return;
        }
        
        TypeManager.Result result = TypeManager.canBreakBlock(p, loc);
        switch(result.resultCode){
            case TypeManager.RESULT_NOT_LUCKYBLOCK:
                dropLuckyBlock(e);
                break;
            case TypeManager.RESULT_NO_GLOBAL_PERMISSION:
            case TypeManager.RESULT_NO_LOCAL_PERMISSION:
                Logger.sendMessage(ConfigManager.getLangMessage("NoPermission"), p);
                e.setCancelled(true);
                break;
            case TypeManager.RESULT_OK:
                if(PackManager.IS_LOADING_PACKS()) {
                    Logger.sendMessage(ConfigManager.getLangMessage("Loading.not-fully-loaded"), p);
                    e.setCancelled(true);
                    break;
                }
                
                boolean requireLuckyTool = ConfigManager.getMainConfig().getBoolean("Objects.LuckyTool.enable");
                boolean canOnlyBreakWithLuckyTool = ConfigManager.getMainConfig().getBoolean("Objects.LuckyTool.onlyCanBreakLuckyBlocksWithLuckyTool");
                if(requireLuckyTool && canOnlyBreakWithLuckyTool){
                    Logger.sendMessage(ConfigManager.getLangMessage("Objects.LuckyTool.need"), p);
                    e.setCancelled(true);
                }else{
                    b.setType(Material.AIR);
                    
                    boolean debugMode = ConfigManager.getMainConfig().getBoolean("LuckyBlock.DebugMode");
                    if(debugMode) {
                        OutcomePack randomPack = result.resultType.getRandomPack();
                        Outcome randomOutcome = randomPack.getRandomOutcome();
                        
                        String playerName = p.getName();
                        Location pLoc = p.getLocation();
                        String pWorldName = pLoc.getWorld().getName();
                        int x = pLoc.getBlockX();
                        int y = pLoc.getBlockY();
                        int z = pLoc.getBlockZ();
                        Location bLoc = b.getLocation();
                        String bWorldName = bLoc.getWorld().getName();
                        int bx = bLoc.getBlockX();
                        int by = bLoc.getBlockY();
                        int bz = bLoc.getBlockZ();
                        String typeName = result.resultType.getTypeName();
                        String outcomePackName = randomPack.getFilename();
                        int outcomeID = randomOutcome.getID();
                        String outcomeName = randomOutcome.getName();
                        
                        String message = String.format("Player %s ('%s' %d %d %d) broke a LuckyBlock at ('%s' %d %d %d) of type '%s'. Selected outcome pack: %s, selected outcome ID: %d, selected outcome name: %s",
                                                        playerName, pWorldName, x, y, z, bWorldName, bx, by, bz, typeName, outcomePackName, outcomeID, outcomeName);
                        Logger.log(message);
                        
                        randomOutcome.execute(p, loc);
                    } else {
                        result.resultType.execute(p, loc);
                    }
                }
                break;
        }
    }
    
    private static final String DEFAULT_MATERIAL = "DEFAULT";
    private void dropLuckyBlock(BlockBreakEvent e){
        boolean drop = ConfigManager.getMainConfig().getBoolean("LuckyBlock.DropOnBlockBreak.enable");
        if(!drop){
            return;
        }
        
        boolean survivalOnly = ConfigManager.getMainConfig().getBoolean("LuckyBlock.DropOnBlockBreak.survivalOnly");
        if(survivalOnly && e.getPlayer().getGameMode() != GameMode.SURVIVAL){
            return;
        }
        
        boolean disableWithSilkTouch = ConfigManager.getMainConfig().getBoolean("LuckyBlock.DropOnBlockBreak.disableWithSilkTouch");
        if(disableWithSilkTouch) {
            ItemStack item = e.getPlayer().getItemInHand();
            if(item != null && item.getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0) {
                return;
            }
        }        
        
        Map<String, Integer> defaultProbabilities = new HashMap<>();
        Map<XMaterial, Map<String, Integer>> materialProbabilities = new EnumMap<>(XMaterial.class);
        
        if(ConfigManager.getMainConfig().getBukkitConfig().isList("LuckyBlock.DropOnBlockBreak.enabledBlocks")) {
            Logger.warn("Invalid values detected inside \"LuckyBlock.DropOnBlockBreak.enabledBlocks\" config section");
            Logger.warn("Expected: map, Found: list");
            Logger.warn("Delete current config file and reload the server to fix this issue");
            return;
        }
        
        ConfigurationSection cs = ConfigManager.getMainConfig().getConfigurationSection("LuckyBlock.DropOnBlockBreak.enabledBlocks");
        for(String key : cs.getKeys(false)) {
            ConfigurationSection subSection = cs.getConfigurationSection(key);
            for(String typeName : subSection.getKeys(false)) {
                LuckyBlockType type = TypeManager.getType(typeName);
                int probability = subSection.getInt(typeName);
                if(type == null) {
                    Logger.warn(String.format("LuckyBlock type with name \"%s\" doesn't exist", typeName));
                    continue;
                }
                
                if(key.equals(DEFAULT_MATERIAL)) {
                    defaultProbabilities.put(typeName, probability);
                } else {
                    XMaterial material = XMaterialUtils.parseXMaterial(key);
                    Map<String, Integer> probabilityMap = materialProbabilities.getOrDefault(material, new HashMap<>());
                    probabilityMap.put(typeName, probability);
                    materialProbabilities.put(material, probabilityMap);
                }
            }
        }
        
        XMaterial blockMaterial = MinecraftVersion.CURRENT_VERSION.isLegacyVersion()
                ? XMaterial.matchXMaterial(new ItemStack(e.getBlock().getType(), 1, e.getBlock().getData()))
                : XMaterial.matchXMaterial(e.getBlock().getType());
        Map<String, Integer> probabilityMap = materialProbabilities.getOrDefault(blockMaterial, defaultProbabilities);
        if(probabilityMap.isEmpty()) {
            return;
        }
        
        Player p = e.getPlayer();
        List<String> commandList = ConfigManager.getMainConfig().getStringList("LuckyBlock.DropOnBlockBreak.commands");
        
        final int totalProbability = 100;
        int randomValue = RandomUtils.getInt(totalProbability);
        for(Map.Entry<String, Integer> entry : probabilityMap.entrySet()) {
            String typeName = entry.getKey();
            int probability = entry.getValue();
            randomValue -= probability;
            if(randomValue < 0) {            
                Block b = e.getBlock();
                boolean dropOriginalItem = ConfigManager.getMainConfig().getBoolean("LuckyBlock.DropOnBlockBreak.dropOriginalItem");
                if(!dropOriginalItem){
                    e.setCancelled(true);
                    b.setType(Material.AIR);
                }

                Location targetLocation = b.getLocation().add(0.5, 0, 0.5);
                LuckyBlockType randomType = TypeManager.getType(typeName);
                randomType.getItem().dropAtLocation(targetLocation);

                commandList.forEach(command -> {
                    byte data = MinecraftVersion.CURRENT_VERSION.isLegacyVersion() ? b.getData() : 0;
                    String fullCommand = command.replace("%x%", p.getLocation().getBlockX() + "")
                                                .replace("%y%", b.getLocation().getBlockY() + "")
                                                .replace("%z%", b.getLocation().getBlockZ() + "")
                                                .replace("%bx%", b.getZ() + "")
                                                .replace("%by%", b.getY() + "")
                                                .replace("%bz%", b.getZ() + "")
                                                .replace("%player%", p.getDisplayName())
                                                .replace("%material%", b.getType().name())
                                                .replace("%data%", data + "")
                                                .replace("%luckyblock_type%", randomType.getTypeName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), fullCommand);
                });
                break;
            }
        }
    }
}
