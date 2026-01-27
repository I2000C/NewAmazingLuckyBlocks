package me.i2000c.newalb.lucky_blocks.editors.menus;

import lombok.Getter;
import lombok.Setter;
import me.i2000c.newalb.api.gui.Menu;

public class GUIManager {
    @Getter
    private static Menu currentMenu = null;
    
    public static void setCurrentMenu(Menu menu){
        currentMenu = menu;
        confirmMenu = menu != null;
    }
    
    @Getter
    @Setter
    private static boolean confirmMenu = false;
}
