package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.editor.EditorType;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.EffectReward;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.ItemReward;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils.textures.Texture;
import me.i2000c.newalb.utils.textures.TextureException;
import me.i2000c.newalb.utils.textures.TextureManager;
import me.i2000c.newalb.utils2.CustomColor;
import me.i2000c.newalb.utils2.EnchantmentWithLevel;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

public class ItemMenu extends Editor<ItemReward>{
    public ItemMenu(){
        InventoryListener.registerInventory(CustomInventoryType.ITEM_MENU, ITEM_MENU_FUNCTION);
        InventoryListener.registerInventory(CustomInventoryType.ITEM_MENU_2, ITEM_MENU_2_FUNCTION);
    }
    
    private int amount;
    
    @Override
    protected void reset(){
        this.amount = 1;
    }
    
    @Override
    protected void newItem(Player player){
        Outcome outcome = RewardListMenu.getCurrentOutcome();
        item = new ItemReward(outcome);
        openItemMenu(player);
    }
    
    @Override
    protected void editItem(Player player){
        openItemMenu(player);
    }
    
    private void openItemMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.ITEM_MENU, 27, "&b&lItem Reward");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.CYAN);
        
        ItemStack select_from_inventory_item = ItemBuilder.newItem(XMaterial.BRICKS)
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
            menu.setItem(i, glass);
        }
        for(int i=18;i<27;i++){
            menu.setItem(i, glass);
        }
        menu.setItem(9, glass);
        menu.setItem(17, glass);
        
        menu.setItem(11, select_from_inventory_item);
        if(item.getItem() != null){
            item.getItem().setAmount(amount);
            menu.setItem(14, item.getItem());
        }else{
            menu.setItem(14, amount_item);
        }
        menu.setItem(15, GUIItem.getPlusLessItem(+1));
        menu.setItem(13, GUIItem.getPlusLessItem(-1));
        menu.setItem(12, creative);
        menu.setItem(10, GUIItem.getBackItem());
        menu.setItem(16, GUIItem.getNextItem());
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction ITEM_MENU_FUNCTION = e -> {
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
                    // Go to next menu
                    if(item.getItem() != null){
                        openItemMenu2(player);
                    }
                    break;
                case 15:
                    amount++;
                    if(amount > 64){
                        amount = 1;
                    }
                    openItemMenu(player);
                    break;
                case 13:
                    amount--;
                    if(amount <= 0){
                        amount = 64;
                    }
                    openItemMenu(player);
                    break;                
                case 12:
                    //Close menu
                    player.closeInventory();
                    Logger.sendMessage("&6Use &b/alb return &6to return to the menu", player);
                    break;
            }
        }else if(e.getLocation() == InventoryLocation.BOTTOM && e.getClickedInventory().getType() == InventoryType.PLAYER){
            if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
                item.setItem(e.getCurrentItem().clone());
                amount = item.getItem().getAmount();
                openItemMenu(player);
            }
        }
