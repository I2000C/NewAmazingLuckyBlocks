package me.i2000c.newalb.lucky_blocks.editors.menus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.PaginatedEditorMenu;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.types.EntityReward;
import me.i2000c.newalb.lucky_blocks.rewards.types.EntityTowerReward;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.misc.OtherUtils;
import me.i2000c.newalb.utils.tasks.Task;

public class EntityTowerMenu extends PaginatedEditorMenu<EntityTowerReward, EntityReward> {
    
    private static final int GLASS_COLUMN = 7;
    private static final int TOWER_COLUMN = 8;
    
    private static final int PREVIOUS_PAGE_SLOT = 49;
    private static final int CURRENT_PAGE_SLOT = 50;
    private static final int NEXT_PAGE_SLOT = 51;
    
    private static final String ENTITY_ID_TAG = "entity_id";
    
    public static final int MAX_TOWER_HEIGHT = 60;
    
    private static final int TOWER_PAGE_SIZE = 6;
    private static final int TOWER_PAGES = BigDecimal.valueOf(MAX_TOWER_HEIGHT).divide(BigDecimal.valueOf(TOWER_PAGE_SIZE), 0, RoundingMode.CEILING).intValue();
    private final List<EntityReward> towerEntities;
    private int towerPage;
    
    public EntityTowerMenu() {
        super("&e&lEntityTower Reward", MenuSize.SIZE_6_ROWS, true, MenuSize.SIZE_5_ROWS, PREVIOUS_PAGE_SLOT, CURRENT_PAGE_SLOT, NEXT_PAGE_SLOT);
        this.towerEntities = Arrays.asList(new EntityReward[MAX_TOWER_HEIGHT]);
        this.towerPage = 0;
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
                boolean playerAlreadyInTower = towerEntities.stream()
                                                            .filter(OtherUtils.not(Objects::isNull))
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
            int i = 0;
            for(int entityID : item.getEntityList()) {
                if(i >= MAX_TOWER_HEIGHT) {
                    break;
                }
                
                EntityReward entityReward;
                if(entityID == EntityTowerReward.PLAYER_ENTITY_ID) {
                    entityReward = EntityTowerReward.getPlayerEntityReward();
                } else {
                    entityReward = item.getOutcome().getEntityReward(entityID);
                }
                towerEntities.set(i++, entityReward);
            }
        }
    }
    
    @Override
    protected void buildMenu(Player player) {
        setBackItem(45);
        setNextItem(46, e -> {
            int notEmptySlots = (int) towerEntities.stream()
                                                   .filter(OtherUtils.not(Objects::isNull))
                                                   .count();
            
            if(notEmptySlots >= 2) {
                List<Integer> entityIDs = towerEntities.stream()
                                                       .filter(OtherUtils.not(Objects::isNull))
                                                       .map(EntityReward::getEntityID)
                                                       .collect(Collectors.toList());
                item.setEntityList(entityIDs);
                onNext(player, item);
            }
        });
        
        ItemStack clearItem = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                              .setDisplayName("&cClear entity tower")
                                              .toItemStack();
        
        setItem(48, clearItem, e -> {
            if(e.isEmptyCursor()) {
                towerPage = 0;
                Collections.fill(towerEntities, null);
                openToPlayer(player);
            }
        });
        
        int index = towerPage * TOWER_PAGE_SIZE;
        int slot = MenuSize.SIZE_6_ROWS.getSize() - 2;
        for(int i=1; i<=TOWER_PAGE_SIZE; i++) {
            ItemStackWrapper wrapper;
            if(i == 1) {
                wrapper = ItemStackWrapper.fromItem(GUIItem.getGlassItem(GlassColor.RED), false);
                wrapper.setDisplayName("&cGo to previous tower page");
                setClickAction(slot, e -> {
                    towerPage = OtherUtils.addInRange(towerPage, -1, 0, TOWER_PAGES);
                    if(!e.isEmptyCursor()) {
                        ItemStack cursor = e.getCursor();
                        Task.runTask(() -> e.getPlayer().setItemOnCursor(cursor), 2L);
                    }
                    openToPlayer(player);
                });
            } else if(i == TOWER_PAGE_SIZE) {
                wrapper = ItemStackWrapper.fromItem(GUIItem.getGlassItem(GlassColor.GREEN), false);
                wrapper.setDisplayName("&aGo to next tower page");
                setClickAction(slot, e -> {
                    towerPage = OtherUtils.addInRange(towerPage, 1, 0, TOWER_PAGES);
                    if(!e.isEmptyCursor()) {
                        ItemStack cursor = e.getCursor();
                        Task.runTask(() -> e.getPlayer().setItemOnCursor(cursor), 2L);
                    }
                    openToPlayer(player);
                });
            } else {
                wrapper = ItemStackWrapper.fromItem(GUIItem.getGlassItem(GlassColor.CYAN), false);
            }
            wrapper.addLoreLine(String.format("&3Current tower page: &6%d &5/ &6%d", towerPage + 1, TOWER_PAGES));
            wrapper.setAmount(index + 1);
            setItem(slot, wrapper.toItemStack());
            
            slot -= MenuSize.SIZE_1_ROW.getSize();
            index++;
        }
        
        slot = MenuSize.SIZE_6_ROWS.getSize() - 1;
        int firstIndex = towerPage * TOWER_PAGE_SIZE;
        int lastIndex = firstIndex + TOWER_PAGE_SIZE;
        for(int i=firstIndex; i<lastIndex; i++) {
            EntityReward entityReward = towerEntities.get(i);
            if(entityReward != null) {
                ItemStackWrapper wrapper = ItemStackWrapper.fromItem(entityReward.getItemToDisplay(), false);
                wrapper.setNbtTag(ENTITY_ID_TAG, entityReward.getEntityID());
                setItem(slot, wrapper.toItemStack());
            }
            slot -= MenuSize.SIZE_1_ROW.getSize();
        }
    }
    
    @Override
    protected void onClickDefault(MenuClickEvent event) {
        if(event.isTopInventory()) {
            int column = event.getSlot() % MenuSize.SIZE_1_ROW.getSize();
            if(column == TOWER_COLUMN) {
                int baseIndex = towerPage * TOWER_PAGE_SIZE;
                int row = event.getSlot() / MenuSize.SIZE_1_ROW.getSize();
                int index = baseIndex + (5 - row);
                ItemStack cursor = event.getCursor();
                ItemStack currentItem = event.getCurrentItem();
                if(event.isEmptyCursor()) {
                    towerEntities.set(index, null);
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
                        towerEntities.set(index, entityReward);
                        event.setCursor(currentItem);
                        event.getInventory().setItem(event.getSlot(), cursor);
                        return;
                    }
                }
            } else if(column == GLASS_COLUMN) {
                return;
            }
        }
        
        event.setCursor(null);
    }
}
