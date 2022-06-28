package me.i2000c.newalb.utils2;

import com.sk89q.jnbt.NBTOutputStream;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.extent.clipboard.io.SchematicWriter;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.Mask2D;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.registry.WorldData;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.Logger;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Schematic{
    private static final Plugin WORLDEDIT_PLUGIN = NewAmazingLuckyBlocks.getWorldEditPlugin();
    
    private static final String FIND_BY_FILE = "findByFile";
    private static final String GET_READER = "getReader";
    private static final String READ = "read";
    private static final String GET_WORLD_DATA = "getWorldData";
    
    private static final String GET_ORIGIN = "getOrigin";
    private static final String GET_DIMENSIONS = "getDimensions";
    private static final String GET_MINIMUM_POINT = "getMinimumPoint";
    private static final String GET_MAXIMUM_POINT = "getMaximumPoint";
    private static final String GET_BLOCK = "getBlock";
    private static final String SET_BLOCK = "setBlock";
    
    private static Constructor clipboardHolderConstructor = null;
    private static final Map<String, Method> METHOD_CACHE = new HashMap<>();
    
    private Clipboard clipboard;
    
    public void loadFromFile(File file, World w) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException{
        //<editor-fold defaultstate="collapsed" desc="Code">
        try(FileInputStream fis = new FileInputStream(file)){
            if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                Class<?> clipboardFormatClass = ClipboardFormat.class;
                Method findByFile = METHOD_CACHE.get(FIND_BY_FILE);
                if(findByFile == null){
                    findByFile = clipboardFormatClass.getDeclaredMethod(FIND_BY_FILE, File.class);
                    METHOD_CACHE.put(FIND_BY_FILE, findByFile);
                }                        
                ClipboardFormat format = (ClipboardFormat) findByFile.invoke(ClipboardFormat.class, file);
                
                Method getReader = METHOD_CACHE.get(GET_READER);
                if(getReader == null){
                    getReader = clipboardFormatClass.getDeclaredMethod(GET_READER, InputStream.class);
                    METHOD_CACHE.put(GET_READER, getReader);
                }
                ClipboardReader reader = (ClipboardReader) getReader.invoke(format, fis);
                Method read = METHOD_CACHE.get(READ);
                if(read == null){
                    read = reader.getClass().getMethod(READ, WorldData.class);
                    METHOD_CACHE.put(READ, read);
                }
                
                BukkitWorld bw = new BukkitWorld(w);
                Method getWorldData = METHOD_CACHE.get(GET_WORLD_DATA);
                if(getWorldData == null){
                    getWorldData = bw.getClass().getMethod(GET_WORLD_DATA);
                    METHOD_CACHE.put(GET_WORLD_DATA, getWorldData);
                }
                    
                clipboard = (Clipboard) read.invoke(reader, getWorldData.invoke(bw));
            }else{
                ClipboardFormat format = ClipboardFormats.findByFile(file);
                ClipboardReader reader = format.getReader(fis);
                clipboard = reader.read();
            }
        }
//</editor-fold>
    }
    
    public void saveToFile(File file, World w) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException{
        //<editor-fold defaultstate="collapsed" desc="Code">
        try(FileOutputStream fos = new FileOutputStream(file)){
            if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                BukkitWorld bw = new BukkitWorld(w);
                Method getWorldData = METHOD_CACHE.get(GET_WORLD_DATA);
                if(getWorldData == null){
                    getWorldData = bw.getClass().getMethod(GET_WORLD_DATA);
                    METHOD_CACHE.put(GET_WORLD_DATA, getWorldData);
                }                

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                SchematicWriter sw = new SchematicWriter(new NBTOutputStream(baos));
                sw.write(clipboard, (WorldData) getWorldData.invoke(bw));

                InputStream is = new ByteArrayInputStream(baos.toByteArray());
                GZIPOutputStream gzipOuputStream = new GZIPOutputStream(fos);

                byte[] buffer = new byte[1024];
                int bytesReaded;
                while((bytesReaded = is.read(buffer)) > 0){
                    gzipOuputStream.write(buffer, 0, bytesReaded);
                }

                is.close();
                baos.close();
                gzipOuputStream.close();
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
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
            BukkitWorld bw = new BukkitWorld(player.getWorld());
            Method getWorldData = METHOD_CACHE.get(GET_WORLD_DATA);
            if(getWorldData == null){
                getWorldData = bw.getClass().getMethod(GET_WORLD_DATA);
                METHOD_CACHE.put(GET_WORLD_DATA, getWorldData);
            }
            
            if(clipboardHolderConstructor == null){
                clipboardHolderConstructor = ClipboardHolder.class.getConstructor(Clipboard.class, WorldData.class);
            }

            ((WorldEditPlugin) WORLDEDIT_PLUGIN).getSession(player)
                    .setClipboard((ClipboardHolder) clipboardHolderConstructor.newInstance(clipboard, getWorldData.invoke(bw)));
        }else{
            ((WorldEditPlugin) WORLDEDIT_PLUGIN).getSession(player).setClipboard(new ClipboardHolder(clipboard));
        }
//</editor-fold>
    }
    
    public void loadFromPlayerClipboard(Player player) throws EmptyClipboardException{
        //<editor-fold defaultstate="collapsed" desc="Code">
        clipboard = ((WorldEditPlugin) WORLDEDIT_PLUGIN).getSession(player).getClipboard().getClipboard();
//</editor-fold>
    }
    
    public void pasteAt(Location location, boolean replaceBlocks, boolean placeAirBlocks) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, WorldEditException{
        //<editor-fold defaultstate="collapsed" desc="Code">
        EditSession session = ((WorldEditPlugin) WORLDEDIT_PLUGIN).getWorldEdit().getEditSessionFactory().getEditSession(new BukkitWorld(location.getWorld()), 10000);
        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
            pasteClipboardLegacy(session, clipboard, location, replaceBlocks, placeAirBlocks);
        }else{
            pasteClipboardNoLegacy(session, clipboard, location, replaceBlocks, placeAirBlocks);
        }
