package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.utils.rewards.SoundReward;
import me.i2000c.newalb.utils.Logger;
import java.math.BigDecimal;
import java.math.RoundingMode;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SoundMenu{
    public static SoundReward reward;
    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.SOUND_MENU, SOUND_MENU_FUNCTION);
            InventoryListener.registerInventory(CustomInventoryType.SOUND_TYPE_MENU, SOUND_TYPE_MENU_FUNCTION);
            
            inventoriesRegistered = true;
        }
        
        index = 0;
        reward = null;
    }
    
    public static void openSoundMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(reward == null){
            reward = new SoundReward(FinishMenu.getCurrentOutcome());
        }
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.SOUND_MENU, 27, "&d&lSound Reward");
        GUIManager.setCurrentInventory(inv);
        
        ItemStack glass = XMaterial.MAGENTA_STAINED_GLASS_PANE.parseItem();
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        
        for(int i=0;i<9;i++){
            inv.setItem(i, glass);
        }
        for(int i=18;i<27;i++){
            inv.setItem(i, glass);
        }
        inv.setItem(9, glass);
        inv.setItem(17, glass);
        
        ItemStack sound = new ItemStack(Material.NOTE_BLOCK);
        meta = sound.getItemMeta();
        if(reward.getType() == null){
            meta.setDisplayName(Logger.color("&6Select sound type"));
        }else{
            meta.setDisplayName(Logger.color("&6Selected type: &3" + reward.getType()));
        }
        sound.setItemMeta(meta);
        
        ItemStack volume = new ItemStack(Material.EMERALD);
        meta = volume.getItemMeta();
        double truncated = BigDecimal.valueOf(reward.getVolume()).setScale(3, RoundingMode.HALF_UP).doubleValue();
        meta.setDisplayName(Logger.color("&aSound volume: &5" + truncated));
        volume.setItemMeta(meta);
        
        ItemStack pitch = new ItemStack(Material.GOLD_NUGGET);
        meta = pitch.getItemMeta();
        truncated = BigDecimal.valueOf(reward.getPitch()).setScale(3, RoundingMode.HALF_UP).doubleValue();
        meta.setDisplayName(Logger.color("&eSound pitch: &5" + truncated));
        pitch.setItemMeta(meta);
        
        ItemStack plus = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
        meta = plus.getItemMeta();
        meta.setDisplayName(Logger.color("&a&l+"));
        plus.setItemMeta(meta);
        
        ItemStack less = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
        meta = less.getItemMeta();
        meta.setDisplayName(Logger.color("&c&l-"));
        less.setItemMeta(meta);
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName(Logger.color("&7Back"));
        back.setItemMeta(meta);
        
        ItemStack next = new ItemStack(Material.ANVIL);
        meta = next.getItemMeta();
        meta.setDisplayName(Logger.color("&bNext"));
        next.setItemMeta(meta);
        
        inv.setItem(10, back);
        inv.setItem(16, next);
        
        inv.setItem(12, sound);
        inv.setItem(13, volume);
        inv.setItem(14, pitch);
        
        inv.setItem(22, less);
        inv.setItem(4, plus);
        
        inv.setItem(23, less);
        inv.setItem(5, plus);
        
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction SOUND_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            switch(e.getSlot()){
                case 12:
                    //Open sound type inventory
                    openSoundTypeMenu(p);
                    break;
                case 22:
                    reward.setVolume((reward.getVolume() - 1.0));
                    if(reward.getVolume() < 0.0){
                        reward.setVolume(20.0);
                    }
                    openSoundMenu(p);
                    break;
                case 4:
                    reward.setVolume((reward.getVolume() + 1.0));
                    if(reward.getVolume() > 20.0){
                        reward.setVolume(0.0);
                    }
                    openSoundMenu(p);
                    break;
                case 23:
                    reward.setPitch((reward.getPitch() - 0.1));
                    if(reward.getPitch() < 0.0){
                        reward.setPitch(2.0);
                    }
                    openSoundMenu(p);
                    break;
                case 5:
                    reward.setPitch((reward.getPitch() + 0.1));
                    if(reward.getPitch() > 2.0){
                        reward.setPitch(0.0);
                    }
                    openSoundMenu(p);
                    break;
                case 10:
                    //Back
                    if(FinishMenu.editMode){
                        FinishMenu.openFinishInventory(p);
                    }else{
                        RewardTypesMenu.openRewardTypesMenu(p);
                    }
                    break;
                case 16:
                    //Next
                    if(reward.getType() != null){
                        FinishMenu.addReward(reward);
                        reset();
                        FinishMenu.openFinishInventory(p);
                    }
                    break;
            }
        }
