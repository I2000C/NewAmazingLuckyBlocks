package me.i2000c.newalb.custom_outcomes.menus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.SoundReward;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GUIPagesAdapter;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.ItemBuilder;

public class SoundMenu extends Editor<SoundReward>{
    public SoundMenu(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        InventoryListener.registerInventory(CustomInventoryType.SOUND_MENU, SOUND_MENU_FUNCTION);
        InventoryListener.registerInventory(CustomInventoryType.SOUND_TYPE_MENU, SOUND_TYPE_MENU_FUNCTION);
        
        soundListAdapter = new GUIPagesAdapter<>(
                SOUND_LIST_MENU_SIZE,
                (sound, index) -> {
                    ItemBuilder builder = ItemBuilder.newItem(XMaterial.NOTE_BLOCK);
                    builder.withDisplayName("&3" + sound.name());
                    if(item.getType() != null && sound == item.getType()){
                        builder.addEnchantment(Enchantment.DURABILITY, 1);
                        builder.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    }
                    return builder.build();
                }
        );
        soundListAdapter.setPreviousPageSlot(PREVIOUS_PAGE_SLOT);
        soundListAdapter.setCurrentPageSlot(CURRENT_PAGE_SLOT);
        soundListAdapter.setNextPageSlot(NEXT_PAGE_SLOT);
        
        soundList = Arrays.asList(Sound.values());
        soundList.sort((sound1, sound2) -> {
            String name1 = sound1.name();
            String name2 = sound2.name();
            return name1.compareTo(name2);
        });
        
        soundListAdapter.setItemList(soundList);
//</editor-fold>
    }
    
    private static final int SOUND_LIST_MENU_SIZE = 45;
    private static final int PREVIOUS_PAGE_SLOT = 51;
    private static final int CURRENT_PAGE_SLOT = 52;
    private static final int NEXT_PAGE_SLOT = 53;
    private static GUIPagesAdapter<Sound> soundListAdapter;
    private static List<Sound> soundList;
    
    @Override
    protected void reset(){
        soundListAdapter.goToMainPage();
    }
    
    @Override
    protected void newItem(Player player){
        Outcome outcome = RewardListMenu.getCurrentOutcome();
        item = new SoundReward(outcome);
        openSoundMenu(player);
    }
    
    @Override
    protected void editItem(Player player){
        openSoundMenu(player);
    }
    
    private void openSoundMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.SOUND_MENU, 27, "&d&lSound Reward");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.MAGENTA);
        
        for(int i=0;i<9;i++){
            menu.setItem(i, glass);
        }
        for(int i=18;i<27;i++){
            menu.setItem(i, glass);
        }
        menu.setItem(9, glass);
        menu.setItem(17, glass);
        
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.NOTE_BLOCK);
        if(item.getType() == null){
            builder.withDisplayName("&6Select sound type");
        }else{
            builder.withDisplayName("&6Selected type: &3" + item.getType().name());
        }
        ItemStack sound = builder.build();
        
        double value = BigDecimal.valueOf(item.getVolume())
                .setScale(3, RoundingMode.HALF_UP)
                .doubleValue();
        ItemStack volume = ItemBuilder.newItem(XMaterial.EMERALD)
                .withDisplayName("&aSound volume: &5" + value)
                .addLoreLine("&3Click to reset")
                .build();
        
        
        value = BigDecimal.valueOf(item.getPitch()).setScale(3, RoundingMode.HALF_UP).doubleValue();
        ItemStack pitch = ItemBuilder.newItem(XMaterial.GOLD_NUGGET)
                .withDisplayName("&eSound pitch: &5" + value)
                .addLoreLine("&3Click to reset")
                .build();
        
        menu.setItem(10, GUIItem.getBackItem());
        menu.setItem(16, GUIItem.getNextItem());
        
        menu.setItem(12, sound);
        menu.setItem(13, volume);
        menu.setItem(14, pitch);
        
        menu.setItem(22, GUIItem.getPlusLessItem(-1));
        menu.setItem(4, GUIItem.getPlusLessItem(+1));
        
        menu.setItem(23, GUIItem.getPlusLessItem(-1));
        menu.setItem(5, GUIItem.getPlusLessItem(+1));
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction SOUND_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 12:
                    //Open sound type inventory
                    openSoundTypeMenu(player);
                    break;
                case 22:
                    item.setVolume(item.getVolume() - 1.0);
                    if(item.getVolume() < 0.0){
                        item.setVolume(20.0);
                    }
                    openSoundMenu(player);
                    break;
                case 13:
                    item.setVolume(10.0);
                    openSoundMenu(player);
                    break;
                case 4:
                    item.setVolume(item.getVolume() + 1.0);
                    if(item.getVolume() > 20.0){
                        item.setVolume(0.0);
                    }
                    openSoundMenu(player);
                    break;
                case 23:
                    item.setPitch(item.getPitch() - 0.1);
                    if(item.getPitch() < 0.0){
                        item.setPitch(2.0);
                    }
                    openSoundMenu(player);
                    break;
                case 14:
                    item.setPitch(1.0);
                    openSoundMenu(player);
                    break;
                case 5:
                    item.setPitch(item.getPitch() + 0.1);
                    if(item.getPitch() > 2.0){
                        item.setPitch(0.0);
                    }
                    openSoundMenu(player);
                    break;
                case 10:
                    //Back
                    onBack.accept(player);
                    break;
                case 16:
                    //Next
                    if(item.getType() != null){
                        onNext.accept(player, item);
                    }
                    break;
            }
        }
//</editor-fold>
    };
    
    private void openSoundTypeMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">        
        Menu menu = GUIFactory.newMenu(CustomInventoryType.SOUND_TYPE_MENU, 54, "&3&lSound Type");
        
        if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_10)){
            ItemStack stop = ItemBuilder.newItem(XMaterial.BARRIER)
                    .withDisplayName("&cStop all sounds")
                    .build();
            
            menu.setItem(48, stop);
        }
        
        menu.setItem(45, GUIItem.getBackItem());
        
        soundListAdapter.updateMenu(menu);        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction SOUND_TYPE_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 45:
                    openSoundMenu(player);
                    break;
                case 48:
                    //Stop sounds if minecraft version >= 1.10
                    if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_10)){
                        for(Sound sound : soundList){
                            player.stopSound(sound);
                        }
                    }
                    break;
                case 51:
                    if(soundListAdapter.goToPreviousPage()){
                        openSoundTypeMenu(player);
                    }
                    break;
                case 52:
                    if(soundListAdapter.goToMainPage()){
                        openSoundTypeMenu(player);
                    }
                    break;
                case 53:
                    if(soundListAdapter.goToNextPage()){
                        openSoundTypeMenu(player);
                    }
                    break;
                default:
                    if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
                        String displayName = ItemBuilder.fromItem(e.getCurrentItem(), false)
                                .getDisplayName();
                        if(displayName != null){
                            item.setType(Sound.valueOf(Logger.stripColor(displayName)));
                            item.execute(player, player.getLocation());
                            openSoundTypeMenu(player);
                        }
                    }
            }
        }
//</editor-fold>
    };
}
