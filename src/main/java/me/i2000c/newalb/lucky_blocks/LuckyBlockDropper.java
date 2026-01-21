package me.i2000c.newalb.lucky_blocks;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.version.MinecraftVersion;
import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.lucky_blocks.rewards.LuckyBlockType;
import me.i2000c.newalb.lucky_blocks.rewards.TypeManager;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.XMaterialUtils;
import me.i2000c.newalb.utils.random.RandomUtils;
import me.i2000c.newalb.utils.tasks.Task;

public class LuckyBlockDropper {
    
    private static final String DEFAULT_MATERIAL = "DEFAULT";
    
    private static boolean enableLuckyBlockDropper;
    private static boolean survivalOnly;
    private static boolean disableWithSilkTouch;
    private static boolean dropOriginalItem;
    private static List<String> commands = new ArrayList<>();
    private static Map<XMaterial, Map<String, Integer>> enabledBlockProbabilites = new EnumMap<>(XMaterial.class);
    private static Map<String, Integer> defaultProbabilites = new LinkedHashMap<>();
    
    private static boolean enableLocationFiltering;
    private static int autoCleanTime;
    private static int minLocationTime;
    private static Map<Location, Long> recentBrokenLocations = new HashMap<>();
    private static Task autoCleanLocationsTask = new Task() {
        @Override
        public void run() {
            Iterator<Map.Entry<Location, Long>> iterator = recentBrokenLocations.entrySet().iterator();
            while(iterator.hasNext()) {
                Map.Entry<Location, Long> entry = iterator.next();
                long timestampMillis = entry.getValue();
                long timeElapsedMillis = System.currentTimeMillis() - timestampMillis;
                long minLocationTimeMillis = minLocationTime * 1000;
                if(timeElapsedMillis > minLocationTimeMillis) {
                    iterator.remove();
                }
            }
        }
    };
    
    private static boolean enableDropCooldown;
    private static int minCooldown;
    private static int maxCooldown;
    private static Map<UUID, Long> dropCooldowns = new HashMap<>();
    
    public static void loadSettings() {
        enableLuckyBlockDropper = ConfigManager.getMainConfig().getBoolean("LuckyBlock.DropOnBlockBreak.enable");
        survivalOnly = ConfigManager.getMainConfig().getBoolean("LuckyBlock.DropOnBlockBreak.survivalOnly");
        disableWithSilkTouch = ConfigManager.getMainConfig().getBoolean("LuckyBlock.DropOnBlockBreak.disableWithSilkTouch");
        dropOriginalItem = ConfigManager.getMainConfig().getBoolean("LuckyBlock.DropOnBlockBreak.dropOriginalItem");
        commands = ConfigManager.getMainConfig().getStringList("LuckyBlock.DropOnBlockBreak.commands");
        
        enabledBlockProbabilites.clear();
        
        if(ConfigManager.getMainConfig().getBukkitConfig().isList("LuckyBlock.DropOnBlockBreak.enabledBlocks")) {
            Logger.warn("Invalid values detected inside \"LuckyBlock.DropOnBlockBreak.enabledBlocks\" config section");
            Logger.warn("Expected: map, Found: list");
            Logger.warn("Delete current config file and reload the server to fix this issue");
            return;
        }
        
        ConfigurationSection section = ConfigManager.getMainConfig().getConfigurationSection("LuckyBlock.DropOnBlockBreak.enabledBlocks");
        for(String materialName : section.getKeys(false)) {
            ConfigurationSection subsection = section.getConfigurationSection(materialName);
            for(String typeName : subsection.getKeys(false)) {
                int probability = subsection.getInt(typeName);
                LuckyBlockType type = TypeManager.getType(typeName);
                if(type == null) {
                    Logger.warn(String.format("LuckyBlock type with name \"%s\" doesn't exist", typeName));
                    continue;
                }
                
                if(materialName.equals(DEFAULT_MATERIAL)) {
                    defaultProbabilites.put(typeName, probability);
                } else {
                    XMaterial material = XMaterialUtils.parseXMaterial(materialName.toUpperCase());
                    enabledBlockProbabilites.computeIfAbsent(material, key -> new LinkedHashMap<>())
                                            .put(typeName, probability);
                }
            }
        }
        
        // Location filtering config
        autoCleanLocationsTask.cancel();
        recentBrokenLocations.clear();
        ConfigurationSection locationFilteringSection = ConfigManager.getMainConfig().getConfigurationSection("LuckyBlock.DropOnBlockBreak.locationFiltering");
        enableLocationFiltering = locationFilteringSection.getBoolean("enable");
        autoCleanTime = locationFilteringSection.getInt("autoCleanTime");
        if(autoCleanTime < 1) {
            autoCleanTime = 1;
        }
        minLocationTime = locationFilteringSection.getInt("minLocationTime");
        if(enableLocationFiltering) {
            long autoCleanTimeTicks = autoCleanTime * 20L;
            autoCleanLocationsTask.runTask(0L, autoCleanTimeTicks);
        }
        
        // Drop cooldown config
        dropCooldowns.clear();
        ConfigurationSection dropCooldownSection = ConfigManager.getMainConfig().getConfigurationSection("LuckyBlock.DropOnBlockBreak.dropCooldown");
        enableDropCooldown = dropCooldownSection.getBoolean("enable");
        minCooldown = dropCooldownSection.getInt("min");
        maxCooldown = dropCooldownSection.getInt("max");
        if(minCooldown < 0) {
            minCooldown = 0;
        }
        if(maxCooldown < minCooldown) {
            maxCooldown = minCooldown;
        }
    }
    
