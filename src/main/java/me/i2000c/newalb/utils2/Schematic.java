package me.i2000c.newalb.utils2;

import com.sk89q.jnbt.NBTOutputStream;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.Mask2D;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.zip.GZIPOutputStream;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.logger.LogLevel;
import me.i2000c.newalb.utils.logger.Logger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Schematic{
    private static final Plugin WORLDEDIT_PLUGIN = NewAmazingLuckyBlocks.getWorldEditPlugin();
    
    private static Class worldDataClass;
    private static Class vectorClass;
    private static Class baseBlockClass;
    
    private static Method findByFile;
    private static Method getReader;
    private static Method read;
    private static Method getWorldData;
    private static Method write;
    private static Method getSession;
    private static Method getWorldEdit;
    private static Method getOrigin;
    private static Method getDimensions;
    private static Method getMinimumPoint;
    private static Method getMaximumPoint;
    private static Method getBlock;
    private static Method setBlock;
        
    private static Method add;
    private static Method subtract;
    private static Method getBlockX;
    private static Method getBlockY;
    private static Method getBlockZ;
    private static Method getX;
    private static Method getY;
    private static Method getZ;
    private static Method isAir;
    
    private static Constructor bukkitWorldConstructor;
    private static Constructor schematicWriterConstructor;
    private static Constructor clipboardHolderConstructor;
    private static Constructor vectorConstructor;
    
    private static boolean initialized = false;
    private static void initialize(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        try{
            Class bukkitWorldClass = Class.forName("com.sk89q.worldedit.bukkit.BukkitWorld");
            bukkitWorldConstructor = bukkitWorldClass.getConstructor(World.class);
            
            if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                Class clipboardFormatClass = ClipboardFormat.class;
                findByFile = clipboardFormatClass.getDeclaredMethod("findByFile", File.class);
                getReader = clipboardFormatClass.getDeclaredMethod("getReader", InputStream.class);
                
                worldDataClass = Class.forName("com.sk89q.worldedit.world.registry.WorldData");
                read = ClipboardReader.class.getMethod("read", worldDataClass);
                
                getWorldData = bukkitWorldClass.getMethod("getWorldData");
                
                Class schematicWriterClass = Class.forName("com.sk89q.worldedit.extent.clipboard.io.SchematicWriter");
                schematicWriterConstructor = schematicWriterClass.getConstructor(NBTOutputStream.class);
                write = schematicWriterClass.getMethod("write", Clipboard.class, worldDataClass);
                
                clipboardHolderConstructor = ClipboardHolder.class.getConstructor(Clipboard.class, worldDataClass);
                
                vectorClass = Class.forName("com.sk89q.worldedit.Vector");
                vectorConstructor = vectorClass.getConstructor(double.class, double.class, double.class);
                baseBlockClass = Class.forName("com.sk89q.worldedit.blocks.BaseBlock");
                
                Class clipboardClass = Clipboard.class;
                getOrigin = clipboardClass.getMethod("getOrigin");
                getDimensions = clipboardClass.getMethod("getDimensions");
                getMinimumPoint = clipboardClass.getMethod("getMinimumPoint");
                getMaximumPoint = clipboardClass.getMethod("getMaximumPoint");
                getBlock = clipboardClass.getMethod("getBlock", vectorClass);
                setBlock = clipboardClass.getMethod("setBlock", vectorClass, baseBlockClass);
                
                add = vectorClass.getMethod("add", vectorClass);
                subtract = vectorClass.getMethod("subtract", vectorClass);
                getBlockX = vectorClass.getMethod("getBlockX");
                getBlockY = vectorClass.getMethod("getBlockY");
                getBlockZ = vectorClass.getMethod("getBlockZ");
                getX = vectorClass.getMethod("getX");
                getY = vectorClass.getMethod("getY");
                getZ = vectorClass.getMethod("getZ");
                
                isAir = baseBlockClass.getMethod("isAir");
            }
            
            getSession = WORLDEDIT_PLUGIN.getClass().getMethod("getSession", Player.class);
            getWorldEdit = WORLDEDIT_PLUGIN.getClass().getMethod("getWorldEdit");
            
            initialized = true;
        }catch(Exception ex){
            Logger.log("An error ocurred while loading schematic classes:", LogLevel.INFO);
            ex.printStackTrace();
        }
//</editor-fold>
    }
    
    private Clipboard clipboard;
    
    public void loadFromFile(File file, World world) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, InstantiationException{
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!initialized){
            initialize();
        }
        
        try(FileInputStream fis = new FileInputStream(file)){
            if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){    
                ClipboardFormat format = (ClipboardFormat) findByFile.invoke(ClipboardFormat.class, file);                
                ClipboardReader reader = (ClipboardReader) getReader.invoke(format, fis);
                
                Object bukkitWorld = bukkitWorldConstructor.newInstance(world);                    
                clipboard = (Clipboard) read.invoke(reader, getWorldData.invoke(bukkitWorld));
            }else{
                ClipboardFormat format = ClipboardFormats.findByFile(file);
                ClipboardReader reader = format.getReader(fis);
                clipboard = reader.read();
            }
        }
//</editor-fold>
    }
    
    public void saveToFile(File file, World world) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, InstantiationException{
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!initialized){
            initialize();
        }
        
        try(FileOutputStream fos = new FileOutputStream(file)){
            if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                try(GZIPOutputStream gzipOuputStream = new GZIPOutputStream(fos)){
                    Object bukkitWorld = bukkitWorldConstructor.newInstance(world);
                    Object schematicWriter = schematicWriterConstructor.newInstance(new NBTOutputStream(gzipOuputStream));
                    write.invoke(schematicWriter, clipboard, getWorldData.invoke(bukkitWorld));
                }
            }else{
                try(ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(fos)){
                    writer.write(clipboard);
                }
            }
        }        
