package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import me.i2000c.newalb.custom_outcomes.utils.rewards.EntityReward;
import me.i2000c.newalb.custom_outcomes.utils.rewards.EntityReward.Equipment;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EntityMenu{
    public static EntityReward reward = null;
    
    private static String effect_name = null;
    private static int effect_time = 60;
    private static int effect_amplifier = 0;
    
    private static int equipment_slot = 0;
    public static Equipment equipment = null;
    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.ENTITY_MENU, ENTITY_MENU_FUNCTION);
            InventoryListener.registerInventory(CustomInventoryType.ENTITY_TYPE_MENU, ENTITY_TYPE_MENU_FUNCTION);
            InventoryListener.registerInventory(CustomInventoryType.ENTITY_EFFECTS_MENU, EFFECTS_MENU_FUNCTION);
            InventoryListener.registerInventory(CustomInventoryType.ENTITY_EFFECTS_MENU_2, EFFECTS_MENU_2_FUNCTION);
            InventoryListener.registerInventory(CustomInventoryType.EQUIPMENT_MENU, EQUIPMENT_MENU_FUNCTION);
            
            inventoriesRegistered = true;
        }
        
        reward = null;
        
        equipment_slot = 0;
        equipment = null;
        
        max_pages = -1;
        index = 0;
        
        effect_name = null;
        effect_time = 60;
        effect_amplifier = 0;
    }
    
    //Entity inventory
    public static void openEntityMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(reward == null){
            reward = new EntityReward(FinishMenu.getCurrentOutcome(), FinishMenu.getCurrentOutcome().getEntityRewardList().size());
            equipment = reward.getEquipment();
        }
        ItemStack glass = GUIItem.getGlassItem(GlassColor.MAGENTA);
        
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.GHAST_SPAWN_EGG);
        if(reward.getType() == null){
            builder.withDisplayName("&6Select entityType");
        }else{
            builder.withDisplayName("&6entityType: &r" + reward.getType());
        }
        ItemStack ent_type = builder.build();
        
        builder = ItemBuilder.newItem(XMaterial.NAME_TAG);
        if(reward.getCustom_name() == null){
            builder.withDisplayName("&aSelect entityCustomName (optional)");
        }else{
            builder.withDisplayName("&aentityCustomName: &r" + reward.getCustom_name());
        }
        ItemStack ent_name = builder.build();
        
        builder = ItemBuilder.newItem(XMaterial.POTION);
        builder.withDisplayName("&3Select entityEffects (optional)");
        if(!reward.getEffects().isEmpty()){
            builder.withLore(reward.getEffects());
        }
        ItemStack ent_effects = builder.build();
        
        ItemStack ent_equipment = ItemBuilder.newItem(XMaterial.DIAMOND_CHESTPLATE)
                .withDisplayName("&eSelect entityEquipment (optional)")
                .build();
        
        ItemStack offsetStack = reward.getOffset().getItemToDisplay();
        
        ItemStack resetName = ItemBuilder.newItem(XMaterial.BARRIER)
                .withDisplayName("&cReset custom name")
                .build();
        
        ItemStack resetEffects = ItemBuilder.newItem(XMaterial.BARRIER)
                .withDisplayName("&cReset effects")
                .build();
        
        ItemStack resetEquipment = ItemBuilder.newItem(XMaterial.BARRIER)
                .withDisplayName("&cReset equipment")
                .build();
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.ENTITY_MENU, 27, "&2&lEntity Reward");
        
        inv.setItem(10, GUIItem.getBackItem());
        inv.setItem(11, ent_type);
        inv.setItem(12, ent_name);
        inv.setItem(13, ent_effects);
        inv.setItem(14, ent_equipment);
        inv.setItem(15, offsetStack);
        inv.setItem(16, GUIItem.getNextItem());
        
        for(int i=0;i<9;i++){
            inv.setItem(i, glass);
        }
        for(int i=18;i<27;i++){
            inv.setItem(i, glass);
        }
        inv.setItem(9, glass);
        inv.setItem(17, glass);
        
        inv.setItem(21, resetName);
        inv.setItem(22, resetEffects);
        inv.setItem(23, resetEquipment);
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction ENTITY_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){

            switch(e.getSlot()){
                case 10:
                    if(FinishMenu.editMode){
                        FinishMenu.openFinishInventory(p);
                    }else{
                        RewardTypesMenu.openRewardTypesMenu(p);
                    }
                    break;
                case 11:
                    //EntityType
                    openEntityTypeInventory(p);
                    break;
                case 12:
                    //EntityCustomName
                    ChatListener.registerPlayer(p, message -> {
                        reward.setCustom_name(message);
                        openEntityMenu(p);
                    });
                    p.closeInventory();
                    break;
                case 13:
                    //EntityEffects
                    openEntityEffectsInventory(p);
                    break;
                case 14:
                    //EntityEquipment
                    openEntityEquipmentInventory(p);
                    break;
                case 15:
                    //Set offset
                    OffsetMenu.reset();
                    OffsetMenu.setCurrentData(reward.getOffset(), pl -> openEntityMenu(pl));
                    OffsetMenu.openOffsetMenu(p);
                    break;
                case 16:
                    if(reward.getType() == null){
                        return;
                    }
                    //open FinishMenu
                    if(!reward.getType().isAlive()){
                        reward.setEffects(new ArrayList());
                        reward.getEquipment().resetEquipment();
                    }
                    FinishMenu.addReward(reward);
                    reset();
                    FinishMenu.openFinishInventory(p);
                    break;
                case 21:
                    reward.setCustom_name(null);
                    openEntityMenu(p);
                    break;
                case 22:
                    reward.getEffects().clear();
                    openEntityMenu(p);
                    break;
                case 23:
                    reward.getEquipment().resetEquipment();
                    openEntityMenu(p);
                    break;
            }
        }
