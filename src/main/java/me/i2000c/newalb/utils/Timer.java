package me.i2000c.newalb.utils;


import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils2.ItemBuilder;
import me.i2000c.newalb.utils2.OtherUtils;
import me.i2000c.newalb.utils2.Task;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;


public class Timer implements Listener{
    private Timer(){}
    private static Timer instance;
    public static Timer getTimer(){
        if(instance == null){
            instance = new Timer();
        }
        return instance;
    }
    
    private static final String TAG = "volcano";
    
    public void executeDarkHole(Player player, Location location){
        int blocks = ConfigManager.getConfig().getInt("Objects.DarkHole.number-of-blocks");
        double radius = ConfigManager.getConfig().getDouble("Objects.DarkHole.radius");
        long ticks = ConfigManager.getConfig().getLong("Objects.DarkHole.time-between-one-block-and-the-next");
        long before_ticks = ConfigManager.getConfig().getLong("Objects.DarkHole.time-before-darkhole");
        
        executeDarkHole(player, location, blocks, radius, ticks, before_ticks, true);
    }
    public void executeDarkHole(Player player, Location location, int height, double radius, long ticks, long before_ticks, boolean squared){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Material noBreakBlockMaterial = Material.valueOf(ConfigManager.getConfig().getString("Objects.DarkHole.block-stop-mode.block"));
        
        Sound sound = XSound.ENTITY_WITHER_AMBIENT.parseSound();
        Effect effect;
        /*try {
            sound = Sound.valueOf("WITHER_IDLE"); // pre 1.9 sound //pre 1.9 sounds are here: http://docs.codelanx.com/Bukkit/1.8/org/bukkit/Sound.html
        } catch(IllegalArgumentException e) {
            sound = Sound.valueOf("ENTITY_WITHER_AMBIENT"); // post 1.9 sound //post 1.9 sounds are here: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html
        }*/
        player.playSound(player.getLocation(), sound, 2.0F, 1.0F);
        
        effect = Effect.ENDER_SIGNAL;
        location.getWorld().playEffect(location, effect, 100); // pre 1.9 effects are here: https://www.spigotmc.org/wiki/effect-list-1-8-8/
                
        Task task = new Task(){
            double x = location.getX();
            double y = location.getY();
            double z = location.getZ();
            
            int ceilRadius = (int) Math.ceil(radius);
            int i = height;
            
            final int minY = OtherUtils.getMinWorldHeight(location.getWorld());
            
            @Override
            public void run(){
                if(i == 0){
                    cancel();
                }else{
                    Location center = new Location(location.getWorld(), x, y, z);
                    double radiusSquared = radius*radius;
                    for(double bx=-ceilRadius;bx<=+ceilRadius;bx++){
                        for(double bz=-ceilRadius;bz<=+ceilRadius;bz++){
                            Location loc = center.clone().add(bx, 0, bz);
                            if(squared || loc.distanceSquared(center) <= radiusSquared){
                                Block b = player.getWorld().getBlockAt(loc);
                                if(b.getType() == noBreakBlockMaterial && ConfigManager.getConfig().getBoolean("Objects.DarkHole.block-stop-mode.enable")){
                                    cancel();
                                }else{
                                    b.setType(Material.AIR);
                                }
                            }
                        }
                    }
                    y--;
                    i--;
                    if(y < minY){
                        cancel();
                    }
                }
            }
        };
        task.runTask(before_ticks, ticks);
//</editor-fold>
    }
    
