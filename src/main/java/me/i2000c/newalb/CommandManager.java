package me.i2000c.newalb;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.i2000c.newalb.custom_outcomes.menus.RewardListMenu;
import me.i2000c.newalb.custom_outcomes.menus.GUIManager;
import me.i2000c.newalb.custom_outcomes.menus.MainMenu;
import me.i2000c.newalb.custom_outcomes.utils.LuckyBlockType;
import me.i2000c.newalb.custom_outcomes.utils.PackManager;
import me.i2000c.newalb.custom_outcomes.utils.TypeManager;
import me.i2000c.newalb.custom_outcomes.utils.rewards.TrapManager;
import me.i2000c.newalb.lang_utils.LangLoader;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.listeners.interact.SpecialItemManager;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.GiveMenu;
import me.i2000c.newalb.utils.LocationManager;
import me.i2000c.newalb.utils.RandomBlocks;
import me.i2000c.newalb.utils.WorldList;
import me.i2000c.newalb.utils.WorldMenu;
import me.i2000c.newalb.utils.logger.LogLevel;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils.textures.InvalidTextureException;
import me.i2000c.newalb.utils.textures.Texture;
import me.i2000c.newalb.utils.textures.TextureException;
import me.i2000c.newalb.utils.textures.TextureManager;
import me.i2000c.newalb.utils.textures.URLTextureException;
import me.i2000c.newalb.utils2.Schematic;
import org.bukkit.Bukkit;
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
        String noPermissionMessage = LangLoader.getMessages().getString("NoPermission");
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
            Logger.sendMessage(LangLoader.getMessages().getString("HelpMessage"), sender);
            return true;
        }else switch(args[0]){
            case "help":
                return executeHelp(sender, args);
            case "give":
                return executeGive(sender, args);
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
                Logger.sendMessage(LangLoader.getMessages().getString("UnknownCommand"), sender);
                return false;
        }
    }
    
    private boolean executeDebug(CommandSender sender, String[] args){
        Player p = getOnlinePlayer(sender, "I2000C");
        if(p != null){
            Logger.sendMessage(p.getItemInHand().getType(), p);
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
        if(help_n > 1){
            key = key + help_n;
        }
        if(!LangLoader.getMessages().isConfigurationSection(key)){
            Logger.sendMessage("&cThat page doesn't exist", sender);
            return false;
        }
        for(String str : LangLoader.getMessages().getConfigurationSection(key).getKeys(false)){
            String text = LangLoader.getMessages().getString(key + "." + str);
            Logger.sendMessage(text.replace("%prefix%", NewAmazingLuckyBlocks.getInstance().prefix), sender);
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
                    
                    String loadwands = LangLoader.getMessages().getString("LoadingWands");
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
                    
                    String loadobjects = LangLoader.getMessages().getString("LoadingObjects");                    
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
                    
                    String loadblocks = LangLoader.getMessages().getString("LoadingBlocks");
                    Logger.sendMessage(loadblocks, sender);
                    for(LuckyBlockType type : TypeManager.getTypes()){
                        ItemStack stack = type.getItem();
                        stack.setAmount(amount);
                        target.getInventory().addItem(stack);
                    }
                    break;
                case "luckytool":
                    if(!checkHasPermission(sender, "Commands.Give.LuckyTool")){
                        return false;
                    }
                    
                    String loadtool = LangLoader.getMessages().getString("LoadingLuckyTool");
                    Logger.sendMessage(loadtool, sender);
                    ItemStack luckyToolItem = SpecialItemManager.getLuckyTool().getItem();
                    luckyToolItem.setAmount(amount);
                    target.getInventory().addItem(luckyToolItem);
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
                    
                    String loadSpecialItem = LangLoader.getMessages().getString("LoadingSpecialItem");
                    Logger.sendMessage(loadSpecialItem, sender);                    
                    stack.setAmount(amount);
                    target.getInventory().addItem(stack);
            }
            return true;
        }
//</editor-fold>
    }
    
    private boolean executeReload(CommandSender sender, String[] args){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!checkHasPermission(sender, "Commands.Reload-permission")){
            return false;
        }
        
        GUIManager.setCurrentInventory(null);

        ConfigManager.reloadConfig();
        ConfigManager.saveConfig();
        LangLoader.reloadMessages();

        WorldList.reloadAll();

        SpecialItemManager.reloadSpecialItems();

        PackManager.loadPacks();
        TrapManager.loadTraps();
        TypeManager.loadTypes();

        LocationManager.saveLocations();

        RewardListMenu.testRewardsPlayerList.clear();

        String reload1 = LangLoader.getMessages().getString("Reload.line1");
        String reload2 = LangLoader.getMessages().getString("Reload.line2");
        String reload3 = LangLoader.getMessages().getString("Reload.line3");
        Logger.sendMessage(reload1, sender);
        Logger.sendMessage(reload2, sender);
        Logger.sendMessage(reload3, sender);

        plugin.prefix = LangLoader.getMessages().getString("InGamePrefix");
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
            Logger.sendMessage("&7Usage: " + LangLoader.getMessages().getString("Helpmenu.line3"), sender);
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

            String placeblocks = LangLoader.getMessages().getString("PlacingBlocks");
            Logger.sendMessage(placeblocks, sender);

            boolean isPlayer = sender instanceof Player;


            String world = player.getWorld().getName();

            if(WorldList.isRegistered(world)){
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
            Logger.sendMessage(LangLoader.getMessages().getString("MenuConfirmation.line1"), sender);
            Logger.sendMessage(LangLoader.getMessages().getString("MenuConfirmation.line2"), sender);
            Logger.sendMessage(LangLoader.getMessages().getString("MenuConfirmation.line3"), sender);
            confirmMenu = false;
            return false;
        }else{
            confirmMenu = true;
        }
        
        RewardListMenu.testRewardsPlayerList.remove(player);
        ChatListener.removePlayer(player);
                
        //Open main menu
        MainMenu.reset();
        MainMenu.openMainMenu(player);
        
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
        
        if(GUIManager.getCurrentInventory() == null){
            Logger.sendMessage("&cYou haven't opened any menu recently", sender);
            return false;
        }else{
            confirmMenu = true;
            player.openInventory(GUIManager.getCurrentInventory());
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
                    WorldList.updateWorlds(false);
                    Logger.sendMessage(LangLoader.getMessages().getString("World-management1.line1"), sender);
                    Logger.sendMessage(LangLoader.getMessages().getString("World-management1.line2"), sender);
                    WorldList.getWorlds().forEach((worldName, worldType) -> {
                        String message = LangLoader.getMessages().getString("World-management1.worldName").replace("%world%", worldName);
                        if(worldType){
                            message += LangLoader.getMessages().getString("World-management1.enabledWorld");
                        }else{
                            message += LangLoader.getMessages().getString("World-management1.disabledWorld");
                        }
                        Logger.sendMessage(message, sender);
                    });
                    return true;
                }else if(args[1].equals("set")){
                    Logger.sendMessage("&cUsage: &7/alb worlds set <world/*> <enabled/disabled>", sender);
                    return false;
                }else if(args[1].equals("toggle")){
                    Logger.sendMessage("&cUsage: &7/alb worlds toggle <world/*>", sender);
                    return false;
                }else{
                    Logger.sendMessage("&cUsage: &7/alb worlds [list/set/toggle]", sender);
                    return false;
                }
            case 3:
                if(args[1].equals("toggle")){
                    String worldName = args[2];
                    
                    if(worldName.equals("*")){
                        String message = "&3Toggled all worlds";
                        WorldList.toggleAllWorlds();
                        Logger.sendMessage(message, sender);
                        return true;
                    }else{
                        String message = LangLoader.getMessages().getString("World-management2.line1").replace("%world%", worldName);
                        if(Bukkit.getWorld(worldName) != null){
                            boolean enabled = !WorldList.getWorlds().get(worldName);

                            if(enabled){
                                message += LangLoader.getMessages().getString("World-management1.enabledWorld");
                            }else{
                                message += LangLoader.getMessages().getString("World-management1.disabledWorld");
                            }
                            WorldList.setWorldEnabled(worldName, enabled);
                            Logger.sendMessage(message, sender);
                            return true;
                        }else{
                            Logger.sendMessage(LangLoader.getMessages().getString("World-management2.line4").replace("%world%", worldName), sender);
                            return false;
                        }
                    }
                }else{
                    Logger.sendMessage("&cUsage: &7/alb worlds toggle <world/*>", sender);
                    return false;
                }
            case 4:
                // /alb worlds set <world> <type>
                if(args[1].equals("set")){
                    String worldName = args[2];
                    String worldEnabled = args[3];
                    if(!args[3].equals("enabled") && !args[3].equals("disabled")){
                        Logger.sendMessage("&cUsage: /alb worlds set <world/*> <enabled/disabled>", sender);
                        return false;
                    }
                    
                    boolean enabled = worldEnabled.equals("enabled");
                    if(worldName.equals("*")){
                        String message = "Setted all worlds to ";
                        if(enabled){
                            message += LangLoader.getMessages().getString("World-management1.enabledWorld");
                        }else{
                            message += LangLoader.getMessages().getString("World-management1.disabledWorld");
                        }
                        WorldList.setAllWorldsEnabled(enabled);
                        Logger.sendMessage(message, sender);
                        return true;
                    }else{
                        String message = LangLoader.getMessages().getString("World-management2.line1").replace("%world%", worldName);
                        if(Bukkit.getWorld(worldName) != null){
                            if(enabled){
                                message += LangLoader.getMessages().getString("World-management1.enabledWorld");
                            }else{
                                message += LangLoader.getMessages().getString("World-management1.disabledWorld");
                            }
                            WorldList.setWorldEnabled(worldName, enabled);
                            Logger.sendMessage(message, sender);
                            return true;
                        }else{
                            Logger.sendMessage(LangLoader.getMessages().getString("World-management2.line4").replace("%world%", worldName), sender);
                            return false;
                        }
                    }                        
                }else{
                    Logger.sendMessage("&cUsage: &7/alb worlds set <world/*> <enabled/disabled>", sender);
                    return false;
                }
            default:
                String unknowncommand = LangLoader.getMessages().getString("UnknownCommand");
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
            Logger.log("An error occurred:", LogLevel.INFO);
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
                Logger.log("An error occurred:", LogLevel.INFO);
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
        if(WorldList.isRegistered(world)){
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
    
    private static final String[] CMD_LIST = {"help", "reload", "give", "randomblocks", "menu", "return", "worlds", 
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
                    if("set".startsWith(args[1].toLowerCase())){
                        ls.add("set");
                    }
                    if("toggle".startsWith(args[1].toLowerCase())){
                        ls.add("toggle");
                    }
                    if("list".startsWith(args[1].toLowerCase())){
                        ls.add("list");
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
                    WorldList.getWorlds().forEach((world, enabled) -> {if(enabled) aux.add(world);});
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
            if(args[0].equalsIgnoreCase("worlds") && 
                    (args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("toggle"))){
                for(String world : ConfigManager.getConfig().getStringList("Worlds-list")){
                    String worldName = world.split(";")[0];
                    if(worldName.toLowerCase().startsWith(args[2].toLowerCase())){
                        ls.add(worldName);
                    }
                }
                ls.add("*");
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
                case "objects":
                case "luckyblock":
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
