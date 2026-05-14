package me.i2000c.newalb.lucky_blocks.editors.menus.sound;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;

import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.api.version.MinecraftVersion;
import me.i2000c.newalb.lucky_blocks.editors.utils.SoundTreeNode;
import me.i2000c.newalb.lucky_blocks.rewards.types.SoundReward;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.misc.OtherUtils;

public class SoundMenu extends EditorMenu<SoundReward> {
    
    private static final boolean IS_SOUND_SEED_SUPPORTED = MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_20_2);
    
    private static final String[][] VALUES = {{"-1", "-0.5", "-0.1", "0", "+0.1", "+0.5", "+1"}, 
                                              {"-0.1", "-0.05", "-0.01", "0", "+0.01", "+0.05", "+0.1"},
                                              {"-100", "-10", "-1", "0", "+1", "+10", "+100"}};
    
    private final List<Consumer<BigDecimal>> soundPropertyList = new ArrayList<>();
    
    public SoundMenu() {
        super("&d&lSound Reward", IS_SOUND_SEED_SUPPORTED ? MenuSize.SIZE_6_ROWS : MenuSize.SIZE_5_ROWS, true);
        
        soundPropertyList.add(value -> {
            if(value.equals(BigDecimal.ZERO)) {
                item.setVolume(SoundReward.DEFAULT_VOLUME);
            } else {
                item.setVolume(OtherUtils.clamp(item.getVolume().add(value), SoundReward.MIN_VOLUME, SoundReward.MAX_VOLUME));
            }
        });
        soundPropertyList.add(value -> {
            if(value.equals(BigDecimal.ZERO)) {
                item.setPitch(SoundReward.DEFAULT_PITCH);
            } else {
                item.setPitch(OtherUtils.clamp(item.getPitch().add(value), SoundReward.MIN_PITCH, SoundReward.MAX_PITCH));
            }
        });
        soundPropertyList.add(value -> {
            if(value.equals(BigDecimal.ZERO)) {
                item.setSeed(null);
            } else {
                long currentSeed = item.getSeed() != null ? item.getSeed() : -1L;
                long newSeed = currentSeed + value.longValue();
                item.setSeed(newSeed >= 0L ? newSeed : null);
            }
        });
    }
    
    @Override
    protected void buildMenu(Player player) {
        addGlassBorder(GlassColor.MAGENTA);
        
        ItemStackWrapper builder = ItemStackWrapper.newItem(XMaterial.NOTE_BLOCK);
        if(item.getType() == null) {
            builder.setDisplayName("&6Select sound type");
        } else {
            builder.setDisplayName("&6Selected type: &3" + item.getType().name());
        }
        ItemStack sound = builder.toItemStack();
        
        ItemStack volume = ItemStackWrapper.newItem(XMaterial.EMERALD)
                                           .setDisplayName(String.format(Locale.ENGLISH, "&aSound volume: &5%.2f", item.getVolume()))
                                           .addLoreLine("&3Click to reset")
                                           .addLoreLine("")
                                           .addLoreLine(String.format(Locale.ENGLISH, "&6Min volume: &d%.2f", SoundReward.MIN_VOLUME))
                                           .addLoreLine("")
                                           .addLoreLine("&2Note that if volume is greater than &l1.0&r&2,")
                                           .addLoreLine("&2  the selected sound won't be louder,")
                                           .addLoreLine("&2  but will be heard from further away")
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
        
        setBackItem(10);
        setNextItem(16, e -> {
            if(item.getType() != null) {
                onNext(player, item);
            }
        });
        
        setItem(13, sound, e -> {
            SoundTypeMenu menu = new SoundTypeMenu();
            if(item.getType() != null) {
                menu.setItemToEdit(SoundTreeNode.getNode(item.getType()));
            }
            menu.setOnBack(this::openToPlayer);
            menu.setOnNext((p, node) -> {
                item.setType(node.getSound());
                openToPlayer(player);
            });
            menu.openToPlayer(player);
        });
        
        if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_10)) {
            setItem(12, stopSound, e -> XSound.getValues().forEach(s -> s.stopSound(player)));
        }
        setItem(14, testSound, e -> item.execute(player, player.getLocation()));
        
        ItemStack[] resetItems = {volume, pitch, seed};
        
        for(int row=0; row<soundPropertyList.size(); row++) {
            if(row == 2 && !IS_SOUND_SEED_SUPPORTED) {
                continue;
            }
            
            int slot = row * 9 + 19;
            for(String value : VALUES[row]) {
                int precision = row == 2 ? 0 : 2;
                ItemStack stack = value.equals("0") ? resetItems[row] : GUIItem.getPlusLessItem(value, precision);
                Consumer<BigDecimal> propertyModifier = soundPropertyList.get(row);
                setItem(slot++, stack, e -> {
                    propertyModifier.accept(new BigDecimal(value));
                    openToPlayer(e.getPlayer());
                });
            }
        }
    }
}
