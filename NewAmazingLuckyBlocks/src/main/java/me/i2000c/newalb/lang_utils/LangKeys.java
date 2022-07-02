package me.i2000c.newalb.lang_utils;

import me.i2000c.newalb.utils.CustomConfig;

public enum LangKeys implements CustomConfig.CustomConfigKey{
    wands_regen_name("Wands.Regen.name"),
    wands_fire_name("Wands.Fire.name"),
    wands_invisibility_name("Wands.Invisibility.name"),
    wands_tnt_name("Wands.TNT.name"),
    wands_slime_name("Wands.Slime.name"),
    wands_lightning_name("Wands.Lightning.name"),
    wands_shield_name("Wands.Shield.name"),
    wands_potion_name("Wands.Potion.name"),
    wands_frost_name("Wands.Frost.name"),
    objects_darkhole_name("Objects.DarkHole.name"),
    objects_minivolcano_name("Objects.MiniVolcano.name"),
    objects_icebow_name("Objects.IceBow.name"),
    objects_autobow_name("Objects.AutoBow.name"),
    objects_multibow_name("Objects.MultiBow.name"),
    objects_explosivebow_name("Objects.ExplosiveBow.name"),
    objects_homingbow_name("Objects.HomingBow.name"),
    objects_hookbow_name("Objects.HookBow.name"),
    objects_playertracker_name("Objects.PlayerTracker.name"),
    objects_playertracker_message1("Objects.PlayerTracker.message1"),
    objects_playertracker_message2("Objects.PlayerTracker.message2"),
    objects_playertracker_message3("Objects.PlayerTracker.message3"),
    objects_endermansoup_name("Objects.EndermanSoup.name"),
    objects_hotpotato_name("Objects.HotPotato.name"),
    objects_luckytool_name("Objects.LuckyTool.name"),
    objects_luckytool_need("Objects.LuckyTool.need"),
    objects_luckytool_disabled("Objects.LuckyTool.disabled"),
    cooldown_message("Cooldown-message"),
    need_permission("need-permission"),
    cannot_rename("cannot-rename"),
    helpmenu_line1("Helpmenu.line1"),
    helpmenu_line2("Helpmenu.line2"),
    helpmenu_line3("Helpmenu.line3"),
    helpmenu_line4("Helpmenu.line4"),
    helpmenu_line5("Helpmenu.line5"),
    helpmenu_line6("Helpmenu.line6"),
    helpmenu_line7("Helpmenu.line7"),
    helpmenu2_line1("Helpmenu2.line1"),
    helpmenu2_line2("Helpmenu2.line2"),
    helpmenu2_line3("Helpmenu2.line3"),
    helpmenu2_line4("Helpmenu2.line4"),
    helpmenu2_line5("Helpmenu2.line5"),
    helpmenu2_line6("Helpmenu2.line6"),
    helpmenu2_line7("Helpmenu2.line7"),
    helpmenu2_line8("Helpmenu2.line8"),
    helpmenu2_line9("Helpmenu2.line9"),
    helpmenu3_line1("Helpmenu3.line1"),
    helpmenu3_line2("Helpmenu3.line2"),
    helpmenu3_line3("Helpmenu3.line3"),
    helpmenu3_line4("Helpmenu3.line4"),
    helpmenu3_line5("Helpmenu3.line5"),
    helpmenu3_line6("Helpmenu3.line6"),
    helpmenu4_line1("Helpmenu4.line1"),
    helpmenu4_line2("Helpmenu4.line2"),
    helpmenu4_line3("Helpmenu4.line3"),
    helpmenu4_line4("Helpmenu4.line4"),
    helpmenu4_line5("Helpmenu4.line5"),
    helpmenu4_line6("Helpmenu4.line6"),
    helpmenu4_line7("Helpmenu4.line7"),
    helpmenu4_line8("Helpmenu4.line8"),
    helpmenu5_line1("Helpmenu5.line1"),
    helpmenu5_line2("Helpmenu5.line2"),
    helpmenu5_line3("Helpmenu5.line3"),
    helpmenu5_line4("Helpmenu5.line4"),
    helpmenu5_line5("Helpmenu5.line5"),
    helpmenu5_line6("Helpmenu5.line6"),
    helpmenu5_line7("Helpmenu5.line7"),
    helpmenu5_line8("Helpmenu5.line8"),
    world_loading_line1("World-loading.line1"),
    world_loading_line2("World-loading.line2"),
    world_loading_line3("World-loading.line3"),
    world_loading_line4("World-loading.line4"),
    world_loading_line5("World-loading.line5"),
    world_loading_line6("World-loading.line6"),
    world_loading_line7("World-loading.line7"),
    world_loading_line8("World-loading.line8"),
    world_management1_line1("World-management1.line1"),
    world_management1_line2("World-management1.line2"),
    world_management1_worldname("World-management1.worldName"),
    world_management1_enabledworld("World-management1.enabledWorld"),
    world_management1_disabledworld("World-management1.disabledWorld"),
    world_management2_line1("World-management2.line1"),
    world_management2_line2("World-management2.line2"),
    world_management2_line3("World-management2.line3"),
    world_management2_line4("World-management2.line4"),
    menuconfirmation_line1("MenuConfirmation.line1"),
    menuconfirmation_line2("MenuConfirmation.line2"),
    menuconfirmation_line3("MenuConfirmation.line3"),
    nopermission("NoPermission"),
    loadingwands("LoadingWands"),
    loadingobjects("LoadingObjects"),
    loadingblocks("LoadingBlocks"),
    placingblocks("PlacingBlocks"),
    loadingluckytool("LoadingLuckyTool"),
    loadingspecialitem("LoadingSpecialItem"),
    loading_line1("Loading.line1"),
    loading_line2("Loading.line2"),
    loading_line3("Loading.line3"),
    enable_line1("Enable.line1"),
    enable_line2("Enable.line2"),
    disable_line1("Disable.line1"),
    reload_line1("Reload.line1"),
    reload_line2("Reload.line2"),
    reload_line3("Reload.line3"),
    helpmessage("HelpMessage"),
    unknowncommand("UnknownCommand"),
    ingameprefix("InGamePrefix"),
    ;
    
    private static final String VERSION_KEY = "Lang_version";
    private static final int CURRENT_VERSION = 1;
    
    private final String value;
    
    private LangKeys(String value){
        this.value = value;
    }
    
    @Override
    public String getValue(){
        return this.value;
    }
    
    public static String getVersionKey(){
        return VERSION_KEY;
    }
    
    public static int getCurrentVersion(){
        return CURRENT_VERSION;
    }
}
