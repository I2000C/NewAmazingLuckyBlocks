package me.i2000c.newalb;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.custom_outcomes.editor.EditorType;
import me.i2000c.newalb.custom_outcomes.menus.GUIManager;
import me.i2000c.newalb.custom_outcomes.menus.RewardListMenu;
import me.i2000c.newalb.custom_outcomes.rewards.LuckyBlockType;
import me.i2000c.newalb.custom_outcomes.rewards.PackManager;
import me.i2000c.newalb.custom_outcomes.rewards.TypeManager;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.TrapManager;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.listeners.interact.SpecialItemManager;
import me.i2000c.newalb.listeners.interact.SpecialItemName;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.GiveMenu;
import me.i2000c.newalb.utils.LangConfig;
import me.i2000c.newalb.utils.LocationManager;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.RandomBlocks;
import me.i2000c.newalb.utils.WorldConfig;
import me.i2000c.newalb.utils.WorldMenu;
import me.i2000c.newalb.utils.textures.InvalidTextureException;
import me.i2000c.newalb.utils.textures.Texture;
import me.i2000c.newalb.utils.textures.TextureException;
import me.i2000c.newalb.utils.textures.TextureManager;
import me.i2000c.newalb.utils.textures.URLTextureException;
import me.i2000c.newalb.utils2.Equipment;
import me.i2000c.newalb.utils2.OtherUtils;
import me.i2000c.newalb.utils2.Schematic;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandManager implements CommandExecutor, TabCompleter{
    private final NewAmazingLuckyBlocks plugin;
    
    public CommandManager(NewAmazingLuckyBlocks instance){
        this.plugin = instance;
    }
  
    public static boolean confirmMenu = false;
    
    private static final String NO_CONSOLE_MSG = "&cYou can't send this command from the console";
    
    private static boolean checkHasPermission(CommandSender sender, String permissionPath){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String noPermissionMessage = LangConfig.getMessage("NoPermission");
        String permission = ConfigManager.getConfig().getString(permissionPath);
        if(sender.hasPermission(permission)){
            return true;
        }else{
            Logger.sendMessage(noPermissionMessage, sender, false);
            return false;
        }
//</editor-fold>
    }
    private static boolean checkNotConsole(CommandSender sender){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(sender instanceof Player){
            return true;
        }else{
            Logger.sendMessage(NO_CONSOLE_MSG, sender);
            return false;
        }
//</editor-fold>
    }
    private static Player getOnlinePlayer(CommandSender sender, String playerName){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = Bukkit.getPlayer(playerName);
        if(player == null){
            Logger.sendMessage("&cPlayer " + playerName + "&c is offline", sender);
        }
        
        return player;
//</editor-fold>
    }
  
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!cmd.getName().equalsIgnoreCase("alb") && !cmd.getName().equalsIgnoreCase("nalb")){
            return false;
        }
        
        if(args.length == 0){
            Logger.sendMessage(plugin.name + " &a" + plugin.version, sender);
            Logger.sendMessage(LangConfig.getMessage("HelpMessage"), sender);
            return true;
        }else switch(args[0]){
            case "help":
                return executeHelp(sender, args);
            case "give":
                return executeGive(sender, args);
            case "take":
                return executeTake(sender, args);
            case "reload":
                return executeReload(sender, args);
            case "randomblocks":
                return executeRandomBlocks(sender, args);
            case "menu":
                return executeMenu(sender, args);
            case "return":
                return executeReturn(sender, args);
            case "worlds":
                return executeWorlds(sender, args);
            case "loadSchematic":
            case "loadSchem":
            case "loadS":
                return executeLoadSchematic(sender, args);
            case "saveSchematic":
            case "saveSchem":
            case "saveS":
                return executeSaveSchematic(sender, args);
            case "removeSchematic":
            case "removeSchem":
            case "removeS":
                return executeRemoveSchematic(sender, args);
            case "clear":
                return executeClear(sender, args);
            case "getSkull":
                return getSkull(sender, args);
            case "debug":
                return executeDebug(sender, args);
            default:
                Logger.sendMessage(LangConfig.getMessage("UnknownCommand"), sender);
                return false;
        }
    }
    
    private boolean executeDebug(CommandSender sender, String[] args){
        Player p = getOnlinePlayer(sender, "I2000C");
        String formatA = args[1];
        String formatB = "";
        if(p != null){
            for(int i=0; i<Equipment.EQUIPMENT_KEYS.length; i++){
                int b;
                switch(i){
                    case 0: b = 0; break;
                    case 1: b = 25; break;
                    case 2: b = 50; break;
                    case 3: b = 75; break;
                    case 4: b = 100; break;
                    default: b = 0; break;
                }
                
                String a = String.format("%" + formatA + "s|%%", 
                        Equipment.EQUIPMENT_KEYS[i] + ":", 
                        b);
                Logger.log(a.length());
                Logger.sendMessage(a, sender);
            }
                
            
            //Logger.sendMessage(p.getItemInHand().getType(), p);
        }
        
        /*OffsetMenu.reset();
        OffsetMenu.setCurrentData(new Offset(), null);
        OffsetMenu.openOffsetMenu(p);*/
        return true;
    }
    
    private boolean executeHelp(CommandSender sender, String[] args){
        //<editor-fold defaultstate="collapsed" desc="Code">
        int help_n;
        if(args.length == 1){
            help_n = 1;
        }else if(args.length > 2){
            Logger.sendMessage("&cUsage: &7/alb help [number]", sender);
            return false;
        }else{
            try{
                help_n = Integer.parseInt(args[1]);
            }catch(IllegalArgumentException e){
                help_n = 1;
            }
        }
        String key = "Helpmenu";
        
        int maxPages = 1;
        for(int i=2;;i++){
            if(!LangConfig.getMessages().isConfigurationSection(key + i)){
                maxPages = i-1;
                break;
            }
        }
        
        if(help_n > 1){
            key += help_n;
        }
        if(!LangConfig.getMessages().isConfigurationSection(key)){
            Logger.sendMessage("&cThat page doesn't exist", sender);
            return false;
        }
        for(String str : LangConfig.getMessages().getConfigurationSection(key).getKeys(false)){
            String text = LangConfig.getMessage(key + "." + str);
            Logger.sendMessage(text
                    .replace("%prefix%", NewAmazingLuckyBlocks.getInstance().prefix)
                    .replace("%maxPages%", String.valueOf(maxPages)), sender, false);
        }
        return true;
//</editor-fold>
    }
    
    private boolean executeGive(CommandSender sender, String[] args){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(args.length == 1){
            if(!checkHasPermission(sender, "Commands.Give.GiveMenu")){
                return false;
            }
            
            if(!checkNotConsole(sender)){
                return false;
            }
                        
            GiveMenu.reset();
            GiveMenu.openGiveMenu((Player) sender);
            return true;
        }else{
            Player target;
            int amount;
            String selectedItem = args[1];
            
            switch(args.length){
                case 2:
                    amount = 1;
                    if(!checkNotConsole(sender)){
                        return false;
                    }
                    
                    target = (Player) sender;
                    break;
                case 3:
                    try{
                        amount = Integer.parseInt(args[2]);
                        if(!checkNotConsole(sender)){
                            return false;
                        }
                        
                        target = (Player) sender;
                    }catch(NumberFormatException ex){
                        amount = 1;
                        target = getOnlinePlayer(sender, args[2]);
                        if(target == null){
                            return false;
                        }
                    }
                    break;
                case 4:
                    target = getOnlinePlayer(sender, args[2]);
                    if(target == null){
                        return false;
                    }
                    try{
                        amount = Integer.parseInt(args[3]);
                    }catch(NumberFormatException ex){
                        amount = 1;
                    }
                    break;
                default:
                    Logger.sendMessage("&cUsage: &7/alb give <wands, objects, luckyblocks, luckytool, other_items...> [player] [amount]", sender);
                    return false;
            }
            
            switch(selectedItem){
                case "wands":
                    if(!checkHasPermission(sender, "Commands.Give.Wands")){
                        return false;
                    }
                    
                    String loadwands = LangConfig.getMessage("LoadingWands");
                    Logger.sendMessage(loadwands, sender);
                    for(SpecialItem wand : SpecialItemManager.getWands()){
                        ItemStack stack = wand.getItem();
                        stack.setAmount(amount);
                        target.getInventory().addItem(stack);
                    }
                    break;
                case "objects":
                    if(!checkHasPermission(sender, "Commands.Give.Objects")){
                        return false;
                    }
                    
                    String loadobjects = LangConfig.getMessage("LoadingObjects");                    
                    Logger.sendMessage(loadobjects, sender);
                    for(SpecialItem object : SpecialItemManager.getObjects()){
                        ItemStack stack = object.getItem();
                        stack.setAmount(amount);
                        target.getInventory().addItem(stack);
                    }
                    break;
                case "luckyblocks":
                    if(!checkHasPermission(sender, "Commands.Give.LuckyBlocks")){
                        return false;
                    }
                    
                    String loadblocks = LangConfig.getMessage("LoadingBlocks");
                    Logger.sendMessage(loadblocks, sender);
                    for(LuckyBlockType type : TypeManager.getTypes()){
                        ItemStack stack = type.getItem();
                        stack.setAmount(amount);
                        target.getInventory().addItem(stack);
                    }
                    break;
                default:
                    if(!checkHasPermission(sender, "Commands.Give.OtherItems")){
                        return false;
                    }
                    
                    ItemStack stack = null;
                    
                    // Get special item
                    SpecialItem specialItem = SpecialItemManager.getSpecialItem(selectedItem);
                    
                    // If selected item is not a special item, check if it is a LuckyBlockType
                    if(specialItem == null){
                        for(LuckyBlockType type : TypeManager.getTypes()){
                            if(type.getTypeName().equals(selectedItem)){
                                stack = type.getItem();
                                break;
                            }
                        }
                        
                        if(stack == null){
                            Logger.sendMessage("&cUsage: &7/alb give <wands, objects, luckyblocks, luckytool, other_items...> [player | amount] [amount]", sender);
                            return false;
                        }
                    }else{
                        stack = specialItem.getItem();
                    }
                    
                    String loadSpecialItem = LangConfig.getMessage("LoadingSpecialItem");
                    Logger.sendMessage(loadSpecialItem, sender);                    
                    stack.setAmount(amount);
                    target.getInventory().addItem(stack);
            }
            return true;
        }
//</editor-fold>
    }
    
    private boolean executeTake(CommandSender sender, String[] args){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String usage = "&cUsage: &7/alb take <all, wands, objects, luckyblocks, luckytool, other_items...> [amount | player [amount]]";
        
        if(args.length == 1){
            Logger.sendMessage(usage, sender);
            return false;
        }
            
        Player target;
        int amount;
        String selectedItem = args[1];

        switch(args.length){
            case 2:
                amount = Integer.MAX_VALUE;
                if(!checkNotConsole(sender)){
                    return false;
                }

                target = (Player) sender;
                break;
            case 3:
                try{
                    amount = Integer.parseInt(args[2]);
                    if(!checkNotConsole(sender)){
                        return false;
                    }

                    target = (Player) sender;
                }catch(NumberFormatException ex){
                    amount = Integer.MAX_VALUE;
                    target = getOnlinePlayer(sender, args[2]);
                    if(target == null){
                        return false;
                    }
                }
                break;
            case 4:
                target = getOnlinePlayer(sender, args[2]);
                if(target == null){
                    return false;
                }
                try{
                    amount = Integer.parseInt(args[3]);
                }catch(NumberFormatException ex){
                    amount = Integer.MAX_VALUE;
                }
                break;
            default:
                Logger.sendMessage(usage, sender);
                return false;
        }

        switch(selectedItem){
            case "wands":
                if(!checkHasPermission(sender, "Commands.Give.Wands")){
                    return false;
                }

                OtherUtils.removePlayerItems(target, amount, itemStack -> {
                    SpecialItemName name = SpecialItem.getSpecialItemName(itemStack);
                    return name != null && name.isWand();
                });
                return true;
            case "objects":
                if(!checkHasPermission(sender, "Commands.Give.Objects")){
                    return false;
                }

                OtherUtils.removePlayerItems(target, amount, itemStack -> {
                    SpecialItemName name = SpecialItem.getSpecialItemName(itemStack);
                    return name != null && !name.isWand();
                });
                return true;
            case "luckyblocks":
                if(!checkHasPermission(sender, "Commands.Give.LuckyBlocks")){
                    return false;
                }

                OtherUtils.removePlayerItems(target, amount, itemStack -> {
                    LuckyBlockType type = TypeManager.getType(itemStack);
                    return type != null;
                });
                return true;
            case "all":
                if(!checkHasPermission(sender, "Commands.Give.Wands")){
                    return false;
                }
                if(!checkHasPermission(sender, "Commands.Give.LuckyBlocks")){
                    return false;
                }
                if(!checkHasPermission(sender, "Commands.Give.Objects")){
                    return false;
                }
                
                OtherUtils.removePlayerItems(target, amount, itemStack -> {
                    SpecialItemName name = SpecialItem.getSpecialItemName(itemStack);
                    if(name != null){
                        return true;
                    }
                    
                    LuckyBlockType type = TypeManager.getType(itemStack);
                    return type != null;
                });
                return true;
            default:
                if(!checkHasPermission(sender, "Commands.Give.OtherItems")){
                    return false;
                }

                // Get special item
                SpecialItem specialItem = SpecialItemManager.getSpecialItem(selectedItem);
                if(specialItem != null){
                    OtherUtils.removePlayerItems(target, amount, itemStack -> {
                        SpecialItemName name = SpecialItem.getSpecialItemName(itemStack);
                        return name == specialItem.getSpecialItemName();
                    });
                    return true;
                }

                // If selected item is not a special item, check if it is a LuckyBlockType
                LuckyBlockType luckyBlockType = TypeManager.getType(selectedItem);
                if(luckyBlockType != null){
                    OtherUtils.removePlayerItems(target, amount, itemStack -> {
                        LuckyBlockType type = TypeManager.getType(itemStack);
                        return luckyBlockType.equals(type);
                    });
                    return true;
                }                
                
                Logger.sendMessage(usage, sender);
                return false;
        }
//</editor-fold>
    }
    
    private boolean executeReload(CommandSender sender, String[] args){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!checkHasPermission(sender, "Commands.Reload-permission")){
            return false;
        }
        
        GUIManager.setCurrentMenu(null);
        RewardListMenu.testRewardsPlayerList.clear();
        
        Logger.logAndMessage(LangConfig.getMessage("Reload.config"), sender);
        ConfigManager.getManager().loadConfig();
        boolean coloredLogger = ConfigManager.getConfig().getBoolean("ColoredLogger");
        Logger.initializeLogger(plugin.prefix, coloredLogger);
        
        Logger.logAndMessage(LangConfig.getMessage("Reload.lang"), sender);
        LangConfig.loadConfig();
        plugin.prefix = LangConfig.getMessage("InGamePrefix");
        
        Logger.logAndMessage(LangConfig.getMessage("Reload.worlds"), sender);
        WorldConfig.reloadWorlds();

        SpecialItemManager.reloadSpecialItems();
        
        Logger.logAndMessage(LangConfig.getMessage("Reload.packs"), sender);
        PackManager.loadPacks();
        TrapManager.loadTraps();
        TypeManager.loadTypes();

        LocationManager.saveLocations();
        
        Logger.logAndMessage(LangConfig.getMessage("Reload.reload-finished"), sender);
        return true;
