package me.i2000c.newalb.lucky_blocks.editors.menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
import me.i2000c.newalb.lucky_blocks.editors.EditorType;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.types.BlockReward;
import me.i2000c.newalb.utils.locations.Offset;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class BlockMenu extends Editor<BlockReward>{
    public BlockMenu(){
        InventoryListener.registerInventory(CustomInventoryType.BLOCK_MENU, BLOCK_MENU_FUNCTION);
    }
    
    @Override
    protected void newItem(Player player){
        Outcome outcome = RewardListMenu.getCurrentOutcome();
        item = new BlockReward(outcome);
        openBlockMenu(player);
    }

    @Override
    protected void editItem(Player player){
        openBlockMenu(player);
    }
    
    private void openBlockMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.BLOCK_MENU, 45, "&d&lBlock Reward");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.PURPLE);

        for(int i=0;i<=9;i++){
            menu.setItem(i, glass);
        }
        for(int i=35;i<45;i++){
            menu.setItem(i, glass);
        }
        menu.setItem(17, glass);
        menu.setItem(18, glass);
        menu.setItem(26, glass);
        menu.setItem(27, glass);
        
        ItemStack usePlayerLocStack = GUIItem.getUsePlayerLocItem(item.isUsePlayerLoc());

        ItemStack isFallingBlockStack = GUIItem.getBooleanItem(
                item.isFallingBlock(), 
                "&5Is falling block", 
                XMaterial.SAND, 
                XMaterial.COBBLESTONE);
        
        ItemStack offsetStack = item.getOffset().getItemToDisplay();
        
        ItemStackWrapper wrapper;
        if(item.getBlockMaterial() != null){
            XMaterial blockMaterial = item.getBlockMaterial();
            switch(blockMaterial){
                case WATER: blockMaterial = XMaterial.WATER_BUCKET; break;
                case LAVA:  blockMaterial = XMaterial.LAVA_BUCKET;  break;
                case FIRE:  blockMaterial = XMaterial.FIRE_CHARGE;  break;
            }
            wrapper = ItemStackWrapper.newItem(blockMaterial);
            wrapper.setDisplayName("&3Selected block");
        }else{
            wrapper = ItemStackWrapper.newItem(XMaterial.BLACK_STAINED_GLASS_PANE)
                                      .setDisplayName("&3Select a &6&lblock &3from your inventory");
        }
        
        ItemStack blockItem = wrapper.toItemStack();

        menu.setItem(10, GUIItem.getBackItem());
        menu.setItem(16, GUIItem.getNextItem());

        menu.setItem(12, usePlayerLocStack);
        menu.setItem(13, isFallingBlockStack);
        menu.setItem(14, offsetStack);
        
        menu.setItem(31, blockItem);
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction BLOCK_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 10:
                    onBack.accept(player);
                    break;
                case 16:
                    if(item.getBlockMaterial() != null){
                        onNext.accept(player, item);
                    }
                    break;
                case 12:
                    item.setUsePlayerLoc(!item.isUsePlayerLoc());
                    openBlockMenu(player);
                    break;
                case 13:
                    item.setFallingBlock(!item.isFallingBlock());
                    openBlockMenu(player);
                    break;
                case 14:
                    Editor<Offset> editor = EditorType.OFFSET.getEditor();
                    editor.editExistingItem(
                            item.getOffset().clone(), 
                            player, 
                            p -> openBlockMenu(p), 
                            (p, offset) -> {
                                item.setOffset(offset);
                                openBlockMenu(p);
                            });
                    break;
            }
        }else if(e.getLocation() == InventoryLocation.BOTTOM){
            ItemStack stack = e.getCurrentItem();
            if(stack == null){
                return;
            }
            
            if(stack.getType().isBlock()){
                item.setBlockMaterial(XMaterial.matchXMaterial(stack));
                openBlockMenu(player);
            }else switch(XMaterial.matchXMaterial(stack.getType())){
                case WATER_BUCKET:
                    item.setBlockMaterial(XMaterial.WATER);
                    openBlockMenu(player);
                    break;
                case LAVA_BUCKET:
                    item.setBlockMaterial(XMaterial.LAVA);
                    openBlockMenu(player);
                    break;
                case FIRE_CHARGE:
                    item.setBlockMaterial(XMaterial.FIRE);
                    openBlockMenu(player);
                    break;
            }
        }
//</editor-fold>
    };
}
