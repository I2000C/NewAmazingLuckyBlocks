package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.BlockReplacingSphereReward;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class BlockReplacingSphereMenu extends Editor<BlockReplacingSphereReward>{
    public BlockReplacingSphereMenu(){
        InventoryListener.registerInventory(CustomInventoryType.BLOCK_REPLACING_SPHERE_MENU, BLOCK_REPLACING_SPHERE_MENU_FUNCTION);
    }
    
    @Override
    public void newItem(Player player){
        Outcome outcome = RewardListMenu.getCurrentOutcome();
        item = new BlockReplacingSphereReward(outcome);
        openBRSMenu(player);
    }
    
    @Override
    public void editItem(Player player){
        openBRSMenu(player);
    }
    
    private void openBRSMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">        
        Menu menu = GUIFactory.newMenu(CustomInventoryType.BLOCK_REPLACING_SPHERE_MENU, 54, "&b&lBRS Reward");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.LIGHT_BLUE);
        
        ItemStack usePlayerLocItem = GUIItem.getUsePlayerLocItem(item.isUsePlayerLoc());
        
        ItemStack replaceLiquids = GUIItem.getBooleanItem(
                item.isReplaceLiquids(), 
                "&bReplace liquids", 
                XMaterial.WATER_BUCKET, 
                XMaterial.BUCKET);
        
        ItemStack removeMaterialsItem = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                                        .setDisplayName("&cClick to remove all materials")
                                                        .toItemStack();
        
        ItemStack materialsItem = ItemStackWrapper.newItem(XMaterial.DIAMOND_ORE)
                                                  .setDisplayName("&6Sphere materials")
                                                  .addLoreLine("&bLeft click on an item of your inventory")
                                                  .addLoreLine("&b   to add it to the list")
                                                  .addLoreLine("&cRight click on an item of your inventory")
                                                  .addLoreLine("&c   to remove it from the list")
                                                  .addLoreLine("&2Current materials:")
                                                  .addLore(item.getSortedMaterialList())
                                                  .toItemStack();
        
        ItemStack minRadiusItem = ItemStackWrapper.newItem(XMaterial.SNOWBALL)
                                                  .setDisplayName("&bMin radius: &6" + item.getMinRadius())
                                                  .addLoreLine("&3Click to reset")
                                                  .toItemStack();
        
        ItemStack maxRadiusItem = ItemStackWrapper.newItem(XMaterial.SLIME_BALL)
                                                  .setDisplayName("&bMax radius: &6" + item.getMaxRadius())
                                                  .addLoreLine("&3Click to reset")
                                                  .toItemStack();
        
        ItemStack ticksBetweenLayersItem = ItemStackWrapper.newItem(XMaterial.CLOCK)
                                                           .setDisplayName("&bTicks between layers: &6" + item.getTicksBetweenLayers())
                                                           .addLoreLine("&3Click to reset")
                                                           .toItemStack();
        
        for(int i=0;i<9;i++){
            menu.setItem(i, glass);
        }
        for(int i=45;i<54;i++){
            menu.setItem(i, glass);
        }
        for(int i=0;i<54;i+=9){
            menu.setItem(i, glass);
        }
        for(int i=8;i<54;i+=9){
            menu.setItem(i, glass);
        }
        
        menu.setItem(10, GUIItem.getBackItem());
        menu.setItem(16, GUIItem.getNextItem());
        
        menu.setItem(4, removeMaterialsItem);
        
        menu.setItem(12, usePlayerLocItem);
        menu.setItem(13, materialsItem);
        menu.setItem(14, replaceLiquids);
        
        menu.setItem(19, GUIItem.getPlusLessItem(-100));
        menu.setItem(20, GUIItem.getPlusLessItem(-10));
        menu.setItem(21, GUIItem.getPlusLessItem(-1));
        menu.setItem(22, minRadiusItem);
        menu.setItem(23, GUIItem.getPlusLessItem(+1));
        menu.setItem(24, GUIItem.getPlusLessItem(+10));
        menu.setItem(25, GUIItem.getPlusLessItem(+100));

        menu.setItem(28, GUIItem.getPlusLessItem(-100));
        menu.setItem(29, GUIItem.getPlusLessItem(-10));
        menu.setItem(30, GUIItem.getPlusLessItem(-1));
        menu.setItem(31, maxRadiusItem);
        menu.setItem(32, GUIItem.getPlusLessItem(+1));
        menu.setItem(33, GUIItem.getPlusLessItem(+10));
        menu.setItem(34, GUIItem.getPlusLessItem(+100));
        
        menu.setItem(37, GUIItem.getPlusLessItem(-100));
        menu.setItem(38, GUIItem.getPlusLessItem(-10));
        menu.setItem(39, GUIItem.getPlusLessItem(-1));
        menu.setItem(40, ticksBetweenLayersItem);
        menu.setItem(41, GUIItem.getPlusLessItem(+1));
        menu.setItem(42, GUIItem.getPlusLessItem(+10));
        menu.setItem(43, GUIItem.getPlusLessItem(+100));
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction BLOCK_REPLACING_SPHERE_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 10:
                    //Return to the previous menu
                    onBack.accept(player);
                    break;
                case 16:
                    //Open next menu
                    if(!item.isEmptyMaterialList()){
                        onNext.accept(player, item);
                    }
                    break;
                case 12:
                    //Toggle use player location
                    item.setUsePlayerLoc(!item.isUsePlayerLoc());
                    openBRSMenu(player);
                    break;
                case 14:
                    //Toggle replace liquids option
                    item.setReplaceLiquids(!item.isReplaceLiquids());
                    openBRSMenu(player);
                    break;
                case 4:
                    //Remove all materials
                    item.clearMaterials();
                    openBRSMenu(player);
                    break;
                //<editor-fold defaultstate="collapsed" desc="MinRadius">
                case 19:
                    //min radius -100
                    int min_radius = item.getMinRadius();
                    min_radius -= 100;
                    if(min_radius < 0){
                        min_radius = 0;
                    }
                    item.setMinRadius(min_radius);
                    openBRSMenu(player);
                    break;
                case 20:
                    //min radius -10
                    min_radius = item.getMinRadius();
                    min_radius -= 10;
                    if(min_radius < 0){
                        min_radius = 0;
                    }
                    item.setMinRadius(min_radius);
                    openBRSMenu(player);
                    break;
                case 21:
                    //min radius -1
                    min_radius = item.getMinRadius();
                    min_radius--;
                    if(min_radius < 0){
                        min_radius = 0;
                    }
                    item.setMinRadius(min_radius);
                    openBRSMenu(player);
                    break;
                case 22:
                    //min radius = 0
                    min_radius = 0;
                    item.setMinRadius(min_radius);
                    openBRSMenu(player);
                    break;
                case 23:
                    //min radius +1
                    min_radius = item.getMinRadius();
                    min_radius++;
                    if(min_radius > item.getMaxRadius()){
                        min_radius = item.getMaxRadius();
                    }
                    item.setMinRadius(min_radius);
                    openBRSMenu(player);
                    break;
                case 24:
                    //min radius +10
                    min_radius = item.getMinRadius();
                    min_radius += 10;
                    if(min_radius > item.getMaxRadius()){
                        min_radius = item.getMaxRadius();
                    }
                    item.setMinRadius(min_radius);
                    openBRSMenu(player);
                    break;
                case 25:
                    //min radius +100
                    min_radius = item.getMinRadius();
                    min_radius += 100;
                    if(min_radius > item.getMaxRadius()){
                        min_radius = item.getMaxRadius();
                    }
                    item.setMinRadius(min_radius);
                    openBRSMenu(player);
                    break;
