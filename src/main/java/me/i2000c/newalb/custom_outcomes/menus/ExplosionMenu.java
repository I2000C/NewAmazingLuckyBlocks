package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import java.util.Arrays;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.utils.rewards.ExplosionReward;
import me.i2000c.newalb.utils.logger.Logger;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.Player;

public class ExplosionMenu{
    public static ExplosionReward reward = null;
    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.EXPLOSION_MENU, EXPLOSION_MENU_FUNCTION);
            
            inventoriesRegistered = true;
        }
        
        reward = null;
    }
    
    //Explosion inventory
    public static void openExplosionMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(reward == null){
            reward = new ExplosionReward(FinishMenu.getCurrentOutcome());
        }
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.EXPLOSION_MENU, 36, "&4&lExplosion Reward");
        
        ItemStack glass = XMaterial.ORANGE_STAINED_GLASS_PANE.parseItem();
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        
        ItemStack minus1 = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
        meta = minus1.getItemMeta();
        meta.setDisplayName("&c&l-1");
        minus1.setItemMeta(meta);
        
        ItemStack minus10 = minus1.clone();
        meta = minus10.getItemMeta();
        meta.setDisplayName("&c&l-10");
        minus10.setItemMeta(meta);
        
        ItemStack minus100 = minus1.clone();
        meta = minus100.getItemMeta();
        meta.setDisplayName("&c&l-100");
        minus100.setItemMeta(meta);
        
        ItemStack plus1 = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
        meta = minus1.getItemMeta();
        meta.setDisplayName("&a&l+1");
        plus1.setItemMeta(meta);
        
        ItemStack plus10 = plus1.clone();
        meta = plus10.getItemMeta();
        meta.setDisplayName("&a&l+10");
        plus10.setItemMeta(meta);
        
        ItemStack plus100 = plus1.clone();
        meta = plus100.getItemMeta();
        meta.setDisplayName("&a&l+100");
        plus100.setItemMeta(meta);
        
        
        ItemStack tntItem = new ItemStack(Material.TNT);
        meta = tntItem.getItemMeta();
        meta.setDisplayName("&6Explosion power: &e" + reward.getPower());
        meta.setLore(Arrays.asList("&3Click to reset"));
        tntItem.setItemMeta(meta);
        
        ItemStack fireItem;
        if(reward.isWithFire()){
            fireItem = XMaterial.FIRE_CHARGE.parseItem();
        }else{
            fireItem = XMaterial.FIREWORK_STAR.parseItem();
        }
        meta = fireItem.getItemMeta();
        if(reward.isWithFire()){
            meta.setDisplayName("&6Generate fire: &atrue");
        }else{
            meta.setDisplayName("&6Generate fire: &cfalse");
        }
        meta.setLore(Arrays.asList("&3Click to toggle"));
        fireItem.setItemMeta(meta);
        
        ItemStack breakBlocksItem;
        if(reward.isBreakBlocks()){
            breakBlocksItem = new ItemStack(Material.IRON_PICKAXE);
        }else{
            breakBlocksItem = new ItemStack(Material.STONE);
        }
        meta = breakBlocksItem.getItemMeta();
        if(reward.isBreakBlocks()){
            meta.setDisplayName("&6Break blocks: &atrue");
        }else{
            meta.setDisplayName("&6Break blocks: &cfalse");
        }
        meta.setLore(Arrays.asList("&3Click to toggle"));
        breakBlocksItem.setItemMeta(meta);
        
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName("&7Back");
        back.setItemMeta(meta);
        
        ItemStack next = new ItemStack(Material.ANVIL);
        meta = next.getItemMeta();
        meta.setDisplayName("&bNext");
        next.setItemMeta(meta);
        
        for(int i=0;i<=9;i++){
            inv.setItem(i, glass);
        }
        for(int i=17;i<=18;i++){
            inv.setItem(i, glass);
        }
        for(int i=26;i<36;i++){
            inv.setItem(i, glass);
        }
        
        inv.setItem(19, back);
        inv.setItem(25, next);
        
        inv.setItem(21, fireItem);
        inv.setItem(23, breakBlocksItem);

        inv.setItem(10, minus100);
        inv.setItem(11, minus10);
        inv.setItem(12, minus1);
        inv.setItem(13, tntItem);
        inv.setItem(14, plus1);
        inv.setItem(15, plus10);
        inv.setItem(16, plus100);
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction EXPLOSION_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            switch(e.getSlot()){
                case 19:
                    //Return to the previous menu
                    if(FinishMenu.editMode){
                        FinishMenu.openFinishInventory(p);
                    }else{
                        RewardTypesMenu.openRewardTypesMenu(p);
                    }
                    break;
                case 25:
                    //Open next menu
                    FinishMenu.addReward(reward);
                    reset();
                    FinishMenu.openFinishInventory(p);
                    break;
                case 21:
                    //Toggle with fire
                    reward.setWithFire(!reward.isWithFire());
                    openExplosionMenu(p);
                    break;
                case 23:
                    //Toggle effect showParticles
                    reward.setBreakBlocks(!reward.isBreakBlocks());
                    openExplosionMenu(p);
                    break;
                //<editor-fold defaultstate="collapsed" desc="Explosion power">
                case 10:
                    //Explosion power -100
                    int power = reward.getPower();
                    power -= 100;
                    if(power < 0){
                        power = 0;
                    }
                    reward.setPower(power);
                    openExplosionMenu(p);
                    break;
                case 11:
                    //Explosion power -10
                    power = reward.getPower();
                    power -= 10;
                    if(power < 0){
                        power = 0;
                    }
                    reward.setPower(power);
                    openExplosionMenu(p);
                    break;
                case 12:
                    //Explosion power -1
                    power = reward.getPower();
                    power--;
                    if(power < 0){
                        power = 0;
                    }
                    reward.setPower(power);
                    openExplosionMenu(p);
                    break;
                case 13:
                    //Explosion power = 4
                    reward.setPower(4);
                    openExplosionMenu(p);
                    break;
                case 14:
                    //Explosion power +1
                    power = reward.getPower();
                    power++;
                    reward.setPower(power);
                    openExplosionMenu(p);
                    break;
                case 15:
                    //Explosion power +10
                    power = reward.getPower();
                    power += 10;
                    reward.setPower(power);
                    openExplosionMenu(p);
                    break;
                case 16:
                    //Explosion power +100
                    power = reward.getPower();
                    power += 100;
                    reward.setPower(power);
                    openExplosionMenu(p);
                    break;
//</editor-fold>

            }
        }
//</editor-fold>
    };
}
