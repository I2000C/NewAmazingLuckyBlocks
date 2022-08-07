package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.utils.rewards.EntityReward;
import me.i2000c.newalb.custom_outcomes.utils.rewards.EntityTowerReward;
import me.i2000c.newalb.utils.logger.Logger;
import java.util.ArrayList;
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
import org.bukkit.event.inventory.ClickType;

public class EntityTowerMenu{
    public static EntityTowerReward reward = null;
    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.ENTITY_TOWER_MENU, ENTITY_TOWER_MENU_FUNCTION);
            
            inventoriesRegistered = true;
        }
        
        reward = null;
    }
    
    public static void openEntityTowerMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(reward == null){
            reward = new EntityTowerReward(FinishMenu.getCurrentOutcome());
        }
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.ENTITY_TOWER_MENU, 54, "&e&lEntityTower Reward");
        
        ItemStack glass = XMaterial.CYAN_STAINED_GLASS_PANE.parseItem();
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName("&7Back");
        back.setItemMeta(meta);
        
        ItemStack next = new ItemStack(Material.ANVIL);
        meta = next.getItemMeta();
        meta.setDisplayName("&bNext");
        next.setItemMeta(meta);
        
        ItemStack reset = new ItemStack(Material.BARRIER);
        meta = reset.getItemMeta();
        meta.setDisplayName("&cReset tower");
        reset.setItemMeta(meta);
        
        for(int i=7;i<54;i+=9){
            inv.setItem(i, glass);
        }
        
        inv.setItem(49, back);
        inv.setItem(50, reset);
        inv.setItem(51, next);
        
        for(int i=0;i<6 && i<reward.getEntityList().size();i++){
            int entityID = reward.getEntityList().get(i);
            inv.setItem(53-i*9, FinishMenu.getCurrentOutcome().getEntityRewardList().get(entityID).getItemToDisplay());
        }
        
        List<EntityReward> entityRewards = FinishMenu.getCurrentOutcome().getEntitiesNotInTowerRewardsList();
        //i is the current entity
        //j is the inventory slot
        int j=0;
        for(int i=0;i<entityRewards.size();i++){
            if(j==7 || j==16 || j==25 || j==34 || j==43){
                j+=2;
            }
            
            if(entityRewards.get(i) != null){
                EntityReward entity = entityRewards.get(i);
                inv.setItem(j, entity.getItemToDisplay());
                if(j == 49){
                    break;
                }
                j++;
            }
        }
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction ENTITY_TOWER_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory towerInv = e.getView().getTopInventory();
        
        Player p = (Player) e.getWhoClicked();
        
        if(!e.getClick().equals(ClickType.LEFT) || e.getClickedInventory() == null){
            e.setCancelled(true);
            return;
        }
        
        if(e.getClickedInventory().equals(e.getView().getBottomInventory())){
            if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR){
                e.setCursor(null);
            }
            e.setCancelled(true);
            return;
        }
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            switch(e.getSlot()){
                case 7:
                case 16:
                case 25:
                case 34:
                case 43:
                case 52:
                    e.setCancelled(true);
                    break;
                case 49:
                    //Back
                    e.setCancelled(true);
                    if(FinishMenu.editMode){
                        FinishMenu.openFinishInventory(p);
                    }else{
                        RewardTypesMenu.openRewardTypesMenu(p);
                    }
                    break;
                case 50:
                    //Reset
                    e.setCancelled(true);
                    EntityTowerMenu.openEntityTowerMenu(p);
                    break;
                case 51:
                    e.setCancelled(true);
                    
                    //Check if slots 1 and 2 are null or not
                    if(!(towerInv.getItem(53) == null || towerInv.getItem(44) == null)){
                        List<String> tower = new ArrayList();
                        
                        String[] id = Logger.stripColor(towerInv.getItem(53).getItemMeta().getLore().get(0)).split("ID: ");
                        tower.add(id[1]);
                        id = Logger.stripColor(towerInv.getItem(44).getItemMeta().getLore().get(0)).split("ID: ");
                        tower.add(id[1]);
                        if(towerInv.getItem(35) != null){
                            id = Logger.stripColor(towerInv.getItem(35).getItemMeta().getLore().get(0)).split("ID: ");
                            tower.add(id[1]);
                            if(towerInv.getItem(26) != null){
                                id = Logger.stripColor(towerInv.getItem(26).getItemMeta().getLore().get(0)).split("ID: ");
                                tower.add(id[1]);
                                if(towerInv.getItem(17) != null){
                                    id = Logger.stripColor(towerInv.getItem(17).getItemMeta().getLore().get(0)).split("ID: ");
                                    tower.add(id[1]);
                                    if(towerInv.getItem(8) != null){
                                        id = Logger.stripColor(towerInv.getItem(8).getItemMeta().getLore().get(0)).split("ID: ");
                                        tower.add(id[1]);
                                    }
                                }
                            }
                        }
                        
                        //Add entities' ids to a list
                        reward.getEntityList().clear();
                        for(int i=0;i<tower.size();i++){
                            reward.getEntityList().add(Integer.parseInt(tower.get(i)));
                        }
                        
                        FinishMenu.addReward(reward);
                        reset();
                        //Open FinishMenu
                        FinishMenu.openFinishInventory(p);
                    }
                    break;
            }
        }
//</editor-fold>
    };
}
