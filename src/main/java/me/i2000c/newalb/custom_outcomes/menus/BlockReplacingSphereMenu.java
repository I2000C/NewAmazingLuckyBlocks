package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.utils.rewards.BlockReplacingSphereReward;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.BLOCK_REPLACING_SPHERE_MENU, 54, "&b&lBRS Reward");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.LIGHT_BLUE);
        
        ItemStack minus1 = ItemBuilder.newItem(XMaterial.RED_STAINED_GLASS_PANE)
                .withDisplayName("&c&l-1")
                .build();
        
        ItemStack minus10 = ItemBuilder.newItem(XMaterial.RED_STAINED_GLASS_PANE)
                .withDisplayName("&c&l-10")
                .build();
        
        ItemStack minus100 = ItemBuilder.newItem(XMaterial.RED_STAINED_GLASS_PANE)
                .withDisplayName("&c&l-100")
                .build();
        
        ItemStack plus1 = ItemBuilder.newItem(XMaterial.LIME_STAINED_GLASS_PANE)
                .withDisplayName("&a&l+1")
                .build();
        
        ItemStack plus10 = ItemBuilder.newItem(XMaterial.LIME_STAINED_GLASS_PANE)
                .withDisplayName("&a&l+10")
                .build();
        
        ItemStack plus100 = ItemBuilder.newItem(XMaterial.LIME_STAINED_GLASS_PANE)
                .withDisplayName("&a&l+100")
                .build();
        
        ItemStack usePlayerLocItem = GUIItem.getUsePlayerLocItem(reward.isUsePlayerLoc());
        
        ItemStack replaceLiquids;
        if(reward.isReplaceLiquids()){
            replaceLiquids = ItemBuilder.newItem(XMaterial.WATER_BUCKET)
                    .withDisplayName("&bReplace liquids: &atrue")
                    .addLoreLine("&3Click to toggle")
                    .build();
        }else{
            replaceLiquids = ItemBuilder.newItem(XMaterial.WATER_BUCKET)
                    .withDisplayName("&bReplace liquids: &cfalse")
                    .addLoreLine("&3Click to toggle")
                    .build();
        }
        
        ItemStack removeMaterialsItem = ItemBuilder.newItem(XMaterial.BARRIER)
                .withDisplayName("&cClick to remove all materials")
                .build();
        
        ItemStack materialsItem = ItemBuilder.newItem(XMaterial.DIAMOND_ORE)
                .withDisplayName("&6Sphere materials")
                .addLoreLine("&bLeft click on an item of your inventory")
                .addLoreLine("&b   to add it to the list")
                .addLoreLine("&cRight click on an item of your inventory")
                .addLoreLine("&c   to remove it from the list")
                .addLoreLine("&2Current materials:")
                .addLore(reward.getOrderedMaterialList())
                .build();
        
        ItemStack minRadiusItem = ItemBuilder.newItem(XMaterial.SNOWBALL)
                .withDisplayName("&bMin radius: &6" + reward.getMinRadius())
                .addLoreLine("&3Click to reset")
                .build();
        
        ItemStack maxRadiusItem = ItemBuilder.newItem(XMaterial.SLIME_BALL)
                .withDisplayName("&bMax radius: &6" + reward.getMaxRadius())
                .addLoreLine("&3Click to reset")
                .build();
        
        ItemStack ticksBetweenLayersItem = ItemBuilder.newItem(XMaterial.CLOCK)
                .withDisplayName("&bTicks between layer: &6" + reward.getTicksBetweenLayers())
                .addLoreLine("&3Click to reset")
                .build();
        
        
        ItemStack back = GUIItem.getBackItem();
        
        ItemStack next = GUIItem.getNextItem();
        
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
        
        inv.setItem(4, removeMaterialsItem);
        
        inv.setItem(12, usePlayerLocItem);
        inv.setItem(13, materialsItem);
        inv.setItem(14, replaceLiquids);
        
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
                    //Toggle replace liquids option
                    reward.setReplaceLiquids(!reward.isReplaceLiquids());
                    openBRSMenu(p);
                    break;
                case 4:
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
