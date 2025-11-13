package me.i2000c.newalb.lucky_blocks.editors.menus;

import me.i2000c.newalb.CommandManager;
import me.i2000c.newalb.api.gui.Menu;

public class GUIManager{
    private static Menu currentMenu = null;
    
    public static void setCurrentMenu(Menu menu){
        currentMenu = menu;
        CommandManager.confirmMenu = menu != null;
    }
    
    public static Menu getCurrentMenu(){
        return currentMenu;
    }
}
