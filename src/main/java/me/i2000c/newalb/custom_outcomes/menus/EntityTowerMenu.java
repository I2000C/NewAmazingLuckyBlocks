package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import java.util.ArrayList;
import java.util.List;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.EntityReward;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.EntityTowerReward;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GUIPagesAdapter;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EntityTowerMenu extends Editor<EntityTowerReward>{
    public EntityTowerMenu(){
        //<editor-fold defaultstate="collapsed" desc="Code">
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
//</editor-fold>
    }
    
    @Override
    protected void reset(){
        entityListAdapter.goToMainPage();
    }
    
    @Override
    protected void newItem(Player player){
        Outcome outcome = RewardListMenu.getCurrentOutcome();
        item = new EntityTowerReward(outcome);
        openEntityTowerMenu(player);
    }

    @Override
    protected void editItem(Player player){
        openEntityTowerMenu(player);
    }
    
    private static final int TOWER_SLOTS[] = {53, 44, 35, 26, 17, 8};
    
    private static final int ENTITY_LIST_MENU_SIZE = 45;
    private static final int PREVIOUS_PAGE_SLOT = 49;
    private static final int CURRENT_PAGE_SLOT = 50;
    private static final int NEXT_PAGE_SLOT = 51;
    private static final String ENTITY_ID_TAG = "entity_id";
    private static GUIPagesAdapter<EntityReward> entityListAdapter;
    
    private void openEntityTowerMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.ENTITY_TOWER_MENU, 54, "&e&lEntityTower Reward");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.CYAN);
        
        ItemStack reset = ItemBuilder.newItem(XMaterial.BARRIER)
                .withDisplayName("&cReload entity list")
                .build();
        
        for(int i=7;i<54;i+=9){
            menu.setItem(i, glass);
        }
        
        menu.setItem(45, GUIItem.getBackItem());
        menu.setItem(46, reset);
        menu.setItem(47, GUIItem.getNextItem());
        
        List<EntityReward> entityRewardList = RewardListMenu
                .getCurrentOutcome()
                .getEntityRewards();
        for(int i=0; i<TOWER_SLOTS.length && i<item.getEntityList().size(); i++){
            int slot = TOWER_SLOTS[i];            
            int entityID = item.getEntityList().get(i);
            
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
                menu.setItem(slot, NBTEditor.set(entityItem, entityID, ENTITY_ID_TAG));
            }
        }
        
        entityRewardList.add(EntityTowerReward.getPlayerEntityReward());
        entityListAdapter.setItemList(entityRewardList);
        
        entityListAdapter.updateMenu(menu);
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction ENTITY_TOWER_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        
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
                        openEntityTowerMenu(player);
                    }
                    break;
                case CURRENT_PAGE_SLOT:
                    e.setCancelled(true);
                    if(entityListAdapter.goToMainPage()){
                        saveTowerEntities(towerInv);
                        openEntityTowerMenu(player);
                    }
                    break;
                case NEXT_PAGE_SLOT:
                    e.setCancelled(true);
                    if(entityListAdapter.goToNextPage()){
                        saveTowerEntities(towerInv);
                        openEntityTowerMenu(player);
                    }
                    break;
                case 45:
                    // Go to previous menu
                    e.setCancelled(true);
                    onBack.accept(player);
                    break;
                case 46:
                    //Reset
                    e.setCancelled(true);
                    openEntityTowerMenu(player);
                    break;
                case 47:
                    // Go to next menu
                    e.setCancelled(true);                    
                    if(saveTowerEntities(towerInv)){
                        onNext.accept(player, item);
                    }
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
    
    private boolean saveTowerEntities(Inventory towerInv){        
        //<editor-fold defaultstate="collapsed" desc="Code">
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
        
        //Add entities' ids to item entity list
        item.getEntityList().clear();
        item.getEntityList().addAll(entityIDList);
        
        if(towerValid){
            item.getEntityList().removeIf(entityID ->
                    entityID == EntityTowerReward.INVALID_ENTITY_ID);
        }
        
        return towerValid && item.getEntityList().size() >= 2;
//</editor-fold>
    }
}
