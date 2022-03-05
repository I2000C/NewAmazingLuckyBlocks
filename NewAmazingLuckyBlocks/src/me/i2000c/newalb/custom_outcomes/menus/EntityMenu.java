package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.i2000c.newalb.custom_outcomes.utils.rewards.EntityReward;
import me.i2000c.newalb.custom_outcomes.utils.rewards.EntityReward.Equipment;
import me.i2000c.newalb.utils.Logger;
import java.util.Arrays;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

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
        ItemStack glass = XMaterial.MAGENTA_STAINED_GLASS_PANE.parseItem();
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        
        ItemStack ent_type = XMaterial.GHAST_SPAWN_EGG.parseItem();
        meta = ent_type.getItemMeta();
        if(reward.getType() == null){
            meta.setDisplayName(Logger.color("&6Select entityType"));
        }else{
            meta.setDisplayName(Logger.color("&6entityType: &r" + reward.getType()));
        }
        ent_type.setItemMeta(meta);
        
        ItemStack ent_name = new ItemStack(Material.NAME_TAG);
        meta = ent_name.getItemMeta();
        if(reward.getCustom_name() == null){
            meta.setDisplayName(Logger.color("&aSelect entityCustomName (optional)"));
        }else{
            meta.setDisplayName(Logger.color("&aentityCustomName: &r" + reward.getCustom_name()));
        }
        ent_name.setItemMeta(meta);
        
        ItemStack ent_effects = new ItemStack(Material.POTION);
        meta = ent_effects.getItemMeta();
        meta.setDisplayName(Logger.color("&3Select entityEffects (optional)"));
        if(!reward.getEffects().isEmpty()){
            meta.setLore(reward.getEffects());
        }
        ent_effects.setItemMeta(meta);
        
        ItemStack ent_equipment = new ItemStack(Material.DIAMOND_CHESTPLATE);
        meta = ent_equipment.getItemMeta();
        meta.setDisplayName(Logger.color("&eSelect entityEquipment (optional)"));
        ent_equipment.setItemMeta(meta);
        
        ItemStack offsetStack = XMaterial.PISTON.parseItem();
        meta = offsetStack.getItemMeta();
        meta.setDisplayName(Logger.color("&3Configure offset"));
        List<String> loreList = new ArrayList();
        loreList.add(Logger.color("&dCurrent Offset:"));
        loreList.add(Logger.color("   &5X: &3" + reward.getOffset().getOffsetX()));
        loreList.add(Logger.color("   &5Y: &3" + reward.getOffset().getOffsetY()));
        loreList.add(Logger.color("   &5Z: &3" + reward.getOffset().getOffsetZ()));
        meta.setLore(loreList);
        offsetStack.setItemMeta(meta);
        
        ItemStack next = new ItemStack(Material.ANVIL);
        meta = next.getItemMeta();
        meta.setDisplayName(Logger.color("&bNext"));
        next.setItemMeta(meta);
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName(Logger.color("&7Back"));
        back.setItemMeta(meta);
        
        ItemStack resetName = new ItemStack(Material.BARRIER);
        meta = resetName.getItemMeta();
        meta.setDisplayName(Logger.color("&cReset custom name"));
        resetName.setItemMeta(meta);
        
        ItemStack resetEffects = new ItemStack(Material.BARRIER);
        meta = resetEffects.getItemMeta();
        meta.setDisplayName(Logger.color("&cReset effects"));
        resetEffects.setItemMeta(meta);
        
        ItemStack resetEquipment = new ItemStack(Material.BARRIER);
        meta = resetEquipment.getItemMeta();
        meta.setDisplayName(Logger.color("&cReset equipment"));
        resetEquipment.setItemMeta(meta);
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.ENTITY_MENU, 27, Logger.color("&2&lEntity Reward"));
        
        inv.setItem(10, back);
        inv.setItem(11, ent_type);
        inv.setItem(12, ent_name);
        inv.setItem(13, ent_effects);
        inv.setItem(14, ent_equipment);
        inv.setItem(15, offsetStack);
        inv.setItem(16, next);
        
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
        String inv_name = Logger.color("&2&lEntity Reward");
        String name = Logger.stripColor(inv_name);
        
        if(e.getView() == null || e.getView().getTitle() == null || e.getClickedInventory() == null){
            return;
        }
        
        
        if(Logger.stripColor(e.getView().getTitle()).equals(name)){
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
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = back.getItemMeta();
        meta.setDisplayName(Logger.color("&7Back"));
        back.setItemMeta(meta);
        
        ItemStack previousPage = new ItemStack(Material.MAGMA_CREAM);
        meta = previousPage.getItemMeta();
        meta.setDisplayName(Logger.color("&7Previous page"));
        previousPage.setItemMeta(meta);
        
        ItemStack nextPage = XMaterial.ENDER_EYE.parseItem();
        meta = nextPage.getItemMeta();
        meta.setDisplayName(Logger.color("&bNext page"));
        nextPage.setItemMeta(meta);
        
        ItemStack pages = new ItemStack(Material.BOOK);
        pages.setAmount(index+1);
        meta = pages.getItemMeta();
        meta.setDisplayName(Logger.color("&6Page &5" + (index+1) + " &6of &d" + max_pages));
        pages.setItemMeta(meta);
        
        inv.setItem(45, back);
        
        inv.setItem(51, previousPage);
        inv.setItem(52, pages);
        inv.setItem(53, nextPage);
        
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
            ItemStack sk = XMaterial.GHAST_SPAWN_EGG.parseItem();
            meta = sk.getItemMeta();
            meta.setDisplayName(Logger.color("&3" + typeName));
            if(EntityType.valueOf(typeName).isAlive()){
                meta.setLore(Arrays.asList(Logger.color("&6Is living entity: &atrue")));
            }else{
                meta.setLore(Arrays.asList(Logger.color("&6Is living entity: &7false")));
            }
            sk.setItemMeta(meta);
            
            inv.setItem(i, sk);
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
                    if(stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()){
                        String typeName = Logger.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                        reward.setType(EntityType.valueOf(typeName));
                        index = 0;
                        openEntityMenu(p);
                    }
            }
        }
