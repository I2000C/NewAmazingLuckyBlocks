package me.i2000c.newalb.lucky_blocks.editors.menus;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.functions.InventoryFunction;
import me.i2000c.newalb.api.gui.CustomInventoryType;
import me.i2000c.newalb.api.gui.GUIFactory;
import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.InventoryLocation;
import me.i2000c.newalb.api.gui.Menu;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.lucky_blocks.editors.Editor;
import me.i2000c.newalb.lucky_blocks.editors.EditorType;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.types.FireworkReward;
import me.i2000c.newalb.utils.locations.Offset;
import me.i2000c.newalb.utils.misc.CustomColor;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

import org.bukkit.FireworkEffect;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FireworkMenu extends Editor<FireworkReward>{
    public FireworkMenu(){
        InventoryListener.registerInventory(CustomInventoryType.FIREWORK_MENU, FIREWORK_MENU_FUNCTION);
    }
    
    private int selectedType;
    
    @Override
    protected void reset(){
        this.selectedType = 0;
    }
    
    @Override
    protected void newItem(Player player){
        Outcome outcome = RewardListMenu.getCurrentOutcome();
        item = new FireworkReward(outcome);
        openFireworkMenu(player);
    }
    
    @Override
    protected void editItem(Player player){
        selectedType = item.getType().ordinal();
        openFireworkMenu(player);
    }
    
    private static final FireworkEffect.Type[] FIREWORK_EFFECTS = FireworkEffect.Type.values();
    private static final XMaterial[] TYPE_MATERIALS = {
        XMaterial.FIREWORK_STAR,
        XMaterial.FIRE_CHARGE,
        XMaterial.NETHER_STAR,
        XMaterial.MELON_SEEDS,
        XMaterial.CREEPER_HEAD
    };
    
    private void openFireworkMenu(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Menu menu = GUIFactory.newMenu(CustomInventoryType.FIREWORK_MENU, 27, "&b&lFirework Reward");
        
        ItemStack amount = ItemStackWrapper.newItem(XMaterial.FIREWORK_ROCKET)
                                           .setAmount(item.getAmount())
                                           .setDisplayName("&3Amount")
                                           .toItemStack();
        
        ItemStack power = ItemStackWrapper.newItem(XMaterial.BLAZE_POWDER)
                                          .setAmount(item.getPower())
                                          .setDisplayName("&6Power")
                                          .toItemStack();
        
        ItemStack withTrail = GUIItem.getBooleanItem(
                item.isWithTrail(), 
                "&5Trail", 
                XMaterial.BLAZE_ROD, 
                XMaterial.BLAZE_ROD);
        
        ItemStack withFlicker = GUIItem.getBooleanItem(
                item.isWithFlicker(), 
                "&5Flicker", 
                XMaterial.TNT, 
                XMaterial.TNT);
        
        ItemStack offsetStack = item.getOffset().getItemToDisplay();
        
        ItemStack fireworkType = ItemStackWrapper.newItem(TYPE_MATERIALS[selectedType])
                                                 .setDisplayName("&aFirework type: &b" + FIREWORK_EFFECTS[selectedType])
                                                 .toItemStack();
        
        //Main color list ItemStacks
        
        ItemStackWrapper wrapper = ItemStackWrapper.newItem(XMaterial.LIME_BANNER);
        wrapper.setDisplayName("&aMain color list");
        if(!item.getColorsHEX().isEmpty()){
            wrapper.setLore(item.getColorsHEX());
        }
        ItemStack mainColorBanner = wrapper.toItemStack();
        
        ItemStack addMainColor = ItemStackWrapper.newItem(XMaterial.LIME_STAINED_GLASS_PANE)
                                                 .setDisplayName("&aAdd main color")
                                                 .toItemStack();
        
        ItemStack resetMainColors = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                                    .setDisplayName("&cReset main color list")
                                                    .toItemStack();   
        
        //Fade color list ItemStacks
        
        wrapper = ItemStackWrapper.newItem(XMaterial.RED_BANNER);
        wrapper.setDisplayName("&cFade color list");
        if(!item.getFadeColorsHEX().isEmpty()){
            wrapper.setLore(item.getFadeColorsHEX());
        }
        ItemStack fadeColorBanner = wrapper.toItemStack();
        
        ItemStack addFadeColor = ItemStackWrapper.newItem(XMaterial.LIME_STAINED_GLASS_PANE)
                                                 .setDisplayName("&aAdd fade color (optional)")
                                                 .toItemStack();
        
        ItemStack resetFadeColors = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                                    .setDisplayName("&cReset fade color list")
                                                    .toItemStack();    
        
        menu.setItem(1, GUIItem.getPlusLessItem(+1));
        menu.setItem(2, GUIItem.getPlusLessItem(+1));
        menu.setItem(19, GUIItem.getPlusLessItem(-1));
        menu.setItem(20, GUIItem.getPlusLessItem(-1));
        menu.setItem(10, amount);
        menu.setItem(11, power);
        
        menu.setItem(12, withTrail);
        menu.setItem(13, withFlicker);
        menu.setItem(4, offsetStack);
        
        menu.setItem(14, fireworkType);
        
        menu.setItem(15, mainColorBanner);
        menu.setItem(6, addMainColor);
        menu.setItem(24, resetMainColors);
        menu.setItem(16, fadeColorBanner);
        menu.setItem(7, addFadeColor);
        menu.setItem(25, resetFadeColors);
        
        menu.setItem(9, GUIItem.getBackItem());
        menu.setItem(17, GUIItem.getNextItem());
        
        menu.openToPlayer(player);
//</editor-fold>
    }
    
    private final InventoryFunction FIREWORK_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getLocation() == InventoryLocation.TOP){
            switch(e.getSlot()){
                case 9:
                    // Go to previous menu
                    onBack.accept(player);
                    break;
                case 17:
                    // Go to next menu
                    if(!item.getColorsHEX().isEmpty()){
                        onNext.accept(player, item);
                    }
                    break;
                case 1:
                    if(item.getAmount() == 10){
                        item.setAmount(1);
                    }else{
                        item.setAmount(item.getAmount()+1);
                    }
                    openFireworkMenu(player);
                    break;
                case 19:
                    if(item.getAmount() == 1){
                        item.setAmount(10);
                    }else{
                        item.setAmount(item.getAmount()-1);
                    }
                    openFireworkMenu(player);
                    break;
                case 2:
                    if(item.getPower() == 5){
                        item.setPower(0);
                    }else{
                        item.setPower(item.getPower()+1);
                    }
                    openFireworkMenu(player);
                    break;
                case 20:
                    if(item.getPower() == 0){
                        item.setPower(5);
                    }else{
                        item.setPower(item.getPower()-1);
                    }
                    openFireworkMenu(player);
                    break;
                case 12:
                    item.setWithTrail(!item.isWithTrail());
                    openFireworkMenu(player);
                    break;
                case 13:
                    item.setWithFlicker(!item.isWithFlicker());
                    openFireworkMenu(player);
                    break;
                case 4:
                    Editor<Offset> offsetEditor = EditorType.OFFSET.getEditor();
                    offsetEditor.editExistingItem(
                            item.getOffset().clone(), 
                            player, 
                            p -> openFireworkMenu(p), 
                            (p, offset) -> {
                                item.setOffset(offset);
                                openFireworkMenu(p);
                            });
                    break;
                case 14:
                    if(selectedType == 4){
                        selectedType = 0;
                    }else{
                        selectedType++;
                    }
                    item.setType(FIREWORK_EFFECTS[selectedType]);
                    openFireworkMenu(player);
                    break;
                case 6:
                    // Add color to main color list
                    Editor<CustomColor> editor = EditorType.COLOR.getEditor();
                    editor.createNewItem(
                            player, 
                            p -> openFireworkMenu(p), 
                            (p, color) -> {
                                item.getColorsHEX().add(color.getHexColorString());
                                openFireworkMenu(p);
                            });
                    break;
                case 7:
                    // Add color to fade color list
                    editor = EditorType.COLOR.getEditor();
                    editor.createNewItem(
                            player, 
                            p -> openFireworkMenu(p), 
                            (p, color) -> {
                                item.getFadeColorsHEX().add(color.getHexColorString());
                                openFireworkMenu(p);
                            });
                    break;
                case 24:
                    //Reset colorList
                    item.getColorsHEX().clear();
                    openFireworkMenu(player);
                    break;
                case 25:
                    //Reset fadeList
                    item.getFadeColorsHEX().clear();
                    openFireworkMenu(player);
                    break;
            }
        }
//</editor-fold>
    };
}