//</editor-fold>
    };
    
    private void openItemMenu2(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.ITEM_MENU_2, 36, "&b&lItem Reward 2");
                
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
            menu.setItem(i, glass);
        }
        for(int i=27;i<36;i++){
            menu.setItem(i, glass);
        }
        
        ItemStack specialItem = null;
        ItemStack removeSpecialData = null;
        ItemStack changePotionType = null;
        ItemStack setPotionColor = null;
        if(TextureManager.isSkull(item.getItem().getType())){
            //TextureMeta
            specialItem = ItemBuilder.newItem(XMaterial.PLAYER_HEAD)
                    .withDisplayName("&5Click to set custom texture")
                    .addLoreLine("&3You can write 'null'")
                    .addLoreLine("&3  if you want to remove the texture")
                    .build();
            
            removeSpecialData = ItemBuilder.newItem(XMaterial.BARRIER)
                    .withDisplayName("&cClick to remove custom texture")
                    .build();
        }else switch(XMaterial.matchXMaterial(item.getItem().getType())){
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
                        .addLoreLine("&dCurrent type: &e" + PotionSplashType.getFromPotion(item.getItem()))
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
        
        menu.setItem(9, GUIItem.getBackItem());
        menu.setItem(11, item.getItem());
        menu.setItem(19, removeSpecialData);
        menu.setItem(20, changePotionType);
        menu.setItem(21, specialItem);
        menu.setItem(13, name);
        menu.setItem(14, lore);
        menu.setItem(15, durability);
        menu.setItem(16, enchantments);
        menu.setItem(17, GUIItem.getNextItem());
        
        menu.setItem(6, GUIItem.getPlusLessItem(+1));
        menu.setItem(24, GUIItem.getPlusLessItem(-1));
        
        menu.setItem(31, resetName);
        menu.setItem(32, resetLore);
        menu.setItem(33, resetDurability);
        menu.setItem(34, resetEnchantments);
        
        if(setPotionColor != null){
            menu.setItem(29, setPotionColor);
        }
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction ITEM_MENU_2_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        switch(e.getSlot()){
            case 9:
                // Go to previous menu
                openItemMenu(player);
                break;
            case 13:
                //Set custom name
                ChatListener.registerPlayer(player, message -> {
                    ItemBuilder.fromItem(item.getItem(), false)
                            .withDisplayName(message);
                    openItemMenu2(player);
                });
                player.closeInventory();
                break;
            case 14:
                //Add lore line
                ChatListener.registerPlayer(player, message -> {
                    ItemBuilder.fromItem(item.getItem(), false)
                            .addLoreLine(message);                    
                    openItemMenu2(player);
                });
                player.closeInventory();
                break;
            case 15:
                //Set durability
                ChatListener.registerPlayer(player, message -> {
                    try{
                        short durability = Short.parseShort(message);
                        if(durability < 0){
                            throw new NumberFormatException();
                        }
                        
                        item.getItem().setDurability(durability);
                    }catch(NumberFormatException ex){
                        Logger.sendMessage("&cInvalid durability value: &b" + message, player);
                        Logger.sendMessage("&bIf you want to return, use &7/alb return", player);
                    }
                    openItemMenu2(player);
                });
                player.closeInventory();
                break;
            case 6:
                //Increase durability
                short durability = item.getItem().getDurability();
                durability++;
                
                item.getItem().setDurability(durability);
                openItemMenu2(player);
                break;
            case 24:
                //Decrease durability
                durability = item.getItem().getDurability();
                
                if(durability > 0){
                    durability--;
                    item.getItem().setDurability(durability);
                    openItemMenu2(player);
                }
                break;
            case 16:
                //Open enchantments menu
                Editor<EnchantmentWithLevel> editor = EditorType.ENCHANTMENT.getEditor();
                editor.createNewItem(
                        player, 
                        p -> openItemMenu2(p), 
                        (p, enchantmentWithLevel) -> {
                            ItemBuilder.fromItem(item.getItem(), false)
                                    .addEnchantment(enchantmentWithLevel.enchantment, enchantmentWithLevel.level);
                            openItemMenu2(p);
                        });
                break;
            case 17:
                // Go to next menu
                onNext.accept(player, item);
                break;
            case 31:
                //Reset custom name
                ItemBuilder.fromItem(item.getItem(), false)
                        .withDisplayName(null);
                openItemMenu2(player);
                break;
            case 32:
                //Reset custom lore
                ItemBuilder.fromItem(item.getItem(), false)
                        .withLore();
                openItemMenu2(player);
                break;
            case 33:
                //Reset durability
                item.getItem().setDurability((short) 0);
                openItemMenu2(player);
                break;
            case 34:
                //Reset enchantments
                ItemBuilder.fromItem(item.getItem(), false)
                        .clearEnchantments();
                openItemMenu2(player);
                break;
            case 29:
                //Open select potion color menu
                if(e.getCurrentItem() != null){
                    if(e.getCurrentItem().getType() == Material.BLAZE_POWDER){
                        if(NewAmazingLuckyBlocks.getMinecraftVersion().compareTo(MinecraftVersion.v1_11) >= 0){
                            Color itemColor = ItemBuilder.fromItem(item.getItem(), false)
                                    .getColor();
                            Editor<CustomColor> editor3 = EditorType.COLOR.getEditor();
                            editor3.editExistingItem(
                                    new CustomColor(itemColor), 
                                    player, 
                                    p -> openItemMenu2(p), 
                                    (p, color) -> {
                                        ItemBuilder.fromItem(item.getItem(), false)
                                                .withColor(color.getBukkitColor());
                                        openItemMenu2(p);
                                    });
                        }
                    }
                }
                break;
            case 21:
                //Special item menu
                if(e.getCurrentItem() != null){
                    if(TextureManager.isSkull(e.getCurrentItem().getType())){
                        //Close menu
                        ChatListener.registerPlayer(player, message -> {
                            try{
                                if(message.equals("null")){
                                    TextureManager.setTexture(item.getItem(), null);
                                }else{
                                    Texture texture = new Texture(message);
                                    TextureManager.setTexture(item.getItem(), texture);
                                }
                                
                                openItemMenu2(player);
                            }catch(TextureException ex){
                                Logger.sendMessage(ex, player);
                                Logger.sendMessage("&bUse &7/alb return &bif you don't know any valid texture", player);
                            }
                        }, false);
                        player.closeInventory();
                        Logger.sendMessage("&3Enter the texture ID and press ENTER", player);
                    }else switch(e.getCurrentItem().getType()){
                        case POTION:
                            //Open potion meta menu
                            Editor<EffectReward> editor2 = EditorType.EFFECT_REWARD.getEditor();
                            EffectMenu.setShowClearEffectsItem(false);                            
                            editor2.createNewItem( 
                                    player, 
                                    p -> openItemMenu2(p), 
                                    (p, effectReward) -> {
                                        PotionEffect potionEffect = new PotionEffect(
                                                effectReward.getPotionEffect(), 
                                                effectReward.getDuration(), 
                                                effectReward.getAmplifier());
                                        ItemBuilder.fromItem(item.getItem(), false)
                                                .addPotionEffect(potionEffect);
                                        openItemMenu2(p);
                                    });
                            break;
                        case LEATHER:
                            //Open armor color menu
                            Color itemColor = ItemBuilder.fromItem(item.getItem(), false)
                                    .getColor();
                            Editor<CustomColor> editor3 = EditorType.COLOR.getEditor();
                            editor3.editExistingItem(
                                    new CustomColor(itemColor), 
                                    player, 
                                    p -> openItemMenu2(p), 
                                    (p, color) -> {
                                        ItemBuilder.fromItem(item.getItem(), false)
                                                .withColor(color.getBukkitColor());
                                        openItemMenu2(p);
                                    });
                            break;
                    }
                }
                break;
            case 20:
                //Change potion type
                if(e.getCurrentItem() != null){
                    if(e.getCurrentItem().getType().name().equals("BREWING_STAND_ITEM")
                            || e.getCurrentItem().getType().name().equals("BREWING_STAND")){
                        PotionSplashType type = PotionSplashType.getFromPotion(item.getItem());
                        type.getNextPotionSplashType().setToPotion(item.getItem());
                        openItemMenu2(player);
                    }
                }
                break;
            case 19:
                //Delete special item meta
                if(e.getCurrentItem() != null){
                    if(TextureManager.isSkull(item.getItem().getType())){
                        TextureManager.setTexture(item.getItem(), null);
                        openItemMenu2(player);
                    }else switch(XMaterial.matchXMaterial(item.getItem().getType())){
                        case POTION:
                        case SPLASH_POTION:
                        case LINGERING_POTION:
                            //Remove all effects
                            ItemBuilder.fromItem(item.getItem(), false)
                                    .clearPotionEffects();
                            
                            if(NewAmazingLuckyBlocks.getMinecraftVersion() == MinecraftVersion.v1_8){
                                Potion potion = Potion.fromItemStack(item.getItem());
                                potion.setType(PotionType.WATER);
                                potion.apply(item.getItem());
                            }else{
                                PotionMeta potionMeta = (PotionMeta) item.getItem().getItemMeta();
                                potionMeta.setBasePotionData(new PotionData(PotionType.WATER));
                                item.getItem().setItemMeta(potionMeta);
                            }
                                                        
                            //Reset potion type to normal
                            PotionSplashType.clearPotionSplashType(item.getItem());
                            openItemMenu2(player);
                            break;
                        case LEATHER_HELMET:
                        case LEATHER_CHESTPLATE:
                        case LEATHER_LEGGINGS:
                        case LEATHER_BOOTS:
                            ItemBuilder.fromItem(item.getItem(), false).withColor(null);
                            openItemMenu2(player);
                            break;
                    }
                }
                break;
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
}