//</editor-fold>
    };
    
    private static final Sound[] soundList = Sound.values();
    private static final int MENU_SIZE = 45;
    private static int max_pages = -1;
    
    private static int index = 0;
    
    private static void openSoundTypeMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(max_pages == -1){
            if(soundList.length % MENU_SIZE == 0){
                max_pages = (soundList.length / MENU_SIZE);
            }else{
                max_pages = (soundList.length / MENU_SIZE) + 1;
            }
        }
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.SOUND_TYPE_MENU, 54, "&3&lSound Type");
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = back.getItemMeta();
        meta.setDisplayName(Logger.color("&2Back"));
        back.setItemMeta(meta);
        
        ItemStack previousPage = new ItemStack(Material.MAGMA_CREAM);
        meta = previousPage.getItemMeta();
        meta.setDisplayName(Logger.color("&7Previous page"));
        previousPage.setItemMeta(meta);
        
        ItemStack nextPage = XMaterial.ENDER_EYE.parseItem();
        meta = nextPage.getItemMeta();
        meta.setDisplayName(Logger.color("&bNext page"));
        nextPage.setItemMeta(meta);
        
        ItemStack pages = new ItemStack(Material.BOOK);
        pages.setAmount(index+1);
        meta = pages.getItemMeta();
        meta.setDisplayName(Logger.color("&6Page &5" + (index+1) + " &6of &d" + max_pages));
        pages.setItemMeta(meta);
        
        if(NewAmazingLuckyBlocks.getMinecraftVersion().compareTo(MinecraftVersion.v1_9) > 0){
            ItemStack stop = new ItemStack(Material.BARRIER);
            meta = stop.getItemMeta();
            meta.setDisplayName(Logger.color("&cStop all sounds"));
            stop.setItemMeta(meta);
            
            inv.setItem(48, stop);
        }
        
        inv.setItem(45, back);
        
        inv.setItem(51, previousPage);
        inv.setItem(52, pages);
        inv.setItem(53, nextPage);
        
        for(int i=45*index;(i-45*index)<MENU_SIZE && i<soundList.length;i++){
            ItemStack stack = new ItemStack(Material.NOTE_BLOCK);
            meta = stack.getItemMeta();
            meta.setDisplayName(Logger.color("&3" + soundList[i].name()));
            if(reward.getType() != null && soundList[i].name().equalsIgnoreCase(reward.getType())){
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            stack.setItemMeta(meta);
            
            inv.setItem((i-45*index), stack);
        }
        
        p.openInventory(inv);
        GUIManager.setCurrentInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction SOUND_TYPE_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            switch(e.getSlot()){
                case 45:
                    openSoundMenu(p);
                    break;
                case 48:
                    //Stop sounds if minecraft version > 1.9
                    if(NewAmazingLuckyBlocks.getMinecraftVersion().compareTo(MinecraftVersion.v1_9) > 0){
                        for(Sound sound : soundList){
                            p.stopSound(sound);
                        }
                    }
                    break;
                case 51:
                    index--;
                    if(index < 0){
                        index = max_pages-1;
                    }
                    openSoundTypeMenu(p);
                    break;
                case 52:
                    break;
                case 53:
                    index++;
                    if(index >= max_pages){
                        index = 0;
                    }
                    openSoundTypeMenu(p);
                    break;
                default:
                    if(e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()){
                        ItemMeta meta = e.getCurrentItem().getItemMeta();
                        if(meta.hasDisplayName()){
                            reward.setType(Logger.stripColor(meta.getDisplayName()));
                            reward.execute(p, p.getLocation());
                            openSoundTypeMenu(p);
                        }
                    }
            }
        }
//</editor-fold>
    };
}
