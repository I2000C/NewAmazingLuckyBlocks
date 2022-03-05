package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.utils.TypeManager;
import me.i2000c.newalb.custom_outcomes.utils.rewards.BlockReplacingSphereReward;
import me.i2000c.newalb.custom_outcomes.utils.rewards.EffectReward;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.OtherUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

public class BlockReplacingSphereMenu{
    public static BlockReplacingSphereReward reward = null;
    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.BLOCK_REPLACING_SPHERE_MENU, BLOCK_REPLACING_SPHERE_MENU_FUNCTION);
            
            inventoriesRegistered = true;
        }
        
        reward = null;
    }
    
    //BRS inventory
    public static void openBRSMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(reward == null){
            reward = new BlockReplacingSphereReward(FinishMenu.getCurrentOutcome());
        }
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.BLOCK_REPLACING_SPHERE_MENU, 54, Logger.color("&b&lBRS Reward"));
        
        ItemStack glass = XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE.parseItem();
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        
        ItemStack minus1 = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
        meta = minus1.getItemMeta();
        meta.setDisplayName(Logger.color("&c&l-1"));
        minus1.setItemMeta(meta);
        
        ItemStack minus10 = minus1.clone();
        meta = minus10.getItemMeta();
        meta.setDisplayName(Logger.color("&c&l-10"));
        minus10.setItemMeta(meta);
        
        ItemStack minus100 = minus1.clone();
        meta = minus100.getItemMeta();
        meta.setDisplayName(Logger.color("&c&l-100"));
        minus100.setItemMeta(meta);
        
        ItemStack plus1 = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
        meta = minus1.getItemMeta();
        meta.setDisplayName(Logger.color("&a&l+1"));
        plus1.setItemMeta(meta);
        
        ItemStack plus10 = plus1.clone();
        meta = plus10.getItemMeta();
        meta.setDisplayName(Logger.color("&a&l+10"));
        plus10.setItemMeta(meta);
        
        ItemStack plus100 = plus1.clone();
        meta = plus100.getItemMeta();
        meta.setDisplayName(Logger.color("&a&l+100"));
        plus100.setItemMeta(meta);
        
        ItemStack usePlayerLocItem;
        if(reward.isUsePlayerLoc()){
            usePlayerLocItem = XMaterial.PLAYER_HEAD.parseItem();
            meta = usePlayerLocItem.getItemMeta();
            meta.setDisplayName(Logger.color("&aUse player location"));
        }else{
            usePlayerLocItem = TypeManager.getMenuItemStack();
            meta = usePlayerLocItem.getItemMeta();
            meta.setDisplayName(Logger.color("&6Use lucky block location"));
        }
        meta.setLore(Logger.color(Arrays.asList("&3Click to toggle")));
        usePlayerLocItem.setItemMeta(meta);
        
        ItemStack removeMaterialsItem = new ItemStack(Material.BARRIER);
        meta = removeMaterialsItem.getItemMeta();
        meta.setDisplayName(Logger.color("&cClick to remove all materials"));
        removeMaterialsItem.setItemMeta(meta);
        
        ItemStack materialsItem = new ItemStack(Material.DIAMOND_ORE);
        meta = materialsItem.getItemMeta();
        meta.setDisplayName(Logger.color("&6Sphere materials"));
        List<String> lore = new ArrayList<>();
        lore.add("&bLeft click on an item of your inventory");
        lore.add("&b   to add it to the list");
        lore.add("&cRight click on an item of your inventory");
        lore.add("&c   to remove it from the list");
        lore.add("&2Current materials:");
        lore.addAll(reward.getOrderedMaterialList());
        meta.setLore(Logger.color(lore));
        materialsItem.setItemMeta(meta);
        
        ItemStack minRadiusItem = XMaterial.SNOWBALL.parseItem();
        meta = minRadiusItem.getItemMeta();
        meta.setDisplayName(Logger.color("&bMin radius: &6" + reward.getMinRadius()));
        meta.setLore(Logger.color(Arrays.asList("&3Click to reset")));
        minRadiusItem.setItemMeta(meta);
        
        ItemStack maxRadiusItem = new ItemStack(Material.SLIME_BALL);
        meta = maxRadiusItem.getItemMeta();
        meta.setDisplayName(Logger.color("&bMax radius: &6" + reward.getMaxRadius()));
        meta.setLore(Logger.color(Arrays.asList("&3Click to reset")));
        maxRadiusItem.setItemMeta(meta);
        
        ItemStack ticksBetweenLayersItem = XMaterial.CLOCK.parseItem();
        meta = ticksBetweenLayersItem.getItemMeta();
        meta.setDisplayName(Logger.color("&bTicks between layer: &6" + reward.getTicksBetweenLayers()));
        meta.setLore(Logger.color(Arrays.asList("&3Click to reset")));
        ticksBetweenLayersItem.setItemMeta(meta);
        
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName(Logger.color("&7Back"));
        back.setItemMeta(meta);
        
        ItemStack next = new ItemStack(Material.ANVIL);
        meta = next.getItemMeta();
        meta.setDisplayName(Logger.color("&bNext"));
        next.setItemMeta(meta);
        
        for(int i=0;i<9;i++){
            inv.setItem(i, glass);
        }
        for(int i=45;i<54;i++){
            inv.setItem(i, glass);
        }
        for(int i=0;i<54;i+=9){
            inv.setItem(i, glass);
        }
        for(int i=8;i<54;i+=9){
            inv.setItem(i, glass);
        }
        
        inv.setItem(10, back);
        inv.setItem(16, next);
        
        inv.setItem(12, usePlayerLocItem);
        inv.setItem(13, materialsItem);
        inv.setItem(14, removeMaterialsItem);
        
        inv.setItem(19, minus100);
        inv.setItem(20, minus10);
        inv.setItem(21, minus1);
        inv.setItem(22, minRadiusItem);
        inv.setItem(23, plus1);
        inv.setItem(24, plus10);
        inv.setItem(25, plus100);

        inv.setItem(28, minus100);
        inv.setItem(29, minus10);
        inv.setItem(30, minus1);
        inv.setItem(31, maxRadiusItem);
        inv.setItem(32, plus1);
        inv.setItem(33, plus10);
        inv.setItem(34, plus100);
        
        inv.setItem(37, minus100);
        inv.setItem(38, minus10);
        inv.setItem(39, minus1);
        inv.setItem(40, ticksBetweenLayersItem);
        inv.setItem(41, plus1);
        inv.setItem(42, plus10);
        inv.setItem(43, plus100);
            
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction BLOCK_REPLACING_SPHERE_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            switch(e.getSlot()){
                case 10:
                    //Return to the previous menu
                    if(FinishMenu.editMode){
                        FinishMenu.openFinishInventory(p);
                    }else{
                        RewardTypesMenu.openRewardTypesMenu(p);
                    }
                    break;
                case 16:
                    //Open next menu
                    if(!reward.emptyMaterialList()){
                        FinishMenu.addReward(reward);
                        reset();
                        FinishMenu.openFinishInventory(p);
                    }
                    break;
                case 12:
                    //Toggle use player location
                    reward.setUsePlayerLoc(!reward.isUsePlayerLoc());
                    openBRSMenu(p);
                    break;
                case 14:
                    //Remove all materials
                    reward.clearMaterials();
                    openBRSMenu(p);
                    break;
                //<editor-fold defaultstate="collapsed" desc="MinRadius">
                case 19:
                    //min radius -100
                    int min_radius = reward.getMinRadius();
                    min_radius -= 100;
                    if(min_radius < 0){
                        min_radius = 0;
                    }
                    reward.setMinRadius(min_radius);
                    openBRSMenu(p);
                    break;
                case 20:
                    //min radius -10
                    min_radius = reward.getMinRadius();
                    min_radius -= 10;
                    if(min_radius < 0){
                        min_radius = 0;
                    }
                    reward.setMinRadius(min_radius);
                    openBRSMenu(p);
                    break;
                case 21:
                    //min radius -1
                    min_radius = reward.getMinRadius();
                    min_radius--;
                    if(min_radius < 0){
                        min_radius = 0;
                    }
                    reward.setMinRadius(min_radius);
                    openBRSMenu(p);
                    break;
                case 22:
                    //min radius = 0
                    min_radius = 0;
                    reward.setMinRadius(min_radius);
                    openBRSMenu(p);
                    break;
                case 23:
                    //min radius +1
                    min_radius = reward.getMinRadius();
                    min_radius++;
                    if(min_radius > reward.getMaxRadius()){
                        min_radius = reward.getMaxRadius();
                    }
                    reward.setMinRadius(min_radius);
                    openBRSMenu(p);
                    break;
                case 24:
                    //min radius +10
                    min_radius = reward.getMinRadius();
                    min_radius += 10;
                    if(min_radius > reward.getMaxRadius()){
                        min_radius = reward.getMaxRadius();
                    }
                    reward.setMinRadius(min_radius);
                    openBRSMenu(p);
                    break;
                case 25:
                    //min radius +100
                    min_radius = reward.getMinRadius();
                    min_radius += 100;
                    if(min_radius > reward.getMaxRadius()){
                        min_radius = reward.getMaxRadius();
                    }
                    reward.setMinRadius(min_radius);
                    openBRSMenu(p);
                    break;
//</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="MaxRadius">
                case 28:
                    //max radius -100
                    int max_radius = reward.getMaxRadius();
                    max_radius -= 100;
                    if(max_radius < reward.getMinRadius()){
                        max_radius = reward.getMinRadius();
                    }
                    reward.setMaxRadius(max_radius);
                    openBRSMenu(p);
                    break;
                case 29:
                    //max radius -10
                    max_radius = reward.getMaxRadius();
                    max_radius -= 10;
                    if(max_radius < reward.getMinRadius()){
                        max_radius = reward.getMinRadius();
                    }
                    reward.setMaxRadius(max_radius);
                    openBRSMenu(p);
                    break;
                case 30:
                    //max radius -1
                    max_radius = reward.getMaxRadius();
                    max_radius--;
                    if(max_radius < reward.getMinRadius()){
                        max_radius = reward.getMinRadius();
                    }
                    reward.setMaxRadius(max_radius);
                    openBRSMenu(p);
                    break;
                case 31:
                    //max radius = 5
                    max_radius = 5;                    
                    if(max_radius < reward.getMinRadius()){
                        max_radius = reward.getMinRadius();
                    }
                    reward.setMaxRadius(max_radius);
                    openBRSMenu(p);
                    break;
                case 32:
                    //max radius +1
                    max_radius = reward.getMaxRadius();
                    max_radius++;
                    if(max_radius < reward.getMinRadius()){
                        max_radius = reward.getMinRadius();
                    }
                    reward.setMaxRadius(max_radius);
                    openBRSMenu(p);
                    break;
                case 33:
                    //max radius +10
                    max_radius = reward.getMaxRadius();
                    max_radius += 10;
                    if(max_radius < reward.getMinRadius()){
                        max_radius = reward.getMinRadius();
                    }
                    reward.setMaxRadius(max_radius);
                    openBRSMenu(p);
                    break;
                case 34:
                    //max radius +100
                    max_radius = reward.getMaxRadius();
                    max_radius += 100;
                    if(max_radius < reward.getMinRadius()){
                        max_radius = reward.getMinRadius();
                    }
                    reward.setMaxRadius(max_radius);
                    openBRSMenu(p);
                    break;
//</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="TicksBetweenLayers">
                case 37:
                    //TicksBetweenLayers -100
                    int ticks = reward.getTicksBetweenLayers();
                    ticks -= 100;
                    if(ticks < 0){
                        ticks = 0;
                    }
                    reward.setTicksBetweenLayers(ticks);
                    openBRSMenu(p);
                    break;
                case 38:
                    //TicksBetweenLayers -10
                    ticks = reward.getTicksBetweenLayers();
                    ticks -= 10;
                    if(ticks < 0){
                        ticks = 0;
                    }
                    reward.setTicksBetweenLayers(ticks);
                    openBRSMenu(p);
                    break;
                case 39:
                    //TicksBetweenLayers -1
                    ticks = reward.getTicksBetweenLayers();
                    ticks--;
                    if(ticks < 0){
                        ticks = 0;
                    }
                    reward.setTicksBetweenLayers(ticks);
                    openBRSMenu(p);
                    break;
                case 40:
                    //TicksBetweenLayers = 10
                    ticks = 10;
                    reward.setTicksBetweenLayers(ticks);
                    openBRSMenu(p);
                    break;
                case 41:
                    //TicksBetweenLayers +1
                    ticks = reward.getTicksBetweenLayers();
                    ticks++;
                    reward.setTicksBetweenLayers(ticks);
                    openBRSMenu(p);
                    break;
                case 42:
                    //TicksBetweenLayers +10
                    ticks = reward.getTicksBetweenLayers();
                    ticks += 10;
                    reward.setTicksBetweenLayers(ticks);
                    openBRSMenu(p);
                    break;
                case 43:
                    //TicksBetweenLayers +100
                    ticks = reward.getTicksBetweenLayers();
                    ticks += 100;
                    reward.setTicksBetweenLayers(ticks);
                    openBRSMenu(p);
                    break;
//</editor-fold>

            }
        }else{
            if(e.getCurrentItem() != null && e.getCurrentItem().getType().isBlock()){
                ItemStack stack = new ItemStack(e.getCurrentItem().getType());
                if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                    stack.setDurability(e.getCurrentItem().getDurability());
                }
                if(e.getClick() == ClickType.LEFT){
                    reward.addItemStack(stack);
                }else if(e.getClick() == ClickType.RIGHT){
                    reward.removeItemStack(stack);
                }                
                openBRSMenu(p);
            }
        }
//</editor-fold>
    };
}
