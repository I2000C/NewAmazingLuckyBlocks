package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.editor.EditorType;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.LightningReward;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import me.i2000c.newalb.utils2.Offset;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LightningMenu extends Editor<LightningReward>{
    public LightningMenu(){
        InventoryListener.registerInventory(CustomInventoryType.LIGHTNING_MENU, LIGHTNING_MENU_FUNCTION);
    }
    
    @Override
    protected void newItem(Player player){
        Outcome outcome = RewardListMenu.getCurrentOutcome();
        item = new LightningReward(outcome);
        openLightningMenu(player);
    }

    @Override
    protected void editItem(Player player){
        openLightningMenu(player);
    }
    
    private void openLightningMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">    
        Menu menu = GUIFactory.newMenu(CustomInventoryType.LIGHTNING_MENU, 27, "&e&lLightning Reward");

        ItemStack glass = GUIItem.getGlassItem(GlassColor.WHITE);

        for(int i=0;i<=9;i++){
            menu.setItem(i, glass);
        }
        for(int i=17;i<27;i++){
            menu.setItem(i, glass);
        }

        ItemStack usePlayerLocStack = GUIItem.getUsePlayerLocItem(item.isUsePlayerLoc());
        
        ItemStackWrapper builder;
        if(item.isCauseDamage()){
            builder = ItemStackWrapper.newItem(XMaterial.LAVA_BUCKET);
            builder.setDisplayName("&cCause damage");
        }else{
            builder = ItemStackWrapper.newItem(XMaterial.WATER_BUCKET);
            builder.setDisplayName("&bDon't cause damage");            
        }
        ItemStack damagePlayerStack = builder.toItemStack();

        ItemStack offsetStack = item.getOffset().getItemToDisplay();

        menu.setItem(10, GUIItem.getBackItem());
        menu.setItem(16, GUIItem.getNextItem());

        menu.setItem(12, usePlayerLocStack);
        menu.setItem(13, damagePlayerStack);
        menu.setItem(14, offsetStack);
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction LIGHTNING_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 10:
                    // Go to previous menu
                    onBack.accept(player);
                    break;
                case 16:
                    // Go to next menu
                    onNext.accept(player, item);
                    break;
                case 12:
                    item.setUsePlayerLoc(!item.isUsePlayerLoc());
                    openLightningMenu(player);
                    break;
                case 13:
                    item.setCauseDamage(!item.isCauseDamage());
                    openLightningMenu(player);
                    break;
                case 14:
                    Editor<Offset> editor = EditorType.OFFSET.getEditor();
                    editor.editExistingItem(
                            item.getOffset().clone(), 
                            player, 
                            p -> openLightningMenu(p), 
                            (p, offset) -> {
                                item.setOffset(offset);
                                openLightningMenu(p);
                            });
                    break;
            }
        }
//</editor-fold>
    };
}
