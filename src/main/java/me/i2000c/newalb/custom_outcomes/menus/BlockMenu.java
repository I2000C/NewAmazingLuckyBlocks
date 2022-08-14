package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.utils.rewards.BlockReward;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BlockMenu{
    public static BlockReward reward;
    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.BLOCK_MENU, BLOCK_MENU_FUNCTION);
            
            inventoriesRegistered = true;
        }
        
        reward = null;
    }
    
    public static void openBlockMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(reward == null){
            reward = new BlockReward(RewardListMenu.getCurrentOutcome());
        }
                
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.BLOCK_MENU, 45, "&d&lBlock Reward");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.PURPLE);

        for(int i=0;i<=9;i++){
            inv.setItem(i, glass);
        }
        for(int i=35;i<45;i++){
            inv.setItem(i, glass);
        }
        inv.setItem(17, glass);
        inv.setItem(18, glass);
        inv.setItem(26, glass);
        inv.setItem(27, glass);
        
        ItemStack usePlayerLocStack = GUIItem.getUsePlayerLocItem(reward.getUsePlayerLoc());

        ItemStack isFallingBlockStack = GUIItem.getBooleanItem(
                reward.getIsFallingBlock(), 
                "&5Is falling block", 
                XMaterial.SAND, 
                XMaterial.COBBLESTONE);
        
        ItemStack offsetStack = reward.getOffset().getItemToDisplay();
        
        ItemBuilder builder;
        if(reward.getItemBlock() != null){
            builder = ItemBuilder.fromItem(reward.getItemBlock());
            switch(builder.getMaterial()){
                case WATER:
                    builder.withMaterial(XMaterial.WATER_BUCKET);
                    break;
                case LAVA:
                    builder.withMaterial(XMaterial.LAVA_BUCKET);
                    break;
                case FIRE:
                    builder.withMaterial(XMaterial.FIRE_CHARGE);
                    break;
            }
            builder.withDisplayName("&3Selected block");
        }else{
            builder = ItemBuilder.newItem(XMaterial.BLACK_STAINED_GLASS_PANE)
                    .withDisplayName("&3Select a &6&lblock &3from your inventory");
        }
        ItemStack blockItem = builder.build();

        inv.setItem(10, GUIItem.getBackItem());
        inv.setItem(16, GUIItem.getNextItem());

        inv.setItem(12, usePlayerLocStack);
        inv.setItem(13, isFallingBlockStack);
        inv.setItem(14, offsetStack);
        
        inv.setItem(31, blockItem);

        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction BLOCK_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 10:
                    reset();
                    if(RewardListMenu.editMode){
                        RewardListMenu.openFinishInventory(p);
                    }else{
                        RewardTypesMenu.openRewardTypesMenu(p);
                    }
                    break;
                case 16:
                    if(reward.getItemBlock() != null){
                        RewardListMenu.addReward(reward);
                        reset();
                        RewardListMenu.openFinishInventory(p);
                    }
                    break;
                case 12:
                    reward.setUsePlayerLoc(!reward.getUsePlayerLoc());
                    openBlockMenu(p);
                    break;
                case 13:
                    reward.setIsFallingBlock(!reward.getIsFallingBlock());
                    openBlockMenu(p);
                    break;
                case 14:
                    OffsetMenu.reset();
                    OffsetMenu.setCurrentData(reward.getOffset(), player -> openBlockMenu(player));
                    OffsetMenu.openOffsetMenu(p);
                    break;
            }
        }else if(e.getLocation() == InventoryLocation.BOTTOM){
            ItemStack stack = e.getCurrentItem();
            if(stack == null){
                return;
            }
            if(stack.getType().isSolid()){
                reward.setItemBlock(e.getCurrentItem().clone());
                openBlockMenu(p);
            }else if(stack.getType() == Material.WATER_BUCKET){
                reward.setItemBlock(new ItemStack(Material.WATER));
                openBlockMenu(p);
            }else if(stack.getType() == Material.LAVA_BUCKET){
                reward.setItemBlock(new ItemStack(Material.LAVA));
                openBlockMenu(p);
            }else if(stack.getType() == Material.FLINT_AND_STEEL){
                reward.setItemBlock(new ItemStack(Material.FIRE));
                openBlockMenu(p);
            }
        }
//</editor-fold>
    };
}
