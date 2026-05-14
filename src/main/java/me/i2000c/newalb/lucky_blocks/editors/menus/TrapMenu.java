package me.i2000c.newalb.lucky_blocks.editors.menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.lucky_blocks.editors.menus.trap.TrapPacksMenu;
import me.i2000c.newalb.lucky_blocks.editors.menus.trap.TrapTypeMenu;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.types.TrapReward;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class TrapMenu extends EditorMenu<TrapReward> {
    
    public TrapMenu() {
        super("&5&lTrap Reward", MenuSize.SIZE_3_ROWS, true);
    }
    
    @Override
    protected void buildMenu(Player player) {
        addGlassBorder(GlassColor.PURPLE);
        
        ItemStack trapMaterialItem = ItemStackWrapper.newItem(item.getTrapMaterial())
                                                     .setDisplayName("&2Selected pressure plate material: &a" + item.getTrapMaterial().name())
                                                     .addLoreLine("&3Click to change")
                                                     .toItemStack();
        
        ItemStackWrapper builder = ItemStackWrapper.newItem(XMaterial.NAME_TAG);        
        if(item.getTrapName() == null) {
            builder.setDisplayName("&2Trap name: &cnull");
        } else {
            builder.setDisplayName("&2Trap name: &r" + item.getTrapName());
        }        
        builder.addLoreLine("&3Click to change");
        ItemStack trapNameItem = builder.toItemStack();
        
        Outcome outcome = item.getTrapOutcome();
        if(outcome == null) {
            builder = ItemStackWrapper.newItem(XMaterial.CHEST);
            builder.setDisplayName("&2Selected trap outcome: &cnull");
        } else {
            builder = ItemStackWrapper.newItem(XMaterial.matchXMaterial(outcome.getIcon()));
            builder.setDisplayName("&2Selected trap outcome: &a" + item.getTrapOutcome());
        }
        builder.addLoreLine("&3Click to select");
        ItemStack trapOutcomeItem = builder.toItemStack();
        
        setBackItem(10);
        setNextItem(16, e -> {
            if(item.getTrapName() != null && item.getTrapOutcome() != null) {
                onNext(player, item);
            }
        });
        
        setItem(12, trapMaterialItem, e -> {
            TrapTypeMenu menu = new TrapTypeMenu();
            menu.setItemToEdit(item.getTrapMaterial());
            menu.setOnBack(this::openToPlayer);
            menu.setOnNext((p, material) -> {
                item.setTrapMaterial(material);
                openToPlayer(player);
            });
            menu.openToPlayer(player);
        });
        
        setItem(13, trapNameItem, e -> {
            ChatListener.registerPlayer(player, message -> {
                item.setTrapName(message);
                openToPlayer(player);
            });
            player.closeInventory();
            Logger.sendMessage("&3Enter the trap name in the chat and then press ENTER", player);
        });
        
        setItem(14, trapOutcomeItem, e -> {
            TrapPacksMenu menu = new TrapPacksMenu();
            menu.setItemToEdit(item.getTrapOutcome());
            menu.setOnBack(this::openToPlayer);
            menu.setOnNext((p, trapOutcome) -> {
                item.setTrapOutcome(trapOutcome);
                openToPlayer(player);
            });
            menu.openToPlayer(player);
        });
    }
}
