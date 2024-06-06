package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.MiniVolcanoReward;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MiniVolcanoMenu extends Editor<MiniVolcanoReward>{
    public MiniVolcanoMenu(){
        InventoryListener.registerInventory(CustomInventoryType.MINI_VOLCANO_MENU, MINI_VOLCANO_MENU_FUNCTION);
    }
    
    @Override
    protected void newItem(Player player){
        Outcome outcome = RewardListMenu.getCurrentOutcome();
        item = new MiniVolcanoReward(outcome);
        openMiniVolcanoMenu(player);
    }
    
    @Override
    protected void editItem(Player player){
        openMiniVolcanoMenu(player);
    }
    
    private void openMiniVolcanoMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.MINI_VOLCANO_MENU, 45, "&c&lMiniVolcano Rewards");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.ORANGE);
        
        for(int i=0;i<9;i++){
            menu.setItem(i, glass);
        }
        for(int i=36;i<45;i++){
            menu.setItem(i, glass);
        }
        menu.setItem(9, glass);
        menu.setItem(17, glass);
        menu.setItem(27, glass);
        menu.setItem(35, glass);        
        
        ItemStack squaredStack = GUIItem.getBooleanItem(
                item.isSquared(), 
                "&bSquared", 
                XMaterial.SNOW_BLOCK, 
                XMaterial.SNOWBALL);
        
        ItemStack heightStack = ItemStackWrapper.newItem(XMaterial.LADDER)
                                                .setDisplayName("&3Height: &b" + item.getHeight())
                                                .addLoreLine("&2Click to reset")
                                                .toItemStack();
        
        ItemStack ticksStack = ItemStackWrapper.newItem(XMaterial.CLOCK)
                                               .setDisplayName("&5Ticks between blocks: &6" + item.getTicks())
                                               .addLoreLine("&3Click to reset")
                                               .toItemStack();
        
        ItemStackWrapper wrapper = ItemStackWrapper.newItem(item.getBaseMaterial());
        wrapper.setDisplayName("&6Base material: &b" + item.getBaseMaterial().name());
        wrapper.addLoreLine("&3Click on a &3&lblock &3of your inventory");
        wrapper.addLoreLine("&3to change it");
        ItemStack baseMaterialStack = wrapper.toItemStack();
        
        ItemStack lavaMaterialStack;
        switch(item.getLavaMaterial()){
            case LAVA:  lavaMaterialStack = new ItemStack(Material.LAVA_BUCKET);  break;
            case WATER: lavaMaterialStack = new ItemStack(Material.WATER_BUCKET); break;
            default:    lavaMaterialStack = item.getLavaMaterial().parseItem();   break;
        }
        ItemStackWrapper.fromItem(lavaMaterialStack, false)
                        .setDisplayName("&cLava material: &b" + item.getLavaMaterial().name())
                        .addLoreLine("&3Click to change");
        
        ItemStack throwBlocksStack = GUIItem.getBooleanItem(
                item.isThrowBlocks(), 
                "&cThrow blocks", 
                XMaterial.FIRE_CHARGE, 
                XMaterial.LAPIS_BLOCK);
        
        menu.setItem(18, GUIItem.getBackItem());
        menu.setItem(26, GUIItem.getNextItem());
        
        menu.setItem(11, GUIItem.getPlusLessItem(-10));
        menu.setItem(12, GUIItem.getPlusLessItem(-1));
        menu.setItem(13, heightStack);
        menu.setItem(14, GUIItem.getPlusLessItem(+1));
        menu.setItem(15, GUIItem.getPlusLessItem(+10));
        
        menu.setItem(20, GUIItem.getPlusLessItem(-10));
        menu.setItem(21, GUIItem.getPlusLessItem(-1));
        menu.setItem(22, ticksStack);
        menu.setItem(23, GUIItem.getPlusLessItem(+1));
        menu.setItem(24, GUIItem.getPlusLessItem(+10));
        
        menu.setItem(29, baseMaterialStack);
        menu.setItem(30, lavaMaterialStack);
        
        menu.setItem(32, squaredStack);
        menu.setItem(33, throwBlocksStack);
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction MINI_VOLCANO_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 18:
                    // Go to previous menu
                    onBack.accept(player);
                    break;
                case 26:
                    // Go to next menu
                    onNext.accept(player, item);
                    break;
                //<editor-fold defaultstate="collapsed" desc="Height">
                case 11:
                    //Height -10
                    int height = item.getHeight() - 10;
                    if(height < 1){
                        item.setHeight(1);
                    }else{
                        item.setHeight(height);
                    }
                    openMiniVolcanoMenu(player);
                    break;
                case 12:
                    //Height -1
                    height = item.getHeight() - 1;
                    if(height < 1){
                        item.setHeight(1);
                    }else{
                        item.setHeight(height);
                    }
                    openMiniVolcanoMenu(player);
                    break;
                case 13:
                    //Height = 1
                    item.setHeight(1);
                    openMiniVolcanoMenu(player);
                    break;
                case 14:
                    //Depath +1
                    item.setHeight(item.getHeight() + 1);
                    openMiniVolcanoMenu(player);
                    break;
                case 15:
                    //Height +10
                    item.setHeight(item.getHeight() + 10);
                    openMiniVolcanoMenu(player);
                    break;
//</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Ticks">
                case 20:
                    //Ticks -10
                    long ticks = item.getTicks() - 10;
                    if(ticks < 0){
                        item.setTicks(0);
                    }else{
                        item.setTicks(ticks);
                    }
                    openMiniVolcanoMenu(player);
                    break;
                case 21:
                    //Ticks -1
                    ticks = item.getTicks() - 1;
                    if(ticks < 0){
                        item.setTicks(0);
                    }else{
                        item.setTicks(ticks);
                    }
                    openMiniVolcanoMenu(player);
                    break;
                case 22:
                    //Ticks = 0
                    item.setTicks(0);
                    openMiniVolcanoMenu(player);
                    break;
                case 23:
                    //Depath +1
                    item.setTicks(item.getTicks() + 1);
                    openMiniVolcanoMenu(player);
                    break;
                case 24:
                    //Ticks +10
                    item.setTicks(item.getTicks() + 10);
                    openMiniVolcanoMenu(player);
                    break;
//</editor-fold>
                case 29:
                    break;
                case 30:
                    //Toggle lava material
                    if(item.getLavaMaterial() == XMaterial.LAVA){
                        item.setLavaMaterial(XMaterial.WATER);
                    }else{
                        item.setLavaMaterial(XMaterial.LAVA);
                    }
                    openMiniVolcanoMenu(player);
                    break;
                case 32:
                    //Toggle isSquared
                    item.setSquared(!item.isSquared());
                    openMiniVolcanoMenu(player);
                    break;
                case 33:
                    //Toggle throw blocks
                    item.setThrowBlocks(!item.isThrowBlocks());
                    openMiniVolcanoMenu(player);
                    break;
            }
        }else if(e.getLocation() == InventoryLocation.BOTTOM){
            ItemStack stack = e.getCurrentItem();
            if(stack != null && stack.getType().isSolid()){
                item.setBaseMaterial(XMaterial.matchXMaterial(stack));
                openMiniVolcanoMenu(player);
            }
        }
        //</editor-fold>
    };
}