//</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="MaxRadius">
                case 28:
                    //max radius -100
                    int max_radius = item.getMaxRadius();
                    max_radius -= 100;
                    if(max_radius < item.getMinRadius()){
                        max_radius = item.getMinRadius();
                    }
                    item.setMaxRadius(max_radius);
                    openBRSMenu(player);
                    break;
                case 29:
                    //max radius -10
                    max_radius = item.getMaxRadius();
                    max_radius -= 10;
                    if(max_radius < item.getMinRadius()){
                        max_radius = item.getMinRadius();
                    }
                    item.setMaxRadius(max_radius);
                    openBRSMenu(player);
                    break;
                case 30:
                    //max radius -1
                    max_radius = item.getMaxRadius();
                    max_radius--;
                    if(max_radius < item.getMinRadius()){
                        max_radius = item.getMinRadius();
                    }
                    item.setMaxRadius(max_radius);
                    openBRSMenu(player);
                    break;
                case 31:
                    //max radius = 5
                    max_radius = 5;                    
                    if(max_radius < item.getMinRadius()){
                        max_radius = item.getMinRadius();
                    }
                    item.setMaxRadius(max_radius);
                    openBRSMenu(player);
                    break;
                case 32:
                    //max radius +1
                    max_radius = item.getMaxRadius();
                    max_radius++;
                    if(max_radius < item.getMinRadius()){
                        max_radius = item.getMinRadius();
                    }
                    item.setMaxRadius(max_radius);
                    openBRSMenu(player);
                    break;
                case 33:
                    //max radius +10
                    max_radius = item.getMaxRadius();
                    max_radius += 10;
                    if(max_radius < item.getMinRadius()){
                        max_radius = item.getMinRadius();
                    }
                    item.setMaxRadius(max_radius);
                    openBRSMenu(player);
                    break;
                case 34:
                    //max radius +100
                    max_radius = item.getMaxRadius();
                    max_radius += 100;
                    if(max_radius < item.getMinRadius()){
                        max_radius = item.getMinRadius();
                    }
                    item.setMaxRadius(max_radius);
                    openBRSMenu(player);
                    break;
