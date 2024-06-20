package me.i2000c.newalb.listeners;

import com.cryptomorin.xseries.XMaterial;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.custom_outcomes.menus.RewardListMenu;
import me.i2000c.newalb.custom_outcomes.rewards.Executable;
import me.i2000c.newalb.custom_outcomes.rewards.LuckyBlockType;
import me.i2000c.newalb.custom_outcomes.rewards.TypeManager;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.WorldManager;
import me.i2000c.newalb.utils2.RandomUtils;
import me.i2000c.newalb.utils2.WorldGuardManager;
import me.i2000c.newalb.utils2.XMaterialUtils;
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

public class BlockBreak implements Listener{
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
                boolean requireLuckyTool = ConfigManager.getMainConfig().getBoolean("Objects.LuckyTool.enable");
                boolean canOnlyBreakWithLuckyTool = ConfigManager.getMainConfig().getBoolean("Objects.LuckyTool.onlyCanBreakLuckyBlocksWithLuckyTool");
                if(requireLuckyTool && canOnlyBreakWithLuckyTool){
                    Logger.sendMessage(ConfigManager.getLangMessage("Objects.LuckyTool.need"), p);
                    e.setCancelled(true);
                }else{
                    b.setType(Material.AIR);
                    result.resultType.execute(p, loc);
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
