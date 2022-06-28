package me.i2000c.newalb.utils2;

import java.io.File;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FilePickerEvent extends Event{
    private static final HandlerList HANDLERS = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    
    private final Player player;
    private final File file;
    
    public FilePickerEvent(Player p, File file){
        this.player = p;
        this.file = file;        
    }
    
    public Player getPlayer(){
        return this.player;
    }
    
    public String getFilePath(){
        return this.file.getAbsolutePath();
    }
    
    public String getFilename(){
        return this.file.getName();
    }
    
    public File getSelectedFile(){
        return this.file;
    }
}