//</editor-fold>
    };
    
    private static final int MENU_SIZE = 45;
    private static int max_pages = 0;
    
    private static int index = 0;
    
    //EntityType inventory
    private static void openEntityTypeInventory(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(max_pages == -1){
            if(EntityType.values().length % MENU_SIZE == 0){
                max_pages = (EntityType.values().length / MENU_SIZE);
            }else{
                max_pages = (EntityType.values().length / MENU_SIZE) + 1;
            }
        }
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.ENTITY_TYPE_MENU, 54, "&d&lEntity List");
        
        inv.setItem(45, GUIItem.getBackItem());
        
        inv.setItem(51, GUIItem.getPreviousPageItem());
        inv.setItem(52, GUIItem.getCurrentPageItem(index+1, max_pages));
        inv.setItem(53, GUIItem.getNextPageItem());
        
        List<String> entTypeNames = new ArrayList();
        for(EntityType ent : EntityType.values()){
            if(ent.isSpawnable() && !ent.equals(EntityType.PLAYER)){
                entTypeNames.add(ent.name());
            }
        }
        Collections.sort(entTypeNames);
        
        int n = Integer.min((entTypeNames.size()-MENU_SIZE*index),MENU_SIZE);
        for(int i=0;i<n;i++){
            String typeName = entTypeNames.get(i + index*MENU_SIZE);
            
            ItemBuilder builder = ItemBuilder.newItem(XMaterial.GHAST_SPAWN_EGG);
            builder.withDisplayName("&3" + typeName);
            if(EntityType.valueOf(typeName).isAlive()){
                builder.addLoreLine("&6Is living entity: &atrue");
            }else{
                builder.addLoreLine("&6Is living entity: &7false");
            }
            
            inv.setItem(i, builder.build());
        }
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction ENTITY_TYPE_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            switch(e.getSlot()){
                case 45:
                    //Back
                    index = 0;
                    openEntityMenu(p);
                    break;
                case 51:
                    //Previous page
                    if(max_pages > 1){
                        index--;
                        if(index < 0){
                            index = max_pages - 1;
                        }
                        openEntityTypeInventory(p);
                    }
                    break;
                case 52:
                    //Home page
                    if(max_pages > 1){
                        index = 0;
                        openEntityTypeInventory(p);
                    }
                    break;
                case 53:
                    //Next page
                    if(max_pages > 1){
                        index++;
                        if(index >= max_pages){
                            index = 0;
                        }
                        openEntityTypeInventory(p);
                    }
                    break;
                default:
                    ItemStack stack = e.getCurrentItem();
                    if(stack != null){
                        String displayName = ItemBuilder.fromItem(stack, false)
                                .getDisplayName();
                        if(displayName != null){
                            String typeName = Logger.stripColor(displayName);
                            reward.setType(EntityType.valueOf(typeName));
                            index = 0;
                            openEntityMenu(p);
                        }                            
                    }
            }
        }
