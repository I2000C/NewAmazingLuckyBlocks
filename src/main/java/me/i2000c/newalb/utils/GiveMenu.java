package me.i2000c.newalb.utils;

import me.i2000c.newalb.utils.logger.Logger;
import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.custom_outcomes.utils.LuckyBlockType;
import me.i2000c.newalb.custom_outcomes.utils.TypeManager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GiveMenu{
    public static Player target = null;
    private static ItemStack playerItem;
    
    private static int inventoryPage = 1;
    private static int maxPages = 1;
    
    private static int amount = 1;
    
    public static void reset(){
        target = null;
        playerItem = null;
    }
    
    public static void openGiveMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(target == null){
            target = p;
        }
        
        if(playerItem == null){
            playerItem = XMaterial.PLAYER_HEAD.parseItem();
            SkullMeta sk = (SkullMeta) playerItem.getItemMeta();
            sk.setDisplayName("&2Player Selected:");
            sk.setLore(Arrays.asList("&b" + target.getName()));
            sk.setOwner(target.getName());
            playerItem.setItemMeta(sk);
        }
        
        ItemStack wands = XMaterial.MUSIC_DISC_FAR.parseItem();
        ItemMeta meta = wands.getItemMeta();
        meta.setDisplayName("&aGive Wands");
        wands.setItemMeta(meta);
        
        ItemStack objects = new ItemStack(Material.BUCKET);
        meta = objects.getItemMeta();
        meta.setDisplayName("&bGive Objects");
        objects.setItemMeta(meta);
        
        ItemStack luckyBlocks = TypeManager.getMenuItemStack();
        meta = luckyBlocks.getItemMeta();
        meta.setDisplayName("&6Give LuckyBlocks");
        meta.setLore(null);
        luckyBlocks.setItemMeta(meta);
        
        ItemStack luckyTool = SpecialItemManager.getLuckyTool().getItem();
        meta = luckyTool.getItemMeta();
        meta.setDisplayName("&eGive the LuckyTool");
        meta.setLore(null);
        luckyTool.setItemMeta(meta);
        
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.GIVE_MENU, 9, "&d&lGive Menu");
        InventoryListener.registerInventory(CustomInventoryType.GIVE_MENU, GIVE_MENU_FUNCTION);
        
        inv.setItem(0, wands);
        inv.setItem(1, objects);
        inv.setItem(2, luckyBlocks);
        inv.setItem(3, luckyTool);
        inv.setItem(8, playerItem);
        
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction GIVE_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            if(target == null){
                target = p;
            }
            amount = 1;
            switch(e.getSlot()){
                case 0:
                    openWandsMenu(p);
                    break;
                case 1:
                    openObjectsMenu(p);
                    break;
                case 2:
                    openLuckyBlocksMenu(p);
                    break;
                case 3:
                    if(target != null){
                        target.getInventory().addItem(SpecialItemManager.getLuckyTool().getItem());
                    }
                    break;
                case 8:
                    openPlayerSelectionMenu(p);
                    break;
            }
        }
//</editor-fold>
    };
    
    private static void openPlayerSelectionMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        List<String> onlinePlayers = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> onlinePlayers.add(player.getName()));
        onlinePlayers.remove(p.getName());
        Collections.sort(onlinePlayers);
        onlinePlayers.add(0, p.getName());
        
        maxPages = onlinePlayers.size() / 51;
        if(onlinePlayers.size() % 51 != 0){
            maxPages++;
        }
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.PLAYER_SELECTION_MENU, 54, "&2&lOnline Player List");
        
        ItemStack previous = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = previous.getItemMeta();
        meta.setDisplayName("&dPrevious");
        previous.setItemMeta(meta);
        
        ItemStack page = new ItemStack(Material.MAGMA_CREAM);
        meta = page.getItemMeta();
        meta.setDisplayName("&6Page &b(&e" + inventoryPage + "&b/&e" + maxPages + "&b)");
        page.setItemMeta(meta);
        
        ItemStack next = XMaterial.ENDER_EYE.parseItem();
        meta = next.getItemMeta();
        meta.setDisplayName("&dNext");
        next.setItemMeta(meta);
        
        inv.setItem(51, previous);
        inv.setItem(52, page);
        inv.setItem(53, next);
        
        
        ItemStack player = XMaterial.PLAYER_HEAD.parseItem();
        SkullMeta sk = (SkullMeta) player.getItemMeta();
        for(int i=51*(inventoryPage-1);i<onlinePlayers.size();i++){
            sk.setDisplayName("&2" + onlinePlayers.get(i));
            sk.setOwner(onlinePlayers.get(i));
            player.setItemMeta(sk);
            
            inv.setItem(i%51, player);
            if(i%51 == 50){
                break;
            }
        }
        
        InventoryListener.registerInventory(CustomInventoryType.PLAYER_SELECTION_MENU, PLAYER_SELECTION_MENU_FUNCTION);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction PLAYER_SELECTION_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            switch(e.getSlot()){
                case 51:
                    if(inventoryPage == 1){
                        openGiveMenu(p);
                    }else{
                        inventoryPage--;
                        openPlayerSelectionMenu(p);
                    }
                    break;
                case 53:
                    if(maxPages > 1){
                        if(inventoryPage == maxPages){
                            inventoryPage = 1;
                        }else{
                            inventoryPage++;
                        }
                    }
                    openPlayerSelectionMenu(p);
                    break;
                case 52:
                    //Do nothing
                    break;
                default:
                    ItemStack sk = e.getCurrentItem();
                    if(sk != null && !sk.getType().equals(Material.AIR)){
                        String playerName = Logger.stripColor(sk.getItemMeta().getDisplayName());
                        target = Bukkit.getPlayer(playerName);
                        openGiveMenu(p);
                        
                        playerItem = XMaterial.PLAYER_HEAD.parseItem();
                        SkullMeta skmeta = (SkullMeta) playerItem.getItemMeta();
                        skmeta.setDisplayName("&2Player Selected:");
                        skmeta.setLore(Arrays.asList("&b" + playerName));
                        skmeta.setOwner(playerName);
                        playerItem.setItemMeta(skmeta);
                    }
                    openGiveMenu(p);
                    break;
            }
        }
