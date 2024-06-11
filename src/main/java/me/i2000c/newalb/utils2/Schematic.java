package me.i2000c.newalb.utils2;

import com.sk89q.jnbt.NBTOutputStream;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
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
import java.util.zip.GZIPOutputStream;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.reflection.ReflectionManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Schematic{
    private static final Plugin WORLDEDIT_PLUGIN = NewAmazingLuckyBlocks.getWorldEditPlugin();
    
    private Clipboard clipboard;
    
    private static Object getWorldData(World world) {
        Object bukkitWorld = ReflectionManager.callConstructor("com.sk89q.worldedit.bukkit.BukkitWorld", world);
        Object worldData = ReflectionManager.callMethod(bukkitWorld, "getWorldData");
        return worldData;
    }
    
    private static com.sk89q.worldedit.world.World getWorldEditWorld(World world) {
        Object bukkitWorld = ReflectionManager.callConstructor("com.sk89q.worldedit.bukkit.BukkitWorld", world);
        return (com.sk89q.worldedit.world.World) bukkitWorld;
    }
    
    public void loadFromFile(File file, World world) throws IOException {
        //<editor-fold defaultstate="collapsed" desc="Code">
        try(FileInputStream fis = new FileInputStream(file)){
            if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()){
                ClipboardFormat format = ReflectionManager.callStaticMethod(ClipboardFormat.class, "findByFile", file);
                ClipboardReader reader = ReflectionManager.callMethod(format, "getReader", fis);
                clipboard = ReflectionManager.callMethod(reader, "read", getWorldData(world));
            }else{
                ClipboardFormat format = ClipboardFormats.findByFile(file);
                if(format == null) {
                    throw new IllegalArgumentException(String.format("Invalid schematic format for file \"%s\"", file.getName()));
                }
                
                ClipboardReader reader = format.getReader(fis);
                clipboard = reader.read();
            }
        }
//</editor-fold>
    }
    
    public void saveToFile(File file, World world) throws IOException {
        //<editor-fold defaultstate="collapsed" desc="Code">
        try(FileOutputStream fos = new FileOutputStream(file)){
            if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()){
                try(GZIPOutputStream gzipOuputStream = new GZIPOutputStream(fos)){
                    Object schematicWriter = ReflectionManager.callConstructor("com.sk89q.worldedit.extent.clipboard.io.SchematicWriter", 
                                                                                    new NBTOutputStream(gzipOuputStream));
                    ReflectionManager.callMethod(schematicWriter, "write", clipboard, getWorldData(world));
                }
            }else{
                try(ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(fos)){
                    writer.write(clipboard);
                }
            }
        }        
//</editor-fold>
    }
    
    public void copyToPlayerClipboard(Player player) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        LocalSession session = ReflectionManager.callMethod(WORLDEDIT_PLUGIN, "getSession", player);
        ClipboardHolder holder;
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()){
            Object worldData = getWorldData(player.getWorld());
            holder = ReflectionManager.callConstructor(ClipboardHolder.class, clipboard, worldData);
        }else{
            holder = new ClipboardHolder(clipboard);
        }
        session.setClipboard(holder);
//</editor-fold>
    }
    
    public void loadFromPlayerClipboard(Player player) throws EmptyClipboardException {
        //<editor-fold defaultstate="collapsed" desc="Code">
        LocalSession session = ReflectionManager.callMethod(WORLDEDIT_PLUGIN, "getSession", player);
        clipboard = session.getClipboard().getClipboard();
//</editor-fold>
    }
    
    public void pasteAt(Player player, Location location, boolean replaceBlocks, boolean placeAirBlocks) throws WorldEditException {
        //<editor-fold defaultstate="collapsed" desc="Code">
        WorldEdit worldEdit = ReflectionManager.callMethod(WORLDEDIT_PLUGIN, "getWorldEdit");
        EditSession session = worldEdit.getEditSessionFactory().getEditSession(getWorldEditWorld(location.getWorld()), 10000);
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()){
            pasteClipboardLegacy(session, clipboard, player, location, replaceBlocks, placeAirBlocks);
        }else{
            pasteClipboardNoLegacy(session, clipboard, player, location, replaceBlocks, placeAirBlocks);
        }
