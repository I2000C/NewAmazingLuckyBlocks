package me.i2000c.newalb.lucky_blocks.editors.menus;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.i2000c.newalb.api.functions.EditorBackFunction;
import me.i2000c.newalb.api.functions.EditorNextFunction;
import me.i2000c.newalb.api.functions.InventoryFunction;
import me.i2000c.newalb.api.gui.CustomInventoryType;
import me.i2000c.newalb.api.gui.GUIFactory;
import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.InventoryLocation;
import me.i2000c.newalb.api.gui.Menu;
import me.i2000c.newalb.api.version.MinecraftVersion;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.lucky_blocks.editors.Editor;
import me.i2000c.newalb.lucky_blocks.editors.EditorType;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.types.SoundReward;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SoundMenu extends Editor<SoundReward> {
    
    public SoundMenu() {
        InventoryListener.registerInventory(CustomInventoryType.SOUND_MENU, SOUND_MENU_FUNCTION);
    }
    
    private static final List<XSound> SOUNDS = new ArrayList<>(XSound.getValues());
    
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
        boolean soundSeedSupported = MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_20_2);
        Menu menu = GUIFactory.newMenu(CustomInventoryType.SOUND_MENU, soundSeedSupported ? 54 : 45, "&d&lSound Reward");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.MAGENTA);
        
        for(int i=0;i<9;i++) {
            menu.setItem(i, glass);
        }
        for(int i=36;i<45;i++) {
            menu.setItem(i, glass);
        }
        menu.setItem(9, glass);
        menu.setItem(18, glass);
        menu.setItem(27, glass);
        menu.setItem(17, glass);
        menu.setItem(26, glass);
        menu.setItem(35, glass);
        
        if(soundSeedSupported) {
            for(int i=45;i<54;i++) {
                menu.setItem(i, glass);
            }
        }
        
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
        
        ItemStack seed = ItemStackWrapper.newItem(XMaterial.DIAMOND)
                                         .setDisplayName("&bSound seed: &5" + (item.getSeed() != null ? item.getSeed() : "RANDOM"))
                                         .addLoreLine("&3Click to reset")
                                         .addLoreLine("")
                                         .addLoreLine("&6Some sounds have different variations.")
                                         .addLoreLine("&6Using a static seed will always play")
                                         .addLoreLine("&6  the same variation for that sound.")
                                         .addLoreLine("")
                                         .addLoreLine("&dThis is available since Minecraft 1.20.2")
                                         .toItemStack();
        
        ItemStack testSound = ItemStackWrapper.newItem(XMaterial.SUNFLOWER)
                                              .setDisplayName("&eTest sound")
                                              .toItemStack();
        ItemStack stopSound = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                              .setDisplayName("&cStop all sounds")
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
        
        if(soundSeedSupported) {
            menu.setItem(37, GUIItem.getPlusLessItem(-100));
            menu.setItem(38, GUIItem.getPlusLessItem(-10));
            menu.setItem(39, GUIItem.getPlusLessItem(-1));
            menu.setItem(40, seed);
            menu.setItem(41, GUIItem.getPlusLessItem(+1));
            menu.setItem(42, GUIItem.getPlusLessItem(+10));
            menu.setItem(43, GUIItem.getPlusLessItem(+100));
        }
        
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
                    EditorBackFunction backFunction = this::openSoundMenu;
                    EditorNextFunction<XSound> nextFunction = (p, sound) -> {
                        item.setType(sound);
                        openSoundMenu(p);
                    };
                    
                    Editor<XSound> editor = EditorType.SOUND_TYPE.getEditor();
                    XSound type = item.getType();
                    if(type == null) {
                        editor.createNewItem(player, backFunction, nextFunction);
                    } else {
                        editor.editExistingItem(type, player, backFunction, nextFunction);
                    }
                    break;
                case 12:
                    //Stop sounds if minecraft version >= 1.10
                    if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_10)) {
                        for(XSound sound : SOUNDS){
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
                case 40:
                    // Reset seed
                    item.setSeed(null);
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
                case 37:
                case 38:
                case 39:
                case 41:
                case 42:
                case 43:
                    if(MinecraftVersion.CURRENT_VERSION.isLessThan(MinecraftVersion.v1_20_2)) {
                        break;
                    }
                    
                    // Modify seed
                    variationValue = Logger.stripColor(ItemStackWrapper.fromItem(e.getCurrentItem(), false).getDisplayName());
                    variation = new BigDecimal(variationValue);
                    
                    Long seed = item.getSeed();
                    if(seed == null) {
                        seed = -1L;
                    }
                    
                    Long newSeed = seed + variation.longValue();
                    if(newSeed < 0L) {
                        newSeed = null;
                    }
                    
                    item.setSeed(newSeed);
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
}
