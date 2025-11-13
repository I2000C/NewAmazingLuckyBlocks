package me.i2000c.newalb.lucky_blocks.editors;

import me.i2000c.newalb.lucky_blocks.editors.menus.BlockMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.BlockReplacingSphereMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.ColorMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.CommandMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.DarkHoleMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.DelayerMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.EffectMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.EnchantmentMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.EntityMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.EntityTowerMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.EquipmentMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.ExplosionMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.FireworkMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.ItemFlagMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.ItemMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.LightningMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.LuckyBlockTypeListMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.LuckyBlockTypeMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.MainMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.MessageMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.MiniVolcanoMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.OffsetMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.OutcomeListMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.OutcomeMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.PackListMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.RewardListMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.RewardTypesMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.SoundMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.SoundTypeMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.SquidExplosionMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.StructureMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.TeleportMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.TrapMenu;
import me.i2000c.newalb.lucky_blocks.editors.nbt.ItemNbtMenu;
import me.i2000c.newalb.lucky_blocks.editors.nbt.ItemNbtTagMenu;
import me.i2000c.newalb.lucky_blocks.editors.nbt.ItemNbtTypeMenu;
import me.i2000c.newalb.utils.files.FilePicker;

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
    ITEM_FLAGS(new ItemFlagMenu()),
    ITEM_NBT(new ItemNbtMenu()),
    ITEM_NBT_TYPE(new ItemNbtTypeMenu()),
    ITEM_NBT_TAG(new ItemNbtTagMenu()),
    EQUIPMENT(new EquipmentMenu()),
    DELAYER(new DelayerMenu()),
    
    ITEM_REWARD(new ItemMenu()),
    COMMAND_REWARD(new CommandMenu()),
    ENTITY_REWARD(new EntityMenu()),
    ENTITY_TOWER_REWARD(new EntityTowerMenu()),
    FIREWORK_REWARD(new FireworkMenu()),
    
    SOUND_REWARD(new SoundMenu()),
    SOUND_TYPE(new SoundTypeMenu()),
    
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
