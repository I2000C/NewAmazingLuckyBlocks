package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.utils.rewards.ItemReward;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils.textures.InvalidHeadException;
import me.i2000c.newalb.utils.textures.Texture;
import me.i2000c.newalb.utils.textures.TextureManager;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class ItemMenu{
    private static String color;
    
    private static String effect_name;
    private static int effect_time;
    private static int effect_amplifier;
        
    private static String enchantName;
    private static int enchantLevel;
    public static int amount;
    
    public static ItemReward reward;
    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.ITEM_MENU, ITEM_MENU_FUNCTION);
            InventoryListener.registerInventory(CustomInventoryType.ITEM_MENU_2, ITEM_MENU_2_FUNCTION);
            InventoryListener.registerInventory(CustomInventoryType.ENCHATMENTS_MENU, ENCHANTMENTS_MENU_FUNCTION);
            InventoryListener.registerInventory(CustomInventoryType.ENCHANTMENT_NAME_MENU, ENCHANTMENT_NAME_MENU_FUNCTION);
            
            InventoryListener.registerInventory(CustomInventoryType.ITEM_COLOR_MENU, ITEM_COLOR_MENU_FUNCTION);
            
            InventoryListener.registerInventory(CustomInventoryType.POTION_EFFECTS_MENU, POTION_EFFECTS_MENU_FUNCTION);
            InventoryListener.registerInventory(CustomInventoryType.POTION_EFFECTS_MENU_2, POTION_EFFECTS_MENU_2_FUNCTION);
            
            inventoriesRegistered = true;
        }
        
        color = null;
        
        effect_name = null;
        effect_time = 60;
        effect_amplifier = 0;
        
        enchantName = null;
        enchantLevel = 1;
        amount = 1;
        
        reward = null;        
    }
    
    public static void openItemMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(reward == null){
            reward = new ItemReward(FinishMenu.getCurrentOutcome());
        }
        
        ItemStack glass = XMaterial.CYAN_STAINED_GLASS_PANE.parseItem();
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName("&7Back");
        back.setItemMeta(meta);
        
        ItemStack item = XMaterial.BRICKS.parseItem();
        meta = item.getItemMeta();
        meta.setDisplayName("&7Select an item from your inventory");
        item.setItemMeta(meta);
        
        ItemStack minus = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
        meta = minus.getItemMeta();
        meta.setDisplayName("&c&l-");
        minus.setItemMeta(meta);
        
        ItemStack plus = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
        meta = plus.getItemMeta();
        meta.setDisplayName("&a&l+");
        plus.setItemMeta(meta);
        
        ItemStack amount_item = XMaterial.BLACK_STAINED_GLASS_PANE.parseItem();
        amount_item.setAmount(amount);
        meta = amount_item.getItemMeta();
        meta.setDisplayName("&bAmount: &r" + amount);
        amount_item.setItemMeta(meta);
        
        ItemStack creative = XMaterial.CRAFTING_TABLE.parseItem();
        meta = creative.getItemMeta();
        meta.setDisplayName("&3Close menu to pick items from creative mode");
        creative.setItemMeta(meta);
        
        ItemStack next = new ItemStack(Material.ANVIL);
        meta = next.getItemMeta();
        meta.setDisplayName("&bNext");
        next.setItemMeta(meta);
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.ITEM_MENU, 27, "&b&lItem Reward");
        for(int i=0;i<9;i++){
            inv.setItem(i, glass);
        }
        for(int i=18;i<27;i++){
            inv.setItem(i, glass);
        }
        inv.setItem(9, glass);
        inv.setItem(17, glass);
        
        inv.setItem(11, item);
        if(reward.getItem() != null){
            reward.getItem().setAmount(amount);
            inv.setItem(14, reward.getItem());
        }else{
            inv.setItem(14, amount_item);
        }
        inv.setItem(15, plus);
        inv.setItem(13, minus);
        inv.setItem(12, creative);
        inv.setItem(10, back);
        inv.setItem(16, next);
        
        p.openInventory(inv);
        GUIManager.setCurrentInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction ITEM_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        if(e.getClickedInventory() == e.getView().getTopInventory()){
            switch(e.getSlot()){
                case 10:
                    if(FinishMenu.editMode){
                        FinishMenu.openFinishInventory(p);
                    }else{
                        RewardTypesMenu.openRewardTypesMenu(p);
                    }
                    break;
                case 15:
                    amount++;
                    if(amount == 65){
                        amount = 1;
                    }
                    openItemMenu(p);
                    break;
                case 13:
                    amount--;
                    if(amount == 0){
                        amount = 64;
                    }
                    openItemMenu(p);
                    break;
                case 16:
                    //open next inventory
                    if(reward.getItem() != null){
                        openItemMenu2(p);
                    }
                    break;
                case 12:
                    //Close menu
                    p.closeInventory();
                    p.sendMessage("&6Use &b/alb return &6to return to the menu");
                    break;
            }
        }else{
            if(e.getClickedInventory() == e.getView().getBottomInventory() && e.getClickedInventory().getType() == InventoryType.PLAYER){
                if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
                    reward.setItem(e.getCurrentItem().clone());
                    amount = reward.getItem().getAmount();
                    openItemMenu(p);
                }
            }
        }