    public static void dropLuckyBlock(BlockBreakEvent e) {
        if(!enableLuckyBlockDropper) {
            return;
        }
        
        if(survivalOnly && e.getPlayer().getGameMode() != GameMode.SURVIVAL){
            return;
        }
        
        if(disableWithSilkTouch) {
            ItemStack item = e.getPlayer().getItemInHand();
            if(item != null && item.getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0) {
                return;
            }
        }
        
        XMaterial material = XMaterialUtils.getXMaterial(e.getBlock());
        Map<String, Integer> probabilites = enabledBlockProbabilites.getOrDefault(material, defaultProbabilites);
        if(probabilites.isEmpty()) {
            return;
        }
        
        if(enableLocationFiltering) {
            Location location = e.getBlock().getLocation();
            Long previousTimestamp = recentBrokenLocations.put(location, System.currentTimeMillis());
            if(previousTimestamp != null) {
                return;
            }
        }
        
        Player p = e.getPlayer();
        if(enableDropCooldown) {
            Long cooldownExpireTime = dropCooldowns.get(p.getUniqueId());
            Long now = System.currentTimeMillis();
            if(cooldownExpireTime != null && cooldownExpireTime > now) {
                return;
            } else {
                int randomCooldownSeconds = RandomUtils.getInt(minCooldown, maxCooldown);
                long randomCooldownMillis = randomCooldownSeconds * 1000L;
                cooldownExpireTime = now + randomCooldownMillis;
                dropCooldowns.put(p.getUniqueId(), cooldownExpireTime);
            }
        }
        
        final int totalProbability = 100;
        int randomValue = RandomUtils.getInt(totalProbability);
        for(Map.Entry<String, Integer> entry : probabilites.entrySet()) {
            String typeName = entry.getKey();
            int probability = entry.getValue();
            randomValue -= probability;
            if(randomValue < 0) {
                Block b = e.getBlock();
                if(!dropOriginalItem){
                    e.setCancelled(true);
                    b.setType(Material.AIR);
                }
                
                Location targetLocation = b.getLocation().add(0.5, 0, 0.5);
                LuckyBlockType randomType = TypeManager.getType(typeName);
                randomType.getItem().dropAtLocation(targetLocation);
                
                commands.forEach(command -> {
                    byte data = MinecraftVersion.CURRENT_VERSION.isLegacyVersion() ? b.getData() : 0;
                    String fullCommand = command.replace("%x%", p.getLocation().getBlockX() + "")
                                                .replace("%y%", b.getLocation().getBlockY() + "")
                                                .replace("%z%", b.getLocation().getBlockZ() + "")
                                                .replace("%bx%", b.getX() + "")
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