//</editor-fold>
    }
    
    private boolean executeRandomBlocks(CommandSender sender, String[] args){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!checkHasPermission(sender, "Commands.RandomBlocks-permission")){
            return false;
        }
        
        Player player;
        if(args.length == 2 && args[1].equalsIgnoreCase("stop")){
            if(RandomBlocks.taskID != 0){
                Bukkit.getScheduler().cancelTask(RandomBlocks.taskID);

                Logger.sendMessage("&cRandomblock task has been cancelled", sender);
                Logger.sendMessage("&b" + RandomBlocks.blocks_placed + " &aBlocks have been placed", sender);

                RandomBlocks.taskID = 0;
            }else{
                Logger.sendMessage("&cThere isn't any randomblocks task running at the moment", sender);
            }

            return false;
        }else if(args.length == 6){
            if(!checkNotConsole(sender)){
                return false;
            }
            player = (Player) sender;
        }else if(args.length == 7){
            player = getOnlinePlayer(sender, args[6]);
            if(player == null){
                return false;
            }
        }else{
            Logger.sendMessage("&7Usage: " + LangConfig.getMessage("Helpmenu.line3"), sender);
            return false;
        }

        if(RandomBlocks.taskID == 0){
            int radx;
            int rady;
            int radz;
            int blocks;
            try{
                radx = Integer.parseInt(args[1]);
            }catch(NumberFormatException numberFormatException){
                Logger.sendMessage(plugin.prefix + " &cargs[1] must be an &ainteger&c, not &6" + args[1], sender);
                return false;
            }

            try{
                rady = Integer.parseInt(args[2]);
            }catch(NumberFormatException numberFormatException){
                Logger.sendMessage(plugin.prefix + " &cargs[2] must be an &ainteger&c, not &6" + args[2], sender);
                return false;
            }

            try{
                radz = Integer.parseInt(args[3]);
            }catch(NumberFormatException numberFormatException){
                Logger.sendMessage(plugin.prefix + " &cargs[3] must be an &ainteger&c, not &6" + args[3], sender);
                return false;
            }

            try{
                blocks = Integer.parseInt(args[4]);
            }catch (NumberFormatException numberFormatException){
                Logger.sendMessage(plugin.prefix + " &cargs[4] must be an &ainteger&c, not &6" + args[4], sender);
                return false;
            }
            boolean floating_blocks;
            boolean forceMode;
            switch(args[5]){
                case "true":
                    floating_blocks = true;
                    forceMode = false;
                    break;
                case "false":
                    floating_blocks = false;
                    forceMode = false;
                    break;
                case "force":
                    floating_blocks = false;
                    forceMode = true;
                    break;
                default:
                    Logger.sendMessage(plugin.prefix + " &cargs[5] must be &atrue&c, &afalse &cor &aforce&c, not &6" + args[5], sender);
                    return false;
            }

            String placeblocks = LangConfig.getMessage("PlacingBlocks");
            Logger.sendMessage(placeblocks, sender);

            boolean isPlayer = sender instanceof Player;


            String world = player.getWorld().getName();

            if(WorldConfig.isEnabled(world)){
                RandomBlocks rb = new RandomBlocks(radx,rady,radz,blocks,floating_blocks,forceMode,player,isPlayer,sender);
                rb.generatePackets();
                return true;
            }else{
                Logger.sendMessage("&cThe world &b" + world + " &cisn't in the worlds list", sender);
                return false;
            }
        }else{
            Logger.sendMessage("&cThere already is a randomblocks task running", sender);
            Logger.sendMessage("&cWait until it finish or use: &a/alb randomblocks stop", sender);
            return false;
        }
