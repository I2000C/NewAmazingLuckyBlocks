package me.i2000c.newalb.utils.menus;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import lombok.NonNull;
import lombok.Setter;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.api.gui.MenuItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.PaginatedEditorMenu;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.misc.OtherUtils;

@Setter
public class FilePickerMenu extends PaginatedEditorMenu<Path, Path> {
    
    private static final int SIZE_1KiB = 1 << 10;
    private static final int SIZE_1MiB = 1 << 20;
    private static final int SIZE_1GiB = 1 << 30;
    
    private final FilePickerMenu previousMenu;
    private Predicate<Path> filenameFilter;
    @NonNull private Path rootPath = NewAmazingLuckyBlocks.getInstance().getDataFolder().toPath();
    
    private FilePickerMenu(FilePickerMenu previousMenu) {
        super("", MenuSize.SIZE_6_ROWS, true, MenuSize.SIZE_5_ROWS, 48, 49, 50);
        this.previousMenu = previousMenu;
    }
    
    public FilePickerMenu() {
        this(null);
    }
    
    private boolean canGoUp() {
        Path parent = item.getParent();
        if(parent == null) {
            return false;
        } else {
            return parent.normalize().startsWith(rootPath.normalize());
        }
    }
    
    @Override
    protected Path createNewItem() {
        return rootPath;
    }
    
    @Override
    public List<Path> getItemList() {
        List<Path> files = new ArrayList<>();
        
        // Add ".." if can go up
        if(canGoUp()) {
            files.add(item.getParent());
        }
        
        try {
            Files.list(item)
                 .filter(Optional.ofNullable(filenameFilter).orElse(path -> true))
                 .forEach(files::add);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        
        return files;
    }
    
    @Override
    public MenuItem mapItemToPage(Path path, int index) {
        XMaterial material = extensionToMaterial(path);
        ItemStackWrapper wrapper = ItemStackWrapper.newItem(material);
        if(path.equals(item.getParent())) {
            wrapper.setDisplayName("&6Name: &b..");
        } else if(Files.isDirectory(path)) {
            wrapper.setDisplayName("&6Name: &b" + path.getFileName());
            wrapper.addLoreLine("&6id: &b" + index);
            
            try {
                int numFiles = (int) Files.list(item)
                                          .filter(Optional.ofNullable(filenameFilter).orElse(p -> true))
                                          .count();
                wrapper.addLoreLine("&6Directory elements: &b" + numFiles);
            } catch(IOException ex) {
                wrapper.addLoreLine("&6Directory elements: &b?");
                ex.printStackTrace();
            }
        } else {
            wrapper.setDisplayName("&6Name: &b" + path.getFileName());
            wrapper.addLoreLine("&6id: &b" + index);
            try {
                wrapper.addLoreLine("&6File size: &b" + getFormatedSize(Files.size(path)));
            } catch(IOException ex) {
                wrapper.addLoreLine("&6File size: &b?");
                ex.printStackTrace();
            }
        }
        
        Consumer<MenuClickEvent> action = e -> {
            Path newPath = path;
            if(Files.isRegularFile(newPath)) {
                // Path is file
                onNext(e.getPlayer(), newPath);
            } else {
                // Path is directory
                if(item.getFileName().toString().equals("..") && previousMenu != null) {
                    previousMenu.openToPlayer(e.getPlayer());
                } else {
                    FilePickerMenu menu = new FilePickerMenu(this);
                    menu.setFilenameFilter(filenameFilter);
                    menu.setRootPath(rootPath);
                    menu.setItemToEdit(newPath);
                    menu.setOnBack(this.getOnBack());
                    menu.setOnNext(this.getOnNext());
                    menu.openToPlayer(e.getPlayer());
                }
            }
        };
        
        return new MenuItem(wrapper, action);
    }
    
    @Override
    protected void buildMenu(Player player) {
        String title = "&e&l" + item.getFileName();
        setTitle(title);
        
        ItemStack refresh = ItemStackWrapper.newItem(XMaterial.WATER_BUCKET)
                                            .setDisplayName("&bRefresh data")
                                            .toItemStack();
        
        setBackItem(45);
        setItem(53, refresh, e -> {
            resetPagination();
            openToPlayer(player);
        });
    }
    
    private static String getFormatedSize(long size) {
        BigDecimal sizeBigDecimal = BigDecimal.valueOf(size);
        if(size < SIZE_1KiB) {
            return "&b" + size + " &6B";
        } else if(size < SIZE_1MiB) {
            return "&b" + sizeBigDecimal.divide(BigDecimal.valueOf(SIZE_1KiB), 3, RoundingMode.HALF_UP) + " &6KiB";
        } else if(size < SIZE_1GiB) {
            return "&b" + sizeBigDecimal.divide(BigDecimal.valueOf(SIZE_1MiB), 3, RoundingMode.HALF_UP) + " &6MiB";
        } else {
            return "&b" + sizeBigDecimal.divide(BigDecimal.valueOf(SIZE_1GiB), 3, RoundingMode.HALF_UP) + " &6GiB";
        }
    }
    
    private XMaterial extensionToMaterial(Path path) {
        if(path == null) {
            return null;
        } else if(Files.isDirectory(path)) {
            Path parentDir = item.getParent();
            if(parentDir != null && parentDir.normalize().equals(path.normalize())) {
                return XMaterial.ENDER_CHEST;
            } else {
                return XMaterial.BOOKSHELF;
            }            
        } else {
            String extension = OtherUtils.getExtension(path);
            switch(extension) {
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
    }
}
