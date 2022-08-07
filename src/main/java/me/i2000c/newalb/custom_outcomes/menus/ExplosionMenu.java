package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.utils.rewards.ExplosionReward;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.ORANGE);
        
        ItemStack tntItem = ItemBuilder.newItem(XMaterial.TNT)
                .withDisplayName("&6Explosion power: &e" + reward.getPower())
                .addLoreLine("&3Click to reset")
                .build();
        
        ItemBuilder builder;
        if(reward.isWithFire()){
            builder = ItemBuilder.newItem(XMaterial.FIRE_CHARGE);
            builder.withDisplayName("&6Generate fire: &atrue");
        }else{
            builder = ItemBuilder.newItem(XMaterial.FIREWORK_STAR);
            builder.withDisplayName("&6Generate fire: &cfalse");
        }
        builder.addLoreLine("&3Click to toggle");
        ItemStack fireItem = builder.build();
        
        if(reward.isBreakBlocks()){
            builder = ItemBuilder.newItem(XMaterial.IRON_PICKAXE);
            builder.withDisplayName("&6Break blocks: &atrue");
        }else{
            builder = ItemBuilder.newItem(XMaterial.STONE);
            builder.withDisplayName("&6Break blocks: &cfalse");
        }
        builder.addLoreLine("&3Click to toggle");
        ItemStack breakBlocksItem = builder.build();
        
        for(int i=0;i<=9;i++){
            inv.setItem(i, glass);
        }
        for(int i=17;i<=18;i++){
            inv.setItem(i, glass);
        }
        for(int i=26;i<36;i++){
            inv.setItem(i, glass);
        }
        
        inv.setItem(19, GUIItem.getBackItem());
        inv.setItem(25, GUIItem.getNextItem());
        
        inv.setItem(21, fireItem);
        inv.setItem(23, breakBlocksItem);

        inv.setItem(10, GUIItem.getPlusLessItem(-100));
        inv.setItem(11, GUIItem.getPlusLessItem(-10));
        inv.setItem(12, GUIItem.getPlusLessItem(-1));
        inv.setItem(13, tntItem);
        inv.setItem(14, GUIItem.getPlusLessItem(+1));
        inv.setItem(15, GUIItem.getPlusLessItem(+10));
        inv.setItem(16, GUIItem.getPlusLessItem(+100));
        
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