    public void executeMiniVolcano(Player player, Location location){
        int height = ConfigManager.getConfig().getInt("Objects.MiniVolcano.height");
        
        String materialName = ConfigManager.getConfig().getString("Objects.MiniVolcano.base-material");
        ItemStack baseMaterial = ItemBuilder.newItem(materialName).build();
        Material lavaMaterial = Material.getMaterial(ConfigManager.getConfig().getString("Objects.MiniVolcano.lava-material"));
        
        long ticks = ConfigManager.getConfig().getLong("Objects.MiniVolcano.time-between-one-block-and-the-next");
        long before_ticks = ConfigManager.getConfig().getLong("Objects.MiniVolcano.time-before-minivolcano");
        
        boolean throwBlocks = ConfigManager.getConfig().getBoolean("Objects.MiniVolcano.throwBlocks.enable");
        executeMiniVolcano(player, location, height, baseMaterial, lavaMaterial, ticks, before_ticks, false, throwBlocks);
    }
    public void executeMiniVolcano(Player player, Location location, int height, ItemStack baseMaterial, Material lavaMaterial, long ticks, long before_ticks, boolean squared, boolean throwBlocks){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Sound sound = XSound.ENTITY_TNT_PRIMED.parseSound();
        Effect effect;
        /*try{
            sound = Sound.valueOf("FUSE"); // pre 1.9 sound //pre 1.9 sounds are here: http://docs.codelanx.com/Bukkit/1.8/org/bukkit/Sound.html
        }catch(IllegalArgumentException ex){
            sound = Sound.valueOf("ENTITY_TNT_PRIMED"); // post 1.9 sound //post 1.9 sounds are here: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html
        }*/
        player.playSound(player.getLocation(), sound, 2.0F, 1.0F);
        
        try{
            effect = Effect.valueOf("LARGE_SMOKE"); // particle effects are here: https://www.digminecraft.com/lists/particle_list_pc.php
            location.getWorld().playEffect(location, effect, 100);
        }catch(IllegalArgumentException ex){
        }
        
        Task task = new Task(){
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            
            int floor = 0;
            int radius = 1;
            
            Location loc = null;
            
            HashMap<Location, ItemStack> block_list = new HashMap<>();
            
            @Override
            public void run(){
                if(floor == 0){
                    while(location.getWorld().getBlockAt(x, y, z).getType() != Material.AIR){
                        y++;
                    }
                    loc = new Location(location.getWorld(), x, y, z);
                    
                    loc.getBlock().setType(lavaMaterial);
                    block_list.put(loc, new ItemStack(lavaMaterial));
                }else if(floor >= height+1){
                    this.cancel();  
                    if(throwBlocks){
                        executeThrowBlocks(loc.add(0, height, 0), lavaMaterial);   
                    }
                }else{
                    for(Location l : new ArrayList<>(block_list.keySet())){
                        ItemStack stack = block_list.get(l);
                        block_list.remove(l);
                        block_list.put(l.add(0, 1, 0), stack);
                    }
                    Location center = new Location(loc.getWorld(), x, y, z);
                    int radiusSquared = radius*radius;
                    for(int bx=x-radius;bx<=x+radius;bx++){
                        for(int bz=z-radius;bz<=z+radius;bz++){
                            Location location = new Location(loc.getWorld(), bx, y, bz);
                            if(squared || location.distanceSquared(center) <= radiusSquared){
                                block_list.put(location, baseMaterial);
                            }
                        }
                    }
                    block_list.put(new Location(loc.getWorld(), x, y, z), new ItemStack(lavaMaterial));
                    for(Location l : block_list.keySet()){
                        ItemStack stack = block_list.get(l);
                        l.getBlock().setType(stack.getType());
                        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                            l.getBlock().setData((byte) stack.getDurability());
                        }
                    }
                    radius++;
                }
                floor++;
            }
        };
        task.runTask(before_ticks, ticks);
//</editor-fold>
    }
    public void executeThrowBlocks(Location location, Material lavaMaterial){
        int numberOfBlocks = ConfigManager.getConfig().getInt("Objects.MiniVolcano.throwBlocks.number-of-blocks");
        double height = ConfigManager.getConfig().getDouble("Objects.MiniVolcano.throwBlocks.height");
        double radius = ConfigManager.getConfig().getDouble("Objects.MiniVolcano.throwBlocks.radius");
        String name = ConfigManager.getConfig().getString("Objects.MiniVolcano.throwBlocks.material");
        ItemStack throwBlocksStack = ItemBuilder.newItem(name).build();
        long time = ConfigManager.getConfig().getLong("Objects.MiniVolcano.throwBlocks.time-between-blocks");

        executeThrowBlocks(location, numberOfBlocks, height, radius, throwBlocksStack, time, lavaMaterial);
    }
    public void executeThrowBlocks(Location location, int numberOfBlocks, double height, double radius, ItemStack throwBlocksStack, long time, Material lavaMaterial){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Location loc1 = location;
        Random r = new Random();
        Task task = new Task(){
            int i = 0;
            @Override
            public void run(){
                if(i >= numberOfBlocks){
                    this.cancel();
                    return;
                }
                int angle360 = r.nextInt(360);
                double angleRadians = Math.toRadians(angle360);
                Location loc2 = loc1.clone()
                        .add(radius * Math.sin(angleRadians), height, radius * Math.cos(angleRadians));
                
                Vector v = loc2.toVector().subtract(loc1.toVector());
                FallingBlock fb = loc2.getWorld().spawnFallingBlock(loc2, throwBlocksStack.getData());
                fb.setDropItem(false);
                fb.setVelocity(v);
                fb.setMetadata(TAG, new FixedMetadataValue(NewAmazingLuckyBlocks.getInstance(), lavaMaterial));
                
                i++;
            }
        };
        task.runTask(0L, time);
//</editor-fold>
    }
    @EventHandler
    private void onBlockFallingBlockHit(EntityChangeBlockEvent e){
        if(e.getEntity().hasMetadata(TAG)){
            Material lavaMaterial = (Material) e.getEntity().getMetadata(TAG).get(0).value();
            e.setCancelled(true);
            e.getBlock().setType(lavaMaterial);
        }
    }
    
    public void executeFrostPathWand(Player player, int distance, Location endLoc){        
        int maxBlocks = ConfigManager.getConfig().getInt("Wands.FrostPathWand.maxBlocks");
        String name = ConfigManager.getConfig().getString("Wands.FrostPathWand.frostMaterial");
        ItemStack frostStack = ItemBuilder.newItem(name).build();
        long before_ticks = ConfigManager.getConfig().getLong("Wands.FrostPathWand.time-before-frostpath");
        long ticks = ConfigManager.getConfig().getLong("Wands.FrostPathWand.time-between-one-block-and-the-next");
        int blocks = ConfigManager.getConfig().getInt("Wands.FrostPathWand.rows-of-blocks-each-time");
        int width = ConfigManager.getConfig().getInt("Wands.FrostPathWand.row-width");
        
        executeFrostPathWand(player, frostStack, blocks, maxBlocks, width, distance, endLoc, before_ticks, ticks);
    }
    
    public void executeFrostPathWand(Player player, ItemStack frostStack, int blocks, int maxBlocks,
            int width, int distance, Location endLoc, long before_ticks, long ticks){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(width <= 0){
            width = 1;
        }else{
            width = (width-1)/2;
        }
        final int w = width;
        
        Set<Material> passableBlocks = EnumSet.of(XMaterial.AIR.parseMaterial(),
                                                  XMaterial.WATER.parseMaterial(),
                                                  XMaterial.LAVA.parseMaterial(),
                                                  XMaterial.GRASS.parseMaterial());
        
        Location l = player.getLocation();
        Vector v = new Vector(endLoc.getBlockX()-l.getBlockX(),endLoc.getBlockY()-l.getBlockY(),endLoc.getBlockZ()-l.getBlockZ());
        v.normalize();
        
        Location location = l.clone();
        Task task = new Task(){
            int i=1;
            ItemStack item = new ItemStack(frostStack);
            
            @Override
            public void run(){
                if(i > maxBlocks || i > distance){
                    cancel();
                }else{
                    int j = 0;
                    do{
                        location.add(v);
                        if(passableBlocks.contains(location.getWorld().getBlockAt(location).getType())){
                            Block b = location.getWorld().getBlockAt(location);
                            BlockPlaceEvent e2 = new BlockPlaceEvent(b, b.getState(), b, item, player, true);
                            Bukkit.getPluginManager().callEvent(e2);
                            if(!e2.isCancelled()){                                
                                b.setType(frostStack.getType());
                                if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                                    b.setData((byte) frostStack.getDurability());
                                }
                                //player.getWorld().playEffect(location.clone().add(0,1,0), Effect.SNOWBALL_BREAK, 0);
                                player.getWorld().playSound(location, XSound.BLOCK_GLASS_BREAK.parseSound(), 2.0F, 1.0F);
                            }  
                        }
                        
                        for(int k=1;k<=w;k++){
                            Block b;
                            int yaw = ((int)location.getYaw()) % 180;
                            if((yaw >= 45 && yaw < 135) || (yaw >= -135 && yaw < -45)){
                                //System.out.println("X-axis");
                                //System.out.println("Yaw = " + yaw);
                                if(passableBlocks.contains(location.getWorld().getBlockAt(location.clone().add(0,0,k)).getType())){
                                    b = location.getWorld().getBlockAt(location.clone().add(0,0,k));
                                    BlockPlaceEvent e2 = new BlockPlaceEvent(b, b.getState(), b, item, player, true);
                                    Bukkit.getPluginManager().callEvent(e2);
                                    if(!e2.isCancelled()){                                
                                        b.setType(frostStack.getType());
                                        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                                            b.setData((byte) frostStack.getDurability());
                                        }
                                    }
                                    
                                    //player.getWorld().playEffect(location.clone().add(0,k,k), Effect.SNOWBALL_BREAK, 0);
                                }
                                if(passableBlocks.contains(location.getWorld().getBlockAt(location.clone().add(0,0,-k)).getType())){
                                    b = location.getWorld().getBlockAt(location.clone().add(0,0,-k));
                                    BlockPlaceEvent e2 = new BlockPlaceEvent(b, b.getState(), b, item, player, true);
                                    Bukkit.getPluginManager().callEvent(e2);
                                    if(!e2.isCancelled()){                                
                                        b.setType(frostStack.getType());
                                        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                                            b.setData((byte) frostStack.getDurability());
                                        }
                                    }
                                    
                                    //player.getWorld().playEffect(location.clone().add(0,k,-k), Effect.SNOWBALL_BREAK, 0);
                                }
                            }else{
                                //System.out.println("Z-axis");
                                //System.out.println("Yaw = " + yaw);
                                if(passableBlocks.contains(location.getWorld().getBlockAt(location.clone().add(k,0,0)).getType())){
                                    b = location.getWorld().getBlockAt(location.clone().add(k,0,0));
                                    BlockPlaceEvent e2 = new BlockPlaceEvent(b, b.getState(), b, item, player, true);
                                    Bukkit.getPluginManager().callEvent(e2);
                                    if(!e2.isCancelled()){                                
                                        b.setType(frostStack.getType());
                                        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                                            b.setData((byte) frostStack.getDurability());
                                        }
                                    }
                                    
                                    //player.getWorld().playEffect(location.clone().add(k,k,0), Effect.SNOWBALL_BREAK, 0);
                                }
                                if(passableBlocks.contains(location.getWorld().getBlockAt(location.clone().add(-k,0,0)).getType())){
                                    b = location.getWorld().getBlockAt(location.clone().add(-k,0,0));
                                    BlockPlaceEvent e2 = new BlockPlaceEvent(b, b.getState(), b, item, player, true);
                                    Bukkit.getPluginManager().callEvent(e2);
                                    if(!e2.isCancelled()){                                
                                        b.setType(frostStack.getType());
                                        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                                            b.setData((byte) frostStack.getDurability());
                                        }
                                    }
                                    
                                    //player.getWorld().playEffect(location.clone().add(-k,k,0), Effect.SNOWBALL_BREAK, 0);
                                }
                            }
                        }
                        
                        j++;
                        i++;
                        if(i > maxBlocks || i > distance){
                            cancel();
                            return;
                        }
                    }while(j < blocks);
                }
            }
        };
        task.runTask(before_ticks, ticks);
