package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.utils.rewards.LightningReward;
import me.i2000c.newalb.utils.Logger;
import java.util.ArrayList;
import java.util.List;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.custom_outcomes.utils.TypeManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
            reward = new LightningReward(FinishMenu.getCurrentOutcome());
        }
                
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.LIGHTNING_MENU, 27, "&e&lLightning Reward");

        ItemMeta meta;

        ItemStack glass = XMaterial.WHITE_STAINED_GLASS_PANE.parseItem();
        meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);

        for(int i=0;i<=9;i++){
            inv.setItem(i, glass);
        }
        for(int i=17;i<27;i++){
            inv.setItem(i, glass);
        }

        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName("&2Back");
        back.setItemMeta(meta);

        ItemStack next = new ItemStack(Material.ANVIL);
        meta = next.getItemMeta();
        meta.setDisplayName("&bNext");
        next.setItemMeta(meta);

        ItemStack usePlayerLocStack;
        if(reward.getUsePlayerLoc()){
            usePlayerLocStack = XMaterial.PLAYER_HEAD.parseItem();
            meta = usePlayerLocStack.getItemMeta();
            meta.setDisplayName("&aUse player location");
        }else{
            usePlayerLocStack = TypeManager.getMenuItemStack();
            meta = usePlayerLocStack.getItemMeta();
            meta.setDisplayName("&6Use lucky block location");
            meta.setLore(null);
        }
        usePlayerLocStack.setItemMeta(meta);

        ItemStack damagePlayerStack;
        if(reward.getCauseDamage()){
            damagePlayerStack = new ItemStack(Material.LAVA_BUCKET);
            meta = damagePlayerStack.getItemMeta();
            meta.setDisplayName("&cCause damage");
        }else{
            damagePlayerStack = new ItemStack(Material.WATER_BUCKET);
            meta = damagePlayerStack.getItemMeta();
            meta.setDisplayName("&bDon't cause damage");            
        }
        damagePlayerStack.setItemMeta(meta);

        ItemStack offsetStack = XMaterial.PISTON.parseItem();
        meta = offsetStack.getItemMeta();
        meta.setDisplayName("&3Configure offset");
        List<String> loreList = new ArrayList<>();
        loreList.add("&dCurrent Offset:");
        loreList.add("   &5X: &3" + reward.getOffset().getOffsetX());
        loreList.add("   &5Y: &3" + reward.getOffset().getOffsetY());
        loreList.add("   &5Z: &3" + reward.getOffset().getOffsetZ());
        meta.setLore(loreList);
        offsetStack.setItemMeta(meta);

        inv.setItem(10, back);
        inv.setItem(16, next);

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
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            switch(e.getSlot()){
                case 10:
                    reset();
                    if(FinishMenu.editMode){
                        FinishMenu.openFinishInventory(p);
                    }else{
                        RewardTypesMenu.openRewardTypesMenu(p);
                    }
                    break;
                case 16:
                    FinishMenu.addReward(reward);
                    reset();
                    FinishMenu.openFinishInventory(p);
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