//</editor-fold>
    };
    
    //EntityEffects inventory
    private static void openEntityEffectsInventory(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.ENTITY_EFFECTS_MENU, 54, "&d&lEffect List");
        
        List<String> effectTypeNames = new ArrayList();
        for(PotionEffectType pe : PotionEffectType.values()){
            try{
                effectTypeNames.add(pe.getName());
            }catch(Exception ex){
                
            }
        }
        Collections.sort(effectTypeNames);
        
        for(int i=0;i<effectTypeNames.size();i++){
            String typeName = effectTypeNames.get(i);
            
            ItemStack sk = new ItemStack(Material.POTION);
            ItemMeta meta = sk.getItemMeta();
            meta.setDisplayName(Logger.color("&d" + typeName));
            PotionMeta pm = (PotionMeta) meta;
            pm.addCustomEffect(new PotionEffect(PotionEffectType.getByName(typeName), 0, 0), true);
            sk.setItemMeta(pm);
            
            inv.setItem(i, sk);
        }
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = back.getItemMeta();
        meta.setDisplayName(Logger.color("&7Back"));
        back.setItemMeta(meta);
        
        inv.setItem(45, back);
        
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
                effect_name = Logger.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                openEntityEffects2Inventory(p);
            }
        }
//</editor-fold>
    };
    
    //EntityEffects2 inventory
    private static void openEntityEffects2Inventory(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.ENTITY_EFFECTS_MENU_2, 45, "&5&lEffect Config");
        
        ItemStack glass = XMaterial.MAGENTA_STAINED_GLASS_PANE.parseItem();
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        
        ItemStack time = XMaterial.CLOCK.parseItem();
        meta = time.getItemMeta();
        if(effect_time < 0){
            meta.setDisplayName(Logger.color("&6Effect time (seconds): &ainfinite"));
        }else{
            meta.setDisplayName(Logger.color("&6Effect time (seconds): &a" + effect_time));
        }
        meta.setLore(Arrays.asList(Logger.color("&3Click to reset")));
        time.setItemMeta(meta);
        
        ItemStack amplifier = new ItemStack(Material.BEACON);
        meta = amplifier.getItemMeta();
        meta.setDisplayName(Logger.color("&6Effect amplifier: &a" + effect_amplifier));
        meta.setLore(Arrays.asList(Logger.color("&3Click to reset")));
        amplifier.setItemMeta(meta);
        
        
        ItemStack minus1 = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
        meta = minus1.getItemMeta();
        meta.setDisplayName(Logger.color("&c&l-1"));
        minus1.setItemMeta(meta);
        
        ItemStack minus10 = minus1.clone();
        meta = minus10.getItemMeta();
        meta.setDisplayName(Logger.color("&c&l-10"));
        minus10.setItemMeta(meta);
        
        ItemStack minus100 = minus1.clone();
        meta = minus100.getItemMeta();
        meta.setDisplayName(Logger.color("&c&l-100"));
        minus100.setItemMeta(meta);
        
        ItemStack plus1 = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
        meta = minus1.getItemMeta();
        meta.setDisplayName(Logger.color("&a&l+1"));
        plus1.setItemMeta(meta);
        
        ItemStack plus10 = plus1.clone();
        meta = plus10.getItemMeta();
        meta.setDisplayName(Logger.color("&a&l+10"));
        plus10.setItemMeta(meta);
        
        ItemStack plus100 = plus1.clone();
        meta = plus100.getItemMeta();
        meta.setDisplayName(Logger.color("&a&l+100"));
        plus100.setItemMeta(meta);
        
        
        ItemStack effectStack = new ItemStack(Material.POTION);
        meta = effectStack.getItemMeta();
        meta.setDisplayName(Logger.color("&bSelected effect: &d" + effect_name));
        PotionMeta pm = (PotionMeta) meta;
        pm.addCustomEffect(new PotionEffect(PotionEffectType.getByName(effect_name), 0, 0), true);
        effectStack.setItemMeta(pm);
        
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName(Logger.color("&7Back"));
        back.setItemMeta(meta);
        
        ItemStack next = new ItemStack(Material.ANVIL);
        meta = next.getItemMeta();
        meta.setDisplayName(Logger.color("&bNext"));
        next.setItemMeta(meta);
        
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
        
        inv.setItem(10, back);
        inv.setItem(16, next);
        
        inv.setItem(13, effectStack);
        
        inv.setItem(19, minus100);
        inv.setItem(20, minus10);
        inv.setItem(21, minus1);
        inv.setItem(22, time);
        inv.setItem(23, plus1);
        inv.setItem(24, plus10);
        inv.setItem(25, plus100);
        
        inv.setItem(28, minus100);
        inv.setItem(29, minus10);
        inv.setItem(30, minus1);
        inv.setItem(31, amplifier);
        inv.setItem(32, plus1);
        inv.setItem(33, plus10);
        inv.setItem(34, plus100);
        
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
        ItemStack glass = XMaterial.BLACK_STAINED_GLASS_PANE.parseItem();
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        
        ItemStack creative = XMaterial.CRAFTING_TABLE.parseItem();
        meta = creative.getItemMeta();
        meta.setDisplayName(Logger.color("&3Close menu to pick items from creative mode"));
        creative.setItemMeta(meta);
        
        ItemStack next = new ItemStack(Material.ANVIL);
        meta = next.getItemMeta();
        meta.setDisplayName(Logger.color("&bNext"));
        next.setItemMeta(meta);
        
        ItemStack helmet = XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE.parseItem();
        meta = helmet.getItemMeta();
        meta.setDisplayName(Logger.color("&bSelect helmet"));
        helmet.setItemMeta(meta);
        
        ItemStack chestplate = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
        meta = chestplate.getItemMeta();
        meta.setDisplayName(Logger.color("&aSelect chestplate"));
        chestplate.setItemMeta(meta);
        
        ItemStack leggings = XMaterial.YELLOW_STAINED_GLASS_PANE.parseItem();
        meta = leggings.getItemMeta();
        meta.setDisplayName(Logger.color("&eSelect leggings"));
        leggings.setItemMeta(meta);
        
        ItemStack boots = XMaterial.ORANGE_STAINED_GLASS_PANE.parseItem();
        meta = boots.getItemMeta();
        meta.setDisplayName(Logger.color("&6Select boots"));
        boots.setItemMeta(meta);
        
        ItemStack item = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
        meta = item.getItemMeta();
        meta.setDisplayName(Logger.color("&cSelect item in hand"));
        item.setItemMeta(meta);
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName(Logger.color("&2Back"));
        back.setItemMeta(meta);
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.EQUIPMENT_MENU, 54, "&e&lEquipment Config");
        
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
            inv.setItem(49, item);
        }else{
            inv.setItem(49, equipment.itemInHand);
        }
        
        inv.setItem(27, back);
        inv.setItem(35, next);
        
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
                    p.sendMessage(Logger.color("&6Use &b/alb return &6to return to the menu"));
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
                    equipment = reward.getEquipment().cloneEquipment();
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
