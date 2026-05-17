package me.i2000c.newalb.lucky_blocks.editors.menus.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.api.version.MinecraftVersion;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.lucky_blocks.editors.menus.EquipmentMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.OffsetMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.effect.EffectMenu;
import me.i2000c.newalb.lucky_blocks.rewards.types.EntityReward;
import me.i2000c.newalb.utils.misc.Equipment;
import me.i2000c.newalb.utils.misc.EquipmentSlot;
import me.i2000c.newalb.utils.misc.ExtendedEntityType;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.misc.OtherUtils;

public class EntityMenu extends EditorMenu<EntityReward> {
    
    private static final int[] VALUES = {-100, -10, -1, 0, +1, +10, +100};
    private static final int[] VALUES2 = {-10, -1, 0, +1, +10};
    
    private final List<Consumer<Integer>> entityPropertyList = new ArrayList<>();
    
    public EntityMenu() {
        super("&2&lEntity Reward", MenuSize.SIZE_6_ROWS, true);
        
        entityPropertyList.add(value -> {
            if(value == 0) {
                item.setHealth(-1);
            } else {
                int health = OtherUtils.clamp(item.getHealth() + value, -1, Integer.MAX_VALUE);
                item.setHealth(health);
            }
        });
        entityPropertyList.add(value -> {
            if(value == 0) {
                item.setSlimeSize(-1);
            } else {
                int slimeSize = OtherUtils.clamp(item.getSlimeSize() + value, -1, Integer.MAX_VALUE);
                item.setSlimeSize(slimeSize);
            }
        });
    }

