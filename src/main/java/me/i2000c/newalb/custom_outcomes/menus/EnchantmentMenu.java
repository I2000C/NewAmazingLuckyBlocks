package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.EnchantmentWithLevel;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantmentMenu extends Editor<EnchantmentWithLevel>{
    // https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/enchantments/Enchantment.html
    // https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Registry.html
    // Since Minecraft 1.21, Enchantment.name() doesn't work as expected --> Use Enchantment.getKey()
    
    private final Set<XEnchantment> ENCHANTMENTS;
    
    public EnchantmentMenu(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        InventoryListener.registerInventory(CustomInventoryType.ENCHATMENTS_MENU, ENCHANTMENTS_MENU_FUNCTION);
        InventoryListener.registerInventory(CustomInventoryType.ENCHANTMENT_NAME_MENU, ENCHANTMENT_NAME_MENU_FUNCTION);
        
        ENCHANTMENTS = new TreeSet<>((ench1, ench2) -> ench1.name().compareTo(ench2.name()));
        for(XEnchantment ench : XEnchantment.VALUES) {
            if(ench.isSupported()) {
                ENCHANTMENTS.add(ench);
            }
        }
//</editor-fold>
    }
    
    @Override
    protected void newItem(Player player){
        item = new EnchantmentWithLevel(null, 1);
        openEnchantmentsMenu(player);
    }
    
    @Override
    protected void editItem(Player player){
        openEnchantmentsMenu(player);
    }
    
    private void openEnchantmentsMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.ENCHATMENTS_MENU, 27, "&d&lEnchantments Menu");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.PURPLE);
        
        ItemStackWrapper builder = ItemStackWrapper.newItem(XMaterial.ENCHANTED_BOOK);
        if(item.enchantment == null){
            builder.setDisplayName("&5Select enchantment");
        }else{
            builder.setDisplayName("&5Selected enchantment: &b" + item.enchantment.name());
            if(item.level > 0){
                builder.addEnchantment(item.enchantment, item.level);
            }else{
                builder.addEnchantment(item.enchantment, 1);
            }
        }
        ItemStack enchantment = builder.toItemStack();
        
        ItemStack level = ItemStackWrapper.newItem(XMaterial.EXPERIENCE_BOTTLE)
                                          .setAmount(item.level)
                                          .setDisplayName("&aSelected level: &b" + item.level)
                                          .addLoreLine("&3Click to select")
                                          .toItemStack();        
        
        for(int i=0;i<9;i++){
            menu.setItem(i, glass);
        }
        for(int i=18;i<27;i++){
            menu.setItem(i, glass);
        }
        menu.setItem(9, glass);
        menu.setItem(17, glass);
        
        menu.setItem(10, GUIItem.getBackItem());
        menu.setItem(12, enchantment);
        menu.setItem(14, level);
        
        menu.setItem(5, GUIItem.getPlusLessItem(+1));
        menu.setItem(23, GUIItem.getPlusLessItem(-1));
        
        menu.setItem(16, GUIItem.getNextItem());
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction ENCHANTMENTS_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        switch(e.getSlot()){
            case 10:
                // Go to previous menu
                onBack.accept(player);
                break;
            case 12:
                //Open select enchant name menu
                openSelectEnchantNameMenu(player);
                break;
            case 14:
                //Set level
                ChatListener.registerPlayer(player, message -> {
                    try{
                        int level = Integer.parseInt(message);
                        if(level <= 0){
                            throw new NumberFormatException();
                        }
                        
                        item.level = level;
                        ChatListener.removePlayer(player);
                        openEnchantmentsMenu(player);
                    }catch(NumberFormatException ex){
                        Logger.sendMessage("&cInvalid enchantment level: &b" + message, player);
                        Logger.sendMessage("&bIf you want to return, use &7/alb return", player);
                    }                    
                }, false);
                player.closeInventory();
                break;
            case 5:
                //Increase level
                item.level++;
                openEnchantmentsMenu(player);
                break;
            case 23:
                //Decrease level
                if(item.level > 1){
                    item.level--;
                    openEnchantmentsMenu(player);
                }
                break;
            case 16:
                if(item.enchantment != null && item.level > 0){
                    onNext.accept(player, item);
                }
                break;
        }
//</editor-fold>
    };
    
    private void openSelectEnchantNameMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.ENCHANTMENT_NAME_MENU, 54, "&5&lSelect enchant name");
        
        Iterator<XEnchantment> iterator = ENCHANTMENTS.iterator();
        for(int i=0; i<45 && iterator.hasNext(); i++){
            XEnchantment enchantment = iterator.next();
            ItemStack enchantItem = ItemStackWrapper.newItem(XMaterial.ENCHANTED_BOOK)
                                                    .setDisplayName("&d" + enchantment.name())
                                                    .addEnchantment(enchantment, 1)
                                                    .toItemStack();
            
            menu.setItem(i, enchantItem);
        }
        
        menu.setItem(53, GUIItem.getBackItem());
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction ENCHANTMENT_NAME_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        ItemStack stack = e.getCurrentItem();
        if(stack != null && stack.getType() != Material.AIR){
            if(e.getSlot() == 53){
                openEnchantmentsMenu(player);
            }else{
                String displayName = ItemStackWrapper.fromItem(stack, false).getDisplayName();
                item.enchantment = XEnchantment.matchXEnchantment(Logger.stripColor(displayName)).orElse(null);
                openEnchantmentsMenu(player);
            }
        }
//</editor-fold>
    };
}
