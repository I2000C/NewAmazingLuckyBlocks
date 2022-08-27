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
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.EntityReward.Age;
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
import me.i2000c.newalb.utils.Logger;
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
                    if(Age.isAgeable(entityType)){
                        builder.addLoreLine("&6Is ageable entity: &atrue");
                    }else{
                        builder.addLoreLine("&6Is ageable entity: &7false");
                    }
                    if(EntityReward.isTameable(entityType)){
                        builder.addLoreLine("&6Is tameable entity: &atrue");
                    }else{
                        builder.addLoreLine("&6Is tameable entity: &7false");
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
        Menu menu = GUIFactory.newMenu(CustomInventoryType.ENTITY_MENU, 54, "&2&lEntity Reward");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.MAGENTA);
        
        XMaterial material = EntityReward.getXMaterialFromEntityType(item.getType());
        ItemBuilder builder = ItemBuilder.newItem(material);
        EntityType entityType = item.getType();
        if(entityType == null){
            builder.withDisplayName("&6Select entity type");
        }else{
            builder.withDisplayName("&6Entity type: &r" + item.getType());
            if(entityType.isAlive()){
                builder.addLoreLine("&3Is living entity: &atrue");
            }else{
                builder.addLoreLine("&3Is living entity: &7false");
            }
            if(Age.isAgeable(entityType)){
                builder.addLoreLine("&3Is ageable entity: &atrue");
            }else{
                builder.addLoreLine("&3Is ageable entity: &7false");
            }
            if(EntityReward.isTameable(entityType)){
                builder.addLoreLine("&3Is tameable entity: &atrue");
            }else{
                builder.addLoreLine("&3Is tameable entity: &7false");
            }
        }
        ItemStack ent_type = builder.build();
        
        builder = ItemBuilder.newItem(XMaterial.NAME_TAG);
        if(item.getCustomName() == null){
            builder.withDisplayName("&aSelect entity custom name (optional)");
        }else{
            builder.withDisplayName("&aEntity custom name: &r" + item.getCustomName());
        }
        ItemStack ent_name = builder.build();
        
        ItemStack ent_name_visible = GUIItem.getBooleanItem(
                item.isCustomNameVisible(), 
                "&9Custom name visible", 
                XMaterial.CYAN_STAINED_GLASS_PANE, 
                XMaterial.GLASS_PANE);
        
        builder = ItemBuilder.newItem(XMaterial.POTION);
        builder.withDisplayName("&3Select entity effects (optional)");
        if(item.getType() == null){
            builder.addLoreLine("&cYou must select an entity first");
        }else if(!item.getType().isAlive()){
            builder.addLoreLine("&cYou cannot add effects to a non-living entity");
        }else{
            builder.withLore(item.getEffects());
        }
        ItemStack ent_effects = builder.build();
        
        builder = ItemBuilder.newItem(XMaterial.DIAMOND_CHESTPLATE);
        builder.withDisplayName("&eSelect entity equipment (optional)");
        if(item.getType() == null){
            builder.addLoreLine("&cYou must select an entity first");
        }else if(!item.getType().isAlive()){
            builder.addLoreLine("&cYou cannot add effects to a non-living entity");
        }
        ItemStack ent_equipment = builder.build();
        
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
        
        if(item.isTamed()){
            builder = ItemBuilder.newItem(XMaterial.LEAD);
            builder.withDisplayName("&eIs tamed: &atrue");
        }else{
            builder = ItemBuilder.newItem(XMaterial.ZOMBIE_HEAD);
            builder.withDisplayName("&eIs tamed: &cfalse");
        }
        builder.addLoreLine("&3Click to toggle");
        builder.addLoreLine("");
        builder.addLoreLine("&6Note that this is only used");
        builder.addLoreLine("&6  if the entity is tameable");
        ItemStack isTamedStack = builder.build();
        
        switch(item.getAge()){
            case BABY:
                builder = ItemBuilder.newItem(XMaterial.LEATHER_HELMET);
                break;
            case ADULT:
                builder = ItemBuilder.newItem(XMaterial.IRON_HELMET);
                break;
            default: //case RANDOM
                builder = ItemBuilder.newItem(XMaterial.GOLDEN_HELMET);
                break;
        }
        builder.withDisplayName("&dCurrent age: &e" + item.getAge().name());
        builder.addLoreLine("&3Click to toggle");
        builder.addLoreLine("");
        builder.addLoreLine("&6Note that this is only used");
        builder.addLoreLine("&6  if the entity is ageable");
        ItemStack ageStack = builder.build();
        
        builder = ItemBuilder.newItem(XMaterial.APPLE);
        if(item.getHealth() >= 0){
            builder.withDisplayName("&bCurrent health: &d" + item.getHealth());
        }else{
            builder.withDisplayName("&bCurrent health: &d&lDEFAULT");
        }
        builder.addLoreLine("&3Click to reset");
        ItemStack healthItem = builder.build();
        
        builder = ItemBuilder.newItem(XMaterial.SLIME_BLOCK);
        if(item.getSlimeSize() >= 0){
            builder.withDisplayName("&aCurrent slime size: &d" + item.getSlimeSize());
        }else{
            builder.withDisplayName("&aCurrent slime size: &d&lDEFAULT");
        }
        builder.addLoreLine("&3Click to reset");
        builder.addLoreLine("");
        builder.addLoreLine("&cIf slime size is greater than 15,");
        builder.addLoreLine("&c  the generated slime will be so big");
        ItemStack slimeSizeItem = builder.build();
        
        
        for(int i=0;i<9;i++){
            menu.setItem(i, glass);
        }
        for(int i=45;i<54;i++){
            menu.setItem(i, glass);
        }
        menu.setItem(9, glass);
        menu.setItem(18, glass);
        menu.setItem(27, glass);
        menu.setItem(36, glass);
        menu.setItem(17, glass);
        menu.setItem(26, glass);
        menu.setItem(35, glass);
        menu.setItem(44, glass);
        
        menu.setItem(10, GUIItem.getBackItem());
        menu.setItem(11, ent_type);
        menu.setItem(12, ent_name);
        if(item.getCustomName() != null){
            menu.setItem(3, ent_name_visible);
        }
        menu.setItem(13, ent_effects);
        menu.setItem(14, ent_equipment);
        menu.setItem(15, offsetStack);
        menu.setItem(16, GUIItem.getNextItem());
        
        if(Age.isAgeable(entityType)){
            menu.setItem(20, ageStack);
        }
        if(EntityReward.isTameable(entityType)){
            menu.setItem(2, isTamedStack);
        }
        if(item.getType() != null && item.getType().isAlive()){
            menu.setItem(34, GUIItem.getPlusLessItem(+100));
            menu.setItem(33, GUIItem.getPlusLessItem(+10));
            menu.setItem(32, GUIItem.getPlusLessItem(+1));
            menu.setItem(31, healthItem);
            menu.setItem(30, GUIItem.getPlusLessItem(-1));
            menu.setItem(29, GUIItem.getPlusLessItem(-10));
            menu.setItem(28, GUIItem.getPlusLessItem(-100));
        }
        if(item.getType() == EntityType.SLIME || item.getType() == EntityType.MAGMA_CUBE){
            menu.setItem(42, GUIItem.getPlusLessItem(+10));
            menu.setItem(41, GUIItem.getPlusLessItem(+1));
            menu.setItem(40, slimeSizeItem);
            menu.setItem(39, GUIItem.getPlusLessItem(-1));
            menu.setItem(38, GUIItem.getPlusLessItem(-10));
        }
        
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
                    
                    onNext.accept(player, item);
                    break;
                case 11:
                    //EntityType
                    openEntityTypeInventory(player);
                    break;
                case 12:
                    //EntityCustomName
                    ChatListener.registerPlayer(player, message -> {
                        item.setCustomName(message);
                        openEntityMenu(player);
                    });
                    player.closeInventory();
                    break;
                case 3:
                    //EntityCustomNameVisible
                    if(item.getCustomName() == null){
                        break;
                    }
                    
                    item.setCustomNameVisible(!item.isCustomNameVisible());
                    openEntityMenu(player);
                    break;
                case 13:
                    //EntityEffects
                    if(item.getType() == null || !item.getType().isAlive()){
                        break;
                    }
                    
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
                    if(item.getType() == null || !item.getType().isAlive()){
                        break;
                    }
                    
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
                case 2:
                    if(!EntityReward.isTameable(item.getType())){
                        break;
                    }
                    
                    item.setIsTamed(!item.isTamed());
                    openEntityMenu(player);
                    break;
                case 20:
                    if(!Age.isAgeable(item.getType())){
                        break;
                    }
                    
                    item.setAge(item.getAge().next());
                    openEntityMenu(player);
                    break;
                case 21:
                    item.setCustomName(null);
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
                    //<editor-fold defaultstate="collapsed" desc="Health">
                case 34:
                    // Health + 100
                    if(item.getType() == null || !item.getType().isAlive()){
                        break;
                    }
                    
                    int health = item.getHealth() + 100;
                    item.setHealth(health);
                    openEntityMenu(player);
                    break;
                case 33:
                    // Health + 10
                    if(item.getType() == null || !item.getType().isAlive()){
                        break;
                    }
                    
                    health = item.getHealth() + 10;
                    item.setHealth(health);
                    openEntityMenu(player);
                    break;
                case 32:
                    // Health + 1
                    if(item.getType() == null || !item.getType().isAlive()){
                        break;
                    }
                    
                    health = item.getHealth() + 1;
                    item.setHealth(health);
                    openEntityMenu(player);
                    break;
                case 31:
                    // Reset health
                    if(item.getType() == null || !item.getType().isAlive()){
                        break;
                    }
                    
                    item.setHealth(-1);
                    openEntityMenu(player);
                    break;
                case 30:
                    // Health - 1
                    if(item.getType() == null || !item.getType().isAlive()){
                        break;
                    }
                    
                    health = item.getHealth() - 1;
                    if(health < 0){
                        health = -1;
                    }
                    item.setHealth(health);
                    openEntityMenu(player);
                    break;
                case 29:
                    // Health - 10
                    if(item.getType() == null || !item.getType().isAlive()){
                        break;
                    }
                    
                    health = item.getHealth() - 10;
                    if(health < 0){
                        health = -1;
                    }
                    item.setHealth(health);
                    openEntityMenu(player);
                    break;
                case 28:
                    // Health - 100
                    if(item.getType() == null || !item.getType().isAlive()){
                        break;
                    }
                    
                    health = item.getHealth() - 100;
                    if(health < 0){
                        health = -1;
                    }
                    item.setHealth(health);
                    openEntityMenu(player);
                    break;
