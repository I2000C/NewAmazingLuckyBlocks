package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.List;
import me.i2000c.newalb.custom_outcomes.utils.rewards.FireworkReward;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GlassColor;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FireworkMenu{
    private static final String[] colors = {"BLACK","RED","DARK GREEN","BROWN","DARK BLUE","PURPLE","CYAN","LIGHT GREY","DARK GREY","PINK","LIGHT GREEN","YELLOW","LIGHT BLUE","MAGENTA","ORANGE","WHITE"};
    static final String[] hexValues = {"000000","FF0000", "006622","663300","0000CC","8000FF","009999","A6A6A6","6B6B6B","FF99FF","33CC33","FFFF00","80CCFF","FF00FF","FF8000","FFFFFF"};
    public static final String[] type = {"BALL","BALL_LARGE","STAR","BURST","CREEPER"};
    private static List<ItemStack> typeMaterial = new ArrayList<>();
    private static XMaterial[] materials = {
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
    };
    
    public static int selectedType = 0;
    private static List<String> list = null;
    private static String color = null;
    public static FireworkReward reward = null;
    
    private static boolean inventoriesRegistered = false;
    
    public static void reset(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.FIREWORK_MENU, FIREWORK_MENU_FUNCTION);
            InventoryListener.registerInventory(CustomInventoryType.COLOR_MENU, COLOR_MENU_FUNCTION);
            
            inventoriesRegistered = true;
        }
        
        selectedType = 0;
        list = null;
        color = null;
        reward = null;
    }
    
    public static void openFireworkMenu(Player p){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.FIREWORK_MENU, 27, "&b&lFirework Reward");
        
        if(reward == null){
            reward = new FireworkReward(FinishMenu.getCurrentOutcome());
        }
        
        if(typeMaterial.isEmpty()){
            typeMaterial.add(XMaterial.FIREWORK_STAR.parseItem());
            typeMaterial.add(XMaterial.FIRE_CHARGE.parseItem());
            typeMaterial.add(XMaterial.NETHER_STAR.parseItem());
            typeMaterial.add(XMaterial.MELON_SEEDS.parseItem());
            typeMaterial.add(XMaterial.CREEPER_HEAD.parseItem());
        }
        
        ItemStack amount = ItemBuilder.newItem(XMaterial.FIREWORK_ROCKET)
                .withAmount(reward.getAmount())
                .withDisplayName("&3Amount")
                .build();
        
        ItemStack power = ItemBuilder.newItem(XMaterial.BLAZE_POWDER)
                .withAmount(reward.getPower())
                .withDisplayName("&6Power")
                .build();
        
        ItemStack withTrail = GUIItem.getBooleanItem(
                reward.withTrail(), 
                "&5Trail", 
                XMaterial.BLAZE_ROD, 
                XMaterial.BLAZE_ROD);
        
        ItemStack withFlicker = GUIItem.getBooleanItem(
                inventoriesRegistered, 
                "&5Flicker", 
                XMaterial.TNT, 
                XMaterial.TNT);
        
        ItemStack fireworkType = ItemBuilder.fromItem(typeMaterial.get(selectedType))
                .withDisplayName("&aFirework type: &b" + type[selectedType])
                .build();
        
        //Main color list ItemStacks
        
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.LIME_BANNER);
        builder.withDisplayName("&aMain color list");
        if(!reward.getHEXColors().isEmpty()){
            builder.withLore(reward.getHEXColors());
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
        if(!reward.getHEXFadeColors().isEmpty()){
            builder.withLore(reward.getHEXFadeColors());
        }
        ItemStack fadeColorBanner = builder.build();
        
        ItemStack addFadeColor = ItemBuilder.newItem(XMaterial.LIME_STAINED_GLASS_PANE)
                .withDisplayName("&aAdd fade color (optional)")
                .build();
        
        ItemStack resetFadeColors = ItemBuilder.newItem(XMaterial.BARRIER)
                .withDisplayName("&cReset fade color list")
                .build();    
        
        inv.setItem(1, GUIItem.getPlusLessItem(+1));
        inv.setItem(2, GUIItem.getPlusLessItem(+1));
        inv.setItem(19, GUIItem.getPlusLessItem(-1));
        inv.setItem(20, GUIItem.getPlusLessItem(-1));
        inv.setItem(10, amount);
        inv.setItem(11, power);
        
        inv.setItem(12, withTrail);
        inv.setItem(13, withFlicker);
        
        inv.setItem(14, fireworkType);
        
        inv.setItem(15, mainColorBanner);
        inv.setItem(6, addMainColor);
        inv.setItem(24, resetMainColors);
        inv.setItem(16, fadeColorBanner);
        inv.setItem(7, addFadeColor);
        inv.setItem(25, resetFadeColors);
        
        inv.setItem(9, GUIItem.getBackItem());
        inv.setItem(17, GUIItem.getNextItem());
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction FIREWORK_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){

            switch(e.getSlot()){
                case 9:
                    if(FinishMenu.editMode){
                        FinishMenu.openFinishInventory(p);
                    }else{
                        RewardTypesMenu.openRewardTypesMenu(p);
                    }
                    break;
                case 1:
                    if(reward.getAmount() == 10){
                        reward.setAmount(1);
                    }else{
                        reward.setAmount(reward.getAmount()+1);
                    }
                    openFireworkMenu(p);
                    break;
                case 19:
                    if(reward.getAmount() == 1){
                        reward.setAmount(10);
                    }else{
                        reward.setAmount(reward.getAmount()-1);
                    }
                    openFireworkMenu(p);
                    break;
                case 2:
                    if(reward.getPower() == 5){
                        reward.setPower(0);
                    }else{
                        reward.setPower(reward.getPower()+1);
                    }
                    openFireworkMenu(p);
                    break;
                case 20:
                    if(reward.getPower() == 0){
                        reward.setPower(5);
                    }else{
                        reward.setPower(reward.getPower()-1);
                    }
                    openFireworkMenu(p);
                    break;
                case 12:
                    reward.setWithTrail(!reward.withTrail());
                    openFireworkMenu(p);
                    break;
                case 13:
                    reward.setWithFlicker(!reward.withFlicker());
                    openFireworkMenu(p);
                    break;
                case 14:
                    if(selectedType == 4){
                        selectedType = 0;
                    }else{
                        selectedType++;
                    }
                    reward.setType(type[selectedType]);
                    openFireworkMenu(p);
                    break;
                case 6:
                    //Open color menu (colorList)
                    list = reward.getHEXColors();
                    openColorInventory(p);
                    break;
                case 7:
                    //Open fade color menu (fadeList)
                    list = reward.getHEXFadeColors();
                    openColorInventory(p);
                    break;
                case 24:
                    //Reset colorList
                    reward.getHEXColors().clear();
                    openFireworkMenu(p);
                    break;
                case 25:
                    //Reset fadeList
                    reward.getHEXFadeColors().clear();
                    openFireworkMenu(p);
                    break;
                case 17:
                    //Open Finish Menu
                    if(!reward.getHEXColors().isEmpty()){
                        FinishMenu.addReward(reward);
                        reset();
                        FinishMenu.openFinishInventory(p);
                    }
                    break;
            }
        }
