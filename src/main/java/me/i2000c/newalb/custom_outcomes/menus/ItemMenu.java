package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import java.util.Set;
import me.i2000c.newalb.MinecraftVersion;
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
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.textures.Texture;
import me.i2000c.newalb.utils.textures.TextureException;
import me.i2000c.newalb.utils.textures.TextureManager;
import me.i2000c.newalb.utils2.CustomColor;
import me.i2000c.newalb.utils2.EnchantmentWithLevel;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import me.i2000c.newalb.utils2.Offset;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
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
    
    private static final int OFFSET_SLOT = 36;
    
    private static final int ITEM_FLAGS_SLOT = 47;
    
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
        
        ItemStack select_from_inventory_item = ItemStackWrapper.newItem(XMaterial.BRICKS)
                                                               .setDisplayName("&7Select an item from your inventory")
                                                               .toItemStack();
        
        ItemStackWrapper wrapper = ItemStackWrapper.newItem(XMaterial.BLACK_STAINED_GLASS_PANE);
        if(item.getItem() == null){
            wrapper.setDisplayName("&bAmount: &r?");
        }else{
            wrapper.setDisplayName("&bAmount: &r" + item.getItem().getAmount());
        }
        ItemStack amount_item = wrapper.toItemStack();
        
        ItemStack creative = ItemStackWrapper.newItem(XMaterial.CRAFTING_TABLE)
                                             .setDisplayName("&3Close menu to pick items from creative mode")
                                             .toItemStack();
        
        for(int i=0;i<9;i++){
            menu.setItem(i, glass);
        }
        for(int i=18;i<27;i++){
            menu.setItem(i, glass);
        }
        menu.setItem(9, glass);
        menu.setItem(17, glass);
        
        menu.setItem(11, select_from_inventory_item);
        if(item.getItem() == null){
            menu.setItem(14, amount_item);
        }else{
            menu.setItem(14, item.getItem());
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
                    if(item.getItem() == null){
                        break;
                    }
                    
                    int amount = item.getItem().getAmount() + 1;
                    if(amount > 64){
                        amount = 1;
                    }
                    item.getItem().setAmount(amount);
                    openItemMenu(player);
                    break;
                case 13:
                    if(item.getItem() == null){
                        break;
                    }
                    
                    amount = item.getItem().getAmount() - 1;
                    if(amount < 1){
                        amount = 64;
                    }
                    item.getItem().setAmount(amount);
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
        
        ItemStack name = ItemStackWrapper.newItem(XMaterial.NAME_TAG)
                                         .setDisplayName("&aClick to set custom name")
                                         .toItemStack();
        
        ItemStack lore = ItemStackWrapper.newItem(XMaterial.OAK_SIGN)
                                         .setDisplayName("&6Click to add lore line")
                                         .toItemStack();
        
        short currentDurability = item.getItem().getDurability();
        short maxDurability = item.getItem().getType().getMaxDurability();
        ItemStackWrapper wrapper = ItemStackWrapper.newItem(XMaterial.IRON_PICKAXE).addLoreLine("&3Click to reset");
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()) {
            wrapper.setDisplayName("&7Current durability: &a" + currentDurability);
        } else {
            wrapper.setDisplayName("&7Current durability: &a" + currentDurability + " &6/ &a" + maxDurability);
        }
        ItemStack durability = wrapper.toItemStack();
        
        ItemStack enchantments = ItemStackWrapper.newItem(XMaterial.ENCHANTING_TABLE)
                                                 .setDisplayName("&dClick to add enchantment")
                                                 .toItemStack();
        
        //Reset items        
        ItemStack resetName = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                              .setDisplayName("&cClick to reset custom name")
                                              .toItemStack();
        
        ItemStack resetLore = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                              .setDisplayName("&cClick to reset lore")
                                              .toItemStack();
        
        ItemStack resetEnchantments = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                                      .setDisplayName("&cClick to reset enchantments")
                                                      .toItemStack();
        
        //Special items
        ItemStack specialItem = null;
        ItemStack removeSpecialData = null;
        ItemStack changePotionType = null;
        ItemStack setPotionColor = null;
        if(TextureManager.isSkull(item.getItem().getType())){
            //TextureMeta
            specialItem = ItemStackWrapper.newItem(XMaterial.PLAYER_HEAD)
                                          .setDisplayName("&5Click to set custom texture")
                                          .addLoreLine("&3You can write 'null'")
                                          .addLoreLine("&3  if you want to remove the texture")
                                          .toItemStack();
            
            removeSpecialData = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                                .setDisplayName("&cClick to remove custom texture")
                                                .toItemStack();
        }else switch(XMaterial.matchXMaterial(item.getItem().getType())){
            case ENCHANTED_BOOK:
                // EnchantmentStorageMeta
                specialItem = ItemStackWrapper.newItem(XMaterial.ENCHANTED_BOOK)
                                              .setDisplayName("&5Click to add custom enchantments to this book")
                                              .toItemStack();
                
                removeSpecialData = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                                    .setDisplayName("&cClick to remove all book enchantments")
                                                    .toItemStack();
                break;
            case POTION:
            case SPLASH_POTION:
            case LINGERING_POTION:
                //PotionMeta
                specialItem = ItemStackWrapper.newItem(XMaterial.POTION)
                                              .setDisplayName("&5Click to add custom potion effects")
                                              .toItemStack();
                
                removeSpecialData = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                                    .setDisplayName("&cClick to remove all potion effects")
                                                    .toItemStack();
                
                changePotionType = ItemStackWrapper.newItem(XMaterial.BREWING_STAND)
                                                   .setDisplayName("&bClick to change potion type")
                                                   .addLoreLine("&dCurrent type: &e" + PotionSplashType.getFromPotion(item.getItem()))
                                                   .toItemStack();
                
                if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_11)){
                    setPotionColor = ItemStackWrapper.newItem(XMaterial.BLAZE_POWDER)
                                                     .setDisplayName("&dClick to set potion color")
                                                     .toItemStack();
                }
                
                break;
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                //LeatherArmorMeta
                specialItem = ItemStackWrapper.newItem(XMaterial.LEATHER)
                                              .setDisplayName("&5Click to set custom armor color")
                                              .toItemStack();
                
                removeSpecialData = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                                    .setDisplayName("&cClick to reset armor color")
                                                    .toItemStack();
                break;
        }
        
        //Spawn mode items
        ItemStackWrapper builder;
        switch(item.getSpawnMode()){
            case DEFAULT:
                builder = ItemStackWrapper.newItem(XMaterial.GRASS_BLOCK);
                builder.addLoreLine("&dIn this mode, the item will spawn");
                builder.addLoreLine("&d  on the ground");
                break;
            case ADD_TO_INV:
                builder = ItemStackWrapper.newItem(XMaterial.CRAFTING_TABLE);
                builder.addLoreLine("&dIn this mode, the item will be");
                builder.addLoreLine("&d  added to player's inventory");
                builder.addLoreLine("&d  if there is some free space");
                builder.addLoreLine("&dIn other case, the item will spawn");
                builder.addLoreLine("&d  on the ground");
                break;
            case SET_TO_INV:
                builder = ItemStackWrapper.newItem(XMaterial.CHEST);
                builder.addLoreLine("&dIn this mode, the item will be");
                builder.addLoreLine("&d  stored in a specific slot of");
                builder.addLoreLine("&d  player's inventory");
                builder.addLoreLine("&d  if that slot is empty");
                builder.addLoreLine("&dIn other case, the item will spawn");
                builder.addLoreLine("&d  on the ground");
                break;
            default: //FORCE_SET_TO_INV
                builder = ItemStackWrapper.newItem(XMaterial.ENDER_CHEST);
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
        builder.setDisplayName("&bSpawn mode: &a" + item.getSpawnMode().name());
        ItemStack spawnModeItem = builder.toItemStack();
        
        builder = ItemStackWrapper.newItem(XMaterial.CHAINMAIL_CHESTPLATE);
        builder.setAmount(item.getSpawnInvSlot());
        if(builder.getAmount() <= 0 && !MinecraftVersion.CURRENT_VERSION.isLegacyVersion()) {
            builder.setAmount(1);
        }
        builder.setDisplayName("&6Current inv slot: &e" + item.getSpawnInvSlot() + " &6/ &e" + ItemReward.getMaxSlot());
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
        
        if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_9)){
            if(item.getSpawnInvSlot() == ItemReward.ITEM_IN_OFF_HAND_SLOT){
                builder.addLoreLine("&2Slot &b41 &2is &5&lITEM IN OFF-HAND");
            }else{
                builder.addLoreLine("&2Slot &b41 &2is &e&lITEM IN OFF-HAND");
            }
        }
        builder.addLoreLine("");
        builder.addLoreLine("&3Click to reset");
        ItemStack spawnInvSlotItem = builder.toItemStack();        
        
        ItemStack offsetStack = item.getOffset().getItemToDisplay();
        builder = ItemStackWrapper.fromItem(offsetStack, false);
        builder.addLoreLine("");
        builder.addLoreLine("&2Offset is only used if");
        if(item.getSpawnMode() == ItemReward.ItemSpawnMode.DEFAULT){
            builder.addLoreLine("&2  spawnMode is &5&lDEFAULT");
        }else{
            builder.addLoreLine("&2  spawnMode is &e&lDEFAULT");
        }
        if(item.getSpawnMode() == ItemReward.ItemSpawnMode.ADD_TO_INV){
            builder.addLoreLine("&2  or &5&lADD_TO_INV");
        }else{
            builder.addLoreLine("&2  or &e&lADD_TO_INV");
        }
        if(item.getSpawnMode() == ItemReward.ItemSpawnMode.SET_TO_INV){
            builder.addLoreLine("&2  or &5&lSET_TO_INV");
        }else{
            builder.addLoreLine("&2  or &e&lSET_TO_INV");
        }
        
        builder = ItemStackWrapper.newItem(XMaterial.MAGENTA_BANNER);
        builder.setDisplayName("&dModify item flags");
        builder.addLoreLine("");
        builder.addLoreLine("&5Current flags:");
        Set<ItemFlag> flags = item.getItem().getItemMeta().getItemFlags();
        for(ItemFlag flag : flags) {
            builder.addLoreLine("  &b" + flag.name());
        }
        ItemStack itemFlagsStack = builder.toItemStack();
        
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
        
        menu.setItem(OFFSET_SLOT, offsetStack);
        
        menu.setItem(ITEM_FLAGS_SLOT, itemFlagsStack);
        
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
                    ItemStackWrapper.fromItem(item.getItem(), false).setDisplayName(message);
                    openItemMenu2(player);
                });
                player.closeInventory();
                break;
            case LORE_SLOT:
                //Add lore line
                ChatListener.registerPlayer(player, message -> {
                    ItemStackWrapper.fromItem(item.getItem(), false)
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
                            ItemStackWrapper.fromItem(item.getItem(), false)
                                    .addEnchantment(enchantmentWithLevel.enchantment, enchantmentWithLevel.level);
                            openItemMenu2(p);
                        });
                break;            
            case RESET_NAME_SLOT:
                //Reset custom name
                ItemStackWrapper.fromItem(item.getItem(), false).setDisplayName(null);
                openItemMenu2(player);
                break;
            case RESET_LORE_SLOT:
                //Reset custom lore
                ItemStackWrapper.fromItem(item.getItem(), false).setLore();
                openItemMenu2(player);
                break;
            case RESET_ENCHANTMENTS_SLOT:
                //Reset enchantments
                ItemStackWrapper.fromItem(item.getItem(), false).clearEnchantments();
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
                ItemStackWrapper wrapper = ItemStackWrapper.fromItem(item.getItem(), false);
                wrapper.setDurability(wrapper.getDurability() - 1);
                openItemMenu2(player);
                break;
            case DURABILITY_SLOT-2:
                //Durability-10
                wrapper = ItemStackWrapper.fromItem(item.getItem(), false);
                wrapper.setDurability(wrapper.getDurability() - 10);
                openItemMenu2(player);
                break;
            case DURABILITY_SLOT-3:
                //Durability-100
                wrapper = ItemStackWrapper.fromItem(item.getItem(), false);
                wrapper.setDurability(wrapper.getDurability() - 100);
                openItemMenu2(player);
                break;
            case DURABILITY_SLOT-4:
                //Durability-1000
                wrapper = ItemStackWrapper.fromItem(item.getItem(), false);
                wrapper.setDurability(wrapper.getDurability() - 1000);
                openItemMenu2(player);
                break;
