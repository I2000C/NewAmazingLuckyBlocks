package me.i2000c.newalb.lucky_blocks.editors.menus;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.MenuItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.PaginatedEditorMenu;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.OutcomePack;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class OutcomeListMenu extends PaginatedEditorMenu<OutcomePack, Outcome> {
    
    private boolean cloneMode;
    private boolean deleteMode;
    
    public OutcomeListMenu() {
        super("&3&lOutcomes list", MenuSize.SIZE_6_ROWS, true, MenuSize.SIZE_5_ROWS);
    }
    
    @Override
    public List<Outcome> getItemList() {
        return item.getSortedOutcomes();
    }
    
    @Override
    public MenuItem mapItemToPage(Outcome outcome, int index) {
        ItemStackWrapper wrapper = ItemStackWrapper.fromItem(outcome.getItemToDisplay(), false);
        Consumer<MenuClickEvent> action = e -> {
            Player player = e.getPlayer();
            if(cloneMode) {
                //Clone outcome
                Outcome clone = outcome.clone();
                clone.setName(clone.getName() + " (clone)");
                item.addOutcome(clone, true);
                resetPagination();
                openToPlayer(player);
            } else if(deleteMode) {
                //Delete outcome
                item.removeOutcome(outcome);
                item.saveOutcomes();
                resetPagination();
                openToPlayer(player);
            } else {
                //Edit outcome
                OutcomeMenu menu = new OutcomeMenu(item);
                menu.setItemToEdit(outcome.clone());
                menu.setOnBack(this::openToPlayer);
                menu.setOnNext((p, editedOutcome) -> {
                    try {
                        editedOutcome.saveOutcome();
                        Logger.logAndMessage("&aOutcome saved with ID &b" + outcome.getID(), p);
                        Logger.log("&6Reloading pack " + item.getPackname() + " ...");
                        item.loadPack();
                        Logger.log("&aPack reloaded");
                        resetPagination();
                        openToPlayer(p);
                    } catch(Exception ex) {
                        Logger.logAndMessage("&cError while saving outcome with ID &b" + outcome.getID(), p);
                        ex.printStackTrace();
                    }
                });
                menu.openToPlayer(player);
            }
        };
        return new MenuItem(wrapper, action);
    }
    
    @Override
    protected void buildMenu(Player player) {
        ItemStack createOutcome = ItemStackWrapper.newItem(XMaterial.SLIME_BALL)
                                                  .setDisplayName("&aCreate new outcome")
                                                  .toItemStack();
        
        ItemStack cloneOutcome = GUIItem.getEnabledDisabledItem(
                cloneMode, 
                "&3Clone outcomes", 
                "&dClone mode", 
                XMaterial.REPEATER, 
                XMaterial.REPEATER);
        
        ItemStack deleteOutcome = GUIItem.getEnabledDisabledItem(
                deleteMode,
                "&cDelete outcomes",
                "&dDelete mode", 
                XMaterial.BARRIER,
                XMaterial.BARRIER);
        ItemStackWrapper.fromItem(deleteOutcome, false)
                .addLoreLine("")
                .addLoreLine("&4&lWARNING: &cIf this mode is enabled,")
                .addLoreLine("&cwhen you click on an outcome,")
                .addLoreLine("&cit will be deleted permanently");
        
        setBackItem(45);
        
        if(!cloneMode && !deleteMode) {
            setItem(46, createOutcome, e -> {
                OutcomeMenu menu = new OutcomeMenu(item);
                menu.setOnBack(this::openToPlayer);
                menu.setOnNext((p, outcome) -> {
                    try {
                        outcome.saveOutcome();
                        Logger.logAndMessage("&aOutcome saved with ID &b" + outcome.getID(), p);
                        Logger.log("&6Reloading pack " + item.getPackname() + " ...");
                        item.loadPack();
                        Logger.log("&aPack reloaded");
                        resetPagination();
                        openToPlayer(p);
                    } catch(Exception ex) {
                        Logger.logAndMessage("&cError while saving outcome with ID &b" + outcome.getID(), p);
                        ex.printStackTrace();
                    }
                });
                menu.openToPlayer(player);
            });
        }
        if(!deleteMode) {
            setItem(47, cloneOutcome, e -> {
                cloneMode = !cloneMode;
                openToPlayer(player);
            });
        }
        if(!cloneMode) {
            setItem(48, deleteOutcome, e -> {
                deleteMode = !deleteMode;
                openToPlayer(player);
            });
        }
    }
}
