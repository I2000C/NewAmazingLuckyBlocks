package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.editor.EditorType;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.FireworkReward;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils2.CustomColor;
import me.i2000c.newalb.utils2.ItemBuilder;
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
        
        ItemStack amount = ItemBuilder.newItem(XMaterial.FIREWORK_ROCKET)
                .withAmount(item.getAmount())
                .withDisplayName("&3Amount")
                .build();
        
        ItemStack power = ItemBuilder.newItem(XMaterial.BLAZE_POWDER)
                .withAmount(item.getPower())
                .withDisplayName("&6Power")
                .build();
        
        ItemStack withTrail = GUIItem.getBooleanItem(
                item.withTrail(), 
                "&5Trail", 
                XMaterial.BLAZE_ROD, 
                XMaterial.BLAZE_ROD);
        
        ItemStack withFlicker = GUIItem.getBooleanItem(
                item.withFlicker(), 
                "&5Flicker", 
                XMaterial.TNT, 
                XMaterial.TNT);
        
        ItemStack fireworkType = ItemBuilder.newItem(TYPE_MATERIALS[selectedType])
                .withDisplayName("&aFirework type: &b" + FIREWORK_EFFECTS[selectedType])
                .build();
        
        //Main color list ItemStacks
        
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.LIME_BANNER);
        builder.withDisplayName("&aMain color list");
        if(!item.getHEXMainColors().isEmpty()){
            builder.withLore(item.getHEXMainColors());
        }
        ItemStack mainColorBanner = builder.build();
        
        ItemStack addMainColor = ItemBuilder.newItem(XMaterial.LIME_STAINED_GLASS_PANE)
                .withDisplayName("&aAdd main color")
                .build();
        
        ItemStack resetMainColors = ItemBuilder.newItem(XMaterial.BARRIER)
                .withDisplayName("&cReset main color list")
                .build();   
        
        //Fade color list ItemStacks
        
        builder = ItemBuilder.newItem(XMaterial.RED_BANNER);
        builder.withDisplayName("&cFade color list");
        if(!item.getHEXFadeColors().isEmpty()){
            builder.withLore(item.getHEXFadeColors());
        }
        ItemStack fadeColorBanner = builder.build();
        
        ItemStack addFadeColor = ItemBuilder.newItem(XMaterial.LIME_STAINED_GLASS_PANE)
                .withDisplayName("&aAdd fade color (optional)")
                .build();
        
        ItemStack resetFadeColors = ItemBuilder.newItem(XMaterial.BARRIER)
                .withDisplayName("&cReset fade color list")
                .build();    
        
        menu.setItem(1, GUIItem.getPlusLessItem(+1));
        menu.setItem(2, GUIItem.getPlusLessItem(+1));
        menu.setItem(19, GUIItem.getPlusLessItem(-1));
        menu.setItem(20, GUIItem.getPlusLessItem(-1));
        menu.setItem(10, amount);
        menu.setItem(11, power);
        
        menu.setItem(12, withTrail);
        menu.setItem(13, withFlicker);
        
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
                    if(!item.getHEXMainColors().isEmpty()){
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
                    item.setWithTrail(!item.withTrail());
                    openFireworkMenu(player);
                    break;
                case 13:
                    item.setWithFlicker(!item.withFlicker());
                    openFireworkMenu(player);
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
                                item.getHEXMainColors().add(color.getHexColorString());
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
                                item.getHEXFadeColors().add(color.getHexColorString());
                                openFireworkMenu(p);
                            });
                    break;
                case 24:
                    //Reset colorList
                    item.getHEXMainColors().clear();
                    openFireworkMenu(player);
                    break;
                case 25:
                    //Reset fadeList
                    item.getHEXFadeColors().clear();
                    openFireworkMenu(player);
                    break;
            }
        }
//</editor-fold>
    };
}