//</editor-fold>
    };
    
    //EntityEffects inventory
    private static void openEntityEffectsInventory(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.EFFECT_MENU_2, 54, "&d&lEffect List");
        
        List<PotionEffectType> effectTypes = Arrays.asList(PotionEffectType.values());
        effectTypes.sort((effectType1, effectType2) -> {
            String effectTypeName1 = effectType1.getName();
            String effectTypeName2 = effectType2.getName();
            return effectTypeName1.compareTo(effectTypeName2);
        });
        
        Iterator<PotionEffectType> iterator = effectTypes.iterator();
        for(int i=0; iterator.hasNext() && i<45; i++){
            PotionEffectType effectType = iterator.next();
            PotionEffect potionEffect = new PotionEffect(effectType, 0, 0);
            ItemStack effectItem = ItemBuilder.newItem(XMaterial.POTION)
                    .withDisplayName("&d" + effectType.getName())
                    .addPotionEffect(potionEffect)
                    .build();
            inv.setItem(i, effectItem);
        }
        
        inv.setItem(45, GUIItem.getBackItem());
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction EFFECTS_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){

            if(e.getSlot() == 45){
                openEntityMenu(p);
                return;
            }
            if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
                String displayName = ItemBuilder.fromItem(e.getCurrentItem(), false)
                        .getDisplayName();
                if(displayName != null){
                    effect_name = Logger.stripColor(displayName);
                    openEntityEffects2Inventory(p);
                }                    
            }
        }