//</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="SlimeSize">
                case 42:
                    // SlimeSize + 10
                    if(item.getType() != EntityType.SLIME && item.getType() != EntityType.MAGMA_CUBE){
                        break;
                    }
                    
                    int slimeSize = item.getSlimeSize() + 10;
                    item.setSlimeSize(slimeSize);
                    openEntityMenu(player);
                    break;
                case 41:
                    // SlimeSize + 1
                    if(item.getType() != EntityType.SLIME && item.getType() != EntityType.MAGMA_CUBE){
                        break;
                    }
                    
                    slimeSize = item.getSlimeSize() + 1;
                    item.setSlimeSize(slimeSize);
                    openEntityMenu(player);
                    break;
                case 40:
                    // Reset slime size
                    if(item.getType() != EntityType.SLIME && item.getType() != EntityType.MAGMA_CUBE){
                        break;
                    }
                    
                    item.setSlimeSize(-1);
                    openEntityMenu(player);
                    break;
                case 39:
                    // SlimeSize - 1
                    if(item.getType() != EntityType.SLIME && item.getType() != EntityType.MAGMA_CUBE){
                        break;
                    }
                    
                    slimeSize = item.getSlimeSize() - 1;
                    if(slimeSize < 0){
                        slimeSize = -1;
                    }
                    item.setSlimeSize(slimeSize);
                    openEntityMenu(player);
                    break;
                case 38:
                    // SlimeSize - 10
                    if(item.getType() != EntityType.SLIME && item.getType() != EntityType.MAGMA_CUBE){
                        break;
                    }
                    
                    slimeSize = item.getSlimeSize() - 10;
                    if(slimeSize < 0){
                        slimeSize = -1;
                    }
                    item.setSlimeSize(slimeSize);
                    openEntityMenu(player);
                    break;
//</editor-fold>
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
