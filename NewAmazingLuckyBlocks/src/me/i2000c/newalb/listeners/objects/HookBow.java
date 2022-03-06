package me.i2000c.newalb.listeners.objects;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import me.i2000c.newalb.NewAmazingLuckyBlocks;
import me.i2000c.newalb.utils.BowItem;
import me.i2000c.newalb.utils.ConfigManager;
import me.i2000c.newalb.utils.LangLoader;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils.WorldList;
import me.i2000c.newalb.utils2.Task;
import me.i2000c.newalb.utils2.OtherUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class HookBow extends BowItem{
    private static final String TAG = "hook_bow";
    
    private static final HashMap<Player, HookData> players = new HashMap<>();
    
    private static class HookData{
        //<editor-fold defaultstate="collapsed" desc="Code">
        public final Player player;
        public Arrow arrow;
        public Location targetLocation;
        public Chicken chicken;
        
        private final Task leashTimeout;
        private long cooldown;
        
        private boolean leashingTimeoutStarted;
        private boolean hookBowTimeoutStarted;
        
        private boolean valid;
        
        public HookData(Player player){
            this.player = player;
            cooldown = 0L;
            valid = false;
            leashingTimeoutStarted = false;
            hookBowTimeoutStarted = false;
            leashTimeout = new Task(){
                @Override
                public void run(){
                    removeArrow();
                    removeChicken();
                    targetLocation = null;
                    if(player.isOnline()){
                        Logger.sendMessage("Leash time has expired", player);
                    }
                    leashingTimeoutStarted = false;
                    valid = false;
                    startHookBowTimeout();
                }
            };
        }
        
        public void setValid(boolean valid){
            this.valid = valid;
        }
        public boolean isValid(){
            return valid;
        }
        
        public long getSecondsLeft(){
            if(hookBowTimeoutStarted){
                return (cooldown - System.currentTimeMillis()) / 1000L;
            }else{
                return -1;
            }
        }
        
        public boolean isArrowAlive(){
            return arrow != null && !arrow.isDead() && !arrow.isOnGround();
        }
        
        public void removeArrow(){
            if(arrow != null){
                arrow.remove();
                arrow = null;
            }
        }
        public void removeChicken(){
            if(chicken != null){
                chicken.remove();
                chicken = null;
            }
        }
        
        public boolean isLeashingTimeoutStarted(){
            return leashingTimeoutStarted;
        }
        public void startLeashingTimeout(){
            if(!isLeashingTimeoutStarted()){
                leashingTimeoutStarted = true;
                int leashTimeoutSeconds = ConfigManager.getConfig().getInt("Objects.HookBow.leashTimeoutSeconds");
                leashTimeout.runTask(leashTimeoutSeconds * 20L);
            }
        }
        public void stopLeashingTimeout(){
            if(isLeashingTimeoutStarted()){
                try{
                    leashTimeout.cancel();
                }catch(Exception ex){                
                }
                leashingTimeoutStarted = false;
            }
        }
        public boolean isHookBowTimeoutStarted(){
            return hookBowTimeoutStarted;
        }
        public void startHookBowTimeout(){
            if(!hookBowTimeoutStarted){
                hookBowTimeoutStarted = true;
                int hookBowTimeoutSeconds = ConfigManager.getConfig().getInt("Objects.HookBow.cooldown-time");
                if(hookBowTimeoutSeconds > 0){
                    cooldown = System.currentTimeMillis() + hookBowTimeoutSeconds*1000;
                }
            }
        }
//</editor-fold>
    }
    
    //https://bukkit.org/threads/setvelocity-vector-and-parabolic-motion.86661/#post-1615773
    private static Vector calculateVelocity(Location loc1, Location loc2, int heightGain, double gravity){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Objects.requireNonNull(loc1);
        Objects.requireNonNull(loc2);
        
        if(!loc1.getWorld().equals(loc2.getWorld())){
            throw new IllegalArgumentException("Loc 1 and loc 2 must be in the same world");
        }
        
        if(loc1.getBlock().getLocation().equals(loc2.getBlock().getLocation())){
            return new Vector(0, 1, 0);
        }
        
        Vector from = loc1.toVector();
        Vector to = loc2.toVector();
        
        // Block locations
        int endGain = to.getBlockY() - from.getBlockY();
        double horizDist = Math.sqrt(loc1.distanceSquared(loc2));
        
        // Height gain
        int gain = heightGain;
        
        double maxGain = gain > (endGain + gain) ? gain : (endGain + gain);
        
        // Solve quadratic equation for velocity
        double a = -horizDist * horizDist / (4 * maxGain);
        double b = horizDist;
        double c = -endGain;
        
        double slope = -b / (2 * a) - Math.sqrt(b * b - 4 * a * c) / (2 * a);
        
        // Vertical velocity
        double vy = Math.sqrt(maxGain * gravity);
        
        // Horizontal velocity
        double vh = vy / slope;
        
        // Calculate horizontal direction
        int dx = to.getBlockX() - from.getBlockX();
        int dz = to.getBlockZ() - from.getBlockZ();
        double mag = Math.sqrt(dx * dx + dz * dz);
        double dirx = dx / mag;
        double dirz = dz / mag;
        
        // Horizontal velocity components
        double vx = vh * dirx;
        double vz = vh * dirz;
        
        return new Vector(vx, vy, vz);
//</editor-fold>
    }
    
    private static List<Player> getPlayersNearPlayer(Player player, int radius){
        List<Player> playerList = player.getWorld().getPlayers();
        playerList.removeIf(p -> p.getLocation().distanceSquared(player.getLocation()) > radius*radius);
        return playerList;
    }
    
    private static void placeLeash(Arrow arrow, Entity target){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player shooter = (Player) arrow.getShooter();
        HookData data = players.get(shooter);
        if(data == null){
            return;
        }
        
        data.targetLocation = null;
        data.chicken = HookBowAux.createEntityChicken(arrow.getLocation());
        data.chicken.setMetadata(TAG, new FixedMetadataValue(NewAmazingLuckyBlocks.getInstance(), TAG));
        
        if(shooter.isOnline() && shooter.getWorld().equals(arrow.getWorld())){
            int leashPacketRadius = ConfigManager.getConfig().getInt("Objects.HookBow.leashPacketRadius");
            HookBowAux.sendLeashAttachPacket(shooter, data.chicken, getPlayersNearPlayer(shooter, leashPacketRadius));
            shooter.sendTitle(Logger.color("&o"), Logger.color("&6Punch to pull (Right click)"));
        }
        
        data.removeArrow();
        data.startLeashingTimeout();
        
        Task.runTask(() -> {
            target.setPassenger(data.chicken);
            data.valid = true;
        }, 2L);
//</editor-fold>
    }
    private static void placeLeash(Arrow arrow, Location target){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player shooter = (Player) arrow.getShooter();
        HookData data = players.get(shooter);
        if(data == null){
            return;
        }
        
        data.targetLocation = target;
        data.chicken = HookBowAux.createEntityChicken(data.targetLocation.clone().add(0, -0.8, 0));
        data.chicken.setMetadata(TAG, new FixedMetadataValue(NewAmazingLuckyBlocks.getInstance(), TAG));
        
        if(shooter.isOnline() && shooter.getWorld().equals(arrow.getWorld())){
            int leashPacketRadius = ConfigManager.getConfig().getInt("Objects.HookBow.leashPacketRadius");
            HookBowAux.sendLeashAttachPacket(shooter, data.chicken, getPlayersNearPlayer(shooter, leashPacketRadius));
            shooter.sendTitle(Logger.color("&o"), Logger.color("&6Punch to pull (Right click)"));
        }
        
        data.removeArrow();
        data.startLeashingTimeout();
        data.valid = true;
//</editor-fold>
    }
    
    
    @EventHandler
    private void onArrowShooted(EntityShootBowEvent e) throws Exception{
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!WorldList.isRegistered(e.getEntity().getWorld().getName())){
            return;
        }
        
        if(e.getEntity() != null && e.getEntity() instanceof Player){
            Player shooter = (Player) e.getEntity();
            ItemStack item = e.getBow();
            if(item != null && item.hasItemMeta()){
                ItemMeta meta = item.getItemMeta();
                if(meta.hasDisplayName()){
                    ItemStack object = getItem();
                    String name = object.getItemMeta().getDisplayName();
                    if(item.getType().equals(Material.BOW) && meta.getDisplayName().equals(name)){
                        if(OtherUtils.checkPermission(shooter, "Objects.HookBow")){
                            HookData auxData = players.get(shooter);
                            if(auxData != null){
                                if(auxData.hookBowTimeoutStarted){
                                    long timeLeft = auxData.getSecondsLeft();
                                    if(timeLeft < 0){
                                        players.remove(shooter);
                                    }else{
                                        Logger.sendMessage(LangLoader.getMessages().getString("Cooldown-message").replace("%time%", timeLeft + ""), shooter);
                                        e.setCancelled(true);
                                        return;
                                    }
                                }else{
                                    Logger.sendMessage("You cannot shoot more than one arrow at the same time", shooter);
                                    e.setCancelled(true);
                                    return;
                                }                                
                            }
                            
                            HookData data = new HookData(shooter);
                            data.arrow = (Arrow) e.getProjectile();
                            data.arrow.setMetadata(TAG, new FixedMetadataValue(NewAmazingLuckyBlocks.getInstance(), true));
                            players.put(shooter, data);
                            
                            int maxDistance = ConfigManager.getConfig().getInt("Objects.HookBow.maxDistance");
                            int maxDistanceSquared = maxDistance*maxDistance;
                            long maxFlyTicks = ConfigManager.getConfig().getLong("Objects.HookBow.maxFlySeconds")*20;
                            boolean stopArrowOnLiquid = ConfigManager.getConfig().getBoolean("Objects.HookBow.stopArrowOnLiquid");
                            Task task = new Task(){
                                Location playerLocation = shooter.getLocation();
                                int i = 0;
                                
                                @Override
                                public void run(){
                                    try{
                                        if(!data.isArrowAlive()){
                                            cancel();
                                            if(data.arrow != null && data.arrow.getLocation().getBlockY() < 0){
                                                shooter.sendTitle("Max distance", "exceeded");
                                                data.removeArrow();
                                                players.remove(shooter);
                                            }
                                        }else if(i >= maxFlyTicks){
                                            cancel();
                                            if(data.arrow != null){
                                                shooter.sendTitle("Max time", "exceeded");
                                                data.removeArrow();
                                                players.remove(shooter);
                                            }
                                        }else if(data.arrow != null && 
                                                 data.arrow.getLocation().distanceSquared(playerLocation) > maxDistanceSquared){
                                            cancel();
                                            if(data.arrow != null){
                                                shooter.sendTitle("Max distance", "exceeded");
                                                data.removeArrow();
                                                players.remove(shooter);
                                            }
                                        }
                                        
                                        if(stopArrowOnLiquid && data.arrow != null){
                                            Block block = data.arrow.getLocation().getBlock();
                                            if(block.isLiquid() && block.getRelative(0, -1, 0).isLiquid()){
                                                try{
                                                    cancel();
                                                    data.arrow.remove();
                                                    onArrowHit(new ProjectileHitEvent(data.arrow));
                                                }catch(Exception ex){                                                    
                                                }                                                    
                                            }
                                        }
                                            
                                        i++;
                                    }catch(Exception ex){
                                        ex.printStackTrace();
                                        cancel();
                                    }
                                }
                            };
                            task.runTask(0L, 1L);
                        }
                    }
                }
            }
        }
//</editor-fold>
    }
    
    @EventHandler
    private void onArrowHit(ProjectileHitEvent e) throws Exception{
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(e.getEntity() instanceof Arrow && e.getEntity().hasMetadata(TAG)){
            Task.runTask(() -> {
                if(e.getEntity().getMetadata(TAG).get(0).asBoolean()){
                    Arrow arrow = (Arrow) e.getEntity();
                    placeLeash(arrow, arrow.getLocation());
                }
            }, 2L);
        }
//</editor-fold>
    }
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = e.getPlayer();
        if(!WorldList.isRegistered(player.getWorld().getName())){
            return;
        }
        
        ItemStack object = getItem();
        if(!OtherUtils.checkItemStack(object, e.getItem())){
            return;
        }
        
        if(!OtherUtils.checkPermission(player, "LuckyTool")){
            return;
        }
        
        HookData data = players.get(player);
        if(data == null || !data.isValid()){
            return;
        }

        if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR){
            int heightGain = ConfigManager.getConfig().getInt("Objects.HookBow.heightGain");
            double gravity = ConfigManager.getConfig().getDouble("Objects.HookBow.gravity");

            e.setCancelled(true);
            player.sendTitle(Logger.color("&o"), Logger.color("&o"));
            Location loc1 = player.getLocation();
            Location loc2 = data.targetLocation != null ? data.targetLocation : data.chicken.getLocation();
            FallingBlock fb = player.getWorld().spawnFallingBlock(player.getLocation(), Material.WATER, (byte) 0);
            fb.setMetadata(TAG, new FixedMetadataValue(NewAmazingLuckyBlocks.getInstance(), TAG));
            Vector v = calculateVelocity(loc1, loc2, heightGain, gravity);
            fb.setDropItem(false);
            fb.setVelocity(v);
            fb.setPassenger(player);
            data.setValid(false);
            data.stopLeashingTimeout();

            Task task = new Task(){
                @Override
                public void run(){
                    try{
                        if(!fb.getLocation().getBlock().isEmpty()
                                || fb.isDead() || fb.isEmpty()){
                            cancel();
                            fb.remove();
                            data.removeChicken();
                            data.startHookBowTimeout();
                        }
                    }catch(Exception ex){
                        ex.printStackTrace();
                        cancel();
                    }
                }
            };
            task.runTask(5L, 1L);
        }else if(e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR){
            e.setCancelled(true);
            e.getPlayer().sendTitle(Logger.color("&o"), Logger.color("&o"));
            data.setValid(false);
            data.stopLeashingTimeout();
            data.removeChicken();
            data.startHookBowTimeout();
            if(e.getPlayer().isOnline()){
                Logger.sendMessage("Leash has been removed", e.getPlayer());
            }
        }
