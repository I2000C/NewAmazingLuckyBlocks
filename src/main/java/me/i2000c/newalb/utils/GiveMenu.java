package me.i2000c.newalb.utils;

import com.cryptomorin.xseries.XMaterial;
import java.util.List;
import java.util.stream.Collectors;
import me.i2000c.newalb.custom_outcomes.rewards.LuckyBlockType;
import me.i2000c.newalb.custom_outcomes.rewards.TypeManager;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.listeners.interact.SpecialItemManager;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
    
    public static void openGiveMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(target == null){
            target = player;
        }
        
        Menu menu = GUIFactory.newMenu(CustomInventoryType.GIVE_MENU, 9, "&d&lGive Menu");
        InventoryListener.registerInventory(CustomInventoryType.GIVE_MENU, GIVE_MENU_FUNCTION);
        
        if(playerItem == null){
            playerItem = ItemBuilder.newItem(XMaterial.PLAYER_HEAD)
                    .withDisplayName("&2Player Selected:")
                    .addLoreLine("&b" + target.getName())
                    .withOwner(target.getName())
                    .build();
        }
        
        ItemStack wands = ItemBuilder.newItem(XMaterial.MUSIC_DISC_FAR)
                .withDisplayName("&aGive Wands")
                .build();
        
        ItemStack objects = ItemBuilder.newItem(XMaterial.BUCKET)
                .withDisplayName("&bGive Objects")
                .build();
        
        ItemStack luckyBlocks = ItemBuilder
                .fromItem(TypeManager.getMenuItemStack(), false)
                .withDisplayName("&6Give LuckyBlocks")
                .build();
        
        ItemStack luckyTool = ItemBuilder
                .fromItem(SpecialItemManager.getLuckyTool().getItem(), false)
                .withDisplayName("&eGive LuckyTool")
                .build();        
        
        menu.setItem(0, wands);
        menu.setItem(1, objects);
        menu.setItem(2, luckyBlocks);
        menu.setItem(3, luckyTool);
        menu.setItem(8, playerItem);
        
        menu.openToPlayer(player, false);
//</editor-fold>
    }
    
    private static final InventoryFunction GIVE_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
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
    
    private static void openPlayerSelectionMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        List<Player> onlinePlayers = Bukkit.getOnlinePlayers()
                .stream()
                .filter(p -> !p.equals(player))
                .sorted((player1, player2) -> player1.getName().compareTo(player2.getName()))
                .collect(Collectors.toList());
        onlinePlayers.add(0, player);
        
        maxPages = onlinePlayers.size() / 51;
        if(onlinePlayers.size() % 51 != 0){
            maxPages++;
        }
        
        Menu menu = GUIFactory.newMenu(CustomInventoryType.PLAYER_SELECTION_MENU, 54, "&2&lOnline Player List");
        
        menu.setItem(51, GUIItem.getPreviousPageItem());
        menu.setItem(52, GUIItem.getCurrentPageItem(inventoryPage, maxPages));
        menu.setItem(53, GUIItem.getNextPageItem());
        
        for(int i=51*(inventoryPage-1);i<onlinePlayers.size();i++){
            ItemStack playerStack = ItemBuilder.newItem(XMaterial.PLAYER_HEAD)
                    .withDisplayName("&2" + onlinePlayers.get(i).getName())
                    .withOwner(onlinePlayers.get(i))
                    .build();
            
            menu.setItem(i%51, playerStack);
            if(i%51 == 50){
                break;
            }
        }
        
        InventoryListener.registerInventory(CustomInventoryType.PLAYER_SELECTION_MENU, PLAYER_SELECTION_MENU_FUNCTION);
        menu.openToPlayer(player, false);
//</editor-fold>
    }
    
    private static final InventoryFunction PLAYER_SELECTION_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
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
                        String displayName = ItemBuilder.fromItem(sk)
                                .getDisplayName();
                        String playerName = Logger.stripColor(displayName);
                        target = Bukkit.getPlayer(playerName);
                        openGiveMenu(p);
                        
                        playerItem = ItemBuilder.newItem(XMaterial.PLAYER_HEAD)
                                .withDisplayName("&2Player Selected:")
                                .addLoreLine("&b" + playerName)
                                .withOwner(playerName)
                                .build();
                    }
                    openGiveMenu(p);
                    break;
            }
        }
//</editor-fold>
    };
    
    
    private static void openWandsMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">        
        ItemStack close = ItemBuilder.newItem(XMaterial.MAGMA_CREAM)
                .withDisplayName("&cClose")
                .build();
        
        Menu menu = GUIFactory.newMenu(CustomInventoryType.WANDS_MENU, 27, "&aGive Wands");
        
        List<SpecialItem> wandsList = SpecialItemManager.getWands();
        for(int i=0; i<wandsList.size() && i<16; i++){
            menu.setItem(i, wandsList.get(i).getItem());
        }
        
        menu.setItem(18, playerItem);
        
        menu.setItem(25, GUIItem.getBackItem());
        menu.setItem(26, close);
        
        InventoryListener.registerInventory(CustomInventoryType.WANDS_MENU, WANDS_MENU_FUNCTION);
        menu.openToPlayer(player, false);
//</editor-fold>
    }
    
    private static final InventoryFunction WANDS_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
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
    
    
    private static void openObjectsMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemStack close = ItemBuilder.newItem(XMaterial.MAGMA_CREAM)
                .withDisplayName("&cClose")
                .build();
        
        Menu menu = GUIFactory.newMenu(CustomInventoryType.OBJECTS_MENU, 27, "&bGive Objects");
        
        List<SpecialItem> objectsList = SpecialItemManager.getObjects();
        for(int i=0; i<objectsList.size() && i<16; i++){
            ItemStack stack = objectsList.get(i).getItem();
            stack.setAmount(amount);
            menu.setItem(i, stack);
        }
        
        menu.setItem(18, playerItem);
        
        menu.setItem(16, GUIItem.getPlusLessItem(+1));
        menu.setItem(17, GUIItem.getPlusLessItem(-1));
        
        menu.setItem(25, GUIItem.getBackItem());
        menu.setItem(26, close);
        
        InventoryListener.registerInventory(CustomInventoryType.OBJECTS_MENU, OBJECTS_MENU_FUNCTION);
        menu.openToPlayer(player, false);
//</editor-fold>
    }
    
    private static final InventoryFunction OBJECTS_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
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
    
    private static void openLuckyBlocksMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">        
        ItemStack close = ItemBuilder.newItem(XMaterial.MAGMA_CREAM)
                .withDisplayName("&cClose")
                .build();
        
        //LuckyBlock
        Menu menu = GUIFactory.newMenu(CustomInventoryType.LUCKYBLOCKS_MENU, 27, "&6Give LuckyBlocks");
        
        List<LuckyBlockType> types = TypeManager.getTypes();
        for(int i=0;i<16 && i<types.size();i++){
            ItemStack stack = types.get(i).getItem();
            stack.setAmount(amount);
            menu.setItem(i, stack);
        }
        
        menu.setItem(18, playerItem);
        
        menu.setItem(16, GUIItem.getPlusLessItem(+1));
        menu.setItem(17, GUIItem.getPlusLessItem(-1));
        
        menu.setItem(25, GUIItem.getBackItem());
        menu.setItem(26, close);
        
        InventoryListener.registerInventory(CustomInventoryType.LUCKYBLOCKS_MENU, LUCKYBLOCKS_MENU_FUNCTION);
        menu.openToPlayer(player, false);
//</editor-fold>
    }
    
    private static final InventoryFunction LUCKYBLOCKS_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
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