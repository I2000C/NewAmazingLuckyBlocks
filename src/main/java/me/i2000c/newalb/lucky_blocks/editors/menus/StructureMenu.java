package me.i2000c.newalb.lucky_blocks.editors.menus;

import com.cryptomorin.xseries.XMaterial;

import java.io.File;
import java.io.FilenameFilter;

import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.api.functions.InventoryFunction;
import me.i2000c.newalb.api.gui.CustomInventoryType;
import me.i2000c.newalb.api.gui.GUIFactory;
import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.InventoryLocation;
import me.i2000c.newalb.api.gui.Menu;
import me.i2000c.newalb.api.version.MinecraftVersion;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.lucky_blocks.editors.Editor;
import me.i2000c.newalb.lucky_blocks.editors.EditorType;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.types.StructureReward;
import me.i2000c.newalb.utils.files.FilePicker;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StructureMenu extends Editor<StructureReward>{
    public StructureMenu(){
        InventoryListener.registerInventory(CustomInventoryType.STRUCTURE_MENU, STRUCTURE_MENU_FUNCTION);
    }
    
    private static final FilenameFilter FILENAME_FILTER = (File dir, String name) -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()){
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
        Menu menu = GUIFactory.newMenu(CustomInventoryType.STRUCTURE_MENU, 36, "&3&lStructure Reward");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.CYAN);
        
        for(int i=0;i<9;i++){
            menu.setItem(i, glass);
        }
        for(int i=27;i<36;i++){
            menu.setItem(i, glass);
        }
        menu.setItem(9, glass);
        menu.setItem(18, glass);
        menu.setItem(17, glass);
        menu.setItem(26, glass);
        
        ItemStackWrapper builder = ItemStackWrapper.newItem(XMaterial.NAME_TAG);
        if(item.getSchematicName() == null){
            builder.setDisplayName("&6Selected file: &cnull");
        }else{
            builder.setDisplayName("&6Selected file: &3" + item.getSchematicName());
        }
        ItemStack pathItem = builder.toItemStack();
        
        ItemStack selectFromChat = ItemStackWrapper.newItem(XMaterial.OAK_SIGN)
                                                   .setDisplayName("&6Select file from chat")
                                                   .toItemStack();
        
        ItemStack selectFromMenu = ItemStackWrapper.newItem(XMaterial.CHEST)
                                                   .setDisplayName("&6Select file from menu")
                                                   .toItemStack();
        
        ItemStack fromPlayer = GUIItem.getUsePlayerLocItem(item.isFromPlayer());
        
        ItemStack replaceBlocks = GUIItem.getBooleanItem(
                item.isReplaceBlocks(), 
                "&3Replace existing blocks", 
                XMaterial.BRICKS, 
                XMaterial.BRICKS,
                "&6Click to toggle");
        
        ItemStack placeAirBlocks = GUIItem.getBooleanItem(
                item.isPlaceAirBlocks(), 
                "&3Place air blocks", 
                XMaterial.GLASS, 
                XMaterial.GLASS,
                "&6Click to toggle");
        
        ItemStack centerPlayerLocation = GUIItem.getBooleanItem(
                item.isCenterPlayerLocation(), 
                "&3Center player location", 
                XMaterial.ARMOR_STAND, 
                XMaterial.ARMOR_STAND,
                "&6Click to toggle");
        
        ItemStack centerPlayerYaw = GUIItem.getBooleanItem(
                item.isCenterPlayerYaw(), 
                "&3Center player yaw", 
                XMaterial.GOLDEN_HORSE_ARMOR, 
                XMaterial.GOLDEN_HORSE_ARMOR,
                "&6Click to toggle");
        
        ItemStack centerPlayerPitch = GUIItem.getBooleanItem(
                item.isCenterPlayerPitch(), 
                "&3Center player pitch", 
                XMaterial.DIAMOND_HORSE_ARMOR, 
                XMaterial.DIAMOND_HORSE_ARMOR,
                "&6Click to toggle");
        
        ItemStack autorotate = GUIItem.getBooleanItem(
                item.isAutorotate(), 
                "&3Auto-rotate schematic towards player direction", 
                XMaterial.COMPASS, 
                XMaterial.GRANITE,
                "&6Click to toggle");
        
        ItemStack schematicAxis = ItemStackWrapper.newItem(XMaterial.REPEATER)
                                                  .setDisplayName("&3Schematic axis: &5" + item.getSchematicAxis() + " &c(" + item.getSchematicAxis().getRotation() + "º)")
                                                  .addLoreLine("&bThis option is used when auto-rotate is &aenabled")
                                                  .addLoreLine("")
                                                  .addLoreLine("&6Click to change")
                                                  .toItemStack();
        
        menu.setItem(9, GUIItem.getBackItem());
        menu.setItem(17, GUIItem.getNextItem());
        
        menu.setItem(10, fromPlayer);
        menu.setItem(11, replaceBlocks);
        menu.setItem(12, placeAirBlocks);
        
        menu.setItem(14, selectFromMenu);        
        menu.setItem(15, pathItem);
        menu.setItem(16, selectFromChat);
        
        menu.setItem(19, centerPlayerLocation);
        menu.setItem(20, centerPlayerYaw);
        menu.setItem(21, centerPlayerPitch);
        
        menu.setItem(23, autorotate);
        menu.setItem(24, schematicAxis);
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction STRUCTURE_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 10:
                    //Toggle fromPlayer
                    item.setFromPlayer(!item.isFromPlayer());
                    openStructureMenu(player);
                    break;
                case 11:
                    //Toggle replaceBlocks
                    item.setReplaceBlocks(!item.isReplaceBlocks());
                    openStructureMenu(player);
                    break;
                case 12:
                    //Toggle placeAirBlocks
                    item.setPlaceAirBlocks(!item.isPlaceAirBlocks());
                    openStructureMenu(player);
                    break;
                case 14:
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
                case 16:
                    //Select schematic file from chat
                    ChatListener.registerPlayer(player, message -> {
                        File file = new File(StructureReward.schematicsFolder, message);
                        if(!file.exists()){
                            Logger.sendMessage("&cFile &6\"" + message + "\" &cdoesn't exist", player);
                            Logger.sendMessage("&cUse &b/alb return &cif you want to return to the menu", player, false);
                            return;
                        }
                        
                        ChatListener.removePlayer(player);
                        item.setSchematicName(message);
                        openStructureMenu(player);
                    }, false);
                    player.closeInventory();
                    break;
                case 19:
                    // Toggle center player location
                    item.setCenterPlayerLocation(!item.isCenterPlayerLocation());
                    openStructureMenu(player);
                    break;
                case 20:
                    // Toggle center player yaw
                    item.setCenterPlayerYaw(!item.isCenterPlayerYaw());
                    openStructureMenu(player);
                    break;
                case 21:
                    // Toggle center player pitch
                    item.setCenterPlayerPitch(!item.isCenterPlayerPitch());
                    openStructureMenu(player);
                    break;
                case 23:
                    // Toggle auto-rotate
                    item.setAutorotate(!item.isAutorotate());
                    openStructureMenu(player);
                    break;
                case 24:
                    // Select schematic axis
                    item.setSchematicAxis(item.getSchematicAxis().next());
                    openStructureMenu(player);
                    break;
                case 9:
                    //Back
                    onBack.accept(player);
                    break;
                case 17:
                    //Next
                    if(item.getSchematicName() != null){
                        onNext.accept(player, item);
                    }
                    break;
            }
        }
//</editor-fold>
    };
}