//</editor-fold>
    }
    
    private void pasteClipboardLegacy(EditSession editSession, Clipboard clipboard, Player player, Location loc, boolean replaceBlocks, boolean placeAirBlocks) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Object origin = ReflectionManager.callMethod(clipboard, "getOrigin");
        Object dimensions = ReflectionManager.callMethod(clipboard, "getDimensions");
        Object minPoint = ReflectionManager.callMethod(clipboard, "getMinimumPoint");
        Object maxPoint = ReflectionManager.callMethod(clipboard, "getMaximumPoint");
        
        Object locationVector = ReflectionManager.callConstructor("com.sk89q.worldedit.Vector", loc.getX(), loc.getY(), loc.getZ());
        Object offset = ReflectionManager.callMethod(locationVector, "subtract", origin);
        
        int minX = ReflectionManager.callMethod(minPoint, "getBlockX");
        int maxX = ReflectionManager.callMethod(maxPoint, "getBlockX");
        int minY = ReflectionManager.callMethod(minPoint, "getBlockY");
        int maxY = ReflectionManager.callMethod(maxPoint, "getBlockY");
        int minZ = ReflectionManager.callMethod(minPoint, "getBlockZ");
        int maxZ = ReflectionManager.callMethod(maxPoint, "getBlockZ");
        for(int x=minX; x<=maxX; x++){
            for(int y=minY; y<=maxY; y++){
                for(int z=minZ; z<=maxZ; z++){
                    Object vector = ReflectionManager.callConstructor("com.sk89q.worldedit.Vector", x, y, z);
                    Object block = ReflectionManager.callMethod(clipboard, "getBlock", vector);
                    
                    boolean isAir = ReflectionManager.callMethod(block, "isAir");
                    if(!placeAirBlocks && isAir){
                        continue;
                    }
                    
                    Object targetVector = ReflectionManager.callMethod(vector, "add", offset);
                    double targetX = ReflectionManager.callMethod(targetVector, "getX");
                    double targetY = ReflectionManager.callMethod(targetVector, "getY");
                    double targetZ = ReflectionManager.callMethod(targetVector, "getZ");
                    
                    Location l = new Location(loc.getWorld(), targetX, targetY, targetZ);
                    if(!replaceBlocks && !l.getBlock().isEmpty()){
                        continue;
                    }
                    
                    if(!WorldGuardManager.canPasteSchematic(player, l)) {
                        continue;
                    }
                    
                    ReflectionManager.callMethod(editSession, "setBlock", targetVector, block);
                }
            }
        }
        
        ReflectionManager.callMethod(editSession, "flushQueue");
//</editor-fold>
    }
    
    //Source: https://matthewmiller.dev/blog/how-to-load-and-save-schematics-with-the-worldedit-api/
    private void pasteClipboardNoLegacy(EditSession session, Clipboard clipboard, Player player, Location location, boolean replaceBlocks, boolean placeAirBlocks) throws WorldEditException{
        //<editor-fold defaultstate="collapsed" desc="Code">        
        final World world = location.getWorld();
        BlockVector3 newOrigin = BlockVector3.at(location.getX(), location.getY(), location.getZ());
        BlockVector3 offset = newOrigin.subtract(clipboard.getOrigin());
        Operation operation = new ClipboardHolder(clipboard).createPaste(session).ignoreAirBlocks(!placeAirBlocks).to(newOrigin)
                .maskSource(new Mask() {
                    @Override
                    public boolean test(BlockVector3 bv) {
                        BlockVector3 target = bv.add(offset);
                        Location loc = BukkitAdapter.adapt(world, target);                     
                        return (replaceBlocks || loc.getBlock().isEmpty()) 
                                    && WorldGuardManager.canPasteSchematic(player, loc);
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
