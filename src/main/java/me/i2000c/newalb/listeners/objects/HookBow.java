package me.i2000c.newalb.listeners.objects;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import me.i2000c.newalb.MinecraftVersion;
import me.i2000c.newalb.config.ConfigManager;
import me.i2000c.newalb.listeners.interact.CustomProjectileHitEvent;
import me.i2000c.newalb.listeners.interact.SpecialItem;
import me.i2000c.newalb.listeners.objects.utils.BowUtils;
import me.i2000c.newalb.listeners.objects.utils.HookBowAux;
import me.i2000c.newalb.reflection.ReflectionManager;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.ItemStackWrapper;
import me.i2000c.newalb.utils2.MetadataManager;
import me.i2000c.newalb.utils2.OtherUtils;
import me.i2000c.newalb.utils2.Task;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class HookBow extends SpecialItem {
    private final HashMap<Player, HookData> players = new HashMap<>();
    
    private double maxDistance;
    private long maxFlySeconds;
    private boolean stopArrowOnLiquid;
    private double arrowSpeed;
    
    private double heightGain;
    private double gravity;
    
    private int leashPacketRadius;
    private long leashTimeoutSeconds;
    
    @Override
    public ItemStack buildItem(){
        this.maxDistance = ConfigManager.getMainConfig().getDouble(super.itemPathKey + ".maxDistance");
        this.maxFlySeconds = ConfigManager.getMainConfig().getLong(super.itemPathKey + ".maxFlySeconds");
        this.stopArrowOnLiquid = ConfigManager.getMainConfig().getBoolean(super.itemPathKey + ".stopArrowOnLiquid");
        this.arrowSpeed = ConfigManager.getMainConfig().getDouble(super.itemPathKey + ".arrowSpeed");
        this.heightGain = ConfigManager.getMainConfig().getDouble(super.itemPathKey + ".heightGain");
        this.gravity = ConfigManager.getMainConfig().getDouble(super.itemPathKey + ".gravity");
        this.leashPacketRadius = ConfigManager.getMainConfig().getInt(super.itemPathKey + ".leashPacketRadius");
        this.leashTimeoutSeconds = ConfigManager.getMainConfig().getLong(super.itemPathKey + ".leashTimeoutSeconds");
        
        return ItemStackWrapper.newItem(XMaterial.BOW)
                .addEnchantment(XEnchantment.POWER, 1)
                .toItemStack();
    }
    
    private void placeLeash(Arrow arrow, Entity target){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player shooter = (Player) arrow.getShooter();
        HookData hookData = players.get(shooter);
        if(hookData == null){
            return;
        }
        
        hookData.targetLocation = null;
        hookData.chicken = HookBowAux.createEntityChicken(arrow.getLocation());
        MetadataManager.setClassMetadata(hookData.chicken, this);
        
        if(shooter.isOnline() && shooter.getWorld().equals(arrow.getWorld())){
            HookBowAux.sendLeashAttachPacket(shooter, hookData.chicken, getPlayersNearPlayer(shooter, leashPacketRadius));
            Logger.sendTitle("", "&6Punch to pull (Right click)", shooter);
        }
        
        hookData.removeArrow();
        hookData.startLeashingTimeout();
        Task.runTask(() -> {
            target.setPassenger(hookData.chicken);
        }, 2L);        
//</editor-fold>
    }
    private void placeLeash(Arrow arrow, Location target){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player shooter = (Player) arrow.getShooter();
        HookData hookData = players.get(shooter);
        if(hookData == null){
            return;
        }
        
        hookData.targetLocation = target;
        hookData.chicken = HookBowAux.createEntityChicken(hookData.targetLocation.clone().add(0, -0.8, 0));
        MetadataManager.setClassMetadata(hookData.chicken, this);
        
        if(shooter.isOnline() && shooter.getWorld().equals(arrow.getWorld())){
            HookBowAux.sendLeashAttachPacket(shooter, hookData.chicken, getPlayersNearPlayer(shooter, leashPacketRadius));
            Logger.sendTitle("", "&6Punch to pull (Right click)", shooter);
        }
        
        hookData.removeArrow();
        hookData.startLeashingTimeout();
//</editor-fold>
    }    
    
    @Override
    public void onArrowShooted(EntityShootBowEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!(e.getEntity() instanceof Player)){
            return;
        }
        
        if(!(e.getProjectile() instanceof Arrow)){
            return;
        }
        
        Player shooter = (Player) e.getEntity();
        Arrow arrow = (Arrow) e.getProjectile();
        
        HookData hookData;
        if(players.containsKey(shooter)){
            hookData = players.get(shooter);
        }else{
            hookData = new HookData(shooter);
            players.put(shooter, hookData);
        }
        
        if(hookData.state != HookState.BEFORE_ARROW_SHOOT){
            e.setCancelled(true);
            return;
        }
        
        hookData.state = HookState.WAITING_ARROW_HIT;
        
        // Shoot the arrow
        hookData.arrow = arrow;
        MetadataManager.setClassMetadata(hookData.arrow, this);
        
        double maxDistanceSquared = maxDistance*maxDistance;
        long maxFlyTicks = maxFlySeconds*20;
        Task task = new Task(){
            Location playerLocation = shooter.getLocation();
            int i = 0;
            final int minY = OtherUtils.getMinWorldHeight(arrow.getWorld());

            @Override
            public void run(){
                try{
                    if(!hookData.isArrowAlive()){
                        cancel();
                        if(hookData.arrow != null && hookData.arrow.getLocation().getBlockY() < minY){
                            Logger.sendTitle("Max distance", "exceeded", shooter);
                            hookData.removeArrow();
                            hookData.state = HookState.BEFORE_ARROW_SHOOT;
                        }
                    }else if(i >= maxFlyTicks){
                        cancel();
                        if(hookData.arrow != null){
                            Logger.sendTitle("Max flying time", "exceeded", shooter);
                            hookData.removeArrow();
                            hookData.state = HookState.BEFORE_ARROW_SHOOT;
                        }
                    }else if(hookData.arrow != null && 
                             hookData.arrow.getLocation().distanceSquared(playerLocation) > maxDistanceSquared){
                        cancel();
                        if(hookData.arrow != null){
                            Logger.sendTitle("Max flying distance", "exceeded", shooter);
                            hookData.removeArrow();
                            hookData.state = HookState.BEFORE_ARROW_SHOOT;
                        }
                    }

                    if(stopArrowOnLiquid && hookData.arrow != null){
                        Block block = hookData.arrow.getLocation().getBlock();
                        if(block.isLiquid() && block.getRelative(0, -1, 0).isLiquid()){
                            try{
                                cancel();
                                hookData.arrow.remove();
                                ProjectileHitEvent event = new ProjectileHitEvent(hookData.arrow);
                                onArrowHit(new CustomProjectileHitEvent(event));
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
//</editor-fold>
    }
    
    @Override
    public void onArrowHit(CustomProjectileHitEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Projectile projectile = e.getProjectile();
        if(!(projectile instanceof Arrow)){
            return;
        }
        
        Arrow arrow = (Arrow) projectile;
        if(!(arrow.getShooter() instanceof Player)){
            return;
        }
        
        Player shooter = (Player) arrow.getShooter();
        HookData hookData = players.get(shooter);
        if(hookData == null || hookData.state != HookState.WAITING_ARROW_HIT){
            return;
        }
        
        Entity hitEntity = e.getHitEntity();
        Block hitBlock = e.getHitBlock();

        if(hitEntity != null){
            placeLeash((Arrow) projectile, hitEntity);
        }else if(hitBlock != null){
            placeLeash((Arrow) projectile, hitBlock.getLocation());
        }
        
        hookData.state = HookState.WAITING_PLAYER_ACTION;
//</editor-fold>
    }
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = e.getPlayer();
        
        HookData hookData = players.get(player);
        if(arrowSpeed > 0 && (hookData == null || hookData.state == HookState.BEFORE_ARROW_SHOOT)) {
            if(e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }
            
            // Generate EntityShootBowEvent
            e.setCancelled(true);
            Vector velocity = player.getLocation().getDirection().multiply(arrowSpeed);
            ItemStack bow = player.getItemInHand();
            boolean isInfiniteBow = BowUtils.isInfiniteBow(player, bow);
            boolean isFireBow = BowUtils.isFireBow(bow);
            
            Optional<ItemStack> arrowItem = BowUtils.getArrowFromPlayerInventory(player, !isInfiniteBow);
            if(!arrowItem.isPresent()) {
                if(isInfiniteBow) {
                    arrowItem = Optional.of(ItemStackWrapper.newItem(XMaterial.ARROW).toItemStack());
                } else {
                    return;
                }
            }
            
            BowUtils.applyDurability(player, bow, 1, 1);
            BowUtils.cancelBowCharging(player);
            Arrow arrow = BowUtils.launchArrow(player, arrowItem.get(), isFireBow, isInfiniteBow, velocity);
            
            EntityShootBowEvent event;
            if(MinecraftVersion.CURRENT_VERSION.isGreaterThanOrEqual(MinecraftVersion.v1_16)) {
                event = ReflectionManager.callConstructor(EntityShootBowEvent.class, player, bow, null, arrow, null, 0f, !isInfiniteBow);
            } else {
                event = new EntityShootBowEvent(player, bow, arrow, 0f);
            }
                    
            onArrowShooted(event);
            return;
        } else if(hookData == null || hookData.state != HookState.WAITING_PLAYER_ACTION) {
            return;
        }
        
        e.setCancelled(true);
        hookData.state = HookState.WAITING_PLAYER_LAND;

        if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR){
            Task.runTask(() -> {
                Logger.removeTitle(player);
            
                Location loc1 = player.getLocation();
                Location loc2 = hookData.targetLocation != null ? hookData.targetLocation : hookData.chicken.getLocation();
                
                FallingBlock fb = player.getWorld().spawnFallingBlock(player.getLocation(), Material.WATER, (byte) 0);
                MetadataManager.setClassMetadata(fb, this);
                Vector v = calculateVelocity(loc1, loc2, heightGain, gravity);
                fb.setDropItem(false);
                fb.setVelocity(v);
                fb.setPassenger(player);
                hookData.stopLeashingTimeout();

                Task task = new Task(){
                    @Override
                    public void run(){
                        try{
                            if(!fb.getLocation().getBlock().isEmpty()
                                    || fb.isDead() || fb.isEmpty()){
                                cancel();
                                fb.remove();
                                hookData.removeChicken();
                                HookBow.super.getPlayerCooldown().updateCooldown(player);
                                hookData.state = HookState.BEFORE_ARROW_SHOOT;
                            }
                        }catch(Exception ex){
                            ex.printStackTrace();
                            cancel();
                        }
                    }
                };
                task.runTask(5L, 1L);
            }, 2L);
        }else if(e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR){
            Logger.removeTitle(e.getPlayer());
            
            hookData.stopLeashingTimeout();
            hookData.removeChicken();            
            if(e.getPlayer().isOnline()){
                Logger.sendMessage("Leash has been removed", e.getPlayer());
            }
            
            super.getPlayerCooldown().updateCooldown(player);
            hookData.state = HookState.BEFORE_ARROW_SHOOT;
        }
//</editor-fold>
    }
    
    @Override
    public void onFallingBlockConvert(EntityChangeBlockEvent e){
        e.getEntity().remove();
        e.setCancelled(true);
    }
    
    @Override
    public void onEntityDamaged(EntityDamageByEntityEvent e){        
        if(e.getEntity() instanceof Chicken){
            //Remove chicken if damaged in creative mode
            e.setCancelled(true);
            e.getEntity().remove();            
            if(e.getDamager() instanceof Player){
                players.remove((Player) e.getDamager());
            }
        }
    }
    
    @Override
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e){
        //Send packet again
        if(e.getRightClicked() instanceof Chicken){
            e.setCancelled(true);
            if(players.containsKey(e.getPlayer())){
                HookBowAux.sendLeashAttachPacket(e.getPlayer(), (Chicken) e.getRightClicked(), getPlayersNearPlayer(e.getPlayer(), leashPacketRadius));
                onPlayerInteract(new PlayerInteractEvent(e.getPlayer(), Action.RIGHT_CLICK_AIR, e.getPlayer().getItemInHand(), null, null));
            }
        }
    }
    
    //https://bukkit.org/threads/setvelocity-vector-and-parabolic-motion.86661/#post-1615773
    private static Vector calculateVelocity(Location loc1, Location loc2, double heightGain, double gravity){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Objects.requireNonNull(loc1);
        Objects.requireNonNull(loc2);
        
        if(!loc1.getWorld().equals(loc2.getWorld())){
            throw new IllegalArgumentException("Loc 1 and loc 2 must be in the same world");
        }
        
        int x1 = loc1.getBlockX();
        int x2 = loc2.getBlockX();
        int z1 = loc1.getBlockZ();
        int z2 = loc2.getBlockZ();
        if(x1 == x2 && z1 == z2){
            // If loc1 is in the same column than loc2, return a vertical vector
            return new Vector(0, 1, 0);
        }
        
        Vector from = loc1.toVector();
        Vector to = loc2.toVector();
        
        // Block locations
        int endGain = to.getBlockY() - from.getBlockY();
        double horizDist = Math.sqrt(loc1.distanceSquared(loc2));
        
        // Height gain
        double gain = heightGain;
        
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
    
    private class HookData{
        //<editor-fold defaultstate="collapsed" desc="Code">
        public final Player player;
        public Arrow arrow;
        public Location targetLocation;
        public Chicken chicken;
        public HookState state;
        
        private final Task leashTimeoutTask;
        
        public HookData(Player player){
            this.player = player;
            this.state = HookState.BEFORE_ARROW_SHOOT;
            this.leashTimeoutTask = new Task(){
                @Override
                public void run(){
                    removeArrow();
                    removeChicken();
                    targetLocation = null;
                    if(player.isOnline()){
                        Logger.sendMessage("&cLeash time has expired", player, false);
                    }
                    
                    HookBow.super.getPlayerCooldown().updateCooldown(player);
                    state = HookState.BEFORE_ARROW_SHOOT;
                }
            };
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
            return leashTimeoutTask.isStarted();
        }
        public void startLeashingTimeout(){
            if(!isLeashingTimeoutStarted()){
                leashTimeoutTask.runTask(leashTimeoutSeconds * 20L);
            }
        }
        public void stopLeashingTimeout(){
            leashTimeoutTask.cancel();
        }
//</editor-fold>
    }
    
    private static enum HookState{
        BEFORE_ARROW_SHOOT,
        WAITING_ARROW_HIT,
        WAITING_PLAYER_ACTION,
        WAITING_PLAYER_LAND;
    }
}
