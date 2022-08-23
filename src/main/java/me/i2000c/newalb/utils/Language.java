package me.i2000c.newalb.utils;

public enum Language{
    EN,
    ES,
    HU,
    NL,
    TR,
    CH_TW;
    
    public static final String LANG_FOLDER_NAME = "lang";    
    private final String langFileName;
    
    private Language(){
        this.langFileName = LANG_FOLDER_NAME + '/' + "lang_" + name() + ".yml";
    }
    
    public String getLangFileName(){
        return langFileName;
    }
    
    private static final Language[] vals = values();
    public static Language[] getValues(){
        return vals;
    }
}