//</editor-fold>
    }
    
    private boolean executeMenu(CommandSender sender, String[] args){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!checkHasPermission(sender, "Commands.Menu-permission")){
            return false;
        }
        
        if(!checkNotConsole(sender)){
            return false;
        }
        
        Player player = (Player) sender;
        if(confirmMenu && ConfigManager.getConfig().getBoolean("Enable-openMenu-confirmation")){
            Logger.sendMessage(LangConfig.getMessage("MenuConfirmation.line1"), sender);
            Logger.sendMessage(LangConfig.getMessage("MenuConfirmation.line2"), sender, false);
            Logger.sendMessage(LangConfig.getMessage("MenuConfirmation.line3"), sender, false);
            confirmMenu = false;
            return false;
        }else{
            confirmMenu = true;
        }
        
        RewardListMenu.testRewardsPlayerList.remove(player);
        ChatListener.removePlayer(player);
                
        //Open main menu
        Editor editor = EditorType.MAIN_MENU.getEditor();
        editor.createNewItem(player, p -> {
            GUIManager.setCurrentMenu(null);
            p.closeInventory();
        }, null);
        
        return true;
//</editor-fold>
    }
    
    private boolean executeReturn(CommandSender sender, String[] args){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!checkHasPermission(sender, "Commands.Menu-permission")){
            return false;
        }
        
        if(!checkNotConsole(sender)){
            return false;
        }
        
        Player player = (Player) sender;
        RewardListMenu.testRewardsPlayerList.remove(player);
        ChatListener.removePlayer(player);
        
        Menu currentMenu = GUIManager.getCurrentMenu();
        if(currentMenu == null){
            Logger.sendMessage("&cYou haven't opened any menu recently", sender);
            return false;
        }else{
            confirmMenu = true;
            currentMenu.openToPlayer(player, false);
            return true;
        }