//</editor-fold>
    };
    
    //EntityEffects2 inventory
    private static void openEntityEffects2Inventory(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.ENTITY_EFFECTS_MENU_2, 45, "&5&lEffect Config");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.MAGENTA);
        
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.CLOCK);
        if(effect_time < 0){
            builder.withDisplayName("&6Effect time (seconds): &ainfinite");
        }else{
            builder.withDisplayName("&6Effect time (seconds): &a" + effect_time);
        }
        builder.addLoreLine("&3Click to reset");
        ItemStack time_item = builder.build();
        
        ItemStack amplifier = ItemBuilder.newItem(XMaterial.BEACON)
                .withDisplayName("&6Effect amplifier: &a" + effect_amplifier)
                .addLoreLine("&3Click to reset")
                .build();
        
        builder = ItemBuilder.newItem(XMaterial.POTION);
        if(effect_name == null){
            builder.withDisplayName("&bSelected effect: &dnull");
        }else{
            builder.withDisplayName("&bSelected effect: &d" + effect_name);
            builder.addPotionEffect(new PotionEffect(PotionEffectType.getByName(effect_name), 0, 0));
        }
        ItemStack effectStack = builder.build();
        
        for(int i=0;i<9;i++){
            inv.setItem(i, glass);
        }
        for(int i=36;i<45;i++){
            inv.setItem(i, glass);
        }
        inv.setItem(9, glass);
        inv.setItem(17, glass);
        inv.setItem(18, glass);
        inv.setItem(26, glass);
        inv.setItem(27, glass);
        inv.setItem(35, glass);
        
        inv.setItem(10, GUIItem.getBackItem());
        inv.setItem(16, GUIItem.getNextItem());
        
        inv.setItem(13, effectStack);
        
        inv.setItem(19, GUIItem.getPlusLessItem(-100));
        inv.setItem(20, GUIItem.getPlusLessItem(-10));
        inv.setItem(21, GUIItem.getPlusLessItem(-1));
        inv.setItem(22, time_item);
        inv.setItem(23, GUIItem.getPlusLessItem(+1));
        inv.setItem(24, GUIItem.getPlusLessItem(+10));
        inv.setItem(25, GUIItem.getPlusLessItem(+100));
        
        inv.setItem(28, GUIItem.getPlusLessItem(-100));
        inv.setItem(29, GUIItem.getPlusLessItem(-10));
        inv.setItem(30, GUIItem.getPlusLessItem(-1));
        inv.setItem(31, amplifier);
        inv.setItem(32, GUIItem.getPlusLessItem(+1));
        inv.setItem(33, GUIItem.getPlusLessItem(+10));
        inv.setItem(34, GUIItem.getPlusLessItem(+100));
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction EFFECTS_MENU_2_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){

            switch(e.getSlot()){
                case 10:
                    //Return to the previous menu
                    openEntityEffectsInventory(p);
                    break;
                /*case 12:
                    //Select effect time
                    player = p;
                    chat2 = true;
                    p.closeInventory();
                    break;
                case 14:
                    //Select effect amplifier
                    player = p;
                    chat3 = true;
                    p.closeInventory();
                    break;*/
                case 16:
                    //Open entity menu
                    if(effect_name != null){
                        reward.getEffects().add(effect_name + ";" + effect_time + ";" + effect_amplifier);
                        effect_name = null;
                        effect_time = 60;
                        effect_amplifier = 0;
                        openEntityMenu(p);
                    }
                    break;
                //<editor-fold defaultstate="collapsed" desc="EffectTime">
                case 19:
                    //Effect time -100
                    effect_time -= 100;
                    if(effect_time < -1){
                        effect_time = -1;
                    }
                    openEntityEffects2Inventory(p);
                    break;
                case 20:
                    //Effect time -10
                    effect_time -= 10;
                    if(effect_time < -1){
                        effect_time = -1;
                    }
                    openEntityEffects2Inventory(p);
                    break;
                case 21:
                    //Effect time -1
                    effect_time--;
                    if(effect_time < -1){
                        effect_time = -1;
                    }
                    openEntityEffects2Inventory(p);
                    break;
                case 22:
                    //Effect time = 60
                    effect_time = 60;
                    openEntityEffects2Inventory(p);
                    break;
                case 23:
                    //Effect time +1
                    effect_time ++;
                    openEntityEffects2Inventory(p);
                    break;
                case 24:
                    //Effect time +10
                    effect_time += 10;
                    openEntityEffects2Inventory(p);
                    break;
                case 25:
                    //Effect time +100
                    effect_time += 100;
                    openEntityEffects2Inventory(p);
                    break;
//</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="EffectAmplifier">
                case 28:
                    //Effect amplifier -100
                    effect_amplifier -= 100;
                    if(effect_amplifier < 0){
                        effect_amplifier = 0;
                    }
                    openEntityEffects2Inventory(p);
                    break;
                case 29:
                    //Effect amplifier -10
                    effect_amplifier -= 10;
                    if(effect_amplifier < 0){
                        effect_amplifier = 0;
                    }
                    openEntityEffects2Inventory(p);
                    break;
                case 30:
                    //Effect amplifier -1
                    effect_amplifier--;
                    if(effect_amplifier < 0){
                        effect_amplifier = 0;
                    }
                    openEntityEffects2Inventory(p);
                    break;
                case 31:
                    //Effect amplifier = 0
                    effect_amplifier = 0;
                    openEntityEffects2Inventory(p);
                    break;
                case 32:
                    //Effect amplifier +1
                    effect_amplifier ++;
                    if(effect_amplifier > 255){
                        effect_amplifier = 255;
                    }
                    openEntityEffects2Inventory(p);
                    break;
                case 33:
                    //Effect amplifier +10
                    effect_amplifier += 10;
                    if(effect_amplifier > 255){
                        effect_amplifier = 255;
                    }
                    openEntityEffects2Inventory(p);
                    break;
                case 34:
                    //Effect amplifier +100
                    effect_amplifier += 100;
                    if(effect_amplifier > 255){
                        effect_amplifier = 255;
                    }
                    openEntityEffects2Inventory(p);
                    break;
//</editor-fold>

            }
        }
