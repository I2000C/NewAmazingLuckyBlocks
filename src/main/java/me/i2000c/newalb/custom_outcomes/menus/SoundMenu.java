package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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
import me.i2000c.newalb.utils2.ItemStackWrapper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class SoundMenu extends Editor<SoundReward>{
    public SoundMenu(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        InventoryListener.registerInventory(CustomInventoryType.SOUND_MENU, SOUND_MENU_FUNCTION);
        InventoryListener.registerInventory(CustomInventoryType.SOUND_TYPE_MENU, SOUND_TYPE_MENU_FUNCTION);
        
        soundListAdapter = new GUIPagesAdapter<>(
                SOUND_LIST_MENU_SIZE,
                (sound, index) -> {
                    ItemStackWrapper builder = ItemStackWrapper.newItem(XMaterial.NOTE_BLOCK);
                    builder.setDisplayName("&3" + sound.name());
                    if(item.getType() != null && sound == item.getType()){
                        builder.addEnchantment(XEnchantment.UNBREAKING, 1);
                        builder.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    }
                    return builder.toItemStack();
                }
        );
        soundListAdapter.setPreviousPageSlot(PREVIOUS_PAGE_SLOT);
        soundListAdapter.setCurrentPageSlot(CURRENT_PAGE_SLOT);
        soundListAdapter.setNextPageSlot(NEXT_PAGE_SLOT);
        
        soundList = new ArrayList<>(Arrays.asList(XSound.values()));        
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
    private static GUIPagesAdapter<XSound> soundListAdapter;
    private static List<XSound> soundList;
    
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
        Menu menu = GUIFactory.newMenu(CustomInventoryType.SOUND_MENU, 45, "&d&lSound Reward");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.MAGENTA);
        
        for(int i=0;i<9;i++){
            menu.setItem(i, glass);
        }
        for(int i=36;i<45;i++){
            menu.setItem(i, glass);
        }
        menu.setItem(9, glass);
        menu.setItem(18, glass);
        menu.setItem(27, glass);
        menu.setItem(17, glass);
        menu.setItem(26, glass);
        menu.setItem(35, glass);
        
        ItemStackWrapper builder = ItemStackWrapper.newItem(XMaterial.NOTE_BLOCK);
        if(item.getType() == null){
            builder.setDisplayName("&6Select sound type");
        }else{
            builder.setDisplayName("&6Selected type: &3" + item.getType().name());
        }
        ItemStack sound = builder.toItemStack();
        
        ItemStack volume = ItemStackWrapper.newItem(XMaterial.EMERALD)
                                           .setDisplayName(String.format(Locale.ENGLISH, "&aSound volume: &5%.2f", item.getVolume()))
                                           .addLoreLine("&3Click to reset")
                                           .addLoreLine("")
                                           .addLoreLine(String.format(Locale.ENGLISH, "&6Min volume: &d%.2f", SoundReward.MIN_VOLUME))
                                           .addLoreLine(String.format(Locale.ENGLISH, "&6Max volume: &d%.2f", SoundReward.MAX_VOLUME))
                                           .toItemStack();        
        
        ItemStack pitch = ItemStackWrapper.newItem(XMaterial.GOLD_NUGGET)
                                          .setDisplayName(String.format(Locale.ENGLISH, "&eSound pitch: &5%.2f", item.getPitch()))
                                          .addLoreLine("&3Click to reset")
                                          .addLoreLine("")
                                          .addLoreLine(String.format(Locale.ENGLISH, "&6Min pitch: &d%.2f", SoundReward.MIN_PITCH))
                                          .addLoreLine(String.format(Locale.ENGLISH, "&6Max pitch: &d%.2f", SoundReward.MAX_PITCH))
                                          .toItemStack();
        
        ItemStack testSound = ItemStackWrapper.newItem(XMaterial.SUNFLOWER)
                                              .setDisplayName("&eTest sound")
                                              .toItemStack();
        ItemStack stopSound = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                              .setDisplayName("&cStop sound")
                                              .toItemStack();
        
        menu.setItem(10, GUIItem.getBackItem());
        menu.setItem(16, GUIItem.getNextItem());
        
        menu.setItem(13, sound);
        menu.setItem(22, volume);
        menu.setItem(31, pitch);
        
        if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_10)) {
            menu.setItem(12, stopSound);
        }
        menu.setItem(14, testSound);
        
        menu.setItem(19, GUIItem.getPlusLessItem(new BigDecimal("-0.5")));
        menu.setItem(20, GUIItem.getPlusLessItem(new BigDecimal("-0.1")));
        menu.setItem(21, GUIItem.getPlusLessItem(new BigDecimal("-0.05")));
        menu.setItem(23, GUIItem.getPlusLessItem(new BigDecimal("+0.05")));
        menu.setItem(24, GUIItem.getPlusLessItem(new BigDecimal("+0.1")));
        menu.setItem(25, GUIItem.getPlusLessItem(new BigDecimal("+0.5")));
        
        menu.setItem(28, GUIItem.getPlusLessItem(new BigDecimal("-0.5")));
        menu.setItem(29, GUIItem.getPlusLessItem(new BigDecimal("-0.1")));
        menu.setItem(30, GUIItem.getPlusLessItem(new BigDecimal("-0.05")));
        menu.setItem(32, GUIItem.getPlusLessItem(new BigDecimal("+0.05")));
        menu.setItem(33, GUIItem.getPlusLessItem(new BigDecimal("+0.1")));
        menu.setItem(34, GUIItem.getPlusLessItem(new BigDecimal("+0.5")));
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction SOUND_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 13:
                    //Open sound type inventory
                    openSoundTypeMenu(player);
                    break;
                case 12:
                    //Stop sounds if minecraft version >= 1.10
                    if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_10)){
                        for(XSound sound : soundList){
                            sound.stopSound(player);
                        }
                    }
                    break;
                case 14:
                    item.execute(player, player.getLocation());
                    break;
                case 22:
                    // Reset volume
                    item.setVolume(SoundReward.DEFAULT_VOLUME);
                    openSoundMenu(player);
                    break;
                case 31:
                    // Reset pitch
                    item.setPitch(SoundReward.DEFAULT_PITCH);
                    openSoundMenu(player);
                    break;
                case 19:
                case 20:
                case 21:
                case 23:
                case 24:
                case 25:
                    // Modify volume
                    String variationValue = Logger.stripColor(ItemStackWrapper.fromItem(e.getCurrentItem(), false).getDisplayName());
                    BigDecimal variation = new BigDecimal(variationValue);
                    item.setVolume(item.getVolume().add(variation));
                    openSoundMenu(player);
                    break;
                case 28:
                case 29:
                case 30:
                case 32:
                case 33:
                case 34:
                    // Modify pitch
                    variationValue = Logger.stripColor(ItemStackWrapper.fromItem(e.getCurrentItem(), false).getDisplayName());
                    variation = new BigDecimal(variationValue);
                    item.setPitch(item.getPitch().add(variation));
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
            ItemStack stop = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                             .setDisplayName("&cStop all sounds")
                                             .toItemStack();
            
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
                        for(XSound sound : soundList){
                            sound.stopSound(player);
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
                        String displayName = ItemStackWrapper.fromItem(e.getCurrentItem(), false)
                                .getDisplayName();
                        if(displayName != null){
                            item.setType(XSound.matchXSound(Logger.stripColor(displayName)).get());
                            item.execute(player, player.getLocation());
                            openSoundTypeMenu(player);
                        }
                    }
            }
        }
//</editor-fold>
    };
}
