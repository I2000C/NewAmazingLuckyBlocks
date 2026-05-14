package me.i2000c.newalb.lucky_blocks.editors.menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.listeners.chat.ChatListener;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.OutcomePack;
import me.i2000c.newalb.utils.logging.Logger;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class OutcomeMenu extends EditorMenu<Outcome> {
    
    private final OutcomePack currentPack;
    
    public OutcomeMenu(OutcomePack currentPack) {
        super("", MenuSize.SIZE_3_ROWS, true);
        this.currentPack = currentPack;
    }
    
    @Override
    public Outcome createNewItem() {
        int probabilty = 100;
        String name = "New outcome " + currentPack.getOutcomes().size();
        Outcome outcome = new Outcome(name, probabilty, -1, currentPack);
        return outcome;
    }
    
    @Override
    protected void buildMenu(Player player) {
        String inventoryName;
        if(isNewItem) {
            inventoryName = "&a&lCreate new outcome";
        } else {
            inventoryName = "&e&lEdit outcome";
        }
        setTitle(inventoryName);
        
        addGlassBorder(GlassColor.CYAN);
        
        ItemStack name = ItemStackWrapper.newItem(XMaterial.OAK_SIGN)
                                         .setDisplayName("&7Outcome name: &r" + item.getName())
                                         .setLore("&3Click to change")
                                         .toItemStack();
        
        ItemStack icon = ItemStackWrapper.fromItem(item.getIcon())
                                         .setDisplayName("&dOutcome icon")
                                         .setLore("&bClick on an item of your inventory",
                                                   "&b   to select the icon of the outcome.",
                                                   "&bBy default it's CHEST")
                                         .toItemStack();
        
        ItemStack creative = ItemStackWrapper.newItem(XMaterial.CRAFTING_TABLE)
                                              .setDisplayName("&3Close menu to pick items from creative mode")
                                              .toItemStack();
        
        ItemStackWrapper builder = ItemStackWrapper.newItem(XMaterial.GLOWSTONE_DUST);
        if(item.getProbability() < 0) {
            builder.setDisplayName("&cProbability must be a positive integer or 0");
        } else {
            builder.setDisplayName("&6Outcome probability: &r" + item.getProbability());
        }
        builder.setLore("&3Click to change");
        ItemStack prob = builder.toItemStack();
        
        setBackItem(10);
        setNextItem(16, e -> {
            if(item.getProbability() > 0) {
                RewardListMenu menu = new RewardListMenu();
                menu.setItemToEdit(item);
                menu.setOnBack(this::openToPlayer);
                menu.setOnNext(this::onNext);
                menu.openToPlayer(player);
            }
        });
        
        setItem(12, name, e -> {
            ChatListener.registerPlayer(player, message -> {
                item.setName(message);
                openToPlayer(player);
            });
            player.closeInventory();
        });
        
        setItem(13, icon);
        
        setItem(14, prob, e -> {
            ChatListener.registerPlayer(player, message -> {
                try {
                    int probability = Integer.parseInt(message);
                    if(probability >= 0) {
                        item.setProbability(probability);
                    } else {
                        item.setProbability(-1);
                    }
                } catch(NumberFormatException ex) {
                    item.setProbability(-2);
                }
                openToPlayer(player);
            });
            player.closeInventory();
        });
        
        setItem(22, creative, e -> {
            player.closeInventory();
            Logger.sendMessage("&6Use &b/alb return &6to return to the menu", player);
        });
    }
    
    @Override
    protected void onClickDefault(MenuClickEvent event) {
        if(event.isBottomInventory() && !event.isEmptyItem()) {
            ItemStack stack = event.getCurrentItem();
            item.setIcon(stack);
            openToPlayer(event.getPlayer());
        }
    }
}
