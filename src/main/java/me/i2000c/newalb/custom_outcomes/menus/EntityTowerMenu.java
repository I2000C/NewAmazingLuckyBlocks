package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import java.util.ArrayList;
import java.util.List;
import me.i2000c.newalb.custom_outcomes.utils.rewards.EntityReward;
import me.i2000c.newalb.custom_outcomes.utils.rewards.EntityTowerReward;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GUIPagesAdapter;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EntityTowerMenu{
    public static EntityTowerReward reward = null;
    
    private static final int TOWER_SLOTS[] = {53, 44, 35, 26, 17, 8};
    
    private static final int ENTITY_LIST_MENU_SIZE = 45;
    private static final int PREVIOUS_PAGE_SLOT = 49;
    private static final int CURRENT_PAGE_SLOT = 50;
    private static final int NEXT_PAGE_SLOT = 51;
    private static final String ENTITY_ID_TAG = "entity_id";
    private static GUIPagesAdapter<EntityReward> entityListAdapter;
    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.ENTITY_TOWER_MENU, ENTITY_TOWER_MENU_FUNCTION);
            
            entityListAdapter = new GUIPagesAdapter<>(
                    ENTITY_LIST_MENU_SIZE,
                    (entityReward, index) -> {
                        ItemStack stack = entityReward.getItemToDisplay();
                        return NBTEditor.set(stack, entityReward.getID(), ENTITY_ID_TAG);
                    }
            );
            entityListAdapter.setPreviousPageSlot(PREVIOUS_PAGE_SLOT);
            entityListAdapter.setCurrentPageSlot(CURRENT_PAGE_SLOT);
            entityListAdapter.setNextPageSlot(NEXT_PAGE_SLOT);
            
            entityListAdapter.setIgnoredColumns(7, 8);
            
            inventoriesRegistered = true;
        }
        
        entityListAdapter.goToMainPage();
        
        reward = null;
    }
    
    public static void openEntityTowerMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(reward == null){
            reward = new EntityTowerReward(RewardListMenu.getCurrentOutcome());
        }
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.ENTITY_TOWER_MENU, 54, "&e&lEntityTower Reward");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.CYAN);
        
        ItemStack reset = ItemBuilder.newItem(XMaterial.BARRIER)
                .withDisplayName("&cReload entity list")
                .build();
        
        for(int i=7;i<54;i+=9){
            inv.setItem(i, glass);
        }
        
        inv.setItem(45, GUIItem.getBackItem());
        inv.setItem(46, reset);
        inv.setItem(47, GUIItem.getNextItem());
        
        List<EntityReward> entityRewardList = RewardListMenu
                .getCurrentOutcome()
                .getEntityRewards();
        for(int i=0; i<TOWER_SLOTS.length && i<reward.getEntityList().size(); i++){
            int slot = TOWER_SLOTS[i];            
            int entityID = reward.getEntityList().get(i);
            
            ItemStack entityItem;
            switch(entityID){
                case EntityTowerReward.PLAYER_ENTITY_ID:
                    entityItem = EntityTowerReward.getPlayerItem();
                    break;
                case EntityTowerReward.INVALID_ENTITY_ID:
                    entityItem = null;
                    break;
                default:
                    EntityReward entityReward = entityRewardList.get(entityID);
                    entityItem = entityReward.getItemToDisplay();
            }
            
            if(entityItem != null){
                inv.setItem(slot, NBTEditor.set(entityItem, entityID, ENTITY_ID_TAG));
            }
        }
        
        entityRewardList.add(EntityTowerReward.getPlayerEntityReward());
        entityListAdapter.setItemList(entityRewardList);
        
        entityListAdapter.updateMenu(inv);
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction ENTITY_TOWER_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        
        if(e.getLocation() == InventoryLocation.NONE){
            e.setCursor(null);
            e.setCancelled(true);            
            return;
        }
        
        if(e.getClick() != ClickType.LEFT){
            e.setCancelled(true);
            return;
        }
        
        if(e.getLocation() == InventoryLocation.BOTTOM){
            if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR){
                e.setCursor(null);
            }
            e.setCancelled(true);
        }else if(e.getLocation() == InventoryLocation.TOP){
            Inventory towerInv = e.getView().getTopInventory();
            
            switch(e.getSlot()){
                case 7:
                case 16:
                case 25:
                case 34:
                case 43:
                case 52:
                    // Click on glass item
                    e.setCancelled(true);
                    break;
                case PREVIOUS_PAGE_SLOT:
                    e.setCancelled(true);
                    if(entityListAdapter.goToPreviousPage()){
                        saveTowerEntities(towerInv);
                        openEntityTowerMenu(p);
                    }
                    break;
                case CURRENT_PAGE_SLOT:
                    e.setCancelled(true);
                    if(entityListAdapter.goToMainPage()){
                        saveTowerEntities(towerInv);
                        openEntityTowerMenu(p);
                    }
                    break;
                case NEXT_PAGE_SLOT:
                    e.setCancelled(true);
                    if(entityListAdapter.goToNextPage()){
                        saveTowerEntities(towerInv);
                        openEntityTowerMenu(p);
                    }
                    break;
                case 45:
                    //Back
                    e.setCancelled(true);
                    if(RewardListMenu.editMode){
                        RewardListMenu.openFinishInventory(p);
                    }else{
                        RewardTypesMenu.openRewardTypesMenu(p);
                    }
                    break;
                case 46:
                    //Reset
                    e.setCancelled(true);
                    EntityTowerMenu.openEntityTowerMenu(p);
                    break;
                case 47:
                    e.setCancelled(true);
                    
                    if(!saveTowerEntities(towerInv)){
                        return;
                    }

                    RewardListMenu.addReward(reward);
                    reset();
                    //Open FinishMenu
                    RewardListMenu.openFinishInventory(p);
                    break;
                default:
                    if((e.getSlot()+1) % 9 == 0){
                        // Tower area
                        e.setCancelled(false);
                    }else{
                        // Entity list area
                        e.setCancelled(true);
                        e.setCursor(e.getCurrentItem());
                    }
            }
        }
//</editor-fold>
    };
    
    private static boolean saveTowerEntities(Inventory towerInv){        
        boolean towerValid = true;
        boolean towerEnd = false;
        List<Integer> entityIDList = new ArrayList<>();
        for(int i=0; i<TOWER_SLOTS.length; i++){
            int slot = TOWER_SLOTS[i];
            ItemStack stack = towerInv.getItem(slot);
            if((stack == null || stack.getType() == Material.AIR)
                    || !NBTEditor.contains(stack, ENTITY_ID_TAG)){
                towerEnd = true;
                entityIDList.add(EntityTowerReward.INVALID_ENTITY_ID);
            }else{
                if(towerEnd){
                    towerValid = false;
                }
                entityIDList.add(NBTEditor.getInt(stack, ENTITY_ID_TAG));
            }
        }
        
        //Add entities' ids to reward entity list
        reward.getEntityList().clear();
        reward.getEntityList().addAll(entityIDList);
        
        if(towerValid){
            reward.getEntityList().removeIf(entityID -> 
                    entityID == EntityTowerReward.INVALID_ENTITY_ID);
        }
                
        return towerValid && reward.getEntityList().size() >= 2;
    }
}
