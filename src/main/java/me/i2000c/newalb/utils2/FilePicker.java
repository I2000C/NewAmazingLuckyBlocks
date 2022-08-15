package me.i2000c.newalb.utils2;

import com.cryptomorin.xseries.XMaterial;
import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.functions.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.GUIItem;
import me.i2000c.newalb.listeners.inventories.GUIPagesAdapter;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.listeners.inventories.InventoryLocation;
import me.i2000c.newalb.listeners.inventories.Menu;
import me.i2000c.newalb.utils.logger.LogLevel;
import me.i2000c.newalb.utils.logger.Logger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FilePicker extends Editor<File>{
    public FilePicker(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        InventoryListener.registerInventory(CustomInventoryType.FILE_PICKER, FILE_PICKER_FUNCTION);
        fileAdapter = new GUIPagesAdapter<>(
                MENU_SIZE,
                (file, index) -> {
                    XMaterial material = extensionToMaterial(file);
                    ItemBuilder builder = ItemBuilder.newItem(material);
                    if(file.getName().equals("..")){
                        builder.withDisplayName("&6Name: &b..");
                    }else if(file.isDirectory()){
                        builder.withDisplayName("&6Name: &b" + file.getName());
                        builder.addLoreLine("&6id: &b" + index);
                        if(file.list() == null){
                            builder.addLoreLine("&6Directory elements: &b?");
                        }else{
                            builder.addLoreLine("&6Directory elements: &b" + file.list().length);
                        }
                    }else{
                        builder.withDisplayName("&6Name: &b" + file.getName());
                        builder.addLoreLine("&6id: &b" + index);
                        builder.addLoreLine("&6File size: " + getFormatedSize(file.length()));
                    }
                    return builder.build();
                }
        );
        fileAdapter.setPreviousPageSlot(PREVIOUS_PAGE_SLOT);
        fileAdapter.setCurrentPageSlot(CURRENT_PAGE_SLOT);
        fileAdapter.setNextPageSlot(NEXT_PAGE_SLOT);
//</editor-fold>
    }
    
    private static final int MAX_TITLE_LONG = 32;    
    private static final int MENU_SIZE = 45;
    private static final int PREVIOUS_PAGE_SLOT = 48;
    private static final int CURRENT_PAGE_SLOT = 49;
    private static final int NEXT_PAGE_SLOT = 50;
    private static GUIPagesAdapter<File> fileAdapter;
    
    private static FilenameFilter filenameFilter;
    private static File currentDirectory;
    private static File rootDirectory;    
    
    public static void setFilenameFilter(FilenameFilter filter){
        filenameFilter = filter;
    }
    public static void setCurrentDirectory(File file){
        currentDirectory = file;
    }
    public static void setRootDirectory(File file){
        rootDirectory = file;
    }
    
    @Override
    protected void reset(){
        fileAdapter.goToMainPage();
    }
    
    @Override
    protected void newItem(Player player){
        openFileMenu(player, rootDirectory, currentDirectory);
    }
    
    @Override
    protected void editItem(Player player){
        openFileMenu(player, rootDirectory, currentDirectory);
    }
    
    private void openFileMenu(Player player, File rootPath, File currentPath){
        //<editor-fold defaultstate="collapsed" desc="Code">
        try{
            if(rootPath == null){
                openFileMenu(player, null, currentPath.getAbsolutePath());
            }else{
                openFileMenu(player, rootPath.getAbsolutePath(), currentPath.getAbsolutePath());
            }
        }catch(NullPointerException ex){
            Logger.log("File: \"" + currentPath + "\" doesn't have parent (Maybe it is the root)", LogLevel.WARN);
        }
//</editor-fold>
    }
    
    private void openFileMenu(Player player, String rootPath, String currentPath){
        //<editor-fold defaultstate="collapsed" desc="Code">
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
            
            Menu menu = GUIFactory.newMenu(CustomInventoryType.FILE_PICKER, 54, title);
            
            ItemStack refresh = ItemBuilder.newItem(XMaterial.WATER_BUCKET)
                    .withDisplayName("&bRefresh data")
                    .build();
            
            menu.setItem(45, GUIItem.getBackItem());
            menu.setItem(53, refresh);
            
            List<File> fileList;
            File[] files = currentDirectory.listFiles(filenameFilter);
            if(rootDirectory != null && currentDirectory.getParentFile().equals(rootDirectory)){
                if(files == null){
                    fileList = null;
                }else{
                    fileList = Arrays.asList(currentDirectory.listFiles(filenameFilter));
                }
            }else{
                if(files == null){
                    fileList = Arrays.asList(new File(".."));
                }else{
                    fileList = new ArrayList<>();
                    fileList.add(new File(".."));
                    fileList.addAll(Arrays.asList(currentDirectory.listFiles(filenameFilter)));
                }
            }
            
            if(fileList == null){
                menu.openToPlayer(player, false);
                return;
            }
            
            fileAdapter.setItemList(fileList);
            fileAdapter.updateMenu(menu);
            
            menu.openToPlayer(player, false);
        }catch(NullPointerException | IllegalArgumentException ex){
            currentDirectory = oldDirectory;
            ex.printStackTrace();
        }
//</editor-fold>
    }
    
    private final InventoryFunction FILE_PICKER_FUNCTION = e -> {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        if(currentDirectory == null){
            return;
        }
        
        if(e.getLocation() == InventoryLocation.TOP){
            if(e.getCurrentItem().getType() == Material.AIR){
                return;
            }
            switch(e.getSlot()){
                case 45:
                    // Go to previous menu
                    onBack.accept(player);
                    break;
                case PREVIOUS_PAGE_SLOT:
                    // Go to previous page
                    if(fileAdapter.goToPreviousPage()){
                        openFileMenu(player, rootDirectory, currentDirectory);
                    }
                    break;
                case CURRENT_PAGE_SLOT:
                    // Go to main page
                    if(fileAdapter.goToMainPage()){
                        openFileMenu(player, rootDirectory, currentDirectory);
                    }
                    break;
                case NEXT_PAGE_SLOT:
                    // Go to next page
                    if(fileAdapter.goToNextPage()){
                        openFileMenu(player, rootDirectory, currentDirectory);
                    }
                    break;
                case 53:
                    // Refresh data
                    openFileMenu(player, rootDirectory, currentDirectory);
                    break;
                default:
                    String displayName = ItemBuilder.fromItem(e.getCurrentItem())
                            .getDisplayName();
                    String itemName = Logger.stripColor(displayName);
                    String path = itemName.split(":")[1].trim();
                    File file = new File(currentDirectory, path);
                    if(file.isFile()){
                        onNext.accept(player, file);
                    }else{
                        fileAdapter.goToMainPage();
                        if(path.equals("..")){
                            openFileMenu(player, rootDirectory, currentDirectory.getParentFile());
                        }else{
                            openFileMenu(player, rootDirectory, file);
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
