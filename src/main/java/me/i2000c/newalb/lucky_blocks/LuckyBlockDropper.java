package me.i2000c.newalb.lucky_blocks;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

public class LuckyBlockDropper {
    
    private static final String DEFAULT_MATERIAL = "DEFAULT";
    
    private static boolean enableLuckyBlockDropper;
    private static boolean survivalOnly;
    private static boolean disableWithSilkTouch;
    private static boolean dropOriginalItem;
    private static List<String> commands = new ArrayList<>();
    private static Map<XMaterial, Map<String, Integer>> enabledBlockProbabilites = new EnumMap<>(XMaterial.class);
    private static Map<String, Integer> defaultProbabilites = new LinkedHashMap<>();
    
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
        
        Player p = e.getPlayer();
        XMaterial material = XMaterialUtils.getXMaterial(e.getBlock());
        Map<String, Integer> probabilites = enabledBlockProbabilites.getOrDefault(material, defaultProbabilites);
        
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