//</editor-fold>
    };
    
    private static void openColorInventory(Player p){        
        //<editor-fold defaultstate="collapsed" desc="Code">
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.COLOR_MENU, 54, "&6&lColor menu");
        
        ItemStack glass = GUIItem.getGlassItem(GlassColor.CYAN);
        
        for(int i=0;i<9;i++){
            inv.setItem(i, glass);
        }for(int i=45;i<54;i++){
            inv.setItem(i, glass);
        }for(int i=9;i<45;i+=9){
            inv.setItem(i, glass);
        }for(int i=17;i<54;i+=9){
            inv.setItem(i, glass);
        }
        
        for(int i=0;i<=15;i++){
            ItemStack sk = getColorItemStackFromDurability(i);
            
            if(i<4){
                inv.setItem(i+10, sk);
            }else if(i<8){
                inv.setItem(i+15, sk);
            }else if(i<12){
                inv.setItem(i+20, sk);
            }else{
                inv.setItem(i+25, sk);
            }
        }
        
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.LEATHER_CHESTPLATE);
        if(color == null || color.equals("ERROR")){
            builder.withDisplayName("&dChosen color: &b" + "null");
        }else{
            builder.withDisplayName("&dChosen color: &b" + color);
            builder.withColor(Color.fromRGB(getDecimalFromHex(color)));
        }
        ItemStack leather = builder.build();
        
        builder = ItemBuilder.newItem(XMaterial.OAK_SIGN);
        if(color != null && color.equals("ERROR")){
            builder.withDisplayName("&cYou must enter a valid hex color");
        }else{
            builder.withDisplayName("&3Choose custom color");
        }
        ItemStack chooseCustomColor = builder.build();
        
        inv.setItem(16, leather);
        inv.setItem(15, chooseCustomColor);
        inv.setItem(42, GUIItem.getBackItem());
        inv.setItem(43, GUIItem.getNextItem());
        
        GUIManager.setCurrentInventory(inv);
        p.openInventory(inv);