//</editor-fold>
    }
    
    //Source: Method place(...) of class CuboidClipboard of WorldEdit
    private void pasteClipboardLegacy2(EditSession editSession, CuboidClipboard c, Location loc, boolean replaceBlocks, boolean placeAirBlocks) throws MaxChangedBlocksException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, WorldEditException{
        //<editor-fold defaultstate="collapsed" desc="Code">
        BaseBlock[][][] data;
        Vector size;
        Vector offset;
        
        Field f = c.getClass().getDeclaredField("data");
        f.setAccessible(true);
        data = (BaseBlock[][][]) f.get(c);
        
        f = c.getClass().getDeclaredField("size");
        f.setAccessible(true);
        size = (Vector) f.get(c);
        
        f = c.getClass().getDeclaredField("offset");
        f.setAccessible(true);
        offset = (Vector) f.get(c);
        
        Vector newOrigin = (new Vector(loc.getX(), loc.getY(), loc.getZ())).add(offset);
        
        for(int x=0; x<size.getBlockX(); x++){
            for(int y=0; y<size.getBlockY(); y++){
                for(int z=0; z<size.getBlockZ(); z++){
                    BaseBlock block = data[x][y][z];
                    if(block != null){
                        if(!placeAirBlocks){
                            if(block.isAir()){
                                continue;
                            }
                        }
                        Vector targetVector = new Vector(x, y, z).add(newOrigin);
                        Location l = new Location(loc.getWorld(), targetVector.getX(), targetVector.getY(), targetVector.getZ());
                        if(!replaceBlocks){
                            if(!l.getBlock().isEmpty()){
                                continue;
                            }
                        }
                        Method method = editSession.getClass().getMethod("setBlock", Vector.class, BaseBlock.class);
                        method.invoke(editSession, targetVector, block);
                        //editSession.setBlock(targetVector, block);
                    }
                }
            }
        }
//</editor-fold>
    }
    
    private void pasteClipboardLegacy(EditSession editSession, Clipboard c, Location loc, boolean replaceBlocks, boolean placeAirBlocks) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Method getOrigin = METHOD_CACHE.get(GET_ORIGIN);
        if(getOrigin == null){
            getOrigin = c.getClass().getMethod(GET_ORIGIN);
            METHOD_CACHE.put(GET_ORIGIN, getOrigin);
        }
        Method getDimensions = METHOD_CACHE.get(GET_DIMENSIONS);
        if(getDimensions == null){
            getDimensions = c.getClass().getMethod(GET_DIMENSIONS);
            METHOD_CACHE.put(GET_DIMENSIONS, getDimensions);
        }
        Method getMinimumPoint = METHOD_CACHE.get(GET_MINIMUM_POINT);
        if(getMinimumPoint == null){
            getMinimumPoint = c.getClass().getMethod(GET_MINIMUM_POINT);
            METHOD_CACHE.put(GET_MINIMUM_POINT, getMinimumPoint);
        }
        Method getMaximumPoint = METHOD_CACHE.get(GET_MAXIMUM_POINT);
        if(getMaximumPoint == null){
            getMaximumPoint = c.getClass().getMethod(GET_MAXIMUM_POINT);
            METHOD_CACHE.put(GET_MAXIMUM_POINT, getMaximumPoint);
        }
        
        Vector origin = (Vector) getOrigin.invoke(c);
        Vector dimensions = (Vector) getDimensions.invoke(c);
        Vector minPoint = (Vector) getMinimumPoint.invoke(c);
        Vector maxPoint = (Vector) getMaximumPoint.invoke(c);
        
        Vector locationVector = new Vector(loc.getX(), loc.getY(), loc.getZ());
        Vector offset = locationVector.subtract(origin);
        
        Method getBlock = METHOD_CACHE.get(GET_BLOCK);
        if(getBlock == null){
            getBlock = c.getClass().getMethod(GET_BLOCK, Vector.class);
            METHOD_CACHE.put(GET_BLOCK, getBlock);
        }
        Method setBlock = METHOD_CACHE.get(SET_BLOCK);
        if(setBlock == null){
            setBlock = editSession.getClass().getMethod(SET_BLOCK, Vector.class, BaseBlock.class);
            METHOD_CACHE.put(SET_BLOCK, setBlock);
        }
        
        for(int x=minPoint.getBlockX();x<=maxPoint.getBlockX();x++){
            for(int y=minPoint.getBlockY();y<=maxPoint.getBlockY();y++){
                for(int z=minPoint.getBlockZ();z<=maxPoint.getBlockZ();z++){
                    Vector v = new Vector(x,y,z);
                    
                    BaseBlock block = (BaseBlock) getBlock.invoke(c, v);
                    
                    if(!placeAirBlocks){
                        if(block.isAir()){
                            continue;
                        }
                    }
                    
                    Vector targetVector = v.add(offset);
                    Location l = new Location(loc.getWorld(), targetVector.getX(), targetVector.getY(), targetVector.getZ());
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
    
    private CuboidClipboard clipboardToCuboid(Clipboard c){
        //<editor-fold defaultstate="collapsed" desc="Code">
        try{
            Method getOrigin = METHOD_CACHE.get(GET_ORIGIN);
            if(getOrigin == null){
                getOrigin = c.getClass().getMethod(GET_ORIGIN);
                METHOD_CACHE.put(GET_ORIGIN, getOrigin);
            }
            Method getDimensions = METHOD_CACHE.get(GET_DIMENSIONS);
            if(getDimensions == null){
                getDimensions = c.getClass().getMethod(GET_DIMENSIONS);
                METHOD_CACHE.put(GET_DIMENSIONS, getDimensions);
            }
            
            Method getBlock = METHOD_CACHE.get(GET_BLOCK);
            if(getBlock == null){
                getBlock = c.getClass().getMethod(GET_BLOCK, Vector.class);
                METHOD_CACHE.put(GET_BLOCK, getBlock);
            }
            Method getMinimumPoint = METHOD_CACHE.get(GET_MINIMUM_POINT);
            if(getMinimumPoint == null){
                getMinimumPoint = c.getClass().getMethod(GET_MINIMUM_POINT);
                METHOD_CACHE.put(GET_MINIMUM_POINT, getMinimumPoint);
            }
            
            Vector origin = (Vector) getOrigin.invoke(c);
            Vector dimensions = (Vector) getDimensions.invoke(c);
            
            CuboidClipboard cc = new CuboidClipboard(dimensions, origin);
            Logger.log(dimensions);
            for(int x=0;x<dimensions.getBlockX();x++){
                for(int y=0;y<dimensions.getBlockY();y++){
                    for(int z=0;z<dimensions.getBlockZ();z++){
                        Vector v = new Vector(x,y,z);
                        
                        Vector min = (Vector) getMinimumPoint.invoke(c.getRegion());
                        BaseBlock block = (BaseBlock) getBlock.invoke(c, min.add(v));
                        
                        cc.setBlock(v, block);
                    }
                }
            }
            return cc;
        }catch(Exception ex){
            Logger.log("An error occurred:", Logger.LogLevel.ERROR);
            ex.printStackTrace();
            return null;
        }
//</editor-fold>
    }
}