//</editor-fold>
    };
    
    private static void openItemMenu2(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ChatListener.removePlayer(p);
        
        ItemStack glass = XMaterial.CYAN_STAINED_GLASS_PANE.parseItem();
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName("&7Back");
        back.setItemMeta(meta);
        
        ItemStack name = new ItemStack(Material.NAME_TAG);
        meta = name.getItemMeta();
        meta.setDisplayName("&aSet custom name");
        name.setItemMeta(meta);
        
        ItemStack lore = XMaterial.OAK_SIGN.parseItem();
        meta = lore.getItemMeta();
        meta.setDisplayName("&6Add lore line");
        lore.setItemMeta(meta);
        
        ItemStack durability = new ItemStack(Material.IRON_PICKAXE);
        meta = durability.getItemMeta();
        meta.setDisplayName("&3Set durability");
        durability.setItemMeta(meta);
        
        ItemStack plus = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
        meta = plus.getItemMeta();
        meta.setDisplayName("&a&l+");
        plus.setItemMeta(meta);
        
        ItemStack less = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
        meta = less.getItemMeta();
        meta.setDisplayName("&c&l-");
        less.setItemMeta(meta);
        
        ItemStack enchantments = XMaterial.ENCHANTING_TABLE.parseItem();
        meta = enchantments.getItemMeta();
        meta.setDisplayName("&dAdd enchantment");
        enchantments.setItemMeta(meta);
        
        ItemStack next = new ItemStack(Material.ANVIL);
        meta = next.getItemMeta();
        meta.setDisplayName("&bNext");
        next.setItemMeta(meta);
        
        //Reset items
        
        ItemStack resetName = new ItemStack(Material.BARRIER);
        meta = resetName.getItemMeta();
        meta.setDisplayName("&cReset custom name");
        resetName.setItemMeta(meta);
        
        ItemStack resetLore = new ItemStack(Material.BARRIER);
        meta = resetLore.getItemMeta();
        meta.setDisplayName("&cReset lore");
        resetLore.setItemMeta(meta);
        
        ItemStack resetDurability = new ItemStack(Material.BARRIER);
        meta = resetDurability.getItemMeta();
        meta.setDisplayName("&cReset durability");
        resetDurability.setItemMeta(meta);
        
        ItemStack resetEnchantments = new ItemStack(Material.BARRIER);
        meta = resetEnchantments.getItemMeta();
        meta.setDisplayName("&cReset enchantments");
        resetEnchantments.setItemMeta(meta);
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.ITEM_MENU_2, 36, "&b&lItem Reward 2");
        for(int i=0;i<9;i++){
            inv.setItem(i, glass);
        }
        for(int i=27;i<36;i++){
            inv.setItem(i, glass);
        }
        
        ItemStack specialItem = null;
        ItemStack removeSpecialData = null;
        ItemStack changePotionType = null;
        ItemStack setPotionColor = null;
        if(TextureManager.isSkull(reward.getItem().getType())){
            //TextureMeta
            specialItem = new ItemStack(TextureManager.getItemSkullMaterial());
            meta = specialItem.getItemMeta();
            meta.setDisplayName("&5Click to set custom texture");
            meta.setLore(Arrays.asList("&3You can write 'null'", "&3  if you want to remove the texture"));
            specialItem.setItemMeta(meta);
            
            removeSpecialData = new ItemStack(Material.BARRIER);
            meta = removeSpecialData.getItemMeta();
            meta.setDisplayName("&cClick to remove custom texture");
            removeSpecialData.setItemMeta(meta);
        }else switch(reward.getItem().getType().name()){
            case "POTION":
            case "SPLASH_POTION":
            case "LINGERING_POTION":
                //PotionMeta
                specialItem = new ItemStack(Material.POTION);
                meta = specialItem.getItemMeta();
                meta.setDisplayName("&5Click to add custom potion effects");
                specialItem.setItemMeta(meta);
                
                removeSpecialData = new ItemStack(Material.BARRIER);
                meta = removeSpecialData.getItemMeta();
                meta.setDisplayName("&cClick to remove all potion effects");
                removeSpecialData.setItemMeta(meta);
                
                changePotionType = XMaterial.BREWING_STAND.parseItem();
                meta = changePotionType.getItemMeta();
                meta.setDisplayName("&bClick to change potion type");
                meta.setLore(Arrays.asList("&dCurrent type: &e" + PotionSplashType.getFromPotion(reward.getItem())));
                changePotionType.setItemMeta(meta);
                
                if(NewAmazingLuckyBlocks.getMinecraftVersion().compareTo(MinecraftVersion.v1_11) >= 0){
                    setPotionColor = new ItemStack(Material.BLAZE_POWDER);
                    meta = setPotionColor.getItemMeta();
                    meta.setDisplayName("&dClick to set the potion color");
                    setPotionColor.setItemMeta(meta);
                }
                
                break;
            case "LEATHER_HELMET":
            case "LEATHER_CHESTPLATE":
            case "LEATHER_LEGGINGS":
            case "LEATHER_BOOTS":
                //LeatherArmorMeta
                specialItem = new ItemStack(Material.LEATHER);
                meta = specialItem.getItemMeta();
                meta.setDisplayName("&5Click to set custom armor color");
                specialItem.setItemMeta(meta);
                
                removeSpecialData = new ItemStack(Material.BARRIER);
                meta = removeSpecialData.getItemMeta();
                meta.setDisplayName("&cClick to reset armor color");
                removeSpecialData.setItemMeta(meta);
                break;
        }
        
        inv.setItem(9, back);
        inv.setItem(11, reward.getItem());
        inv.setItem(19, removeSpecialData);
        inv.setItem(20, changePotionType);
        inv.setItem(21, specialItem);
        inv.setItem(13, name);
        inv.setItem(14, lore);
        inv.setItem(15, durability);
        inv.setItem(16, enchantments);
        inv.setItem(17, next);
        
        inv.setItem(6, plus);
        inv.setItem(24, less);
        
        inv.setItem(31, resetName);
        inv.setItem(32, resetLore);
        inv.setItem(33, resetDurability);
        inv.setItem(34, resetEnchantments);
        
        if(setPotionColor != null){
            inv.setItem(29, setPotionColor);
        }
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction ITEM_MENU_2_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        switch(e.getSlot()){
            case 9:
                openItemMenu(p);
                break;
            case 13:
                //Set custom name
                ChatListener.registerPlayer(p, message -> {
                    ItemMeta meta = reward.getItem().getItemMeta();
                    meta.setDisplayName(message);
                    reward.getItem().setItemMeta(meta);
                    openItemMenu2(p);
                });
                p.closeInventory();
                break;
            case 14:
                //Add lore line
                ChatListener.registerPlayer(p, message -> {
                    ItemMeta meta = reward.getItem().getItemMeta();
                
                    List<String> lore;
                    if(meta.getLore() == null){
                        lore = new ArrayList<>();
                    }else{
                        lore = meta.getLore();
                    }
                    lore.add(message);

                    meta.setLore(lore);
                    reward.getItem().setItemMeta(meta);
                    
                    openItemMenu2(p);
                });
                p.closeInventory();
                break;
            case 15:
                //Set durability
                ChatListener.registerPlayer(p, message -> {
                    try{
                        reward.getItem().setDurability(Short.parseShort(message));
                    }catch(NumberFormatException ex){
                    }
                    openItemMenu2(p);
                });
                p.closeInventory();
                break;
            case 6:
                //Increase durability
                short durability = reward.getItem().getDurability();
                durability++;
                
                reward.getItem().setDurability(durability);
                openItemMenu2(p);
                break;
            case 24:
                //Decrease durability
                durability = reward.getItem().getDurability();
                
                if(durability > 0){
                    durability--;
                    reward.getItem().setDurability(durability);
                    openItemMenu2(p);
                }
                break;
            case 16:
                //Open enchantments menu
                openEnchantmentsMenu(p);
                break;
            case 17:
                //Finish Menu
                FinishMenu.addReward(reward);
                reset();
                FinishMenu.openFinishInventory(p);
                break;
            case 31:
                //Reset custom name
                ItemMeta meta = reward.getItem().getItemMeta();
                meta.setDisplayName(null);
                reward.getItem().setItemMeta(meta);
                openItemMenu2(p);
                break;
            case 32:
                //Reset custom lore
                meta = reward.getItem().getItemMeta();
                meta.setLore(null);
                reward.getItem().setItemMeta(meta);
                openItemMenu2(p);
                break;
            case 33:
                //Reset durability
                reward.getItem().setDurability((short) 0);
                openItemMenu2(p);
                break;
            case 34:
                //Reset enchantments
                reward.getItem().getEnchantments().keySet().forEach((ench) -> {
                    reward.getItem().removeEnchantment(ench);
                });
                openItemMenu2(p);
                break;
            case 29:
                //Open select potion color menu
                if(e.getCurrentItem() != null){
                    if(e.getCurrentItem().getType() == Material.BLAZE_POWDER){
                        if(NewAmazingLuckyBlocks.getMinecraftVersion().compareTo(MinecraftVersion.v1_11) >= 0){
                            PotionMeta potionMeta = (PotionMeta) reward.getItem().getItemMeta();
                            if(potionMeta.hasColor()){
                                color = FireworkMenu.getHexFromDecimal(potionMeta.getColor().asRGB());
                            }                            
                            openColorInventory(p);
                        }
                    }
                }
                break;
            case 21:
                //Special item menu
                if(e.getCurrentItem() != null){
                    if(TextureManager.isSkull(e.getCurrentItem().getType())){
                        //Close menu
                        ChatListener.registerPlayer(p, message -> {
                            try{
                                if(message.equals("null")){
                                    TextureManager.setTexture(reward.getItem(), null);
                                }else{
                                    Texture texture = new Texture(message);
                                    TextureManager.setTexture(reward.getItem(), texture);
                                }
                                
                                openItemMenu2(p);
                            }catch(InvalidHeadException ex){
                                Logger.sendMessage("&cInvalid texture", p);
                                Logger.sendMessage("&bUse &7/alb return &bif you don't know any valid texture", p);
                            }
                        }, false);
                        p.closeInventory();
                        Logger.sendMessage("&3Enter the texture ID and press ENTER", p);
                    }else switch(e.getCurrentItem().getType()){
                        case POTION:
                            //Open potion meta menu
                            openPotionEffectsInventory(p);
                            break;
                        case LEATHER:
                            //Open armor color menu
                            LeatherArmorMeta lam = (LeatherArmorMeta) reward.getItem().getItemMeta();
                            color = FireworkMenu.getHexFromDecimal(lam.getColor().asRGB());
                            openColorInventory(p);
                            break;
                    }
                }
                break;
            case 20:
                //Change potion type
                if(e.getCurrentItem() != null){
                    if(e.getCurrentItem().getType().name().equals("BREWING_STAND_ITEM")
                            || e.getCurrentItem().getType().name().equals("BREWING_STAND")){
                        PotionSplashType type = PotionSplashType.getFromPotion(reward.getItem());
                        type.getNextPotionSplashType().setToPotion(reward.getItem());
                        openItemMenu2(p);
                    }
                }
                break;
            case 19:
                //Delete special item meta
                if(e.getCurrentItem() != null){
                    if(TextureManager.isSkull(reward.getItem().getType())){
                        TextureManager.setTexture(reward.getItem(), null);
                        openItemMenu2(p);
                    }else switch(reward.getItem().getType().name()){
                        case "POTION":
                        case "SPLASH_POTION":
                        case "LINGERING_POTION":
                            //Remove all effects
                            PotionMeta potionMeta = (PotionMeta) reward.getItem().getItemMeta();
                            potionMeta.clearCustomEffects();
                            reward.getItem().setItemMeta(potionMeta);
                            
                            if(NewAmazingLuckyBlocks.getMinecraftVersion() == MinecraftVersion.v1_8){
                                Potion potion = Potion.fromItemStack(reward.getItem());
                                potion.setType(PotionType.WATER);
                                potion.apply(reward.getItem());
                            }else{
                                potionMeta.setBasePotionData(new PotionData(PotionType.WATER));
                                reward.getItem().setItemMeta(potionMeta);
                            }
                            
                            
                            //Reset potion type to normal
                            PotionSplashType.clearPotionSplashType(reward.getItem());
                            openItemMenu2(p);
                            break;
                        case "LEATHER_HELMET":
                        case "LEATHER_CHESTPLATE":
                        case "LEATHER_LEGGINGS":
                        case "LEATHER_BOOTS":
                            LeatherArmorMeta lam = (LeatherArmorMeta) reward.getItem().getItemMeta();
                            lam.setColor(null);
                            reward.getItem().setItemMeta(lam);
                            openItemMenu2(p);
                            break;
                    }
                }
                break;
        }
