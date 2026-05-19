package me.i2000c.newalb.utils.menus.preview;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import lombok.NonNull;
import me.i2000c.newalb.api.gui.MenuItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.PaginatedMenu;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.lucky_blocks.rewards.LuckyBlockType;
import me.i2000c.newalb.lucky_blocks.rewards.OutcomePack;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

public class LuckyBlockPreviewMenu extends PaginatedMenu<Map.Entry<OutcomePack, Integer>> {
    
    private final LuckyBlockType luckyBlockType;

    public LuckyBlockPreviewMenu(@NonNull LuckyBlockType luckyBlockType) {
        super("&bCurrent packs", MenuSize.SIZE_4_ROWS, false, MenuSize.SIZE_3_ROWS);
        this.luckyBlockType = luckyBlockType;
    }
    
    @Override
    public List<Map.Entry<OutcomePack, Integer>> getItemList() {
        return new ArrayList<>(luckyBlockType.getPacks().entrySet());
    }
    
    @Override
    public MenuItem mapItemToPage(Map.Entry<OutcomePack, Integer> entry, int index) {
        OutcomePack pack = entry.getKey();
        int probability = entry.getValue();
        ItemStackWrapper wrapper = ItemStackWrapper.fromItem(pack.getItemToDisplay(), false)
                                                   .addLoreLine("")
                                                   .addLoreLine("&eProbability: &2" + probability)
                                                   .addLoreLine("")
                                                   .addLoreLine("&3Click to see outcome list");
        Consumer<MenuClickEvent> action = e -> {
            OutcomePackPreviewMenu menu = new OutcomePackPreviewMenu(pack);
            menu.setOnBack(this::openToPlayer);
            menu.openToPlayer(e.getPlayer());
        };
        return new MenuItem(wrapper, action);
    }

    @Override
    protected void buildMenu(Player player) {
        ItemStack exit = ItemStackWrapper.newItem(XMaterial.IRON_DOOR)
                                         .setDisplayName("&cExit")
                                         .toItemStack();
        setItem(27, exit, e -> onBack(e.getPlayer()));
    }
}
