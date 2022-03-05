package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.Player;

public class RewardTypesMenu{
    public static void openRewardTypesMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.REWARD_TYPES_MENU, 27, "&2&lSelect reward type");
        
        ItemStack stack = new ItemStack(Material.IRON_INGOT);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Logger.color("&aCreate Item Rewards"));
        stack.setItemMeta(meta);
        
        ItemStack stack2 = new ItemStack(Material.NAME_TAG);
        meta = stack2.getItemMeta();
        meta.setDisplayName(Logger.color("&7Create Command Rewards"));
        stack2.setItemMeta(meta);
        
        ItemStack stack3 = new ItemStack(Material.BONE);
        meta = stack.getItemMeta();
        meta.setDisplayName(Logger.color("&5Create Entity Rewards"));
        stack3.setItemMeta(meta);
        
        ItemStack stack4 = new ItemStack(Material.ARMOR_STAND);
        meta = stack4.getItemMeta();
        meta.setDisplayName(Logger.color("&eCreate EntityTower Rewards"));
        if(FinishMenu.getCurrentOutcome().getEntityRewardList().size() < 2){
            List<String> lore2 = new ArrayList();
            lore2.add(Logger.color("&cYou need to have created at least 2 entities"));
            lore2.add(Logger.color("&cin order to use this reward"));
            
            meta.setLore(lore2);
        }else{
            int available_entities = FinishMenu.getCurrentOutcome().getEntitiesNotInTowerRewards();
            if(available_entities < 2){
                List<String> lore2 = new ArrayList();
                lore2.add(Logger.color("&cYou need to create more entities"));
                lore2.add(Logger.color("&cin order to use this reward"));
                
                meta.setLore(lore2);
            }
        }
        stack4.setItemMeta(meta);
        
        ItemStack stack5 = XMaterial.FIREWORK_ROCKET.parseItem();
        meta = stack5.getItemMeta();
        meta.setDisplayName(Logger.color("&bCreate Firework Rewards"));
        stack5.setItemMeta(meta);
        
        ItemStack stack6 = new ItemStack(Material.JUKEBOX);
        meta = stack6.getItemMeta();
        meta.setDisplayName(Logger.color("&dCreate Sound Rewards"));
        stack6.setItemMeta(meta);
        
        ItemStack stack7 = XMaterial.BRICKS.parseItem();
        meta = stack7.getItemMeta();
        meta.setDisplayName(Logger.color("&3Create Structure Rewards"));
        if(NewAmazingLuckyBlocks.getWorldEditPlugin() == null){
            List<String> lore = Arrays.asList("&cYou need WorldEdit in order to use this reward");
            meta.setLore(Logger.color(lore));
        }
        stack7.setItemMeta(meta);
        
        ItemStack stack8 = XMaterial.BRICK.parseItem();
        meta = stack8.getItemMeta();
        meta.setDisplayName(Logger.color("&dCreate block rewards"));
        stack8.setItemMeta(meta);
        
        ItemStack stack9 = XMaterial.WHITE_WOOL.parseItem();
        meta = stack9.getItemMeta();
        meta.setDisplayName(Logger.color("&eCreate lightning rewards"));
        stack9.setItemMeta(meta);
        
        ItemStack stack10 = new ItemStack(Material.BUCKET);
        meta = stack10.getItemMeta();
        meta.setDisplayName(Logger.color("&8Create dark hole rewards"));
        stack10.setItemMeta(meta);
        
        ItemStack stack11 = new ItemStack(Material.LAVA_BUCKET);
        meta = stack11.getItemMeta();
        meta.setDisplayName(Logger.color("&cCreate mini volcano rewards"));
        stack11.setItemMeta(meta);
        
        ItemStack stack12 = new ItemStack(Material.BOOK);
        meta = stack12.getItemMeta();
        meta.setDisplayName(Logger.color("&7Create message rewards"));
        stack12.setItemMeta(meta);
        
        ItemStack stack13 = new ItemStack(Material.POTION);
        meta = stack13.getItemMeta();
        meta.setDisplayName(Logger.color("&5Create effect rewards"));
        stack13.setItemMeta(meta);
        
        ItemStack stack14 = new ItemStack(Material.TNT);
        meta = stack14.getItemMeta();
        meta.setDisplayName(Logger.color("&4Create explosion rewards"));
        stack14.setItemMeta(meta);
        
        ItemStack stack15 = new ItemStack(Material.DIAMOND_ORE);
        meta = stack15.getItemMeta();
        meta.setDisplayName(Logger.color("&bCreate block replacing sphere (BRS) rewards"));
        stack15.setItemMeta(meta);
        
        ItemStack stack16 = XMaterial.OAK_PRESSURE_PLATE.parseItem();
        meta = stack16.getItemMeta();
        meta.setDisplayName(Logger.color("&5Create trap rewards"));
        stack16.setItemMeta(meta);
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName(Logger.color("&2Back"));
        back.setItemMeta(meta);
        
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
        
        inv.setItem(18, back);
        
        InventoryListener.registerInventory(CustomInventoryType.REWARD_TYPES_MENU, REWARD_TYPES_MENU_FUNCTION);
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction REWARD_TYPES_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            
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
                    int availableEntities = FinishMenu.getCurrentOutcome().getEntitiesNotInTowerRewards();
                    if(availableEntities >= 2){
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
                    FinishMenu.openFinishInventory(p);
                    break;
            }
        }
//</editor-fold>
    };
}
