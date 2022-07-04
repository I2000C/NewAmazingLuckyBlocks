package me.i2000c.newalb;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.i2000c.newalb.custom_outcomes.menus.FinishMenu;
import me.i2000c.newalb.custom_outcomes.menus.GUIManager;
import me.i2000c.newalb.custom_outcomes.menus.MainMenu;
import me.i2000c.newalb.custom_outcomes.utils.LuckyBlockType;
import me.i2000c.newalb.custom_outcomes.utils.PackManager;
import me.i2000c.newalb.custom_outcomes.utils.TypeManager;
import me.i2000c.newalb.custom_outcomes.utils.rewards.TrapManager;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.GiveMenu;
import me.i2000c.newalb.lang_utils.LangLoader;
import me.i2000c.newalb.utils.LocationManager;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.RandomBlocks;
import me.i2000c.newalb.utils.SpecialItem;
import me.i2000c.newalb.utils.SpecialItemManager;
import me.i2000c.newalb.utils.WorldList;
import me.i2000c.newalb.utils.WorldMenu;
import me.i2000c.newalb.utils2.Schematic;
import me.i2000c.newalb.utils2.TextureManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    
    public String intColorSet(int uses){
        String intColor;  
        if(uses >= 10){
          intColor = "&a";  
        }else if(uses < 10 && uses >= 5){
          intColor = "&e";    
        }else if(uses < 5 && uses >= 1){
          intColor = "&6";    
        }else{
          intColor = "&4";    
        }
        return intColor;
    }
  
    public static boolean confirmMenu = false;
  
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!cmd.getName().equalsIgnoreCase("alb") && !cmd.getName().equalsIgnoreCase("nalb")){
            return false;
        }
        
        if(args.length == 0){
            sender.sendMessage(plugin.name + " " + ChatColor.GREEN + plugin.version);
            sender.sendMessage(Logger.color(LangLoader.getMessages().getString("HelpMessage")));
            return true;
        }else switch(args[0]){
            case "help":
                return executeHelp(sender, args);
            case "give":
                return executeGive(sender, args);
            case "reload":
                return executeReload(sender, args);
            case "randomblock":
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
                sender.sendMessage(Logger.color(LangLoader.getMessages().getString("UnknownCommand")));
                return false;
        }
    }
    
    private boolean executeDebug(CommandSender sender, String[] args){
        Player p = Bukkit.getPlayer("I2000C");
        Logger.sendMessage(p.getItemInHand().getType(), p);
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
            sender.sendMessage(Logger.color("&cUsage: &7/alb help [number]"));
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
            sender.sendMessage(Logger.color("&cThat page doesn't exist"));
            return false;
        }
        for(String str : LangLoader.getMessages().getConfigurationSection(key).getKeys(false)){
            String text = LangLoader.getMessages().getString(key + "." + str);
            sender.sendMessage(Logger.color(text.replaceAll("%prefix%", NewAmazingLuckyBlocks.getInstance().prefix)));
        }
        return true;
