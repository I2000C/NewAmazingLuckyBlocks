package me.i2000c.newalb.lucky_blocks.editors.menus;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.api.version.MinecraftVersion;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.lucky_blocks.rewards.types.StructureReward;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.menus.FilePickerMenu;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class StructureMenu extends EditorMenu<StructureReward> {
    
    public StructureMenu() {
        super("&3&lStructure Reward", MenuSize.SIZE_4_ROWS, true);
    }
    
    private static final Predicate<Path> FILENAME_FILTER = path -> {
        String name = path.getFileName().toString();
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()) {
            return name.endsWith(".schematic");
        } else {
            return name.endsWith(".schematic") || name.endsWith(".schem");
        }
    };
    
    @Override
    protected void buildMenu(Player player) {
        addGlassBorder(GlassColor.CYAN);
        
        ItemStackWrapper builder = ItemStackWrapper.newItem(XMaterial.NAME_TAG);
        if(item.getSchematicName() == null) {
            builder.setDisplayName("&6Selected file: &cnull");
        } else {
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
        
        setBackItem(9);
        setNextItem(17, e -> {
            if(item.getSchematicName() != null) {
                onNext(player, item);
            }
        });
        
        setItem(10, fromPlayer, e -> {
            item.setFromPlayer(!item.isFromPlayer());
            openToPlayer(player);
        });
        setItem(11, replaceBlocks, e -> {
            item.setReplaceBlocks(!item.isReplaceBlocks());
            openToPlayer(player);
        });
        setItem(12, placeAirBlocks, e -> {
            item.setPlaceAirBlocks(!item.isPlaceAirBlocks());
            openToPlayer(player);
        });
        
        setItem(14, selectFromMenu, e -> {
            FilePickerMenu menu = new FilePickerMenu();
            menu.setFilenameFilter(FILENAME_FILTER);
            menu.setRootPath(StructureReward.SCHEMATICS_FOLDER);
            menu.setOnBack(this::openToPlayer);
            menu.setOnNext((p, path) -> {
                item.setSchematicName(path.getFileName().toString());
                openToPlayer(player);
            });
            menu.openToPlayer(player);
        });        
        
        setItem(15, pathItem);
        setItem(16, selectFromChat, e -> {
            player.closeInventory();
            ChatListener.registerPlayer(player, message -> {
                Path path = StructureReward.SCHEMATICS_FOLDER.resolve(message);
                if(!Files.exists(path)) {
                    Logger.sendMessage("&cFile &6\"" + message + "\" &cdoesn't exist", player);
                    Logger.sendMessage("&cUse &b/alb return &cif you want to return to the menu", player, false);
                    return;
                }
                
                ChatListener.removePlayer(player);
                item.setSchematicName(message);
                openToPlayer(player);
            }, false);
        });
        
        setItem(19, centerPlayerLocation, e -> {
            item.setCenterPlayerLocation(!item.isCenterPlayerLocation());
            openToPlayer(player);
        });
        setItem(20, centerPlayerYaw, e -> {
            item.setCenterPlayerYaw(!item.isCenterPlayerYaw());
            openToPlayer(player);
        });
        setItem(21, centerPlayerPitch, e -> {
            item.setCenterPlayerPitch(!item.isCenterPlayerPitch());
            openToPlayer(player);
        });
        
        setItem(23, autorotate, e -> {
            item.setAutorotate(!item.isAutorotate());
            openToPlayer(player);
        });
        setItem(24, schematicAxis, e -> {
            item.setSchematicAxis(item.getSchematicAxis().next());
            openToPlayer(player);
        });
    }
}
