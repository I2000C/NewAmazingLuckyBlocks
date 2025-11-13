package me.i2000c.newalb.integration;

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
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.math.transform.Identity;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.PasteBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.zip.GZIPOutputStream;

import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.api.version.MinecraftVersion;
import me.i2000c.newalb.utils.reflection.ReflectionManager;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
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
    
    private static Object getOrigin(Clipboard clipboard) {
        return ReflectionManager.callMethod(clipboard, "getOrigin");
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
    
    public void pasteAt(Player player, Location location, boolean replaceBlocks, boolean placeAirBlocks, int rotation) throws WorldEditException {
        //<editor-fold defaultstate="collapsed" desc="Code">
        WorldEdit worldEdit = ReflectionManager.callMethod(WORLDEDIT_PLUGIN, "getWorldEdit");
        EditSession session = worldEdit.getEditSessionFactory().getEditSession(getWorldEditWorld(location.getWorld()), 10000);
        
        Transform transform;
        if(rotation == 0) {
            transform = new Identity();
        } else {
            if(rotation % 90 != 0) {
                throw new IllegalArgumentException("Rotation must be a multiple of 90, not: " + rotation);
            }
            
            transform = new AffineTransform().rotateY(-rotation);
        }
        
        if(MinecraftVersion.CURRENT_VERSION.isLegacyVersion()){
            pasteClipboardLegacy(session, clipboard, player, location, replaceBlocks, placeAirBlocks, transform);
        }else{
            pasteClipboardNoLegacy(session, clipboard, player, location, replaceBlocks, placeAirBlocks, transform);
        }
//</editor-fold>
    }
    
    private void pasteClipboardLegacy(EditSession session, Clipboard clipboard, Player player, Location location, boolean replaceBlocks, boolean placeAirBlocks, Transform transform) throws WorldEditException {
        //<editor-fold defaultstate="collapsed" desc="Code">
        Object worldData = getWorldData(location.getWorld());
        ClipboardHolder holder = ReflectionManager.callConstructor(ClipboardHolder.class, clipboard, worldData);
        holder.setTransform(transform);
        
        PasteBuilder builder = ReflectionManager.callMethod(holder, "createPaste", session, worldData);
        
        Object origin = getOrigin(clipboard);
        Object newOrigin = ReflectionManager.callConstructor("com.sk89q.worldedit.Vector", location.getX(), location.getY(), location.getZ());        
        Object offset = ReflectionManager.callMethod(newOrigin, "subtract", origin);
        
        ReflectionManager.callMethod(builder, "to", newOrigin);
        
        Operation operation = builder.build();
        
        // https://stackoverflow.com/questions/1082850/java-reflection-create-an-implementing-class
        Mask mask = (Mask) Proxy.newProxyInstance(Mask.class.getClassLoader(), 
                                                  new Class[] {Mask.class}, 
                                                  new CustomMask(player, clipboard, offset, holder.getTransform(), replaceBlocks, placeAirBlocks));
        ReflectionManager.callMethod(operation, "setSourceMask", mask);
        
        Operations.complete(operation);
        ReflectionManager.callMethod(session, "flushQueue");
//</editor-fold>
    }
    
    //Source: https://matthewmiller.dev/blog/how-to-load-and-save-schematics-with-the-worldedit-api/
    private void pasteClipboardNoLegacy(EditSession session, Clipboard clipboard, Player player, Location location, boolean replaceBlocks, boolean placeAirBlocks, Transform transform) throws WorldEditException{
        //<editor-fold defaultstate="collapsed" desc="Code">
        ClipboardHolder holder = new ClipboardHolder(clipboard);
        holder.setTransform(transform);
        
        PasteBuilder builder = holder.createPaste(session);
        
        BlockVector3 origin = (BlockVector3) getOrigin(clipboard);
        BlockVector3 newOrigin = BlockVector3.at(location.getX(), location.getY(), location.getZ());
        BlockVector3 offset = newOrigin.subtract(origin);
        
        builder.to(newOrigin);
        
        Mask mask = new CustomMask(player, clipboard, offset, holder.getTransform(), replaceBlocks, placeAirBlocks);
        builder.maskSource(mask);
        
        Operation operation = builder.build();
        Operations.complete(operation);
        session.flushSession();
//</editor-fold>
    }
    
    private static class CustomMask implements Mask, InvocationHandler {
        
        private final Player player;
        private final Clipboard clipboard;
        private final Object offset;
        private final Transform transform;
        private final boolean replaceBlocks;
        private final boolean placeAirBlocks;
        
        private final Object origin;
        
        public CustomMask(Player player, Clipboard clipboard, Object offset, Transform transform, boolean replaceBlocks, boolean placeAirBlocks) {
            this.player = player;
            this.clipboard = clipboard;
            this.offset = offset;
            this.transform = transform;
            this.replaceBlocks = replaceBlocks;
            this.placeAirBlocks = placeAirBlocks;
            this.origin = getOrigin(clipboard);
        }
        
        private boolean test(Location location, boolean newBlockIsAir) {
            Block block = location.getBlock();
            boolean canPlaceBlock = (replaceBlocks || block.isEmpty()) && 
                                    (placeAirBlocks || !newBlockIsAir) && 
                                    WorldGuardManager.canPasteSchematic(player, location);
            return canPlaceBlock;
        }
        
        @Override
        public boolean test(BlockVector3 vector) {
            BlockVector3 target = vector.subtract((BlockVector3) origin);
            target = transform.apply(target.toVector3()).toBlockPoint();
            target = target.add((BlockVector3) origin);
            target = target.add((BlockVector3) offset);
            
            Location location = BukkitAdapter.adapt(player.getWorld(), target);
            
            boolean newBlockIsAir = clipboard.getBlock(vector).getBlockType().getMaterial().isAir();
            return test(location, newBlockIsAir);
        }
        
        public boolean test(Object vector) {
            Object target = ReflectionManager.callMethod(vector, "subtract", origin);
            target = ReflectionManager.callMethod(transform, "apply", target);
            target = ReflectionManager.callMethod(target, "add", origin);
            target = ReflectionManager.callMethod(target, "add", offset);
            
            double x = ReflectionManager.callMethod(target, "getX");
            double y = ReflectionManager.callMethod(target, "getY");
            double z = ReflectionManager.callMethod(target, "getZ");
            Location location = new Location(player.getWorld(), x, y, z);
            
            Object baseBlock = ReflectionManager.callMethod(clipboard, "getBlock", vector);
            boolean newBlockIsAir = ReflectionManager.callMethod(baseBlock, "isAir");
            return test(location, newBlockIsAir);
        }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return test(args[0]);
        }
        
        @Override
        public Mask2D toMask2D() {
            return null;
        }
    }
}