//</editor-fold>
    };
        
    private static void openEnchantmentsMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemStack glass = XMaterial.PURPLE_STAINED_GLASS_PANE.parseItem();
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName("&7Back");
        back.setItemMeta(meta);
        
        ItemStack enchantment = new ItemStack(Material.ENCHANTED_BOOK);
        meta = enchantment.getItemMeta();
        if(enchantName == null){
            meta.setDisplayName("&5Select enchantment");
        }else{
            meta.setDisplayName("&5Selected enchantment: &b" + enchantName);
            if(enchantLevel > 0){
                meta.addEnchant(Enchantment.getByName(enchantName), enchantLevel, true);
            }else{
                meta.addEnchant(Enchantment.getByName(enchantName), 1, true);
            }
        }
        enchantment.setItemMeta(meta);
        
        ItemStack level = XMaterial.EXPERIENCE_BOTTLE.parseItem();
        meta = level.getItemMeta();
        meta.setDisplayName("&aSelected level: &b" + enchantLevel);
        meta.setLore(Arrays.asList("&3Click to select"));
        level.setAmount(enchantLevel);
        level.setItemMeta(meta);
        
        ItemStack plus = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
        meta = plus.getItemMeta();
        meta.setDisplayName("&a&l+");
        plus.setItemMeta(meta);
        
        ItemStack less = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
        meta = less.getItemMeta();
        meta.setDisplayName("&c&l-");
        less.setItemMeta(meta);
        
        ItemStack next = new ItemStack(Material.ANVIL);
        meta = next.getItemMeta();
        meta.setDisplayName("&bNext");
        next.setItemMeta(meta);
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.ENCHATMENTS_MENU, 27, "&d&lEnchantments Menu");
        for(int i=0;i<9;i++){
            inv.setItem(i, glass);
        }
        for(int i=18;i<27;i++){
            inv.setItem(i, glass);
        }
        inv.setItem(9, glass);
        inv.setItem(17, glass);
        
        inv.setItem(10, back);
        inv.setItem(12, enchantment);
        inv.setItem(14, level);
        
        inv.setItem(5, plus);
        inv.setItem(23, less);
        
        inv.setItem(16, next);
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction ENCHANTMENTS_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        switch(e.getSlot()){
            case 10:
                openItemMenu2(p);
                break;
            case 12:
                //Open select enchant name menu
                openSelectEnchantNameMenu(p);
                break;
            case 14:
                //Set level
                ChatListener.registerPlayer(p, message -> {
                    try{
                        enchantLevel = Integer.parseInt(message);
                    }catch(NumberFormatException ex){
                    }
                    openEnchantmentsMenu(p);
                });
                p.closeInventory();
                break;
            case 5:
                //Increase level
                enchantLevel++;
                openEnchantmentsMenu(p);
                break;
            case 23:
                //Decrease level
                if(enchantLevel > 1){
                    enchantLevel--;
                    openEnchantmentsMenu(p);
                }
                break;
            case 16:
                if(enchantName != null && enchantLevel > 0){
                    ItemMeta meta = reward.getItem().getItemMeta();
                    meta.addEnchant(Enchantment.getByName(enchantName), enchantLevel, true);
                    reward.getItem().setItemMeta(meta);
                    enchantName = null;
                    enchantLevel = 0;
                    openItemMenu2(p);
                }
                break;
        }