//</editor-fold>
    }
    
    private boolean executeWorlds(CommandSender sender, String[] args){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!checkHasPermission(sender, "Commands.Worlds-permission")){
            return false;
        }
        
        switch(args.length){
            case 1:
                // /alb worlds
                //Open worlds menu
                if(!checkNotConsole(sender)){
                    return false;
                }
                WorldMenu.reset();
                WorldMenu.openWorldsMenu((Player) sender);
                return true;
            case 2:
                // /alb worlds list
                if(args[1].equals("list")){
                    WorldConfig.WorldListMode currentMode = WorldConfig.getWorldListMode();
                    String currentModeMessage = LangConfig.getMessage("World-management1.currentMode")
                            .replace("%worldListMode%", currentMode.toString());
                    Logger.sendMessage(currentModeMessage, sender);
                    Logger.sendMessage(LangConfig.getMessage("World-management1.title"), sender);
                    Logger.sendMessage(LangConfig.getMessage("World-management1.separator"), sender);
                    WorldConfig.getWorlds().forEach(worldName -> {
                        String message = LangConfig.getMessage("World-management1.worldName").replace("%world%", worldName);
                        Logger.sendMessage(message, sender);
                    });
                    return true;
                // /alb worlds changeListMode
                }else if(args[1].equals("changeListMode")) {
                    WorldConfig.WorldListMode nextMode = WorldConfig.getWorldListMode().next();
                    WorldConfig.setWorldListMode(nextMode);
                    String message = LangConfig.getMessage("World-management2.worldListModeChanged")
                            .replace("%worldListMode%", nextMode.toString());
                    Logger.sendMessage(message, sender);
                    return true;
                }else if(args[1].equals("add")){
                    Logger.sendMessage("&cUsage: &7/alb worlds add <world>", sender);
                    return false;
                }else if(args[1].equals("delete")){
                    Logger.sendMessage("&cUsage: &7/alb worlds delete <world>", sender);
                    return false;
                }else{
                    Logger.sendMessage("&cUsage: &7/alb worlds [list|changeListMode|add|delete]", sender);
                    return false;
                }
            case 3:
                // /alb worlds [add|delete] <world>
                if(args[1].equals("add")){
                    String worldName = args[2];                    
                    String message;
                    if(Bukkit.getWorld(worldName) == null) {
                        message = LangConfig.getMessage("World-management2.worldNotExists")
                                .replace("%world%", worldName);
                        Logger.sendMessage(message, sender);
                        return false;
                    }
                    
                    if(WorldConfig.addWorld(worldName)) {
                        message = LangConfig.getMessage("World-management2.worldAdded")
                                .replace("%world%", worldName);
                        Logger.sendMessage(message, sender);
                        return true;
                    } else {
                        message = LangConfig.getMessage("World-management2.worldAlreadyAdded")
                                .replace("%world%", worldName);
                        Logger.sendMessage(message, sender);
                        return false;
                    }
                }else if(args[1].equals("delete")) {
                    String worldName = args[2];                    
                    String message;
                    if(Bukkit.getWorld(worldName) == null) {
                        message = LangConfig.getMessage("World-management2.worldNotExists")
                                .replace("%world%", worldName);
                        Logger.sendMessage(message, sender);
                        return false;
                    }
                    
                    if(WorldConfig.addWorld(worldName)) {
                        message = LangConfig.getMessage("World-management2.worldDeleted")
                                .replace("%world%", worldName);
                        Logger.sendMessage(message, sender);
                        return true;
                    } else {
                        message = LangConfig.getMessage("World-management2.worldAlreadyDeleted")
                                .replace("%world%", worldName);
                        Logger.sendMessage(message, sender);
                        return false;
                    }
                } else {
                    Logger.sendMessage("&cUsage: &7/alb worlds [add|delete] <world>", sender);
                    return false;
                }
            default:
                String unknowncommand = LangConfig.getMessage("UnknownCommand");
                Logger.sendMessage(unknowncommand, sender);
                return false;
        }
