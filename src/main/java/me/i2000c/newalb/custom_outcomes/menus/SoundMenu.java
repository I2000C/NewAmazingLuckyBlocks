package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import java.math.BigDecimal;
import java.math.RoundingMode;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.utils.rewards.SoundReward;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

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
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.MAGENTA);
        
        for(int i=0;i<9;i++){
            inv.setItem(i, glass);
        }
        for(int i=18;i<27;i++){
            inv.setItem(i, glass);
        }
        inv.setItem(9, glass);
        inv.setItem(17, glass);
        
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.NOTE_BLOCK);
        if(reward.getType() == null){
            builder.withDisplayName("&6Select sound type");
        }else{
            builder.withDisplayName("&6Selected type: &3" + reward.getType());
        }
        ItemStack sound = builder.build();
        
        double value = BigDecimal.valueOf(reward.getVolume())
                .setScale(3, RoundingMode.HALF_UP)
                .doubleValue();
        ItemStack volume = ItemBuilder.newItem(XMaterial.EMERALD)
                .withDisplayName("&aSound volume: &5" + value)
                .build();
        
        
        value = BigDecimal.valueOf(reward.getPitch()).setScale(3, RoundingMode.HALF_UP).doubleValue();
        ItemStack pitch = ItemBuilder.newItem(XMaterial.GOLD_NUGGET)
                .withDisplayName("&eSound pitch: &5" + value)
                .build();
        
        inv.setItem(10, GUIItem.getBackItem());
        inv.setItem(16, GUIItem.getNextItem());
        
        inv.setItem(12, sound);
        inv.setItem(13, volume);
        inv.setItem(14, pitch);
        
        inv.setItem(22, GUIItem.getPlusLessItem(-1));
        inv.setItem(4, GUIItem.getPlusLessItem(+1));
        
        inv.setItem(23, GUIItem.getPlusLessItem(-1));
        inv.setItem(5, GUIItem.getPlusLessItem(+1));
        
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
        
        if(NewAmazingLuckyBlocks.getMinecraftVersion().compareTo(MinecraftVersion.v1_10) >= 0){
            ItemStack stop = ItemBuilder.newItem(XMaterial.BARRIER)
                    .withDisplayName("&cStop all sounds")
                    .build();
            
            inv.setItem(48, stop);
        }
        
        inv.setItem(45, GUIItem.getBackItem());
        
        inv.setItem(51, GUIItem.getPreviousPageItem());
        inv.setItem(52, GUIItem.getCurrentPageItem(index+1, max_pages));
        inv.setItem(53, GUIItem.getNextPageItem());
        
        for(int i=45*index;(i-45*index)<MENU_SIZE && i<soundList.length;i++){
            ItemBuilder builder = ItemBuilder.newItem(XMaterial.NOTE_BLOCK);
            builder.withDisplayName("&3" + soundList[i].name());
            if(reward.getType() != null && soundList[i].name().equals(reward.getType())){
                builder.addEnchantment(Enchantment.DURABILITY, 1);
                builder.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            
            inv.setItem((i-45*index), builder.build());
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
                    if(e.getCurrentItem() != null){
                        String displayName = ItemBuilder.fromItem(e.getCurrentItem(), false)
                                .getDisplayName();
                        if(displayName != null){
                            reward.setType(Logger.stripColor(displayName));
                            reward.execute(p, p.getLocation());
                            openSoundTypeMenu(p);
                        }
                    }
            }
        }
//</editor-fold>
    };
}
