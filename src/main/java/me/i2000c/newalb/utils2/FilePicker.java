package me.i2000c.newalb.utils2;

import com.cryptomorin.xseries.XMaterial;
import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.utils.logger.LogLevel;
import me.i2000c.newalb.utils.logger.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FilePicker{
    private static final int MAX_TITLE_LONG = 32;
    private static File currentDirectory;
    private static File rootDirectory;
    private static FilenameFilter mainFilter;
    
    private static final int MENU_SIZE = 50;
    private static int index;
    private static int maxPage;
    
    private static boolean inventoriesRegistered = false;
    
    public static void resetFilePicker(){
        if(!inventoriesRegistered){
            //Register inventories
            InventoryListener.registerInventory(CustomInventoryType.FILE_PICKER, FILE_PICKER_FUNCTION);
            
            inventoriesRegistered = true;
        }
        
        index = 0;
        currentDirectory = null;
        rootDirectory = null;
        mainFilter = null;
    }
    
    public static void openFileMenu(Player player, File rootPath, File currentPath, FilenameFilter filter){
        //<editor-fold defaultstate="collapsed" desc="Code">
        try{
            if(rootPath == null){
                openFileMenu(player, null, currentPath.getAbsolutePath(), filter);
            }else{
                openFileMenu(player, rootPath.getAbsolutePath(), currentPath.getAbsolutePath(), filter);
            }
        }catch(NullPointerException ex){
            Logger.log("File: \"" + currentPath + "\" doesn't have parent (Maybe it is the root)", LogLevel.INFO);
        }
//</editor-fold>
    }
    
    public static void openFileMenu(Player player, String rootPath, String currentPath, FilenameFilter filter){
        //<editor-fold defaultstate="collapsed" desc="Code">
        mainFilter = filter;
        if(rootPath != null){
            rootDirectory = new File(rootPath);
        }
        File oldDirectory = currentDirectory;
        currentDirectory = new File(currentPath);
        if(currentDirectory == null){
            Logger.sendMessage("&cDirectory &6" + currentPath + " &ccannot be null", player);
            currentDirectory = oldDirectory;
            return;
        }else if(!currentDirectory.exists()){
            Logger.sendMessage("&cDirectory &6" + currentPath + " &cdoesn't exist", player);
            currentDirectory = oldDirectory;
            return;
        }else if(currentDirectory.isFile()){
            Logger.sendMessage("&6" + currentPath + " &cisn't a directory", player);
            currentDirectory = oldDirectory;
            return;
        }else if(!currentDirectory.canRead()){
            Logger.sendMessage("&cYou can't read directory &6" + currentPath + " &c(Read Permission)", player);
            currentDirectory = oldDirectory;
            return;
        }else if(rootDirectory != null && currentDirectory.getName().equals(rootDirectory.getName())){
            Logger.sendMessage("&cYou cannot go into an upper directory", player);
            currentDirectory = oldDirectory;
            return;
        }
        
        try{
            String title = "&e&l" + currentDirectory.getName();
            if(title.length() > MAX_TITLE_LONG){
                title = title.substring(0, MAX_TITLE_LONG-5) + "...";
            }
            
            Inventory inv = GUIFactory.createInventory(CustomInventoryType.FILE_PICKER, 54, title);
            
            ItemStack refresh = ItemBuilder.newItem(XMaterial.WATER_BUCKET)
                    .withDisplayName("&bRefresh data")
                    .build();
            inv.setItem(53, refresh);
            
            List<File> fileList;
            File[] files = currentDirectory.listFiles(filter);
            if(rootDirectory != null && currentDirectory.getParentFile().equals(rootDirectory)){
                if(files == null){
                    fileList = null;
                }else{
                    fileList = Arrays.asList(currentDirectory.listFiles(filter));
                }
            }else{
                if(files == null){
                    fileList = Arrays.asList(new File(".."));
                }else{
                    fileList = new ArrayList();
                    fileList.add(new File(".."));
                    fileList.addAll(Arrays.asList(currentDirectory.listFiles(filter)));
                }
            }
            
            if(fileList == null){
                inv.setItem(51, GUIItem.getCurrentPageItem(index+1, maxPage));
                player.openInventory(inv);
                return;
            }
            
            maxPage = fileList.size() / MENU_SIZE;
            /*if(fileList.size() % MENU_SIZE == 0){
            maxPage = fileList.size() / MENU_SIZE;
            }else{
            maxPage = (fileList.size() / MENU_SIZE) + 1;
            }*/            
            
            int n = Integer.min(fileList.size()-MENU_SIZE*index, MENU_SIZE);
            for(int i=0; i<n; i++){
                File file = fileList.get(i+index*MENU_SIZE);
                XMaterial material = extensionToMaterial(file);
                ItemBuilder builder = ItemBuilder.newItem(material);
                if(file.getName().equals("..")){
                    builder.withDisplayName("&6Name: &b..");
                }else if(file.isDirectory()){
                    builder.withDisplayName("&6Name: &b" + file.getName());
                    if(file.list() == null){
                        builder.addLoreLine("&6Directory elements: &b?");
                    }else{
                        builder.addLoreLine("&6Directory elements: &b" + file.list().length);
                    }
                    builder.addLoreLine("&6id: &b" + i);
                }else{
                    builder.withDisplayName("&6Name: &b" + file.getName());
                    builder.addLoreLine(title);
                    builder.addLoreLine("&6File size: " + getFormatedSize(file.length()));
                    builder.addLoreLine("&6id: &b" + i);
                }
                
                inv.setItem(i, builder.build());
            }
            player.openInventory(inv);
        }catch(NullPointerException | IllegalArgumentException ex){
            currentDirectory = oldDirectory;
            ex.printStackTrace();
        }
//</editor-fold>
    }
    
    private static final InventoryFunction FILE_PICKER_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        if(currentDirectory == null){
            return;
        }
        
        if(e.getClickedInventory().equals(e.getView().getTopInventory())){
            if(e.getCurrentItem().getType() == Material.AIR){
                return;
            }
            switch(e.getSlot()){
                case 50:
                    //Goto previous page
                    index--;
                    if(index < 0){
                        index = maxPage;
                    }
                    openFileMenu(p, rootDirectory, currentDirectory, mainFilter);
                    break;
                case 51:
                    //Goto page 1
                    index = 0;
                    openFileMenu(p, rootDirectory, currentDirectory, mainFilter);
                    break;
                case 52:
                    //Goto next page
                    index++;
                    if(index > maxPage){
                        index = 0;
                    }
                    openFileMenu(p, rootDirectory, currentDirectory, mainFilter);
                    break;
                case 53:
                    //Refresh data
                    openFileMenu(p, rootDirectory, currentDirectory, mainFilter);
                    break;
                default:
                    String displayName = ItemBuilder.fromItem(e.getCurrentItem())
                            .getDisplayName();
                    String itemName = Logger.stripColor(displayName);
                    String path = itemName.split(":")[1].trim();
                    File file = new File(currentDirectory, path);
                    if(file.isFile()){
                        Bukkit.getPluginManager().callEvent(new FilePickerEvent(p, file.getAbsoluteFile()));
                    }else{
                        index = 0;
                        if(path.equals("..")){
                            openFileMenu(p, rootDirectory, currentDirectory.getParentFile(), mainFilter);
                        }else{
                            openFileMenu(p, rootDirectory, file, mainFilter);
                        }
                    }
                }
            }
//</editor-fold>
    };
    
    private static String getFormatedSize(long size){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(size < (1<<10)){
            return "&b" + size + " &6B";
        }else if(size > (1<<10) && size < (1<<20)){
            return "&b" + BigDecimal.valueOf(size/1024.0).setScale(3, RoundingMode.HALF_UP).doubleValue() + " &6KiB";
        }else if(size > (1<<20) && size < (1<<30)){
            return "&b" + BigDecimal.valueOf(size/(1024.0*1024.0)).setScale(3, RoundingMode.HALF_UP).doubleValue() + " &6MiB";
        }else{
            return "&b" + BigDecimal.valueOf(size/(1024.0*1024.0*1024.0)).setScale(3, RoundingMode.HALF_UP).doubleValue() + " &6GiB";
        }
//</editor-fold>
    }
    
    private static XMaterial extensionToMaterial(File file){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(file == null){
            return null;
        }else if(file.isDirectory()){
            if(file.getName().equals("..")){
                return XMaterial.ENDER_CHEST;
            }else{
                return XMaterial.BOOKSHELF;
            }            
        }else{
            String data[] = file.getName().split("[.]");
            String extension = data[data.length-1];
            switch(extension){
                case "schem":
                case "schematic":
                    return XMaterial.MAP;
                case "txt":
                case "yml":
                case "yaml":
                case "json":
                case "properties":
                    return XMaterial.OAK_SIGN;
                case "jpg":
                case "jpeg":
                case "png":
                case "gif":
                case "bmp":
                    return XMaterial.PAINTING;
                case "doc":
                case "docx":
                    return XMaterial.BOOK;
                case "jar":
                case "exe":
                case "msi":
                case "bat":
                case "cmd":
                    return XMaterial.COMMAND_BLOCK;
                default:
                    return XMaterial.ITEM_FRAME;
            }
        }
//</editor-fold>
    }
}