//</editor-fold>
    };
    
    private static void openSelectEnchantNameMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.ENCHANTMENT_NAME_MENU, 54, "&5&lSelect enchant name");
        
        List<String> enchantments = new ArrayList();
        
        for(Enchantment ench : Enchantment.values()){
            enchantments.add(ench.getName());
        }
        enchantments.sort(null);
        int i = 0;
        for(String ench : enchantments){
            ItemStack sk = new ItemStack(Material.ENCHANTED_BOOK);
            ItemMeta meta = sk.getItemMeta();
            meta.setDisplayName("&d" + enchantments.get(i));
            meta.addEnchant(Enchantment.getByName(ench), 1, true);
            sk.setItemMeta(meta);
            
            inv.setItem(i, sk);
            i++;
        }
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = back.getItemMeta();
        meta.setDisplayName("&7Back");
        back.setItemMeta(meta);
        
        inv.setItem(53, back);
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction ENCHANTMENT_NAME_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        if(e.getCurrentItem().getType() != Material.AIR){
            if(e.getSlot() == 53){
                openEnchantmentsMenu(p);
            }else{
                enchantName = Logger.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                openEnchantmentsMenu(p);
            }
        }
//</editor-fold>
    };
    
    
    private static void openColorInventory(Player p){        
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.ITEM_COLOR_MENU, 54, "&6&lColor menu");
        
        ItemStack glass = XMaterial.CYAN_STAINED_GLASS_PANE.parseItem();
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        
        for(int i=0;i<9;i++){
            inv.setItem(i, glass);
        }for(int i=45;i<54;i++){
            inv.setItem(i, glass);
        }for(int i=9;i<45;i+=9){
            inv.setItem(i, glass);
        }for(int i=17;i<54;i+=9){
            inv.setItem(i, glass);
        }
        
        for(int i=0;i<=15;i++){
            ItemStack sk = FireworkMenu.getColorItemStackFromDurability(i);
            
            if(i<4){
                inv.setItem(i+10, sk);
            }else if(i<8){
                inv.setItem(i+15, sk);
            }else if(i<12){
                inv.setItem(i+20, sk);
            }else{
                inv.setItem(i+25, sk);
            }
        }
        
        ItemStack coloredItem = reward.getItem().clone();
        meta = coloredItem.getItemMeta();
        if(meta instanceof LeatherArmorMeta){
            LeatherArmorMeta lam = (LeatherArmorMeta) meta;
            if(color == null || color.equals("ERROR")){
                lam.setDisplayName("&dChosen color: &b" + "null");
            }else{
                lam.setDisplayName("&dChosen color: &b" + color);
                lam.setColor(Color.fromRGB(FireworkMenu.getDecimalFromHex(color)));
            }
        }else if(meta instanceof PotionMeta){
            PotionMeta potionMeta = (PotionMeta) meta;
            if(color == null || color.equals("ERROR")){
                potionMeta.setDisplayName("&dChosen color: &b" + "null");
            }else{
                potionMeta.setDisplayName("&dChosen color: &b" + color);
                potionMeta.setColor(Color.fromRGB(FireworkMenu.getDecimalFromHex(color)));
            }
        }            
        coloredItem.setItemMeta(meta);
        
        ItemStack chooseCustomColor = XMaterial.OAK_SIGN.parseItem();
        meta = chooseCustomColor.getItemMeta();
        if(color != null && color.equals("ERROR")){
            meta.setDisplayName("&cYou must enter a valid hex color");
        }else{
            meta.setDisplayName("&3Choose custom color");
        }
        chooseCustomColor.setItemMeta(meta);
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName("&bBack");
        back.setItemMeta(meta);
        
        ItemStack next = new ItemStack(Material.ANVIL);
        meta = next.getItemMeta();
        meta.setDisplayName("&bNext");
        next.setItemMeta(meta);
        
        inv.setItem(16, coloredItem);
        inv.setItem(15, chooseCustomColor);
        inv.setItem(42, back);
        inv.setItem(43, next);
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction ITEM_COLOR_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            
            switch(e.getSlot()){
                case 15:
                    ChatListener.registerPlayer(p, message -> {
                        if(message.length() == 6){
                            try{
                                int a = FireworkMenu.getDecimalFromHex(color);
                                color = message.toUpperCase();
                            }catch(Exception ex){
                                color = "ERROR";
                            }
                        }else{
                            color = "ERROR";
                        }
                        openColorInventory(p);
                    });
                    p.closeInventory();
                    break;
                case 42:
                    //Open item menu 2 inventory
                    openItemMenu2(p);
                    break;
                case 43:
                    //Open item menu 2 inventory
                    if(color != null && !color.equals("ERROR")){
                        Color c = Color.fromRGB(FireworkMenu.getDecimalFromHex(color));
                        
                        ItemMeta meta = reward.getItem().getItemMeta();
                        if(meta instanceof LeatherArmorMeta){
                            LeatherArmorMeta lam = (LeatherArmorMeta) meta;
                            lam.setColor(c);
                        }else if(meta instanceof PotionMeta){
                            PotionMeta potionMeta = (PotionMeta) meta;
                            potionMeta.setColor(c);
                        }
                        
                        reward.getItem().setItemMeta(meta);
                        openItemMenu2(p);
                    }
                    break;
                default:
                    String aux = FireworkMenu.getHexColorFromItemStack(e.getCurrentItem());
                    if(aux != null){
                        color = aux;
                        openColorInventory(p);
                    }
            }
        }
