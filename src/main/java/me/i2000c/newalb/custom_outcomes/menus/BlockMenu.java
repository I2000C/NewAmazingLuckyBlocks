package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.editor.EditorType;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.BlockReward;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.Offset;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        
        ItemStack usePlayerLocStack = GUIItem.getUsePlayerLocItem(item.getUsePlayerLoc());

        ItemStack isFallingBlockStack = GUIItem.getBooleanItem(
                item.getIsFallingBlock(), 
                "&5Is falling block", 
                XMaterial.SAND, 
                XMaterial.COBBLESTONE);
        
        ItemStack offsetStack = item.getOffset().getItemToDisplay();
        
        ItemBuilder builder;
        if(item.getItemBlock() != null){
            builder = ItemBuilder.fromItem(item.getItemBlock());
            switch(builder.getXMaterial()){
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
                    if(item.getItemBlock() != null){
                        onNext.accept(player, item);
                    }
                    break;
                case 12:
                    item.setUsePlayerLoc(!item.getUsePlayerLoc());
                    openBlockMenu(player);
                    break;
                case 13:
                    item.setIsFallingBlock(!item.getIsFallingBlock());
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
                item.setItemBlock(new ItemStack(stack.getType()));
                if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                    item.getItemBlock().setDurability(stack.getDurability());
                }
                openBlockMenu(player);
            }else switch(XMaterial.matchXMaterial(stack.getType())){
                case WATER_BUCKET:
                    item.setItemBlock(XMaterial.WATER.parseItem());
                    openBlockMenu(player);
                    break;
                case LAVA_BUCKET:
                    item.setItemBlock(XMaterial.LAVA.parseItem());
                    openBlockMenu(player);
                    break;
                case FIRE_CHARGE:
                    item.setItemBlock(XMaterial.FIRE.parseItem());
                    openBlockMenu(player);
                    break;
            }
        }
//</editor-fold>
    };
}
