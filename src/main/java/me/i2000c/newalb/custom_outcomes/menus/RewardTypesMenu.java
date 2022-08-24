package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.editor.EditorType;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils2.ItemBuilder;
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
        
        ItemStack stack = ItemBuilder.newItem(XMaterial.IRON_INGOT)
                .withDisplayName("&aCreate Item rewards")
                .build();
        
        ItemStack stack2 = ItemBuilder.newItem(XMaterial.NAME_TAG)
                .withDisplayName("&7Create Command Rewards")
                .build();
        
        ItemStack stack3 = ItemBuilder.newItem(XMaterial.BONE)
                .withDisplayName("&5Create Entity Rewards")
                .build();
        
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.ARMOR_STAND);
        builder.withDisplayName("&eCreate EntityTower Rewards");
        
        if(RewardListMenu.getCurrentOutcome().getEntityRewardsNumber() < 1){
            builder.addLoreLine("&cYou need to have created at least 1 entity");
            builder.addLoreLine("  &cin order to use this reward");
        }
        ItemStack stack4 = builder.build();
        
        ItemStack stack5 = ItemBuilder.newItem(XMaterial.FIREWORK_ROCKET)
                .withDisplayName("&bCreate Firework Rewards")
                .build();
        
        ItemStack stack6 = ItemBuilder.newItem(XMaterial.JUKEBOX)
                .withDisplayName("&dCreate Sound Rewards")
                .build();
        
        builder = ItemBuilder.newItem(XMaterial.BRICKS);
        builder.withDisplayName("&3Create Structure Rewards");
        if(NewAmazingLuckyBlocks.getWorldEditPlugin() == null){
            builder.addLoreLine("&cYou need WorldEdit in order to use this reward");
        }
        ItemStack stack7 = builder.build();
        
        ItemStack stack8 = ItemBuilder.newItem(XMaterial.BRICK)
                .withDisplayName("&dCreate block rewards")
                .build();
        
        ItemStack stack9 = ItemBuilder.newItem(XMaterial.WHITE_WOOL)
                .withDisplayName("&eCreate lightning rewards")
                .build();
        
        ItemStack stack10 = ItemBuilder.newItem(XMaterial.BUCKET)
                .withDisplayName("&8Create dark hole rewards")
                .build();
        
        ItemStack stack11 = ItemBuilder.newItem(XMaterial.LAVA_BUCKET)
                .withDisplayName("&cCreate mini volcano rewards")
                .build();
        
        ItemStack stack12 = ItemBuilder.newItem(XMaterial.BOOK)
                .withDisplayName("&7Create message rewards")
                .build();
        
        ItemStack stack13 = ItemBuilder.newItem(XMaterial.POTION)
                .withDisplayName("&5Create effect rewards")
                .build();
        
        ItemStack stack14 = ItemBuilder.newItem(XMaterial.TNT)
                .withDisplayName("&4Create explosion rewards")
                .build();
        
        ItemStack stack15 = ItemBuilder.newItem(XMaterial.DIAMOND_ORE)
                .withDisplayName("&bCreate block replacing sphere (BRS) rewards")
                .build();
        
        ItemStack stack16 = ItemBuilder.newItem(XMaterial.OAK_PRESSURE_PLATE)
                .withDisplayName("&5Create trap rewards")
                .build();
        
        ItemStack stack17 = ItemBuilder.newItem(XMaterial.COMPASS)
                .withDisplayName("&eCreate teleport rewards")
                .build();
        
        ItemStack stack18 = ItemBuilder.newItem(XMaterial.INK_SAC)
                .withDisplayName("&7Create squid explosion rewards")
                .build();
        
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