//</editor-fold>
    };
    
    
    public static enum PotionSplashType{
        //<editor-fold defaultstate="collapsed" desc="Code">
        NORMAL,
        SPLASH,
        LINGERING;
        
        public static PotionSplashType getFromPotion(ItemStack stack){
            if(NewAmazingLuckyBlocks.getMinecraftVersion() == MinecraftVersion.v1_8){
                if(stack.getType() != Material.POTION){
                    return null;
                }
                
                Potion potion = Potion.fromItemStack(stack);
                if(potion.isSplash()){
                    return SPLASH;
                }else{
                    return NORMAL;
                }
            }else switch(stack.getType()){
                case POTION:
                    return NORMAL;
                case SPLASH_POTION:
                    return SPLASH;
                case LINGERING_POTION:
                    return LINGERING;
                default:
                    return null;
            }
        }
        
        public static void clearPotionSplashType(ItemStack stack){
            NORMAL.setToPotion(stack);
        }
        
        public void setToPotion(ItemStack stack){
            if(NewAmazingLuckyBlocks.getMinecraftVersion() == MinecraftVersion.v1_8){
                Potion potion = Potion.fromItemStack(stack);
                potion.setSplash(this != NORMAL);
                potion.apply(stack);
            }else{
                switch(this){
                    case NORMAL:
                        stack.setType(Material.POTION);
                        break;
                    case SPLASH:
                        stack.setType(Material.SPLASH_POTION);
                        break;
                    case LINGERING:
                        stack.setType(Material.LINGERING_POTION);
                        break;
                }
            }
        }
        
        public PotionSplashType getNextPotionSplashType(){
            if(NewAmazingLuckyBlocks.getMinecraftVersion() == MinecraftVersion.v1_8){
                return this == NORMAL ? SPLASH : NORMAL;
            }else{
                switch(this){
                    case NORMAL:
                        return SPLASH;
                    case SPLASH:
                        return LINGERING;
                    default:
                        return NORMAL;
                }
            }
        }
        
        @Override
        public String toString(){
            return name().toLowerCase();
        }
//</editor-fold>
    }
    
    //PotionEffects inventory
    private static void openPotionEffectsInventory(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.POTION_EFFECTS_MENU, 54, "&d&lEffect List");
        
        List<String> effectTypeNames = new ArrayList<>();
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
            meta.setDisplayName("&d" + typeName);
            PotionMeta pm = (PotionMeta) meta;
            pm.addCustomEffect(new PotionEffect(PotionEffectType.getByName(typeName), 0, 0), true);
            sk.setItemMeta(pm);
            
            inv.setItem(i, sk);
        }
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = back.getItemMeta();
        meta.setDisplayName("&7Back");
        back.setItemMeta(meta);
        
        inv.setItem(45, back);
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction POTION_EFFECTS_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){

            if(e.getSlot() == 45){
                openItemMenu2(p);
                return;
            }
            if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
                effect_name = Logger.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                openPotionEffects2Inventory(p);
            }
        }
