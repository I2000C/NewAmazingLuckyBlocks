package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.utils.rewards.StructureReward;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.FilePickerEvent;
import java.io.File;
import java.io.FilenameFilter;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.custom_outcomes.utils.TypeManager;
import me.i2000c.newalb.utils2.FilePicker;
import me.i2000c.newalb.utils2.TextureManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
            reward = new StructureReward(FinishMenu.getCurrentOutcome());
        }
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.STRUCTURE_MENU, 27, "&3&lStructure Reward");
        GUIManager.setCurrentInventory(inv);
        
        ItemStack glass = XMaterial.CYAN_STAINED_GLASS_PANE.parseItem();
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        
        for(int i=0;i<9;i++){
            inv.setItem(i, glass);
        }
        for(int i=18;i<27;i++){
            inv.setItem(i, glass);
        }
        inv.setItem(9, glass);
        inv.setItem(17, glass);
        
        ItemStack pathItem = new ItemStack(Material.NAME_TAG);
        meta = pathItem.getItemMeta();
        if(reward.getSchematicName() == null){
            meta.setDisplayName("&6Selected file: &cnull");
        }else{
            meta.setDisplayName("&6Selected file: &3" + reward.getSchematicName());
        }
        pathItem.setItemMeta(meta);
        
        ItemStack selectFromChat = new ItemStack(Material.SIGN);
        meta = selectFromChat.getItemMeta();
        meta.setDisplayName("&6Select file from chat");
        selectFromChat.setItemMeta(meta);
        
        ItemStack selectFromMenu = new ItemStack(Material.CHEST);
        meta = selectFromMenu.getItemMeta();
        meta.setDisplayName("&6Select file from menu");
        selectFromMenu.setItemMeta(meta);
        
        ItemStack fromPlayer;
        if(reward.isFromPlayer()){
            fromPlayer = TextureManager.getItemSkullStack();
        }else{
            fromPlayer = TypeManager.getMenuItemStack();
        }
        meta = fromPlayer.getItemMeta();
        if(reward.isFromPlayer()){
            meta.setDisplayName("&3Source: &2Player location");
        }else{
            meta.setDisplayName("&3Source: &eLuckyBlock location");
        }
        meta.setLore(null);
        fromPlayer.setItemMeta(meta);
        
        ItemStack replaceBlocks = XMaterial.BRICKS.parseItem();
        meta = replaceBlocks.getItemMeta();
        if(reward.isReplaceBlocks()){
            meta.setDisplayName("&3Replace existing blocks: &atrue");
        }else{
            meta.setDisplayName("&3Replace existing blocks: &7false");
        }
        replaceBlocks.setItemMeta(meta);
        
        ItemStack placeAirBlocks = new ItemStack(Material.GLASS);
        meta = placeAirBlocks.getItemMeta();
        if(reward.isPlaceAirBlocks()){
            meta.setDisplayName("&3Place air blocks: &atrue");
        }else{
            meta.setDisplayName("&3Place air blocks: &7false");
        }
        placeAirBlocks.setItemMeta(meta);
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName("&7Back");
        back.setItemMeta(meta);
        
        ItemStack next = new ItemStack(Material.ANVIL);
        meta = next.getItemMeta();
        meta.setDisplayName("&bNext");
        next.setItemMeta(meta);
        
        inv.setItem(10, back);
        inv.setItem(16, next);
        
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
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
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
                    if(FinishMenu.editMode){
                        FinishMenu.openFinishInventory(p);
                    }else{
                        RewardTypesMenu.openRewardTypesMenu(p);
                    }
                    break;
                case 16:
                    //Next
                    if(reward.getSchematicName()!= null){
                        FinishMenu.addReward(reward);
                        reset();
                        FinishMenu.openFinishInventory(p);
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
