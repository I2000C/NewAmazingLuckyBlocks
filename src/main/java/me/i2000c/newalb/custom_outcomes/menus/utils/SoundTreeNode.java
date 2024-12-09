package me.i2000c.newalb.custom_outcomes.menus.utils;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import java.util.ArrayList;
import java.util.Arrays;
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
    
    @Getter(lazy = true)
    private static final SoundTreeNode rootNode = buildSoundTree();
    
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
        
        for(XSound sound : XSound.values()) {
            SoundTreeNode currentNode = mainNode;
            
            String soundName = sound.name();
            String[] parts = soundName.split("\\.");
            for(int i=0; i<parts.length; i++) {
                String part = parts[i];
                if(i == parts.length-1) {
                    currentNode.children.putIfAbsent(part, new SoundTreeNode(currentNode.level + 1, part, currentNode, sound));
                } else {
                    currentNode.children.putIfAbsent(part, new SoundTreeNode(currentNode.level + 1, part, currentNode, null));
                }
                currentNode = currentNode.children.get(part);
            }
        }
        
        return mainNode;
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
                    case "block":
                        String materialName = key;
                        if(materialName.toUpperCase().contains("WATER")) {
                            material = XMaterial.WATER_BUCKET;
                        } else if(materialName.toUpperCase().contains("LAVA")) {
                            material = XMaterial.LAVA_BUCKET;
                        } else if(materialName.toUpperCase().contains("FIRE")) {
                            material = XMaterial.FIRE_CHARGE;
                        } else {
                            material = XMaterial.matchXMaterial(materialName).orElse(XMaterial.COMMAND_BLOCK);
                        }
                        
                        if(!XMaterialUtils.isItem(material)) {
                            material = XMaterial.COMMAND_BLOCK;
                        }
                        
                        break;
                    case "entity":
                        String entityName = key;
                        if(key.toUpperCase().contains("PLAYER")) {
                            material = XMaterial.PLAYER_HEAD;
                        } else if(key.toUpperCase().contains("FIREWORK")) {
                            material = XMaterial.FIREWORK_ROCKET;
                        } else if(key.toUpperCase().contains("FISHING")) {
                            material = XMaterial.FISHING_ROD;
                        } else if(key.toUpperCase().contains("DRAGON")) {
                            material = XMaterial.DRAGON_EGG;
                        } else if(key.toUpperCase().contains("MINECART")) {
                            material = XMaterial.MINECART;
                        } else if(key.toUpperCase().contains("TNT")) {
                            material = XMaterial.TNT;
                        } else if(key.toUpperCase().contains("EXPERIENCE")) {
                            material = XMaterial.EXPERIENCE_BOTTLE;
                        } else if(key.toUpperCase().contains("ARROW")) {
                            material = XMaterial.ARROW;
                        } else {
                            String entitySpawnEggName = entityName.toUpperCase() + "_SPAWN_EGG";
                            material = XMaterial.matchXMaterial(entitySpawnEggName).orElse(XMaterial.GHAST_SPAWN_EGG);
                        }
                        
                        if(!material.isSupported()) {
                            material = XMaterial.GHAST_SPAWN_EGG;
                        }
                        break;
                    case "music_disc":
                        String musicDiscName = key;
                        String musicDiscMaterialName = "MUSIC_DISC_" + musicDiscName.toUpperCase();
                        material = XMaterial.matchXMaterial(musicDiscMaterialName).orElse(XMaterial.MUSIC_DISC_FAR);
                        break;
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
