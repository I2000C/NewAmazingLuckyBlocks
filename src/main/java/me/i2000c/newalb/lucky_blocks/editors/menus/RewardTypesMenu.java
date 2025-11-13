package me.i2000c.newalb.lucky_blocks.editors.menus;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.api.functions.InventoryFunction;
import me.i2000c.newalb.api.gui.CustomInventoryType;
import me.i2000c.newalb.api.gui.GUIFactory;
import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.InventoryLocation;
import me.i2000c.newalb.api.gui.Menu;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.lucky_blocks.editors.Editor;
import me.i2000c.newalb.lucky_blocks.editors.EditorType;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RewardTypesMenu extends Editor<Editor>{
    public RewardTypesMenu(){
        InventoryListener.registerInventory(CustomInventoryType.REWARD_TYPES_MENU, REWARD_TYPES_MENU_FUNCTION);
    }
    
    @Override
    public void newItem(Player player){
        openRewardTypesMenu(player);
    }
    
    @Override
    public void editItem(Player player){
        throw new UnsupportedOperationException();
    }
    
    private void openRewardTypesMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.REWARD_TYPES_MENU, 27, "&2&lSelect reward type");
        
        ItemStack stack = ItemStackWrapper.newItem(XMaterial.IRON_INGOT)
                                          .setDisplayName("&aCreate Item rewards")
                                          .toItemStack();
        
        ItemStack stack2 = ItemStackWrapper.newItem(XMaterial.NAME_TAG)
                                           .setDisplayName("&7Create Command Rewards")
                                           .toItemStack();
        
        ItemStack stack3 = ItemStackWrapper.newItem(XMaterial.BONE)
                                           .setDisplayName("&5Create Entity Rewards")
                                           .toItemStack();
        
        ItemStackWrapper wrapper = ItemStackWrapper.newItem(XMaterial.ARMOR_STAND);
        wrapper.setDisplayName("&eCreate EntityTower Rewards");
        
        if(RewardListMenu.getCurrentOutcome().getEntityRewardsNumber() < 1){
            wrapper.addLoreLine("&cYou need to have created at least 1 entity");
            wrapper.addLoreLine("  &cin order to use this reward");
        }
        ItemStack stack4 = wrapper.toItemStack();
        
        ItemStack stack5 = ItemStackWrapper.newItem(XMaterial.FIREWORK_ROCKET)
                                           .setDisplayName("&bCreate Firework Rewards")
                                           .toItemStack();
        
        ItemStack stack6 = ItemStackWrapper.newItem(XMaterial.JUKEBOX)
                                           .setDisplayName("&dCreate Sound Rewards")
                                           .toItemStack();
        
        wrapper = ItemStackWrapper.newItem(XMaterial.BRICKS);
        wrapper.setDisplayName("&3Create Structure Rewards");
        if(NewAmazingLuckyBlocks.getWorldEditPlugin() == null){
            wrapper.addLoreLine("&cYou need WorldEdit in order to use this reward");
        }
        ItemStack stack7 = wrapper.toItemStack();
        
        ItemStack stack8 = ItemStackWrapper.newItem(XMaterial.BRICK)
                                           .setDisplayName("&dCreate block rewards")
                                           .toItemStack();
        
        ItemStack stack9 = ItemStackWrapper.newItem(XMaterial.WHITE_WOOL)
                                           .setDisplayName("&eCreate lightning rewards")
                                           .toItemStack();
        
        ItemStack stack10 = ItemStackWrapper.newItem(XMaterial.BUCKET)
                                            .setDisplayName("&8Create dark hole rewards")
                                            .toItemStack();
        
        ItemStack stack11 = ItemStackWrapper.newItem(XMaterial.LAVA_BUCKET)
                                            .setDisplayName("&cCreate mini volcano rewards")
                                            .toItemStack();
        
        ItemStack stack12 = ItemStackWrapper.newItem(XMaterial.BOOK)
                                            .setDisplayName("&7Create message rewards")
                                            .toItemStack();
        
        ItemStack stack13 = ItemStackWrapper.newItem(XMaterial.POTION)
                                            .setDisplayName("&5Create effect rewards")
                                            .toItemStack();
        
        ItemStack stack14 = ItemStackWrapper.newItem(XMaterial.TNT)
                                            .setDisplayName("&4Create explosion rewards")
                                            .toItemStack();
        
        ItemStack stack15 = ItemStackWrapper.newItem(XMaterial.DIAMOND_ORE)
                                            .setDisplayName("&bCreate block replacing sphere (BRS) rewards")
                                            .toItemStack();
        
        ItemStack stack16 = ItemStackWrapper.newItem(XMaterial.OAK_PRESSURE_PLATE)
                                            .setDisplayName("&5Create trap rewards")
                                            .toItemStack();
        
        ItemStack stack17 = ItemStackWrapper.newItem(XMaterial.COMPASS)
                                            .setDisplayName("&eCreate teleport rewards")
                                            .toItemStack();
        
        ItemStack stack18 = ItemStackWrapper.newItem(XMaterial.INK_SAC)
                                            .setDisplayName("&7Create squid explosion rewards")
                                            .toItemStack();
        
        menu.setItem(0, stack);
        menu.setItem(1, stack2);
        menu.setItem(2, stack3);
        menu.setItem(3, stack4);
        menu.setItem(4, stack5);
        menu.setItem(5, stack6);
        menu.setItem(6, stack7);
        menu.setItem(7, stack8);
        menu.setItem(8, stack9);
        menu.setItem(9, stack10);
        menu.setItem(10, stack11);
        menu.setItem(11, stack12);
        menu.setItem(12, stack13);
        menu.setItem(13, stack14);
        menu.setItem(14, stack15);
        menu.setItem(15, stack16);
        menu.setItem(16, stack17);
        menu.setItem(17, stack18);
        
        menu.setItem(18, GUIItem.getBackItem());
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction REWARD_TYPES_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            EditorType type = null;
            switch(e.getSlot()){
                case 18:
                    //Back to the previous menu
                    onBack.accept(player);
                    return;
                case 0:
                    // item reward editor
                    type = EditorType.ITEM_REWARD;
                    break;
                case 1:
                    // command reward editor
                    type = EditorType.COMMAND_REWARD;
                    break;
                case 2:
                    // entity reward editor
                    type = EditorType.ENTITY_REWARD;
                    break;
                case 3:
                    // entityTower reward editor
                    int availableEntities = RewardListMenu.getCurrentOutcome().getEntityRewardsNumber();
                    if(availableEntities < 1){
                        break;
                    }
                    
                    type = EditorType.ENTITY_TOWER_REWARD;
                    break;
                case 4:
                    // firework reward editor
                    type = EditorType.FIREWORK_REWARD;
                    break;
                case 5:
                    // sound reward editor
                    type = EditorType.SOUND_REWARD;
                    break;
                case 6:
                    // structure reward editor
                    if(NewAmazingLuckyBlocks.getWorldEditPlugin() == null){
                        break;
                    }
                    
                    type = EditorType.STRUCTURE_REWARD;
                    break;
                case 7:
                    // block reward editor
                    type = EditorType.BLOCK_REWARD;
                    break;
                case 8:
                    // lightning reward editor
                    type = EditorType.LIGHTNING_REWARD;
                    break;
                case 9:
                    // dark hole reward editor
                    type = EditorType.DARK_HOLE_REWARD;
                    break;
                case 10:
                    // mini volcano reward editor
                    type = EditorType.MINI_VOLCANO_REWARD;
                    break;
                case 11:
                    // message reward editor
                    type = EditorType.MESSAGE_REWARD;
                    break;
                case 12:
                    // effect reward editor
                    type = EditorType.EFFECT_REWARD;
                    EffectMenu.setShowClearEffectsItem(true);
                    break;
                case 13:
                    // explosion reward editor
                    type = EditorType.EXPLOSION_REWARD;
                    break;
                case 14:
                    // block replacing sphere reward editor
                    type = EditorType.BLOCK_REPLACING_SPHERE_REWARD;
                    break;
                case 15:
                    // trap reward editor
                    type = EditorType.TRAP_REWARD;
                    break;
                case 16:
                    // teleport reward editor
                    type = EditorType.TELEPORT_REWARD;
                    break;
                case 17:
                    // squid explosion reward editor
                    type = EditorType.SQUID_EXPLOSION_REWARD;
                    break;
            }
            
            if(type != null){
                Editor editor = type.getEditor();
                editor.createNewItem(
                        player, 
                        p -> openRewardTypesMenu(p), 
                        onNext);
            }
        }
//</editor-fold>
    };
}