//</editor-fold>
    }
    
    private static final InventoryFunction COLOR_MENU_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            
            switch(e.getSlot()){
                case 15:
                    ChatListener.registerPlayer(p, message -> {
                        if(message.length() == 6){
                            try{
                                int a = getDecimalFromHex(color);
                                color = message.toUpperCase();
                            }catch(Exception ex){
                                color = "ERROR";
                            }
                        }else{
                            color = "ERROR";
                        }
                        openColorInventory(p);
                    });
                    p.closeInventory();
                    break;
                case 42:
                    //Open firework inventory
                    openFireworkMenu(p);
                    break;
                case 43:
                    //Open firework inventory
                    if(color != null && !color.equals("ERROR")){
                        list.add(color);
                        list = null;
                        openFireworkMenu(p);
                    }
                    break;
                default:
                    String aux = getHexColorFromItemStack(e.getCurrentItem());
                    if(aux != null){
                        color = aux;
                        openColorInventory(p);
                    }
            }
        }
//</editor-fold>
    };    
    
    static String getHexColorFromItemStack(ItemStack stack){
        //<editor-fold defaultstate="collapsed" desc="Code">
        XMaterial material = XMaterial.matchXMaterial(stack);
        for(int i=0; i<materials.length; i++){
            if(materials[i] == material){
                return hexValues[i];
            }
        }        
        return null;
//</editor-fold>
    }
    
    // 0 <= i <= 15
    static ItemStack getColorItemStackFromDurability(int i){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(i<0 || i>15){
            return null;
        }
        
        return ItemBuilder.newItem(materials[i])
                .withDisplayName("&d" + colors[i] + ": &b" + hexValues[i])
                .build();
//</editor-fold>
    }
    
    public static int getDecimalFromHex(String hex){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String digits = "0123456789ABCDEF";
        hex = hex.toUpperCase();
        int val = 0;
        for (int i=0;i<hex.length();i++){
            char c = hex.charAt(i);
            int d = digits.indexOf(c);
            val = 16 * val + d;
        }
        return val;
//</editor-fold>
    }
    
    public static String getHexFromDecimal(int decimal){
        //<editor-fold defaultstate="collapsed" desc="Code">
        int rem;  
        String hex = "";   
        char hexchars[] = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};  
        while(decimal > 0){  
            rem = decimal % 16;   
            hex = hexchars[rem] + hex;   
            decimal = decimal / 16;  
        }  
        return hex;
//</editor-fold>        
    }
}
