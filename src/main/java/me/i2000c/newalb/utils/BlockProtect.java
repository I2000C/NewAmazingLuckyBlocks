package me.i2000c.newalb.utils;

import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.custom_outcomes.rewards.LuckyBlockType;
import me.i2000c.newalb.custom_outcomes.rewards.TypeManager;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.listeners.interact.SpecialItems;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class BlockProtect implements Listener{
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void noSkullCrash(BlockFromToEvent event){
        if(!WorldManager.isEnabled(event.getToBlock().getWorld().getName())) {
            return;
        }
        
        LuckyBlockType type = TypeManager.getType(event.getToBlock());
        if(type == null) {
            return;
        }
        
        event.setCancelled(true);
        if(!ConfigManager.getMainConfig().getBoolean("LuckyBlock.EnableEnvironmentProtection")) {
            event.getToBlock().setType(Material.AIR);
            Location loc = event.getToBlock().getLocation();
            type.getItem().dropAtLocation(loc);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void noSkullCrash2(EntityExplodeEvent event){
        if(!WorldManager.isEnabled(event.getLocation().getWorld().getName())) {
            return;
        }
        
        if(ConfigManager.getMainConfig().getBoolean("LuckyBlock.EnableEnvironmentProtection")) {
            event.blockList().removeIf(block -> TypeManager.getType(block) != null);
        } else {
            event.blockList().removeIf(block -> {
                LuckyBlockType type = TypeManager.getType(block);
                if(type != null) {
                    block.setType(Material.AIR);
                    Location loc = block.getLocation();
                    type.getItem().dropAtLocation(loc);
                    return true;
                } else {
                    return false;
                }
            });
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void noSkullCrash3(BlockExplodeEvent event){
        if(!WorldManager.isEnabled(event.getBlock().getLocation().getWorld().getName())) {
            return;
        }
        
        if(ConfigManager.getMainConfig().getBoolean("LuckyBlock.EnableEnvironmentProtection")) {
            event.blockList().removeIf(block -> TypeManager.getType(block) != null);
        } else {
            event.blockList().removeIf(block -> {
                LuckyBlockType type = TypeManager.getType(block);
                if(type != null) {
                    block.setType(Material.AIR);
                    Location loc = block.getLocation();
                    type.getItem().dropAtLocation(loc);
                    return true;
                } else {
                    return false;
                }
            });
        }
    }
    
    @EventHandler
    public void playerRenameItem(InventoryClickEvent event){
        if(event.getView().getType() == InventoryType.ANVIL) {
            ItemStack item = event.getView().getItem(0);
            if(event.getRawSlot() == 2) {                
                if(item.getType() != Material.AIR && event.getView().getItem(2).getType() != Material.AIR) {
                    if(TypeManager.getType(item) != null){
                        event.setCancelled(true);                                              
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void playerRenameItem2(InventoryClickEvent event){
        if(event.getView().getType() != InventoryType.ANVIL){
            return;
        }
        
        ItemStack sk0 = event.getView().getItem(0);
        ItemStack sk1 = event.getView().getItem(1);
        ItemStack sk2 = event.getView().getItem(2);
        
        if(sk0.getType() == Material.AIR || sk1.getType() == Material.AIR || sk2.getType() == Material.AIR){
            return;
        }
        
        SpecialItem specialItem = SpecialItems.getByItemStack(sk0);
        if(specialItem == SpecialItems.auto_bow || specialItem == SpecialItems.multi_bow){            
            String displayName = ItemStackWrapper.fromItem(sk0, false)
                                                 .getDisplayName();
            ItemStackWrapper.fromItem(sk2, false)
                            .setDisplayName(displayName);
            event.getView().setItem(2, sk2);
        }
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
    private void playerUseEnchantedBookInAnvil(InventoryClickEvent e) {
        // https://bukkit.org/threads/inventory-anvil-events.142990/
        // https://bukkit.org/threads/1-8-open-an-anvil-inventory-not-much-code.328178/
        // https://www.google.com/search?q=set+custom+items+in+anvil+inventory+spigot+nms&rlz=1C1CHWL_esES900ES900&ei=Sva_Y8HmHIn9kwX3mLPwAw&ved=0ahUKEwjBs-fe_cH8AhWJ_qQKHXfMDD4Q4dUDCA8&uact=5&oq=set+custom+items+in+anvil+inventory+spigot+nms&gs_lcp=Cgxnd3Mtd2l6LXNlcnAQAzIFCAAQogQ6CAgAEKIEELADOgUIIRCgAUoECEEYAUoFCEASATFKBAhGGABQzglY2xRgzxVoAXAAeACAAfYBiAHFBpIBBTEuNC4xmAEAoAEByAEFwAEB&sclient=gws-wiz-serp
        //
        // Most useful:
        // https://www.spigotmc.org/threads/anvil-inventory-invisible-item.541914/#post-4341342
        Inventory inv = e.getClickedInventory();
        if(inv == null || inv.getType() != InventoryType.ANVIL || !inv.equals(e.getView().getTopInventory())) {
            return;
        }
        
        if(e.getSlot() != 2) {
            return;
        }
        
        ItemStack sk0 = inv.getItem(0);
        ItemStack sk1 = inv.getItem(1);
        ItemStack sk2 = inv.getItem(2);
        
        if(sk0 == null || sk1 == null || sk2 == null) {
            return;
        }
        
        if(sk1.getType() == Material.ENCHANTED_BOOK) {
            ItemStackWrapper builder = ItemStackWrapper.fromItem(sk2, false);
            ItemStackWrapper.fromItem(sk1, false).getBookEnchantments()
                    .forEach((enchantment, level) -> builder.addEnchantment(enchantment, level));
        }
    }
}
