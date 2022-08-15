package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.editor.EditorType;
import me.i2000c.newalb.custom_outcomes.rewards.Equipment;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.EffectReward;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.EntityReward;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GUIPagesAdapter;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.Offset;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EntityMenu extends Editor<EntityReward>{
    public EntityMenu(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        InventoryListener.registerInventory(CustomInventoryType.ENTITY_MENU, ENTITY_MENU_FUNCTION);
        InventoryListener.registerInventory(CustomInventoryType.ENTITY_TYPE_MENU, ENTITY_TYPE_MENU_FUNCTION);
        
        entityListAdapter = new GUIPagesAdapter<>(
                ENTITY_LIST_MENU_SIZE,
                (entityType, index) -> {
                    XMaterial material = EntityReward.getXMaterialFromEntityType(entityType);
                    ItemBuilder builder = ItemBuilder.newItem(material);
                    builder.withDisplayName("&3" + entityType.name());
                    if(entityType.isAlive()){
                        builder.addLoreLine("&6Is living entity: &atrue");
                    }else{
                        builder.addLoreLine("&6Is living entity: &7false");
                    }
                    return builder.build();
                }
        );
        entityListAdapter.setPreviousPageSlot(PREVIOUS_PAGE_SLOT);
        entityListAdapter.setCurrentPageSlot(CURRENT_PAGE_SLOT);
        entityListAdapter.setNextPageSlot(NEXT_PAGE_SLOT);
        
        List<EntityType> entityTypes = new ArrayList<>(Arrays.asList(EntityType.values()));
        entityTypes.removeIf(entityType ->
                entityType == EntityType.PLAYER || !entityType.isSpawnable());
        entityTypes.sort((entityType1, entityType2) -> {
            String name1 = entityType1.name();
            String name2 = entityType2.name();
            return name1.compareTo(name2);
        });
        
        entityListAdapter.setItemList(entityTypes);
//</editor-fold>
    }
    
    private static final int ENTITY_LIST_MENU_SIZE = 45;
    private static final int PREVIOUS_PAGE_SLOT = 51;
    private static final int CURRENT_PAGE_SLOT = 52;
    private static final int NEXT_PAGE_SLOT = 53;
    private static GUIPagesAdapter<EntityType> entityListAdapter;
    
    @Override
    protected void reset(){
        entityListAdapter.goToMainPage();
    }
    
    @Override
    protected void newItem(Player player){
        Outcome outcome = RewardListMenu.getCurrentOutcome();
        item = new EntityReward(outcome);
        openEntityMenu(player);
    }
    
    @Override
    protected void editItem(Player player){
        openEntityMenu(player);
    }
    
    //Entity inventory
    private void openEntityMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.ENTITY_MENU, 27, "&2&lEntity Reward");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.MAGENTA);
        
        XMaterial material = EntityReward.getXMaterialFromEntityType(item.getType());
        ItemBuilder builder = ItemBuilder.newItem(material);
        if(item.getType() == null){
            builder.withDisplayName("&6Select entityType");
        }else{
            builder.withDisplayName("&6entityType: &r" + item.getType());
        }
        ItemStack ent_type = builder.build();
        
        builder = ItemBuilder.newItem(XMaterial.NAME_TAG);
        if(item.getCustom_name() == null){
            builder.withDisplayName("&aSelect entityCustomName (optional)");
        }else{
            builder.withDisplayName("&aentityCustomName: &r" + item.getCustom_name());
        }
        ItemStack ent_name = builder.build();
        
        builder = ItemBuilder.newItem(XMaterial.POTION);
        builder.withDisplayName("&3Select entityEffects (optional)");
        if(!item.getEffects().isEmpty()){
            builder.withLore(item.getEffects());
        }
        ItemStack ent_effects = builder.build();
        
        ItemStack ent_equipment = ItemBuilder.newItem(XMaterial.DIAMOND_CHESTPLATE)
                .withDisplayName("&eSelect entityEquipment (optional)")
                .build();
        
        ItemStack offsetStack = item.getOffset().getItemToDisplay();
        
        ItemStack resetName = ItemBuilder.newItem(XMaterial.BARRIER)
                .withDisplayName("&cReset custom name")
                .build();
        
        ItemStack resetEffects = ItemBuilder.newItem(XMaterial.BARRIER)
                .withDisplayName("&cReset effects")
                .build();
        
        ItemStack resetEquipment = ItemBuilder.newItem(XMaterial.BARRIER)
                .withDisplayName("&cReset equipment")
                .build();
        
        menu.setItem(10, GUIItem.getBackItem());
        menu.setItem(11, ent_type);
        menu.setItem(12, ent_name);
        menu.setItem(13, ent_effects);
        menu.setItem(14, ent_equipment);
        menu.setItem(15, offsetStack);
        menu.setItem(16, GUIItem.getNextItem());
        
        for(int i=0;i<9;i++){
            menu.setItem(i, glass);
        }
        for(int i=18;i<27;i++){
            menu.setItem(i, glass);
        }
        menu.setItem(9, glass);
        menu.setItem(17, glass);
        
        menu.setItem(21, resetName);
        menu.setItem(22, resetEffects);
        menu.setItem(23, resetEquipment);
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction ENTITY_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        if(e.getLocation() == InventoryLocation.TOP){

            switch(e.getSlot()){
                case 10:
                    // Go to previous menu
                    onBack.accept(player);
                    break;
                case 16:
                    // Go to next menu and generate entityID
                    if(item.getType() == null){
                        break;
                    }
                    
                    if(!item.getType().isAlive()){
                        item.setEffects(new ArrayList());
                        item.getEquipment().resetEquipment();
                    }
                    
                    int entityID = item.getOutcome().getEntityRewardsNumber();
                    item.setID(entityID);
                    
                    onNext.accept(player, item);
                    break;
                case 11:
                    //EntityType
                    openEntityTypeInventory(player);
                    break;
                case 12:
                    //EntityCustomName
                    ChatListener.registerPlayer(player, message -> {
                        item.setCustom_name(message);
                        openEntityMenu(player);
                    });
                    player.closeInventory();
                    break;
                case 13:
                    //EntityEffects
                    Editor<EffectReward> editor = EditorType.EFFECT_REWARD.getEditor();
                    EffectMenu.setShowClearEffectsItem(false);
                    editor.createNewItem(
                            player, 
                            p -> openEntityMenu(p), 
                            (p, effectReward) -> {
                                String effectName = effectReward.getPotionEffect().getName();
                                int effectDuration = effectReward.getDuration();
                                int effectAmplifier = effectReward.getAmplifier();
                                item.getEffects().add(effectName + ";" + effectDuration + ";" + effectAmplifier);
                                
                                openEntityMenu(p);
                            });
                    break;
                case 14:
                    //EntityEquipment
                    Editor<Equipment> editor2 = EditorType.EQUIPMENT.getEditor();
                    editor2.editExistingItem(
                            item.getEquipment().clone(), 
                            player, 
                            p -> openEntityMenu(p), 
                            (p, equipment) -> {
                                item.setEquipment(equipment);
                                openEntityMenu(p);
                            });
                    break;
                case 15:
                    //Set offset
                    Editor<Offset> editor3 = EditorType.OFFSET.getEditor();
                    editor3.editExistingItem(
                            item.getOffset().clone(), 
                            player, 
                            p -> openEntityMenu(p), 
                            (p, offset) -> {
                                item.setOffset(offset);
                                openEntityMenu(p);
                            });
                    break;
                case 21:
                    item.setCustom_name(null);
                    openEntityMenu(player);
                    break;
                case 22:
                    item.getEffects().clear();
                    openEntityMenu(player);
                    break;
                case 23:
                    item.getEquipment().resetEquipment();
                    openEntityMenu(player);
                    break;
            }
        }