//</editor-fold>
    };
    
    //PotionEffects2 inventory
    private static void openPotionEffects2Inventory(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.POTION_EFFECTS_MENU_2, 45, "&5&lEffect Config");
        
        ItemStack glass = XMaterial.MAGENTA_STAINED_GLASS_PANE.parseItem();
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        
        ItemStack time = XMaterial.CLOCK.parseItem();
        meta = time.getItemMeta();
        if(effect_time < 0){
            meta.setDisplayName("&6Effect time (seconds): &ainfinite");
        }else{
            meta.setDisplayName("&6Effect time (seconds): &a" + effect_time);
        }
        meta.setLore(Arrays.asList("&3Click to reset"));
        time.setItemMeta(meta);
        
        ItemStack amplifier = new ItemStack(Material.BEACON);
        meta = amplifier.getItemMeta();
        meta.setDisplayName("&6Effect amplifier: &a" + effect_amplifier);
        meta.setLore(Arrays.asList("&3Click to reset"));
        amplifier.setItemMeta(meta);
        
        
        ItemStack minus1 = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
        meta = minus1.getItemMeta();
        meta.setDisplayName("&c&l-1");
        minus1.setItemMeta(meta);
        
        ItemStack minus10 = minus1.clone();
        meta = minus10.getItemMeta();
        meta.setDisplayName("&c&l-10");
        minus10.setItemMeta(meta);
        
        ItemStack minus100 = minus1.clone();
        meta = minus100.getItemMeta();
        meta.setDisplayName("&c&l-100");
        minus100.setItemMeta(meta);
        
        ItemStack plus1 = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
        meta = minus1.getItemMeta();
        meta.setDisplayName("&a&l+1");
        plus1.setItemMeta(meta);
        
        ItemStack plus10 = plus1.clone();
        meta = plus10.getItemMeta();
        meta.setDisplayName("&a&l+10");
        plus10.setItemMeta(meta);
        
        ItemStack plus100 = plus1.clone();
        meta = plus100.getItemMeta();
        meta.setDisplayName("&a&l+100");
        plus100.setItemMeta(meta);
        
        
        ItemStack effectStack = new ItemStack(Material.POTION);
        meta = effectStack.getItemMeta();
        meta.setDisplayName("&bSelected effect: &d" + effect_name);
        PotionMeta pm = (PotionMeta) meta;
        pm.addCustomEffect(new PotionEffect(PotionEffectType.getByName(effect_name), 0, 0), true);
        effectStack.setItemMeta(pm);
        
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName("&7Back");
        back.setItemMeta(meta);
        
        ItemStack next = new ItemStack(Material.ANVIL);
        meta = next.getItemMeta();
        meta.setDisplayName("&bNext");
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
    
    private static final InventoryFunction POTION_EFFECTS_MENU_2_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){

            switch(e.getSlot()){
                case 10:
                    //Return to the previous menu
                    openPotionEffectsInventory(p);
                    break;
                case 16:
                    //Open item menu 2
                    if(effect_name != null){
                        PotionMeta meta = (PotionMeta) reward.getItem().getItemMeta();
                        meta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(effect_name), effect_time*20, effect_amplifier), true);
                        reward.getItem().setItemMeta(meta);
                        effect_name = null;
                        effect_time = 60;
                        effect_amplifier = 0;
                        openItemMenu2(p);
                    }
                    break;
                //<editor-fold defaultstate="collapsed" desc="EffectTime">
                case 19:
                    //Effect time -100
                    effect_time -= 100;
                    if(effect_time < -1){
                        effect_time = -1;
                    }
                    openPotionEffects2Inventory(p);
                    break;
                case 20:
                    //Effect time -10
                    effect_time -= 10;
                    if(effect_time < -1){
                        effect_time = -1;
                    }
                    openPotionEffects2Inventory(p);
                    break;
                case 21:
                    //Effect time -1
                    effect_time--;
                    if(effect_time < -1){
                        effect_time = -1;
                    }
                    openPotionEffects2Inventory(p);
                    break;
                case 22:
                    //Effect time = 60
                    effect_time = 60;
                    openPotionEffects2Inventory(p);
                    break;
                case 23:
                    //Effect time +1
                    effect_time ++;
                    openPotionEffects2Inventory(p);
                    break;
                case 24:
                    //Effect time +10
                    effect_time += 10;
                    openPotionEffects2Inventory(p);
                    break;
                case 25:
                    //Effect time +100
                    effect_time += 100;
                    openPotionEffects2Inventory(p);
                    break;
