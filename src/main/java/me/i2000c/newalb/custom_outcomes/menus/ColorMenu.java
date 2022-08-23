package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.CustomColor;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ColorMenu extends Editor<CustomColor>{
    public ColorMenu(){
        InventoryListener.registerInventory(CustomInventoryType.COLOR_MENU, COLOR_MENU_FUNCTION);
    }
    
    private static final int COLOR_ITEM_SLOTS[] = {10, 11, 12, 13,
                                                   19, 20, 21, 22,
                                                   28, 29, 30, 31,
                                                   37, 38, 39, 40};
    
    private static final String[] COLOR_NAMES = {
        //<editor-fold defaultstate="collapsed" desc="Code">
        "BLACK",
        "RED",
        "DARK GREEN",
        "BROWN",
        "DARK BLUE",
        "PURPLE",
        "CYAN",
        "LIGHT GREY",
        "DARK GREY",
        "PINK",
        "LIGHT GREEN",
        "YELLOW",
        "LIGHT BLUE",
        "MAGENTA",
        "ORANGE",
        "WHITE"
//</editor-fold>
    };
    private static final String[] COLOR_HEX_VALUES = {
        //<editor-fold defaultstate="collapsed" desc="Code">
        "000000",
        "FF0000",
        "006622",
        "663300",
        "0000CC",
        "8000FF",
        "009999",
        "A6A6A6",
        "6B6B6B",
        "FF99FF",
        "33CC33",
        "FFFF00",
        "80CCFF",
        "FF00FF",
        "FF8000",
        "FFFFFF"
//</editor-fold>
    };
    private static final XMaterial[] COLOR_MATERIALS = {
        //<editor-fold defaultstate="collapsed" desc="Code">
        XMaterial.INK_SAC,
        XMaterial.RED_DYE,
        XMaterial.GREEN_DYE,
        XMaterial.COCOA_BEANS,
        XMaterial.LAPIS_LAZULI,
        XMaterial.PURPLE_DYE,
        XMaterial.CYAN_DYE,
        XMaterial.LIGHT_GRAY_DYE,
        XMaterial.GRAY_DYE,
        XMaterial.PINK_DYE,
        XMaterial.LIME_DYE,
        XMaterial.YELLOW_DYE,
        XMaterial.LIGHT_BLUE_DYE,
        XMaterial.MAGENTA_DYE,
        XMaterial.ORANGE_DYE,
        XMaterial.BONE_MEAL
//</editor-fold>
    };
    
    @Override
    protected void newItem(Player player){
        item = null;
        openColorInventory(player);
    }
    
    @Override
    protected void editItem(Player player){
        openColorInventory(player);
    }
    
    private void openColorInventory(Player player){        
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.COLOR_MENU, 54, "&6&lColor menu");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.CYAN);
        
        for(int i=0;i<9;i++){
            menu.setItem(i, glass);
        }for(int i=45;i<54;i++){
            menu.setItem(i, glass);
        }for(int i=9;i<45;i+=9){
            menu.setItem(i, glass);
        }for(int i=17;i<54;i+=9){
            menu.setItem(i, glass);
        }
        
        for(int i=0; i<COLOR_ITEM_SLOTS.length; i++){
            ItemStack sk = getColorItemStackFromDurability(i);
            menu.setItem(COLOR_ITEM_SLOTS[i], sk);
        }
        
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.LEATHER_CHESTPLATE);
        if(item == null){
            builder.withDisplayName("&dChosen color: &b" + "null");
        }else{
            builder.withDisplayName("&dChosen color: &b" + item);
            builder.withColor(item.getBukkitColor());
        }
        ItemStack leather = builder.build();
        
        ItemStack chooseCustomColor = ItemBuilder.newItem(XMaterial.OAK_SIGN)
                .withDisplayName("&3Choose custom color")
                .build();
        
        menu.setItem(16, leather);
        menu.setItem(15, chooseCustomColor);
        menu.setItem(42, GUIItem.getBackItem());
        menu.setItem(43, GUIItem.getNextItem());
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction COLOR_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){            
            switch(e.getSlot()){
                case 15:
                    ChatListener.registerPlayer(player, message -> {
                        try{
                            item = new CustomColor(message);
                            ChatListener.removePlayer(player);
                            openColorInventory(player);
                        }catch(Exception ex){
                            Logger.sendMessage("&cInvalid color string: &b" + message, player);
                            Logger.sendMessage("&bIf you don't know any valid color, use &7/alb return", player);
                        }
                    }, false);
                    player.closeInventory();
                    break;
                case 42:
                    // Back to previous menu
                    onBack.accept(player);
                    break;
                case 43:
                    //Open firework inventory
                    if(item != null){
                        onNext.accept(player, item);
                    }
                    break;
                default:
                    String colorHEX = getHexColorFromItemStack(e.getCurrentItem());
                    if(colorHEX != null){
                        item = new CustomColor(colorHEX);
                        openColorInventory(player);
                    }
            }
        }
//</editor-fold>
    };    
    
    
    private static String getHexColorFromItemStack(ItemStack stack){
        //<editor-fold defaultstate="collapsed" desc="Code">
        XMaterial material = XMaterial.matchXMaterial(stack);
        for(int i=0; i<COLOR_MATERIALS.length; i++){
            if(COLOR_MATERIALS[i] == material){
                return COLOR_HEX_VALUES[i];
            }
        }
        
        return null;
//</editor-fold>
    }
    
    // 0 <= i <= 15
    private static ItemStack getColorItemStackFromDurability(int i){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(i<0 || i>15){
            return null;
        }
        
        return ItemBuilder.newItem(COLOR_MATERIALS[i])
                .withDisplayName("&d" + COLOR_NAMES[i] + ": &b" + COLOR_HEX_VALUES[i])
                .build();
//</editor-fold>
    }
}
