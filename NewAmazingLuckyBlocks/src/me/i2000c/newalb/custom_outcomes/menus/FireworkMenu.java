package me.i2000c.newalb.custom_outcomes.menus;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.List;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.custom_outcomes.utils.rewards.FireworkReward;
import me.i2000c.newalb.utils.Logger;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.Player;

import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.Color;

public class FireworkMenu{
    private static final String[] colors = {"BLACK","RED","DARK GREEN","BROWN","DARK BLUE","PURPLE","CYAN","LIGHT GREY","DARK GREY","PINK","LIGHT GREEN","YELLOW","LIGHT BLUE","MAGENTA","ORANGE","WHITE"};
    static final String[] hexValues = {"000000","FF0000", "006622","663300","0000CC","8000FF","009999","A6A6A6","6B6B6B","FF99FF","33CC33","FFFF00","80CCFF","FF00FF","FF8000","FFFFFF"};
    public static final String[] type = {"BALL","BALL_LARGE","STAR","BURST","CREEPER"};
    private static List<ItemStack> typeMaterial = new ArrayList<>();
    
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
        
        ItemStack plus = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
        ItemMeta meta = plus.getItemMeta();
        meta.setDisplayName(Logger.color("&a&l+"));
        plus.setItemMeta(meta);
        
        ItemStack minus = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
        meta = minus.getItemMeta();
        meta.setDisplayName(Logger.color("&c&l-"));
        minus.setItemMeta(meta);
        
        ItemStack amt = XMaterial.FIREWORK_ROCKET.parseItem();
        amt.setAmount(reward.getAmount());
        meta = amt.getItemMeta();
        meta.setDisplayName(Logger.color("&3Amount"));
        amt.setItemMeta(meta);
        
        ItemStack pow = new ItemStack(Material.BLAZE_POWDER);
        pow.setAmount(reward.getPower());
        meta = pow.getItemMeta();
        meta.setDisplayName(Logger.color("&6Power"));
        pow.setItemMeta(meta);
        
        ItemStack withTrail = new ItemStack(Material.BLAZE_ROD);
        meta = withTrail.getItemMeta();
        if(reward.withTrail()){
            meta.setDisplayName(Logger.color("&5Trail: &atrue"));
        }else{
            meta.setDisplayName(Logger.color("&5Trail: &cfalse"));
        }
        withTrail.setItemMeta(meta);
        
        ItemStack withFlicker = new ItemStack(Material.TNT);
        meta = withFlicker.getItemMeta();
        if(reward.withFlicker()){
            meta.setDisplayName(Logger.color("&5Flicker: &atrue"));
        }else{
            meta.setDisplayName(Logger.color("&5Flicker: &cfalse"));
        }
        withFlicker.setItemMeta(meta);
        
        ItemStack fireworkType = typeMaterial.get(selectedType).clone();
        meta = fireworkType.getItemMeta();
        meta.setDisplayName(Logger.color("&aFirework type: &b" + type[selectedType]));
        fireworkType.setItemMeta(meta);
        
        //Color list ItemStacks
        
        ItemStack colorBanner = XMaterial.LIME_BANNER.parseItem();
        meta = colorBanner.getItemMeta();
        meta.setDisplayName(Logger.color("&aColor list"));
        if(reward.getHEXColors().isEmpty()){
            meta.setLore(null);
        }else{
            meta.setLore(reward.getHEXColors());
        }
        colorBanner.setItemMeta(meta);
        
        ItemStack addColor = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
        addColor.setDurability((short) 5);
        meta = addColor.getItemMeta();
        meta.setDisplayName(Logger.color("&aAdd color"));
        addColor.setItemMeta(meta);
        
        ItemStack resetColor = new ItemStack(Material.BARRIER);
        meta = resetColor.getItemMeta();
        meta.setDisplayName(Logger.color("&cReset color list"));
        resetColor.setItemMeta(meta);
        
        //Fade color list ItemStacks
        
        ItemStack fadeBanner = XMaterial.RED_BANNER.parseItem();
        meta = fadeBanner.getItemMeta();
        meta.setDisplayName(Logger.color("&cFade color list"));
        if(reward.getHEXFadeColors().isEmpty()){
            meta.setLore(null);
        }else{
            meta.setLore(reward.getHEXFadeColors());
        }
        fadeBanner.setItemMeta(meta);
        
        ItemStack addFadeColor = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
        meta = addFadeColor.getItemMeta();
        meta.setDisplayName(Logger.color("&aAdd fade color (opcional)"));
        addFadeColor.setItemMeta(meta);
        
        ItemStack resetFadeColor = new ItemStack(Material.BARRIER);
        meta = resetFadeColor.getItemMeta();
        meta.setDisplayName(Logger.color("&cReset fade color list"));
        resetFadeColor.setItemMeta(meta);
        
        //Back and Next ItemStacks
        
        ItemStack next = new ItemStack(Material.ANVIL);
        meta = next.getItemMeta();
        meta.setDisplayName(Logger.color("&bNext"));
        next.setItemMeta(meta);
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName(Logger.color("&7Back"));
        back.setItemMeta(meta);
        
        
        Inventory inv = GUIFactory.createInventory(CustomInventoryType.FIREWORK_MENU, 27, Logger.color("&b&lFirework Reward"));
        
        inv.setItem(1, plus);
        inv.setItem(2, plus);
        inv.setItem(19, minus);
        inv.setItem(20, minus);
        inv.setItem(10, amt);
        inv.setItem(11, pow);
        
        inv.setItem(12, withTrail);
        inv.setItem(13, withFlicker);
        
