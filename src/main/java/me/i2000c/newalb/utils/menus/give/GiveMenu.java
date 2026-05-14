package me.i2000c.newalb.utils.menus.give;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.Menu;
import me.i2000c.newalb.listeners.interact.SpecialItems;
import me.i2000c.newalb.lucky_blocks.rewards.TypeManager;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class GiveMenu extends Menu {
    
    private Player targetPlayer;

    public GiveMenu() {
        super("&d&lGive Menu", MenuSize.SIZE_1_ROW, false);
    }

    @Override
    protected void buildMenu(Player player) {
        if(targetPlayer == null) {
            targetPlayer = player;
        }
        
        ItemStack playerItem = ItemStackWrapper.newItem(XMaterial.PLAYER_HEAD)
                                               .setDisplayName("&2Player Selected:")
                                               .addLoreLine("&b" + targetPlayer.getName())
                                               .setOwner(targetPlayer)
                                               .toItemStack();
        
        ItemStack wands = ItemStackWrapper.newItem(XMaterial.MUSIC_DISC_FAR)
                                          .setDisplayName("&aGive Wands")
                                          .toItemStack();

        ItemStack objects = ItemStackWrapper.newItem(XMaterial.BUCKET)
                                            .setDisplayName("&bGive Objects")
                                            .toItemStack();
        
        ItemStack luckyBlocks = ItemStackWrapper.fromItem(TypeManager.getMenuItemStack(), false)
                                                .setDisplayName("&6Give LuckyBlocks")
                                                .toItemStack();
        
        ItemStack luckyTool = SpecialItems.lucky_tool.getItem()
                                                     .setDisplayName("&eGive LuckyTool")
                                                     .setLore()
                                                     .toItemStack();
        
        setItem(0, wands, e -> {
            ItemGiveMenu itemGiveMenu = new ItemGiveMenu("&aGive Wands", e.getPlayer(), SpecialItems.getWands());
            itemGiveMenu.setOnBack(this::openToPlayer);
            itemGiveMenu.openToPlayer(player);
        });
        
        setItem(1, objects, e -> {
            ItemGiveMenu itemGiveMenu = new ItemGiveMenu("&bGive Objects", e.getPlayer(), SpecialItems.getObjects());
            itemGiveMenu.setOnBack(this::openToPlayer);
            itemGiveMenu.openToPlayer(player);
        });
        
        setItem(2, luckyBlocks, e -> {
            ItemGiveMenu itemGiveMenu = new ItemGiveMenu("&6Give LuckyBlocks", e.getPlayer(), TypeManager.getTypes());
            itemGiveMenu.setOnBack(this::openToPlayer);
            itemGiveMenu.openToPlayer(player);
        });
        
        setItem(3, luckyTool, e -> {
            targetPlayer.getInventory().addItem(SpecialItems.lucky_tool.getItem().toItemStack());
            if(targetPlayer.equals(player)) {
                targetPlayer.updateInventory();
            }
        });
        
        setItem(8, playerItem, e -> {
            PlayerSelecionMenu playerMenu = new PlayerSelecionMenu();
            playerMenu.setItemToEdit(player);
            playerMenu.setOnBack(this::openToPlayer);
            playerMenu.setOnNext((p, selectedPlayer) -> {
                targetPlayer = selectedPlayer;
                openToPlayer(p);
            });
            playerMenu.openToPlayer(player);
        });
    }
}
