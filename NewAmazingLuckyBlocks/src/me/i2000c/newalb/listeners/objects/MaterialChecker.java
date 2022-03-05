package me.i2000c.newalb.listeners.objects;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.event.player.PlayerInteractEvent;


public class MaterialChecker {
    private static List<String> protectedMaterials = new ArrayList();
    
    private static void addProtectedMaterials(){
        protectedMaterials.add("CHEST");
        protectedMaterials.add("TRAPPED_CHEST");
        protectedMaterials.add("ENDER_CHEST");
        
        protectedMaterials.add("FURNACE");
        
        protectedMaterials.add("ENCHANTING_TABLE");
        
        protectedMaterials.add("ANVIL");
        protectedMaterials.add("CHIPPED_ANVIL");
        protectedMaterials.add("DAMAGED_ANVIL");
        
        protectedMaterials.add("WORKBENCH");
        protectedMaterials.add("CRAFTING_TABLE");
        
        protectedMaterials.add("PAINTING");
        protectedMaterials.add("SIGN");
        protectedMaterials.add("SIGN_POST");
        protectedMaterials.add("BED_BLOCK");
        protectedMaterials.add("ITEM_FRAME");
        
        protectedMaterials.add("JUKEBOX");
        protectedMaterials.add("NOTEBLOCK");
        
        protectedMaterials.add("BREWING_STAND");
        
        protectedMaterials.add("DISPENSER");
        protectedMaterials.add("LEVER");
        protectedMaterials.add("STONE_BUTTON");
        protectedMaterials.add("WOOD_BUTTON");
        protectedMaterials.add("OAK_BUTTON");
        protectedMaterials.add("SPRUCE_BUTTON");
        protectedMaterials.add("BIRCH_BUTTON");
        protectedMaterials.add("JUNGLE_BUTTON");
        protectedMaterials.add("ACACIA_BUTTON");
        protectedMaterials.add("DARK_OAK_BUTTON");
        protectedMaterials.add("CRIMSON_BUTTON");
        protectedMaterials.add("WARPED_BUTTON");
        protectedMaterials.add("POLISHED_BLACKSTONE_BUTTON");        
        
        protectedMaterials.add("DAYLIGHT_DETECTOR");
        protectedMaterials.add("DAYLIGHT_DETECTOR_INVERTED");
        protectedMaterials.add("FENCE_GATE");
        protectedMaterials.add("OAK_FENCE_GATE");
        protectedMaterials.add("SPRUCE_FENCE_GATE");
        protectedMaterials.add("BIRCH_FENCE_GATE");
        protectedMaterials.add("JUNGLE_FENCE_GATE");
        protectedMaterials.add("DARK_OAK_FENCE_GATE");
        protectedMaterials.add("ACACIA_FENCE_GATE");
        protectedMaterials.add("CRIMSON_FENCE_GATE");
        protectedMaterials.add("WARPED_FENCE_GATE");
        
        protectedMaterials.add("WOODEN_DOOR");
        protectedMaterials.add("OAK_DOOR");
        protectedMaterials.add("IRON_DOOR");
        protectedMaterials.add("SPRUCE_DOOR");
        protectedMaterials.add("BIRCH_DOOR");
        protectedMaterials.add("JUNGLE_DOOR");
        protectedMaterials.add("ACACIA_DOOR");
        protectedMaterials.add("DARK_OAK_DOOR");
        protectedMaterials.add("CRIMSON_DOOR");
        protectedMaterials.add("WARPED_DOOR");
        protectedMaterials.add("TRAPDOOR");
        protectedMaterials.add("IRON_TRAPDOOR");
        protectedMaterials.add("OAK_TRAPDOOR");
        protectedMaterials.add("WOODEN_TRAPDOOR");
        protectedMaterials.add("SPRUCE_TRAPDOOR");
        protectedMaterials.add("BIRCH_TRAPDOOR");
        protectedMaterials.add("JUNGLE_TRAPDOOR");
        protectedMaterials.add("ACACIA_TRAPDOOR");
        protectedMaterials.add("DARK_OAK_TRAPDOOR");
        protectedMaterials.add("CRIMSON_TRAPDOOR");
        protectedMaterials.add("WARPED_TRAPDOOR");
        protectedMaterials.add("HOPPER");
        protectedMaterials.add("DROPPER");
        
        protectedMaterials.add("REPEATER");
        protectedMaterials.add("DIODE_BLOCK_ON");
        protectedMaterials.add("DIODE_BLOCK_OFF");
        protectedMaterials.add("COMPARATOR");
        protectedMaterials.add("REDSTONE_COMPARATOR_ON");
        protectedMaterials.add("REDSTONE_COMPARATOR_OFF");
        
        protectedMaterials.add("COMMAND");
        protectedMaterials.add("COMMAND_CHAIN");
        protectedMaterials.add("COMMAND_REPEATING");
        protectedMaterials.add("COMMAND_MINECART");
        
        protectedMaterials.add("COMMAND_BLOCK");
    }
    
    public static boolean check(PlayerInteractEvent e){
        if(protectedMaterials.isEmpty()){
            addProtectedMaterials();
        }
        
        
        try{
            if(protectedMaterials.contains(e.getClickedBlock().getType().name())){
                return !e.getPlayer().isSneaking();
            }else{
                //System.out.println(e.getClickedBlock().getType().name());
                return false;
            }
        }catch(NullPointerException ex){
            return false;
        }
    }
}