    @Override
    protected void buildMenu(Player player) {
        addGlassBorder(GlassColor.MAGENTA);
        
        ExtendedEntityType entityType = item.getType();
        XMaterial material;
        if(entityType != null) {
            material = item.getType().getMaterial();
        } else {
            material = XMaterial.GHAST_SPAWN_EGG;
        }        
        ItemStackWrapper wrapper = ItemStackWrapper.newItem(material);        
        if(entityType == null) {
            wrapper.setDisplayName("&6Select entity type");
        } else {
            wrapper.setDisplayName("&6Entity type: &r" + item.getType());
            if(entityType.isAlive()) {
                wrapper.addLoreLine("&3Is living entity: &atrue");
            } else {
                wrapper.addLoreLine("&3Is living entity: &7false");
            }
            if(entityType.isAgeable()) {
                wrapper.addLoreLine("&3Is ageable entity: &atrue");
            } else {
                wrapper.addLoreLine("&3Is ageable entity: &7false");
            }
            if(entityType.isTameable()) {
                wrapper.addLoreLine("&3Is tameable entity: &atrue");
            } else {
                wrapper.addLoreLine("&3Is tameable entity: &7false");
            }
            if(entityType.isAngryable()) {
                wrapper.addLoreLine("&3Is angryable entity: &atrue");
            } else {
                wrapper.addLoreLine("&3Is angryable entity: &7false");
            }
        }
        ItemStack ent_type = wrapper.toItemStack();
        
        wrapper = ItemStackWrapper.newItem(XMaterial.NAME_TAG);
        if(item.getCustomName() == null) {
            wrapper.setDisplayName("&aSelect entity custom name (optional)");
        } else {
            wrapper.setDisplayName("&aEntity custom name: &r" + item.getCustomName());
        }
        wrapper.addLoreLine("");
        wrapper.addLoreLine("&7Use &a%player% &7if you want to use");
        wrapper.addLoreLine("&7  the player's name in the entity name");
        ItemStack ent_name = wrapper.toItemStack();
        
        ItemStack ent_name_visible = GUIItem.getBooleanItem(
                item.isCustomNameVisible(), 
                "&9Custom name visible", 
                XMaterial.CYAN_STAINED_GLASS_PANE, 
                XMaterial.GLASS_PANE);
        
        wrapper = ItemStackWrapper.newItem(XMaterial.POTION);
        wrapper.setDisplayName("&3Select entity effects (optional)");
        if(item.getType() == null) {
            wrapper.addLoreLine("&cYou must select an entity first");
        }else if(!item.getType().isAlive()) {
            wrapper.addLoreLine("&cYou cannot add effects to a non-living entity");
        } else {
            for(String effect : item.getEffects()) {
                wrapper.addLoreLine("  &d" + effect);
            }
        }
        ItemStack ent_effects = wrapper.toItemStack();
        
        wrapper = ItemStackWrapper.newItem(XMaterial.DIAMOND_CHESTPLATE);
        wrapper.setDisplayName("&eSelect entity equipment (optional)");
        if(item.getType() == null) {
            wrapper.addLoreLine("&cYou must select an entity first");
        } else if(!item.getType().isAlive()) {
            wrapper.addLoreLine("&cYou cannot add effects to a non-living entity");
        } else if(!item.getEquipment().isEmpty()) {
            Equipment equipment = item.getEquipment();
            for(EquipmentSlot equipmentSlot : EquipmentSlot.VALUES) {
                if(equipmentSlot == EquipmentSlot.ITEM_IN_OFF_HAND && MinecraftVersion.CURRENT_VERSION.is_1_8()) {
                    continue;
                }
                
                ItemStack stack = equipment.getItem(equipmentSlot);
                if(stack == null) {
                    wrapper.addLoreLine(String.format("    &6%s: &cnull", equipmentSlot.getConfigKey()));
                } else {
                    ItemStackWrapper wrapper2 = ItemStackWrapper.fromItem(stack, false);
                    String name = wrapper2.toString();
                    int amount = wrapper2.getAmount();
                    int dropChance = equipment.getDropChance(equipmentSlot);
                    wrapper.addLoreLine(String.format("    &6%s: &d%s x%d (%d %%)", equipmentSlot.getConfigKey(), name, amount, dropChance));
                }
            }
        }
        ItemStack ent_equipment = wrapper.toItemStack();
        
        ItemStack offsetStack = item.getOffset().getItemToDisplay();
        
        ItemStack usePlayerLocStack = GUIItem.getUsePlayerLocItem(item.isUsePlayerLoc());
        
        ItemStack resetName = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                              .setDisplayName("&cReset custom name")
                                              .toItemStack();
        
        ItemStack resetEffects = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                                 .setDisplayName("&cReset effects")
                                                 .toItemStack();
        
        ItemStack resetEquipment = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                                   .setDisplayName("&cReset equipment")
                                                   .toItemStack();
        
        if(item.isTamed()) {
            wrapper = ItemStackWrapper.newItem(XMaterial.LEAD);
            wrapper.setDisplayName("&eIs tamed: &atrue");
        } else {
            wrapper = ItemStackWrapper.newItem(XMaterial.ZOMBIE_HEAD);
            wrapper.setDisplayName("&eIs tamed: &cfalse");
        }
        wrapper.addLoreLine("&3Click to toggle");
        wrapper.addLoreLine("");
        wrapper.addLoreLine("&6Note that this is only used");
        wrapper.addLoreLine("&6  if the entity is tameable");
        ItemStack isTamedStack = wrapper.toItemStack();
        
        if(item.isAngry()) {
            wrapper = ItemStackWrapper.newItem(XMaterial.LAVA_BUCKET);
            wrapper.setDisplayName("&eIs angry: &atrue");
        } else {
            wrapper = ItemStackWrapper.newItem(XMaterial.WATER_BUCKET);
            wrapper.setDisplayName("&eIs angry: &cfalse");
        }
        wrapper.addLoreLine("&3Click to toggle");
        wrapper.addLoreLine("");
        wrapper.addLoreLine("&6Note that this is only used");
        wrapper.addLoreLine("&6  if the entity is angryable");
        ItemStack isAngryStack = wrapper.toItemStack();
        
        switch(item.getAge()) {
            case BABY:
                wrapper = ItemStackWrapper.newItem(XMaterial.LEATHER_HELMET);
                break;
            case ADULT:
                wrapper = ItemStackWrapper.newItem(XMaterial.IRON_HELMET);
                break;
            default: //case RANDOM
                wrapper = ItemStackWrapper.newItem(XMaterial.GOLDEN_HELMET);
                break;
        }
        wrapper.setDisplayName("&dCurrent age: &e" + item.getAge().name());
        wrapper.addLoreLine("&3Click to toggle");
        wrapper.addLoreLine("");
        wrapper.addLoreLine("&6Note that this is only used");
        wrapper.addLoreLine("&6  if the entity is ageable");
        ItemStack ageStack = wrapper.toItemStack();
        
        wrapper = ItemStackWrapper.newItem(XMaterial.APPLE);
        if(item.getHealth() >= 0) {
            wrapper.setDisplayName("&bCurrent health: &d" + item.getHealth());
        } else {
            wrapper.setDisplayName("&bCurrent health: &d&lDEFAULT");
        }
        wrapper.addLoreLine("&3Click to reset");
        ItemStack healthItem = wrapper.toItemStack();
        
        wrapper = ItemStackWrapper.newItem(XMaterial.SLIME_BLOCK);
        if(item.getSlimeSize() >= 0) {
            wrapper.setDisplayName("&aCurrent slime size: &d" + item.getSlimeSize());
        } else {
            wrapper.setDisplayName("&aCurrent slime size: &d&lDEFAULT");
        }
        wrapper.addLoreLine("&3Click to reset");
        wrapper.addLoreLine("");
        wrapper.addLoreLine("&cIf slime size is greater than 15,");
        wrapper.addLoreLine("&c  the generated slime will be so big");
        ItemStack slimeSizeItem = wrapper.toItemStack();
        
        setBackItem(10);
        setNextItem(16, e -> {
            if(item.getType() == null) {
                return;
            }
            
            if(!item.getType().isAlive()) {
                item.setEffects(new ArrayList<>());
                item.getEquipment().reset();
            }
            
            onNext(player, item);
        });
        
        setItem(11, ent_type, e -> {
            EntitySelectMenu menu = new EntitySelectMenu();
            menu.setItemToEdit(item.getType());
            menu.setOnBack(this::openToPlayer);
            menu.setOnNext((p, extendedEntityType) -> {
                item.setType(extendedEntityType);
                openToPlayer(player);
            });
            menu.openToPlayer(player);
        });
        setItem(12, ent_name, e -> {
            ChatListener.registerPlayer(player, message -> {
                item.setCustomName(message);
                openToPlayer(player);
            });
            player.closeInventory();
        });
        if(item.getCustomName() != null) {
            setItem(3, ent_name_visible, e -> {
                item.setCustomNameVisible(!item.isCustomNameVisible());
                openToPlayer(player);
            });
        }
        setItem(13, ent_effects, e -> {
            if(item.getType() == null || !item.getType().isAlive()) {
                return;
            }
            
            EffectMenu menu = new EffectMenu(false);
            menu.setOnBack(this::openToPlayer);
            menu.setOnNext((p, effectReward) -> {
                String effectName = effectReward.getPotionEffect().name();
                int effectDuration = effectReward.getDuration();
                int effectAmplifier = effectReward.getAmplifier();
                item.getEffects().add(effectName + ";" + effectDuration + ";" + effectAmplifier);
                openToPlayer(player);
            });
            menu.openToPlayer(player);
        });
        setItem(14, ent_equipment, e -> {
            if(item.getType() == null || !item.getType().isAlive()) {
                return;
            }
            
            EquipmentMenu menu = new EquipmentMenu();
            menu.setItemToEdit(item.getEquipment().clone());
            menu.setOnBack(this::openToPlayer);
            menu.setOnNext((p, equipment) -> {
                item.setEquipment(equipment);
                openToPlayer(player);
            });
            menu.openToPlayer(player);
        });
        setItem(15, offsetStack, e -> {
            OffsetMenu menu = new OffsetMenu();
            menu.setItemToEdit(item.getOffset());
            menu.setOnBack(this::openToPlayer);
            menu.setOnNext((p, offset) -> {
                item.setOffset(offset);
                openToPlayer(player);
            });
            menu.openToPlayer(player);
        });
        setItem(25, usePlayerLocStack, e -> {
            item.setUsePlayerLoc(!item.isUsePlayerLoc());
            openToPlayer(player);
        });
        
        if(entityType != null && entityType.isAgeable()) {
            setItem(20, ageStack, e -> {
                item.setAge(item.getAge().next());
                openToPlayer(player);
            });
        }
        if(entityType != null && entityType.isTameable()) {
            setItem(2, isTamedStack, e -> {
                item.setTamed(!item.isTamed());
                openToPlayer(player);
            });
        }
        if(entityType != null && entityType.isAngryable()) {
            setItem(19, isAngryStack, e -> {
                item.setAngry(!item.isAngry());
                openToPlayer(player);
            });
        }
        
        setItem(21, resetName, e -> {
            item.setCustomName(null);
            openToPlayer(player);
        });
        setItem(22, resetEffects, e -> {
            item.getEffects().clear();
            openToPlayer(player);
        });
        setItem(23, resetEquipment, e -> {
            item.getEquipment().reset();
            openToPlayer(player);
        });
        
        if(entityType != null) {
            ItemStack[] resetItems= {healthItem, slimeSizeItem};
            
            for(int row=0; row<entityPropertyList.size(); row++) {
                int slot = -1;
                int[] values = {};
                if(row == 0 && entityType.isAlive()) {
                    slot = 28;
                    values = VALUES;
                } else if(row == 1 && entityType.isSlime()) {
                    slot = 38;
                    values = VALUES2;
                }
                
                if(slot > 0) {
                    for(int value : values) {
                        ItemStack stack = value == 0 ? resetItems[row] : GUIItem.getPlusLessItem(value);
                        Consumer<Integer> propertyModifier = entityPropertyList.get(row);
                        setItem(slot++, stack, e -> {
                            propertyModifier.accept(value);
                            openToPlayer(e.getPlayer());
                        });
                    }
                }
            }
        }
    }
}