//</editor-fold>
    }
    
    
    
    @EventHandler
    private void onFallingBlockConvert(EntityChangeBlockEvent e){
        if(e.getEntity().hasMetadata(TAG)){
            e.getEntity().remove();
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    private void onEntityDamaged(EntityDamageByEntityEvent e){        
        if(e.getDamager() instanceof Arrow && e.getDamager().hasMetadata(TAG)){
           e.getDamager().setMetadata(TAG, new FixedMetadataValue(NewAmazingLuckyBlocks.getInstance(), false));
           placeLeash((Arrow) e.getDamager(), e.getEntity());
        }else if(e.getEntity() instanceof Chicken && e.getEntity().hasMetadata(TAG)){
            //Remove chicken if damaged in creative mode
            e.getEntity().remove();
            e.setCancelled(true);
            if(e.getDamager() instanceof Player){
                players.remove((Player) e.getDamager());
            }
        }
    }
    
    @EventHandler
    private void PlayerInteract2(PlayerInteractAtEntityEvent e){
        //Send packet again
        if(e.getRightClicked() instanceof Chicken){
            if(e.getRightClicked().hasMetadata(TAG)){
                e.setCancelled(true);
                if(players.containsKey(e.getPlayer())){
                    int leashPacketRadius = ConfigManager.getConfig().getInt("Objects.HookBow.leashPacketRadius");
                    HookBowAux.sendLeashAttachPacket(e.getPlayer(), (Chicken) e.getRightClicked(), getPlayersNearPlayer(e.getPlayer(), leashPacketRadius));
                    onPlayerInteract(new PlayerInteractEvent(e.getPlayer(), Action.RIGHT_CLICK_AIR, e.getPlayer().getItemInHand(), null, null));
                }
            }
        }
    }
    
    @Override
    public ItemStack buildItem(){
        ItemStack stack = new ItemStack(Material.BOW);
        
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Logger.color(LangLoader.getMessages().getString("Objects.HookBow.name")));
        meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        stack.setItemMeta(meta);
        
        return stack;
    }
}
