package me.i2000c.newalb.lucky_blocks.editors.menus;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.functions.InventoryFunction;
import me.i2000c.newalb.api.gui.CustomInventoryType;
import me.i2000c.newalb.api.gui.GUIFactory;
import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.InventoryLocation;
import me.i2000c.newalb.api.gui.Menu;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.lucky_blocks.editors.Editor;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.types.ExplosionReward;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ExplosionMenu extends Editor<ExplosionReward>{
    public ExplosionMenu(){
        InventoryListener.registerInventory(CustomInventoryType.EXPLOSION_MENU, EXPLOSION_MENU_FUNCTION);
    }
    
    @Override
    protected void newItem(Player player){
        Outcome outcome = RewardListMenu.getCurrentOutcome();
        item = new ExplosionReward(outcome);
        openExplosionMenu(player);
    }
    
    @Override
    protected void editItem(Player player){
        openExplosionMenu(player);
    }
    
    private void openExplosionMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.EXPLOSION_MENU, 36, "&4&lExplosion Reward");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.ORANGE);
        
        ItemStack tntItem = ItemStackWrapper.newItem(XMaterial.TNT)
                                            .setDisplayName("&6Explosion power: &e" + item.getPower())
                                            .addLoreLine("&3Click to reset")
                                            .toItemStack();
        
        ItemStack fireItem = GUIItem.getBooleanItem(
                item.isWithFire(), 
                "&6Generate fire", 
                XMaterial.FIRE_CHARGE, 
                XMaterial.FIREWORK_STAR);
        
        ItemStack breakBlocksItem = GUIItem.getBooleanItem(
                item.isBreakBlocks(), 
                "&6Break blocks", 
                XMaterial.IRON_PICKAXE, 
                XMaterial.STONE);
        
        for(int i=0;i<=9;i++){
            menu.setItem(i, glass);
        }
        for(int i=17;i<=18;i++){
            menu.setItem(i, glass);
        }
        for(int i=26;i<36;i++){
            menu.setItem(i, glass);
        }
        
        menu.setItem(19, GUIItem.getBackItem());
        menu.setItem(25, GUIItem.getNextItem());
        
        menu.setItem(21, fireItem);
        menu.setItem(23, breakBlocksItem);

        menu.setItem(10, GUIItem.getPlusLessItem(-100));
        menu.setItem(11, GUIItem.getPlusLessItem(-10));
        menu.setItem(12, GUIItem.getPlusLessItem(-1));
        menu.setItem(13, tntItem);
        menu.setItem(14, GUIItem.getPlusLessItem(+1));
        menu.setItem(15, GUIItem.getPlusLessItem(+10));
        menu.setItem(16, GUIItem.getPlusLessItem(+100));
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction EXPLOSION_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 19:
                    //Return to the previous menu
                    onBack.accept(player);
                    break;
                case 25:
                    //Open next menu
                    onNext.accept(player, item);
                    break;
                case 21:
                    //Toggle with fire
                    item.setWithFire(!item.isWithFire());
                    openExplosionMenu(player);
                    break;
                case 23:
                    //Toggle effect showParticles
                    item.setBreakBlocks(!item.isBreakBlocks());
                    openExplosionMenu(player);
                    break;
                //<editor-fold defaultstate="collapsed" desc="Explosion power">
                case 10:
                    //Explosion power -100
                    int power = item.getPower();
                    power -= 100;
                    if(power < 0){
                        power = 0;
                    }
                    item.setPower(power);
                    openExplosionMenu(player);
                    break;
                case 11:
                    //Explosion power -10
                    power = item.getPower();
                    power -= 10;
                    if(power < 0){
                        power = 0;
                    }
                    item.setPower(power);
                    openExplosionMenu(player);
                    break;
                case 12:
                    //Explosion power -1
                    power = item.getPower();
                    power--;
                    if(power < 0){
                        power = 0;
                    }
                    item.setPower(power);
                    openExplosionMenu(player);
                    break;
                case 13:
                    //Explosion power = 4
                    item.setPower(4);
                    openExplosionMenu(player);
                    break;
                case 14:
                    //Explosion power +1
                    power = item.getPower();
                    power++;
                    item.setPower(power);
                    openExplosionMenu(player);
                    break;
                case 15:
                    //Explosion power +10
                    power = item.getPower();
                    power += 10;
                    item.setPower(power);
                    openExplosionMenu(player);
                    break;
                case 16:
                    //Explosion power +100
                    power = item.getPower();
                    power += 100;
                    item.setPower(power);
                    openExplosionMenu(player);
                    break;
//</editor-fold>

            }
        }
//</editor-fold>
    };
}
