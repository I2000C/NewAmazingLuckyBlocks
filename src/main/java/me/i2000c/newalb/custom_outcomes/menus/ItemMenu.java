package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.utils.rewards.ItemReward;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils.textures.InvalidTextureException;
import me.i2000c.newalb.utils.textures.Texture;
import me.i2000c.newalb.utils.textures.TextureManager;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.ITEM_MENU, 27, "&b&lItem Reward");
        
        if(reward == null){
            reward = new ItemReward(FinishMenu.getCurrentOutcome());
        }
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.CYAN);
        
        ItemStack item = ItemBuilder.newItem(XMaterial.BRICKS)
                .withDisplayName("&7Select an item from your inventory")
                .build();
        
        ItemStack amount_item = ItemBuilder.newItem(XMaterial.BLACK_STAINED_GLASS_PANE)
                .withAmount(amount)
                .withDisplayName("&bAmount: &r" + amount)
                .build();
        
        ItemStack creative = ItemBuilder.newItem(XMaterial.CRAFTING_TABLE)
                .withDisplayName("&3Close menu to pick items from creative mode")
                .build();
        
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
        inv.setItem(15, GUIItem.getPlusLessItem(+1));
        inv.setItem(13, GUIItem.getPlusLessItem(-1));
        inv.setItem(12, creative);
        inv.setItem(10, GUIItem.getBackItem());
        inv.setItem(16, GUIItem.getNextItem());
        
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
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.ITEM_MENU_2, 36, "&b&lItem Reward 2");
                
        ItemStack glass = GUIItem.getGlassItem(GlassColor.CYAN);
        
        ItemStack name = ItemBuilder.newItem(XMaterial.NAME_TAG)
                .withDisplayName("&aSet custom name")
                .build();
        
        ItemStack lore = ItemBuilder.newItem(XMaterial.OAK_SIGN)
                .withDisplayName("&6Add lore line")
                .build();
        
        ItemStack durability = ItemBuilder.newItem(XMaterial.IRON_PICKAXE)
                .withDisplayName("&3Set durability")
                .build();
        
        ItemStack enchantments = ItemBuilder.newItem(XMaterial.ENCHANTING_TABLE)
                .withDisplayName("&dAdd enchantment")
                .build();
        
        //Reset items
        
        ItemStack resetName = ItemBuilder.newItem(XMaterial.BARRIER)
                .withDisplayName("&cReset custom name")
                .build();
        
        ItemStack resetLore = ItemBuilder.newItem(XMaterial.BARRIER)
                .withDisplayName("&cReset lore")
                .build();
        
        ItemStack resetDurability = ItemBuilder.newItem(XMaterial.BARRIER)
                .withDisplayName("&cReset durability")
                .build();
        
        ItemStack resetEnchantments = ItemBuilder.newItem(XMaterial.BARRIER)
                .withDisplayName("&cReset enchantments")
                .build();
        
        
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
            specialItem = ItemBuilder.newItem(XMaterial.PLAYER_HEAD)
                    .withDisplayName("&5Click to set custom texture")
                    .addLoreLine("&3You can write 'null'")
                    .addLoreLine("&3  if you want to remove the texture")
                    .build();
            
            removeSpecialData = ItemBuilder.newItem(XMaterial.BARRIER)
                    .withDisplayName("&cClick to remove custom texture")
                    .build();
        }else switch(XMaterial.matchXMaterial(reward.getItem().getType())){
            case POTION:
            case SPLASH_POTION:
            case LINGERING_POTION:
                //PotionMeta
                specialItem = ItemBuilder.newItem(XMaterial.POTION)
                        .withDisplayName("&5Click to add custom potion effects")
                        .build();
                
                removeSpecialData = ItemBuilder.newItem(XMaterial.BARRIER)
                        .withDisplayName("&cClick to remove all potion effects")
                        .build();
                
                changePotionType = ItemBuilder.newItem(XMaterial.BREWING_STAND)
                        .withDisplayName("&bClick to change potion type")
                        .addLoreLine("&dCurrent type: &e" + PotionSplashType.getFromPotion(reward.getItem()))
                        .build();
                
                if(NewAmazingLuckyBlocks.getMinecraftVersion().compareTo(MinecraftVersion.v1_11) >= 0){
                    setPotionColor = ItemBuilder.newItem(XMaterial.BLAZE_POWDER)
                            .withDisplayName("&dClick to set potion color")
                            .build();
                }
                
                break;
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                //LeatherArmorMeta
                specialItem = ItemBuilder.newItem(XMaterial.LEATHER)
                        .withDisplayName("&5Click to set custom armor color")
                        .build();
                
                removeSpecialData = ItemBuilder.newItem(XMaterial.BARRIER)
                        .withDisplayName("&cClick to reset armor color")
                        .build();
                break;
        }
        
        inv.setItem(9, GUIItem.getBackItem());
        inv.setItem(11, reward.getItem());
        inv.setItem(19, removeSpecialData);
        inv.setItem(20, changePotionType);
        inv.setItem(21, specialItem);
        inv.setItem(13, name);
        inv.setItem(14, lore);
        inv.setItem(15, durability);
        inv.setItem(16, enchantments);
        inv.setItem(17, GUIItem.getNextItem());
        
        inv.setItem(6, GUIItem.getPlusLessItem(+1));
        inv.setItem(24, GUIItem.getPlusLessItem(-1));
        
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
                            }catch(InvalidTextureException ex){
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
                            Color itemColor = ItemBuilder.fromItem(reward.getItem(), false).getColor();
                            color = FireworkMenu.getHexFromDecimal(itemColor.asRGB());
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
                    }else switch(XMaterial.matchXMaterial(reward.getItem().getType())){
                        case POTION:
                        case SPLASH_POTION:
                        case LINGERING_POTION:
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
                        case LEATHER_HELMET:
                        case LEATHER_CHESTPLATE:
                        case LEATHER_LEGGINGS:
                        case LEATHER_BOOTS:
                            ItemBuilder.fromItem(reward.getItem(), false).withColor(null);
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
        ItemStack glass = GUIItem.getGlassItem(GlassColor.PURPLE);
        
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.ENCHANTED_BOOK);
        if(enchantName == null){
            builder.withDisplayName("&5Select enchantment");
        }else{
            builder.withDisplayName("&5Selected enchantment: &b" + enchantName);
            if(enchantLevel > 0){
                builder.addEnchantment(Enchantment.getByName(enchantName), enchantLevel);
            }else{
                builder.addEnchantment(Enchantment.getByName(enchantName), 1);
            }
        }
        ItemStack enchantment = builder.build();
        
        ItemStack level = ItemBuilder.newItem(XMaterial.EXPERIENCE_BOTTLE)
                .withAmount(enchantLevel)
                .withDisplayName("&aSelected level: &b" + enchantLevel)
                .addLoreLine("&3Click to select")
                .build();
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.ENCHATMENTS_MENU, 27, "&d&lEnchantments Menu");
        for(int i=0;i<9;i++){
            inv.setItem(i, glass);
        }
        for(int i=18;i<27;i++){
            inv.setItem(i, glass);
        }
        inv.setItem(9, glass);
        inv.setItem(17, glass);
        
        inv.setItem(10, GUIItem.getBackItem());
        inv.setItem(12, enchantment);
        inv.setItem(14, level);
        
        inv.setItem(5, GUIItem.getPlusLessItem(+1));
        inv.setItem(23, GUIItem.getPlusLessItem(-1));
        
        inv.setItem(16, GUIItem.getNextItem());
        
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
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.CYAN);
        
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
        
        ItemBuilder builder = ItemBuilder.fromItem(reward.getItem());
        if(color == null || color.equals("ERROR")){
            builder.withDisplayName("&dChosen color: &b" + "null");
        }else{
            builder.withDisplayName("&dChosen color: &b" + color);
            builder.withColor(Color.fromRGB(FireworkMenu.getDecimalFromHex(color)));
        }
        ItemStack coloredItem = builder.build();
        
        builder = ItemBuilder.newItem(XMaterial.OAK_SIGN);
        if(color != null && color.equals("ERROR")){
            builder.withDisplayName("&cYou must enter a valid hex color");
        }else{
            builder.withDisplayName("&3Choose custom color");
        }
        ItemStack chooseCustomColor = builder.build();
                
        inv.setItem(16, coloredItem);
        inv.setItem(15, chooseCustomColor);
        inv.setItem(42, GUIItem.getBackItem());
        inv.setItem(43, GUIItem.getNextItem());
        
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
                        ItemBuilder.fromItem(reward.getItem(), false).withColor(c);
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
        
        List<PotionEffectType> effectTypes = Arrays.asList(PotionEffectType.values());
        effectTypes.sort((effectType1, effectType2) -> {
            String effectTypeName1 = effectType1.getName();
            String effectTypeName2 = effectType2.getName();
            return effectTypeName1.compareTo(effectTypeName2);
        });
        
        Iterator<PotionEffectType> iterator = effectTypes.iterator();
        for(int i=1; iterator.hasNext() && i<45; i++){
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