//</editor-fold>
    }
    
    public void executeIceBow(Entity ent){
        String name = ConfigManager.getConfig().getString("Objects.IceBow.freeze-material");
        ItemStack iceStack = ItemBuilder.newItem(name).build();
        boolean protect = ConfigManager.getConfig().getBoolean("Objects.IceBow.protect-structures");
        long before_ticks = ConfigManager.getConfig().getLong("Objects.IceBow.time-before-freezing");
        long ticks = ConfigManager.getConfig().getLong("Objects.IceBow.time-between-one-block-and-the-next");
        
        executeIceBow(ent, iceStack, protect, before_ticks, ticks);
    }
    
    public void executeIceBow(Entity ent, ItemStack iceStack, boolean protect, long before_ticks, long ticks){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Location l = ent.getLocation();
        
        Task task = new Task(){
            int x = l.getBlockX();
            int y = l.getBlockY();
            int z = l.getBlockZ();
            
            int i = 0;
            
            @Override
            public void run(){
                List<Block> blocks = new ArrayList();
                if(i>=2){
                    Block b = ent.getWorld().getBlockAt(x, y+i, z);
                    if(!protect || (protect && b.getType().equals(Material.AIR))){
                        ent.getWorld().playSound(l, XSound.BLOCK_GLASS_BREAK.parseSound(), 2.0F, 1.0F);
                        b.setType(iceStack.getType());
                        if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                            b.setData((byte) iceStack.getDurability());
                        }
                    }
                    cancel();
                    return;
                }else{
                    Block b1 = ent.getWorld().getBlockAt(x+1, y+i, z);
                    Block b2 = ent.getWorld().getBlockAt(x-1, y+i, z);
                    Block b3 = ent.getWorld().getBlockAt(x, y+i, z+1);
                    Block b4 = ent.getWorld().getBlockAt(x, y+i, z-1);
                    blocks.add(b1);
                    blocks.add(b2);
                    blocks.add(b3);
                    blocks.add(b4);
                    for(Block b : blocks){
                        if(!protect || (protect && b.getType().equals(Material.AIR))){
                            ent.getWorld().playSound(l, XSound.BLOCK_GLASS_BREAK.parseSound(), 2.0F, 1.0F);
                            b.setType(iceStack.getType());
                            if(NewAmazingLuckyBlocks.getMinecraftVersion().isLegacyVersion()){
                                b.setData((byte) iceStack.getDurability());
                            }
                        }
                    }
                }
                i++;
            }
        };
        
        if(ticks < 0){
            ticks = 0;
        }
        if(before_ticks < 0){
            before_ticks = 0;
        }
        
        task.runTask(before_ticks, ticks);     
//</editor-fold>
    }
}
