package me.i2000c.newalb.listeners.interact;

import com.cryptomorin.xseries.XMaterial;
import java.util.EnumSet;
import org.bukkit.event.player.PlayerInteractEvent;


public class MaterialChecker{
    private static final EnumSet<XMaterial> PROTECTED_MATERIALS = EnumSet.noneOf(XMaterial.class);
    
    static {
        PROTECTED_MATERIALS.add(XMaterial.CHEST);
        PROTECTED_MATERIALS.add(XMaterial.TRAPPED_CHEST);
        PROTECTED_MATERIALS.add(XMaterial.ENDER_CHEST);
        
        PROTECTED_MATERIALS.add(XMaterial.FURNACE);
        
        PROTECTED_MATERIALS.add(XMaterial.ENCHANTING_TABLE);
        
        PROTECTED_MATERIALS.add(XMaterial.ANVIL);
        PROTECTED_MATERIALS.add(XMaterial.CHIPPED_ANVIL);
        PROTECTED_MATERIALS.add(XMaterial.DAMAGED_ANVIL);
        
        PROTECTED_MATERIALS.add(XMaterial.CRAFTING_TABLE);
        
        PROTECTED_MATERIALS.add(XMaterial.PAINTING);
        PROTECTED_MATERIALS.add(XMaterial.OAK_SIGN);
        PROTECTED_MATERIALS.add(XMaterial.RED_BED);
        PROTECTED_MATERIALS.add(XMaterial.ITEM_FRAME);
        
        PROTECTED_MATERIALS.add(XMaterial.JUKEBOX);
        PROTECTED_MATERIALS.add(XMaterial.NOTE_BLOCK);
        
        PROTECTED_MATERIALS.add(XMaterial.BREWING_STAND);
        
        PROTECTED_MATERIALS.add(XMaterial.DISPENSER);
        PROTECTED_MATERIALS.add(XMaterial.LEVER);
        PROTECTED_MATERIALS.add(XMaterial.STONE_BUTTON);
        PROTECTED_MATERIALS.add(XMaterial.OAK_BUTTON);
        PROTECTED_MATERIALS.add(XMaterial.SPRUCE_BUTTON);
        PROTECTED_MATERIALS.add(XMaterial.BIRCH_BUTTON);
        PROTECTED_MATERIALS.add(XMaterial.JUNGLE_BUTTON);
        PROTECTED_MATERIALS.add(XMaterial.ACACIA_BUTTON);
        PROTECTED_MATERIALS.add(XMaterial.DARK_OAK_BUTTON);
        PROTECTED_MATERIALS.add(XMaterial.CRIMSON_BUTTON);
        PROTECTED_MATERIALS.add(XMaterial.WARPED_BUTTON);
        PROTECTED_MATERIALS.add(XMaterial.POLISHED_BLACKSTONE_BUTTON);
        
        PROTECTED_MATERIALS.add(XMaterial.DAYLIGHT_DETECTOR);
        PROTECTED_MATERIALS.add(XMaterial.OAK_FENCE_GATE);
        PROTECTED_MATERIALS.add(XMaterial.SPRUCE_FENCE_GATE);
        PROTECTED_MATERIALS.add(XMaterial.BIRCH_FENCE_GATE);
        PROTECTED_MATERIALS.add(XMaterial.JUNGLE_FENCE_GATE);
        PROTECTED_MATERIALS.add(XMaterial.DARK_OAK_FENCE_GATE);
        PROTECTED_MATERIALS.add(XMaterial.ACACIA_FENCE_GATE);
        PROTECTED_MATERIALS.add(XMaterial.CRIMSON_FENCE_GATE);
        PROTECTED_MATERIALS.add(XMaterial.WARPED_FENCE_GATE);
        
        PROTECTED_MATERIALS.add(XMaterial.OAK_DOOR);
        PROTECTED_MATERIALS.add(XMaterial.IRON_DOOR);
        PROTECTED_MATERIALS.add(XMaterial.SPRUCE_DOOR);
        PROTECTED_MATERIALS.add(XMaterial.BIRCH_DOOR);
        PROTECTED_MATERIALS.add(XMaterial.JUNGLE_DOOR);
        PROTECTED_MATERIALS.add(XMaterial.ACACIA_DOOR);
        PROTECTED_MATERIALS.add(XMaterial.DARK_OAK_DOOR);
        PROTECTED_MATERIALS.add(XMaterial.CRIMSON_DOOR);
        PROTECTED_MATERIALS.add(XMaterial.WARPED_DOOR);
        PROTECTED_MATERIALS.add(XMaterial.IRON_TRAPDOOR);
        PROTECTED_MATERIALS.add(XMaterial.OAK_TRAPDOOR);
        PROTECTED_MATERIALS.add(XMaterial.SPRUCE_TRAPDOOR);
        PROTECTED_MATERIALS.add(XMaterial.BIRCH_TRAPDOOR);
        PROTECTED_MATERIALS.add(XMaterial.JUNGLE_TRAPDOOR);
        PROTECTED_MATERIALS.add(XMaterial.ACACIA_TRAPDOOR);
        PROTECTED_MATERIALS.add(XMaterial.DARK_OAK_TRAPDOOR);
        PROTECTED_MATERIALS.add(XMaterial.CRIMSON_TRAPDOOR);
        PROTECTED_MATERIALS.add(XMaterial.WARPED_TRAPDOOR);
        PROTECTED_MATERIALS.add(XMaterial.HOPPER);
        PROTECTED_MATERIALS.add(XMaterial.DROPPER);
        
        PROTECTED_MATERIALS.add(XMaterial.REPEATER);
        PROTECTED_MATERIALS.add(XMaterial.COMPARATOR);
        
        PROTECTED_MATERIALS.add(XMaterial.COMMAND_BLOCK);
        PROTECTED_MATERIALS.add(XMaterial.COMMAND_BLOCK_MINECART);
    }
    
    public static boolean check(PlayerInteractEvent e){
        if(e.getClickedBlock() == null){
            return false;
        }
        
        XMaterial material = XMaterial.matchXMaterial(e.getClickedBlock().getType());
        return PROTECTED_MATERIALS.contains(material) && !e.getPlayer().isSneaking();
    }
}
