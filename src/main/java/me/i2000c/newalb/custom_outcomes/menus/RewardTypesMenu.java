package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RewardTypesMenu{
    public static void openRewardTypesMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.REWARD_TYPES_MENU, 27, "&2&lSelect reward type");
        
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
        
        inv.setItem(0, stack);
        inv.setItem(1, stack2);
        inv.setItem(2, stack3);
        inv.setItem(3, stack4);
        inv.setItem(4, stack5);
        inv.setItem(5, stack6);
        inv.setItem(6, stack7);
        inv.setItem(7, stack8);
        inv.setItem(8, stack9);
        inv.setItem(9, stack10);
        inv.setItem(10, stack11);
        inv.setItem(11, stack12);
        inv.setItem(12, stack13);
        inv.setItem(13, stack14);
        inv.setItem(14, stack15);
        inv.setItem(15, stack16);
        
        inv.setItem(18, GUIItem.getBackItem());
        
        InventoryListener.registerInventory(CustomInventoryType.REWARD_TYPES_MENU, REWARD_TYPES_MENU_FUNCTION);
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction REWARD_TYPES_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){            
            switch(e.getSlot()){
                case 0:
                    //Open item inventory
                    ItemMenu.reset();
                    ItemMenu.openItemMenu(p);
                    break;
                case 1:
                    //Open command inventory
                    CommandMenu.reset();
                    CommandMenu.openCommandMenu(p);
                    break;
                case 2:
                    //Open entity inventory
                    EntityMenu.reset();
                    EntityMenu.openEntityMenu(p);
                    break;
                case 3:
                    //Open entityTower inventory
                    int availableEntities = RewardListMenu.getCurrentOutcome().getEntityRewardsNumber();
                    if(availableEntities >= 1){
                        EntityTowerMenu.reset();
                        EntityTowerMenu.openEntityTowerMenu(p);
                    }
                    break;
                case 4:
                    //Open firework inventory
                    FireworkMenu.reset();
                    FireworkMenu.openFireworkMenu(p);
                    break;
                case 5:
                    //Open sound inventory
                    SoundMenu.reset();
                    SoundMenu.openSoundMenu(p);
                    break;
                case 6:
                    //Open structure inventory
                    if(NewAmazingLuckyBlocks.getWorldEditPlugin() != null){
                        StructureMenu.reset();
                        StructureMenu.openStructureMenu(p);
                    }
                    break;
                case 7:
                    //Open block inventory
                    BlockMenu.reset();
                    BlockMenu.openBlockMenu(p);
                    break;
                case 8:
                    //Open lightning inventory
                    LightningMenu.reset();
                    LightningMenu.openLightningMenu(p);
                    break;
                case 9:
                    //Open dark hole inventory
                    DarkHoleMenu.reset();
                    DarkHoleMenu.openDarkHoleMenu(p);
                    break;
                case 10:
                    //Open mini volcano inventory
                    MiniVolcanoMenu.reset();
                    MiniVolcanoMenu.openMiniVolcanoMenu(p);
                    break;
                case 11:
                    //Open message inventory
                    MessageMenu.reset();
                    MessageMenu.openMessageMenu(p);
                    break;
                case 12:
                    //Open effect inventory
                    EffectMenu.reset();
                    EffectMenu.openEffectMenu(p);
                    break;
                case 13:
                    //Open explosion inventory
                    ExplosionMenu.reset();
                    ExplosionMenu.openExplosionMenu(p);
                    break;
                case 14:
                    //Open block replacing sphere inventory
                    BlockReplacingSphereMenu.reset();
                    BlockReplacingSphereMenu.openBRSMenu(p);
                    break;
                case 15:
                    //Open trap inventory
                    TrapMenu.reset();
                    TrapMenu.openTrapMenu(p);
                    break;
                case 18:
                    //Back to the previous menu
                    RewardListMenu.openFinishInventory(p);
                    break;
            }
        }
//</editor-fold>
    };
}