//</editor-fold>
    }
    
    private boolean executeLoadSchematic(CommandSender sender, String[] args){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!checkHasPermission(sender, "Commands.Schematic")){
            return false;
        }
        
        if(!checkNotConsole(sender)){
            return false;
        }
        
        if(NewAmazingLuckyBlocks.getWorldEditPlugin() == null){
            Logger.sendMessage("&cYou need WorldEdit in order to use that command", sender);
            return false;
        }
        
        if(args.length != 2){
            Logger.sendMessage("&cUse: &7/alb " + args[0] + " <schematicName>", sender);
            return false;
        }
        
        Player p = (Player) sender;
        File file = new File(NewAmazingLuckyBlocks.getInstance().getDataFolder(), "schematics" + File.separator + args[1]);
        if(!file.exists()){
            Logger.sendMessage("&cSchematic &6\"" + args[1] + "\" &cdoesn't exist", sender);
            return false;
        }
        
        try{
            Schematic s = new Schematic();
            s.loadFromFile(file, p.getWorld());
            s.copyToPlayerClipboard(p);
            Logger.sendMessage("&aSchematic &6\"" + args[1] + "\" &ahas been &eloaded &ainto your clipboard", sender);
            return true;
        }catch(Exception ex){
            Logger.sendMessage("&cAn error occurred. More info in the Console", sender);
            Logger.err("An error occurred:");
            ex.printStackTrace();
            return false;
        }