//</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="EffectAmplifier">
                case 28:
                    //Effect amplifier -100
                    effect_amplifier -= 100;
                    if(effect_amplifier < 0){
                        effect_amplifier = 0;
                    }
                    openPotionEffects2Inventory(p);
                    break;
                case 29:
                    //Effect amplifier -10
                    effect_amplifier -= 10;
                    if(effect_amplifier < 0){
                        effect_amplifier = 0;
                    }
                    openPotionEffects2Inventory(p);
                    break;
                case 30:
                    //Effect amplifier -1
                    effect_amplifier--;
                    if(effect_amplifier < 0){
                        effect_amplifier = 0;
                    }
                    openPotionEffects2Inventory(p);
                    break;
                case 31:
                    //Effect amplifier = 0
                    effect_amplifier = 0;
                    openPotionEffects2Inventory(p);
                    break;
                case 32:
                    //Effect amplifier +1
                    effect_amplifier ++;
                    if(effect_amplifier > 255){
                        effect_amplifier = 255;
                    }
                    openPotionEffects2Inventory(p);
                    break;
                case 33:
                    //Effect amplifier +10
                    effect_amplifier += 10;
                    if(effect_amplifier > 255){
                        effect_amplifier = 255;
                    }
                    openPotionEffects2Inventory(p);
                    break;
                case 34:
                    //Effect amplifier +100
                    effect_amplifier += 100;
                    if(effect_amplifier > 255){
                        effect_amplifier = 255;
                    }
                    openPotionEffects2Inventory(p);
                    break;
//</editor-fold>

            }
        }
//</editor-fold>
    };    
}