//</editor-fold>
    }
    
    private boolean executeGive(CommandSender sender, String[] args){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String permission;
        String noConsoleMessage = "&cYou can't send this command from the console";
        if(args.length == 1){
            permission = ConfigManager.getConfig().getString("Commands.Give.GiveMenu");
            if(!sender.hasPermission(permission)){
                String noperm = Logger.color(LangLoader.getMessages().getString("NoPermission"));
                sender.sendMessage(noperm);
                return false;
            }
            if(!(sender instanceof Player)){
                Logger.log(noConsoleMessage);
                return false;
            }else{
                GiveMenu.reset();
                GiveMenu.openGiveMenu((Player) sender);
                return true;
            }
        }else{
            Player target;
            int amount;
            String selectedItem = args[1];
            
            switch(args.length){
                case 2:
                    amount = 1;
                    if(!(sender instanceof Player)){
                        Logger.log(noConsoleMessage);
                        return false;
                    }
                    target = (Player) sender;
                    break;
                case 3:
                    try{
                        amount = Integer.parseInt(args[2]);
                        if(!(sender instanceof Player)){
                            Logger.log(noConsoleMessage);
                            return false;
                        }
                        target = (Player) sender;
                    }catch(NumberFormatException ex){
                        amount = 1;
                        target = Bukkit.getPlayer(args[2]);
                        if(target == null){
                            sender.sendMessage(Logger.color("&cPlayer " + args[2] + "&c is offline"));
                            return false;
                        }
                    }
                    break;
                case 4:
                    target = Bukkit.getPlayer(args[2]);
                    if(target == null){
                        sender.sendMessage(Logger.color("&cPlayer " + args[2] + "&c is offline"));
                        return false;
                    }
                    try{
                        amount = Integer.parseInt(args[3]);
                    }catch(NumberFormatException ex){
                        amount = 1;
                    }
                    break;
                default:
                    Logger.sendMessage("&cUsage: &7/alb give <wands, objects, luckyblocks, luckytool, other_items...> [player | amount] [amount]", sender);
                    return false;
            }
            
            switch(selectedItem){
                case "wands":
                    if(!sender.hasPermission(ConfigManager.getConfig().getString("Commands.Give.Wands"))){
                        Logger.sendMessage(LangLoader.getMessages().getString("NoPermission"), sender);
                        return false;
                    }
                    
                    String loadwands = LangLoader.getMessages().getString("LoadingWands");
                    Logger.sendMessage(loadwands, sender);
                    for(SpecialItem wand : SpecialItemManager.getWands()){
                        ItemStack stack = wand.getItem();
                        target.getInventory().addItem(stack);
                    }
                    break;
                case "objects":
                    if(!sender.hasPermission(ConfigManager.getConfig().getString("Commands.Give.Objects"))){
                        Logger.sendMessage(LangLoader.getMessages().getString("NoPermission"), sender);
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
                    if(!sender.hasPermission(ConfigManager.getConfig().getString("Commands.Give.LuckyBlocks"))){
                        Logger.sendMessage(LangLoader.getMessages().getString("NoPermission"), sender, false);
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
                    if(!sender.hasPermission(ConfigManager.getConfig().getString("Commands.Give.LuckyTool"))){
                        Logger.sendMessage(LangLoader.getMessages().getString("NoPermission"), sender, false);
                        return false;
                    }
                    
                    String loadtool = LangLoader.getMessages().getString("LoadingLuckyTool");
                    Logger.sendMessage(loadtool, sender);
                    target.getInventory().addItem(SpecialItemManager.getLuckyTool().getItem());
                    break;
                default:
                    if(!sender.hasPermission(ConfigManager.getConfig().getString("Commands.Give.OtherItems"))){
                        Logger.sendMessage(LangLoader.getMessages().getString("NoPermission"), sender, false);
                        return false;
                    }
                    
                    ItemStack stack = null;
                    SpecialItem specialItem = SpecialItemManager.getSpecialItem(selectedItem);
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
        String permission = ConfigManager.getConfig().getString("Commands.Reload-permission");
        if(sender.hasPermission(permission)){
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
            
            FinishMenu.testRewardsPlayerList.clear();
            
            String reload1 = Logger.color(LangLoader.getMessages().getString("Reload.line1"));
            String reload2 = Logger.color(LangLoader.getMessages().getString("Reload.line2"));
            String reload3 = Logger.color(LangLoader.getMessages().getString("Reload.line3"));
            Logger.sendMessage(reload1, sender);
            Logger.sendMessage(reload2, sender);
            Logger.sendMessage(reload3, sender);
            
            plugin.prefix = LangLoader.getMessages().getString("InGamePrefix");
            return true;
        }else{
            String noperm = Logger.color(LangLoader.getMessages().getString("NoPermission"));
            sender.sendMessage(noperm);
            return false;
        }
//</editor-fold>
    }
    
    private boolean executeRandomBlocks(CommandSender sender, String[] args){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String permission = ConfigManager.getConfig().getString("Commands.RandomBlocks-permission");
        if(sender.hasPermission(permission)){
            Player player;
            if(args.length == 2 && args[1].equalsIgnoreCase("stop")){
                if(RandomBlocks.taskID != 0){
                    Bukkit.getScheduler().cancelTask(RandomBlocks.taskID);
                    
                    sender.sendMessage(Logger.color("&cRandomblock task has been cancelled"));
                    sender.sendMessage(Logger.color("&b" + RandomBlocks.blocks_placed + " &aBlocks have been placed"));
                    
                    RandomBlocks.taskID = 0;
                }else{
                    sender.sendMessage(Logger.color("&cThere isn't any randomblock task running at the moment"));
                }
                
                return false;
            }else if(args.length == 6){
                if(!(sender instanceof Player)){
                    Logger.log("&cYou can't send this command from the console");
                    return false;
                }
                player = (Player) sender;
            }else if(args.length == 7){
                player = Bukkit.getServer().getPlayer(args[6]);
                if(player == null){
                    sender.sendMessage(Logger.color("&cPlayer " + args[6] + "&c is offline"));
                    return false;
                }
            }else{
                sender.sendMessage(Logger.color("&7Usage: " + LangLoader.getMessages().getString("Helpmenu.line3")));
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
                    sender.sendMessage(Logger.color(plugin.prefix + " &cargs[1] must be an &ainteger&c, not &6" + args[1]));
                    return false;
                }
                
                try{
                    rady = Integer.parseInt(args[2]);
                }catch(NumberFormatException numberFormatException){
                    sender.sendMessage(Logger.color(plugin.prefix + " &cargs[2] must be an &ainteger&c, not &6" + args[2]));
                    return false;
                }
                
                try{
                    radz = Integer.parseInt(args[3]);
                }catch(NumberFormatException numberFormatException){
                    sender.sendMessage(Logger.color(plugin.prefix + " &cargs[3] must be an &ainteger&c, not &6" + args[3]));
                    return false;
                }
                
                try{
                    blocks = Integer.parseInt(args[4]);
                }catch (NumberFormatException numberFormatException){
                    sender.sendMessage(Logger.color(plugin.prefix + " &cargs[4] must be an &ainteger&c, not &6" + args[4]));
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
                        sender.sendMessage(Logger.color(plugin.prefix + " &cargs[5] must be &atrue&c, &afalse &cor &aforce&c, not &6" + args[5]));
                        return false;
                }
                
                
                String placeblocks = Logger.color(LangLoader.getMessages().getString("PlacingBlocks"));
                sender.sendMessage(placeblocks);
                
                boolean isPlayer = sender instanceof Player;
                
                
                String world = player.getWorld().getName();
                
                if(WorldList.isRegistered(world)){
                    RandomBlocks rb = new RandomBlocks(radx,rady,radz,blocks,floating_blocks,forceMode,player,isPlayer,sender);
                    rb.generatePackets();
                    return true;
                }else{
                    sender.sendMessage(Logger.color("&cThe world &b" + world + " &cisn't in the worlds list"));
                    return false;
                }
            }else{
                sender.sendMessage(ChatColor.RED + "There already is a randomblock task running");
                sender.sendMessage(ChatColor.RED + "Wait until it finish or use: " + ChatColor.AQUA + "/alb randomblock stop");
                return false;
            }
        }else{
            String noperm = Logger.color(LangLoader.getMessages().getString("NoPermission"));
            sender.sendMessage(noperm);
            return false;
        }
//</editor-fold>
    }
    
    private boolean executeMenu(CommandSender sender, String[] args){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String permission = ConfigManager.getConfig().getString("Commands.Menu-permission");
        if(!sender.hasPermission(permission)){
            String noperm = Logger.color(LangLoader.getMessages().getString("NoPermission"));
            sender.sendMessage(noperm);
            return false;
        }
        if(!(sender instanceof Player)){
            Logger.log(plugin.name+ChatColor.RED+" You can't send this command from the console");
            return false;
        }
        
        Player player = (Player) sender;
        if(confirmMenu && ConfigManager.getConfig().getBoolean("Enable-openMenu-confirmation")){
            player.sendMessage(Logger.color(LangLoader.getMessages().getString("MenuConfirmation.line1")));
            player.sendMessage(Logger.color(LangLoader.getMessages().getString("MenuConfirmation.line2")));
            player.sendMessage(Logger.color(LangLoader.getMessages().getString("MenuConfirmation.line3")));
            confirmMenu = false;
            return false;
        }else{
            confirmMenu = true;
        }
        
        FinishMenu.testRewardsPlayerList.remove(player);
        ChatListener.removePlayer(player);
                
        //Open main menu
        MainMenu.reset();
        MainMenu.openMainMenu(player);
        
        return true;
//</editor-fold>
    }
    
    private boolean executeReturn(CommandSender sender, String[] args){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String permission = ConfigManager.getConfig().getString("Commands.Menu-permission");
        if(!sender.hasPermission(permission)){
            String noperm = Logger.color(LangLoader.getMessages().getString("NoPermission"));
            sender.sendMessage(noperm);
            return false;
        }
        if(!(sender instanceof Player)){
            Logger.log("&cYou can't send this command from the console");
            return false;
        }
        
        Player player = (Player) sender;
        FinishMenu.testRewardsPlayerList.remove(player);
        ChatListener.removePlayer(player);
        
        if(GUIManager.getCurrentInventory() == null){
            player.sendMessage(Logger.color("&cYou haven't opened any menu recently"));
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
        String permission = ConfigManager.getConfig().getString("Commands.Worlds-permission");
        if(!sender.hasPermission(permission)){
            String noperm = Logger.color(LangLoader.getMessages().getString("NoPermission"));
            sender.sendMessage(noperm);
            return false;
        }
        
        switch(args.length){
            case 1:
                // /alb worlds
                //Open worlds menu
                if(sender instanceof Player){
                    WorldMenu.reset();
                    WorldMenu.openWorldsMenu((Player) sender);
                    return true;
                }else{
                    sender.sendMessage(Logger.color("&cYou can't use this command from the Console"));
                    return false;
                }
                //break;
            case 2:
                // /alb worlds list
                if(args[1].equalsIgnoreCase("list")){
                    WorldList.updateWorlds(false);
                    sender.sendMessage(Logger.color(LangLoader.getMessages().getString("World-management1.line1")));
                    sender.sendMessage(Logger.color(LangLoader.getMessages().getString("World-management1.line2")));
                    WorldList.getWorlds().forEach((worldName, worldType) -> {
                        String message = (LangLoader.getMessages().getString("World-management1.worldName").replace("%world%", worldName));
                        if(worldType){
                            message += LangLoader.getMessages().getString("World-management1.enabledWorld");
                        }else{
                            message += LangLoader.getMessages().getString("World-management1.disabledWorld");
                        }
                        Logger.sendMessage(message, sender);
                    });
                    return true;
                }else{
                    sender.sendMessage(Logger.color("&cUsage: &7/alb worlds list"));
                    return false;
                }
                
                //break;
            case 4:
                // /alb worlds set <world> <type>
                if(args[1].equalsIgnoreCase("set")){
                    String worldName = args[2];
                    String worldEnabled = args[3];
                    if(!args[3].equals("enabled") && !args[3].equals("disabled")){
                        sender.sendMessage(Logger.color("&cUsage: /alb set <world> <enabled/disabled>"));
                        return false;
                    }
                    
                    boolean enabled = worldEnabled.equals("enabled");
                    
                    String message = LangLoader.getMessages().getString("World-management2.line1").replaceAll("%world%", worldName);
                    if(Bukkit.getWorld(worldName) != null){
                        if(enabled){
                            message += LangLoader.getMessages().getString("World-management1.enabledWorld");
                        }else{
                            message += LangLoader.getMessages().getString("World-management1.disabledWorld");
                        }
                        sender.sendMessage(Logger.color(message));
                        WorldList.setWorldEnabled(worldName, enabled);
                        return true;
                    }else{
                        sender.sendMessage(Logger.color(LangLoader.getMessages().getString("World-management2.line4").replaceAll("%world%", worldName)));
                        return false;
                    }
                }else{
                    sender.sendMessage(Logger.color("&cUsage: /alb set <world> <enabled/disabled>"));
                    return false;
                }
                
                //break;
            default:
                String unknowncommand = Logger.color(LangLoader.getMessages().getString("UnknownCommand"));
                sender.sendMessage(unknowncommand);
                return false;
                //break;
        }
//</editor-fold>
    }
    
    private boolean executeLoadSchematic(CommandSender sender, String[] args){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String permission = ConfigManager.getConfig().getString("Commands.Schematic");
        if(!sender.hasPermission(permission)){
            String noperm = Logger.color(LangLoader.getMessages().getString("NoPermission"));
            sender.sendMessage(noperm);
            return false;
        }
        
        if(!(sender instanceof Player)){
            Logger.sendMessage("&cYou can't use this command from the Console", sender);
            return false;
        }
        
        if(NewAmazingLuckyBlocks.getWorldEditPlugin() == null){
            Logger.sendMessage("&cYou need WorldEdit in order to use that command", sender);
            return false;
        }
        
        if(args.length != 2){
            sender.sendMessage(Logger.color("&cUse: &7/alb " + args[0] + " <schematicName>"));
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
            Logger.log("An error occurred:", Logger.LogLevel.ERROR);
            ex.printStackTrace();
            return false;
        }
//</editor-fold>
    }
    
    private boolean executeSaveSchematic(CommandSender sender, String[] args){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String permission = ConfigManager.getConfig().getString("Commands.Schematic");
        if(!sender.hasPermission(permission)){
            String noperm = Logger.color(LangLoader.getMessages().getString("NoPermission"));
            sender.sendMessage(noperm);
            return false;
        }
        
        if(!(sender instanceof Player)){
            Logger.sendMessage("&cYou can't use this command from the Console", sender);
            return false;
        }
        
        if(NewAmazingLuckyBlocks.getWorldEditPlugin() == null){
            Logger.sendMessage("&cYou need WorldEdit in order to use that command", sender);
            return false;
        }
        
        if(args.length != 2){
            sender.sendMessage(Logger.color("&cUse: &7/alb " + args[0] + " <schematicName>"));
            return false;
        }
        
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
            if(!args[1].endsWith(".schematic")){
                sender.sendMessage(Logger.color("&cSchematic files must end in &6.schematic"));
                return false;
            }
        }else{
            if(!args[1].endsWith(".schem")){
                sender.sendMessage(Logger.color("&cSchematic files must end in &6.schem"));
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
                Logger.log("An error occurred:", Logger.LogLevel.ERROR);
                ex.printStackTrace();
            }                
            return false;
        }
//</editor-fold>
    }
    
    private boolean executeRemoveSchematic(CommandSender sender, String[] args){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String permission = ConfigManager.getConfig().getString("Commands.Schematic");
        if(!sender.hasPermission(permission)){
            String noperm = Logger.color(LangLoader.getMessages().getString("NoPermission"));
            sender.sendMessage(noperm);
            return false;
        }
        
        if(NewAmazingLuckyBlocks.getWorldEditPlugin() == null){
            Logger.sendMessage("&cYou need WorldEdit in order to use that command", sender);
            return false;
        }
        
        if(args.length != 2){
            sender.sendMessage(Logger.color("&cUse: &7/alb " + args[0] + " <schematicName>"));
            return false;
        }
        
        File file = new File(NewAmazingLuckyBlocks.getInstance().getDataFolder(), "schematics" + File.separator + args[1]);
        if(!file.exists()){
            sender.sendMessage(Logger.color("&cSchematic &6\"" + args[1] + "\" &cdoesn't exist"));
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
        String permission = ConfigManager.getConfig().getString("Commands.Clear-permission");
        if(!sender.hasPermission(permission)){
            String noperm = Logger.color(LangLoader.getMessages().getString("NoPermission"));
            sender.sendMessage(noperm);
            return false;
        }
        
        if(args.length == 1){
            LocationManager.removeLocations();
            sender.sendMessage(Logger.color("&aAll LuckyBlocks were removed"));
            return true;
        }
        String world = args[1];
        if(WorldList.isRegistered(world)){
            LocationManager.removeLocations(Bukkit.getWorld(world));
            sender.sendMessage(Logger.color("&aAll LuckyBlocks of the world &b" + world + " &awere removed"));
            return true;
        }
        
        sender.sendMessage(Logger.color("&cThe world &b" + world + " &cisn't in the world list"));
        return false;
//</editor-fold>
    }
    
    private boolean getSkull(CommandSender sender, String[] args){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String permission = ConfigManager.getConfig().getString("Commands.GetSkull-permission");
        if(!sender.hasPermission(permission)){
            String noperm = Logger.color(LangLoader.getMessages().getString("NoPermission"));
            sender.sendMessage(noperm);
            return false;
        }
        
        if(!(sender instanceof Player)){
            sender.sendMessage(Logger.color("&cYou can't use this command from the Console"));
            return false;
        }
        
        if(args.length != 2){
            Logger.sendMessage("&cUsage: &7/alb getSkull <textureID>", sender);
            return false;
        }
        
        try{
            TextureManager.Texture texture = new TextureManager.Texture(args[1]);
            ItemStack textureItem = TextureManager.getItemSkullStack();
            TextureManager.setTexture(textureItem, texture);
            ((Player) sender).getInventory().addItem(textureItem);
            return true;
        }catch(TextureManager.InvalidHeadException ex){
            Logger.sendMessage("&cInvalid texture ID", sender);
            return false;
        }
//</editor-fold>
    }
    
    private static final String[] CMD_LIST = {"help", "reload", "give", "randomblock", "menu", "return", "worlds", 
        "loadSchematic", "loadSchem", "loadS", "saveSchematic", "saveSchem", "saveS", 
        "removeSchematic", "removeSchem", "removeS", "clear", "getSkull"};
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!sender.hasPermission(ConfigManager.getConfig().getString("Commands.Tab-completer"))){
            return null;
        }
        
        List<String> ls = new ArrayList<>();
        
        if(args[0].equalsIgnoreCase("")){
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
                case "randomblock":
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
            if(args[0].equalsIgnoreCase("worlds") && args[1].equalsIgnoreCase("set")){
                for(String world : ConfigManager.getConfig().getStringList("Worlds-list")){
                    String worldName = world.split(";")[0];
                    if(worldName.toLowerCase().startsWith(args[2].toLowerCase())){
                        ls.add(worldName);
                    }
                }
                return ls;
            }
            
            if(args[0].equalsIgnoreCase("randomblock")){
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
            if(args[0].equalsIgnoreCase("randomblock")){
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
            if(args[0].equalsIgnoreCase("randomblock")){
                ls.add("5");
            }else{
                for(Player p : Bukkit.getOnlinePlayers()){
                    if(p.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase())){
                        ls.add(p.getName());
                    }
                }
            }
        }else if(args.length == 6){
            if(args[0].equalsIgnoreCase("randomblock")){
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