//</editor-fold>
    }
    
    private boolean executeSaveSchematic(CommandSender sender, String[] args){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!checkHasPermission(sender, "Commands.Schematic")){
            return false;
        }
        
        if(!checkNotConsole(sender)){
            return false;
        }
        
        if(NewAmazingLuckyBlocks.getWorldEditPlugin() == null){
            Logger.sendMessage("&cYou need WorldEdit in order to use that command", sender);
            return false;
        }
        
        if(args.length != 2){
            Logger.sendMessage("&cUse: &7/alb " + args[0] + " <schematicName>", sender);
            return false;
        }
        
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
            if(!args[1].endsWith(".schematic")){
                Logger.sendMessage("&cSchematic files must end in &6.schematic", sender);
                return false;
            }
        }else{
            if(!args[1].endsWith(".schem")){
                Logger.sendMessage("&cSchematic files must end in &6.schem", sender);
                return false;
            }
        }
        
        Player p = (Player) sender;
        File file = new File(NewAmazingLuckyBlocks.getInstance().getDataFolder(), "schematics" + File.separator + args[1]);
        
        try{
            Schematic s = new Schematic();
            s.loadFromPlayerClipboard(p);
            s.saveToFile(file, p.getWorld());
            Logger.sendMessage("&aSchematic &6\"" + args[1] + "\" &ahas been &bsaved &afrom your clipboard", sender);
            return true;
        }catch(Exception ex){
            if(ex.getClass().getName().contains("EmptyClipboardException")){
                Logger.sendMessage("&cYour clipboard is empty", sender);
            }else{
                Logger.sendMessage("&cAn error occurred. More info in the Console", sender);
                Logger.err("An error occurred:");
                ex.printStackTrace();
            }                
            return false;
        }