//</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="TicksBetweenLayers">
                case 37:
                    //TicksBetweenLayers -100
                    int ticks = item.getTicksBetweenLayers();
                    ticks -= 100;
                    if(ticks < 0){
                        ticks = 0;
                    }
                    item.setTicksBetweenLayers(ticks);
                    openBRSMenu(player);
                    break;
                case 38:
                    //TicksBetweenLayers -10
                    ticks = item.getTicksBetweenLayers();
                    ticks -= 10;
                    if(ticks < 0){
                        ticks = 0;
                    }
                    item.setTicksBetweenLayers(ticks);
                    openBRSMenu(player);
                    break;
                case 39:
                    //TicksBetweenLayers -1
                    ticks = item.getTicksBetweenLayers();
                    ticks--;
                    if(ticks < 0){
                        ticks = 0;
                    }
                    item.setTicksBetweenLayers(ticks);
                    openBRSMenu(player);
                    break;
                case 40:
                    //TicksBetweenLayers = 10
                    ticks = 10;
                    item.setTicksBetweenLayers(ticks);
                    openBRSMenu(player);
                    break;
                case 41:
                    //TicksBetweenLayers +1
                    ticks = item.getTicksBetweenLayers();
                    ticks++;
                    item.setTicksBetweenLayers(ticks);
                    openBRSMenu(player);
                    break;
                case 42:
                    //TicksBetweenLayers +10
                    ticks = item.getTicksBetweenLayers();
                    ticks += 10;
                    item.setTicksBetweenLayers(ticks);
                    openBRSMenu(player);
                    break;
                case 43:
                    //TicksBetweenLayers +100
                    ticks = item.getTicksBetweenLayers();
                    ticks += 100;
                    item.setTicksBetweenLayers(ticks);
                    openBRSMenu(player);
                    break;
//</editor-fold>

            }
        }else if(e.getLocation() == InventoryLocation.BOTTOM){
            ItemStack stack = e.getCurrentItem();
            if(stack == null){
                return;
            }
            
            XMaterial material = null;
            if(stack.getType().isBlock()){
                material = XMaterial.matchXMaterial(stack);
            }else switch(XMaterial.matchXMaterial(stack.getType())){
                case WATER_BUCKET:  material = XMaterial.WATER; break;
                case LAVA_BUCKET:   material = XMaterial.LAVA;  break;
                case FIRE_CHARGE:   material = XMaterial.FIRE;  break;
            }
            
            if(material != null) {
                if(e.getClick() == ClickType.LEFT)       item.addMaterial(material);
                else if(e.getClick() == ClickType.RIGHT) item.removeMaterial(material);
                
                openBRSMenu(player);
            }
        }
//</editor-fold>
    };
}
