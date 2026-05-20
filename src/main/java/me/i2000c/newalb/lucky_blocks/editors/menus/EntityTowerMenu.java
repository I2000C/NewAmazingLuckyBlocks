package me.i2000c.newalb.lucky_blocks.editors.menus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.PaginatedEditorMenu;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.types.EntityReward;
import me.i2000c.newalb.lucky_blocks.rewards.types.EntityTowerReward;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class EntityTowerMenu extends PaginatedEditorMenu<EntityTowerReward, EntityReward> {
    
    private static final int GLASS_COLUMN = 7;
    private static final int TOWER_COLUMN = 8;
    
    private static final int PREVIOUS_PAGE_SLOT = 49;
    private static final int CURRENT_PAGE_SLOT = 50;
    private static final int NEXT_PAGE_SLOT = 51;
    
    private static final String ENTITY_ID_TAG = "entity_id";
    
    private final Map<Integer, EntityReward> entityTowerSlotMap;
    
    public EntityTowerMenu() {
        super("&e&lEntityTower Reward", MenuSize.SIZE_6_ROWS, true, MenuSize.SIZE_5_ROWS, PREVIOUS_PAGE_SLOT, CURRENT_PAGE_SLOT, NEXT_PAGE_SLOT);
        this.entityTowerSlotMap = new TreeMap<>(Collections.reverseOrder());
    }
    
    @Override
    public boolean ignoreColumn(int column) {
        return column == GLASS_COLUMN || column == TOWER_COLUMN;
    }
    
    @Override
    public List<EntityReward> getItemList() {
        Outcome outcome = item.getOutcome();
        List<EntityReward> entityRewardList = new ArrayList<>();
        entityRewardList.add(EntityTowerReward.getPlayerEntityReward()); // EntityReward that represents Player
        entityRewardList.addAll(outcome.getEntityRewards());
        return entityRewardList;
    }
    
    @Override
    public MenuItem mapItemToPage(EntityReward entityReward, int index) {
        ItemStackWrapper wrapper = ItemStackWrapper.fromItem(entityReward.getItemToDisplay(), false);
        wrapper.setNbtTag(ENTITY_ID_TAG, entityReward.getEntityID());
        Consumer<MenuClickEvent> action = e -> {
            if(entityReward.getEntityID() == EntityTowerReward.PLAYER_ENTITY_ID) {
                boolean playerAlreadyInTower = entityTowerSlotMap.values()
                                                                 .stream()
                                                                 .map(EntityReward::getEntityID)
                                                                 .anyMatch(id -> id == EntityTowerReward.PLAYER_ENTITY_ID); 
                if(playerAlreadyInTower) {
                    return;
                }
            }
            e.setCursor(e.getCurrentItem());
        };
        return new MenuItem(wrapper, action);
    }
    
    @Override
    protected void onFirstOpen(Player player) {
        if(!isNewItem) {
            int slot = MenuSize.SIZE_6_ROWS.getSize() - 1;
            for(int entityID : item.getEntityList()) {
                EntityReward entityReward;
                if(entityID == EntityTowerReward.PLAYER_ENTITY_ID) {
                    entityReward = EntityTowerReward.getPlayerEntityReward();
                } else {
                    entityReward = item.getOutcome().getEntityReward(entityID);
                }
                entityTowerSlotMap.put(slot, entityReward);
                slot -= MenuSize.SIZE_1_ROW.getSize();
            }
        }
    }
    
    @Override
    protected void buildMenu(Player player) {
        addGlassColumn(GlassColor.CYAN, 7);
        
        setBackItem(45);
        setNextItem(46, e -> {
            if(entityTowerSlotMap.size() >= 2) {
                List<Integer> entityIDs = entityTowerSlotMap.values()
                                                            .stream()
                                                            .map(EntityReward::getEntityID)
                                                            .collect(Collectors.toList());
                item.setEntityList(entityIDs);
                onNext(player, item);
            }
        });
        
        ItemStack clearItem = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                              .setDisplayName("&cReset entity tower")
                                              .toItemStack();
        
        setItem(48, clearItem, e -> {
            if(e.isEmptyCursor()) {
                entityTowerSlotMap.clear();
                openToPlayer(player);
            }
        });
        
        entityTowerSlotMap.forEach((slot, entityReward) -> {
            ItemStackWrapper wrapper = ItemStackWrapper.fromItem(entityReward.getItemToDisplay(), false);
            wrapper.setNbtTag(ENTITY_ID_TAG, entityReward.getEntityID());
            setItem(slot, wrapper.toItemStack());
        });
    }
    
    @Override
    protected void onClickDefault(MenuClickEvent event) {
        if(event.isTopInventory()) {
            int column = event.getSlot() % MenuSize.SIZE_1_ROW.getSize();
            if(column == TOWER_COLUMN) {
                ItemStack cursor = event.getCursor();
                ItemStack currentItem = event.getCurrentItem();
                if(event.isEmptyCursor()) {
                    entityTowerSlotMap.remove(event.getSlot());
                    event.setCursor(currentItem);
                    event.getInventory().setItem(event.getSlot(), cursor);
                    return;
                } else {
                    ItemStackWrapper wrapper = ItemStackWrapper.fromItem(event.getCursor(), false);
                    Integer entityID = wrapper.getIntTag(ENTITY_ID_TAG);
                    if(entityID != null) {
                        EntityReward entityReward = entityID == EntityTowerReward.PLAYER_ENTITY_ID
                                                                ? EntityTowerReward.getPlayerEntityReward()
                                                                : item.getOutcome().getEntityRewards().get(entityID);
                        entityTowerSlotMap.put(event.getSlot(), entityReward);
                        event.setCursor(currentItem);
                        event.getInventory().setItem(event.getSlot(), cursor);
                        return;
                    }
                }
            }
        }
        
        event.setCursor(null);
    }
}
