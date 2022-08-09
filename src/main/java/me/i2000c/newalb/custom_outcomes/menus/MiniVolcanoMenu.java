package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.utils.rewards.MiniVolcanoReward;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MiniVolcanoMenu{
    public static MiniVolcanoReward reward;
    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.MINI_VOLCANO_MENU, MINI_VOLCANO_MENU_FUNCTION);
            
            inventoriesRegistered = true;
        }
        
        reward = null;
    }
    
    public static void openMiniVolcanoMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(reward == null){
            reward = new MiniVolcanoReward(FinishMenu.getCurrentOutcome());
        }
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.MINI_VOLCANO_MENU, 45, "&c&lMiniVolcano Rewards");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.ORANGE);
        
        for(int i=0;i<9;i++){
            inv.setItem(i, glass);
        }
        for(int i=36;i<45;i++){
            inv.setItem(i, glass);
        }
        inv.setItem(9, glass);
        inv.setItem(17, glass);
        inv.setItem(27, glass);
        inv.setItem(35, glass);        
        
        ItemStack squaredStack = GUIItem.getBooleanItem(
                reward.isSquared(), 
                "&bSquared", 
                XMaterial.SNOW_BLOCK, 
                XMaterial.SNOWBALL);
        
        ItemStack heightStack = ItemBuilder.newItem(XMaterial.LADDER)
                .withDisplayName("&3Height: &b" + reward.getHeight())
                .build();
        
        ItemStack ticksStack = ItemBuilder.newItem(XMaterial.CLOCK)
                .withDisplayName("&5Ticks between blocks: &6" + reward.getTicks())
                .build();
        
        ItemStack baseMaterialStack = ItemBuilder.newItem(XMaterial.matchXMaterial(reward.getBaseMaterial()))
                .withDisplayName("&6Base material: &b" + reward.getBaseMaterial().name())
                .addLoreLine("&3Click on a &3&lblock &3of your inventory")
                .addLoreLine("&3to change it")
                .build();
        
        ItemStack lavaMaterialStack;
        switch(reward.getLavaMaterial()){
            case LAVA:
                lavaMaterialStack = new ItemStack(Material.LAVA_BUCKET);
                break;
            case WATER:
                lavaMaterialStack = new ItemStack(Material.WATER_BUCKET);
                break;
            default:
                lavaMaterialStack = new ItemStack(reward.getLavaMaterial());
                break;
        }
        ItemBuilder.fromItem(lavaMaterialStack, false)
                .withDisplayName("&cLava material: &b" + reward.getLavaMaterial().name())
                .addLoreLine("&3Click to change");
        
        ItemStack throwBlocksStack = GUIItem.getBooleanItem(
                reward.isThrowBlocks(), 
                "&cThrow blocks", 
                XMaterial.FIRE_CHARGE, 
                XMaterial.LAPIS_BLOCK);
        
        inv.setItem(18, GUIItem.getBackItem());
        inv.setItem(26, GUIItem.getNextItem());
        
        inv.setItem(11, GUIItem.getPlusLessItem(-10));
        inv.setItem(12, GUIItem.getPlusLessItem(-1));
        inv.setItem(13, heightStack);
        inv.setItem(14, GUIItem.getPlusLessItem(+1));
        inv.setItem(15, GUIItem.getPlusLessItem(+10));
        
        inv.setItem(20, GUIItem.getPlusLessItem(-10));
        inv.setItem(21, GUIItem.getPlusLessItem(-1));
        inv.setItem(22, ticksStack);
        inv.setItem(23, GUIItem.getPlusLessItem(+1));
        inv.setItem(24, GUIItem.getPlusLessItem(+10));
        
        inv.setItem(29, baseMaterialStack);
        inv.setItem(30, lavaMaterialStack);
        
        inv.setItem(32, squaredStack);
        inv.setItem(33, throwBlocksStack);
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction MINI_VOLCANO_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            switch(e.getSlot()){
                case 18:
                    //Back
                    reset();
                    if(FinishMenu.editMode){
                        FinishMenu.openFinishInventory(p);
                    }else{
                        RewardTypesMenu.openRewardTypesMenu(p);
                    }
                    break;
                case 26:
                    //Next
                    FinishMenu.addReward(reward);
                    reset();
                    FinishMenu.openFinishInventory(p);
                    break;
                //<editor-fold defaultstate="collapsed" desc="Height">
                case 11:
                    //Height -10
                    int height = reward.getHeight() - 10;
                    if(height < 1){
                        reward.setHeight(1);
                    }else{
                        reward.setHeight(height);
                    }
                    openMiniVolcanoMenu(p);
                    break;
                case 12:
                    //Height -1
                    height = reward.getHeight() - 1;
                    if(height < 1){
                        reward.setHeight(1);
                    }else{
                        reward.setHeight(height);
                    }
                    openMiniVolcanoMenu(p);
                    break;
                case 13:
                    //Height = 1
                    reward.setHeight(1);
                    openMiniVolcanoMenu(p);
                    break;
                case 14:
                    //Depath +1
                    reward.setHeight(reward.getHeight() + 1);
                    openMiniVolcanoMenu(p);
                    break;
                case 15:
                    //Height +10
                    reward.setHeight(reward.getHeight() + 10);
                    openMiniVolcanoMenu(p);
                    break;
//</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Ticks">
                case 20:
                    //Ticks -10
                    long ticks = reward.getTicks() - 10;
                    if(ticks < 0){
                        reward.setTicks(0);
                    }else{
                        reward.setTicks(ticks);
                    }
                    openMiniVolcanoMenu(p);
                    break;
                case 21:
                    //Ticks -1
                    ticks = reward.getTicks() - 1;
                    if(ticks < 0){
                        reward.setTicks(0);
                    }else{
                        reward.setTicks(ticks);
                    }
                    openMiniVolcanoMenu(p);
                    break;
                case 22:
                    //Ticks = 0
                    reward.setTicks(0);
                    openMiniVolcanoMenu(p);
                    break;
                case 23:
                    //Depath +1
                    reward.setTicks(reward.getTicks() + 1);
                    openMiniVolcanoMenu(p);
                    break;
                case 24:
                    //Ticks +10
                    reward.setTicks(reward.getTicks() + 10);
                    openMiniVolcanoMenu(p);
                    break;
//</editor-fold>
                case 29:
                    break;
                case 30:
                    //Toggle lava material
                    if(reward.getLavaMaterial() == Material.LAVA){
                        reward.setLavaMaterial(Material.WATER);
                    }else{
                        reward.setLavaMaterial(Material.LAVA);
                    }
                    openMiniVolcanoMenu(p);
                    break;
                case 32:
                    //Toggle isSquared
                    reward.setSquared(!reward.isSquared());
                    openMiniVolcanoMenu(p);
                    break;
                case 33:
                    //Toggle throw blocks
                    reward.setThrowBlocks(!reward.isThrowBlocks());
                    openMiniVolcanoMenu(p);
                    break;
            }
        }else{
            ItemStack stack = e.getCurrentItem();
            if(stack != null && stack.getType().isSolid()){
                reward.setBaseMaterial(stack.getType());
                openMiniVolcanoMenu(p);
            }
        }
        //</editor-fold>
    };
}
