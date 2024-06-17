package me.i2000c.newalb.listeners.interact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.i2000c.newalb.listeners.objects.AutoBow;
import me.i2000c.newalb.listeners.objects.DarkHole;
import me.i2000c.newalb.listeners.objects.EndermanSoup;
import me.i2000c.newalb.listeners.objects.ExplosiveBow;
import me.i2000c.newalb.listeners.objects.HomingBow;
import me.i2000c.newalb.listeners.objects.HookBow;
import me.i2000c.newalb.listeners.objects.HotPotato;
import me.i2000c.newalb.listeners.objects.IceBow;
import me.i2000c.newalb.listeners.objects.ItemStealer;
import me.i2000c.newalb.listeners.objects.LuckyTool;
import me.i2000c.newalb.listeners.objects.MiniVolcano;
import me.i2000c.newalb.listeners.objects.MultiBow;
import me.i2000c.newalb.listeners.objects.PlayerTracker;
import me.i2000c.newalb.listeners.objects.SwappingBall;
import me.i2000c.newalb.listeners.wands.FireWand;
import me.i2000c.newalb.listeners.wands.FrostPathWand;
import me.i2000c.newalb.listeners.wands.InvisibilityWand;
import me.i2000c.newalb.listeners.wands.LightningWand;
import me.i2000c.newalb.listeners.wands.PotionWand;
import me.i2000c.newalb.listeners.wands.RegenWand;
import me.i2000c.newalb.listeners.wands.ShieldWand;
import me.i2000c.newalb.listeners.wands.SlimeWand;
import me.i2000c.newalb.listeners.wands.TntWand;
import me.i2000c.newalb.reflection.RefClass;
import me.i2000c.newalb.reflection.RefField;
import me.i2000c.newalb.utils2.NBTUtils;
import org.bukkit.inventory.ItemStack;

public class SpecialItems {
    private static final List<SpecialItem> ITEMS_BY_ID = new ArrayList<>();
    private static final Map<String, SpecialItem> ITEMS_BY_NAME = new HashMap<>();
    private static final List<String> ITEMS_NAMES = new ArrayList<>();
    
    static int GLOBAL_ID = 0;
    
    ////////////////////////////////////////
    // Begin of special items declaration //
    ////////////////////////////////////////
    
    public static final LuckyTool lucky_tool = new LuckyTool();
    
    public static final RegenWand regen_wand = new RegenWand();
    public static final InvisibilityWand invisibility_wand = new InvisibilityWand();
    public static final TntWand tnt_wand = new TntWand();
    public static final SlimeWand slime_wand = new SlimeWand();
    public static final FireWand fire_wand = new FireWand();
    public static final LightningWand lightning_wand = new LightningWand();
    public static final ShieldWand shield_wand = new ShieldWand();
    public static final PotionWand potion_wand = new PotionWand();
    public static final FrostPathWand frost_path_wand = new FrostPathWand();
    
    public static final DarkHole dark_hole = new DarkHole();
    public static final MiniVolcano mini_volcano = new MiniVolcano();
    public static final PlayerTracker player_tracker = new PlayerTracker();
    public static final EndermanSoup enderman_soup = new EndermanSoup();
    public static final HotPotato hot_potato = new HotPotato();
    public static final IceBow ice_bow = new IceBow();
    public static final AutoBow auto_bow = new AutoBow();
    public static final MultiBow multi_bow = new MultiBow();
    public static final ExplosiveBow explosive_bow = new ExplosiveBow();
    public static final HomingBow homing_bow = new HomingBow();
    public static final HookBow hook_bow = new HookBow();
    public static final SwappingBall swapping_ball = new SwappingBall();
    public static final ItemStealer item_stealer = new ItemStealer();
    
    //////////////////////////////////////
    // End of special items declaration //
    //////////////////////////////////////
    
    static {
        for(RefField field : RefClass.of(SpecialItems.class).getFields()) {
            Object object = field.getStaticValue();
            if(object instanceof SpecialItem) {
                SpecialItem specialItem = (SpecialItem) object;
                ITEMS_NAMES.add(specialItem.getName());
                ITEMS_BY_ID.add(specialItem);
                ITEMS_BY_NAME.put(specialItem.getName(), specialItem);
            }
        }
    }
    
    public static SpecialItem getById(int id) {
        if(id < 0 || id >= ITEMS_BY_ID.size()) {
            return null;
        } else {
            return ITEMS_BY_ID.get(id);
        }
    }
    
    public static SpecialItem getByName(String name) {
        return ITEMS_BY_NAME.get(name);
    }
    
    public static SpecialItem getByItemStack(ItemStack stack) {
        if(stack == null || !stack.hasItemMeta()) {
            return null;
        }
        
        if(NBTUtils.contains(stack, SpecialItem.ITEM_TAG)) {
            int itemID = NBTUtils.getInt(stack, SpecialItem.ITEM_TAG);
            return getById(itemID);
        } else {
            return null;
        }
    }
    
    public static List<String> getItemsNames() {
        return ITEMS_NAMES;
    }
    
    public static int getNumberOfItems() {
        return ITEMS_BY_ID.size();
    }
    public static List<SpecialItem> getAllItems() {
        return ITEMS_BY_ID;
    }
    public static List<SpecialItem> getWands() {
        return ITEMS_BY_ID.stream()
                          .filter(item -> item.isWand())
                          .collect(Collectors.toList());
    }
    public static List<SpecialItem> getObjects() {
        return ITEMS_BY_ID.stream()
                          .filter(item -> !item.isWand())
                          .collect(Collectors.toList());
    }
    
    public static void loadItems() {
        ITEMS_BY_ID.forEach(item -> item.loadItem());
    }
}
