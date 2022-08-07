package me.i2000c.newalb.utils2;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.utils.logger.Logger;
import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.i2000c.newalb.listeners.inventories.CustomInventoryType;
import me.i2000c.newalb.listeners.inventories.GUIFactory;
import me.i2000c.newalb.listeners.inventories.InventoryFunction;
import me.i2000c.newalb.listeners.inventories.InventoryListener;
import me.i2000c.newalb.utils.logger.LogLevel;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
            
            ItemStack back = new ItemStack(Material.ENDER_PEARL);
            ItemMeta meta = back.getItemMeta();
            meta.setDisplayName("&2Previous page");
            back.setItemMeta(meta);
            inv.setItem(50, back);
            
            ItemStack next = new ItemStack(Material.MAGMA_CREAM);
            meta = next.getItemMeta();
            meta.setDisplayName("&5Next page");
            next.setItemMeta(meta);
            inv.setItem(52, next);
            
            ItemStack page = new ItemStack(Material.NAME_TAG);
            meta = page.getItemMeta();
            meta.setDisplayName("&6Pag &3? &6of &5?");
            page.setItemMeta(meta);
            
            ItemStack refresh = new ItemStack(Material.WATER_BUCKET);
            meta = refresh.getItemMeta();
            meta.setDisplayName("&bRefresh data");
            refresh.setItemMeta(meta);
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
                inv.setItem(51, page);
                player.openInventory(inv);
                return;
            }
            
            maxPage = fileList.size() / MENU_SIZE;
            /*if(fileList.size() % MENU_SIZE == 0){
            maxPage = fileList.size() / MENU_SIZE;
            }else{
            maxPage = (fileList.size() / MENU_SIZE) + 1;
            }*/
            
            meta = page.getItemMeta();
            meta.setDisplayName("&6Pag &3" + (index+1) + " &6of &5" + (maxPage+1));
            page.setItemMeta(meta);
            inv.setItem(51, page);
            
            
            ItemStack stack;
            int n = Integer.min(fileList.size()-MENU_SIZE*index, MENU_SIZE);
            for(int i=0; i<n; i++){
                File file = fileList.get(i+index*MENU_SIZE);
                if(file.getName().equals("..")){
                    stack = new ItemStack(Material.ENDER_CHEST);
                    meta = stack.getItemMeta();
                    meta.setDisplayName("&6Name: &b..");
                    stack.setItemMeta(meta);
                }else if(file.isDirectory()){
                    stack = new ItemStack(extensionToMaterial(file));
                    meta = stack.getItemMeta();
                    meta.setDisplayName("&6Name: &b" + file.getName());
                    List<String> lore;
                    if(file.list() == null){
                        lore = Arrays.asList("&6Directory elements: &b?", "&6id: &b" + i);
                    }else{
                        lore = Arrays.asList("&6Directory elements: &b" + file.list().length, "&6id: &b" + i);
                    }
                    meta.setLore(lore);
                    stack.setItemMeta(meta);
                }else{
                    stack = new ItemStack(extensionToMaterial(file));
                    meta = stack.getItemMeta();
                    meta.setDisplayName("&6Name: &b" + file.getName());
                    List<String> lore = Arrays.asList("&6File size: " + getFormatedSize(file.length()), "&6id: &b" + i);
                    meta.setLore(lore);
                    stack.setItemMeta(meta);
                }
                inv.setItem(i, stack);
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
                    String itemName = Logger.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
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
    
    private static Material extensionToMaterial(File file){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(file == null){
            return null;
        }else if(file.isDirectory()){
            return Material.BOOKSHELF;
        }else{
            String data[] = file.getName().split("[.]");
            String extension = data[data.length-1];
            switch(extension){
                case "schem":
                case "schematic":
                    return Material.MAP;
                case "txt":
                case "yml":
                case "yaml":
                case "json":
                case "properties":
                    return Material.SIGN;
                case "jpg":
                case "jpeg":
                case "png":
                case "gif":
                case "bmp":
                    return Material.PAINTING;
                case "doc":
                case "docx":
                    return Material.BOOK;
                case "jar":
                case "exe":
                case "msi":
                case "bat":
                case "cmd":
                    return XMaterial.COMMAND_BLOCK.parseMaterial();
                default:
                    return Material.ITEM_FRAME;
            }
        }
//</editor-fold>
    }
}
