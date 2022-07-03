package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.utils.rewards.MiniVolcanoReward;
import me.i2000c.newalb.utils.Logger;
import java.util.Arrays;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        
        ItemMeta meta;
        
        ItemStack glass = XMaterial.ORANGE_STAINED_GLASS_PANE.parseItem();
        meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        
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
        
        ItemStack plus1 = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
        meta = plus1.getItemMeta();
        meta.setDisplayName(Logger.color("&a&l+1"));
        plus1.setItemMeta(meta);
        
        ItemStack plus10 = plus1.clone();
        meta = plus10.getItemMeta();
        meta.setDisplayName(Logger.color("&a&l+10"));
        plus10.setItemMeta(meta);
        
        ItemStack minus1 = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
        meta = minus1.getItemMeta();
        meta.setDisplayName(Logger.color("&c&l-1"));
        minus1.setItemMeta(meta);
        
        ItemStack minus10 = minus1.clone();
        meta = minus10.getItemMeta();
        meta.setDisplayName(Logger.color("&c&l-10"));
        minus10.setItemMeta(meta);
        
        
        ItemStack squaredStack;
        if(reward.isSquared()){
            squaredStack = new ItemStack(Material.SNOW_BLOCK);
            meta = squaredStack.getItemMeta();
            meta.setDisplayName(Logger.color("&bSquared: &atrue"));
        }else{
            squaredStack = XMaterial.SNOWBALL.parseItem();
            meta = squaredStack.getItemMeta();
            meta.setDisplayName(Logger.color("&bSquared: &7false"));
        }
        squaredStack.setItemMeta(meta);
        
        ItemStack heightStack = new ItemStack(Material.LADDER);
        meta = heightStack.getItemMeta();
        meta.setDisplayName(Logger.color("&3Height: &b" + reward.getHeight()));
        heightStack.setItemMeta(meta);
        
        ItemStack ticksStack = XMaterial.CLOCK.parseItem();
        meta = ticksStack.getItemMeta();
        meta.setDisplayName(Logger.color("&5Ticks between blocks: &6" + reward.getTicks()));
        ticksStack.setItemMeta(meta);
        
        ItemStack baseMaterialStack = new ItemStack(reward.getBaseMaterial());
        meta = baseMaterialStack.getItemMeta();
        meta.setDisplayName(Logger.color("&6Base material: &b" + reward.getBaseMaterial().name()));
        meta.setLore(Arrays.asList(Logger.color("&3Click on a &3&lblock &3of your inventory"), Logger.color("&3to change it")));
        baseMaterialStack.setItemMeta(meta);
        
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
        meta = lavaMaterialStack.getItemMeta();
        meta.setDisplayName(Logger.color("&cLava material: &b" + reward.getLavaMaterial().name()));
        meta.setLore(Arrays.asList(Logger.color("&3Click to change")));
        lavaMaterialStack.setItemMeta(meta);
        
        ItemStack throwBlocksStack;
        if(reward.isThrowBlocks()){
            throwBlocksStack = XMaterial.FIRE_CHARGE.parseItem();
            meta = throwBlocksStack.getItemMeta();
            meta.setDisplayName(Logger.color("&cThrow blocks: &atrue"));
        }else{
            throwBlocksStack = new ItemStack(Material.LAPIS_BLOCK);
            meta = throwBlocksStack.getItemMeta();
            meta.setDisplayName(Logger.color("&cThrow blocks: &7false"));
        }
        throwBlocksStack.setItemMeta(meta);
        
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName(Logger.color("&2Back"));
        back.setItemMeta(meta);
        
        ItemStack next = new ItemStack(Material.ANVIL);
        meta = next.getItemMeta();
        meta.setDisplayName(Logger.color("&bNext"));
        next.setItemMeta(meta);
        
        inv.setItem(18, back);
        inv.setItem(26, next);
        
        inv.setItem(11, minus10);
        inv.setItem(12, minus1);
        inv.setItem(13, heightStack);
        inv.setItem(14, plus1);
        inv.setItem(15, plus10);
        
        inv.setItem(20, minus10);
        inv.setItem(21, minus1);
        inv.setItem(22, ticksStack);
        inv.setItem(23, plus1);
        inv.setItem(24, plus10);
        
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
