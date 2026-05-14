package me.i2000c.newalb.lucky_blocks.editors.menus.item;

import java.util.List;
import java.util.Set;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;

import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.api.version.MinecraftVersion;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.lucky_blocks.editors.menus.ColorMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.ItemFlagMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.OffsetMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.effect.EffectMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.enchantment.EnchantmentMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.nbt.ItemNbtMenu;
import me.i2000c.newalb.lucky_blocks.rewards.types.ItemReward;
import me.i2000c.newalb.lucky_blocks.rewards.types.ItemReward.PotionSplashType;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.CustomColor;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.textures.Texture;

@SuppressWarnings("deprecation")
class ItemMenu2 extends EditorMenu<ItemReward> {
    
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
    private static final int ITEM_NBT_SLOT = 51;
    private static final int ITEM_UNBREAKABLE_SLOT = 49;

    public ItemMenu2() {
        super("&b&lItem Reward 2", MenuSize.SIZE_6_ROWS, true);
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    protected void buildMenu(Player player) {
        addGlassBorder(GlassColor.CYAN, true);
        
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
        
        XMaterial material = XMaterial.matchXMaterial(item.getItem().getType());
        if(Texture.isSkull(item.getItem())) {
            //TextureMeta
            specialItem = ItemStackWrapper.newItem(XMaterial.PLAYER_HEAD)
                                          .setDisplayName("&5Click to set custom texture")
                                          .addLoreLine("&3You can write 'null'")
                                          .addLoreLine("&3  if you want to remove the texture")
                                          .toItemStack();
            
            removeSpecialData = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                                .setDisplayName("&cClick to remove custom texture")
                                                .toItemStack();
        } else switch(material) {
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
            case TIPPED_ARROW:
            case SUSPICIOUS_STEW:
                //PotionMeta
                ItemStackWrapper specialItemWrapper = ItemStackWrapper.newItem(XMaterial.POTION);
                specialItemWrapper.setDisplayName("&5Click to add custom potion effects");
                specialItemWrapper.addLoreLine("&3Current effects:");
                List<PotionEffect> effects = ItemStackWrapper.fromItem(item.getItem(), false).getPotionEffects();
                effects.forEach(effect -> {
                    String effectName = XPotion.of(effect.getType()).name();
                    int duration = effect.getDuration();
                    int amplifier = effect.getAmplifier();
                    boolean isAmbient = effect.isAmbient();
                    boolean showParticles = effect.hasParticles();
                    specialItemWrapper.addLoreLine(String.format("  &d%s;%d;%d;%s;%s",
                            effectName, duration, amplifier, isAmbient, showParticles));
                });
                specialItem = specialItemWrapper.toItemStack();
                
                removeSpecialData = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                                    .setDisplayName("&cClick to remove all potion effects")
                                                    .toItemStack();
                
                if(material.name().contains("POTION")) {
                    changePotionType = ItemStackWrapper.newItem(XMaterial.BREWING_STAND)
                                                       .setDisplayName("&bClick to change potion type")
                                                       .addLoreLine("&dCurrent type: &e" + PotionSplashType.getFromPotion(item.getItem()))
                                                       .toItemStack();
                }
                
                if(material != XMaterial.SUSPICIOUS_STEW) {
                    if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_11)) {
                        setPotionColor = ItemStackWrapper.newItem(XMaterial.BLAZE_POWDER)
                                                         .setDisplayName("&dClick to set potion color")
                                                         .toItemStack();
                    }
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
        switch(item.getSpawnMode()) {
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
        if(item.getSpawnMode() == ItemReward.ItemSpawnMode.SET_TO_INV) {
            builder.addLoreLine("&2  spawnMode is &5&lSET_TO_INV");
        } else {
            builder.addLoreLine("&2  spawnMode is &e&lSET_TO_INV");
        }
        if(item.getSpawnMode() == ItemReward.ItemSpawnMode.FORCE_SET_TO_INV) {
            builder.addLoreLine("&2  &2or &5&lFORCE_SET_TO_INV");
        } else {
            builder.addLoreLine("&2  &2or &e&lFORCE_SET_TO_INV");
        }
        builder.addLoreLine("");
        builder.addLoreLine("&2Slots from &b0 &2to &b8 &2are from hotbar");
        builder.addLoreLine("&2Slots from &b9 &2to &b35 &2are from");
        builder.addLoreLine("&2  survival inventory");
        if(item.getSpawnInvSlot() == ItemReward.HELMET_SLOT) {
            builder.addLoreLine("&2Slot &b36 &2is &5&lHELMET");
        } else {
            builder.addLoreLine("&2Slot &b36 &2is &e&lHELMET");
        }
        if(item.getSpawnInvSlot() == ItemReward.CHESTPLATE_SLOT) {
            builder.addLoreLine("&2Slot &b37 &2is &5&lCHESTPLATE");
        } else {
            builder.addLoreLine("&2Slot &b37 &2is &e&lCHESTPLATE");
        }
        if(item.getSpawnInvSlot() == ItemReward.LEGGINGS_SLOT) {
            builder.addLoreLine("&2Slot &b38 &2is &5&lLEGGINGS");
        } else {
            builder.addLoreLine("&2Slot &b38 &2is &e&lLEGGINGS");
        }
        if(item.getSpawnInvSlot() == ItemReward.BOOTS_SLOT) {
            builder.addLoreLine("&2Slot &b39 &2is &5&lBOOTS");
        } else {
            builder.addLoreLine("&2Slot &b39 &2is &e&lBOOTS");
        }
        if(item.getSpawnInvSlot() == ItemReward.ITEM_IN_HAND_SLOT) {
            builder.addLoreLine("&2Slot &b40 &2is &5&lITEM IN HAND");
        } else {
            builder.addLoreLine("&2Slot &b40 &2is &e&lITEM IN HAND");
        }
        
        if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_9)) {
            if(item.getSpawnInvSlot() == ItemReward.ITEM_IN_OFF_HAND_SLOT) {
                builder.addLoreLine("&2Slot &b41 &2is &5&lITEM IN OFF-HAND");
            } else {
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
        if(item.getSpawnMode() == ItemReward.ItemSpawnMode.DEFAULT) {
            builder.addLoreLine("&2  spawnMode is &5&lDEFAULT");
        } else {
            builder.addLoreLine("&2  spawnMode is &e&lDEFAULT");
        }
        if(item.getSpawnMode() == ItemReward.ItemSpawnMode.ADD_TO_INV) {
            builder.addLoreLine("&2  or &5&lADD_TO_INV");
        } else {
            builder.addLoreLine("&2  or &e&lADD_TO_INV");
        }
        if(item.getSpawnMode() == ItemReward.ItemSpawnMode.SET_TO_INV) {
            builder.addLoreLine("&2  or &5&lSET_TO_INV");
        } else {
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
        
        ItemStack itemNbtStack = ItemStackWrapper.newItem(XMaterial.WRITABLE_BOOK)
                                                 .setDisplayName("&6Modify NBT tags")
                                                 .toItemStack();
        
        if(ItemStackWrapper.fromItem(item.getItem(), false).isUnbreakable()) {
            builder = ItemStackWrapper.newItem(XMaterial.ANVIL);
            builder.setDisplayName("&bUnbreakable item: &atrue");
        } else {
            builder = ItemStackWrapper.newItem(XMaterial.DAMAGED_ANVIL);
            builder.setDisplayName("&bUnbreakable item: &cfalse");
        }
        builder.addLoreLine("");
        builder.addLoreLine("&3Click to toggle");
        ItemStack itemUnbreakableStack = builder.toItemStack();
        
        setBackItem(BACK_SLOT);
        setNextItem(NEXT_SLOT);
        setItem(ITEM_SLOT, item.getItem());
        setItem(REMOVE_SPECIAL_DATA_SLOT, removeSpecialData);
        setItem(POTION_TYPE_SLOT, changePotionType);
        setItem(SPECIAL_ITEM_SLOT, specialItem);
        setItem(NAME_SLOT, name);
        setItem(LORE_SLOT, lore);
        setItem(ENCHANTMENTS_SLOT, enchantments);
        
        setItem(RESET_NAME_SLOT, resetName);
        setItem(RESET_LORE_SLOT, resetLore);
        setItem(RESET_ENCHANTMENTS_SLOT, resetEnchantments);
        
        if(setPotionColor != null) {
            setItem(POTION_COLOR_SLOT, setPotionColor);
        }
        
        setItem(DURABILITY_SLOT, durability);
        
        for(int i=1, multiplier=1; i<=4; i++, multiplier *= 10) {
            setItem(DURABILITY_SLOT-i, GUIItem.getPlusLessItem(-1*multiplier));
            setItem(DURABILITY_SLOT+i, GUIItem.getPlusLessItem(+1*multiplier));
        }
        
        setItem(SPAWN_MODE_SLOT, spawnModeItem);
        setItem(SPAWN_INV_SLOT_SLOT, spawnInvSlotItem);
        
        for(int i=1, multiplier=1; i<=2; i++, multiplier *= 10) {
            setItem(SPAWN_INV_SLOT_SLOT-i, GUIItem.getPlusLessItem(-1*multiplier));
            setItem(SPAWN_INV_SLOT_SLOT+i, GUIItem.getPlusLessItem(+1*multiplier));
        }
        
        setItem(OFFSET_SLOT, offsetStack);
        
        setItem(ITEM_FLAGS_SLOT, itemFlagsStack);
        setItem(ITEM_NBT_SLOT, itemNbtStack);
        setItem(ITEM_UNBREAKABLE_SLOT, itemUnbreakableStack);
    }
    
    @SuppressWarnings("incomplete-switch")
    @Override
    protected void onClickDefault(MenuClickEvent event) {
        if(event.isEmptyItem()) {
            return;
        }
        
        Player player = event.getPlayer();
        
        switch(event.getSlot()) {
            case NAME_SLOT:
                //Set custom name
                ChatListener.registerPlayer(player, message -> {
                    ItemStackWrapper.fromItem(item.getItem(), false).setDisplayName(message);
                    openToPlayer(player);
                });
                player.closeInventory();
                break;
            case LORE_SLOT:
                //Add lore line
                ChatListener.registerPlayer(player, message -> {
                    ItemStackWrapper.fromItem(item.getItem(), false)
                                    .addLoreLine(message);                    
                    openToPlayer(player);
                });
                player.closeInventory();
                break;
            case ENCHANTMENTS_SLOT:
                //Open enchantments menu
                EnchantmentMenu enchantmentMenu = new EnchantmentMenu();
                enchantmentMenu.setOnBack(this::openToPlayer);
                enchantmentMenu.setOnNext((p, enchantmentWithLevel) -> {
                    ItemStackWrapper.fromItem(item.getItem(), false)
                                    .addEnchantment(enchantmentWithLevel.enchantment, enchantmentWithLevel.level);
                    openToPlayer(player);
                });
                enchantmentMenu.openToPlayer(player);
                break;            
            case RESET_NAME_SLOT:
                //Reset custom name
                ItemStackWrapper.fromItem(item.getItem(), false).setDisplayName(null);
                openToPlayer(player);
                break;
            case RESET_LORE_SLOT:
                //Reset custom lore
                ItemStackWrapper.fromItem(item.getItem(), false).setLore();
                openToPlayer(player);
                break;
            case RESET_ENCHANTMENTS_SLOT:
                //Reset enchantments
                ItemStackWrapper.fromItem(item.getItem(), false).clearEnchantments();
                openToPlayer(player);
                break;
            case DURABILITY_SLOT:
                //Reset durability
                ItemStackWrapper wrapper = ItemStackWrapper.fromItem(item.getItem(), false);
                wrapper.setDurability(0);
                openToPlayer(player);
                break;
                //<editor-fold defaultstate="collapsed" desc="Decrease durability slots">
            case DURABILITY_SLOT-1:
                //Durability-1
                wrapper = ItemStackWrapper.fromItem(item.getItem(), false);
                wrapper.setDurability(wrapper.getDurability() - 1);
                openToPlayer(player);
                break;
            case DURABILITY_SLOT-2:
                //Durability-10
                wrapper = ItemStackWrapper.fromItem(item.getItem(), false);
                wrapper.setDurability(wrapper.getDurability() - 10);
                openToPlayer(player);
                break;
            case DURABILITY_SLOT-3:
                //Durability-100
                wrapper = ItemStackWrapper.fromItem(item.getItem(), false);
                wrapper.setDurability(wrapper.getDurability() - 100);
                openToPlayer(player);
                break;
            case DURABILITY_SLOT-4:
                //Durability-1000
                wrapper = ItemStackWrapper.fromItem(item.getItem(), false);
                wrapper.setDurability(wrapper.getDurability() - 1000);
                openToPlayer(player);
                break;
                
            case DURABILITY_SLOT+1:
                //Durability+1
                wrapper = ItemStackWrapper.fromItem(item.getItem(), false);
                wrapper.setDurability(wrapper.getDurability() + 1);
                openToPlayer(player);
                break;
            case DURABILITY_SLOT+2:
                //Durability+10
                wrapper = ItemStackWrapper.fromItem(item.getItem(), false);
                wrapper.setDurability(wrapper.getDurability() + 10);
                openToPlayer(player);
                break;
            case DURABILITY_SLOT+3:
                //Durability+100
                wrapper = ItemStackWrapper.fromItem(item.getItem(), false);
                wrapper.setDurability(wrapper.getDurability() + 100);
                openToPlayer(player);
                break;
            case DURABILITY_SLOT+4:
                //Durability+1000
                wrapper = ItemStackWrapper.fromItem(item.getItem(), false);
                wrapper.setDurability(wrapper.getDurability() + 1000);
                openToPlayer(player);
                break;
                
            case POTION_COLOR_SLOT:
                //Open select potion color menu
                if(event.getCurrentItem() != null) {
                    if(event.getCurrentItem().getType() == Material.BLAZE_POWDER) {
                        if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_11)) {
                            Color itemColor = ItemStackWrapper.fromItem(item.getItem(), false).getColor();
                            ColorMenu colorMenu = new ColorMenu();
                            colorMenu.setOnBack(this::openToPlayer);
                            colorMenu.setOnNext((p, color) -> {
                                ItemStackWrapper.fromItem(item.getItem(), false).setColor(color.getBukkitColor());
                                openToPlayer(player);
                            });
                            
                            if(itemColor != null) {
                                colorMenu.setItemToEdit(new CustomColor(itemColor));
                            }
                            
                            colorMenu.openToPlayer(player);
                        }
                    }
                }
                break;
            case SPECIAL_ITEM_SLOT:
                //Special item menu
                if(event.getCurrentItem() != null) {
                    if(Texture.isSkull(event.getCurrentItem())) {
                        //Close menu
                        ChatListener.registerPlayer(player, message -> {
                            if(message.equals("null")) {
                                ItemStackWrapper.fromItem(item.getItem(), false).setTexture(null);
                            } else {
                                ItemStackWrapper.fromItem(item.getItem(), false).setTexture(Texture.of(message));
                            }
                            
                            openToPlayer(player);
                        }, false);
                        player.closeInventory();
                        Logger.sendMessage("&3Enter the texture ID (or null) and press ENTER", player);
                    } else switch(event.getCurrentItem().getType()) {
                        case ENCHANTED_BOOK:
                            // Open enchantment menu
                            enchantmentMenu = new EnchantmentMenu();
                            enchantmentMenu.setOnBack(this::openToPlayer);
                            enchantmentMenu.setOnNext((p, enchantmentWithLevel) -> {
                                ItemStackWrapper.fromItem(item.getItem(), false)
                                                .addBookEnchantment(enchantmentWithLevel.enchantment, enchantmentWithLevel.level);
                                openToPlayer(player);
                            });
                            enchantmentMenu.openToPlayer(player);
                            break;
                        case POTION:
                            //Open potion meta menu
                            EffectMenu effectMenu = new EffectMenu(false);
                            effectMenu.setOnBack(this::openToPlayer);
                            effectMenu.setOnNext((p, effectReward) -> {
                                PotionEffect potionEffect = new PotionEffect(
                                        effectReward.getPotionEffect().getPotionEffectType(), 
                                        effectReward.getDuration()*20, 
                                        effectReward.getAmplifier(),
                                        effectReward.isAmbient(),
                                        effectReward.isShowParticles());
                                ItemStackWrapper.fromItem(item.getItem(), false).addPotionEffect(potionEffect);
                                openToPlayer(player);
                            });
                            effectMenu.openToPlayer(player);
                            break;
                        case LEATHER:
                            //Open armor color menu
                            Color itemColor = ItemStackWrapper.fromItem(item.getItem(), false).getColor();
                            ColorMenu colorMenu = new ColorMenu();
                            colorMenu.setOnBack(this::openToPlayer);
                            colorMenu.setOnNext((p, color) -> {
                                ItemStackWrapper.fromItem(item.getItem(), false).setColor(color.getBukkitColor());
                                openToPlayer(player);
                            });
                            
                            if(itemColor != null) {
                                colorMenu.setItemToEdit(new CustomColor(itemColor));
                            }
                            
                            colorMenu.openToPlayer(player);
                            break;
                    }
                }
                break;
            case POTION_TYPE_SLOT:
                //Change potion type
                if(event.getCurrentItem() != null) {
                    XMaterial material = XMaterial.matchXMaterial(event.getCurrentItem());
                    if(material == XMaterial.BREWING_STAND) {
                        PotionSplashType type = PotionSplashType.getFromPotion(item.getItem());
                        type.next().setToPotion(item.getItem());
                        openToPlayer(player);
                    }
                }
                break;
            case REMOVE_SPECIAL_DATA_SLOT:
                //Delete special item meta
                if(event.getCurrentItem() != null) {
                    XMaterial material = XMaterial.matchXMaterial(item.getItem().getType());
                    if(Texture.isSkull(item.getItem())) {
                        ItemStackWrapper.fromItem(item.getItem(), false).setTexture(null);
                        openToPlayer(player);
                    } else switch(material) {
                        case ENCHANTED_BOOK:
                            // Remove all book enchantments
                            ItemStackWrapper.fromItem(item.getItem(), false)
                                    .clearBookEnchantments();
                            openToPlayer(player);
                            break;
                        case POTION:
                        case SPLASH_POTION:
                        case LINGERING_POTION:
                        case TIPPED_ARROW:
                        case SUSPICIOUS_STEW:
                            //Remove all effects
                            ItemStackWrapper.fromItem(item.getItem(), false)
                                            .clearPotionEffects();
                            
                            if(material.name().contains("POTION")) {
                                if(MinecraftVersion.CURRENT_VERSION.is_1_8()) {
                                    Potion potion = Potion.fromItemStack(item.getItem());
                                    potion.setType(PotionType.WATER);
                                    potion.apply(item.getItem());
                                } else {
                                    PotionMeta potionMeta = (PotionMeta) item.getItem().getItemMeta();
                                    potionMeta.setBasePotionData(new PotionData(PotionType.WATER));
                                    item.getItem().setItemMeta(potionMeta);
                                }

                                //Reset potion type to normal
                                PotionSplashType.clearPotionSplashType(item.getItem());
                            }
                            
                            openToPlayer(player);
                            break;
                        case LEATHER_HELMET:
                        case LEATHER_CHESTPLATE:
                        case LEATHER_LEGGINGS:
                        case LEATHER_BOOTS:
                            ItemStackWrapper.fromItem(item.getItem(), false).setColor(null);
                            openToPlayer(player);
                            break;
                    }
                }
                break;
            case SPAWN_MODE_SLOT:
                item.setSpawnMode(item.getSpawnMode().next());
                openToPlayer(player);
                break;
            case SPAWN_INV_SLOT_SLOT:
                item.setSpawnInvSlot(0);
                openToPlayer(player);
                break;
            case SPAWN_INV_SLOT_SLOT-1:
                int spawnInvSlot = item.getSpawnInvSlot() - 1;
                if(spawnInvSlot < 0) {
                    spawnInvSlot = ItemReward.getMaxSlot();
                }
                item.setSpawnInvSlot(spawnInvSlot);
                openToPlayer(player);
                break;
            case SPAWN_INV_SLOT_SLOT-2:
                spawnInvSlot = item.getSpawnInvSlot() - 10;
                if(spawnInvSlot < 0) {
                    spawnInvSlot = ItemReward.getMaxSlot();
                }
                item.setSpawnInvSlot(spawnInvSlot);
                openToPlayer(player);
                break;
            case SPAWN_INV_SLOT_SLOT+1:
                spawnInvSlot = item.getSpawnInvSlot() + 1;
                if(spawnInvSlot > ItemReward.getMaxSlot()) {
                    spawnInvSlot = 0;
                }
                item.setSpawnInvSlot(spawnInvSlot);
                openToPlayer(player);
                break;
            case SPAWN_INV_SLOT_SLOT+2:
                spawnInvSlot = item.getSpawnInvSlot() + 10;
                if(spawnInvSlot > ItemReward.getMaxSlot()) {
                    spawnInvSlot = 0;
                }
                item.setSpawnInvSlot(spawnInvSlot);
                openToPlayer(player);
                break;
            case OFFSET_SLOT:
                OffsetMenu offsetMenu = new OffsetMenu();
                offsetMenu.setItemToEdit(item.getOffset().clone());
                offsetMenu.setOnBack(this::openToPlayer);
                offsetMenu.setOnNext((p, offset) -> {
                    item.setOffset(offset);
                    openToPlayer(player);
                });
                offsetMenu.openToPlayer(player);
                break;
            case ITEM_FLAGS_SLOT:
                ItemFlagMenu itemFlagMenu = new ItemFlagMenu();
                itemFlagMenu.setItemToEdit(item.getItem());
                itemFlagMenu.setOnBack(this::openToPlayer);
                itemFlagMenu.setOnNext((p, __) -> openToPlayer(player));
                itemFlagMenu.openToPlayer(player);
                break;
            case ITEM_NBT_SLOT:
                ItemNbtMenu itemNbtMenu = new ItemNbtMenu();
                itemNbtMenu.setItemToEdit(item.getItem());
                itemNbtMenu.setOnBack(this::openToPlayer);
                itemNbtMenu.setOnNext((p, __) -> openToPlayer(player));
                itemNbtMenu.openToPlayer(player);
                break;
            case ITEM_UNBREAKABLE_SLOT:
                wrapper = ItemStackWrapper.fromItem(item.getItem(), false);
                wrapper.setUnbreakable(!wrapper.isUnbreakable());
                openToPlayer(player);
                break;
        }
    }
}