//</editor-fold>
    };
    
    //Equipment inventory
    private static void openEntityEquipmentInventory(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.EQUIPMENT_MENU, 54, "&e&lEquipment Config");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.BLACK);
        
        ItemStack creative = ItemBuilder.newItem(XMaterial.CRAFTING_TABLE)
                .withDisplayName("&3Close menu to pick items from creative mode")
                .build();
        
        ItemStack helmet = ItemBuilder.newItem(XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE)
                .withDisplayName("&bSelect helmet")
                .build();
        
        ItemStack chestplate = ItemBuilder.newItem(XMaterial.LIME_STAINED_GLASS_PANE)
                .withDisplayName("&aSelect chestplate")
                .build();
        
        ItemStack leggings = ItemBuilder.newItem(XMaterial.YELLOW_STAINED_GLASS_PANE)
                .withDisplayName("&eSelect leggings")
                .build();
        
        ItemStack boots = ItemBuilder.newItem(XMaterial.ORANGE_STAINED_GLASS_PANE)
                .withDisplayName("&6Select boots")
                .build();
        
        ItemStack itemInHand = ItemBuilder.newItem(XMaterial.RED_STAINED_GLASS_PANE)
                .withDisplayName("&cSelect item in hand")
                .build();
        
        for(int i=0;i<54;i++){
            inv.setItem(i, glass);
        }
        
        if(equipment.helmet == null){
            inv.setItem(13, helmet);
        }else{
            inv.setItem(13, equipment.helmet);
        }
        if(equipment.chestplate == null){
            inv.setItem(22, chestplate);
        }else{
            inv.setItem(22, equipment.chestplate);
        }
        if(equipment.leggings == null){
            inv.setItem(31, leggings);
        }else{
            inv.setItem(31, equipment.leggings);
        }
        if(equipment.boots == null){
            inv.setItem(40, boots);
        }else{
            inv.setItem(40, equipment.boots);
        }
        if(equipment.itemInHand == null){
            inv.setItem(49, itemInHand);
        }else{
            inv.setItem(49, equipment.itemInHand);
        }
        
        inv.setItem(27, GUIItem.getBackItem());
        inv.setItem(35, GUIItem.getNextItem());
        
        inv.setItem(45, creative);
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction EQUIPMENT_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            //Change the 'equipment_slot' depending on what slot has been clicked from {13, 22, 31, 40, 49}
            switch(e.getSlot()){
                case 45:
                    //Close menu
                    p.closeInventory();
                    Logger.sendMessage("&6Use &b/alb return &6to return to the menu", p);
                    break;
                case 13:
                    equipment_slot = 13;
                    break;
                case 22:
                    equipment_slot = 22;
                    break;
                case 31:
                    equipment_slot = 31;
                    break;
                case 40:
                    equipment_slot = 40;
                    break;
                case 49:
                    equipment_slot = 49;
                    break;
                case 35:
                    //Open next inventory when "next" is clicked and then set 'equipment_slot' to 0
                    //Add all ItemStacks to the 'equipment' list
                    equipment_slot = 0;
                    reward.setEquipment(equipment);

                    openEntityMenu(p);
                    break;
                case 27:
                    //Reset equipment
                    equipment = reward.getEquipment().clone();
                    openEntityMenu(p);
                    break;
            }
        }else if(e.getClickedInventory().equals(e.getView().getBottomInventory())){
            if(equipment_slot != 0){
                switch(equipment_slot){
                    case 13:
                        if(e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)){
                            equipment.helmet = null;
                        }else{
                            equipment.helmet = e.getCurrentItem().clone();
                        }
                        break;
                    case 22:
                        if(e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)){
                            equipment.chestplate = null;
                        }else{
                            equipment.chestplate = e.getCurrentItem().clone();
                        }
                        break;
                    case 31:
                        if(e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)){
                            equipment.leggings = null;
                        }else{
                            equipment.leggings = e.getCurrentItem().clone();
                        }
                        break;
                    case 40:
                        if(e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)){
                            equipment.boots = null;
                        }else{
                            equipment.boots = e.getCurrentItem().clone();
                        }
                        break;
                    case 49:
                        if(e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)){
                            equipment.itemInHand = null;
                        }else{
                            equipment.itemInHand = e.getCurrentItem().clone();
                        }
                        break;
                }

                //Clone the selected item to the item in the 'equipment_slot' slot
                openEntityEquipmentInventory(p);
            }
        }
//</editor-fold>
    };
}
