package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.utils.rewards.BlockReward;
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
            reward = new BlockReward(FinishMenu.getCurrentOutcome());
        }
                
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.BLOCK_MENU, 45, "&d&lBlock Reward");

        ItemMeta meta;

        ItemStack glass = XMaterial.PURPLE_STAINED_GLASS_PANE.parseItem();
        meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);

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

        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName(Logger.color("&2Back"));
        back.setItemMeta(meta);

        ItemStack next = new ItemStack(Material.ANVIL);
        meta = next.getItemMeta();
        meta.setDisplayName(Logger.color("&bNext"));
        next.setItemMeta(meta);

        ItemStack usePlayerLocStack;
        if(reward.getUsePlayerLoc()){
            usePlayerLocStack = XMaterial.PLAYER_HEAD.parseItem();
            meta = usePlayerLocStack.getItemMeta();
            meta.setDisplayName(Logger.color("&aUse player location"));
        }else{
            usePlayerLocStack = TypeManager.getMenuItemStack();
            meta = usePlayerLocStack.getItemMeta();
            meta.setDisplayName(Logger.color("&6Use lucky block location"));
            meta.setLore(null);
        }
        usePlayerLocStack.setItemMeta(meta);

        ItemStack isFallingBlockStack;
        if(reward.getIsFallingBlock()){
            isFallingBlockStack = new ItemStack(Material.SAND);
            meta = isFallingBlockStack.getItemMeta();
            meta.setDisplayName(Logger.color("&5Is falling block: &atrue"));
        }else{
            isFallingBlockStack = new ItemStack(Material.COBBLESTONE);
            meta = isFallingBlockStack.getItemMeta();
            meta.setDisplayName(Logger.color("&5Is falling block: &cfalse"));
        }
        isFallingBlockStack.setItemMeta(meta);

        ItemStack offsetStack = XMaterial.PISTON.parseItem();
        meta = offsetStack.getItemMeta();
        meta.setDisplayName(Logger.color("&3Configure offset"));
        List<String> loreList = new ArrayList();
        loreList.add(Logger.color("&dCurrent Offset:"));
        loreList.add(Logger.color("   &5X: &3" + reward.getOffset().getOffsetX()));
        loreList.add(Logger.color("   &5Y: &3" + reward.getOffset().getOffsetY()));
        loreList.add(Logger.color("   &5Z: &3" + reward.getOffset().getOffsetZ()));
        meta.setLore(loreList);
        offsetStack.setItemMeta(meta);
        
        ItemStack blockItem;
        if(reward.getItemBlock() != null){
            blockItem = reward.getItemBlock().clone();
            switch(blockItem.getType()){
                case WATER:
                    blockItem.setType(Material.WATER_BUCKET);
                    break;
                case LAVA:
                    blockItem.setType(Material.LAVA_BUCKET);
                    break;
                case FIRE:
                    blockItem.setType(Material.FLINT_AND_STEEL);
                    break;
            }
            meta = blockItem.getItemMeta();
            meta.setDisplayName(Logger.color("&3Selected block"));
        }else{
            blockItem = XMaterial.BLACK_STAINED_GLASS_PANE.parseItem();
            meta = blockItem.getItemMeta();
            meta.setDisplayName(Logger.color("&3Select a &6&lblock &3from your inventory"));
        }
        blockItem.setItemMeta(meta);

        inv.setItem(10, back);
        inv.setItem(16, next);

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
                    if(reward.getItemBlock() != null){
                        FinishMenu.addReward(reward);
                        reset();
                        FinishMenu.openFinishInventory(p);
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
        }else{
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
