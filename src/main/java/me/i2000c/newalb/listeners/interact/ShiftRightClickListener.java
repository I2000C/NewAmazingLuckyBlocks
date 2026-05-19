package me.i2000c.newalb.listeners.interact;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import me.i2000c.newalb.api.version.MinecraftVersion;
import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.lucky_blocks.rewards.LuckyBlockType;
import me.i2000c.newalb.lucky_blocks.rewards.PackManager;
import me.i2000c.newalb.lucky_blocks.rewards.TypeManager;
import me.i2000c.newalb.utils.locations.WorldManager;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.menus.preview.LuckyBlockPreviewMenu;

public class ShiftRightClickListener implements Listener {
    
    @EventHandler(priority = EventPriority.LOW)
    private static void onPlayerInteract(PlayerInteractEvent event) {
        boolean previewModeEnabled = ConfigManager.getMainConfig().getBoolean("LuckyBlock.PreviewMode.enable");
        if(!previewModeEnabled) {
            return;
        }
        
        if(!MinecraftVersion.CURRENT_VERSION.is_1_8()){
            if(event.getHand() == EquipmentSlot.OFF_HAND){
                return;
            }
        }
        
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        Player player = event.getPlayer();
        if(!player.isSneaking()) {
            return;
        }
        
        Block block = event.getClickedBlock();
        if(block == null) {
            return;
        }
        
        if(!WorldManager.isEnabled(player.getWorld().getName())) {
            return;
        }
        
        LuckyBlockType luckyBlockType = TypeManager.getType(block);
        if(luckyBlockType == null) {
            return;
        }
        
        boolean requiredPermission = ConfigManager.getMainConfig().getBoolean("LuckyBlock.PreviewMode.required-permission");
        String previewPermission = ConfigManager.getMainConfig().getString("LuckyBlock.PreviewMode.permission");
        if(requiredPermission && !player.hasPermission(previewPermission)) {
            Logger.sendMessage(ConfigManager.getLangMessage("NoPermission"), player);
            event.setCancelled(true);
            return;
        }
        
        if(PackManager.IS_LOADING_PACKS()) {
            Logger.sendMessage(ConfigManager.getLangMessage("Loading.not-fully-loaded"), player);
            event.setCancelled(true);
            return;
        }
        
        LuckyBlockPreviewMenu menu = new LuckyBlockPreviewMenu(luckyBlockType);
        menu.setOnBack(p -> p.closeInventory());
        menu.openToPlayer(player);
    }
}
