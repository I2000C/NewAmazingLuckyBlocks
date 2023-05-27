package me.i2000c.newalb.listeners;

import com.cryptomorin.xseries.XMaterial;
import java.util.List;
import java.util.stream.Collectors;
import me.i2000c.newalb.custom_outcomes.menus.RewardListMenu;
import me.i2000c.newalb.custom_outcomes.rewards.Executable;
import me.i2000c.newalb.custom_outcomes.rewards.TypeManager;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.LangConfig;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.WorldConfig;
import me.i2000c.newalb.utils2.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreak implements Listener{
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e){
        Block b = e.getBlock();
        Location loc = b.getLocation().add(0.5, 0, 0.5);
        Player p = e.getPlayer();
        World w = p.getWorld();
        
        Executable exec = RewardListMenu.testRewardsPlayerList.get(p);
        if(exec != null){
            b.setType(Material.AIR);
            exec.execute(p, loc);
            return;
        }
        
        if(WorldConfig.isEnabled(w.getName())){
            TypeManager.Result result = TypeManager.canBreakBlock(p, loc);
            switch(result.resultCode){
                case TypeManager.RESULT_NOT_LUCKYBLOCK:
                    dropLuckyBlock(e);
                    break;
                case TypeManager.RESULT_NO_GLOBAL_PERMISSION:
                case TypeManager.RESULT_NO_LOCAL_PERMISSION:
                    Logger.sendMessage(LangConfig.getMessage("NoPermission"), p);
                    e.setCancelled(true);
                    break;
                case TypeManager.RESULT_OK:
                    boolean requireLuckyTool = ConfigManager.getConfig().getBoolean("LuckyTool.enable");
                    boolean canOnlyBreakWithLuckyTool = ConfigManager.getConfig().getBoolean("LuckyTool.onlyCanBreakLuckyBlocksWithLuckyTool");
                    if(requireLuckyTool && canOnlyBreakWithLuckyTool){
                        Logger.sendMessage(LangConfig.getMessage("Objects.LuckyTool.need"), p);
                        e.setCancelled(true);
                    }else{
                        b.setType(Material.AIR);
                        result.resultType.execute(p, loc);
                    }
                    break;
            }
        }
    }
    
    private void dropLuckyBlock(BlockBreakEvent e){
        boolean drop = ConfigManager.getConfig().getBoolean("LuckyBlock.DropOnBlockBreak.enable");
        if(!drop){
            return;
        }
        
        boolean survivalOnly = ConfigManager.getConfig().getBoolean("LuckyBlock.DropOnBlockBreak.survivalOnly");
        if(survivalOnly && e.getPlayer().getGameMode() != GameMode.SURVIVAL){
            return;
        }
        
        boolean disableWithSilkTouch = ConfigManager.getConfig().getBoolean("LuckyBlock.DropOnBlockBreak.disableWithSilkTouch");
        if(disableWithSilkTouch) {
            ItemStack item = e.getPlayer().getItemInHand();
            if(item != null && item.getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0) {
                return;
            }
        }        
        
        List<XMaterial> blockList = ConfigManager.getConfig().getStringList("LuckyBlock.DropOnBlockBreak.enabledBlocks")
                .stream().map(materialName -> XMaterial.matchXMaterial(materialName).get())
                .collect(Collectors.toList());
        
        if(!blockList.isEmpty() && !blockList.contains(XMaterial.matchXMaterial(e.getBlock().getType()))){
            return;
        }
        
        List<String> commandList = ConfigManager.getConfig().getStringList("LuckyBlock.DropOnBlockBreak.commands");
        
        int prob = ConfigManager.getConfig().getInt("LuckyBlock.DropOnBlockBreak.probability");
        prob = prob > 100 ? 100 : prob;
        prob = prob < 0 ? 0 : prob;
        
        if(RandomUtils.getInt(100) < prob){
            Block b = e.getBlock();
            boolean dropOriginalItem = ConfigManager.getConfig().getBoolean("LuckyBlock.DropOnBlockBreak.dropOriginalItem");
            if(!dropOriginalItem){
                e.setCancelled(true);
                b.setType(Material.AIR);
            }
            Location targetLocation = b.getLocation().add(0.5, 0, 0.5);
            ItemStack luckyItem = TypeManager.getRandomLuckyBlockType().getItem();
            b.getWorld().dropItemNaturally(targetLocation, luckyItem);
            
            commandList.forEach(command -> {
                String fullCommand = command.replace("%bx%", b.getLocation().getBlockX() + "")
                                            .replace("%by%", b.getLocation().getBlockY() + "")
                                            .replace("%bz%", b.getLocation().getBlockZ() + "")
                                            .replace("%player%", e.getPlayer().getDisplayName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), fullCommand);
            });
        }
    }
}