//</editor-fold>
    }
    
    public void copyToPlayerClipboard(Player player) throws IllegalAccessException, IllegalArgumentException, InstantiationException, InvocationTargetException, NoSuchMethodException{
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!initialized){
            initialize();
        }
        
        LocalSession session = (LocalSession) getSession.invoke(WORLDEDIT_PLUGIN, player);
        ClipboardHolder holder;
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
            Object bukkitWorld = bukkitWorldConstructor.newInstance(player.getWorld());
            holder = (ClipboardHolder) clipboardHolderConstructor.newInstance(clipboard, getWorldData.invoke(bukkitWorld));
        }else{
            holder = new ClipboardHolder(clipboard);
        }
        session.setClipboard(holder);
//</editor-fold>
    }
    
    public void loadFromPlayerClipboard(Player player) throws EmptyClipboardException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!initialized){
            initialize();
        }
        
        LocalSession session = (LocalSession) getSession.invoke(WORLDEDIT_PLUGIN, player);
        clipboard = session.getClipboard().getClipboard();
//</editor-fold>
    }
    
    public void pasteAt(Location location, boolean replaceBlocks, boolean placeAirBlocks) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, WorldEditException, InstantiationException{
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!initialized){
            initialize();
        }
        
        Object bukkitWorld = bukkitWorldConstructor.newInstance(location.getWorld());
        WorldEdit worldEdit = (WorldEdit) getWorldEdit.invoke(WORLDEDIT_PLUGIN);
        EditSession session = worldEdit.getEditSessionFactory().getEditSession((com.sk89q.worldedit.world.World) bukkitWorld, 10000);
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
            pasteClipboardLegacy(session, clipboard, location, replaceBlocks, placeAirBlocks);
        }else{
            pasteClipboardNoLegacy(session, clipboard, location, replaceBlocks, placeAirBlocks);
        }
//</editor-fold>
    }
    
    private void pasteClipboardLegacy(EditSession editSession, Clipboard clipboard, Location loc, boolean replaceBlocks, boolean placeAirBlocks) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!initialized){
            initialize();
        }
        
        Object origin = getOrigin.invoke(clipboard);
        Object dimensions = getDimensions.invoke(clipboard);
        Object minPoint = getMinimumPoint.invoke(clipboard);
        Object maxPoint = getMaximumPoint.invoke(clipboard);
        
        Object locationVector = vectorConstructor.newInstance(loc.getX(), loc.getY(), loc.getZ());
        Object offset = subtract.invoke(locationVector, origin);
        
        int minX = (Integer) getBlockX.invoke(minPoint);
        int maxX = (Integer) getBlockX.invoke(maxPoint);
        int minY = (Integer) getBlockY.invoke(minPoint);
        int maxY = (Integer) getBlockY.invoke(maxPoint);
        int minZ = (Integer) getBlockZ.invoke(minPoint);
        int maxZ = (Integer) getBlockZ.invoke(maxPoint);
        for(int x=minX; x<=maxX; x++){
            for(int y=minY; y<=maxY; y++){
                for(int z=minZ; z<=maxZ; z++){
                    Object vector = vectorConstructor.newInstance(x,y,z);                    
                    Object block = getBlock.invoke(clipboard, vector);
                    
                    if(!placeAirBlocks){
                        if((Boolean) isAir.invoke(block)){
                            continue;
                        }
                    }
                    
                    Object targetVector = add.invoke(vector, offset);
                    double targetX = (Double) getX.invoke(targetVector);
                    double targetY = (Double) getY.invoke(targetVector);
                    double targetZ = (Double) getZ.invoke(targetVector);
                    
                    Location l = new Location(loc.getWorld(), targetX, targetY, targetZ);
                    if(!replaceBlocks){
                        if(!l.getBlock().isEmpty()){
                            continue;
                        }
                    }
                    
                    setBlock.invoke(editSession, targetVector, block);
                }
            }
        }
//</editor-fold>
    }
    
    //Source: https://matthewmiller.dev/blog/how-to-load-and-save-schematics-with-the-worldedit-api/
    private void pasteClipboardNoLegacy(EditSession session, Clipboard clipboard, Location location, boolean replaceBlocks, boolean placeAirBlocks) throws WorldEditException{
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!initialized){
            initialize();
        }
        
        final World w = location.getWorld();
        BlockVector3 newOrigin = BlockVector3.at(location.getX(), location.getY(), location.getZ());
        BlockVector3 offset = newOrigin.subtract(clipboard.getOrigin());
        Operation operation = new ClipboardHolder(clipboard).createPaste(session).ignoreAirBlocks(!placeAirBlocks).to(newOrigin)
                .maskSource(new Mask() {
                    @Override
                    public boolean test(BlockVector3 bv) {
                        if(replaceBlocks){
                            return true;
                        }else{
                            BlockVector3 target = bv.add(offset);
                            return w.getBlockAt(target.getBlockX(), target.getBlockY(), target.getBlockZ()).isEmpty();
                        }
                    }
                    @Override
                    public Mask2D toMask2D() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                }).build();
        Operations.complete(operation);
        session.flushSession();
//</editor-fold>
    }
}
