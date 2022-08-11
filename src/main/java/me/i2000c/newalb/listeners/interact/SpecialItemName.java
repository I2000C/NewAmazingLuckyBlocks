package me.i2000c.newalb.listeners.interact;

public enum SpecialItemName{
    lucky_tool(false),

    regen_wand(true),
    inv_wand(true),
    tnt_wand(true),
    slime_wand(true),
    fire_wand(true),
    lightning_wand(true),
    shield_wand(true),
    potion_wand(true),
    frost_path_wand(true),

    dark_hole(false),
    mini_volcano(false),
    player_tracker(false),
    enderman_soup(false),
    hot_potato(false),
    ice_bow(false),
    auto_bow(false),
    multi_bow(false),
    explosive_bow(false),
    homing_bow(false),
    hook_bow(false);

    private final boolean isWand;

    private SpecialItemName(boolean isWand){
        this.isWand = isWand;
    }

    public boolean isWand(){
        return this.isWand;
    }

    public static SpecialItemName fromString(String str){
        try{
            return SpecialItemName.valueOf(str.replace("%", ""));
        }catch(IllegalArgumentException ex){
            return null;
        }
    }
}