//</editor-fold>
    }
    
    private boolean executeRemoveSchematic(CommandSender sender, String[] args){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!checkHasPermission(sender, "Commands.Schematic")){
            return false;
        }
        
        if(NewAmazingLuckyBlocks.getWorldEditPlugin() == null){
            Logger.sendMessage("&cYou need WorldEdit in order to use that command", sender);
            return false;
        }
        
        if(args.length != 2){
            Logger.sendMessage("&cUse: &7/alb " + args[0] + " <schematicName>", sender);
            return false;
        }
        
        File file = new File(NewAmazingLuckyBlocks.getInstance().getDataFolder(), "schematics" + File.separator + args[1]);
        if(!file.exists()){
            Logger.sendMessage("&cSchematic &6\"" + args[1] + "\" &cdoesn't exist", sender);
            return false;
        }
        
        if(file.delete()){
            Logger.sendMessage("&aSchematic &6\"" + args[1] + "\" &ahas been &4removed", sender);
            return true;
        }else{
            Logger.sendMessage("&aSchematic &6\"" + args[1] + "\" &ccouldn't be removed", sender);
            return false;
        }
//</editor-fold>
    }
    
    private boolean executeClear(CommandSender sender, String[] args){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!checkHasPermission(sender, "Commands.Clear-permission")){
            return false;
        }
        
        if(args.length == 1){
            LocationManager.removeLocations();
            Logger.sendMessage("&aAll LuckyBlocks were removed", sender);
            return true;
        }
        String world = args[1];
        if(WorldConfig.isEnabled(world)){
            LocationManager.removeLocations(Bukkit.getWorld(world));
            Logger.sendMessage("&aAll LuckyBlocks of the world &b" + world + " &awere removed", sender);
            return true;
        }
        
        Logger.sendMessage("&cThe world &b" + world + " &cisn't in the world list", sender);
        return false;
//</editor-fold>
    }
    
    private boolean getSkull(CommandSender sender, String[] args){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!checkHasPermission(sender, "Commands.GetSkull-permission")){
            return false;
        }
        
        if(!checkNotConsole(sender)){
            return false;
        }
        
        if(args.length != 2){
            Logger.sendMessage("&cUsage: &7/alb getSkull <textureID>", sender);
            return false;
        }
        
        try{
            Texture texture = new Texture(args[1]);
            ItemStack textureItem = TextureManager.getItemSkullStack();
            TextureManager.setTexture(textureItem, texture);
            ((Player) sender).getInventory().addItem(textureItem);
            return true;
        }catch(InvalidTextureException ex){
            Logger.sendMessage("&cInvalid texture ID", sender);
            return false;
        }catch(URLTextureException ex){
            Logger.sendMessage("&cAn error occured while loading texture:", sender);
            Logger.sendMessage("    &4" + ex, sender);
            return false;
        }catch(TextureException ex){
            return false;
        }
