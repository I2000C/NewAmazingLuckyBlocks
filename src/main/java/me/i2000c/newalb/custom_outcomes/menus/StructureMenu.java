package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import java.io.File;
import java.io.FilenameFilter;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.editor.EditorType;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.StructureReward;
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
import me.i2000c.newalb.utils2.FilePicker;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StructureMenu extends Editor<StructureReward>{
    public StructureMenu(){
        InventoryListener.registerInventory(CustomInventoryType.STRUCTURE_MENU, STRUCTURE_MENU_FUNCTION);
    }
    
    private static final FilenameFilter FILENAME_FILTER = (File dir, String name) -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
            return name.endsWith(".schematic");
        }else{
            return name.endsWith(".schematic") || name.endsWith(".schem");
        }
//</editor-fold>
    };
    
    @Override
    protected void newItem(Player player){
        Outcome outcome = RewardListMenu.getCurrentOutcome();
        item = new StructureReward(outcome);
        openStructureMenu(player);
    }
    
    @Override
    protected void editItem(Player player){
        openStructureMenu(player);
    }    
    
    private void openStructureMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.STRUCTURE_MENU, 27, "&3&lStructure Reward");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.CYAN);
        
        for(int i=0;i<9;i++){
            menu.setItem(i, glass);
        }
        for(int i=18;i<27;i++){
            menu.setItem(i, glass);
        }
        menu.setItem(9, glass);
        menu.setItem(17, glass);
        
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.NAME_TAG);
        if(item.getSchematicName() == null){
            builder.withDisplayName("&6Selected file: &cnull");
        }else{
            builder.withDisplayName("&6Selected file: &3" + item.getSchematicName());
        }
        ItemStack pathItem = builder.build();
        
        ItemStack selectFromChat = ItemBuilder.newItem(XMaterial.OAK_SIGN)
                .withDisplayName("&6Select file from chat")
                .build();
        
        ItemStack selectFromMenu = ItemBuilder.newItem(XMaterial.CHEST)
                .withDisplayName("&6Select file from menu")
                .build();
        
        ItemStack fromPlayer = GUIItem.getUsePlayerLocItem(item.isFromPlayer());
        
        ItemStack replaceBlocks = GUIItem.getBooleanItem(
                item.isReplaceBlocks(), 
                "&3Replace existing blocks", 
                XMaterial.BRICKS, 
                XMaterial.BRICKS);
        
        ItemStack placeAirBlocks = GUIItem.getBooleanItem(
                item.isPlaceAirBlocks(), 
                "&3Place air blocks", 
                XMaterial.GLASS, 
                XMaterial.GLASS);
        
        menu.setItem(10, GUIItem.getBackItem());
        menu.setItem(16, GUIItem.getNextItem());
        
        menu.setItem(12, fromPlayer);
        menu.setItem(13, replaceBlocks);
        menu.setItem(14, placeAirBlocks);
        
        menu.setItem(6, selectFromMenu);
        menu.setItem(24, selectFromChat);        
        menu.setItem(15, pathItem);
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction STRUCTURE_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 12:
                    //Toggle fromPlayer
                    item.setFromPlayer(!item.isFromPlayer());
                    openStructureMenu(player);
                    break;
                case 13:
                    //Toggle replaceBlocks
                    item.setReplaceBlocks(!item.isReplaceBlocks());
                    openStructureMenu(player);
                    break;
                case 14:
                    //Toggle placeAirBlocks
                    item.setPlaceAirBlocks(!item.isPlaceAirBlocks());
                    openStructureMenu(player);
                    break;
                case 6:
                    //Select schematic file from menu
                    File schematicsFolder = new File(NewAmazingLuckyBlocks.getInstance().getDataFolder(), "schematics");
                    schematicsFolder.mkdirs();
                    
                    Editor<File> editor = EditorType.FILE_SELECTOR.getEditor();
                    FilePicker.setFilenameFilter(FILENAME_FILTER);
                    FilePicker.setCurrentDirectory(schematicsFolder);
                    FilePicker.setRootDirectory(schematicsFolder.getParentFile());
                    editor.createNewItem(
                            player, 
                            p -> openStructureMenu(p), 
                            (p, file) -> {
                                item.setSchematicName(file.getName());
                                openStructureMenu(p);
                            });
                    break;
                case 24:
                    //Select schematic file from chat
                    ChatListener.registerPlayer(player, message -> {
                        File file = new File(StructureReward.schematicsFolder, message);
                        if(!file.exists()){
                            Logger.sendMessage("&cFile &6\"" + message + "\" &cdoesn't exist", player);
                            return;
                        }
                        item.setSchematicName(message);
                        openStructureMenu(player);
                    });
                    player.closeInventory();
                    break;
                case 10:
                    //Back
                    onBack.accept(player);
                    break;
                case 16:
                    //Next
                    if(item.getSchematicName()!= null){
                        onNext.accept(player, item);
                    }
                    break;
            }
        }
//</editor-fold>
    };
}