        inv.setItem(14, fireworkType);
        
        inv.setItem(15, colorBanner);
        inv.setItem(6, addColor);
        inv.setItem(24, resetColor);
        inv.setItem(16, fadeBanner);
        inv.setItem(7, addFadeColor);
        inv.setItem(25, resetFadeColor);
        
        inv.setItem(9, back);
        inv.setItem(17, next);
        
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
        
        ItemStack glass = XMaterial.CYAN_STAINED_GLASS_PANE.parseItem();
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(Logger.color(" "));
        glass.setItemMeta(meta);
        
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
        
        ItemStack leather = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta lam = (LeatherArmorMeta) leather.getItemMeta();
        if(color == null || color.equals("ERROR")){
            lam.setDisplayName(Logger.color("&dChosen color: &b" + "null"));
        }else{
            lam.setDisplayName(Logger.color("&dChosen color: &b" + color));
            lam.setColor(Color.fromRGB(getDecimalFromHex(color)));
        }
        leather.setItemMeta(lam);
        
        ItemStack chooseCustomColor = XMaterial.OAK_SIGN.parseItem();
        meta = chooseCustomColor.getItemMeta();
        if(color != null && color.equals("ERROR")){
            meta.setDisplayName(Logger.color("&cYou must enter a valid hex color"));
        }else{
            meta.setDisplayName(Logger.color("&3Choose custom color"));
        }
        chooseCustomColor.setItemMeta(meta);
        
        ItemStack back = new ItemStack(Material.ENDER_PEARL);
        meta = back.getItemMeta();
        meta.setDisplayName(Logger.color("&bBack"));
        back.setItemMeta(meta);
        
        ItemStack next = new ItemStack(Material.ANVIL);
        meta = next.getItemMeta();
        meta.setDisplayName(Logger.color("&bNext"));
        next.setItemMeta(meta);
        
        inv.setItem(16, leather);
        inv.setItem(15, chooseCustomColor);
        inv.setItem(42, back);
        inv.setItem(43, next);
        
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
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
            if(stack.getType() == Material.INK_SACK){
                return hexValues[stack.getDurability()];
            }else{
                return null;
            }
        }else switch(stack.getType().name()){
            case "INK_SAC":
                return hexValues[0];
            case "RED_DYE":
            case "ROSE_RED":
                return hexValues[1];
            case "GREEN_DYE":
            case "CACTUS_GREEN":
                return hexValues[2];
            case "COCOA_BEANS":
                return hexValues[3];
            case "LAPIS_LAZULI":
                return hexValues[4];
            case "PURPLE_DYE":
                return hexValues[5];
            case "CYAN_DYE":
                return hexValues[6];
            case "LIGHT_GRAY_DYE":
                return hexValues[7];
            case "GRAY_DYE":
                return hexValues[8];
            case "PINK_DYE":
                return hexValues[9];
            case "LIME_DYE":
                return hexValues[10];
            case "YELLOW_DYE":
            case "DANDELION_YELLOW":
                return hexValues[11];
            case "LIGHT_BLUE_DYE":
                return hexValues[12];
            case "MAGENTA_DYE":
                return hexValues[13];
            case "ORANGE_DYE":
                return hexValues[14];
            case "BONE_MEAL":
                return hexValues[15];
            default:
                return null;
        }
//</editor-fold>
    }
    
    // PRE: 0 <= i <= 15
    static ItemStack getColorItemStackFromDurability(int i){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(i<0 || i>15){
            return null;
        }
        
        ItemStack stack;
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
            stack = new ItemStack(Material.INK_SACK);
            stack.setDurability((short) i);
        }else{
            Material material;
            switch(i){
                case 0:
                    material = Material.valueOf("INK_SAC");
                    break;
                case 1:
                    try{
                        material = Material.valueOf("RED_DYE");
                    }catch(Exception ex){
                        material = Material.valueOf("ROSE_RED");
                    }
                    break;
                case 2:
                    try{
                        material = Material.valueOf("GREEN_DYE");
                    }catch(Exception ex){
                        material = Material.valueOf("CACTUS_GREEN");
                    }
                    break;
                case 3:
                    material = Material.valueOf("COCOA_BEANS");
                    break;
                case 4:
                    material = Material.valueOf("LAPIS_LAZULI");
                    break;
                case 5:
                    material = Material.valueOf("PURPLE_DYE");
                    break;
                case 6:
                    material = Material.valueOf("CYAN_DYE");
                    break;
                case 7:
                    material = Material.valueOf("LIGHT_GRAY_DYE");
                    break;
                case 8:
                    material = Material.valueOf("GRAY_DYE");
                    break;
                case 9:
                    material = Material.valueOf("PINK_DYE");
                    break;
                case 10:
                    material = Material.valueOf("LIME_DYE");
                    break;
                case 11:
                    try{
                        material = Material.valueOf("YELLOW_DYE");
                    }catch(Exception ex){
                        material = Material.valueOf("DANDELION_YELLOW");
                    }
                    break;
                case 12:
                    material = Material.valueOf("LIGHT_BLUE_DYE");
                    break;
                case 13:
                    material = Material.valueOf("MAGENTA_DYE");
                    break;
                case 14:
                    material = Material.valueOf("ORANGE_DYE");
                    break;
                case 15:
                    material = Material.valueOf("BONE_MEAL");
                    break;
                default:
                    material = null;
            }
            stack = new ItemStack(material);
        }
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Logger.color("&d" + colors[i] + ": &b" + hexValues[i]));
        stack.setItemMeta(meta);
        return stack;
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