//</editor-fold>
    };
    
    //EntityType inventory
    private void openEntityTypeInventory(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.ENTITY_TYPE_MENU, 54, "&d&lEntity List");
        
        menu.setItem(45, GUIItem.getBackItem());
        
        entityListAdapter.updateMenu(menu);
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction ENTITY_TYPE_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 45:
                    // Back
                    entityListAdapter.goToMainPage();
                    openEntityMenu(player);
                    break;
                case 51:
                    // Go to previous page
                    if(entityListAdapter.goToPreviousPage()){
                        openEntityTypeInventory(player);
                    }
                    break;
                case 52:
                    // Go to main page
                    if(entityListAdapter.goToMainPage()){
                        openEntityTypeInventory(player);
                    }
                    break;
                case 53:
                    // Go to next page
                    if(entityListAdapter.goToNextPage()){
                        openEntityTypeInventory(player);
                    }
                    break;
                default:
                    ItemStack stack = e.getCurrentItem();
                    if(stack != null){
                        String displayName = ItemBuilder.fromItem(stack, false)
                                .getDisplayName();
                        if(displayName != null){
                            String typeName = Logger.stripColor(displayName);
                            item.setType(EntityType.valueOf(typeName));
                            entityListAdapter.goToMainPage();
                            openEntityMenu(player);
                        }                            
                    }
            }
        }
//</editor-fold>
    };    
}
