package me.i2000c.newalb.utils.menus.give;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.MenuItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.PaginatedEditorMenu;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class PlayerSelecionMenu extends PaginatedEditorMenu<Player, Player> {

    public PlayerSelecionMenu() {
        super("&2&lOnline Player List", MenuSize.SIZE_6_ROWS, false, MenuSize.SIZE_5_ROWS, 51, 52, 53);
    }
    
    @Override
    public List<Player> getItemList() {
        List<Player> players = Bukkit.getOnlinePlayers()
                                     .stream()
                                     .sorted(Comparator.comparing(Player::getName))
                                     .collect(Collectors.toList());
        return players;
    }

    @Override
    public MenuItem mapItemToPage(Player player, int index) {
        ItemStackWrapper wrapper = ItemStackWrapper.newItem(XMaterial.PLAYER_HEAD)
                                                   .setDisplayName("&2" + player.getName())
                                                   .setOwner(player);
        
        if(player.equals(this.item)) {
            wrapper.addEnchantment(XEnchantment.UNBREAKING, 1);
            wrapper.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        
        Consumer<MenuClickEvent> action = e -> {
            this.item = e.getPlayer();
            onNext(player, this.item);
        };
        
        return new MenuItem(wrapper, action);
    }

    @Override
    protected void buildMenu(Player player) {
        setBackItem(45);
    }
}