//</editor-fold>
    };
    
    
    private static void openWandsMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = back.getItemMeta();
        meta.setDisplayName("&dBack");
        back.setItemMeta(meta);
        
        ItemStack close = new ItemStack(Material.MAGMA_CREAM);
        meta = close.getItemMeta();
        meta.setDisplayName("&cClose");
        close.setItemMeta(meta);
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.WANDS_MENU, 27, "&aGive Wands");
        
        List<SpecialItem> wandsList = SpecialItemManager.getWands();
        for(int i=0; i<wandsList.size() && i<16; i++){
            inv.setItem(i, wandsList.get(i).getItem());
        }
        
        inv.setItem(18, playerItem);
        
        inv.setItem(25, back);
        inv.setItem(26, close);
        
        InventoryListener.registerInventory(CustomInventoryType.WANDS_MENU, WANDS_MENU_FUNCTION);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction WANDS_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            switch(e.getSlot()){
                case 18:
                    //Do nothing
                    break;
                case 25:
                    openGiveMenu(p);
                    break;
                case 26:
                    p.closeInventory();
                    break;
                default:
                    if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
                        target.getInventory().addItem(e.getCurrentItem().clone());
                    }
            }
        }
//</editor-fold>
    };
    
    
    private static void openObjectsMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = back.getItemMeta();
        meta.setDisplayName("&dBack");
        back.setItemMeta(meta);
        
        ItemStack close = new ItemStack(Material.MAGMA_CREAM);
        meta = close.getItemMeta();
        meta.setDisplayName("&cClose");
        close.setItemMeta(meta);
        
        ItemStack plus = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
        meta = plus.getItemMeta();
        meta.setDisplayName("&a&l+");
        plus.setItemMeta(meta);
        
        ItemStack less = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
        meta = less.getItemMeta();
        meta.setDisplayName("&c&l-");
        less.setItemMeta(meta);
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.OBJECTS_MENU, 27, "&bGive Objects");
        
        List<SpecialItem> objectsList = SpecialItemManager.getObjects();
        for(int i=0; i<objectsList.size() && i<16; i++){
            ItemStack stack = objectsList.get(i).getItem();
            stack.setAmount(amount);
            inv.setItem(i, stack);
        }
        
        inv.setItem(18, playerItem);
        
        inv.setItem(16, plus);
        inv.setItem(17, less);
        
        inv.setItem(25, back);
        inv.setItem(26, close);
        
        InventoryListener.registerInventory(CustomInventoryType.OBJECTS_MENU, OBJECTS_MENU_FUNCTION);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction OBJECTS_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            switch(e.getSlot()){
                case 16:
                    if(amount == 64){
                        amount = 1;
                    }else{
                        amount++;
                    }
                    openObjectsMenu(p);
                    break;
                case 17:
                    if(amount == 1){
                        amount = 64;
                    }else{
                        amount--;
                    }
                    openObjectsMenu(p);
                    break;
                case 18:
                    //Do nothing
                    break;
                case 25:
                    openGiveMenu(p);
                    break;
                case 26:
                    p.closeInventory();
                    break;
                default:
                    if(e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR)){
                        target.getInventory().addItem(e.getCurrentItem().clone());
                    }
            }
        }
//</editor-fold>
    };
    
    private static void openLuckyBlocksMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = back.getItemMeta();
        meta.setDisplayName("&dBack");
        back.setItemMeta(meta);
        
        ItemStack close = new ItemStack(Material.MAGMA_CREAM);
        meta = close.getItemMeta();
        meta.setDisplayName("&cClose");
        close.setItemMeta(meta);
        
        ItemStack plus = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
        meta = plus.getItemMeta();
        meta.setDisplayName("&a&l+");
        plus.setItemMeta(meta);
        
        ItemStack less = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
        meta = less.getItemMeta();
        meta.setDisplayName("&c&l-");
        less.setItemMeta(meta);
        
        //LuckyBlock
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.LUCKYBLOCKS_MENU, 27, "&6Give LuckyBlocks");
        
        List<LuckyBlockType> types = TypeManager.getTypes();
        for(int i=0;i<16 && i<types.size();i++){
            ItemStack stack = types.get(i).getItem();
            stack.setAmount(amount);
            inv.setItem(i, stack);
        }
        
        inv.setItem(18, playerItem);
        
        inv.setItem(16, plus);
        inv.setItem(17, less);
        
        inv.setItem(25, back);
        inv.setItem(26, close);
        
        InventoryListener.registerInventory(CustomInventoryType.LUCKYBLOCKS_MENU, LUCKYBLOCKS_MENU_FUNCTION);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction LUCKYBLOCKS_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            switch(e.getSlot()){
                case 16:
                    if(amount == 64){
                        amount = 1;
                    }else{
                        amount++;
                    }
                    openLuckyBlocksMenu(p);
                    break;
                case 17:
                    if(amount == 1){
                        amount = 64;
                    }else{
                        amount--;
                    }
                    openLuckyBlocksMenu(p);
                    break;
                case 18:
                    //Do nothing
                    break;
                case 25:
                    openGiveMenu(p);
                    break;
                case 26:
                    p.closeInventory();
                    break;
                default:
                    if(e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR)){
                        target.getInventory().addItem(e.getCurrentItem().clone());
                    }
            }
        }
//</editor-fold>
    };
}