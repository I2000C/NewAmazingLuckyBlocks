package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import java.io.File;
import java.io.FilenameFilter;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.utils.rewards.StructureReward;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils2.FilePicker;
import me.i2000c.newalb.utils2.FilePickerEvent;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class StructureMenu implements Listener{
    public static StructureReward reward;
    
    private static Player player;
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.STRUCTURE_MENU, STRUCTURE_MENU_FUNCTION);
        }
        
        reward = null;
        player = null;
    }
    
    public static void openStructureMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(reward == null){
            reward = new StructureReward(RewardListMenu.getCurrentOutcome());
        }
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.STRUCTURE_MENU, 27, "&3&lStructure Reward");
        GUIManager.setCurrentInventory(inv);
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.CYAN);
        
        for(int i=0;i<9;i++){
            inv.setItem(i, glass);
        }
        for(int i=18;i<27;i++){
            inv.setItem(i, glass);
        }
        inv.setItem(9, glass);
        inv.setItem(17, glass);
        
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.NAME_TAG);
        if(reward.getSchematicName() == null){
            builder.withDisplayName("&6Selected file: &cnull");
        }else{
            builder.withDisplayName("&6Selected file: &3" + reward.getSchematicName());
        }
        ItemStack pathItem = builder.build();
        
        ItemStack selectFromChat = ItemBuilder.newItem(XMaterial.OAK_SIGN)
                .withDisplayName("&6Select file from chat")
                .build();
        
        ItemStack selectFromMenu = ItemBuilder.newItem(XMaterial.CHEST)
                .withDisplayName("&6Select file from menu")
                .build();
        
        ItemStack fromPlayer = GUIItem.getUsePlayerLocItem(reward.isFromPlayer());
        
        ItemStack replaceBlocks = GUIItem.getBooleanItem(
                reward.isReplaceBlocks(), 
                "&3Replace existing blocks", 
                XMaterial.BRICKS, 
                XMaterial.BRICKS);
        
        ItemStack placeAirBlocks = GUIItem.getBooleanItem(
                reward.isPlaceAirBlocks(), 
                "&3Place air blocks", 
                XMaterial.GLASS, 
                XMaterial.GLASS);
        
        inv.setItem(10, GUIItem.getBackItem());
        inv.setItem(16, GUIItem.getNextItem());
        
        inv.setItem(12, fromPlayer);
        inv.setItem(13, replaceBlocks);
        inv.setItem(14, placeAirBlocks);
        
        inv.setItem(6, selectFromChat);
        inv.setItem(24, selectFromMenu);
        inv.setItem(15, pathItem);
        
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction STRUCTURE_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 12:
                    //Toggle fromPlayer
                    reward.setFromPlayer(!reward.isFromPlayer());
                    openStructureMenu(p);
                    break;
                case 13:
                    //Toggle replaceBlocks
                    reward.setReplaceBlocks(!reward.isReplaceBlocks());
                    openStructureMenu(p);
                    break;
                case 14:
                    //Toggle placeAirBlocks
                    reward.setPlaceAirBlocks(!reward.isPlaceAirBlocks());
                    openStructureMenu(p);
                    break;
                case 6:
                    //Select schematic file from chat
                    ChatListener.registerPlayer(p, message -> {
                        File file = new File(StructureReward.schematicsFolder, message);
                        if(!file.exists()){
                            Logger.sendMessage("&cFile &6\"" + message + "\" &cdoesn't exist", p);
                            return;
                        }
                        reward.setSchematicName(message);
                        openStructureMenu(p);
                    });
                    p.closeInventory();
                    break;
                case 24:
                    //Select schematic file from menu
                    File schematicsFolder = new File(NewAmazingLuckyBlocks.getInstance().getDataFolder(), "schematics");
                    schematicsFolder.mkdirs();
                    FilenameFilter filter = (File dir, String name) -> {
                        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                            return name.endsWith(".schematic");
                        }else{
                            return name.endsWith(".schematic") || name.endsWith(".schem");
                        }
                    };
                    FilePicker.resetFilePicker();
                    player = p;
                    FilePicker.openFileMenu(p, schematicsFolder.getParentFile(), schematicsFolder, filter);
                    break;
                case 10:
                    //Back
                    if(RewardListMenu.editMode){
                        RewardListMenu.openFinishInventory(p);
                    }else{
                        RewardTypesMenu.openRewardTypesMenu(p);
                    }
                    break;
                case 16:
                    //Next
                    if(reward.getSchematicName()!= null){
                        RewardListMenu.addReward(reward);
                        reset();
                        RewardListMenu.openFinishInventory(p);
                    }
                    break;
            }
        }
//</editor-fold>
    };
    
    @EventHandler
    private static void onFileSelected(FilePickerEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(e.getPlayer().equals(player)){
            reward.setSchematicName(e.getFilename());
            openStructureMenu(player);
        }
//</editor-fold>
    }
}
