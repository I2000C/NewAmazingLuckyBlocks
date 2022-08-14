package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.utils.rewards.LightningReward;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LightningMenu{
    public static LightningReward reward;
    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.LIGHTNING_MENU, LIGHTNING_MENU_FUNCTION);
            
            inventoriesRegistered = true;
        }
        
        reward = null;
    }
    
    public static void openLightningMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(reward == null){
            reward = new LightningReward(RewardListMenu.getCurrentOutcome());
        }
                
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.LIGHTNING_MENU, 27, "&e&lLightning Reward");

        ItemStack glass = GUIItem.getGlassItem(GlassColor.WHITE);

        for(int i=0;i<=9;i++){
            inv.setItem(i, glass);
        }
        for(int i=17;i<27;i++){
            inv.setItem(i, glass);
        }

        ItemStack usePlayerLocStack = GUIItem.getUsePlayerLocItem(reward.getUsePlayerLoc());
        
        ItemBuilder builder;
        if(reward.getCauseDamage()){
            builder = ItemBuilder.newItem(XMaterial.LAVA_BUCKET);
            builder.withDisplayName("&cCause damage");
        }else{
            builder = ItemBuilder.newItem(XMaterial.WATER_BUCKET);
            builder.withDisplayName("&bDon't cause damage");            
        }
        ItemStack damagePlayerStack = builder.build();

        ItemStack offsetStack = reward.getOffset().getItemToDisplay();

        inv.setItem(10, GUIItem.getBackItem());
        inv.setItem(16, GUIItem.getNextItem());

        inv.setItem(12, usePlayerLocStack);
        inv.setItem(13, damagePlayerStack);
        inv.setItem(14, offsetStack);

        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction LIGHTNING_MENU_FUNCTION = e -> {
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
                    RewardListMenu.addReward(reward);
                    reset();
                    RewardListMenu.openFinishInventory(p);
                    break;
                case 12:
                    reward.setUsePlayerLoc(!reward.getUsePlayerLoc());
                    openLightningMenu(p);
                    break;
                case 13:
                    reward.setCauseDamage(!reward.getCauseDamage());
                    openLightningMenu(p);
                    break;
                case 14:
                    OffsetMenu.reset();
                    OffsetMenu.setCurrentData(reward.getOffset(), player -> openLightningMenu(player));
                    OffsetMenu.openOffsetMenu(p);
                    break;
            }
        }
//</editor-fold>
    };
}
