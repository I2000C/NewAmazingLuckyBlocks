package me.i2000c.newalb.utils.menus.give;

import java.util.List;
import java.util.Objects;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import lombok.NonNull;
import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.Menu;
import me.i2000c.newalb.lucky_blocks.rewards.ItemProvider;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.misc.OtherUtils;

public class ItemGiveMenu extends Menu {
    
    @NonNull
    private final Player targetPlayer;
    
    @NonNull
    private final List<? extends ItemProvider> itemsToDisplay;
    
    int amount = 1;
    
    public ItemGiveMenu(String titleMenu, @NonNull Player targetPlayer, List<? extends ItemProvider> itemsToDisplay) {
        super(titleMenu, MenuSize.SIZE_3_ROWS, false);
        this.targetPlayer = targetPlayer;
        this.itemsToDisplay = itemsToDisplay;
    }

    @Override
    protected void buildMenu(Player player) {
        Objects.requireNonNull(targetPlayer);
        
        for(int i=0; i<itemsToDisplay.size() && i<16; i++) {
            ItemStack stack = itemsToDisplay.get(i).getItem().toItemStack();
            stack.setAmount(amount);
            setItem(i, stack, e -> {
                targetPlayer.getInventory().addItem(stack.clone());
                if(targetPlayer.equals(player)) {
                    targetPlayer.updateInventory();
                }
            });
        }
        
        ItemStack playerItem = ItemStackWrapper.newItem(XMaterial.PLAYER_HEAD)
                                               .setDisplayName("&2Player Selected:")
                                               .addLoreLine("&b" + targetPlayer.getName())
                                               .setOwner(targetPlayer)
                                               .toItemStack();
        
        ItemStack close = ItemStackWrapper.newItem(XMaterial.MAGMA_CREAM)
                                          .setDisplayName("&cClose")
                                          .toItemStack();
        
        setItem(16, GUIItem.getPlusLessItem(+1), e -> {
            amount = OtherUtils.addInRange(amount, 1, 1, 65);
            openToPlayer(player);
        });
        
        setItem(17, GUIItem.getPlusLessItem(-1), e -> {
            amount = OtherUtils.subInRange(amount, 1, 1, 65);
            openToPlayer(player);
        });
        
        setItem(18, playerItem);
        
        setBackItem(25);
        
        setItem(26, close, e -> e.getPlayer().closeInventory());
    }

}
