package me.i2000c.newalb.custom_outcomes.editor;

import me.i2000c.newalb.custom_outcomes.menus.BlockMenu;
import me.i2000c.newalb.custom_outcomes.menus.BlockReplacingSphereMenu;
import me.i2000c.newalb.custom_outcomes.menus.ColorMenu;
import me.i2000c.newalb.custom_outcomes.menus.CommandMenu;
import me.i2000c.newalb.custom_outcomes.menus.DarkHoleMenu;
import me.i2000c.newalb.custom_outcomes.menus.DelayerMenu;
import me.i2000c.newalb.custom_outcomes.menus.EffectMenu;
import me.i2000c.newalb.custom_outcomes.menus.EnchantmentMenu;
import me.i2000c.newalb.custom_outcomes.menus.EntityMenu;
import me.i2000c.newalb.custom_outcomes.menus.EntityTowerMenu;
import me.i2000c.newalb.custom_outcomes.menus.EquipmentMenu;
import me.i2000c.newalb.custom_outcomes.menus.ExplosionMenu;
import me.i2000c.newalb.custom_outcomes.menus.FireworkMenu;
import me.i2000c.newalb.custom_outcomes.menus.ItemMenu;
import me.i2000c.newalb.custom_outcomes.menus.LightningMenu;
import me.i2000c.newalb.custom_outcomes.menus.LuckyBlockTypeListMenu;
import me.i2000c.newalb.custom_outcomes.menus.LuckyBlockTypeMenu;
import me.i2000c.newalb.custom_outcomes.menus.MainMenu;
import me.i2000c.newalb.custom_outcomes.menus.MessageMenu;
import me.i2000c.newalb.custom_outcomes.menus.MiniVolcanoMenu;
import me.i2000c.newalb.custom_outcomes.menus.OffsetMenu;
import me.i2000c.newalb.custom_outcomes.menus.OutcomeListMenu;
import me.i2000c.newalb.custom_outcomes.menus.OutcomeMenu;
import me.i2000c.newalb.custom_outcomes.menus.PackListMenu;
import me.i2000c.newalb.custom_outcomes.menus.RewardListMenu;
import me.i2000c.newalb.custom_outcomes.menus.RewardTypesMenu;
import me.i2000c.newalb.custom_outcomes.menus.SoundMenu;
import me.i2000c.newalb.custom_outcomes.menus.SquidExplosionMenu;
import me.i2000c.newalb.custom_outcomes.menus.StructureMenu;
import me.i2000c.newalb.custom_outcomes.menus.TeleportMenu;
import me.i2000c.newalb.custom_outcomes.menus.TrapMenu;
import me.i2000c.newalb.utils2.FilePicker;

public enum EditorType{
    REWARD_TYPES(new RewardTypesMenu()),
    REWARD_LIST(new RewardListMenu()),
    OUTCOME(new OutcomeMenu()),
    OUTCOME_LIST(new OutcomeListMenu()),
    PACK_LIST(new PackListMenu()),
    LUCKY_BLOCK_TYPE(new LuckyBlockTypeMenu()),
    LUCKY_BLOCK_TYPE_LIST(new LuckyBlockTypeListMenu()),
    MAIN_MENU(new MainMenu()),
    
    COLOR(new ColorMenu()),
    OFFSET(new OffsetMenu()),
    ENCHANTMENT(new EnchantmentMenu()),
    EQUIPMENT(new EquipmentMenu()),
    DELAYER(new DelayerMenu()),
    
    ITEM_REWARD(new ItemMenu()),
    COMMAND_REWARD(new CommandMenu()),
    ENTITY_REWARD(new EntityMenu()),
    ENTITY_TOWER_REWARD(new EntityTowerMenu()),
    FIREWORK_REWARD(new FireworkMenu()),
    SOUND_REWARD(new SoundMenu()),
    
    STRUCTURE_REWARD(new StructureMenu()),
    FILE_SELECTOR(new FilePicker()),
    
    BLOCK_REWARD(new BlockMenu()),
    LIGHTNING_REWARD(new LightningMenu()),
    DARK_HOLE_REWARD(new DarkHoleMenu()),
    MINI_VOLCANO_REWARD(new MiniVolcanoMenu()),
    MESSAGE_REWARD(new MessageMenu()),
    EFFECT_REWARD(new EffectMenu()),
    EXPLOSION_REWARD(new ExplosionMenu()),
    BLOCK_REPLACING_SPHERE_REWARD(new BlockReplacingSphereMenu()),
    TRAP_REWARD(new TrapMenu()),
    TELEPORT_REWARD(new TeleportMenu()),
    SQUID_EXPLOSION_REWARD(new SquidExplosionMenu()),
    ;
    
    private final Editor editor;
    
    private EditorType(Editor editor){
        this.editor = editor;
    }
    
    public Editor getEditor(){
        return this.editor;
    }
}
