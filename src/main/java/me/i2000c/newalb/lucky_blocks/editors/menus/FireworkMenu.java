package me.i2000c.newalb.lucky_blocks.editors.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.FireworkEffect;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.lucky_blocks.rewards.types.FireworkReward;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.misc.OtherUtils;

public class FireworkMenu extends EditorMenu<FireworkReward> {
    
    private static final FireworkEffect.Type[] FIREWORK_TYPES = FireworkEffect.Type.values();
    private static final XMaterial[] TYPE_MATERIALS = {
            XMaterial.FIREWORK_STAR,
            XMaterial.FIRE_CHARGE,
            XMaterial.NETHER_STAR,
            XMaterial.MELON_SEEDS,
            XMaterial.CREEPER_HEAD
        };
    
    private static final int[] VALUES = {+1, 0, -1};
    
    private final List<Consumer<Integer>> fireworkPropertyList = new ArrayList<>();
    
    public FireworkMenu() {
        super("&b&lFirework Reward", MenuSize.SIZE_3_ROWS, true);
        
        fireworkPropertyList.add(value -> {
            int amount = OtherUtils.addInRange(item.getAmount(), value, 1, 11);
            item.setAmount(amount);
        });
        fireworkPropertyList.add(value -> {
            int power = OtherUtils.addInRange(item.getPower(), value, 0, 6);
            item.setPower(power);
        });
    }
    
    @Override
    protected void buildMenu(Player player) {
        ItemStack amountItem = ItemStackWrapper.newItem(XMaterial.FIREWORK_ROCKET)
                                               .setAmount(item.getAmount())
                                               .setDisplayName("&3Amount: &b" + item.getAmount())
                                               .toItemStack();
        
        ItemStack powerItem = ItemStackWrapper.newItem(XMaterial.BLAZE_POWDER)
                                              .setAmount(OtherUtils.clamp(item.getPower(), 1, 64))
                                              .setDisplayName("&6Power: &b" + item.getPower())
                                              .toItemStack();
        
        ItemStack withTrail = GUIItem.getBooleanItem(
                item.isWithTrail(), 
                "&5Trail", 
                XMaterial.BLAZE_ROD, 
                XMaterial.BLAZE_ROD);
        
        ItemStack withFlicker = GUIItem.getBooleanItem(
                item.isWithFlicker(), 
                "&5Flicker", 
                XMaterial.TNT, 
                XMaterial.TNT);
        
        ItemStack offsetStack = item.getOffset().getItemToDisplay();
        
        ItemStack fireworkType = ItemStackWrapper.newItem(TYPE_MATERIALS[item.getType().ordinal()])
                                                 .setDisplayName("&aFirework type: &b" + item.getType().name())
                                                 .toItemStack();
        
        //Main color list ItemStacks
        
        ItemStackWrapper wrapper = ItemStackWrapper.newItem(XMaterial.LIME_BANNER);
        wrapper.setDisplayName("&aMain color list");
        if(!item.getColorsHEX().isEmpty()) {
            wrapper.setLore(item.getColorsHEX());
        }
        ItemStack mainColorBanner = wrapper.toItemStack();
        
        ItemStack addMainColor = ItemStackWrapper.newItem(XMaterial.LIME_STAINED_GLASS_PANE)
                                                 .setDisplayName("&aAdd main color")
                                                 .toItemStack();
        
        ItemStack resetMainColors = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                                    .setDisplayName("&cReset main color list")
                                                    .toItemStack();   
        
        //Fade color list ItemStacks
        
        wrapper = ItemStackWrapper.newItem(XMaterial.RED_BANNER);
        wrapper.setDisplayName("&cFade color list");
        if(!item.getFadeColorsHEX().isEmpty()) {
            wrapper.setLore(item.getFadeColorsHEX());
        }
        ItemStack fadeColorBanner = wrapper.toItemStack();
        
        ItemStack addFadeColor = ItemStackWrapper.newItem(XMaterial.LIME_STAINED_GLASS_PANE)
                                                 .setDisplayName("&aAdd fade color (optional)")
                                                 .toItemStack();
        
        ItemStack resetFadeColors = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                                    .setDisplayName("&cReset fade color list")
                                                    .toItemStack();
        
        ItemStack[] resetItems = {amountItem, powerItem};
        
        for(int column=0; column<fireworkPropertyList.size(); column++) {
            int slot = column + 1;
            for(int value : VALUES) {
                ItemStack stack = value == 0 ? resetItems[column] : GUIItem.getPlusLessItem(value);
                Consumer<Integer> propertyModifier = fireworkPropertyList.get(column);
                setItem(slot, stack, e -> {
                    propertyModifier.accept(value);
                    openToPlayer(e.getPlayer());
                });
                slot += MenuSize.SIZE_1_ROW.getSize();
            }
        }
        
        setItem(12, withTrail, e -> {
            item.setWithTrail(!item.isWithTrail());
            openToPlayer(player);
        });
        setItem(13, withFlicker, e -> {
            item.setWithFlicker(!item.isWithFlicker());
            openToPlayer(player);
        });
        setItem(4, offsetStack, e -> {
            OffsetMenu menu = new OffsetMenu();
            menu.setItemToEdit(item.getOffset().clone());
            menu.setOnBack(this::openToPlayer);
            menu.setOnNext((p, offset) -> {
                item.setOffset(offset);
                openToPlayer(player);
            });
            menu.openToPlayer(player);
        });
        
        setItem(14, fireworkType, e -> {
            int type = item.getType().ordinal();
            type = OtherUtils.addInRange(type, 1, 0, FIREWORK_TYPES.length);
            item.setType(FIREWORK_TYPES[type]);
            openToPlayer(player);
        });
        
        setItem(15, mainColorBanner);
        setItem(6, addMainColor, e -> {
            ColorMenu menu = new ColorMenu();
            menu.setOnBack(this::openToPlayer);
            menu.setOnNext((p, color) -> {
                item.getColorsHEX().add(color.getHexColorString());
                openToPlayer(player);
            });
            menu.openToPlayer(player);
        });
        setItem(24, resetMainColors, e -> {
            item.getColorsHEX().clear();
            openToPlayer(player);
        });
        setItem(16, fadeColorBanner);
        setItem(7, addFadeColor, e -> {
            ColorMenu menu = new ColorMenu();
            menu.setOnBack(this::openToPlayer);
            menu.setOnNext((p, color) -> {
                item.getFadeColorsHEX().add(color.getHexColorString());
                openToPlayer(player);
            });
            menu.openToPlayer(player);
        });
        setItem(25, resetFadeColors, e -> {
            item.getFadeColorsHEX().clear();
            openToPlayer(player);
        });
        
        setBackItem(9);
        setNextItem(17, e -> {
            if(!item.getColorsHEX().isEmpty()) {
                onNext(player, item);
            }
        });
    }
}
