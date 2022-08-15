package me.i2000c.newalb.custom_outcomes.rewards;

import java.lang.reflect.Constructor;
import me.i2000c.newalb.custom_outcomes.editor.EditorType;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.BlockReplacingSphereReward;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.BlockReward;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.CommandReward;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.DarkHoleReward;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.EffectReward;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.EntityReward;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.EntityTowerReward;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.ExplosionReward;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.FireworkReward;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.ItemReward;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.LightningReward;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.MessageReward;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.MiniVolcanoReward;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.ParticleReward;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.SoundReward;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.StructureReward;
import me.i2000c.newalb.custom_outcomes.rewards.reward_types.TrapReward;

public enum RewardType{
    item(ItemReward.class, EditorType.ITEM_REWARD),
    command(CommandReward.class, EditorType.COMMAND_REWARD),
    entity(EntityReward.class, EditorType.ENTITY_REWARD),
    tower_entity(EntityTowerReward.class, EditorType.ENTITY_TOWER_REWARD),
    firework(FireworkReward.class, EditorType.FIREWORK_REWARD),
    sound(SoundReward.class, EditorType.SOUND_REWARD),
    structure(StructureReward.class, EditorType.STRUCTURE_REWARD),
    block(BlockReward.class, EditorType.BLOCK_REWARD),
    lightning(LightningReward.class, EditorType.LIGHTNING_REWARD),
    dark_hole(DarkHoleReward.class, EditorType.DARK_HOLE_REWARD),
    mini_volcano(MiniVolcanoReward.class, EditorType.MINI_VOLCANO_REWARD),
    message(MessageReward.class, EditorType.MESSAGE_REWARD),
    effect(EffectReward.class, EditorType.EFFECT_REWARD),
    explosion(ExplosionReward.class, EditorType.EXPLOSION_REWARD),
    block_replacing_sphere(BlockReplacingSphereReward.class, EditorType.BLOCK_REPLACING_SPHERE_REWARD),
    trap(TrapReward.class, EditorType.TRAP_REWARD),
    
    particle(ParticleReward.class, null);
    
    private final Constructor rewardConstructor;
    private final EditorType editorType;

    private RewardType(Class rewardClass, EditorType editorType){
        try{
            this.rewardConstructor = rewardClass.getConstructor(Outcome.class);
            this.editorType = editorType;
        }catch(Exception ex){
            throw new Error(ex);
        }
    }
    
    public Reward createReward(Outcome outcome){
        try{
            return (Reward) rewardConstructor.newInstance(outcome);
        }catch(Exception ex){
            throw new Error(ex);
        }
    }
    
    public EditorType getEditorType(){
        return this.editorType;
    }
}
