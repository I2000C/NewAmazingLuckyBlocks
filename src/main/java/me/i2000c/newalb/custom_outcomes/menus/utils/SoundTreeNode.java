package me.i2000c.newalb.custom_outcomes.menus.utils;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import me.i2000c.newalb.custom_outcomes.rewards.Displayable;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import me.i2000c.newalb.utils2.XMaterialUtils;
import org.bukkit.inventory.ItemStack;


@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SoundTreeNode implements Displayable {
    
    @Getter(lazy = true)
    private static final SoundTreeNode rootNode = buildSoundTree();
    
    private static final Map<String, XMaterial> SOUND_ITEM_MATERIALS = new HashMap<>();
    private static final Map<String, XMaterial> SOUND_BLOCK_MATERIALS = new HashMap<>();
    private static final Map<String, XMaterial> SOUND_ENTITY_MATERIALS = new HashMap<>();
    
    static {
        SOUND_ITEM_MATERIALS.put("FLINTANDSTEEL", XMaterial.FLINT_AND_STEEL);
        SOUND_ITEM_MATERIALS.put("LODESTONE_COMPASS", XMaterial.LODESTONE);
        SOUND_ITEM_MATERIALS.put("SHOVEL", XMaterial.IRON_SHOVEL);
        SOUND_ITEM_MATERIALS.put("HOE", XMaterial.IRON_HOE);
        SOUND_ITEM_MATERIALS.put("AXE", XMaterial.IRON_AXE);
        SOUND_ITEM_MATERIALS.put("ARMOR", XMaterial.IRON_CHESTPLATE);
        SOUND_ITEM_MATERIALS.put("BOTTLE", XMaterial.GLASS_BOTTLE);
        SOUND_ITEM_MATERIALS.put("FIRECHARGE", XMaterial.FIRE_CHARGE);
        SOUND_ITEM_MATERIALS.put("CROP", XMaterial.WHEAT_SEEDS);
        
        SOUND_BLOCK_MATERIALS.put("WATER", XMaterial.WATER_BUCKET);
        SOUND_BLOCK_MATERIALS.put("LAVA", XMaterial.LAVA_BUCKET);
        SOUND_BLOCK_MATERIALS.put("FIRE", XMaterial.FIRE_CHARGE);
        
        SOUND_ENTITY_MATERIALS.put("PLAYER", XMaterial.PLAYER_HEAD);
        SOUND_ENTITY_MATERIALS.put("FIREWORK", XMaterial.FIREWORK_ROCKET);
        SOUND_ENTITY_MATERIALS.put("FISHING", XMaterial.FISHING_ROD);
        SOUND_ENTITY_MATERIALS.put("DRAGON", XMaterial.DRAGON_EGG);
        SOUND_ENTITY_MATERIALS.put("EXPERIENCE", XMaterial.EXPERIENCE_BOTTLE);
        SOUND_ENTITY_MATERIALS.put("BOAT", XMaterial.OAK_BOAT);
        SOUND_ENTITY_MATERIALS.put("FISH", XMaterial.COD);
        SOUND_ENTITY_MATERIALS.put("ITEM", XMaterial.IRON_PICKAXE);
    }
    
    private static XMaterial getSoundItemMaterial(String soundName) {
        String uppercaseSoundName = soundName.toUpperCase();
        
        XMaterial material = XMaterial.matchXMaterial(uppercaseSoundName).orElse(null);
        if(material == null || !XMaterialUtils.isItem(material)) {
            String key = SOUND_ITEM_MATERIALS.keySet().stream()
                                                       .filter(uppercaseSoundName::contains)
                                                       .findFirst().orElse(null);
            material = SOUND_ITEM_MATERIALS.get(key);
        }
        
        if(material == null || !XMaterialUtils.isItem(material)) {
            material = XMaterial.IRON_INGOT;
        }
        
        return material;
    }
    
    private static XMaterial getSoundBlockMaterial(String soundName) {
        String uppercaseSoundName = soundName.toUpperCase();
        
        XMaterial material = XMaterial.matchXMaterial(uppercaseSoundName).orElse(null);
        if(material == null || !XMaterialUtils.isItem(material)) {
            String key = SOUND_BLOCK_MATERIALS.keySet().stream()
                                                       .filter(uppercaseSoundName::contains)
                                                       .findFirst().orElse(null);
            material = SOUND_BLOCK_MATERIALS.get(key);
        }
        
        if(material == null || !XMaterialUtils.isItem(material)) {
            material = XMaterial.COMMAND_BLOCK;
        }
        
        return material;
    }
    
    private static XMaterial getSoundEntityMaterial(String soundName) {
        String uppercaseSoundName = soundName.toUpperCase();
        
        String entitySpawnEggName = uppercaseSoundName + "_SPAWN_EGG";
        XMaterial material = XMaterial.matchXMaterial(entitySpawnEggName).orElse(null);
        if(material == null) {
            material = XMaterial.matchXMaterial(uppercaseSoundName).orElse(null);
        }
        
        if(material == null) {
            String key = SOUND_ENTITY_MATERIALS.keySet().stream()
                                               .filter(uppercaseSoundName::contains)
                                               .findFirst().orElse(null);
            material = SOUND_ENTITY_MATERIALS.get(key);
        }
        
        if(material == null || !material.isSupported()) {
            material = XMaterial.GHAST_SPAWN_EGG;
        }
        
        return material;
    }
    
    public static SoundTreeNode getNode(XSound sound) {
        String soundName = sound.name();
        String[] parts = soundName.split("\\.");
        int i = 0;
        
        SoundTreeNode currentNode = getRootNode();
        while(!currentNode.isLeafNode()) {
            currentNode = currentNode.children.get(parts[i++]);
        }
        
        return currentNode;
    }
    
    private static SoundTreeNode buildSoundTree() {
        SoundTreeNode mainNode = new SoundTreeNode(0, null, null, null);
        List<XSound> sounds = new ArrayList<>(Arrays.asList(XSound.values()));
        sounds.remove(XSound.INTENTIONALLY_EMPTY);
        sounds.sort((sound1, sound2) -> sound1.name().compareTo(sound2.name()));
        
        for(XSound sound : sounds) {
            SoundTreeNode currentNode = mainNode;
            
            String soundName = sound.name();
            String[] parts = soundName.split("\\.");
            for(int i=0; i<parts.length; i++) {
                String part = parts[i];
                XSound nodeSound = (i == parts.length-1) ? sound : null;
                currentNode.children.putIfAbsent(part, new SoundTreeNode(currentNode.level + 1, part, currentNode, nodeSound));
                currentNode = currentNode.children.get(part);
            }
        }
        
        return mainNode;
    }
    
    
    
    private final int level;
    private final String key;
    private final SoundTreeNode parentNode;
    private final XSound sound;
    private final Map<String, SoundTreeNode> children = new TreeMap<>();
    
    public boolean isRootNode() {
        return key == null;
    }
    
    public boolean isLeafNode() {
        return children.isEmpty();
    }
    
    
    public boolean isParentNode(SoundTreeNode node) {
        SoundTreeNode currentNode = this;
        while(!currentNode.isRootNode()) {
            if(currentNode == node) {
                return true;
            }            
            currentNode = currentNode.parentNode;
        }
        
        return false;
    }
    
    @Override
    public ItemStack getItemToDisplay() {
        if(isLeafNode()) {
            return ItemStackWrapper.newItem(XMaterial.NOTE_BLOCK)
                    .setDisplayName("&6" + this.key)
                    .toItemStack();
        }
        
        XMaterial material;
        switch(level) {
            case 1:
                // Main sound category
                switch(key) {
                    case "ambient": material = XMaterial.TORCH; break;
                    case "block": material = XMaterial.STONE; break;
                    case "enchant": material = XMaterial.ENCHANTING_TABLE; break;
                    case "entity": material = XMaterial.GHAST_SPAWN_EGG; break;
                    case "event": material = XMaterial.PAPER; break;
                    case "item": material = XMaterial.IRON_INGOT; break;
                    case "music": material = XMaterial.JUKEBOX; break;
                    case "music_disc": material = XMaterial.MUSIC_DISC_FAR; break;
                    case "particle": material = XMaterial.MELON_SEEDS; break;
                    case "ui": material = XMaterial.ITEM_FRAME; break;
                    case "weather": material = XMaterial.WATER_BUCKET; break;
                    default: material = XMaterial.NETHER_STAR; break;
                }
                break;
            case 2:
                // Sound subcategory
                String parentKey = parentNode.key;
                switch(parentKey) {
                    case "item": material = getSoundItemMaterial(key); break;
                    case "block": material = getSoundBlockMaterial(key); break;
                    case "entity": material = getSoundEntityMaterial(key); break;
                    default: material = ItemStackWrapper.fromItem(parentNode.getItemToDisplay(), false).getMaterial();
                }
                break;
            default:
                // Other sound subsubcategories
                material = ItemStackWrapper.fromItem(parentNode.getItemToDisplay(), false).getMaterial();
        }
        
        return ItemStackWrapper.newItem(material)
                               .setDisplayName("&6" + this.key)
                               .toItemStack();
    }
}