//</editor-fold>
    }
    
    private static final String[] CMD_LIST = {"help", "reload", "give", "take", "randomblocks", "menu", "return", "worlds", 
        "loadSchematic", "loadSchem", "loadS", "saveSchematic", "saveSchem", "saveS", 
        "removeSchematic", "removeSchem", "removeS", "clear", "getSkull"};
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!sender.hasPermission(ConfigManager.getConfig().getString("Commands.Tab-completer"))){
            return null;
        }
        
        List<String> ls = new ArrayList<>();
        
        if(args[0].isEmpty()){
            ls = Arrays.asList(CMD_LIST);
        }else if(args.length == 1){
            for(String str : CMD_LIST){
                if(str.toLowerCase().startsWith(args[0].toLowerCase())){
                    ls.add(str);
                }
            }
        }else if(args.length == 2){
            switch(args[0]){
                case "take":
                    if("all".startsWith(args[1].toLowerCase())){
                        ls.add("all");
                    }
                case "give":
                    if("wands".startsWith(args[1].toLowerCase())){
                        ls.add("wands");
                    }
                    if("objects".startsWith(args[1].toLowerCase())){
                        ls.add("objects");
                    }
                    if("luckyblocks".startsWith(args[1].toLowerCase())){
                        ls.add("luckyblocks");
                    }
                    for(String specialName : SpecialItemManager.getSpecialNames()){
                        if(specialName.startsWith(args[1].toLowerCase())){
                            ls.add(specialName);
                        }
                    }
                    for(LuckyBlockType type : TypeManager.getTypes()){
                        if(type.getTypeName().startsWith(args[1].toLowerCase())){
                            ls.add(type.getTypeName());
                        }
                    }
                    break;
                case "reload":
                case "return":
                case "menu":
                    break;
                case "randomblocks":
                    if("stop".startsWith(args[1].toLowerCase())){
                        ls.add("stop");
                    }
                    if("5".startsWith(args[1].toLowerCase())){
                        ls.add("5");
                    }                    
                    break;
                case "worlds":
                    if("add".startsWith(args[1].toLowerCase())){
                        ls.add("add");
                    }
                    if("delete".startsWith(args[1].toLowerCase())){
                        ls.add("delete");
                    }
                    if("list".startsWith(args[1].toLowerCase())){
                        ls.add("list");
                    }                    
                    if("changeListMode".startsWith(args[1].toLowerCase())){
                        ls.add("changeListMode");
                    }                    
                    break;
                case "loadSchematic":
                case "loadSchem":
                case "loadS":
                case "removeSchematic":
                case "removeSchem":
                case "removeS":
                    File schematicFolder = new File(NewAmazingLuckyBlocks.getInstance().getDataFolder(), "schematics");
                    if(schematicFolder.exists()){
                        for(String str : schematicFolder.list()){
                            if(str.toLowerCase().startsWith(args[1].toLowerCase())){
                                ls.add(str);
                            }
                        }
                    }
                    break;
                case "clear":
                    List<String> aux = ls;
                    WorldConfig.getWorlds().forEach(world -> aux.add(world));
                    break;
                default:
                    for(Player p : Bukkit.getOnlinePlayers()){
                        if(p.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase())){
                            ls.add(p.getName());
                        }
                    }
                    break;
            }
        }else if(args.length == 3){
            if(args[0].equalsIgnoreCase("worlds")) {
                if(args[1].equalsIgnoreCase("add")) {
                    for(World world : Bukkit.getWorlds()) {
                        if(!WorldConfig.getWorlds().contains(world.getName())) {
                            ls.add(world.getName());
                        }
                    }
                } else if(args[1].equalsIgnoreCase("delete")) {
                    ls.addAll(WorldConfig.getWorlds());
                }
                return ls;
            }
            
            if(args[0].equalsIgnoreCase("randomblocks")){
                try{
                    int test = Integer.parseInt(args[1]);
                    ls.add("5");
                    return ls;
                }catch(NumberFormatException ex){
                }
            }
            
            switch(args[1]){
                case "wands":
                case "objects":
                case "luckyblocks":
                    ls.add("5");
                    break;
                default:
                    for(Player p : Bukkit.getOnlinePlayers()){
                        if(p.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase())){
                            ls.add(p.getName());
                        }
                    }
            }
        }else if(args.length == 4){
            if(args[0].equalsIgnoreCase("randomblocks")){
                ls.add("5");
            }else if(args[0].equalsIgnoreCase("worlds") && args[1].equalsIgnoreCase("set")){
                if("enabled".startsWith(args[3].toLowerCase())){
                    ls.add("enabled");
                }
                if("disabled".startsWith(args[3].toLowerCase())){
                    ls.add("disabled");
                }
            }else{
                for(Player p : Bukkit.getOnlinePlayers()){
                    if(p.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase())){
                        ls.add(p.getName());
                    }
                }
            }
        }else if(args.length == 5){
            if(args[0].equalsIgnoreCase("randomblocks")){
                ls.add("5");
            }else{
                for(Player p : Bukkit.getOnlinePlayers()){
                    if(p.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase())){
                        ls.add(p.getName());
                    }
                }
            }
        }else if(args.length == 6){
            if(args[0].equalsIgnoreCase("randomblocks")){
                if("true".startsWith(args[5].toLowerCase())){
                    ls.add("true");
                }
                if("false".startsWith(args[5].toLowerCase())){
                    ls.add("false");
                }
                if("force".startsWith(args[5].toLowerCase())){
                    ls.add("force");
                }
            }else{
                for(Player p : Bukkit.getOnlinePlayers()){
                if(p.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase())){
                    ls.add(p.getName());
                }
            }
            }
        }else{
            for(Player p : Bukkit.getOnlinePlayers()){
                if(p.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase())){
                    ls.add(p.getName());
                }
            }
        }
        
        Collections.sort(ls);
        return ls;
    //</editor-fold>
    }
}
