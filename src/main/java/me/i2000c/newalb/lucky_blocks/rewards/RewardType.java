package me.i2000c.newalb.lucky_blocks.rewards;

import com.cryptomorin.xseries.XMaterial;

import lombok.Getter;
import me.i2000c.newalb.lucky_blocks.rewards.types.BlockReplacingSphereReward;
import me.i2000c.newalb.lucky_blocks.rewards.types.BlockReward;
import me.i2000c.newalb.lucky_blocks.rewards.types.CommandReward;
import me.i2000c.newalb.lucky_blocks.rewards.types.DarkHoleReward;
import me.i2000c.newalb.lucky_blocks.rewards.types.EffectReward;
import me.i2000c.newalb.lucky_blocks.rewards.types.EntityReward;
import me.i2000c.newalb.lucky_blocks.rewards.types.EntityTowerReward;
import me.i2000c.newalb.lucky_blocks.rewards.types.ExplosionReward;
import me.i2000c.newalb.lucky_blocks.rewards.types.FireworkReward;
import me.i2000c.newalb.lucky_blocks.rewards.types.ItemReward;
import me.i2000c.newalb.lucky_blocks.rewards.types.LightningReward;
import me.i2000c.newalb.lucky_blocks.rewards.types.MessageReward;
import me.i2000c.newalb.lucky_blocks.rewards.types.MiniVolcanoReward;
import me.i2000c.newalb.lucky_blocks.rewards.types.SoundReward;
import me.i2000c.newalb.lucky_blocks.rewards.types.SquidExplosionReward;
import me.i2000c.newalb.lucky_blocks.rewards.types.StructureReward;
import me.i2000c.newalb.lucky_blocks.rewards.types.TeleportReward;
import me.i2000c.newalb.lucky_blocks.rewards.types.TrapReward;
import me.i2000c.newalb.utils.reflection.RefClass;

@Getter
public enum RewardType {
    item(ItemReward.class, XMaterial.IRON_INGOT, "&aCreate item rewards"),
    command(CommandReward.class, XMaterial.NAME_TAG, "&7Create command rewards"),
    entity(EntityReward.class, XMaterial.BONE, "&5Create entity rewards"),
    tower_entity(EntityTowerReward.class, XMaterial.ARMOR_STAND, "&eCreate entity tower rewards"),
    firework(FireworkReward.class, XMaterial.FIREWORK_ROCKET, "&bCreate firework rewards"),
    sound(SoundReward.class, XMaterial.JUKEBOX, "&dCreate sound rewards"),
    structure(StructureReward.class, XMaterial.BRICKS, "&3Create structure rewards"),
    block(BlockReward.class, XMaterial.BRICK, "&dCreate block rewards"),
    lightning(LightningReward.class, XMaterial.WHITE_WOOL, "&eCreate lightning rewards"),
    dark_hole(DarkHoleReward.class, XMaterial.BUCKET, "&8Create dark hole rewards"),
    mini_volcano(MiniVolcanoReward.class, XMaterial.LAVA_BUCKET, "&cCreate mini volcano rewards"),
    message(MessageReward.class, XMaterial.BOOK, "&7Create message rewards"),
    effect(EffectReward.class, XMaterial.POTION, "&5Create effect rewards"),
    explosion(ExplosionReward.class, XMaterial.TNT, "&4Create explosion rewards"),
    block_replacing_sphere(BlockReplacingSphereReward.class, XMaterial.DIAMOND_ORE, "&bCreate block replacing sphere (BRS) rewards"),
    trap(TrapReward.class, XMaterial.OAK_PRESSURE_PLATE, "&5Create trap rewards"),
    teleport(TeleportReward.class, XMaterial.COMPASS, "&eCreate teleport rewards"),
    squid_explosion(SquidExplosionReward.class, XMaterial.INK_SAC, "&7Create squid explosion rewards"),;
    
    public static final RewardType[] VALUES = values();
    
    private final RefClass rewardClass;
    private final XMaterial rewardItem;
    private final String rewardDisplayName;
    
    private RewardType(Class<? extends Reward<?>> rewardClass, XMaterial rewardItem, String rewardDisplayName) {
        this.rewardClass = RefClass.of(rewardClass);
        this.rewardItem = rewardItem;
        this.rewardDisplayName = rewardDisplayName;
    }
    
    public Reward<?> createReward(Outcome outcome) {
        return this.rewardClass.callConstructor(outcome);
    }
}