//</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Increase durability slots">
            case DURABILITY_SLOT+1:
                //Durability+1
                wrapper = ItemStackWrapper.fromItem(item.getItem(), false);
                wrapper.setDurability(wrapper.getDurability() + 1);
                openItemMenu2(player);
                break;
            case DURABILITY_SLOT+2:
                //Durability+10
                wrapper = ItemStackWrapper.fromItem(item.getItem(), false);
                wrapper.setDurability(wrapper.getDurability() + 10);
                openItemMenu2(player);
                break;
            case DURABILITY_SLOT+3:
                //Durability+100
                wrapper = ItemStackWrapper.fromItem(item.getItem(), false);
                wrapper.setDurability(wrapper.getDurability() + 100);
                openItemMenu2(player);
                break;
            case DURABILITY_SLOT+4:
                //Durability+1000
                wrapper = ItemStackWrapper.fromItem(item.getItem(), false);
                wrapper.setDurability(wrapper.getDurability() + 1000);
                openItemMenu2(player);
                break;
//</editor-fold>
            case POTION_COLOR_SLOT:
                //Open select potion color menu
                if(e.getCurrentItem() != null){
                    if(e.getCurrentItem().getType() == Material.BLAZE_POWDER){
                        if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_11)){
                            Color itemColor = ItemStackWrapper.fromItem(item.getItem(), false)
                                    .getColor();
                            Editor<CustomColor> editor3 = EditorType.COLOR.getEditor();
                            editor3.editExistingItem(
                                    new CustomColor(itemColor), 
                                    player, 
                                    p -> openItemMenu2(p), 
                                    (p, color) -> {
                                        ItemStackWrapper.fromItem(item.getItem(), false).setColor(color.getBukkitColor());
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
                        case ENCHANTED_BOOK:
                            // Open enchantment menu
                            Editor<EnchantmentWithLevel> editorBook = EditorType.ENCHANTMENT.getEditor();
                            editorBook.createNewItem(
                                    player, 
                                    p -> openItemMenu2(p), 
                                    (p, enchantmentWithLevel) -> {
                                        ItemStackWrapper.fromItem(item.getItem(), false)
                                                .addBookEnchantment(enchantmentWithLevel.enchantment, enchantmentWithLevel.level);
                                        openItemMenu2(p);
                                    });
                            break;
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
                                                effectReward.getDuration()*20, 
                                                effectReward.getAmplifier());
                                        ItemStackWrapper.fromItem(item.getItem(), false)
                                                .addPotionEffect(potionEffect);
                                        openItemMenu2(p);
                                    });
                            break;
                        case LEATHER:
                            //Open armor color menu
                            Color itemColor = ItemStackWrapper.fromItem(item.getItem(), false)
                                    .getColor();
                            Editor<CustomColor> editor3 = EditorType.COLOR.getEditor();
                            editor3.editExistingItem(
                                    new CustomColor(itemColor), 
                                    player, 
                                    p -> openItemMenu2(p), 
                                    (p, color) -> {
                                        ItemStackWrapper.fromItem(item.getItem(), false).setColor(color.getBukkitColor());
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
                        case ENCHANTED_BOOK:
                            // Remove all book enchantments
                            ItemStackWrapper.fromItem(item.getItem(), false)
                                    .clearBookEnchantments();
                            openItemMenu2(player);
                            break;
                        case POTION:
                        case SPLASH_POTION:
                        case LINGERING_POTION:
                            //Remove all effects
                            ItemStackWrapper.fromItem(item.getItem(), false)
                                    .clearPotionEffects();
                            
                            if(MinecraftVersion.CURRENT_VERSION.is_1_8()){
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
                            ItemStackWrapper.fromItem(item.getItem(), false).setColor(null);
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
            case OFFSET_SLOT:
                Editor<Offset> offsetEditor = EditorType.OFFSET.getEditor();
                    offsetEditor.editExistingItem(
                            item.getOffset().clone(), 
                            player, 
                            p -> openItemMenu2(p), 
                            (p, offset) -> {
                                item.setOffset(offset);
                                openItemMenu2(p);
                            });
                break;
            case ITEM_FLAGS_SLOT:
                Editor<ItemStack> itemFlagEditor = EditorType.ITEM_FLAGS.getEditor();
                itemFlagEditor.editExistingItem(item.getItem(),
                                                player,
                                                this::openItemMenu2,
                                                (p, __) -> openItemMenu2(p));
                break;
        }
//</editor-fold>
    };
}
