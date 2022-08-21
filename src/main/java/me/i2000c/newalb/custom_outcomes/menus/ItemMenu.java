package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.editor.EditorType;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.EffectReward;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.ItemReward;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.ItemReward.PotionSplashType;
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
    
    private static final int BACK_SLOT = 9;
    private static final int NEXT_SLOT = 17;
    private static final int ITEM_SLOT = 11;
    private static final int NAME_SLOT = 14;
    private static final int LORE_SLOT = 15;
    private static final int ENCHANTMENTS_SLOT = 16;
    
    private static final int RESET_NAME_SLOT = 23;
    private static final int RESET_LORE_SLOT = 24;
    private static final int RESET_ENCHANTMENTS_SLOT = 25;
    
    private static final int DURABILITY_SLOT = 31;
    
    private static final int REMOVE_SPECIAL_DATA_SLOT = 19;
    private static final int POTION_TYPE_SLOT = 20;
    private static final int SPECIAL_ITEM_SLOT = 21;
    private static final int POTION_COLOR_SLOT = 18;
    
    private static final int SPAWN_MODE_SLOT = 44;
    private static final int SPAWN_INV_SLOT_SLOT = 40;
    
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
        amount = item.getItem().getAmount();
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
        Menu menu = GUIFactory.newMenu(CustomInventoryType.ITEM_MENU_2, 54, "&b&lItem Reward 2");
                
        ItemStack glass = GUIItem.getGlassItem(GlassColor.CYAN);
        for(int i=0;i<9;i++){
            menu.setItem(i, glass);
        }
        for(int i=45;i<54;i++){
            menu.setItem(i, glass);
        }
        
        ItemStack name = ItemBuilder.newItem(XMaterial.NAME_TAG)
                .withDisplayName("&aClick to set custom name")
                .build();
        
        ItemStack lore = ItemBuilder.newItem(XMaterial.OAK_SIGN)
                .withDisplayName("&6Click to add lore line")
                .build();
        
        ItemStack durability = ItemBuilder.newItem(XMaterial.IRON_PICKAXE)
                .withDisplayName("&7Current durability: &a" + item.getItem().getDurability())
                .addLoreLine("&3Click to reset")
                .build();
        
        ItemStack enchantments = ItemBuilder.newItem(XMaterial.ENCHANTING_TABLE)
                .withDisplayName("&dClick to add enchantment")
                .build();
        
        //Reset items        
        ItemStack resetName = ItemBuilder.newItem(XMaterial.BARRIER)
                .withDisplayName("&cClick to reset custom name")
                .build();
        
        ItemStack resetLore = ItemBuilder.newItem(XMaterial.BARRIER)
                .withDisplayName("&cClick to reset lore")
                .build();
        
        ItemStack resetEnchantments = ItemBuilder.newItem(XMaterial.BARRIER)
                .withDisplayName("&cClick to reset enchantments")
                .build();
        
        //Special items
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
        
        //Spawn mode items
        ItemBuilder builder;
        switch(item.getSpawnMode()){
            case DEFAULT:
                builder = ItemBuilder.newItem(XMaterial.GRASS_BLOCK);
                builder.addLoreLine("&dIn this mode, the item will spawn");
                builder.addLoreLine("&d  on the ground");
                break;
            case ADD_TO_INV:
                builder = ItemBuilder.newItem(XMaterial.CRAFTING_TABLE);
                builder.addLoreLine("&dIn this mode, the item will be");
                builder.addLoreLine("&d  added to player's inventory");
                builder.addLoreLine("&d  if there is some free space");
                builder.addLoreLine("&dIn other case, the item will spawn");
                builder.addLoreLine("&d  on the ground");
                break;
            case SET_TO_INV:
                builder = ItemBuilder.newItem(XMaterial.CHEST);
                builder.addLoreLine("&dIn this mode, the item will be");
                builder.addLoreLine("&d  stored in a specific slot of");
                builder.addLoreLine("&d  player's inventory");
                builder.addLoreLine("&d  if that slot is empty");
                builder.addLoreLine("&dIn other case, the item will spawn");
                builder.addLoreLine("&d  on the ground");
                break;
            default: //FORCE_SET_TO_INV
                builder = ItemBuilder.newItem(XMaterial.ENDER_CHEST);
                builder.addLoreLine("&dIn this mode, the item will be");
                builder.addLoreLine("&d  stored in a specific slot of");
                builder.addLoreLine("&d  player's inventory");
                builder.addLoreLine("&dIf the slot is not empty,");
                builder.addLoreLine("&d  the item in the slot");
                builder.addLoreLine("&d  will be dropped near the player");
                break;
        }
        builder.addLoreLine("");
        builder.addLoreLine("&3Click to change");
        builder.withDisplayName("&bSpawn mode: &a" + item.getSpawnMode().name());
        ItemStack spawnModeItem = builder.build();
        
        builder = ItemBuilder.newItem(XMaterial.CHAINMAIL_CHESTPLATE);
        builder.withAmount(item.getSpawnInvSlot());
        builder.withDisplayName("&6Current inv slot: &e" + item.getSpawnInvSlot() + " &6/ &e" + ItemReward.getMaxSlot());
        builder.addLoreLine("&2This slot is only used when");
        if(item.getSpawnMode() == ItemReward.ItemSpawnMode.SET_TO_INV){
            builder.addLoreLine("&2  spawnMode is &5&lSET_TO_INV");
        }else{
            builder.addLoreLine("&2  spawnMode is &e&lSET_TO_INV");
        }
        if(item.getSpawnMode() == ItemReward.ItemSpawnMode.FORCE_SET_TO_INV){
            builder.addLoreLine("&2  &2or &5&lFORCE_SET_TO_INV");
        }else{
            builder.addLoreLine("&2  &2or &e&lFORCE_SET_TO_INV");
        }        
        builder.addLoreLine("");
        builder.addLoreLine("&2Slots from &b0 &2to &b8 &2are from hotbar");
        builder.addLoreLine("&2Slots from &b9 &2to &b35 &2are from");
        builder.addLoreLine("&2  survival inventory");
        if(item.getSpawnInvSlot() == ItemReward.HELMET_SLOT){
            builder.addLoreLine("&2Slot &b36 &2is &5&lHELMET");
        }else{
            builder.addLoreLine("&2Slot &b36 &2is &e&lHELMET");
        }
        if(item.getSpawnInvSlot() == ItemReward.CHESTPLATE_SLOT){
            builder.addLoreLine("&2Slot &b37 &2is &5&lCHESTPLATE");
        }else{
            builder.addLoreLine("&2Slot &b37 &2is &e&lCHESTPLATE");
        }
        if(item.getSpawnInvSlot() == ItemReward.LEGGINGS_SLOT){
            builder.addLoreLine("&2Slot &b38 &2is &5&lLEGGINGS");
        }else{
            builder.addLoreLine("&2Slot &b38 &2is &e&lLEGGINGS");
        }
        if(item.getSpawnInvSlot() == ItemReward.BOOTS_SLOT){
            builder.addLoreLine("&2Slot &b39 &2is &5&lBOOTS");
        }else{
            builder.addLoreLine("&2Slot &b39 &2is &e&lBOOTS");
        }
        if(item.getSpawnInvSlot() == ItemReward.ITEM_IN_HAND_SLOT){
            builder.addLoreLine("&2Slot &b40 &2is &5&lITEM IN HAND");
        }else{
            builder.addLoreLine("&2Slot &b40 &2is &e&lITEM IN HAND");
        }
        
        if(NewAmazingLuckyBlocks.getMinecraftVersion().compareTo(MinecraftVersion.v1_9) >= 0){
            if(item.getSpawnInvSlot() == ItemReward.ITEM_IN_OFF_HAND_SLOT){
                builder.addLoreLine("&2Slot &b41 &2is &5&lITEM IN OFF-HAND");
            }else{
                builder.addLoreLine("&2Slot &b41 &2is &e&lITEM IN OFF-HAND");
            }
        }
        builder.addLoreLine("");
        builder.addLoreLine("&3Click to reset");
        ItemStack spawnInvSlotItem = builder.build();
        
        menu.setItem(BACK_SLOT, GUIItem.getBackItem());
        menu.setItem(NEXT_SLOT, GUIItem.getNextItem());
        
        menu.setItem(ITEM_SLOT, item.getItem());
        menu.setItem(REMOVE_SPECIAL_DATA_SLOT, removeSpecialData);
        menu.setItem(POTION_TYPE_SLOT, changePotionType);
        menu.setItem(SPECIAL_ITEM_SLOT, specialItem);
        menu.setItem(NAME_SLOT, name);
        menu.setItem(LORE_SLOT, lore);
        menu.setItem(ENCHANTMENTS_SLOT, enchantments);
        
        menu.setItem(RESET_NAME_SLOT, resetName);
        menu.setItem(RESET_LORE_SLOT, resetLore);
        menu.setItem(RESET_ENCHANTMENTS_SLOT, resetEnchantments);
        
        if(setPotionColor != null){
            menu.setItem(POTION_COLOR_SLOT, setPotionColor);
        }
        
        menu.setItem(DURABILITY_SLOT, durability);
        
        for(int i=1, multiplier=1; i<=4; i++, multiplier *= 10){
            menu.setItem(DURABILITY_SLOT-i, GUIItem.getPlusLessItem(-1*multiplier));
            menu.setItem(DURABILITY_SLOT+i, GUIItem.getPlusLessItem(+1*multiplier));
        }
        
        menu.setItem(SPAWN_MODE_SLOT, spawnModeItem);
        menu.setItem(SPAWN_INV_SLOT_SLOT, spawnInvSlotItem);
        
        for(int i=1, multiplier=1; i<=2; i++, multiplier *= 10){
            menu.setItem(SPAWN_INV_SLOT_SLOT-i, GUIItem.getPlusLessItem(-1*multiplier));
            menu.setItem(SPAWN_INV_SLOT_SLOT+i, GUIItem.getPlusLessItem(+1*multiplier));
        }
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction ITEM_MENU_2_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        switch(e.getSlot()){
            case BACK_SLOT:
                // Go to previous menu
                openItemMenu(player);
                break;
            case NEXT_SLOT:
                // Go to next menu
                onNext.accept(player, item);
                break;
            case NAME_SLOT:
                //Set custom name
                ChatListener.registerPlayer(player, message -> {
                    ItemBuilder.fromItem(item.getItem(), false)
                            .withDisplayName(message);
                    openItemMenu2(player);
                });
                player.closeInventory();
                break;
            case LORE_SLOT:
                //Add lore line
                ChatListener.registerPlayer(player, message -> {
                    ItemBuilder.fromItem(item.getItem(), false)
                            .addLoreLine(message);                    
                    openItemMenu2(player);
                });
                player.closeInventory();
                break;
            case ENCHANTMENTS_SLOT:
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
            case RESET_NAME_SLOT:
                //Reset custom name
                ItemBuilder.fromItem(item.getItem(), false)
                        .withDisplayName(null);
                openItemMenu2(player);
                break;
            case RESET_LORE_SLOT:
                //Reset custom lore
                ItemBuilder.fromItem(item.getItem(), false)
                        .withLore();
                openItemMenu2(player);
                break;
            case RESET_ENCHANTMENTS_SLOT:
                //Reset enchantments
                ItemBuilder.fromItem(item.getItem(), false)
                        .clearEnchantments();
                openItemMenu2(player);
                break;
            case DURABILITY_SLOT:
                //Reset durability
                item.getItem().setDurability((short) 0);
                openItemMenu2(player);
                break;
                //<editor-fold defaultstate="collapsed" desc="Decrease durability slots">
            case DURABILITY_SLOT-1:
                //Durability-1
                ItemBuilder builder = ItemBuilder.fromItem(item.getItem(), false);
                builder.withDurability(builder.getDurability() - 1);
                openItemMenu2(player);
                break;
            case DURABILITY_SLOT-2:
                //Durability-10
                builder = ItemBuilder.fromItem(item.getItem(), false);
                builder.withDurability(builder.getDurability() - 10);
                openItemMenu2(player);
                break;
            case DURABILITY_SLOT-3:
                //Durability-100
                builder = ItemBuilder.fromItem(item.getItem(), false);
                builder.withDurability(builder.getDurability() - 100);
                openItemMenu2(player);
                break;
            case DURABILITY_SLOT-4:
                //Durability-1000
                builder = ItemBuilder.fromItem(item.getItem(), false);
                builder.withDurability(builder.getDurability() - 1000);
                openItemMenu2(player);
                break;
//</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Increase durability slots">
            case DURABILITY_SLOT+1:
                //Durability+1
                builder = ItemBuilder.fromItem(item.getItem(), false);
                builder.withDurability(builder.getDurability() + 1);
                openItemMenu2(player);
                break;
            case DURABILITY_SLOT+2:
                //Durability+10
                builder = ItemBuilder.fromItem(item.getItem(), false);
                builder.withDurability(builder.getDurability() + 10);
                openItemMenu2(player);
                break;
            case DURABILITY_SLOT+3:
                //Durability+100
                builder = ItemBuilder.fromItem(item.getItem(), false);
                builder.withDurability(builder.getDurability() + 100);
                openItemMenu2(player);
                break;
            case DURABILITY_SLOT+4:
                //Durability+1000
                builder = ItemBuilder.fromItem(item.getItem(), false);
                builder.withDurability(builder.getDurability() + 1000);
                openItemMenu2(player);
                break;
//</editor-fold>
            case POTION_COLOR_SLOT:
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
            case SPECIAL_ITEM_SLOT:
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
            case POTION_TYPE_SLOT:
                //Change potion type
                if(e.getCurrentItem() != null){
                    XMaterial material = XMaterial.matchXMaterial(e.getCurrentItem());
                    if(material == XMaterial.BREWING_STAND){
                        PotionSplashType type = PotionSplashType.getFromPotion(item.getItem());
                        type.next().setToPotion(item.getItem());
                        openItemMenu2(player);
                    }
                }
                break;
            case REMOVE_SPECIAL_DATA_SLOT:
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
            case SPAWN_MODE_SLOT:
                item.setSpawnMode(item.getSpawnMode().next());
                openItemMenu2(player);
                break;
            case SPAWN_INV_SLOT_SLOT:
                item.setSpawnInvSlot(0);
                openItemMenu2(player);
                break;
            case SPAWN_INV_SLOT_SLOT-1:
                int spawnInvSlot = item.getSpawnInvSlot() - 1;
                if(spawnInvSlot < 0){
                    spawnInvSlot = ItemReward.getMaxSlot();
                }
                item.setSpawnInvSlot(spawnInvSlot);
                openItemMenu2(player);
                break;
            case SPAWN_INV_SLOT_SLOT-2:
                spawnInvSlot = item.getSpawnInvSlot() - 10;
                if(spawnInvSlot < 0){
                    spawnInvSlot = ItemReward.getMaxSlot();
                }
                item.setSpawnInvSlot(spawnInvSlot);
                openItemMenu2(player);
                break;
            case SPAWN_INV_SLOT_SLOT+1:
                spawnInvSlot = item.getSpawnInvSlot() + 1;
                if(spawnInvSlot > ItemReward.getMaxSlot()){
                    spawnInvSlot = 0;
                }
                item.setSpawnInvSlot(spawnInvSlot);
                openItemMenu2(player);
                break;
            case SPAWN_INV_SLOT_SLOT+2:
                spawnInvSlot = item.getSpawnInvSlot() + 10;
                if(spawnInvSlot > ItemReward.getMaxSlot()){
                    spawnInvSlot = 0;
                }
                item.setSpawnInvSlot(spawnInvSlot);
                openItemMenu2(player);
                break;
        }
//</editor-fold>
    };
}
